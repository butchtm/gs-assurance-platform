ALTER TABLE gs.document
ADD COLUMN is_missing bool NULL default false;

UPDATE gs.document
SET
  is_missing = true
WHERE
  id in (
    '3859f715-3563-4645-e6c4-08dcee903f67',
    '52d25fdf-517c-4515-e6c5-08dcee903f67',
    '3c1bbef6-dc3a-4fd0-e6b4-08dcee903f67',
    '5ea51b59-e3c7-4b08-e6b5-08dcee903f67',
    '7dd00e66-b0c8-44c7-e6b6-08dcee903f67',
    'd53317e2-d845-4965-e6c6-08dcee903f67',
    'a024e1ef-6447-44e2-e6c7-08dcee903f67',
    'a3cae359-7250-472c-e6cb-08dcee903f67',
    '7f9d536f-fd7f-4355-e6c8-08dcee903f67',
    '9e150068-3c59-48e3-e6c9-08dcee903f67',
    '648e1998-2daf-4b5c-e6ca-08dcee903f67',
    'aef240f4-a5d3-461a-e6dc-08dcee903f67',
    'faad9465-acca-4ab4-e6dd-08dcee903f67',
    '2cd1e9fe-0e0b-4b3b-e6de-08dcee903f67',
    '09996801-7eff-4b87-e6df-08dcee903f67',
    '993cb527-82de-4db1-e6e1-08dcee903f67',
    'fb469577-b617-426e-e6e2-08dcee903f67',
    'c9ba725e-63c9-43be-53d0-08dcee908654',
    'e4c91071-b8b8-44f7-53d9-08dcee908654',
    '6c8e729d-5ce8-4ef3-53db-08dcee908654',
    'fbf3d615-33e9-491e-53dc-08dcee908654',
    '3a18771c-2f98-4b50-53d1-08dcee908654',
    '4ff40c30-12c1-4156-53d2-08dcee908654',
    'a00cb9c0-2257-47ff-e6e0-08dcee903f67',
    '7405ace5-44d6-4665-e6db-08dcee903f67',
    '0ab7d7bb-38a4-42c6-53dd-08dcee908654',
    '5c1250da-c33e-4b26-e6cc-08dcee903f67',
    '4c6d7c1c-f91f-47a4-e6d2-08dcee903f67',
    'aaf47746-1eea-4e4b-e6d3-08dcee903f67',
    '3ff9f85f-df81-48bf-e6d4-08dcee903f67',
    'ac481f0d-ecc0-4e54-e6d5-08dcee903f67',
    '8aba4e59-6e39-4a2f-e6d7-08dcee903f67',
    '80ec6a35-21e7-482a-e6ce-08dcee903f67',
    '7f5639ef-6043-4dce-e6da-08dcee903f67',
    'd8018e5a-0906-43c7-e6d6-08dcee903f67',
    'c64b8470-1848-4b32-e6cd-08dcee903f67',
    '9dbc60cc-5870-4269-e6cf-08dcee903f67',
    '78c9fca2-8d9d-40b5-e6d0-08dcee903f67',
    '329b96ac-c3ec-41c0-e6d1-08dcee903f67',
    '5f71533f-d0a6-4f80-e6d9-08dcee903f67',
    '53334f67-e7cc-4e9b-53fc-08dcee908654',
    '5a04324e-6b16-43f0-53fd-08dcee908654',
    'd827a7ab-98ed-44e4-53fe-08dcee908654',
    '2fc3ac12-61f2-4d9b-53ff-08dcee908654',
    '48621e9c-2824-41aa-53eb-08dcee908654',
    '4724e550-4d67-457e-53e3-08dcee908654',
    'df855552-e6de-49d2-53e4-08dcee908654',
    'c10758c9-af4e-47c7-53e5-08dcee908654',
    '91fd0ccd-86a5-4295-53f6-08dcee908654',
    '400c3238-847d-4998-53f8-08dcee908654',
    '425603ed-15f0-4650-53f9-08dcee908654',
    'a29c4b02-339b-4a17-53fa-08dcee908654',
    '3772589e-350f-4175-53f1-08dcee908654',
    '3511c3bd-a3b3-4552-53f2-08dcee908654',
    '2bb5310a-d83e-41b1-53de-08dcee908654',
    '09be00ae-a388-4fd5-53df-08dcee908654',
    '0122fa7d-4216-483a-53e0-08dcee908654',
    'ec2e16be-f1f5-45cc-53e1-08dcee908654',
    '474d40c7-2de7-47e8-540c-08dcee908654',
    '2a272056-fd9b-4780-540d-08dcee908654',
    'b03a3b0d-75cb-4131-540e-08dcee908654',
    '4fba2e11-725f-455f-540f-08dcee908654',
    'f49537f6-f16b-42ca-5410-08dcee908654',
    'e0253aab-3475-4491-5411-08dcee908654',
    '58f1284d-710a-41e0-5412-08dcee908654',
    '948221cf-8f02-4215-5413-08dcee908654',
    '04bc52e8-d7d9-47ab-5422-08dcee908654',
    'd0db3cbc-4544-471f-6a97-08dceef34178',
    'fd8041be-8d0c-45fc-6a98-08dceef34178',
    '4b939c08-5a66-45b2-6a99-08dceef34178',
    'e87f03e4-951f-4e44-6a9a-08dceef34178',
    '97d25caa-e871-4974-6a9b-08dceef34178',
    '6d7cc893-deed-4f23-6a9c-08dceef34178',
    '04ccb2fc-9403-4f65-6a9e-08dceef34178',
    '26b85812-265e-48a8-6a9f-08dceef34178',
    'e0535e4a-b34c-4735-48e5-08dcf1b22b25',
    '031c64e3-4f38-48ce-eeab-08dcf256b439',
    '8df63bfc-d3c9-485a-eeac-08dcf256b439',
    'b70d4691-01d3-4c80-e6b7-08dcee903f67',
    'de139e93-0648-4765-e6b8-08dcee903f67',
    'ae162e62-b5be-4d6a-e6b9-08dcee903f67',
    '5876e495-b61b-4dc9-e6ba-08dcee903f67',
    '62fd20a9-2d34-4887-e6bb-08dcee903f67',
    '93ca5c33-6834-4e6b-e6bc-08dcee903f67',
    '7572c8e1-633e-4cc6-e6bd-08dcee903f67',
    '67acc54e-58b4-4bce-e6be-08dcee903f67',
    '32f06266-985f-4a96-e6bf-08dcee903f67',
    '8f0120f5-dee3-455d-e6c0-08dcee903f67',
    'd481b919-cd3a-4d63-e6c1-08dcee903f67',
    '5c87e5bb-2e64-4e84-e6c2-08dcee903f67',
    'bc2fa85f-4051-4e06-e6c3-08dcee903f67',
    '47bffde0-b8c4-4ea6-53d4-08dcee908654',
    'fb426990-38ed-4451-53d7-08dcee908654',
    'fae831eb-1631-4f2d-53d8-08dcee908654',
    '42d278b7-55b7-4bfc-53da-08dcee908654',
    '6b470860-96af-4f82-53ef-08dcee908654',
    'b26467e8-32d2-403e-53f0-08dcee908654',
    '6a7b2163-1bd4-4af3-ccb2-08dcffdbd0c4',
    'd07b6aa5-eb35-4585-ccb3-08dcffdbd0c4',
    'fcb38b58-3aeb-4de6-b8e8-08dcffdbd123',
    'cc5ea2af-5444-40e7-dc0c-08dd014f3d91',
    'fd66fe74-4cbb-4f15-dc0d-08dd014f3d91',
    '3bf39377-5fea-4748-3f41-08dd0150c39f',
    'fd8de0b4-77ee-42f4-060a-08dd12cede5e',
    '7c44dd0a-ae26-4c07-060b-08dd12cede5e',
    '86c3360b-6a44-431b-53ec-08dcee908654',
    'a736e4bf-d8ab-4a2b-53ed-08dcee908654',
    '731b35c0-d857-49bf-53ee-08dcee908654',
    'fd4fd56a-60f7-4dc7-48e6-08dcf1b22b25',
    '94ef09c8-0b1a-4141-48e7-08dcf1b22b25',
    'e151ef9f-bf0a-4707-e900-08dd16519c97',
    '57bf4e1f-a8ff-436e-3276-08dd33bc1b4f',
    'dbc5b098-7c43-4932-e6d8-08dcee903f67',
    '75810108-bb66-4249-53f3-08dcee908654',
    '29da10e8-7f73-4679-8507-08dd0adc43ca',
    '9e3fc215-bb1b-4020-53e2-08dcee908654',
    '5c55e5c5-9e15-4ac2-53e6-08dcee908654',
    '07bcb655-03c9-437b-53e7-08dcee908654',
    '155cab35-204c-476d-53e8-08dcee908654',
    'c50de096-31ad-49b6-53e9-08dcee908654',
    'fb47aa5e-a40b-4fc6-53ea-08dcee908654',
    '1e05aee6-1980-463d-53f4-08dcee908654',
    'a49c43fb-96e2-4708-53f5-08dcee908654',
    '03393e74-b2ae-46b7-53f7-08dcee908654',
    'a3464d82-18f2-4839-7d47-08dd51e43c9a',
    '0927082e-2bef-4bfb-3809-08dd524b231c',
    '1ffcb96b-06c5-4719-380a-08dd524b231c',
    '61b23b7d-c506-4968-380b-08dd524b231c',
    '7ddb30a3-b87b-4bdf-380c-08dd524b231c',
    '74459bd6-f21c-4e2b-380d-08dd524b231c',
    'e9eaf0c0-a8b7-48cc-380e-08dd524b231c',
    '18d2dfe3-5b98-4b67-380f-08dd524b231c',
    'e87f3b94-7ba6-4d6f-4b0a-08dd5f9bef9a',
    '8ad6cc56-e3b5-4ddb-7d4c-08dd51e43c9a',
    '2ce17769-40fa-46c0-3818-08dd524b231c',
    'eb8ab0b2-285f-49bf-3819-08dd524b231c',
    '7ae3ab6d-cec5-4a62-381a-08dd524b231c',
    'f792b92c-18ac-4586-2097-08dc01bfd5d2',
    '4fe9aa20-da8d-4e7f-7d49-08dd51e43c9a',
    '08c2107a-70f9-4b57-3811-08dd524b231c',
    'ac6a324b-bcc7-491c-3812-08dd524b231c',
    '34fb8bd1-0dfb-4023-3813-08dd524b231c',
    '17f350f7-ea3a-4a65-3814-08dd524b231c',
    'aaca2158-4c8a-41af-3815-08dd524b231c',
    '7a2f630b-7848-4f84-3816-08dd524b231c',
    'efafde62-3936-4962-20b2-08dd03d41eb8',
    '74c7bea5-cda3-4f70-20b3-08dd03d41eb8',
    '647c7162-6e66-49c7-20b4-08dd03d41eb8',
    'ca5e8247-cc62-4db3-20b5-08dd03d41eb8',
    '35244c95-5009-4ba6-a306-08dd073ee0c2',
    '1a0b2fba-f438-4268-d2b6-08dd106c79ee',
    '4768ad01-7ef4-4238-d2b7-08dd106c79ee',
    'dab34cf4-4994-4822-d2b8-08dd106c79ee',
    '645098e4-1325-42e4-d2b9-08dd106c79ee',
    'fa719b67-32ea-4112-0586-08dd12cede5e',
    '710bae90-5dc0-4460-0587-08dd12cede5e',
    'e424866f-9ed3-4559-0588-08dd12cede5e',
    '3df05546-6803-4543-0589-08dd12cede5e',
    'fdd48fac-4946-43e3-058a-08dd12cede5e',
    '469ab194-6813-49d8-058b-08dd12cede5e',
    'dbdbb66a-8205-4fc3-408f-08dd1d29563b',
    'cec2e03e-1580-4b8d-4091-08dd1d29563b',
    'ed1796d3-7554-46d1-4092-08dd1d29563b'
  );

