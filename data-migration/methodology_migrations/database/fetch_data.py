import logging
import pandas as pd

def fetch_data_from_db(engine, query, params=None):
    data_frame = {}
    try:
        with engine.connect() as conn:
            data_frame = pd.read_sql(query, conn, params=params)
    except Exception as e:
        logging.exception(f"Error occurred when executing queries: {e}")
        raise
    return data_frame
