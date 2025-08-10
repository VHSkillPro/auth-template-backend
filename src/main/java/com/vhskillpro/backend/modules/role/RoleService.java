package com.vhskillpro.backend.modules.role;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vhskillpro.backend.modules.role.dto.RoleDTO;

import jakarta.transaction.Transactional;

@Service
public class RoleService {
  private ModelMapper modelMapper;
  private RoleRepository roleRepository;

  public RoleService(ModelMapper modelMapper, RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
    this.modelMapper = modelMapper;
  }

  /**
   * Retrieves a paginated list of roles filtered by the given keyword, mapping
   * each Role entity to a RoleDTO.
   * The search is performed on both the name and title fields of the Role entity,
   * ignoring case sensitivity.
   * The permissions field in RoleDTO is intentionally skipped during the mapping
   * process.
   *
   * @param keyword  the keyword to filter roles by name and title
   * @param pageable the pagination information
   * @return a page of RoleDTO objects matching the search criteria
   */
  @Transactional
  public Page<RoleDTO> findAll(String keyword, Pageable pageable) {
    Page<RoleDTO> roleDTOsPage = roleRepository
        .findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(keyword, keyword, pageable)
        .map(role -> modelMapper.map(role, RoleDTO.class));

    return roleDTOsPage.map(roleDTO -> {
      roleDTO.setPermissions(null);
      return roleDTO;
    });
  }

  /**
   * Retrieves a role by its unique identifier and maps it to a RoleDTO.
   *
   * @param id the unique identifier of the role to retrieve
   * @return an {@link Optional} containing the mapped {@link RoleDTO} if found,
   *         or an empty Optional if not found
   */
  @Transactional
  public Optional<RoleDTO> findById(Long id) {
    return roleRepository.findById(id).map(role -> modelMapper.map(role, RoleDTO.class));
  }
}
