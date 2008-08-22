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
package com.sitescape.team.portlet.forum;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.util.SimpleMultipartFile;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;

/**
 * @author Peter Hurley
 *
 */
public class AddAttachmentController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		if (op.equals(WebKeys.OPERATION_ADD_FILES_FROM_APPLET) || op.equals(WebKeys.OPERATION_ADD_FILES_BY_BROWSE_FOR_ENTRY)) {
			//See if the add entry form was submitted
			//The form was submitted. Go process it
			Map fileMap=null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
				
				if (op.equals(WebKeys.OPERATION_ADD_FILES_FROM_APPLET)) {
					//We need to parse the fileMap and create SimpleMultipartFile with the filename reset using the URLEncoder.decode method.
					
					Map appletFileMap = new HashMap();
					String nameValue = ObjectKeys.FILES_FROM_APPLET_FOR_BINDER;
		        	boolean blnCheckForAppletFile = true;
		        	int intFileCount = 1;
		        	while (blnCheckForAppletFile) {
		        		String fileEleName = nameValue + Integer.toString(intFileCount);
		        		if (fileMap.containsKey(fileEleName)) {
		        	    	MultipartFile myFile = (MultipartFile)fileMap.get(fileEleName);
		        	    	String utf8EncodedFileName = myFile.getOriginalFilename();
		        	    	String utf8DecodedFileName = URLDecoder.decode(utf8EncodedFileName, "UTF-8");
		        	    	java.io.InputStream content = myFile.getInputStream();
		        	    	MultipartFile mf = new SimpleMultipartFile(utf8DecodedFileName, content);
		        	    	appletFileMap.put(fileEleName, mf);
		        	    	intFileCount++;
		        		} else {
		        			blnCheckForAppletFile = false;
		        		}
		        	}
		        	fileMap = appletFileMap;
				}
				
			} else {
				fileMap = new HashMap();
			}
			Set deleteAtts = new HashSet();
			for (Iterator iter=formData.entrySet().iterator(); iter.hasNext();) {
				Map.Entry e = (Map.Entry)iter.next();
				String key = (String)e.getKey();
				if (key.startsWith("_delete_")) {
					deleteAtts.add(key.substring(8));
				}
			}
			String strFilesErrors = "";
			try {
				getFolderModule().modifyEntry(folderId, entryId, new MapInputData(formData), fileMap, deleteAtts, null, null);
			} catch (WriteFilesException wf) {
				strFilesErrors = wf.toString();
			}
			
			if (op.equals(WebKeys.OPERATION_ADD_FILES_FROM_APPLET)) {
				setupAppletResponse(response, folderId, entryId, strFilesErrors);
			}
			else {
				String closeDivFunctionName = PortletRequestUtils.getStringParameter(request, WebKeys.ENTRY_ATTACHMENT_DIV_CLOSE_FUNCTION, "");
				if (closeDivFunctionName != null) {
					closeDivFunctionName = closeDivFunctionName.replaceAll("strErrorMessage", strFilesErrors);
				}
				setupCloseDiv(response, folderId, closeDivFunctionName);
			}
			//flag reload of folder listing
			//response.setRenderParameter(WebKeys.RELOAD_URL_FORCED, "");
		} else {
			response.setRenderParameters(formData);		
		}
	}

	private void setupCloseDiv(ActionResponse response, Long folderId, String strCloseDivFunctionName) {
		//return to close div page
		response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_FILES_BY_BROWSE_FOR_ENTRY);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
		response.setRenderParameter(WebKeys.ENTRY_ATTACHMENT_DIV_CLOSE_FUNCTION, strCloseDivFunctionName);
	}	

	private void setupAppletResponse(ActionResponse response, Long folderId, Long entryId, String strFilesErrors) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_APPLET_RESPONSE);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
		response.setRenderParameter(WebKeys.FILE_PROCESSING_ERRORS, strFilesErrors);
	}
	
	private void setupReloadOpener(ActionResponse response, Long folderId, Long entryId) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
	}
	private void setupCloseWindow(ActionResponse response) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CLOSE_WINDOW);
	}
	private void setupViewEntry(ActionResponse response, Long folderId, Long entryId) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());		
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
		response.setRenderParameter(WebKeys.IS_REFRESH, "1");
	}
	private void setupViewFolder(ActionResponse response, Long folderId) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
		
	}
		
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
		String closeDivFunctionName = PortletRequestUtils.getStringParameter(request, WebKeys.ENTRY_ATTACHMENT_DIV_CLOSE_FUNCTION, "");

		Map model = new HashMap();	
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		model.put(WebKeys.OPERATION, action);
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String path;
		FolderEntry entry=null;
		if (op.equals(WebKeys.OPERATION_ADD_FILES_BY_BROWSE_FOR_ENTRY)) {
			model.put(WebKeys.ENTRY_ATTACHMENT_DIV_CLOSE_FUNCTION, closeDivFunctionName);
			path="definition_elements/close_div";
		} else {
			Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
			entry  = getFolderModule().getEntry(folderId, entryId);
				
			model.put(WebKeys.ENTRY, entry);
			model.put(WebKeys.FOLDER, entry.getParentFolder());
			model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_FORM);
			DefinitionHelper.getDefinition(entry.getEntryDef(), model, "//item[@type='form']");
			path = WebKeys.VIEW_MODIFY_ENTRY;
		}
		
			
		return new ModelAndView(path, model);
	}
}

