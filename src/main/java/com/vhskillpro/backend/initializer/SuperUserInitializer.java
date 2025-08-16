package com.vhskillpro.backend.initializer;

import com.vhskillpro.backend.modules.user.User;
import com.vhskillpro.backend.modules.user.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SuperUserInitializer implements CommandLineRunner {
  private static final Logger logger = LoggerFactory.getLogger(SuperUserInitializer.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final SuperUserProperties superUserProperties;

  public SuperUserInitializer(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      SuperUserProperties superUserProperties) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.superUserProperties = superUserProperties;
  }

  @Override
  @Transactional
  public void run(String... args) {
    logger.info("Starting SuperUserInitializer...");

    if (userRepository.existsByEmail(superUserProperties.getEmail())) {
      logger.info("Super user already exists, skipping creation.");
      return;
    }

    User superUser = new User();
    superUser.setEmail(superUserProperties.getEmail());
    superUser.setPassword(passwordEncoder.encode(superUserProperties.getPassword()));
    superUser.setFirstName(superUserProperties.getFirstName());
    superUser.setLastName(superUserProperties.getLastName());
    superUser.setEnabled(true);
    superUser.setSuperuser(true);

    userRepository.save(superUser);
    logger.info("Super user created successfully.");
  }
}
