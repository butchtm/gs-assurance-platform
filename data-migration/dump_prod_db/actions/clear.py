import logging
import psycopg2
from psycopg2 import sql
from db.config import Config

_config = Config()
TARGET_DB_CONFIG = getattr(_config, 'target_db', None)


def _psycopg2_params(db_cfg: dict) -> dict:
    return {
        'host': db_cfg.get('host'),
        'port': db_cfg.get('port'),
        'dbname': db_cfg.get('database'),
        'user': db_cfg.get('user'),
        'password': db_cfg.get('password'),
    }

def clear_target_database_standalone():
    """Clear all tables from the 'gs' schema and the public.databasechangelog table.
    
    Creates its own database connection using the target config.
    """
    if TARGET_DB_CONFIG is None:
        raise RuntimeError("Target database config not found. Set DUMP_AND_RESTORE_TARGET environment variable.")
    
    target_conn = None
    try:
        target_conn = psycopg2.connect(**_psycopg2_params(TARGET_DB_CONFIG))
        clear_target_database(target_conn)
        logging.info("Target database cleared successfully.")
    except Exception as e:
        logging.error(f"An error occurred while clearing the database: {e}")
        raise
    finally:
        if target_conn:
            target_conn.close()

def clear_target_database(connection):
    """Clear all tables from the 'gs' schema and the public.databasechangelog table.
    
    Excludes tables specified in the config (like gs.user).
    """
    excluded_tables = getattr(_config, 'exclude_tables', [])

    excluded_table_names = ['user', 'account']
    for table in excluded_tables:
        if '.' in table:
            excluded_table_names.append(table.split('.', 1)[1]) 
        else:
            excluded_table_names.append(table)
    
    try:
        with connection.cursor() as cursor:
            logging.info("Clearing tables from 'gs' schema (excluding: %s)...", excluded_table_names)
            cursor.execute("SELECT tablename FROM pg_tables WHERE schemaname='gs';")
            tables = cursor.fetchall()
            
            for table in tables:
                table_name = table[0]
                if table_name in excluded_table_names:
                    logging.info("Skipping excluded table: gs.%s", table_name)
                    continue
                logging.info("Dropping table: gs.%s", table_name)
                cursor.execute(f"DROP TABLE IF EXISTS gs.{table_name} CASCADE;")
            
            # Clear public.databasechangelog table
            logging.info("Clearing public.databasechangelog table...")
            cursor.execute("DROP TABLE IF EXISTS public.databasechangelog CASCADE;")
            logging.info("Dropped table: public.databasechangelog")
            
            connection.commit()
            logging.info("Database clearing completed successfully.")
            
    except psycopg2.Error as e:
        connection.rollback()
        logging.error("Error clearing database: %s", e)
        raise

def clear_target_database_with_connection(connection):
    """Clear all tables from the gs schema using an existing connection.
    
    Excludes tables specified in the config (like gs.user).
    """
    excluded_tables = getattr(_config, 'exclude_tables', [])

    excluded_table_names = []
    for table in excluded_tables:
        if '.' in table:
            excluded_table_names.append(table.split('.', 1)[1]) 
        else:
            excluded_table_names.append(table)
    
    with connection.cursor() as cursor:
        cursor.execute("SELECT tablename FROM pg_tables WHERE schemaname='gs';")
        tables = cursor.fetchall()
        for table in tables:
            table_name = table[0]
            if table_name in excluded_table_names:
                logging.info("Skipping excluded table: gs.%s", table_name)
                continue
            cursor.execute(sql.SQL("DROP TABLE IF EXISTS {}.{} CASCADE;").format(
                sql.Identifier('gs'), sql.Identifier(table_name)))
    connection.commit()