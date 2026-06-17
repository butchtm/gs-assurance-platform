CREATE TABLE
    gs.project_risk_summary (
        id SERIAL NOT NULL,
        project_id integer NOT NULL,
        risk boolean NOT NULL,
        summary varchar NOT NULL,
        date_applied TIMESTAMP WITHOUT TIME ZONE NOT NULL,
        PRIMARY KEY (id),
        CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES gs.project (id)
    );