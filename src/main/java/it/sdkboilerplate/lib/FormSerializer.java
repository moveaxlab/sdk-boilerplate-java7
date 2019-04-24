package it.sdkboilerplate.lib;

import it.sdkboilerplate.exceptions.UnserializableObjectException;
import it.sdkboilerplate.objects.SdkBodyType;

import java.lang.reflect.Field;


public class FormSerializer implements Serializer {
    /**
     * Serializes an SdkObject into its form-urlencoded representation
     *
     * @param bodyObject SdkObject to be serialized
     * @return String in form-urlencoded format
     */
    public String serialize(SdkBodyType bodyObject) throws UnserializableObjectException {
        try {
            String urlEncode = "";
            Field[] attributes = bodyObject.getClass().getDeclaredFields();
            for (int i = 0; i < attributes.length; i++) {
                urlEncode += attributes[i].getName() + "=" + attributes[i].get(bodyObject);
                if (i < attributes.length - 1) {
                    urlEncode += "&";
                }
            }
            return urlEncode;
        } catch (IllegalAccessException e) {
            throw new UnserializableObjectException();
        }
    }

}
