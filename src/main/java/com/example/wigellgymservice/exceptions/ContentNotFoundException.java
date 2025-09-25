package com.example.wigellgymservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class ContentNotFoundException extends RuntimeException {
    private final String object;

    public ContentNotFoundException(String object) {
        super(String.format("No %S found",object));
        this.object = object;
    }

    public String getObject() {
        return object;
    }
}
