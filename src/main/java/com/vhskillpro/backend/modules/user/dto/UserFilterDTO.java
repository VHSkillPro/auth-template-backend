package com.vhskillpro.backend.modules.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User filter criteria")
public class UserFilterDTO {
  @Schema(description = "Search keyword for user filtering", example = "john")
  private String keyword;

  @Schema(description = "Filter by enabled status", example = "true|false|0|1")
  private String enabled;

  @Schema(description = "Filter by locked status", example = "true|false|0|1")
  private String locked;

  @Schema(description = "Filter by superuser status", example = "true|false|0|1")
  private String superuser;

  @Schema(description = "Filter by role name", example = "admin")
  private String roleName;
}
