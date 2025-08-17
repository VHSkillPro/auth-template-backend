package com.vhskillpro.backend.utils.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtProperties {
  private String secretKey;
  private Long accessTokenExpiration;
  private Long refreshTokenExpiration;
  private Long resetPasswordTokenExpiration;
  private Long verificationTokenExpiration;
}
