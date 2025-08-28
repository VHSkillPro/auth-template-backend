package com.vhskillpro.backend.modules.role;

import com.vhskillpro.backend.common.response.ApiResponse;
import com.vhskillpro.backend.common.response.DataApiResponse;
import com.vhskillpro.backend.common.response.PagedApiResponse;
import com.vhskillpro.backend.common.swagger.BadRequestDataApiResponse;
import com.vhskillpro.backend.exception.AppException;
import com.vhskillpro.backend.modules.role.dto.RoleCreateDTO;
import com.vhskillpro.backend.modules.role.dto.RoleDTO;
import com.vhskillpro.backend.modules.role.dto.RoleDetailDTO;
import com.vhskillpro.backend.modules.role.dto.RoleUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import javax.management.relation.RoleNotFoundException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
   * @param keyword Optional search keyword to filter roles.
   * @param pageable Pagination and sorting information.
   * @return A paginated API response containing RoleDTO objects and a success message.
   */
  @Operation(
      summary = "Get list roles",
      parameters = {
        @Parameter(name = "keyword", description = "Search keyword to filter"),
        @Parameter(name = "page", description = "Page number (0-based index)"),
        @Parameter(name = "size", description = "Number of items per page"),
        @Parameter(
            name = "sort",
            description = "Sorting criteria in the format: property(,asc|desc)")
      },
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "ROLE_INDEX_SUCCESS",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedApiResponseRoleDTO.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "FORBIDDEN",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @GetMapping
  public PagedApiResponse<RoleDTO> index(
      @RequestParam(defaultValue = "") String keyword,
      @Parameter(hidden = true) Pageable pageable) {
    Page<RoleDTO> roleDTOs = roleService.findAll(keyword, pageable);
    return PagedApiResponse.success(roleDTOs, RoleMessages.ROLE_INDEX_SUCCESS.toString());
  }

  /**
   * Retrieves the details of a role by its ID.
   *
   * @param id the ID of the role to retrieve
   * @return a {@link DataApiResponse} containing the role details and a success message
   * @throws AppException if the role with the specified ID is not found
   */
  @Operation(
      summary = "Get role detail by ID",
      parameters = {@Parameter(name = "id", description = "Role ID")},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "ROLE_SHOW_SUCCESS",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponseRoleDetailDTO.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "FORBIDDEN",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "ROLE_NOT_FOUND",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @GetMapping("/{id}")
  public DataApiResponse<RoleDetailDTO> show(@PathVariable Long id) {
    RoleDetailDTO roleDTO =
        roleService
            .findById(id)
            .orElseThrow(
                () ->
                    new AppException(HttpStatus.NOT_FOUND, RoleMessages.ROLE_NOT_FOUND.toString()));
    return DataApiResponse.success(roleDTO, RoleMessages.ROLE_SHOW_SUCCESS.toString());
  }

  /**
   * Creates a new role with the provided details.
   *
   * @param roleCreateDTO the data transfer object containing role details to be created
   * @return an ApiResponse indicating the result of the creation operation
   * @throws javax.validation.ConstraintViolationException if the input data is invalid
   */
  @Operation(
      summary = "Create a new role",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "ROLE_CREATE_SUCCESS",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "BAD_REQUEST",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDataApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "FORBIDDEN",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<Void> create(@Valid @RequestBody RoleCreateDTO roleCreateDTO) {
    roleService.create(roleCreateDTO);
    return ApiResponse.created(RoleMessages.ROLE_CREATE_SUCCESS.toString());
  }

  /**
   * Updates an existing role with the specified ID using the provided update data.
   *
   * @param id the ID of the role to update
   * @param roleUpdateDTO the data transfer object containing updated role information
   * @return an {@link ApiResponse} indicating the success of the update operation
   */
  @Operation(
      summary = "Update role by ID",
      parameters = {@Parameter(name = "id", description = "Role ID")},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "ROLE_UPDATE_SUCCESS",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "BAD_REQUEST",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BadRequestDataApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "FORBIDDEN",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "ROLE_NOT_FOUND",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @PutMapping("/{id}")
  public ApiResponse<Void> update(
      @PathVariable Long id, @Valid @RequestBody RoleUpdateDTO roleUpdateDTO) {
    roleService.update(id, roleUpdateDTO);
    return ApiResponse.success(RoleMessages.ROLE_UPDATE_SUCCESS.toString());
  }

  /**
   * Deletes a specific role by its ID.
   *
   * @param id the ID of the role to delete
   * @return an {@link ApiResponse} indicating success or failure of the deletion
   * @throws RoleNotFoundException if the role with the specified ID does not exist
   */
  @Operation(
      summary = "Delete role by ID",
      parameters = {@Parameter(name = "id", description = "Role ID")},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "ROLE_DELETE_SUCCESS",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "FORBIDDEN",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "ROLE_NOT_FOUND",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "ROLE_DELETE_CONFLICT",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    roleService.delete(id);
    return ApiResponse.success(RoleMessages.ROLE_DELETE_SUCCESS.toString());
  }

  private class PagedApiResponseRoleDTO extends PagedApiResponse<RoleDTO> {}

  private class DataApiResponseRoleDetailDTO extends DataApiResponse<RoleDetailDTO> {}
}
