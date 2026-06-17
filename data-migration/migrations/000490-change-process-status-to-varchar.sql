ALTER TABLE gs.process
ALTER COLUMN process_status TYPE varchar;

ALTER TABLE gs.process
ADD CHECK (process_status IN ('STARTED', 'COMPLETE', 'FAILED'));

DROP TYPE gs.process_status;