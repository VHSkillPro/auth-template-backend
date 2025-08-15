package com.vhskillpro.backend.modules.user;

import com.vhskillpro.backend.common.response.PagedApiResponse;
import com.vhskillpro.backend.modules.user.dto.UserDTO;
import com.vhskillpro.backend.modules.user.dto.UserFilterDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
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
      })
  @GetMapping
  public PagedApiResponse<UserDTO> index(
      @Parameter(hidden = true) UserFilterDTO filter, @Parameter(hidden = true) Pageable pageable) {
    System.out.println(filter);
    Page<UserDTO> userDTOs = userService.findAll(filter, pageable);
    return PagedApiResponse.success(userDTOs, UserMessages.USER_INDEX_SUCCESS.getMessage());
  }
}
