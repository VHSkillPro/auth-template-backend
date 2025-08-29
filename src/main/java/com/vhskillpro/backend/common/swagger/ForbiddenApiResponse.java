package com.vhskillpro.backend.common.swagger;

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
    responseCode = "403",
    description = "FORBIDDEN",
    content =
        @Content(
            mediaType = "application/json",
            schema =
                @Schema(implementation = com.vhskillpro.backend.common.response.ApiResponse.class)))
public @interface ForbiddenApiResponse {}
