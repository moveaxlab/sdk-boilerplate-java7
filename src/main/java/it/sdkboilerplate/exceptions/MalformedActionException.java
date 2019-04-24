package it.sdkboilerplate.exceptions;

/**
 * Thrown on reflective operation exceptions regarding Sdk Action classes
 */
public class MalformedActionException extends RuntimeException {
    public MalformedActionException() {
    }

    public MalformedActionException(String message) {
        super(message);
    }
}
