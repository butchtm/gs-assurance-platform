DO '
DECLARE
  bad_row_count integer;
BEGIN
  SELECT
    COUNT(1)
  INTO
    bad_row_count
  FROM
    gs.project
  WHERE
    gsid !~ ''^GS\d+$''
  ;
  IF bad_row_count > 0 THEN
    RAISE EXCEPTION ''non-conforming gsids, cannot convert field'';
  END IF;
END
';

ALTER TABLE gs.project
ADD COLUMN gsid_num int4 GENERATED ALWAYS AS (SUBSTR(gsid, 3)::int) STORED
;
