/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.portlet.binder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.Validator;

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
		if (op.equals(WebKeys.OPERATION_DELETE)) {
			//retrieve binder so we can return to parent
			Binder binder = getBinderModule().getBinder(binderId);
			//get view data, before binder is deleted
			setupViewOnDelete(response, binder, binderType);	
			getBinderModule().deleteBinder(binderId);
			response.setRenderParameter(WebKeys.RELOAD_URL_FORCED, "");
		} else if(op.equals(WebKeys.OPERATION_SYNCHRONIZE_MIRRORED_FOLDER)) {
			// This trick is here to handle the situation where the synchronization
			// causes the binder to be deleted.
			Binder binder = getBinderModule().getBinder(binderId);
			// First, setup the view as if the binder is to be deleted.
			setupViewOnDelete(response, binder, binderType);
			if(getFolderModule().synchronize(binderId)) {
				// The binder was not deleted (typical situation). 
				// Setup the right view which will override the previous setup.
				setupViewBinder(response, binderId, binderType);
			}
			else {
				// The binder was indeed deleted.  Finish it up.
				response.setRenderParameter(WebKeys.RELOAD_URL_FORCED, "");			
			}
		} else if (formData.containsKey("okBtn")) {
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

				getBinderModule().modifyBinder(binderId, mid, fileMap, deleteAtts);
				
			} else if (op.equals(WebKeys.OPERATION_MOVE)) {
				//must be a move
				Long destinationId = PortletRequestUtils.getLongParameter(request, "destination");
				if (destinationId != null) getBinderModule().moveBinder(binderId, new Long(destinationId));
			}
			setupViewBinder(response, binderId, binderType);
			
		} else if (formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			setupViewBinder(response, binderId, binderType);
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
		if (op.equals(WebKeys.OPERATION_MOVE)) {
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
			Workspace ws = getWorkspaceModule().getTopWorkspace();
			Document wsTree = getWorkspaceModule().getDomWorkspaceTree(ws.getId(), new WsDomTreeBuilder(ws, true, this),1);
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			path = WebKeys.VIEW_MOVE_BINDER;
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
			model.put(WebKeys.CONFIG_JSP_STYLE, "form");
			Definition binderDef = binder.getEntryDef();
			if (binderDef == null) {
				DefinitionHelper.getDefaultBinderDefinition(binder, model, "//item[@type='form']");
			} else {
				DefinitionHelper.getDefinition(binderDef, model, "//item[@type='form']");
			}
			path = WebKeys.VIEW_MODIFY_BINDER;
		} 
		return new ModelAndView(path, model);
	}

	
}

