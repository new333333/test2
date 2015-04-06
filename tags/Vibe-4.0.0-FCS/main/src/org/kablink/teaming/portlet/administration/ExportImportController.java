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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.activation.FileTypeMap;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.zip.ZipOutputStream;
import org.dom4j.Document;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.portletadapter.MultipartFileSupport;
import org.kablink.teaming.portletadapter.portlet.HttpServletResponseReachable;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.StatusTicket;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.tree.TreeHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.ExportException;
import org.kablink.teaming.web.util.ExportHelper;
import org.kablink.teaming.web.util.ListFolderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebStatusTicket;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.ModelAndView;

public class ExportImportController  extends  SAbstractController {

	private Long binderId;
	private Map options;
	private String filename = "export.zip";
	private FileTypeMap mimeTypes = new ConfigurableMimeFileTypeMap();
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.OPERATION, "");
		
		Map formData = request.getParameterMap();
		if(operation.equals(WebKeys.OPERATION_IMPORT)){
			binderId = PortletRequestUtils.getLongParameter(request,  WebKeys.URL_BINDER_ID);
			Binder binder = getBinderModule().getBinder(binderId);
			getBinderModule().checkAccess(binder, BinderOperation.export);
			
			if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
				Map fileMap=null;
				if (request instanceof MultipartFileSupport) {
					// Create a status ticket
					StatusTicket statusTicket = WebStatusTicket.newStatusTicket(
							PortletRequestUtils.getStringParameter(request, WebKeys.URL_STATUS_TICKET_ID, "none"), request);
					
					fileMap = ((MultipartFileSupport) request).getFileMap();
			    	MultipartFile myFile = (MultipartFile)fileMap.get("imports");
			    	InputStream fIn = myFile.getInputStream();
			    	BufferedInputStream b_fIn = new BufferedInputStream(fIn);
			    	
					Map reportMap = new HashMap();
					reportMap.put(ExportHelper.workspaces, new Integer(0));
					reportMap.put(ExportHelper.folders, new Integer(0));
					reportMap.put(ExportHelper.entries, new Integer(0));
					reportMap.put(ExportHelper.files, new Integer(0));
					reportMap.put(ExportHelper.errors, new Integer(0));
					reportMap.put(ExportHelper.errorList, new ArrayList<String>());
			    	try {
			    		ExportHelper.importZip(binderId, fIn, statusTicket, reportMap);	
			    	} catch(ExportException e) {
			    		reportMap.put("errors", (Integer)reportMap.get("errors") + 1);
			    		List eList = (List)reportMap.get("errorList");
			    		eList.add(e.getMessage());
			    	}

					String[] reportData = new String[] {
							((Integer)reportMap.get(ExportHelper.workspaces)).toString(),
							((Integer)reportMap.get(ExportHelper.folders)).toString(),
							((Integer)reportMap.get(ExportHelper.entries)).toString(),
							((Integer)reportMap.get(ExportHelper.files)).toString(),
							((Integer)reportMap.get(ExportHelper.errors)).toString()
						};
					statusTicket.setStatus(NLT.get("administration.export_import.importReport", reportData) + 
							getFormattedErrorMessages((List<String>) reportMap.get(ExportHelper.errorList)));
			    	statusTicket.done();
			    	request.setAttribute("ss_reportData", reportMap);
				} else {
					response.setRenderParameters(formData);
				}
			} else
				response.setRenderParameters(formData);
		}
		if (formData.containsKey("closeBtn") && WebHelper.isMethodPost(request)) {
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
		}
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		binderId = PortletRequestUtils.getLongParameter(request,  WebKeys.URL_BINDER_ID);
		Binder binder = getBinderModule().getBinder(binderId);
		getBinderModule().checkAccess(binder, BinderOperation.export);
		
		HttpServletResponse res = ((HttpServletResponseReachable)response).getHttpServletResponse();		
		Map<String,Object> model = new HashMap<String,Object>();
		
		// Create a status ticket
		String statusWindow = PortletRequestUtils.getStringParameter(request, WebKeys.URL_STATUS_WINDOW, "");
		String statusTicketId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_STATUS_TICKET_ID, "none");
		if (!statusWindow.equals("")) {
			//This is the status window frame; start it
			model.put(WebKeys.URL_BINDER_ID, binderId);
			model.put("ss_statusWindow", true);
			return new ModelAndView(WebKeys.VIEW_ADMIN_EXPORT_IMPORT, model);
		}
		StatusTicket statusTicket = WebStatusTicket.newStatusTicket(statusTicketId, request);

		try {
			//Set up the standard beans
			BinderHelper.setupStandardBeans(this, request, response, model, binderId);
		} catch(NoBinderByTheIdException exc) {
			statusTicket.setStatus(exc.getLocalizedMessage());
			res.setContentType(mimeTypes.getContentType(filename));
			res.setHeader("Cache-Control", "private");
			res.setHeader(
						"Content-Disposition",
						"attachment; filename=\"" + filename + "\"");
			
			ZipOutputStream zipOut = new ZipOutputStream(res.getOutputStream());
			
			//Standard zip encoding is cp437. (needed when chars are outside the ASCII range)
			zipOut.setEncoding("cp437");
			zipOut.finish();
			
			return null;
		}
	
		String entryIdStr = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		Long entryId = null;
		if (!entryIdStr.equals("")) entryId = new Long(entryIdStr);
		
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.OPERATION, "");
		
		boolean showMenu = PortletRequestUtils.getBooleanParameter(request, WebKeys.URL_SHOW_MENU, false);
		
		if (showMenu) {
			//not ajax request
			model.put(WebKeys.URL_BINDER_ID, binderId);
			Document tree = null;
			try {
				tree = getBinderModule().getDomBinderTree(binderId, 
						new WsDomTreeBuilder(null, true, this),0);
			} catch (AccessControlException ac) {}
			model.put(WebKeys.FOLDER_DOM_TREE, tree);

			return new ModelAndView(WebKeys.VIEW_ADMIN_EXPORT_IMPORT, model);
		}
		
		//EXPORTING...
		if (operation.equals(WebKeys.OPERATION_EXPORT)) {
			res.setContentType(mimeTypes.getContentType(filename));
			res.setHeader("Cache-Control", "private");
			res.setHeader(
						"Content-Disposition",
						"attachment; filename=\"" + filename + "\"");
			
			Collection<Long> binderIds = new HashSet<Long>();
			if (entryId != null) {
				FolderEntry entry = null;
				
				try {
					entry = getFolderModule().getEntry(binderId, entryId);
				} catch(NoFolderEntryByTheIdException exc) {
			    	statusTicket.setStatus(exc.getLocalizedMessage());
					ZipOutputStream zipOut = new ZipOutputStream(res.getOutputStream());		
		
					//Standard zip encoding is cp437. (needed when chars are outside the ASCII range)
					zipOut.setEncoding("cp437");
					zipOut.finish();
				
					return null;
				}
			} else {
				UserProperties userFolderProperties = (UserProperties)model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ);
				options = ListFolderHelper.getSearchFilter(this, request, getBinderModule().getBinder(binderId), userFolderProperties);
				options.put(ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.SEARCH_MAX_HITS_FOLDER_ENTRIES_EXPORT);
				Map formData = request.getParameterMap();
				binderIds = TreeHelper.getSelectedIds(formData);
			}
			Boolean noSubBinders = PortletRequestUtils.getBooleanParameter(request, "noSubBinders", false);
			Map reportMap = new HashMap();
			reportMap.put(ExportHelper.workspaces, new Integer(0));
			reportMap.put(ExportHelper.folders, new Integer(0));
			reportMap.put(ExportHelper.entries, new Integer(0));
			reportMap.put(ExportHelper.files, new Integer(0));
			reportMap.put(ExportHelper.errors, new Integer(0));
			reportMap.put(ExportHelper.errorList, new ArrayList<String>());
			try {
				getBinderModule().export(binderId, entryId, res.getOutputStream(), options, binderIds, 
						noSubBinders, statusTicket, reportMap);
	    	} catch(ExportException e) {
	    		reportMap.put("errors", (Integer)reportMap.get("errors") + 1);
	    		List eList = (List)reportMap.get("errorList");
	    		eList.add(e.getMessage());
	    	}

			String[] reportData = new String[] {
					((Integer)reportMap.get(ExportHelper.workspaces)).toString(),
					((Integer)reportMap.get(ExportHelper.folders)).toString(),
					((Integer)reportMap.get(ExportHelper.entries)).toString(),
					((Integer)reportMap.get(ExportHelper.files)).toString(),
					((Integer)reportMap.get(ExportHelper.errors)).toString()
				};
			statusTicket.setStatus(NLT.get("administration.export_import.exportReport", reportData) + 
					getFormattedErrorMessages((List<String>) reportMap.get(ExportHelper.errorList)));
			statusTicket.done();
			return null;
		}
		model.put("ss_reportData", request.getAttribute("ss_reportData"));
		return new ModelAndView("administration/exportImport_summary", model);
	}
	
	protected String getFormattedErrorMessages(List<String> errorMessages) {
		StringBuffer result = new StringBuffer();
		if (!errorMessages.isEmpty()) {
			result.append("<br/><br/>");
			for (String msg : errorMessages) {
				result.append(msg).append("<br/>");
			}
		}
		return result.toString();
	}
}