package com.vhskillpro.backend.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object for resetting user password")
public class ResetPasswordDTO {
  @Schema(description = "New password for the user")
  @NotBlank(message = "Password is required")
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$",
      message =
          "Password must be at least 8 characters long and contain at least one number, one"
              + " lowercase letter, one uppercase letter, and one special character")
  private String password;

  @Schema(description = "Reset password token")
  @NotBlank(message = "Token is required")
  private String token;
}
