ALTER TABLE gs.request_monitoring_period
ADD COLUMN fee_schedule integer;

ALTER TABLE gs.request_monitoring_period
ADD COLUMN period_id integer;

ALTER TABLE gs.request_monitoring_period
ALTER COLUMN start_date
DROP NOT NULL;

ALTER TABLE gs.request_monitoring_period
ALTER COLUMN end_date
DROP NOT NULL;

ALTER TABLE gs.project_crediting_monitoring_period
ADD COLUMN fee_schedule integer;