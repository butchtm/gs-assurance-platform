import psycopg2
from psycopg2 import sql
import logging

def connect_to_db(host, database, user, password):
    try:
        connection = psycopg2.connect(
            host=host,
            database=database,
            user=user,
            password=password
        )
        logging.info("Connection to database established.")
        return connection
    except Exception as e:
        logging.error(f"Error connecting to database: {e}")
        raise

def close_connection(connection):
    if connection:
        connection.close()
        logging.info("Database connection closed.")

def clear_target_database(connection):
    try:
        with connection.cursor() as cursor:
            cursor.execute("SELECT tablename FROM pg_tables WHERE schemaname='public';")
            tables = cursor.fetchall()
            for table in tables:
                cursor.execute(sql.SQL("DROP TABLE IF EXISTS {} CASCADE;").format(sql.Identifier(table[0])))
            connection.commit()
            logging.info("Target database cleared.")
    except Exception as e:
        logging.error(f"Error clearing target database: {e}")
        raise