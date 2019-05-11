package it.sdkboilerplate.exceptions;

/**
 * Sdk Http Exception class.
 */
public abstract class SdkHttpException extends SdkException {
    private String debugInfo;
    private String requestId;

    public String getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(String debugInfo) {
        this.debugInfo = debugInfo;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
