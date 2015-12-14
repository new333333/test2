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
package org.kablink.teaming.remoteapplication.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.remoteapplication.RemoteApplicationException;
import org.kablink.teaming.remoteapplication.RemoteApplicationManager;
import org.kablink.teaming.security.accesstoken.AccessToken;
import org.kablink.teaming.security.accesstoken.AccessTokenManager;
import org.kablink.teaming.security.accesstoken.AccessToken.BinderAccessConstraints;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;


public class RemoteApplicationManagerImpl implements RemoteApplicationManager {

	protected static Protocol protocol;
	static {
		if(Utils.isSunVM())
			protocol = new Protocol("https", new EasySSLProtocolSocketFactory(), 443); 
	}

	protected Log logger = LogFactory.getLog(getClass());

	// The version of the API/protocol.
	private static final String PARAMETER_NAME_VERSION = "ss_version";
	// The ID of the application - uniquely assigned to the application. 
	private static final String PARAMETER_NAME_APPLICATION_ID = "ss_application_id";
	// The ID of the context user
	private static final String PARAMETER_NAME_USER_ID = "ss_user_id";
	// The access token
	private static final String PARAMETER_NAME_ACCESS_TOKEN = "ss_access_token";
	// The scope of the access token
	private static final String PARAMETER_NAME_TOKEN_SCOPE = "ss_token_scope";
	// Renderable or not
	private static final String PARAMETER_NAME_RENDERABLE = "ss_renderable";
	// The ID of the binder (optional)
	private static final String PARAMETER_NAME_BINDER_ID = "ss_binder_id";
	// The type of access constraints around the binder (optional) - meaningful
	// only when PARAMETER_NAME_BINDER_ID is specified.
	private static final String PARAMETER_NAME_BINDER_ACCESS_CONSTRAINTS = "ss_binder_access_constraints"; 

	private static final String API_VERSION = "1.0";
	
	private static final int BUFFER_SIZE = 4096;
	
	private static final String TYPE_INTERACTIVE = "interactive";
	private static final String TYPE_BACKGROUND = "background";

	private ProfileDao profileDao;
	private AccessTokenManager accessTokenManager;
	
