package it.sdkboilerplate.http;

import java.util.HashMap;

public class SdkRequest {
    private String route;
    private String verb;
    private String body;
    private HashMap<String, String> headers;
    private HashMap<String, String> queryParameters;


    public String getRoute() {
        return this.route;
    }

    public String getVerb() {
        return this.verb;
    }

    public String getBody() {
        return this.body;
    }

    public HashMap<String, String> getHeaders() {
        return this.headers;
    }

    public HashMap<String, String> getQueryParameters() {
        return this.queryParameters;
    }

    public void setHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public SdkRequest(String route, String verb, HashMap<String, String> headers, HashMap<String, String> queryParameters, String body) {
        this.route = route;
        this.verb = verb;
        this.headers = headers;
        this.queryParameters = queryParameters;
        this.body = body;
    }
}
