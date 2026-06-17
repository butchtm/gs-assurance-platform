--
-- Request types to requires consultation table
--
CREATE TABLE gs.request_type_consultation (
    request_type_id integer NOT NULL,
    requires_consultation bool NULL DEFAULT true,
	PRIMARY KEY(request_type_id)
);

--
-- Foreign Keys
--
ALTER TABLE gs.request_type_consultation
ADD FOREIGN KEY (request_type_id) REFERENCES gs.request_type ON DELETE RESTRICT;

--
-- Adding records for each request type
--
-- Those that do  require consultation
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1001, true);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1002, true);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1003, true);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1005, true);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1006, true);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1007, true);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1008, true);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1009, true);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1016, true);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1017, true);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1018, true);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1019, true);

-- Those that do not require consultation
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1000, false);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1012, false);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1014, false);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1015, false);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1020, false);

-- Those where consultation is optional
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1004, null);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1010, null);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1011, null);
INSERT INTO gs.request_type_consultation
(request_type_id, requires_consultation)
VALUES (1013, null);
