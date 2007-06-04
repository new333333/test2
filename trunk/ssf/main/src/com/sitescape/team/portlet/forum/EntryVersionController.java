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
import com.sitescape.team.domain.EntityIdentifier;

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
		
		Map formData = request.getParameterMap();
				
		List changeList = new ArrayList();
		List changes = null;
		String entityType = PortletRequestUtils.getStringParameter(request,  "entityType", "folderEntry");
		EntityIdentifier entityIdentifier = new EntityIdentifier(entityId, EntityIdentifier.EntityType.valueOf(entityType));

		if (entityId != null) {
			if (operation.equals(ChangeLog.MODIFYENTRY)) {
				//Start the list with the "addEntry" entry
				changes = getAdminModule().getChanges(entityIdentifier, ChangeLog.ADDENTRY);
				changeList.addAll(BinderHelper.BuildChangeLogBeans(changes));
			}
			changes = getAdminModule().getChanges(entityIdentifier, operation);
			changeList.addAll(BinderHelper.BuildChangeLogBeans(changes));
		}
		
		model.put(WebKeys.CHANGE_LOG_LIST, changeList);
		model.put(WebKeys.ENTITY_ID, entityId);

		return new ModelAndView(viewPath, model);
	} 

}