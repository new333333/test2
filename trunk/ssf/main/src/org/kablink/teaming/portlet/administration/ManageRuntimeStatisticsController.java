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
package org.kablink.teaming.portlet.administration;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.keyshield.KShieldHelper;
import org.kablink.teaming.remoting.ws.service.search.SearchService;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.portlet.ModelAndView;

public class ManageRuntimeStatisticsController extends SAbstractController {

	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
	}
	
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if(WebKeys.MRS_OPERATION_DUMP.equals(op)) {
			String data = getAdminModule().dumpRuntimeStatisticsAsString();
			reportData(response, data);
		}
		else if(WebKeys.MRS_OPERATION_DUMP_TO_LOG.equals(op)) {
			getAdminModule().dumpRuntimeStatisticsToLog();
			reportSuccess(response);
		}
		else if(WebKeys.MRS_OPERATION_ENABLE_SIMPLE_PROFILER.equals(op)) {
			getAdminModule().enableSimpleProfiler();
			reportSuccess(response);
		}
		else if(WebKeys.MRS_OPERATION_DISABLE_SIMPLE_PROFILER.equals(op)) {
			getAdminModule().disableSimpleProfiler();
			reportSuccess(response);
		}
		else if(WebKeys.MRS_OPERATION_CLEAR.equals(op)) {
			getAdminModule().clearSimpleProfiler();
			reportSuccess(response);
		}
		else if(WebKeys.MRS_OPERATION_SEARCH.equals(op)) {
			doSearch(response,
					PortletRequestUtils.getStringParameter(request, "query", ""),
					PortletRequestUtils.getIntParameter(request, "offset", 0),
					PortletRequestUtils.getIntParameter(request, "max", 25));
		}
		else if(WebKeys.MRS_OPERATION_BROADCAST.equals(op)) {
			doBroadcast(response,
					PortletRequestUtils.getStringParameter(request, "message", ""));
		}
		else if(WebKeys.MRS_OPERATION_DUMP_FILE_SYNC_STATS.equals(op)) {
			String data = getAdminModule().dumpFileSyncStatsAsString();
			reportData(response, data);
		}
		else if(WebKeys.MRS_OPERATION_DUMP_FILE_SYNC_STATS_TO_LOG.equals(op)) {
			getAdminModule().dumpFileSyncStatsToLog();
			reportSuccess(response);
		}
		else if(WebKeys.MRS_OPERATION_ENABLE_FILE_SYNC_STATS.equals(op)) {
			getAdminModule().enableFileSyncStats();
			reportSuccess(response);
		}
		else if(WebKeys.MRS_OPERATION_DISABLE_FILE_SYNC_STATS.equals(op)) {
			getAdminModule().disableFileSyncStats();
			reportSuccess(response);
		}
		else if(WebKeys.MRS_OPERATION_STOP_FILE_SYNC.equals(op)) {
			long id = PortletRequestUtils.getLongParameter(request, "id", 0);
			getFolderModule().requestNetFolderFullSyncStop(id);
			reportSuccess(response);
		}
		else if(WebKeys.MRS_OPERATION_KSHIELD.equals(op)) {
			String value = PortletRequestUtils.getStringParameter(request, "token", "off");
			if(value.equalsIgnoreCase("on") || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("1"))
				KShieldHelper.pretendHardwareTokenIsPresent = true;
			else
				KShieldHelper.pretendHardwareTokenIsPresent = false;
			reportData(response, String.valueOf(KShieldHelper.pretendHardwareTokenIsPresent));
		}
		else if(op.equals("")) {
			reportNoop(response);
		}
		else {
			reportUnrecognized(response);
		}
		// Right now, this controller renders very little.
		return null;
	}
	
	protected void reportData(RenderResponse response, String data) throws IOException {
		data = data.replace(Constants.NEWLINE, "<br/>");
		response.getWriter().write("<html><head></head><body>" + data + "</body></html>");
	}
	
	protected void reportSuccess(RenderResponse response) throws IOException {
		response.getWriter().write("<html><head></head><body>Success!</body></html>");
	}
	
	protected void reportNoop(RenderResponse response) throws IOException {
		response.getWriter().write("<html><head></head><body>No operation specified</body></html>");
	}
	
	protected void reportUnrecognized(RenderResponse response) throws IOException {
		response.getWriter().write("<html><head></head><body>Unrecognized operation</body></html>");
	}
	
	protected void doSearch(RenderResponse response, String qry, int offset, int maxResults) throws IOException {
		long totalStartTime = System.nanoTime();;
		
		String query = "<QUERY><AND><AND><FIELD exactphrase=\"false\"><TERMS>" + qry + "</TERMS></FIELD></AND></AND></QUERY>";
		
		long startTime = System.nanoTime();
		BinderModule bm = (BinderModule) SpringContextUtil.getBean("binderModule");
		Document queryDoc = getDocument(query);
		Map entries = bm.executeSearchQuery(queryDoc, org.kablink.util.search.Constants.SEARCH_MODE_NORMAL, offset, maxResults,
				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(org.kablink.util.search.Constants.DOC_TYPE_FIELD,org.kablink.util.search.Constants.DOCID_FIELD));
		double duration = elapsedTimeInMs(startTime);

		Integer total = (Integer) entries.get(ObjectKeys.SEARCH_COUNT_TOTAL);

		StringBuilder sb = new StringBuilder();
		sb.append("<html><head></head><body>").
		append("Search query = [" + qry + "]<br>").
		append("Total count = " + total + "<p>").
		append("BinderModule.executeSearchQuery(Document,int,int) took ").append(duration).append(" milliseconds to execute<p>");
		List entrylist = (List)entries.get(ObjectKeys.SEARCH_ENTRIES);
		
		Iterator entryIterator = entrylist.listIterator();
		int i = 1;
		while (entryIterator.hasNext()) {
			Map result = (Map) entryIterator.next();
			String docType = (String) result.get(org.kablink.util.search.Constants.DOC_TYPE_FIELD);
			String docId = (String) result.get(org.kablink.util.search.Constants.DOCID_FIELD);
			sb.append("(").append(i++).append(") docType=").append(docType).append(" docId=").append(docId).append("<br>");
		}
		sb.append("<p>Total time is ").append(elapsedTimeInMs(totalStartTime)).append(" ms").
		append("</body></html>");
		response.getWriter().write(sb.toString());
	}
	
	protected void doBroadcast(RenderResponse response, String message) throws IOException {
		RabbitTemplate rabbitTemplate = (RabbitTemplate) SpringContextUtil.getBean("rabbitTemplate");
		rabbitTemplate.convertAndSend("broadcast_exchange", "", message);
		String reply = "<html><head></head><body>Successfully dropped the message in the broadcast exchange</body></html>";
		response.getWriter().write(reply);
	}
	
	protected Document getDocument(String xml) {
		// Parse XML string into a document tree.
		try {
			return DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			logger.error(e);
			throw new IllegalArgumentException(e.toString());
		}
	}
	
	protected double elapsedTimeInMs(long startTimeInNanoseconds) {
		return (System.nanoTime() - startTimeInNanoseconds)/1000000.0;
	}

}
