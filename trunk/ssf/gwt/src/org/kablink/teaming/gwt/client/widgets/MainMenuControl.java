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

import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
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
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.mainmenu.ManageMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.FolderColumnsConfigDlg.FolderColumnsConfigDlgClient;
import org.kablink.teaming.gwt.client.mainmenu.ManageMenuPopup.ManageMenuPopupClient;
import org.kablink.teaming.gwt.client.mainmenu.FolderColumnsConfigDlg;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarBox;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarButton;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarPopupBase;
import org.kablink.teaming.gwt.client.mainmenu.MenuBarToggle;
import org.kablink.teaming.gwt.client.mainmenu.MyFavoritesMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.MyTeamsMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.RecentPlacesMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.SearchMenuPanel;
import org.kablink.teaming.gwt.client.mainmenu.SearchOptionsComposite;
import org.kablink.teaming.gwt.client.mainmenu.SearchOptionsComposite.SearchOptionsCompositeClient;
import org.kablink.teaming.gwt.client.mainmenu.TeamManagementInfo;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuBar;
import org.kablink.teaming.gwt.client.mainmenu.ViewsMenuPopup;
import org.kablink.teaming.gwt.client.mainmenu.ViewsMenuPopup.ViewsMenuPopupClient;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderColumnsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderColumnsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetTeamManagementInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetToolbarItemsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetToolbarItemsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SaveFolderColumnsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnBrowseHierarchyInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;


/**
 * This widget will display Vibe's main menu control.
 * 
 * @author drfoster@novell.com
 */
