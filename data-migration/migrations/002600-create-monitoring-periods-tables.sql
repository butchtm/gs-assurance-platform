CREATE TABLE gs.project_crediting_monitoring_period (
    id integer NOT NULL,
    project_id integer NOT NULL,
    period_id integer NOT NULL,
    start_date date,
    end_date date,
    PRIMARY KEY(id, project_id, period_id),
    CONSTRAINT fk_crediting_period
        FOREIGN KEY (project_id, period_id)
        REFERENCES gs.project_crediting_period(project_id, period_id)
);

ALTER TABLE gs.request
ADD COLUMN monitoring_period_id integer;

CREATE TABLE gs.request_monitoring_period (
    request_id integer NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    PRIMARY KEY (request_id),
    CONSTRAINT fk_request_monitoring_period_request
        FOREIGN KEY (request_id)
        REFERENCES gs.request(id) ON DELETE CASCADE
);