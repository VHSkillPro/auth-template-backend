-- This migration script inserts permissions into the tbl_permission table.

INSERT INTO tbl_permission (name, title, description)
VALUES ('permission:read', 'Read permissions', 'Allows reading permissions');