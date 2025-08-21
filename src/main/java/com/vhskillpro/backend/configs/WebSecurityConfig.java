package com.vhskillpro.backend.configs;

import com.vhskillpro.backend.exception.CustomAccessDeniedHandler;
import com.vhskillpro.backend.exception.CustomAuthenticationEntryPoint;
import com.vhskillpro.backend.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
  private final JwtAuthenticationFilter jwtAuthFilter;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  public WebSecurityConfig(
      JwtAuthenticationFilter jwtAuthFilter,
      CustomAccessDeniedHandler customAccessDeniedHandler,
      CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    this.customAccessDeniedHandler = customAccessDeniedHandler;
  }

  /**
   * Creates a {@link PasswordEncoder} bean that uses BCrypt hashing algorithm.
   *
   * <p>This encoder is used to securely hash and verify user passwords. BCrypt is recommended for
   * password storage due to its adaptive nature, making brute-force attacks more difficult.
   *
   * @return a {@link BCryptPasswordEncoder} instance for password encoding
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * An array of URL patterns that are excluded from authentication checks. These endpoints are
   * publicly accessible and do not require authentication.
   */
  private static final String[] WHITELIST_URLS = {
    "/api/v1/auth/sign-in",
    "/api/v1/auth/resend-verification-email",
    "/api/v1/auth/verify-email",
    "/api/v1/auth/refresh",
    "/api/v1/auth/send-reset-password-email",
    "/api/v1/auth/reset-password",
    "/api/v1/auth/sign-up",
    "/swagger-ui/**",
    "/v3/api-docs/**"
  };

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth -> auth.requestMatchers(WHITELIST_URLS).permitAll().anyRequest().authenticated())
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(customAuthenticationEntryPoint)
                    .accessDeniedHandler(customAccessDeniedHandler))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
