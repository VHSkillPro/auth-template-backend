package com.vhskillpro.backend.common.validation.validators;

import com.vhskillpro.backend.common.validation.constraints.RoleExist;
import com.vhskillpro.backend.modules.role.RoleRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class RoleExistValidator implements ConstraintValidator<RoleExist, Long> {
  @Autowired private RoleRepository roleRepository;

  @Override
  public boolean isValid(Long roleId, ConstraintValidatorContext context) {
    if (roleId == null) {
      return true;
    }
    return roleRepository.existsById(roleId);
  }
}
