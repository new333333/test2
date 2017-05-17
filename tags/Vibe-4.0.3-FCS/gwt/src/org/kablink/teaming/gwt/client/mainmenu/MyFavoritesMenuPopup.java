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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.rpc.shared.AddFavoriteCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFavoritesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFavoritesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.RemoveFavoriteCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.ContextBinderProvider;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Class used for the My Favorites menu item popup.  
 * 
 * @author drfoster@novell.com
 */
public class MyFavoritesMenuPopup extends MenuBarPopupBase {
	private final String IDBASE = "myFavorites_";
	
	private BinderInfo m_currentBinder;	// The currently selected binder.

	/*
	 * Defines the management operations supported on the favorites.
	 */
	private enum FavoriteOperation {
		ADD,
		EDIT,
		REMOVE
	}
	
	/*
	 * Inner class that handles selecting on an individual favorite.
	 */
	private class FavoriteCommand implements Command {
		private FavoriteInfo m_favorite;	// The favorite selected.

		/**
		 * Constructor method.
		 * 
		 * @param favorite
		 */
		FavoriteCommand(FavoriteInfo favorite) {
			// Initialize the super class...
			super();
			
			// ...and store the parameter.
			m_favorite = favorite;
		}

		/**
		 * Called when the user selects a favorite.
		 * 
		 * Implements the Command.execute() method.
		 */
		@Override
		public void execute() {
			// Fire a selection changed event.
			GetBinderPermalinkCmd cmd = new GetBinderPermalinkCmd(m_favorite.getValue());
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
						m_favorite.getValue());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					String binderPermalink;
					StringRpcResponseData responseData;

					responseData = (StringRpcResponseData) response.getResponseData();
					binderPermalink = responseData.getStringValue();
					
