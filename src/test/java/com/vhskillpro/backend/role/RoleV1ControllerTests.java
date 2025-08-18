package com.vhskillpro.backend.role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vhskillpro.backend.common.response.ApiResponse;
import com.vhskillpro.backend.common.response.DataApiResponse;
import com.vhskillpro.backend.common.response.PagedApiResponse;
import com.vhskillpro.backend.exception.AppException;
import com.vhskillpro.backend.modules.role.RoleMessages;
import com.vhskillpro.backend.modules.role.RoleService;
import com.vhskillpro.backend.modules.role.RoleV1Controller;
import com.vhskillpro.backend.modules.role.dto.RoleCreateDTO;
import com.vhskillpro.backend.modules.role.dto.RoleDTO;
import com.vhskillpro.backend.modules.role.dto.RoleDetailDTO;
import com.vhskillpro.backend.modules.role.dto.RoleUpdateDTO;
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
@DisplayName("RoleV1Controller Tests")
class RoleV1ControllerTests {

  @Mock private RoleService roleService;

  @InjectMocks private RoleV1Controller roleV1Controller;

  private Instant now;
  private List<RoleDTO> roleDTOs;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    now = Instant.now();
    pageable = PageRequest.of(0, 10);

    roleDTOs =
        List.of(
            RoleDTO.builder()
                .id(1L)
                .name("admin")
                .title("Administrator")
                .description("Manages all aspects of the system")
                .createdAt(now)
                .updatedAt(now)
                .build(),
            RoleDTO.builder()
                .id(2L)
                .name("editor")
                .title("Editor")
                .description("Can edit content")
                .createdAt(now)
                .updatedAt(now)
                .build(),
            RoleDTO.builder()
                .id(3L)
                .name("viewer")
                .title("Viewer")
                .description("Can view content")
                .createdAt(now)
                .updatedAt(now)
                .build());
  }

  @Nested
  @DisplayName("index() method tests")
  class IndexTests {

    @Test
    @DisplayName("Should return paginated roles when keyword is provided")
    void shouldReturnPaginatedRoles_whenKeywordIsProvided() {
      String keyword = "admin";
      Page<RoleDTO> rolePage = new PageImpl<>(List.of(roleDTOs.get(0)), pageable, 1);

      when(roleService.findAll(eq(keyword), eq(pageable))).thenReturn(rolePage);

      PagedApiResponse<RoleDTO> response = roleV1Controller.index(keyword, pageable);

      assertThat(response).isNotNull();
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
      assertThat(response.getMessage()).isEqualTo(RoleMessages.ROLE_INDEX_SUCCESS.getMessage());
      assertThat(response.getData()).hasSize(1);
      assertThat(response.getData()).containsExactly(roleDTOs.get(0));
      assertThat(response.getMeta()).isNotNull();
      assertThat(response.getMeta().getTotal()).isEqualTo(1);

      verify(roleService, times(1)).findAll(eq(keyword), eq(pageable));
    }

    @Test
    @DisplayName("Should return paginated roles when keyword is empty")
    void shouldReturnPaginatedRoles_whenKeywordIsEmpty() {
      String keyword = "";
      Page<RoleDTO> rolePage = new PageImpl<>(roleDTOs, pageable, roleDTOs.size());

      when(roleService.findAll(eq(keyword), eq(pageable))).thenReturn(rolePage);

      PagedApiResponse<RoleDTO> response = roleV1Controller.index(keyword, pageable);

      assertThat(response).isNotNull();
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
      assertThat(response.getMessage()).isEqualTo(RoleMessages.ROLE_INDEX_SUCCESS.getMessage());
      assertThat(response.getData()).hasSize(3);
      assertThat(response.getMeta()).isNotNull();
      assertThat(response.getMeta().getTotal()).isEqualTo(3);

      verify(roleService, times(1)).findAll(eq(keyword), eq(pageable));
    }

    @Test
    @DisplayName("Should return empty page when no roles match keyword")
    void shouldReturnEmptyPage_whenNoRolesMatchKeyword() {
      String keyword = "notfound";
      Page<RoleDTO> emptyPage = Page.empty(pageable);

      when(roleService.findAll(eq(keyword), eq(pageable))).thenReturn(emptyPage);

      PagedApiResponse<RoleDTO> response = roleV1Controller.index(keyword, pageable);

      assertThat(response).isNotNull();
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
      assertThat(response.getMessage()).isEqualTo(RoleMessages.ROLE_INDEX_SUCCESS.getMessage());
      assertThat(response.getData()).isEmpty();
      assertThat(response.getMeta()).isNotNull();
      assertThat(response.getMeta().getTotal()).isEqualTo(0);

      verify(roleService, times(1)).findAll(eq(keyword), eq(pageable));
    }

    @Test
    @DisplayName("Should handle pagination correctly")
    void shouldHandlePaginationCorrectly() {
      String keyword = "";
      Pageable customPageable = PageRequest.of(1, 2);
      Page<RoleDTO> rolePage = new PageImpl<>(List.of(roleDTOs.get(2)), customPageable, 3);

      when(roleService.findAll(eq(keyword), eq(customPageable))).thenReturn(rolePage);

      PagedApiResponse<RoleDTO> response = roleV1Controller.index(keyword, customPageable);

      assertThat(response).isNotNull();
      assertThat(response.getMeta().getPage()).isEqualTo(1);
      assertThat(response.getMeta().getSize()).isEqualTo(2);
      assertThat(response.getMeta().getTotal()).isEqualTo(3);
      assertThat(response.getMeta().getPages()).isEqualTo(2);

      verify(roleService, times(1)).findAll(eq(keyword), eq(customPageable));
    }
  }

  @Nested
  @DisplayName("show() method tests")
  class ShowTests {

    @Test
    @DisplayName("Should return role when found by ID")
    void shouldReturnRole_whenFoundById() {
      Long roleId = 1L;
      RoleDetailDTO expectedRole =
          RoleDetailDTO.builder()
              .id(1L)
              .name("admin")
              .title("Administrator")
              .description("Manages all aspects of the system")
              .permissions(List.of())
              .build();

      when(roleService.findById(roleId)).thenReturn(Optional.of(expectedRole));

      DataApiResponse<RoleDetailDTO> response = roleV1Controller.show(roleId);

      assertThat(response).isNotNull();
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
      assertThat(response.getMessage()).isEqualTo(RoleMessages.ROLE_SHOW_SUCCESS.getMessage());
      assertThat(response.getData()).isEqualTo(expectedRole);

      verify(roleService, times(1)).findById(roleId);
    }

    @Test
    @DisplayName("Should throw AppException when role not found")
    void shouldThrowAppException_whenRoleNotFound() {
      Long roleId = 999L;

      when(roleService.findById(roleId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> roleV1Controller.show(roleId))
          .isInstanceOf(AppException.class)
          .satisfies(
              exception -> {
                AppException appException = (AppException) exception;
                assertThat(appException.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                assertThat(appException.getMessage())
                    .isEqualTo(RoleMessages.ROLE_NOT_FOUND.getMessage());
              });

      verify(roleService, times(1)).findById(roleId);
    }
  }

  @Nested
  @DisplayName("create() method tests")
  class CreateTests {

    @Test
    @DisplayName("Should create role and return created response")
    void shouldCreateRole_andReturnCreatedResponse() {
      RoleCreateDTO createDTO =
          RoleCreateDTO.builder()
              .name("contributor")
              .title("Contributor")
              .description("Can contribute content")
              .permissionIds(List.of(1L, 2L))
              .build();

      when(roleService.create(eq(createDTO))).thenReturn(RoleDetailDTO.builder().id(10L).build());

      ApiResponse<Void> response = roleV1Controller.create(createDTO);

      assertThat(response).isNotNull();
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED.value());
      assertThat(response.getMessage()).isEqualTo(RoleMessages.ROLE_CREATE_SUCCESS.getMessage());

      verify(roleService, times(1)).create(eq(createDTO));
    }
  }

  @Nested
  @DisplayName("update() method tests")
  class UpdateTests {

    @Test
    @DisplayName("Should update role and return success response")
    void shouldUpdateRole_andReturnSuccessResponse() {
      Long roleId = 5L;
      RoleUpdateDTO updateDTO =
          RoleUpdateDTO.builder()
              .title("New Title")
              .description("New Description")
              .permissionIds(List.of(1L))
              .build();

      when(roleService.update(eq(roleId), eq(updateDTO)))
          .thenReturn(RoleDetailDTO.builder().id(roleId).build());

      ApiResponse<Void> response = roleV1Controller.update(roleId, updateDTO);

      assertThat(response).isNotNull();
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
      assertThat(response.getMessage()).isEqualTo(RoleMessages.ROLE_UPDATE_SUCCESS.getMessage());

      verify(roleService, times(1)).update(eq(roleId), eq(updateDTO));
    }
  }

  @Nested
  @DisplayName("delete() method tests")
  class DeleteTests {

    @Test
    @DisplayName("Should delete role and return success response")
    void shouldDeleteRole_andReturnSuccessResponse() {
      Long roleId = 7L;

      ApiResponse<Void> response = roleV1Controller.delete(roleId);

      assertThat(response).isNotNull();
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
      assertThat(response.getMessage()).isEqualTo(RoleMessages.ROLE_DELETE_SUCCESS.getMessage());

      verify(roleService, times(1)).delete(eq(roleId));
    }
  }

  @Nested
  @DisplayName("Edge cases and error handling")
  class EdgeCasesAndErrorHandling {

    @Test
    @DisplayName("Should handle service throwing unexpected exception in index")
    void shouldHandleServiceThrowingUnexpectedExceptionInIndex() {
      String keyword = "test";
      RuntimeException unexpectedException = new RuntimeException("Database connection failed");

      when(roleService.findAll(eq(keyword), any(Pageable.class))).thenThrow(unexpectedException);

      assertThatThrownBy(() -> roleV1Controller.index(keyword, pageable))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Database connection failed");

      verify(roleService, times(1)).findAll(eq(keyword), any(Pageable.class));
    }

    @Test
    @DisplayName("Should handle service throwing exception in show method")
    void shouldHandleServiceThrowingExceptionInShowMethod() {
      Long roleId = 1L;
      RuntimeException unexpectedException = new RuntimeException("Database connection failed");

      when(roleService.findById(roleId)).thenThrow(unexpectedException);

      assertThatThrownBy(() -> roleV1Controller.show(roleId))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Database connection failed");

      verify(roleService, times(1)).findById(roleId);
    }
  }

  @Nested
  @DisplayName("Response structure validation")
  class ResponseStructureValidation {

    @Test
    @DisplayName("Should return correct response structure for index method")
    void shouldReturnCorrectResponseStructureForIndexMethod() {
      String keyword = "";
      Page<RoleDTO> rolePage = new PageImpl<>(roleDTOs, pageable, roleDTOs.size());

      when(roleService.findAll(eq(keyword), eq(pageable))).thenReturn(rolePage);

      PagedApiResponse<RoleDTO> response = roleV1Controller.index(keyword, pageable);

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
      Long roleId = 1L;
      RoleDetailDTO expectedRole =
          RoleDetailDTO.builder()
              .id(1L)
              .name("admin")
              .title("Administrator")
              .description("Manages all aspects of the system")
              .permissions(List.of())
              .build();

      when(roleService.findById(roleId)).thenReturn(Optional.of(expectedRole));

      DataApiResponse<RoleDetailDTO> response = roleV1Controller.show(roleId);

      assertThat(response).isInstanceOf(DataApiResponse.class);
      assertThat(response.getData()).isInstanceOf(RoleDetailDTO.class);
      assertThat(response.getData().getId()).isNotNull();
      assertThat(response.getData().getName()).isNotNull();
      assertThat(response.getData().getTitle()).isNotNull();
      assertThat(response.getData().getDescription()).isNotNull();
    }
  }
}
