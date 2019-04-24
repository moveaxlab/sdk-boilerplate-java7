package it.sdkboilerplate.lib;

import it.sdkboilerplate.exceptions.UnserializableObjectException;
import it.sdkboilerplate.objects.SdkBodyType;

/**
 * Interface which allows the serialization of Sdk objects into raw strings
 */
public interface Serializer {
    String serialize(SdkBodyType sdkObject) throws UnserializableObjectException;

}
