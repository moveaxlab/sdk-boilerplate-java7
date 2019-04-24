package mocks.actions;

import it.sdkboilerplate.exceptions.SdkHttpException;

import it.sdkboilerplate.objects.SdkBodyType;
import it.sdkboilerplate.lib.ApiContext;
import it.sdkboilerplate.validation.Schema;
import mocks.exceptions.NotFoundException;
import mocks.objects.TestUserRetrieval;


import java.util.HashMap;

public class TestRetrieveUserAction extends TestAction {
    @Override
    public String getRoute() {
        return "/read/{userUUID}";
    }

    @Override
    public String getVerb() {
        return "GET";
    }

    @Override
    public Class<? extends SdkBodyType> getRequestBodyClass() {
        return null;
    }

    @Override
    public Class<? extends SdkBodyType> getResponseBodyClass() {
        return TestUserRetrieval.class;
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
        errors.put("404", NotFoundException.class);
        return errors;
    }

    @Override
    public HashMap<String, String> getHeaders() {
        return null;
    }

    private void setUserUUID(String userUUID) {
        this.setRouteParameter("userUUID", userUUID);
    }

    public TestRetrieveUserAction(ApiContext ctx) {
        super(ctx);
    }
}
