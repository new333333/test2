/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.web.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.Toolbar;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.portlet.ModelAndView;

/**
 * Helper methods for the GWT UI code.
 * 
 * @author drfoster@novell.com
 */
public class GwtUIHelper {
	// Used as a qualifier in a toolbar to indicate the value maps to a
	// GWT UI TeamingAction value.
	public final static String GWTUI_TEAMING_ACTION = "GwtUI.TeamingAction";
	
	// The key into the session cache to store the GWT UI toolbar
	// beans.
	public final static String CACHED_TOOLBARS_KEY = "gwt-ui-toolbars";
	
	// When caching a toolbar containing an AdaptedPortletURL, we
	// must also store the URL in string form for the GWT UI to use.
	// If we don't, an NPE is generated when we try to convert it. 
	public final static String URLFIXUP_PATCH = ".urlAsString";

	// The names of the toolbar beans stored in the session cache for
	// the GWT UI toolbar.
	public final static String[] CACHED_TOOLBARS = new String[] {
		WebKeys.CALENDAR_IMPORT_TOOLBAR,
		WebKeys.EMAIL_SUBSCRIPTION_TOOLBAR,
		WebKeys.FOLDER_ACTIONS_TOOLBAR,
		WebKeys.FOLDER_TOOLBAR,
		WebKeys.FOLDER_VIEWS_TOOLBAR,
		WebKeys.GWT_MISC_TOOLBAR,
		WebKeys.WHATS_NEW_TOOLBAR,
	};

	/**
	 * Builds the GWT miscellaneous toolbar for a binder.
	 *
	 * @param bs
	 * @param request
	 * @param user
	 * @param binder
	 * @param model
	 * @param qualifiers
	 * @param gwtMiscToolbar
	 */
	@SuppressWarnings("unchecked")
	public static void buildGwtMiscToolbar(AllModulesInjected bs, RenderRequest request, User user, Binder binder, Map model, Toolbar gwtMiscToolbar) {
		// We only add the GWT miscellaneous toolbar items if the GWT
		// UI is active.  Is it?
		HashMap<String, String> qualifiers;
		if (isGwtUIActive(request)) {
			// Yes!  Are we running as other than guest and is the
			// binder we're on other than the profiles container?
			boolean isGuest = isCurrentUserGuest();
			if ((!isGuest) && (EntityIdentifier.EntityType.profiles != binder.getEntityType())) {
				// Yes!  Add a toolbar item that to track the binder.
				String action = "TRACK_BINDER";
				String actionTitleKey = getTrackThisKey(binder);
				boolean isTracked = BinderHelper.isBinderTracked(bs, binder.getId());
				if (isTracked) {
					action   = "UN" + action;
					actionTitleKey += "Not";
				}
				qualifiers = new HashMap<String, String>();
				qualifiers.put(GWTUI_TEAMING_ACTION, action);
				gwtMiscToolbar.addToolbarMenu("track", NLT.get(actionTitleKey), "", qualifiers);
			}
			
//!			...this needs to be implemented...
		}
	}

	/*
	 * Generates the appropriate resource key for tracking the given
	 * binder.
	 * 
	 * Note:  The logic for which key is used was copied from
	 *        sidebar_track2.jsp.
	 */
	private static String getTrackThisKey(Binder binder) {
		String trackThisKey = "relevance.trackThis";
		switch (binder.getEntityType()) {
		case workspace:
			if (Definition.USER_WORKSPACE_VIEW == binder.getDefinitionType()) trackThisKey += "Person";
			else                                                              trackThisKey += "Workspace";
			break;
			
		case folder:
			String dFamily = "";
			Element familyProperty = ((Element) binder.getDefaultViewDef().getDefinition().getRootElement().selectSingleNode("//properties/property[@name='family']"));
			if (familyProperty != null) {
				dFamily = familyProperty.attributeValue("value", "");
				if (null == dFamily) {
					dFamily = "";
				}
			}
			if (dFamily.equalsIgnoreCase("calendar")) trackThisKey += "Calendar";
			else                                      trackThisKey += "Folder";
		}
		return trackThisKey;
	}
	
	/**
	 * Builds the GWT UI toolbar for a binder.
	 * 
	 * @param bs
	 * @param request
	 * @param user
	 * @param binder
	 * @param model
	 * @param qualifiers
	 * @param gwtUIToolbar
	 */
	@SuppressWarnings("unchecked")
	public static void buildGwtUIToolbar(AllModulesInjected bs, RenderRequest request, User user, Binder binder, Map model, Toolbar gwtUIToolbar) {
		// If the GWT UI is enabled and we're not in captive mode...
		if (isGwtUIEnabled() && (!(MiscUtil.isCaptive(request)))) {
			// ...add the GWT UI button to the menu bar.
			String title = "Activate the Durango UI";
			Map qualifiers = new HashMap();
			qualifiers.put("title", title);
			qualifiers.put("icon", "gwt.png");
			qualifiers.put("iconGwtUI", "true");
			qualifiers.put("onClick", "ss_toggleGwtUI(true);return false;");
			gwtUIToolbar.addToolbarMenu("1_gwtUI", title, "javascript: //;", qualifiers);
		}
	}

	/**
	 * When in GWT UI mode, extracts the toolbar beans from the model
	 * and stores them in the session cache.
	 * 
	 * @param pRequest
	 * @param mv
	 */
	public static ModelAndView cacheToolbarBeans(PortletRequest pRequest, ModelAndView mv) {
		return cacheToolbarBeans(WebHelper.getHttpServletRequest(pRequest), mv);
	}

