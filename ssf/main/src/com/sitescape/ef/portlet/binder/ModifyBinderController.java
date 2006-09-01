package com.sitescape.ef.portlet.binder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DefinitionHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;

/**
 * @author Peter Hurley
 *
 */
public class ModifyBinderController extends AbstractBinderController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
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
			response.setRenderParameter("ssReloadUrl", "");
			
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
				getBinderModule().modifyBinder(binderId, new MapInputData(formData), fileMap, deleteAtts);
			} else if (op.equals(WebKeys.OPERATION_MOVE)) {
				//must be a move
				Long destinationId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "destination"));
				getBinderModule().moveBinder(binderId, destinationId);
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

