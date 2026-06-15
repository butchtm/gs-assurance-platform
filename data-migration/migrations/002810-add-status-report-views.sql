create view gs.v_status_report_projects as
select p.id, pt.code as "type", pt.description as "type_description", p.project_start_date as "start_date"
from gs.project p 
join gs.project_project_type ppt on ppt.project_id = p.id
join gs.project_type pt on ppt.project_type_id = pt.id;

create view gs.v_status_report_requests as
select r.id, rt.code as "type", rt.description as "type_description", rs.code as "status_code", r.start_date
from gs.request r 
join gs.request_status rs on rs.id = r.current_request_status_id
join gs.request_type rt on rt.id = r.request_type_id;

GRANT SELECT ON TABLE gs.v_status_report_projects TO gs_assurance_db_writer;
GRANT SELECT ON TABLE gs.v_status_report_requests TO gs_assurance_db_writer;