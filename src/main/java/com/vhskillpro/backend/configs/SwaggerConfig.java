package com.vhskillpro.backend.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Backend API")
            .version("1.0.0")
            .contact(new Contact()
                .name("Ngô Văn Hải")
                .email("nvhai090903@gmail.com"))
            .license(new License().name("Apache 2.0")
                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
  }
}
