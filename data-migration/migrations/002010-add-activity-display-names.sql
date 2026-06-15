ALTER TABLE gs.activity
ADD COLUMN display_name varchar NULL;

ALTER TABLE gs.request_activity
ADD COLUMN display_name varchar NULL;

UPDATE gs.activity SET display_name = 'Assigning for CC' WHERE id = 0;
UPDATE gs.activity SET display_name = 'Round 1 Completeness Check' WHERE id = 1;
UPDATE gs.activity SET display_name = 'Round 2+ Completeness Check' WHERE id = 2;
UPDATE gs.activity SET display_name = 'Assigning for Review' WHERE id = 3;
UPDATE gs.activity SET display_name = 'Round 1 Review' WHERE id = 4;
UPDATE gs.activity SET display_name = 'Round 2+ Review' WHERE id = 5;
UPDATE gs.activity SET display_name = 'Assigning for Review' WHERE id = 6;
UPDATE gs.activity SET display_name = 'Round 1 Review' WHERE id = 7;
UPDATE gs.activity SET display_name = 'Harmonising Round 1 Feedback' WHERE id = 8;
UPDATE gs.activity SET display_name = 'Round 2+ Review' WHERE id = 9;
UPDATE gs.activity SET display_name = 'Harmonising Round 2+ Feedback' WHERE id = 10;
UPDATE gs.activity SET display_name = 'Assigning for Review' WHERE id = 11;
UPDATE gs.activity SET display_name = 'Round 1 Review' WHERE id = 12;
UPDATE gs.activity SET display_name = 'Harmonising Round 1 Feedback' WHERE id = 13;
UPDATE gs.activity SET display_name = 'Round 2+ Review' WHERE id = 14;
UPDATE gs.activity SET display_name = 'Harmonising Round 2+ Feedback' WHERE id = 15;
