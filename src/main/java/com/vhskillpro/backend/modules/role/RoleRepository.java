package com.vhskillpro.backend.modules.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
  /**
   * Retrieves a paginated list of {@link Role} entities whose name and title
   * contain the specified substrings,
   * ignoring case considerations.
   *
   * @param name     the substring to search for within the role's name
   *                 (case-insensitive)
   * @param title    the substring to search for within the role's title
   *                 (case-insensitive)
   * @param pageable the pagination information
   * @return a {@link Page} of {@link Role} entities matching the search criteria
   */
  public Page<Role> findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
      String name,
      String title,
      Pageable pageable);

  /**
   * Checks if a role with the specified name exists in the repository.
   *
   * @param name the name of the role to check for existence
   * @return true if a role with the given name exists, false otherwise
   */
  public boolean existsByName(String name);
}
