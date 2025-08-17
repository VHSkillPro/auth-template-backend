package com.vhskillpro.backend.initializer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "application.admin")
public class SuperUserProperties {
  private String email;
  private String password;
  private String firstName;
  private String lastName;
}
