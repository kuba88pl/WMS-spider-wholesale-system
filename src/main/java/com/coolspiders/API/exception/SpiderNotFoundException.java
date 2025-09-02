package com.coolspiders.API.exception;

public class SpiderNotFoundException extends RuntimeException {
    public SpiderNotFoundException(String message) {
        super(message);
    }
}
