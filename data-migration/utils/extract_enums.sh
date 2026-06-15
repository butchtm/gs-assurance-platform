#!/usr/bin/env python3

import json
import sys

schema = 'gs'

enum_table_mapping = {
    'CarbonStream': 'carbon_stream',
    'CurrentStandardVersion': 'current_standard_version',
    'FeeSchedule': 'fee_schedule',
    'Methodologies': 'methodology',
    'ProgrammeOfActivities': 'programme_of_activities',
    'ProjectAssets': 'project_asset',
    'ProjectCategory': 'project_category',
    'ProjectLocation': 'project_location',
    'ProjectSize': 'project_size',
    'ProjectTypes': 'project_type',
    'RegistrationType': 'registration_type',
    'SDGs': 'sustainable_development_goal',
    'ReviewDocumentStatus': 'request_document_status',
    'ReviewType': 'request_type',
    'InvoiceStatus': 'invoice_status',
    'ProjectState': 'project_status',
    'ReviewStatus': 'request_status',
    'DocumentType': 'document_type'}

enums = json.load(sys.stdin)

enums_done = set()

for enum in enums:
	enum_name = enum['EnumName']
	if enum_name in enums_done:
		continue
	table_name = enum_table_mapping[enum_name]
	print(f"""
DROP TABLE IF EXISTS {schema}.{table_name};
CREATE TABLE {schema}.{table_name} (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);""")
	for enum_value in enum['EnumItems']:
		code = enum_value['EnumCode']
		desc = enum_value['EnumDescription']
		desc = desc.replace("'", "''")
		id = enum_value['ID']
		print(f"""
INSERT INTO {schema}.{table_name}
	(id, code, description)
	VALUES ({id},'{code}','{desc}');""")
	enums_done.add(enum_name)
