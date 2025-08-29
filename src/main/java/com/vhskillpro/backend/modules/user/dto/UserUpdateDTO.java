package com.vhskillpro.backend.modules.user.dto;

import com.vhskillpro.backend.common.validation.constraints.RoleExist;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
  @NotNull(message = "USER_LAST_NAME_REQUIRED")
  private String lastName;

  @NotNull(message = "USER_FIRST_NAME_REQUIRED")
  private String firstName;

  @NotNull(message = "USER_LOCKED_STATUS_REQUIRED")
  private Boolean locked;

  @RoleExist() private Long roleId;
}
