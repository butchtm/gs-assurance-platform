import logging
import pandas as pd
import psycopg2

def run_invoice_update_sql(data: pd.DataFrame, cursor):
    invoices_update = data['invoicesToInsert'].copy()
    if not invoices_update.empty:
        try:
            logging.info(f"Running invoice upsert - {len(invoices_update.index)} rows to upsert.")
            for _, row in invoices_update.iterrows():
                execute_invoice_sql(row, cursor)

        except Exception as e:
            logging.exception(f"Failed to update invoices {e}")
            raise

def execute_invoice_sql(row, cursor):
    try:
        query = """ \
INSERT INTO gs.invoice \
(id, amount, status_id, is_archived, status_changed_at, customer_email, last_manual_check)  \
VALUES (%(id)s, %(amount)s, %(status_id)s, %(is_archived)s, %(status_changed_at)s, %(customer_email)s, %(last_manual_check)s) \
ON CONFLICT (id)  \
DO UPDATE SET  \
amount=%(amount)s, \
status_id=%(status_id)s, \
is_archived=%(is_archived)s, \
status_changed_at=%(status_changed_at)s, \
customer_email=%(customer_email)s, \
last_manual_check=%(last_manual_check)s;"""
        values = {'id': row['ID'],
                  'amount': row['Amount'],
                  'status_id': row['Status'],
                  'is_archived': row['IsArchived'],
                  'status_changed_at': row['StatusChangedAt'],
                  'customer_email': row['CustomerEmail'],
                  'last_manual_check': row['LastManualCheck']
                  }
        cursor.execute(query, values)
    except psycopg2.DatabaseError as e:
        logging.error(f"Error inserting/updating gs.invoice: {e}")
        raise