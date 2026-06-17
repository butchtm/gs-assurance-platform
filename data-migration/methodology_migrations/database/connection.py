import logging
from sqlalchemy import create_engine
from dotenv import load_dotenv
import os

load_dotenv()

def get_assurance_db_insert_cursor():
    engine = get_assurance_db_engine()
    return engine.raw_connection().cursor()


def get_assurance_db_engine():
    assurance_staging_conn = {
        "host": os.getenv("ASSURANCE_DB_HOST"),
        "port": os.getenv("ASSURANCE_DB_PORT"),
        "dbname": os.getenv("ASSURANCE_DB_NAME"),
        "user": os.getenv("ASSURANCE_DB_USER"),
        "password": os.getenv("ASSURANCE_DB_PASSWORD"),
    }

    try:
        connection_url = (
            f"postgresql://{assurance_staging_conn['user']}:{assurance_staging_conn['password']}@"
            f"{assurance_staging_conn['host']}:{assurance_staging_conn['port']}/"
            f"{assurance_staging_conn['dbname']}"
        )
        return create_engine(connection_url)
    except Exception as e:
        logging.error(f"Failed to create Assurance DB engine: {e}")
        raise