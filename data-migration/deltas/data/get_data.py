import logging

import pandas as pd

from datatypes import RequestIdsToUpsert
from data.get_documents import gather_document_information
from data.get_invoices import gather_invoice_information
from data.get_requests import gather_request_history, gather_request_information
from data.get_projects import gather_base_project_request, gather_project_history, gather_project_history_from_project, gather_project_information_from_project, gather_project_information_from_request
from data.get_accounts import gather_account_information
from data.invoice_id_updater import compare_and_update_invoice_ids
from data.project_id_updater import compare_and_update_project_ids
from data.request_id_updater import compare_and_update_request_ids


def gather_all_information_to_update(ids: RequestIdsToUpsert, project_ids):
    logging.info('Gathering all relevant data for the request ids found.')

    request_data = {
        "requestsToInsert": pd.DataFrame(),
        "requestsToUpdate": pd.DataFrame()
    }
    project_data = pd.DataFrame()
    account_information = pd.DataFrame()
    base_project_request_data = pd.DataFrame()
    document_data = pd.DataFrame()
    invoice_data = pd.DataFrame()
    project_history_data = pd.DataFrame()
    request_history_data = pd.DataFrame()

    #Get project information both by request ids and project ids
    project_request_data = gather_project_information_from_request(ids)
    project_project_data = gather_project_information_from_project(project_ids)

    project_request_history_data = gather_project_history(ids)
    project_project_history_data = gather_project_history_from_project(project_ids)

    if ids and project_ids:
        project_data = pd.concat(
            [project_request_data, project_project_data],
            verify_integrity=True,
            ignore_index=True
        )
        project_history_data = pd.concat(
            [project_request_history_data, project_project_history_data],
            verify_integrity=True,
            ignore_index=True
        )
    elif project_ids:
        project_data = project_project_data
        project_history_data = project_project_history_data
    elif ids:
        project_data = project_request_data
        project_history_data = project_request_history_data

    if not project_data.empty:
        account_information = gather_account_information(project_data)
    
    if ids:
        request_data = gather_request_information(ids)
        base_project_request_data = gather_base_project_request(ids)
        document_data = gather_document_information(ids)
        invoice_data = gather_invoice_information(ids)
        request_history_data = gather_request_history(ids)

        #Check if any request ids/invoices mismatch between both systems
        compare_and_update_invoice_ids(ids)
        compare_and_update_project_ids(project_data)
        compare_and_update_request_ids(ids)

    return {
        "requestsToInsert": request_data["requestsToInsert"],
        "requestsToUpdate": request_data["requestsToUpdate"],
        "accountsToUpsert": account_information,
        "projectsToUpdate": project_data,
        "baseProjectRequestToUpdate": base_project_request_data,
        "documentsToInsert": document_data,
        "invoicesToInsert": invoice_data,
        "projectHistoryToUpsert": project_history_data,
        "requestHistoryToUpsert": request_history_data
    }
