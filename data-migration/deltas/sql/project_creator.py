import logging
import pandas as pd
import psycopg2

def upsert_projects_sql(data: pd.DataFrame, cursor):
    projects_upsert= data['projectsToUpdate'].copy()
    projects_upsert.drop(['ProjectLocation',
                          'ProjectCategory',
                          'ProjectAssets',
                          'ZohoId',
                          'ProjectTypes',
                          'CreditingPeriod1StartDate',
                          'CreditingPeriod1EndDate',
                          'CreditingPeriod2StartDate',
                          'CreditingPeriod2EndDate',
                          'CreditingPeriod3StartDate',
                          'CreditingPeriod3EndDate',
                          'Methodologies',
                          'SDGs',
                          'CityTownCommunity',
                          'RegionStateProvince',
                          'StreetAndNumber'], axis=1, inplace=True)
    
    try:
        logging.info(f"Running project upsert - {len(projects_upsert.index)} rows to upsert.")
        for _, row in projects_upsert.iterrows():
            convert_project(row, cursor)
    except Exception as e:
        logging.exception(f"Failed to Insert Projects {e}")
        raise

def project_locations_upsert_sql(data: pd.DataFrame, cursor):
    projects = data['projectsToUpdate']
    locations_upsert = projects[['ID','ProjectLocation']]
    filtered_locations = locations_upsert[locations_upsert['ProjectLocation'].str.contains("None") == False]

    if not filtered_locations.empty:
        filtered_locations['ProjectLocation'] = filtered_locations['ProjectLocation'].apply(lambda x: list(map(int, x.split(';'))))
        filtered_locations = filtered_locations.explode('ProjectLocation') # type: ignore

        try:
            logging.info(f"Running project locations upsert - {len(locations_upsert.index)} rows to upsert.")
            for _, row in filtered_locations.iterrows():
                convert_project_attribute(row=row, table_name="project_project_location", sc_column_name="ProjectLocation",
                                            as_column_name="project_location_id", cursor=cursor, sc_project_col_name="ID")
        except Exception as e:
            logging.exception(f"Failed to Insert Project Locations {e}")
            raise

def project_assets_upsert_sql(data: pd.DataFrame, cursor):
    projects = data['projectsToUpdate']
    assets_upsert = projects[['ID', 'ProjectAssets']]

    filtered_assets = assets_upsert[
        assets_upsert['ProjectAssets'].notna()
    ].copy()

    filtered_assets['ProjectAssets'] = filtered_assets['ProjectAssets'].apply(
        lambda x: list(map(int, filter(None, x.split(';'))))
    )   

    filtered_assets = filtered_assets[filtered_assets['ProjectAssets'].apply(bool)] #remove None from list
    if not filtered_assets.empty:
        filtered_assets = filtered_assets.explode('ProjectAssets') # type: ignore

        try:
            logging.info(f"Running project assets upsert - {len(assets_upsert.index)} rows to upsert.")
            for _, row in filtered_assets.iterrows():
                convert_project_attribute(row=row,  table_name="project_project_asset", sc_column_name="ProjectAssets",
                                                                as_column_name="project_asset_id", cursor=cursor, sc_project_col_name="ID")
        except Exception as e:
            logging.exception(f"Failed to Insert Project Assets {e}")
            raise
   
def project_types_upsert_sql(data: pd.DataFrame, cursor):
    projects = data['projectsToUpdate']
    types_upsert = projects[['ID', 'ProjectTypes']]
    filtered_types = types_upsert[types_upsert['ProjectTypes'].str.contains("None") == False]
    if not filtered_types.empty:
        filtered_types['ProjectTypes'] = filtered_types['ProjectTypes'].apply(lambda x: list(map(int, x.split(';'))))
        filtered_types = filtered_types.explode('ProjectTypes') # type: ignore

        try:
            logging.info(f"Running project types upsert - {len(filtered_types.index)} rows to upsert.")
            for _, row in filtered_types.iterrows():
                convert_project_attribute(row=row,  table_name="project_project_type", sc_column_name="ProjectTypes",
                                                                as_column_name="project_type_id", cursor=cursor, sc_project_col_name="ID")
        except Exception as e:
            logging.exception(f"Failed to Insert Project Types {e}")
            raise

