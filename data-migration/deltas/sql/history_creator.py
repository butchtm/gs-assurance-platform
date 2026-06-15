import logging
import pandas as pd
import psycopg2

def run_project_history_sql(data: pd.DataFrame, cursor):
    project_history = data['projectHistoryToUpsert']
    project_history = project_history.convert_dtypes()
    if not project_history.empty:
        try:
            logging.info(f"Running project history upsert - {len(project_history.index)} rows to upsert.")
            for _, row in project_history.iterrows():
                execute_project_history_sql(row, cursor)
        except Exception as e:
            logging.exception(f"Failed to update project history {e}")
            raise

def run_request_history_sql(data: pd.DataFrame, cursor):
    request_history = data['requestHistoryToUpsert']
    request_history = request_history.convert_dtypes()
    if not request_history.empty:
        try:
            logging.info(f"Running request history upsert - {len(request_history.index)} rows to upsert.")
            for _, row in request_history.iterrows():
                execute_request_history_sql(row, cursor)
        except Exception as e:
            logging.exception(f"Failed to update request history {e}")
            raise

def execute_project_history_sql(row, conn):
    try:
        query = """
        INSERT INTO gs.project_status_history 
        (id, previous_status_id, next_status_id, base_project_id)  
        VALUES (%(id)s, %(previous_status_id)s, %(next_status_id)s, %(base_project_id)s)
        ON CONFLICT (id)  
        DO UPDATE SET  
        base_project_id=%(base_project_id)s, 
        previous_status_id=%(previous_status_id)s,
        next_status_id=%(next_status_id)s;"""
        values = {'id': row['ID'],
                  'base_project_id': row['BaseProjectID'],
                  'previous_status_id': row['PreviousState'],
                  'next_status_id': row['NextState']}
        conn.execute(query, values)
    except psycopg2.DatabaseError as e:
        logging.error(f"Error inserting/updating gs.project_status_history: {e}")
        raise

def execute_request_history_sql(row, conn):
    try:
        query = """
        INSERT INTO gs.request_status_history 
            (id, changed_by, changed_at, previous_status_id, next_status_id, request_id)  
        VALUES (%(id)s, %(changed_by)s, %(changed_at)s, %(previous_status_id)s, %(next_status_id)s, %(request_id)s)
        ON CONFLICT (id)  
        DO UPDATE SET  
        changed_by=%(changed_by)s,
        changed_at=%(changed_at)s,
        previous_status_id=%(previous_status_id)s,
        next_status_id=%(next_status_id)s, 
        request_id=%(request_id)s;"""
        values = {'id': row['ID'],
                  'changed_by': row['ChangedBy'],
                  'changed_at': row['ChangedAt'],
                  'request_id': row['ReviewID'],
                  'previous_status_id': row['PreviousState'],
                  'next_status_id': row['NextState']
                  }
        conn.execute(query, values)
    except psycopg2.DatabaseError as e:
        logging.error(f"Error inserting/updating gs.request_status_history: {e}")
        raise