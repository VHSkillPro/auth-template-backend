package com.vhskillpro.backend.modules.role.dto;

import com.vhskillpro.backend.common.validation.constraints.PermissionsExist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateDTO {
  @NotBlank(message = "ROLE_TITLE_REQUIRED")
  private String title;

  @NotNull(message = "ROLE_DESCRIPTION_REQUIRED")
  private String description;

  @NotNull(message = "ROLE_PERMISSION_IDS_REQUIRED")
  @PermissionsExist()
  private List<Long> permissionIds;
}