def project_methodologies_upsert_sql(data: pd.DataFrame, cursor):
    projects = data['projectsToUpdate']
    methodologies_upsert = projects[['ID', 'Methodologies']]
    filtered_methodologies = methodologies_upsert[methodologies_upsert['Methodologies'].str.contains("None") == False]
    if not filtered_methodologies.empty:
        filtered_methodologies['Methodologies'] = filtered_methodologies['Methodologies'].apply(lambda x: list(map(int, x.split(';'))))
        filtered_methodologies = filtered_methodologies.explode('Methodologies') # type: ignore

        try:
            logging.info(f"Running project methodologies upsert - {len(filtered_methodologies.index)} rows to upsert.")
            for _, row in filtered_methodologies.iterrows():
                convert_project_attribute(row=row, table_name="project_methodology", sc_column_name="Methodologies",
                                                                as_column_name="methodology_id", cursor=cursor, sc_project_col_name="ID")
        except Exception as e:
            logging.exception(f"Failed to Insert Project Methodologies {e}")
            raise

def project_sdgs_upsert_sql(data: pd.DataFrame, cursor):
    projects = data['projectsToUpdate']
    sdgs_upsert = projects[['ID', 'SDGs']]
    filtered_sdgs = sdgs_upsert[sdgs_upsert['SDGs'].str.contains("None") == False]
    if not filtered_sdgs.empty:
        filtered_sdgs['SDGs'] = filtered_sdgs['SDGs'].apply(lambda x: list(map(int, x.split(';'))))
        filtered_sdgs = filtered_sdgs.explode('SDGs') # type: ignore

        try:
            logging.info(f"Running project sdgs upsert - {len(filtered_sdgs.index)} rows to upsert.")
            for _, row in filtered_sdgs.iterrows():
                convert_project_attribute(row=row, table_name="project_sustainable_development_goal", sc_column_name="SDGs",
                                                                as_column_name="sustainable_development_goal_id", cursor=cursor, sc_project_col_name="ID")
        except Exception as e:
            logging.exception(f"Failed to Insert Project SDGs {e}")
            raise

def project_base_request_upsert_sql(data: pd.DataFrame, cursor):
    projects = data['baseProjectRequestToUpdate']
    bpr_upsert = projects[['ProjectsID', 'ReviewsID']]
    if not bpr_upsert.empty:
        bpr_upsert = bpr_upsert.convert_dtypes()

        try:
            logging.info(f"Running project base project requests upsert - {len(bpr_upsert.index)} rows to upsert.")
            for _, row in bpr_upsert.iterrows():
                convert_project_attribute(row=row, table_name="base_project_request", sc_column_name="ReviewsID",
                                                                as_column_name="request_id", cursor=cursor, sc_project_col_name="ProjectsID")
        except Exception as e:
            logging.exception(f"Failed to Insert Project Base Requests {e}")
            raise

def project_transition_upsert_sql(data: pd.DataFrame, cursor):
    projects = data['projectsToUpdate']
    transition_data = projects[['ID', 'ProjectLinkInOtherScheme', 'CreditingPeriodStartDateOtherSchemes',
                                'CreditingPeriodEndDateOtherSchemes', 'TransitionApprovalProcedure',
                                'TransitionFromOtherScheme', 'TransitionPathway']]
    if not transition_data.empty:
        logging.info(f"Running project transition upsert - {len(transition_data.index)} rows to upsert.")
        for _, row in transition_data.iterrows():
            execute_transition_project_upsert_sql(row, cursor)

def project_crediting_upsert_sql(data: pd.DataFrame, cursor):
    projects = data['projectsToUpdate']

    crediting_periods_upsert = projects[['ID',
                                         'CreditingPeriod1StartDate',
                                         'CreditingPeriod1EndDate',
                                         'CreditingPeriod2StartDate',
                                         'CreditingPeriod2EndDate',
                                         'CreditingPeriod3StartDate',
                                         'CreditingPeriod3EndDate']]
    logging.info(f"Running project crediting period upsert.")

    def process_period(row, period, start_col, end_col):
        try:
            convert_crediting_period(
                project_id=row['ID'],
                period=period,
                start_date=row[start_col],
                end_date=row[end_col],
                cursor=cursor
            )
        except Exception as e:
            logging.exception(f"Error processing project ID {row['ID']}, period {period}: {e}")

    period_mappings = [
        (1, 'CreditingPeriod1StartDate', 'CreditingPeriod1EndDate'),
        (2, 'CreditingPeriod2StartDate', 'CreditingPeriod2EndDate'),
        (3, 'CreditingPeriod3StartDate', 'CreditingPeriod3EndDate')
    ]

    for _, row in crediting_periods_upsert.iterrows():
        for period, start_col, end_col in period_mappings:
            if row[start_col] is not None and row[end_col] is not None:
                process_period(row, period, start_col, end_col)

