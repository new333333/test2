/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.util;

import org.kablink.teaming.gwt.client.util.TreeInfo;


/**
 * Class used to communicate information about a selected binder
 * between the WorkspaceTreeControl and its registered ActionHandler's.
 * 
 * @author drfoster@novell.com
 */
public class OnSelectBinderInfo {
	private boolean m_forceSidebarReload;	// true -> Regardless of the instigator, force the sidebar tree to reload.
	private boolean m_isPermalinkUrl;
	private boolean m_isTrash;
	private Instigator m_instigator = Instigator.UNKNOWN;
	private Long m_binderId;
	private String m_binderUrl;

	// Various marker strings used to recognize the format of a URL.
	private final static String GWT_MARKER = "seen_by_gwt";

	// Used to identify the instigator of the Binder selection.
	public enum Instigator {
		ACTIVITY_STREAM_BINDER_SELECT,	// The binder link in an activity stream was selected.
		ACTIVITY_STREAM_SOURCE_SELECT,	// The top level source link of an activity stream was selected.
		BREADCRUMB_TREE_SELECT,			// A binder in the bread crumb tree was selected. 
		CONTENT_AREA_CHANGED,			// The content of the content area changed.
		FAVORITE_SELECT,				// A favorite was selected from the My Favorites menu.
		FORCE_SIDEBAR_RELOAD,			// Forces the sidebar to reload.
		FORCE_FULL_RELOAD,				// Forces the full UI to reload.
		GOTO_CONTENT_URL,				// User clicked on something that loads some URL into the content frame.
		PROFILE_QUICK_VIEW_SELECT,		// The workspace or profile button in the quick view dialog was selected.
		RECENT_PLACE_SELECT,			// A recent place was selected from the Recent Places menu.
		SEARCH_SELECT,					// A link from the search options dialog search results was selected.
		SIDEBAR_TREE_SELECT,			// A binder in the sidebar tree was select.
		TEAM_SELECT,					// A team was selected from the My Teams menu.
		TRACKED_USER_SELECT,			// A tracked user was selected from the profile page.
		VIEW_TEAM_MEMBERS,				// The members of the current binders team are to be show.
		
		UNKNOWN,						// Default value.  Should never be processed.
	}

	/**
	 * Constructor method.  (1 of 4)
	 * 
	 * @param ti
	 */
	public OnSelectBinderInfo(TreeInfo ti, Instigator instigator) {
		// Always use the final form of the constructor.
		this(Long.parseLong(ti.getBinderInfo().getBinderId()), ti.getBinderPermalink(), ti.getBinderInfo().isBinderTrash(), instigator);
	}

	/**
	 * Constructor method.  (2 of 4)
	 * 
	 * @param binderId
	 * @param binderUrl
	 * @param isTrash
	 * @param instigator
	 */
	public OnSelectBinderInfo(String binderId, String binderUrl, boolean isTrash, Instigator instigator) {
		// Always use the final form of the constructor.
		this(Long.parseLong(binderId), binderUrl, isTrash, instigator);
	}
	
	/**
	 * Constructor method.  (3 of 4)
	 * 
	 * @param binderUrl
	 * @param isTrash
	 * @param instigator
	 */
	public OnSelectBinderInfo(String binderUrl, boolean isTrash, Instigator instigator) {
		// Always use the final form of the constructor.
		this(((Long) null), binderUrl, isTrash, instigator);
	}
	
	/**
	 * Constructor method.  (4 of 4)
	 * 
	 * @param binderId
	 * @param binderUrl
	 * @param isTrash
	 * @param instigator
	 */
	public OnSelectBinderInfo(Long binderId, String binderUrl, boolean isTrash, Instigator instigator) {
		// Simply store the parameters.
		setBinderId(binderId);
		setBinderUrl(binderUrl);
		m_isTrash = isTrash;
		m_instigator = instigator;
	}

	/*
	 * Performs any URL fixing that's required based on the format of
	 * the URL.
	 */
	private void fixupUrl() {
		// If the URL is a permalink...
		m_isPermalinkUrl = GwtClientHelper.isPermalinkUrl(m_binderUrl);
		if (m_isPermalinkUrl) {
			// ...add a GWT marker.
			m_binderUrl = GwtClientHelper.appendUrlParam(m_binderUrl, GWT_MARKER, "1");
		}
	}
	
	/**
	 * Returns the Binder ID from this OnSelectBinderInfo object.
	 * 
	 * @return
	 */
	public Long getBinderId() {
		return m_binderId;
	}
	
	/**
	 * Returns the Binder URL from this OnSelectBinderInfo object.
	 * 
	 * @return
	 */
	public String getBinderUrl() {
		return m_binderUrl;
	}

	/**
	 * Returns the true if the sidebar should be forced to reload
	 * regardless of the instigator and false otherwise.
	 * 
	 * @return
	 */
	public boolean getForceSidebarReload() {
		return m_forceSidebarReload;
	}
	
	/**
	 * Returns the instigator of the Binder selection, if known.
	 * 
	 * @return
	 */
	public Instigator getInstigator() {
		return m_instigator;
	}
	/**
	 * Returns true if the Binder URL is a permalink and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isPermalinkUrl() {
		return m_isPermalinkUrl;
	}
	
	/**
	 * Returns true if for a Binder's trash and false otherwise.
	 * 
	 * @return
	 */
	public boolean isTrash() {
		return m_isTrash;
	}
	
	/*
	 * Stores the Binder ID into this OnSelectBinderInfo object.
	 */
	private void setBinderId(Long binderId) {
		m_binderId = binderId;
	}
	
	/**
	 * Stores a Binder URL into this OnSelectBinderInfo object.
	 * 
	 * @param binderUrl
	 */
	public void setBinderUrl(String binderUrl) {
		m_binderUrl = binderUrl;
		if (GwtClientHelper.hasString(m_binderUrl)) {
			fixupUrl();
		}
	}

	/**
	 * Stores a true/false flag indicating whether the sidebar should
	 * be forced to reload regardless of the instigator.
	 * 
	 * @param forceSidebarReload
	 */
	public void setForceSidebarReload(boolean forceSidebarReload) {
		m_forceSidebarReload = forceSidebarReload;
	}
	
	/**
	 * Store an instigator for the binder selection.
	 * 
	 * @param instigator
	 */
	public void setInstigator(Instigator instigator) {
		m_instigator = instigator;
	}
}
