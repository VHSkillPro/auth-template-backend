-- Insert permissions for roles

INSERT INTO tbl_permission (name, title, description)
VALUES
  ('role:read', 'Read roles', 'Allows reading roles'),
  ('role:create', 'Create roles', 'Allows creating roles'),
  ('role:update', 'Update roles', 'Allows updating roles'),
  ('role:delete', 'Delete roles', 'Allows deleting roles');