package com.vhskillpro.backend.modules.user.dto;

import com.vhskillpro.backend.common.validation.constraints.EmailNotExisted;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
@Schema(description = "Data transfer object for creating a new user")
public class UserCreateDTO {
  @Schema(description = "Email of the user", example = "user@example.com")
  @NotBlank(message = "Email is required")
  @Email(message = "Email is invalid")
  @EmailNotExisted(message = "Email already exists")
  private String email;

  @Schema(description = "Password of the user")
  @NotBlank(message = "Password is required")
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$",
      message =
          "Password must be at least 8 characters long and contain at least one number, one"
              + " lowercase letter, one uppercase letter, and one special character")
  private String password;

  @Schema(description = "Last name of the user", example = "Doe")
  @NotBlank(message = "Last name is required")
  private String lastName;

  @Schema(description = "First name of the user", example = "John")
  @NotBlank(message = "First name is required")
  private String firstName;
}
