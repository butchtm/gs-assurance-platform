--
-- Entities for notification domain
--

CREATE TABLE gs.notification_template (
	id serial NOT NULL,
    name varchar(50) NOT NULL,
    version varchar(50) NOT NULL,
    subject varchar NOT NULL,
    html_body varchar NOT NULL,
    text_body varchar NOT NULL,
    created_timestamp timestamp NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (name, version)
);

CREATE TABLE gs.notification (
	id serial NOT NULL,
    account_id int4 NOT NULL,
    addressees varchar[] NOT NULL,
    subject varchar NOT NULL,
    html_body varchar NOT NULL,
    text_body varchar NOT NULL,
    created_timestamp timestamp NOT NULL,
    messaging_reference varchar NULL,
    PRIMARY KEY (id)
);

ALTER TABLE gs.notification
ADD FOREIGN KEY (account_id) REFERENCES gs.account ON DELETE RESTRICT;

CREATE TABLE gs.notification_contact_mapping (
	id serial NOT NULL,
    address varchar NULL,
    mapped_address varchar NULL,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX notification_contact_mapping_address_key
ON gs.notification_contact_mapping (
    address
) NULLS NOT DISTINCT;
