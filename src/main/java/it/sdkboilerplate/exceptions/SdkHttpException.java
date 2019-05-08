package it.sdkboilerplate.exceptions;

/**
 * Sdk Http Exception class.
 */
public abstract class SdkHttpException extends SdkException {
    private String rawRequest;
    private String rawResponse;

    public void setRawRequest(String rawRequest) {
        this.rawRequest = rawRequest;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    public String getRawRequest() {
        return rawRequest;
    }

    public String getRawResponse() {
        return rawResponse;
    }
}
