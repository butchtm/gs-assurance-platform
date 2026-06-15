import logging
import sys

from database import get_assurance_db_engine
from process_project import convert_old_methodologies_to_new_structure

def main():
    logging.basicConfig(level=logging.NOTSET)
    engine = get_assurance_db_engine()
    logging.info("--- Methodologies migration started ---")
    try:
        with engine.begin() as conn:
            print("Converting Old Project Methodologies to Project Methodologies...")
            convert_old_methodologies_to_new_structure(conn)
        logging.info("--- Methodologies migration complete ---")
        sys.exit(0)
    except Exception as e:
        logging.error(f"Migration Failed. Check Errors --- {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()