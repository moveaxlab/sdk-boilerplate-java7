package mocks.actions;

import it.sdkboilerplate.exceptions.SdkHttpException;
import it.sdkboilerplate.exceptions.UnserializableObjectException;
import it.sdkboilerplate.http.Headers;
import it.sdkboilerplate.http.MediaType;
import it.sdkboilerplate.objects.SdkBodyType;
import it.sdkboilerplate.lib.ApiContext;
import it.sdkboilerplate.validation.Schema;
import mocks.exceptions.ValidationException;
import mocks.objects.TestUserCreation;

import java.util.HashMap;

public class TestCreateUserAction extends TestAction {
    @Override
    public String getRoute() {
        return "/create";
    }

    @Override
    public String getVerb() {
        return "POST";
    }

    @Override
    public Class<? extends SdkBodyType> getRequestBodyClass() {
        return TestUserCreation.class;
    }

    @Override
    public Class<? extends SdkBodyType> getResponseBodyClass() {
        return null;
    }

    @Override
    public Schema getQueryParametersSchema() {
        return new Schema();
    }

    @Override
    public Schema getRouteParametersSchema() {
        return new Schema();
    }

    @Override
    public HashMap<String, Class<? extends SdkHttpException>> getErrors() {
        HashMap<String, Class<? extends SdkHttpException>> errors = new HashMap();
        errors.put("422", ValidationException.class);
        return errors;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap();
        headers.put(Headers.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        headers.put(Headers.ACCEPT, MediaType.APPLICATION_JSON);
        return headers;
    }

    public void setUser(TestUserCreation userCreation) throws UnserializableObjectException {
        this.setRequestBody(userCreation);
    }

    public TestCreateUserAction(ApiContext ctx) {
        super(ctx);
    }
}
