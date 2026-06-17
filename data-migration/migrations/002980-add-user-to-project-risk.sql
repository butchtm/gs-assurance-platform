ALTER TABLE gs.project_risk_summary
ADD COLUMN user_id int4 NULL;

ALTER TABLE gs.project_risk_summary ADD CONSTRAINT fk_project_risk_summary_user_id FOREIGN KEY (user_id) REFERENCES gs.user (id);