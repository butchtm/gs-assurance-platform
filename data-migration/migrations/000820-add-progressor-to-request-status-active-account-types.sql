ALTER TABLE gs.request_status_account_type
ADD COLUMN can_progress boolean NOT NULL DEFAULT false;

UPDATE gs.request_status_account_type
SET
  can_progress = true
WHERE
  account_type_id in (
    SELECT
      id
    FROM
      gs.account_type
    WHERE
      code IN ('PROJECT-DEVELOPER', 'VVB', 'GS-ARM-TEAM')
  )
;
