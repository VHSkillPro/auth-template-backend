package com.vhskillpro.backend.role;

import static org.assertj.core.api.Assertions.assertThat;

import com.vhskillpro.backend.modules.role.Role;
import com.vhskillpro.backend.modules.role.RoleRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@DisplayName("RoleRepository Tests")
class RoleRepositoryTests {

  @Autowired private RoleRepository roleRepository;

  private Role createRole(String name, String title, String description) {
    return Role.builder().name(name).title(title).description(description).build();
  }

  @Test
  @DisplayName(
      "findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase should filter by keyword and paginate")
  void findByNameAndTitleContainingIgnoreCase_shouldWork() {
    roleRepository.saveAll(
        List.of(
            createRole("admin", "Read Admin", "System administrator"),
            createRole("adapter", "Advanced Admin", "Adapter role"),
            createRole("user", "User", "Regular user")));

    Pageable pageable = PageRequest.of(0, 10);

    Page<Role> page =
        roleRepository.findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            "ad", "ad", pageable);

    assertThat(page.getTotalElements()).isEqualTo(2);
    assertThat(page.getContent())
        .extracting(Role::getName)
        .containsExactlyInAnyOrder("admin", "adapter");
  }

  @Test
  @DisplayName("existsByName should return true only when role exists")
  void existsByName_shouldReturnCorrectly() {
    roleRepository.save(createRole("manager", "Manager", "Manages things"));

    assertThat(roleRepository.existsByName("manager")).isTrue();
    assertThat(roleRepository.existsByName("unknown")).isFalse();
  }
}
