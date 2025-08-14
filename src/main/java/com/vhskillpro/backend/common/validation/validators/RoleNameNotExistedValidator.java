package com.vhskillpro.backend.common.validation.validators;

import org.springframework.beans.factory.annotation.Autowired;

import com.vhskillpro.backend.common.validation.constraints.RoleNameNotExisted;
import com.vhskillpro.backend.modules.role.RoleRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoleNameNotExistedValidator implements ConstraintValidator<RoleNameNotExisted, String> {
  @Autowired
  private RoleRepository roleRepository;

  @Override
  public boolean isValid(String roleName, ConstraintValidatorContext context) {
    if (roleName == null || roleName.trim().isEmpty()) {
      return true;
    }

    return !roleRepository.existsByName(roleName);
  }
}
