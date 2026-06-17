CREATE TABLE gs.project_crediting_period_methodology (
	project_id int4 NOT NULL,
	period_id int4 NOT NULL,
	methodology_id int4 NOT NULL,
	"version" DECIMAL,
	PRIMARY KEY(project_id, period_id, methodology_id),
	FOREIGN KEY (project_id) REFERENCES gs.project(id) ON DELETE RESTRICT,
	FOREIGN KEY (project_id, period_id) REFERENCES gs.project_crediting_period(project_id, period_id) ON DELETE RESTRICT,
	FOREIGN KEY (methodology_id) REFERENCES gs.methodology(id) ON DELETE RESTRICT
);

GRANT SELECT, INSERT, UPDATE, DELETE ON gs.project_crediting_period_methodology TO gs_assurance_db_writer;