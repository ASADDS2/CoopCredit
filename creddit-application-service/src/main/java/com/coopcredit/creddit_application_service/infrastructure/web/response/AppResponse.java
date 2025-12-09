package com.coopcredit.creddit_application_service.infrastructure.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> AppResponse<T> success(T data) {
        return new AppResponse<>(true, "Operation completed successfully", data, LocalDateTime.now());
    }

    public static <T> AppResponse<T> success(String message, T data) {
        return new AppResponse<>(true, message, data, LocalDateTime.now());
    }

    public static <T> AppResponse<T> error(String message) {
        return new AppResponse<>(false, message, null, LocalDateTime.now());
    }
}
