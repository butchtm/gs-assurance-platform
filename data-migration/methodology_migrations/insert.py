import logging
from sqlalchemy import text

def insert_methodologies(methodology, conn):
    query = text(
        """
        INSERT INTO gs.project_crediting_period_methodology (project_id, period_id, methodology_id, "version")
        VALUES (:project_id, :period_id, :methodology_id, :version)
        ON CONFLICT DO NOTHING;
        """
    )
    try:
        conn.execute(
            query,
            {
                "project_id": methodology["project_id"],
                "period_id": methodology["period_id"],
                "methodology_id": int(methodology["methodology_id"]),
                "version": methodology.get("methodology_version", None),
            },
        )
    except Exception as e:
        logging.error(f"Error inserting into project_crediting_period_methodology: {e}")
        raise

def count_methodologies_on_period(project_id, period, conn):
    query = text(
        """SELECT
                count(*)
            FROM
                gs.project_crediting_period_methodology pcpm 
            WHERE 
                pcpm.project_id = :project_id
                AND pcpm.period_id = :period_id;"""
    )
    try:
        return conn.execute(
            query,
            {
                "project_id": project_id,
                "period_id": period,
            },
        ).fetchall()[0][0]
    
    except Exception as e:
        logging.error(f"Error inserting into project_crediting_period_methodology: {e}")
        raise
