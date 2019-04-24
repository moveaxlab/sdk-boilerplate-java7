package it.sdkboilerplate.http.agents;

import it.sdkboilerplate.lib.ApiContext;

public class UserAgentFactory {
    public static UserAgent make(ApiContext ctx) {
        return new ApacheHttpAgent(ctx.getHostname(), ctx.getConfig());
    }
}
