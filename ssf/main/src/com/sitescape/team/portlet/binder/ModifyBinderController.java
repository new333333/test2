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
package com.sitescape.team.portlet.binder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.tree.TreeHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;

/**
 * @author Peter Hurley
 *
 */
public class ModifyBinderController extends AbstractBinderController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {

		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String binderType = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_BINDER_TYPE);	
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String deleteSource = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DELETE_SOURCE, null);
		if(op.equals(WebKeys.OPERATION_SYNCHRONIZE_MIRRORED_FOLDER)) {
			// This trick is here to handle the situation where the synchronization
			// causes the binder to be deleted.
			Binder binder = getBinderModule().getBinder(binderId);
			// First, setup the view as if the binder is to be deleted.
			setupViewOnDelete(response, binder, binderType);
			if(getFolderModule().synchronize(binderId, null)) {
				// The binder was not deleted (typical situation). 
				// Setup the right view which will override the previous setup.
				setupViewBinder(response, binderId, binderType);
			}
			else {
				// The binder was indeed deleted.  Finish it up.
				response.setRenderParameter(WebKeys.RELOAD_URL_FORCED, "");			
			}
		} else if (formData.containsKey("okBtn") || formData.containsKey("applyBtn")) {
			if (op.equals("") || op.equals(WebKeys.OPERATION_MODIFY)) { 			
				//	The modify form was submitted. Go process it
				Map fileMap = null;
				if (request instanceof MultipartFileSupport) {
					fileMap = ((MultipartFileSupport) request).getFileMap();
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
				MapInputData mid = new MapInputData(formData);
		   		if (mid.exists(ObjectKeys.FIELD_BINDER_MIRRORED)) {
		   			boolean mirrored = Boolean.parseBoolean(mid.getSingleValue(ObjectKeys.FIELD_BINDER_MIRRORED));
		   			if(mirrored) {
		   				Map formDataPlus = new HashMap(formData);
		   				formDataPlus.put(ObjectKeys.FIELD_BINDER_LIBRARY, "true");
		   				mid = new MapInputData(formDataPlus);
		   			}
		   		}
		   		try {
		   			getBinderModule().modifyBinder(binderId, mid, fileMap, deleteAtts, null);				
		   			if (formData.containsKey("okBtn")) setupReloadOpener(response, binderId);	
		   			if (formData.containsKey("applyBtn")) response.setRenderParameters(formData);
		   		} catch (ConfigurationException cf) {
		   			response.setRenderParameters(formData);
		   			response.setRenderParameter(WebKeys.EXCEPTION, cf.getLocalizedMessage() != null ? cf.getLocalizedMessage() : cf.getMessage());
		   		}
			} else if (op.equals(WebKeys.OPERATION_MOVE)) {
				//must be a move
				Long destinationId = TreeHelper.getSelectedId(formData);
				if (destinationId != null) getBinderModule().moveBinder(binderId, destinationId, null);
				setupReloadOpener(response, binderId);
			} else if (op.equals(WebKeys.OPERATION_COPY)) {
				Long destinationId = TreeHelper.getSelectedId(formData);
				if (destinationId != null) getBinderModule().copyBinder(binderId, destinationId, null);
				setupReloadOpener(response, binderId);
				
			} else if (op.equals(WebKeys.OPERATION_DELETE)) {
				// The delete-mirrored-binder form was submitted.
				//retrieve binder so we can return to parent
				Binder binder = getBinderModule().getBinder(binderId);			
				Binder parentBinder = binder.getParentBinder();			
				//get view data, before binder is deleted
				setupViewOnDelete(response, binder, binderType);	
				getBinderModule().deleteBinder(binderId, Boolean.parseBoolean(deleteSource), null);
				setupReloadOpener(response, parentBinder.getId());
			} else {
				setupReloadOpener(response, binderId);			
			}	
		} else if (formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			setupCloseWindow(response);
		} else if (op.equals(WebKeys.OPERATION_DELETE)) {
			response.setRenderParameters(formData);
		} else {
			response.setRenderParameters(formData);		
		}
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				

		Map model = new HashMap();	
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		model.put(WebKeys.OPERATION, action);
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String path;
		if (op.equals(WebKeys.OPERATION_MOVE) || op.equals(WebKeys.OPERATION_COPY)) {
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
			Workspace ws = getWorkspaceModule().getTopWorkspace();
			Document wsTree = getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, this),1);
			Element top = (Element)wsTree.getRootElement();
			if (top != null) {
				//cannot move to top
				top.addAttribute("displayOnly", "true");
			}
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			model.put(WebKeys.OPERATION, op);
			path = WebKeys.VIEW_MOVE_BINDER;
		} else if (op.equals(WebKeys.OPERATION_DELETE)) {
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
			path = WebKeys.VIEW_CONFIRM_DELETE_MIRRRED_BINDER;
		} else {
			Binder binder = getBinderModule().getBinder(binderId);
//			String binderType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_TYPE, "");

//			Map publicBinderDefs = new HashMap();
//			DefinitionUtils.getDefinitions(model);
//			if (binderType.equals(EntityIdentifier.EntityType.folder.name())) {
//				publicBinderDefs = (Map) model.get(WebKeys.PUBLIC_FOLDER_DEFINITIONS);
//			} else if (binderType.equals(EntityIdentifier.EntityType.workspace.name())) {
//				publicBinderDefs = (Map) model.get(WebKeys.PUBLIC_WORKSPACE_DEFINITIONS);
//			}
			
//			model.put(WebKeys.PUBLIC_BINDER_DEFINITIONS, publicBinderDefs);

//			DefinitionUtils.getDefinitions(binder, model);
			
			model.put(WebKeys.BINDER, binder);
//			model.put(WebKeys.ENTRY, binder);
			model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_FORM);
			Definition binderDef = binder.getEntryDef();
			if (binderDef == null) {
				DefinitionHelper.getDefaultBinderDefinition(binder, model, "//item[@type='form']");
			} else {
				DefinitionHelper.getDefinition(binderDef, model, "//item[@type='form']");
			}
			model.put(WebKeys.EXCEPTION, request.getParameter(WebKeys.EXCEPTION));
			path = WebKeys.VIEW_MODIFY_BINDER;
		} 
		return new ModelAndView(path, model);
	}

	
}

