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
package org.kablink.teaming.util;

import java.io.IOException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.logging.Log;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.methods.CheckinMethod;

public class SWebdavResource extends WebdavResource {

    public SWebdavResource(HttpURL httpURL, Credentials credentials, int action,
            int depth) throws HttpException, IOException {
    	super(httpURL, credentials, action, depth);
    }
    
    public SWebdavResource(HttpURL httpURL, int action, int depth)
    throws HttpException, IOException {
    	super(httpURL, action, depth);
    }
    
    public SWebdavResource(HttpURL httpURL, int depth)
    throws HttpException, IOException {
    	super(httpURL, depth);
    }
    
    public SWebdavResource(HttpURL httpURL, int depth, boolean followRedirects)
    throws HttpException, IOException {
    	super(httpURL, depth, followRedirects);
    }
    
    public SWebdavResource(HttpURL httpURL)
    throws HttpException, IOException {
    	super(httpURL);
    }
    
    public SWebdavResource(HttpURL httpURL, boolean followRedirects)
    throws HttpException, IOException {
    	super(httpURL, followRedirects);
    }
    
    public SWebdavResource(HttpURL httpURL, String proxyHost, int proxyPort)
    throws HttpException, IOException {
    	super(httpURL, proxyHost, proxyPort);
    }
    
    public SWebdavResource(HttpURL httpURL, String proxyHost, int proxyPort, boolean followRedirects)
    throws HttpException, IOException {
    	super(httpURL, proxyHost, proxyPort, followRedirects);
    }
    
    public SWebdavResource(HttpURL httpURL, String proxyHost, int proxyPort,
            Credentials proxyCredentials)
    throws HttpException, IOException {
    	super(httpURL, proxyHost, proxyPort, proxyCredentials);
	}
    
	public SWebdavResource(HttpURL httpURL, String proxyHost, int proxyPort,
        Credentials proxyCredentials, boolean followRedirects)
      throws HttpException, IOException {
    	super(httpURL, proxyHost, proxyPort, proxyCredentials, followRedirects);		
	}
	
    public SWebdavResource(String escapedHttpURL)
    throws HttpException, IOException {
    	super(escapedHttpURL);
    }
    
    public SWebdavResource(String escapedHttpURL, boolean followRedirects)
    throws HttpException, IOException {
    	super(escapedHttpURL, followRedirects);
    }
    
    public SWebdavResource(String escapedHttpURL, Credentials credentials)
    throws HttpException, IOException {
    	super(escapedHttpURL, credentials);
    }
    
    public SWebdavResource(String escapedHttpURL, Credentials credentials,
            boolean followRedirects)
         throws HttpException, IOException {
    	super(escapedHttpURL, credentials, followRedirects);
    }
    
    public SWebdavResource(String escapedHttpURL, String proxyHost,
            int proxyPort) throws HttpException, IOException {
    	super(escapedHttpURL, proxyHost, proxyPort);
    }
    
    public SWebdavResource(String escapedHttpURL, String proxyHost,
            int proxyPort, Credentials proxyCredentials)
    throws HttpException, IOException {
    	super(escapedHttpURL, proxyHost, proxyPort, proxyCredentials);
    }
    
    public SWebdavResource(HttpURL httpURL, String additionalPath)
    throws HttpException, IOException {
    	super(httpURL, additionalPath);
    }
    
    public SWebdavResource(HttpURL httpURL, String additionalPath, boolean followRedirects)
    throws HttpException, IOException {
    	super(httpURL, additionalPath, followRedirects);
    }
	
    /**
     * Execute the CHECKIN method for the given path. 
     * 
     * @param path the server relative path of the resource to check in
     * @return the value of the Location response header if the method is
     * succeeded. Returns <code>null</code> otherwise. 
     * @throws HttpException
     * @throws IOException
     */
    public String checkin(String path) throws HttpException, IOException {

        setClient();
        CheckinMethod method = new CheckinMethod(URIUtil.encodePath(path));
        method.setDebug(debug);
        method.setFollowRedirects(this.followRedirects);

        generateIfHeader(method);
        generateTransactionHeader(method);
        int statusCode = client.executeMethod(method);

        setStatusCode(statusCode);

        boolean boolResult = (statusCode >= 200 && statusCode < 300) ? true : false;
	
        String location = null;
        
        if(boolResult) {
        	location = method.getResponseHeader("Location").getValue();
        }
        
        return location;
    }

	public void logError(Log logger) {
		// Log the HTTP status code and error message.
		logger.error("status code=" + this.getStatusCode() + ", " +
				"status message=[" + this.getStatusMessage() + "]");		
	}

}
