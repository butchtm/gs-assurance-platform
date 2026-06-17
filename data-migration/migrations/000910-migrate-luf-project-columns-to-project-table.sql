-- add columns to project
ALTER TABLE gs.project
ADD COLUMN last_activity timestamp NULL;

ALTER TABLE gs.project
ADD COLUMN notes text;

ALTER TABLE gs.project
ADD COLUMN certified_project_date timestamp NULL;

ALTER TABLE gs.project
ADD COLUMN last_design_certification_renewal_date timestamp NULL;

ALTER TABLE gs.project
ADD COLUMN last_performance_review_date timestamp NULL;

--migrate data from luf_project to project
UPDATE gs.project p
SET last_activity = luf.last_activity,
    notes = luf.notes,
    certified_project_date = luf.certified_project_date,
    last_design_certification_renewal_date = luf.last_design_certification_renewal_date,
    last_performance_review_date = luf.last_performance_review_date
FROM gs.luf_project luf
WHERE luf.project_id = p.id;

--migrate certification date from luf_project to project if project is not already set
UPDATE gs.project p
SET design_certification_date = luf.certified_design_date
FROM gs.luf_project luf
where luf.project_id = p.id and p.design_certification_date is null and luf.certified_design_date is not null;

--remove luf_project table
DROP TABLE gs.luf_project;

-- remove deprecated columns from project table
ALTER TABLE gs.project
DROP COLUMN project_category_id;

ALTER TABLE gs.project
DROP COLUMN city_town_community;

ALTER TABLE gs.project
DROP COLUMN region_state_province;

ALTER TABLE gs.project
DROP COLUMN street_and_number;
