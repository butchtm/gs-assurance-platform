import logging

from sqlalchemy import bindparam, text
from database.fetch_data import fetch_data_from_db
from database.connection import get_sc_data_engine

def gather_account_information(project_data):

    project_account_names = set(project_data['ProjectDeveloperName'])
    account_query = text("""
        SELECT DISTINCT vwAc.*, REPLACE(p.ProjectDeveloperEmail, '  ', ' ') AS ProjectDeveloperEmail
        FROM vwAccounts vwAc
        LEFT JOIN Projects p ON vwAc.Account_Name = p.ProjectDeveloperName 
        WHERE vwAC.Account_Name IN :project_account_names
    """).bindparams(bindparam('project_account_names', expanding=True))

    try:
        accounts_to_upsert = fetch_data_from_db(
            get_sc_data_engine(), 
            account_query, 
            {'project_account_names': list(project_account_names)}
        )
        return accounts_to_upsert
    except Exception as e:
        logging.exception(f"Failed to get Accounts - {e}")
        raise

