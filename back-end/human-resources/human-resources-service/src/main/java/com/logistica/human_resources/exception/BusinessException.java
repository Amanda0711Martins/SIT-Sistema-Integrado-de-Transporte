// BusinessException.java
package com.logistica.human_resources.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}