-- Add started_at and finished_at columns to document_pii_analysis table
ALTER TABLE gs.document_pii_analysis ADD started_at timestamp NULL;
ALTER TABLE gs.document_pii_analysis ADD textract_finished_at timestamp NULL;
ALTER TABLE gs.document_pii_analysis ADD comprehend_finished_at timestamp NULL;
ALTER TABLE gs.document_pii_analysis ADD textract_duration_ms bigint NULL;
ALTER TABLE gs.document_pii_analysis ADD comprehend_duration_ms bigint NULL;

