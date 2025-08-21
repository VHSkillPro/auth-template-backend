package com.vhskillpro.backend.modules.user;

public enum UserMessages {
  USER_INDEX_SUCCESS("Users retrieved successfully"),
  USER_SHOW_SUCCESS("User retrieved successfully"),
  USER_CREATE_SUCCESS("User created successfully"),
  USER_UPDATE_SUCCESS("User updated successfully"),
  USER_DELETE_SUCCESS("User deleted successfully"),
  USER_NOT_FOUND("User not found"),
  USER_ALREADY_ENABLED("User is already enabled"),
  USER_NOT_ENABLED("User account is not enabled. Please enabled it from email or contact support."),
  USER_LOCKED("User account is locked. Please contact support.");

  private final String message;

  UserMessages(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
