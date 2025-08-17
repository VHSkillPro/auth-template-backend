package com.vhskillpro.backend.modules.user.dto;

import com.vhskillpro.backend.common.validation.constraints.RoleExist;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$",
      message =
          "Password must be at least 8 characters long and contain at least one number, one"
              + " lowercase letter, one uppercase letter, and one special character")
  @Schema(
      description = "User's password (skip update if value is null)",
      example = "\"P@ssw0rd123\" or null")
  private String password;

  @NotNull(message = "Last name is not null")
  private String lastName;

  @NotNull(message = "First name is not null")
  private String firstName;

  @NotNull(message = "Locked status is not null")
  private Boolean locked;

  @RoleExist(message = "Role does not exist")
  private Long roleId;
}
