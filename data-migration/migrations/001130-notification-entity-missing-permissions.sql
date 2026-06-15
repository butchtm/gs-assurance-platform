GRANT SELECT ON gs.notification_template TO gs_assurance_db_writer;
GRANT SELECT ON gs.notification_contact_mapping TO gs_assurance_db_writer;
GRANT SELECT ON gs.request_status_notification TO gs_assurance_db_writer;

GRANT USAGE, SELECT ON gs.notification_id_seq TO gs_assurance_db_writer;

GRANT SELECT, INSERT, UPDATE ON gs.notification TO gs_assurance_db_writer;