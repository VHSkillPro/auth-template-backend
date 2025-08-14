package com.vhskillpro.backend.modules.role.dto;

import java.util.List;

import com.vhskillpro.backend.common.validation.constraints.PermissionsExist;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for updating an existing role")
public class RoleUpdateDTO {
  @Schema(description = "Title of the role", example = "Administrator")
  @NotBlank(message = "Title is required")
  private String title;

  @Schema(description = "Description of the role", example = "Full access to all resources")
  @NotNull(message = "Description is required")
  private String description;

  @Schema(description = "List of permission IDs associated with the role", example = "[1, 2, 3]")
  @NotNull(message = "Permissions are required")
  @PermissionsExist(message = "One or more permissions do not exist")
  private List<Long> permissionIds;
}
