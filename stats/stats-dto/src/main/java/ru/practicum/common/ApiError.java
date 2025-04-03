package ru.practicum.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@RequiredArgsConstructor
@SuperBuilder(toBuilder = true)
public class ApiError {
    private List<StackTraceElement> errors;
    private String message;
    private String reason;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private HttpStatus status;

    public ApiError(HttpStatus httpStatus, Throwable e) {
        this.status = httpStatus;
        this.errors = Arrays.stream(e.getStackTrace()).toList();
        this.message = e.toString();
        this.reason = e.getMessage();
        this.timestamp = LocalDateTime.now();
    }
}