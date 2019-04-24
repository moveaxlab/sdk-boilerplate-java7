package it.sdkboilerplate.exceptions;

/**
 * Exception thrown on malformed Hook subclasses definitions
 */
public class MalformedHookException extends SdkException {
    public MalformedHookException(String message) {
        super(message);
    }

    public MalformedHookException() {
    }
}
