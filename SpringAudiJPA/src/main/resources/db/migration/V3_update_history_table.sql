USE jpa_audit_db;
ALTER TABLE history
    ADD COLUMN username VARCHAR(255);

