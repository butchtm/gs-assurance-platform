CREATE TABLE gs.invoice (
	id int4 NOT NULL,
	amount numeric(18, 2) NOT NULL,
	status_id int4 NOT NULL,
	is_archived bool NOT NULL,
	status_changed_at timestamp NOT NULL,
	customer_email text NULL,
	last_manual_check timestamp NULL,
    PRIMARY KEY(id)
);

ALTER TABLE gs.invoice
ADD FOREIGN KEY (status_id) REFERENCES gs.invoice_status ON DELETE RESTRICT;