/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.remoteapplication.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

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

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.Application;
import com.sitescape.team.remoteapplication.RemoteApplicationException;
import com.sitescape.team.remoteapplication.RemoteApplicationManager;
import com.sitescape.team.security.accesstoken.AccessToken;
import com.sitescape.team.security.accesstoken.AccessTokenManager;
import com.sitescape.team.security.accesstoken.AccessToken.BinderAccessConstraints;
import com.sitescape.team.util.SPropsUtil;

public class RemoteApplicationManagerImpl implements RemoteApplicationManager {

	protected static Protocol protocol;
	static {
		protocol = new Protocol("https", new EasySSLProtocolSocketFactory(), 443); 
	}

	protected Log logger = LogFactory.getLog(getClass());

	private static final String PARAMETER_NAME_ACTION = "ss_action_name";
	private static final String PARAMETER_NAME_APPLICATION_ID = "ss_application_id";
	private static final String PARAMETER_NAME_USER_ID = "ss_user_id";
	private static final String PARAMETER_NAME_ACCESS_TOKEN = "ss_access_token";
	private static final String PARAMETER_NAME_BINDER_ID = "ss_binder_id";
	private static final String PARAMETER_NAME_BINDER_ACCESS_CONSTRAINTS = "ss_binder_access_constraints"; 

	private static final int BUFFER_SIZE = 4096;

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

	public void executeInteractiveAction(Action action, Map<String,String> params, Long applicationId, 
			String tokenInfoId, OutputStream out) throws RemoteApplicationException {
		AccessToken accessToken = getAccessTokenManager().newInteractiveToken(applicationId, tokenInfoId);
		try {
			executeInteractiveAction(action, params, applicationId, accessToken, out);
		}
		catch(IOException e) {
			throw new RemoteApplicationException(applicationId, e.toString(), e);
		}
	}

	public void executeInteractiveAction(Action action, Map<String,String> params, Long applicationId, String tokenInfoId, 
			Long binderId, BinderAccessConstraints binderAccessConstraints, OutputStream out) 
	throws RemoteApplicationException {
		AccessToken accessToken = getAccessTokenManager().newInteractiveToken
		(applicationId, tokenInfoId, binderId, binderAccessConstraints);
		try {
			executeInteractiveAction(action, params, applicationId, accessToken, out);
		}
		catch(IOException e) {
			throw new RemoteApplicationException(applicationId, e.toString(), e);
		}
	}

	private void executeInteractiveAction(Action action, Map<String,String> params, Long applicationId, 
	AccessToken accessToken, OutputStream out) throws RemoteApplicationException, IOException {
		RequestContext rc = RequestContextHolder.getRequestContext();
		Application application = getProfileDao().loadApplication(applicationId, rc.getZoneId());
		HttpURL hrl = getHttpUrl(application.getPostUrl());
		HttpClient client = new HttpClient();
		HostConfiguration hc = client.getHostConfiguration();
		if(hrl instanceof HttpsURL && 
				SPropsUtil.getBoolean("remoteapp.allow.self.signed.certificate", true))
			hc.setHost(hrl.getHost(), hrl.getPort(), protocol);
		else
			hc.setHost(hrl);
		PostMethod method = new PostMethod(application.getPostUrl());
		try {
			method.addParameter(PARAMETER_NAME_ACTION, action.name());
			if(params != null) {
				for(Map.Entry<String, String> entry : params.entrySet()) {
					method.addParameter(entry.getKey(), entry.getValue());
				}
			}
			method.addParameter(PARAMETER_NAME_APPLICATION_ID, applicationId.toString());
			method.addParameter(PARAMETER_NAME_USER_ID, rc.getUserId().toString());
			method.addParameter(PARAMETER_NAME_ACCESS_TOKEN, accessToken.toStringRepresentation());
			if(accessToken.getBinderId() != null) {
				method.addParameter(PARAMETER_NAME_BINDER_ID, accessToken.getBinderId().toString());
				method.addParameter(PARAMETER_NAME_BINDER_ACCESS_CONSTRAINTS, String.valueOf(accessToken.getBinderAccessConstraints().getNumber()));
			}	
			int timeout = SPropsUtil.getInt("remoteapp.so.timeout", 0);
			if(timeout > 0) {
				method.getParams().setSoTimeout(timeout);
			}
			int statusCode = client.executeMethod(method);
			if(statusCode != HttpStatus.SC_OK) {
				StatusLine statusLine = method.getStatusLine();
				throw new RemoteApplicationException(applicationId, statusLine.toString());
			}
			InputStream in = method.getResponseBodyAsStream();
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
		}
		finally {
			method.releaseConnection();
		}
	}
	
	private HttpURL getHttpUrl(String urlStr) throws URIException  {
		if(urlStr.startsWith("https"))
			return new HttpsURL(urlStr);
		else
			return new HttpURL(urlStr);
	}

	public int copy(InputStream in, OutputStream out) throws IOException {
		int byteCount = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			byteCount += bytesRead;
		}
		out.flush();
		return byteCount;
	}

}
