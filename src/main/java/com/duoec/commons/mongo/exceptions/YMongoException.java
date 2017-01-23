package com.duoec.commons.mongo.exceptions;

/**
 * Created by ycoe on 16/3/18.
 */
public class YMongoException extends RuntimeException {
    public YMongoException(Exception e) {
        super(e);
    }

    public YMongoException(String message) {
        super(message);
    }

    public YMongoException(String message, Exception e) {
        super(message, e);
    }
}
