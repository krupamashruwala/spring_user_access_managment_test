package com.sample.demoSample.util.error;


public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 8550229767513365271L;

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(final String message) {
        super(message);
    }

    public UserNotFoundException(final Throwable cause) {
        super(cause);
    }
}