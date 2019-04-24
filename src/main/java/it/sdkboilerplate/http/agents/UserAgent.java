package it.sdkboilerplate.http.agents;

import it.sdkboilerplate.exceptions.SdkException;
import it.sdkboilerplate.http.SdkRequest;
import it.sdkboilerplate.http.SdkResponse;

import java.util.HashMap;

public abstract class UserAgent {

    private String hostname;
    private HashMap<String, Object> config;

    public String getHostname() {
        return hostname;
    }

    public HashMap<String, Object> getConfig() {
        return config;
    }

    public abstract SdkResponse send(SdkRequest sdkRequest) throws SdkException;

    UserAgent(String hostname, HashMap<String, Object> config) {
        this.hostname = hostname;
        this.config = config;
    }
}
