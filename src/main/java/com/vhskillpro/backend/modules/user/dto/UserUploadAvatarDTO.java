package com.vhskillpro.backend.modules.user.dto;

import com.vhskillpro.backend.common.validation.constraints.ValidImage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "User avatar upload request")
public class UserUploadAvatarDTO {
  @NotNull(message = "Avatar file is required")
  @ValidImage(
      message = "Invalid image file",
      maxSize = 2 * 1024 * 1024,
      allowedTypes = {"image/jpeg", "image/png", "image/jpg"})
  @Schema(description = "Avatar image file to upload", required = true)
  private MultipartFile avatarFile;
}
