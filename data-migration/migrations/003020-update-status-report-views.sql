drop view if exists gs.v_status_report_requests;

create view
    gs.v_status_report_requests as
select
    r.id,
    rt.code as "type",
    rt.description as "type_description",
    rs.code as "status_code",
    r.start_date,
    r.end_date
from
    gs.request r
    join gs.request_status rs on rs.id = r.current_request_status_id
    join gs.request_type rt on rt.id = r.request_type_id;

GRANT
SELECT
    ON TABLE gs.v_status_report_requests TO gs_assurance_db_writer;