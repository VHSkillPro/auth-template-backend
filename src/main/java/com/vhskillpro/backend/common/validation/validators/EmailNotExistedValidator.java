package com.vhskillpro.backend.common.validation.validators;

import com.vhskillpro.backend.common.validation.constraints.EmailNotExisted;
import com.vhskillpro.backend.modules.user.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailNotExistedValidator implements ConstraintValidator<EmailNotExisted, String> {
  @Autowired private UserRepository userRepository;

  @Override
  public boolean isValid(String email, ConstraintValidatorContext context) {
    if (email == null || email.trim().isEmpty()) {
      return true;
    }
    return !userRepository.existsByEmail(email);
  }
}
