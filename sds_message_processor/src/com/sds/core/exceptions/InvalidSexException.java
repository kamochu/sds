package com.sds.core.exceptions;

/**
 *
 * @author Samuel Kamochu
 */
public class InvalidSexException extends Exception {

    public InvalidSexException() {
        this("Invalid Sex");
    }

    public InvalidSexException(String message) {
        super(message);
    }

    public InvalidSexException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSexException(Throwable cause) {
        super(cause);
    }

    public InvalidSexException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
