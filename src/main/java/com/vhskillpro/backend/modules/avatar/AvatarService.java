package com.vhskillpro.backend.modules.avatar;

import com.vhskillpro.backend.utils.r2.R2Properties;
import com.vhskillpro.backend.utils.r2.R2Service;
import java.io.IOException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AvatarService {
  private static final String AVATAR_BUCKET = "avatar";

  private final R2Service r2Service;
  private final R2Properties r2Properties;

  public AvatarService(R2Service r2Service, R2Properties r2Properties) {
    this.r2Service = r2Service;
    this.r2Properties = r2Properties;
  }

  /**
   * Uploads an avatar image file to the configured storage bucket.
   *
   * <p>Generates a unique object key by combining a random UUID with the original filename, then
   * uploads the file using the provided {@link MultipartFile} details.
   *
   * @param file the avatar image file to upload
   * @return the unique object key assigned to the uploaded file
   * @throws IOException if an I/O error occurs during file upload
   */
  public String uploadAvatar(MultipartFile file) throws IOException {
    String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
    String objectKey = UUID.randomUUID().toString() + "." + extension;

    r2Service.uploadFile(
        AVATAR_BUCKET, objectKey, file.getInputStream(), file.getSize(), file.getContentType());
    return objectKey;
  }

  /**
   * Generates a presigned URL for accessing an avatar image stored in the specified bucket.
   *
   * @param objectKey the key (filename or path) of the avatar object in the storage bucket
   * @return a presigned URL that allows temporary access to the avatar image
   */
  public String getAvatarUrl(String objectKey) {
    return r2Service.generatePresignedUrl(
        AVATAR_BUCKET, objectKey, r2Properties.getPresignedUrlExpiration());
  }

  /**
   * Deletes an avatar image from the storage bucket using the specified object key.
   *
   * @param objectKey the key (filename or path) of the avatar object to delete
   */
  public void deleteAvatar(String objectKey) {
    r2Service.deleteFile(AVATAR_BUCKET, objectKey);
  }
}
