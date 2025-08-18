package com.vhskillpro.backend.permission;

import java.util.Optional;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;

@TestConfiguration
public class TestJpaConfig {

  // Provide a simple AuditorAware bean if auditing is enabled anywhere (safe no-op)
  @Bean
  @Primary
  public AuditorAware<String> auditorAware() {
    return () -> Optional.of("test");
  }
}
