package com.vhskillpro.backend.utils.email;

import com.vhskillpro.backend.exception.AppException;
import org.springframework.beans.factory.annotation.Value;
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

  public EmailService(JavaMailSender emailSender) {
    this.emailSender = emailSender;
  }

  /**
   * Sends a verification email to the specified recipient with a verification token.
   *
   * <p>The method checks if the user exists by their email address. If the user does not exist, it
   * throws an {@link AppException} with a NOT_FOUND status. If the user exists, it constructs a
   * verification URL containing the token and sends an email with the verification link.
   *
   * @param to the recipient's email address
   * @param token the verification token to be included in the email
   * @return {@code true} if the email was sent successfully, {@code false} otherwise
   * @throws AppException if the user with the specified email does not exist
   */
  public Boolean sendVerificationEmail(String to, String token) {
    try {
      String subject = "Email Verification";
      String verificationUrl =
          "http://" + DOMAIN + ":" + PORT + "/api/v1/auth/verify-email?token=" + token;

      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(to);
      message.setSubject(subject);
      message.setText(
          "Please verify your email by clicking the following link: " + verificationUrl);
      emailSender.send(message);

      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
