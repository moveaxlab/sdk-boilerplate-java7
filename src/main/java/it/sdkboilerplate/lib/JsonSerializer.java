package it.sdkboilerplate.lib;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.sdkboilerplate.exceptions.MalformedSdkObjectException;
import it.sdkboilerplate.exceptions.UnserializableObjectException;
import it.sdkboilerplate.objects.SdkBodyType;
import it.sdkboilerplate.objects.SdkCollection;
import it.sdkboilerplate.objects.SdkObject;

import java.lang.reflect.InvocationTargetException;


@SuppressWarnings("unchecked")
public class JsonSerializer implements Serializer {
    /**
     * Serialized an SdkObject instance into its Json Representation
     *
     * @param bodyObject SdkObject to be serialized
     * @return String in Json Format
     * @throws UnserializableObjectException if the SdkObject is malformed
     */
    public String serialize(SdkBodyType bodyObject) throws UnserializableObjectException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (bodyObject instanceof SdkObject) {
                return mapper.writeValueAsString(bodyObject.getClass().getMethod("toHashMap").invoke(bodyObject));
            } else if (bodyObject instanceof SdkCollection) {
                return mapper.writeValueAsString(bodyObject.getClass().getMethod("toArray").invoke(bodyObject));
            } else {
                throw new UnserializableObjectException();
            }
        } catch (JsonProcessingException e) {
            throw new UnserializableObjectException();
        } catch (NoSuchMethodException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new MalformedSdkObjectException(e.getMessage());
        }
    }


}
