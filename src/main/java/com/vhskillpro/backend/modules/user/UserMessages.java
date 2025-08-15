package com.vhskillpro.backend.modules.user;

public enum UserMessages {
  USER_INDEX_SUCCESS("Users retrieved successfully");

  private final String message;

  UserMessages(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
