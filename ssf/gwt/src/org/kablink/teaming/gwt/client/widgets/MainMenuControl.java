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
 * [ssf/m_images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.mainmenu.ActionsMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.ManageMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarBox;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarButton;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarToggle;
import org.kablink.teaming.gwt.client.mainmenu.MyFavoritesMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.MyTeamsMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.RecentPlacesMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.SearchMenuPanel;
import org.kablink.teaming.gwt.client.mainmenu.TopRankedDlg;
import org.kablink.teaming.gwt.client.mainmenu.TeamManagementInfo;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.OnBrowseHierarchyInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.TopRankedInfo;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;


/**
 * This widget will display Teaming's main menu control.
 * 
 * @author drfoster@novell.com
 */
public class MainMenuControl extends Composite implements ActionRequestor, ActionTrigger {
	private BinderInfo m_contextBinder;
	private FlowPanel m_buttonsPanel;
	private FlowPanel m_contextPanel;
	private MenuBarBox m_myWorkspaceBox;
	private MenuBarBox m_myTeamsBox;
	private MenuBarBox m_myFavoritesBox;
	private MenuBarBox m_closeAdminBox;
	private GwtTeamingMainMenuImageBundle m_images = GwtTeaming.getMainMenuImageBundle();
	private GwtTeamingMessages m_messages = GwtTeaming.getMessages();
	private List<ActionHandler> m_actionHandlers = new ArrayList<ActionHandler>();
	private SearchMenuPanel m_searchPanel;
	
	/**
	 * Constructor method.
	 */
	public MainMenuControl() {
		// Create the menu's main panel...
		FlowPanel menuPanel = new FlowPanel();
		menuPanel.addStyleName("mainMenuControl");

		// ...add the common items at the left end of the menu...
		addCommonItems(menuPanel);
		
		// ...add a FlowPanel for the context dependent items.  (Note
		// ...that these items will be added when the content panel
		// ...loads via calls to MainMenuControl.contextLoaded().)...
		m_contextPanel = new FlowPanel();
		m_contextPanel.addStyleName("mainMenuContent");
		menuPanel.add(m_contextPanel);
		
		// ...add the search widgets to the right end of the menu...
		m_searchPanel = new SearchMenuPanel(this);
		menuPanel.add(m_searchPanel);
		
		// ...and finally, all composites must call initWidget() in
		// ...their constructors.
		initWidget(menuPanel);
	}

	/**
	 * Called to add an ActionHandler to this MainMenuControl.
	 * 
	 * Implements the ActionRequestor.addActionHandler() interface method.
	 * 
	 * @param actionHandler
	 */
	public void addActionHandler(ActionHandler actionHandler) {
		m_actionHandlers.add( actionHandler );
	}

	/*
	 * Adds the Actions item to the context based portion of the menu
	 * bar.
	 */
	private void addActionsToContext(List<ToolbarItem> toolbarItemList) {
		final ActionsMenuPopup amp = new ActionsMenuPopup(this);
		amp.setCurrentBinder(m_contextBinder);
		amp.setToolbarItemList(toolbarItemList);
		if (amp.shouldShowMenu()) {
			final MenuBarBox actionsBox = new MenuBarBox("ss_mainMenuActions", m_messages.mainMenuBarActions(), true);
			actionsBox.addClickHandler(
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						int left = actionsBox.getAbsoluteLeft();
						amp.showPopup(left, getPopupMenuTop(actionsBox));
					}
				});
			m_contextPanel.add(actionsBox);
		}
	}
	
	/*
	 * Adds the "Close Administration" button to the common portion of the menu bar.
	 * bar.
	 */
	private void addCloseAdministrationToCommon( FlowPanel menuPanel )
	{
		m_closeAdminBox = new MenuBarBox( "ss_mainMenuCloseAdmin", m_messages.close(), false );
		m_closeAdminBox.addClickHandler(
			new ClickHandler()
			{
				public void onClick( ClickEvent event )
				{
					triggerAction( TeamingAction.CLOSE_ADMINISTRATION );
				}
			});
		menuPanel.add( m_closeAdminBox );
		m_closeAdminBox.setVisible( false );
	}// end addCloseAdministrationToCommon()
	

	/*
	 * Adds the items to the menu bar that are always there, regardless
	 * of context.
	 */
	private void addCommonItems(FlowPanel menuPanel) {
		// Create a panel to hold the buttons at the left edge of the
		// menu bar...
		m_buttonsPanel = new FlowPanel();
		m_buttonsPanel.addStyleName("mainMenuButton_Group");
		
		// ...add the slide-left/right toggle...
		MenuBarToggle wsTreeSlider = new MenuBarToggle(this, m_images.slideLeft(), m_messages.mainMenuAltLeftNavHideShow(), TeamingAction.HIDE_LEFT_NAVIGATION, m_images.slideRight(), m_messages.mainMenuAltLeftNavHideShow(), TeamingAction.SHOW_LEFT_NAVIGATION);
		wsTreeSlider.addStyleName("mainMenuButton subhead-control-bg1 roundcornerSM");
		m_buttonsPanel.add(wsTreeSlider);

		// ...add the slide-up/down toggle...
		MenuBarToggle mastHeadSlider = new MenuBarToggle(this, m_images.slideUp(), m_messages.mainMenuAltMastHeadHideShow(), TeamingAction.HIDE_MASTHEAD, m_images.slideDown(), m_messages.mainMenuAltMastHeadHideShow(), TeamingAction.SHOW_MASTHEAD);
		mastHeadSlider.addStyleName("mainMenuButton subhead-control-bg1 roundcornerSM");
		m_buttonsPanel.add(mastHeadSlider);

		// ...add the browse hierarchy button...
		MenuBarButton bhButton = new MenuBarButton(this, m_images.browseHierarchy(), m_messages.mainMenuAltBrowseHierarchy(), TeamingAction.BROWSE_HIERARCHY);
		bhButton.setActionObject(new OnBrowseHierarchyInfo(bhButton));
		bhButton.addStyleName("mainMenuButton subhead-control-bg1 roundcornerSM");
		m_buttonsPanel.add(bhButton);

		// ...if the user is allowed to exit GWT UI mode...
		final ActionTrigger actionTrigger = this;
		GwtTeaming.getRpcService().getGwtUIExclusive(new AsyncCallback<Boolean>() {
			public void onFailure(Throwable t) {
				Window.alert(t.toString());
			}
			public void onSuccess(Boolean isGwtUIExclusive) {
				if (!isGwtUIExclusive) {
					// ...add the GWT UI button...
					MenuBarButton gwtUIButton = new MenuBarButton(actionTrigger, m_images.gwtUI(), m_messages.mainMenuAltGwtUI(), TeamingAction.TOGGLE_GWT_UI);
					gwtUIButton.addStyleName("mainMenuButton subhead-control-bg1 roundcornerSM");
					m_buttonsPanel.add(gwtUIButton);
				}
			}
		});

		// ...add the buttons to the menu...
		menuPanel.add(m_buttonsPanel);

		// ...and finally, add the common drop down items to the menu bar.
		addMyWorkspaceToCommon(menuPanel);
		addMyTeamsToCommon(    menuPanel);
		addMyFavoritesToCommon(menuPanel);
		addCloseAdministrationToCommon( menuPanel );
	}
	
	/*
	 * Adds the Manage item to the context based portion of the menu
	 * bar.
	 */
	private void addManageToContext(final List<ToolbarItem> toolbarItemList, final TeamManagementInfo tmi) {
		String manageName;
		switch (m_contextBinder.getBinderType()) {
		default:
		case OTHER:                                                      return;
		case FOLDER:     manageName = m_messages.mainMenuBarFolder();    break;
		case WORKSPACE:  manageName = m_messages.mainMenuBarWorkspace(); break;
		}
		
		final ManageMenuPopup mmp = new ManageMenuPopup(this, manageName);
		mmp.setCurrentBinder(m_contextBinder);
		mmp.setToolbarItemList(toolbarItemList);
		mmp.setTeamManagementInfo(tmi);
		if (mmp.shouldShowMenu()) {
			final MenuBarBox manageBox = new MenuBarBox("ss_mainMenuManage", manageName, true);
			manageBox.addClickHandler(
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						int left = manageBox.getAbsoluteLeft();
						mmp.showPopup(left, getPopupMenuTop(manageBox));
					}
				});
			m_contextPanel.add(manageBox);
		}
	}
	
	/*
	 * Adds the My Favorites item to the common portion of the menu
	 * bar.
	 */
	private void addMyFavoritesToCommon(FlowPanel menuPanel) {
		m_myFavoritesBox = new MenuBarBox("ss_mainMenuMyFavorites", m_messages.mainMenuBarMyFavorites(), true);
		final ActionTrigger actionTrigger = this;
		m_myFavoritesBox.addClickHandler(
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					int left = m_myFavoritesBox.getAbsoluteLeft();
					MyFavoritesMenuPopup mfmp = new MyFavoritesMenuPopup(actionTrigger);
					mfmp.setCurrentBinder(m_contextBinder);
					mfmp.showPopup(left, getPopupMenuTop(m_myFavoritesBox));
				}
			});
		menuPanel.add(m_myFavoritesBox);
	}
	
	/*
	 * Adds the My Teams item to the common portion of the menu bar.
	 */
	private void addMyTeamsToCommon(FlowPanel menuPanel) {
		m_myTeamsBox = new MenuBarBox("ss_mainMenuMyTeams", m_messages.mainMenuBarMyTeams(), true);
		final ActionTrigger actionTrigger = this;
		m_myTeamsBox.addClickHandler(
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					int left = m_myTeamsBox.getAbsoluteLeft();
					MyTeamsMenuPopup mtmp = new MyTeamsMenuPopup(actionTrigger);
					mtmp.setCurrentBinder(m_contextBinder);
					mtmp.showPopup(left, getPopupMenuTop(m_myTeamsBox));
				}
			});
		menuPanel.add(m_myTeamsBox);
	}
	
	/*
	 * Adds the My Workspace item to the common portion of the menu
	 * bar.
	 */
	private void addMyWorkspaceToCommon(FlowPanel menuPanel) {
		m_myWorkspaceBox = new MenuBarBox("ss_mainMenuMyWorkspace", m_images.home16(), m_messages.mainMenuBarMyWorkspace());
		m_myWorkspaceBox.addClickHandler(
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					triggerAction(TeamingAction.MY_WORKSPACE);
				}
			});
		menuPanel.add(m_myWorkspaceBox);
	}

	/*
	 * Adds the Recent Places item to the context based portion of the
	 * menu bar.
	 */
	private void addRecentPlacesToContext(List<ToolbarItem> toolbarItemList) {
		final RecentPlacesMenuPopup rpmp = new RecentPlacesMenuPopup(this);
		rpmp.setCurrentBinder(m_contextBinder);
		rpmp.setToolbarItemList(toolbarItemList);
		if (rpmp.shouldShowMenu()) {
			final MenuBarBox rpBox = new MenuBarBox("ss_mainMenuRecentPlaces", m_messages.mainMenuBarRecentPlaces(), true);
			rpBox.addClickHandler(
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						int left = rpBox.getAbsoluteLeft();
						rpmp.showPopup(left, getPopupMenuTop(rpBox));
					}
				});
			m_contextPanel.add(rpBox);
		}
	}
	
	/*
	 * Adds the Top Ranked item to the context based portion of the
	 * menu bar.
	 */
	private void addTopRankedToContext() {
		final ActionTrigger actionTrigger = this;
		final MenuBarBox topRankedBox = new MenuBarBox("ss_mainMenuTopRanged", m_messages.mainMenuBarTopRanked());
		topRankedBox.addClickHandler(
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					// Remove the selection from the menu item...
					Element menuItemElement = Document.get().getElementById("ss_mainMenuTopRanged");
					menuItemElement.removeClassName("mainMenuPopup_BoxHover");
					
					// ...and run the top ranked dialog.
					GwtTeaming.getRpcService().getTopRanked(new AsyncCallback<List<TopRankedInfo>>() {
						public void onFailure(Throwable t) {
							Window.alert(t.toString());
						}
						public void onSuccess(List<TopRankedInfo> triList) {
							TopRankedDlg topRankedDlg = new TopRankedDlg(
								false,	// false -> Don't auto hide.
								true,	// true  -> Modal.
								actionTrigger,
								topRankedBox.getAbsoluteLeft(),
								getPopupMenuTop(topRankedBox),
								triList);
							topRankedDlg.addStyleName("topRankedDlg");
							topRankedDlg.show();
						}
					});
				}
			});
		m_contextPanel.add(topRankedBox);
	}
	
	/**
	 * Called when a new context has been loaded into the content panel
	 * to refresh the menu contents.
	 * 
	 * @param binderId
	 */
	public void contextLoaded(final String binderId, final boolean inSearch) {
		// Rebuild the context based panel based on the new context.
		m_contextPanel.clear();
		GwtTeaming.getRpcService().getBinderInfo(binderId, new AsyncCallback<BinderInfo>() {
			public void onFailure(Throwable t) {
				m_contextBinder = null;
				Window.alert(t.toString());
			}
			public void onSuccess(BinderInfo binderInfo) {
				m_contextBinder = binderInfo;
				GwtTeaming.getRpcService().getToolbarItems(binderId, new AsyncCallback<List<ToolbarItem>>() {
					public void onFailure(Throwable t) {
						Window.alert(t.toString());
					}
					public void onSuccess(final List<ToolbarItem> toolbarItemList)  {
						GwtTeaming.getRpcService().getTeamManagementInfo(binderId, new AsyncCallback<TeamManagementInfo>() {
							public void onFailure(Throwable t) {
								Window.alert(t.toString());
							}
							public void onSuccess(final TeamManagementInfo tmi)  {
								// Handle inSearch vs. not inSearch.
//!								...this needs to be implemented...
								addManageToContext(      toolbarItemList, tmi);
								addRecentPlacesToContext(toolbarItemList);
								addActionsToContext(     toolbarItemList);
								if (inSearch) {
									addTopRankedToContext();
								}
							}
						});
					}
				});
			}
		});
	}

	/*
	 * Returns where to position the top of popup menus.
	 * 
	 * Note:  This is a hack to get around a problem I don't
	 * understand.  With Firefox, the MenuBarBox's offset height is
	 * always 0.  With IE, Chrome, Safari, ... it works.  This hack
	 * basically makes the menu popups come up in the same position,
	 * regardless of browser. 
	 */
	private int getPopupMenuTop(MenuBarBox mbb) {
		int mbbHeight = mbb.getOffsetHeight();
		int top;
		if (0 == mbbHeight)
			 top = (mbb.getAbsoluteTop() - 20);
		else top = (mbb.getElement().getAbsoluteBottom() - mbbHeight);
		return top;
	}
	
	/**
	 * Show all the menus and controls on this menu control and hide
	 * the Close administration menu item..  This is used when we close
	 * the Site Administration" page.
	 */
	public void hideAdministrationMenubar() {
		// Show the widget that holds the expand/contract left
		// navigation, expand/contract header, ... widgets.
		m_buttonsPanel.setVisible(true);
		
		// Show My Workspace, My Teams and My Favorites.
		m_myWorkspaceBox.setVisible(true);
		m_myTeamsBox.setVisible(true);
		m_myFavoritesBox.setVisible(true);
		
		// Show the panel that holds the menu items.
		m_contextPanel.setVisible(true);
		
		// Show the search panel.
		m_searchPanel.setVisible(true);
		
		// Hide the Close administration menu item.
		m_closeAdminBox.setVisible(false);
	}
	
	/**
	 * Hide all the menus and controls on this menu control and shows
	 * the Close administration menu item.  This is used when we invoke
	 * the Site Administration page.
	 */
	public void showAdministrationMenubar() {
		// Hide the widget that holds the expand/contract left
		// navigation, expand/contract header, ... widgets
		m_buttonsPanel.setVisible(false);
		
		// Hide My Workspace, My Teams and My Favorites.
		m_myWorkspaceBox.setVisible(false);
		m_myTeamsBox.setVisible(false);
		m_myFavoritesBox.setVisible(false);
		
		// Hide the panel that holds the menu items.
		m_contextPanel.setVisible(false);
		
		// Hide the search panel.
		m_searchPanel.setVisible(false);
		
		// Show the Close administration menu item.
		m_closeAdminBox.setVisible(true);
	}
	
	/**
	 * Fires a TeamingAction at the registered ActionHandler's.
	 * 
	 * Implements the ActionTrigger.triggerAction() method. 
	 *
	 * @param action
	 * @param obj
	 */
	public void triggerAction(TeamingAction action, Object obj) {
		// Scan the ActionHandler's that have been registered...
		for (Iterator<ActionHandler> ahIT = m_actionHandlers.iterator(); ahIT.hasNext(); ) {
			// ...firing the action at each.
			ahIT.next().handleAction(action, obj);
		}
	}
	
	public void triggerAction(TeamingAction action) {
		// Always use the initial form of the method.
		triggerAction(action, null);
	}
}
