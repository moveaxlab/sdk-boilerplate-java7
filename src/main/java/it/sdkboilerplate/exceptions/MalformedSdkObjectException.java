package it.sdkboilerplate.exceptions;

/**
 * Thrown on reflective operations exceptions regaring malformed Sdk Object classes
 */
public class MalformedSdkObjectException extends RuntimeException {
    public MalformedSdkObjectException() {
    }

    public MalformedSdkObjectException(String message) {
        super(message);
    }
}
