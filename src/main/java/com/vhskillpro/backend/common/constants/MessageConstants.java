package com.vhskillpro.backend.common.constants;

public enum MessageConstants {
  INTERNAL_SERVER_ERROR("An unexpected error occurred. Please try again later."),
  ;

  private final String message;

  MessageConstants(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
