package com.vhskillpro.backend.exception;

import com.vhskillpro.backend.common.constants.MessageConstants;
import com.vhskillpro.backend.common.response.ApiResponse;
import com.vhskillpro.backend.common.response.BadRequestResponse;
import com.vhskillpro.backend.common.response.DataApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handles exceptions of type {@link AppException} thrown within the application.
   *
   * @param ex the {@link AppException} instance that was thrown
   * @return an {@link ApiResponse} containing information about the exception
   */
  @Hidden
  @ExceptionHandler(AppException.class)
  public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
    return ResponseEntity.status(ex.getStatusCode()).body(ApiResponse.from(ex));
  }

  /**
   * Handles exceptions thrown when method arguments fail validation.
   *
   * <p>This method is invoked when a {@link MethodArgumentNotValidException} is thrown, typically
   * due to validation errors on request parameters or body. It constructs a {@link
   * BadRequestResponse} from the exception and wraps it in a {@link DataApiResponse} with a bad
   * request status and a standard error message.
   *
   * @param ex the exception containing validation errors
   * @return a {@link DataApiResponse} containing the bad request details and error message
   */
  @Hidden
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public DataApiResponse<BadRequestResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    BadRequestResponse response = BadRequestResponse.from(ex);
    return DataApiResponse.badRequest(response, MessageConstants.BAD_REQUEST.getMessage());
  }

  /**
   * Handles {@link InsufficientAuthenticationException} thrown when a user attempts to access a
   * resource without sufficient authentication. Responds with HTTP 401 Unauthorized status and
   * returns an {@link ApiResponse} containing the exception message.
   *
   * @param ex the {@code InsufficientAuthenticationException} encountered
   * @return an {@code ApiResponse<Void>} indicating unauthorized access
   */
  @Hidden
  @ExceptionHandler(InsufficientAuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ApiResponse<Void> handleInsufficientAuthenticationException(
      InsufficientAuthenticationException ex) {
    return ApiResponse.unauthorized(ex.getMessage());
  }

  /**
   * Handles {@link AuthorizationDeniedException} thrown when a user is denied authorization.
   * Responds with HTTP 403 Forbidden status and returns an {@link ApiResponse} containing the
   * exception message.
   *
   * @param ex the {@link AuthorizationDeniedException} instance
   * @return an {@link ApiResponse} with forbidden status and the exception message
   */
  @Hidden
  @ExceptionHandler(AuthorizationDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ApiResponse<Void> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
    return ApiResponse.forbidden(ex.getMessage());
  }

  /**
   * Handles all uncaught exceptions thrown within the application. Logs the exception details and
   * returns a standardized internal server error response.
   *
   * @param ex the exception that was thrown
   * @return an {@link ApiResponse} indicating an internal server error
   */
  @io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "500",
      description = "INTERNAL_SERVER_ERROR",
      content =
          @io.swagger.v3.oas.annotations.media.Content(
              mediaType = "application/json",
              schema =
                  @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class)))
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ApiResponse<Void> handleException(Exception ex) {
    logger.error(MessageConstants.INTERNAL_SERVER_ERROR.getMessage(), ex);
    return ApiResponse.internalServerError(MessageConstants.INTERNAL_SERVER_ERROR.getMessage());
  }
}
