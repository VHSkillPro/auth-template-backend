package com.vhskillpro.backend.utils.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  private final JavaMailSender emailSender;

  @Value("${server.domain}")
  private String DOMAIN;

  @Value("${server.port}")
  private String PORT;

  @Value("${application.frontend.url}")
  private String FRONTEND_URL;

  public EmailService(JavaMailSender emailSender) {
    this.emailSender = emailSender;
  }

  /**
   * Sends a verification email to the specified recipient with a verification link containing the
   * provided token.
   *
   * @param to the recipient's email address
   * @param token the verification token to be included in the email link
   * @throws MailException if there is an error sending the email
   */
  public void sendVerificationEmail(String to, String token) {
    try {
      String subject = "Email Verification";
      String verificationUrl = FRONTEND_URL + "/verify-email?token=" + token;

      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(to);
      message.setSubject(subject);
      message.setText(
          "Please verify your email by clicking the following link: " + verificationUrl);

      emailSender.send(message);
    } catch (MailException ex) {
      throw ex;
    }
  }

  /**
   * Sends a password reset email to the specified recipient.
   *
   * <p>The email contains a link with a reset token that allows the user to reset their password.
   *
   * @param to the recipient's email address
   * @param token the password reset token to be included in the reset link
   * @throws MailException if there is an error sending the email
   */
  public void sendResetPasswordEmail(String to, String token) {
    try {
      String subject = "Reset Password";
      String resetUrl = FRONTEND_URL + "/reset-password?token=" + token;

      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(to);
      message.setSubject(subject);
      message.setText("To reset your password, please click the following link: " + resetUrl);

      emailSender.send(message);
    } catch (MailException ex) {
      throw ex;
    }
  }
}
