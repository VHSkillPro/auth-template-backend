package com.vhskillpro.backend.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vhskillpro.backend.common.response.DataApiResponse;
import com.vhskillpro.backend.common.response.PagedApiResponse;
import com.vhskillpro.backend.exception.AppException;
import com.vhskillpro.backend.modules.permission.PermissionMessages;
import com.vhskillpro.backend.modules.permission.PermissionService;
import com.vhskillpro.backend.modules.permission.PermissionV1Controller;
import com.vhskillpro.backend.modules.permission.dto.PermissionDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionV1Controller Tests")
class PermissionV1ControllerTests {

  @Mock private PermissionService permissionService;

  @InjectMocks private PermissionV1Controller permissionV1Controller;

  private Instant now;
  private List<PermissionDTO> permissionDTOs;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    now = Instant.now();
    pageable = PageRequest.of(0, 10);

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

  @Nested
  @DisplayName("index() method tests")
  class IndexTests {

    @Test
    @DisplayName("Should return paginated permissions when keyword is provided")
    void shouldReturnPaginatedPermissions_whenKeywordIsProvided() {
      // Arrange
      String keyword = "read";
      Page<PermissionDTO> permissionPage =
          new PageImpl<>(List.of(permissionDTOs.get(0), permissionDTOs.get(2)), pageable, 2);

      when(permissionService.findAll(eq(keyword), eq(pageable))).thenReturn(permissionPage);

      // Act
      PagedApiResponse<PermissionDTO> response = permissionV1Controller.index(keyword, pageable);

      // Assert
      assertThat(response).isNotNull();
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
      assertThat(response.getMessage())
          .isEqualTo(PermissionMessages.PERMISSION_INDEX_SUCCESS.getMessage());
      assertThat(response.getData()).hasSize(2);
      assertThat(response.getData())
          .containsExactlyInAnyOrder(permissionDTOs.get(0), permissionDTOs.get(2));
      assertThat(response.getMeta()).isNotNull();
      assertThat(response.getMeta().getPage()).isEqualTo(0);
      assertThat(response.getMeta().getSize()).isEqualTo(10);
      assertThat(response.getMeta().getTotal()).isEqualTo(2);
      assertThat(response.getMeta().getPages()).isEqualTo(1);

      verify(permissionService, times(1)).findAll(eq(keyword), eq(pageable));
    }

    @Test
    @DisplayName("Should return paginated permissions when keyword is empty")
    void shouldReturnPaginatedPermissions_whenKeywordIsEmpty() {
      // Arrange
      String keyword = "";
      Page<PermissionDTO> permissionPage =
          new PageImpl<>(permissionDTOs, pageable, permissionDTOs.size());

      when(permissionService.findAll(eq(keyword), eq(pageable))).thenReturn(permissionPage);

      // Act
      PagedApiResponse<PermissionDTO> response = permissionV1Controller.index(keyword, pageable);

      // Assert
      assertThat(response).isNotNull();
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
      assertThat(response.getMessage())
          .isEqualTo(PermissionMessages.PERMISSION_INDEX_SUCCESS.getMessage());
      assertThat(response.getData()).hasSize(3);
      assertThat(response.getData()).containsExactlyInAnyOrderElementsOf(permissionDTOs);
      assertThat(response.getMeta()).isNotNull();
      assertThat(response.getMeta().getTotal()).isEqualTo(3);

      verify(permissionService, times(1)).findAll(eq(keyword), eq(pageable));
    }

    @Test
    @DisplayName("Should return empty page when no permissions match keyword")
    void shouldReturnEmptyPage_whenNoPermissionsMatchKeyword() {
      // Arrange
      String keyword = "notfound";
      Page<PermissionDTO> emptyPage = Page.empty(pageable);

      when(permissionService.findAll(eq(keyword), eq(pageable))).thenReturn(emptyPage);

      // Act
      PagedApiResponse<PermissionDTO> response = permissionV1Controller.index(keyword, pageable);

      // Assert
      assertThat(response).isNotNull();
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
      assertThat(response.getMessage())
          .isEqualTo(PermissionMessages.PERMISSION_INDEX_SUCCESS.getMessage());
      assertThat(response.getData()).isEmpty();
      assertThat(response.getMeta()).isNotNull();
      assertThat(response.getMeta().getTotal()).isEqualTo(0);

      verify(permissionService, times(1)).findAll(eq(keyword), eq(pageable));
    }

