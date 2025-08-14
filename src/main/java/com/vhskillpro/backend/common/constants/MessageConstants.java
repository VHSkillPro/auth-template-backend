package com.vhskillpro.backend.common.constants;

public enum MessageConstants {
  BAD_REQUEST("The request was invalid or cannot be served. Please check the input data."),
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
