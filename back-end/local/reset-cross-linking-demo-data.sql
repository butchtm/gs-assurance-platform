-- Reset local Proposal 2 demo data back to the seeded project baseline.
-- Keeps seeded users/accounts and the five seeded cross-linking projects.

DO $$
DECLARE
    seeded_project_ids CONSTANT bigint[] := ARRAY[910101, 910102, 910103, 910104, 910105];
    request_to_delete RECORD;
    project_to_delete RECORD;
BEGIN
    FOR request_to_delete IN
        SELECT DISTINCT bpr.request_id
        FROM gs.base_project_request bpr
        WHERE bpr.project_id >= 910000
    LOOP
        RAISE NOTICE 'deleting request %', request_to_delete.request_id;

        UPDATE gs.request SET current_round_index = NULL WHERE id = request_to_delete.request_id;
        UPDATE gs.request SET batch_id = NULL WHERE id = request_to_delete.request_id;
        UPDATE gs.request SET current_activity_id = NULL WHERE id = request_to_delete.request_id;
        UPDATE gs.batch SET parent_request_id = NULL WHERE parent_request_id = request_to_delete.request_id;

        DELETE FROM gs.request_form WHERE request_id = request_to_delete.request_id;
        DELETE FROM gs.methodology_update WHERE project_methodology_update_id IN (
            SELECT pmu.id FROM gs.project_methodology_update pmu WHERE request_id = request_to_delete.request_id
        );
        DELETE FROM gs.conversation_message_receipt cmr WHERE cmr.message_id IN (
            SELECT cm.id FROM gs.conversation_message cm WHERE cm.conversation_id IN (
                SELECT c.id FROM gs.conversation c WHERE request_id = request_to_delete.request_id
            )
        );
        DELETE FROM gs.conversation_message WHERE conversation_id IN (
            SELECT c.id FROM gs.conversation c WHERE c.request_id = request_to_delete.request_id
        );
        DELETE FROM gs.conversation WHERE request_id = request_to_delete.request_id;
        DELETE FROM gs.project_methodology_update WHERE request_id = request_to_delete.request_id;
        DELETE FROM gs.request_methodology WHERE request_id = request_to_delete.request_id;
        DELETE FROM gs.stakeholder_consultation WHERE request_id = request_to_delete.request_id;
        DELETE FROM gs.request_activity WHERE request_id = request_to_delete.request_id;
        DELETE FROM gs.batch_request WHERE request_id = request_to_delete.request_id;
        DELETE FROM gs.batch WHERE parent_request_id = request_to_delete.request_id;
        DELETE FROM gs.pii_metadata WHERE request_id = request_to_delete.request_id;
        DELETE FROM gs."comment" WHERE request_id = request_to_delete.request_id;
        DELETE FROM gs.request_round WHERE request_id = request_to_delete.request_id;
        DELETE FROM gs.request_status_history WHERE request_id = request_to_delete.request_id;
        DELETE FROM gs.request_account_assignment WHERE request_id = request_to_delete.request_id;
        DELETE FROM gs.request_document WHERE request_id = request_to_delete.request_id;
        DELETE FROM gs.base_project_request WHERE request_id = request_to_delete.request_id;
        DELETE FROM gs.request WHERE id = request_to_delete.request_id;
    END LOOP;

    FOR project_to_delete IN
        SELECT id
        FROM gs.project
        WHERE id >= 910000
          AND id <> ALL(seeded_project_ids)
    LOOP
        RAISE NOTICE 'deleting project %', project_to_delete.id;

        DELETE FROM gs.methodology_update WHERE project_methodology_update_id IN (
            SELECT pmu.id FROM gs.project_methodology_update pmu WHERE project_id = project_to_delete.id
        );
        DELETE FROM gs.project_methodology_update WHERE project_id = project_to_delete.id;

        UPDATE gs.project SET master_project_id = NULL WHERE master_project_id = project_to_delete.id;
        UPDATE gs.project SET parent_project_id = NULL WHERE parent_project_id = project_to_delete.id;
        DELETE FROM gs.external_system_project_update WHERE project_id = project_to_delete.id;
        DELETE FROM gs.project_project_location WHERE project_id = project_to_delete.id;
        DELETE FROM gs.project_project_asset WHERE project_id = project_to_delete.id;
        DELETE FROM gs.project_methodology WHERE project_id = project_to_delete.id;
        DELETE FROM gs.project_sustainable_development_goal WHERE project_id = project_to_delete.id;
        DELETE FROM gs.project_status_history WHERE base_project_id = project_to_delete.id;
        DELETE FROM gs.project_project_type WHERE project_id = project_to_delete.id;
        DELETE FROM gs.project_crediting_period_methodology WHERE project_id = project_to_delete.id;
        DELETE FROM gs.project_crediting_monitoring_period WHERE project_id = project_to_delete.id;
        DELETE FROM gs.project_crediting_period WHERE project_id = project_to_delete.id;
        DELETE FROM gs.transition_project WHERE project_id = project_to_delete.id;
        DELETE FROM gs.base_project_document WHERE project_id = project_to_delete.id;
        DELETE FROM gs.stakeholder_consultation WHERE project_id = project_to_delete.id;
        DELETE FROM gs.batch_request WHERE project_id = project_to_delete.id;
        DELETE FROM gs.project_risk_summary WHERE project_id = project_to_delete.id;
        DELETE FROM gs.pii_metadata WHERE project_id = project_to_delete.id;
        DELETE FROM gs.project WHERE id = project_to_delete.id;
    END LOOP;
END $$;

SELECT setval('gs.project_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM gs.project), 910105));
SELECT setval('gs.project_gsid_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM gs.project), 910105));
SELECT setval('gs.request_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM gs.request), 1));
