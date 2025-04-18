package com.schedulify.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> errors = new HashMap<>();

    public ValidationErrorResponse(int status, String message) {
        super(status, message);
    }

    public void addValidationError(String field, String message) {
        errors.put(field, message);
    }
}

