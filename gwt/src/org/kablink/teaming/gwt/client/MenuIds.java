/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client;

/**
 * String IDs used by various menus.
 * 
 * @author drfoster@novell.com
 */
public class MenuIds {
	// Entry menu bar IDs.
	public final static String	ENTRY_DEFINED_FILTER		= "ss_entryMenuDefinedFilter";
	public final static String	ENTRY_COPY_FILTERS			= "ss_entryMenuCopyFilters";
	public final static String	ENTRY_EDIT_FILTERS			= "ss_entryMenuEditFilters";
	public final static String	ENTRY_FILTER_COMPOSITE		= "ss_entryMenuFilterComposite";
	public final static String	ENTRY_FILTER_DIV			= "ss_entryMenuFilterDIV";
	public final static String	ENTRY_FILTER_IMAGE			= "ss_entryMenuFilterImage";
	public final static String	ENTRY_FILTER_INPUT			= "ss_entryMenuFilterInput";
	public final static String	ENTRY_FILTER_POPUP			= "ss_entryMenuFilterPopup";
	public final static String	ENTRY_FILTERS_OFF			= "ss_entryMenuFiltersOff";
	public final static String	ENTRY_MENU					= "ss_entryMenu";
	
	// Folder entry viewer menu bar IDs.
	public final static String	FEVIEW_MENU					= "ss_feViewMenu";
	
	// Main menu bar IDs.
	public final static String	MAIN_BREADCRUMB_BROWSER		= "ss_mainMenuBreadCrumbBrowser";
	public final static String	MAIN_CLOSE_ADMIN			= "ss_mainMenuCloseAdmin";
	public final static String	MAIN_GLOBAL_SEARCH_BAR		= "ss_mainMenuGlobalSearchBar";
	public final static String	MAIN_GLOBAL_SEARCH_BUTTON	= "ss_mainMenuGlobalSearchButton";
	public final static String	MAIN_GLOBAL_SEARCH_OPTIONS	= "ss_mainMenuGlobalSearchOptions";
	public final static String	MAIN_GLOBAL_SEARCH_INPUT	= "ss_mainMenuGlobalSearchInput";
	public final static String	MAIN_MANAGE					= "ss_mainMenuManage";
	public final static String	MAIN_MASTHEAD_VISIBILITY	= "ss_mainMenuMastheadVisibility";
	public final static String	MAIN_MY_FAVORITES			= "ss_mainMenuMyFavorites";
	public final static String	MAIN_MY_TEAMS				= "ss_mainMenuMyTeams";
	public final static String	MAIN_MY_WORKSPACE			= "ss_mainMenuMyWorkspace";
	public final static String	MAIN_RECENT_PLACES			= "ss_mainMenuRecentPlaces";
	public final static String	MAIN_SIDEBAR_VISIBILITY		= "ss_mainMenuSidebarVisibility";
	public final static String	MAIN_VIEWS					= "ss_mainMenuViews";
	public final static String	MAIN_WHATS_NEW				= "ss_mainMenuWhatsNew";

	// Manage menu (Folder and Workspace) IDs.
	public final static String	MANAGE_COMMON				= "ss_manageMenuCommon";
	public final static String	MANAGE_EDIT_TEAM			= "editTeam";
	public final static String	MANAGE_MAIL_TEAM			= "mailTeam";
	public final static String	MANAGE_MEET_TEAM			= "meetTeam";
	public final static String	MANAGE_VIEW_TEAM			= "viewTeam";
	
	/*
	 * Constructor method. 
	 */
	private MenuIds() {
		// Inhibits this class from being instantiated.
	}
}
