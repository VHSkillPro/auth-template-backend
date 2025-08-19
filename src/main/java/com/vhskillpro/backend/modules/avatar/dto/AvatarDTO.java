package com.vhskillpro.backend.modules.avatar.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Data transfer object for user avatar")
public class AvatarDTO {
  @Schema(description = "Avatar image URL")
  private String avatarUrl;
}
