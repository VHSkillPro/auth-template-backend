package com.vhskillpro.backend.modules.permission;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.vhskillpro.backend.modules.permission.dto.PermissionDTO;

@Service
public class PermissionService {
  private PermissionRepository permissionRepository;
  private ModelMapper modelMapper;

  public PermissionService(PermissionRepository permissionRepository, ModelMapper modelMapper) {
    this.permissionRepository = permissionRepository;
    this.modelMapper = modelMapper;
  }

  /**
   * Retrieves a permission by id and maps it to a PermissionDTO.
   *
   * @param id the unique identifier of the permission to retrieve
   * @return an {@link Optional} containing the mapped {@link PermissionDTO} if
   *         found, or an empty {@link Optional} if not found
   */
  public Optional<PermissionDTO> findById(Long id) {
    return permissionRepository.findById(id)
        .map(permission -> modelMapper.map(permission, PermissionDTO.class));
  }
}
