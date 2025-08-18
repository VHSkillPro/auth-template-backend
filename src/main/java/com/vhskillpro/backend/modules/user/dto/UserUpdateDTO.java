package com.vhskillpro.backend.modules.user.dto;

import com.vhskillpro.backend.common.validation.constraints.RoleExist;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object for updating user information")
public class UserUpdateDTO {
  @NotNull(message = "Last name is not null")
  private String lastName;

  @NotNull(message = "First name is not null")
  private String firstName;

  @NotNull(message = "Locked status is not null")
  private Boolean locked;

  @RoleExist(message = "Role does not exist")
  private Long roleId;
}
