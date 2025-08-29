package com.vhskillpro.backend.modules.permission;

import com.vhskillpro.backend.common.response.ApiResponse;
import com.vhskillpro.backend.common.response.DataApiResponse;
import com.vhskillpro.backend.common.response.PagedApiResponse;
import com.vhskillpro.backend.common.swagger.ForbiddenApiResponse;
import com.vhskillpro.backend.common.swagger.UnauthorizedApiResponse;
import com.vhskillpro.backend.exception.AppException;
import com.vhskillpro.backend.modules.permission.dto.PermissionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/permission")
@Tag(name = "Permission (V1)", description = "APIs for managing permissions")
public class PermissionV1Controller {
  private PermissionService permissionService;

  public PermissionV1Controller(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  /**
   * Retrieves a paginated list of permissions, optionally filtered by a keyword.
   *
   * @param keyword the search keyword to filter permissions (optional, defaults to empty string)
   * @param pageable the pagination and sorting information
   * @return an {@link PagedApiResponse} containing a list of {@link PermissionDTO} objects and a
   *     success message
   */
  @Operation(
      summary = "Get list permissions",
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
            description = "PERMISSION_INDEX_SUCCESS",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PagedApiResponsePermissionDTO.class))),
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping
  public PagedApiResponse<PermissionDTO> index(
      @RequestParam(defaultValue = "") String keyword,
      @Parameter(hidden = true) Pageable pageable) {
    Page<PermissionDTO> permissions = permissionService.findAll(keyword, pageable);
    return PagedApiResponse.success(
        permissions, PermissionMessages.PERMISSION_INDEX_SUCCESS.toString());
  }

  /**
   * Retrieves the details of a specific permission by its ID.
   *
   * @param id the ID of the permission to retrieve
   * @return an {@link DataApiResponse} containing the {@link PermissionDTO} and a success message
   * @throws AppException if the permission with the specified ID is not found
   */
  @Operation(
      summary = "Get permission detail by ID",
      parameters = {@Parameter(name = "id", description = "Permission ID")},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "PERMISSION_SHOW_SUCCESS",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DataApiResponsePermissionDTO.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "PERMISSION_NOT_FOUND",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
      })
  @UnauthorizedApiResponse
  @ForbiddenApiResponse
  @GetMapping("/{id}")
  public DataApiResponse<PermissionDTO> show(@PathVariable Long id) {
    PermissionDTO permissionDTO =
        permissionService
            .findById(id)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, PermissionMessages.PERMISSION_NOT_FOUND.toString()));
    return DataApiResponse.success(
        permissionDTO, PermissionMessages.PERMISSION_SHOW_SUCCESS.toString());
  }

  private class PagedApiResponsePermissionDTO extends PagedApiResponse<PermissionDTO> {}

  private class DataApiResponsePermissionDTO extends DataApiResponse<PermissionDTO> {}
}