def convert_crediting_period(project_id, period, start_date, end_date, cursor):
    try:
        query = """
        INSERT INTO gs.project_crediting_period (
            project_id, period_id, start_date, end_date
        )
        VALUES (
            %(project_id)s, %(period)s, %(start_date)s, %(end_date)s
        )
        ON CONFLICT (project_id, period_id)
        DO UPDATE SET
            start_date = %(start_date)s,
            end_date = %(end_date)s;
        """
        
        cursor.execute(query, {
            'project_id': project_id,
            'period': period,
            'start_date': start_date,
            'end_date': end_date
        })

    except psycopg2.DatabaseError as e:
        logging.error(f"Error inserting/updating crediting period: {e}")
        raise

def convert_project_attribute(row, table_name, sc_project_col_name, sc_column_name, as_column_name, cursor):

    try:
        query = f"""
        INSERT INTO gs.{table_name} (
            project_id, {as_column_name}
        )
        VALUES (
            %(project_id)s, %(attribute_value)s
        )
        ON CONFLICT (project_id, {as_column_name})
        DO NOTHING;
        """

        cursor.execute(query, {
            'project_id': row[sc_project_col_name],
            'attribute_value': row[sc_column_name]
        })

    except psycopg2.DatabaseError as e:
        logging.error(f"Error inserting into {table_name} for column {as_column_name}: {e}")
        raise

def get_project_account_id(account_name, cursor):
    try:
        query = "SELECT id FROM gs.account WHERE name = %s"
        cursor.execute(query, (account_name,))
        results = cursor.fetchall()

        if len(results) == 1:
            first_id = int(results[0][0])
            return first_id
        else:
            if not results: #Can't find account in SC so link to Orphaned account for manual check
                return get_project_account_id('GS Delta Orphan Account', cursor)
            else:
                raise ValueError(f"No Default Orphan Account found or Multiple IDs found found for Account name '{account_name}'")
    except Exception as e:
        logging.exception(f"Error fetching account ID: {e}")
        raise

