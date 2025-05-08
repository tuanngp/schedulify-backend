package com.schedulify.backend.controller;

import com.schedulify.backend.model.dto.base.BaseApiResponse;
import com.schedulify.backend.service.BaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
public abstract class BaseController<T, ID, S extends BaseService<T, ID>> {

    protected final S service;

    protected BaseController(S service) {
        this.service = service;
    }

    @Operation(summary = "Get all resources", description = "Returns a list of all resources")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<BaseApiResponse<List<T>>> getAll() {
        List<T> entities = service.findAll();
        return ResponseEntity.ok(BaseApiResponse.success(entities));
    }

    @Operation(summary = "Get paginated resources", description = "Returns a paginated list of resources")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated list"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/page")
    public ResponseEntity<BaseApiResponse<List<T>>> getAllPaginated(
            @Parameter(description = "Pagination information") Pageable pageable) {
        Page<T> pagedResult = service.findAll(pageable);
        return ResponseEntity.ok(
                BaseApiResponse.pagination(
                        pagedResult.getContent(),
                        pagedResult.getNumber(),
                        pagedResult.getSize(),
                        pagedResult.getTotalElements(),
                        pagedResult.getTotalPages()
                )
        );
    }

    @Operation(summary = "Get resource by ID", description = "Returns a single resource by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved resource"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BaseApiResponse<T>> getById(
            @Parameter(description = "ID of the resource to retrieve", required = true) 
            @PathVariable ID id) {
        return service.findById(id)
                .map(entity -> ResponseEntity.ok(BaseApiResponse.success(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(BaseApiResponse.error("Resource not found with id: " + id, HttpStatus.NOT_FOUND)));
    }

    @Operation(summary = "Create new resource", description = "Creates a new resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Resource created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<BaseApiResponse<T>> create(
            @Parameter(description = "Resource to create", required = true) 
            @RequestBody T entity) {
        T savedEntity = service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseApiResponse.created(savedEntity));
    }

    @Operation(summary = "Update resource", description = "Updates an existing resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource updated successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BaseApiResponse<T>> update(
            @Parameter(description = "ID of the resource to update", required = true) 
            @PathVariable ID id,
            @Parameter(description = "Updated resource", required = true) 
            @RequestBody T entity) {
        T updatedEntity = service.update(id, entity);
        return ResponseEntity.ok(BaseApiResponse.success(updatedEntity, "Updated successfully"));
    }

    @Operation(summary = "Delete resource", description = "Deletes a resource by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resource deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseApiResponse<Void>> delete(
            @Parameter(description = "ID of the resource to delete", required = true) 
            @PathVariable ID id) {
        service.deleteById(id);
        return ResponseEntity.ok(BaseApiResponse.success(null, "Deleted successfully"));
    }
}