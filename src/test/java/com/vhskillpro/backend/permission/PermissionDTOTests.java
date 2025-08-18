package com.vhskillpro.backend.permission;

import static org.assertj.core.api.Assertions.assertThat;

import com.vhskillpro.backend.modules.permission.dto.PermissionDTO;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PermissionDTO Tests")
class PermissionDTOTests {

  @Test
  @DisplayName("Builder should set all fields correctly")
  void builder_shouldSetAllFields() {
    Instant now = Instant.now();
    PermissionDTO dto =
        PermissionDTO.builder()
            .id(1L)
            .name("permission:read")
            .title("Read permissions")
            .description("Allows reading permissions")
            .createdAt(now)
            .updatedAt(now)
            .build();

    assertThat(dto.getId()).isEqualTo(1L);
    assertThat(dto.getName()).isEqualTo("permission:read");
    assertThat(dto.getTitle()).isEqualTo("Read permissions");
    assertThat(dto.getDescription()).isEqualTo("Allows reading permissions");
    assertThat(dto.getCreatedAt()).isEqualTo(now);
    assertThat(dto.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("No-args constructor with setters should work")
  void noArgsConstructor_withSetters_shouldWork() {
    Instant now = Instant.now();
    PermissionDTO dto = new PermissionDTO();
    dto.setId(2L);
    dto.setName("permission:write");
    dto.setTitle("Write permissions");
    dto.setDescription("Allows writing permissions");
    dto.setCreatedAt(now);
    dto.setUpdatedAt(now);

    assertThat(dto.getId()).isEqualTo(2L);
    assertThat(dto.getName()).isEqualTo("permission:write");
    assertThat(dto.getTitle()).isEqualTo("Write permissions");
    assertThat(dto.getDescription()).isEqualTo("Allows writing permissions");
    assertThat(dto.getCreatedAt()).isEqualTo(now);
    assertThat(dto.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("All-args constructor should set fields correctly")
  void allArgsConstructor_shouldSetFields() {
    Instant created = Instant.now();
    Instant updated = created.plusSeconds(60);

    PermissionDTO dto =
        new PermissionDTO(3L, "role:read", "Read role", "Allows reading roles", created, updated);

    assertThat(dto.getId()).isEqualTo(3L);
    assertThat(dto.getName()).isEqualTo("role:read");
    assertThat(dto.getTitle()).isEqualTo("Read role");
    assertThat(dto.getDescription()).isEqualTo("Allows reading roles");
    assertThat(dto.getCreatedAt()).isEqualTo(created);
    assertThat(dto.getUpdatedAt()).isEqualTo(updated);
  }

  @Test
  @DisplayName("Equals and hashCode should consider all fields")
  void equalsAndHashCode_shouldWork() {
    Instant now = Instant.now();

    PermissionDTO dto1 =
        PermissionDTO.builder()
            .id(10L)
            .name("permission:manage")
            .title("Manage permissions")
            .description("Allows managing permissions")
            .createdAt(now)
            .updatedAt(now)
            .build();

    PermissionDTO dto2 =
        PermissionDTO.builder()
            .id(10L)
            .name("permission:manage")
            .title("Manage permissions")
            .description("Allows managing permissions")
            .createdAt(now)
            .updatedAt(now)
            .build();

    PermissionDTO dto3 =
        PermissionDTO.builder()
            .id(11L) // different id
            .name("permission:manage")
            .title("Manage permissions")
            .description("Allows managing permissions")
            .createdAt(now)
            .updatedAt(now)
            .build();

    assertThat(dto1).isEqualTo(dto2).hasSameHashCodeAs(dto2);
    assertThat(dto1).isNotEqualTo(dto3);
  }

  @Test
  @DisplayName("toString should contain key fields")
  void toString_shouldContainKeyFields() {
    PermissionDTO dto =
        PermissionDTO.builder()
            .id(20L)
            .name("permission:export")
            .title("Export permissions")
            .description("Allows exporting permissions")
            .build();

    String toString = dto.toString();
    assertThat(toString).contains("PermissionDTO");
    assertThat(toString).contains("permission:export");
    assertThat(toString).contains("Export permissions");
  }
}
