package com.srm.credit.exception;

/** Raised when a business rule is violated (maps to HTTP 422). */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
