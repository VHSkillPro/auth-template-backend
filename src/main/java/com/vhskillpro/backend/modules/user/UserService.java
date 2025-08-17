package com.vhskillpro.backend.modules.user;

import com.vhskillpro.backend.exception.AppException;
import com.vhskillpro.backend.modules.role.RoleRepository;
import com.vhskillpro.backend.modules.user.dto.UserCreateDTO;
import com.vhskillpro.backend.modules.user.dto.UserDTO;
import com.vhskillpro.backend.modules.user.dto.UserFilterDTO;
import com.vhskillpro.backend.modules.user.dto.UserUpdateDTO;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
  private final ModelMapper modelMapper;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(
      ModelMapper modelMapper,
      UserRepository userRepository,
      RoleRepository roleRepository,
      @Lazy PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.modelMapper = modelMapper;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
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
  @PreAuthorize("hasAnyAuthority('all:all', 'user:read')")
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
  @PreAuthorize("hasAnyAuthority('all:all', 'user:read')")
  @Transactional
  public Optional<UserDTO> findById(Long id) {
    return userRepository.findById(id).map(user -> modelMapper.map(user, UserDTO.class));
  }

  /**
   * Creates a new user with the provided details.
   *
   * @param userCreateDTO the DTO containing user creation details
   * @return the created user mapped to {@link UserDTO}
   */
  @PreAuthorize("hasAnyAuthority('all:all', 'user:create')")
  @Transactional
  public UserDTO create(UserCreateDTO userCreateDTO) {
    // Create user
    User user =
        User.builder()
            .email(userCreateDTO.getEmail())
            .password(passwordEncoder.encode(userCreateDTO.getPassword()))
            .firstName(userCreateDTO.getFirstName())
            .lastName(userCreateDTO.getLastName())
            .enabled(false)
            .locked(false)
            .superuser(false)
            .build();

    // Save user
    User savedUser = userRepository.save(user);

    return modelMapper.map(savedUser, UserDTO.class);
  }

  /**
   * Updates the details of an existing user identified by the given userId.
   *
   * @param userId the ID of the user to update
   * @param userUpdateDTO the DTO containing updated user information
   * @return the updated user as a UserDTO
   * @throws AppException if the user with the given ID is not found
   */
  @PreAuthorize("hasAnyAuthority('all:all', 'user:update')")
  @Transactional
  public UserDTO update(Long userId, UserUpdateDTO userUpdateDTO) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, UserMessages.USER_NOT_FOUND.getMessage()));

    // Assign updated values
    if (userUpdateDTO.getPassword() != null) {
      user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
    }

    user.setFirstName(userUpdateDTO.getFirstName());
    user.setLastName(userUpdateDTO.getLastName());
    user.setLocked(userUpdateDTO.getLocked());
    user.setRole(
        userUpdateDTO.getRoleId() != null
            ? roleRepository.getReferenceById(userUpdateDTO.getRoleId())
            : null);

    // Save updated user
    User updatedUser = userRepository.save(user);

    return modelMapper.map(updatedUser, UserDTO.class);
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
  @Override
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
}
