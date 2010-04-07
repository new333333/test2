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

import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;


/**
 * Class used for the Favorites menu item popup.  
 * 
 * @author drfoster@novell.com
 *
 */
public class FavoritesMenuPopup extends MenuBarPopup {
	private final String IDBASE = "favorites_";
	
	private int m_menuLeft;	// Left coordinate of where the menu is to be placed.
	private int m_menuTop;	// Top  coordinate of where the menu is to be placed.

	/*
	 * Defines the management operations supported on the favorites.
	 */
	private enum FavoriteOperation {
		ADD,
		EDIT,
		REMOVE
	}
	
	/*
	 * Inner class that handles clicks on an individual favorite.
	 */
	private class FavoriteClickHandler implements ClickHandler {
		private FavoriteInfo m_favorite;	// The favorite clicked on.

		/**
		 * Class constructor.
		 * 
		 * @param favorite
		 */
		FavoriteClickHandler(FavoriteInfo favorite) {
			// Simply store the parameter.
			m_favorite = favorite;
		}

		/**
		 * Called when the user clicks on a favorite.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			// Hide the menu...
			hide();

			// ...and trigger a selection changed event.
			m_rpcService.getBinderPermalink(m_favorite.getValue(), new AsyncCallback<String>()
			{
				public void onFailure(Throwable t) {Window.alert(t.toString());}
				public void onSuccess(String binderPermalink) {
					m_actionTrigger.triggerAction(
						TeamingAction.SELECTION_CHANGED,
						new OnSelectBinderInfo(
							m_favorite.getValue(),
							binderPermalink,
							false,
							Instigator.OTHER));
				}
			});
		}
	}
	
	/*
	 * Inner class that handles clicks on favorites commands.
	 */
	private class ManageClickHandler implements ClickHandler {
		private FavoriteOperation m_operation;		// The which favorite management operation to perform.
		private List<FavoriteInfo> m_favoritesList;	// ADD/REMOVE:  Not used.  EDIT:  The user's current favorites list.
		private String m_id;						// ADD:  ID of binder to add.  REMOVE:  ID of favorite to remove.  EDIT:  Not used.
		
		/**
		 * Class constructors.
		 * 
		 * @param operation
		 * @param id
		 * @param fList
		 */
		ManageClickHandler(FavoriteOperation operation, String id) {
			m_operation = operation;
			m_id = id;
		}
		
		ManageClickHandler(FavoriteOperation operation, List<FavoriteInfo> fList) {
			m_operation = operation;
			m_favoritesList = fList;
		}
		
		/**
		 * Called when the user clicks on a favorites management
		 * command.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			// Hide the menu.
			hide();
			
			// What operation are we performing?
			switch (m_operation) {
			case ADD:
				// Adding the current binder to the favorites!
				m_rpcService.addFavorite(m_id, new AsyncCallback<Boolean>() {
					public void onFailure(Throwable t) {
						Window.alert(t.toString());
					}
					public void onSuccess(Boolean result)  {}
				});
				break;
				
			case REMOVE:
				// Removing the current binder from the favorites!
				m_rpcService.removeFavorite(m_id, new AsyncCallback<Boolean>() {
					public void onFailure(Throwable t) {
						Window.alert(t.toString());
					}
					public void onSuccess(Boolean result)  {}
				});
				break;
				
			case EDIT:
				// Edit the current favorites list!
				EditFavoritesDlg editDlg = new EditFavoritesDlg(true, true, m_menuLeft, m_menuTop, m_favoritesList);
				editDlg.addStyleName("favoritesDlg");
				editDlg.show();
				break;
			}
		}
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param left
	 * @param top
	 */
	public FavoritesMenuPopup(ActionTrigger actionTrigger, int left, int top) {
		// Initialize the super class...
		super(actionTrigger, GwtTeaming.getMessages().mainMenuBarFavorites(), left, top);
		
		// ...and initialize everything else.
		m_menuLeft = left;
		m_menuTop  = top;
	}
	
	/**
	 * Completes construction of the menu and shows it.
	 */
	public void showFavorites(final String binderId) {
		m_rpcService.getFavorites(new AsyncCallback<List<FavoriteInfo>>() {
			public void onFailure(Throwable t) {
				Window.alert(t.toString());
			}
			public void onSuccess(final List<FavoriteInfo> fList)  {
				// Scan the favorites...
				boolean currentIsFavorite = false;
				int fCount = 0;
				MenuPopupAnchor fA;
				String currentFavoriteId = null;
				for (Iterator<FavoriteInfo> fIT = fList.iterator(); fIT.hasNext(); ) {
					// ...creating an item structure for each.
					FavoriteInfo favorite = fIT.next();
					String mtId = (IDBASE + favorite.getId());
					
					fA = new MenuPopupAnchor(mtId, favorite.getName(), favorite.getHover(), new FavoriteClickHandler(favorite));
					addContentWidget(fA);
					fCount += 1;
					
					if (binderId.equals(favorite.getValue())) {
						currentIsFavorite = true;
						currentFavoriteId = favorite.getId();
					}
				}
				
				// If there weren't any favorites...
				if (0 == fCount) {
					// ...put something in the menu that tells the user
					// ...that.
					MenuPopupLabel content = new MenuPopupLabel(m_messages.mainMenuFavoritesNoFavorites());
					addContentWidget(content);
				}

				// Add a spacer between the favorites and favorite
				// commands.
				FlowPanel spacerPanel = new FlowPanel();
				spacerPanel.addStyleName("mainMenuPopup_ItemSpacer");
				addContentWidget(spacerPanel);
				
				// Add the favorite command items.
				MenuPopupAnchor mtA;
				if (currentIsFavorite)
					 mtA = new MenuPopupAnchor((IDBASE + "Remove"), m_messages.mainMenuFavoritesRemove(), null, new ManageClickHandler(FavoriteOperation.REMOVE, currentFavoriteId));
				else mtA = new MenuPopupAnchor((IDBASE + "Add"),    m_messages.mainMenuFavoritesAdd(),    null, new ManageClickHandler(FavoriteOperation.ADD,    binderId));
				addContentWidget(mtA);
				if (0 < fCount) {
					mtA = new MenuPopupAnchor((IDBASE + "Edit"), m_messages.mainMenuFavoritesEdit(), null, new ManageClickHandler(FavoriteOperation.EDIT, fList));
					addContentWidget(mtA);
				}
						
				// Finally, show the menu popup.
				show();
			}
		});
	}
}