    @Test
    @DisplayName("Should handle pagination correctly")
    void shouldHandlePaginationCorrectly() {
      // Arrange
      String keyword = "";
      Pageable customPageable = PageRequest.of(1, 2); // Second page, 2 items per page
      Page<PermissionDTO> permissionPage =
          new PageImpl<>(List.of(permissionDTOs.get(2)), customPageable, 3);

      when(permissionService.findAll(eq(keyword), eq(customPageable))).thenReturn(permissionPage);

      // Act
      PagedApiResponse<PermissionDTO> response =
          permissionV1Controller.index(keyword, customPageable);

      // Assert
      assertThat(response).isNotNull();
      assertThat(response.getMeta().getPage()).isEqualTo(1);
      assertThat(response.getMeta().getSize()).isEqualTo(2);
      assertThat(response.getMeta().getTotal()).isEqualTo(3);
      assertThat(response.getMeta().getPages()).isEqualTo(2);

      verify(permissionService, times(1)).findAll(eq(keyword), eq(customPageable));
    }
  }

  @Nested
  @DisplayName("show() method tests")
  class ShowTests {

    @Test
    @DisplayName("Should return permission when found by ID")
    void shouldReturnPermission_whenFoundById() {
      // Arrange
      Long permissionId = 1L;
      PermissionDTO expectedPermission = permissionDTOs.get(0);

      when(permissionService.findById(permissionId)).thenReturn(Optional.of(expectedPermission));

      // Act
      DataApiResponse<PermissionDTO> response = permissionV1Controller.show(permissionId);

      // Assert
      assertThat(response).isNotNull();
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
      assertThat(response.getMessage())
          .isEqualTo(PermissionMessages.PERMISSION_SHOW_SUCCESS.getMessage());
      assertThat(response.getData()).isEqualTo(expectedPermission);

      verify(permissionService, times(1)).findById(permissionId);
    }

    @Test
    @DisplayName("Should throw AppException when permission not found")
    void shouldThrowAppException_whenPermissionNotFound() {
      // Arrange
      Long permissionId = 999L;

      when(permissionService.findById(permissionId)).thenReturn(Optional.empty());

      // Act & Assert
      assertThatThrownBy(() -> permissionV1Controller.show(permissionId))
          .isInstanceOf(AppException.class)
          .satisfies(
              exception -> {
                AppException appException = (AppException) exception;
                assertThat(appException.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                assertThat(appException.getMessage())
                    .isEqualTo(PermissionMessages.PERMISSION_NOT_FOUND.getMessage());
              });

      verify(permissionService, times(1)).findById(permissionId);
    }

    @Test
    @DisplayName("Should handle null ID parameter")
    void shouldHandleNullIdParameter() {
      // Arrange
      when(permissionService.findById(null)).thenReturn(Optional.empty());

      // Act & Assert
      assertThatThrownBy(() -> permissionV1Controller.show(null))
          .isInstanceOf(AppException.class)
          .satisfies(
              exception -> {
                AppException appException = (AppException) exception;
                assertThat(appException.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                assertThat(appException.getMessage())
                    .isEqualTo(PermissionMessages.PERMISSION_NOT_FOUND.getMessage());
              });

      verify(permissionService, times(1)).findById(null);
    }

    @Test
    @DisplayName("Should return correct permission for different IDs")
    void shouldReturnCorrectPermission_forDifferentIds() {
      // Arrange
      Long permissionId1 = 1L;
      Long permissionId2 = 2L;
      PermissionDTO permission1 = permissionDTOs.get(0);
      PermissionDTO permission2 = permissionDTOs.get(1);

      when(permissionService.findById(permissionId1)).thenReturn(Optional.of(permission1));
      when(permissionService.findById(permissionId2)).thenReturn(Optional.of(permission2));

      // Act
      DataApiResponse<PermissionDTO> response1 = permissionV1Controller.show(permissionId1);
      DataApiResponse<PermissionDTO> response2 = permissionV1Controller.show(permissionId2);

      // Assert
      assertThat(response1.getData()).isEqualTo(permission1);
      assertThat(response2.getData()).isEqualTo(permission2);

      verify(permissionService, times(1)).findById(permissionId1);
      verify(permissionService, times(1)).findById(permissionId2);
    }
  }

  @Nested
  @DisplayName("Edge cases and error handling")
  class EdgeCasesAndErrorHandling {

    @Test
    @DisplayName("Should handle service throwing unexpected exception")
    void shouldHandleServiceThrowingUnexpectedException() {
      // Arrange
      String keyword = "test";
      RuntimeException unexpectedException = new RuntimeException("Database connection failed");

      when(permissionService.findAll(eq(keyword), any(Pageable.class)))
          .thenThrow(unexpectedException);

      // Act & Assert
      assertThatThrownBy(() -> permissionV1Controller.index(keyword, pageable))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Database connection failed");

      verify(permissionService, times(1)).findAll(eq(keyword), any(Pageable.class));
    }

    @Test
    @DisplayName("Should handle service throwing exception in show method")
    void shouldHandleServiceThrowingExceptionInShowMethod() {
      // Arrange
      Long permissionId = 1L;
      RuntimeException unexpectedException = new RuntimeException("Database connection failed");

      when(permissionService.findById(permissionId)).thenThrow(unexpectedException);

      // Act & Assert
      assertThatThrownBy(() -> permissionV1Controller.show(permissionId))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Database connection failed");

      verify(permissionService, times(1)).findById(permissionId);
    }

    @Test
    @DisplayName("Should handle very long keyword")
    void shouldHandleVeryLongKeyword() {
      // Arrange
      String longKeyword = "a".repeat(1000);
      Page<PermissionDTO> emptyPage = Page.empty(pageable);

      when(permissionService.findAll(eq(longKeyword), eq(pageable))).thenReturn(emptyPage);

      // Act
      PagedApiResponse<PermissionDTO> response =
          permissionV1Controller.index(longKeyword, pageable);

      // Assert
      assertThat(response).isNotNull();
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getData()).isEmpty();

      verify(permissionService, times(1)).findAll(eq(longKeyword), eq(pageable));
    }

    @Test
    @DisplayName("Should handle special characters in keyword")
    void shouldHandleSpecialCharactersInKeyword() {
      // Arrange
      String specialKeyword = "!@#$%^&*()";
      Page<PermissionDTO> emptyPage = Page.empty(pageable);

      when(permissionService.findAll(eq(specialKeyword), eq(pageable))).thenReturn(emptyPage);

      // Act
      PagedApiResponse<PermissionDTO> response =
          permissionV1Controller.index(specialKeyword, pageable);

      // Assert
      assertThat(response).isNotNull();
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getData()).isEmpty();

      verify(permissionService, times(1)).findAll(eq(specialKeyword), eq(pageable));
    }
  }

