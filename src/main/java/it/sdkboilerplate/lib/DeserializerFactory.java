package it.sdkboilerplate.lib;

import it.sdkboilerplate.exceptions.UnknownContentTypeException;
import it.sdkboilerplate.http.MediaType;

public class DeserializerFactory {
    public static Deserializer make(String mediaType) throws UnknownContentTypeException {
        if (mediaType.equals(MediaType.APPLICATION_JSON)) return new JsonDeserializer();
        if (mediaType.equals(MediaType.APPLICATION_FORM)) return new FormDeserializer();
        throw new UnknownContentTypeException();
    }

}
