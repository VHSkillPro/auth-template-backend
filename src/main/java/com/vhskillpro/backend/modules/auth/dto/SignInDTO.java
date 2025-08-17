package com.vhskillpro.backend.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data transfer object for user sign-in")
public class SignInDTO {
  @Schema(description = "The email address of the user")
  @NotBlank(message = "Email is required")
  @Email(message = "Email is invalid")
  private String email;

  @Schema(description = "The password of the user")
  @NotBlank(message = "Password is required")
  private String password;
}
