package com.vhskillpro.backend.permission;

import static org.assertj.core.api.Assertions.assertThat;

import com.vhskillpro.backend.modules.permission.Permission;
import com.vhskillpro.backend.modules.permission.PermissionRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@Import(TestJpaConfig.class)
@DisplayName("PermissionRepository Tests")
class PermissionRepositoryTests {

  @Autowired private PermissionRepository permissionRepository;

  private Permission createPermission(String name, String title, String description) {
    return Permission.builder().name(name).title(title).description(description).build();
  }

  @Test
  @DisplayName(
      "findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase should filter by keyword case-insensitively and paginate")
  void findByNameAndTitleContainingIgnoreCase_shouldWork() {
    permissionRepository.saveAll(
        List.of(
            createPermission("permission:read", "Read permissions", "Allows reading permissions"),
            createPermission("permission:write", "Write permissions", "Allows writing permissions"),
            createPermission("role:read", "Read role", "Allows reading roles")));

    Pageable pageable = PageRequest.of(0, 10);

    Page<Permission> page =
        permissionRepository.findByNameContainingIgnoreCaseAndTitleContainingIgnoreCase(
            "read", "read", pageable);

    assertThat(page.getTotalElements()).isEqualTo(2);
    assertThat(page.getContent())
        .extracting(Permission::getName)
        .containsExactlyInAnyOrder("permission:read", "role:read");
  }

  @Test
  @DisplayName("countByIdIn should count existing IDs only")
  void countByIdIn_shouldCountExisting() {
    Permission p1 =
        permissionRepository.save(
            createPermission("permission:read", "Read permissions", "Allows reading permissions"));
    Permission p2 =
        permissionRepository.save(
            createPermission(
                "permission:write", "Write permissions", "Allows writing permissions"));

    Long count = permissionRepository.countByIdIn(List.of(p1.getId(), p2.getId(), 999L));
    assertThat(count).isEqualTo(2L);
  }

  @Test
  @DisplayName("findByIdIn should return only existing IDs and preserve no specific order")
  void findByIdIn_shouldReturnExisting() {
    Permission p1 =
        permissionRepository.save(
            createPermission("permission:read", "Read permissions", "Allows reading permissions"));
    Permission p2 =
        permissionRepository.save(
            createPermission(
                "permission:write", "Write permissions", "Allows writing permissions"));

    List<Permission> found = permissionRepository.findByIdIn(List.of(p1.getId(), p2.getId(), 999L));

    assertThat(found).hasSize(2);
    assertThat(found)
        .extracting(Permission::getId)
        .containsExactlyInAnyOrder(p1.getId(), p2.getId());
  }
}
