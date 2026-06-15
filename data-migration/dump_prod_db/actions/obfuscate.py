import logging
import psycopg2
import random
import string
from psycopg2 import sql
from db.config import Config
from .clear import _psycopg2_params

_config = Config()
TARGET_DB_CONFIG = getattr(_config, 'target_db', None)
SCHEMA = 'gs'
USER_TABLE = 'user'
ACCOUNT_TABLE = 'account'


def generate_random_string(length: int, prefix: str = '') -> str:
    """Generate a random string of specified length with optional prefix."""
    chars = string.ascii_lowercase + string.digits
    random_part = ''.join(random.choice(chars) for _ in range(length))
    return f"{prefix}{random_part}"


def generate_random_email(domain: str = 'example.com') -> str:
    """Generate a random email address."""
    username = generate_random_string(8)
    return f"{username}@{domain}"


def obfuscate_data():
    """Obfuscate usernames, email addresses, and family names in user and account tables.
    
    Creates its own database connection using the target config.
    """
    if TARGET_DB_CONFIG is None:
        raise RuntimeError("Target database config not found. Set DUMP_AND_RESTORE_TARGET environment variable.")
    
    target_conn = None
    try:
        target_conn = psycopg2.connect(**_psycopg2_params(TARGET_DB_CONFIG))
        target_conn.autocommit = False
        
        with target_conn.cursor() as cursor:
            logging.info("Obfuscating usernames, email addresses, and family names in gs.user table...")
            obfuscate_user_table(cursor)
            
            logging.info("Obfuscating email addresses in gs.account table...")
            obfuscate_account_table(cursor)
            
            target_conn.commit()
            logging.info("Data obfuscation completed successfully.")
            
    except Exception as e:
        if target_conn:
            target_conn.rollback()
        logging.error(f"An error occurred while obfuscating data: {e}")
        raise
    finally:
        if target_conn:
            target_conn.close()


def obfuscate_user_table(cursor):
    """Obfuscate usernames, email addresses, and family names in the user table."""
    try:
        # Get all user records
        select_query = sql.SQL("SELECT id, user_name, email_address, family_name FROM {}.{}").format(
            sql.Identifier(SCHEMA), sql.Identifier(USER_TABLE)
        )
        cursor.execute(select_query)
        users = cursor.fetchall()
        
        logging.info(f"Found {len(users)} users to obfuscate")
        
        used_usernames = set()
        used_emails = set()
        used_family_names = set()
        
        for user_id, original_username, original_email, original_family_name in users:
            new_username = generate_random_string(10, 'user_')
            while new_username in used_usernames:
                new_username = generate_random_string(10, 'user_')
            used_usernames.add(new_username)
            
            new_email = generate_random_email()
            while new_email in used_emails:
                new_email = generate_random_email()
            used_emails.add(new_email)
            
            new_family_name = generate_random_string(8, 'surname_')
            while new_family_name in used_family_names:
                new_family_name = generate_random_string(8, 'surname_')
            used_family_names.add(new_family_name)
            
            update_query = sql.SQL("""
                UPDATE {}.{} 
                SET user_name = %s, email_address = %s, family_name = %s 
                WHERE id = %s
            """).format(
                sql.Identifier(SCHEMA), sql.Identifier(USER_TABLE)
            )
            
            cursor.execute(update_query, (new_username, new_email, new_family_name, user_id))
            logging.debug(f"Updated user {user_id}: {original_username} -> {new_username}, {original_email} -> {new_email}, {original_family_name} -> {new_family_name}")
        
        logging.info(f"Successfully obfuscated {len(users)} user records")
        
    except psycopg2.Error as e:
        logging.error(f"Error obfuscating user table: {e}")
        raise


def obfuscate_account_table(cursor):
    """Obfuscate email addresses in the account table."""
    try:
        select_query = sql.SQL("SELECT id, email_address FROM {}.{}").format(
            sql.Identifier(SCHEMA), sql.Identifier(ACCOUNT_TABLE)
        )
        cursor.execute(select_query)
        accounts = cursor.fetchall()
        
        logging.info(f"Found {len(accounts)} accounts to obfuscate")
        
        # Track used emails to ensure uniqueness
        used_emails = set()
        
        for account_id, original_email in accounts:

            new_email = generate_random_email('obfuscated.com')
            while new_email in used_emails:
                new_email = generate_random_email('obfuscated.com')
            used_emails.add(new_email)
            
            update_query = sql.SQL("""
                UPDATE {}.{} 
                SET email_address = %s 
                WHERE id = %s
            """).format(
                sql.Identifier(SCHEMA), sql.Identifier(ACCOUNT_TABLE)
            )
            
            cursor.execute(update_query, (new_email, account_id))
            logging.debug(f"Updated account {account_id}: {original_email} -> {new_email}")
        
        logging.info(f"Successfully obfuscated {len(accounts)} account records")
        
    except psycopg2.Error as e:
        logging.error(f"Error obfuscating account table: {e}")
        raise