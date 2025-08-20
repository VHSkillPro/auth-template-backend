package com.vhskillpro.backend.modules.auth;

import com.vhskillpro.backend.common.response.ApiResponse;
import com.vhskillpro.backend.common.response.DataApiResponse;
import com.vhskillpro.backend.modules.auth.dto.ProfileDTO;
import com.vhskillpro.backend.modules.auth.dto.RefreshDTO;
import com.vhskillpro.backend.modules.auth.dto.ResendVerificationEmailDTO;
import com.vhskillpro.backend.modules.auth.dto.SignInDTO;
import com.vhskillpro.backend.modules.auth.dto.TokenDTO;
import com.vhskillpro.backend.modules.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth (V1)", description = "APIs for user authentication")
public class AuthV1Controller {
  private final AuthService authService;

  public AuthV1Controller(AuthService authService) {
    this.authService = authService;
  }

  /**
   * Handles user sign-in requests.
   *
   * <p>Accepts user credentials via a {@link SignInDTO} object, authenticates the user, and returns
   * a JWT token wrapped in a {@link DataApiResponse} if authentication is successful.
   *
   * @param signInDTO the sign-in request payload containing user credentials
   * @return a {@link DataApiResponse} containing the authentication token and a success message
   */
  @Operation(
      summary = "Sign In",
      description =
          "Authenticates a user and returns a JWT token. This API doesn't need authentication.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User signed in successfully",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = DataApiResponseTokenDTO.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Bad request, e.g., invalid credentials",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class)))
      })
  @PostMapping("/sign-in")
  public DataApiResponse<TokenDTO> signIn(@Valid @RequestBody SignInDTO signInDTO) {
    TokenDTO token = authService.signIn(signInDTO);
    return DataApiResponse.success(token, AuthMessages.SIGN_IN_SUCCESS.getMessage());
  }

  /**
   * Handles the request to resend a verification email to the user.
   *
   * <p>Expects a {@link ResendVerificationEmailDTO} containing the user's email address. Invokes
   * the authentication service to send a new verification email. Returns a success response if the
   * email was sent successfully.
   *
   * @param resendVerificationEmailDTO DTO containing the email address to resend verification to
   * @return {@link ApiResponse} indicating the result of the operation
   */
  @Operation(
      summary = "Resend Verification Email",
      description =
          "Resends a verification email to the user with the specified email address. This API"
              + " doesn't need authentication.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Verification email resent successfully",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Bad request, e.g., user already enabled or token already sent",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class)))
      })
  @PostMapping("/resend-verification-email")
  public ApiResponse<Void> resendVerificationEmail(
      @Valid @RequestBody ResendVerificationEmailDTO resendVerificationEmailDTO) {
    authService.sendVerificationEmail(resendVerificationEmailDTO.getEmail());
    return ApiResponse.success(AuthMessages.RESEND_VERIFICATION_EMAIL_SUCCESS.getMessage());
  }

  /**
   * Verifies a user's email address using the provided token.
   *
   * <p>This endpoint is typically called when a user clicks on an email verification link. Upon
   * successful verification, a success response is returned.
   *
   * @param token the email verification token sent to the user's email address
   * @return an {@link ApiResponse} indicating the result of the verification process
   */
  @Operation(
      summary = "Verify Email",
      description =
          "Verifies a user's email address using the provided token. This API doesn't need"
              + " authentication.",
      parameters = {
        @io.swagger.v3.oas.annotations.Parameter(
            name = "token",
            description = "The email verification token sent to the user's email address",
            required = true)
      },
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Email verified successfully",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Bad request, e.g., invalid or expired token",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class)))
      })
  @GetMapping("/verify-email")
  public ApiResponse<Void> verifyEmail(@RequestParam String token) {
    authService.verifyEmail(token);
    return ApiResponse.success(AuthMessages.EMAIL_VERIFICATION_SUCCESS.getMessage());
  }

  /**
   * Retrieves the profile information of the currently authenticated user.
   *
   * <p>This endpoint requires authentication and returns the user's profile details.
   *
   * @param authentication the authentication object containing the user's credentials
   * @return a {@link DataApiResponse} containing the {@link ProfileDTO} of the authenticated user
   */
  @Operation(
      summary = "Get User Profile",
      description = "Fetches the profile of the authenticated user.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Profile fetched successfully",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = DataApiResponseProfileDTO.class))),
      })
  @GetMapping("/profile")
  public DataApiResponse<ProfileDTO> getProfile(Authentication authentication) {
    Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
    ProfileDTO profile = authService.getProfile(userId);
    return DataApiResponse.success(profile, AuthMessages.PROFILE_FETCH_SUCCESS.getMessage());
  }

  /**
   * Handles the refresh token request.
   *
   * <p>Accepts a valid {@link RefreshDTO} containing the refresh token and returns a new access
   * token.
   *
   * @param refreshDTO the DTO containing the refresh token, validated for correctness
   * @return a {@link DataApiResponse} containing the refreshed {@link TokenDTO} and a success
   *     message
   */
  @Operation(
      summary = "Refresh Token",
      description = "Refreshes the access token using the provided refresh token.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Token refreshed successfully",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = DataApiResponseTokenDTO.class)))
      })
  @PostMapping("/refresh")
  public DataApiResponse<TokenDTO> refresh(@Valid @RequestBody RefreshDTO refreshDTO) {
    TokenDTO tokenDTO = authService.refresh(refreshDTO);
    return DataApiResponse.success(tokenDTO, AuthMessages.TOKEN_REFRESH_SUCCESS.getMessage());
  }

  private class DataApiResponseTokenDTO extends DataApiResponse<TokenDTO> {}

  private class DataApiResponseProfileDTO extends DataApiResponse<ProfileDTO> {}
}
