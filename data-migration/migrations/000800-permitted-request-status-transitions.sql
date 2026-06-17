--
-- There's no IN_AUDIT status in the ingested ref data - we need one.
--
INSERT INTO gs.request_status
  (id, code, description)
VALUES
  (11, 'IN_AUDIT', 'In Audit');

--
-- There's no IN_ASSIGNMENT status - we need one.
--
INSERT INTO gs.request_status
  (id, code, description)
VALUES
  (12, 'IN_ASSIGNMENT', 'In Assignment');

--
-- There's no IN_ROUND_COMPLETION status - we need one.
--
INSERT INTO gs.request_status
  (id, code, description)
VALUES
  (13, 'IN_ROUND_COMPLETION', 'In Round Completion');

--
-- Temp table defining the permitted status transitions and additional condition on round of review count
--
CREATE TABLE pg_temp.temp_request_status_transition (
  request_status_code text NOT NULL,
  permitted_request_status_code text NOT NULL,
  minimum_round_of_review_count int4 NULL,
  PRIMARY KEY (request_status_code, permitted_request_status_code)
);

INSERT INTO pg_temp.temp_request_status_transition
  (request_status_code, permitted_request_status_code, minimum_round_of_review_count)
VALUES
   ('IN_PREPARATION', 'IN_AUDIT', null)
  ,('IN_PREPARATION', 'COMPLETENESS_CHECK', null)
  ,('IN_PREPARATION', 'IN_PROGRESS', 1)
  ,('IN_PREPARATION', 'OMITTED', null)
  ,('IN_PREPARATION', 'CANCELLED', null)

  ,('IN_AUDIT', 'COMPLETENESS_CHECK', null)
  ,('IN_AUDIT', 'IN_PROGRESS', 1)
  ,('IN_AUDIT', 'OMITTED', null)
  ,('IN_AUDIT', 'CANCELLED', null)

  ,('COMPLETENESS_CHECK', 'IN_PREPARATION', null)
  ,('COMPLETENESS_CHECK', 'IN_AUDIT', null)
  ,('COMPLETENESS_CHECK', 'IN_ASSIGNMENT', null)
  ,('COMPLETENESS_CHECK', 'OMITTED', null)
  ,('COMPLETENESS_CHECK', 'CANCELLED', null)

  ,('IN_ASSIGNMENT', 'IN_PROGRESS', null)
  ,('IN_ASSIGNMENT', 'OMITTED', null)
  ,('IN_ASSIGNMENT', 'CANCELLED', null)

  ,('IN_PROGRESS', 'IN_ROUND_COMPLETION', null)
  ,('IN_PROGRESS', 'OMITTED', null)
  ,('IN_PROGRESS', 'CANCELLED', null)

  ,('IN_ROUND_COMPLETION', 'IN_PREPARATION', null)
  ,('IN_ROUND_COMPLETION', 'IN_AUDIT', null)
  ,('IN_ROUND_COMPLETION', 'COMPLETED_APPROVED', null)
  ,('IN_ROUND_COMPLETION', 'COMPLETED_REJECTED', null)
  ,('IN_ROUND_COMPLETION', 'OMITTED', null)
  ,('IN_ROUND_COMPLETION', 'CANCELLED', null)
;

--
-- Permitted request status transitions
--
CREATE TABLE gs.request_status_transition (
    request_status_id int4 NOT NULL,
    permitted_request_status_id int4 NOT NULL,
    minimum_round_of_review_count int4 NULL,
    PRIMARY KEY (request_status_id, permitted_request_status_id)
);

ALTER TABLE gs.request_status_transition
ADD FOREIGN KEY (request_status_id) REFERENCES gs.request_status ON DELETE RESTRICT;

ALTER TABLE gs.request_status_transition
ADD FOREIGN KEY (permitted_request_status_id) REFERENCES gs.request_status ON DELETE RESTRICT;

INSERT INTO gs.request_status_transition
  (request_status_id, permitted_request_status_id, minimum_round_of_review_count)
SELECT
  rs1.id, rs2.id, st.minimum_round_of_review_count
FROM
  pg_temp.temp_request_status_transition st
JOIN
  gs.request_status rs1
ON
  st.request_status_code = rs1.code
JOIN
  gs.request_status rs2
ON
  st.permitted_request_status_code = rs2.code
;

GRANT SELECT ON gs.request_status_transition TO gs_assurance_db_writer;