import logging
import pandas as pd

def insert_requests_sql(data: pd.DataFrame, cursor):
    if not data['requestsToInsert'].empty:
        requests_insert = data['requestsToInsert'].copy()
        requests_insert.drop(['AssignedExternalReviewer', 'AssignedQAReviewer'], axis=1, inplace=True)
        try:
            logging.info(f"Running requests insert - {len(requests_insert.index)} rows to insert.")
            for _, row in requests_insert.iterrows():
                convert_request_insert(cursor, row)
        except Exception as e:
            logging.exception(f"Failed to Insert Requests {e}")
            raise
    else:
        logging.info("No Requests to Insert Currently - Skipping")

def update_requests_sql(data: pd.DataFrame, cursor):
    if not data['requestsToUpdate'].empty:
        requests_update = data['requestsToUpdate'].copy()
        requests_update.drop(['AssignedExternalReviewer', 'AssignedQAReviewer'], axis=1, inplace=True)
        try:
            logging.info(f"Running requests upsert - {len(requests_update.index)} rows to upsert.")
            for _, row in requests_update.iterrows():
                convert_request_upsert(cursor, row)
        except Exception as e:
            logging.exception(f"Failed to Update Requests {e}")
            raise
    else:
        logging.info("No Requests to Update Currently")

def convert_request_insert(cursor, row):
    try:
        query = """
        INSERT INTO gs.request (
            id, end_date, booking_date, request_type_id, index,
            request_name, last_request_status_change_date,
            current_request_status_id, crediting_period, invoice_id,
            same_request_type_count, request_document_status,
            replacement_request_id, emission_reduction_amount,
            manually_added, migrated
        )
        VALUES (
            %(id)s, %(end_date)s, %(booking_date)s, %(request_type_id)s, %(index)s,
            %(request_name)s, %(last_request_status_change_date)s,
            %(current_request_status_id)s, %(crediting_period)s, %(invoice_id)s,
            %(same_request_type_count)s, %(request_document_status)s,
            %(replacement_request_id)s, %(emission_reduction_amount)s,
            %(manually_added)s, %(migrated)s
        );
        """

        cursor.execute(query, {
            'id': row['ID'],
            'end_date': row['EndDate'],
            'booking_date': row['BookingDate'],
            'request_type_id': row['ReviewType'],
            'index': row['Index'],
            'request_name': row['ReviewName'],
            'last_request_status_change_date': row['ReviewState_LastStateChangeDate'],
            'current_request_status_id': row['ReviewState_CurrentState'],
            'crediting_period': row['ReviewCreditingPeriod'],
            'invoice_id': row['InvoiceID'],
            'same_request_type_count': row['SameReviewTypeCount'],
            'request_document_status': row['ReviewDocumentStatus'],
            'replacement_request_id': row['ReplacementReviewId'],
            'emission_reduction_amount': row['EmissionReductionAmount'],
            'manually_added': row['ManuallyAdded'],
            'migrated': True
        })
    except Exception as e:
        logging.error(f"Error inserting request: {e}")
        raise

def convert_request_upsert(cursor, row):
    try:
        query = """
        INSERT INTO gs.request (
            id, end_date, booking_date, request_type_id, index,
            request_name, last_request_status_change_date,
            current_request_status_id, crediting_period, invoice_id,
            same_request_type_count, request_document_status,
            replacement_request_id, emission_reduction_amount,
            manually_added, migrated
        )
        VALUES (
            %(id)s, %(end_date)s, %(booking_date)s, %(request_type_id)s, %(index)s,
            %(request_name)s, %(last_request_status_change_date)s,
            %(current_request_status_id)s, %(crediting_period)s, %(invoice_id)s,
            %(same_request_type_count)s, %(request_document_status)s,
            %(replacement_request_id)s, %(emission_reduction_amount)s,
            %(manually_added)s, %(migrated)s
        )
        ON CONFLICT (id)
        DO UPDATE SET
            end_date = %(end_date)s,
            booking_date = %(booking_date)s,
            request_type_id = %(request_type_id)s,
            index = %(index)s,
            request_name = %(request_name)s,
            last_request_status_change_date = %(last_request_status_change_date)s,
            current_request_status_id = %(current_request_status_id)s,
            crediting_period = %(crediting_period)s,
            invoice_id = %(invoice_id)s,
            same_request_type_count = %(same_request_type_count)s,
            request_document_status = %(request_document_status)s,
            replacement_request_id = %(replacement_request_id)s,
            emission_reduction_amount = %(emission_reduction_amount)s,
            manually_added = %(manually_added)s,
            migrated = %(migrated)s;
        """

        cursor.execute(query, {
            'id': row['ID'],
            'end_date': row['EndDate'],
            'booking_date': row['BookingDate'],
            'request_type_id': row['ReviewType'],
            'index': row['Index'],
            'request_name': row['ReviewName'],
            'last_request_status_change_date': row['ReviewState_LastStateChangeDate'],
            'current_request_status_id': row['ReviewState_CurrentState'],
            'crediting_period': row['ReviewCreditingPeriod'],
            'invoice_id': row['InvoiceID'],
            'same_request_type_count': row['SameReviewTypeCount'],
            'request_document_status': row['ReviewDocumentStatus'],
            'replacement_request_id': row['ReplacementReviewId'],
            'emission_reduction_amount': row['EmissionReductionAmount'],
            'manually_added': row['ManuallyAdded'],
            'migrated': True
        })
    except Exception as e:
        logging.error(f"Error updating request: {e}")
        raise
