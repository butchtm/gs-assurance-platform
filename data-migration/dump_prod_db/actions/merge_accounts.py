import logging
import psycopg2
from psycopg2 import sql
from db.config import Config
from .clear import _psycopg2_params

_config = Config()
SOURCE_DB_CONFIG = getattr(_config, 'source_db', None)
TARGET_DB_CONFIG = getattr(_config, 'target_db', None)
SCHEMA = 'gs'
ACCOUNTS_TABLE = 'account'  # adjust here if table name changes
BACKUP_TABLE = '_target_accounts_backup'
COMPOSITE_KEY = ['name', 'created_time']  # composite business key for idempotent merge


def qual(schema: str, name: str) -> str:
    """Return a fully qualified, SQL-safe identifier reference as text (for regclass usage)."""
    return f"{schema}.{name}"


def table_exists(cur, schema: str, table: str) -> bool:
    cur.execute(
        """
        SELECT EXISTS (
            SELECT 1 FROM information_schema.tables
            WHERE table_schema = %s AND table_name = %s
        );
        """,
        (schema, table)
    )
    return bool(cur.fetchone()[0])


def get_pk_column(cur, schema: str, table: str) -> str:
    """Return the first primary key column name for the table."""
    cur.execute(
        """
        SELECT a.attname
        FROM pg_index i
        JOIN pg_attribute a ON a.attrelid = i.indrelid AND a.attnum = ANY(i.indkey)
        WHERE i.indrelid = %s::regclass
          AND i.indisprimary
        ORDER BY a.attnum
        LIMIT 1;
        """,
        (qual(schema, table),)
    )
    row = cur.fetchone()
    if not row:
        raise RuntimeError(f"Could not determine primary key column for {schema}.{table}")
    return row[0]


def pre_restore_backup_target_accounts():
    if TARGET_DB_CONFIG is None:
        raise RuntimeError("Target DB config not available")
    conn = psycopg2.connect(**_psycopg2_params(TARGET_DB_CONFIG))
    try:
        with conn.cursor() as cur:
            if not table_exists(cur, SCHEMA, ACCOUNTS_TABLE):
                logging.warning("Target accounts table %s.%s does not exist; nothing to backup.", SCHEMA, ACCOUNTS_TABLE)
                return
            if table_exists(cur, SCHEMA, BACKUP_TABLE):
                logging.info("Recreating existing backup table %s.%s", SCHEMA, BACKUP_TABLE)
                cur.execute(sql.SQL("DROP TABLE {}.{};" ).format(sql.Identifier(SCHEMA), sql.Identifier(BACKUP_TABLE)))
                conn.commit()
            logging.info("Creating backup table %s.%s from %s.%s", SCHEMA, BACKUP_TABLE, SCHEMA, ACCOUNTS_TABLE)
            cur.execute(
                sql.SQL("CREATE TABLE {}.{} AS SELECT * FROM {}.{};").format(
                    sql.Identifier(SCHEMA), sql.Identifier(BACKUP_TABLE), sql.Identifier(SCHEMA), sql.Identifier(ACCOUNTS_TABLE)
                )
            )
            cur.execute(sql.SQL("SELECT COUNT(*) FROM {}.{};").format(sql.Identifier(SCHEMA), sql.Identifier(BACKUP_TABLE)))
            count = cur.fetchone()[0]
            conn.commit()
            logging.info("Backup completed (%s rows)", count)
    finally:
        conn.close()


def validate_tables_exist(cur) -> bool:
    """Validate both accounts and backup tables exist before attempting merge."""
    if not table_exists(cur, SCHEMA, BACKUP_TABLE):
        logging.warning("No backup table %s.%s found; skipping accounts merge.", SCHEMA, BACKUP_TABLE)
        return False
    if not table_exists(cur, SCHEMA, ACCOUNTS_TABLE):
        logging.error("Accounts table %s.%s not found; cannot merge.", SCHEMA, ACCOUNTS_TABLE)
        return False
    return True


