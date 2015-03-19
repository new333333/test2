/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.SearchRecentPlaceEvent;
import org.kablink.teaming.gwt.client.rpc.shared.GetRecentPlacesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetRecentPlacesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.ContextBinderProvider;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Class used for the recent places menu item popup.  
 * 
 * @author drfoster@novell.com
 */
public class RecentPlacesMenuPopup extends MenuBarPopupBase {
	private final String IDBASE = "recentPlaces_";
	
	private BinderInfo m_currentBinder;	// The currently selected binder.
	
	/*
	 * Inner class that handles selecting an individual place.
	 */
	private class PlaceCommand implements Command {
		private RecentPlaceInfo m_place;	// The place selected on.

		/**
		 * Constructor method.
		 * 
		 * @param place
		 */
		PlaceCommand(RecentPlaceInfo place) {
			// Initialize the super class...
			super();
			
			// ...and store the parameter.
			m_place = place;
		}

		/**
		 * Called when the user selects a place.
		 * 
		 * Implements the Command.execute() method.
		 */
		@Override
		public void execute() {
			// Trigger the appropriate action for the place.
			switch (m_place.getTypeEnum()) {
			case BINDER:
				EventHelper.fireChangeContextEventAsync(
					m_place.getBinderId(),
					m_place.getPermalinkUrl(),
					Instigator.RECENT_PLACE_SELECT);
				
				break;
			
			case SEARCH:
				GwtTeaming.fireEvent(
					new SearchRecentPlaceEvent(
						new Integer(
							m_place.getId())));
				
				break;
			}
		}
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param binderProvider
	 */
	public RecentPlacesMenuPopup(ContextBinderProvider binderProvider) {
		// Initialize the super class.
		super(binderProvider);
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
	 * Not used for the Recent Places menu.
	 * 
	 * Implements the MenuBarPopupBase.setToolbarItemList() abstract
	 * method.
	 * 
	 * @param toolbarItemList
	 */
	@Override
	public void setToolbarItemList(List<ToolbarItem> toolbarItemList) {
		// Unused.
	}
	
	/**
	 * Not used for the Recent Places menu.  Always returns true.
	 * 
	 * Implements the MenuBarPopupBase.shouldShowMenu() abstract
	 * method.
	 * 
	 * @return
	 */
	@Override
	public boolean shouldShowMenu() {
		return true;
	}
	
	/**
	 * Completes construction of the menu.
	 * 
	 * Implements the MenuBarPopupBase.populateMenu() abstract method.
	 */
	@Override
	public void populateMenu() {
		// Have we populated the menu yet?
		if (!(hasContent())) {
			// No!  Populate it now.
			GetRecentPlacesCmd cmd = new GetRecentPlacesCmd(Long.parseLong(m_currentBinder.getBinderId()));
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetRecentPlaces());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response)  {
					List<RecentPlaceInfo> rpList;
					GetRecentPlacesRpcResponseData responseData;
					
					responseData = (GetRecentPlacesRpcResponseData) response.getResponseData();
					rpList = responseData.getRecentPlaces();
					
					// Populate the 'Recent Places' popup menu
					// asynchronously so that we can release the AJAX
					// request ASAP.
					populateRecentPlacesMenuAsync(rpList);
				}
			});
		}
	}
	
	/*
	 * Asynchronously populates the 'Recent Places' popup menu.
	 */
	private void populateRecentPlacesMenuAsync(final List<RecentPlaceInfo> rpList) {
		ScheduledCommand populateMenu = new ScheduledCommand() {
			@Override
			public void execute() {
				populateRecentPlacesMenuNow(rpList);
			}
		};
		Scheduler.get().scheduleDeferred(populateMenu);
	}
	
	/*
	 * Synchronously populates the 'Recent Places' popup menu.
	 */
	private void populateRecentPlacesMenuNow(List<RecentPlaceInfo> rpList) {
		// Scan the places...
		int mtCount = 0;
		MenuPopupAnchor rpA;
		for (Iterator<RecentPlaceInfo> rpIT = rpList.iterator(); rpIT.hasNext(); ) {
			// ...creating an item structure for each.
			RecentPlaceInfo place = rpIT.next();
			String rpId = (IDBASE + place.getId());
			
			rpA = new MenuPopupAnchor(rpId, place.getTitle(), place.getEntityPath(), new PlaceCommand(place));
			addContentMenuItem(rpA);
			mtCount += 1;
		}
		
		// If there weren't any places...
		if (0 == mtCount) {
			// ...put something in the menu that tells the user
			// ...that.
			MenuPopupLabel content = new MenuPopupLabel(m_messages.mainMenuRecentPlacesNoPlaces());
			addContentMenuItem(content);
		}
	}
}
