ALTER TABLE gs.request
ADD COLUMN delta_excluded bool DEFAULT false NOT NULL;

ALTER TABLE gs.project
ADD COLUMN delta_excluded bool DEFAULT false NOT NULL;