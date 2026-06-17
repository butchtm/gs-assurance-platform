import logging
import os
import sys

import numpy as np
import pandas as pd

from database import fetch_data_from_db, get_assurance_db_engine, get_sc_data_engine
from comparisons import compare_requests_to_update, compare_requests_to_insert, compare_projects_to_insert, compare_project_statuses_to_insert,compare_request_document
from data.get_data import gather_all_information_to_update
from executeUpserts import convert_to_sql_and_execute
from datatypes import RequestIdsToUpsert

assurance_request_query = "SELECT * FROM gs.request"
sc_request_query = "SELECT * FROM Reviews"

sc_in_flight_request_query = """SELECT * FROM Reviews
                                WHERE ReviewState_CurrentState 
                                NOT IN (8,7,4,5,0,2,1,3)"""

assurance_project_all_query = "SELECT * FROM gs.project"

assurance_request_document_query = "SELECT * FROM gs.request_document"
sc_request_document_query = "SELECT * FROM DocumentReview"


#Get all projects that:
#Have at least one associated request with status 0 (NOT REQUESTED)
#Do not have a request that has a status other than 0 (NOT REQUESTED)
assurance_project_query = """SELECT p.* 
                                FROM gs.project p
                                WHERE EXISTS (
                                    SELECT 1
                                    from gs.base_project_request bpr
                                    JOIN gs.request r ON bpr.request_id = r.id
                                    WHERE bpr.project_id = p.id
                                    AND r.current_request_status_id = 0
                                )
                                AND NOT EXISTS (
                                    SELECT 1
                                    from gs.base_project_request bpr
                                    JOIN gs.request r ON bpr.request_id = r.id
                                    WHERE bpr.project_id = p.id
                                    AND r.current_request_status_id <> 0
                                ); 
                        """

#Get all projects that:
#Every request on the project has a status of 0 (NOT REQUESTED)
sc_project_query = """
                    SELECT p.*
                    FROM Projects p
                    WHERE EXISTS (
                        SELECT 1
                        FROM BaseProjectReview bpr
                        JOIN Reviews r ON bpr.ReviewsID = r.ID
                        WHERE bpr.ProjectsID = p.ID
                        AND r.ReviewState_CurrentState = 0
                    )
                    AND NOT EXISTS (
                        SELECT 1
                        FROM BaseProjectReview bpr
                        JOIN Reviews r ON bpr.ReviewsID = r.ID
                        WHERE bpr.ProjectsID = p.ID
                        AND r.ReviewState_CurrentState <> 0
                    );
                    """

def main():
    logging.basicConfig(level=logging.NOTSET)

    #Set default to false if unless ENV set to true
    FINAL_MIGRATION: bool = os.getenv('FINAL_MIGRATION', 'False').lower() == 'true'
    logging.info(f"FINAL_MIGRATION = {FINAL_MIGRATION}")

    assurance_request_data = fetch_data_from_db(get_assurance_db_engine(), assurance_request_query)
    sc_request_data = fetch_data_from_db(get_sc_data_engine(), sc_request_query)

    assurance_project_all_data = fetch_data_from_db(get_assurance_db_engine(), assurance_project_all_query)
    assurance_project_data = fetch_data_from_db(get_assurance_db_engine(), assurance_project_query)
    sc_project_data = fetch_data_from_db(get_sc_data_engine(), sc_project_query)

    def request_is_in_flight():
        '''
            This function returns request ids that exist are in flight
            Exluding Statuses: Cancelled, Omitted, Completed_Approved, Completed_Rejected, 
            Not_Requested, Planned_To_Start
        '''
        in_flight_requests = []

        if FINAL_MIGRATION:
            logging.info("Fetching inflight requests....")
            sc_request_data = fetch_data_from_db(get_sc_data_engine(), sc_in_flight_request_query)
            in_flight_requests = sc_request_data['ID'].to_numpy().tolist()

        return in_flight_requests
    
    def document_meta_data_exists_in_sc_and_not_assurance():
        '''
            This function returns document ids that are in SC but missing in assurance
        '''
        request_document_missing_data = pd.DataFrame()

        if FINAL_MIGRATION:
            logging.info("Fetching ALL document request relationships....")
            assurance_request_document_data = fetch_data_from_db(get_assurance_db_engine(), assurance_request_document_query)
            sc_request_document_data = fetch_data_from_db(get_sc_data_engine(), sc_request_document_query)
            request_document_missing_data = compare_request_document(
                assurance_request_document_data, sc_request_document_data
            )

        return request_document_missing_data
    
    def request_exists_in_sc_and_not_in_assurance():
        '''
            This function returns request ids that exist in SC and don't exist in assurance.
        '''
        logging.info('Comparing requests existing in sc and not in assurance.')
        return compare_requests_to_insert(
            assurance_request_data, sc_request_data
        )

    def request_closed_exists_in_sc_and_assurance():
        '''
            This function returns request ids that are of closed status in SC and open in assurance.
        '''
        logging.info('Comparing requests closed in sc and open in assurance.')
        return compare_requests_to_update(
            assurance_request_data, sc_request_data
        )
    
    def project_exists_in_sc_and_not_in_assurance():
        '''
            This function returns project ids in SC that have only NOT_REQUESTED requests
            that do not exist in Assurance. As a request-status migration would not pick these details up.
        '''
        logging.info('Comparing projects in sc and not in assurance.')
        return compare_projects_to_insert(
            assurance_project_all_data, sc_project_data
        )
    
    def project_exists_in_both_different_statuses():
        '''
            Gets project ids from SC that exclusively have NOT_REQUESTED requests and are absent in Assurance.
            Projects that already exist in Assurance with any request having a status other than NOT_REQUESTED 
            are excluded to avoid overwriting any changes made in Assurance.
        '''
        logging.info('Comparing projects that exist in both with different statuses.')
        return compare_project_statuses_to_insert(
            assurance_project_data, sc_project_data
        )
    
    #Create dictionary of request ids to upsert
    dict_of_ids: RequestIdsToUpsert = {'existsInScNotInAssurance': request_exists_in_sc_and_not_in_assurance().tolist(),
                    'closedInScOpenInAssurance': request_closed_exists_in_sc_and_assurance().tolist(),
                    'requestIsInFlght': request_is_in_flight(),
                    }
    
    #Create dictionary of project ids to upsert
    project_ids = np.unique(
        np.concatenate([
            project_exists_in_sc_and_not_in_assurance(),
            project_exists_in_both_different_statuses()
        ])
    ).astype(int).tolist()

    if any(dict_of_ids[key] for key in dict_of_ids) or project_ids or FINAL_MIGRATION:
        raw_data = gather_all_information_to_update(dict_of_ids, project_ids)
        raw_data['missing_document_requests'] = document_meta_data_exists_in_sc_and_not_assurance()
        try:
            convert_to_sql_and_execute(raw_data, FINAL_MIGRATION)
            logging.info("--- Delta migration complete ---")
            sys.exit(0)
        except Exception as e:
            logging.info(f"Migration Failed Check Errors --- {e}")
            sys.exit(1)
    else:
        logging.info(" --- Nothing to insert currently! ---")
        sys.exit(0)


if __name__ == "__main__":
    main()