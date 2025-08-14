package com.vhskillpro.backend.common.response;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "API response containing a single data item")
public class DataApiResponse<T> extends ApiResponse<T> {
  @Schema(description = "Data returned by the API call")
  private T data;

  /**
   * Creates a successful {@link DataApiResponse} instance with the provided data
   * and message.
   *
   * @param <T>     the type of the response data
   * @param data    the data to include in the response
   * @param message a message describing the success
   * @return a {@code DataApiResponse} object representing a successful response
   */
  public static <T> DataApiResponse<T> success(T data, String message) {
    return DataApiResponse.<T>builder()
        .success(true)
        .statusCode(HttpStatus.OK.value())
        .message(message)
        .data(data)
        .build();
  }

  /**
   * Creates a {@link DataApiResponse} representing a bad request (HTTP 400)
   * response.
   *
   * @param data    the {@link BadRequestResponse} payload containing details
   *                about the bad request
   * @param message a descriptive message explaining the reason for the bad
   *                request
   * @return a {@link DataApiResponse} with success set to false, status code 400,
   *         the provided message, and data
   */
  public static DataApiResponse<BadRequestResponse> badRequest(BadRequestResponse data, String message) {
    return DataApiResponse.<BadRequestResponse>builder()
        .success(false)
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .message(message)
        .data(data)
        .build();
  }
}
