package com.vhskillpro.backend.modules.avatar;

import com.vhskillpro.backend.common.response.ApiResponse;
import com.vhskillpro.backend.common.response.DataApiResponse;
import com.vhskillpro.backend.modules.avatar.dto.AvatarDTO;
import com.vhskillpro.backend.modules.user.dto.UserUploadAvatarDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
   * <p>Expects a multipart/form-data request containing the avatar file. The avatar is associated
   * with the user identified by the provided ID.
   *
   * @param id the ID of the user whose avatar is being uploaded
   * @param userUploadAvatarDTO DTO containing the avatar file to upload
   * @return an {@link ApiResponse} indicating the success of the upload operation
   */
  @Operation(
      summary = "Upload user avatar",
      description = "Uploads a user's avatar image. Only users ",
      parameters = {@Parameter(name = "id", description = "Unique identifier of the user")},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "User avatar upload details",
              content =
                  @io.swagger.v3.oas.annotations.media.Content(
                      mediaType = "multipart/form-data",
                      schema =
                          @io.swagger.v3.oas.annotations.media.Schema(
                              implementation = UserUploadAvatarDTO.class))),
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User avatar uploaded successfully",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid file format or size",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class)))
      })
  @PutMapping(value = "/{id}/avatar", consumes = "multipart/form-data")
  public ApiResponse<Void> uploadAvatar(
      @PathVariable Long id, @Valid @ModelAttribute UserUploadAvatarDTO userUploadAvatarDTO) {
    avatarService.uploadAvatar(id, userUploadAvatarDTO.getAvatarFile());
    return ApiResponse.success(AvatarMessages.AVATAR_UPLOAD_SUCCESS.getMessage());
  }

  /**
   * Retrieves the avatar of a user by their unique identifier.
   *
   * @param id the unique identifier of the user whose avatar is to be retrieved
   * @return a {@link DataApiResponse} containing the {@link AvatarDTO} if found, along with a
   *     success message
   * @throws UserNotFoundException if the user with the specified ID does not exist
   */
  @Operation(
      summary = "Get user avatar",
      description = "Retrieves the avatar of a user by their ID",
      parameters = {@Parameter(name = "id", description = "Unique identifier of the user")},
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User avatar retrieved successfully",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = DataApiResponseAvatarDTO.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class)))
      })
  @GetMapping(value = "/{id}/avatar")
  public DataApiResponse<AvatarDTO> getAvatar(@PathVariable Long id) {
    AvatarDTO avatarDTO = avatarService.getAvatar(id);
    return DataApiResponse.success(avatarDTO, AvatarMessages.AVATAR_GET_SUCCESS.getMessage());
  }

  private class DataApiResponseAvatarDTO extends DataApiResponse<AvatarDTO> {}
}
