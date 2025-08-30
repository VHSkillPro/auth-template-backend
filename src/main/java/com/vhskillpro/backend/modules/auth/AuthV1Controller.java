package com.vhskillpro.backend.modules.auth;

import com.vhskillpro.backend.common.response.ApiResponse;
import com.vhskillpro.backend.common.response.DataApiResponse;
import com.vhskillpro.backend.common.swagger.BadRequestApiResponse;
import com.vhskillpro.backend.common.swagger.UnauthorizedApiResponse;
import com.vhskillpro.backend.modules.auth.dto.ProfileDTO;
import com.vhskillpro.backend.modules.auth.dto.RefreshDTO;
import com.vhskillpro.backend.modules.auth.dto.ResendVerificationEmailDTO;
import com.vhskillpro.backend.modules.auth.dto.ResetPasswordDTO;
import com.vhskillpro.backend.modules.auth.dto.SendResetPasswordEmailDTO;
import com.vhskillpro.backend.modules.auth.dto.SignInDTO;
import com.vhskillpro.backend.modules.auth.dto.SignUpDTO;
import com.vhskillpro.backend.modules.auth.dto.TokenDTO;
import com.vhskillpro.backend.modules.user.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "SIGN_IN_SUCCESS",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = DataApiResponseTokenDTO.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "EMAIL_OR_PASSWORD_INVALID, USER_NOT_ENABLED, USER_LOCKED",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class)))
      })
  @BadRequestApiResponse
  @PostMapping("/sign-in")
  public DataApiResponse<TokenDTO> signIn(@Valid @RequestBody SignInDTO signInDTO) {
    TokenDTO token = authService.signIn(signInDTO);
    return DataApiResponse.success(token, AuthMessages.SIGN_IN_SUCCESS.toString());
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
      summary = "Send Verification Email",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "SEND_VERIFICATION_EMAIL_SUCCESS",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "USER_ALREADY_ENABLED, VERIFICATION_TOKEN_ALREADY_SENT",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "USER_NOT_FOUND",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
      })
  @PostMapping("/send-verification-email")
  public ApiResponse<Void> resendVerificationEmail(
      @Valid @RequestBody ResendVerificationEmailDTO resendVerificationEmailDTO) {
    authService.sendVerificationEmail(resendVerificationEmailDTO.getEmail());
    return ApiResponse.success(AuthMessages.SEND_VERIFICATION_EMAIL_SUCCESS.toString());
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
      parameters = {
        @io.swagger.v3.oas.annotations.Parameter(
            name = "token",
            description = "The email verification token sent to the user's email address",
            required = true)
      },
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "EMAIL_VERIFY_SUCCESS",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description =
                "INVALID_VERIFICATION_TOKEN, VERIFICATION_TOKEN_NOT_FOUND, USER_ALREADY_ENABLED",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "USER_NOT_FOUND",
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
    return ApiResponse.success(AuthMessages.EMAIL_VERIFY_SUCCESS.toString());
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
      summary = "Get user profile",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "PROFILE_GET_SUCCESS",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = DataApiResponseProfileDTO.class))),
      })
  @UnauthorizedApiResponse
  @GetMapping("/profile")
  public DataApiResponse<ProfileDTO> getProfile(Authentication authentication) {
    Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
    ProfileDTO profile = authService.getProfile(userId);
    return DataApiResponse.success(profile, AuthMessages.PROFILE_GET_SUCCESS.toString());
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
      summary = "Refresh",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "REFRESH_SUCCESS",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = DataApiResponseTokenDTO.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "INVALID_REFRESH_TOKEN, REFRESH_TOKEN_BLACKLISTED",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class)))
      })
  @PostMapping("/refresh")
  public DataApiResponse<TokenDTO> refresh(@Valid @RequestBody RefreshDTO refreshDTO) {
    TokenDTO tokenDTO = authService.refresh(refreshDTO);
    return DataApiResponse.success(tokenDTO, AuthMessages.REFRESH_SUCCESS.toString());
  }

  /**
   * Handles the request to send a password reset email to the user.
   *
   * <p>Expects a valid {@link SendResetPasswordEmailDTO} object containing the user's email
   * address. Delegates the email sending process to {@code authService}. Returns a success response
   * if the email was sent successfully.
   *
   * @param sendResetPasswordEmail the request body containing the user's email address
   * @return an {@link ApiResponse} indicating the result of the operation
   */
  @Operation(
      summary = "Send reset password email",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "RESET_PASSWORD_EMAIL_SENT",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "RESET_PASSWORD_TOKEN_ALREADY_SENT",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "USER_NOT_FOUND, USER_LOCKED, USER_NOT_ENABLED",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class)))
      })
  @PostMapping("/send-reset-password-email")
  public ApiResponse<Void> sendResetPasswordEmail(
      @Valid @RequestBody SendResetPasswordEmailDTO sendResetPasswordEmail) {
    authService.sendResetPasswordEmail(sendResetPasswordEmail.getEmail());
    return ApiResponse.success(AuthMessages.RESET_PASSWORD_EMAIL_SENT.toString());
  }

  /**
   * Handles the password reset request for a user.
   *
   * <p>Expects a {@link ResetPasswordDTO} object containing the necessary information to reset the
   * user's password. Delegates the password reset logic to the {@code authService}. Returns a
   * success response if the operation completes successfully.
   *
   * @param resetPasswordDTO the DTO containing password reset details, validated before processing
   * @return an {@link ApiResponse} indicating the success of the password reset operation
   */
  @Operation(
      summary = "Reset password",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "RESET_PASSWORD_SUCCESS",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "INVALID_RESET_PASSWORD_TOKEN",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "USER_NOT_FOUND, USER_LOCKED, USER_NOT_ENABLED",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class)))
      })
  @PostMapping("/reset-password")
  public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
    authService.resetPassword(resetPasswordDTO);
    return ApiResponse.success(AuthMessages.RESET_PASSWORD_SUCCESS.toString());
  }

  /**
   * Signs out the user by invalidating the provided refresh token.
   *
   * <p>This endpoint expects a refresh token as a request parameter and will invalidate it,
   * effectively signing the user out of the system.
   *
   * @param refreshToken the refresh token to be invalidated
   * @return an {@link ApiResponse} indicating successful sign out
   */
  @Operation(
      summary = "Sign out",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "SIGN_OUT_SUCCESS",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "INVALID_REFRESH_TOKEN",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class)))
      })
  @GetMapping("/sign-out")
  public ApiResponse<Void> signOut(
      @RequestHeader("Authorization") String bearerToken, @RequestParam String refreshToken) {
    String accessToken = bearerToken.substring("Bearer ".length());
    authService.signOut(accessToken, refreshToken);
    return ApiResponse.success(AuthMessages.SIGN_OUT_SUCCESS.toString());
  }

  /**
   * Handles user registration requests.
   *
   * <p>Receives a {@link SignUpDTO} containing user details, validates the input, and delegates the
   * registration process to the {@code authService}. Returns a success response if registration is
   * successful.
   *
   * @param signUpDTO the data transfer object containing user registration details
   * @return an {@link ApiResponse} indicating the result of the registration process
   */
  @Operation(
      summary = "Sign up",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "SIGN_UP_SUCCESS",
            content =
                @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema =
                        @io.swagger.v3.oas.annotations.media.Schema(
                            implementation = ApiResponse.class)))
      })
  @BadRequestApiResponse
  @PostMapping("/sign-up")
  public ApiResponse<Void> signUp(@Valid @RequestBody SignUpDTO signUpDTO) {
    authService.signUp(signUpDTO);
    return ApiResponse.success(AuthMessages.SIGN_UP_SUCCESS.toString());
  }

  private class DataApiResponseTokenDTO extends DataApiResponse<TokenDTO> {}

  private class DataApiResponseProfileDTO extends DataApiResponse<ProfileDTO> {}
}
