--
-- Add permissions for select on public stakeholders views
--
GRANT SELECT ON gs.v_public_stakeholder_projects TO gs_assurance_db_writer;
GRANT SELECT ON gs.v_public_stakeholder_requests TO gs_assurance_db_writer;
GRANT SELECT ON gs.v_public_stakeholder_documents TO gs_assurance_db_writer;
