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
package org.kablink.teaming.liferaylight.portlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.PortletPreferencesUtil;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

public class DeprecatedController extends AbstractController {
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
	public static final String ADMINISTRATION_PORTLET="ss_administration";
	public static final String WELCOME_PORTLET="ss_welcome";

	protected void handleActionRequestInternal(ActionRequest request,
			ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}

	protected ModelAndView handleRenderRequestInternal(RenderRequest request,
			RenderResponse response) throws Exception {
		Map<String,Object> model = new HashMap<String,Object>();
 		model.put(WebKeys.WINDOW_STATE, request.getWindowState());
 		PortletPreferences prefs = null;
 		try {
 			prefs = request.getPreferences();
  		} catch(Exception e) {
 		}
  		
  		//Get the URL of the Teaming site
  		String teamingUrl = "";
  		Long companyId = (Long) request.getAttribute("COMPANY_ID");
  		if(companyId != null)
  			teamingUrl = SPropsUtil.getString("teaming.url." + String.valueOf(companyId.longValue()), "");
  		if(teamingUrl.equals(""))
  			teamingUrl = SPropsUtil.getString("teaming.url", "");
   		model.put("ssTeamingUrl", teamingUrl);

		String displayType = getDisplayType(request);
		if (FORUM_PORTLET.equals(displayType)) {
			//This is the portlet view; get the configured list of folders to show
			String[] preferredBinderIds = new String[0];
			if (prefs != null) preferredBinderIds = PortletPreferencesUtil.getValues(prefs, WebKeys.FORUM_PREF_FORUM_ID_LIST, new String[0]);

			//Build the jsp bean (sorted by folder title)
			List<Long> binderIds = new ArrayList<Long>();
			for (int i = 0; i < preferredBinderIds.length; i++) {
				binderIds.add(new Long(preferredBinderIds[i]));
			}
			try {
				response.setProperty(RenderResponse.EXPIRATION_CACHE,"300");
			} catch(Exception e) {}
			//return new ModelAndView("bookmarks", model);
			
		} else if (WORKSPACE_PORTLET.equals(displayType)) {
			//return new ModelAndView("workspace_tree", model);
			
		} else if (WELCOME_PORTLET.equals(displayType)) {
			//return new ModelAndView("welcome", model);
		    
		} else if (PRESENCE_PORTLET.equals(displayType)) {
 			Set ids = new HashSet();		
 			if (prefs != null) {
 				ids.addAll(LongIdUtil.getIdsAsLongSet(PortletPreferencesUtil.getValue(prefs, WebKeys.PRESENCE_PREF_USER_LIST, "")));
 	 			ids.addAll(LongIdUtil.getIdsAsLongSet(PortletPreferencesUtil.getValue(prefs, WebKeys.PRESENCE_PREF_GROUP_LIST, "")));
 			}
			try {
				response.setProperty(RenderResponse.EXPIRATION_CACHE,"300");
			} catch(Exception e) {}
  			model.put(WebKeys.USER_LIST, LongIdUtil.getIdsAsString(ids));
  			//return new ModelAndView("buddy_list", model);

		} else if (TOOLBAR_PORTLET.equals(displayType) || WORKAREA_PORTLET.equals(displayType)) {
 			return new ModelAndView("teaming", model);

		} else if (BLOG_SUMMARY_PORTLET.equals(displayType)) {
			//return new ModelAndView("blog", model);
			
		} else if (WIKI_PORTLET.equals(displayType)) {
			//return new ModelAndView("wiki", model);
			
		} else if (GUESTBOOK_SUMMARY_PORTLET.equals(displayType)) {
			//return new ModelAndView("guestbook", model);
			
		} else if (TASK_SUMMARY_PORTLET.equals(displayType)) {
			//return new ModelAndView("tasks", model);
			
		} else if (SEARCH_PORTLET.equals(displayType)) {
			//return new ModelAndView("search", model);
			
		} else if (GALLERY_PORTLET.equals(displayType)) {
			//return new ModelAndView("photo", model);
			
		} else if (ADMINISTRATION_PORTLET.equals(displayType)) {
			//return new ModelAndView("administration", model);
		}

		return new ModelAndView("deprecated", model);
	}

	public String getDisplayType(PortletRequest request) {
		PortletConfig pConfig = (PortletConfig)request.getAttribute("javax.portlet.config");
		String pName = pConfig.getPortletName();
		
		//For liferay we use instances and the name will be changed slightly
		//That is why we check for the name with contains
		if (pName.contains(FORUM_PORTLET))
			return FORUM_PORTLET;
		else if (pName.contains(WORKSPACE_PORTLET))
			return WORKSPACE_PORTLET;
		else if (pName.contains(PRESENCE_PORTLET))
			return PRESENCE_PORTLET;
		else if (pName.contains(BLOG_SUMMARY_PORTLET))
			return BLOG_SUMMARY_PORTLET;
		else if (pName.contains(GALLERY_PORTLET))
			return GALLERY_PORTLET;
		else if (pName.contains(GUESTBOOK_SUMMARY_PORTLET))
			return GUESTBOOK_SUMMARY_PORTLET;
		else if (pName.contains(TASK_SUMMARY_PORTLET))
			return TASK_SUMMARY_PORTLET;
		else if (pName.contains(SEARCH_PORTLET))
			return SEARCH_PORTLET;
		else if (pName.contains(TOOLBAR_PORTLET))
			return WORKAREA_PORTLET;
		else if (pName.contains(WIKI_PORTLET))
			return WIKI_PORTLET;
		else if (pName.contains(MOBILE_PORTLET))
			return MOBILE_PORTLET;
		else if (pName.contains(WORKAREA_PORTLET))
			return WORKAREA_PORTLET;
		else if (pName.contains(WELCOME_PORTLET))
			return WELCOME_PORTLET;
		else if (pName.contains(RELEVANCE_DASHBOARD_PORTLET))
			return RELEVANCE_DASHBOARD_PORTLET;
		return null;
	}

}
