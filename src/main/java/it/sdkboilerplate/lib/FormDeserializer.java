package it.sdkboilerplate.lib;

import it.sdkboilerplate.exceptions.DeserializationException;
import it.sdkboilerplate.exceptions.UnknownBodyTypeException;
import it.sdkboilerplate.objects.SdkBodyType;


public class FormDeserializer implements Deserializer {
    @Override
    public SdkBodyType deserialize(String formSerialization, Class<? extends SdkBodyType> sdkObjectClass) throws UnknownBodyTypeException, DeserializationException {
        String jsonSerialization = this.paramsToJson(formSerialization);
        JsonDeserializer deserializer = new JsonDeserializer();
        return deserializer.deserialize(jsonSerialization, sdkObjectClass);
    }

    private String paramsToJson(String formSerialization) {
        formSerialization = formSerialization.replaceAll("=", "\":\"");
        formSerialization = formSerialization.replaceAll("&", "\",\"");
        return "{\"" + formSerialization + "\"}";
    }

}
