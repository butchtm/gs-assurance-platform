import logging
import pandas as pd
from sqlalchemy import bindparam, text
from database.fetch_data import fetch_data_from_db
from database.connection import get_sc_data_engine

def replace_nans(df):

    df = df.astype(object)
    
    # Replace NaN with None
    df = df.where(pd.notnull(df), None)
    
    # Replace boolean NaN/None with False
    boolean_cols = df.select_dtypes(include=[bool]).columns
    df[boolean_cols] = df[boolean_cols].fillna(False)
    
    return df

def gather_request_information(requestids):
    to_update_ids = requestids['closedInScOpenInAssurance'] + requestids['requestIsInFlght']
    to_insert_ids = requestids['existsInScNotInAssurance']
    
    review_base_query = "SELECT * FROM Reviews r WHERE r.ID IN :ids"

    requests_to_update = pd.DataFrame()
    requests_to_insert = pd.DataFrame()

    if to_update_ids:
        query_update = text(review_base_query).bindparams(bindparam('ids', expanding=True))
        try:
            requests_to_update = fetch_data_from_db(
                get_sc_data_engine(),
                query_update,
                {'ids': to_update_ids}
            )
            requests_to_update = replace_nans(requests_to_update)

             
            # Update in-flight requst IDs to have status 'In Preperation'
            in_flight_ids = set(requestids['requestIsInFlght'])
            in_flight_request_rows = requests_to_update['ID'].isin(in_flight_ids)
            requests_to_update.loc[in_flight_request_rows, 'ReviewState_CurrentState'] = 6

        except Exception as e:
            logging.exception(f"Failed to get requests to update - {e}")
            raise

    if to_insert_ids:
        query_insert = text(review_base_query).bindparams(bindparam('ids', expanding=True))
        try:
            requests_to_insert = fetch_data_from_db(
                get_sc_data_engine(),
                query_insert,
                {'ids': to_insert_ids}
            )
            requests_to_insert = replace_nans(requests_to_insert)
        except Exception as e:
            logging.exception(f"Failed to get requests to insert - {e}")
            raise

    return {
        "requestsToInsert": requests_to_insert,
        "requestsToUpdate": requests_to_update,
    }

def gather_request_history(requestids):
    request_ids_to_upsert = requestids['closedInScOpenInAssurance'] + requestids['existsInScNotInAssurance'] + requestids['requestIsInFlght']
    
    request_history_query = text("""
        SELECT * FROM ReviewStateChangeLog rscl WHERE rscl.ReviewID IN :request_ids_to_upsert
    """).bindparams(bindparam('request_ids_to_upsert', expanding=True))
    
    try:
        history_to_upsert = fetch_data_from_db(
            get_sc_data_engine(),
            request_history_query,
            {'request_ids_to_upsert': request_ids_to_upsert}
        )
        return history_to_upsert
    except Exception as e:
        logging.exception(f"Failed to get requests to update - {e}")
        return False
    