package com.vhskillpro.backend.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFilterDTO {
  private String keyword;
  private String enabled;
  private String locked;
  private String superuser;
  private String roleName;
}
