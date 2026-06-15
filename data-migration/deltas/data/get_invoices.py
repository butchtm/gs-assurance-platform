import logging

import pandas as pd
from sqlalchemy import bindparam, text
from database.fetch_data import fetch_data_from_db
from database.connection import get_sc_data_engine

def replace_nans(df):

    df = df.astype(object)
    
     # Replace NaN and NaT with None
    df = df.where(pd.notnull(df) & df.applymap(lambda x: x is not pd.NaT), None)
    
    # Replace boolean NaN/None with False
    boolean_cols = df.select_dtypes(include=[bool]).columns
    df[boolean_cols] = df[boolean_cols].fillna(False)
    
    return df

def gather_invoice_information(requestids):
    closed_changed_ids = requestids['closedInScOpenInAssurance'] + requestids['existsInScNotInAssurance'] + requestids['requestIsInFlght']
    
    invoice_query = text("""
        SELECT i.*, r.ID as ReviewsID 
        FROM Invoices i 
        LEFT JOIN Reviews r ON i.ID = r.InvoiceID 
        WHERE r.ID IN :closed_changed_ids
    """).bindparams(bindparam('closed_changed_ids', expanding=True))
    
    try:
        invoices_to_insert = fetch_data_from_db(
            get_sc_data_engine(),
            invoice_query,
            {'closed_changed_ids': closed_changed_ids}
        )
        invoices_to_insert['IsArchived'] = invoices_to_insert['IsArchived'].astype(bool)
        invoices_to_insert = replace_nans(invoices_to_insert)
        return invoices_to_insert
    except Exception as e:
        logging.exception(f"Failed to get Invoices - {e}")
        return False