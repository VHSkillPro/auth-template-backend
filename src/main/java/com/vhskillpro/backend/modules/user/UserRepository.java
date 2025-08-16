package com.vhskillpro.backend.modules.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  /**
   * Retrieves a user by their email address.
   *
   * @param email the email address of the user to find
   * @return an {@link Optional} containing the found {@link User}, or empty if no user exists with
   *     the given email
   */
  public Optional<User> findByEmail(String email);

  /**
   * Checks if a user exists with the specified email address.
   *
   * @param email the email address to check for existence
   * @return true if a user with the given email exists, false otherwise
   */
  public boolean existsByEmail(String email);
}
