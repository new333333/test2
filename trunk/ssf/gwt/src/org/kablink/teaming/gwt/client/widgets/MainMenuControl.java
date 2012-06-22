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
 * [ssf/m_images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.gwt.client.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.event.ActivityStreamEnterEvent;
import org.kablink.teaming.gwt.client.event.AdministrationExitEvent;
import org.kablink.teaming.gwt.client.event.BrowseHierarchyEvent;
import org.kablink.teaming.gwt.client.event.ContextChangedEvent;
import org.kablink.teaming.gwt.client.event.ContextChangingEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.GotoMyWorkspaceEvent;
import org.kablink.teaming.gwt.client.event.InvokeAboutEvent;
import org.kablink.teaming.gwt.client.event.InvokeClipboardEvent;
import org.kablink.teaming.gwt.client.event.InvokeConfigureColumnsEvent;
import org.kablink.teaming.gwt.client.event.InvokeEmailNotificationEvent;
import org.kablink.teaming.gwt.client.event.InvokeImportIcalFileEvent;
import org.kablink.teaming.gwt.client.event.InvokeImportIcalUrlEvent;
import org.kablink.teaming.gwt.client.event.InvokeSendEmailToTeamEvent;
import org.kablink.teaming.gwt.client.event.MastheadHideEvent;
import org.kablink.teaming.gwt.client.event.MastheadShowEvent;
import org.kablink.teaming.gwt.client.event.SidebarHideEvent;
import org.kablink.teaming.gwt.client.event.SidebarShowEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.ViewWhatsNewInBinderEvent;
import org.kablink.teaming.gwt.client.event.ViewWhatsUnseenInBinderEvent;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.mainmenu.ClipboardDlg;
import org.kablink.teaming.gwt.client.mainmenu.ClipboardDlg.ClipboardDlgClient;
import org.kablink.teaming.gwt.client.mainmenu.EmailNotificationDlg;
import org.kablink.teaming.gwt.client.mainmenu.EmailNotificationDlg.EmailNotificationDlgClient;
import org.kablink.teaming.gwt.client.mainmenu.FolderColumnsConfigDlg;
import org.kablink.teaming.gwt.client.mainmenu.FolderColumnsConfigDlg.FolderColumnsConfigDlgClient;
import org.kablink.teaming.gwt.client.mainmenu.ImportIcalByFileDlg;
import org.kablink.teaming.gwt.client.mainmenu.ImportIcalByFileDlg.ImportIcalByFileDlgClient;
import org.kablink.teaming.gwt.client.mainmenu.ImportIcalByUrlDlg;
import org.kablink.teaming.gwt.client.mainmenu.ImportIcalByUrlDlg.ImportIcalByUrlDlgClient;
import org.kablink.teaming.gwt.client.mainmenu.ManageMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.ManageMenuPopup.ManageMenuPopupClient;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarBox;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarButton;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarToggle;
import org.kablink.teaming.gwt.client.mainmenu.MyFavoritesMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.MyTeamsMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.RecentPlacesMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.SearchMenuPanel;
import org.kablink.teaming.gwt.client.mainmenu.SearchOptionsComposite;
import org.kablink.teaming.gwt.client.mainmenu.SearchOptionsComposite.SearchOptionsCompositeClient;
import org.kablink.teaming.gwt.client.mainmenu.SendEmailToContributors;
import org.kablink.teaming.gwt.client.mainmenu.TeamManagementInfo;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuBar;
import org.kablink.teaming.gwt.client.mainmenu.ViewsMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.ViewsMenuPopup.ViewsMenuPopupClient;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetTeamManagementInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetToolbarItemsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetToolbarItemsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.ContextBinderProvider;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnBrowseHierarchyInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.TeamingPopupPanel;

/**
 * This widget will display Vibe's main menu control.
 * 
 * @author drfoster@novell.com
 */
public class MainMenuControl extends Composite
	implements ContextBinderProvider,
	// Event handlers implemented by this class.
		ContextChangedEvent.Handler,
		ContextChangingEvent.Handler,
		InvokeClipboardEvent.Handler,
		InvokeConfigureColumnsEvent.Handler,
		InvokeAboutEvent.Handler,
		InvokeEmailNotificationEvent.Handler,
		InvokeImportIcalFileEvent.Handler,
		InvokeImportIcalUrlEvent.Handler,
		InvokeSendEmailToTeamEvent.Handler,
		MastheadHideEvent.Handler,
		MastheadShowEvent.Handler,
		SidebarHideEvent.Handler,
		SidebarShowEvent.Handler,
		ViewWhatsNewInBinderEvent.Handler,
		ViewWhatsUnseenInBinderEvent.Handler
{
	private BinderInfo						m_contextBinder;
	private ClipboardDlg					m_clipboardDlg;
	private ContextMenuSynchronizer			m_contextMenuSync;
	private ContextLoadInfo					m_lastContextLoaded;
	private EmailNotificationDlg			m_emailNotificationDlg;
	private GwtMainPage						m_mainPage;
	private GwtTeamingMainMenuImageBundle	m_images   = GwtTeaming.getMainMenuImageBundle();
	private GwtTeamingMessages 				m_messages = GwtTeaming.getMessages();
	private ImportIcalByFileDlg				m_iiFileDlg;
	private ImportIcalByUrlDlg				m_iiUrlDlg;
	private MenuBarBox						m_closeAdminBox;
	private MenuBarBox						m_manageBox;
	private MenuBarBox						m_myFavoritesBox;
	private MenuBarBox						m_myTeamsBox;
	private MenuBarBox						m_myWorkspaceBox;
	private MenuBarBox						m_recentPlacesBox;
	private MenuBarBox						m_viewsBox;
	private MenuBarBox						m_whatsNewBox;
	private MenuBarButton					m_bhButton;
	private MenuBarButton					m_soButton;
	private MenuBarToggle					m_wsTreeSlider;
	private MenuBarToggle					m_mastHeadSlider;
	private MyFavoritesMenuPopup			m_myFavoritesMenuPopup;
	private MyTeamsMenuPopup				m_myTeamsMenuPopup;
	private SearchMenuPanel					m_searchPanel;
	private TeamingPopupPanel               m_aboutPopup;
	private TeamingPopupPanel               m_soPopup;
	private VibeMenuBar						m_mainMenu;

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_topRegisteredEvents = new TeamingEvents[] {
		// Context events.
		TeamingEvents.CONTEXT_CHANGED,
		TeamingEvents.CONTEXT_CHANGING,
		
		// Invoke events.
		TeamingEvents.INVOKE_ABOUT,
		TeamingEvents.INVOKE_CLIPBOARD,
		TeamingEvents.INVOKE_CONFIGURE_COLUMNS,
		TeamingEvents.INVOKE_EMAIL_NOTIFICATION,
		TeamingEvents.INVOKE_IMPORT_ICAL_FILE,
		TeamingEvents.INVOKE_IMPORT_ICAL_URL,
		TeamingEvents.INVOKE_SEND_EMAIL_TO_TEAM,
		
		// Masthead events.
		TeamingEvents.MASTHEAD_HIDE,
		TeamingEvents.MASTHEAD_SHOW,
		
		// Sidebar events.
		TeamingEvents.SIDEBAR_HIDE,
		TeamingEvents.SIDEBAR_SHOW,
		
		// View events.
		TeamingEvents.VIEW_WHATS_NEW_IN_BINDER,
		TeamingEvents.VIEW_WHATS_UNSEEN_IN_BINDER,
	};

	/*
	 * Interface used as context items are constructed to indicate
	 * their completion.
	 */
	private interface ContextItemCallback {
		public void complete();
	}
	
	/*
	 * Inner class used to control the construction of multiple context
	 * menu items.
	 */
	private static class ContextMenuSynchronizer {
		private boolean	m_constructionInProgress;	// Set true while we're populating the context menus.
		private int		m_constructedItemCount;		// Count of items constructed (as they're being constructed.)
		private int		m_expectedItemCount;		// Count of items expected to be constructed.  Once reached, construction mode is disabled.
		
		/*
		 * Constructor method.
		 */
		private ContextMenuSynchronizer() {
			// Nothing to do.  Default data member values are
			// sufficient.
		}
		
		/*
		 * Initiates context menu construction tracking.
		 */
		private void activateContextConstruction(boolean isASActive) {
			// Are we already in construction mode?
			if (m_constructionInProgress) {
				// Yes!  Then it shouldn't be getting activated again.
				// Tell the user about the problem.
				GwtClientHelper.debugAlert("MainMenuControl.activateContextConstruction( *Internal Error* ):  Activation call when already active.");
			}
			
			else {
				// No, we aren't in construction mode!  Enter it now.
				m_constructionInProgress = true;
				m_constructedItemCount      = 0;
				
				// In activity stream mode, there's 1 context menu
				// item.  Otherwise, there are 3.
				m_expectedItemCount = (isASActive ? 1 : 3);
			}
		}

		/*
		 * Called when a context menu item has been constructed.  If
		 * the number of context items expected has been reached,
		 * construction mode is turned off.
		 */
		private void contextItemConstructed() {
			// Are we in construction mode?
			if (!m_constructionInProgress) {
				// No!  Then nothing should be getting completed!
				// Tell the user about the problem.
				GwtClientHelper.debugAlert("MainMenuControl.contextItemConstructed( *Internal Error* ):  Item complete call when not active.");
			}

			else {
				// Yes, we're in construction mode!  Bump the
				// constructed item count...
				m_constructedItemCount += 1;
				
				// ...and if we've reached what we're expecting...
				if (m_constructedItemCount == m_expectedItemCount) {
					// ...disable construction mode.
					m_constructionInProgress = false;
				}
			}
		}

		/*
		 * Returns true if we're currently in construction mode and
		 * false otherwise.
		 */
		private boolean isConstructionInProgress() {
			return m_constructionInProgress;
		}
	}
	
	/*
	 * Inner class used to track the information used to load a
	 * context.
	 * 
	 * See the parameters to contextLoaded().
	 */
	private static class ContextLoadInfo {
		private boolean m_inSearch;
		private String  m_binderId;
		private String  m_searchTabId;

		/*
		 * Class constructor.
		 */
		private ContextLoadInfo(String binderId, boolean inSearch, String searchTabId) {
			// Simply store the parameters.
			m_binderId    = binderId;
			m_inSearch    = inSearch;
			m_searchTabId = searchTabId;
		}
	}
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private MainMenuControl(GwtMainPage mainPage, String menuUsage) {
		// Initialize the superclass.
		super();
		
		// Store the parameters.
		m_mainPage = mainPage;
		
		// Construct the object we'll use to control the creation of
		// multiple context menu items.
		m_contextMenuSync = new ContextMenuSynchronizer();
		
		// Register the events to be handled by this class.
		if (menuUsage.equalsIgnoreCase("top")) {
			// Register the events targeted to Vibe's top level menu
			// bar.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				m_topRegisteredEvents,
				this);
		}
		
		// Create the menu's main panel...
		FlowPanel menuPanel = new FlowPanel();
		menuPanel.addStyleName("gwt-MenuBar-horizontal vibe-mainMenuControl");

		// ...create the main menu's MenuBar...
		m_mainMenu = new VibeMenuBar("vibe-mainMenuContent");
		menuPanel.add(m_mainMenu);
		
		// ...add the common items at the left end of the menu...
		addCommonItems();
		
		// ...add the search widgets to the right end of the menu...
		m_searchPanel = new SearchMenuPanel();
		menuPanel.add(m_searchPanel);
		m_soButton = new MenuBarButton(m_images.searchOptions(), m_messages.mainMenuAltSearchOptions(), new Command() {
			@Override
			public void execute() {
				m_soButton.removeStyleName("subhead-control-bg2");
				m_soPopup = new TeamingPopupPanel(true, false);
				GwtClientHelper.rollDownPopup(m_soPopup);
				m_soPopup.addStyleName("searchOptions_Browser roundcornerSM-bottom");
				SearchOptionsComposite.createAsync(
						m_soPopup,
						new SearchOptionsCompositeClient() {					
					@Override
					public void onUnavailable() {
						// Nothing to do.  Error handled in
						// asynchronous provider.
					}
					
					@Override
					public void onSuccess(SearchOptionsComposite soc) {
						// Connect things together...
						soc.addStyleName("searchOptions");
						m_soPopup.setWidget(soc);
						m_soPopup.setGlassEnabled(true);
						m_soPopup.setGlassStyleName("vibe-mainMenuPopup_Glass");
						
						// ...and show the search options popup.  We do
						// ...this as a scheduled command so that the
						// ...asynchronous processing related to the
						// ...creation of the SearchOptionsComposite
						// ...has a chance to complete.
						ScheduledCommand doShow = new ScheduledCommand() {
							@Override
							public void execute() {
								// Position and show the popup as per
								// the position of the search panel on
								// the menu.
								m_soPopup.showRelativeTo(m_soButton);
							}
						};
						Scheduler.get().scheduleDeferred(doShow);
					}
				});
			}});
		m_soButton.addStyleName("vibe-mainMenuButton subhead-control-bg1 roundcornerSM");
		MenuBar soBar = new MenuBar();
		soBar.addStyleName("vibe-mainMenuSearchOptions_Button");
		soBar.addItem(m_soButton);
		menuPanel.add(soBar);
		
		// ...and finally, all composites must call initWidget() in
		// ...their constructors.
		initWidget(menuPanel);
	}

	/*
	 * Adds the "Close Administration" button to the common portion of
	 * the menu bar.
	 */
	private void addCloseAdministrationToCommon(MenuBar menuPanel) {
		m_closeAdminBox = new MenuBarBox("ss_mainMenuCloseAdmin", m_messages.close());
		m_closeAdminBox.setCommand(
			new Command() {
				@Override
				public void execute() {
					AdministrationExitEvent.fireOne();
				}
			});
		menuPanel.addItem(m_closeAdminBox);
		m_closeAdminBox.setVisible(false);
		m_closeAdminBox.setEnabled(false);
	}
	
	/*
	 * Adds the items to the menu bar that are always there, regardless
	 * of context.
	 */
	private void addCommonItems() {
		// ...add the slide-left/right toggle...
		m_wsTreeSlider = new MenuBarToggle(m_images.slideLeft(), m_messages.mainMenuAltLeftNavHideShow(), TeamingEvents.SIDEBAR_HIDE, m_images.slideRight(), m_messages.mainMenuAltLeftNavHideShow(), TeamingEvents.SIDEBAR_SHOW);
		m_wsTreeSlider.addStyleName("vibe-mainMenuButton subhead-control-bg1 roundcornerSM");
		m_mainMenu.addItem(m_wsTreeSlider);

		// ...add the slide-up/down toggle...
		m_mastHeadSlider = new MenuBarToggle(m_images.slideUp(), m_messages.mainMenuAltMastHeadHideShow(), TeamingEvents.MASTHEAD_HIDE, m_images.slideDown(), m_messages.mainMenuAltMastHeadHideShow(), TeamingEvents.MASTHEAD_SHOW);
		m_mastHeadSlider.addStyleName("vibe-mainMenuButton subhead-control-bg1 roundcornerSM");
		m_mainMenu.addItem(m_mastHeadSlider);

		// ...in full Vibe mode...
		if (!(GwtClientHelper.isVibeLite())) {
			// ...add the browse hierarchy button...
			BrowseHierarchyEvent bhe = new BrowseHierarchyEvent();
			m_bhButton = new MenuBarButton(m_images.browseHierarchy(), m_messages.mainMenuAltBrowseHierarchy(), bhe);
			bhe.setOnBrowseHierarchyInfo(new OnBrowseHierarchyInfo(m_bhButton));
			m_bhButton.addStyleName("vibe-mainMenuButton subhead-control-bg1 roundcornerSM");
			m_mainMenu.addItem(m_bhButton);
		}

		// ...and finally, add the common drop down items to the menu bar.
		addMyWorkspaceToCommon(        m_mainMenu);
		addWhatsNewToCommon(           m_mainMenu);
		addMyFavoritesToCommon(        m_mainMenu);
		if (!(GwtClientHelper.isVibeLite())) {
			addMyTeamsToCommon(        m_mainMenu);
		}
		addCloseAdministrationToCommon(m_mainMenu);
	}
	
	/*
	 * Adds the Manage item to the context based portion of the menu
	 * bar.
	 */
	private void addManageToContext(final List<ToolbarItem> toolbarItemList, final TeamManagementInfo tmi, final ContextItemCallback callback) {
		if (null == m_manageBox) {
			String manageNameCalc;
			switch (m_contextBinder.getBinderType()) {
			default:
			case OTHER:                                                          return;
			case FOLDER:     manageNameCalc = m_messages.mainMenuBarFolder();    break;
			case WORKSPACE:  manageNameCalc = m_messages.mainMenuBarWorkspace(); break;
			}
			
			final String manageName = manageNameCalc;
			ManageMenuPopup.createAsync(this, manageName, new ManageMenuPopupClient() {			
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
					callback.complete();
				}
				
				@Override
				public void onSuccess(final ManageMenuPopup mmp) {
					mmp.setCurrentBinder(m_contextBinder);
					mmp.setToolbarItemList(toolbarItemList);
					mmp.setTeamManagementInfo(tmi);
					if (mmp.shouldShowMenu()) {
						m_manageBox = new MenuBarBox("ss_mainMenuManage", manageName, mmp.getMenuBar());
						mmp.setMenuBox(m_manageBox);
						m_mainMenu.addItem(m_manageBox);
					}
					callback.complete();
				}
			});
		}
		
		else {
			GwtClientHelper.debugAlert("MainMenuControl.addManageToContext( *Internal Error* ):  Duplicate Request:  Ignored");
			callback.complete();
		}
	}
	
	/*
	 * Adds the My Favorites item to the common portion of the menu
	 * bar.
	 */
	private void addMyFavoritesToCommon(MenuBar menuPanel) {
		m_myFavoritesMenuPopup = new MyFavoritesMenuPopup(this);
		m_myFavoritesBox = new MenuBarBox("ss_mainMenuMyFavorites", m_messages.mainMenuBarMyFavorites(), m_myFavoritesMenuPopup.getMenuBar());
		m_myFavoritesMenuPopup.setMenuBox(m_myFavoritesBox);
		menuPanel.addItem(m_myFavoritesBox);
	}
	
	/*
	 * Adds the My Teams item to the common portion of the menu bar.
	 */
	private void addMyTeamsToCommon(MenuBar menuPanel) {
		m_myTeamsMenuPopup = new MyTeamsMenuPopup(this);
		m_myTeamsBox = new MenuBarBox("ss_mainMenuMyTeams", m_messages.mainMenuBarMyTeams(), m_myTeamsMenuPopup.getMenuBar());
		m_myTeamsMenuPopup.setMenuBox(m_myTeamsBox);
		menuPanel.addItem(m_myTeamsBox);
	}
	
	/*
	 * Adds the My Workspace item to the common portion of the menu
	 * bar.
	 */
	private void addMyWorkspaceToCommon(MenuBar menuPanel) {
		m_myWorkspaceBox = new MenuBarBox("ss_mainMenuMyWorkspace", m_images.home16(), m_messages.mainMenuBarMyWorkspace());
		m_myWorkspaceBox.setCommand(
			new Command() {
				@Override
				public void execute() {
					GotoMyWorkspaceEvent.fireOne();
				}
			});
		menuPanel.addItem(m_myWorkspaceBox);
	}

	/*
	 * Adds the Recent Places item to the context based portion of the
	 * menu bar.
	 */
	private void addRecentPlacesToContext(List<ToolbarItem> toolbarItemList, ContextItemCallback callback) {
		if (null == m_recentPlacesBox) {
			final RecentPlacesMenuPopup rpmp = new RecentPlacesMenuPopup(this);
			rpmp.setCurrentBinder(m_contextBinder);
			rpmp.setToolbarItemList(toolbarItemList);
			if (rpmp.shouldShowMenu()) {
				m_recentPlacesBox = new MenuBarBox("ss_mainMenuRecentPlaces", m_messages.mainMenuBarRecentPlaces(), rpmp.getMenuBar());
				rpmp.setMenuBox(m_recentPlacesBox);
				m_mainMenu.addItem(m_recentPlacesBox);
			}
			callback.complete();
		}
		
		else {
			GwtClientHelper.debugAlert("MainMenuControl.addRecentPlacesToContext( *Internal Error* ):  Duplicate Request:  Ignored");
			callback.complete();
		}
	}

	/*
	 * Adds the Views item to the context based portion of the menu
	 * bar.
	 */
	private void addViewsToContext(final List<ToolbarItem> toolbarItemList, boolean inSearch, String searchTabId, final ContextItemCallback callback) {
		if (null == m_viewsBox) {
			ViewsMenuPopup.createAsync(this, inSearch, searchTabId, new ViewsMenuPopupClient() {			
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
					callback.complete();
				}
				
				@Override
				public void onSuccess(final ViewsMenuPopup vmp) {
					vmp.setCurrentBinder(m_contextBinder);
					vmp.setToolbarItemList(toolbarItemList);
					if (vmp.shouldShowMenu()) {
						m_viewsBox = new MenuBarBox("ss_mainMenuViews", m_messages.mainMenuBarViews(), vmp.getMenuBar());
						vmp.setMenuBox(m_viewsBox);
						m_mainMenu.addItem(m_viewsBox);
					}
					callback.complete();
				}
			});
		}
		
		else {
			GwtClientHelper.debugAlert("MainMenuControl.addViewsToContext( *Internal Error* ):  Duplicate Request:  Ignored");
			callback.complete();
		}
	}
	
	/*
	 * Adds the What's New item to the common portion of the menu bar.
	 */
	private void addWhatsNewToCommon(MenuBar menuPanel) {
		m_whatsNewBox = new MenuBarBox("ss_mainMenuWhatsNew", m_images.newMenu(), m_messages.mainMenuBarWhatsNew());
		m_whatsNewBox.setCommand(
			new Command() {
				@Override
				public void execute() {
					doWhatsNewAsync(ActivityStreamDataType.OTHER);
				}
			});
		menuPanel.addItem(m_whatsNewBox);
	}

	/*
	 * Called to remove the context based menu items (Workspace,
	 * Folder, ...) from the menu bar.
	 * 
	 * This is typically done immediately before a known context switch
	 * so that invalid menu items (i.e., those based on a previous
	 * context) are not available until the new context fully loads.
	 */
	private void clearContextMenus() {
		// If we have a manage box...
		if (null != m_manageBox) {
			// ...remove it...
			m_mainMenu.removeItem(m_manageBox);
			m_manageBox = null;
		}

		// ...if we have recent places box...
		if (null != m_recentPlacesBox) {
			// ...remove it...
			m_mainMenu.removeItem(m_recentPlacesBox);
			m_recentPlacesBox = null;
		}

		// ...and if we have a views box...
		if (null != m_viewsBox) {
			// ...remove it.
			m_mainMenu.removeItem(m_viewsBox);
			m_viewsBox = null;
		}
	}
	
	/**
	 * Called when a new context has been loaded into the content panel
	 * to refresh the menu contents.
	 * 
	 * Note:  If searchTabId as a non-null, non-empty value, it will be
	 *    used as the ID of the current tab (i.e., search results) and
	 *    implies that that the current search can be saved.
	 *    
	 *    If it's null or an empty value, it implies that there is no
	 *    current search that can be saved and no saving capabilities
	 *    will be exposed.
	 *    
	 * @param binderId
	 * @param inSearch
	 * @param searchTabId
	 */
	public void contextLoaded(final String binderId, final boolean inSearch, final String searchTabId) {
		// Keep track of the context that we're loading.
		setContext(binderId, inSearch, searchTabId);
		
		// Rebuild the context based panel based on the new context.
		GetBinderInfoCmd cmd = new GetBinderInfoCmd( binderId );
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				m_contextBinder = null;
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetBinderInfo(),
					binderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {				
				// Show the context asynchronously so that we can
				// release the AJAX request ASAP.
				BinderInfo binderInfo = ((BinderInfo) response.getResponseData());
				showContextAsync(binderInfo, binderId, inSearch, searchTabId);
			}
		});
	}

	/*
	 * Asynchronously enters activity stream mode on the current
	 * binder.
	 */
	private void doWhatsNewAsync(final ActivityStreamDataType ss) {
		ScheduledCommand doShow = new ScheduledCommand() {
			@Override
			public void execute() {
				doWhatsNewNow(ss);
			}
		};
		Scheduler.get().scheduleDeferred(doShow);
	}
	
	/*
	 * Synchronously enters activity stream mode on the current binder.
	 */
	private void doWhatsNewNow(ActivityStreamDataType ss) {
		// Are we connected to a binder?
		if (null != m_contextBinder) {
			// Yes!  Use it as the current binder for the
			// activity stream.
			ActivityStreamInfo asi = new ActivityStreamInfo();
			asi.setActivityStream(ActivityStream.CURRENT_BINDER);
			asi.setBinderId(m_contextBinder.getBinderId());
			asi.setTitle(   m_contextBinder.getBinderTitle());
			GwtTeaming.fireEvent(new ActivityStreamEnterEvent(asi, ss));
		}
		
		else {
			// No, we're not connected to a binder!  Just
			// use the UI supplied default activity stream.
			GwtTeaming.fireEvent(new ActivityStreamEnterEvent(ss));
		}
	}
	
	/**
	 * Returns the menu's current binder context.
	 * 
	 * Implements the ContextBinderProvider.getContextBinder() method.
	 */
	@Override
	public BinderInfo getContextBinder() {
		return m_contextBinder;
	}
	
	/**
	 * Show all the menus and controls on this menu control and hide
	 * the Close administration menu item..  This is used when we close
	 * the Site Administration" page.
	 */
	public void hideAdministrationMenubar() {
		// Do we think we're in administration mode?
		if (m_closeAdminBox.isVisible()) {
			// Yes!  Show the widget that holds the expand/contract
			// left navigation, expand/contract header, ... widgets.
			m_bhButton.setVisible(true);
			
			// Show My Workspace, My Teams, My Favorites and What's
			// New.
			m_myWorkspaceBox.setVisible(true);
			if (null != m_myTeamsBox)     m_myTeamsBox.setVisible(    true);
			if (null != m_myFavoritesBox) m_myFavoritesBox.setVisible(true);
			if (null != m_whatsNewBox)    m_whatsNewBox.setVisible(   true);
			
			// Show the panel that holds the menu items.
			setContextMenusVibibile(true);
			
			// Show the search panel.
			m_searchPanel.setVisible(true);
			m_soButton.setVisible(   true);
			
			// Hide the Close administration menu item.
			m_closeAdminBox.setVisible(false);
		}
	}

	/**
	 * Handles ContextChangedEvent's received by this class.
	 * 
	 * Implements the ContextChangedEvent.Handler.onContextChanged() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContextChanged(final ContextChangedEvent event)
	{
		// Is the event data is valid?
		OnSelectBinderInfo osbInfo = event.getOnSelectBinderInfo();
		if (GwtClientHelper.validateOSBI(osbInfo, false)) {
			// Yes!  Put it into effect.
			Instigator instigator = osbInfo.getInstigator();
			if (Instigator.CONTENT_AREA_CHANGED == instigator) {
				contextLoaded(
					osbInfo.getBinderId().toString(),
					m_mainPage.isInSearch(),
					m_mainPage.getSearchTabId());
			}
		}
	}
	
	/**
	 * Handles ContextChangingEvent's received by this class.
	 * 
	 * Implements the ContextChangingEvent.Handler.onContextChanging() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContextChanging(final ContextChangingEvent event) {
		clearContextMenus();
	}
	
	/**
	 * Handles InvokeAboutEvent's received by this class.
	 * 
	 * Implements the InvokeAboutEvent.Handler.onInvokeAbout() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeAbout(InvokeAboutEvent event) {
		ScheduledCommand doShow = new ScheduledCommand() {
			@Override
			public void execute() {
				onInvokeAboutNow();
			}
		};
		Scheduler.get().scheduleDeferred(doShow);
	}

	/*
	 * Synchronously invokes the about dialog.
	 */
	private void onInvokeAboutNow() {
		// If we haven't created the about popup yet...
		if (null == m_aboutPopup) {
			// ...create it now...
			m_aboutPopup = new TeamingPopupPanel(true, true);
			m_aboutPopup.removeStyleName("gwt-PopupPanel");
			m_aboutPopup.addStyleName("vibe-aboutBox");
			Image aboutImg = new Image();
			RequestInfo ri = GwtClientHelper.getRequestInfo();
			String imageFile = (ri.isNovellTeaming() ? "teaming_about_screen.png" : "kablink_about_screen.png");
			aboutImg.setUrl(ri.getImagesPath() + "pics/masthead/" + imageFile);
			m_aboutPopup.setWidget(aboutImg);
			m_aboutPopup.setGlassEnabled(true);
			m_aboutPopup.setGlassStyleName("teamingDlgBox_Glass");
			m_aboutPopup.addAttachHandler(new Handler() {
				@Override
				public void onAttachOrDetach(AttachEvent event) {
					// Is this an attach event? 
					if (event.isAttached()) {
						// Yes!  At this point, the about has been
						// rendered and we need to center it in the
						// window.
						m_aboutPopup.center();
					}
				}
			});
		}
		
		// ...and show it.
		m_aboutPopup.show();
	}
	
	/**
	 * Handles InvokeClipboardEvent's received by this class.
	 * 
	 * Implements the InvokeClipboardEvent.Handler.onInvokeClipboard() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeClipboard(InvokeClipboardEvent event) {
		// Asynchronously invoke the clipboard dialog.
		ScheduledCommand doLoadCBDlg = new ScheduledCommand() {
			@Override
			public void execute() {
				onInvokeClipboardNow();
			}
		};
		Scheduler.get().scheduleDeferred(doLoadCBDlg);
	}

	/*
	 * Synchronously invokes the clipboard dialog.
	 */
	private void onInvokeClipboardNow() {
		// Have we instantiated a clipboard dialog yet?
		if (null == m_clipboardDlg) {
			// No!  Instantiate one now.
			ClipboardDlg.createAsync(new ClipboardDlgClient() {			
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(final ClipboardDlg cpDlg) {
					// ...and show it.
					m_clipboardDlg = cpDlg;
					ScheduledCommand doShow = new ScheduledCommand() {
						@Override
						public void execute() {
							showClipboardDlgNow();
						}
					};
					Scheduler.get().scheduleDeferred(doShow);
				}
			});
		}
		
		else {
			// Yes, we've instantiated a clipboard dialog already!
			// Simply show it.
			showClipboardDlgNow();
		}
	}
	
	/**
	 * Handles InvokeConfigureColumnsEvent's received by this class.
	 * 
	 * Implements the InvokeConfigureColumnsEvent.Handler.onInvokeConfigureColumns() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeConfigureColumns(InvokeConfigureColumnsEvent event) {
		// Create an instance of the dialog...
		FolderColumnsConfigDlg.createAsync(new FolderColumnsConfigDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(final FolderColumnsConfigDlg fcDlg) {
				ScheduledCommand doShow = new ScheduledCommand() {
					@Override
					public void execute() {
						// ..and initialize and show it.
						FolderColumnsConfigDlg.initAndShow(fcDlg, m_contextBinder);
					}
				};
				Scheduler.get().scheduleDeferred(doShow);
			}
		});
	}
	
	/**
	 * Handles InvokeEmailNotificationEvent's received by this class.
	 * 
	 * Implements the InvokeEmailNotificationEvent.Handler.onInvokeEmailNotification() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeEmailNotification(InvokeEmailNotificationEvent event) {
		// Asynchronously invoke the email notification dialog.
		ScheduledCommand doLoadENDlg = new ScheduledCommand() {
			@Override
			public void execute() {
				onInvokeEmailNotificationNow();
			}
		};
		Scheduler.get().scheduleDeferred(doLoadENDlg);
	}

	/*
	 * Synchronously invokes the email notification dialog.
	 */
	private void onInvokeEmailNotificationNow() {
		// Have we instantiated an email notification dialog yet?
		if (null == m_emailNotificationDlg) {
			// No!  Instantiate one now.
			EmailNotificationDlg.createAsync(new EmailNotificationDlgClient() {			
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(final EmailNotificationDlg enDlg) {
					// ...and show it.
					m_emailNotificationDlg = enDlg;
					ScheduledCommand doShow = new ScheduledCommand() {
						@Override
						public void execute() {
							showEmailNotificationDlgNow();
						}
					};
					Scheduler.get().scheduleDeferred(doShow);
				}
			});
		}
		
		else {
			// Yes, we've instantiated an email notification dialog
			// already!  Simply show it.
			showEmailNotificationDlgNow();
		}
	}
	
	
	/**
	 * Handles InvokeImportIcalFileEvent's received by this class.
	 * 
	 * Implements the InvokeImportIcalFileEvent.Handler.onInvokeImportIcalFile() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeImportIcalFile(InvokeImportIcalFileEvent event) {
		// Have we instantiated an import iCal by file dialog yet?
		if (null == m_iiFileDlg) {
			// No!  Instantiate one now.
			ImportIcalByFileDlg.createAsync(new ImportIcalByFileDlgClient() {			
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(final ImportIcalByFileDlg iiFileDlg) {
					// ...and show it.
					m_iiFileDlg = iiFileDlg;
					ScheduledCommand doShow = new ScheduledCommand() {
						@Override
						public void execute() {
							showImportIcalByFileDlgNow();
						}
					};
					Scheduler.get().scheduleDeferred(doShow);
				}
			});
		}
		
		else {
			// Yes, we've instantiated an import iCal by file dialog
			// already!  Simply show it.
			showImportIcalByFileDlgNow();
		}
	}
	
	/**
	 * Handles InvokeImportIcalUrlEvent's received by this class.
	 * 
	 * Implements the InvokeImportIcalUrlEvent.Handler.onInvokeImportIcalUrl() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeImportIcalUrl(InvokeImportIcalUrlEvent event) {
		// Have we instantiated an import iCal by URL dialog yet?
		if (null == m_iiUrlDlg) {
			// No!  Instantiate one now.
			ImportIcalByUrlDlg.createAsync(new ImportIcalByUrlDlgClient() {			
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(final ImportIcalByUrlDlg iiUrlDlg) {
					// ...and show it.
					m_iiUrlDlg = iiUrlDlg;
					ScheduledCommand doShow = new ScheduledCommand() {
						@Override
						public void execute() {
							showImportIcalByUrlDlgNow();
						}
					};
					Scheduler.get().scheduleDeferred(doShow);
				}
			});
		}
		
		else {
			// Yes, we've instantiated an import iCal by URL dialog
			// already!  Simply show it.
			showImportIcalByUrlDlgNow();
		}
	}
	
	/**
	 * Handles InvokeSendEmailToTeamEvent's received by this class.
	 * 
	 * Implements the InvokeSendEmailToTeamEvent.Handler.onInvokeSendEmailToTeam() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeSendEmailToTeam(InvokeSendEmailToTeamEvent event) {
		// Instantiate a SendEmailToContributors object and use that to
		// request the contributors and launch the send email window.
		SendEmailToContributors smtc = new SendEmailToContributors(event.getBaseSendUrl());
		smtc.doSend(m_contextBinder.getBinderIdAsLong());
	}
	
	/**
	 * Handles MastheadHideEvent's received by this class.
	 * 
	 * Implements the MastheadHideEvent.Handler.onMastheadHide() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMastheadHide(MastheadHideEvent event) {
		setMastheadSliderMenuItemState(TeamingEvents.MASTHEAD_SHOW);
	}
	
	/**
	 * Handles MastheadShowEvent's received by this class.
	 * 
	 * Implements the MastheadShowEvent.Handler.onMastheadShow() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMastheadShow(MastheadShowEvent event) {
		setMastheadSliderMenuItemState(TeamingEvents.MASTHEAD_HIDE);
	}
	
	/**
	 * Handles SidebarHideEvent's received by this class.
	 * 
	 * Implements the SidebarHideEvent.Handler.onSidebarHide() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSidebarHide(SidebarHideEvent event) {
		setWorkspaceTreeSliderMenuItemState(TeamingEvents.SIDEBAR_SHOW);
	}
	
	/**
	 * Handles SidebarShowEvent's received by this class.
	 * 
	 * Implements the SidebarShowEvent.Handler.onSidebarShow() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSidebarShow(SidebarShowEvent event) {
		setWorkspaceTreeSliderMenuItemState(TeamingEvents.SIDEBAR_HIDE);
	}
	
	/**
	 * Handles ViewWhatsNewInBinderEvent's received by this class.
	 * 
	 * Implements the ViewWhatsNewInBinderEvent.Handler.onViewWhatsNewInBinder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewWhatsNewInBinder(ViewWhatsNewInBinderEvent event) {
		doWhatsNewAsync(ActivityStreamDataType.ALL);
	}
	
	/**
	 * Handles ViewWhatsUnseenInBinderEvent's received by this class.
	 * 
	 * Implements the ViewWhatsUnseenInBinderEvent.Handler.onViewWhatsUnseenInBinder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewWhatsUnseenInBinder(ViewWhatsUnseenInBinderEvent event) {
		doWhatsNewAsync(ActivityStreamDataType.UNREAD);
	}
	
	/**
	 * Resets the current menu context to the one that was last
	 * loaded.
	 */
	public void resetContext() {
		// If we're tracking a previously loaded context...
		if (null != m_lastContextLoaded) {
			// ...re-load it.
			contextLoaded(
				m_lastContextLoaded.m_binderId,
				m_lastContextLoaded.m_inSearch,
				m_lastContextLoaded.m_searchTabId);
		}
	}

	/**
	 * Sets the parameters as the most recently loaded context.
	 * 
	 * @param binderId
	 * @param inSearch
	 * @param searchTabId
	 */
	public void setContext(String binderId, boolean inSearch, String searchTabId) {
		m_lastContextLoaded = new ContextLoadInfo(binderId, inSearch, searchTabId);
	}

	/*
	 * Sets the visibility state of the various context menus.
	 */
	private void setContextMenusVibibile(boolean visible) {
		// If we have a manage box...
		if (null != m_manageBox) {
			// ...hide/show it...
			m_manageBox.setVisible(visible);
		}

		// ...if we have recent places box...
		if (null != m_recentPlacesBox) {
			// ...hide/show it...
			m_recentPlacesBox.setVisible(visible);
		}

		// ...and if we have a views box...
		if (null != m_viewsBox) {
			// ...hide/show it...
			m_viewsBox.setVisible(visible);
		}
	}
	
	/**
	 * Set the state of the "show/hide masthead" menu item.
	 * 
	 * @param event
	 */
	public void setMastheadSliderMenuItemState(TeamingEvents event) {
		m_mastHeadSlider.setState(event);
	}
	
	
	/**
	 * Set the state of the "show/hide workspace tree" menu item.
	 * 
	 * @param event
	 */
	public void setWorkspaceTreeSliderMenuItemState(TeamingEvents event) {
		m_wsTreeSlider.setState(event);
	}
	
	/**
	 * Hide all the menus and controls on this menu control and shows
	 * the Close administration menu item.  This is used when we invoke
	 * the Site Administration page.
	 */
	public void showAdministrationMenubar() {
		// Do we think we're in administration mode?
		if (!(m_closeAdminBox.isVisible())) {
			// No!  Hide the browse button
			m_bhButton.setVisible(false);
			
			// Hide My Workspace, My Teams, My Favorites and What's New.
			m_myWorkspaceBox.setVisible(false);
			if (null != m_myTeamsBox)     m_myTeamsBox.setVisible(    false);
			if (null != m_myFavoritesBox) m_myFavoritesBox.setVisible(false);
			if (null != m_whatsNewBox)    m_whatsNewBox.setVisible(   false);
			
			// Hide the panel that holds the menu items.
			setContextMenusVibibile(false);
			
			// Hide the search panel.
			m_searchPanel.setVisible(false);
			m_soButton.setVisible(   false);
			
			// Show the Close administration menu item.
			m_closeAdminBox.setVisible(true);
		}
	}

	/*
	 * Synchronously shows the clipboard dialog.
	 */
	private void showClipboardDlgNow() {
		ClipboardDlg.initAndShow(m_clipboardDlg, m_contextBinder);
	}
	
	/*
	 * Asynchronously shows the context that was loaded.
	 */
	private void showContextAsync(final BinderInfo binderInfo, final String binderId, final boolean inSearch, final String searchTabId) {
		ScheduledCommand doShow = new ScheduledCommand() {
			@Override
			public void execute() {
				showContextNow(binderInfo, binderId, inSearch, searchTabId);
			}
		};
		Scheduler.get().scheduleDeferred(doShow);
	}
	
	/*
	 * Synchronously shows the context that was loaded.
	 */
	private void showContextNow(final BinderInfo binderInfo, final String binderId, final boolean inSearch, final String searchTabId) {
		m_contextBinder = binderInfo;
		GetToolbarItemsCmd cmd = new GetToolbarItemsCmd( binderId );
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetToolbarItems(),
					binderId);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				GetToolbarItemsRpcResponseData responseData = ((GetToolbarItemsRpcResponseData) response.getResponseData());
				final List<ToolbarItem> toolbarItemList = ((null == responseData) ? null : responseData.getToolbarItems());

				// Run the 'Get Team Management' RPC request as a
				// scheduled command so the RPC request that got us
				// here can be terminated.
				ScheduledCommand doGet = new ScheduledCommand() {
					@Override
					public void execute() {
						GetTeamManagementInfoCmd cmd = new GetTeamManagementInfoCmd(binderId);
						GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
							@Override
							public void onFailure(Throwable t) {
								GwtClientHelper.handleGwtRPCFailure(
									t,
									m_messages.rpcFailure_GetTeamManagement(),
									binderId);
							}
							
							@Override
							public void onSuccess(VibeRpcResponse response) {
								// Show the toolbar items asynchronously so
								// that we can release the AJAX request ASAP.
								TeamManagementInfo tmi = ((TeamManagementInfo) response.getResponseData());
								showToolbarItemsAsync(
									inSearch,
									searchTabId,
									toolbarItemList,
									tmi);
							}
						});
					}
				};
				Scheduler.get().scheduleDeferred(doGet);
			}
		});
	}

	/*
	 * Synchronously shows the email notification dialog.
	 */
	private void showEmailNotificationDlgNow() {
		EmailNotificationDlg.initAndShow(m_emailNotificationDlg, m_contextBinder);
	}
	
	/*
	 * Synchronously shows the import iCal by file dialog.
	 */
	private void showImportIcalByFileDlgNow() {
		ImportIcalByFileDlg.initAndShow(m_iiFileDlg, m_contextBinder);
	}
	
	/*
	 * Synchronously shows the import iCal by URL dialog.
	 */
	private void showImportIcalByUrlDlgNow() {
		ImportIcalByUrlDlg.initAndShow(m_iiUrlDlg, m_contextBinder);
	}
	
	/*
	 * Asynchronously shows the toolbar items.
	 */
	private void showToolbarItemsAsync(final boolean inSearch, final String searchTabId, final List<ToolbarItem> toolbarItemList, final TeamManagementInfo tmi) {
		ScheduledCommand doShow = new ScheduledCommand() {
			@Override
			public void execute() {
				showToolbarItemsNow(inSearch, searchTabId, toolbarItemList, tmi);
			}
		};
		Scheduler.get().scheduleDeferred(doShow);
	}
	
	/*
	 * Synchronously shows the toolbar items.
	 */
	private void showToolbarItemsNow(boolean inSearch, String searchTabId, List<ToolbarItem> toolbarItemList, final TeamManagementInfo tmi) {
		// Do we currently have context items being constructed?
		if (m_contextMenuSync.isConstructionInProgress()) {
			// Yes!  That's an error.  Tell the user.
//!			GwtClientHelper.debugAlert("MainMenuControl.showToolbarItemsNow( *Internal Error* ):  Request to show items while they're already in the process of being shown.");
		}
		
		else {
			// No, we don't currently have context items being
			// constructed!  Initiate the construction processing now.
			boolean isASActive = m_mainPage.isActivityStreamActive();
			m_contextMenuSync.activateContextConstruction(isASActive);
			
			// Clear any context menus currently displayed...
			clearContextMenus();
			
			// ...and handle the variations based on activity stream mode.
			addRecentPlacesToContext(toolbarItemList, new ContextItemCallback() {
				@Override
				public void complete() {
					m_contextMenuSync.contextItemConstructed();
				}
			});
			if ((!isASActive) && (!(GwtClientHelper.isVibeLite()))) {
				addManageToContext(toolbarItemList, tmi,new ContextItemCallback() {
					@Override
					public void complete() {
						m_contextMenuSync.contextItemConstructed();
					}
				});
			}
			
			if (!isASActive) {
				addViewsToContext( toolbarItemList, inSearch, searchTabId, new ContextItemCallback() {
					@Override
					public void complete() {
						m_contextMenuSync.contextItemConstructed();
					}
				});
			}
		}
	}

	/**
	 * Callback interface to interact with the menu asynchronously
	 * after it loads. 
	 */
	public interface MainMenuControlClient {
		void onSuccess(MainMenuControl mainMenuCtrl);
		void onUnavailable();
	}

	/**
	 * Loads the MainMenuControl split point and returns an instance of
	 * it via the callback.
	 * 
	 * @param mainPage
	 * @param menuUsage
	 * @param menuClient
	 */
	public static void createAsync(final GwtMainPage mainPage, final String menuUsage, final MainMenuControlClient menuClient) {
		GWT.runAsync(MainMenuControl.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess() {
				MainMenuControl mainMenuCtrl = new MainMenuControl(mainPage, menuUsage);
				menuClient.onSuccess(mainMenuCtrl);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_MainMenuControl() );
				menuClient.onUnavailable();
			}
		});
	}
}
