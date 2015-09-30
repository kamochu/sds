package com.sds.core.exceptions;

/**
 *
 * @author Samuel Kamochu
 */
public class InvalidAgeException extends Exception {

    public InvalidAgeException() {
        this("Invalid Age");
    }

    public InvalidAgeException(String message) {
        super(message);
    }

    public InvalidAgeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAgeException(Throwable cause) {
        super(cause);
    }

    public InvalidAgeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
