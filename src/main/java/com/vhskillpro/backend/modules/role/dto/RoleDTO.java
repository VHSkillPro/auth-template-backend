package com.vhskillpro.backend.modules.role.dto;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "Role DTO")
public class RoleDTO {
  @Schema(description = "Unique identifier for the role", example = "1")
  private Long id;

  @Schema(description = "Name of the role", example = "Admin")
  private String name;

  @Schema(description = "Title of the role", example = "Administrator")
  private String title;

  @Schema(description = "Description of the role", example = "Manages all aspects of the system")
  private String description;

  @Schema(description = "Creation timestamp of the role", example = "2023-01-01T12:00:00Z")
  private Instant createdAt;

  @Schema(description = "Last update timestamp of the role", example = "2023-01-01T12:00:00Z")
  private Instant updatedAt;
}
