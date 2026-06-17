--Adjust dates when running this 



ALTER TABLE gs.project DISABLE TRIGGER ALL;

DO $$
DECLARE
  project1 int;
  project2 int;
  project3 int;
  request1 int;
  request2 int;
  request3 int;
BEGIN

	project1 := nextval('gs.project_id_seq');
	project2 := nextval('gs.project_id_seq');
	project3 := nextval('gs.project_id_seq');

	-- Insert projects (one per date scenario)
	INSERT INTO gs.project (id, gsid, name, project_classification_id, account_id) 
	VALUES (project1, CONCAT('GS1', project1), 'Past Project Test', 1, 1);  -- Past scenario
	
	INSERT INTO gs.project (id, gsid, name, project_classification_id, account_id) 
	VALUES (project2, CONCAT('GS2', project2), 'Current Project TEst', 1, 1);  -- Current scenario
	
	INSERT INTO gs.project (id, gsid, name, project_classification_id, account_id) 
	VALUES (project3, CONCAT('GS3', project3), 'Future Project Test', 1, 1);  -- Future scenario
	
	-- Insert crediting periods (numeric IDs)
	-- Project 1: Past period (end_date < 2025-03-03)
	INSERT INTO gs.project_crediting_period (period_id, project_id, start_date, end_date)
	VALUES (1, project1, '2025-01-01', '2025-02-28');
	
	-- Project 2: Current period (2025-03-01 <= current_date <= 2025-03-31)
	INSERT INTO gs.project_crediting_period (period_id, project_id, start_date, end_date)
	VALUES (2, project2, '2025-03-01', '2025-03-31');
	
	-- Project 3: Future period (start_date > 2025-03-03)
	INSERT INTO gs.project_crediting_period (period_id, project_id, start_date, end_date)
	VALUES (3, project3, '2025-03-10', '2025-03-20');
	
	request1 := nextval('gs.request_id_seq');
	request2 := nextval('gs.request_id_seq');
	request3 := nextval('gs.request_id_seq');

	-- Insert requests for each project, using status id 4 (COMPLETED_APPROVED) and request type 7.
	INSERT INTO gs.request (id, request_name, crediting_period, current_request_status_id, request_type_id, index, same_request_type_count, manually_added)
	VALUES
	  (request1, 'Past Request', 1, 4, 7, 1, 1, False),
	  (request2, 'Current Request', 2, 4, 7, 1, 1, False),
	  (request3, 'Future Request', 3, 4, 7, 1, 1, False);
	
	INSERT INTO gs.base_project_request (request_id, project_id)
	VALUES
	  (request1, project1),
	  (request2, project2),
	  (request3, project3);
	
	INSERT INTO gs.request_methodology (request_id, crediting_period_id, methodology_id, methodology_version)
	VALUES
	  (request1, 1, 10, 1.3),
	  (request2, 2, 20, 1.5),
	  (request3, 3, 30, 2.5);
	
	-- Insert a project methodologies - the ones on project2 should get converted
	INSERT INTO gs.project_methodology (project_id, methodology_id)
	VALUES
	  (project2, 1),
	  (project2, 2),
	  (project2, 40),
	  (project1, 1),
      (project3, 1);

END $$;

ALTER TABLE gs.project ENABLE TRIGGER ALL;

