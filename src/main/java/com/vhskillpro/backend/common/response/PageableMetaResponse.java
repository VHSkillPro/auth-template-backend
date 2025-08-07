package com.vhskillpro.backend.common.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageableMetaResponse {
  private int page;
  private int size;
  private long total;
  private long pages;
}
