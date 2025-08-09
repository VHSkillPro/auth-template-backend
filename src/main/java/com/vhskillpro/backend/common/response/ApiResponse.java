package com.vhskillpro.backend.common.response;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vhskillpro.backend.exception.AppException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response structure")
public class ApiResponse<T> {
  @Schema(description = "Indicates whether the API call was successful", example = "true")
  private boolean success;

  @Schema(description = "HTTP status code of the response", example = "200")
  private int statusCode;

  @Schema(description = "Message describing the result of the API call", example = "Operation successful")
  private String message;

  @Schema(description = "Data returned by the API call")
  private T data;

  @Schema(description = "Pagination metadata for the response")
  private PageableMetaResponse meta;

  /**
   * Creates an {@link ApiResponse} instance representing a failed operation based
   * on the provided {@link AppException}.
   *
   * @param ex the {@link AppException} containing error details
   * @return an {@link ApiResponse} representing the error
   */
  public static ApiResponse<Void> from(AppException ex) {
    return ApiResponse.<Void>builder()
        .success(false)
        .statusCode(ex.getStatusCode().value())
        .message(ex.getMessage())
        .data(null)
        .meta(null)
        .build();
  }

  /**
   * Creates a successful {@link ApiResponse} with a custom message and no data
   * payload.
   *
   * @param message the success message to include in the response
   * @return an {@code ApiResponse<Void>} instance indicating success, with HTTP
   *         status 200 and the provided message
   */
  public static ApiResponse<Void> success(String message) {
    return ApiResponse.<Void>builder()
        .success(true)
        .statusCode(HttpStatus.OK.value())
        .message(message)
        .data(null)
        .meta(null)
        .build();
  }

  /**
   * Creates a successful {@link ApiResponse} instance with the provided data and
   * message.
   *
   * @param <T>     the type of the response data
   * @param data    the data to include in the response
   * @param message a message describing the success
   * @return an {@code ApiResponse} object representing a successful response
   */
  public static <T> ApiResponse<T> success(T data, String message) {
    return ApiResponse.<T>builder()
        .success(true)
        .statusCode(HttpStatus.OK.value())
        .message(message)
        .data(data)
        .meta(null)
        .build();
  }

  /**
   * Creates a successful {@link ApiResponse} containing a paginated list of data
   * and an optional message.
   *
   * @param <T>     the type of elements in the page
   * @param data    the {@link Page} containing the data to be returned
   * @param message a message describing the response
   * @return an {@link ApiResponse} with the list of items, pagination metadata,
   *         and success status
   */
  public static <T> ApiResponse<List<T>> success(Page<T> data, String message) {
    return ApiResponse.<List<T>>builder()
        .success(true)
        .statusCode(HttpStatus.OK.value())
        .message(message)
        .data(data.getContent())
        .meta(PageableMetaResponse.from(data))
        .build();
  }

  /**
   * Creates an {@link ApiResponse} representing an internal server error (HTTP
   * 500).
   *
   * @param message the error message to include in the response
   * @return an {@code ApiResponse<Void>} with success set to {@code false},
   *         status code 500,
   *         the provided message, and {@code null} data and meta fields
   */
  public static ApiResponse<Void> internalServerError(String message) {
    return ApiResponse.<Void>builder()
        .success(false)
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message(message)
        .data(null)
        .meta(null)
        .build();
  }
}
