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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.security.AccessControlException;
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
	protected static Log m_logger = LogFactory.getLog(GwtUIHelper.class);
	
	// ssf*.properties settings that affect various aspects of the GWT
	// based UI.
	private final static boolean IS_ACTIVITY_STREAMS_ENABLED  =           SPropsUtil.getBoolean("enable.activity.streams",   true );
	private final static boolean IS_ACTIVITY_STREAMS_ON_LOGIN =           SPropsUtil.getBoolean("activity.stream.on.login",  true );
	private final static boolean IS_MODIFY_TOP_ENTRY_ON_REPLY = false; // SPropsUtil.getBoolean("modify.top.entry.on.reply", false);
	
	// String used to recognized an '&' formatted URL vs. a '/'
	// formatted permalink URL.
	private final static String AMPERSAND_FORMAT_MARKER = "a/do?";
	
	// The key into the session cache to store the GWT UI tab and
	// toolbar beans.
	public final static String CACHED_TABS_KEY				= "gwt-ui-tabs";
	public final static String CACHED_TOOLBARS_KEY			= "gwt-ui-toolbars";
	public final static String CACHED_TOP_RANKED_PEOPLE_KEY	= "gwt-ui-topRankedPeople";
	public final static String CACHED_TOP_RANKED_PLACES_KEY	= "gwt-ui-topRankedPlaces";
	
	// The names of the toolbar beans stored in the session cache for
	// the GWT UI.
	private final static String[] CACHED_TOOLBARS = new String[] {
		WebKeys.CALENDAR_IMPORT_TOOLBAR,
		WebKeys.EMAIL_SUBSCRIPTION_TOOLBAR,
		WebKeys.FOLDER_ACTIONS_TOOLBAR,
		WebKeys.FOLDER_TOOLBAR,
		WebKeys.FOLDER_VIEWS_TOOLBAR,
		WebKeys.GWT_MISC_TOOLBAR,
		WebKeys.WHATS_NEW_TOOLBAR,
	};

	// Used as a qualifier in a toolbar to indicate the value maps to a
	// GWT UI TeamingAction.
	public final static String GWTUI_TEAMING_ACTION = "GwtUI.TeamingAction";
	
	// When caching a toolbar containing an AdaptedPortletURL, we
	// must also store the URL in string form for the GWT UI to use.
	// If we don't, an NPE is generated when we try to convert it. 
	public final static String URLFIXUP_PATCH = ".urlAsString";

	// Inner class used exclusively by addTrackBinderToToolbar() to
	// assist in building the toolbar items to support tracking.
	private static class TrackInfo {
		String m_action;
		String m_resourceKey;
		String m_tbName;
		
		TrackInfo(String tbName, String action, String resourceKey) {
			m_tbName = tbName;
			m_action = action;
			m_resourceKey = resourceKey;
		}
	}
	
	/*
	 * Adds an about item to the toolbar.
	 */
	@SuppressWarnings("unchecked")
	private static void addAboutToToolbar(AllModulesInjected bs, RenderRequest request, Map model, Toolbar tb) {
		// Construct an About... toolbar item.
		HashMap qualifiers = new HashMap();
		qualifiers.put("onClick", "ss_showAbout( 'ss_aboutBoxDiv' ); return false;");
		tb.addToolbarMenu(
			"about",
			NLT.get("misc.about"),
			"#",
			qualifiers);
	}
	
	/*
	 * Adds a branding item to the toolbar.
	 */
	@SuppressWarnings("unchecked")
	private static void addBrandingToToolbar(AllModulesInjected bs, RenderRequest request, Map model, Binder binder, Toolbar tb) {
		// If we don't have a binder...
		if (null == binder) {
			// ...it can't be branded.
			return;
		}

		// Does the user have rights to brand this binder?
		if (bs.getBinderModule().testAccess(binder, BinderOperation.modifyBinder)) {
			// Yes!  Add the appropriate toolbar item. 
			String menuKey = "toolbar.menu.brand.";
			if      (binder instanceof Workspace) menuKey += "workspace";
			else if (binder instanceof Folder)    menuKey += "folder";
			else                                  return;
			
			addTeamingActionToToolbar(
				tb,
				"branding",
				"EDIT_BRANDING",
				NLT.get(menuKey),
				"#",
				null);
		}
	}
	
	/*
	 * Adds a clipboard item to the toolbar.
	 */
	@SuppressWarnings("unchecked")
	private static void addClipboardToToolbar(AllModulesInjected bs, RenderRequest request, Map model, Binder binder, Toolbar tb) {
		// If we're not showing the clipboard...
		Boolean showClipboard = ((Boolean) model.get(WebKeys.TOOLBAR_CLIPBOARD_SHOW));
		if ((null == showClipboard) || (!showClipboard)) {
			// ...bail.
			return;
		}
		
		// If we don't have any contributor IDs...
		String contributorIds = ((String) model.get(WebKeys.TOOLBAR_CLIPBOARD_IDS_AS_JS_STRING));
		if (null == contributorIds) {
			// ...default to an empty list.
			contributorIds = "";
		}

		// Finally, generate the clipboard toolbar menu item.
		Map qualifiers = new HashMap();
		String onClickJS = "ss_muster.showForm('ss_muster_users', [" + contributorIds + "], '" + String.valueOf(binder.getId()) + "');";
		qualifiers.put("onclick", onClickJS);
		tb.addToolbarMenu(
			"clipboard",
			NLT.get("toolbar.menu.clipboard"),
			"#",
			qualifiers);
	}
	
	/*
	 * Adds a configure columns item to the toolbar.
	 */
	@SuppressWarnings("unchecked")
	private static void addConfigureColumnsToToolbar(AllModulesInjected bs, RenderRequest request, Map model, Binder binder, Toolbar tb) {
		// In the current context, can the user configure columns?
		// Note:  The check here follows the same logic as implemented
		// in sidebar_configure_columns.jsp.
		String viewType = ((String) model.get(WebKeys.VIEW_TYPE));
		if (MiscUtil.hasString(viewType)) {
			if (viewType.equalsIgnoreCase("folder") ||
					viewType.equalsIgnoreCase("table") ||
					viewType.equalsIgnoreCase("file")) {
				// Yes!  Construct a Configure Columns toolbar item.
				AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
				url.setParameter(WebKeys.ACTION, "__ajax_request");
				url.setParameter(WebKeys.URL_OPERATION, "configure_folder_columns");
				url.setParameter(WebKeys.URL_BINDER_ID, String.valueOf(binder.getId()));
				HashMap qualifiers = new HashMap();
				qualifiers.put("onClick", "ss_createPopupDiv(this, 'ss_folder_column_menu');return false;");
				tb.addToolbarMenu(
					"configureColumns",
					NLT.get("misc.configureColumns"),
					url.toString(),
					qualifiers);
			}
		}
	}
	
	/*
	 * Adds a send email item to the toolbar.
	 */
	@SuppressWarnings("unchecked")
	private static void addSendEmailToToolbar(AllModulesInjected bs, RenderRequest request, Map model, Binder binder, Toolbar tb) {
		// If we don't have a URL...
		String url = ((String) model.get(WebKeys.TOOLBAR_SENDMAIL_URL));
		if (!(MiscUtil.hasString(url))) {
			// ...we don't add anything to the toolbar.
			return;
		}

		// If we don't have any contributor IDs to send to...
		String[] contributorIds = ((String[]) model.get(WebKeys.TOOLBAR_SENDMAIL_IDS));
		if (null == contributorIds) {
			// ...default to an empty list.
			contributorIds = new String[0];
		}
		int contributorsCount = contributorIds.length;
				
		// If we don't have any contributors...
		Boolean post;
		if (0 == contributorsCount) {
			// ...we don't have to worry about the post flag.
			post = Boolean.FALSE;
		}
		else {
			// Otherwise, if we weren't passed a post flag...
			post = ((Boolean) model.get(WebKeys.TOOLBAR_SENDMAIL_POST));
			if (null == post) {
				// ...default it to true since there were contributors.
				post = Boolean.TRUE;
			}
		}
		
		// Generate the qualifiers to run a URL in a popup window...
		Map qualifiers = new HashMap();
		qualifiers.put("popup",       "true");
		qualifiers.put("popupWidth",  "600");
		qualifiers.put("popupHeight", "600");
		if (post) {
			// ...allowing for any contributors...
			String contributors = "";
			for (int i = 0 ; i < contributorsCount; i += 1) {
				if (0 < i) contributors += ",";
				contributors += String.valueOf(contributorIds[i]);
			}
			qualifiers.put("popup.fromForm",            "true");
			qualifiers.put("popup.hiddenInput.count",   "1");
			qualifiers.put("popup.hiddenInput.0.name",  WebKeys.USER_IDS_TO_ADD);
			qualifiers.put("popup.hiddenInput.0.value", contributors);
		}
		
		// ...and add it as a toolbar menu item.
		tb.addToolbarMenu(
			"sendEmail",
			NLT.get("toolbar.menu.sendMail"),
			url,
			qualifiers);
	}
	
	/*
	 * Adds a share this binder item to the toolbar.
	 */
	@SuppressWarnings("unchecked")
	private static void addShareBinderToToolbar(AllModulesInjected bs, RenderRequest request, Map model, Binder binder, Toolbar tb) {
		// Generate the qualifiers to run a URL in a popup window...
		Map qualifiers = new HashMap();
		qualifiers.put("popup",       "true");
		qualifiers.put("popupWidth",  "550");
		qualifiers.put("popupHeight", "750");
		
		// ...generate the URL to share the binder...
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
		url.setParameter(WebKeys.ACTION, "__ajax_relevance");
		url.setParameter(WebKeys.URL_OPERATION, "share_this_binder");
		url.setParameter(WebKeys.URL_BINDER_ID, String.valueOf(binder.getId()));
		
		// ...and add it as a toolbar menu item.
		tb.addToolbarMenu(
			"share",
			NLT.get(
				buildRelevanceKey(
					binder,
					"relevance.shareThis")),
			url.toString(),
			qualifiers);
	}
	
	/**
	 * Adds a teaming action a toolbar.
	 * 
	 * @param tb
	 * @param menuName
	 * @param teamingAction
	 * @param title
	 * @param url
	 * @param qualifiers
	 */
	@SuppressWarnings("unchecked")
	public static void addTeamingActionToToolbar(Toolbar tb, String menuName, String teamingAction, String title, String url, Map qualifiers) {
		// Add the teaming action to the qualifiers...
		if (null == qualifiers) {
			qualifiers = new HashMap();
		}
		qualifiers.put(GWTUI_TEAMING_ACTION, teamingAction);
		
		// ...and add it as a toolbar menu item.
		tb.addToolbarMenu(
			menuName,
			((null == title) ? "" : title),
			((null == url)   ? "" : url),
			qualifiers);
	}
	
	public static void addTeamingActionToToolbar(Toolbar tb, String menuName, String teamingAction, String title) {
		addTeamingActionToToolbar(tb, menuName, teamingAction, title, null, null);
	}
	
	/*
	 * Adds a track this binder item to the toolbar.
	 *
	 * Cases handled:
	 *    User workspace:  Tracked     / Person:  Tracked.
	 *    User workspace:  Tracked     / Person:  Not Tracked.
	 *    Workspace:       Tracked.
	 *    Calendar:        Tracked.
	 *    Folder:          Tracked.
	 *    
	 *    User workspace:  Not Tracked / Person:  Tracked.
	 *    User workspace:  Not Tracked / Person:  Not Tracked.
	 *    Workspace:       Not Tracked.
	 *    Calendar:        Not Tracked.
	 *    Folder:          Not Tracked.
	 */
	@SuppressWarnings("unchecked")
	private static void addTrackBinderToToolbar(AllModulesInjected bs, RenderRequest request, Map model, Binder binder, Toolbar tb) {
		// Construct an ArrayList to hold TrackInfo objects that
		// describe what track operations we'll need in the menu.
		ArrayList<TrackInfo> tiList = new ArrayList<TrackInfo>();

		// What do we know about this binder?
		Long binderId = binder.getId();
		boolean binderIsWorkspace     = (binder instanceof Workspace);
		boolean binderIsCalendar      = BinderHelper.isBinderCalendar(binder);
		boolean binderIsUserWorkspace = BinderHelper.isBinderUserWorkspace(binder);
		boolean binderTracked         = BinderHelper.isBinderTracked(bs, binderId);
		boolean personTracked         = BinderHelper.isPersonTracked(bs, binderId);

		// Build TrackInfo objects for the toolbar items we need to add
		// for tracking (or untracking) whatever we're looking at.
		if (binderTracked) {
			if (personTracked) {
				// User workspace:  Tracked / Person:  Tracked.
				tiList.add(    new TrackInfo("track",       "UNTRACK_BINDER", "relevance.trackThisWorkspaceNot"));				
				tiList.add(    new TrackInfo("trackPerson", "UNTRACK_PERSON", "relevance.trackThisPersonNot"));				
			}
			else {
				if (binderIsUserWorkspace) {
					// User workspace:  Tracked / Person:  Not Tracked.
					tiList.add(new TrackInfo("track",       "UNTRACK_BINDER", "relevance.trackThisWorkspaceNot"));				
					tiList.add(new TrackInfo("trackPerson", "TRACK_BINDER",   "relevance.trackThisPerson"));				
				}
				else if (binderIsWorkspace) {
					// Workspace:  Tracked.
					tiList.add(new TrackInfo("track",       "UNTRACK_BINDER", "relevance.trackThisWorkspaceNot"));				
				}
				else if (binderIsCalendar) {
					// Calendar:  Tracked.
					tiList.add(new TrackInfo("track",       "UNTRACK_BINDER", "relevance.trackThisCalendarNot"));				
				}
				else {
					// Folder:  Tracked.
					tiList.add(new TrackInfo("track",       "UNTRACK_BINDER", "relevance.trackThisFolderNot"));				
				}
			}
		}
		else {
			if (personTracked) {
				// User workspace:  Not Tracked / Person:  Tracked.
				tiList.add(    new TrackInfo("track",       "TRACK_BINDER",   "relevance.trackThisWorkspace"));				
				tiList.add(    new TrackInfo("trackPerson", "UNTRACK_PERSON", "relevance.trackThisPersonNot"));				
			}
			else if (binderIsUserWorkspace) {
				// User workspace:  Not Tracked / Person:  Not Tracked.
				tiList.add(    new TrackInfo("track",       "TRACK_BINDER",   "relevance.trackThisPerson"));				
			}
			else if (binderIsWorkspace) {
				// Workspace:  Not Tracked.
				tiList.add(    new TrackInfo("track",       "TRACK_BINDER",   "relevance.trackThisWorkspace"));				
			}
			else if (binderIsCalendar) {
				// Calendar:  Not Tracked.
				tiList.add(    new TrackInfo("track",       "TRACK_BINDER",   "relevance.trackThisCalendar"));				
			}
			else {
				// Folder:  Not Tracked.
				tiList.add(    new TrackInfo("track",       "TRACK_BINDER",   "relevance.trackThisFolder"));				
			}
		}

		// Scan the TrackInfo objects we generated...
		for (Iterator<TrackInfo> tiIT = tiList.iterator(); tiIT.hasNext(); ) {
			// ...adding a toolbar item for each.
			TrackInfo ti = tiIT.next();
			addTeamingActionToToolbar(tb, ti.m_tbName, ti.m_action, NLT.get(ti.m_resourceKey));
		}
	}

	/*
	 * Adds a trash item to the toolbar.
	 */
	@SuppressWarnings("unchecked")
	private static void addTrashToToolbar(AllModulesInjected bs, RenderRequest request, Map model, Binder binder, Toolbar tb) {
		// Construct a permalink URL to the trash for the current binder...
		String binderPermalink = PermaLinkUtil.getPermalink(request, binder);
		String trashPermalink = getTrashPermalink(binderPermalink);
		
		// ...and add t to the toolbar.
		addTeamingActionToToolbar(
			tb,
			"trash",
			"GOTO_PERMALINK_URL",
			NLT.get("toolbar.menu.trash"),
			trashPermalink,
			null);
	}
	
	/**
	 * Appends a parameter to to a URL.
	 * 
	 * @param urlString
	 * @param pName
	 * @param pValue
	 * 
	 * @return
	 */
	public static String appendUrlParam(String urlString, String pName, String pValue) {
		String param;
		boolean useAmpersand = (0 < urlString.indexOf(AMPERSAND_FORMAT_MARKER));
		if (useAmpersand)
			 param = ("&" + pName + "=" + pValue);
		else param = ("/" + pName + "/" + pValue);
		if (0 > urlString.indexOf(param)) {
			urlString += param;
		}
		return urlString;
	}
	
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
	public static void buildGwtMiscToolbar(AllModulesInjected bs, RenderRequest request, Binder binder, Map model, Toolbar gwtMiscToolbar) {
		// We only add the GWT miscellaneous toolbar items if the GWT
		// UI is active.  Is it?
		if (isGwtUIActive(request)) {
			// Yes!  Add an about toolbar item.
			addAboutToToolbar( bs, request, model, gwtMiscToolbar);
			
			// Do we have a binder and are we running as other than
			// guest?
			if ((null != binder) && (!(isCurrentUserGuest()))) {
				// Yes!  Is the binder we're on other than the profiles
				// container?
				addBrandingToToolbar(bs, request, model, binder, gwtMiscToolbar);
				if (EntityIdentifier.EntityType.profiles != binder.getEntityType()) {
					// Yes!  Add the various binder based toolbar
					// items.
					addClipboardToToolbar(       bs, request, model, binder, gwtMiscToolbar);
					addConfigureColumnsToToolbar(bs, request, model, binder, gwtMiscToolbar);
					addSendEmailToToolbar(       bs, request, model, binder, gwtMiscToolbar);
					addShareBinderToToolbar(     bs, request, model, binder, gwtMiscToolbar);
					addTrackBinderToToolbar(     bs, request, model, binder, gwtMiscToolbar);
					addTrashToToolbar(           bs, request, model, binder, gwtMiscToolbar);
				}
			}
		}
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

	/*
	 * Generates the appropriate resource key for the given binder.
	 * 
	 * Note:  The logic for which key is based on the logic from
	 *        sidebar_track2.jsp.
	 */
	private static String buildRelevanceKey(Binder binder, String keyBase) {
		String relevanceKey = keyBase;
		switch (binder.getEntityType()) {
		case workspace:
			relevanceKey += "Workspace";
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
			if (dFamily.equalsIgnoreCase("calendar")) relevanceKey += "Calendar";
			else                                      relevanceKey += "Folder";
		}
		
		return relevanceKey;
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

		// Clear any previous stuff we may have cached.
		HttpSession hSession = WebHelper.getRequiredSession(hRequest);
		hSession.removeAttribute(CACHED_TABS_KEY);
		hSession.removeAttribute(CACHED_TOOLBARS_KEY);
		hSession.removeAttribute(CACHED_TOP_RANKED_PEOPLE_KEY);
		hSession.removeAttribute(CACHED_TOP_RANKED_PLACES_KEY);

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
		HashMap<String, Map> tbHM = new HashMap<String, Map>();
		for (int i = 0; i < CACHED_TOOLBARS.length; i += 1) {
			// Does a toolbar by this name exist?
			String tbName = CACHED_TOOLBARS[i];
			Map tb = ((Map) model.get(tbName));
			if ((null != tb) && (!(tb.isEmpty()))) {
				// Yes! Add it to the HashMap.
				fixupAdaptedPortletURLs(tb);
				tbHM.put(tbName, tb);
			}
		}

		// Finally, store the stuff we cache in the session cache.
		hSession.setAttribute(CACHED_TABS_KEY,              new GwtUISessionData(model.get(WebKeys.TABS)));
		hSession.setAttribute(CACHED_TOOLBARS_KEY,          new GwtUISessionData(tbHM));
		hSession.setAttribute(CACHED_TOP_RANKED_PEOPLE_KEY, new GwtUISessionData(model.get(WebKeys.FOLDER_ENTRYPEOPLE)));
		hSession.setAttribute(CACHED_TOP_RANKED_PLACES_KEY, new GwtUISessionData(model.get(WebKeys.FOLDER_ENTRYPLACES)));
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
	 * Returns a Binder from it's ID guarding against any exceptions.
	 * If an exception is caught, null is returned. 
	 * 
	 * @param bm
	 * @param binderId
	 * 
	 * @return
	 */
	public static Binder getBinderSafely(BinderModule bm, Long binderId) {
		Binder reply;
		try {
			reply = bm.getBinder(binderId);
			if ((null != reply) && (reply.isDeleted() || isBinderPreDeleted(reply))) {
				reply = null;
			}
		}
		catch (Exception e) {
			m_logger.debug("GwtUIHelper.getBinderSafely(Binder could not be accessed - EXCEPTION:  " + e.getMessage() + ")");
			reply = null;
		}
		return reply;
	}
	
	public static Binder getBinderSafely(BinderModule bm, String binderId) {
		// Always use the initial form of this method.
		return getBinderSafely(bm, Long.parseLong(binderId));
	}
	
	/**
	 * Returns a Binder from it's ID guarding against all but access
	 * control and no binder exceptions.  If another exception is
	 * caught, null is returned.
	 * 
	 * @param bm
	 * @param binderId
	 * 
	 * @return
	 */
	public static Binder getBinderSafely2(BinderModule bm, Long binderId) throws AccessControlException, NoBinderByTheIdException {
		Binder reply;
		try {
			reply = bm.getBinder(binderId);
			if ((null != reply) && (reply.isDeleted() || isBinderPreDeleted(reply))) {
				reply = null;
			}
		}
		catch (Exception e) {
			if (e instanceof AccessControlException) {
				throw ((AccessControlException) e);
			}
			
			else if (e instanceof NoBinderByTheIdException) {
				throw ((NoBinderByTheIdException) e);
			}
			
			else {
				m_logger.debug("GwtUIHelper.getBinderSafely2(Binder could not be accessed - EXCEPTION:  " + e.getMessage() + ")");
				reply = null;
			}
		}
		return reply;
	}
	
	public static Binder getBinderSafely2(BinderModule bm, String binderId) throws AccessControlException, NoBinderByTheIdException {
		// Always use the initial form of this method.
		return getBinderSafely2(bm, Long.parseLong(binderId));
	}
	
	/**
	 * Returns a FolderEntry from it's ID guarding against any
	 * exceptions.  If an exception is caught, null is returned.
	 * 
	 * @param fm
	 * @param binderId
	 * @param entryId
	 * 
	 * @return
	 */
	public static FolderEntry getEntrySafely(FolderModule fm, Long binderId, Long entryId) {
		FolderEntry reply;
		try {
			reply = fm.getEntry(binderId, entryId);
			if ((null != reply) && (reply.isDeleted() || reply.isPreDeleted())) {
				reply = null;
			}
		}
		catch (Exception e) {
			m_logger.debug("GwtUIHelper.getEntrySafely(FolderEntry could not be accessed - EXCEPTION:  " + e.getMessage() + ")");
			reply = null;
		}
		return reply;
	}
	
	public static FolderEntry getEntrySafely(FolderModule fm, Long entryId) {
		// Always use the initial form of the method.
		return getEntrySafely(fm, null, entryId);
	}
	
	public static FolderEntry getEntrySafely(FolderModule fm, String entryId) {
		// Always use the initial form of the method.
		return getEntrySafely(fm, null, Long.parseLong(entryId));
	}
	
	public static FolderEntry getEntrySafely(FolderModule fm, String binderId, String entryId) {
		// Always use the initial form of the method.
		return getEntrySafely(fm, Long.parseLong(binderId), Long.parseLong(entryId));
	}
		
	/**
	 * Returns a FolderEntry from it's ID guarding against all but
	 * access control and no binder exceptions.  If another exception
	 * is caught, null is returned.
	 * 
	 * @param fm
	 * @param binderId
	 * @param entryId
	 * 
	 * @return
	 */
	public static FolderEntry getEntrySafely2(FolderModule fm, Long binderId, Long entryId) throws AccessControlException, NoBinderByTheIdException {
		FolderEntry reply;
		try {
			reply = fm.getEntry(binderId, entryId);
			if ((null != reply) && (reply.isDeleted() || reply.isPreDeleted())) {
				reply = null;
			}
		}
		catch (Exception e) {
			if (e instanceof AccessControlException) {
				throw ((AccessControlException) e);
			}
			
			else if (e instanceof NoBinderByTheIdException) {
				throw ((NoBinderByTheIdException) e);
			}
			
			else {
				m_logger.debug("GwtUIHelper.getEntrySafely2(FolderEntry could not be accessed - EXCEPTION:  " + e.getMessage() + ")");
				reply = null;
			}
		}
		return reply;
	}
	
	public static FolderEntry getEntrySafely2(FolderModule fm, Long entryId) throws AccessControlException, NoBinderByTheIdException {
		// Always use the initial form of the method.
		return getEntrySafely2(fm, null, entryId);
	}
	
	public static FolderEntry getEntrySafely2(FolderModule fm, String entryId) throws AccessControlException, NoBinderByTheIdException {
		// Always use the initial form of the method.
		return getEntrySafely2(fm, null, Long.parseLong(entryId));
	}
	
	public static FolderEntry getEntrySafely2(FolderModule fm, String binderId, String entryId) throws AccessControlException, NoBinderByTheIdException {
		// Always use the initial form of the method.
		return getEntrySafely2(fm, Long.parseLong(binderId), Long.parseLong(entryId));
	}
		
	/**
	 * Returns the string representation of the top most workspace ID.
	 * If the true top workspace ID can't be accessed, the current
	 * user's workspace ID is returned.
	 * 
	 * @param bs
	 * 
	 * @return
	 */
	public static String getTopWSIdSafely(AllModulesInjected bs) {
		Long topWSId;
		
		try {
			topWSId = bs.getWorkspaceModule().getTopWorkspace().getId();
		}
		catch (Exception e) {
			topWSId = null;
		}
		if (null == topWSId) {
			User user = RequestContextHolder.getRequestContext().getUser();
			topWSId = bs.getProfileModule().getEntryWorkspaceId(user.getId());
		}
		
		String reply = ((null == topWSId) ? "" : String.valueOf(topWSId.longValue()));
		return reply;
	}

	/**
	 * Returns s User from it's ID guarding against any exceptions.  If
	 * an exception is caught, null is returned.
	 * 
	 * @param pm
	 * @param userId
	 * 
	 * @return
	 */
	public static User getUserSafely(ProfileModule pm, String userId) {
		return getUserSafely(pm, Long.parseLong(userId));
	}
	
	public static User getUserSafely(ProfileModule pm, Long userId) {
		User reply;
		try {
			reply = ((User) pm.getEntry(userId));
			if ((null != reply) && reply.isDeleted()) {
				reply = null;
			}
		}
		catch (Exception e) {
			m_logger.debug("GwtUIHelper.getUserSafely(User could not be accessed - EXCEPTION:  " + e.getMessage() + ")");
			reply = null;
		}
		return reply;
	}
	
	/**
	 * Returns true if we're supposed to start with an activity stream
	 * on login or false otherwise.
	 * 
	 * @return
	 */
	public static boolean isActivityStreamOnLogin() {
		return IS_ACTIVITY_STREAMS_ON_LOGIN;
	}
	
	/**
	 * Returns true if activity streams are enabled and false otherwise.
	 * 
	 * @return
	 */
	public static boolean isActivityStreamsEnabled() {
		return IS_ACTIVITY_STREAMS_ENABLED;
	}
	
	/**
	 * Returns true if a Binder is preDeleted and false otherwise.
	 * 
	 * @param binder
	 *  
	 * @return
	 */
	public static boolean isBinderPreDeleted(Binder binder) {
		// If we have a Binder...
		boolean reply = false;
		if (null != binder) {
			// ...check it if it's a Folder or Workspace.
			if (binder instanceof Folder) {
				reply = ((Folder) binder).isPreDeleted();
			}
			else if (binder instanceof Workspace) {
				reply = ((Workspace) binder).isPreDeleted();
			}
		}
		
		// If we get here, reply is true if the Binder is
		// preDeleted and false otherwise.
		return reply;
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
	 * Returns true if the GWT UI should be default UI and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isGwtUIDefault() {
		return new GwtUIDefaults().isGwtUIDefault();
	}

	/**
	 * Returns true if the GWT UI should be available and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isGwtUIEnabled() {
		return new GwtUIDefaults().isGwtUIEnabled();
	}

	/**
	 * Returns true if once in GWT UI mode, we should disallow
	 * returning to the traditional UI and false otherwise.
	 * 
	 * @return
	 */
	public static boolean isGwtUIExclusive() {
		return new GwtUIDefaults().isGwtUIExclusive();
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
				Object durangoUI = hSession.getAttribute(GwtUIDefaults.GWT_UI_ENABLED_FLAG);
				if (null == durangoUI) {
					reply = new Boolean(isGwtUIDefault());
				}
				else {
					reply = ((durangoUI instanceof Boolean) && ((Boolean) durangoUI).booleanValue());
				}
			}
		}
		return reply;

	}

	/**
	 * Checks the session for the session captive (i.e., running under
	 * GroupWise) state.  If a value is stored, it's returned.  If a
	 * value is not stored, any captive flag in the request is used to
	 * generate one and that's stored in the session and returned.
	 * 
	 * @param pRequest
	 * 
	 * @return
	 */
	public static boolean isSessionCaptive(PortletRequest pRequest) {
		// Do we have access to the session?
		boolean reply = false;
		PortletSession session = WebHelper.getRequiredPortletSession(pRequest);;
		if (null != session) {
			// Yes!  Do we have a session captive flag stored?
			String captive;
			Boolean isCaptive = ((Boolean) session.getAttribute(WebKeys.SESSION_CAPTIVE));
			if (null == isCaptive) {
				// No!  Store a session captive flag based on there
				// being a 'captive=true' setting in the request.
				captive = PortletRequestUtils.getStringParameter(pRequest, WebKeys.URL_CAPTIVE, "false");
				isCaptive = ((MiscUtil.hasString(captive) && "true".equalsIgnoreCase(captive)) ? Boolean.TRUE : Boolean.FALSE);
				session.setAttribute(WebKeys.SESSION_CAPTIVE, isCaptive);
			}
			reply = isCaptive.booleanValue();			
		}
		
		// If we get here, reply is true if we're in session captive
		// mode and false otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Returns true if we're supposed to change the top entry's
	 * modification time when a reply is posted or modified and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isModifyTopEntryOnReply() {
		return IS_MODIFY_TOP_ENTRY_ON_REPLY;
	}
	
	/*
	 * Takes a Binder permalink and does what's necessary to bring up
	 * the trash on that Binder.
	 */
	public static String getTrashPermalink(String binderPermalink) {
		return appendUrlParam(binderPermalink, WebKeys.URL_SHOW_TRASH, "true");
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
			hSession.setAttribute(GwtUIDefaults.GWT_UI_ENABLED_FLAG, new Boolean(gwtUIActive && isGwtUIEnabled()));
		}
	}

	/**
	 * Returns the URL for the GWT UI to use to bring up help.
	 * 
	 * @return
	 */
	private final static String BASE_HELPURL = "http://www.novell.com/documentation/vibe_onprem3/vibeprem3_user/data/bookinfo.html";
	public static String getHelpUrl() {
		return MiscUtil.localizeHelpUrl(BASE_HELPURL);
	}
}
