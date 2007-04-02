package com.sitescape.team.portlet.forum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.User;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;


public class EntryVersionController extends  SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long entityId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTITY_ID);
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, null);
		String viewPath = "forum/view_description_history";
		Map model = new HashMap();
				
		List changeList = new ArrayList();
		List changes = null;
		String entityType = PortletRequestUtils.getStringParameter(request,  "entityType", "folderEntry");
		if (entityId != null) {
			if (operation.equals(ChangeLog.MODIFYENTRY)) {
				//Start the list with the "addEntry" entry
				changes = getAdminModule().getChanges(entityId, entityType, ChangeLog.ADDENTRY);
				changeList.addAll(BinderHelper.BuildChangeLogBeans(changes));
			}
			changes = getAdminModule().getChanges(entityId, entityType, operation);
			changeList.addAll(BinderHelper.BuildChangeLogBeans(changes));
		}
		
		model.put(WebKeys.CHANGE_LOG_LIST, changeList);
		
		return new ModelAndView(viewPath, model);
	} 

}