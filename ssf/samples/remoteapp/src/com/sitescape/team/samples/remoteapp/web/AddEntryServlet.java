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
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
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

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.client.ws.TeamingServiceSoapBindingStub;
import com.sitescape.team.client.ws.TeamingServiceSoapServiceLocator;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.util.search.Constants;
import com.sitescape.team.search.filter.SearchFilterKeys;
import com.sitescape.util.servlet.StringServletResponse;

public class AddEntryServlet extends HttpServlet {

	private static final String TEAMING_SERVICE_ADDRESS = "http://localhost:8080/ssr/token/ws/TeamingService";
	
	private static final String PARAMETER_NAME_VERSION = "ss_version";
	private static final String PARAMETER_NAME_APPLICATION_ID = "ss_application_id";
	private static final String PARAMETER_NAME_USER_ID = "ss_user_id";
	private static final String PARAMETER_NAME_ACCESS_TOKEN = "ss_access_token";
	private static final String PARAMETER_NAME_TOKEN_SCOPE = "ss_token_scope";
	private static final String PARAMETER_NAME_RENDERABLE = "ss_renderable";

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
	throws ServletException, IOException {
		try {
			String version = req.getParameter(PARAMETER_NAME_VERSION);
			String applicationId = req.getParameter(PARAMETER_NAME_APPLICATION_ID);
			String userId = req.getParameter(PARAMETER_NAME_USER_ID);
			String accessToken = req.getParameter(PARAMETER_NAME_ACCESS_TOKEN);
			String tokenScope = req.getParameter(PARAMETER_NAME_TOKEN_SCOPE);
			boolean renderable = Boolean.parseBoolean(req.getParameter(PARAMETER_NAME_RENDERABLE));
			String pathInfo = req.getPathInfo();
			if (pathInfo.equals("/form")) {
				String jsp = "/WEB-INF/jsp/addentry/entry_form.jsp";				
				RequestDispatcher rd = req.getRequestDispatcher(jsp);	
				StringServletResponse resp2 = new StringServletResponse(resp);	
				rd.include(req, resp2);	
				resp.getWriter().print(resp2.getString());
			} else {
			
				Document tasksDoc = getTaskList(accessToken, Long.valueOf(userId));
				
				String result = tasksDoc.toString();
				
				resp.getWriter().print(result);
			}
		}
		catch(IOException e) {
			throw e;
		}
		catch(Exception e) {
			throw new ServletException(e);
		}
	}

	private Document getTaskList(String accessToken, Long userId) throws ServiceException, DocumentException, RemoteException {
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator.getTeamingService();
		Document query = DocumentHelper.createDocument();
		Element rootElement = query.addElement(Constants.AND_ELEMENT);
		Element field = rootElement.addElement(Constants.FIELD_ELEMENT);
		field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,Constants.FAMILY_FIELD);
		Element child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
		child.setText(Constants.FAMILY_FIELD_TASK);
		field = rootElement.addElement(Constants.FIELD_ELEMENT);
		field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,Constants.ENTRY_TYPE_FIELD);
		child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
		child.setText(Constants.ENTRY_TYPE_ENTRY);
		String searchResultsAsXML = stub.search_search(accessToken, query.asXML(), 0, 10);
		
		Document doc = DocumentHelper.parseText(searchResultsAsXML);
		Element rootElem = doc.getRootElement();
		
		return doc;
	}
}
