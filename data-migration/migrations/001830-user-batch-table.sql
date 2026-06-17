CREATE TABLE gs.user_batch(
    id integer NOT NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp,
    count integer NOT NULL,
    success_count integer NOT NULL DEFAULT 0,
    errors JSONB,

    PRIMARY KEY(id)
);

CREATE SEQUENCE gs.seq_user_batch
    AS integer OWNED BY gs.user_batch.id;

GRANT USAGE, SELECT ON gs.seq_user_batch TO gs_assurance_db_writer;

ALTER TABLE gs.user_batch
    ALTER COLUMN id SET DEFAULT nextval('gs.seq_user_batch');

GRANT SELECT, INSERT, UPDATE ON gs.user_batch TO gs_assurance_db_writer;

