package com.vhskillpro.backend.common.responsev2;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Schema(description = "Standard API response")
public class ApiResponse<T> {
  @Schema(description = "Indicates whether the API call was successful", example = "true")
  private boolean success;

  @Schema(description = "HTTP status code of the response", example = "200")
  private int statusCode;

  @Schema(description = "Message describing the result of the API call", example = "Operation successful")
  private String message;
}
