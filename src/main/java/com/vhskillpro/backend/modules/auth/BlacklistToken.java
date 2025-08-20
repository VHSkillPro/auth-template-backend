package com.vhskillpro.backend.modules.auth;

import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("blacklist_tokens")
public class BlacklistToken {
  @Id private String token;

  @TimeToLive(unit = TimeUnit.SECONDS)
  private Long timeToLive;
}
