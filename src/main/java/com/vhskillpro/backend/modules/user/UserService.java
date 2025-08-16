package com.vhskillpro.backend.modules.user;

import com.vhskillpro.backend.modules.user.dto.UserDTO;
import com.vhskillpro.backend.modules.user.dto.UserFilterDTO;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
  private final ModelMapper modelMapper;
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository, ModelMapper modelMapper) {
    this.userRepository = userRepository;
    this.modelMapper = modelMapper;
  }

  /**
   * Retrieves a paginated list of users based on the provided filter criteria.
   *
   * <p>The method constructs a dynamic specification using the fields from {@link UserFilterDTO},
   * allowing filtering by keyword (matching full name or email), enabled status, locked status,
   * superuser status, and role name. The results are mapped to {@link UserDTO} objects.
   *
   * @param filter the filter criteria containing keyword, enabled, locked, superuser, and role name
   * @param pageable the pagination information
   * @return a page of {@link UserDTO} objects matching the filter criteria
   */
  @Transactional
  public Page<UserDTO> findAll(UserFilterDTO filter, Pageable pageable) {
    Specification<User> spec =
        Specification.allOf(
            UserSpecification.keywordContainingIgnoreCase(filter.getKeyword()),
            UserSpecification.hasEnabledStatus(filter.getEnabled()),
            UserSpecification.hasLockedStatus(filter.getLocked()),
            UserSpecification.hasSuperuserStatus(filter.getSuperuser()),
            UserSpecification.roleNameContainingIgnoreCase(filter.getRoleName()));
    return userRepository.findAll(spec, pageable).map(user -> modelMapper.map(user, UserDTO.class));
  }

  /**
   * Retrieves a user by their unique identifier and maps the entity to a UserDTO.
   *
   * @param id the unique identifier of the user to retrieve
   * @return an {@link Optional} containing the {@link UserDTO} if found, or empty if not found
   */
  @Transactional
  public Optional<UserDTO> findById(Long id) {
    return userRepository.findById(id).map(user -> modelMapper.map(user, UserDTO.class));
  }

  /**
   * Loads a user's details by their username (email).
   *
   * <p>Retrieves the user from the repository using the provided email. If the user is not found,
   * throws a {@link UsernameNotFoundException}. Constructs a {@link CustomUserDetails} object with
   * the user's information. If the user has a role and is not a superuser, sets the authorities
   * based on the role's permissions.
   *
   * @param username the email of the user to load
   * @return a {@link CustomUserDetails} object containing the user's details
   * @throws UsernameNotFoundException if no user is found with the given email
   */
  @Transactional
  public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmail(username)
            .orElseThrow(
                () -> new UsernameNotFoundException(UserMessages.USER_NOT_FOUND.getMessage()));

    CustomUserDetails userDetails =
        CustomUserDetails.builder()
            .id(user.getId())
            .email(user.getEmail())
            .password(user.getPassword())
            .enabled(user.isEnabled())
            .locked(user.isLocked())
            .superuser(user.isSuperuser())
            .roleId(user.getRole() != null ? user.getRole().getId() : null)
            .verificationToken(user.getVerificationToken())
            .build();

    if (user.getRole() != null && !user.isSuperuser()) {
      List<GrantedAuthority> authorities =
          user.getRole().getPermissions().stream()
              .map(permission -> new SimpleGrantedAuthority(permission.getName()))
              .collect(Collectors.toList());
      userDetails.setAuthorities(authorities);
    }

    return userDetails;
  }

  /**
   * Updates the verification token for a user with the specified user ID.
   *
   * <p>Retrieves the user from the repository, sets the new verification token, saves the updated
   * user entity, and returns a mapped {@link UserDTO}.
   *
   * @param userId the ID of the user whose verification token is to be updated
   * @param token the new verification token to set
   * @return the updated user as a {@link UserDTO}
   * @throws UsernameNotFoundException if the user with the given ID is not found
   */
  @Transactional
  public UserDTO updateVerificationToken(Long userId, String token) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new UsernameNotFoundException(UserMessages.USER_NOT_FOUND.getMessage()));
    user.setVerificationToken(token);
    User updatedUser = userRepository.save(user);
    return modelMapper.map(updatedUser, UserDTO.class);
  }
}
