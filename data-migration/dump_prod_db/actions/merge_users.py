import logging
import psycopg2
from psycopg2 import sql
from db.config import Config
from .clear import _psycopg2_params

_config = Config()
SOURCE_DB_CONFIG = getattr(_config, 'source_db', None)
TARGET_DB_CONFIG = getattr(_config, 'target_db', None)
SCHEMA = 'gs'
USER_TABLE = 'user'
BACKUP_TABLE = '_target_users_backup'
PRIMARY_KEY = 'idp_user_id'  # unique, stable identifier for user merge


def qualify_identifier(schema: str, name: str) -> str:
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
        (qualify_identifier(schema, table),)
    )
    row = cur.fetchone()
    if not row:
        raise RuntimeError(f"Could not determine primary key column for {schema}.{table}")
    return row[0]


def pre_restore_backup_target_users():
    if TARGET_DB_CONFIG is None:
        raise RuntimeError("Target DB config not available")
    conn = psycopg2.connect(**_psycopg2_params(TARGET_DB_CONFIG))
    try:
        with conn.cursor() as cur:
            
            if not table_exists(cur, SCHEMA, USER_TABLE):
                logging.warning("Target user table %s.%s does not exist; nothing to backup.", SCHEMA, USER_TABLE)
                return
            
            if table_exists(cur, SCHEMA, BACKUP_TABLE):
                logging.info("Recreating existing backup table %s.%s", SCHEMA, BACKUP_TABLE)
                cur.execute(sql.SQL("DROP TABLE {}.{};" ).format(sql.Identifier(SCHEMA), sql.Identifier(BACKUP_TABLE)))
                conn.commit()
            
            logging.info("Creating backup table %s.%s from %s.%s", SCHEMA, BACKUP_TABLE, SCHEMA, USER_TABLE)
            cur.execute(
                sql.SQL("CREATE TABLE {}.{} AS SELECT * FROM {}.{};").format(
                    sql.Identifier(SCHEMA), sql.Identifier(BACKUP_TABLE), sql.Identifier(SCHEMA), sql.Identifier(USER_TABLE)
                )
            )
            cur.execute(sql.SQL("SELECT COUNT(*) FROM {}.{};").format(sql.Identifier(SCHEMA), sql.Identifier(BACKUP_TABLE)))
            count = cur.fetchone()[0]
            conn.commit()
            logging.info("Backup completed (%s rows)", count)
    finally:
        conn.close()


def validate_tables_exist(cur) -> bool:
    """Validate both user and backup tables exist before attempting merge."""
    if not table_exists(cur, SCHEMA, BACKUP_TABLE):
        logging.warning("No backup table %s.%s found; skipping user merge.", SCHEMA, BACKUP_TABLE)
        return False
    if not table_exists(cur, SCHEMA, USER_TABLE):
        logging.error("User table %s.%s not found; cannot merge.", SCHEMA, USER_TABLE)
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
    """Get ordered list of column names from the user table."""
    cur.execute(
        """
        SELECT column_name FROM information_schema.columns
        WHERE table_schema = %s AND table_name = %s
        ORDER BY ordinal_position;
        """,
        (SCHEMA, USER_TABLE)
    )
    return [r[0] for r in cur.fetchall()]


def resolve_account_ids_for_backup(cur):
    """Create a temp table with backup users and resolved account IDs using mapping."""

    cur.execute(
        sql.SQL(
            """
            SELECT 1 FROM information_schema.tables
            WHERE table_schema = %s AND table_name = '_account_id_mapping'
            """
        ),
        (SCHEMA,)
    )
    has_mapping = bool(cur.fetchone())

    cur.execute("DROP TABLE IF EXISTS temp_user_backup_resolved;")
    if has_mapping:
        logging.info("Resolving user backup account_ids via mapping table...")
        cur.execute(
            sql.SQL(
                f"""
                CREATE TEMP TABLE temp_user_backup_resolved AS
                SELECT b.*, m.new_account_id AS resolved_account_id
                FROM {SCHEMA}.{BACKUP_TABLE} b
                LEFT JOIN {SCHEMA}._account_id_mapping m ON b.account_id = m.old_account_id;
                """
            )
        )
    else:
        logging.warning("_account_id_mapping not found; proceeding without account id remap")
        cur.execute(
            sql.SQL(
                f"""
                CREATE TEMP TABLE temp_user_backup_resolved AS
                SELECT b.*, b.account_id AS resolved_account_id
                FROM {SCHEMA}.{BACKUP_TABLE} b;
                """
            )
        )

    cur.execute("CREATE INDEX ON temp_user_backup_resolved(idp_user_id);")
    cur.execute("CREATE INDEX ON temp_user_backup_resolved(resolved_account_id);")
    logging.info("Prepared temp_user_backup_resolved with resolved account IDs")


