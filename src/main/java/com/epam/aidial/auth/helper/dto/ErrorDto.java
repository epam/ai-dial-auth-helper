package com.epam.aidial.auth.helper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorDto {
    private String statusCode;

    @JsonProperty("status_code")
    private String message;

    public String getStatusCode() {
        return statusCode;
    }

    public ErrorDto setStatusCode(String statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ErrorDto setMessage(String message) {
        this.message = message;
        return this;
    }
}
