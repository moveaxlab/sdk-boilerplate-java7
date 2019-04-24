package it.sdkboilerplate.hooks;

import it.sdkboilerplate.http.SdkRequest;
import it.sdkboilerplate.http.SdkResponse;
import it.sdkboilerplate.lib.ApiContext;

/**
 * SuccessHook abstract class implementing Hook interface. SuccessHook instances are constructed with the sdkRequest and
 * sdkResponse objects and are run if the Http sdkResponse has a success status code (200 - 299)
 */
public abstract class SuccessHook implements Hook {
    public ApiContext ctx;
    public SdkRequest sdkRequest;
    public SdkResponse sdkResponse;


    public SuccessHook(ApiContext ctx, SdkRequest sdkRequest, SdkResponse sdkResponse) {
        this.ctx = ctx;
        this.sdkRequest = sdkRequest;
        this.sdkResponse = sdkResponse;
    }
}
