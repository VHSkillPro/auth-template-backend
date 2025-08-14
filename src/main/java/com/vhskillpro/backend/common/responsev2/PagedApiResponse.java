package com.vhskillpro.backend.common.responsev2;

import java.util.List;

import org.springframework.data.domain.Page;
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
@Schema(description = "API response containing a paginated list of data items")
public class PagedApiResponse<T> extends ApiResponse<T> {
  @Schema(description = "Data returned by the API call")
  private List<T> data;

  @Schema(description = "Pagination metadata")
  private PageableMetaResponse meta;

  /**
   * Creates a successful {@link PagedApiResponse} containing paginated data and
   * metadata.
   *
   * @param <T>     the type of elements in the page
   * @param page    the {@link Page} object containing the data and pagination
   *                information
   * @param message a message describing the response
   * @return a {@link PagedApiResponse} instance representing a successful
   *         paginated response
   */
  public static <T> PagedApiResponse<T> success(Page<T> page, String message) {
    return PagedApiResponse.<T>builder()
        .success(true)
        .statusCode(HttpStatus.OK.value())
        .message(message)
        .data(page.getContent())
        .meta(PageableMetaResponse.from(page))
        .build();
  }
}
