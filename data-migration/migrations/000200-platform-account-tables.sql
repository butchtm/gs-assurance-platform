CREATE TABLE gs.account (
	id int4 NOT NULL,
	name varchar(4000) NOT NULL,
	email_address varchar(4000) NOT NULL,
	account_type_id int4 NOT NULL,
	created_time timestamp NOT NULL,
	is_active bool NULL,
	address varchar(4000) NULL,
	registry_account_id varchar(100) NULL,
	PRIMARY KEY(id)
);

CREATE SEQUENCE
  gs.seq_account
AS
  integer
OWNED BY
  gs.account.id;

SELECT
  setval('gs.seq_project', 1);

-- wire the sequence into the id column
ALTER TABLE gs.account
ALTER COLUMN id SET DEFAULT nextval('gs.seq_account');

ALTER TABLE gs.account
ADD FOREIGN KEY (account_type_id) REFERENCES gs.account_type ON DELETE RESTRICT;

-- users
CREATE TABLE gs.user (
	id int4 NOT NULL,
	user_name varchar(4000) NOT NULL,
	email_address varchar(4000) NOT NULL,
	account_id int4 NULL,
	is_active bool NULL,
	PRIMARY KEY(id),
	UNIQUE (user_name),
	UNIQUE (email_address)
);

CREATE SEQUENCE
  gs.seq_user
AS
  integer
OWNED BY
  gs.user.id;

SELECT
  setval('gs.seq_user', 1);

ALTER TABLE gs.user
ADD FOREIGN KEY (account_id) REFERENCES gs.account ON DELETE RESTRICT;
