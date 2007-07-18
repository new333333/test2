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

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.User;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;


/**
 * @author Peter Hurley
 *
 */
public class ViewPermalinkController  extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
 		Map<String,Object> model = new HashMap<String,Object>();
        User user = RequestContextHolder.getRequestContext().getUser();
 		
 		String url = BinderHelper.getBinderPermaLink(this);
		String binderId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		String entryId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		String entityType= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_TYPE, "");
		String newTab= PortletRequestUtils.getStringParameter(request, WebKeys.URL_NEW_TAB, "");
		
		if (!binderId.equals("")) url = url.replaceAll(WebKeys.URL_BINDER_ID_PLACE_HOLDER, binderId);
		if (!entryId.equals("")) url = url.replaceAll(WebKeys.URL_ENTRY_ID_PLACE_HOLDER, entryId);
		if (!entityType.equals("")) url = url.replaceAll(WebKeys.URL_ENTITY_TYPE_PLACE_HOLDER, entityType);
		if (!newTab.equals("")) url = url.replaceAll(WebKeys.URL_NEW_TAB_PLACE_HOLDER, newTab);
		
		if (entityType.equals("") && entryId.equals("") && !binderId.equals("")) {
			Binder binder = getBinderModule().getBinder(new Long(binderId));
			entityType = binder.getEntityType().name();
		}
		if (entityType.equals(EntityIdentifier.EntityType.workspace.toString())) {
			url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_ws_listing");
		} else if (entityType.equals(EntityIdentifier.EntityType.folder.toString())) {
			url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_folder_listing");
		} else if (entityType.equals(EntityIdentifier.EntityType.folderEntry.toString())) {
			String displayStyle = user.getDisplayStyle();
			if (displayStyle != null && displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
				url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_folder_entry");
			} else {
				url = url.replaceAll(WebKeys.URL_ACTION_PLACE_HOLDER, "view_folder_listing");
			}
		}
 		
		model.put(WebKeys.PERMALINK, url);
			
    	if(logger.isDebugEnabled()) {
    		logger.debug("Permalink followed: " + url);
    	}

    	return new ModelAndView("binder/view_permalink", model);
	}

}
