package com.vhskillpro.backend.common.validation.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.vhskillpro.backend.modules.permission.PermissionRepository;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionsExistValidator Tests")
class PermissionsExistValidatorTests {

  @Mock private PermissionRepository permissionRepository;
  @Mock private ConstraintValidatorContext context;

  private PermissionsExistValidator validator;

  @BeforeEach
  void setUp() {
    validator = new PermissionsExistValidator();
    ReflectionTestUtils.setField(validator, "permissionRepository", permissionRepository);
  }

  @Test
  @DisplayName("Null or empty list is valid")
  void nullOrEmpty_isValid() {
    assertThat(validator.isValid(null, context)).isTrue();
    assertThat(validator.isValid(List.of(), context)).isTrue();
  }

  @Test
  @DisplayName("Returns true when all IDs exist; false when any missing")
  void returnsTrueWhenAllExist_falseWhenAnyMissing() {
    List<Long> ids = List.of(1L, 2L, 3L);
    when(permissionRepository.countByIdIn(eq(ids))).thenReturn(3L);
    assertThat(validator.isValid(ids, context)).isTrue();

    when(permissionRepository.countByIdIn(eq(ids))).thenReturn(2L);
    assertThat(validator.isValid(ids, context)).isFalse();
  }
}
