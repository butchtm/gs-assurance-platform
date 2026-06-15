CREATE TABLE gs.conversation_status (
    id integer NOT NULL,
    code varchar NOT NULL,
    description varchar NOT NULL,
    PRIMARY KEY(id)
);

GRANT SELECT ON gs.conversation_status TO gs_assurance_db_writer;

INSERT INTO gs.conversation_status(id, code, description)
VALUES
(0, 'OPEN', 'Open'),
(1, 'CLOSED', 'Closed');

CREATE TABLE gs.conversation (
    id integer NOT NULL,
    request_id integer NOT NULL UNIQUE,
    status_id integer NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_request
        FOREIGN KEY (request_id)
        REFERENCES gs.request(id),
    CONSTRAINT fk_status
        FOREIGN KEY (status_id)
        REFERENCES gs.conversation_status(id)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON gs.conversation TO gs_assurance_db_writer;

CREATE SEQUENCE
  gs.seq_conversation
AS
  integer
OWNED BY
  gs.conversation.id;

ALTER TABLE gs.conversation
ALTER COLUMN id SET DEFAULT nextval('gs.seq_conversation');

GRANT USAGE, SELECT ON gs.seq_conversation TO gs_assurance_db_writer;

