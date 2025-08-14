-- Create table role_permission (PostgreSQL)

CREATE TABLE tbl_role_permission (
  id SERIAL PRIMARY KEY,
  role_id BIGINT NOT NULL,
  permission_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (role_id, permission_id),
  FOREIGN KEY (role_id) REFERENCES tbl_role(id),
  FOREIGN KEY (permission_id) REFERENCES tbl_permission(id)
);