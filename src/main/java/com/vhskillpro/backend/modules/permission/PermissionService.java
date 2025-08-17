package com.vhskillpro.backend.modules.permission;

import com.vhskillpro.backend.modules.permission.dto.PermissionDTO;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {
  private PermissionRepository permissionRepository;
  private ModelMapper modelMapper;

  public PermissionService(PermissionRepository permissionRepository, ModelMapper modelMapper) {
    this.permissionRepository = permissionRepository;
    this.modelMapper = modelMapper;
  }

  /**
   * Retrieves a paginated list of PermissionDTO objects whose name and title contain the specified
   * keyword (case-insensitive).
   *
   * @param keyword the keyword to search for in the name and title fields of permissions
   * @param pageable the pagination and sorting information
   * @return a page of PermissionDTO objects matching the search criteria
   */
  @PreAuthorize("hasAnyAuthority('all:all', 'permission:read')")
  public Page<PermissionDTO> findAll(String keyword, Pageable pageable) {
    Page<Permission> permissions =
        permissionRepository.findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            keyword, keyword, pageable);
    return permissions.map(permission -> modelMapper.map(permission, PermissionDTO.class));
  }

  /**
   * Retrieves a permission by id and maps it to a PermissionDTO.
   *
   * @param id the unique identifier of the permission to retrieve
   * @return an {@link Optional} containing the mapped {@link PermissionDTO} if found, or an empty
   *     {@link Optional} if not found
   */
  @PreAuthorize("hasAnyAuthority('all:all', 'permission:read')")
  public Optional<PermissionDTO> findById(Long id) {
    return permissionRepository
        .findById(id)
        .map(permission -> modelMapper.map(permission, PermissionDTO.class));
  }
}
