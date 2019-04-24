package it.sdkboilerplate.http;

import it.sdkboilerplate.exceptions.DeserializationException;
import it.sdkboilerplate.exceptions.UnknownBodyTypeException;
import it.sdkboilerplate.exceptions.UnknownContentTypeException;
import it.sdkboilerplate.lib.Deserializer;
import it.sdkboilerplate.lib.DeserializerFactory;

import it.sdkboilerplate.objects.SdkBodyType;

import java.util.HashMap;

public class SdkResponse {
    public Integer getStatusCode() {
        return this.statusCode;
    }

    public String getRawBody() {
        return this.body;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    private Integer statusCode;
    private String body;
    private HashMap<String, String> headers;

    public SdkResponse(Integer statusCode, String body, HashMap<String, String> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    /**
     * @return True iff the response status code is in the range 200-299
     */
    public boolean isFailed() {
        return 200 > this.statusCode || this.statusCode > 299;
    }

    /**
     * Formats the raw response body into the appropriate object class
     *
     * @param sdkObjectClass Class of the sdkObject
     * @param <T> type of the parsed object
     * @return SdkObject instance
     * @throws UnknownContentTypeException When the response content type is not handled by the sdk
     * @throws UnknownBodyTypeException When then type of the class is malformed or unknown
     * @throws DeserializationException On response body de-serialization errors
     */
    public <T extends SdkBodyType> T format(Class<? extends SdkBodyType> sdkObjectClass) throws UnknownContentTypeException, UnknownBodyTypeException, DeserializationException {
        if (sdkObjectClass == null) {
            return null;
        }
        Deserializer deserializer = DeserializerFactory.make(this.headers.get(Headers.CONTENT_TYPE));
        return (T) deserializer.deserialize(this.body, sdkObjectClass);

    }
}