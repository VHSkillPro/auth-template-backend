package com.vhskillpro.backend.modules.permission;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
  /**
   * Retrieves a paginated list of {@link Permission} entities whose {@code name} and {@code title}
   * contain the specified substrings, ignoring case sensitivity.
   *
   * @param name the substring to search for within the {@code name} field (case-insensitive)
   * @param title the substring to search for within the {@code title} field (case-insensitive)
   * @param pageable the pagination information
   * @return a {@link Page} of {@link Permission} entities matching the search criteria
   */
  public Page<Permission> findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
      String name, String title, Pageable pageable);

  /**
   * Counts the number of Permission entities whose IDs are contained in the specified list.
   *
   * @param permissionIds the list of permission IDs to match
   * @return the count of Permission entities with IDs in the given list
   */
  public Long countByIdIn(List<Long> permissionIds);

  /**
   * Retrieves a list of Permission entities whose IDs are contained in the specified list.
   *
   * @param permissionIds the list of permission IDs to search for
   * @return a list of Permission entities matching the given IDs
   */
  public List<Permission> findByIdIn(List<Long> permissionIds);
}
