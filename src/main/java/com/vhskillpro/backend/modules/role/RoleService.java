package com.vhskillpro.backend.modules.role;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vhskillpro.backend.modules.permission.PermissionRepository;
import com.vhskillpro.backend.modules.role.dto.RoleCreateDTO;
import com.vhskillpro.backend.modules.role.dto.RoleDTO;
import com.vhskillpro.backend.modules.role.dto.RoleDetailDTO;

import jakarta.transaction.Transactional;

@Service
public class RoleService {
  private ModelMapper modelMapper;
  private RoleRepository roleRepository;
  private PermissionRepository permissionRepository;

  public RoleService(
      ModelMapper modelMapper,
      RoleRepository roleRepository,
      PermissionRepository permissionRepository) {
    this.roleRepository = roleRepository;
    this.modelMapper = modelMapper;
    this.permissionRepository = permissionRepository;
  }

  /**
   * Retrieves a paginated list of roles filtered by the given keyword, mapping
   * each Role entity to a RoleDTO.
   * The search is performed on both the name and title fields of the Role entity,
   * ignoring case sensitivity.
   *
   * @param keyword  the keyword to filter roles by name and title
   * @param pageable the pagination information
   * @return a page of RoleDTO objects matching the search criteria
   */
  @Transactional
  public Page<RoleDTO> findAll(String keyword, Pageable pageable) {
    return roleRepository
        .findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(keyword, keyword, pageable)
        .map(role -> modelMapper.map(role, RoleDTO.class));
  }

  /**
   * Retrieves a role by its unique identifier and maps it to a RoleDetailDTO.
   *
   * @param id the unique identifier of the role to retrieve
   * @return an {@link Optional} containing the mapped {@link RoleDetailDTO} if
   *         found, or an empty Optional if not found
   */
  @Transactional
  public Optional<RoleDetailDTO> findById(Long id) {
    return roleRepository.findById(id)
        .map(role -> modelMapper.map(role, RoleDetailDTO.class));
  }

  /**
   * Creates and saves a new {@link Role} entity using the provided
   * {@link RoleCreateDTO}.
   * Maps the saved entity to a {@link RoleDetailDTO} and returns it.
   *
   * @param roleCreateDTO the data transfer object containing role details and
   *                      permission IDs
   * @return the detailed data transfer object of the saved role
   */
  @Transactional
  public RoleDetailDTO create(RoleCreateDTO roleCreateDTO) {
    Role role = Role.builder()
        .name(roleCreateDTO.getName())
        .title(roleCreateDTO.getTitle())
        .description(roleCreateDTO.getDescription())
        .permissions(permissionRepository.findAllById(roleCreateDTO.getPermissionIds()))
        .build();

    role = roleRepository.save(role);
    return modelMapper.map(role, RoleDetailDTO.class);
  }

  /**
   * Deletes a role by its ID.
   * <p>
   * This method first checks if the role with the specified ID exists.
   * If it does, it deletes all associated role permissions and then deletes the
   * role itself.
   * Returns {@code true} if the role was deleted successfully, or {@code false}
   * if the role does not exist.
   *
   * @param roleId the ID of the role to delete
   * @return {@code true} if the role was deleted, {@code false} otherwise
   */
  @Transactional
  public boolean delete(Long roleId) {
    if (!roleRepository.existsById(roleId)) {
      return false;
    }
    roleRepository.deleteById(roleId);
    return true;
  }
}
