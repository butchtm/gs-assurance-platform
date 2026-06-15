CREATE TABLE gs.project (
	id int4 NOT NULL,
	gsid varchar NOT NULL UNIQUE,
	status bool NOT NULL,
	name varchar(255) NOT NULL,
	description varchar NOT NULL,
	programme_of_activities_id int4 NOT NULL,
	project_start_date timestamp NOT NULL,
	project_category_id int4 NOT NULL,
	registration_type_id int4 NOT NULL,
	has_project_assets bool NOT NULL,
	current_standard_version_id int4 NOT NULL,
	carbon_stream_id int4 NOT NULL,
	project_developer_name varchar NOT NULL,
	project_developer_email varchar NOT NULL,
	project_size_id int4 NOT NULL,
	current_request_id int4 NULL,
	project_status_id int4 NULL,
	bundle_id int4 NULL,
	design_certification_date timestamp NULL,
	design_certification_date_manual_set bool NULL,
	fee_schedule_id int4 NOT NULL,
	estimated_annual_units int4 NOT NULL,
	master_project_id int4 NULL,
	city_town_community varchar NULL,
	region_state_province varchar NULL,
	street_and_number varchar NULL,
	listing_date timestamp NULL,
	listing_date_manual_set bool NULL,
	parent_project_id int4 NULL,
	migrated bool NOT NULL DEFAULT false,
	PRIMARY KEY(id)
);

ALTER TABLE gs.project 
ADD FOREIGN KEY (master_project_id) REFERENCES gs.project ON DELETE RESTRICT;

ALTER TABLE gs.project 
ADD FOREIGN KEY (parent_project_id) REFERENCES gs.project ON DELETE RESTRICT;

ALTER TABLE gs.project 
ADD FOREIGN KEY (programme_of_activities_id) REFERENCES gs.programme_of_activities ON DELETE RESTRICT;

ALTER TABLE gs.project 
ADD FOREIGN KEY (project_category_id) REFERENCES gs.project_category ON DELETE RESTRICT;

ALTER TABLE gs.project 
ADD FOREIGN KEY (registration_type_id) REFERENCES gs.registration_type ON DELETE RESTRICT;

ALTER TABLE gs.project
ADD FOREIGN KEY (current_standard_version_id) REFERENCES gs.current_standard_version ON DELETE RESTRICT;

ALTER TABLE gs.project
ADD FOREIGN KEY (carbon_stream_id) REFERENCES gs.carbon_stream ON DELETE RESTRICT;

ALTER TABLE gs.project
ADD FOREIGN KEY (project_size_id) REFERENCES gs.project_size ON DELETE RESTRICT;

ALTER TABLE gs.project
ADD FOREIGN KEY (fee_schedule_id) REFERENCES gs.fee_schedule ON DELETE RESTRICT;

ALTER TABLE gs.project 
ADD FOREIGN KEY (project_status_id) REFERENCES gs.project_status ON DELETE RESTRICT;


CREATE TABLE gs.project_project_location (
	project_id int4 NOT NULL,
	project_location_id int4 NOT NULL,
	PRIMARY KEY(project_id, project_location_id)
);

ALTER TABLE gs.project_project_location 
ADD FOREIGN KEY (project_id) REFERENCES gs.project ON DELETE RESTRICT;

ALTER TABLE gs.project_project_location 
ADD FOREIGN KEY (project_location_id) REFERENCES gs.project_location ON DELETE RESTRICT;


CREATE TABLE gs.project_project_asset (
	project_id int4 NOT NULL,
	project_asset_id int4 NOT NULL,
	PRIMARY KEY(project_id, project_asset_id)
);

ALTER TABLE gs.project_project_asset 
ADD FOREIGN KEY (project_id) REFERENCES gs.project ON DELETE RESTRICT;

ALTER TABLE gs.project_project_asset 
ADD FOREIGN KEY (project_asset_id) REFERENCES gs.project_asset ON DELETE RESTRICT;


CREATE TABLE gs.project_project_type (
	project_id int4 NOT NULL,
	project_type_id int4 NOT NULL,
	PRIMARY KEY(project_id, project_type_id)
);

ALTER TABLE gs.project_project_type 
ADD FOREIGN KEY (project_id) REFERENCES gs.project ON DELETE RESTRICT;

ALTER TABLE gs.project_project_type 
ADD FOREIGN KEY (project_type_id) REFERENCES gs.project_type ON DELETE RESTRICT;


CREATE TABLE gs.project_methodology (
	project_id int4 NOT NULL,
	methodology_id int4 NOT NULL,
	PRIMARY KEY(project_id, methodology_id)
);

ALTER TABLE gs.project_methodology 
ADD FOREIGN KEY (project_id) REFERENCES gs.project ON DELETE RESTRICT;

ALTER TABLE gs.project_methodology 
ADD FOREIGN KEY (methodology_id) REFERENCES gs.methodology ON DELETE RESTRICT;


CREATE TABLE gs.project_sustainable_development_goal (
	project_id int4 NOT NULL,
	sustainable_development_goal_id int4 NOT NULL,
	PRIMARY KEY(project_id, sustainable_development_goal_id)
);

ALTER TABLE gs.project_sustainable_development_goal 
ADD FOREIGN KEY (project_id) REFERENCES gs.project ON DELETE RESTRICT;

ALTER TABLE gs.project_sustainable_development_goal 
ADD FOREIGN KEY (sustainable_development_goal_id) REFERENCES gs.sustainable_development_goal ON DELETE RESTRICT;


CREATE TABLE gs.project_crediting_period (
	project_id int4 NOT NULL,
	period_id int4 NOT NULL,
	start_date timestamp NOT NULL,
	end_date timestamp NOT NULL,
	PRIMARY KEY(project_id, period_id)
);

ALTER TABLE gs.project_crediting_period
ADD FOREIGN KEY (project_id) REFERENCES gs.project ON DELETE RESTRICT;

-- LUF Projects
CREATE TABLE gs.luf_project (
	project_id int4 NOT NULL,
	ar_soil_carbon bool NOT NULL,
	eligible_area_ha int4 NOT NULL,
	small_holder bool NOT NULL,
	total_area_ha int4 NOT NULL,
	last_activity timestamp NULL,
	notes text NULL,
	certified_design_date timestamp NULL,
	certified_project_date timestamp NULL,
	last_design_certification_renewal_date timestamp NULL,
	last_performance_review_date timestamp NULL,
	PRIMARY KEY(project_id)
);

ALTER TABLE gs.luf_project
ADD FOREIGN KEY (project_id) REFERENCES gs.project ON DELETE RESTRICT;

-- Energy Projects
CREATE TABLE gs.energy_project (
	project_id int4 NOT NULL,
	project_link_in_other_scheme text NULL,
	crediting_period_start_date_other_schemes timestamp NULL,
	crediting_period_end_date_other_schemes timestamp NULL,
	transition_approval_procedure int4 NULL,
	transition_from_other_scheme int4 NULL,
	transition_pathway int4 NULL,
	PRIMARY KEY(project_id)
);

ALTER TABLE gs.energy_project
ADD FOREIGN KEY (project_id) REFERENCES gs.project ON DELETE RESTRICT;