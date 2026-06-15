import logging

from sqlalchemy import bindparam, text
from database.connection import get_assurance_db_engine, get_sc_data_engine
from database.fetch_data import fetch_data_from_db
from datatypes import RequestIdsToUpsert


def fetch_sc_reviews_invoices(sc_engine, ids):
    ids = ids['existsInScNotInAssurance'] + ids['closedInScOpenInAssurance'] + ids['requestIsInFlght']
    query = """
        SELECT
            r.ID AS sc_request_id,
            r.InvoiceID AS invoice_id
        FROM Reviews r
        WHERE r.ID IN :ids
    """
    query = text(query).bindparams(bindparam('ids', expanding=True))
    return fetch_data_from_db(sc_engine, query, {"ids": tuple(ids)})

def fetch_assurance_requests_invoices_bulk(assurance_engine, invoice_ids):
    query = """
        SELECT 
            id AS ass_request_id,
            invoice_id
        FROM gs.request
        WHERE invoice_id IN :invoice_ids
    """
    query = text(query).bindparams(bindparam('invoice_ids', expanding=True))
    return fetch_data_from_db(
        assurance_engine,
        query,
        {"invoice_ids": tuple(invoice_ids)}
    )

def bump_invoice_id_in_assurance(invoice_id) -> bool:
    engine = get_assurance_db_engine()
    query = text("""
        UPDATE gs.invoice
           SET id = nextval('gs.invoice_id_seq')
         WHERE id = :invoice_id
    """)
    try:
        with engine.connect() as conn:
            with conn.begin():
                result = conn.execute(query, {"invoice_id": int(invoice_id)})
                return result.rowcount > 0
    except Exception:
        return False


def compare_and_update_invoice_ids(ids: RequestIdsToUpsert):
    sc_engine = get_sc_data_engine()
    assurance_engine = get_assurance_db_engine()

    try:
        sc_df = fetch_sc_reviews_invoices(sc_engine, ids)
        if sc_df.empty:
            logging.info("No SC Invoice -> Assurance ID Mismatches.")
            return

        invoice_ids = sc_df["invoice_id"].dropna().unique().tolist()
        if not invoice_ids:
            logging.info("No invoice IDs found in SC records.")
            return

        assurance_df = fetch_assurance_requests_invoices_bulk(assurance_engine, invoice_ids)

        merged_df = sc_df.merge(
            assurance_df,
            on="invoice_id",
            how="left"
        )

        #mismatched request_id -> invoice_id relations
        mismatch_df = merged_df.loc[
            (merged_df["ass_request_id"].notnull()) &
            (merged_df["ass_request_id"] != merged_df["sc_request_id"])
        ]

        if mismatch_df.empty:
            logging.info("No request->invoice mismatches found.")
            return

        for _, row in mismatch_df.iterrows():
            bump_invoice_id_in_assurance(row["invoice_id"])
            logging.info(
                f"Bumped assurance invoice_id={row['invoice_id']} to latest sequence due to mismatch"
                f"(request_id={row['ass_request_id']} != sc_id={row['sc_request_id']})"
            )

    except Exception as e:
        logging.error(f"Error when comparing and updating invoice id: {e}")
        raise