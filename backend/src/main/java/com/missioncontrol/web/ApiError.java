package com.missioncontrol.web;

import java.time.Instant;
import java.util.List;

public record ApiError(
        int status,
        String error,
        String message,
        List<FieldError> fieldErrors,
        Instant timestamp
) {

    public static ApiError of(int status, String error, String message) {
        return new ApiError(status, error, message, List.of(), Instant.now());
    }

    public static ApiError of(int status, String error, String message, List<FieldError> fieldErrors) {
        return new ApiError(status, error, message, fieldErrors, Instant.now());
    }

    public record FieldError(String field, String message) {
    }
}
