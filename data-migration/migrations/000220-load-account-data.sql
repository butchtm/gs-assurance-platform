-- Drive the account set up from the MIGRATED projects
-- TODO: There may be accounts from projects that haven't come across (due to other dq issues)
INSERT INTO gs.account (
	name,
    email_address,
	account_type_id,
	created_time
)
WITH pd_contacts (
    project_developer_name,
    project_developer_email
) 
AS
(
    SELECT DISTINCT
        project_developer_name,
        project_developer_email
    FROM
        gs.project
)
SELECT 
    project_developer_name, 
    CASE
        WHEN (count(1) > 1) THEN
            'GS TO DISAMBIGUATE'
        ELSE
            min(project_developer_email)
    END CASE,
    (
        SELECT
            id
        FROM 
            gs.account_type
        WHERE
            code = 'PROJECT-DEVELOPER'
    ),
    now()
FROM
  pd_contacts
GROUP BY
  project_developer_name;
