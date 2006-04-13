package com.sitescape.ef.portlet.forum;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;

/**
 * @author Peter Hurley
 *
 */
public class ModifyBinderController extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {

		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		if (action.equals(WebKeys.ACTION_DELETE_BINDER)) {
			Binder binder = getBinderModule().getBinder(binderId);
			getBinderModule().deleteBinder(binderId);
			setupViewBinder(response, binder.getParentBinder().getId());		
			response.setRenderParameter("ssReloadUrl", "");
			
		} else if (formData.containsKey("okBtn")) {
			if (action.equals(WebKeys.ACTION_MODIFY_BINDER)) { 			
				//	The modify form was submitted. Go process it
				Map fileMap = null;
				if (request instanceof MultipartFileSupport) {
					fileMap = ((MultipartFileSupport) request).getFileMap();
				} else {
					fileMap = new HashMap();
				}
				getBinderModule().modifyBinder(binderId, new MapInputData(formData), fileMap);
			} else if (action.equals(WebKeys.ACTION_MOVE_BINDER)) {
				//must be a move
				Long destinationId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "destination"));
				getBinderModule().moveBinder(binderId, destinationId);
			}
			setupViewBinder(response, binderId);
			
		} else if (formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			setupViewBinder(response, binderId);
		} else {
			response.setRenderParameters(formData);		
		}
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
		RenderResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				

		Map model = new HashMap();	
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		String path;
		if (action.equals(WebKeys.ACTION_MODIFY_BINDER)) {
			try {
				Binder binder = getBinderModule().getBinder(binderId);
				model.put(WebKeys.BINDER, binder);
				model.put(WebKeys.ENTRY, binder);
				model.put(WebKeys.CONFIG_JSP_STYLE, "form");
				Definition binderDef = binder.getEntryDef();
				if (binderDef == null) {
					DefinitionUtils.getDefaultBinderDefinition(binder, model, "//item[@type='form']");
				} else {
					DefinitionUtils.getDefinition(binderDef, model, "//item[@type='form']");
				}
				path = WebKeys.VIEW_MODIFY_BINDER;
			} catch (NoDefinitionByTheIdException nd) {
				return returnToViewForum(request, response, formData, binderId);
			}
		} else if (action.equals(WebKeys.ACTION_MOVE_BINDER)) {
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
			path = WebKeys.VIEW_MOVE_BINDER;
		} else
			return returnToViewForum(request, response, formData, binderId);
			
		return new ModelAndView(path, model);
	}
}

