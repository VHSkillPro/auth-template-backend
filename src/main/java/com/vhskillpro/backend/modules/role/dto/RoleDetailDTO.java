package com.vhskillpro.backend.modules.role.dto;

import com.vhskillpro.backend.modules.permission.dto.PermissionDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Detailed Role DTO with associated permissions")
public class RoleDetailDTO extends RoleDTO {
  @Schema(description = "List of permissions associated with the role")
  private List<PermissionDTO> permissions;
}
