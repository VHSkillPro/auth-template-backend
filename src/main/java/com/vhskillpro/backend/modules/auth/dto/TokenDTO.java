package com.vhskillpro.backend.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data transfer object for authentication tokens")
public class TokenDTO {
  @Schema(
      description = "The access token for the user",
      example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String accessToken;

  @Schema(
      description = "The refresh token for the user",
      example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
  private String refreshToken;

  @Schema(description = "The expiration time of the access token", example = "3600")
  private Long accessTokenExpiration;

  @Schema(description = "The expiration time of the refresh token", example = "86400")
  private Long refreshTokenExpiration;
}
