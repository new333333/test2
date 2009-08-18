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

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.util.search.Constants;
import org.kablink.util.servlet.StringServletResponse;

import org.kablink.teaming.client.ws.TeamingServiceSoapBindingStub;
import org.kablink.teaming.client.ws.TeamingServiceSoapServiceLocator;
import org.kablink.teaming.client.ws.model.FolderEntry;

public class WSTesterServlet extends HttpServlet {

	private static final String TEAMING_SERVICE_ADDRESS = "http://localhost:8080/ssr/token/ws/TeamingServiceV1";
	
	private static final String PARAMETER_NAME_VERSION = "ss_version";
	private static final String PARAMETER_NAME_APPLICATION_ID = "ss_application_id";
	private static final String PARAMETER_NAME_USER_ID = "ss_user_id";
	private static final String PARAMETER_NAME_ACCESS_TOKEN = "ss_access_token";
	private static final String PARAMETER_NAME_TOKEN_SCOPE = "ss_token_scope";
	private static final String PARAMETER_NAME_RENDERABLE = "ss_renderable";

	private static final String PARAMETER_FORM_TITLE = "title";
	private static final String PARAMETER_FORM_DESCRIPTION = "description";
	private static final String PARAMETER_FORM_BINDER_ID = "binderId";
	private static final String PARAMETER_FORM_DEFINITION_ID = "definitionId";
	private static final String PARAMETER_FORM_BUTTON_OK = "okBtn";
	private static final String PARAMETER_FORM_BUTTON_CANCEL = "cancelBtn";

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
				String binderId = req.getParameter(PARAMETER_FORM_BINDER_ID);
				String jsp = "/WEB-INF/jsp/ws_tester/tester.jsp";				
				RequestDispatcher rd = req.getRequestDispatcher(jsp);	
				StringServletResponse resp2 = new StringServletResponse(resp);	
				req.setAttribute(PARAMETER_NAME_ACCESS_TOKEN, accessToken);
				req.setAttribute(PARAMETER_NAME_USER_ID, userId);
				req.setAttribute(PARAMETER_FORM_BINDER_ID, binderId);
				rd.include(req, resp2);	
				resp.getWriter().print(resp2.getString());
			
			} else if (pathInfo.equals("/submit")) {
				Map params = req.getParameterMap();
				String result = "";
				if (params.containsKey(PARAMETER_FORM_BUTTON_OK)) {
					String operation = req.getParameter("operation");
					if (operation == null) operation = "";

					TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
					locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS);
					TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator
							.getTeamingService();

					if (operation.equals("folder_getFolderEntry")) {
						String binderId = req.getParameter("binderId");
						String entryId = req.getParameter("entryId");
						String includeAttachments = req.getParameter("includeAttachments");
						FolderEntry entry = stub.folder_getEntry(accessToken, 
								new Long(entryId), Boolean.valueOf(includeAttachments), false);
						result = entry.toString();
					
					/*
					 * folder_getFolderEntryAsXML and folder_addFolderEntry operations that use XML
					 * string as input/output are removed from the new Teaming web services interface.
					 * Use instead the new folder_addEntry opetation taking FolderEntry object as input.
					 *  
					} else if (operation.equals("folder_getFolderEntryAsXML")) {
						String binderId = req.getParameter("binderId_getFolderEntry");
						String entryId = req.getParameter("entryId_getFolderEntry");
						String includeAttachments = req.getParameter("includeAttachments_getFolderEntry");
						String entryXml = stub.folder_getFolderEntryAsXML(accessToken, new Long(binderId), 
								new Long(entryId), Boolean.valueOf(includeAttachments));
						result = entryXml;
					
					} else if (operation.equals("folder_addFolderEntry")) {
						String binderId = req.getParameter("binderId_addFolderEntry");
						String definitionId = req.getParameter("definitionId_addFolderEntry");
						String entryXml = req.getParameter("entryXml");
						Long entryId = stub.folder_addFolderEntry(accessToken, new Long(binderId), 
								definitionId, entryXml, "");
						result = entryId.toString();
					*/
					} else if (operation.equals("search_search")) {
						String query = req.getParameter("query");
						String start = req.getParameter("startCount");
						if (start == null) start = "0";
						String max = req.getParameter("maxCount");
						if (max == null) max = "10";
						result = stub.search_search(accessToken, query, new Integer(start), new Integer(max));
						
					}
					
				} else if (params.containsKey(PARAMETER_FORM_BUTTON_CANCEL)) {
					//Cancel button
				}
				resp.getWriter().print(result);
				
			} else {
				String result = "error: URL must end with \"/form\" or \"/submit\"";
				
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
}
