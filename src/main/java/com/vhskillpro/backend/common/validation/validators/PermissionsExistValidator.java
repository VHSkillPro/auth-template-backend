package com.vhskillpro.backend.common.validation.validators;

import com.vhskillpro.backend.common.validation.constraints.PermissionsExist;
import com.vhskillpro.backend.modules.permission.PermissionRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class PermissionsExistValidator
    implements ConstraintValidator<PermissionsExist, List<Long>> {
  @Autowired private PermissionRepository permissionRepository;

  @Override
  public boolean isValid(List<Long> permissionIds, ConstraintValidatorContext context) {
    if (permissionIds == null || permissionIds.isEmpty()) {
      return true;
    }

    long foundPermissions = permissionRepository.countByIdIn(permissionIds);
    return foundPermissions == permissionIds.size();
  }
}
