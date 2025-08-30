package com.vhskillpro.backend.common.swagger;

import com.vhskillpro.backend.common.response.BadRequestResponse;
import com.vhskillpro.backend.common.response.DataApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
    responseCode = "400",
    description = "BAD_REQUEST",
    content =
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = BadRequestDataApiResponse.class)))
public @interface BadRequestApiResponse {}

class BadRequestDataApiResponse extends DataApiResponse<BadRequestResponse> {}
