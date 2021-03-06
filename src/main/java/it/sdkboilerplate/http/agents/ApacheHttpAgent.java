package it.sdkboilerplate.http.agents;


import it.sdkboilerplate.exceptions.*;
import it.sdkboilerplate.http.SdkRequest;
import it.sdkboilerplate.http.SdkResponse;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;


public class ApacheHttpAgent extends UserAgent {
    public ApacheHttpAgent(String hostname, HashMap<String, Object> config) {
        super(hostname, config);
    }

    public SdkResponse send(SdkRequest request) throws SdkException {
        CloseableHttpClient client = this.getClient();
        HttpRequestBase requestBase = this.getAgentRequest(request);
        this.setRequestHeaders(requestBase, request.getHeaders());
        this.setRequestUri(requestBase, request);
        try {
            HttpResponse response = client.execute(requestBase);
            return this.convertResponse(response);
        } catch (ClientProtocolException exc) {
            throw new CouldNotSendRequest();
        } catch (IOException exc) {
            throw new CouldNotSendRequest();
        }
    }

    private HttpRequestBase getAgentRequest(SdkRequest request) throws UnknownBodyTypeException {
        String verb = request.getVerb().toLowerCase();
        try {
            if (verb.equals("get"))
                return this.buildGetRequest(request);
            if (verb.equals("delete"))
                return this.buildDeleteRequest(request);
            if (verb.equals("put"))
                return this.buildPutRequest(request);
            if (verb.equals("post"))
                return this.buildPostRequest(request);
            if (verb.equals("patch"))
                return this.buildPatchRequest(request);
        } catch (UnsupportedEncodingException exc) {
            throw new UnknownBodyTypeException();
        }
        throw new UnknownVerbException();
    }

    private SdkResponse convertResponse(HttpResponse response) throws IOException {
        return new SdkResponse(this.getResponseStatus(response), this.getResponseBody(response), this.getResponseHeaders(response.getAllHeaders()));
    }

    private HttpRequestBase buildGetRequest(SdkRequest request) {
        return new HttpGet();
    }

    private HttpRequestBase buildPutRequest(SdkRequest request) throws UnsupportedEncodingException {
        HttpPut putRequest = new HttpPut();
        putRequest.setEntity(new StringEntity(request.getBody()));
        return putRequest;
    }

    private HttpRequestBase buildDeleteRequest(SdkRequest request) {
        return new HttpDelete();

    }

    private HttpRequestBase buildPostRequest(SdkRequest request) throws UnsupportedEncodingException {
        HttpPost postRequest = new HttpPost();
        postRequest.setEntity(new StringEntity(request.getBody()));
        return postRequest;

    }

    private HttpRequestBase buildPatchRequest(SdkRequest request) throws UnsupportedEncodingException {
        HttpPatch patchRequest = new HttpPatch();
        patchRequest.setEntity(new StringEntity(request.getBody()));
        return patchRequest;
    }

    private void setRequestHeaders(HttpRequestBase rawRequest, HashMap<String, String> headers) {
        for (Map.Entry header : headers.entrySet()) {
            rawRequest.setHeader(header.getKey().toString(), header.getValue().toString());
        }
    }

    private void setRequestUri(HttpRequestBase rawRequest, SdkRequest request) throws MalformedUrlException {
        try {
            URIBuilder builder = new URIBuilder(this.getHostname() + request.getRoute());
            for (Map.Entry queryParam : request.getQueryParameters().entrySet()) {
                builder.addParameter(queryParam.getKey().toString(), queryParam.getValue().toString());
            }
            rawRequest.setURI(builder.build());
        } catch (URISyntaxException exc) {
            throw new MalformedUrlException();
        }
    }

    private HashMap<String, String> getResponseHeaders(Header[] headers) {
        HashMap<String, String> formattedHeaders = new HashMap<String, String>();
        for (Header h : headers) {
            formattedHeaders.put(h.getName(), h.getValue());
        }
        return formattedHeaders;
    }

    private String getResponseBody(HttpResponse response) throws IOException {
        BufferedInputStream inputBuffer = new BufferedInputStream(response.getEntity().getContent());
        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
        int result = inputBuffer.read();
        while (result != -1) {
            byte b = (byte) result;
            outputBuffer.write(b);
            result = inputBuffer.read();
        }
        return outputBuffer.toString();

    }

    private Integer getResponseStatus(HttpResponse response) {
        return response.getStatusLine().getStatusCode();
    }

    private CloseableHttpClient getClient() {
        HttpClientBuilder builder = HttpClients.custom();
        if (this.isProxyRequest()) {
            this.setRequestProxy(builder);
        }

        if (!(Boolean) this.getConfig().get("verifySSL")) {
            try {
                builder.setHostnameVerifier(new AllowAllHostnameVerifier()).
                        setSslcontext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                                return true;
                            }
                        }).build());
            } catch (Exception exc) {
                throw new RuntimeException();
            }

        }

        return builder.build();
    }

    private void setRequestProxy(HttpClientBuilder builder) {
        HashMap proxyConfig = this.getProxyConfig();
        HttpHost proxy = new HttpHost((String) proxyConfig.get("hostname"), (int) proxyConfig.get("port"), (String) proxyConfig.get("protocol"));
        builder.setProxy(proxy);
        if (this.isProxyAuthenticated()) {
            HashMap credentialsConfig = this.getProxyCredentials();
            Credentials proxyCredentials = new UsernamePasswordCredentials((String) credentialsConfig.get("user"), (String) credentialsConfig.get("password"));
            AuthScope authScope = new AuthScope((String) proxyConfig.get("hostname"), (int) proxyConfig.get("port"));
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(authScope, proxyCredentials);
            builder.setDefaultCredentialsProvider(provider);

        }
    }

    private boolean isProxyRequest() {
        return this.getConfig().containsKey("proxy");
    }

    private HashMap<String, Object> getProxyConfig() {
        return (HashMap<String, Object>) this.getConfig().get("proxy");
    }

    private boolean isProxyAuthenticated() {
        return this.getProxyConfig().containsKey("credentials");
    }

    private HashMap<String, String> getProxyCredentials() {
        return (HashMap) this.getProxyConfig().get("credentials");
    }
}
