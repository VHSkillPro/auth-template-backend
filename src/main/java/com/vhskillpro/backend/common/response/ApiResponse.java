package com.vhskillpro.backend.common.response;

import com.vhskillpro.backend.exception.AppException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Schema(description = "Standard API response")
public class ApiResponse<T> {
  @Schema(description = "Indicates whether the API call was successful")
  private boolean success;

  @Schema(description = "HTTP status code of the response")
  private int statusCode;

  @Schema(description = "Message describing the result of the API call")
  private String message;

  /**
   * Creates a successful {@link ApiResponse} with the given message and HTTP status 200 (OK).
   *
   * @param message the success message to include in the response
   * @return an {@code ApiResponse<Void>} indicating success
   */
  public static ApiResponse<Void> success(String message) {
    return ApiResponse.<Void>builder()
        .success(true)
        .statusCode(HttpStatus.OK.value())
        .message(message)
        .build();
  }

  /**
   * Creates an {@link ApiResponse} indicating a successful resource creation.
   *
   * @param message the message to include in the response
   * @return an {@code ApiResponse<Void>} with HTTP status 201 (Created) and the provided message
   */
  public static ApiResponse<Void> created(String message) {
    return ApiResponse.<Void>builder()
        .success(true)
        .statusCode(HttpStatus.CREATED.value())
        .message(message)
        .build();
  }

  /**
   * Creates an {@link ApiResponse} instance representing a failed response based on the provided
   * {@link AppException}.
   *
   * @param ex the {@link AppException} containing error details
   * @return an {@link ApiResponse} with success set to {@code false}, the status code and message
   *     from the exception, and a {@code null} data payload
   */
  public static ApiResponse<Void> from(AppException ex) {
    return ApiResponse.<Void>builder()
        .success(false)
        .statusCode(ex.getStatusCode().value())
        .message(ex.getMessage())
        .build();
  }

  /**
   * Creates an {@link ApiResponse} indicating an unauthorized request.
   *
   * @param message the message describing the unauthorized error
   * @return an {@link ApiResponse} with success set to false, HTTP status code 401 (Unauthorized),
   *     and the provided message
   */
  public static ApiResponse<Void> unauthorized(String message) {
    return ApiResponse.<Void>builder()
        .success(false)
        .statusCode(HttpStatus.UNAUTHORIZED.value())
        .message(message)
        .build();
  }

  /**
   * Creates an {@link ApiResponse} representing a forbidden (HTTP 403) response.
   *
   * @param message the message to include in the response
   * @return an {@link ApiResponse} with success set to false, status code 403, and the provided
   *     message
   */
  public static ApiResponse<Void> forbidden(String message) {
    return ApiResponse.<Void>builder()
        .success(false)
        .statusCode(HttpStatus.FORBIDDEN.value())
        .message(message)
        .build();
  }

  /**
   * Creates an {@link ApiResponse} representing an internal server error (HTTP 500).
   *
   * @param message the error message to include in the response
   * @return an {@code ApiResponse<Void>} with success set to {@code false}, status code 500, and
   *     the provided message
   */
  public static ApiResponse<Void> internalServerError(String message) {
    return ApiResponse.<Void>builder()
        .success(false)
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message(message)
        .build();
  }
}
