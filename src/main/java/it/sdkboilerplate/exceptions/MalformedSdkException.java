package it.sdkboilerplate.exceptions;

/**
 * Thrown on Reflective operations exceptions regarding custom sdk exception classes
 */
public class MalformedSdkException extends RuntimeException {
    public MalformedSdkException() {
    }

    public MalformedSdkException(String message) {
        super(message);
    }

}
