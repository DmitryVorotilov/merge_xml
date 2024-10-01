-- liquibase formatted sql

-- changeset A.Bogaichuk:1727792099000-2
ALTER TABLE validation_process ALTER COLUMN total_doc_ref DROP NOT NULL;

-- changeset A.Bogaichuk:1727792099000-1
ALTER TABLE validation_file_history RENAME COLUMN fail_fields TO failure_reason;