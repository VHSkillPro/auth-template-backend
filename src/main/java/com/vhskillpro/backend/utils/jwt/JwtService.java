package com.vhskillpro.backend.utils.jwt;

import com.vhskillpro.backend.modules.user.CustomUserDetails;
import com.vhskillpro.backend.utils.jwt.claims.AccessTokenExtraClaims;
import com.vhskillpro.backend.utils.jwt.claims.RefreshTokenExtraClaims;
import com.vhskillpro.backend.utils.jwt.claims.ResetPasswordTokenExtraClaims;
import com.vhskillpro.backend.utils.jwt.claims.TokenExtraClaims;
import com.vhskillpro.backend.utils.jwt.claims.VerificationTokenExtraClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final JwtProperties jwtProperties;

  public JwtService(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
  }

  /**
   * Generates a JWT token based on the provided extra claims and user details. The expiration time
   * of the token is determined by the type of extra claims:
   *
   * <ul>
   *   <li>{@code AccessTokenExtraClaims}: Uses access token expiration time.
   *   <li>{@code RefreshTokenExtraClaims}: Uses refresh token expiration time.
   *   <li>{@code ResetPasswordTokenExtraClaims}: Uses reset password token expiration time.
   *   <li>{@code VerifyTokenExtraClaims}: Uses verification token expiration time.
   * </ul>
   *
   * The token includes the claims, subject (username), issued time, expiration time, and a unique
   * ID.
   *
   * @param extraClaims the extra claims specifying the token type and additional data
   * @param userDetails the user details for whom the token is generated
   * @return the generated JWT token as a string
   * @throws IllegalArgumentException if the token type is unsupported
   */
  public String generateToken(TokenExtraClaims extraClaims, CustomUserDetails userDetails) {
    // Get expiration time depend on token type
    Long expirationTime = 0L;
    switch (extraClaims) {
      case AccessTokenExtraClaims _ -> {
        expirationTime = jwtProperties.getAccessTokenExpiration();
      }
      case RefreshTokenExtraClaims _ -> {
        expirationTime = jwtProperties.getRefreshTokenExpiration();
      }
      case ResetPasswordTokenExtraClaims _ -> {
        expirationTime = jwtProperties.getResetPasswordTokenExpiration();
      }
      case VerificationTokenExtraClaims _ -> {
        expirationTime = jwtProperties.getVerificationTokenExpiration();
      }
      default -> throw new IllegalArgumentException("Unsupported token type");
    }

    // Build the JWT token
    return Jwts.builder()
        .claims(extraClaims.toMap())
        .subject(userDetails.getId().toString())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expirationTime * 1000))
        .id(UUID.randomUUID().toString())
        .signWith(getSignInKey())
        .compact();
  }

  /**
   * Extracts the subject (typically the user's identifier) from the given JWT token.
   *
   * @param token the JWT token from which to extract the subject
   * @return the subject contained in the token
   */
  public String getSubject(String token) {
    return getPayload(token).getSubject();
  }

  /**
   * Validates a JWT token for a given user.
   *
   * <p>This method checks if the provided token is not expired and if the user ID extracted from
   * the token matches the ID of the provided {@link CustomUserDetails} instance.
   *
   * @param token the JWT token to validate
   * @param userDetails the user details to validate against
   * @return {@code true} if the token is valid and belongs to the user; {@code false} otherwise
   */
  public boolean isValidToken(String token, CustomUserDetails userDetails) {
    // Check if the token is expired
    try {
      if (isTokenExpired(token)) {
        return false;
      }
    } catch (Exception e) {
      return false;
    }

    // Extract user ID from the token
    Long id = Long.valueOf(getPayload(token).getSubject());

    // Validate the user ID against the user details
    return id != null && id.equals(userDetails.getId());
  }

  /**
   * Checks if the provided JWT token is valid.
   *
   * <p>A token is considered valid if it is not expired. If any exception occurs during the
   * validation process, the method returns {@code false}.
   *
   * @param token the JWT token to validate
   * @return {@code true} if the token is valid and not expired; {@code false} otherwise
   */
  public boolean isValidToken(String token) {
    try {
      return !isTokenExpired(token);
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Checks if the provided JWT token has expired.
   *
   * @param token the JWT token to check
   * @return {@code true} if the token is expired, {@code false} otherwise
   */
  public boolean isTokenExpired(String token) {
    Date expiration = getPayload(token).getExpiration();
    return expiration.before(new Date());
  }

  /**
   * Extracts the payload (claims) from a JWT token.
   *
   * @param token the JWT token to parse
   * @return the claims contained in the token's payload
   * @throws io.jsonwebtoken.JwtException if the token is invalid or cannot be parsed
   */
  private Claims getPayload(String token) {
    return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
  }

  /**
   * Retrieves the signing key used for JWT operations.
   *
   * <p>This method decodes the secret key from Base64 format and generates an HMAC SHA key for
   * signing and verifying JWT tokens.
   *
   * @return the {@link Key} object used for JWT signing and verification
   */
  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
