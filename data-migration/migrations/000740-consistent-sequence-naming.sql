ALTER SEQUENCE gs.seq_account
RENAME TO account_id_seq;

ALTER SEQUENCE gs.seq_audit_event
RENAME TO audit_event_id_seq;

ALTER SEQUENCE gs.seq_project
RENAME TO project_id_seq;

ALTER SEQUENCE gs.seq_project_gsid
RENAME TO project_gsid_seq;

-- fix ownership
ALTER SEQUENCE gs.project_gsid_seq
OWNED BY gs.project.gsid;

ALTER SEQUENCE gs.seq_request
RENAME TO request_id_seq;

ALTER SEQUENCE gs.seq_user
RENAME TO user_id_seq;