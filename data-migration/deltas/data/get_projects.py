import logging
import pandas as pd
from sqlalchemy import bindparam, text
from database.connection import get_sc_data_engine
from database.fetch_data import fetch_data_from_db
import warnings
warnings.simplefilter(action='ignore', category=FutureWarning)

def replace_nans(df):

    df = df.astype(object)
    
     # Replace NaN and NaT with None
    df = df.where(pd.notnull(df) & df.applymap(lambda x: x is not pd.NaT), None)
    
    # Replace boolean NaN/None with False
    boolean_cols = df.select_dtypes(include=[bool]).columns
    df[boolean_cols] = df[boolean_cols].fillna(False)
    
    return df

def gather_base_project_request(requestids):
    request_ids_to_upsert = requestids['closedInScOpenInAssurance'] + requestids['existsInScNotInAssurance'] + requestids['requestIsInFlght']
    
    query = text("""
        SELECT * FROM BaseProjectReview bpr WHERE bpr.ReviewsID IN :request_ids_to_upsert
    """).bindparams(bindparam('request_ids_to_upsert', expanding=True))
    
    try:
        base_project_request_to_upsert = fetch_data_from_db(
            get_sc_data_engine(),
            query,
            {'request_ids_to_upsert': request_ids_to_upsert}
        )
        return base_project_request_to_upsert
    except Exception as e:
        logging.exception(f"Failed to get Base Project Request - {e}")
        return False

def gather_project_history(requestids):
    request_ids_to_upsert = requestids['closedInScOpenInAssurance'] + requestids['existsInScNotInAssurance'] + requestids['requestIsInFlght']
    
    project_history_base_query = text("""
        SELECT * FROM ProjectStateChangeLog pscl 
        LEFT JOIN BaseProjectReview bpr ON pscl.BaseProjectID = bpr.ProjectsID
        WHERE bpr.ReviewsID IN :request_ids_to_upsert
    """).bindparams(bindparam('request_ids_to_upsert', expanding=True))
    
    try:
        history_to_upsert = fetch_data_from_db(
            get_sc_data_engine(),
            project_history_base_query,
            {'request_ids_to_upsert': request_ids_to_upsert}
        )
        return history_to_upsert
    except Exception as e:
        logging.exception(f"Failed to get Project History - {e}")
        return pd.DataFrame()
    
def gather_project_history_from_project(project_ids):
    
    project_history_base_query = text("""
        SELECT * FROM ProjectStateChangeLog pscl 
        WHERE pscl.BaseProjectID IN :project_ids
    """).bindparams(bindparam('project_ids', expanding=True))
    
    try:
        history_to_upsert = fetch_data_from_db(
            get_sc_data_engine(),
            project_history_base_query,
            {'project_ids': project_ids}
        )
        return history_to_upsert
    except Exception as e:
        logging.exception(f"Failed to get Project History - {e}")
        return pd.DataFrame()


def gather_project_information_from_project(project_ids) -> pd.DataFrame:

     project_base_query = text("""
        SELECT p.*, ld.LastActivity, ld.Notes, ld.CertifiedProjectDate, 
               ld.LastDesignCertificationRenewalDate, LastPerformanceReviewDate, 
               CertifiedDesignDate, epd.TransitionPathway, epd.TransitionFromOtherScheme,
               epd.TransitionApprovalProcedure, epd.CreditingPeriodEndDateOtherSchemes, 
               epd.CreditingPeriodStartDateOtherSchemes, epd.ProjectLinkInOtherScheme
        FROM Projects p
        LEFT JOIN LUFProjectsDetails ld ON p.ID = ld.ID  
        LEFT JOIN EnergyProjectsDetails epd ON p.ID = epd.ID
        WHERE p.id IN :project_ids_to_insert
    """).bindparams(bindparam('project_ids_to_insert', expanding=True))
     
     try:
        projects_to_insert = fetch_data_from_db(
            get_sc_data_engine(),
            project_base_query,
            {'project_ids_to_insert': project_ids}
        )

        projects_to_insert = replace_nans(projects_to_insert)
        return projects_to_insert

     except Exception as e:
        logging.exception(f"Failed to get Projects from project ids - {e}")
        return pd.DataFrame()


def gather_project_information_from_request(request_ids) -> pd.DataFrame:
    request_ids_to_upsert = request_ids['closedInScOpenInAssurance'] + request_ids['existsInScNotInAssurance'] + request_ids['requestIsInFlght']

    project_request_base_query = text("""
        SELECT p.*, ld.LastActivity, ld.Notes, ld.CertifiedProjectDate,
               ld.LastDesignCertificationRenewalDate, LastPerformanceReviewDate,
               CertifiedDesignDate, epd.TransitionPathway, epd.TransitionFromOtherScheme,
               epd.TransitionApprovalProcedure, epd.CreditingPeriodEndDateOtherSchemes,
               epd.CreditingPeriodStartDateOtherSchemes, epd.ProjectLinkInOtherScheme
        FROM Projects p
        LEFT JOIN LUFProjectsDetails ld ON p.ID = ld.ID
        LEFT JOIN EnergyProjectsDetails epd ON p.ID = epd.ID
        LEFT JOIN BaseProjectReview r ON p.ID = r.ProjectsID
        WHERE r.ReviewsID IN :request_ids_to_upsert
    """).bindparams(bindparam('request_ids_to_upsert', expanding=True))

    try:
        projects_to_update = fetch_data_from_db(
            get_sc_data_engine(),
            project_request_base_query,
            {'request_ids_to_upsert': request_ids_to_upsert}
        )
       
        projects_to_update = replace_nans(projects_to_update)

        return projects_to_update
    except Exception as e:
        logging.exception(f"Failed to get Projects from request ids - {e}")
        return pd.DataFrame()

