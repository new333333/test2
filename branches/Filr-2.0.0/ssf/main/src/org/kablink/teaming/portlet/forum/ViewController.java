/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.portlet.forum;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PortletPreferencesUtil;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;




/**
 * @author Peter Hurley
 *
 */
public class ViewController  extends SAbstractController {
	public static final String RELEVANCE_DASHBOARD_PORTLET="ss_relevance_dashboard";
	public static final String BLOG_SUMMARY_PORTLET="ss_blog";
	public static final String FORUM_PORTLET="ss_forum";
	public static final String GALLERY_PORTLET="ss_gallery";
	public static final String GUESTBOOK_SUMMARY_PORTLET="ss_guestbook";
	public static final String TASK_SUMMARY_PORTLET="ss_task";
	public static final String MOBILE_PORTLET="ss_mobile";
	public static final String PRESENCE_PORTLET="ss_presence";
	public static final String SEARCH_PORTLET="ss_search";
	public static final String TOOLBAR_PORTLET="ss_toolbar";
	public static final String WIKI_PORTLET="ss_wiki";
	public static final String WORKSPACE_PORTLET="ss_workspacetree";
	public static final String WORKAREA_PORTLET="ss_workarea";
	public static final String WELCOME_PORTLET="ss_welcome";

	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
 		PortletPreferences prefs = null;
 		String ss_initialized = null;
 		try {
 			prefs = request.getPreferences();
 		} catch(Exception e) {}
		if (prefs != null) {
			ss_initialized = PortletPreferencesUtil.getValue(prefs, WebKeys.PORTLET_PREF_INITIALIZED, null);
			if (Validator.isNull(ss_initialized)) {
				prefs.setValue(WebKeys.PORTLET_PREF_INITIALIZED, "true");
				prefs.store();
			}
		}
        User user = RequestContextHolder.getRequestContext().getUser();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		if (op.equals(WebKeys.OPERATION_SET_DISPLAY_STYLE)) {
			Map<String,Object> updates = new HashMap<String,Object>();
			updates.put(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE, 
					PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
			getProfileModule().modifyEntry(user.getId(), new MapInputData(updates));
		}
		response.setRenderParameters(request.getParameterMap());
		try {response.setWindowState(request.getWindowState());} catch(Exception e){};
	}
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		return BinderHelper.CommonPortletDispatch(this, request, response);
	}
}
