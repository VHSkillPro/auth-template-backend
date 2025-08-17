package com.vhskillpro.backend.initializer;

import com.vhskillpro.backend.modules.auth.AuthService;
import com.vhskillpro.backend.modules.user.User;
import com.vhskillpro.backend.modules.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SuperUserInitializer implements CommandLineRunner {
  private static final Logger logger = LoggerFactory.getLogger(SuperUserInitializer.class);

  private final Validator validator;
  private final AuthService authService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final SuperUserProperties superUserProperties;

  public SuperUserInitializer(
      Validator validator,
      AuthService authService,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      SuperUserProperties superUserProperties) {
    this.validator = validator;
    this.authService = authService;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.superUserProperties = superUserProperties;
  }

  @Override
  @Transactional
  public void run(String... args) {
    logger.info("Starting SuperUserInitializer...");

    // Validate super user properties
    if (!validateProperties()) {
      logger.error("Super user properties validation failed. Initialization aborted.");
      return;
    }

    // Find existing user
    User existingUser = userRepository.findByEmail(superUserProperties.getEmail()).orElse(null);

    // Check if super user already exists
    if (existingUser != null) {
      if (existingUser.isEnabled()) {
        logger.info("Super user already exists. Skipping creation.");
      } else {
        try {
          authService.sendVerificationEmail(existingUser.getEmail());
          logger.info("Verification email sent to super user: {}", existingUser.getEmail());
        } catch (MailException ex) {
          logger.error("Failed to send verification email to super user");
        }
      }

      return;
    }

    // Create user
    User user =
        User.builder()
            .email(superUserProperties.getEmail())
            .password(passwordEncoder.encode(superUserProperties.getPassword()))
            .firstName(superUserProperties.getFirstName())
            .lastName(superUserProperties.getLastName())
            .enabled(false)
            .locked(false)
            .superuser(true)
            .build();
    User savedUser = userRepository.save(user);

    // Send verification email
    try {
      authService.sendVerificationEmail(savedUser.getEmail());
      logger.info("Verification email sent to super user: {}", savedUser.getEmail());
    } catch (MailException ex) {
      logger.error("Failed to send verification email to super user");
    }
  }

  /**
   * Validates the properties of the super user.
   *
   * <p>This method checks the following:
   *
   * <ul>
   *   <li>Email: Must not be blank and must be a valid email format.
   *   <li>Password: Must not be blank and must match the required complexity:
   *       <ul>
   *         <li>At least one digit
   *         <li>At least one lowercase letter
   *         <li>At least one uppercase letter
   *         <li>At least one special character from !@#$%^&*
   *         <li>Minimum length of 8 characters
   *       </ul>
   *   <li>First name: Must not be blank.
   *   <li>Last name: Must not be blank.
   * </ul>
   *
   * @return {@code true} if all properties are valid; {@code false} otherwise.
   */
  private boolean validateProperties() {
    // Validate email
    if (!validator
        .validate(superUserProperties.getEmail(), NotBlank.class, Email.class)
        .isEmpty()) {
      return false;
    }

    // Validate password
    if (!validator.validate(superUserProperties.getPassword(), NotBlank.class).isEmpty()) {
      return false;
    }
    Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$");
    if (!pattern.matcher(superUserProperties.getPassword()).matches()) {
      return false;
    }

    // Validate first name
    if (!validator.validate(superUserProperties.getFirstName(), NotBlank.class).isEmpty()) {
      return false;
    }

    // Validate last name
    if (!validator.validate(superUserProperties.getLastName(), NotBlank.class).isEmpty()) {
      return false;
    }

    return true;
  }
}
