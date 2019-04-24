package it.sdkboilerplate.callbacks;

import it.sdkboilerplate.exceptions.*;
import it.sdkboilerplate.lib.ApiContext;
import it.sdkboilerplate.lib.JsonDeserializer;
import it.sdkboilerplate.objects.SdkObject;

import java.util.HashMap;

/**
 * Abstract Callback Handler Class used to handle callbacks. It provides a parse method which converts the raw body
 * of the callback request into an sdk object.
 * Callback Verification and callback namespace retrieval from the raw request body must be implemented
 */
public abstract class CallbackHandler {
    public ApiContext ctx;

    public CallbackHandler(ApiContext ctx) {
        this.ctx = ctx;
    }

    public abstract String getCallbackNamespace(byte[] rawBody) throws CallbackParsingException;

    /**
     * Verifies the callback request against the custom protocol of the server sending the callbacks.
     *
     * @param headers The headers of the request
     * @param rawBody The raw request body
     * @throws CallbackParsingException On callback request parsing errors
     * @throws CallbackVerificationException On any error during the signature verification
     */
    public abstract void verify(HashMap<String, String> headers, byte[] rawBody) throws CallbackParsingException, CallbackVerificationException;

    /**
     * Method to map callback namespaces and SdkObject classes which represent the body of the callback requests
     *
     * @return An HashMap mapping callback namespaces and SdkObject classes
     */
    public abstract HashMap<String, Class<? extends SdkObject>> getCallbacks();

    /**
     * Verifies the callback request and deserializes the rawBody into the appropriate sdk object
     *
     * @param headers Headers of the callback request
     * @param rawBody RawBody of the callback request
     * @return SdkObject instance mapped with the callback namespace
     * @throws DeserializationException On errors de-serializing the object contained in the callback request body
     * @throws CallbackParsingException On callback request parsing errors
     * @throws CallbackVerificationException On any error during the signature verification
     * @throws UnknownCallbackTypeException When the callback namespace extracted from the body is undefined
     */
    public SdkObject parse(HashMap<String, String> headers, byte[] rawBody)
            throws UnknownCallbackTypeException, CallbackVerificationException, CallbackParsingException, DeserializationException {
        this.verify(headers, rawBody);
        String callbackNamespace = this.getCallbackNamespace(rawBody);
        JsonDeserializer deserializer = new JsonDeserializer();
        try {
            return (SdkObject) deserializer.deserialize(new String(rawBody), this.getCallbacks().get(callbackNamespace));
        } catch (UnknownBodyTypeException e) {
            throw new UnknownCallbackTypeException();
        }
    }
}
