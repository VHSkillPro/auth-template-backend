package com.vhskillpro.backend.utils.email;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService Tests")
class EmailServiceTests {

  @Mock private JavaMailSender mailSender;

  @InjectMocks private EmailService emailService;

  @Test
  @DisplayName("Should send verification email with correct content")
  void shouldSendVerificationEmail_withCorrectContent() {
    ReflectionTestUtils.setField(emailService, "DOMAIN", "localhost");
    ReflectionTestUtils.setField(emailService, "PORT", "8080");

    emailService.sendVerificationEmail("user@example.com", "abc123");

    verify(mailSender).send(any(SimpleMailMessage.class));
  }

  @Test
  @DisplayName("Should rethrow MailException from mail sender")
  void shouldRethrowMailException_fromMailSender() {
    ReflectionTestUtils.setField(emailService, "DOMAIN", "localhost");
    ReflectionTestUtils.setField(emailService, "PORT", "8080");

    doThrow(new MailException("send-failed") {})
        .when(mailSender)
        .send(any(SimpleMailMessage.class));

    assertThatThrownBy(() -> emailService.sendVerificationEmail("user@example.com", "abc123"))
        .isInstanceOf(MailException.class)
        .hasMessage("send-failed");
  }
}
