package com.duoec.commons.mongo.exceptions;

/**
 * Created by ycoe on 16/3/18.
 */
public class ReflenctionException extends RuntimeException {
    public ReflenctionException(String message) {
        super(message);
    }

    public ReflenctionException(String message, Exception ex) {
        super(message, ex);
    }
}
