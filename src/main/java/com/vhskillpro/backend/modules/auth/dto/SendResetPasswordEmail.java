package com.vhskillpro.backend.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendResetPasswordEmail {
  @Schema(description = "Email address of the user to resend verification email")
  @NotBlank(message = "Email is required")
  @Email(message = "Email is invalid")
  private String email;
}
