package it.sdkboilerplate.lib;

import it.sdkboilerplate.exceptions.UnknownContentTypeException;
import it.sdkboilerplate.http.MediaType;


public class SerializerFactory {

    public static Serializer make(String mediaType) throws UnknownContentTypeException {
        if (mediaType.equals(MediaType.APPLICATION_JSON)) return new JsonSerializer();
        if (mediaType.equals(MediaType.APPLICATION_FORM)) return new FormSerializer();
        throw new UnknownContentTypeException();

    }
}
