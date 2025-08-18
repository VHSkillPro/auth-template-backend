package com.vhskillpro.backend.modules.auth;

public enum AuthMessages {
  SIGN_IN_SUCCESS("Sign-in successful"),
  EMAIL_VERIFICATION_SUCCESS("Email verification successful"),
  RESEND_VERIFICATION_EMAIL_SUCCESS("Verification email resent successfully"),
  PROFILE_FETCH_SUCCESS("Profile fetched successfully"),
  EMAIL_OR_PASSWORD_INVALID("Email or password is invalid"),
  VERIFICATION_TOKEN_ALREADY_SENT(
      "Verification token has already been sent. Please re-send the email later."),
  VERIFICATION_TOKEN_NOT_FOUND("Verification token not found."),
  INVALID_VERIFICATION_TOKEN("Invalid verification token."),
  EMAIL_SENDING_FAILED("Failed to send email."),
  USER_LOCKED("User account is locked. Please contact support."),
  USER_NOT_ENABLED("User account is not enabled. Please enabled it from email or contact support.");

  private final String message;

  AuthMessages(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
