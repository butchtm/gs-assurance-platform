import logging

from sqlalchemy import bindparam, text
from database.fetch_data import fetch_data_from_db
from database.connection import get_sc_data_engine
import warnings
warnings.simplefilter(action='ignore', category=FutureWarning)

def gather_document_information(requestids):
    closed_changed_ids = requestids['closedInScOpenInAssurance'] + requestids['existsInScNotInAssurance'] + requestids['requestIsInFlght']
    
    document_query = text("""
        SELECT d.*, dr.ReviewsID 
        FROM Documents d 
        LEFT JOIN DocumentReview dr ON dr.DocumentsID = d.ID 
        WHERE dr.ReviewsID IN :closed_changed_ids
    """).bindparams(bindparam('closed_changed_ids', expanding=True))
    
    try:
        documents_to_insert = fetch_data_from_db(
            get_sc_data_engine(), 
            document_query, 
            {'closed_changed_ids': closed_changed_ids}
        )
        documents_to_insert['IsLatest'] = documents_to_insert['IsLatest'].fillna(False)
        documents_to_insert[['IsLatest', 'Access', 'Category']] = documents_to_insert[['IsLatest', 'Access', 'Category']].astype(bool)
        documents_to_insert['Hash'] = documents_to_insert['Hash'].apply(lambda x: x.decode() if x is not None else None)
        return documents_to_insert
    except Exception as e:
        logging.exception(f"Failed to get Documents - {e}")
        raise

