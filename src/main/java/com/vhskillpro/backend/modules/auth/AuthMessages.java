package com.vhskillpro.backend.modules.auth;

public enum AuthMessages {
  // Profile
  PROFILE_FETCH_SUCCESS("Profile fetched successfully"),

  // Sign in
  SIGN_IN_SUCCESS("Sign-in successfully"),
  EMAIL_OR_PASSWORD_INVALID("Email or password is invalid"),

  // Refresh
  TOKEN_REFRESH_SUCCESS("Token refresh successfully"),
  INVALID_REFRESH_TOKEN("Invalid refresh token."),
  REFRESH_TOKEN_BLACKLISTED("Refresh token is blacklisted."),

  // Verification
  EMAIL_VERIFICATION_SUCCESS("Email verification successfully"),
  VERIFICATION_TOKEN_NOT_FOUND("Verification token not found."),
  INVALID_VERIFICATION_TOKEN("Invalid verification token."),

  // Send verification email
  RESEND_VERIFICATION_EMAIL_SUCCESS("Verification email resent successfully"),
  EMAIL_SENDING_FAILED("Failed to send email."),
  VERIFICATION_TOKEN_ALREADY_SENT(
      "Verification token has already been sent. Please re-send the email later."),

  // Send reset password email
  RESET_PASSWORD_EMAIL_SENT("Reset password email sent successfully"),
  RESET_PASSWORD_TOKEN_ALREADY_SENT(
      "Reset password token has already been sent. Please re-send the email later."),

  // Reset password
  RESET_PASSWORD_SUCCESS("Reset password successfully"),
  INVALID_RESET_PASSWORD_TOKEN("Invalid reset password token."),

  // Sign out
  SIGN_OUT_SUCCESS("Sign-out successfully");

  private final String message;

  AuthMessages(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
