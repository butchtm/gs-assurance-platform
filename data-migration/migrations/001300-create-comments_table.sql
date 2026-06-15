--
-- Comments table for storing all comments made against a request
--
CREATE TABLE gs.comment (
    id integer NOT NULL,
    request_id integer NOT NULL,
    submitted_comment varchar NOT NULL,
    uploaded_by_name varchar NOT NULL,
    uploaded_by_email varchar NOT NULL,
    account_id integer NULL,
    uploaded_date timestamp NOT NULL,
    public_access bool NOT NULL,
    anonymous bool NOT NULL,
    addressed bool NOT NULL,
    PRIMARY KEY(id)
);

--
-- Foreign Keys
--
ALTER TABLE gs.comment
ADD FOREIGN KEY (request_id) REFERENCES gs.request ON DELETE RESTRICT;

ALTER TABLE gs.comment
ADD FOREIGN KEY (account_id) REFERENCES gs.account ON DELETE RESTRICT;

GRANT SELECT, INSERT, DELETE ON gs.comment TO gs_assurance_db_writer;

-- create the table sequence
CREATE SEQUENCE
  gs.seq_comment
AS
  integer
OWNED BY
  gs.comment.id;

-- wire the sequence into the id column
ALTER TABLE gs.comment
ALTER COLUMN id SET DEFAULT nextval('gs.seq_comment');

GRANT USAGE, SELECT ON gs.seq_comment TO gs_assurance_db_writer;