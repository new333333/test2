/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.simpleurl.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kablink.teaming.asmodule.bridge.BridgeClient;


public class SimpleURLServlet extends HttpServlet {

	private static final String SERVICE_CLASS_NAME = "org.kablink.teaming.util.SimpleNameUtil";
	
	private static final String SERVICE_METHOD_NAME = "resolveURL";
	
	private static final Class[] SERVICE_METHOD_ARG_TYPES = 
		new Class[] {boolean.class, String.class, int.class, String.class};

	private static final int BUFFER_SIZE = 4096;

	private String landingPagePath;
	
	public void init(ServletConfig config) throws ServletException {
		landingPagePath = config.getServletContext().getInitParameter("landingPagePath");
		super.init(config);
	}
	
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
    	String simpleName = req.getPathInfo();
    	
    	if(simpleName == null || simpleName.equals("/")) {
    		resp.sendRedirect(landingPagePath);
    	}
    	else if(knownStaticResource(simpleName)) {
    		getKnownStaticResource(simpleName, resp);
    	}
    	else {
	    	boolean isSecure = req.isSecure();
	    	String hostname = req.getServerName();
	    	int port = req.getServerPort();
	    	if(simpleName.startsWith("/"))
	    		simpleName = simpleName.substring(1);
	    	
			String resolvedURI = null;
			try {
				resolvedURI = (String) BridgeClient.invoke(null, null, 
						SERVICE_CLASS_NAME, SERVICE_METHOD_NAME, SERVICE_METHOD_ARG_TYPES,
						new Object[] {isSecure, hostname, port, simpleName});
			} catch (Exception e) {
				throw new ServletException(e);
			}
			
			if(resolvedURI != null)
				resp.sendRedirect(resolvedURI);
			else
				resp.sendError(HttpServletResponse.SC_NOT_FOUND, simpleName);
    	}
    }

    private boolean knownStaticResource(String resourcePath) {
		return (resourcePath.equals("/favicon.ico") || resourcePath.equals("/robots.txt"));
	}
    
    private void getKnownStaticResource(String resourcePath, HttpServletResponse resp) throws ServletException, IOException {
 		InputStream in = getServletConfig().getServletContext().getResourceAsStream(resourcePath);
		if(in != null) {
    		OutputStream out = resp.getOutputStream();
    		try {
    			copyStream(in, out);
    		}
    		finally {
    			try {
    				in.close();
    			}
    			catch(IOException ignore) {}
    		}
		}
		else {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, resourcePath);
		}
    }
    
    private void copyStream(InputStream in, OutputStream out)
			throws IOException {
		// Copy the input stream to the output stream
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}
    }
}
