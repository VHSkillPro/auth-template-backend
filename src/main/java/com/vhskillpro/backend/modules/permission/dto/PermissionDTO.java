package com.vhskillpro.backend.modules.permission.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDTO {
  private Long id;
  private String name;
  private String title;
  private String description;
  private Instant createdAt;
  private Instant updatedAt;
}
