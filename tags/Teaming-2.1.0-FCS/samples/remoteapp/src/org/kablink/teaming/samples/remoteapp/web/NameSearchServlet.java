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
package org.kablink.teaming.samples.remoteapp.web;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.DocumentException;
import org.kablink.teaming.client.ws.WebServiceClientUtil;
import org.kablink.util.servlet.StringServletResponse;

import org.kablink.teaming.client.ws.TeamingServiceSoapBindingStub;
import org.kablink.teaming.client.ws.TeamingServiceSoapServiceLocator;
import org.kablink.teaming.client.ws.model.User;

public class NameSearchServlet extends HttpServlet {

	private static final String GOOGLE_SEARCH_TEMPLATE = "http://www.google.com/search?hl=en&q=@@@&btnG=Google+Search";
	
	private static final String TEAMING_SERVICE_ADDRESS = "http://localhost:8080/ssr/token/ws/TeamingServiceV1";
	

	private static final String PARAMETER_NAME_VERSION = "ss_version";
	private static final String PARAMETER_NAME_APPLICATION_ID = "ss_application_id";
	private static final String PARAMETER_NAME_USER_ID = "ss_user_id";
	private static final String PARAMETER_NAME_ACCESS_TOKEN = "ss_access_token";
	private static final String PARAMETER_NAME_TOKEN_SCOPE = "ss_token_scope";
	private static final String PARAMETER_NAME_RENDERABLE = "ss_renderable";

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
	throws ServletException, IOException {
		try {
			// Get the paramaters passed in.
			String version = req.getParameter(PARAMETER_NAME_VERSION);
			String applicationId = req.getParameter(PARAMETER_NAME_APPLICATION_ID);
			String userId = req.getParameter(PARAMETER_NAME_USER_ID);
			String accessToken = req.getParameter(PARAMETER_NAME_ACCESS_TOKEN);
			String tokenScope = req.getParameter(PARAMETER_NAME_TOKEN_SCOPE);
			boolean renderable = Boolean.parseBoolean(req.getParameter(PARAMETER_NAME_RENDERABLE));
			
			// Get ready for web services calls to the Teaming.
			TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
			locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS);
			TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();

			// To see if access check is working properly
			//stub.search_getWorkspaceTreeAsXML(accessToken, 1, 1, "");
			
			// Get the title of the user by making a web services call.
			String title = getUserTitle(stub, accessToken, Long.valueOf(userId));
			
			// Search Google for the title.
			String result = googleForName(req, resp, title);
			
			// Just to demonstrate how to upload file to the Teaming through web services. 
			// Irrelevant to this sample, so commented out.
			//uploadFile(stub, accessToken);
			
			// Write the response.
			resp.getWriter().print(result);
		}
		catch(ServletException e) {
			throw e;
		}
		catch(IOException e) {
			throw e;
		}
		catch(Exception e) {
			throw new ServletException(e);
		}
	}

	private String getUserTitle(TeamingServiceSoapBindingStub stub, String accessToken, Long userId) throws ServiceException, DocumentException, RemoteException {
		User user = stub.profile_getUser(accessToken, userId, false);
		
		return user.getTitle();
	}
	
	private String googleForName(HttpServletRequest req, HttpServletResponse resp, String userTitle) throws IOException, ServletException {
		String searchStr = userTitle.replace(" ", "+");
		String searchUrl = GOOGLE_SEARCH_TEMPLATE.replace("@@@", searchStr);
		
		String result = "";
		HttpClient httpClient = new HttpClient();
		GetMethod getMethod = new GetMethod(searchUrl);
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if(statusCode == HttpStatus.SC_OK) {
				String body = getMethod.getResponseBodyAsString();
				int idx = body.indexOf("swrnum=");
				if(idx >= 0) {
					int idx2 = body.indexOf("\"", idx+7);
					if(idx2 >= 0) {
						String value = body.substring(idx+7, idx2);
						StringBuilder sb = new StringBuilder();
						
						/*
						// Option 1 - Generate the html markup right here.
						sb.append("<strong>Hey, pay attention everyone!</strong><br>");
						sb.append("<pre>");
						sb.append("About " + value + " matches for " + userTitle + " on Google");
						sb.append("</pre>");
						result = sb.toString();
						*/
						
						// Option 2 - Use JSP template to generate the html markup
						String jsp = "/WEB-INF/jsp/namesearch/view.jsp";	
						RequestDispatcher rd = req.getRequestDispatcher(jsp);	
						StringServletResponse resp2 = new StringServletResponse(resp);	
						req.setAttribute("count", value);
						req.setAttribute("title", userTitle);
						rd.include(req, resp2);	
						result = resp2.getString();
					}
				}
				return result;
			}
			else {
				throw new ServletException(getMethod.getStatusLine().toString());
			}
		}
		finally {
			getMethod.releaseConnection();
		}
	}
	
	private void uploadFile(TeamingServiceSoapBindingStub stub, String accessToken) throws Exception {
		// Do not use this method for general purpose, since it uses hard-coded 
		// binder ID and enry ID, etc. Useful only for one-off testing.
		File file = new File("C:/junk/junk1/chinese-application.doc");
		WebServiceClientUtil.attachFile(stub, file);
		stub.folder_uploadFile(accessToken, 9, "upload", "chinese-application.doc");
	}
}