	/**
	 * When in GWT UI mode, extracts the toolbar beans from the model
	 * and stores them in the session cache.
	 * 
	 * @param hRequest
	 * @param mv
	 */
	public static ModelAndView cacheToolbarBeans(HttpServletRequest hRequest, ModelAndView mv) {
		cacheToolbarBeansImpl(hRequest, ((null == mv) ? null : mv.getModel()));
		return mv;
	}

	/*
	 * When in GWT UI mode, extracts the toolbar beans from the model
	 * and stores them in the session cache.
	 */
	@SuppressWarnings("unchecked")
	private static void cacheToolbarBeansImpl(HttpServletRequest hRequest, Map model) {
		// If we don't have an HttpServletRequest...
		if (null == hRequest) {
			// ...bail.
			return;
		}

		// Clear any previous toolbars we may have cached.
		HttpSession hSession = WebHelper.getRequiredSession(hRequest);
		hSession.removeAttribute(CACHED_TOOLBARS_KEY);

		// If we're not in GWT UI mode...
		if (!(isGwtUIActive(hRequest))) {
			// ...bail.
			return;
		}

		// If we don't have a model or the model data is empty...
		if ((null == model) || (0 == model.size())) {
			// ...bail.
			return;
		}

		// Scan the names of toolbars we need to cache.
		HashMap<String, SortedMap> tbHM = new HashMap<String, SortedMap>();
		for (int i = 0; i < CACHED_TOOLBARS.length; i += 1) {
			// Does a toolbar by this name exist?
			String tbName = CACHED_TOOLBARS[i];
			SortedMap tb = ((SortedMap) model.get(tbName));
			if ((null != tb) && (!(tb.isEmpty()))) {
				// Yes! Add it to the HashMap.
				fixupAdaptedPortletURLs(tb);
				tbHM.put(tbName, tb);
			}
		}

		// Finally, store the HashMap of toolbars in the session
		// cache.
		hSession.setAttribute(CACHED_TOOLBARS_KEY, tbHM);
	}

	/*
	 * Walks the toolbar map and stores the URL from any
	 * AdaptedPortletURL in its string form.
	 * 
	 * We do this because when the GWT UI code accesses the toolbars,
	 * it cannot process the AdaptedPortletURL as a string as an NPE 
	 * is generated when we try to convert it. 
	 */
	@SuppressWarnings("unchecked")
	private static void fixupAdaptedPortletURLs(Map tbMap) {
		Set tbKeySet = tbMap.keySet();
		for (Iterator tbKeyIT = tbKeySet.iterator(); tbKeyIT.hasNext(); ) {
			Object tbKeyO = tbKeyIT.next();
			if (tbKeyO instanceof String) {
				String tbKey = ((String) tbKeyO);
				Object tbO = tbMap.get(tbKey);
				if (tbO instanceof AdaptedPortletURL) {
					String url = ((AdaptedPortletURL) tbO).toString();
					tbMap.put(tbKey + URLFIXUP_PATCH, url);
				}
				else if (tbO instanceof Map) {
					fixupAdaptedPortletURLs((Map) tbO);
				}
			}
		}
	}

	/**
	 * Returns true if the current user is the built-in guest user and
	 * false otherwise.
	 * 
	 * @return
	 */
	public static boolean isCurrentUserGuest() {
		User user = RequestContextHolder.getRequestContext().getUser();
		return ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId());
	}
	
	/**
	 * Returns true if the GWT UI should be available and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isGwtUIEnabled() {
		String durangoUI = SPropsUtil.getString("use-durango-ui", "");
		return (MiscUtil.hasString(durangoUI) && "1".equals(durangoUI));
	}

	/**
	 * Returns true if the GWT UI should be active and false otherwise.
	 * 
	 * @param pRequest
	 * 
	 * @return
	 */
	public static boolean isGwtUIActive(PortletRequest pRequest) {
		HttpServletRequest hRequest = WebHelper.getHttpServletRequest(pRequest);
		boolean reply = (null != pRequest);
		if (reply) {
			reply = isGwtUIActive(hRequest);
		}
		return reply;
	}

	/**
	 * Returns true if the GWT UI should be active and false otherwise.
	 * 
	 * @param hRequest
	 * 
	 * @return
	 */
	public static boolean isGwtUIActive(HttpServletRequest hRequest) {
		boolean reply = isGwtUIEnabled();
		if (reply) {
			reply = (null != hRequest);
			if (reply) {
				HttpSession hSession = WebHelper.getRequiredSession(hRequest);
				Object durangoUI = hSession.getAttribute("use-durango-ui");
				reply = ((null != durangoUI) && (durangoUI instanceof Boolean) && ((Boolean) durangoUI).booleanValue());
			}
		}
		return reply;

	}

	/**
	 * Updates stores the current GWT UI active flag in the session
	 * cache.
	 * 
	 * @param pRequest
	 * @param gwtUIActive
	 */
	public static void setGwtUIActive(PortletRequest pRequest, boolean gwtUIActive) {
		HttpServletRequest hRequest = WebHelper.getHttpServletRequest(pRequest);
		if (null != hRequest) {
			setGwtUIActive(hRequest, gwtUIActive);
		}
	}

	/**
	 * Updates stores the current GWT UI active flag in the session
	 * cache.
	 * 
	 * @param hRequest
	 * @param gwtUIActive
	 */
	public static void setGwtUIActive(HttpServletRequest hRequest, boolean gwtUIActive) {
		if (null != hRequest) {
			HttpSession hSession = WebHelper.getRequiredSession(hRequest);
			hSession.setAttribute("use-durango-ui", new Boolean(gwtUIActive && isGwtUIEnabled()));
		}
	}
}
