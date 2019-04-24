package it.sdkboilerplate.exceptions;

/**
 * Sdk Generic Exception Base Class
 */
public abstract class SdkException extends Exception {
    public SdkException(String message) {
        super(message);
    }

    public SdkException() {
        super();
    }
}
