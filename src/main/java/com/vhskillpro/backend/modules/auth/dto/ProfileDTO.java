package com.vhskillpro.backend.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for User Profile")
public class ProfileDTO {
  @Schema(description = "Unique identifier of the user")
  private Long id;

  @Schema(description = "Email address of the user")
  private String email;

  @Schema(description = "First name of the user")
  private String firstName;

  @Schema(description = "Last name of the user")
  private String lastName;

  @Schema(description = "List of permissions assigned to the user")
  private List<String> permissions;

  @Schema(description = "Indicates if the user is enabled")
  private Boolean enabled;

  @Schema(description = "Indicates if the user is locked")
  private Boolean locked;

  @Schema(description = "Indicates if the user is a superuser")
  private Boolean superuser;

  @Schema(description = "Timestamp when the user was created")
  private Instant createdAt;

  @Schema(description = "Timestamp when the user was last updated")
  private Instant updatedAt;
}
