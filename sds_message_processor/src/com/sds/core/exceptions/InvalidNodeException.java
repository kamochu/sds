/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sds.core.exceptions;

/**
 *
 * @author kamochu
 */
public class InvalidNodeException extends Exception {

    public InvalidNodeException() {
        super("Invalid node id exception - node id provided does not exist in the system");
    }

    public InvalidNodeException(String message) {
        super(message);
    }

    public InvalidNodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidNodeException(Throwable cause) {
        super(cause);
    }

    public InvalidNodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
