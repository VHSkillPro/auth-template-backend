package com.vhskillpro.backend.modules.user;

public enum UserMessages {
  USER_INDEX_SUCCESS("Users retrieved successfully"),
  USER_SHOW_SUCCESS("User retrieved successfully"),
  USER_NOT_FOUND("User not found");

  private final String message;

  UserMessages(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