					// Fire the selection changed event
					// asynchronously so that we can release the AJAX
					// request ASAP.
					EventHelper.fireChangeContextEventAsync(
						m_favorite.getValue(),
						binderPermalink,
						Instigator.FAVORITE_SELECT);
				}
			});
		}
	}
	
	/*
	 * Inner class that handles selects a favorites management command.
	 */
	private class ManageCommand implements Command {
		private FavoriteOperation m_operation;		// The which favorite management operation to perform.
		private List<FavoriteInfo> m_favoritesList;	// ADD/REMOVE:  Not used.  EDIT:  The user's current favorites list.
		private String m_id;						// ADD:  ID of binder to add.  REMOVE:  ID of favorite to remove.  EDIT:  Not used.
		
		/**
		 * Constructor method.
		 * 
		 * @param operation
		 * @param id
		 */
		ManageCommand(FavoriteOperation operation, String id) {
			// Initial the super class...
			super();
			
			// ...and store the parameters.
			m_operation = operation;
			m_id = id;
		}

		/**
		 * Constructor method.
		 * 
		 * @param operation
		 * @param fList
		 */
		ManageCommand(FavoriteOperation operation, List<FavoriteInfo> fList) {
			// Initialize the super class...
			super();
			
			// ...and store the parameters.
			m_operation     = operation;
			m_favoritesList = fList;
		}
		
		/**
		 * Called when the user selects a favorites management
		 * command.
		 * 
		 * Implements the Command.execute() method.
		 */
		@Override
		public void execute() {
			// What operation are we performing?
			switch (m_operation) {
			case ADD:
				AddFavoriteCmd cmd;
				
				// Adding the current binder to the favorites!
				cmd = new AddFavoriteCmd( m_id );
				GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable t) {
						GwtClientHelper.handleGwtRPCFailure(
							t,
							m_messages.rpcFailure_AddFavorite(),
							m_id);
					}
					@Override
					public void onSuccess(VibeRpcResponse response)  {}
				});
				break;
				
			case REMOVE:
				RemoveFavoriteCmd rfCmd;
				
				// Removing the current binder from the favorites!
				rfCmd = new RemoveFavoriteCmd( m_id );
				GwtClientHelper.executeCommand( rfCmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable t) {
						GwtClientHelper.handleGwtRPCFailure(
							t,
							m_messages.rpcFailure_RemoveFavorite(),
							m_id);
					}
					@Override
					public void onSuccess(VibeRpcResponse response)  {}
				});
				break;
				
			case EDIT:
				// Edit the current favorites list!
				EditFavoritesDlg editDlg = new EditFavoritesDlg(
					false,	// false -> Don't auto hide.
					true,	// true  -> Modal .
					getRelativeX(),
					getRelativeY(),
					m_favoritesList);
				editDlg.addStyleName("favoritesDlg");
				editDlg.show();
				break;
			}
		}
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param binderProvider
	 */
	public MyFavoritesMenuPopup(ContextBinderProvider binderProvider) {
		// Initialize the super class.
		super(binderProvider);
	}

	/**
	 * Called when the menu popup closes.
	 * 
	 * Overrides the MenuBarPopupBase.onDetach() method.
	 */
	@Override
	public void onDetach() {
		// Remove the menu's content so that it rereads its data each
		// each time it's shown.
		super.onDetach();
		clearItems();
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
	 * Not used for the Favorites menu.
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
	 * Not used for the Favorites menu.  Always returns true.
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
	 * Implements the MenuBarPopupBase.populateMenu() abstract method.
	 */
	@Override
	public void populateMenu() {
		// If we haven't populated the menu yet...
		if (!(hasContent())) {
			// ...populate it now.
			GetFavoritesCmd cmd = new GetFavoritesCmd();
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetFavorites());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response)  {
					List<FavoriteInfo> fList;
					GetFavoritesRpcResponseData responseData;
					
					responseData = (GetFavoritesRpcResponseData) response.getResponseData();
					fList = responseData.getFavorites();
					
					// Populate the 'My Favorites' popup menu
					// asynchronously so that we can release the AJAX
					// request ASAP.
					populateMyFavoritesMenuAsync(fList);
				}
			});
		}
	}
	
	/*
	 * Asynchronously populates the 'My Favorites' popup menu.
	 */
	private void populateMyFavoritesMenuAsync(final List<FavoriteInfo> fList)  {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateMyFavoritesMenuNow(fList);
			}
		});
	}
	
	/*
	 * Synchronously populates the 'My Favorites' popup menu.
	 */
	private void populateMyFavoritesMenuNow(List<FavoriteInfo> fList) {
		// Scan the favorites...
		boolean currentIsFavorite = false;
		int fCount = 0;
		MenuPopupAnchor fA;
		String currentFavoriteId = null;
		for (Iterator<FavoriteInfo> fIT = fList.iterator(); fIT.hasNext(); ) {
			// ...creating an item structure for each.
			FavoriteInfo favorite = fIT.next();
			String mtId = (IDBASE + favorite.getId());
			
			fA = new MenuPopupAnchor(mtId, favorite.getName(), favorite.getHover(), new FavoriteCommand(favorite));
			addContentMenuItem(fA);
			fCount += 1;
			
			if (m_currentBinder.getBinderId().equals(favorite.getValue())) {
				currentIsFavorite = true;
				currentFavoriteId = favorite.getId();
			}
		}
		
		// If there weren't any favorites...
		if (0 == fCount) {
			// ...put something in the menu that tells the user
			// ...that.
			MenuPopupLabel content = new MenuPopupLabel(m_messages.mainMenuFavoritesNoFavorites());
			addContentMenuItem(content);
		}

		// Do we need to add any favorite commands?
		boolean currentCanBeFavorite = ((null != m_currentBinder) && (!(GwtTeaming.getMainPage().isActivityStreamActive())));
		if (currentCanBeFavorite || (0 < fCount)) {
			// Yes!  Add a spacer between the favorites and
			// the commands... 
			addSpacerMenuItem();
		
			// ...and add the favorite command items.
			MenuPopupAnchor mtA;
			if (currentCanBeFavorite) {
				if (currentIsFavorite)
					 mtA = new MenuPopupAnchor((IDBASE + "Remove"), m_messages.mainMenuFavoritesRemove(), null, new ManageCommand(FavoriteOperation.REMOVE, currentFavoriteId));
				else mtA = new MenuPopupAnchor((IDBASE + "Add"),    m_messages.mainMenuFavoritesAdd(),    null, new ManageCommand(FavoriteOperation.ADD,    m_currentBinder.getBinderId()));
				addContentMenuItem(mtA);
			}
			if (0 < fCount) {
				mtA = new MenuPopupAnchor((IDBASE + "Edit"), m_messages.mainMenuFavoritesEdit(), null, new ManageCommand(FavoriteOperation.EDIT, fList));
				addContentMenuItem(mtA);
			}
		}
	}
}
