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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * Class used for the Activity Streams menu item popup.  
 * 
 * @author drfoster@novell.com
 */
public class ActivityStreamsPopup extends MenuBarPopupBase {
	private final String IDBASE = "activityStreams_";	// Base ID for the items created in this menu.
	
	private BinderInfo m_currentBinder;	// The currently selected binder.
	private int m_menuLeft;				// Left coordinate of where the menu is to be placed.
	private int m_menuTop;				// Top  coordinate of where the menu is to be placed.
	private String[] m_myFavoritesIds;	// Binder IDs of the user's favorites.
	private String[] m_myTeamsIds;		// Binder IDs of the user's team's
	
	/**
	 * Class constructor.
	 * 
	 * @param actionTrigger
	 */
	public ActivityStreamsPopup(ActionTrigger actionTrigger) {
		// Initialize the super class...
		super(actionTrigger, GwtTeaming.getMessages().mainMenuBarActivityStreams());
		
		// ...and read the information needed for the menu items.
        DeferredCommand.addCommand(
	    	new Command() {
	    		public void execute() {
	    			getMyTeams();
	    			getMyFavorites();
	    			getFollowedPeople();
	    			getFollowedPlaces();
	    		}
	    });		
	}

	/*
	 * Adds the My Favorites menu item to the Activity Streams popup
	 * menu as needed.
	 */
	private void addMyFavoritesMenuItem() {
		// Do we have any favorites?
		int myFavorites = ((null == m_myFavoritesIds) ? 0 : m_myFavoritesIds.length);
		if (0 < myFavorites) {
			// Yes!  Construct the ToolbarItem...
			ToolbarItem tbi = new ToolbarItem();
			tbi = new ToolbarItem();
			tbi.setName("asMyFavorites");
			tbi.setTitle(m_messages.mainMenuActivityStreamsMyFavorites());

			// ...apply the selected Activity Stream...
			ActivityStreamInfo asi = new ActivityStreamInfo(ActivityStream.MY_FAVORITES, m_myFavoritesIds);
			tbi.setTeamingAction(TeamingAction.ACTIVITY_STREAM);
			tbi.setUrl(asi.getStringValue());

			// ...and add the ToolbarItem to the menu.
			addContextMenuItem(IDBASE, tbi);
		}
	}
	
	/*
	 * Adds the My Teams menu item to the Activity Streams popup menu
	 * as needed.
	 */
	private void addMyTeamsMenuItem() {
		// Do we have any teams?
		int myTeams = ((null == m_myTeamsIds) ? 0 : m_myTeamsIds.length);
		if (0 < myTeams) {
			// Yes!  Construct the ToolbarItem...
			ToolbarItem tbi = new ToolbarItem();
			tbi = new ToolbarItem();
			tbi.setName("asMyTeams");
			tbi.setTitle(m_messages.mainMenuActivityStreamsMyTeams());

			// ...apply the selected Activity Stream...
			ActivityStreamInfo asi = new ActivityStreamInfo(ActivityStream.MY_TEAMS, m_myTeamsIds);
			tbi.setTeamingAction(TeamingAction.ACTIVITY_STREAM);
			tbi.setUrl(asi.getStringValue());

			// ...and add the ToolbarItem to the menu.
			addContextMenuItem(IDBASE, tbi);
		}
	}
	
	/*
	 * Adds the Followed People menu item to the Activity Streams popup
	 * menu as needed.
	 */
	private void addFollowedPeopleMenuItem() {
//!		...this needs to be implemented...
	}
	
	/*
	 * Adds the Followed Places menu item to the Activity Streams popup
	 * menu as needed.
	 */
	private void addFollowedPlacesMenuItem() {
//!		...this needs to be implemented...
	}
	
	/*
	 * Adds the Site Wide menu item to the Activity Streams popup menu
	 * as needed.
	 */
	private void addSiteWideMenuItem() {
		// Construct the ToolbarItem...
		ToolbarItem tbi = new ToolbarItem();
		tbi = new ToolbarItem();
		tbi.setName("asSiteWide");
		tbi.setTitle(m_messages.mainMenuActivityStreamsSiteWide());

		// ...apply the selected Activity Stream...
		ActivityStreamInfo asi = new ActivityStreamInfo(ActivityStream.SITE_WIDE);
		tbi.setTeamingAction(TeamingAction.ACTIVITY_STREAM);
		tbi.setUrl(asi.getStringValue());

		// ...and add the ToolbarItem to the menu.
		addContextMenuItem(IDBASE, tbi);
	}
	
