package com.vhskillpro.backend.modules.user;

import com.vhskillpro.backend.common.response.ApiResponse;
import com.vhskillpro.backend.common.response.DataApiResponse;
import com.vhskillpro.backend.common.response.PagedApiResponse;
import com.vhskillpro.backend.common.swagger.BadRequestApiResponse;
import com.vhskillpro.backend.common.swagger.ForbiddenApiResponse;
import com.vhskillpro.backend.common.swagger.UnauthorizedApiResponse;
import com.vhskillpro.backend.exception.AppException;
import com.vhskillpro.backend.modules.user.dto.UserCreateDTO;
import com.vhskillpro.backend.modules.user.dto.UserDTO;
import com.vhskillpro.backend.modules.user.dto.UserFilterDTO;
import com.vhskillpro.backend.modules.user.dto.UserUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User (V1)", description = "APIs for managing users")
public class UserV1Controller {
  private final UserService userService;

  public UserV1Controller(UserService userService) {
    this.userService = userService;
  }

  /**
   * Retrieves a paginated list of users based on the provided filter criteria.
   *
   * <p>This endpoint supports filtering by keyword, enabled status, locked status, superuser
   * status, and role name. Pagination and sorting can be controlled via the page, size, and sort
   * parameters.
   *
   * @param filter the filter criteria for user retrieval (keyword, enabled, locked, superuser,
   *     roleName)
   * @param pageable pagination and sorting information (page, size, sort)
   * @return a paginated response containing user data and a success message
   */
  @Operation(
      summary = "Get list users",
      parameters = {
        @Parameter(name = "keyword", description = "Search keyword for user retrieval"),
        @Parameter(name = "enabled", description = "Filter by enabled status"),
        @Parameter(name = "locked", description = "Filter by locked status"),
        @Parameter(name = "superuser", description = "Filter by superuser status"),
        @Parameter(name = "roleName", description = "Filter by role name"),
        @Parameter(name = "page", description = "Page number (0-based index)"),
        @Parameter(name = "size", description = "Number of items per page"),
        @Parameter(
            name = "sort",
            description = "Sorting criteria in the format: property(,asc|desc)")
      },
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "USER_INDEX_SUCCESS",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = PagedApiResponseUserDTO.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping
  public PagedApiResponse<UserDTO> index(
      @Parameter(hidden = true) UserFilterDTO filter, @Parameter(hidden = true) Pageable pageable) {
    Page<UserDTO> userDTOs = userService.findAll(filter, pageable);
    return PagedApiResponse.success(userDTOs, UserMessages.USER_INDEX_SUCCESS.toString());
  }

  /**
   * Retrieves a user by their unique identifier.
   *
   * @param id the unique identifier of the user to retrieve
   * @return a {@link DataApiResponse} containing the {@link UserDTO} if found
   * @throws AppException if the user with the specified id is not found
   */
  @Operation(
      summary = "Get user detail by ID",
      parameters = {@Parameter(name = "id", description = "User ID")},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "USER_SHOW_SUCCESS",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = DataApiResponseUserDTO.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "USER_NOT_FOUND",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping("/{id}")
  public DataApiResponse<UserDTO> show(@PathVariable Long id) {
    UserDTO userDTO =
        userService
            .findById(id)
            .orElseThrow(
                () ->
                    new AppException(HttpStatus.NOT_FOUND, UserMessages.USER_NOT_FOUND.toString()));
    return DataApiResponse.success(userDTO, UserMessages.USER_SHOW_SUCCESS.toString());
  }

  /**
   * Creates a new user with the provided details.
   *
   * @param userCreateDTO the data transfer object containing user creation information; must be
   *     valid
   * @return an ApiResponse indicating the success of the user creation operation
   */
  @Operation(
      summary = "Create a new user",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @io.swagger.v3.oas.annotations.media.Content(
                      mediaType = "application/json",
                      schema =
                          @io.swagger.v3.oas.annotations.media.Schema(
                              implementation = UserCreateDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "USER_CREATE_SUCCESS",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
      })
  @BadRequestApiResponse
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @PostMapping()
  public ApiResponse<Void> create(@Valid @RequestBody UserCreateDTO userCreateDTO) {
    userService.create(userCreateDTO);
    return ApiResponse.success(UserMessages.USER_CREATE_SUCCESS.toString());
  }

  /**
   * Updates the user information for the specified user ID.
   *
   * @param id the ID of the user to update
   * @param userUpdateDTO the data transfer object containing updated user information
   * @return an {@link ApiResponse} indicating the success of the update operation
   */
  @Operation(
      summary = "Update user by ID",
      parameters = {@Parameter(name = "id", description = "User ID")},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @io.swagger.v3.oas.annotations.media.Content(
                      mediaType = "application/json",
                      schema =
                          @io.swagger.v3.oas.annotations.media.Schema(
                              implementation = UserUpdateDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "USER_UPDATE_SUCCESS",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "USER_NOT_FOUND",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
      })
  @BadRequestApiResponse
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @PutMapping("/{id}")
  public ApiResponse<Void> update(
      @PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
    userService.update(id, userUpdateDTO);
    return ApiResponse.success(UserMessages.USER_UPDATE_SUCCESS.toString());
  }

  @Operation(
      summary = "Delete user by ID",
      parameters = {@Parameter(name = "id", description = "User ID")},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "USER_DELETE_SUCCESS",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "USER_NOT_FOUND",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return ApiResponse.success(UserMessages.USER_DELETE_SUCCESS.toString());
  }

  private class PagedApiResponseUserDTO extends PagedApiResponse<UserDTO> {}

  private class DataApiResponseUserDTO extends DataApiResponse<UserDTO> {}
}
