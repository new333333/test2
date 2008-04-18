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
package com.sitescape.team.samples.remoteapp.web;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.client.ws.folder.FolderServiceSoapBindingStub;
import com.sitescape.team.client.ws.folder.FolderServiceSoapServiceLocator;
import com.sitescape.team.client.ws.profile.ProfileServiceSoapBindingStub;
import com.sitescape.team.client.ws.profile.ProfileServiceSoapServiceLocator;

public class NameSearchServlet extends HttpServlet {

	private static final String GOOGLE_SEARCH_TEMPLATE = "http://www.google.com/search?hl=en&q=@@@&btnG=Google+Search";
	
	private static final String PROFILE_SERVICE_ADDRESS = "http://localhost:8080/ssr/token/ws/ProfileService";
	private static final String FOLDER_SERVICE_ADDRESS = "http://localhost:8080/ssr/token/ws/FolderService";
	private static final Long PROFILE_BINDER_ID = Long.valueOf(2);
	
	private static final String PARAMETER_NAME_ACTION = "ss_action_name";
	private static final String PARAMETER_NAME_VERSION = "ss_version";
	private static final String PARAMETER_NAME_APPLICATION_ID = "ss_application_id";
	private static final String PARAMETER_NAME_USER_ID = "ss_user_id";
	private static final String PARAMETER_NAME_ACCESS_TOKEN = "ss_access_token";

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
	throws ServletException, IOException {
		try {
			String action = req.getParameter(PARAMETER_NAME_ACTION);
			String version = req.getParameter(PARAMETER_NAME_VERSION);
			String applicationId = req.getParameter(PARAMETER_NAME_APPLICATION_ID);
			String userId = req.getParameter(PARAMETER_NAME_USER_ID);
			String accessToken = req.getParameter(PARAMETER_NAME_ACCESS_TOKEN);
			
			String title = getUserTitle(accessToken, Long.valueOf(userId));
			
			String result = googleForName(title);
			
			//uploadFile(accessToken);
			
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

	private String getUserTitle(String accessToken, Long userId) throws ServiceException, DocumentException, RemoteException {
		ProfileServiceSoapServiceLocator locator = new ProfileServiceSoapServiceLocator();
		locator.setProfileServiceEndpointAddress(PROFILE_SERVICE_ADDRESS);
		ProfileServiceSoapBindingStub stub = (ProfileServiceSoapBindingStub) locator.getProfileService();
		String principalAsXML = stub.getPrincipalAsXML(accessToken, PROFILE_BINDER_ID, userId);
		
		Document doc = DocumentHelper.parseText(principalAsXML);
		Element rootElem = doc.getRootElement();
		Element elm = (Element) rootElem.selectSingleNode("/principal");
		String userTitle = elm.attributeValue("title");
		
		return userTitle;
	}
	
	private String googleForName(String userTitle) throws IOException, ServletException {
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
						sb.append("<strong>Hey, pay attention everyone!</strong><br>");
						sb.append("<pre>");
						sb.append("About " + value + " matches for " + userTitle + " on Google");
						sb.append("</pre>");
						result = sb.toString();
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
	
	private void uploadFile(String accessToken) throws Exception {
		// Do not use this method for general purpose, since it uses hard-coded 
		// binder ID and enry ID, etc. Useful only for one shot testing.
		FolderServiceSoapServiceLocator locator = new FolderServiceSoapServiceLocator();
		locator.setFolderServiceEndpointAddress(FOLDER_SERVICE_ADDRESS);
		FolderServiceSoapBindingStub stub = (FolderServiceSoapBindingStub) locator.getFolderService();
		DataHandler dhSource = new DataHandler(new FileDataSource(new File("C:/junk/junk1/chinese-application.doc")));
		stub.addAttachment(dhSource);
		stub._setProperty(Call.ATTACHMENT_ENCAPSULATION_FORMAT, Call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);
		stub.uploadFolderFile(accessToken, 33, 9, "upload", "chinese-application.doc");
	}
}
