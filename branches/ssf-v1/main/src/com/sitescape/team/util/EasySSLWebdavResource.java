package com.sitescape.team.util;

import java.io.IOException;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.webdav.lib.WebdavState;

public class EasySSLWebdavResource extends SWebdavResource {

	protected static Protocol protocol;
	static {
		protocol = new Protocol("https", new EasySSLProtocolSocketFactory(), 443); 
	}
	
	public EasySSLWebdavResource(HttpURL httpURL) throws HttpException,
			IOException {
		super(httpURL);
	}

	public HttpClient getSessionInstance(HttpURL httpURL, boolean reset)
			throws IOException {
		// This code is copied from org.apache.webdav.lib.WebdavSession and 
		// modified to suit our needs. 
		if (reset || client == null) {
			client = new HttpClient();
			// Set a state which allows lock tracking
			client.setState(new WebdavState());
			HostConfiguration hostConfig = client.getHostConfiguration();
			hostConfig.setHost(httpURL.getHost(), httpURL.getPort(), protocol);
			if (proxyHost != null && proxyPort > 0)
				hostConfig.setProxy(proxyHost, proxyPort);

			if (hostCredentials == null) {
				String userName = httpURL.getUser();
				if (userName != null && userName.length() > 0) {
					hostCredentials = new UsernamePasswordCredentials(userName,
							httpURL.getPassword());
				}
			}

			if (hostCredentials != null) {
				HttpState clientState = client.getState();
				clientState.setCredentials(null, httpURL.getHost(),
						hostCredentials);
				clientState.setAuthenticationPreemptive(true);
			}

			if (proxyCredentials != null) {
				client.getState().setProxyCredentials(null, proxyHost,
						proxyCredentials);
			}
		}

		return client;
	}
}
