import pandas as pd

def compare_projects_to_insert(project_assurance_df, project_sc_df):
    ''''    
        Returns an array of project IDs from the SC system that do not exist in the assurance system.

        The function standardises the column names by renaming 'id' to 'ID' in the assurance dataframe.
        It then performs a left join merge on the SC dataframe (left) and the assurance dataframe (right)
        using the 'ID' column with an indicator to mark the source of each row. Then it filters for rows
        that appear only in the SC dataframe (i.e where '_merge' equals 'left_only') and returns their project IDs.
    '''
    # Set column names to compare to the same
    project_assurance_df.rename(
        columns={"id": "ID"}, inplace=True
    )

    # Merge the datasets
    merged_df = pd.merge(
        project_sc_df,
        project_assurance_df,
        on=["ID"],
        how="left",
        indicator=True,
    )

    # Output the project ids not in assurance without requests
    project_id_df = merged_df[merged_df["_merge"] == "left_only"]
    return project_id_df['ID'].to_numpy()

def compare_project_statuses_to_insert(project_assurance_df, project_sc_df):
    '''
        Returns an array of project IDs where the status from the SC system does not match 
        the status from the assurance system.

        The function filters the merged dataframe (result of an inner join on project IDs) 
        to include only rows present in both dataframes (shown by '_merge' == 'both') and 
        then compares the 'ProjectState_ProjectState' column (SC system) with the 
        'project_status_id' column (assurance system). It returns the project IDs where these 
        status values differ.
    '''

    # Set column names to compare to the same
    project_assurance_df.rename(
        columns={"id": "ID"}, inplace=True
    )

    merged_df = pd.merge(
        project_sc_df,
        project_assurance_df,
        on=["ID"],
        how="inner",
        indicator=True,
        suffixes=("_sc", "_assurance")
    )

    status_diff = (
        (merged_df["_merge"] == "both") &
        (merged_df["ProjectState_ProjectState"] != merged_df["project_status_id"])
    )

    # Return project ids where status are different
    return merged_df.loc[status_diff, "ID"].to_numpy()

