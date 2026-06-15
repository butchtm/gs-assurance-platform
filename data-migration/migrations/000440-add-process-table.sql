CREATE TYPE gs.process_status AS ENUM (
  'STARTED',
  'COMPLETED',
  'FAILED'
);

CREATE TABLE gs.process (
    id serial NOT NULL,
    process_code varchar NOT NULL,
    process_status gs.process_status NOT NULL,
    start_timestamp timestamp NOT NULL,
    status_timestamp timestamp NOT NULL,
    process_message varchar NULL,
    PRIMARY KEY (id)
);