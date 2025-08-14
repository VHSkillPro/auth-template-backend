package com.vhskillpro.backend.modules.role;

public enum RoleMessages {
  ROLE_INDEX_SUCCESS("Roles retrieved successfully"),
  ROLE_SHOW_SUCCESS("Role retrieved successfully"),
  ROLE_CREATE_SUCCESS("Role created successfully"),
  ROLE_UPDATE_SUCCESS("Role updated successfully"),
  ROLE_DELETE_SUCCESS("Role deleted successfully"),
  ROLE_NOT_FOUND("Role not found");

  private String message;

  RoleMessages(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
