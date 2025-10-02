package com.example.wigellgymservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class ResourceNotFoundException extends RuntimeException {
    private final String object;
    private final String field;
    private final Object value;

    public ResourceNotFoundException(String object, String field, Object value) {
        super(String.format("No %s with %s [%s] found", object, field, value));
        this.object = object;
        this.field = field;
        this.value = value;
    }

    public String getObject() {
        return object;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
}
