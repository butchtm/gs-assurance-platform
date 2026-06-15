CREATE TABLE
  gs.request_crediting_period (
    request_id INTEGER NOT NULL CONSTRAINT fk_request_crediting_period_request REFERENCES gs.request (id) ON DELETE CASCADE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    CONSTRAINT pk_request_crediting_period PRIMARY KEY (request_id)
  );

ALTER TABLE gs.request_methodology DROP constraint request_methodology_pkey;
ALTER TABLE gs.request_methodology ADD PRIMARY KEY (request_id, methodology_id);
ALTER TABLE gs.request_methodology DROP COLUMN crediting_period_id;