/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to communicate information about context (i.e.,
 * binder, ...) selection.
 * 
 * @author drfoster@novell.com
 */
public class OnSelectBinderInfo {
	private BinderInfo	m_binderInfo;	//
	private boolean		m_permalinkUrl;	//
	private Instigator	m_instigator;	//
	private String		m_binderUrl;	//

	// Various marker strings used to recognize the format of a URL.
	private final static String GWT_MARKER = "seen_by_gwt";

	/**
	 * Enumeration used to identify the instigator of a binder
	 * selection.
	 */
	public enum Instigator implements IsSerializable {
		ACTIVITY_STREAM_BINDER_SELECT,	// The binder link in an activity stream was selected.
		ACTIVITY_STREAM_SOURCE_SELECT,	// The top level source link of an activity stream was selected.
		ADMINISTRATION_CONSOLE,			// The admin console instigated the selection.
		BLOG_PAGE_SELECT,				// The selection in the blog view changed.
		BREADCRUMB_TREE_SELECT,			// A binder in the bread crumb tree was selected. 
		FAVORITE_SELECT,				// A favorite was selected from the My Favorites menu.
		FORCE_FULL_RELOAD,				// Forces the full UI to reload.
		GOTO_CONTENT_URL,				// User clicked on something that loads some URL into the content frame.
		JSP_CONTENT_LOADED,				// Unknown JSP content has been loaded into the content area.
		PROFILE_QUICK_VIEW_SELECT,		// The workspace or profile button in the quick view dialog was selected.
		RECENT_PLACE_SELECT,			// A recent place was selected from the Recent Places menu.
		SEARCH_RESULTS,					// The result of a search is being loaded.
		SEARCH_SELECT,					// A link from the search options dialog search results was selected.
		SIDEBAR_TREE_SELECT,			// A binder in the sidebar tree was select.
		TEAM_SELECT,					// A team was selected from the My Teams menu.
		TRACKED_USER_SELECT,			// A tracked user was selected from the profile page.
		VIEW_TEAM_MEMBERS,				// The members of the current binders team are to be show.
		VIEW_FOLDER_ENTRY,				// A folder entry is being shown.
		
		UNKNOWN;						// Default value.  Should never be processed.
	}

	/**
	 * Class constructor.
	 * 
	 * @param ti
	 * @param instigator
	 */
	public OnSelectBinderInfo(TreeInfo ti, Instigator instigator) {
		// Always use the final form of the constructor.
		this(ti.getBinderInfo(), ti.getBinderPermalink(), instigator);
	}

	/**
	 * Class constructor.
	 * 
	 * @param binderUrl
	 * @param instigator
	 */
	public OnSelectBinderInfo(String binderUrl, Instigator instigator) {
		// Always use the final form of the constructor.
		this(((BinderInfo) null), binderUrl, instigator);
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param binderInfo
	 * @param binderUrl
	 * @param instigator
	 */
	public OnSelectBinderInfo(BinderInfo binderInfo, String binderUrl, Instigator instigator) {
		// Initialize the super class...
		super();
		
		// ...and store the parameters.
		setBinderInfo(binderInfo);
		setBinderUrl( binderUrl );
		setInstigator(instigator);
	}

	/*
	 * Performs any URL fixing that's required based on the format of
	 * the URL.
	 */
	private void fixupUrl() {
		// If the URL is a permalink...
		m_permalinkUrl = GwtClientHelper.isPermalinkUrl(m_binderUrl);
		if (m_permalinkUrl) {
			// ...add a GWT marker.
			m_binderUrl = GwtClientHelper.appendUrlParam(m_binderUrl, GWT_MARKER, "1");
		}
	}

	/**
	 * Returns the binder ID from this OnSelectBinderInfo object.
	 * 
	 * @return
	 */
	public Long getBinderId() {
		return ((null == m_binderInfo) ? null : m_binderInfo.getBinderIdAsLong());
	}
	
	/**
	 * Returns the BinderInfo from this OnSelectBinderInfo object.
	 * 
	 * @return
	 */
	public BinderInfo getBinderInfo() {
		return m_binderInfo;
	}
	
	/**
	 * Returns the binder URL from this OnSelectBinderInfo object.
	 * 
	 * @return
	 */
	public String getBinderUrl() {
		return m_binderUrl;
	}

	/**
	 * Returns the instigator of the binder selection, if known.
	 * 
	 * @return
	 */
	public Instigator getInstigator() {
		return m_instigator;
	}
	
	/**
	 * Returns true if this OnSelectBinderInfo refers to a collection
	 * and false otherwise.
	 * 
	 * @return
	 */
	public boolean isCollection() {
		return ((null != m_binderInfo) && m_binderInfo.isBinderCollection());
	}
	
	/**
	 * Returns true if the binder URL is a permalink and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isPermalinkUrl() {
		return m_permalinkUrl;
	}
	
	/**
	 * Returns true if for a Binder's trash and false otherwise.
	 * 
	 * @return
	 */
	public boolean isTrash() {
		return ((null != m_binderInfo) && m_binderInfo.isBinderTrash());
	}

	/*
	 * Stores the BinderInfo into this OnSelectBinderInfo object.
	 */
	private void setBinderInfo(BinderInfo binderInfo) {
		m_binderInfo = binderInfo;
	}

	/**
	 * Stores a binder URL into this OnSelectBinderInfo object.
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
	 * Store an instigator for the binder selection.
	 * 
	 * @param instigator
	 */
	public void setInstigator(Instigator instigator) {
		m_instigator = instigator;
	}
}
