import logging

from sqlalchemy import bindparam, text
from database.connection import get_assurance_db_engine, get_sc_data_engine
from database.fetch_data import fetch_data_from_db


def fetch_non_migrated_request_information(assurance_engine, request_ids):
    query = """
        SELECT
            id AS ass_request_id,
            migrated
        FROM gs.request
        WHERE migrated = False and id IN :request_ids
    """
    query = text(query).bindparams(bindparam('request_ids', expanding=True))
    return fetch_data_from_db(
        assurance_engine,
        query,
        {"request_ids": tuple(request_ids)}
    )

def bump_request_id_in_assurance(request_id) -> bool:

    logging.info(
                f"REQUEST ID UPDATED: Bumped assurance request_id={request_id} to latest sequence due to it being a non-migrated request"
                f"and SC has a request id with the same ID"
            )
    
    engine = get_assurance_db_engine()
    query = text("""
        UPDATE gs.request
           SET id = nextval('gs.request_id_seq')
         WHERE id = :request_id
    """)
    try:
        with engine.connect() as conn:
            with conn.begin():
                result = conn.execute(query, {"request_id": int(request_id)})
                return result.rowcount > 0
    except Exception:
        return False


def compare_and_update_request_ids(ids):
    assurance_engine = get_assurance_db_engine()
    try:
        request_ids = ids['existsInScNotInAssurance'] + ids['closedInScOpenInAssurance'] + ids['requestIsInFlght']
        matched_non_migrated_ids = fetch_non_migrated_request_information(assurance_engine, request_ids)

        if matched_non_migrated_ids.empty:
            logging.info("No SC Request -> Assurance ID Mismatches.")
            return
        
        for _, row in matched_non_migrated_ids.iterrows():
            bump_request_id_in_assurance(row["ass_request_id"])

    except Exception as e:
        logging.error(f"Error when checking project ids: {e}")
        raise