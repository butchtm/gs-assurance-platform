ALTER TABLE gs.project
ADD COLUMN display_not_requested_status_requests bool DEFAULT false NOT NULL;

UPDATE gs.project
SET display_not_requested_status_requests = true
WHERE gsid IN (
  'GS11709',
  'GS11899',
  'GS6503',
  'GS12051',
  'GS11743',
  'GS11800',
  'GS11801',
  'GS12237',
  'GS12480',
  'GS12556',
  'GS12558',
  'GS12559',
  'GS12560',
  'GS12381',
  'GS13078',
  'GS12982',
  'GS13005',
  'GS13006',
  'GS12997',
  'GS12998',
  'GS12690',
  'GS13002',
  'GS12967',
  'GS12968',
  'GS12929',
  'GS12930',
  'GS13103',
  'GS13085',
  'GS12922',
  'GS12923'
);