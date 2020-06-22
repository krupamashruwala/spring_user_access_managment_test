package com.sample.demoSample.util.error;

public final class GenericException extends RuntimeException {

    private static final long serialVersionUID = 7671328062161712846L;

    public GenericException() {
        super();
    }

    public GenericException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public GenericException(final String message) {
        super(message);
    }

    public GenericException(final Throwable cause) {
        super(cause);
    }
}
