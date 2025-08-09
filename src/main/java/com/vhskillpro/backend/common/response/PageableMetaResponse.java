package com.vhskillpro.backend.common.response;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Pagination metadata for the response")
public class PageableMetaResponse {
  @Schema(description = "Current page number", example = "0")
  private int page;

  @Schema(description = "Size of the page", example = "20")
  private int size;

  @Schema(description = "Total number of elements", example = "100")
  private long total;

  @Schema(description = "Total number of pages", example = "5")
  private long pages;

  @Schema(description = "Sort order applied to the results", example = "name,asc")
  private String sort;

  /**
   * Creates a {@link PageableMetaResponse} instance from a given {@link Page}
   * object.
   *
   * @param page the {@link Page} object containing pagination information
   * @return a {@link PageableMetaResponse} populated with page number, size,
   *         total elements, total pages, and sort order
   */
  public static PageableMetaResponse from(Page<?> page) {
    return PageableMetaResponse.builder()
        .page(page.getNumber())
        .size(page.getSize())
        .total(page.getTotalElements())
        .pages(page.getTotalPages())
        .sort(page.getSort().toString())
        .build();
  }
}
