import logging

import pandas as pd
from database.connection import get_assurance_db_engine
from database.fetch_data import fetch_data_from_db
from insert import insert_methodologies, count_methodologies_on_period

all_project_methodologies = """
    select project_id, methodology_id
    FROM gs.project_methodology
"""
current_project_crediting_periods = """
    select project_id, period_id, start_date, end_date from gs.project_crediting_period pcp
    where NOW() >= pcp.start_date and NOW() <= pcp.end_date
"""

def set_project_methodologies_to_project(crediting_period, methodologies: pd.DataFrame, cursor):
    record_count = count_methodologies_on_period(crediting_period["project_id"], crediting_period["period_id"], cursor)
    logging.info(f"Number of methodologies attached to project:{crediting_period['project_id']}, in period {crediting_period['period_id']}: {record_count}")
    if record_count == 0:
        for _, row in methodologies.iterrows():
            logging.info(f"Converting old methodology to new structure on project id {row['project_id']}. With methodology_id: {row['methodology_id']}")
            insert_methodologies(
                {
                    "project_id": crediting_period["project_id"],
                    "period_id": crediting_period["period_id"],
                    "methodology_id": row["methodology_id"],
                    "version": False,
                },
                cursor
            )
        return 1
    else:
        logging.info(f"Not converting old methodology to new structure on project id {crediting_period['project_id']},"
                     f" with period_id: {crediting_period['period_id']}, as methodologies already exist on period.")
        return 0


def convert_old_methodologies_to_new_structure(cursor):
    all_methodologies = fetch_data_from_db(get_assurance_db_engine(), all_project_methodologies)
    current_crediting_periods = fetch_data_from_db(get_assurance_db_engine(), current_project_crediting_periods)
    count = 0
    for _, row in current_crediting_periods.iterrows():
        current_project_id = row["project_id"]
        methodologies = all_methodologies[all_methodologies["project_id"] == current_project_id]
        count = count + set_project_methodologies_to_project(row, methodologies, cursor)
    logging.info(f"Number of active periods: {len(current_crediting_periods)}"
                 f"\nNumber of active periods updated: {count}"
                 f"\nNumber of periods not updated due to already containing methodologies: {len(current_crediting_periods) - count}")

