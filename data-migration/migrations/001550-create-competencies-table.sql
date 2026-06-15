CREATE TABLE gs.competencies (
	id integer NOT NULL,
	estimated_annual_emission_reduction_amount integer NULL,
	PRIMARY KEY (id)
);
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.competencies TO gs_assurance_db_writer;

CREATE SEQUENCE gs.seq_competencies
AS integer OWNED BY gs.competencies.id;
ALTER TABLE gs.competencies
ALTER COLUMN id SET DEFAULT nextval('gs.seq_competencies');

GRANT USAGE, SELECT ON gs.seq_competencies TO gs_assurance_db_writer;

CREATE TABLE gs.competencies_certified_products (
	competencies_id integer NOT NULL,
	certified_products_id integer NOT NULL,
	UNIQUE (competencies_id, certified_products_id),
	FOREIGN KEY (certified_products_id) REFERENCES gs.project_asset(id) ON DELETE RESTRICT,
	FOREIGN KEY (competencies_id) REFERENCES gs.competencies(id) ON DELETE RESTRICT
);
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.competencies_certified_products TO gs_assurance_db_writer;

CREATE TABLE gs.competencies_project_gsids (
	competencies_id integer NULL,
	project_gsids varchar(255) NULL,
	UNIQUE (competencies_id, project_gsids),
	FOREIGN KEY (competencies_id) REFERENCES gs.competencies(id) ON DELETE RESTRICT
);
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.competencies_project_gsids TO gs_assurance_db_writer;

CREATE TABLE gs.competencies_methodologies (
	competencies_id integer NULL,
	methodologies_id integer NULL,
	UNIQUE (competencies_id, methodologies_id),
	FOREIGN KEY (methodologies_id) REFERENCES gs.methodology(id) ON DELETE RESTRICT,
	FOREIGN KEY (competencies_id) REFERENCES gs.competencies(id) ON DELETE RESTRICT
);
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.competencies_methodologies TO gs_assurance_db_writer;

CREATE TABLE gs.competencies_project_developers (
	competencies_id integer NULL,
	project_developers_id integer NULL,
	UNIQUE (competencies_id, project_developers_id),
	FOREIGN KEY (project_developers_id) REFERENCES gs.account(id) ON DELETE RESTRICT,
	FOREIGN KEY (competencies_id) REFERENCES gs.competencies(id) ON DELETE RESTRICT
);
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.competencies_project_developers TO gs_assurance_db_writer;

CREATE TABLE gs.competencies_project_locations (
	competencies_id integer NULL,
	project_locations_id integer NULL,
	UNIQUE (competencies_id, project_locations_id),
	FOREIGN KEY (competencies_id) REFERENCES gs.competencies(id) ON DELETE RESTRICT,
	FOREIGN KEY (project_locations_id) REFERENCES gs.project_location(id) ON DELETE RESTRICT
);
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.competencies_project_locations TO gs_assurance_db_writer;

CREATE TABLE gs.competencies_project_scales (
	competencies_id integer NULL,
	project_scales_id integer NULL,
	UNIQUE (competencies_id, project_scales_id),
	FOREIGN KEY (project_scales_id) REFERENCES gs.project_size(id) ON DELETE RESTRICT,
	FOREIGN KEY (competencies_id) REFERENCES gs.competencies(id) ON DELETE RESTRICT
);
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.competencies_project_scales TO gs_assurance_db_writer;

CREATE TABLE gs.competencies_project_types (
	competencies_id integer NULL,
	project_types_id integer NULL,
	UNIQUE (competencies_id, project_types_id),
	FOREIGN KEY (competencies_id) REFERENCES gs.competencies(id) ON DELETE RESTRICT,
	FOREIGN KEY (project_types_id) REFERENCES gs.project_type(id) ON DELETE RESTRICT
);
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.competencies_project_types TO gs_assurance_db_writer;

CREATE TABLE gs.competencies_request_types (
	competencies_id integer NULL,
	request_types_id integer NULL,
	UNIQUE (competencies_id, request_types_id),
	FOREIGN KEY (competencies_id) REFERENCES gs.competencies(id) ON DELETE RESTRICT,
	FOREIGN KEY (request_types_id) REFERENCES gs.request_type(id) ON DELETE RESTRICT
);
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.competencies_request_types TO gs_assurance_db_writer;

CREATE TABLE gs.competencies_vvbs_assigned (
	competencies_id integer NULL,
	vvbs_assigned_id integer NULL,
	UNIQUE (competencies_id, vvbs_assigned_id),
	FOREIGN KEY (competencies_id) REFERENCES gs.competencies(id) ON DELETE RESTRICT,
	FOREIGN KEY (vvbs_assigned_id) REFERENCES gs.account(id) ON DELETE RESTRICT
);
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.competencies_vvbs_assigned TO gs_assurance_db_writer;

CREATE TABLE gs.competencies_sustainable_development_goals (
	competencies_id integer NULL,
	sustainable_development_goals_id integer NULL,
	UNIQUE (competencies_id, sustainable_development_goals_id),
	FOREIGN KEY (sustainable_development_goals_id) REFERENCES gs.sustainable_development_goal(id) ON DELETE RESTRICT,
	FOREIGN KEY (competencies_id) REFERENCES gs.competencies(id) ON DELETE RESTRICT
);
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.competencies_sustainable_development_goals TO gs_assurance_db_writer;

ALTER TABLE gs.account
    ADD competencies_id integer NULL;

ALTER TABLE gs.account
    ADD FOREIGN KEY (competencies_id) REFERENCES gs.competencies ON DELETE RESTRICT;