def convert_project(row, cursor):
    try:
        project_developer_acc_id = get_project_account_id(row['ProjectDeveloperName'], cursor)

        query = """
        INSERT INTO gs.project (
            id, gsid, status, name, description, project_classification_id, 
            project_start_date, registration_type_id, has_project_assets, 
            current_standard_version_id, carbon_stream_id, project_developer_name, 
            project_developer_email, project_size_id, current_request_id, 
            project_status_id, bundle_id, design_certification_date, 
            design_certification_date_manual_set, fee_schedule_id, 
            estimated_annual_units, master_project_id, listing_date, 
            listing_date_manual_set, parent_project_id, account_id, migrated
        ) 
        VALUES (
            %(id)s, %(gsid)s, %(status)s, %(name)s, %(description)s, 
            %(project_classification_id)s, %(project_start_date)s, 
            %(registration_type_id)s, %(has_project_assets)s, 
            %(current_standard_version_id)s, %(carbon_stream_id)s, 
            %(project_developer_name)s, %(project_developer_email)s, 
            %(project_size_id)s, %(current_request_id)s, %(project_status_id)s, 
            %(bundle_id)s, %(design_certification_date)s, 
            %(design_certification_date_manual_set)s, %(fee_schedule_id)s, 
            %(estimated_annual_units)s, %(master_project_id)s, %(listing_date)s, 
            %(listing_date_manual_set)s, %(parent_project_id)s, %(account_id)s, %(migrated)s
        ) 
        ON CONFLICT (id) 
        DO UPDATE SET
            gsid = %(gsid)s,
            status = %(status)s,
            name = %(name)s,
            description = %(description)s,
            project_classification_id = %(project_classification_id)s,
            project_start_date = %(project_start_date)s,
            registration_type_id = %(registration_type_id)s,
            has_project_assets = %(has_project_assets)s,
            current_standard_version_id = %(current_standard_version_id)s,
            carbon_stream_id = %(carbon_stream_id)s,
            project_size_id = %(project_size_id)s,
            current_request_id = %(current_request_id)s,
            project_status_id = %(project_status_id)s,
            bundle_id = %(bundle_id)s,
            design_certification_date = %(design_certification_date)s,
            design_certification_date_manual_set = %(design_certification_date_manual_set)s,
            fee_schedule_id = %(fee_schedule_id)s,
            estimated_annual_units = %(estimated_annual_units)s,
            master_project_id = %(master_project_id)s,
            listing_date = %(listing_date)s,
            listing_date_manual_set = %(listing_date_manual_set)s,
            parent_project_id = %(parent_project_id)s;
        """

        cursor.execute(query, {
            'id': row['ID'],
            'gsid': row['GSID'],
            'status': row['Status'],
            'name': row['Name'],
            'description': row['Description'],
            'project_classification_id': row['ProgrammeOfActivities'],
            'project_start_date': row['ProjectStartDate'],
            'registration_type_id': row['RegistrationType'],
            'has_project_assets': row['HasProjectAssets'],
            'current_standard_version_id': row['CurrentStandardVersion'],
            'carbon_stream_id': row['CarbonStream'],
            'project_developer_name': row['ProjectDeveloperName'],
            'project_developer_email': row['ProjectDeveloperEmail'],
            'project_size_id': row['ProjectSize'],
            'current_request_id': row['CurrentReviewID'],
            'project_status_id': row['ProjectState_ProjectState'],
            'bundle_id': row['BundleID'],
            'design_certification_date': row['DesignCertificationDate'],
            'design_certification_date_manual_set': row['DesignCertificationDateManualSet'],
            'fee_schedule_id': row['FeeSchedule'],
            'estimated_annual_units': row['EstimatedAnnualUnits'],
            'master_project_id': row['MasterProjectID'],
            'listing_date': row['ListingDate'],
            'listing_date_manual_set': row['ListingDateManualSet'],
            'parent_project_id': row['ParentProjectID'],
            'account_id': project_developer_acc_id,
            'migrated': True
        })
    except psycopg2.DatabaseError as e:
        logging.error(f"Error inserting/updating project: {e}")
        raise

def execute_transition_project_upsert_sql(row, cursor):
    try:
        query = """ \
    INSERT INTO gs.transition_project \
    (project_id, project_link_in_other_scheme, crediting_period_start_date_other_schemes, \
    crediting_period_end_date_other_schemes, transition_approval_procedure_id, \
    transition_from_other_scheme_id, transition_pathway_id) \
    VALUES (%(project_id)s, %(project_link_in_other_scheme)s, %(crediting_period_start_date_other_schemes)s, \
    %(crediting_period_end_date_other_schemes)s, %(transition_approval_procedure_id)s, \
    %(transition_from_other_scheme_id)s, %(transition_pathway_id)s) \
    ON CONFLICT (project_id)  \
    DO UPDATE SET  \
    project_link_in_other_scheme=%(project_link_in_other_scheme)s, \
    crediting_period_start_date_other_schemes=%(crediting_period_start_date_other_schemes)s, \
    crediting_period_end_date_other_schemes=%(crediting_period_end_date_other_schemes)s, \
    transition_approval_procedure_id=%(transition_approval_procedure_id)s, \
    transition_from_other_scheme_id=%(transition_from_other_scheme_id)s, \
    transition_pathway_id=%(transition_pathway_id)s;"""
        values = {'project_id': row['ID'],
                  'project_link_in_other_scheme': row['ProjectLinkInOtherScheme'],
                  'crediting_period_start_date_other_schemes': row['CreditingPeriodStartDateOtherSchemes'],
                  'crediting_period_end_date_other_schemes': row['CreditingPeriodEndDateOtherSchemes'],
                  'transition_approval_procedure_id': row['TransitionApprovalProcedure'],
                  'transition_from_other_scheme_id': row['TransitionFromOtherScheme'],
                  'transition_pathway_id': row['TransitionPathway']
                  }
        cursor.execute(query, values)
    except psycopg2.DatabaseError as e:
        logging.error(f"Error inserting/updating gs.transition_project: {e}")
        raise