package it.sdkboilerplate.actions;

import it.sdkboilerplate.exceptions.*;
import it.sdkboilerplate.hooks.FailureHook;
import it.sdkboilerplate.hooks.Hook;
import it.sdkboilerplate.hooks.PreSendHook;
import it.sdkboilerplate.hooks.SuccessHook;
import it.sdkboilerplate.http.Headers;
import it.sdkboilerplate.http.SdkRequest;
import it.sdkboilerplate.http.SdkResponse;
import it.sdkboilerplate.http.agents.UserAgent;
import it.sdkboilerplate.http.agents.UserAgentFactory;
import it.sdkboilerplate.lib.ApiContext;
import it.sdkboilerplate.lib.Serializer;
import it.sdkboilerplate.lib.SerializerFactory;
import it.sdkboilerplate.objects.SdkBodyType;
import it.sdkboilerplate.objects.SdkCollection;
import it.sdkboilerplate.objects.SdkObject;
import it.sdkboilerplate.validation.Schema;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstract Action class. Subclasses must defines getters for elements which will be used to
 * build http requests
 */
@SuppressWarnings({"FieldCanBeLocal"})
public abstract class Action {

    private ApiContext ctx;
    private HashMap<String, String> routeParameters;
    private HashMap<String, String> queryParameters;
    private SdkBodyType requestBody;

    public ApiContext getContext() {
        return this.ctx;
    }

    public abstract ArrayList<Class<? extends FailureHook>> getFailureHooks();

    public abstract ArrayList<Class<? extends SuccessHook>> getSuccessHooks();

    public abstract ArrayList<Class<? extends PreSendHook>> getPreSendHooks();

    public void setRouteParameters(HashMap<String, String> routeParameters) {
        this.validateRouteParams(routeParameters);
        this.routeParameters = routeParameters;
    }

    public void setQueryParameters(HashMap<String, String> queryParameters) {
        this.validateQueryParams(queryParameters);
        this.queryParameters = queryParameters;
    }

    public void setRequestBody(SdkBodyType requestBody) throws UnserializableObjectException {
        this.validateRequestBody(requestBody);
        this.requestBody = requestBody;
    }

    public void setRouteParameter(String key, String value) {
        this.routeParameters.put(key, value);
    }

    public void setQueryParameter(String key, String value) {
        this.queryParameters.put(key, value);
    }

    private HashMap<String, String> getRouteParameters() {
        return this.routeParameters;
    }

    private HashMap<String, String> getQueryParameters() {
        return this.queryParameters;
    }

    private SdkBodyType getRequestBody() {
        return this.requestBody;
    }

    /**
     * @return Route of the http request
     */
    public abstract String getRoute();

    /**
     * @return Verb of the http request (GET,PUT,PATCH,POST,DELETE)
     */
    public abstract String getVerb();

    /**
     * @return SdkObject | SdkCollection subclass
     */
    public abstract Class<? extends SdkBodyType> getRequestBodyClass();

    /**
     * @return SdkObject | SdkCollection subclass
     */
    public abstract Class<? extends SdkBodyType> getResponseBodyClass();

    /**
     * @return Mapping between error codes and exception classes
     */
    public abstract HashMap<String, Class<? extends SdkHttpException>> getErrors();

    /**
     * @return Validation Schema for route parameters
     */
    public abstract Schema getRouteParametersSchema();

    /**
     * @return Validation Schema for query parameters
     */
    public abstract Schema getQueryParametersSchema();

    /**
     * @return Headers to be appended to the request
     */
    public abstract HashMap<String, String> getHeaders();

    /**
     * Validates the SdkObject passed as request body
     *
     * @param body Body of the request
     */
    private void validateRequestBody(SdkBodyType body) throws UnserializableObjectException {
        Class<? extends SdkBodyType> requestBodyClass = this.getRequestBodyClass();
        try {
            Method getSchema = requestBodyClass.getMethod("getSchema");
            Method serializationMethod = null;
            if (body instanceof SdkObject) {
                serializationMethod = requestBodyClass.getMethod("toHashMap");
            } else if (body instanceof SdkCollection) {
                serializationMethod = requestBodyClass.getMethod("toArray");
            } else {
                throw new UnserializableObjectException();
            }

            this.validate(serializationMethod.invoke(body), (Schema) getSchema.invoke(body));
        } catch (NoSuchMethodException exc) {
            throw new UnserializableObjectException(exc.getMessage());
        } catch (IllegalAccessException exc) {
            throw new UnserializableObjectException(exc.getMessage());
        } catch (InvocationTargetException exc) {
            throw new UnserializableObjectException(exc.getMessage());
        }
    }

    /**
     * Validates the query parameters map
     *
     * @param queryParameters HashMap representing query parameters of the request
     */
    private void validateQueryParams(HashMap<String, String> queryParameters) {
    }

    /**
     * Validates the route parameters map
     *
     * @param routeParameters HashMap representing route parameters of the request
     */
    private void validateRouteParams(HashMap<String, String> routeParameters) {
    }

    /**
     * Validates an hashMap against an hashMap schema
     *
     * @param value  Value being validated
     * @param schema Validation Schema
     */
    private <T> void validate(T value, Schema schema) {
    }

    /**
     * Returns the key which will be used to extract the error_code from the sdkResponse
     *
     * @param sdkResponse SdkResponse object
     * @return Error Key
     */
    public String getExceptionKey(SdkResponse sdkResponse) {
        return sdkResponse.getStatusCode().toString();
    }

    /**
     * Returns the SdkException to throw based on the error code extracted from the sdkResponse
     *
     * @param sdkResponse Http sdkResponse
     * @return SdkHttpException class to be thrown
     */
    private Class<? extends SdkHttpException> getException(SdkResponse sdkResponse) throws UnknownHttpException {

        Class<? extends SdkHttpException> exceptionClass = this.getErrors().get(this.getExceptionKey(sdkResponse));
        if (exceptionClass == null) {
            throw new UnknownHttpException();
        }
        return exceptionClass;

    }

