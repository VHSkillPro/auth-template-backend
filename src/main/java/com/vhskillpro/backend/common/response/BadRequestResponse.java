package com.vhskillpro.backend.common.response;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.MethodArgumentNotValidException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response for bad request errors")
public class BadRequestResponse {
  @Schema(description = "Map of field errors where the key is the field name and the value is the error message", example = "{\"username\": \"Username is required\", \"email\": \"Email format is invalid\"}")
  private Map<String, String> errors;

  /**
   * Creates a {@link BadRequestResponse} from a
   * {@link MethodArgumentNotValidException}.
   * <p>
   * Extracts field errors from the exception and collects them into a map where
   * the key is the field name
   * and the value is the corresponding error message. If multiple errors exist
   * for the same field, their messages
   * are concatenated with a semicolon.
   *
   * @param ex the {@link MethodArgumentNotValidException} containing validation
   *           errors
   * @return a {@link BadRequestResponse} populated with the extracted field
   *         errors
   */
  public static BadRequestResponse from(MethodArgumentNotValidException ex) {
    Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(
            error -> error.getField(),
            error -> error.getDefaultMessage(),
            (existing, replacement) -> existing + ";" + replacement));

    return BadRequestResponse.builder()
        .errors(errors)
        .build();
  }
}
