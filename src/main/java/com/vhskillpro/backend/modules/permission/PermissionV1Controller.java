package com.vhskillpro.backend.modules.permission;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vhskillpro.backend.common.response.ApiResponse;
import com.vhskillpro.backend.exception.AppException;
import com.vhskillpro.backend.modules.permission.dto.PermissionDTO;

@RestController
@RequestMapping("/api/v1/permission")
public class PermissionV1Controller {
  private PermissionService permissionService;

  public PermissionV1Controller(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  /**
   * Retrieves a paginated list of permissions, optionally filtered by a keyword.
   *
   * @param keyword  the search keyword to filter permissions (optional, defaults
   *                 to empty string)
   * @param pageable the pagination and sorting information
   * @return an {@link ApiResponse} containing a list of {@link PermissionDTO}
   *         objects and a success message
   */
  @GetMapping
  public ApiResponse<List<PermissionDTO>> index(
      @RequestParam(defaultValue = "") String keyword, Pageable pageable) {
    Page<PermissionDTO> permissions = permissionService.findAll(keyword, pageable);
    return ApiResponse.success(permissions, PermissionMessages.PERMISSION_INDEX_SUCCESS.getMessage());
  }

  /**
   * Retrieves the details of a specific permission by its ID.
   *
   * @param id the ID of the permission to retrieve
   * @return an {@link ApiResponse} containing the {@link PermissionDTO} and a
   *         success message
   * @throws AppException if the permission with the specified ID is not found
   */
  @GetMapping("/{id}")
  public ApiResponse<PermissionDTO> show(@PathVariable Long id) {
    PermissionDTO permissionDTO = permissionService.findById(id)
        .orElseThrow(
            () -> new AppException(HttpStatus.NOT_FOUND, PermissionMessages.PERMISSION_NOT_FOUND.getMessage()));
    return ApiResponse.success(permissionDTO, PermissionMessages.PERMISSION_SHOW_SUCCESS.getMessage());
  }
}
