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
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.PortletPreferencesUtil;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;



/**
 * @author Peter Hurley
 *
 */
public class ViewController  extends SAbstractController {
	public static final String BLOG_SUMMARY_PORTLET="ss_blog";
	public static final String FORUM_PORTLET="ss_forum";
	public static final String GALLERY_PORTLET="ss_gallery";
	public static final String GUESTBOOK_SUMMARY_PORTLET="ss_guestbook";
	public static final String TASK_SUMMARY_PORTLET="ss_task";
	public static final String PRESENCE_PORTLET="ss_presence";
	public static final String SEARCH_PORTLET="ss_search";
	public static final String TOOLBAR_PORTLET="ss_toolbar";
	public static final String WIKI_PORTLET="ss_wiki";
	public static final String WORKSPACE_PORTLET="ss_workspacetree";

	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
 		PortletPreferences prefs = request.getPreferences();
		String ss_initialized = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_INITIALIZED, null);
		if (Validator.isNull(ss_initialized)) {
			prefs.setValue(WebKeys.PORTLET_PREF_INITIALIZED, "true");
			prefs.store();
		}
        User user = RequestContextHolder.getRequestContext().getUser();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		if (op.equals(WebKeys.OPERATION_SET_DISPLAY_STYLE)) {
			Map<String,Object> updates = new HashMap<String,Object>();
			updates.put(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE, 
					PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
			getProfileModule().modifyEntry(user.getParentBinder().getId(), user.getId(), new MapInputData(updates));
		}
		response.setRenderParameters(request.getParameterMap());
		response.setWindowState(request.getWindowState());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		return BinderHelper.CommonPortletDispatch(this, request, response);
	}
}
