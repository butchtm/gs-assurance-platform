import logging
import os
import subprocess
import psycopg2
from psycopg2 import sql
from db.config import Config
from .obfuscate import obfuscate_data
from .merge_users import post_restore_merge_target_users, pre_restore_backup_target_users
from .merge_accounts import post_restore_merge_target_accounts, pre_restore_backup_target_accounts
from .clear import clear_target_database_with_connection

_config = Config()
SOURCE_DB_CONFIG = getattr(_config, 'source_db', None)
TARGET_DB_CONFIG = getattr(_config, 'target_db', None)


def _psycopg2_params(db_cfg: dict) -> dict:
    return {
        'host': db_cfg.get('host'),
        'port': db_cfg.get('port'),
        'dbname': db_cfg.get('database'),
        'user': db_cfg.get('user'),
        'password': db_cfg.get('password'),
    }

def restore_data_from_dump(connection, dump_file):
    """Restore data from a PostgreSQL dump file.
    
    Handles both text SQL files and binary custom format dumps.
    For binary dumps, connection can be None as pg_restore is used directly.
    """
    with open(dump_file, 'rb') as f:
        first_bytes = f.read(5)
    
    if first_bytes == b'PGDMP':
        if TARGET_DB_CONFIG is None:
            raise RuntimeError("Target database config not found for pg_restore.")
        
        db_config = TARGET_DB_CONFIG
        
        # Build pg_restore command
        cmd = [
            'pg_restore',
            '--host', db_config.get('host', 'localhost'),
            '--port', str(db_config.get('port', 5432)),
            '--username', db_config.get('user'),
            '--dbname', db_config.get('database'),
            '--verbose',
            '--clean', 
            '--if-exists', 
            '--no-owner',
            dump_file
        ]
        
        env = os.environ.copy()
        if db_config.get('password'):
            env['PGPASSWORD'] = db_config.get('password')
        
        try:
            result = subprocess.run(cmd, env=env, capture_output=True, text=True)
            
            if result.stdout:
                logging.info("pg_restore stdout: %s", result.stdout)
            if result.stderr:
                logging.info("pg_restore stderr: %s", result.stderr)
            
            if result.returncode == 0:
                logging.info("pg_restore completed successfully")
            elif result.returncode == 1:
                logging.warning("pg_restore completed with warnings (exit code 1). This is often normal.")
                if "errors ignored on restore" in result.stderr:
                    logging.info("Some errors were ignored during restore - this is typically expected.")
            else:
                logging.error("pg_restore failed with exit code %d", result.returncode)
                raise subprocess.CalledProcessError(result.returncode, cmd, result.stdout, result.stderr)
                
        except subprocess.CalledProcessError as e:
            logging.error("pg_restore failed with exit code %d", e.returncode)
            if e.stdout:
                logging.error("pg_restore stdout: %s", e.stdout)
            if e.stderr:
                logging.error("pg_restore stderr: %s", e.stderr)
            raise
    else:
        if connection is None:
            raise RuntimeError("Database connection required for text SQL files")
            
        try:
            with open(dump_file, 'r', encoding='utf-8') as file:
                sql_script = file.read()
        except UnicodeDecodeError:
            with open(dump_file, 'r', encoding='latin-1') as file:
                sql_script = file.read()
        
        with connection.cursor() as cursor:
            cursor.execute(sql_script)
        connection.commit()

def restore_database(dump_file: str | None = None, clear_target: bool = True):
    """Restore a database from a SQL dump file.

    dump_file: path to SQL file. If None, falls back to $DUMP_FILE or 'dump_file.dump'.
    clear_target: drop all public tables before restore.
    """
    if TARGET_DB_CONFIG is None:
        raise RuntimeError("Target database config not found. Set DUMP_AND_RESTORE_TARGET environment variable.")
        
    dump_file = dump_file or os.getenv('DUMP_FILE', 'dump_file.dump')

    if not os.path.exists(dump_file):
        logging.error(f"Dump file not found: {dump_file}")
        return

    # Check if this is a binary PostgreSQL dump
    with open(dump_file, 'rb') as f:
        first_bytes = f.read(5)
    
    if first_bytes == b'PGDMP':
        logging.info("Restoring data from dump '%s'...", dump_file)

        try:
            pre_restore_backup_target_accounts()
        except Exception as e:
            logging.warning("Account pre restore backup failed: %s", e)
        
        try:
            pre_restore_backup_target_users()
        except Exception as e:
            logging.warning("User pre restore backup failed: %s", e)
        

        restore_data_from_dump(None, dump_file)  # No connection needed for pg_restore
        logging.info("Data restoration complete.")

         # Obfuscate backup table data before merge
        logging.info("Obfuscating user and accounts table table before merge...")
        try:
            obfuscate_data()
            logging.info("User backup table obfuscation complete.")
        except Exception as obf_err:
            logging.warning("Could not obfuscate user and accounts table table: %s", obf_err)

        logging.info("Attempting account merge (if backup exists)...")
        try:
            post_restore_merge_target_accounts()
        except Exception as e:
            logging.warning("Account post restore merge failed: %s", e)

        # Attempt merge regardless of env variable (safe if no backup)
        logging.info("Attempting user merge (if backup exists)...")
        try:
            post_restore_merge_target_users()
        except Exception as e:
            logging.warning("User post restore merge failed: %s", e)
            
        
    else:
        # For text SQL files, use the database connection approach
        target_conn = None
        try:
            target_conn = psycopg2.connect(**_psycopg2_params(TARGET_DB_CONFIG))

            if clear_target:
                logging.info("Clearing target database...")
                clear_target_database_with_connection(target_conn)

            logging.info("Restoring data from dump '%s'...", dump_file)
            restore_data_from_dump(target_conn, dump_file)
            logging.info("Data restoration complete.")
            logging.info("Attempting user merge (if backup exists)...")
            try:
                post_restore_merge_target_users()
            except Exception as e:
                logging.warning("User merge skipped/failed: %s", e)
                
            logging.info("Attempting account merge (if backup exists)...")
            try:
                post_restore_merge_target_accounts()
            except Exception as e:
                logging.warning("Account merge skipped/failed: %s", e)

        except Exception as e:
            logging.error(f"An error occurred during restoration: {e}")
        finally:
            if target_conn:
                target_conn.close()