package com.vhskillpro.backend.common.response;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageableMetaResponse {
  private int page;
  private int size;
  private long total;
  private long pages;
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
