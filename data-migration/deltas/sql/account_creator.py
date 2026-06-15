import logging
import pandas as pd
import psycopg2
from sqlalchemy import text

from database.connection import get_assurance_db_engine
from database.fetch_data import fetch_data_from_db

def run_account_upsert_sql(data: pd.DataFrame, cursor):
    accounts_upsert = data['accountsToUpsert'].copy()
    if not accounts_upsert.empty:
        try:
            logging.info(f"Running accounts upsert - {len(accounts_upsert.index)} rows to upsert.")
            for _, row in accounts_upsert.iterrows():
                account_name = row['Account_Name']
                account_exists_id = check_if_account_exists_sql(account_name)
                if not account_exists_id:
                    execute_account_insert_sql(row, cursor)
        except Exception as e:
            logging.exception(f"Failed to update accounts {e}")
            raise

def check_if_account_exists_sql(account_name):
    get_id_query = text("SELECT id FROM gs.account WHERE name = :account_name")

    try:
        id_dataframe = fetch_data_from_db(
            get_assurance_db_engine(), 
            get_id_query, 
            {'account_name': account_name}
        )
        account_id = False
        
        if id_dataframe.shape[0] == 1:
            account_id = int(id_dataframe.iloc[0]['id'])
        
        return account_id
    except Exception as e:
        logging.exception(f"Error fetching account ID: {e}")
        raise

def execute_account_insert_sql(row, conn):
    try:
        query = """
        INSERT INTO gs.account
        ("name", email_address, account_type_id, created_time, is_active, address, registry_account_id, competencies_id)
        VALUES (%(name)s, %(email_address)s, %(account_type_id)s, %(created_time)s, %(is_active)s, 
        %(address)s, %(registry_account_id)s, %(competencies_id)s);"""
        values = {'name': row['Account_Name'],
                  'email_address': row['ProjectDeveloperEmail'],
                  'account_type_id': 3, #Project Developer Account Type ID
                  'created_time': row['Created_Time'],
                  'is_active': True, #Active column is always null in SC so set to true
                  'address': None,
                  'registry_account_id': None,
                  'competencies_id': None
                  }
        conn.execute(query, values)
    except psycopg2.DatabaseError as e:
        logging.error(f"Error inserting/updating gs.document: {e}")
        raise