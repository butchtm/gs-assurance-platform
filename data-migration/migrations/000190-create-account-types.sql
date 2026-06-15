CREATE TABLE gs.account_type (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.account_type
    (id, code, description)
VALUES
    (0,'REVIEWER','Reviewer'),
    (1,'VVB','VVB'),
    (2,'GOLD-STANDARD','Gold Standard'),
    (3,'PROJECT-DEVELOPER','Project Developer'),
    (4,'GS-ARM-TEAM','Gold Standard ARM Team'),
    (5,'GS-FINANCE','Gold Standard Finance'),
    (6,'TAC','TAC');