import logging
from sql import (request_creator, project_creator, document_creator,
                 invoice_creator, history_creator, trigger_actions, account_creator, sequence_actions, insert_into_registry_update)
from database import get_assurance_db_insert_cursor
def convert_to_sql_and_execute(data, final_migration: bool):
    cursor = get_assurance_db_insert_cursor()
    try:
        trigger_actions.disable_triggers(cursor)

        if not data['invoicesToInsert'].empty:
            invoice_creator.run_invoice_update_sql(data, cursor)

        if not data['requestsToInsert'].empty or not data['requestsToUpdate'].empty:
            request_creator.insert_requests_sql(data, cursor)
            request_creator.update_requests_sql(data, cursor)
        
        if not data['documentsToInsert'].empty:
            document_creator.run_document_update_sql(data, cursor)
            document_creator.run_base_document_request_sql(data, cursor)

        if not data['accountsToUpsert'].empty:
            account_creator.run_account_upsert_sql(data, cursor)
        
        if not data['projectsToUpdate'].empty:
            project_creator.upsert_projects_sql(data, cursor)
            project_creator.project_locations_upsert_sql(data, cursor)
            project_creator.project_assets_upsert_sql(data, cursor)
            project_creator.project_types_upsert_sql(data, cursor)
            project_creator.project_methodologies_upsert_sql(data, cursor)
            project_creator.project_transition_upsert_sql(data, cursor)
            project_creator.project_sdgs_upsert_sql(data, cursor)
            project_creator.project_crediting_upsert_sql(data, cursor)
        
        if not data['baseProjectRequestToUpdate'].empty:
            project_creator.project_base_request_upsert_sql(data, cursor)

        if not data['projectHistoryToUpsert'].empty:
            history_creator.run_project_history_sql(data, cursor)

        if not data['requestHistoryToUpsert'].empty:
            history_creator.run_request_history_sql(data, cursor)
        
        if 'projectsToUpdate' in data and not data['projectsToUpdate'].empty:
            insert_into_registry_update(data, cursor)

        if final_migration:
            document_creator.run_request_document_all_sql(data['missing_document_requests'], cursor)
            
        sequence_actions.reset_request_sequence(cursor)
        sequence_actions.reset_invoice_sequence(cursor)
        sequence_actions.reset_project_history_sequence(cursor)
        sequence_actions.reset_request_history_sequence(cursor)
        sequence_actions.reset_project_sequence(cursor)
        trigger_actions.enable_triggers(cursor)
        cursor.connection.commit()

        

    except Exception as e:
        logging.error(f"Error inserting/updating: {e}")
        cursor.connection.rollback()
        raise
    finally:
        cursor.close()
