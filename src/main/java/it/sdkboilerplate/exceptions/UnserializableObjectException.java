package it.sdkboilerplate.exceptions;

/**
 * Thrown when there are ReflectiveOperation errors computing serialization of an sdkObject
 */
public class UnserializableObjectException extends SdkException {
    public UnserializableObjectException(String message) {
        super(message);
    }
    public UnserializableObjectException(){}
}
