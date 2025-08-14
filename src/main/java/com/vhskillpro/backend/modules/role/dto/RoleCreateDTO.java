package com.vhskillpro.backend.modules.role.dto;

import com.vhskillpro.backend.common.validation.constraints.PermissionsExist;
import com.vhskillpro.backend.common.validation.constraints.RoleNameNotExisted;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for creating a new role")
public class RoleCreateDTO {
  @Schema(description = "Name of the role", example = "admin")
  @NotBlank(message = "Name is required")
  @RoleNameNotExisted(message = "Role name already exists")
  private String name;

  @Schema(description = "Title of the role", example = "Administrator")
  @NotBlank(message = "Title is required")
  private String title;

  @Schema(description = "Description of the role", example = "Full access to all resources")
  @NotNull(message = "Description is not null")
  private String description;

  @Schema(description = "List of permission IDs associated with the role", example = "[1, 2, 3]")
  @NotNull(message = "Permission IDs are not null")
  @PermissionsExist(message = "One or more permissions do not exist")
  private List<Long> permissionIds;
}