  @Nested
  @DisplayName("Response structure validation")
  class ResponseStructureValidation {

    @Test
    @DisplayName("Should return correct response structure for index method")
    void shouldReturnCorrectResponseStructureForIndexMethod() {
      // Arrange
      String keyword = "";
      Page<PermissionDTO> permissionPage =
          new PageImpl<>(permissionDTOs, pageable, permissionDTOs.size());

      when(permissionService.findAll(eq(keyword), eq(pageable))).thenReturn(permissionPage);

      // Act
      PagedApiResponse<PermissionDTO> response = permissionV1Controller.index(keyword, pageable);

      // Assert
      assertThat(response).isInstanceOf(PagedApiResponse.class);
      assertThat(response.getData()).isInstanceOf(List.class);
      assertThat(response.getMeta()).isNotNull();
      assertThat(response.getMeta().getPage()).isGreaterThanOrEqualTo(0);
      assertThat(response.getMeta().getSize()).isGreaterThan(0);
      assertThat(response.getMeta().getTotal()).isGreaterThanOrEqualTo(0);
      assertThat(response.getMeta().getPages()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Should return correct response structure for show method")
    void shouldReturnCorrectResponseStructureForShowMethod() {
      // Arrange
      Long permissionId = 1L;
      PermissionDTO expectedPermission = permissionDTOs.get(0);

      when(permissionService.findById(permissionId)).thenReturn(Optional.of(expectedPermission));

      // Act
      DataApiResponse<PermissionDTO> response = permissionV1Controller.show(permissionId);

      // Assert
      assertThat(response).isInstanceOf(DataApiResponse.class);
      assertThat(response.getData()).isInstanceOf(PermissionDTO.class);
      assertThat(response.getData().getId()).isNotNull();
      assertThat(response.getData().getName()).isNotNull();
      assertThat(response.getData().getTitle()).isNotNull();
      assertThat(response.getData().getDescription()).isNotNull();
    }
  }
}
