package it.sdkboilerplate.lib;

import it.sdkboilerplate.cache.CacheAdapter;
import it.sdkboilerplate.exceptions.ConfigurationException;

import java.util.HashMap;

public class ApiContext {
    private String hostname;
    private HashMap<String, Object> config;
    private CacheAdapter cache;

    public String getHostname() {
        return this.hostname;
    }

    public HashMap<String, Object> getConfig() {
        return this.config;
    }

    public ApiContext(String hostname, HashMap<String, Object> config, CacheAdapter cache) throws ConfigurationException {
        this.hostname = hostname;
        this.config = config;
        this.cache = cache;
        this.validateConfig();
    }

    private void validateConfig() throws ConfigurationException {
        if (this.config.get("timeout") == null || this.config.get("verifySSL") == null || this.config.get("mode") == null) {
            throw new ConfigurationException();
        }
    }

    public CacheAdapter getCache() {
        return this.cache;
    }
}