    /**
     * Creates the route substituting route parameters
     *
     * @throws SdkException when route parameters are missing
     */
    private String buildRoute() throws SdkException {
        return StrSubstitutor.replace(this.getRoute(), this.getRouteParameters(), "{", "}");
    }


    /**
     * Runs a given array of hooks
     *
     * @param hooks Hooks to be run
     */
    private void runHooks(ArrayList<Hook> hooks) {
        for (Hook hook : hooks) {
            hook.run();
        }
    }

    /**
     * Constructs and run every defined preSendHook class passing the api context and the SdkRequest to new instances
     *
     * @param request SdkRequest
     * @throws MalformedHookException If the hook subclass is malformed
     */
    private void runPreSendHooks(SdkRequest request) throws SdkException {
        try {
            ArrayList<Hook> hooks = new ArrayList();
            for (Class<? extends PreSendHook> preSendHookClass : this.getPreSendHooks()) {
                Constructor<? extends PreSendHook> hookConstructor = preSendHookClass.getConstructor(ApiContext.class, SdkRequest.class);
                hooks.add(hookConstructor.newInstance(this.ctx, request));
            }
            this.runHooks(hooks);
        } catch (IllegalAccessException e) {
            throw new MalformedHookException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new MalformedHookException(e.getMessage());
        } catch (InstantiationException e) {
            throw new MalformedHookException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new MalformedHookException(e.getMessage());
        }
    }

    private void runFailureHooks(SdkRequest request, SdkResponse response, SdkException exception) throws SdkException {
        try {
            ArrayList<Hook> hooks = new ArrayList();
            for (Class<? extends FailureHook> failureHookClass : this.getFailureHooks()) {
                Constructor<? extends FailureHook> hookConstructor = failureHookClass.getConstructor(
                        ApiContext.class,
                        SdkRequest.class,
                        SdkResponse.class,
                        SdkException.class);
                hooks.add(hookConstructor.newInstance(this.ctx, request, response, exception));
            }
            this.runHooks(hooks);
        } catch (IllegalAccessException e) {
            throw new MalformedHookException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new MalformedHookException(e.getMessage());
        } catch (InstantiationException e) {
            throw new MalformedHookException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new MalformedHookException(e.getMessage());
        }
    }

    private void runSuccessHooks(SdkRequest request, SdkResponse response) throws SdkException {
        try {
            ArrayList<Hook> hooks = new ArrayList();
            for (Class<? extends SuccessHook> successHookClass : this.getSuccessHooks()) {
                Constructor<? extends SuccessHook> hookConstructor = successHookClass.getConstructor(ApiContext.class, SdkRequest.class, SdkResponse.class);
                hooks.add(hookConstructor.newInstance(this.ctx, request, response));
            }
            this.runHooks(hooks);
        } catch (IllegalAccessException e) {
            throw new MalformedHookException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new MalformedHookException(e.getMessage());
        } catch (InstantiationException e) {
            throw new MalformedHookException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new MalformedHookException(e.getMessage());
        }
    }

    private String serializeRequestBody() throws UnknownContentTypeException, UnserializableObjectException {
        SdkBodyType requestBody = this.getRequestBody();
        if (requestBody != null) {
            Serializer serializer = SerializerFactory.make(this.getHeaders().get(Headers.CONTENT_TYPE));
            return serializer.serialize(requestBody);
        }
        return null;
    }

    private UserAgent getUserAgent() {
        return UserAgentFactory.make(this.ctx);
    }

    /**
     * Compiles and runs the http request
     *
     * @return The SdkObject representing the response body (null if there is no response body)
     * @throws SdkException     On request build | send errors
     * @throws SdkHttpException When a failure response is received
     */
    public SdkBodyType run() throws SdkException, SdkHttpException {
        // Serialize the request body and construct the SdkRequestObject
        HashMap<String, String> headers = this.getHeaders();

        String serializedBody = this.serializeRequestBody();
        SdkRequest request = new SdkRequest(this.buildRoute(), this.getVerb(), headers, this.getQueryParameters(), serializedBody);

        // Run the preSendHooks
        this.runPreSendHooks(request);
        // Get the configured user agent and send the SdkRequest
        UserAgent agent = this.getUserAgent();
        SdkResponse response = agent.send(request);

        if (response.isFailed()) {
            try {
                // If the response has a status code not in 200-299, runs the failure hooks and throws the appropriate defined exception
                Class<? extends SdkHttpException> exceptionClass = this.getException(response);
                SdkHttpException exceptionInstance = exceptionClass.getConstructor().newInstance();
                // Run the failure hooks and throw the exception
                this.runFailureHooks(request, response, exceptionInstance);
                throw exceptionInstance;
            } catch (NoSuchMethodException e) {
                throw new MalformedSdkException(e.getMessage());
            } catch (IllegalAccessException e) {
                throw new MalformedSdkException(e.getMessage());
            } catch (InstantiationException e) {
                throw new MalformedSdkException(e.getMessage());
            } catch (InvocationTargetException e) {
                throw new MalformedSdkException(e.getMessage());
            }
        } else {
            // Else run the success hooks and returns the response formatted accordingly as the action defines
            this.runSuccessHooks(request, response);
            Class<? extends SdkBodyType> responseBodyClass = this.getResponseBodyClass();
            return response.format(responseBodyClass);
        }
    }

    public Action(ApiContext ctx) {
        this.ctx = ctx;
        this.routeParameters = new HashMap();
        this.queryParameters = new HashMap();
        this.requestBody = null;
    }
}
