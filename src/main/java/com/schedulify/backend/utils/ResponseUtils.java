package com.schedulify.backend.utils;

import com.schedulify.backend.model.dto.base.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtils {
    /**
     * Tạo ResponseEntity với ApiResponse chứa dữ liệu thành công
     *
     * @param data    dữ liệu trả về
     * @param message thông báo
     * @return ResponseEntity với ApiResponse
     */
    public static <T> ResponseEntity<ApiResponse<T>> ok(T data, String message) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .status(HttpStatus.OK.value())
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Tạo ResponseEntity với ApiResponse chứa dữ liệu thành công
     *
     * @param data    dữ liệu trả về
     * @return ResponseEntity với ApiResponse
     */
    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ok(data, "OK");
    }

    /**
     * Tạo ResponseEntity với ApiResponse chứa lỗi
     *
     * @param message thông báo lỗi
     * @param status  HttpStatus của lỗi
     * @return ResponseEntity với ApiResponse
     */
    public static <T> ResponseEntity<ApiResponse<T>> fail(String message, HttpStatus status) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .status(status.value())
                .data(null)
                .build();
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Tạo ResponseEntity với ApiResponse chứa lỗi
     *
     * @param message thông báo lỗi
     * @return ResponseEntity với ApiResponse
     */
    public static <T> ResponseEntity<ApiResponse<T>> fail(String message) {
        return fail(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Tạo ResponseEntity với ApiResponse chứa lỗi
     *
     * @param status HttpStatus của lỗi
     * @return ResponseEntity với ApiResponse
     */
    public static <T> ResponseEntity<ApiResponse<T>> fail(HttpStatus status) {
        return fail("Error", status);
    }
}
