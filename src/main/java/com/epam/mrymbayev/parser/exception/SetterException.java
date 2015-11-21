package com.epam.mrymbayev.parser.exception;

public class SetterException extends Exception {
    public SetterException() {
        super();
    }

    public SetterException(String message) {
        super(message);
    }

    public SetterException(String message, Throwable cause) {
        super(message, cause);
    }

    public SetterException(Throwable cause) {
        super(cause);
    }
}
