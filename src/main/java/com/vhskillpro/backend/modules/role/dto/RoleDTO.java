package com.vhskillpro.backend.modules.role.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RoleDTO {
  private Long id;
  private String name;
  private String title;
  private String description;
  private Instant createdAt;
  private Instant updatedAt;
}
