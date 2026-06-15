CREATE TABLE gs.pathway (
	id integer NOT NULL UNIQUE,
	name varchar NOT NULL UNIQUE,
	index integer NOT NULL,
	fast_track bool,
    PRIMARY KEY(id)
);
GRANT SELECT ON gs.pathway TO gs_assurance_db_writer;

CREATE TABLE gs.pathway_group (
	id integer NOT NULL UNIQUE,
	name varchar NOT NULL UNIQUE,
	index integer NOT NULL,
	total_days integer NOT NULL,
    pathway_id integer NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_pathway
	  FOREIGN KEY (pathway_id) 
      REFERENCES gs.pathway(id) 
);
GRANT SELECT ON gs.pathway_group TO gs_assurance_db_writer;

CREATE TABLE gs.activity (
	id integer NOT NULL UNIQUE,
	name varchar NOT NULL,
	index integer NOT NULL,
	total_days integer NOT NULL,
	internal bool,
    account_type_id integer NOT NULL,
    pathway_group_id integer NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_account_type
	  FOREIGN KEY (account_type_id) 
      REFERENCES gs.account_type(id),
    CONSTRAINT fk_pathway_group
	  FOREIGN KEY (pathway_group_id) 
      REFERENCES gs.pathway_group(id) 
);
GRANT SELECT ON gs.activity TO gs_assurance_db_writer;

CREATE TABLE gs.request_activity (
	id integer NOT NULL UNIQUE,
	request_id integer NOT NULL,
	activity_id integer NOT NULL,
	start_date timestamp NOT NULL,
	planned_end_date timestamp NOT NULL,
	end_date timestamp NOT NULL,
    responsible_account_id integer NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_request
	  FOREIGN KEY (request_id) 
      REFERENCES gs.request(id),
    CONSTRAINT fk_activity
	  FOREIGN KEY (activity_id) 
      REFERENCES gs.activity(id),
    CONSTRAINT fk_account
	  FOREIGN KEY (responsible_account_id) 
      REFERENCES gs.account(id) 
);
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.request_activity TO gs_assurance_db_writer;

ALTER TABLE gs.request 
ADD COLUMN current_activity_id integer,
ADD CONSTRAINT fk_current_activity FOREIGN KEY (current_activity_id) REFERENCES gs.request_activity(id);