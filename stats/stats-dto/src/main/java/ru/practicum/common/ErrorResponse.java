package ru.practicum.common;

public class ErrorResponse {
    private final String error;

    public ErrorResponse(final String error) {
        this.error = error;
    }

    public String getError() {
        return this.error;
    }
}