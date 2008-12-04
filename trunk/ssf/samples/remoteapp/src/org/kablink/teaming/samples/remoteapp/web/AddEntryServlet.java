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
package org.kablink.teaming.samples.remoteapp.web;

import static org.kablink.util.search.Constants.ENTRY_TYPE_ENTRY;
import static org.kablink.util.search.Constants.ENTRY_TYPE_FIELD;
import static org.kablink.util.search.Constants.FAMILY_FIELD;
import static org.kablink.util.search.Constants.FAMILY_FIELD_TASK;
import static org.kablink.util.search.Restrictions.eq;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

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
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.servlet.StringServletResponse;

import org.kablink.teaming.client.ws.TeamingServiceSoapBindingStub;
import org.kablink.teaming.client.ws.TeamingServiceSoapServiceLocator;
import org.kablink.teaming.client.ws.model.Description;
import org.kablink.teaming.client.ws.model.FolderEntry;

public class AddEntryServlet extends HttpServlet {

	private static final String TEAMING_SERVICE_ADDRESS = "http://localhost:8080/ssr/token/ws/TeamingService";
	
	private static final String PARAMETER_NAME_VERSION = "ss_version";
	private static final String PARAMETER_NAME_APPLICATION_ID = "ss_application_id";
	private static final String PARAMETER_NAME_USER_ID = "ss_user_id";
	private static final String PARAMETER_NAME_ACCESS_TOKEN = "ss_access_token";
	private static final String PARAMETER_NAME_TOKEN_SCOPE = "ss_token_scope";
	private static final String PARAMETER_NAME_RENDERABLE = "ss_renderable";

	private static final String PARAMETER_FORM_TITLE = "title";
	private static final String PARAMETER_FORM_DESCRIPTION = "description";
	private static final String PARAMETER_FORM_BINDER_ID = "binderId";
	private static final String PARAMETER_FORM_ENTRY_ID = "entryId";
	private static final String PARAMETER_FORM_OPERATION = "operation";
	private static final String PARAMETER_FORM_OPERATION_ENTRY_VIEW = "view";
	private static final String PARAMETER_FORM_OPERATION_ENTRY_FORM = "form";
	private static final String PARAMETER_FORM_DEFINITION_ID = "definitionId";
	private static final String PARAMETER_FORM_RETURN_URL = "returnUrl";
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
				String entryId = req.getParameter(PARAMETER_FORM_ENTRY_ID);
				String operation = req.getParameter(PARAMETER_FORM_OPERATION);
				String definitionId = req.getParameter(PARAMETER_FORM_DEFINITION_ID);
				if (definitionId == null) definitionId = ObjectKeys.DEFAULT_FOLDER_ENTRY_DEF;
				String jsp = "/WEB-INF/jsp/addentry/entry_form.jsp";
				if (PARAMETER_FORM_OPERATION_ENTRY_FORM.equals(operation)) {
					jsp = "/WEB-INF/jsp/addentry/entry_view.jsp";
				}
				RequestDispatcher rd = req.getRequestDispatcher(jsp);	
				StringServletResponse resp2 = new StringServletResponse(resp);	
				req.setAttribute(PARAMETER_NAME_ACCESS_TOKEN, accessToken);
				req.setAttribute(PARAMETER_NAME_USER_ID, userId);
				req.setAttribute(PARAMETER_FORM_BINDER_ID, binderId);
				req.setAttribute(PARAMETER_FORM_ENTRY_ID, entryId);
				req.setAttribute(PARAMETER_FORM_DEFINITION_ID, definitionId);
				rd.include(req, resp2);	
				resp.getWriter().print(resp2.getString());
			
			} else if (pathInfo.equals("/submit")) {
				Map params = req.getParameterMap();
				String result = "";
				if (params.containsKey(PARAMETER_FORM_BUTTON_OK)) {
					String title = req.getParameter(PARAMETER_FORM_TITLE);
					String description = req.getParameter(PARAMETER_FORM_DESCRIPTION);
					String binderId = req.getParameter(PARAMETER_FORM_BINDER_ID);
					String definitionId = req.getParameter(PARAMETER_FORM_DEFINITION_ID);
					String returnUrl = req.getParameter(PARAMETER_FORM_RETURN_URL);

					TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
					locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS);
					TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator
							.getTeamingService();

					FolderEntry entry = new FolderEntry();
					entry.setParentBinderId(Long.valueOf(binderId));
					entry.setTitle(title);
					Description desc = new Description();
					desc.setFormat(1);					
					desc.setText(description);					
					entry.setDescription(desc);
					Long entryId = stub.folder_addEntry(accessToken, entry, null);
					
					/*
					Document entryDoc = DocumentHelper.createDocument();
					Element rootElement = entryDoc.addElement("entry");
					Element titleElement = rootElement.addElement("attribute");
					titleElement.addAttribute("name", "title");
					titleElement.setText(title);
					Element descriptionElement = rootElement.addElement("attribute");
					descriptionElement.addAttribute("name", "description");
					descriptionElement.addAttribute("type", "description");
					descriptionElement.addAttribute("format", "1");
					descriptionElement.setText(description);
					Long entryId = stub.folder_addFolderEntry(accessToken, new Long(binderId), 
							definitionId, entryDoc.asXML(), null);
					*/
					
					String jsp = "/WEB-INF/jsp/addentry/entry_return.jsp";				
					RequestDispatcher rd = req.getRequestDispatcher(jsp);	
					StringServletResponse resp2 = new StringServletResponse(resp);	
					req.setAttribute(PARAMETER_NAME_ACCESS_TOKEN, accessToken);
					req.setAttribute(PARAMETER_NAME_USER_ID, userId);
					req.setAttribute(PARAMETER_FORM_BINDER_ID, binderId);
					req.setAttribute(PARAMETER_FORM_DEFINITION_ID, definitionId);
					req.setAttribute(PARAMETER_FORM_RETURN_URL, returnUrl);
					rd.include(req, resp2);	
					resp.getWriter().print(resp2.getString());
				} else if (params.containsKey(PARAMETER_FORM_BUTTON_CANCEL)) {
					//Cancel button
				}
				resp.getWriter().print(result);
				
			} else {
				String result = "Error: URL must end with \"/form\" or \"/submit\"";
				resp.getWriter().print(result);
			}
		}
		catch(IOException e) {
			String result = "Error: an unexpected error occurred: ";
			resp.getWriter().print(result);
			resp.getWriter().print(e.toString());
		}
		catch(Exception e) {
			String result = "Error: an unexpected error occurred: ";
			resp.getWriter().print(result);
			resp.getWriter().print(e.toString());
		}
	}
}
