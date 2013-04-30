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

import static org.kablink.util.search.Constants.*;
import static org.kablink.util.search.Restrictions.eq;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.servlet.RequestDispatcher;
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
import org.kablink.teaming.task.TaskHelper;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.servlet.StringServletResponse;

import org.kablink.teaming.client.ws.TeamingServiceSoapBindingStub;
import org.kablink.teaming.client.ws.TeamingServiceSoapServiceLocator;

public class TaskListServlet extends HttpServlet {

	private static final String TEAMING_SERVICE_ADDRESS = "http://localhost:8080/ssr/token/ws/TeamingServiceV1";

	private static final Long PROFILE_BINDER_ID = Long.valueOf(2);

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

			List taskList = getTaskList(accessToken, Long.valueOf(userId));

			String jsp = "/WEB-INF/jsp/tasklist/view_task_list.jsp";
			RequestDispatcher rd = req.getRequestDispatcher(jsp);
			StringServletResponse resp2 = new StringServletResponse(resp);
			req.setAttribute("taskList", taskList);
			rd.include(req, resp2);
			resp.getWriter().print(resp2.getString());
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private List getTaskList(String accessToken, Long userId)
			throws ServiceException, DocumentException, RemoteException {
		List taskList = new ArrayList();
		TeamingServiceSoapServiceLocator locator = new TeamingServiceSoapServiceLocator();
		locator.setTeamingServiceEndpointAddress(TEAMING_SERVICE_ADDRESS);
		TeamingServiceSoapBindingStub stub = (TeamingServiceSoapBindingStub) locator
				.getTeamingService();

		Criteria crit = new Criteria();
		crit.add(eq(FAMILY_FIELD, FAMILY_FIELD_TASK))
			.add(eq(ENTRY_TYPE_FIELD, ENTRY_TYPE_ENTRY))
			.add(eq(TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME, userId.toString()));

		Document query = crit.toQuery();

		String searchResultsAsXML = stub.search_search(accessToken, query
				.asXML(), 0, 10);

		Document doc = DocumentHelper.parseText(searchResultsAsXML);
		Element rootElem = doc.getRootElement();

		Iterator entryIterator = rootElem.elementIterator();
		while (entryIterator.hasNext()) {
			Element entryEle = (Element) entryIterator.next();
			Long docId = Long.valueOf(((String) entryEle.attributeValue("id")));
			Long binderId = Long.valueOf(((String) entryEle
					.attributeValue("binderId")));
			Map entryMap = new HashMap();
			entryMap.put("id", Long.valueOf(((String) entryEle
					.attributeValue("id"))));
			entryMap.put("binderId", Long.valueOf(((String) entryEle
					.attributeValue("binderId"))));
			entryMap.put("title", (String) entryEle.attributeValue("title"));
			entryMap.put("href", (String) entryEle.attributeValue("href"));
			taskList.add(entryMap);
		}

		return taskList;
	}
}
