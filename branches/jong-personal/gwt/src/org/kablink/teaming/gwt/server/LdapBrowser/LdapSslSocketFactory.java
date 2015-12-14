/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.server.LdapBrowser;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ?
 * 
 * @author rvasudevan
 * @author drfoster@novell.com
 */
public class LdapSslSocketFactory extends SSLSocketFactory {
	protected static Log m_logger = LogFactory.getLog(LdapSslSocketFactory.class);
	
	private SSLSocketFactory	m_factory;	//

	/**
	 * Constructor method.
	 */
	public LdapSslSocketFactory() {
		try {
			// Here you can instantiate your own SSL context with
			// hardened security and your own trust managers which can
			// check the extension
			// SSLContext sslcontext = SSLSecurityInitializer.getContext();
			   SSLContext sslcontext = null;	//!
			
			if (null == sslcontext) {
				sslcontext = SSLContext.getInstance("TLS");
				sslcontext.init(
					null,	// null -> No KeyManager required.
					new TrustManager[] {new LdapSslTrustManager()},
					new java.security.SecureRandom());
			}

			m_factory = ((SSLSocketFactory) sslcontext.getSocketFactory());
		}
		
		catch (Exception ex) {
			m_logger.error("::LdapSssSocketFactory( Constructor Exception )", ex);
		}
	}

	/**
	 * ?
	 * 
	 * @return
	 */
	public static SocketFactory getDefault() {
		return new LdapSslSocketFactory();
	}

	/**
	 * ?
	 *
	 * @param socket
	 * @param s
	 * @param i
	 * @param flag
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	@Override
	public Socket createSocket(Socket socket, String s, int i, boolean flag) throws IOException {
		return m_factory.createSocket(socket, s, i, flag);
	}

	/**
	 * ?
	 * 
	 * @param inaddr
	 * @param i
	 * @param inaddr1
	 * @param j
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	@Override
	public Socket createSocket(InetAddress inaddr, int i, InetAddress inaddr1, int j) throws IOException {
		return m_factory.createSocket(inaddr, i, inaddr1, j);
	}

	/**
	 * ?
	 * 
	 * @param inaddr
	 * @param i
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	@Override
	public Socket createSocket(InetAddress inaddr, int i) throws IOException {
		return m_factory.createSocket(inaddr, i);
	}

	/**
	 * ?
	 *
	 * @param s
	 * @param i
	 * @param inaddr
	 * @param j
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	@Override
	public Socket createSocket(String s, int i, InetAddress inaddr, int j) throws IOException {
		return m_factory.createSocket(s, i, inaddr, j);
	}

	/**
	 * ?
	 *
	 * @param s
	 * @param i
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	@Override
	public Socket createSocket(String s, int i) throws IOException {
		return m_factory.createSocket(s, i);
	}

	/**
	 * ?
	 * 
	 * @return
	 */
	@Override
	public String[] getDefaultCipherSuites() {
		return m_factory.getSupportedCipherSuites();
	}

	/**
	 * ?
	 *
	 * @return
	 */
	@Override
	public String[] getSupportedCipherSuites() {
		return m_factory.getSupportedCipherSuites();
	}
}
