package it.sdkboilerplate.lib;

import it.sdkboilerplate.cache.CacheAdapter;
import it.sdkboilerplate.exceptions.ConfigurationException;

import java.util.HashMap;
import java.util.HashSet;

public class ApiContext {
    private String hostname;
    private HashMap<String, Object> config;
    private CacheAdapter cache;
    private final static HashSet validProtocols = new HashSet<String>() {{
        add("http");
        add("https");
    }};

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
            throw new ConfigurationException("timeout, verifySSL and mode are mandatory, check configuration");
        }
        if (this.config.containsKey("proxy")) {
            HashMap<String, Object> proxyConfiguration = (HashMap) this.config.get("proxy");
            if (!(proxyConfiguration.get("hostname") instanceof String))
                throw new ConfigurationException("Proxy ip address invalid");
            if (!(proxyConfiguration.get("port") instanceof Integer))
                throw new ConfigurationException("Proxy port invalid (must be an integer)");
            if (!(proxyConfiguration.get("protocol") instanceof String) || !(this.validateProxyProtocol(proxyConfiguration.get("protocol").toString())))
                throw new ConfigurationException("Proxy protocol is invalid, valid protocols are " + validProtocols.toString());
            if (proxyConfiguration.containsKey("credentials") && !(this.validateProxyCredentials((HashMap) proxyConfiguration.get("credentials"))))
                throw new ConfigurationException("Invalid credentials, specify user and password");
        }
    }

    public CacheAdapter getCache() {
        return this.cache;
    }

    private boolean validateProxyProtocol(String protocol) {
        return validProtocols.contains(protocol);
    }

    private boolean validateProxyCredentials(HashMap credentials) {
        return credentials.containsKey("user") && credentials.containsKey("password");
    }
}
