package com.vhskillpro.backend.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.vhskillpro.backend.modules.permission.Permission;
import com.vhskillpro.backend.modules.permission.dto.PermissionDTO;
import com.vhskillpro.backend.modules.role.Role;
import com.vhskillpro.backend.modules.role.RoleRepository;
import com.vhskillpro.backend.modules.role.RoleService;
import com.vhskillpro.backend.modules.role.dto.RoleDTO;
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

@ExtendWith(MockitoExtension.class)
public class RoleServiceTests {
  @Mock private RoleRepository roleRepository;

  @Mock private ModelMapper modelMapper;

  @InjectMocks private RoleService roleService;

  private Instant now;
  private List<Role> roles;
  private List<RoleDTO> roleDTOs;
  private List<Permission> permissions;
  private List<PermissionDTO> permissionDTOs;

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

    permissionDTOs =
        List.of(
            PermissionDTO.builder()
                .id(1L)
                .name("permission:read")
                .title("Read permissions")
                .description("Allows reading permissions")
                .createdAt(now)
                .updatedAt(now)
                .build(),
            PermissionDTO.builder()
                .id(2L)
                .name("permission:write")
                .title("Write permissions")
                .description("Allows writing permissions")
                .createdAt(now)
                .updatedAt(now)
                .build(),
            PermissionDTO.builder()
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
        List.of(
            RoleDTO.builder()
                .id(1L)
                .name("Admin")
                .title("Administrator")
                .description("Full access to the system")
                .permissions(permissionDTOs)
                .createdAt(now)
                .updatedAt(now)
                .build(),
            RoleDTO.builder()
                .id(2L)
                .name("User")
                .title("Regular User")
                .description("Limited access to the system")
                .permissions(List.of(permissionDTOs.get(0), permissionDTOs.get(2)))
                .createdAt(now)
                .updatedAt(now)
                .build(),
            RoleDTO.builder()
                .id(3L)
                .name("Guest")
                .title("Guest User")
                .description("Minimal access to the system")
                .permissions(List.of(permissionDTOs.get(0)))
                .createdAt(now)
                .updatedAt(now)
                .build());
  }

  @Test
  public void findAll_shouldReturnPageOfPermissionDTOs_whenKeywordMatches() {
    // Arrange
    String keyword = "e";
    Pageable pageable = PageRequest.of(0, 10);
    Page<Role> rolePage = new PageImpl<>(List.of(roles.get(1), roles.get(2)), pageable, 2);

    when(roleRepository.findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            eq(keyword), eq(keyword), eq(pageable)))
        .thenReturn(rolePage);
    when(modelMapper.map(roles.get(1), RoleDTO.class)).thenReturn(roleDTOs.get(1));
    when(modelMapper.map(roles.get(2), RoleDTO.class)).thenReturn(roleDTOs.get(2));

    // Act
    Page<RoleDTO> result = roleService.findAll(keyword, pageable);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.getTotalElements());
    assertEquals(2, result.getContent().size());
    assertTrue(result.getContent().contains(roleDTOs.get(1)));
    assertTrue(result.getContent().contains(roleDTOs.get(2)));
    assertNull(result.getContent().get(0).getPermissions());
    assertNull(result.getContent().get(1).getPermissions());

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
    assertNotNull(result);
    assertEquals(0, result.getTotalElements());
    assertEquals(0, result.getContent().size());

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
    when(modelMapper.map(roles.get(0), RoleDTO.class)).thenReturn(roleDTOs.get(0));
    when(modelMapper.map(roles.get(1), RoleDTO.class)).thenReturn(roleDTOs.get(1));
    when(modelMapper.map(roles.get(2), RoleDTO.class)).thenReturn(roleDTOs.get(2));

    // Act
    Page<RoleDTO> result = roleService.findAll(keyword, pageable);

    // Assert
    assertNotNull(result);
    assertEquals(roles.size(), result.getTotalElements());
    assertEquals(roles.size(), result.getContent().size());
    assertTrue(result.getContent().contains(roleDTOs.get(0)));
    assertTrue(result.getContent().contains(roleDTOs.get(1)));
    assertTrue(result.getContent().contains(roleDTOs.get(2)));
    assertNull(result.getContent().get(0).getPermissions());
    assertNull(result.getContent().get(1).getPermissions());
    assertNull(result.getContent().get(2).getPermissions());

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
    RoleDTO roleDTO = roleDTOs.get(0);

    when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
    when(modelMapper.map(role, RoleDTO.class)).thenReturn(roleDTO);

    // Act
    Optional<RoleDTO> result = roleService.findById(roleId);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(roleId, result.get().getId());
    assertEquals(role.getName(), result.get().getName());
    assertEquals(role.getTitle(), result.get().getTitle());
    assertEquals(role.getDescription(), result.get().getDescription());
    assertEquals(role.getCreatedAt(), result.get().getCreatedAt());
    assertEquals(role.getUpdatedAt(), result.get().getUpdatedAt());
    assertNotNull(result.get().getPermissions());

    verify(roleRepository).findById(roleId);
    verify(modelMapper).map(role, RoleDTO.class);
  }

  @Test
  public void findById_shouldReturnEmptyOptional_whenNotFound() {
    // Arrange
    Long roleId = 999L;

    when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

    // Act
    Optional<RoleDTO> result = roleService.findById(roleId);

    // Assert
    assertTrue(result.isEmpty());

    verify(roleRepository).findById(roleId);
    verifyNoInteractions(modelMapper);
  }
}
