package com.vhskillpro.backend.modules.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vhskillpro.backend.common.response.ApiResponse;
import com.vhskillpro.backend.common.response.DataApiResponse;
import com.vhskillpro.backend.common.response.PagedApiResponse;
import com.vhskillpro.backend.common.swagger.BadRequestDataApiResponse;
import com.vhskillpro.backend.exception.AppException;
import com.vhskillpro.backend.modules.role.dto.RoleCreateDTO;
import com.vhskillpro.backend.modules.role.dto.RoleDTO;
import com.vhskillpro.backend.modules.role.dto.RoleDetailDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/role")
@Tag(name = "Role (V1)", description = "APIs for CRUD operations on roles")
public class RoleV1Controller {
  private RoleService roleService;

  public RoleV1Controller(RoleService roleService) {
    this.roleService = roleService;
  }

  /**
   * Handles HTTP GET requests to retrieve a paginated list of roles.
   *
   * @param keyword  Optional search keyword to filter roles.
   * @param pageable Pagination and sorting information.
   * @return A paginated API response containing RoleDTO objects and a success
   *         message.
   */
  @Operation(summary = "Get paginated roles list", description = "Fetches a list of roles with optional keyword-based search and pagination. "
      + "If no keyword is provided, all roles are returned.", parameters = {
          @Parameter(name = "keyword", description = "Search keyword to filter", example = "read"),
          @Parameter(name = "page", description = "Page number (0-based index)", example = "0"),
          @Parameter(name = "size", description = "Number of items per page", example = "10"),
          @Parameter(name = "sort", description = "Sorting criteria in the format: property(,asc|desc)", example = "name,asc")
      }, responses = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Roles list retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedApiResponseRoleDTO.class)))
      })
  @GetMapping
  public PagedApiResponse<RoleDTO> index(
      @RequestParam(defaultValue = "") String keyword, @Parameter(hidden = true) Pageable pageable) {
    Page<RoleDTO> roleDTOs = roleService.findAll(keyword, pageable);
    return PagedApiResponse.success(roleDTOs, RoleMessages.ROLE_INDEX_SUCCESS.getMessage());
  }

  /**
   * Retrieves the details of a role by its ID.
   *
   * @param id the ID of the role to retrieve
   * @return a {@link DataApiResponse} containing the role details and a success
   *         message
   * @throws AppException if the role with the specified ID is not found
   */
  @Operation(summary = "Get role by ID", description = "Fetches the details of a specific role by its ID.", parameters = {
      @Parameter(name = "id", description = "ID of the role to retrieve", example = "1")
  }, responses = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DataApiResponseRoleDetailDTO.class))),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
  })
  @GetMapping("/{id}")
  public DataApiResponse<RoleDetailDTO> show(@PathVariable Long id) {
    RoleDetailDTO roleDTO = roleService.findById(id)
        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, RoleMessages.ROLE_NOT_FOUND.getMessage()));
    return DataApiResponse.success(roleDTO, RoleMessages.ROLE_SHOW_SUCCESS.getMessage());
  }

  /**
   * Creates a new role with the provided details.
   *
   * @param roleCreateDTO the data transfer object containing role details to be
   *                      created
   * @return an ApiResponse indicating the result of the creation operation
   * @throws javax.validation.ConstraintViolationException if the input data is
   *                                                       invalid
   */
  @Operation(summary = "Create a new role", description = "Creates a new role with the provided details.", responses = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Role created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestDataApiResponse.class)))
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<Void> create(@Valid @RequestBody RoleCreateDTO roleCreateDTO) {
    roleService.create(roleCreateDTO);
    return ApiResponse.created(RoleMessages.ROLE_CREATE_SUCCESS.getMessage());
  }

  private class PagedApiResponseRoleDTO extends PagedApiResponse<RoleDTO> {
  }

  private class DataApiResponseRoleDetailDTO extends DataApiResponse<RoleDetailDTO> {
  }
}
