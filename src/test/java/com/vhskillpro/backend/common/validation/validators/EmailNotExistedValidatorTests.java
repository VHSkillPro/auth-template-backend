package com.vhskillpro.backend.common.validation.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.vhskillpro.backend.modules.user.UserRepository;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailNotExistedValidator Tests")
class EmailNotExistedValidatorTests {

  @Mock private UserRepository userRepository;
  @Mock private ConstraintValidatorContext context;

  private EmailNotExistedValidator validator;

  @BeforeEach
  void setUp() {
    validator = new EmailNotExistedValidator();
    ReflectionTestUtils.setField(validator, "userRepository", userRepository);
  }

  @Test
  @DisplayName("Null or blank is valid")
  void nullOrBlank_isValid() {
    assertThat(validator.isValid(null, context)).isTrue();
    assertThat(validator.isValid("", context)).isTrue();
    assertThat(validator.isValid("   ", context)).isTrue();
  }

  @Test
  @DisplayName("Returns true when email not exists; false when exists")
  void returnsTrueWhenNotExists_falseWhenExists() {
    when(userRepository.existsByEmail(eq("a@b.com"))).thenReturn(false);
    when(userRepository.existsByEmail(eq("exists@a.com"))).thenReturn(true);

    assertThat(validator.isValid("a@b.com", context)).isTrue();
    assertThat(validator.isValid("exists@a.com", context)).isFalse();
  }
}
