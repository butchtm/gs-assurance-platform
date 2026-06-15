ALTER TABLE gs.project
  DROP CONSTRAINT IF EXISTS project_master_project_id_fkey,
  ADD CONSTRAINT project_master_project_id_fkey
    FOREIGN KEY (master_project_id)
    REFERENCES gs.project (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.project
  DROP CONSTRAINT IF EXISTS project_parent_project_id_fkey,
  ADD CONSTRAINT project_parent_project_id_fkey
    FOREIGN KEY (parent_project_id)
    REFERENCES gs.project (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.bundle
  DROP CONSTRAINT IF EXISTS bundle_master_project_id_fkey,
  ADD CONSTRAINT bundle_master_project_id_fkey
    FOREIGN KEY (master_project_id)
    REFERENCES gs.project (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.project_project_location
  DROP CONSTRAINT IF EXISTS project_project_location_project_id_fkey,
  ADD CONSTRAINT project_project_location_project_id_fkey
    FOREIGN KEY (project_id)
    REFERENCES gs.project (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.project_project_asset
  DROP CONSTRAINT IF EXISTS project_project_asset_project_id_fkey,
  ADD CONSTRAINT project_project_asset_project_id_fkey
    FOREIGN KEY (project_id)
    REFERENCES gs.project (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.project_methodology
  DROP CONSTRAINT IF EXISTS project_methodology_project_id_fkey,
  ADD CONSTRAINT project_methodology_project_id_fkey
    FOREIGN KEY (project_id)
    REFERENCES gs.project (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.project_sustainable_development_goal
  DROP CONSTRAINT IF EXISTS project_sustainable_development_goal_project_id_fkey,
  ADD CONSTRAINT project_sustainable_development_goal_project_id_fkey
    FOREIGN KEY (project_id)
    REFERENCES gs.project (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.project_project_type
  DROP CONSTRAINT IF EXISTS project_project_type_project_id_fkey,
  ADD CONSTRAINT project_project_type_project_id_fkey
    FOREIGN KEY (project_id)
    REFERENCES gs.project (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.project_crediting_period
  DROP CONSTRAINT IF EXISTS project_crediting_period_project_id_fkey,
  ADD CONSTRAINT project_crediting_period_project_id_fkey
    FOREIGN KEY (project_id)
    REFERENCES gs.project (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.transition_project
  DROP CONSTRAINT IF EXISTS transition_project_project_id_fkey,
  ADD CONSTRAINT transition_project_project_id_fkey
    FOREIGN KEY (project_id)
    REFERENCES gs.project (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.base_project_request
  DROP CONSTRAINT IF EXISTS base_project_request_project_id_fkey,
  ADD CONSTRAINT base_project_request_project_id_fkey
    FOREIGN KEY (project_id)
    REFERENCES gs.project (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.base_project_document
  DROP CONSTRAINT IF EXISTS base_project_document_project_id_fkey,
  ADD CONSTRAINT base_project_document_project_id_fkey
    FOREIGN KEY (project_id)
    REFERENCES gs.project (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.stakeholder_consultation
  DROP CONSTRAINT IF EXISTS stakeholder_consultation_project_id_fkey,
  ADD CONSTRAINT stakeholder_consultation_project_id_fkey
    FOREIGN KEY (project_id)
    REFERENCES gs.project (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.batch_request
  DROP CONSTRAINT IF EXISTS batch_request_project_id_fkey,
  ADD CONSTRAINT batch_request_project_id_fkey
    FOREIGN KEY (project_id)
    REFERENCES gs.project (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.registry_update
  DROP CONSTRAINT IF EXISTS registry_update_project_id_fkey,
  ADD CONSTRAINT registry_update_project_id_fkey
    FOREIGN KEY (project_id)
    REFERENCES gs.project (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;
