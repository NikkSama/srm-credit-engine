package com.srm.credit.exception;

/** Raised when a referenced resource does not exist (maps to HTTP 404). */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
