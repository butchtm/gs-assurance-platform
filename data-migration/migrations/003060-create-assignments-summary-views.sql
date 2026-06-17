create view gs.v_request_assignments_summary as
select a.id as account_id, a.account_type_id as account_type_id, a.name as account_name, raa.assignment_type_id as assignment_type_id,
    count(*) as total,
    count(*) filter (where r.current_request_status_id in (4, 5, 7, 8)) as closed_count,
    count(*) filter (where r.current_request_status_id not in (4, 5, 7, 8)) as open_count,
    count(*) filter (where r.current_request_status_id = 10) as in_progress_count,
    count(*) filter (where r.current_request_status_id = 10 and r.current_round_index = 1) as first_round_count
from gs.request_account_assignment raa
left join gs.account a on raa.account_id = a.id
left join gs.request r on raa.request_id = r.id
group by a.id, a.name, raa.assignment_type_id
order by account_name;

create view gs.v_request_assignments_summary_totals as
select raa.assignment_type_id, a.account_type_id,
    count(*) as total,
    count(*) filter (where r.current_request_status_id in (4, 5, 7, 8)) as closed_count,
    count(*) filter (where r.current_request_status_id not in (4, 5, 7, 8)) as open_count,
    count(*) filter (where r.current_request_status_id = 10) as in_progress_count,
    count(*) filter (where r.current_request_status_id = 10 and r.current_round_index = 1) as first_round_count
from gs.request_account_assignment raa
left join gs.account a on raa.account_id = a.id
left join gs.request r on raa.request_id = r.id
group by raa.assignment_type_id, a.account_type_id
order by assignment_type_id;