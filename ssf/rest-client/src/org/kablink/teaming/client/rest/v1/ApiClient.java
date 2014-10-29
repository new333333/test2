/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
 *
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 *
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 *
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 *
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 *
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

package org.kablink.teaming.client.rest.v1;

import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.json.JSONConfiguration;
import org.codehaus.jackson.map.ObjectMapper;
import org.kablink.teaming.rest.v1.provider.DefaultJAXBContextResolver;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import org.kablink.teaming.rest.v1.provider.DefaultObjectMapperContextResolver;

/**
 * @author jong
 */
public class ApiClient {

    private static final MediaType[] ACCEPTABLE_MEDIA_TYPES_DEFAULT = new MediaType[]{MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE};

    private String serverUrl; // Vibe server URL including scheme (http/https), hostname, and optional port (e.g. http://mycompany:8080/)
    private String baseUrl; // Vibe server base URL for REST API (e.g. http://mycompany:8080/rest/v1/) - This is constructed from server URL.
    private Client client; // underlying Jersey client
    private MediaType[] acceptableMediaTypes = ACCEPTABLE_MEDIA_TYPES_DEFAULT;
    private Api api;

    /**
     * Create a <code>ApiClient</code> instance with the URL for Vibe server and the login credential
     * which will be used for HTTP Basic Authentication.
     *
     * @param serverUrl Vibe server URL including scheme (http/https), hostname, and optional port (e.g. http://mycompany:8080/)
     * @param username  Vibe username
     * @param password  Vibe password
     * @return
     */
    public static ApiClient create(String serverUrl, String username, String password) {
        return create(serverUrl, username, password, null, 0, DefaultJAXBContextResolver.class);
    }

    public static ApiClient create(String serverUrl, String username, String password, String proxyHost, int proxyPort) {
        return create(serverUrl, username, password, proxyHost, proxyPort, DefaultJAXBContextResolver.class);
    }

    /**
     * Create a <code>ApiClient</code> instance with the URL for Vibe server and the login credential
     * which will be used for HTTP Basic Authentication.
     *
     * @param serverUrl                Vibe server URL including scheme (http/https), hostname, and optional port (e.g. http://mycompany:8080/)
     * @param username                 Vibe username
     * @param password                 Vibe password
     * @param jaxbContextResolverClass JAXB context resolver class
     * @return
     */
    public static ApiClient create(String serverUrl, String username, String password, String proxyHost, int proxyPort, Class jaxbContextResolverClass) {
        if (!serverUrl.endsWith("/"))
            serverUrl += "/";

        DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
        Map<String, Object> props = config.getProperties();
        props.put(ApacheHttpClientConfig.PROPERTY_HANDLE_COOKIES, Boolean.TRUE);
        props.put(ApacheHttpClientConfig.PROPERTY_PREEMPTIVE_AUTHENTICATION, Boolean.TRUE);
        props.put(ApacheHttpClientConfig.PROPERTY_CONNECT_TIMEOUT, 5000);
        //props.put(ClientConfig.FPROPERTY_FOLLOW_REDIRECTS, Boolean.TRUE);

        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

        config.getState().setCredentials(null, null, -1, username, password);
        if (proxyHost!=null) {
            config.getState().setProxyCredentials(null, proxyHost, proxyPort, "", "");
            props.put(ApacheHttpClientConfig.PROPERTY_PROXY_URI, "http://" + proxyHost + ":" + proxyPort + "/");
        }

        if (jaxbContextResolverClass != null) {
            config.getClasses().add(jaxbContextResolverClass);
            config.getClasses().add(DefaultObjectMapperContextResolver.class);
        }

        ApacheHttpClient c = ApacheHttpClient.create(config);
        return new ApiClient(serverUrl, c);
    }

    public ApiClient(String serverUrl, Client c) {
        this(serverUrl, serverUrl + "rest", c);
    }

    private ApiClient(String serverUrl, String baseUrl, Client c) {
        this.serverUrl = serverUrl;
        this.baseUrl = baseUrl;
        this.client = c;
        this.api = new ApiImpl(this);
    }

    /**
     * Return URL for Vibe server.
     *
     * @return
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * Return a <code>Api</code> object on which application can invoke API calls.
     *
     * @return
     */
    public Api getApi() {
        return api;
    }

    /**
     * Return a handle on the underlying Jersey client object. This method can be used to set custom settings
     * before making API calls.
     *
     * @return
     */
    public Client getClient() {
        return client;
    }

    /**
     * Return an array of acceptable media types. The returned array should never be modified.
     *
     * @return
     */
    public MediaType[] getAcceptableMediaTypes() {
        return acceptableMediaTypes;
    }

    /**
     * Set an array of acceptable media types. This method can be used to override the default settings.
     *
     * @param acceptableMediaTypes
     */
    public void setAcceptableMediaTypes(MediaType[] acceptableMediaTypes) {
        this.acceptableMediaTypes = acceptableMediaTypes;
    }

    /**
     * Release the resources associated with this client. The application must call this method when done with a client.
     */
    public void destroy() {
        client.destroy();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public ObjectMapper getObjectMapper() {
        return getClient().getProviders().getContextResolver(ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE).getContext(null);
    }

    private static TrustManager[] trustAllCerts;

    static {
        trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                return;
            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                return;
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLContext.setDefault(sc);
            //HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {

        }
    }
}
