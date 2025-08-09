package com.vhskillpro.backend.modules.permission.dto;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Permission DTO")
public class PermissionDTO {
  @Schema(description = "Unique identifier for the permission", example = "1")
  private Long id;

  @Schema(description = "Name of the permission", example = "permission:read")
  private String name;

  @Schema(description = "Title of the permission", example = "Read permissions")
  private String title;

  @Schema(description = "Description of the permission", example = "Allows reading permissions")
  private String description;

  @Schema(description = "Timestamp when the permission was created", example = "2023-01-01T12:00:00Z")
  private Instant createdAt;

  @Schema(description = "Timestamp when the permission was last updated", example = "2023-01-01T12:00:00Z")
  private Instant updatedAt;
}
