package com.vhskillpro.backend.modules.auth;

import com.vhskillpro.backend.exception.AppException;
import com.vhskillpro.backend.modules.auth.dto.ProfileDTO;
import com.vhskillpro.backend.modules.auth.dto.RefreshDTO;
import com.vhskillpro.backend.modules.auth.dto.ResetPasswordDTO;
import com.vhskillpro.backend.modules.auth.dto.SignInDTO;
import com.vhskillpro.backend.modules.auth.dto.SignUpDTO;
import com.vhskillpro.backend.modules.auth.dto.TokenDTO;
import com.vhskillpro.backend.modules.user.CustomUserDetails;
import com.vhskillpro.backend.modules.user.User;
import com.vhskillpro.backend.modules.user.UserMessages;
import com.vhskillpro.backend.modules.user.UserRepository;
import com.vhskillpro.backend.modules.user.UserService;
import com.vhskillpro.backend.modules.user.dto.UserDTO;
import com.vhskillpro.backend.utils.email.EmailService;
import com.vhskillpro.backend.utils.jwt.JwtProperties;
import com.vhskillpro.backend.utils.jwt.JwtService;
import com.vhskillpro.backend.utils.jwt.claims.AccessTokenExtraClaims;
import com.vhskillpro.backend.utils.jwt.claims.RefreshTokenExtraClaims;
import com.vhskillpro.backend.utils.jwt.claims.ResetPasswordTokenExtraClaims;
import com.vhskillpro.backend.utils.jwt.claims.VerificationTokenExtraClaims;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final ModelMapper modelMapper;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProperties jwtProperties;
  private final JwtService jwtService;
  private final EmailService emailService;
  private final UserService userService;
  private final BlacklistTokenRepository blacklistTokenRepository;

  AuthService(
      PasswordEncoder passwordEncoder,
      JwtProperties jwtProperties,
      JwtService jwtService,
      EmailService emailService,
      UserRepository userRepository,
      UserService userService,
      BlacklistTokenRepository blacklistTokenRepository,
      ModelMapper modelMapper) {
    this.passwordEncoder = passwordEncoder;
    this.jwtProperties = jwtProperties;
    this.jwtService = jwtService;
    this.emailService = emailService;
    this.userRepository = userRepository;
    this.userService = userService;
    this.blacklistTokenRepository = blacklistTokenRepository;
    this.modelMapper = modelMapper;
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
      throw new AppException(HttpStatus.UNAUTHORIZED, UserMessages.USER_NOT_ENABLED.getMessage());
    }

    // If user is locked
    if (user.isLocked()) {
      throw new AppException(HttpStatus.UNAUTHORIZED, UserMessages.USER_LOCKED.getMessage());
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
    RefreshTokenExtraClaims refreshTokenExtraClaims =
        RefreshTokenExtraClaims.builder().email(user.getEmail()).build();

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

  /**
   * Refreshes the authentication tokens using the provided refresh token.
   *
   * <p>Validates the refresh token, retrieves the associated user, and generates new access and
   * refresh tokens.
   *
   * @param refreshDTO the DTO containing the refresh token
   * @return a {@link TokenDTO} containing the new access and refresh tokens along with their
   *     expiration times
   * @throws AppException if the refresh token is invalid
   */
  public TokenDTO refresh(RefreshDTO refreshDTO) {
    String token = refreshDTO.getRefreshToken();

    // Validate the refresh token
    if (!jwtService.isValidToken(token)) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, AuthMessages.INVALID_REFRESH_TOKEN.getMessage());
    }

    // Check if refresh token is blacklisted
    if (blacklistTokenRepository.existsById(token)) {
      throw new AppException(
          HttpStatus.UNAUTHORIZED, AuthMessages.REFRESH_TOKEN_BLACKLISTED.getMessage());
    }

    // Get user from token
    String email = jwtService.getPayload(token).get("email", String.class);
    CustomUserDetails userDetails = userService.loadUserByUsername(email);

    // Generate new tokens
    AccessTokenExtraClaims accessTokenExtraClaims =
        AccessTokenExtraClaims.builder()
            .email(userDetails.getEmail())
            .roleId(userDetails.getRoleId())
            .superuser(userDetails.isSuperuser())
            .build();
    RefreshTokenExtraClaims refreshTokenExtraClaims =
        RefreshTokenExtraClaims.builder().email(userDetails.getEmail()).build();

    TokenDTO tokenDTO =
        TokenDTO.builder()
            .accessToken(jwtService.generateToken(accessTokenExtraClaims, userDetails))
            .refreshToken(jwtService.generateToken(refreshTokenExtraClaims, userDetails))
            .accessTokenExpiration(jwtProperties.getAccessTokenExpiration())
            .refreshTokenExpiration(jwtProperties.getRefreshTokenExpiration())
            .build();

    // Blacklist the old refresh token
    blacklistTokenRepository.save(
        BlacklistToken.builder()
            .token(token)
            .timeToLive(jwtProperties.getRefreshTokenExpiration())
            .build());

    return tokenDTO;
  }

  /**
   * Sends a reset password email to the user with the specified email address.
   *
   * @param email the email address of the user requesting a password reset
   * @throws AppException if the user is not found, is locked or disabled, or if email sending fails
   */
  public void sendResetPasswordEmail(String email) {
    // Check if user exists
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, UserMessages.USER_NOT_FOUND.getMessage()));

    // Check if user is locked or disabled
    if (user.isLocked()) {
      throw new AppException(HttpStatus.NOT_FOUND, UserMessages.USER_LOCKED.getMessage());
    }

    if (!user.isEnabled()) {
      throw new AppException(HttpStatus.NOT_FOUND, UserMessages.USER_NOT_ENABLED.getMessage());
    }

    // Check if a reset password token is already sent
    String oldToken = user.getVerificationToken();
    if (jwtService.isValidToken(oldToken)
        && Long.valueOf(jwtService.getSubject(oldToken)) == user.getId()) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, AuthMessages.RESET_PASSWORD_TOKEN_ALREADY_SENT.getMessage());
    }

    // Generate reset password token
    ResetPasswordTokenExtraClaims extraClaims =
        ResetPasswordTokenExtraClaims.builder().email(email).build();

    String token = jwtService.generateToken(extraClaims, user.getId());

    // Send reset password email
    try {
      emailService.sendResetPasswordEmail(user.getEmail(), token);
    } catch (MailException e) {
      throw new AppException(
          HttpStatus.INTERNAL_SERVER_ERROR, AuthMessages.EMAIL_SENDING_FAILED.getMessage());
    }

    // Update user's verification token
    user.setVerificationToken(token);
    userRepository.save(user);
  }

  /**
   * Resets the user's password using the provided reset password token and new password. If any
   * validation fails, an {@link AppException} is thrown with the appropriate HTTP status and
   * message.
   *
   * @param resetPasswordDTO Data transfer object containing the reset password token and new
   *     password.
   * @throws AppException if the token is invalid, the user is not found, the user is
   *     locked/disabled, or the token does not match.
   */
  public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
    // Validate the reset password token
    if (!jwtService.isValidToken(resetPasswordDTO.getToken())) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, AuthMessages.INVALID_RESET_PASSWORD_TOKEN.getMessage());
    }

    // Get user from token
    String email = jwtService.getPayload(resetPasswordDTO.getToken()).get("email", String.class);
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, UserMessages.USER_NOT_FOUND.getMessage()));

    // Check if user is locked
    if (user.isLocked()) {
      throw new AppException(HttpStatus.NOT_FOUND, UserMessages.USER_LOCKED.getMessage());
    }

    // Check if user is enabled
    if (!user.isEnabled()) {
      throw new AppException(HttpStatus.NOT_FOUND, UserMessages.USER_NOT_ENABLED.getMessage());
    }

    // Check if reset password token is valid
    if (!resetPasswordDTO.getToken().equals(user.getVerificationToken())) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, AuthMessages.INVALID_RESET_PASSWORD_TOKEN.getMessage());
    }

    // Update user's password
    user.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));
    user.setVerificationToken(null);
    userRepository.save(user);
  }

  /**
   * Signs out the user by invalidating the provided refresh token.
   *
   * @param refreshToken the refresh token to be invalidated
   * @throws AppException if the token is invalid or does not match the authenticated user
   */
  public void signOut(String accessToken, String refreshToken) {
    // Check refresh token is valid
    if (!jwtService.isValidToken(refreshToken)) {
      throw new AppException(
          HttpStatus.UNAUTHORIZED, AuthMessages.INVALID_REFRESH_TOKEN.getMessage());
    }

    if (blacklistTokenRepository.existsById(refreshToken)) {
      throw new AppException(
          HttpStatus.UNAUTHORIZED, AuthMessages.INVALID_REFRESH_TOKEN.getMessage());
    }

    String email = jwtService.getPayload(refreshToken).get("email", String.class);
    CustomUserDetails userDetails =
        (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (userDetails.getEmail() == null || !userDetails.getEmail().equals(email)) {
      throw new AppException(
          HttpStatus.UNAUTHORIZED, AuthMessages.INVALID_REFRESH_TOKEN.getMessage());
    }

    // Blacklist the refresh token
    BlacklistToken blacklistToken =
        BlacklistToken.builder()
            .token(refreshToken)
            .timeToLive(jwtProperties.getRefreshTokenExpiration())
            .build();
    blacklistTokenRepository.save(blacklistToken);

    // Blacklist the access token
    BlacklistToken accessBlacklistToken =
        BlacklistToken.builder()
            .token(accessToken)
            .timeToLive(jwtProperties.getAccessTokenExpiration())
            .build();
    blacklistTokenRepository.save(accessBlacklistToken);
  }

  /**
   * Registers a new user with the provided sign-up details.
   *
   * <p>This method creates a new {@link User} entity using the information from the given {@link
   * SignUpDTO}, encodes the password, sets default values for enabled, locked, and superuser
   * fields, saves the user to the repository, and returns a mapped {@link UserDTO}.
   *
   * @param signUpDTO the data transfer object containing user registration details
   * @return a {@link UserDTO} representing the newly registered user
   */
  @Transactional
  public UserDTO signUp(SignUpDTO signUpDTO) {
    User user =
        User.builder()
            .email(signUpDTO.getEmail())
            .password(passwordEncoder.encode(signUpDTO.getPassword()))
            .firstName(signUpDTO.getFirstName())
            .lastName(signUpDTO.getLastName())
            .enabled(false)
            .locked(false)
            .superuser(false)
            .build();

    // Save user
    User savedUser = userRepository.save(user);

    return modelMapper.map(savedUser, UserDTO.class);
  }
}