DROP VIEW IF EXISTS gs.v_public_stakeholder_documents;

CREATE VIEW
  gs.v_public_stakeholder_documents AS
SELECT
  d.id AS document_id,
  r.id AS request_id,
  d.file_name AS filename,
  d.access AS access,
  d.category AS category,
  dt.description AS type,
  d.uploaded_timestamp AS uploaded_timestamp,
  d.modified_timestamp AS modified_timestamp,
  d.is_latest AS is_latest,
  d.is_missing as is_missing,
  COALESCE(ddct.description, 'Supporting') as display_category_type
FROM
  gs.document d
  LEFT JOIN gs.base_project_document bpd ON bpd.document_id = d.id
  LEFT JOIN gs.project p ON p.id = bpd.project_id
  LEFT JOIN gs.project_status ps ON p.project_status_id = ps.id
  LEFT JOIN gs.request_document rd ON rd.document_id = d.id
  LEFT JOIN gs.request r ON rd.request_id = r.id
  LEFT JOIN gs.document_type dt ON d.document_type_id = dt.id
  LEFT JOIN gs.request_type_document_type rtdt ON rtdt.document_type_id = dt.id
  AND rtdt.request_type_id = r.request_type_id
  LEFT JOIN gs.document_display_category ddct ON ddct.id = rtdt.document_display_category_id
  LEFT JOIN gs.stakeholder_consultation sc ON p.id = sc.project_id
