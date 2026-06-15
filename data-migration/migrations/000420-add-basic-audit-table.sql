CREATE TABLE gs.audit_event (
    id int4 NOT NULL,
    idp_user_id uuid NOT NULL,
    event_timestamp timestamp NOT NULL,
    event_subject varchar NOT NULL, -- e.g. a project, a request, a document etc
    additional_information jsonb NULL,
    PRIMARY KEY (id)
);

CREATE SEQUENCE
  gs.seq_audit_event
AS
  integer
OWNED BY
  gs.audit_event.id;

SELECT
  setval('gs.seq_audit_event', 1);

-- wire the sequence into the id column
ALTER TABLE gs.audit_event
ALTER COLUMN id SET DEFAULT nextval('gs.seq_audit_event');