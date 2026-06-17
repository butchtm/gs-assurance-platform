CREATE TABLE gs.design_cert_date_matrix
(
    id              INT  NOT NULL,
    request_type_id INT  NOT NULL,
    vvb_assigned    BOOL NOT NULL,
    days            INT  NOT NULL,
    CONSTRAINT design_cert_date_matrix_pkey PRIMARY KEY (id),
    CONSTRAINT request_type_id_fkey FOREIGN KEY (request_type_id) REFERENCES gs.request_type (id) ON DELETE RESTRICT
);

INSERT INTO gs.design_cert_date_matrix (id, request_type_id, vvb_assigned, days)
VALUES (1, 1001, TRUE, 28),
       (2, 1002, TRUE, 28),
       (3, 1003, TRUE, 28),
       (4, 1017, TRUE, 42),
       (5, 1018, TRUE, 42),
       (6, 1019, TRUE, 42),
       (7, 1004, TRUE, 14),
       (8, 1004, FALSE, 28);

GRANT SELECT ON gs.design_cert_date_matrix TO gs_assurance_db_writer;