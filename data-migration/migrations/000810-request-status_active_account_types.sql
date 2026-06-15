--
-- Temp table defining the active account types per request status
--
CREATE TABLE pg_temp.temp_request_status_account_type (
  request_status_code text NOT NULL,
  account_type_code text NOT NULL,
  PRIMARY KEY (request_status_code, account_type_code)
);

INSERT INTO pg_temp.temp_request_status_account_type
  (request_status_code, account_type_code)
VALUES
   ('IN_PREPARATION', 'PROJECT-DEVELOPER')
  ,('IN_AUDIT', 'VVB')
  ,('COMPLETENESS_CHECK', 'GS-ARM-TEAM')
  ,('IN_ASSIGNMENT', 'GS-ARM-TEAM')
  ,('IN_ASSIGNMENT', 'GS-FINANCE')
  ,('IN_PROGRESS', 'REVIEWER')
  ,('IN_PROGRESS', 'GS-FINANCE')
  ,('IN_ROUND_COMPLETION', 'GS-ARM-TEAM')
;

--
-- Active account types per request status
--
CREATE TABLE gs.request_status_account_type (
    request_status_id int4 NOT NULL,
    account_type_id int4 NOT NULL,
    PRIMARY KEY (request_status_id, account_type_id)
);

ALTER TABLE gs.request_status_account_type
ADD FOREIGN KEY (request_status_id) REFERENCES gs.request_status ON DELETE RESTRICT;

ALTER TABLE gs.request_status_account_type
ADD FOREIGN KEY (account_type_id) REFERENCES gs.account_type ON DELETE RESTRICT;

INSERT INTO gs.request_status_account_type
  (request_status_id, account_type_id)
SELECT
  rs.id, at1.id
FROM
  pg_temp.temp_request_status_account_type rsat
JOIN
  gs.request_status rs
ON
  rsat.request_status_code = rs.code
JOIN
  gs.account_type at1
ON
  rsat.account_type_code = at1.code
;

GRANT SELECT ON gs.request_status_account_type TO gs_assurance_db_writer;