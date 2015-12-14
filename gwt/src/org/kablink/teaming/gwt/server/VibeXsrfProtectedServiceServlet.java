/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.server;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.Utils;

import com.google.gwt.user.client.rpc.RpcToken;
import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.user.client.rpc.XsrfToken;
import com.google.gwt.user.server.rpc.XsrfProtectedServiceServlet;
import com.google.gwt.util.tools.shared.Md5Utils;
import com.google.gwt.util.tools.shared.StringUtils;

/**
 * Wraps GWT's XsrfProtectedServiceServlet so that we can invoke it
 * token validation method.
 * 
 * See:
 *    http://www.gwtproject.org/doc/latest/DevGuideSecurityRpcXsrf.html and
 *  
 * @author drfoster
 */
public class VibeXsrfProtectedServiceServlet extends XsrfProtectedServiceServlet {
	private static final String GWT_XSRF_SESSION_COOKIE_NAME	= "JSESSIONID";	// As per 'gwt.xsrf.session_cookie_name' in the GWT XSRF documentation.

	// Initialized the first time it's needed with an indication of
	// whether we should allow duplicate session cookies.  If they
	// are not allowed, they will generate an XSRF error if
	// encountered.
	private static Boolean m_allowDuplicateSessionCookies;	//

	/**
	 * Constructor method.
	 */
	public VibeXsrfProtectedServiceServlet() {
		// Construct the super class with the name of the session
		// cookie.
		super(GWT_XSRF_SESSION_COOKIE_NAME);
	}
	
	/*
	 * Retrieves named cookies from supplied request.
	 */
	private static List<Cookie> getCookies(HttpServletRequest request, String cookieName) {
		List<Cookie> reply = new ArrayList<Cookie>();
		
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie:  cookies) {
				if (cookieName.equals(cookie.getName())) {
					reply.add(cookie);
				}
			}
		}

		// If we get here, reply refers to a List<Cookie> of the
		// cookies matching the given name.  Return it.
		return reply;
	}
	
	  
	/**
	 * Exposes a method we can call to invoke the GWT XSRF token
	 * validation handler.
	 * 
	 * Overrides the XsrfProtectedServiceServlet.validateXsrfToken() method.
	 * 
	 * @param token
	 * @param method
	 * 
	 * @throws RpcTokenException
	 */
	@Override
	public void validateXsrfToken(RpcToken token, Method method) throws RpcTokenException {
		// If we haven't read the 'allow duplicate session cookies'
		// flag from the properties yet...
		if (null == m_allowDuplicateSessionCookies) {
			// ...read it now.
			m_allowDuplicateSessionCookies = SPropsUtil.getBoolean(
				"xsrf.allow.duplicate.session.cookies",
				Utils.checkIfFilr());
		}
		
		try {
			// Simply call the super class' version of this method.
//			super.validateXsrfToken(token, method);	// DRF (20150618):  Commented out in favor of our implementation of the method (see below) that potentially allows duplicate session cookies.
			validateXsrfTokenImpl(token, method, m_allowDuplicateSessionCookies);
		}
		
		catch (IllegalArgumentException iae) {
			// An IllegalArgumentException will be thrown if the
			// validation detects a duplicate session cookie.  We want
			// that displayed to the user as an RPC token exception.
			throw new RpcTokenException(iae.getMessage());
		}
	}
	
	/*
	 * Validates token included with and RPCRequest against XSRF cookie
	 * attacks.
	 * 
	 * Unlike XsrfProtectedServiceServlet.validateXsrfToken(), which
	 * this was copied from, this version optionally ALLOWS duplicate
	 * session cookies.
	 * 
	 * We need to allow them in Filr, for example, because the VA
	 * configuration tooling using Jetty (on port 9443) generates a
	 * session cookie separate and distinct from the one we (Tomcat)
	 * uses.  Without allowing duplicates, switching from the VA
	 * configuration piece to the web application would cause
	 * unexpected XSRF violation errors.
	 */
	private void validateXsrfTokenImpl(RpcToken rpcToken, Method method, boolean allowDuplicateSessionCookies) throws RpcTokenException {
		// If we weren't given a token...
		if (rpcToken == null) {
			// ...it's an error.
			throw new RpcTokenException("XSRF token missing.");
		}

		// Do we any session cookies?
		List<Cookie> sessionCookies = getCookies(
			getThreadLocalRequest(),
			GWT_XSRF_SESSION_COOKIE_NAME);
		
		if (sessionCookies.isEmpty()) {
			// No!  Throw an appropriate RpcTokenException.
	    	throw new RpcTokenException("Session cookie is missing!  Unable to verify XSRF cookie.");
		}
		
		// Yes, we have some session cookies!  Do we have more than one
		// session cookie while not allowing duplicates?
		else if ((1 < sessionCookies.size()) && (!allowDuplicateSessionCookies)) {
			// Yes!  Throw an appropriate IllegalArgumentException as
			// that can be a sign of a cookie overriding attempt.
			throw new IllegalArgumentException("Duplicate session cookies!  Cookie override attack?");
		}
		
		// Scan the session cookies.
	    String xsrfToken  = ((XsrfToken) rpcToken).getToken();
		for (Cookie sessionCookie:  sessionCookies) {
			// Is this session cookie valid?
		    if ((null == sessionCookie.getValue()) || (0 == sessionCookie.getValue().length())) {
				// No!  Throw an appropriate RpcTokenException.
		    	throw new RpcTokenException("Session cookie is empty!  Unable to verify XSRF token.");
		    }
	
		    // Does this session cookie's token match what we're
		    // validating against?
		    String cookieToken = StringUtils.toHexString(Md5Utils.getMd5Digest(sessionCookie.getValue().getBytes()));
		    if (cookieToken.equals(xsrfToken)) {
		    	// Yes!  Then we're done.  Simply return.
		    	return;
		    }
		}
		
		// If we get here, we didn't find a session cookie with a
		// matching token!  Throw an appropriate RpcTokenException.
    	throw new RpcTokenException("Invalid XSRF token.");
	}
}
