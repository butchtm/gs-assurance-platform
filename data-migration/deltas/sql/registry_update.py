import logging

def insert_into_registry_update(data, cursor):
    """
    Inserts unique project IDs into the registry_update table for registry platform updates.
    Args:
        data (dict): Dictionary containing a DataFrame under the 'projectsToUpdate' key.
        cursor: Database cursor to execute the insert queries.
    """
    try:
        if 'projectsToUpdate' not in data or data['projectsToUpdate'].empty:
            logging.warning("No projects to update or 'projectsToUpdate' is missing in data.")
            return

        projects_to_update = data['projectsToUpdate']

        if 'ID' not in projects_to_update.columns:
            logging.error("'ID' column is missing from projectsToUpdate.")
            raise ValueError("'ID' column is required in projectsToUpdate.")

        project_ids = list(set(projects_to_update['ID'].tolist()))
        logging.info(f"Preparing to insert unique project IDs: {project_ids}")

        for project_id in project_ids:
            try:
                cursor.execute("""
                    INSERT INTO gs.registry_update (project_id, created_timestamp, attempts)
                    VALUES (%s, NOW(), 0)
                    ON CONFLICT DO NOTHING
                """, (project_id,))
                logging.info(f"Inserted project ID: {project_id}")
            except Exception as e:
                logging.error(f"Failed to insert project ID {project_id}: {e}")
                raise

        logging.info(f"Successfully processed {len(project_ids)} unique project(s) for registry_update.")

    except Exception as e:
        logging.error(f"Failed to insert into registry_update: {e}")
        raise
