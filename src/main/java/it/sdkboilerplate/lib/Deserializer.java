package it.sdkboilerplate.lib;

import it.sdkboilerplate.exceptions.DeserializationException;
import it.sdkboilerplate.exceptions.UnknownBodyTypeException;
import it.sdkboilerplate.objects.SdkBodyType;

/**
 * Interface which allows the deserialization of raw serialized strings into SdkType instances
 */
public interface Deserializer {
    SdkBodyType deserialize(String serialized, Class<? extends SdkBodyType> sdkObjectClass) throws UnknownBodyTypeException, DeserializationException;

}
