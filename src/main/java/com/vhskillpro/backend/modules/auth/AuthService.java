package com.vhskillpro.backend.modules.auth;

import com.vhskillpro.backend.exception.AppException;
import com.vhskillpro.backend.modules.auth.dto.SignInDTO;
import com.vhskillpro.backend.modules.auth.dto.TokenDTO;
import com.vhskillpro.backend.modules.user.CustomUserDetails;
import com.vhskillpro.backend.modules.user.User;
import com.vhskillpro.backend.modules.user.UserMessages;
import com.vhskillpro.backend.modules.user.UserRepository;
import com.vhskillpro.backend.modules.user.UserService;
import com.vhskillpro.backend.utils.email.EmailService;
import com.vhskillpro.backend.utils.jwt.JwtProperties;
import com.vhskillpro.backend.utils.jwt.JwtService;
import com.vhskillpro.backend.utils.jwt.claims.AccessTokenExtraClaims;
import com.vhskillpro.backend.utils.jwt.claims.RefreshTokenExtraClaims;
import com.vhskillpro.backend.utils.jwt.claims.VerificationTokenExtraClaims;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private final UserService userService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProperties jwtProperties;
  private final JwtService jwtService;
  private final EmailService emailService;

  AuthService(
      UserService userService,
      PasswordEncoder passwordEncoder,
      JwtProperties jwtProperties,
      JwtService jwtService,
      EmailService emailService,
      UserRepository userRepository) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.jwtProperties = jwtProperties;
    this.jwtService = jwtService;
    this.emailService = emailService;
    this.userRepository = userRepository;
  }

  @Transactional
  public TokenDTO signIn(SignInDTO signInDTO) {
    // Check if user exists
    CustomUserDetails userDetails;
    try {
      userDetails = userService.loadUserByUsername(signInDTO.getEmail());
    } catch (UsernameNotFoundException ex) {
      throw new AppException(
          HttpStatus.UNAUTHORIZED, AuthMessages.EMAIL_OR_PASSWORD_INVALID.getMessage());
    } catch (Exception ex) {
      throw ex;
    }

    // If user is not enabled
    if (!userDetails.isEnabled()) {
      throw new AppException(HttpStatus.UNAUTHORIZED, AuthMessages.USER_NOT_ENABLED.getMessage());
    }

    // If user is locked
    if (userDetails.isLocked()) {
      throw new AppException(HttpStatus.UNAUTHORIZED, AuthMessages.USER_LOCKED.getMessage());
    }

    // Check if the password matches
    if (!passwordEncoder.matches(signInDTO.getPassword(), userDetails.getPassword())) {
      throw new AppException(
          HttpStatus.UNAUTHORIZED, AuthMessages.EMAIL_OR_PASSWORD_INVALID.getMessage());
    }

    // Generate JWT token
    AccessTokenExtraClaims accessTokenExtraClaims =
        AccessTokenExtraClaims.builder()
            .email(userDetails.getEmail())
            .roleId(userDetails.getRoleId())
            .superuser(userDetails.isSuperuser())
            .build();
    RefreshTokenExtraClaims refreshTokenExtraClaims = RefreshTokenExtraClaims.builder().build();

    TokenDTO tokenDTO =
        TokenDTO.builder()
            .accessToken(jwtService.generateToken(accessTokenExtraClaims, userDetails))
            .refreshToken(jwtService.generateToken(refreshTokenExtraClaims, userDetails))
            .accessTokenExpiration(jwtProperties.getAccessTokenExpiration())
            .refreshTokenExpiration(jwtProperties.getRefreshTokenExpiration())
            .build();

    return tokenDTO;
  }

  /**
   * Sends a verification email to the specified user email address.
   *
   * @param email the email address of the user to send the verification email to
   * @return {@code true} if the verification email was sent successfully; otherwise, an exception
   *     is thrown
   * @throws AppException if the user is not found, already enabled, a token was already sent, or
   *     email sending fails
   */
  public boolean sendVerificationEmail(String email) {
    // Check if user exists
    CustomUserDetails userDetails;
    try {
      userDetails = userService.loadUserByUsername(email);
    } catch (UsernameNotFoundException ex) {
      throw new AppException(HttpStatus.NOT_FOUND, UserMessages.USER_NOT_FOUND.getMessage());
    } catch (Exception ex) {
      throw ex;
    }

    // Check if user is already enabled
    if (userDetails.isEnabled()) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, UserMessages.USER_ALREADY_ENABLED.getMessage());
    }

    // Check if verification token already exists
    String oldToken = userDetails.getVerificationToken();
    if (oldToken != null && jwtService.isValidToken(oldToken, userDetails)) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, AuthMessages.VERIFICATION_TOKEN_ALREADY_SENT.getMessage());
    }

    // Generate verification token
    VerificationTokenExtraClaims extraClaims =
        VerificationTokenExtraClaims.builder().email(email).build();
    String token = jwtService.generateToken(extraClaims, userDetails);

    // Send verification email
    boolean isSend = emailService.sendVerificationEmail(userDetails.getEmail(), token);
    if (!isSend) {
      throw new AppException(
          HttpStatus.INTERNAL_SERVER_ERROR, AuthMessages.EMAIL_SENDING_FAILED.getMessage());
    }

    // Save token
    userService.updateVerificationToken(userDetails.getId(), token);

    return isSend;
  }

  /**
   * Verifies a user's email using the provided verification token.
   *
   * @param token the verification token to validate and use for enabling the user
   * @throws AppException if verification fails for any of the above reasons
   */
  @Transactional
  public void verifyEmail(String token) {
    // Check if the token is valid
    if (!jwtService.isValidToken(token)) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, AuthMessages.INVALID_VERIFICATION_TOKEN.getMessage());
    }

    // Get user ID from token
    Long userId = Long.valueOf(jwtService.getSubject(token));
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, UserMessages.USER_NOT_FOUND.getMessage()));

    // Check if verification token is valid
    if (user.getVerificationToken() == null || !user.getVerificationToken().equals(token)) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, AuthMessages.VERIFICATION_TOKEN_NOT_FOUND.getMessage());
    }

    // Check if user is already enabled
    if (user.isEnabled()) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, UserMessages.USER_ALREADY_ENABLED.getMessage());
    }

    // Enable the user and clear the verification token
    user.setEnabled(true);
    user.setVerificationToken(null);
  }
}
