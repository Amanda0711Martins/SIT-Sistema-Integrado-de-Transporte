// ResourceNotFoundException.java
package com.logistica.human_resources.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}