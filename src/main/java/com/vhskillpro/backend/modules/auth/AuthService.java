package com.vhskillpro.backend.modules.auth;

import com.vhskillpro.backend.exception.AppException;
import com.vhskillpro.backend.modules.auth.dto.ProfileDTO;
import com.vhskillpro.backend.modules.auth.dto.SignInDTO;
import com.vhskillpro.backend.modules.auth.dto.TokenDTO;
import com.vhskillpro.backend.modules.user.CustomUserDetails;
import com.vhskillpro.backend.modules.user.User;
import com.vhskillpro.backend.modules.user.UserMessages;
import com.vhskillpro.backend.modules.user.UserRepository;
import com.vhskillpro.backend.utils.email.EmailService;
import com.vhskillpro.backend.utils.jwt.JwtProperties;
import com.vhskillpro.backend.utils.jwt.JwtService;
import com.vhskillpro.backend.utils.jwt.claims.AccessTokenExtraClaims;
import com.vhskillpro.backend.utils.jwt.claims.RefreshTokenExtraClaims;
import com.vhskillpro.backend.utils.jwt.claims.VerificationTokenExtraClaims;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProperties jwtProperties;
  private final JwtService jwtService;
  private final EmailService emailService;

  AuthService(
      PasswordEncoder passwordEncoder,
      JwtProperties jwtProperties,
      JwtService jwtService,
      EmailService emailService,
      UserRepository userRepository) {
    this.passwordEncoder = passwordEncoder;
    this.jwtProperties = jwtProperties;
    this.jwtService = jwtService;
    this.emailService = emailService;
    this.userRepository = userRepository;
  }

  /**
   * Authenticates a user based on the provided sign-in credentials.
   *
   * <p>If authentication fails at any step, an {@link AppException} is thrown with an appropriate
   * HTTP status and message.
   *
   * @param signInDTO the data transfer object containing the user's email and password
   * @return a {@link TokenDTO} containing the generated access and refresh tokens along with their
   *     expiration times
   * @throws AppException if the user does not exist, is not enabled, is locked, or if the password
   *     is invalid
   */
  @Transactional
  public TokenDTO signIn(SignInDTO signInDTO) {
    // Check if user exists
    User user =
        userRepository
            .findByEmail(signInDTO.getEmail())
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.UNAUTHORIZED,
                        AuthMessages.EMAIL_OR_PASSWORD_INVALID.getMessage()));

    // If user is not enabled
    if (!user.isEnabled()) {
      throw new AppException(HttpStatus.UNAUTHORIZED, AuthMessages.USER_NOT_ENABLED.getMessage());
    }

    // If user is locked
    if (user.isLocked()) {
      throw new AppException(HttpStatus.UNAUTHORIZED, AuthMessages.USER_LOCKED.getMessage());
    }

    // Check if the password matches
    if (!passwordEncoder.matches(signInDTO.getPassword(), user.getPassword())) {
      throw new AppException(
          HttpStatus.UNAUTHORIZED, AuthMessages.EMAIL_OR_PASSWORD_INVALID.getMessage());
    }

    // Generate JWT token
    AccessTokenExtraClaims accessTokenExtraClaims =
        AccessTokenExtraClaims.builder()
            .email(user.getEmail())
            .roleId(user.getRole() != null ? user.getRole().getId() : null)
            .superuser(user.isSuperuser())
            .build();
    RefreshTokenExtraClaims refreshTokenExtraClaims = RefreshTokenExtraClaims.builder().build();

    TokenDTO tokenDTO =
        TokenDTO.builder()
            .accessToken(jwtService.generateToken(accessTokenExtraClaims, user.getId()))
            .refreshToken(jwtService.generateToken(refreshTokenExtraClaims, user.getId()))
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
  @Transactional
  public void sendVerificationEmail(String email) {
    // Check if user exists
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, UserMessages.USER_NOT_FOUND.getMessage()));

    // Check if user is already enabled
    if (user.isEnabled()) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, UserMessages.USER_ALREADY_ENABLED.getMessage());
    }

    // Check if verification token already exists
    String oldToken = user.getVerificationToken();
    if (jwtService.isValidToken(oldToken)
        && Long.valueOf(jwtService.getSubject(oldToken)) == user.getId()) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, AuthMessages.VERIFICATION_TOKEN_ALREADY_SENT.getMessage());
    }

    // Generate verification token
    VerificationTokenExtraClaims extraClaims =
        VerificationTokenExtraClaims.builder().email(email).build();
    String token = jwtService.generateToken(extraClaims, user.getId());

    // Send verification email
    try {
      emailService.sendVerificationEmail(user.getEmail(), token);
    } catch (MailException e) {
      throw new AppException(
          HttpStatus.INTERNAL_SERVER_ERROR, AuthMessages.EMAIL_SENDING_FAILED.getMessage());
    }

    // Save token
    user.setVerificationToken(token);
    userRepository.save(user);
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
    userRepository.save(user);
  }

  /**
   * Retrieves the profile information for a user by their ID.
   *
   * <p>This method fetches the user entity from the repository, determines the user's permissions
   * (including superuser privileges), and maps the user data to a {@link ProfileDTO}.
   *
   * @param userId the ID of the user whose profile is to be retrieved
   * @return a {@link ProfileDTO} containing the user's profile information and permissions
   * @throws AppException if the user is not found in the repository
   */
  public ProfileDTO getProfile(Long userId) {
    CustomUserDetails userDetails =
        (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    return ProfileDTO.builder()
        .id(userDetails.getId())
        .email(userDetails.getEmail())
        .firstName(userDetails.getFirstName())
        .lastName(userDetails.getLastName())
        .permissions(
            userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList())
        .enabled(userDetails.isEnabled())
        .locked(userDetails.isLocked())
        .superuser(userDetails.isSuperuser())
        .createdAt(userDetails.getCreatedAt())
        .updatedAt(userDetails.getUpdatedAt())
        .build();
  }
}
