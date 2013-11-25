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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.SearchRecentPlaceEvent;
import org.kablink.teaming.gwt.client.rpc.shared.GetRecentPlacesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetRecentPlacesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * Class used for the recent places menu item popup.  
 * 
 * @author drfoster@novell.com
 */
public class RecentPlacesMenuPopup extends MenuBarPopupBase {
	private final String IDBASE = "recentPlaces_";
	
	@SuppressWarnings("unused")
	private BinderInfo m_currentBinder;	// The currently selected binder.
	
	/*
	 * Inner class that handles clicks on individual places.
	 */
	private class PlaceClickHandler implements ClickHandler {
		private RecentPlaceInfo m_place;	// The place clicked on.

		/**
		 * Class constructor.
		 * 
		 * @param place
		 */
		PlaceClickHandler(RecentPlaceInfo place) {
			// Simply store the parameter.
			m_place = place;
		}

		/**
		 * Called when the user clicks on a place.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			// Hide the menu...
			hide();
			
			// ...and trigger the appropriate action for the place.
			switch (m_place.getTypeEnum()) {
			case BINDER:
				GwtTeaming.fireEvent(
					new ChangeContextEvent(
						new OnSelectBinderInfo(
							m_place.getBinderId(),
							m_place.getPermalinkUrl(),
							false,
							Instigator.RECENT_PLACE_SELECT)));
				
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
	 * Class constructor.
	 */
	public RecentPlacesMenuPopup() {
		// Initialize the super class.
		super(GwtTeaming.getMessages().mainMenuBarRecentPlaces());
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
	 * Completes construction of the menu and shows it.
	 * 
	 * Implements the MenuBarPopupBase.showPopup() abstract method.
	 * 
	 * @param left
	 * @param top
	 */
	@Override
	public void showPopup(int left, int top) {
		GetRecentPlacesCmd cmd;
		
		// Position the popup and if we've already constructed its
		// content...
		setPopupPosition(left, top);
		if (hasContent()) {
			// ...simply show it and bail.
			show();
			return;
		}
		
		// Otherwise, read the users recent places.
		cmd = new GetRecentPlacesCmd();
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetRecentPlaces());
			}
			
			public void onSuccess(VibeRpcResponse response)  {
				List<RecentPlaceInfo> rpList;
				GetRecentPlacesRpcResponseData responseData;
				
				responseData = (GetRecentPlacesRpcResponseData) response.getResponseData();
				rpList = responseData.getRecentPlaces();
				
				// Show the 'Recent Places' popup menu asynchronously
				// so that we can release the AJAX request ASAP.
				showRecentPlacesMenuAsync(rpList);
			}
		});
	}
	
	/*
	 * Asynchronously shows the 'Recent Places' popup menu.
	 */
	private void showRecentPlacesMenuAsync(final List<RecentPlaceInfo> rpList) {
		ScheduledCommand showMenu = new ScheduledCommand() {
			@Override
			public void execute() {
				showRecentPlacesMenuNow(rpList);
			}
		};
		Scheduler.get().scheduleDeferred(showMenu);
	}
	
	/*
	 * Synchronously shows the 'Recent Places' popup menu.
	 */
	private void showRecentPlacesMenuNow(List<RecentPlaceInfo> rpList) {
		// Scan the places...
		int mtCount = 0;
		MenuPopupAnchor rpA;
		for (Iterator<RecentPlaceInfo> rpIT = rpList.iterator(); rpIT.hasNext(); ) {
			// ...creating an item structure for each.
			RecentPlaceInfo place = rpIT.next();
			String rpId = (IDBASE + place.getId());
			
			rpA = new MenuPopupAnchor(rpId, place.getTitle(), place.getEntityPath(), new PlaceClickHandler(place));
			addContentWidget(rpA);
			mtCount += 1;
		}
		
		// If there weren't any places...
		if (0 == mtCount) {
			// ...put something in the menu that tells the user
			// ...that.
			MenuPopupLabel content = new MenuPopupLabel(m_messages.mainMenuRecentPlacesNoPlaces());
			addContentWidget(content);
		}
		
		// Finally, show the menu popup.
		show();
	}
}