def get_backup_count(cur) -> int:
    """Count rows in the backup table."""
    cur.execute(
        sql.SQL("SELECT COUNT(*) FROM {}.{};").format(
            sql.Identifier(SCHEMA), sql.Identifier(BACKUP_TABLE)
        )
    )
    return cur.fetchone()[0]


def get_table_columns(cur) -> list[str]:
    """Get ordered list of column names from the accounts table."""
    cur.execute(
        """
        SELECT column_name FROM information_schema.columns
        WHERE table_schema = %s AND table_name = %s
        ORDER BY ordinal_position;
        """,
        (SCHEMA, ACCOUNTS_TABLE)
    )
    return [r[0] for r in cur.fetchall()]


def create_account_id_mapping(cur, pk_col: str) -> int:
    """Create mapping table old_account_id -> new_account_id using composite key.

    Composite key columns defined in COMPOSITE_KEY. Assumes backup table rows now all have a
    matching canonical row in the restored (source) + inserted target table based on the key.
    """
    for col in COMPOSITE_KEY:
        # Basic validation; if columns missing, raise for clarity.
        cur.execute(
            """
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = %s AND table_name = %s AND column_name = %s
            """,
            (SCHEMA, BACKUP_TABLE, col)
        )
        if not cur.fetchone():
            raise RuntimeError(f"Composite key column '{col}' not found in backup table {SCHEMA}.{BACKUP_TABLE}")

    conditions = sql.SQL(' AND ').join(
        sql.SQL("a.{c} IS NOT DISTINCT FROM b.{c}").format(c=sql.Identifier(col)) for col in COMPOSITE_KEY
    )

    try:
        cur.execute(sql.SQL("DROP TABLE IF EXISTS {}._account_id_mapping;").format(sql.Identifier(SCHEMA)))
        mapping_sql = sql.SQL(
            """
            CREATE TABLE {}._account_id_mapping AS
            SELECT b.{pk}::INTEGER AS old_account_id,
                   a.{pk}::INTEGER AS new_account_id,
                   CURRENT_TIMESTAMP AS created_at
            FROM {}.{} b
            JOIN {}.{} a ON {conditions}
            """
        ).format(
            sql.Identifier(SCHEMA),
            sql.Identifier(SCHEMA), sql.Identifier(BACKUP_TABLE),
            sql.Identifier(SCHEMA), sql.Identifier(ACCOUNTS_TABLE),
            conditions=conditions,
            pk=sql.Identifier(pk_col)
        )
        cur.execute(mapping_sql)
        cur.execute(
            sql.SQL("CREATE INDEX ON {}._account_id_mapping(old_account_id);").format(sql.Identifier(SCHEMA))
        )
        cur.execute(
            sql.SQL("SELECT COUNT(*) FROM {}._account_id_mapping;").format(sql.Identifier(SCHEMA))
        )
        count = cur.fetchone()[0]
        logging.info("Created account ID mapping table (%s rows)", count)
        return count
    except Exception as e:
        logging.warning("Could not build account id mapping: %s", e)
        return 0


def cleanup_backup_table(cur):
    """Attempt to drop the backup table after successful merge."""
    try:
        cur.execute(
            sql.SQL("DROP TABLE {}.{};").format(sql.Identifier(SCHEMA), sql.Identifier(BACKUP_TABLE))
        )
        logging.info("Dropped backup table %s.%s", SCHEMA, BACKUP_TABLE)
    except Exception as drop_err:
        logging.warning("Could not drop backup table %s.%s: %s", SCHEMA, BACKUP_TABLE, drop_err)


