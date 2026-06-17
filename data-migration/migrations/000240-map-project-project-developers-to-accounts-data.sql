ALTER TABLE gs.project
ADD COLUMN account_id int4 NULL;

ALTER TABLE gs.project
ADD FOREIGN KEY (account_id) REFERENCES gs.account ON DELETE RESTRICT;

UPDATE 
  gs.project p
SET
  account_id = a.id
FROM
  gs.account a
WHERE
  p.project_developer_name = a.name;

-- TODO: Remove project.project_developer_name and project.project_developer_email after API updated