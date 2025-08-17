package com.vhskillpro.backend.modules.user;

import com.vhskillpro.backend.common.response.ApiResponse;
import com.vhskillpro.backend.common.response.DataApiResponse;
import com.vhskillpro.backend.common.response.PagedApiResponse;
import com.vhskillpro.backend.exception.AppException;
import com.vhskillpro.backend.modules.user.dto.UserCreateDTO;
import com.vhskillpro.backend.modules.user.dto.UserDTO;
import com.vhskillpro.backend.modules.user.dto.UserFilterDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User (V1)", description = "APIs for CRUD operations on users")
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
      summary = "Get a paginated list of users",
      description = "Retrieves a paginated list of users based on filter criteria.",
      parameters = {
        @Parameter(name = "keyword", description = "Search keyword for user retrieval"),
        @Parameter(
            name = "enabled",
            description = "Filter by enabled status",
            example = "true|false"),
        @Parameter(
            name = "locked",
            description = "Filter by locked status",
            example = "true|false"),
        @Parameter(
            name = "superuser",
            description = "Filter by superuser status",
            example = "true|false"),
        @Parameter(name = "roleName", description = "Filter by role name", example = "admin"),
        @Parameter(name = "page", description = "Page number (0-based index)", example = "0"),
        @Parameter(name = "size", description = "Number of items per page", example = "10"),
        @Parameter(
            name = "sort",
            description = "Sorting criteria in the format: property(,asc|desc)",
            example = "name,asc")
      },
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = PagedApiResponseUserDTO.class))),
      })
  @GetMapping
  public PagedApiResponse<UserDTO> index(
      @Parameter(hidden = true) UserFilterDTO filter, @Parameter(hidden = true) Pageable pageable) {
    System.out.println(filter);
    Page<UserDTO> userDTOs = userService.findAll(filter, pageable);
    return PagedApiResponse.success(userDTOs, UserMessages.USER_INDEX_SUCCESS.getMessage());
  }

  /**
   * Retrieves a user by their unique identifier.
   *
   * @param id the unique identifier of the user to retrieve
   * @return a {@link DataApiResponse} containing the {@link UserDTO} if found
   * @throws AppException if the user with the specified id is not found
   */
  @Operation(
      summary = "Get user by ID",
      description = "Retrieves a user by their unique identifier.",
      parameters = {@Parameter(name = "id", description = "Unique identifier of the user")},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User retrieved successfully",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = DataApiResponseUserDTO.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
      })
  @GetMapping("/{id}")
  public DataApiResponse<UserDTO> show(@PathVariable Long id) {
    UserDTO userDTO =
        userService
            .findById(id)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, UserMessages.USER_NOT_FOUND.getMessage()));
    return DataApiResponse.success(userDTO, UserMessages.USER_SHOW_SUCCESS.getMessage());
  }

  @PostMapping()
  public ApiResponse<Void> create(@Valid @RequestBody UserCreateDTO userCreateDTO) {
    userService.create(userCreateDTO);
    return ApiResponse.success(UserMessages.USER_CREATE_SUCCESS.getMessage());
  }

  private class PagedApiResponseUserDTO extends PagedApiResponse<UserDTO> {}

  private class DataApiResponseUserDTO extends DataApiResponse<UserDTO> {}
}
