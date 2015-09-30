package com.sds.core.exceptions;

/**
 *
 * @author Samuel Kamochu
 */
public class InvalidNumberOfParametersException extends Exception {

    public InvalidNumberOfParametersException() {
        this("Invalid Number of Parameters");
    }

    public InvalidNumberOfParametersException(String message) {
        super(message);
    }

    public InvalidNumberOfParametersException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidNumberOfParametersException(Throwable cause) {
        super(cause);
    }

    public InvalidNumberOfParametersException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
