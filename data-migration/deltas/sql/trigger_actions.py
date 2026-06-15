import logging

def disable_triggers(cursor):
    logging.info("Disabling triggers on target db for current session.")
    try:
        query = "SET session_replication_role = replica;"
        cursor.execute(query)
    except Exception as e:
        logging.error(f"Failed to disable triggers on target db with exception: {e}")

def enable_triggers(cursor):
    logging.info("Re-enabling triggers on target db for current session.")
    try:
        query = "SET session_replication_role = DEFAULT;"
        cursor.execute(query)
    except Exception as e:
        logging.error(f"Failed to enable triggers on target db with exception: {e}")
