import pandas as pd

def compare_requests_to_update(project_request_assurance_df, project_request_sc_df):
    '''
    Returns an array of request IDs from SC that are closed and open in assurance.

    The function first removes open requests from the SC DataFrame by dropping rows where 
    'ReviewState_CurrentState' is one of [0, 1, 2, 3, 6, 9, 10]. 
    
    It then renames the columns in the assurance DataFrame to align with the SC DataFrame and then 
    merging on 'ID' and 'ReviewState_CurrentState'.
    
    Then it filters for requests that appear only in the SC DataFrame (shown by '_merge' equal 
    to 'left_only'), returning these request IDs as a numpy array.
    '''

    project_request_sc_df.drop(
        project_request_sc_df.loc[
            project_request_sc_df['ReviewState_CurrentState'].isin([0, 1, 2, 3, 6, 9, 10])
        ].index,
        inplace=True
    )
    
    project_request_assurance_df.rename(
        columns={"id": "ID", "current_request_status_id": "ReviewState_CurrentState"}, inplace=True
    )

    # Merge the datasets
    merged_df = pd.merge(
        project_request_sc_df,
        project_request_assurance_df,
        on=["ID", "ReviewState_CurrentState"],
        how="left",
        indicator=True,
    )

    # Output the closed request ids
    closed_known_requests = merged_df[merged_df["_merge"] == "left_only"]
    return closed_known_requests['ID'].to_numpy()


def compare_requests_to_insert(project_request_assurance_df, project_request_sc_df):
    '''
    Returns an array of request IDs from SC that do not exist in the assurance.

    The function standardises the key column by renaming 'id' to 'ID' in the assurance DataFrame.

    It then performs a left join merge on the SC and assurance DataFrames using 'ID' as the key.

    Rows that only appear in the SC DataFrame (shown by '_merge' equal to 'left_only')
    represent new or unknown requests that need to be inserted. The function returns these request IDs
    as a numpy array.
    '''
     
    # Set column names to compare to the same
    project_request_assurance_df.rename(
        columns={"id": "ID"}, inplace=True
    )

    # Merge the datasets
    merged_df = pd.merge(
        project_request_sc_df,
        project_request_assurance_df,
        on=["ID"],
        how="left",
        indicator=True,
    )

    # Output the new request ids
    new_unknown_request_ids = merged_df[merged_df["_merge"] == "left_only"]
    return new_unknown_request_ids['ID'].to_numpy()


def normalize_uuid_column(df, column_name):
    df = df.dropna(subset=[column_name]).copy()

    df[column_name] = (
        df[column_name]
        .astype(str)
        .str.strip('{}')
        .str.lower()
    )
    return df

def compare_request_document(document_request_assurance_df, document_request_sc_df):
    """
    Returns a numpy array of document IDs that are linked to requests present in both SC and assurance
    but whose document relations exist only in SC.

    """
    assurance = (
        document_request_assurance_df
        .rename(columns={'request_id': 'ReviewsID', 'document_id': 'DocumentsID'})
        .pipe(normalize_uuid_column, 'DocumentsID')
    )

    sc = (
        document_request_sc_df
        .rename(columns={'DocumentsId': 'DocumentsID', 'ReviewsID': 'ReviewsID'})
        .pipe(normalize_uuid_column, 'DocumentsID')
    )


    diff = sc.merge(
        assurance,
        on=['ReviewsID','DocumentsID'],
        how='left',
        indicator=True
    )
    missing = diff[diff['_merge']=='left_only']
    return missing[['ReviewsID','DocumentsID']]


