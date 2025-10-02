package com.example.wigellgymservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class ContentNotFoundException extends RuntimeException {
    private final String reason;

    public ContentNotFoundException(String reason) {
        super(String.format("No %s found",reason));
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