public class MainMenuControl extends Composite
	implements
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
	private ContextLoadInfo					m_lastContextLoaded;
	private EditSuccessfulHandler           m_editFolderColumnsSuccessHandler;
	private FolderColumnsConfigDlg          m_folderColumnsDlg;
	private GwtMainPage						m_mainPage;
	private GwtTeamingMainMenuImageBundle	m_images   = GwtTeaming.getMainMenuImageBundle();
	private GwtTeamingMessages 				m_messages = GwtTeaming.getMessages();
	private Integer 						m_folderColumnsDlgX;
	private Integer 						m_folderColumnsDlgY;
	private MenuBarBox						m_closeAdminBox;
	private MenuBarBox						m_myFavoritesBox;
	private MenuBarBox						m_myTeamsBox;
	private MenuBarBox						m_myWorkspaceBox;
	private MenuBarBox						m_whatsNewBox;
	private MenuBarButton					m_bhButton;
	private MenuBarButton					m_soButton;
	private MenuBarPopupBase				m_openPopupMenu;
	private MenuBarToggle					m_wsTreeSlider;
	private MenuBarToggle					m_mastHeadSlider;
	private MyFavoritesMenuPopup			m_myFavoritesMenuPopup;
	private MyTeamsMenuPopup				m_myTeamsMenuPopup;
	private SearchMenuPanel					m_searchPanel;
	@SuppressWarnings("unused")
	private String							m_menuUsage;	// Which context this menu is being used in.
	private TeamingPopupPanel               m_soPopup;
	private VibeMenuBar						m_buttonsMenu;
	private VibeMenuBar						m_commonMenu;
	private VibeMenuBar						m_contextMenu;

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
		// Store the parameter.
		m_mainPage  = mainPage;
		m_menuUsage = menuUsage;
		
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

		// ...add the common items at the left end of the menu...
		addCommonItems(menuPanel);
		
		// ...add a FlowPanel for the context dependent items.  (Note
		// ...that these items will be added when the content panel
		// ...loads via calls to MainMenuControl.contextLoaded().)...
		m_contextMenu = new VibeMenuBar("vibe-mainMenuContent");
		menuPanel.add(m_contextMenu);
		
		// ...add the search widgets to the right end of the menu...
		m_searchPanel = new SearchMenuPanel();
		menuPanel.add(m_searchPanel);
		final MainMenuControl mainMenu = this;
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
						ScheduledCommand showSOPopup = new ScheduledCommand() {
							@Override
							public void execute() {
								// Position and show the popup as per
								// the position of the search panel on
								// the menu.
								m_soPopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
									@Override
									public void setPosition(int offsetWidth, int offsetHeight) {
										int soPopupLeft = ((m_soButton.getAbsoluteLeft() + m_soButton.getOffsetWidth()) - offsetWidth);
										int soPopupTop  = mainMenu.getParent().getElement().getAbsoluteBottom();
										m_soPopup.setPopupPosition(soPopupLeft, soPopupTop);
									}
								});
							}
						};
						Scheduler.get().scheduleDeferred(showSOPopup);
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
		m_closeAdminBox = new MenuBarBox("ss_mainMenuCloseAdmin", m_messages.close(), false);
		m_closeAdminBox.setCommand(
			new Command() {
				@Override
				public void execute() {
					AdministrationExitEvent.fireOne();
				}
			});
		menuPanel.addItem(m_closeAdminBox);
		m_closeAdminBox.setVisible(false);
	}
	
	/*
	 * Adds the items to the menu bar that are always there, regardless
	 * of context.
	 */
	private void addCommonItems(FlowPanel menuPanel) {
		// Create a panel to hold the buttons at the left edge of the
		// menu bar...
		m_buttonsMenu = new VibeMenuBar("vibe-mainMenuButton_Group");
		
		// ...add the slide-left/right toggle...
		m_wsTreeSlider = new MenuBarToggle(m_images.slideLeft(), m_messages.mainMenuAltLeftNavHideShow(), TeamingEvents.SIDEBAR_HIDE, m_images.slideRight(), m_messages.mainMenuAltLeftNavHideShow(), TeamingEvents.SIDEBAR_SHOW);
		m_wsTreeSlider.addStyleName("vibe-mainMenuButton subhead-control-bg1 roundcornerSM");
		m_buttonsMenu.addItem(m_wsTreeSlider);

		// ...add the slide-up/down toggle...
		m_mastHeadSlider = new MenuBarToggle(m_images.slideUp(), m_messages.mainMenuAltMastHeadHideShow(), TeamingEvents.MASTHEAD_HIDE, m_images.slideDown(), m_messages.mainMenuAltMastHeadHideShow(), TeamingEvents.MASTHEAD_SHOW);
		m_mastHeadSlider.addStyleName("vibe-mainMenuButton subhead-control-bg1 roundcornerSM");
		m_buttonsMenu.addItem(m_mastHeadSlider);

		// ...add the browse hierarchy button...
		BrowseHierarchyEvent bhe = new BrowseHierarchyEvent();
		m_bhButton = new MenuBarButton(m_images.browseHierarchy(), m_messages.mainMenuAltBrowseHierarchy(), bhe);
		bhe.setOnBrowseHierarchyInfo(new OnBrowseHierarchyInfo(m_bhButton));
		m_bhButton.addStyleName("vibe-mainMenuButton subhead-control-bg1 roundcornerSM");
		m_buttonsMenu.addItem(m_bhButton);

		// ...add the buttons to the menu...
		menuPanel.add(m_buttonsMenu);

		// ...and finally, add the common drop down items to the menu bar.
		m_commonMenu = new VibeMenuBar("vibe-mainMenuButton_Group");
		menuPanel.add(m_commonMenu);
		addMyWorkspaceToCommon(        m_commonMenu);
		addWhatsNewToCommon(           m_commonMenu);
		addMyFavoritesToCommon(        m_commonMenu);
		addMyTeamsToCommon(            m_commonMenu);
		addCloseAdministrationToCommon(m_commonMenu);
	}
	
	/*
	 * Adds the Manage item to the context based portion of the menu
	 * bar.
	 */
	private void addManageToContext(final List<ToolbarItem> toolbarItemList, final TeamManagementInfo tmi) {
		String manageNameCalc;
		switch (m_contextBinder.getBinderType()) {
		default:
		case OTHER:                                                          return;
		case FOLDER:     manageNameCalc = m_messages.mainMenuBarFolder();    break;
		case WORKSPACE:  manageNameCalc = m_messages.mainMenuBarWorkspace(); break;
		}
		
		final String manageName = manageNameCalc;
		ManageMenuPopup.createAsync(manageName, new ManageMenuPopupClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(final ManageMenuPopup mmp) {
				mmp.setCurrentBinder(m_contextBinder);
				mmp.setToolbarItemList(toolbarItemList);
				mmp.setTeamManagementInfo(tmi);
				if (mmp.shouldShowMenu()) {
					final MenuBarBox manageBox = new MenuBarBox("ss_mainMenuManage", manageName, true);
					manageBox.setCommand(
						new Command() {
							@Override
							public void execute() {
								if (mmp.isShowing())
								     hidePopupMenu(mmp);
								else showPopupMenu(mmp, manageBox);
							}
						});
					m_contextMenu.addItem(manageBox);
				}
			}
		});
	}
	
	/*
	 * Adds the My Favorites item to the common portion of the menu
	 * bar.
	 */
	private void addMyFavoritesToCommon(MenuBar menuPanel) {
		m_myFavoritesBox = new MenuBarBox("ss_mainMenuMyFavorites", m_messages.mainMenuBarMyFavorites(), true);
		m_myFavoritesBox.setCommand(
			new Command() {
				@Override
				public void execute() {
					if (null == m_myFavoritesMenuPopup) {
						m_myFavoritesMenuPopup = new MyFavoritesMenuPopup();
						m_myFavoritesMenuPopup.setCurrentBinder(m_contextBinder);
						showPopupMenu(m_myFavoritesMenuPopup, m_myFavoritesBox);
						m_myFavoritesMenuPopup.addCloseHandler(new CloseHandler<PopupPanel>(){
							@Override
							public void onClose(CloseEvent<PopupPanel> event) {
								m_myFavoritesMenuPopup = null;
							}});
					}
					else {
					     hidePopupMenu(m_myFavoritesMenuPopup);
					}
				}
			});
		menuPanel.addItem(m_myFavoritesBox);
	}
	
	/*
	 * Adds the My Teams item to the common portion of the menu bar.
	 */
	private void addMyTeamsToCommon(MenuBar menuPanel) {
		m_myTeamsBox = new MenuBarBox("ss_mainMenuMyTeams", m_messages.mainMenuBarMyTeams(), true);
		m_myTeamsBox.setCommand(
			new Command() {
				@Override
				public void execute() {
					if (null == m_myTeamsMenuPopup) {
						m_myTeamsMenuPopup = new MyTeamsMenuPopup();
						m_myTeamsMenuPopup.setCurrentBinder(m_contextBinder);
						showPopupMenu(m_myTeamsMenuPopup, m_myTeamsBox);
						m_myTeamsMenuPopup.addCloseHandler(new CloseHandler<PopupPanel>(){
							@Override
							public void onClose(CloseEvent<PopupPanel> event) {
								m_myTeamsMenuPopup = null;
							}});
					}
					else {
					     hidePopupMenu(m_myTeamsMenuPopup);
					}
				}
			});
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
	private void addRecentPlacesToContext(List<ToolbarItem> toolbarItemList) {
		final RecentPlacesMenuPopup rpmp = new RecentPlacesMenuPopup();
		rpmp.setCurrentBinder(m_contextBinder);
		rpmp.setToolbarItemList(toolbarItemList);
		if (rpmp.shouldShowMenu()) {
			final MenuBarBox rpBox = new MenuBarBox("ss_mainMenuRecentPlaces", m_messages.mainMenuBarRecentPlaces(), true);
			rpBox.setCommand(
				new Command() {
					@Override
					public void execute() {
						if (rpmp.isShowing())
						     hidePopupMenu(rpmp);
						else showPopupMenu(rpmp, rpBox);
					}
				});
			m_contextMenu.addItem(rpBox);
		}
	}

	/*
	 * Adds the Views item to the context based portion of the menu
	 * bar.
	 */
	private void addViewsToContext(final List<ToolbarItem> toolbarItemList, boolean inSearch, String searchTabId) {
		ViewsMenuPopup.createAsync(inSearch, searchTabId, new ViewsMenuPopupClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(final ViewsMenuPopup vmp) {
				vmp.setCurrentBinder(m_contextBinder);
				vmp.setToolbarItemList(toolbarItemList);
				if (vmp.shouldShowMenu()) {
					final MenuBarBox actionsBox = new MenuBarBox("ss_mainMenuViews", m_messages.mainMenuBarViews(), true);
					actionsBox.setCommand(
						new Command() {
							@Override
							public void execute() {
								if (vmp.isShowing())
								     hidePopupMenu(vmp);
								else showPopupMenu(vmp, actionsBox);
							}
						});
					m_contextMenu.addItem(actionsBox);
				}
			}
		});
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
					// Are we connected to a binder?
					if (null != m_contextBinder) {
						// Yes!  Use it as the current binder for the
						// activity stream.
						ActivityStreamInfo asi = new ActivityStreamInfo();
						asi.setActivityStream(ActivityStream.CURRENT_BINDER);
						asi.setBinderId(m_contextBinder.getBinderId());
						asi.setTitle(   m_contextBinder.getBinderTitle());
						GwtTeaming.fireEvent(new ActivityStreamEnterEvent(asi));
					}
					
					else {
						// No, we're not connected to a binder!  Just
						// use the UI supplied default activity stream.
						GwtTeaming.fireEvent(new ActivityStreamEnterEvent());
					}
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
		if (null != m_contextMenu) {
			m_contextMenu.clearItems();
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
			public void onFailure(Throwable t) {
				m_contextBinder = null;
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetBinderInfo(),
					binderId);
			}
			public void onSuccess(VibeRpcResponse response) {				
				// Show the context asynchronously so that we can
				// release the AJAX request ASAP.
				BinderInfo binderInfo = ((BinderInfo) response.getResponseData());
				showContextAsync(binderInfo, binderId, inSearch, searchTabId);
			}
		});
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
			m_contextMenu.setVisible(true);
			
			// Show the search panel.
			m_searchPanel.setVisible(true);
			m_soButton.setVisible(   true);
			
			// Hide the Close administration menu item.
			m_closeAdminBox.setVisible(false);
		}
	}

	/*
	 * Called to hide a popup menu.
	 */
	private void hidePopupMenu(MenuBarPopupBase popup) {
		if (null != popup) {
			popup.hideMenu();
			if (popup.equals(m_openPopupMenu)) {
				m_openPopupMenu = null;
			}
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
//!		...this needs to be implemented...
		Window.alert("MainMenuControl.onInvokeAbout():  ...this needs to be implemented...");
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
//!		...this needs to be implemented...
		Window.alert("MainMenuControl.onInvokeClipboard():  ...this needs to be implemented...");
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
		AsyncCallback<VibeRpcResponse> rpcReadCallback;
		
		// Create a callback that will be called when we get the folder columns.
		rpcReadCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetFolderColumns() );
			}// end onFailure()
	
			/**
			 * We successfully retrieved the folder's columns.  Now invoke the "edit folder columns" dialog.
			 */
			public void onSuccess( VibeRpcResponse response )
			{
				List<FolderColumn> folderColumns;
				List<FolderColumn> folderColumnsAll;
				folderColumns = ((FolderColumnsRpcResponseData)response.getResponseData()).getFolderColumns();
				folderColumnsAll = ((FolderColumnsRpcResponseData)response.getResponseData()).getFolderColumnsAll();
				
				// Create a handler that will be called when the user presses the ok button in the dialog.
				if ( m_editFolderColumnsSuccessHandler == null )
				{
					m_editFolderColumnsSuccessHandler = new EditSuccessfulHandler()
					{
						private AsyncCallback<VibeRpcResponse> rpcSaveCallback = null;
						private List<FolderColumn> newFolderColumns = null;
						
						/**
						 * This method gets called when user user presses ok in the "Folder Columns Configuration" dialog.
						 */
						@SuppressWarnings("unchecked")
						public boolean editSuccessful( Object obj )
						{
							newFolderColumns = (List<FolderColumn>) obj;
							
							// Create the callback that will be used when we issue an ajax request to save the folder column settings.
							if ( rpcSaveCallback == null )
							{
								rpcSaveCallback = new AsyncCallback<VibeRpcResponse>()
								{
									/**
									 * 
									 */
									public void onFailure( Throwable t )
									{
										GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_SaveFolderColumns() );
									}// end onFailure()
							
									/**
									 * 
									 * @param result
									 */
									public void onSuccess( VibeRpcResponse response )
									{
										@SuppressWarnings("unused")
										Boolean result;
										
										result = ((BooleanRpcResponseData) response.getResponseData()).getBooleanValue();
										
										// The folder columns affect how things are displayed in the content frame.
										// So we need to reload the page in the content frame.
										//reloadContentPanel();
										
									}// end onSuccess()
								};
							}
					
							// Issue an ajax request to save the folder columns.
							{
								SaveFolderColumnsCmd cmd;
								
								// Issue an ajax request to save the folder columns to the db.  rpcSaveCallback will
								// be called when we get the response back.
								cmd = new SaveFolderColumnsCmd( m_contextBinder.getBinderId(), newFolderColumns );
								GwtClientHelper.executeCommand( cmd, rpcSaveCallback );
							}
							
							return true;
						}// end editSuccessful()
					};
				}
				
				// Get a new "Folder Columns Config" dialog?
				m_folderColumnsDlgX = -1;
				m_folderColumnsDlgY = -1;
				if ( m_folderColumnsDlgX == -1 ) {
					m_folderColumnsDlgX = m_contextMenu.getAbsoluteLeft();
					if ( m_folderColumnsDlgX < 75 )
						m_folderColumnsDlgX = 75;
				}
				
				if ( m_folderColumnsDlgY == -1 ) {
					m_folderColumnsDlgY = m_contextMenu.getAbsoluteTop();
					if ( m_folderColumnsDlgY < 75 )
						m_folderColumnsDlgY = 75;
				}
				FolderColumnsConfigDlg.createAsync(
						 true, 
						 true, 
						 m_folderColumnsDlgX, 
						 m_folderColumnsDlgY, 
						 m_contextBinder.getBinderId(), 
						 folderColumns,
						 folderColumnsAll,
						 new FolderColumnsConfigDlgClient() {				
					public void onUnavailable()
					{
						// Nothing to do.  Error handled in
						// asynchronous provider.
					}// end onUnavailable()
					
					public void onSuccess( FolderColumnsConfigDlg fcDlg )
					{
						m_folderColumnsDlg = fcDlg;
						m_folderColumnsDlg.setPopupPosition( m_folderColumnsDlgX, m_folderColumnsDlgY );
						m_folderColumnsDlg.show(true);  //Show this centered
					}// end onSuccess()
				} );
			} // end onSuccess()
		};
		// Issue an ajax request to get the folder columns.  This invokes the "folder columns config" dialog.
		{
			GetFolderColumnsCmd cmd;
			
			// Issue an ajax request to get the personal preferences from the db.
			cmd = new GetFolderColumnsCmd(Long.valueOf(m_contextBinder.getBinderId()), m_contextBinder.getFolderType(), Boolean.TRUE);
			GwtClientHelper.executeCommand( cmd, rpcReadCallback );
		}
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
//!		...this needs to be implemented...
		Window.alert("MainMenuControl.onInvokeEmailNotification():  ...this needs to be implemented...");
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
//!		...this needs to be implemented...
		Window.alert("MainMenuControl.onInvokeImportIcalFile( " + event.getImportType() + " ):  ...this needs to be implemented...");
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
//!		...this needs to be implemented...
		Window.alert("MainMenuControl.onInvokeImportIcalUrl( " + event.getImportType() + " ):  ...this needs to be implemented...");
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
//!		...this needs to be implemented...
		Window.alert("MainMenuControl.onInvokeSendEmailToTeam():  ...this needs to be implemented...");
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
//!		...this needs to be implemented...
		Window.alert("MainMenuControl.onViewWhatsNewInBinder():  ...this needs to be implemented...");
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
//!		...this needs to be implemented...
		Window.alert("MainMenuControl.onViewWhatsUnseenInBinder():  ...this needs to be implemented...");
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
			m_contextMenu.setVisible(false);
			
			// Hide the search panel.
			m_searchPanel.setVisible(false);
			m_soButton.setVisible(   false);
			
			// Show the Close administration menu item.
			m_closeAdminBox.setVisible(true);
		}
	}

	/*
	 * Asynchronously shows the context that was loaded.
	 */
	private void showContextAsync(final BinderInfo binderInfo, final String binderId, final boolean inSearch, final String searchTabId) {
		ScheduledCommand showContext = new ScheduledCommand() {
			@Override
			public void execute() {
				showContextNow(binderInfo, binderId, inSearch, searchTabId);
			}
		};
		Scheduler.get().scheduleDeferred(showContext);
	}
	
	/*
	 * Synchronously shows the context that was loaded.
	 */
	private void showContextNow(final BinderInfo binderInfo, final String binderId, final boolean inSearch, final String searchTabId) {
		m_contextBinder = binderInfo;
		GetToolbarItemsCmd cmd = new GetToolbarItemsCmd( binderId );
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetToolbarItems(),
					binderId);
			}
			public void onSuccess(VibeRpcResponse response) {
				GetToolbarItemsRpcResponseData responseData = ((GetToolbarItemsRpcResponseData) response.getResponseData());
				final List<ToolbarItem> toolbarItemList = ((null == responseData) ? null : responseData.getToolbarItems());

				// Run the 'Get Team Management' RPC request as a
				// scheduled command so the RPC request that got us
				// here can be terminated.
				ScheduledCommand getTMInfo = new ScheduledCommand() {
					@Override
					public void execute() {
						GetTeamManagementInfoCmd cmd = new GetTeamManagementInfoCmd(binderId);
						GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
							public void onFailure(Throwable t) {
								GwtClientHelper.handleGwtRPCFailure(
									t,
									m_messages.rpcFailure_GetTeamManagement(),
									binderId);
							}
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
				Scheduler.get().scheduleDeferred(getTMInfo);
			}
		});
	}

	/*
	 * Called to show a popup menu opened from a menu bar box.
	 */
	private void showPopupMenu(MenuBarPopupBase popup, MenuBarBox box) {
		if (null != m_openPopupMenu) {
			m_openPopupMenu.hideMenu();
			m_openPopupMenu = null;
		}
		popup.showMenu(box);
		m_openPopupMenu = popup;
	}
	
	/*
	 * Asynchronously shows the toolbar items.
	 */
	private void showToolbarItemsAsync(final boolean inSearch, final String searchTabId, final List<ToolbarItem> toolbarItemList, final TeamManagementInfo tmi) {
		ScheduledCommand showTBIs = new ScheduledCommand() {
			@Override
			public void execute() {
				showToolbarItemsNow(inSearch, searchTabId, toolbarItemList, tmi);
			}
		};
		Scheduler.get().scheduleDeferred(showTBIs);
	}
	
	/*
	 * Synchronously shows the toolbar items.
	 */
	private void showToolbarItemsNow(boolean inSearch, String searchTabId, List<ToolbarItem> toolbarItemList, final TeamManagementInfo tmi) {
		// Clear any context menus currently displayed...
		clearContextMenus();
		
		// ...and handle variations based on activity stream mode.
		addRecentPlacesToContext(toolbarItemList);
		if (!(m_mainPage.isActivityStreamActive())) {
			addManageToContext(toolbarItemList, tmi                  );
			addViewsToContext( toolbarItemList, inSearch, searchTabId);
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
