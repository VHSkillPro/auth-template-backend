package com.vhskillpro.backend.modules.user.dto;

import com.vhskillpro.backend.common.validation.constraints.EmailNotExisted;
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
public class UserCreateDTO {
  @NotBlank(message = "USER_EMAIL_REQUIRED")
  @Email(message = "USER_EMAIL_INVALID")
  @EmailNotExisted(message = "USER_EMAIL_ALREADY_EXISTS")
  private String email;

  @NotBlank(message = "USER_PASSWORD_REQUIRED")
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$",
      message = "USER_PASSWORD_INVALID")
  private String password;

  @NotBlank(message = "USER_LAST_NAME_REQUIRED")
  private String lastName;

  @NotBlank(message = "USER_FIRST_NAME_REQUIRED")
  private String firstName;
}
