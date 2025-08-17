package com.vhskillpro.backend.modules.user;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class UserSpecification {

  /**
   * Creates a {@link Specification} for {@link User} entities that matches users whose email, first
   * name, last name, or full name contains the given keyword, ignoring case.
   *
   * <p>If the keyword is {@code null} or blank, the specification matches all users. Otherwise, it
   * performs a case-insensitive search on the email and the concatenated full name (first name +
   * space + last name).
   *
   * @param keyword the search keyword to match against user email and full name
   * @return a {@link Specification} that matches users containing the keyword (case-insensitive)
   */
  public static Specification<User> keywordContainingIgnoreCase(String keyword) {
    return (root, _, criteriaBuilder) -> {
      if (keyword == null || keyword.isBlank()) {
        return criteriaBuilder.conjunction();
      }

      String likePattern = "%" + keyword.toLowerCase() + "%";
      Path<String> emailPath = root.get("email");
      Path<String> firstNamePath = root.get("firstName");
      Path<String> lastNamePath = root.get("lastName");
      Expression<String> fullNamePath =
          criteriaBuilder.concat(criteriaBuilder.concat(firstNamePath, " "), lastNamePath);

      return criteriaBuilder.or(
          criteriaBuilder.like(criteriaBuilder.lower(emailPath), likePattern),
          criteriaBuilder.like(criteriaBuilder.lower(fullNamePath), likePattern));
    };
  }

  /**
   * Creates a {@link Specification} to filter {@link User} entities based on their enabled status.
   *
   * @param status a string representing the enabled status; if blank or null, no filtering is
   *     applied. If "true", only enabled users are returned; if "false", only disabled users are
   *     returned.
   * @return a {@link Specification} for filtering users by enabled status.
   */
  public static Specification<User> hasEnabledStatus(String status) {
    return (root, _, criteriaBuilder) -> {
      if (!StringUtils.hasText(status)) {
        return criteriaBuilder.conjunction();
      }
      boolean isEnabled = Boolean.parseBoolean(status);
      Path<Boolean> enabledPath = root.get("enabled");
      return isEnabled ? criteriaBuilder.isTrue(enabledPath) : criteriaBuilder.isFalse(enabledPath);
    };
  }

  /**
   * Creates a {@link Specification} to filter {@link User} entities based on their locked status.
   *
   * @param status a {@link String} representing the desired locked status ("true" or "false"). If
   *     the string is empty or null, no filtering is applied.
   * @return a {@link Specification} that matches users with the specified locked status, or all
   *     users if the status is not provided.
   */
  public static Specification<User> hasLockedStatus(String status) {
    return (root, _, criteriaBuilder) -> {
      if (!StringUtils.hasText(status)) {
        return criteriaBuilder.conjunction();
      }
      boolean isLocked = Boolean.parseBoolean(status);
      Path<Boolean> lockedPath = root.get("locked");
      return isLocked ? criteriaBuilder.isTrue(lockedPath) : criteriaBuilder.isFalse(lockedPath);
    };
  }

  /**
   * Creates a {@link Specification} to filter {@link User} entities based on their superuser
   * status.
   *
   * @param status a {@code String} representing the desired superuser status; if blank or null, no
   *     filtering is applied. The value should be parsable as a boolean ("true" or "false").
   * @return a {@link Specification} that matches users whose {@code superuser} field corresponds to
   *     the parsed boolean value.
   */
  public static Specification<User> hasSuperuserStatus(String status) {
    return (root, _, criteriaBuilder) -> {
      if (!StringUtils.hasText(status)) {
        return criteriaBuilder.conjunction();
      }
      boolean isSuperuser = Boolean.parseBoolean(status);
      Path<Boolean> superuserPath = root.get("superuser");
      return isSuperuser
          ? criteriaBuilder.isTrue(superuserPath)
          : criteriaBuilder.isFalse(superuserPath);
    };
  }

  /**
   * Creates a JPA Specification to filter {@link User} entities by their associated role name,
   * performing a case-insensitive containment check.
   *
   * @param roleName the role name substring to search for (case-insensitive)
   * @return a {@link Specification} that matches users whose role name contains the given
   *     substring, ignoring case
   */
  public static Specification<User> roleNameContainingIgnoreCase(String roleName) {
    return (root, _, criteriaBuilder) -> {
      if (roleName == null || roleName.isBlank()) {
        return criteriaBuilder.conjunction();
      }
      String likePattern = "%" + roleName.toLowerCase() + "%";
      Path<String> roleNamePath = root.join("role").get("name");
      return criteriaBuilder.like(criteriaBuilder.lower(roleNamePath), likePattern);
    };
  }
}