def build_user_upsert(cur, pk_col: str) -> sql.Composed:
    """Construct an INSERT .. ON CONFLICT statement excluding PK, upserting on primary key."""
    columns = get_table_columns(cur)
    if PRIMARY_KEY not in columns:
        raise RuntimeError(f"Primary key column '{PRIMARY_KEY}' missing from {SCHEMA}.{USER_TABLE}")
    non_pk_columns = [c for c in columns if c != pk_col]
    # Ensure account_id uses resolved_account_id
    select_list = []
    for c in non_pk_columns:
        if c == 'account_id':
            select_list.append(sql.SQL('resolved_account_id'))
        else:
            select_list.append(sql.Identifier(c))

    insert_cols = sql.SQL(',').join(sql.Identifier(c) for c in non_pk_columns)
    select_cols = sql.SQL(',').join(select_list)

    # Upsert: update only account_id (and maybe later other mutable columns)
    upsert_sql = sql.SQL(
            """
            INSERT INTO {}.{} ({cols})
            SELECT {selects}
            FROM temp_user_backup_resolved b
            ON CONFLICT ({nk}) DO UPDATE
                SET account_id = EXCLUDED.account_id
            """
    ).format(
            sql.Identifier(SCHEMA), sql.Identifier(USER_TABLE),
            cols=insert_cols,
            selects=select_cols,
            nk=sql.Identifier(PRIMARY_KEY)
    )
    return upsert_sql


def cleanup_backup_table(cur, keep_account_mapping: bool = False):
    try:
        cur.execute(
            sql.SQL("DROP TABLE IF EXISTS {}.{};").format(sql.Identifier(SCHEMA), sql.Identifier(BACKUP_TABLE))
        )
        logging.info("Dropped backup table %s.%s", SCHEMA, BACKUP_TABLE)
    except Exception as drop_err:
        logging.warning("Could not drop backup table %s.%s: %s", SCHEMA, BACKUP_TABLE, drop_err)

    # Temp table
    try:
        cur.execute("DROP TABLE IF EXISTS temp_user_backup_resolved;")
    except Exception:
        pass

    # Legacy offset table removal (no longer used)
    try:
        cur.execute(f"DROP TABLE IF EXISTS {SCHEMA}._account_merge_offset;")
    except Exception:
        pass

    if not keep_account_mapping:
        try:
            cur.execute(f"DROP TABLE IF EXISTS {SCHEMA}._account_id_mapping;")
            logging.info("Dropped account id mapping table")
        except Exception as cleanup_map_err:
            logging.warning("Could not drop account id mapping table: %s", cleanup_map_err)


def post_restore_merge_target_users(dry_run: bool = False, keep_account_mapping: bool = False):
    if TARGET_DB_CONFIG is None:
        raise RuntimeError("Target DB config not available")
    
    conn = psycopg2.connect(**_psycopg2_params(TARGET_DB_CONFIG))
    try:
        with conn.cursor() as cur:
            if not validate_tables_exist(cur):
                return

            pk_col = get_pk_column(cur, SCHEMA, USER_TABLE)
            logging.info("Detected primary key column '%s' for %s.%s", pk_col, SCHEMA, USER_TABLE)

            backup_count = get_backup_count(cur)
            if backup_count == 0:
                logging.info("Backup table empty; nothing to merge.")
                return

            # Count existing matches by key prior to insert
            cur.execute(
                sql.SQL(
                    f"""
                    SELECT COUNT(*)
                    FROM {SCHEMA}.{BACKUP_TABLE} b
                    JOIN {SCHEMA}.{USER_TABLE} u ON u.{PRIMARY_KEY} = b.{PRIMARY_KEY}
                    """
                )
            )
            existing_matches = cur.fetchone()[0]

            resolve_account_ids_for_backup(cur)
            upsert_sql = build_user_upsert(cur, pk_col)

            cur.execute(upsert_sql)
            total_affected = cur.rowcount  # inserted + updated (all conflict resolutions count)
            logging.info("Upsert affected %s rows (backup: %s, pre-existing matches: %s)", total_affected, backup_count, existing_matches)

            # Build user id mapping (old -> new) post upsert
            try:
                cur.execute(sql.SQL("DROP TABLE IF EXISTS {}._user_id_mapping;").format(sql.Identifier(SCHEMA)))
                mapping_sql = sql.SQL(
                    """
                    CREATE TABLE {}._user_id_mapping AS
                    SELECT b.{pk} AS old_user_id, u.{pk} AS new_user_id, b.{nk} AS {nk}
                    FROM {}.{} b
                    JOIN {}.{} u ON u.{nk} = b.{nk};
                    """
                ).format(
                    sql.Identifier(SCHEMA),
                    sql.Identifier(SCHEMA), sql.Identifier(BACKUP_TABLE),
                    sql.Identifier(SCHEMA), sql.Identifier(USER_TABLE),
                    pk=sql.Identifier(pk_col),
                    nk=sql.Identifier(PRIMARY_KEY)
                )
                cur.execute(mapping_sql)
                cur.execute(sql.SQL("CREATE INDEX ON {}._user_id_mapping(old_user_id);").format(sql.Identifier(SCHEMA)))
                logging.info("Created _user_id_mapping table")
            except Exception as map_err:
                logging.warning("Could not create _user_id_mapping (non-fatal): %s", map_err)

            cleanup_backup_table(cur, keep_account_mapping=keep_account_mapping)
            conn.commit()
    finally:
        conn.close()

__all__ = [
    'pre_restore_backup_target_users',
    'post_restore_merge_target_users'
]