def post_restore_merge_target_accounts(dry_run: bool = False):
    if TARGET_DB_CONFIG is None:
        raise RuntimeError("Target DB config not available")
    
    conn = psycopg2.connect(**_psycopg2_params(TARGET_DB_CONFIG))
    try:
        with conn.cursor() as cur:
            if not validate_tables_exist(cur):
                return

            pk_col = get_pk_column(cur, SCHEMA, ACCOUNTS_TABLE)
            logging.info("Detected primary key column '%s' for %s.%s", pk_col, SCHEMA, ACCOUNTS_TABLE)

            backup_count = get_backup_count(cur)
            
            if backup_count == 0:
                logging.info("Backup table empty; nothing to merge.")
                return

            logging.info("Reconciling %s previously backed-up target accounts (composite key: %s)...", backup_count, ','.join(COMPOSITE_KEY))

            # NULL out competencies_id to avoid FK constraint violations
            logging.info("Nullifying competencies_id in backup table to avoid FK violations...")
            cur.execute(
                sql.SQL("UPDATE {}.{} SET competencies_id = NULL;").format(
                    sql.Identifier(SCHEMA), sql.Identifier(BACKUP_TABLE)
                )
            )
            logging.info("Updated %s rows in backup table", cur.rowcount)
            
            logging.info("Nullifying competencies_id in accounts table to avoid FK violations...")
            cur.execute(
                sql.SQL("UPDATE {}.{} SET competencies_id = NULL;").format(
                    sql.Identifier(SCHEMA), sql.Identifier(ACCOUNTS_TABLE)
                )
            )
            logging.info("Updated %s rows in accounts table", cur.rowcount)

            for col in COMPOSITE_KEY:
                for tbl in (ACCOUNTS_TABLE, BACKUP_TABLE):
                    cur.execute(
                        """
                        SELECT 1 FROM information_schema.columns
                        WHERE table_schema=%s AND table_name=%s AND column_name=%s
                        """,
                        (SCHEMA, tbl, col)
                    )
                    if not cur.fetchone():
                        raise RuntimeError(f"Natural key column '{col}' missing on {SCHEMA}.{tbl}")

            # Determine existing matches count
            match_conditions = ' AND '.join([f"a.{c} IS NOT DISTINCT FROM b.{c}" for c in COMPOSITE_KEY])
            cur.execute(
                sql.SQL(
                    f"""
                    SELECT COUNT(*)
                    FROM {SCHEMA}.{BACKUP_TABLE} b
                    JOIN {SCHEMA}.{ACCOUNTS_TABLE} a ON {match_conditions}
                    """
                )
            )
            existing_matches = cur.fetchone()[0]

            # Build insert of missing rows (exclude PK so new IDs are assigned)
            all_columns = get_table_columns(cur)
            non_pk_columns = [c for c in all_columns if c != pk_col]
            col_idents = sql.SQL(',').join(sql.Identifier(c) for c in non_pk_columns)
            select_cols = sql.SQL(',').join(sql.Identifier(c) for c in non_pk_columns)

            not_exists_conditions = ' AND '.join([f"a.{c} IS NOT DISTINCT FROM b.{c}" for c in COMPOSITE_KEY])
            insert_sql = sql.SQL(
                """
                INSERT INTO {}.{} ({})
                SELECT {}
                FROM {}.{} b
                WHERE NOT EXISTS (
                    SELECT 1 FROM {}.{} a WHERE {conditions}
                )
                """
            ).format(
                sql.Identifier(SCHEMA),
                sql.Identifier(ACCOUNTS_TABLE),
                col_idents,
                select_cols,
                sql.Identifier(SCHEMA),
                sql.Identifier(BACKUP_TABLE),
                sql.Identifier(SCHEMA),
                sql.Identifier(ACCOUNTS_TABLE),
                conditions=sql.SQL(not_exists_conditions)
            )

            cur.execute(insert_sql)
            inserted = cur.rowcount
            logging.info("Inserted %s new account rows (already present: %s)", inserted, existing_matches)

            # Build mapping table (covers both existing & newly inserted)
            mapping_count = create_account_id_mapping(cur, pk_col)
            skipped = backup_count - mapping_count
            if skipped > 0:
                logging.info("%s backup account rows did not map (unexpected)", skipped)
        
            
            cleanup_backup_table(cur)
            
            conn.commit()
    finally:
        conn.close()

__all__ = [
    'pre_restore_backup_target_accounts',
    'post_restore_merge_target_accounts'
]