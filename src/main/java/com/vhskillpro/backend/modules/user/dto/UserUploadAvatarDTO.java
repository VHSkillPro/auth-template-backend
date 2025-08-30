package com.vhskillpro.backend.modules.user.dto;

import com.vhskillpro.backend.common.validation.constraints.ValidImage;
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
public class UserUploadAvatarDTO {
  @NotNull(message = "USER_AVATAR_REQUIRED")
  @ValidImage(
      maxSize = 2 * 1024 * 1024,
      allowedTypes = {"image/jpeg", "image/png", "image/jpg"})
  private MultipartFile avatarFile;
}
