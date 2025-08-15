-- Insert permissions for users

INSERT INTO tbl_permission (name, title, description)
VALUES
  ('user:read', 'Read users', 'Allows reading users'),
  ('user:create', 'Create users', 'Allows creating users'),
  ('user:update', 'Update users', 'Allows updating users'),
  ('user:delete', 'Delete users', 'Allows deleting users');