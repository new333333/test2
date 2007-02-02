package com.sitescape.ef.portlet.administration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.PortletRequestUtils;

public class ViewChangeLogController  extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {

		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request,  WebKeys.URL_BINDER_ID);
		Long entityId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTITY_ID);
		if ((binderId == null) && (entityId == null)) {
			//not ajax request
			return new ModelAndView(WebKeys.VIEW_ADMIN_CHANGELOG, model);
		}
		response.setContentType("text/xml");
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, null);
		List changes = null;
		if (binderId != null) {
			//get all changes for a binder
			changes = getAdminModule().getChanges(binderId, operation);
		} else {
			String entityType = PortletRequestUtils.getStringParameter(request,  "entityType", "folderEntry");
			if (entityId != null) {
				changes = getAdminModule().getChanges(entityId, entityType, operation);
			}
		}
			
		model.put("changeLogs", changes);
			
		return new ModelAndView(WebKeys.VIEW_ADMIN_UPDATE_CHANGELOG, model);
	}
}