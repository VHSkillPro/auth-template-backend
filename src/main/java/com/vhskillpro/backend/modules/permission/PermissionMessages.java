package com.vhskillpro.backend.modules.permission;

public enum PermissionMessages {
  PERMISSION_INDEX_SUCCESS("Permissions retrieved successfully"),
  PERMISSION_SHOW_SUCCESS("Permission retrieved successfully"),
  PERMISSION_NOT_FOUND("Permission not found.");

  private final String message;

  PermissionMessages(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
