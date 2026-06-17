import logging


def reset_request_sequence(cursor):
    logging.info("Resetting gs.request_id_seq to max id + 1 from gs.request.")
    try:
        cursor.execute("SELECT max(id) FROM gs.request;")
        max_id = cursor.fetchone()[0] or 0
        if max_id:
            cursor.execute(f"SELECT setval('gs.request_id_seq', {max_id + 1});")
        else:
            logging.error("No MAX ID found for Requests table")
    except Exception as e:
        logging.error(f"Failed to reset sequence with exception: {e}")

def reset_project_sequence(cursor):
    logging.info("Resetting gs.project_id_seq to max id + 1 from gs.project.")
    try:
        cursor.execute("SELECT max(id) FROM gs.project;")
        max_id = cursor.fetchone()[0] or 0
        if max_id:
            cursor.execute(f"SELECT setval('gs.project_id_seq', {max_id + 1});")
        else:
            logging.error("No MAX ID found for Project table")
    except Exception as e:
        logging.error(f"Failed to reset sequence with exception: {e}")

def reset_invoice_sequence(cursor):
    logging.info("Resetting gs.invoice_id_seq to max id + 1 from gs.invoice.")
    try:
        cursor.execute("SELECT max(id) FROM gs.invoice;")
        max_id = cursor.fetchone()[0] or 0
        if max_id:
            cursor.execute(f"SELECT setval('gs.invoice_id_seq', {max_id + 1});")
        else:
            logging.error("No MAX ID found for Invoice table")
    except Exception as e:
        logging.error(f"Failed to reset sequence with exception: {e}")

def reset_request_history_sequence(cursor):
    logging.info("Resetting gs.request_status_history_id_seq to max id + 1 from gs.request_status_history.")
    try:
        cursor.execute("SELECT max(id) FROM gs.request_status_history;")
        max_id = cursor.fetchone()[0] or 0
        if max_id:
            cursor.execute(f"SELECT setval('gs.request_status_history_id_seq', {max_id + 1});")
        else:
            logging.error("No MAX ID found for Request Status History table")
    except Exception as e:
        logging.error(f"Failed to reset sequence with exception: {e}")

def reset_project_history_sequence(cursor):
    logging.info("Resetting gs.project_status_history_id_seq to max id + 1 from gs.project_status_history.")
    try:
        cursor.execute("SELECT max(id) FROM gs.project_status_history;")
        max_id = cursor.fetchone()[0] or 0
        if max_id:
            cursor.execute(f"SELECT setval('gs.project_status_history_id_seq', {max_id + 1});")
        else:
            logging.error("No MAX ID found for Project Status History table")
    except Exception as e:
        logging.error(f"Failed to reset sequence with exception: {e}")