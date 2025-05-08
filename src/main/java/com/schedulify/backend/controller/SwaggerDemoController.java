package com.schedulify.backend.controller;

import com.schedulify.backend.model.dto.base.BaseApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
@Tag(name = "Demo", description = "Demo API to showcase Swagger documentation")
public class SwaggerDemoController {

    @Operation(summary = "Get a hello message", description = "Returns a hello message with the provided name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved message",
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = BaseApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid name supplied",
                    content = @Content)
    })
    @GetMapping("/hello")
    public ResponseEntity<BaseApiResponse<String>> getHello(
            @Parameter(description = "Name to greet")
            @RequestParam(required = false, defaultValue = "World") String name) {
        return ResponseEntity.ok(BaseApiResponse.success("Hello, " + name + "!"));
    }
} 