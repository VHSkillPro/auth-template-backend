package com.vhskillpro.backend.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshDTO {
  @NotBlank(message = "Refresh token must not be blank")
  private String refreshToken;
}
