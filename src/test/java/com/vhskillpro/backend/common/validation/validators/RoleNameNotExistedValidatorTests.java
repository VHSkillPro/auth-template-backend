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
@DisplayName("RoleNameNotExistedValidator Tests")
class RoleNameNotExistedValidatorTests {

  @Mock private RoleRepository roleRepository;
  @Mock private ConstraintValidatorContext context;

  private RoleNameNotExistedValidator validator;

  @BeforeEach
  void setUp() {
    validator = new RoleNameNotExistedValidator();
    ReflectionTestUtils.setField(validator, "roleRepository", roleRepository);
  }

  @Test
  @DisplayName("Null or blank is valid")
  void nullOrBlank_isValid() {
    assertThat(validator.isValid(null, context)).isTrue();
    assertThat(validator.isValid("", context)).isTrue();
    assertThat(validator.isValid("   ", context)).isTrue();
  }

  @Test
  @DisplayName("Returns true when role name not exists; false when exists")
  void returnsTrueWhenNotExists_falseWhenExists() {
    when(roleRepository.existsByName(eq("admin"))).thenReturn(true);
    when(roleRepository.existsByName(eq("guest"))).thenReturn(false);

    assertThat(validator.isValid("guest", context)).isTrue();
    assertThat(validator.isValid("admin", context)).isFalse();
  }
}
