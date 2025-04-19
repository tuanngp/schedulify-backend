package com.schedulify.backend.controller;

import com.schedulify.backend.model.dto.base.ApiResponse;
import com.schedulify.backend.service.BaseService;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<T>>> getAll() {
        List<T> entities = service.findAll();
        return ResponseEntity.ok(ApiResponse.success(entities));
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<List<T>>> getAllPaginated(Pageable pageable) {
        Page<T> pagedResult = service.findAll(pageable);
        return ResponseEntity.ok(
                ApiResponse.pagination(
                        pagedResult.getContent(),
                        pagedResult.getNumber(),
                        pagedResult.getSize(),
                        pagedResult.getTotalElements(),
                        pagedResult.getTotalPages()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<T>> getById(@PathVariable ID id) {
        return service.findById(id)
                .map(entity -> ResponseEntity.ok(ApiResponse.success(entity)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Resource not found with id: " + id, HttpStatus.NOT_FOUND)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<T>> create(@RequestBody T entity) {
        T savedEntity = service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(savedEntity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<T>> update(@PathVariable ID id, @RequestBody T entity) {
        T updatedEntity = service.update(id, entity);
        return ResponseEntity.ok(ApiResponse.success(updatedEntity, "Updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable ID id) {
        service.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted successfully"));
    }
}