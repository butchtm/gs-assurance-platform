INSERT INTO gs.pathway (id, name, index, fast_track)
VALUES
(4, 'Performance Certification | External', 4, FALSE),
(5, 'Performance Certification | Internal', 5, FALSE),
(6, 'Inclusion with VVB | External', 6, TRUE),
(7, 'Inclusion with VVB | Internal', 7, FALSE),
(8, 'Combined Design and Performance certification | External', 8, FALSE),
(9, 'Combined Design and Performance certification | Internal', 9, FALSE);

INSERT INTO gs.pathway_group (id, name, index, total_days, pathway_id)
VALUES
(8, 'Performance Certification | External', 0, 15, 4),
(9, 'Performance Certification | External', 1, 10, 4),
(10, 'Performance Certification | Internal', 0, 15, 5),
(11, 'Performance Certification | Internal', 1, 10, 5),
(12, 'Inclusion with VVB | External', 0, 10, 6),
(13, 'Inclusion with VVB | External', 1, 5, 6),
(14, 'Inclusion with VVB | Internal', 0, 10, 7),
(15, 'Inclusion with VVB | Internal', 1, 5, 7),
(16, 'Combined Design and Performance certification | External', 0, 30, 8),
(17, 'Combined Design and Performance certification | External', 1, 15, 8),
(18, 'Combined Design and Performance certification | Internal', 0, 30, 9),
(19, 'Combined Design and Performance certification | Internal', 1, 15, 9);

INSERT INTO gs.activity (id, name, display_name, index, total_days, internal, account_type_id, pathway_group_id)
VALUES
--1.5
(16, 'Assignment for project review and obtain declaration of CoI', 'Assigning for Review', 0, 4, TRUE, 4, 8),
(17, 'Conclusion of round 1 review', 'Round 1 Review', 1, 6, FALSE, 0, 8),
(18, 'Harmonisation of feedback', 'Harmonising Round 1 Feedback', 2, 5, TRUE, 4, 8),

(19, 'Conclusion of round 2+ review', 'Round 2+ Review', 0, 5, FALSE, 0, 9),
(20, 'Harmonisation of feedback', 'Harmonising Round 2+ Feedback', 1, 5, TRUE, 4, 9),
--1.6
(21, 'Assignment for project review and obtain declaration of CoI', 'Assigning for Review', 0, 2, TRUE, 4, 10),
(22, 'Conclusion of round 1 review', 'Round 1 Review', 1, 8, TRUE, 0, 10),
(23, 'Harmonisation of Feedback', 'Harmonising Round 1 Feedback', 2, 5, TRUE, 4, 10),

(24, 'Conclusion of round 2+ review', 'Round 2+ Review', 0, 5, TRUE, 0, 11),
(25, 'Harmonisation of feedback', 'Harmonising Round 2+ Feedback', 1, 5, TRUE, 4, 11),
--1.7
(26, 'Assignment for project review and obtain declaration of CoI', 'Assigning for Review', 0, 4, TRUE, 4, 12),
(27, 'Conclusion of round 1 review', 'Round 1 Review', 1, 3, FALSE, 0, 12),
(28, 'Harmonisation of feedback', 'Harmonising Round 1 Feedback', 2, 3, TRUE, 4, 12),

(29, 'Conclusion of round 2+ review', 'Round 2+ Review', 0, 3, FALSE, 0, 13),
(30, 'Harmonisation of feedback', 'Harmonising Round 2+ Feedback', 1, 2, TRUE, 4, 13),
--1.8
(31, 'Assignment for project review and obtain declaration of CoI', 'Assigning for Review', 0, 2, TRUE, 4, 14),
(32, 'Conclusion of round 1 review', 'Round 1 Review', 1, 4, TRUE, 4, 14),
(33, 'Harmonisation of feedback', 'Harmonising Round 1 Feedback', 1, 4, TRUE, 4, 14),

(34, 'Conclusion of round 2+ review', 'Round 2+ Review', 0, 3, TRUE, 0, 15),
(35, 'Harmonisation of feedback', 'Harmonising Round 2+ Feedback', 1, 2, TRUE, 4, 15),
--1.9
(36, 'Assignment for project review and obtain declaration of CoI', 'Assigning for Review', 0, 4, TRUE, 4, 16),
(37, 'Conclusion of round 1 review', 'Round 1 Review', 1, 14, FALSE, 0, 16),
(38, 'Harmonisation of feedback', 'Harmonising Round 1 Feedback', 2, 12, TRUE, 4, 16),

(39, 'Conclusion of round 2+ review', 'Round 2+ Review', 0, 9, FALSE, 0, 17),
(40, 'Harmonisation of feedback', 'Harmonising Round 2+ Feedback', 1, 6, TRUE, 4, 17),
--1.10
(41, 'Assignment for project review and obtain declaration of CoI', 'Assigning for Review', 0, 2, TRUE, 4, 18),
(42, 'Conclusion of round 1 review', 'Round 1 Review', 1, 15, TRUE, 0, 18),
(43, 'Harmonisation of feedback', 'Harmonising Round 1 Feedback', 2, 13, TRUE, 4, 18),

(44, 'Conclusion of round 2+ review', 'Round 2+ Review', 0, 8, TRUE, 0, 19),
(45, 'Harmonisation of feedback', 'Harmonising Round 2+ Feedback', 1, 7, TRUE, 4, 19);
