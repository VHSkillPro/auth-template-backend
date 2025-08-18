package com.vhskillpro.backend.user;

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
import com.vhskillpro.backend.modules.user.UserMessages;
import com.vhskillpro.backend.modules.user.UserService;
import com.vhskillpro.backend.modules.user.UserV1Controller;
import com.vhskillpro.backend.modules.user.dto.UserCreateDTO;
import com.vhskillpro.backend.modules.user.dto.UserDTO;
import com.vhskillpro.backend.modules.user.dto.UserFilterDTO;
import com.vhskillpro.backend.modules.user.dto.UserUpdateDTO;
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
@DisplayName("UserV1Controller Tests")
class UserV1ControllerTests {

  @Mock private UserService userService;

  @InjectMocks private UserV1Controller controller;

  private Pageable pageable;
  private List<UserDTO> users;

  @BeforeEach
  void setUp() {
    pageable = PageRequest.of(0, 10);
    users =
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
  @DisplayName("index() tests")
  class IndexTests {
    @Test
    @DisplayName("Should return paginated users with meta and message")
    void shouldReturnPaginatedUsers_withMetaAndMessage() {
      UserFilterDTO filter = UserFilterDTO.builder().keyword("john").build();
      Page<UserDTO> page = new PageImpl<>(List.of(users.get(0)), pageable, 1);

      when(userService.findAll(eq(filter), eq(pageable))).thenReturn(page);

      PagedApiResponse<UserDTO> response = controller.index(filter, pageable);

      assertThat(response).isNotNull();
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
      assertThat(response.getMessage()).isEqualTo(UserMessages.USER_INDEX_SUCCESS.getMessage());
      assertThat(response.getData()).containsExactly(users.get(0));
      assertThat(response.getMeta()).isNotNull();
      assertThat(response.getMeta().getTotal()).isEqualTo(1);

      verify(userService, times(1)).findAll(eq(filter), eq(pageable));
    }

    @Test
    @DisplayName("Should bubble up unexpected error from service")
    void shouldBubbleUpUnexpectedErrorFromService() {
      UserFilterDTO filter = UserFilterDTO.builder().keyword("err").build();
      RuntimeException ex = new RuntimeException("boom");
      when(userService.findAll(eq(filter), any(Pageable.class))).thenThrow(ex);

      assertThatThrownBy(() -> controller.index(filter, pageable))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("boom");
    }
  }

  @Nested
  @DisplayName("show() tests")
  class ShowTests {
    @Test
    @DisplayName("Should return user when found")
    void shouldReturnUser_whenFound() {
      Long id = 1L;
      UserDTO dto = users.get(0);
      when(userService.findById(id)).thenReturn(Optional.of(dto));

      DataApiResponse<UserDTO> response = controller.show(id);

      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
      assertThat(response.getMessage()).isEqualTo(UserMessages.USER_SHOW_SUCCESS.getMessage());
      assertThat(response.getData()).isEqualTo(dto);
      verify(userService).findById(id);
    }

    @Test
    @DisplayName("Should throw AppException when not found")
    void shouldThrowAppException_whenNotFound() {
      Long id = 99L;
      when(userService.findById(id)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> controller.show(id))
          .isInstanceOf(AppException.class)
          .satisfies(
              e -> {
                AppException ex = (AppException) e;
                assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                assertThat(ex.getMessage()).isEqualTo(UserMessages.USER_NOT_FOUND.getMessage());
              });
    }
  }

  @Nested
  @DisplayName("create() tests")
  class CreateTests {
    @Test
    @DisplayName("Should delegate to service and return 200 created message")
    void shouldDelegateAndReturnCreated() {
      UserCreateDTO input =
          UserCreateDTO.builder()
              .email("new@example.com")
              .password("Password1!")
              .firstName("New")
              .lastName("User")
              .build();

      when(userService.create(eq(input))).thenReturn(UserDTO.builder().id(10L).build());

      ApiResponse<Void> response = controller.create(input);
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
      assertThat(response.getMessage()).isEqualTo(UserMessages.USER_CREATE_SUCCESS.getMessage());
      verify(userService).create(eq(input));
    }
  }

  @Nested
  @DisplayName("update() tests")
  class UpdateTests {
    @Test
    @DisplayName("Should delegate to service and return success message")
    void shouldDelegateAndReturnSuccess() {
      Long id = 5L;
      UserUpdateDTO upd =
          UserUpdateDTO.builder().firstName("A").lastName("B").locked(true).roleId(1L).build();
      when(userService.update(eq(id), eq(upd))).thenReturn(UserDTO.builder().id(id).build());

      ApiResponse<Void> response = controller.update(id, upd);
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
      assertThat(response.getMessage()).isEqualTo(UserMessages.USER_UPDATE_SUCCESS.getMessage());
      verify(userService).update(eq(id), eq(upd));
    }
  }

  @Nested
  @DisplayName("delete() tests")
  class DeleteTests {
    @Test
    @DisplayName("Should delegate delete and return success message")
    void shouldDelegateDelete_andReturnSuccess() {
      Long id = 7L;
      ApiResponse<Void> response = controller.delete(id);
      assertThat(response.isSuccess()).isTrue();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
      assertThat(response.getMessage()).isEqualTo(UserMessages.USER_DELETE_SUCCESS.getMessage());
      verify(userService).delete(eq(id));
    }
  }
}
