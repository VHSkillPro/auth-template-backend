package com.vhskillpro.backend.modules.avatar;

public enum AvatarMessages {
  AVATAR_GET_SUCCESS("User avatar retrieved successfully"),
  AVATAR_UPLOAD_SUCCESS("User avatar uploaded successfully"),
  AVATAR_DELETE_SUCCESS("User avatar deleted successfully");

  private final String message;

  AvatarMessages(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
