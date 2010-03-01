/*
 * $Header: /home/cvs/jakarta-slide/webdavclient/clientlib/src/java/org/apache/webdav/lib/WebdavSession.java,v 1.7 2004/07/30 13:20:48 ib Exp $
 * $Revision: 1.7 $
 * $Date: 2004/07/30 13:20:48 $
 *
 * ====================================================================
 *
 * Copyright 1999-2002 The Apache Software Foundation 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.kablink.teaming.util;

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
import org.kablink.teaming.util.SWebdavResource;

public class EasySSLWebdavResource extends SWebdavResource {

	protected static Protocol protocol;
	static {
		protocol = new Protocol("https", new EasySSLProtocolSocketFactory(), 443); 
	}
	
	public EasySSLWebdavResource(HttpURL httpURL) throws HttpException,
			IOException {
		super(httpURL);
	}

    /**
     * Get a <code>HttpClient</code> instance.
     * This method returns a new client instance, when reset is true.
     *
     * @param httpURL The http URL to connect.  only used the authority part.
     * @param reset The reset flag to represent whether the saved information
     *              is used or not.
     * @return An instance of <code>HttpClient</code>.
     * @exception IOException
     */
	public HttpClient getSessionInstance(HttpURL httpURL, boolean reset)
			throws IOException {
		// This code is copied from org.apache.webdav.lib.WebdavSession and 
		// modified to suit our needs. 
		if (reset || client == null) {
			client = new HttpClient();
			// Set a state which allows lock tracking
			client.setState(new WebdavState());
			HostConfiguration hostConfig = client.getHostConfiguration();
			// SiteScape: Changed to use the custom protocol rather than the default.
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
