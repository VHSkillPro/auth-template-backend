package com.vhskillpro.backend.role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.vhskillpro.backend.exception.AppException;
import com.vhskillpro.backend.modules.permission.Permission;
import com.vhskillpro.backend.modules.permission.PermissionRepository;
import com.vhskillpro.backend.modules.permission.dto.PermissionDTO;
import com.vhskillpro.backend.modules.role.Role;
import com.vhskillpro.backend.modules.role.RoleRepository;
import com.vhskillpro.backend.modules.role.RoleService;
import com.vhskillpro.backend.modules.role.dto.RoleCreateDTO;
import com.vhskillpro.backend.modules.role.dto.RoleDTO;
import com.vhskillpro.backend.modules.role.dto.RoleDetailDTO;
import com.vhskillpro.backend.modules.role.dto.RoleUpdateDTO;
import com.vhskillpro.backend.modules.user.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTests {
  @Mock private RoleRepository roleRepository;

  @Mock private ModelMapper modelMapper;

  @Mock private PermissionRepository permissionRepository;

  @Mock private UserRepository userRepository;

  @InjectMocks private RoleService roleService;

  private Instant now;
  private List<Role> roles;
  private List<RoleDTO> roleDTOs;
  private List<RoleDetailDTO> roleDetailDTOs;
  private List<Permission> permissions;

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
                .build(),
            Permission.builder()
                .id(2L)
                .name("permission:write")
                .title("Write permissions")
                .description("Allows writing permissions")
                .createdAt(now)
                .updatedAt(now)
                .build(),
            Permission.builder()
                .id(3L)
                .name("role:read")
                .title("Read role")
                .description("Allows reading roles")
                .createdAt(now)
                .updatedAt(now)
                .build());

    roles =
        List.of(
            Role.builder()
                .id(1L)
                .name("Admin")
                .title("Administrator")
                .description("Full access to the system")
                .permissions(permissions)
                .createdAt(now)
                .updatedAt(now)
                .build(),
            Role.builder()
                .id(2L)
                .name("User")
                .title("Regular User")
                .description("Limited access to the system")
                .permissions(List.of(permissions.get(0), permissions.get(2)))
                .createdAt(now)
                .updatedAt(now)
                .build(),
            Role.builder()
                .id(3L)
                .name("Guest")
                .title("Guest User")
                .description("Minimal access to the system")
                .permissions(List.of(permissions.get(0)))
                .createdAt(now)
                .updatedAt(now)
                .build());

    roleDTOs =
        roles.stream()
            .map(
                role -> {
                  return RoleDTO.builder()
                      .id(role.getId())
                      .name(role.getName())
                      .title(role.getTitle())
                      .description(role.getDescription())
                      .createdAt(role.getCreatedAt())
                      .updatedAt(role.getUpdatedAt())
                      .build();
                })
            .collect(java.util.stream.Collectors.toList());

    roleDetailDTOs =
        roles.stream()
            .map(
                role -> {
                  return RoleDetailDTO.builder()
                      .id(role.getId())
                      .name(role.getName())
                      .title(role.getTitle())
                      .description(role.getDescription())
                      .createdAt(role.getCreatedAt())
                      .updatedAt(role.getUpdatedAt())
                      .permissions(
                          role.getPermissions().stream()
                              .map(
                                  permission -> {
                                    return PermissionDTO.builder()
                                        .id(permission.getId())
                                        .name(permission.getName())
                                        .title(permission.getTitle())
                                        .description(permission.getDescription())
                                        .createdAt(permission.getCreatedAt())
                                        .updatedAt(permission.getUpdatedAt())
                                        .build();
                                  })
                              .toList())
                      .build();
                })
            .collect(java.util.stream.Collectors.toList());
  }

  @Test
  public void findAll_shouldReturnPageOfPermissionDTOs_whenKeywordMatches() {
    // Arrange
    String keyword = "e";
    Pageable pageable = PageRequest.of(0, 10);

    Role role1 = roles.get(1);
    Role role2 = roles.get(2);
    RoleDTO roleDTO1 = roleDTOs.get(1);
    RoleDTO roleDTO2 = roleDTOs.get(2);

    Page<Role> rolePage = new PageImpl<>(List.of(role1, role2), pageable, 2);

    when(roleRepository.findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            eq(keyword), eq(keyword), eq(pageable)))
        .thenReturn(rolePage);

    when(modelMapper.map(any(Role.class), eq(RoleDTO.class)))
        .thenAnswer(
            invocation -> {
              Role source = invocation.getArgument(0);
              if (source.getId().equals(role1.getId())) {
                return roleDTO1;
              }
              if (source.getId().equals(role2.getId())) {
                return roleDTO2;
              }
              return null;
            });

    // Act
    Page<RoleDTO> result = roleService.findAll(keyword, pageable);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).hasSize(2).containsExactlyInAnyOrder(roleDTO1, roleDTO2);

    verify(roleRepository)
        .findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            eq(keyword), eq(keyword), eq(pageable));
    verify(modelMapper, times(2)).map(any(Role.class), eq(RoleDTO.class));
  }

  @Test
  public void findAll_shouldReturnEmptyPage_whenNoMatches() {
    // Arrange
    String keyword = "12347uasdfashrf8u3q";
    Pageable pageable = PageRequest.of(0, 10);
    Page<Role> emptyPage = Page.empty(pageable);

    when(roleRepository.findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            eq(keyword), eq(keyword), eq(pageable)))
        .thenReturn(emptyPage);

    // Act
    Page<RoleDTO> result = roleService.findAll(keyword, pageable);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(0);
    assertThat(result.getContent()).isEmpty();

    verify(roleRepository)
        .findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            eq(keyword), eq(keyword), eq(pageable));
    verifyNoInteractions(modelMapper);
  }

  @Test
  public void findAll_shouldReturnPage_whenKeywordIsEmpty() {
    // Arrange
    String keyword = "";
    Pageable pageable = PageRequest.of(0, 10);
    Page<Role> rolePage = new PageImpl<>(roles, pageable, roles.size());

    when(roleRepository.findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            eq(keyword), eq(keyword), eq(pageable)))
        .thenReturn(rolePage);

    when(modelMapper.map(any(Role.class), eq(RoleDTO.class)))
        .thenAnswer(
            invocation -> {
              Role source = invocation.getArgument(0);
              return roleDTOs.stream()
                  .filter(dto -> dto.getId().equals(source.getId()))
                  .findFirst()
                  .orElse(null);
            });

    // Act
    Page<RoleDTO> result = roleService.findAll(keyword, pageable);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getTotalElements()).isEqualTo(roles.size());
    assertThat(result.getContent())
        .hasSize(roles.size())
        .containsExactlyInAnyOrderElementsOf(roleDTOs);

    verify(roleRepository)
        .findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            eq(keyword), eq(keyword), eq(pageable));
    verify(modelMapper, times(3)).map(any(Role.class), eq(RoleDTO.class));
  }

  @Test
  public void findById_shouldReturnRoleDTO_whenFound() {
    // Arrange
    Long roleId = 1L;
    Role role = roles.get(0);
    RoleDetailDTO roleDetailDTO = roleDetailDTOs.get(0);

    when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
    when(modelMapper.map(role, RoleDetailDTO.class)).thenReturn(roleDetailDTO);

    // Act
    Optional<RoleDetailDTO> result = roleService.findById(roleId);

    // Assert
    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(roleId);
    assertThat(result.get().getName()).isEqualTo(role.getName());
    assertThat(result.get().getTitle()).isEqualTo(role.getTitle());
    assertThat(result.get().getDescription()).isEqualTo(role.getDescription());
    assertThat(result.get().getCreatedAt()).isEqualTo(role.getCreatedAt());
    assertThat(result.get().getUpdatedAt()).isEqualTo(role.getUpdatedAt());
    assertThat(result.get().getPermissions()).isNotNull();

    verify(roleRepository).findById(roleId);
    verify(modelMapper).map(role, RoleDetailDTO.class);
  }

  @Test
  public void findById_shouldReturnEmptyOptional_whenNotFound() {
    // Arrange
    Long roleId = 999L;

    when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

    // Act
    Optional<RoleDetailDTO> result = roleService.findById(roleId);

    // Assert
    assertThat(result).isNotPresent();

    verify(roleRepository).findById(roleId);
    verifyNoInteractions(modelMapper);
  }

  @Test
  public void create_shouldSaveAndReturnRoleDetailDTO() {
    // Arrange
    RoleCreateDTO input =
        RoleCreateDTO.builder()
            .name("manager")
            .title("Manager")
            .description("Manage stuff")
            .permissionIds(List.of(1L, 2L))
            .build();

    List<Permission> foundPermissions = List.of(permissions.get(0), permissions.get(1));
    when(permissionRepository.findAllById(input.getPermissionIds())).thenReturn(foundPermissions);

    Role saved =
        Role.builder()
            .id(100L)
            .name(input.getName())
            .title(input.getTitle())
            .description(input.getDescription())
            .permissions(foundPermissions)
            .createdAt(now)
            .updatedAt(now)
            .build();
    when(roleRepository.save(any(Role.class))).thenReturn(saved);

    RoleDetailDTO expected =
        RoleDetailDTO.builder()
            .id(100L)
            .name("manager")
            .title("Manager")
            .description("Manage stuff")
            .permissions(
                foundPermissions.stream()
                    .map(
                        p ->
                            PermissionDTO.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .title(p.getTitle())
                                .description(p.getDescription())
                                .createdAt(p.getCreatedAt())
                                .updatedAt(p.getUpdatedAt())
                                .build())
                    .toList())
            .createdAt(now)
            .updatedAt(now)
            .build();
    when(modelMapper.map(saved, RoleDetailDTO.class)).thenReturn(expected);

    // Act
    RoleDetailDTO result = roleService.create(input);

    // Assert
    assertThat(result).isEqualTo(expected);
    verify(permissionRepository).findAllById(input.getPermissionIds());
    verify(roleRepository).save(any(Role.class));
    verify(modelMapper).map(saved, RoleDetailDTO.class);
  }

  @Test
  public void update_shouldUpdateAndReturnRoleDetailDTO_whenFound() {
    // Arrange
    Long id = 1L;
    Role existing = roles.get(0);
    when(roleRepository.findById(id)).thenReturn(Optional.of(existing));

    RoleUpdateDTO update =
        RoleUpdateDTO.builder()
            .title("Updated Title")
            .description("Updated Desc")
            .permissionIds(List.of(2L, 3L))
            .build();

    List<Permission> newPerms = List.of(permissions.get(1), permissions.get(2));
    when(permissionRepository.findAllById(update.getPermissionIds())).thenReturn(newPerms);

    Role saved =
        Role.builder()
            .id(existing.getId())
            .name(existing.getName())
            .title(update.getTitle())
            .description(update.getDescription())
            .permissions(newPerms)
            .createdAt(existing.getCreatedAt())
            .updatedAt(now)
            .build();
    when(roleRepository.save(existing)).thenReturn(saved);

    RoleDetailDTO expected =
        RoleDetailDTO.builder()
            .id(saved.getId())
            .name(saved.getName())
            .title(saved.getTitle())
            .description(saved.getDescription())
            .permissions(
                newPerms.stream()
                    .map(
                        p ->
                            PermissionDTO.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .title(p.getTitle())
                                .description(p.getDescription())
                                .createdAt(p.getCreatedAt())
                                .updatedAt(p.getUpdatedAt())
                                .build())
                    .toList())
            .createdAt(saved.getCreatedAt())
            .updatedAt(saved.getUpdatedAt())
            .build();
    when(modelMapper.map(saved, RoleDetailDTO.class)).thenReturn(expected);

    // Act
    RoleDetailDTO result = roleService.update(id, update);

    // Assert
    assertThat(result).isEqualTo(expected);
    verify(roleRepository).findById(id);
    verify(permissionRepository).findAllById(update.getPermissionIds());
    verify(roleRepository).save(existing);
    verify(modelMapper).map(saved, RoleDetailDTO.class);
  }

  @Test
  public void update_shouldThrowNotFound_whenRoleMissing() {
    // Arrange
    Long id = 404L;
    when(roleRepository.findById(id)).thenReturn(Optional.empty());

    RoleUpdateDTO update =
        RoleUpdateDTO.builder().title("t").description("d").permissionIds(List.of()).build();

    // Act & Assert
    try {
      roleService.update(id, update);
    } catch (AppException ex) {
      assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(ex.getMessage()).isNotBlank();
    }

    verify(roleRepository).findById(id);
    verify(permissionRepository, never()).findAllById(update.getPermissionIds());
    verify(roleRepository, never()).save(any(Role.class));
  }

  @Test
  public void delete_shouldDelete_whenExistsAndNoUserReferences() {
    // Arrange
    Long id = 10L;
    when(roleRepository.existsById(id)).thenReturn(true);
    when(userRepository.existsByRoleId(id)).thenReturn(false);

    // Act
    roleService.delete(id);

    // Assert
    verify(roleRepository).existsById(id);
    verify(userRepository).existsByRoleId(id);
    verify(roleRepository).deleteById(id);
  }

  @Test
  public void delete_shouldThrowNotFound_whenRoleDoesNotExist() {
    // Arrange
    Long id = 11L;
    when(roleRepository.existsById(id)).thenReturn(false);

    // Act & Assert
    try {
      roleService.delete(id);
    } catch (AppException ex) {
      assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    verify(roleRepository).existsById(id);
    verify(userRepository, never()).existsByRoleId(id);
    verify(roleRepository, never()).deleteById(id);
  }

  @Test
  public void delete_shouldThrowConflict_whenUsersExistForRole() {
    // Arrange
    Long id = 12L;
    when(roleRepository.existsById(id)).thenReturn(true);
    when(userRepository.existsByRoleId(id)).thenReturn(true);

    // Act & Assert
    try {
      roleService.delete(id);
    } catch (AppException ex) {
      assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    verify(roleRepository).existsById(id);
    verify(userRepository).existsByRoleId(id);
    verify(roleRepository, never()).deleteById(id);
  }
}
