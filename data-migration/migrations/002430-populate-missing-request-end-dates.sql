-- AP-1253: Populate end dates for requests where the end date had not been set due to a bug
-- COMPLETED/INACTIVE IDs =
-- 4: COMPLETED_APPROVED_ID
-- 5: COMPLETED_REJECTED_ID
-- 7: OMITTED_ID
-- 8: CANCELLED_ID

update gs.request
set end_date = (
	select changed_at
	from gs.request_status_history history
	where history.request_id = gs.request.id
	and history.previous_status_id not in (4,5,7,8)
	and history.next_status_id in (4,5,7,8)
	order by history.changed_at asc
	limit 1
)
where gs.request.end_date is null 
and exists (
	select 1 from 
	gs.request_status_history history
	where history.request_id = gs.request.id
	and history.previous_status_id not in (4,5,7,8)
	and history.next_status_id in (4,5,7,8)
);