WHERE
  sc.consultation_type_id = 1
  AND d.access = true
  AND sc.request_id = r.id;

GRANT
SELECT
  ON gs.v_public_stakeholder_documents TO gs_assurance_db_writer;

DROP VIEW gs.v_public_documents;

CREATE VIEW
  gs.v_public_documents (
    gsid,
    project_name,
    project_status_id,
    project_status_code,
    project_status_description,
    request_id,
    request_name,
    request_index,
    request_type_id,
    request_type_code,
    request_type_description,
    document_display_category_id,
    document_display_category_code,
    document_display_category_description,
    document_type_id,
    document_type_code,
    document_type_description,
    document_id,
    document_file_name,
    document_file_size,
    document_content_type,
    document_access,
    document_uploaded,
    document_modified,
    document_is_missing
  ) AS
SELECT
  p.gsid,
  p.name,
  p.project_status_id,
  ps.code,
  ps.description,
  r.id,
  r.request_name,
  r.index,
  r.request_type_id,
  rt.code,
  rt.description,
  vcrd.document_display_category_id,
  ddc.code,
  ddc.description,
  vcrd.document_type_id,
  dt.code,
  dt.description,
  d.id,
  d.file_name,
  d.file_size,
  d.content_type,
  d.access,
  d.uploaded_timestamp,
  d.modified_timestamp,
  d.is_missing
FROM
  gs.project p
  INNER JOIN gs.project_status ps ON p.project_status_id = ps.id
  INNER JOIN gs.base_project_request bpr ON p.id = bpr.project_id
  INNER JOIN gs.v_categorised_request_document vcrd ON bpr.request_id = vcrd.request_id
  INNER JOIN gs.document_display_category ddc ON vcrd.document_display_category_id = ddc.id
  INNER JOIN gs.document_type dt ON vcrd.document_type_id = dt.id
  INNER JOIN gs.request r ON bpr.request_id = r.id
  INNER JOIN gs.request_type rt ON r.request_type_id = rt.id
  INNER JOIN gs."document" d ON vcrd.document_id = d.id
WHERE
  d.access = true
  AND ps.id != 1;

GRANT
SELECT
  ON gs.v_public_documents TO gs_assurance_db_writer;