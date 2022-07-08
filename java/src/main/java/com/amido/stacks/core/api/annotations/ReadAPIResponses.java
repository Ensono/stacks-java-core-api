package com.amido.stacks.core.api.annotations;

import com.amido.stacks.core.api.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
    responseCode = "404",
    description = "Resource not found",
    content =
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(
    responseCode = "400",
    description = "Bad Request",
    content =
        @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)))
@SecurityRequirement(name = "bearerAuth")
public @interface ReadAPIResponses {}
