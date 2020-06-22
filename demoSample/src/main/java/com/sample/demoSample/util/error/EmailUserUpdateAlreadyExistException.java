package com.sample.demoSample.util.error;

public final class EmailUserUpdateAlreadyExistException extends RuntimeException {

    private static final long serialVersionUID = 7697878062161712846L;

    public EmailUserUpdateAlreadyExistException() {
        super();
    }

    public EmailUserUpdateAlreadyExistException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EmailUserUpdateAlreadyExistException(final String message) {
        super(message);
    }

    public EmailUserUpdateAlreadyExistException(final Throwable cause) {
        super(cause);
    }
}
