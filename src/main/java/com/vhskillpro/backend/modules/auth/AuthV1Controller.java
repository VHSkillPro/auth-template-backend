package com.vhskillpro.backend.modules.auth;

import com.vhskillpro.backend.common.response.ApiResponse;
import com.vhskillpro.backend.common.response.DataApiResponse;
import com.vhskillpro.backend.modules.auth.dto.ResendVerificationEmailDTO;
import com.vhskillpro.backend.modules.auth.dto.SignInDTO;
import com.vhskillpro.backend.modules.auth.dto.TokenDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
      description = "Resends a verification email to the user with the specified email address.",
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Verification email resent successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Bad request, e.g., user already enabled or token already sent")
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
      description = "Verifies a user's email address using the provided token.",
      parameters = {
        @io.swagger.v3.oas.annotations.Parameter(
            name = "token",
            description = "The email verification token sent to the user's email address",
            required = true)
      },
      responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Email verified successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Bad request, e.g., invalid or expired token")
      })
  @GetMapping("/verify-email")
  public ApiResponse<Void> verifyEmail(@RequestParam String token) {
    authService.verifyEmail(token);
    return ApiResponse.success(AuthMessages.EMAIL_VERIFICATION_SUCCESS.getMessage());
  }
}
