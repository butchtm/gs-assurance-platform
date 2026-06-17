insert into gs.batch_type
    (id, code, description)
values
    (4, 'TWO_OR_MORE_VPA_CPA', '2 or more VPA/CPA');

insert into gs.batch_mapping
	(batch_type_id, request_type_id, project_classification_id)
values
    (4, 1000, 2),
    (4, 1006, 2),
    (4, 1010, 2),
    (4, 1012, 2),
    (4, 1013, 2),
    (4, 1014, 2),
    (4, 1015, 2),
    (4, 1016, 2);