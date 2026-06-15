CREATE TABLE gs.conversation_message (
    id integer NOT NULL,
    conversation_id integer NOT NULL,
    content varchar NOT NULL,
    sent_by_user_id integer NOT NULL,
    sent_at timestamp NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_user
        FOREIGN KEY (sent_by_user_id)
        REFERENCES gs.user(id)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON gs.conversation_message TO gs_assurance_db_writer;

CREATE SEQUENCE
  gs.seq_conversation_message
AS
  integer
OWNED BY
  gs.conversation_message.id;

ALTER TABLE gs.conversation_message
ALTER COLUMN id SET DEFAULT nextval('gs.seq_conversation_message');

GRANT USAGE, SELECT ON gs.seq_conversation_message TO gs_assurance_db_writer;

CREATE TABLE gs.conversation_message_receipt (
    id integer NOT NULL,
    message_id integer NOT NULL,
    read_by_user_id integer NOT NULL,
    read_at timestamp NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_message
        FOREIGN KEY (message_id)
        REFERENCES gs.conversation_message(id),
    CONSTRAINT fk_user
        FOREIGN KEY (read_by_user_id)
        REFERENCES gs.user(id)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON gs.conversation_message_receipt TO gs_assurance_db_writer;

CREATE SEQUENCE
  gs.seq_conversation_message_receipt
AS
  integer
OWNED BY
  gs.conversation_message_receipt.id;

ALTER TABLE gs.conversation_message_receipt
ALTER COLUMN id SET DEFAULT nextval('gs.seq_conversation_message_receipt');

GRANT USAGE, SELECT ON gs.seq_conversation_message_receipt TO gs_assurance_db_writer;

CREATE UNIQUE INDEX idx_message_receipt_message_user
ON gs.conversation_message_receipt (message_id, read_by_user_id);