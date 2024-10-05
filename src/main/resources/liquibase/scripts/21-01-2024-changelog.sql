-- liquibase formatted sql

-- changeset astonuser:1724242384342-2
CREATE TABLE validation_file_history (validation_id UUID NOT NULL, file_name VARCHAR(255) NOT NULL, doc_ref VARCHAR(255) NOT NULL, is_success BOOLEAN NOT NULL, fail_fields TEXT, validation_date TIMESTAMP WITHOUT TIME ZONE NOT NULL, validation_process_id UUID, CONSTRAINT pk_validation_file_history PRIMARY KEY (validation_id));

-- changeset astonuser:1724242384342-3
CREATE TABLE validation_process (id UUID NOT NULL, dir_ref VARCHAR(255) NOT NULL, is_success BOOLEAN NOT NULL, total_doc_ref VARCHAR(255), validation_process_date TIMESTAMP WITHOUT TIME ZONE NOT NULL, CONSTRAINT pk_validation_process PRIMARY KEY (id));

-- changeset astonuser:1724242384342-4
ALTER TABLE validation_file_history ADD CONSTRAINT FK_VALIDATION_FILE_HISTORY_ON_VALIDATION_PROCESS FOREIGN KEY (validation_process_id) REFERENCES validation_process (id);

-- changeset astonuser:1724242384342-1
ALTER TABLE history ALTER COLUMN doc_ref TYPE VARCHAR(255) USING (doc_ref::VARCHAR(255));

