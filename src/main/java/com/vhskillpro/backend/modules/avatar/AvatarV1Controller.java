package com.vhskillpro.backend.modules.avatar;

import com.vhskillpro.backend.common.response.ApiResponse;
import com.vhskillpro.backend.common.response.DataApiResponse;
import com.vhskillpro.backend.common.swagger.BadRequestApiResponse;
import com.vhskillpro.backend.common.swagger.UnauthorizedApiResponse;
import com.vhskillpro.backend.modules.avatar.dto.AvatarDTO;
import com.vhskillpro.backend.modules.user.CustomUserDetails;
import com.vhskillpro.backend.modules.user.dto.UserUploadAvatarDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "Avatar (V1)", description = "APIs for managing user avatars")
public class AvatarV1Controller {
  private final AvatarService avatarService;

  public AvatarV1Controller(AvatarService avatarService) {
    this.avatarService = avatarService;
  }

  /**
   * Handles the uploading of a user's avatar image.
   *
   * <p>This endpoint accepts a multipart/form-data request containing the avatar image file. Only
   * authenticated users can upload their avatar.
   *
   * @param authentication the authentication object containing the current user's details
   * @param userUploadAvatarDTO the DTO containing the avatar file to be uploaded
   * @return an {@link ApiResponse} indicating the result of the upload operation
   */
  @Operation(
      summary = "Upload user avatar",
      parameters = {@Parameter(name = "id", description = "User ID")},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @io.swagger.v3.oas.annotations.media.Content(
                      mediaType = "multipart/form-data",
                      schema =
                          @io.swagger.v3.oas.annotations.media.Schema(
                              implementation = UserUploadAvatarDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "AVATAR_UPLOAD_SUCCESS",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
      })
  @BadRequestApiResponse
  @UnauthorizedApiResponse
  @PutMapping(value = "/avatar", consumes = "multipart/form-data")
  public ApiResponse<Void> uploadAvatar(
      Authentication authentication,
      @Valid @ModelAttribute UserUploadAvatarDTO userUploadAvatarDTO) {
    Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
    avatarService.uploadAvatar(userId, userUploadAvatarDTO.getAvatarFile());
    return ApiResponse.success(AvatarMessages.AVATAR_UPLOAD_SUCCESS.toString());
  }

  /**
   * Retrieves the avatar of the authenticated user.
   *
   * @param authentication the authentication object containing user details
   * @return a {@link DataApiResponse} containing the user's avatar data and a success message
   */
  @Operation(
      summary = "Get user avatar",
      parameters = {@Parameter(name = "id", description = "User ID")},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "AVATAR_GET_SUCCESS",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = DataApiResponseAvatarDTO.class)))
      })
  @UnauthorizedApiResponse
  @GetMapping(value = "/avatar")
  public DataApiResponse<AvatarDTO> getAvatar(Authentication authentication) {
    Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
    AvatarDTO avatarDTO = avatarService.getAvatar(userId);
    return DataApiResponse.success(avatarDTO, AvatarMessages.AVATAR_GET_SUCCESS.toString());
  }

  /**
   * Deletes the avatar of the authenticated user.
   *
   * <p>This endpoint removes the avatar associated with the currently authenticated user. Returns a
   * success response if the avatar is deleted, or a not found response if the user does not exist.
   *
   * @param authentication the authentication object containing the user's details
   * @return an {@link ApiResponse} indicating the result of the delete operation
   */
  @Operation(
      summary = "Delete user avatar",
      parameters = {@Parameter(name = "id", description = "User ID")},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "AVATAR_DELETE_SUCCESS",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class)))
      })
  @UnauthorizedApiResponse
  @DeleteMapping(value = "/avatar")
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<Void> deleteAvatar(Authentication authentication) {
    Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
    avatarService.deleteAvatar(userId);
    return ApiResponse.success(AvatarMessages.AVATAR_DELETE_SUCCESS.toString());
  }

  private class DataApiResponseAvatarDTO extends DataApiResponse<AvatarDTO> {}
}
