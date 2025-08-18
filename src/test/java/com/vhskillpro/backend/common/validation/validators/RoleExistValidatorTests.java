package com.vhskillpro.backend.common.validation.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.vhskillpro.backend.modules.role.RoleRepository;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleExistValidator Tests")
class RoleExistValidatorTests {

  @Mock private RoleRepository roleRepository;
  @Mock private ConstraintValidatorContext context;

  private RoleExistValidator validator;

  @BeforeEach
  void setUp() {
    validator = new RoleExistValidator();
    ReflectionTestUtils.setField(validator, "roleRepository", roleRepository);
  }

  @Test
  @DisplayName("Null is valid")
  void nullIsValid() {
    assertThat(validator.isValid(null, context)).isTrue();
  }

  @Test
  @DisplayName("Returns true when role exists; false when not")
  void returnsTrueWhenExists_falseWhenNot() {
    when(roleRepository.existsById(eq(1L))).thenReturn(true);
    when(roleRepository.existsById(eq(2L))).thenReturn(false);

    assertThat(validator.isValid(1L, context)).isTrue();
    assertThat(validator.isValid(2L, context)).isFalse();
  }
}
