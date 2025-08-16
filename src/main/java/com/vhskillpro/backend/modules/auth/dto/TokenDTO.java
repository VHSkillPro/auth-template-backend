package com.vhskillpro.backend.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {
  private String accessToken;
  private String refreshToken;
  private Long accessTokenExpiration;
  private Long refreshTokenExpiration;
}
