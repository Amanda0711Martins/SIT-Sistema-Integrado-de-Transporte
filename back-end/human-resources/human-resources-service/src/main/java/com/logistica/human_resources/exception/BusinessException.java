// BusinessException.java
package com.logistics.HumanResources.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}