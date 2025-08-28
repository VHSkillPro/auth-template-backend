package com.vhskillpro.backend.modules.role.dto;

import com.vhskillpro.backend.modules.permission.dto.PermissionDTO;
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
public class RoleDetailDTO extends RoleDTO {
  private List<PermissionDTO> permissions;
}