	protected ProfileDao getProfileDao() {
		return profileDao;
	}

	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}

	protected AccessTokenManager getAccessTokenManager() {
		return accessTokenManager;
	}

	public void setAccessTokenManager(AccessTokenManager accessTokenManager) {
		this.accessTokenManager = accessTokenManager;
	}

	public void executeSessionScopedRenderableAction(Map<String,String> params, Long applicationId, 
			HttpServletRequest request, Writer out) 
	throws RemoteApplicationException {
		RequestContext rc = RequestContextHolder.getRequestContext();
		Application application = getProfileDao().loadApplication(applicationId, rc.getZoneId());
		if(Validator.isNull(application.getPostUrl())) {
			if(logger.isDebugEnabled())
				logger.debug("User " + rc.getUserId() + " invoking application " + applicationId + " with no POST URL");
			return;
		}
		AccessToken accessToken = getAccessTokenManager().getSessionScopedToken
		(applicationId, rc.getUserId(), WebHelper.getTokenInfoId(request));
		try {
			executeAction(params, applicationId, accessToken, out);
		}
		catch(IOException e) {
			throw new RemoteApplicationException(applicationId, e.toString(), e);
		}
	}

	public void executeSessionScopedRenderableAction(Map<String,String> params, Long applicationId, 
			Long binderId, BinderAccessConstraints binderAccessConstraints, 
			HttpServletRequest request, Writer out) 
	throws RemoteApplicationException {
		RequestContext rc = RequestContextHolder.getRequestContext();
		Application application = getProfileDao().loadApplication(applicationId, rc.getZoneId());
		if(Validator.isNull(application.getPostUrl())) {
			if(logger.isDebugEnabled())
				logger.debug("User " + rc.getUserId() + " invoking application " + applicationId + " with no POST URL");
			return;
		}
		AccessToken accessToken = getAccessTokenManager().getSessionScopedToken
		(applicationId, RequestContextHolder.getRequestContext().getUserId(), 
				WebHelper.getTokenInfoId(request), binderId, binderAccessConstraints);
		try {
			executeAction(params, applicationId, accessToken, out);
		}
		catch(IOException e) {
			throw new RemoteApplicationException(applicationId, e.toString(), e);
		}
	}

	private String executeAction(Map<String,String> params, Long applicationId, 
	AccessToken accessToken, Writer out) throws RemoteApplicationException, IOException {
		RequestContext rc = RequestContextHolder.getRequestContext();
		Application application = getProfileDao().loadApplication(applicationId, rc.getZoneId());
		HttpURL hrl = getHttpUrl(application.getPostUrl());
		HttpClient client = new HttpClient();
		HostConfiguration hc = client.getHostConfiguration();
		if((protocol != null) && (hrl instanceof HttpsURL) && 
				SPropsUtil.getBoolean("remoteapp.allow.self.signed.certificate", true))
			hc.setHost(hrl.getHost(), hrl.getPort(), protocol);
		else
			hc.setHost(hrl);
		// Use relative url only, otherwise, SSL connection won't work even with 
		// EasySSLProtocolSocketFactory. 
		PostMethod method = new PostMethod(hrl.getPathQuery());
		try {
			method.addParameter(PARAMETER_NAME_VERSION, API_VERSION);
			method.addParameter(PARAMETER_NAME_APPLICATION_ID, applicationId.toString());
			method.addParameter(PARAMETER_NAME_USER_ID, rc.getUserId().toString());
			method.addParameter(PARAMETER_NAME_ACCESS_TOKEN, accessToken.toStringRepresentation());
			method.addParameter(PARAMETER_NAME_TOKEN_SCOPE, accessToken.getScope().name());
			method.addParameter(PARAMETER_NAME_RENDERABLE, (out != null)? "true" : "false");
			if(accessToken.getBinderId() != null) {
				method.addParameter(PARAMETER_NAME_BINDER_ID, accessToken.getBinderId().toString());
				method.addParameter(PARAMETER_NAME_BINDER_ACCESS_CONSTRAINTS, String.valueOf(accessToken.getBinderAccessConstraints().getNumber()));
			}	
			if(params != null) {
				for(Map.Entry<String, String> entry : params.entrySet()) {
					method.addParameter(entry.getKey(), entry.getValue());
				}
			}
			// A timeout value of zero is interpreted as an infinite timeout.
			method.getParams().setSoTimeout(application.getTimeout()*1000);
			int statusCode = client.executeMethod(method);
			if(statusCode != HttpStatus.SC_OK) {
				StatusLine statusLine = method.getStatusLine();
				throw new RemoteApplicationException(applicationId, statusLine.toString());
			}
			if(out != null) {
				Reader in = getReader(method);

				try {
					copy(in, out); // Do NOT use Spring's FileCopyUtils since it closes both streams.
				}
				finally {
					try {
						in.close();
					}
					catch (IOException ex) {
						logger.warn("Could not close InputStream", ex);
					}
				}
				return null;
			}
			else {
				return method.getResponseBodyAsString();
			}
		}
		finally {
			method.releaseConnection();
		}
	}
	
	private Reader getReader(PostMethod method) throws IOException  {
		Reader reader;
		String charset = method.getResponseCharSet();
		if(charset == null || charset.length() == 0) {
			reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
		}
		else {
			try {
				reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), charset));
			} catch (UnsupportedEncodingException e) {
				logger.warn("Unsupported encoding: " + charset + ". System encoding used.");
				reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
			}
		}
		return reader;
	}
	
	private HttpURL getHttpUrl(String urlStr) throws URIException  {
		if(urlStr.startsWith("https"))
			return new HttpsURL(urlStr);
		else
			return new HttpURL(urlStr);
	}

	public int copy(Reader in, Writer out) throws IOException {
		int byteCount = 0;
		char[] buffer = new char[BUFFER_SIZE];
		int bytesRead = -1;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			byteCount += bytesRead;
		}
		out.flush();
		return byteCount;
	}

	public String executeRequestScopedNonRenderableAction(Map<String, String> params, Long applicationId) throws RemoteApplicationException {
		RequestContext rc = RequestContextHolder.getRequestContext();
		Application application = getProfileDao().loadApplication(applicationId, rc.getZoneId());
		if(Validator.isNull(application.getPostUrl())) {
			if(logger.isDebugEnabled())
				logger.debug("User " + rc.getUserId() + " invoking application " + applicationId + " with no POST URL");
			return null;
		}
		AccessToken accessToken = getAccessTokenManager().getRequestScopedToken
		(applicationId, RequestContextHolder.getRequestContext().getUserId());
		try {
			return executeAction(params, applicationId, accessToken, null);
		}
		catch(IOException e) {
			throw new RemoteApplicationException(applicationId, e.toString(), e);
		}
		finally {
			getAccessTokenManager().destroyRequestScopedToken(accessToken);
		}
	}

	public String executeRequestScopedNonRenderableAction(Map<String, String> params, 
			Long applicationId, Long binderId, BinderAccessConstraints binderAccessConstraints) throws RemoteApplicationException {
		RequestContext rc = RequestContextHolder.getRequestContext();
		Application application = getProfileDao().loadApplication(applicationId, rc.getZoneId());
		if(Validator.isNull(application.getPostUrl())) {
			if(logger.isDebugEnabled())
				logger.debug("User " + rc.getUserId() + " invoking application " + applicationId + " with no POST URL");
			return null;
		}
		AccessToken accessToken = getAccessTokenManager().getRequestScopedToken
		(applicationId, RequestContextHolder.getRequestContext().getUserId(), binderId, binderAccessConstraints);
		try {
			return executeAction(params, applicationId, accessToken, null);
		}
		catch(IOException e) {
			throw new RemoteApplicationException(applicationId, e.toString(), e);
		}
		finally {
			getAccessTokenManager().destroyRequestScopedToken(accessToken);
		}
	}

	/*
	public static void main(String[] args) throws Exception {
		String url = "https://localhost:8443/remoteapp/namesearch";
		HttpURL hrl = new HttpsURL(url);
		HttpClient client = new HttpClient();
		HostConfiguration hc = client.getHostConfiguration();
		hc.setHost(hrl.getHost(), hrl.getPort(), protocol);
		PostMethod method = new PostMethod(hrl.getPathQuery()); // User relative url only!!!
		int statusCode = client.executeMethod(method);
		System.out.println("STATUS CODE: " + statusCode);
		System.out.println(method.getStatusLine());
	}*/
	
	/*
	public static void main(String[] args) {
		String inStr = "<strong>Hey, pay attention everyone!</strong><br><pre>Hello!</pre>";
		java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(inStr.getBytes());
		java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		
		Tidy tidy = new Tidy();
		tidy.setXHTML(true);
		tidy.setOnlyErrors(true);
		Document doc = tidy.parseDOM(in, null);
		tidy.pprint(doc, out);
		String outStr = new String(out.toByteArray());
		System.out.println(outStr);
	}*/
}
