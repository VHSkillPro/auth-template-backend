package com.vhskillpro.backend.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vhskillpro.backend.exception.AppException;
import com.vhskillpro.backend.modules.permission.Permission;
import com.vhskillpro.backend.modules.role.Role;
import com.vhskillpro.backend.modules.role.RoleRepository;
import com.vhskillpro.backend.modules.user.CustomUserDetails;
import com.vhskillpro.backend.modules.user.User;
import com.vhskillpro.backend.modules.user.UserMessages;
import com.vhskillpro.backend.modules.user.UserRepository;
import com.vhskillpro.backend.modules.user.UserService;
import com.vhskillpro.backend.modules.user.dto.UserCreateDTO;
import com.vhskillpro.backend.modules.user.dto.UserDTO;
import com.vhskillpro.backend.modules.user.dto.UserFilterDTO;
import com.vhskillpro.backend.modules.user.dto.UserUpdateDTO;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTests {

  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;
  @Mock private ModelMapper modelMapper;
  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserService userService;

  private Instant now;
  private Role roleAdmin;
  private List<Permission> permissions;
  private List<User> users;
  private List<UserDTO> userDTOs;

  @BeforeEach
  void setUp() {
    now = Instant.now();

    permissions =
        List.of(
            Permission.builder()
                .id(1L)
                .name("permission:read")
                .title("Read permissions")
                .description("Allows reading permissions")
                .createdAt(now)
                .updatedAt(now)
                .build());

    roleAdmin =
        Role.builder()
            .id(10L)
            .name("admin")
            .title("Administrator")
            .description("All access")
            .permissions(permissions)
            .createdAt(now)
            .updatedAt(now)
            .build();

    users =
        List.of(
            User.builder()
                .id(1L)
                .email("john@example.com")
                .password("encoded")
                .firstName("John")
                .lastName("Doe")
                .enabled(true)
                .locked(false)
                .superuser(false)
                .role(roleAdmin)
                .createdAt(now)
                .updatedAt(now)
                .build(),
            User.builder()
                .id(2L)
                .email("jane@example.com")
                .password("encoded")
                .firstName("Jane")
                .lastName("Smith")
                .enabled(false)
                .locked(true)
                .superuser(false)
                .role(null)
                .createdAt(now)
                .updatedAt(now)
                .build());

    userDTOs =
        List.of(
            UserDTO.builder()
                .id(1L)
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .enabled(true)
                .locked(false)
                .superuser(false)
                .build(),
            UserDTO.builder()
                .id(2L)
                .email("jane@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .enabled(false)
                .locked(true)
                .superuser(false)
                .build());
  }

  @Nested
  @DisplayName("findAll() tests")
  class FindAllTests {
    @Test
    @DisplayName("Should return paged UserDTOs for filter")
    void shouldReturnPagedUserDTOs_forFilter() {
      Pageable pageable = PageRequest.of(0, 10);
      UserFilterDTO filter =
          UserFilterDTO.builder()
              .keyword("john")
              .enabled("true")
              .locked("")
              .superuser(null)
              .roleName("admin")
              .build();

      Page<User> userPage = new PageImpl<>(List.of(users.get(0)), pageable, 1);
      when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
      when(modelMapper.map(users.get(0), UserDTO.class)).thenReturn(userDTOs.get(0));

      Page<UserDTO> result = userService.findAll(filter, pageable);

      assertThat(result).isNotNull();
      assertThat(result.getTotalElements()).isEqualTo(1);
      assertThat(result.getContent()).containsExactly(userDTOs.get(0));
      verify(userRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should return empty page when repository returns none")
    void shouldReturnEmptyPage_whenRepositoryReturnsNone() {
      Pageable pageable = PageRequest.of(0, 10);
      UserFilterDTO filter = UserFilterDTO.builder().build();

      when(userRepository.findAll(any(Specification.class), eq(pageable)))
          .thenReturn(Page.empty(pageable));

      Page<UserDTO> result = userService.findAll(filter, pageable);
      assertThat(result.getTotalElements()).isEqualTo(0);
      assertThat(result.getContent()).isEmpty();
    }
  }

  @Nested
  @DisplayName("findById() tests")
  class FindByIdTests {
    @Test
    @DisplayName("Should return DTO when user exists")
    void shouldReturnDto_whenUserExists() {
      Long id = 1L;
      when(userRepository.findById(id)).thenReturn(Optional.of(users.get(0)));
      when(modelMapper.map(users.get(0), UserDTO.class)).thenReturn(userDTOs.get(0));

      Optional<UserDTO> result = userService.findById(id);
      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(userDTOs.get(0));
    }

    @Test
    @DisplayName("Should return empty when user missing")
    void shouldReturnEmpty_whenUserMissing() {
      Long id = 99L;
      when(userRepository.findById(id)).thenReturn(Optional.empty());

      Optional<UserDTO> result = userService.findById(id);
      assertThat(result).isNotPresent();
      verify(modelMapper, never()).map(any(User.class), eq(UserDTO.class));
    }
  }

  @Nested
  @DisplayName("create() tests")
  class CreateTests {
    @Test
    @DisplayName("Should encode password, save user, and return mapped DTO")
    void shouldEncodePasswordSaveAndReturnDto() {
      UserCreateDTO input =
          UserCreateDTO.builder()
              .email("new@example.com")
              .password("PlainP@ss1")
              .firstName("New")
              .lastName("User")
              .build();

      when(passwordEncoder.encode(eq("PlainP@ss1"))).thenReturn("ENCODED");

      User saved =
          User.builder()
              .id(123L)
              .email("new@example.com")
              .password("ENCODED")
              .firstName("New")
              .lastName("User")
              .enabled(false)
              .locked(false)
              .superuser(false)
              .build();
      when(userRepository.save(any(User.class))).thenReturn(saved);

      UserDTO expected =
          UserDTO.builder()
              .id(123L)
              .email("new@example.com")
              .firstName("New")
              .lastName("User")
              .enabled(false)
              .locked(false)
              .superuser(false)
              .build();
      when(modelMapper.map(saved, UserDTO.class)).thenReturn(expected);

      UserDTO result = userService.create(input);

      assertThat(result).isEqualTo(expected);
      verify(passwordEncoder).encode("PlainP@ss1");
      verify(userRepository).save(any(User.class));
      verify(modelMapper).map(saved, UserDTO.class);
    }
  }

  @Nested
  @DisplayName("update() tests")
  class UpdateTests {
    @Test
    @DisplayName("Should update fields and set role when roleId provided")
    void shouldUpdateFields_andSetRole_whenRoleIdProvided() {
      Long userId = 1L;
      User u0 = users.get(0);
      User existing =
          User.builder()
              .id(u0.getId())
              .email(u0.getEmail())
              .password(u0.getPassword())
              .firstName(u0.getFirstName())
              .lastName(u0.getLastName())
              .enabled(u0.isEnabled())
              .locked(u0.isLocked())
              .superuser(u0.isSuperuser())
              .role(u0.getRole())
              .createdAt(u0.getCreatedAt())
              .updatedAt(u0.getUpdatedAt())
              .build();
      when(userRepository.findById(userId)).thenReturn(Optional.of(existing));

      UserUpdateDTO update =
          UserUpdateDTO.builder()
              .firstName("NewFirst")
              .lastName("NewLast")
              .locked(true)
              .roleId(10L)
              .build();

      when(roleRepository.getReferenceById(10L)).thenReturn(roleAdmin);

      User saved =
          User.builder()
              .id(existing.getId())
              .email(existing.getEmail())
              .password(existing.getPassword())
              .firstName("NewFirst")
              .lastName("NewLast")
              .enabled(existing.isEnabled())
              .locked(true)
              .superuser(existing.isSuperuser())
              .role(roleAdmin)
              .createdAt(existing.getCreatedAt())
              .updatedAt(now)
              .build();
      when(userRepository.save(existing)).thenReturn(saved);

      UserDTO expected =
          UserDTO.builder()
              .id(existing.getId())
              .email(existing.getEmail())
              .firstName("NewFirst")
              .lastName("NewLast")
              .locked(true)
              .enabled(existing.isEnabled())
              .superuser(existing.isSuperuser())
              .build();
      when(modelMapper.map(saved, UserDTO.class)).thenReturn(expected);

      UserDTO result = userService.update(userId, update);

      assertThat(result).isEqualTo(expected);
      verify(roleRepository).getReferenceById(10L);
      verify(userRepository).save(existing);
    }

    @Test
    @DisplayName("Should clear role when roleId is null")
    void shouldClearRole_whenRoleIdIsNull() {
      Long userId = 2L;
      User u1 = users.get(1);
      User existing =
          User.builder()
              .id(u1.getId())
              .email(u1.getEmail())
              .password(u1.getPassword())
              .firstName(u1.getFirstName())
              .lastName(u1.getLastName())
              .enabled(u1.isEnabled())
              .locked(u1.isLocked())
              .superuser(u1.isSuperuser())
              .role(roleAdmin)
              .createdAt(u1.getCreatedAt())
              .updatedAt(u1.getUpdatedAt())
              .build();
      when(userRepository.findById(userId)).thenReturn(Optional.of(existing));

      UserUpdateDTO update =
          UserUpdateDTO.builder().firstName("A").lastName("B").locked(false).roleId(null).build();

      User saved =
          User.builder()
              .id(existing.getId())
              .email(existing.getEmail())
              .password(existing.getPassword())
              .firstName("A")
              .lastName("B")
              .enabled(existing.isEnabled())
              .locked(false)
              .superuser(existing.isSuperuser())
              .role(null)
              .createdAt(existing.getCreatedAt())
              .updatedAt(now)
              .build();
      when(userRepository.save(existing)).thenReturn(saved);

      when(modelMapper.map(saved, UserDTO.class))
          .thenReturn(
              UserDTO.builder()
                  .id(saved.getId())
                  .email(saved.getEmail())
                  .firstName("A")
                  .lastName("B")
                  .locked(false)
                  .enabled(saved.isEnabled())
                  .superuser(saved.isSuperuser())
                  .build());

      UserDTO result = userService.update(userId, update);

      assertThat(result.getFirstName()).isEqualTo("A");
      assertThat(result.getLastName()).isEqualTo("B");
      verify(roleRepository, never()).getReferenceById(any());
    }

    @Test
    @DisplayName("Should throw NOT_FOUND when user missing")
    void shouldThrowNotFound_whenUserMissing() {
      Long userId = 404L;
      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      UserUpdateDTO update =
          UserUpdateDTO.builder().firstName("A").lastName("B").locked(false).roleId(1L).build();

      assertThatThrownBy(() -> userService.update(userId, update))
          .isInstanceOf(AppException.class)
          .satisfies(
              ex -> {
                AppException appEx = (AppException) ex;
                assertThat(appEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                assertThat(appEx.getMessage()).isEqualTo(UserMessages.USER_NOT_FOUND.getMessage());
              });
    }
  }

  @Nested
  @DisplayName("delete() tests")
  class DeleteTests {
    @Test
    @DisplayName("Should delete when exists")
    void shouldDelete_whenExists() {
      Long id = 10L;
      when(userRepository.existsById(id)).thenReturn(true);

      userService.delete(id);

      verify(userRepository).existsById(id);
      verify(userRepository).deleteById(id);
    }

    @Test
    @DisplayName("Should throw NOT_FOUND when missing")
    void shouldThrowNotFound_whenMissing() {
      Long id = 11L;
      when(userRepository.existsById(id)).thenReturn(false);

      assertThatThrownBy(() -> userService.delete(id))
          .isInstanceOf(AppException.class)
          .satisfies(
              ex -> {
                AppException appEx = (AppException) ex;
                assertThat(appEx.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
              });
      verify(userRepository, never()).deleteById(any());
    }
  }

  @Nested
  @DisplayName("loadUserByUsername() tests")
  class LoadUserByUsernameTests {
    @Test
    @DisplayName("Should build CustomUserDetails with authorities from role permissions")
    void shouldBuildCustomUserDetails_withAuthoritiesFromRolePermissions() {
      User user = users.get(0);
      when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

      CustomUserDetails details = userService.loadUserByUsername(user.getEmail());

      assertThat(details.getId()).isEqualTo(user.getId());
      assertThat(details.getUsername()).isEqualTo(user.getEmail());
      assertThat(details.isEnabled()).isEqualTo(user.isEnabled());
      assertThat(details.isAccountNonLocked()).isEqualTo(!user.isLocked());
      List<String> authorityNames =
          details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
      assertThat(authorityNames)
          .containsExactlyInAnyOrderElementsOf(
              permissions.stream().map(Permission::getName).toList());
    }

    @Test
    @DisplayName("Should grant all:all for superuser")
    void shouldGrantAllForSuperuser() {
      User u = users.get(0);
      User superUser =
          User.builder()
              .id(u.getId())
              .email(u.getEmail())
              .password(u.getPassword())
              .firstName(u.getFirstName())
              .lastName(u.getLastName())
              .enabled(u.isEnabled())
              .locked(u.isLocked())
              .superuser(true)
              .role(u.getRole())
              .createdAt(u.getCreatedAt())
              .updatedAt(u.getUpdatedAt())
              .build();
      when(userRepository.findByEmail(superUser.getEmail())).thenReturn(Optional.of(superUser));

      CustomUserDetails details = userService.loadUserByUsername(superUser.getEmail());
      List<String> authorityNames =
          details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
      assertThat(authorityNames).containsExactly("all:all");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user missing")
    void shouldThrowUsernameNotFound_whenMissing() {
      when(userRepository.findByEmail("absent@example.com")).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.loadUserByUsername("absent@example.com"))
          .isInstanceOf(UsernameNotFoundException.class)
          .hasMessage(UserMessages.USER_NOT_FOUND.getMessage());
    }
  }
}
