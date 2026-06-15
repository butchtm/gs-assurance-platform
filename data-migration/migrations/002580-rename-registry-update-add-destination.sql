ALTER TABLE gs.registry_update
RENAME TO external_system_project_update;

ALTER TABLE gs.external_system_project_update
ADD COLUMN destination_system TEXT NOT NULL DEFAULT 'REGISTRY';

ALTER TABLE gs.external_system_project_update ADD CONSTRAINT external_system_project_update_destination_system_check CHECK (destination_system IN ('REGISTRY', 'SDG_TOOL'));

ALTER TABLE gs.external_system_project_update
DROP CONSTRAINT IF EXISTS registry_update_pkey;

ALTER TABLE gs.external_system_project_update
ADD PRIMARY KEY (project_id, destination_system);