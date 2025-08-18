package com.vhskillpro.backend.utils.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vhskillpro.backend.modules.user.CustomUserDetails;
import com.vhskillpro.backend.utils.jwt.claims.AccessTokenExtraClaims;
import com.vhskillpro.backend.utils.jwt.claims.RefreshTokenExtraClaims;
import com.vhskillpro.backend.utils.jwt.claims.ResetPasswordTokenExtraClaims;
import com.vhskillpro.backend.utils.jwt.claims.TokenExtraClaims;
import com.vhskillpro.backend.utils.jwt.claims.VerificationTokenExtraClaims;
import io.jsonwebtoken.Claims;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JwtService Tests")
class JwtServiceTests {

  private JwtProperties props;
  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    props = new JwtProperties();
    // Generate a 256-bit key (32 bytes) and base64-encode it
    byte[] secret = new byte[32];
    for (int i = 0; i < secret.length; i++) secret[i] = (byte) (i + 1);
    props.setSecretKey(Base64.getEncoder().encodeToString(secret));
    props.setAccessTokenExpiration(60L);
    props.setRefreshTokenExpiration(120L);
    props.setResetPasswordTokenExpiration(180L);
    props.setVerificationTokenExpiration(240L);

    jwtService = new JwtService(props);
  }

  private CustomUserDetails user(long id) {
    return CustomUserDetails.builder().id(id).email("user@example.com").build();
  }

  @Test
  @DisplayName("generateToken with CustomUserDetails sets subject and not expired")
  void generateToken_withUserDetails() {
    String token =
        jwtService.generateToken(
            AccessTokenExtraClaims.builder()
                .email("user@example.com")
                .roleId(1L)
                .superuser(false)
                .build(),
            user(42));

    String subject = jwtService.getSubject(token);
    assertThat(subject).isEqualTo("42");
    assertThat(jwtService.isValidToken(token)).isTrue();
  }

  @Test
  @DisplayName("generateToken with userId overload sets subject and not expired")
  void generateToken_withUserId() {
    String token =
        jwtService.generateToken(
            VerificationTokenExtraClaims.builder().email("v@example.com").build(), 77L);

    assertThat(jwtService.getSubject(token)).isEqualTo("77");
    assertThat(jwtService.isValidToken(token)).isTrue();
  }

  @Test
  @DisplayName("getPayload returns claims and includes our extras")
  void getPayload_shouldReturnClaims() {
    String token =
        jwtService.generateToken(
            ResetPasswordTokenExtraClaims.builder().email("reset@example.com").build(), 5L);

    Claims claims = jwtService.getPayload(token);
    assertThat(claims.getSubject()).isEqualTo("5");
    assertThat(claims.get("email", String.class)).isEqualTo("reset@example.com");
  }

  @Test
  @DisplayName("isValidToken(token, user) matches user id and is not expired")
  void isValidToken_withUser() {
    String token =
        jwtService.generateToken(RefreshTokenExtraClaims.builder().build(), user(9).getId());
    assertThat(jwtService.isValidToken(token, user(9))).isTrue();
    assertThat(jwtService.isValidToken(token, user(10))).isFalse();
  }

  @Test
  @DisplayName("isTokenExpired returns false for newly generated token")
  void isTokenExpired_falseForNew() {
    String token = jwtService.generateToken(AccessTokenExtraClaims.builder().build(), 1L);
    assertThat(jwtService.isTokenExpired(token)).isFalse();
  }

  @Test
  @DisplayName("generateToken throws for unsupported claims type")
  void generateToken_unsupportedClaims() {
    TokenExtraClaims unsupported = () -> java.util.Map.of("k", "v");
    assertThatThrownBy(() -> jwtService.generateToken(unsupported, 1L))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
