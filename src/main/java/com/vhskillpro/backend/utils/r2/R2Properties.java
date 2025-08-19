package com.vhskillpro.backend.utils.r2;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cloudflare.r2")
public class R2Properties {
  private String accountId;
  private String accessKeyId;
  private String secretAccessKey;
  private long presignedUrlExpiration;
}
