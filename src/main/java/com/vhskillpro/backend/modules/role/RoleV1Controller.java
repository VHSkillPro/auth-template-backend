package com.vhskillpro.backend.modules.role;

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
import com.vhskillpro.backend.modules.role.dto.RoleDTO;

import io.swagger.v3.oas.annotations.tags.Tag;

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
   * @param keyword  an optional search keyword to filter roles (default is empty
   *                 string)
   * @param pageable pagination information (page number, size, sorting)
   * @return an ApiResponse containing a page of RoleDTOs and a success message
   */
  @GetMapping
  public ApiResponse<List<RoleDTO>> index(
      @RequestParam(defaultValue = "") String keyword, Pageable pageable) {
    Page<RoleDTO> roleDTOs = roleService.findAll(keyword, pageable);
    return ApiResponse.success(roleDTOs, RoleMessages.ROLE_INDEX_SUCCESS.getMessage());
  }

  /**
   * Retrieves the details of a role by its unique identifier.
   *
   * @param id the unique identifier of the role to retrieve
   * @return an {@link ApiResponse} containing the {@link RoleDTO} and a success
   *         message
   * @throws AppException if the role with the specified id is not found
   */
  @GetMapping("/{id}")
  public ApiResponse<RoleDTO> show(@PathVariable Long id) {
    RoleDTO roleDTO = roleService.findById(id)
        .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, RoleMessages.ROLE_NOT_FOUND.getMessage()));
    return ApiResponse.success(roleDTO, RoleMessages.ROLE_SHOW_SUCCESS.getMessage());
  }
}
