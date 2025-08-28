package com.vhskillpro.backend.common.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class PagedApiResponse<T> extends ApiResponse<T> {
  private List<T> data;
  private PageableMetaResponse meta;

  /**
   * Creates a successful {@link PagedApiResponse} containing paginated data and metadata.
   *
   * @param <T> the type of elements in the page
   * @param page the {@link Page} object containing the data and pagination information
   * @param message a message describing the response
   * @return a {@link PagedApiResponse} instance representing a successful paginated response
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