	/*
	 * Adds the Current Workspace/Folder menu item to the Activity
	 * Streams popup menu as needed.
	 */
	private void addCurrentBinderMenuItem() {
		String itemName;
		
		// Construct the ToolbarItem...
		switch (m_currentBinder.getBinderType()) {
		default:
		case OTHER:      itemName = m_messages.mainMenuActivityStreamsCurrentBinder();    break;
		case FOLDER:     itemName = m_messages.mainMenuActivityStreamsCurrentFolder();    break;
		case WORKSPACE:  itemName = m_messages.mainMenuActivityStreamsCurrentWorkspace(); break;
		}

		ToolbarItem tbi = new ToolbarItem();
		tbi = new ToolbarItem();
		tbi.setName("asCurrentBinder");
		tbi.setTitle(itemName);
		
		// ...apply the selected Activity Stream...
		ActivityStreamInfo asi = new ActivityStreamInfo(ActivityStream.CURRENT_BINDER, m_currentBinder.getBinderId());
		tbi.setTeamingAction(TeamingAction.ACTIVITY_STREAM);
		tbi.setUrl(asi.getStringValue());
		
		// ...and add the ToolbarItem to the menu.
		addContextMenuItem(IDBASE, tbi);
	}

	/*
	 * Pulls the user's team information from the server.
	 */
	private void getMyTeams() {
		// Can we read the user's teams?
		m_rpcService.getMyTeams(new HttpRequestInfo(), new AsyncCallback<List<TeamInfo>>() {
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem.
				GwtClientHelper.handleGwtRPCFailure(
					m_messages.rpcFailure_GetMyTeams());
			}
			
			public void onSuccess(List<TeamInfo> mtList)  {
				// Yes!  Are there any teams?
				int mtCount = mtList.size();
				m_myTeamsIds = new String[mtCount];
				if (0 < mtCount) {
					// Yes!  Scan them...
					int i = 0;
					for (TeamInfo mt: mtList) {
						// ...adding their IDs to the My Teams IDs
						// ...array.
						m_myTeamsIds[i++] = mt.getBinderId();
					}
				}
			}
		});
	}
	
	/*
	 * Pulls the user's favorites information from the server.
	 */
	private void getMyFavorites() {
		// Can we read the user's favorites?
		m_rpcService.getFavorites(new AsyncCallback<List<FavoriteInfo>>() {
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem.
				GwtClientHelper.handleGwtRPCFailure(
					m_messages.rpcFailure_GetFavorites());
			}
			
			public void onSuccess(final List<FavoriteInfo> fList)  {
				// Yes!  Are there any favorites?
				int fCount = fList.size();
				m_myFavoritesIds = new String[fCount];
				if (0 < fCount) {
					// Yes!  Scan them...
					int i = 0;
					for (FavoriteInfo f: fList) {
						// ...adding their IDs to the My Favorites IDs
						// ...array.
						m_myFavoritesIds[i++] = f.getValue();
					}
				}
			}
		});
	}
	
	/*
	 * Pulls the user's followed people information from the server.
	 */
	private void getFollowedPeople() {
//!		...this needs to be implemented...		
	}
	
	/*
	 * Pulls the user's followed places information from the server.
	 */
	private void getFollowedPlaces() {
//!		...this needs to be implemented...		
	}
	
	/**
	 * Stores information about the currently selected binder.
	 * 
	 * Implements the MenuBarPopupBase.setCurrentBinder() abstract
	 * method.
	 * 
	 * @param binderInfo
	 */
	@Override
	public void setCurrentBinder(BinderInfo binderInfo) {
		// Simply store the parameter.
		m_currentBinder = binderInfo;
	}

	/**
	 * Store information about the context based toolbar requirements
	 * via a List<ToolbarItem>.
	 * 
	 * Implements the MenuBarPopupBase.setToolbarItemList() abstract
	 * method.
	 * 
	 * @param toolbarItemList
	 */
	@Override
	public void setToolbarItemList(List<ToolbarItem> toolbarItemList) {
		// ToolbarItems's are not used for the Activity Streams menu.
	}
	
	/**
	 * Called to determine if given the List<ToolbarItem>, should
	 * the menu be shown.  Returns true if it should be shown and false
	 * otherwise.
	 * 
	 * Implements the MenuBarPopupBase.shouldShowMenu() abstract
	 * method.
	 * 
	 * @return
	 */
	@Override
	public boolean shouldShowMenu() {
		// The Activity Streams menu is always shown.
		return true;
	}

	/**
	 * Completes construction of the menu and shows it.
	 * 
	 * Implements the MenuBarPopupBase.showPopup() abstract method.
	 * 
	 * @param left
	 * @param top
	 */
	@Override
	public void showPopup(int left, int top) {
		// Position the menu...
		m_menuLeft = left;
		m_menuTop  = top;
		setPopupPosition(m_menuLeft, m_menuTop);
		
		// Have we constructed the menu's contents yet?
		if (!(hasContent())) {
			// No!  Construct it now.
			addMyTeamsMenuItem();
			addMyFavoritesMenuItem();
			addFollowedPeopleMenuItem();
			addFollowedPlacesMenuItem();
			addSiteWideMenuItem();
			addSpacerMenuItem();
			addCurrentBinderMenuItem();
		}
					
		// Finally, show the popup.
		show();
	}
}
