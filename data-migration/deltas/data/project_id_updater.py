import logging

from sqlalchemy import bindparam, text
from database.connection import get_assurance_db_engine, get_sc_data_engine
from database.fetch_data import fetch_data_from_db


def fetch_non_migrated_project_information(assurance_engine, project_ids):
    query = """
        SELECT
            id AS ass_project_id,
            migrated
        FROM gs.project
        WHERE migrated = False and id IN :project_ids
    """
    query = text(query).bindparams(bindparam('project_ids', expanding=True))
    return fetch_data_from_db(
        assurance_engine,
        query,
        {"project_ids": tuple(project_ids)}
    )

def bump_project_id_in_assurance(project_id) -> bool:

    logging.info(
                f"PROJECT ID UPDATED: Bumped assurance project_id={project_id} to latest sequence due to it being a non-migrated project"
                f"and SC has a project id with the same ID"
            )
    
    engine = get_assurance_db_engine()
    query = text("""
        UPDATE gs.project
           SET id = nextval('gs.project_id_seq')
         WHERE id = :project_id
    """)
    try:
        with engine.connect() as conn:
            with conn.begin():
                result = conn.execute(query, {"project_id": int(project_id)})
                return result.rowcount > 0
    except Exception:
        return False


def compare_and_update_project_ids(ids):
    assurance_engine = get_assurance_db_engine()
    try:
        if ids.empty:
            return
    
        project_ids = ids["ID"].tolist()
        matched_non_migrated_ids = fetch_non_migrated_project_information(assurance_engine, project_ids)

        if matched_non_migrated_ids.empty:
            logging.info("No SC Project -> Assurance ID Mismatches.")
            return
    
        for _, row in matched_non_migrated_ids.iterrows():
            bump_project_id_in_assurance(row["ass_project_id"])

    except Exception as e:
        logging.error(f"Error when checking project ids: {e}")
        raise