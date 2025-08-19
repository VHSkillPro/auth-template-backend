package com.vhskillpro.backend.modules.user.dto;

import com.vhskillpro.backend.modules.role.dto.RoleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
  private Long id;
  private String email;
  private String firstName;
  private String lastName;
  private boolean enabled;
  private boolean locked;
  private boolean superuser;
  private RoleDTO role;
}
