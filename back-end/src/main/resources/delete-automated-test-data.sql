DO $$
DECLARE
    project_to_delete RECORD;
    request_to_delete RECORD;
BEGIN
    FOR project_to_delete IN
        SELECT * FROM gs.project p where p."name" like '%automation test%'
    LOOP
		raise notice 'deleting project %', project_to_delete.id;
		delete from gs.methodology_update where project_methodology_update_id in (
		    select pmu.id from gs.project_methodology_update pmu where project_id = project_to_delete.id
		);
		delete from gs.project_methodology_update where project_id = project_to_delete.id;
		FOR request_to_delete IN
            SELECT request_id FROM gs.base_project_request bpr WHERE bpr.project_id = project_to_delete.id
        LOOP
			raise notice 'deleting request %', request_to_delete.request_id;
			update gs.request set current_round_index = null where id = request_to_delete.request_id;
			update gs.request set batch_id = null where id = request_to_delete.request_id;
			update gs.request set current_activity_id = null where id = request_to_delete.request_id;
			update gs.batch set parent_request_id = null where parent_request_id = request_to_delete.request_id;
            delete from gs.request_form where request_id = request_to_delete.request_id;
            delete from gs.methodology_update where project_methodology_update_id in (
                select pmu.id from gs.project_methodology_update pmu where request_id = request_to_delete.request_id
            );
            delete from gs.conversation_message_receipt cmr where cmr.message_id in (
                select cm.id from gs.conversation_message cm where cm.conversation_id in (
                    select c.id from gs.conversation c where request_id = request_to_delete.request_id
                )
            );
            delete from gs.conversation_message where conversation_id in (
                select c.id from gs.conversation c where c.request_id = request_to_delete.request_id
            );
            delete from gs.conversation where request_id = request_to_delete.request_id;
            delete from gs.project_methodology_update where request_id = request_to_delete.request_id;
            delete from gs.request_methodology where request_id = request_to_delete.request_id;
			delete from gs.stakeholder_consultation where request_id = request_to_delete.request_id;
			delete from gs.request_activity where request_id = request_to_delete.request_id;
			delete from gs.batch_request where request_id = request_to_delete.request_id;
			delete from gs.batch where parent_request_id = request_to_delete.request_id;
       		delete from gs.pii_metadata where request_id = request_to_delete.request_id;
			delete from gs."comment" where request_id = request_to_delete.request_id;
			delete from gs.request_round where request_id = request_to_delete.request_id;
			delete from gs.request_status_history where request_id = request_to_delete.request_id;
			delete from gs.request_account_assignment where request_id = request_to_delete.request_id;
			delete from gs.request_document where request_id = request_to_delete.request_id;
			delete from gs.base_project_request where request_id = request_to_delete.request_id;
			delete from gs.request where id = request_to_delete.request_id;
			raise notice 'deleted request %', request_to_delete.request_id;
        END LOOP;

		update gs.project set master_project_id = null where master_project_id = project_to_delete.id;
		update gs.project set parent_project_id = null where parent_project_id = project_to_delete.id;
        delete from gs.external_system_project_update where project_id = project_to_delete.id;
        delete from gs.project_project_location where project_id = project_to_delete.id;
		delete from gs.project_project_asset where project_id = project_to_delete.id;
		delete from gs.project_methodology where project_id = project_to_delete.id;
		delete from gs.project_sustainable_development_goal where project_id = project_to_delete.id;
		delete from gs.project_status_history where base_project_id = project_to_delete.id;
		delete from gs.project_project_type  where project_id = project_to_delete.id;
		delete from gs.project_crediting_period_methodology where project_id = project_to_delete.id;
        delete from gs.project_crediting_monitoring_period	where project_id = project_to_delete.id;
        delete from gs.project_crediting_period where project_id = project_to_delete.id;
		delete from gs.transition_project where project_id = project_to_delete.id;
		delete from gs.base_project_document where project_id = project_to_delete.id;
		delete from gs.stakeholder_consultation where project_id = project_to_delete.id;
		delete from gs.batch_request where project_id = project_to_delete.id;
		delete from gs.project_risk_summary where project_id = project_to_delete.id;
		delete from gs.pii_metadata where project_id = project_to_delete.id;
		delete from gs.project where ID = project_to_delete.id;
		raise notice 'deleted project %', project_to_delete.id;
    END LOOP;
END $$;