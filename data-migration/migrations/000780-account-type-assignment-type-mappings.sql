CREATE TABLE pg_temp.temp_assignment_account_type_map (
  assignment_type_code text NOT NULL,
  account_type_code text NOT NULL,
  PRIMARY KEY (assignment_type_code, account_type_code)
);

INSERT INTO pg_temp.temp_assignment_account_type_map
  (assignment_type_code, account_type_code)
VALUES
   ('AUDIT', 'VVB')
  ,('REVIEW', 'REVIEWER')
  ,('QA/QC', 'REVIEWER')
  ,('QA/QC', 'GS-ARM-TEAM')
  ,('COLLECT_FEE', 'GS-FINANCE')
  ,('COMPLETENESS_CHECK', 'GS-ARM-TEAM')
;

CREATE TABLE gs.assignment_type_account_type (
    assignment_type_id int4 NOT NULL,
    account_type_id int4 NOT NULL,
    PRIMARY KEY (assignment_type_id, account_type_id)
);

ALTER TABLE gs.assignment_type_account_type
ADD FOREIGN KEY (assignment_type_id) REFERENCES gs.assignment_type ON DELETE RESTRICT;

ALTER TABLE gs.assignment_type_account_type
ADD FOREIGN KEY (account_type_id) REFERENCES gs.account_type ON DELETE RESTRICT;

INSERT INTO gs.assignment_type_account_type
  (assignment_type_id, account_type_id)
SELECT
  at1.id, at2.id
FROM
  pg_temp.temp_assignment_account_type_map m
JOIN
  gs.assignment_type at1
ON
  m.assignment_type_code = at1.code
JOIN
  gs.account_type at2
ON
  m.account_type_code = at2.code
;

-- remove if DB has done tidy up
-- DELETE FROM gs.account_type
-- WHERE
--   id = 2;