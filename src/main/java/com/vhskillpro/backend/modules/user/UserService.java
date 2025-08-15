package com.vhskillpro.backend.modules.user;

import com.vhskillpro.backend.modules.user.dto.UserDTO;
import com.vhskillpro.backend.modules.user.dto.UserFilterDTO;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final ModelMapper modelMapper;
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository, ModelMapper modelMapper) {
    this.userRepository = userRepository;
    this.modelMapper = modelMapper;
  }

  /**
   * Retrieves a paginated list of users based on the provided filter criteria.
   *
   * <p>The method constructs a dynamic specification using the fields from {@link UserFilterDTO},
   * allowing filtering by keyword (matching full name or email), enabled status, locked status,
   * superuser status, and role name. The results are mapped to {@link UserDTO} objects.
   *
   * @param filter the filter criteria containing keyword, enabled, locked, superuser, and role name
   * @param pageable the pagination information
   * @return a page of {@link UserDTO} objects matching the filter criteria
   */
  @Transactional
  public Page<UserDTO> findAll(UserFilterDTO filter, Pageable pageable) {
    Specification<User> spec =
        Specification.allOf(
            UserSpecification.keywordContainingIgnoreCase(filter.getKeyword()),
            UserSpecification.hasEnabledStatus(filter.getEnabled()),
            UserSpecification.hasLockedStatus(filter.getLocked()),
            UserSpecification.hasSuperuserStatus(filter.getSuperuser()),
            UserSpecification.roleNameContainingIgnoreCase(filter.getRoleName()));
    return userRepository.findAll(spec, pageable).map(user -> modelMapper.map(user, UserDTO.class));
  }
}
