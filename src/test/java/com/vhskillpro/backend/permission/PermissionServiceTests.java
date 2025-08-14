package com.vhskillpro.backend.permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.vhskillpro.backend.modules.permission.Permission;
import com.vhskillpro.backend.modules.permission.PermissionRepository;
import com.vhskillpro.backend.modules.permission.PermissionService;
import com.vhskillpro.backend.modules.permission.dto.PermissionDTO;
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
public class PermissionServiceTests {
  @Mock private PermissionRepository permissionRepository;

  @Mock private ModelMapper modelMapper;

  @InjectMocks private PermissionService permissionService;

  private Instant now;
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
  }

  @Test
  public void findAll_shouldReturnPageOfPermissionDTOs_whenKeywordMatches() {
    // Arrange
    String keyword = "read";
    Pageable pageable = PageRequest.of(0, 10);
    Page<Permission> permissionPage =
        new PageImpl<>(List.of(permissions.get(0), permissions.get(2)), pageable, 2);

    when(permissionRepository.findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            eq(keyword), eq(keyword), eq(pageable)))
        .thenReturn(permissionPage);
    when(modelMapper.map(permissions.get(0), PermissionDTO.class))
        .thenReturn(permissionDTOs.get(0));
    when(modelMapper.map(permissions.get(2), PermissionDTO.class))
        .thenReturn(permissionDTOs.get(2));

    // Act
    Page<PermissionDTO> result = permissionService.findAll(keyword, pageable);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.getTotalElements());
    assertEquals(2, result.getContent().size());
    assertTrue(result.getContent().contains(permissionDTOs.get(0)));
    assertTrue(result.getContent().contains(permissionDTOs.get(2)));

    verify(permissionRepository)
        .findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            eq(keyword), eq(keyword), eq(pageable));
    verify(modelMapper, times(2)).map(any(Permission.class), eq(PermissionDTO.class));
  }

  @Test
  public void findAll_shouldReturnEmptyPage_whenNoMatches() {
    // Arrange
    String keyword = "notfound";
    Pageable pageable = PageRequest.of(0, 10);
    Page<Permission> emptyPage = Page.empty(pageable);

    when(permissionRepository.findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            eq(keyword), eq(keyword), eq(pageable)))
        .thenReturn(emptyPage);

    // Act
    Page<PermissionDTO> result = permissionService.findAll(keyword, pageable);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.getTotalElements());
    verify(permissionRepository)
        .findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            eq(keyword), eq(keyword), eq(pageable));
    verifyNoInteractions(modelMapper);
  }

  @Test
  public void findAll_shouldReturnPage_whenKeywordIsEmpty() {
    // Arrange
    String keyword = "";
    Pageable pageable = PageRequest.of(0, 10);
    Page<Permission> permissionPage = new PageImpl<>(permissions, pageable, permissions.size());

    when(permissionRepository.findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            eq(keyword), eq(keyword), eq(pageable)))
        .thenReturn(permissionPage);
    for (int i = 0; i < permissions.size(); i++) {
      when(modelMapper.map(permissions.get(i), PermissionDTO.class))
          .thenReturn(permissionDTOs.get(i));
    }

    // Act
    Page<PermissionDTO> result = permissionService.findAll(keyword, pageable);

    // Assert
    assertNotNull(result);
    assertEquals(permissions.size(), result.getTotalElements());
    verify(permissionRepository)
        .findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            eq(keyword), eq(keyword), eq(pageable));
    verify(modelMapper, times(permissions.size()))
        .map(any(Permission.class), eq(PermissionDTO.class));
  }

  @Test
  public void findById_shouldReturnPermissionDTO_whenFound() {
    // Arrange
    int index = 0;
    Permission permission = permissions.get(index);
    Long permissionId = permission.getId();

    when(permissionRepository.findById(permissionId)).thenReturn(Optional.of(permission));
    when(modelMapper.map(permission, PermissionDTO.class)).thenReturn(permissionDTOs.get(index));

    // Act
    Optional<PermissionDTO> result = permissionService.findById(permissionId);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(permissionId, result.get().getId());
    assertEquals(permission.getName(), result.get().getName());
    assertEquals(permission.getTitle(), result.get().getTitle());
    assertEquals(permission.getDescription(), result.get().getDescription());
    assertEquals(permission.getCreatedAt(), result.get().getCreatedAt());
    assertEquals(permission.getUpdatedAt(), result.get().getUpdatedAt());

    verify(permissionRepository).findById(permissionId);
    verify(modelMapper).map(permission, PermissionDTO.class);
  }

  @Test
  public void findById_shouldReturnEmptyOptional_whenNotFound() {
    // Arrange
    Long id = 4L;
    when(permissionRepository.findById(id)).thenReturn(Optional.empty());

    // Act
    Optional<PermissionDTO> result = permissionService.findById(id);

    // Assert
    assertTrue(result.isEmpty());
    verify(permissionRepository).findById(id);
    verifyNoInteractions(modelMapper);
  }
}
