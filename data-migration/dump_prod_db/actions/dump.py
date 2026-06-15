import subprocess
import os
import psycopg2
from psycopg2 import sql
from db.config import Config

_config = Config()
SOURCE_DB_CONFIG = getattr(_config, 'source_db', None)
TARGET_DB_CONFIG = getattr(_config, 'target_db', None)

def dump_database():
    if SOURCE_DB_CONFIG is None:
        raise RuntimeError("Source database config not found. Set DUMP_AND_RESTORE_SOURCE environment variable.")
    
    dump_command = build_pg_dump_command()
    
    env = os.environ.copy()
    if SOURCE_DB_CONFIG.get('password'):
        env['PGPASSWORD'] = SOURCE_DB_CONFIG.get('password')
    
    try:
        subprocess.run(dump_command, env=env, check=True)
        print("Database dump completed successfully.")
    except subprocess.CalledProcessError as e:
        print(f"Error during database dump: {e}")

def build_pg_dump_command():
    if SOURCE_DB_CONFIG is None:
        raise RuntimeError("Source database config not found. Set DUMP_AND_RESTORE_SOURCE environment variable.")

    command = [
        "pg_dump",
        "-h", SOURCE_DB_CONFIG['host'],
        "-U", SOURCE_DB_CONFIG['user'],
        "-d", SOURCE_DB_CONFIG['database'],
        "-p", str(SOURCE_DB_CONFIG['port']),
        "-F", "c",
    ] + ["-f", "dump_file.dump"]
    
    return command