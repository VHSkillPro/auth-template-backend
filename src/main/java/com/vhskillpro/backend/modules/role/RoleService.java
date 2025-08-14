package com.vhskillpro.backend.modules.role;

import com.vhskillpro.backend.exception.AppException;
import com.vhskillpro.backend.modules.permission.PermissionRepository;
import com.vhskillpro.backend.modules.role.dto.RoleCreateDTO;
import com.vhskillpro.backend.modules.role.dto.RoleDTO;
import com.vhskillpro.backend.modules.role.dto.RoleDetailDTO;
import com.vhskillpro.backend.modules.role.dto.RoleUpdateDTO;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
   * Retrieves a paginated list of roles filtered by the given keyword, mapping each Role entity to
   * a RoleDTO. The search is performed on both the name and title fields of the Role entity,
   * ignoring case sensitivity.
   *
   * @param keyword the keyword to filter roles by name and title
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
   * @return an {@link Optional} containing the mapped {@link RoleDetailDTO} if found, or an empty
   *     Optional if not found
   */
  @Transactional
  public Optional<RoleDetailDTO> findById(Long id) {
    return roleRepository.findById(id).map(role -> modelMapper.map(role, RoleDetailDTO.class));
  }

  /**
   * Creates and saves a new {@link Role} entity using the provided {@link RoleCreateDTO}. Maps the
   * saved entity to a {@link RoleDetailDTO} and returns it.
   *
   * @param roleCreateDTO the data transfer object containing role details and permission IDs
   * @return the detailed data transfer object of the saved role
   */
  @Transactional
  public RoleDetailDTO create(RoleCreateDTO roleCreateDTO) {
    Role role =
        Role.builder()
            .name(roleCreateDTO.getName())
            .title(roleCreateDTO.getTitle())
            .description(roleCreateDTO.getDescription())
            .permissions(permissionRepository.findAllById(roleCreateDTO.getPermissionIds()))
            .build();

    role = roleRepository.save(role);
    return modelMapper.map(role, RoleDetailDTO.class);
  }

  /**
   * Updates an existing role with the provided details.
   *
   * <p>Finds the role by its ID, updates its title, description, and permissions, then saves the
   * changes and returns the updated role as a {@link RoleDetailDTO}.
   *
   * @param id the ID of the role to update
   * @param roleUpdateDTO the DTO containing updated role information
   * @return the updated role details as a {@link RoleDetailDTO}
   * @throws AppException if the role with the specified ID is not found
   */
  @Transactional
  public RoleDetailDTO update(Long id, RoleUpdateDTO roleUpdateDTO) {
    Role role =
        roleRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new AppException(
                        HttpStatus.NOT_FOUND, RoleMessages.ROLE_NOT_FOUND.getMessage()));

    role.setTitle(roleUpdateDTO.getTitle());
    role.setDescription(roleUpdateDTO.getDescription());
    role.setPermissions(permissionRepository.findAllById(roleUpdateDTO.getPermissionIds()));

    role = roleRepository.save(role);
    return modelMapper.map(role, RoleDetailDTO.class);
  }

  /**
   * Deletes a role by its ID.
   *
   * <p>Checks if the role exists before attempting deletion. If the role does not exist, throws an
   * {@link AppException} with a NOT_FOUND status and a relevant message.
   *
   * @param roleId the ID of the role to delete
   * @return {@code true} if the role was successfully deleted
   * @throws AppException if the role with the specified ID does not exist
   */
  @Transactional
  public boolean delete(Long roleId) {
    if (!roleRepository.existsById(roleId)) {
      throw new AppException(HttpStatus.NOT_FOUND, RoleMessages.ROLE_NOT_FOUND.getMessage());
    }
    roleRepository.deleteById(roleId);
    return true;
  }
}
