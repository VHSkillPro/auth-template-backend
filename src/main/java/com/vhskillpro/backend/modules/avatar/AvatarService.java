package com.vhskillpro.backend.modules.avatar;

import com.vhskillpro.backend.exception.AppException;
import com.vhskillpro.backend.modules.avatar.dto.AvatarDTO;
import com.vhskillpro.backend.modules.user.User;
import com.vhskillpro.backend.modules.user.UserMessages;
import com.vhskillpro.backend.modules.user.UserRepository;
import com.vhskillpro.backend.utils.r2.R2Properties;
import com.vhskillpro.backend.utils.r2.R2Service;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AvatarService {
  private static final String AVATAR_BUCKET = "avatar";

  private final R2Service r2Service;
  private final R2Properties r2Properties;
  private final UserRepository userRepository;

  public AvatarService(
      R2Service r2Service, R2Properties r2Properties, UserRepository userRepository) {
    this.r2Service = r2Service;
    this.r2Properties = r2Properties;
    this.userRepository = userRepository;
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

  /**
   * Retrieves the avatar URL for a user by their ID.
   *
   * @param userId the ID of the user whose avatar is to be retrieved
   * @return an {@link AvatarDTO} containing the avatar URL
   * @throws AppException if the user with the specified ID is not found
   */
  @Transactional
  public AvatarDTO getAvatar(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new AppException(HttpStatus.NOT_FOUND, UserMessages.USER_NOT_FOUND.toString()));
    String avatarUrl = user.getAvatarUrl();

    return AvatarDTO.builder()
        .avatarUrl(avatarUrl != null ? getAvatarUrl(avatarUrl) : null)
        .build();
  }

  /**
   * Uploads a new avatar for the specified user.
   *
   * <p>This method finds the user by their ID, uploads the provided avatar file, deletes the user's
   * previous avatar if it exists, and updates the user's avatar URL. Access is restricted to the
   * user themselves.
   *
   * @param userId the ID of the user uploading the avatar
   * @param avatarFile the avatar image file to upload
   * @throws AppException if the user is not found, or if the upload fails
   */
  @Transactional
  public void uploadAvatar(Long userId, MultipartFile avatarFile) {
    try {
      // Find the user by ID
      User user =
          userRepository
              .findById(userId)
              .orElseThrow(
                  () ->
                      new AppException(
                          HttpStatus.NOT_FOUND, UserMessages.USER_NOT_FOUND.toString()));

      // Upload the avatar file and get the object key
      String objectKey = uploadAvatar(avatarFile);

      // If the user already has an avatar, delete the old one
      if (user.getAvatarUrl() != null) {
        deleteAvatar(user.getAvatarUrl());
      }

      // Set the new avatar URL for the user
      user.setAvatarUrl(objectKey);
      userRepository.save(user);
    } catch (AppException e) {
      throw e;
    } catch (Exception e) {
      System.err.println(e.getMessage());
      throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload avatar");
    }
  }

  /**
   * Deletes the avatar associated with the specified user.
   *
   * <p>This method first retrieves the user by their ID. If the user exists and has an avatar URL,
   * it deletes the avatar resource, sets the user's avatar URL to {@code null}, and saves the
   * updated user. Access is restricted to the user whose ID matches the authenticated principal.
   *
   * @param userId the ID of the user whose avatar is to be deleted
   * @throws AppException if the user is not found
   */
  @Transactional
  public void deleteAvatar(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new AppException(HttpStatus.NOT_FOUND, UserMessages.USER_NOT_FOUND.toString()));
    String avatarUrl = user.getAvatarUrl();
    if (avatarUrl != null) {
      deleteAvatar(avatarUrl);
      user.setAvatarUrl(null);
      userRepository.save(user);
    }
  }
}
