ALTER TABLE gs.pathway
DROP CONSTRAINT pathway_name_key;

ALTER TABLE gs.pathway_group
DROP CONSTRAINT pathway_group_name_key;

ALTER TABLE gs.request_activity
ALTER COLUMN end_date DROP NOT NULL;

INSERT INTO gs.pathway (id, name, index, fast_track)
VALUES
(0, 'Completeness Check', 0, NULL),
(1, 'Preliminary Review Fast Track', 1, TRUE),
(2, 'QA/QC Internal Only', 2, FALSE),
(3, 'Review By RoE', 3, FALSE);

INSERT INTO gs.pathway_group (id, name, index, total_days, pathway_id)
VALUES
(0, 'Completeness Check', 0, 10, 0),
(1, 'Completeness Check', 1, 3, 0),
(2, 'Preliminary Review Fast Track', 0, 15, 1),
(3, 'Preliminary Review Fast Track', 1, 8, 1),
(4, 'QA/QC Internal Only', 0, 21, 2),
(5, 'QA/QC Internal Only', 1, 15, 2),
(6, 'Review by RoE', 0, 21, 3),
(7, 'Review by RoE', 1, 15, 3);

INSERT INTO gs.activity (id, name, index, total_days, internal, account_type_id, pathway_group_id)
VALUES
(0, 'Assignment of CC', 0, 2, TRUE, 4, 0),
(1, 'Complete Completeness Check', 1, 8, TRUE, 4, 0),
(2, 'Complete Completeness Check', 0, 3, TRUE, 4, 1),
(3, 'Assignment of PR', 0, 2, TRUE, 4, 2),
(4, 'Conclusion of Review', 1, 13, TRUE, 4, 2),
(5, 'Conclusion of Review', 0, 8, TRUE, 4, 3),
(6, 'Assignment for Project Review', 0, 2, TRUE, 4, 4),
(7, 'Conclusion of Review', 1, 10, TRUE, 0, 4),
(8, 'Harmonisation of Feedback', 2, 9, TRUE, 4, 4),
(9, 'Conclusion of Review', 0, 8, TRUE, 0, 5),
(10, 'Harmonisation of Feedback', 1, 7, TRUE, 4, 5),
(11, 'Assignment for Project Review and Obtain Declaration of CoI', 0, 4, TRUE, 4, 6),
(12, 'Conclusion of Review', 1, 9, FALSE, 0, 6),
(13, 'Harmonisation of Feedback', 2, 8, TRUE, 4, 6),
(14, 'Conclusion of Review', 0, 9, FALSE, 0, 7),
(15, 'Harmonisation of Feedback', 1, 6, TRUE, 4, 7);
