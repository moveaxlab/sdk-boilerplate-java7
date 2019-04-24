package it.sdkboilerplate.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.sdkboilerplate.exceptions.JsonSerializationException;

import java.io.IOException;

public class Json {
    private final static ObjectMapper mapper = new ObjectMapper();

    public static String dump(Object obj) throws JsonSerializationException {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException();
        }
    }

    public static Object load(String jsonString, Class<?> targetClass) throws JsonSerializationException {
        try {
            return mapper.readValue(jsonString, targetClass);
        } catch (IOException e) {
            throw new JsonSerializationException();
        }
    }
}
