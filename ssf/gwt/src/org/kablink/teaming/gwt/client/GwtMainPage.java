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

import java.util.ArrayList;
import java.util.Date;

import org.kablink.teaming.gwt.client.UIStateManager.UIState;
import org.kablink.teaming.gwt.client.admin.GwtAdminAction;
import org.kablink.teaming.gwt.client.binderviews.ProfileEntryDlg;
import org.kablink.teaming.gwt.client.binderviews.ProfileEntryDlg.ProfileEntryDlgClient;
import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.event.ActivityStreamEnterEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent;
import org.kablink.teaming.gwt.client.event.AdministrationActionEvent;
import org.kablink.teaming.gwt.client.event.AdministrationEvent;
import org.kablink.teaming.gwt.client.event.AdministrationExitEvent;
import org.kablink.teaming.gwt.client.event.AdministrationUpgradeCheckEvent;
import org.kablink.teaming.gwt.client.event.BrowseHierarchyEvent;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.ChangeFavoriteStateEvent;
import org.kablink.teaming.gwt.client.event.CheckManageUsersActiveEvent;
import org.kablink.teaming.gwt.client.event.CheckManageUsersActiveEvent.ManageUsersActiveCallback;
import org.kablink.teaming.gwt.client.event.ContentChangedEvent;
import org.kablink.teaming.gwt.client.event.ContentChangedEvent.Change;
import org.kablink.teaming.gwt.client.event.ContextChangedEvent;
import org.kablink.teaming.gwt.client.event.ContextChangingEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.EditCurrentBinderBrandingEvent;
import org.kablink.teaming.gwt.client.event.EditPersonalPreferencesEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.FilesDroppedEvent;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.GetCurrentViewInfoEvent;
import org.kablink.teaming.gwt.client.event.GetCurrentViewInfoEvent.ViewInfoCallback;
import org.kablink.teaming.gwt.client.event.GotoContentUrlEvent;
import org.kablink.teaming.gwt.client.event.GotoMyWorkspaceEvent;
import org.kablink.teaming.gwt.client.event.GotoPermalinkUrlEvent;
import org.kablink.teaming.gwt.client.event.InvokeAddNewFolderEvent;
import org.kablink.teaming.gwt.client.event.InvokeChangePasswordDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeDownloadDesktopAppEvent;
import org.kablink.teaming.gwt.client.event.InvokeHelpEvent;
import org.kablink.teaming.gwt.client.event.InvokeShareBinderEvent;
import org.kablink.teaming.gwt.client.event.InvokeSimpleProfileEvent;
import org.kablink.teaming.gwt.client.event.JspLayoutChangedEvent;
import org.kablink.teaming.gwt.client.event.LoginEvent;
import org.kablink.teaming.gwt.client.event.LogoutEvent;
import org.kablink.teaming.gwt.client.event.MastheadHideEvent;
import org.kablink.teaming.gwt.client.event.MastheadShowEvent;
import org.kablink.teaming.gwt.client.event.MenuHideEvent;
import org.kablink.teaming.gwt.client.event.MenuShowEvent;
import org.kablink.teaming.gwt.client.event.PublicCollectionStateChangedEvent;
import org.kablink.teaming.gwt.client.event.SearchAdvancedEvent;
import org.kablink.teaming.gwt.client.event.SearchRecentPlaceEvent;
import org.kablink.teaming.gwt.client.event.SearchSavedEvent;
import org.kablink.teaming.gwt.client.event.SearchSimpleEvent;
import org.kablink.teaming.gwt.client.event.SearchTagEvent;
import org.kablink.teaming.gwt.client.event.SetDesktopDownloadAppControlVisibilityEvent;
import org.kablink.teaming.gwt.client.event.ShowCollectionEvent;
import org.kablink.teaming.gwt.client.event.ShowContentControlEvent;
import org.kablink.teaming.gwt.client.event.SidebarHideEvent;
import org.kablink.teaming.gwt.client.event.SidebarShowEvent;
import org.kablink.teaming.gwt.client.event.SizeChangedEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.TrackCurrentBinderEvent;
import org.kablink.teaming.gwt.client.event.UntrackCurrentBinderEvent;
import org.kablink.teaming.gwt.client.event.UntrackCurrentPersonEvent;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.event.ViewCurrentBinderTeamMembersEvent;
import org.kablink.teaming.gwt.client.event.ViewForumEntryEvent;
import org.kablink.teaming.gwt.client.event.ViewTeamingFeedEvent;
import org.kablink.teaming.gwt.client.event.ViewResourceLibraryEvent;
import org.kablink.teaming.gwt.client.profile.widgets.GwtQuickViewDlg;
import org.kablink.teaming.gwt.client.profile.widgets.GwtQuickViewDlg.GwtQuickViewDlgClient;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.CanModifyBinderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ChangeFavoriteStateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.CollectionPointData;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetCollectionPointDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetMainPageInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetPersonalPrefsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetUserWorkspaceInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.MainPageInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.PersistActivityStreamSelectionCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveBrandingCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SavePersonalPrefsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.TrackBinderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UntrackBinderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UntrackPersonCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UserWorkspaceInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.Agent;
import org.kablink.teaming.gwt.client.util.AgentBase;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderInfoHelper;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HistoryHelper;
import org.kablink.teaming.gwt.client.util.HistoryInfo;
import org.kablink.teaming.gwt.client.util.OnBrowseHierarchyInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.ViewFolderEntryInfo;
import org.kablink.teaming.gwt.client.util.BinderInfoHelper.BinderInfoCallback;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.SimpleProfileParams;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TreeMode;
import org.kablink.teaming.gwt.client.util.VibeProduct;
import org.kablink.teaming.gwt.client.util.ViewInfo;
import org.kablink.teaming.gwt.client.whatsnew.ActionsPopupMenu;
import org.kablink.teaming.gwt.client.whatsnew.ActionsPopupMenu.ActionMenuItem;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.ActivityStreamCtrlClient;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.ActivityStreamCtrlUsage;
import org.kablink.teaming.gwt.client.widgets.AddNewFolderDlg;
import org.kablink.teaming.gwt.client.widgets.AddNewFolderDlg.AddNewFolderDlgClient;
import org.kablink.teaming.gwt.client.widgets.AdminControl;
import org.kablink.teaming.gwt.client.widgets.AdminControl.AdminControlClient;
import org.kablink.teaming.gwt.client.widgets.ChangePasswordDlg;
import org.kablink.teaming.gwt.client.widgets.ChangePasswordDlg.ChangePasswordDlgClient;
import org.kablink.teaming.gwt.client.widgets.ContentControl;
import org.kablink.teaming.gwt.client.widgets.ContentControl.ContentControlClient;
import org.kablink.teaming.gwt.client.widgets.DesktopAppDownloadControl;
import org.kablink.teaming.gwt.client.widgets.DesktopAppDownloadControl.DesktopAppDownloadControlClient;
import org.kablink.teaming.gwt.client.widgets.DesktopAppDownloadControlCookies.Cookie;
import org.kablink.teaming.gwt.client.widgets.DesktopAppDownloadDlg;
import org.kablink.teaming.gwt.client.widgets.DesktopAppDownloadDlg.DesktopAppDownloadDlgClient;
import org.kablink.teaming.gwt.client.widgets.EditBrandingDlg;
import org.kablink.teaming.gwt.client.widgets.EditBrandingDlg.EditBrandingDlgClient;
import org.kablink.teaming.gwt.client.widgets.LoginDlg;
import org.kablink.teaming.gwt.client.widgets.LoginDlg.LoginDlgClient;
import org.kablink.teaming.gwt.client.widgets.LoginDlg.LoginStatus;
import org.kablink.teaming.gwt.client.widgets.MainMenuControl;
import org.kablink.teaming.gwt.client.widgets.MainMenuControl.MainMenuControlClient;
import org.kablink.teaming.gwt.client.widgets.DesktopAppDownloadControlCookies;
import org.kablink.teaming.gwt.client.widgets.MastHead;
import org.kablink.teaming.gwt.client.widgets.PersonalPreferencesDlg;
import org.kablink.teaming.gwt.client.widgets.TagThisDlg;
import org.kablink.teaming.gwt.client.widgets.TagThisDlg.TagThisDlgClient;
import org.kablink.teaming.gwt.client.widgets.VibeDockLayoutPanel;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl.WorkspaceTreeControlClient;
import org.kablink.teaming.gwt.client.workspacetree.BreadcrumbTreePopup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This widget will display the main Vibe page.
 * 
 * @author jwootton@novell.com
 */
public class GwtMainPage extends ResizeComposite
	implements
		// Event handlers implemented by this class.
		ActivityStreamEvent.Handler,
		ActivityStreamEnterEvent.Handler,
		ActivityStreamExitEvent.Handler,
		AdministrationActionEvent.Handler,
		AdministrationEvent.Handler,
		AdministrationExitEvent.Handler,
		AdministrationUpgradeCheckEvent.Handler,
		BrowseHierarchyEvent.Handler,
		ChangeFavoriteStateEvent.Handler,
		ContentChangedEvent.Handler,
		ContextChangedEvent.Handler,
		ContextChangingEvent.Handler,
		EditCurrentBinderBrandingEvent.Handler,
		EditPersonalPreferencesEvent.Handler,
		FullUIReloadEvent.Handler,
		GotoContentUrlEvent.Handler,
		GotoMyWorkspaceEvent.Handler,
		GotoPermalinkUrlEvent.Handler,
		InvokeAddNewFolderEvent.Handler,
		InvokeChangePasswordDlgEvent.Handler,
		InvokeDownloadDesktopAppEvent.Handler,
		InvokeHelpEvent.Handler,
		InvokeSimpleProfileEvent.Handler,
		InvokeShareBinderEvent.Handler,
		LoginEvent.Handler,
		LogoutEvent.Handler,
		MastheadHideEvent.Handler,
		MastheadShowEvent.Handler,
		MenuHideEvent.Handler,
		MenuShowEvent.Handler,
		SearchAdvancedEvent.Handler,
		SearchRecentPlaceEvent.Handler,
		SearchSavedEvent.Handler,
		SearchSimpleEvent.Handler,
		SearchTagEvent.Handler,
		SetDesktopDownloadAppControlVisibilityEvent.Handler,
		ShowCollectionEvent.Handler,
		ShowContentControlEvent.Handler,
		SidebarHideEvent.Handler,
		SidebarShowEvent.Handler,
		SizeChangedEvent.Handler,
		TrackCurrentBinderEvent.Handler,
		UntrackCurrentBinderEvent.Handler,
		UntrackCurrentPersonEvent.Handler,
		ViewCurrentBinderTeamMembersEvent.Handler,
		ViewResourceLibraryEvent.Handler,
		ViewTeamingFeedEvent.Handler
{
	public static boolean m_novellTeaming = true;
	public static RequestInfo m_requestInfo = jsGetRequestInfo();;
	public static ContentControl m_contentCtrl;

	private boolean m_controlKeyDown;
	private AddNewFolderDlg m_addNewFolderDlg = null;
	private VibeDockLayoutPanel m_mainPanel = null;
	private DockLayoutPanel m_splitLayoutPanel = null;
	private MainContentLayoutPanel m_contentLayoutPanel = null;
	private DesktopAppDownloadControl m_dadCtrl = null;
	private FlowPanel m_headerPanel = null;
	private boolean m_inSearch = false;
	private String m_searchTabId = "";
	private EditBrandingDlg m_editBrandingDlg = null;
	private PersonalPreferencesDlg m_personalPrefsDlg = null;
	private LoginDlg m_loginDlg = null;
	private TagThisDlg m_tagThisDlg = null;
	private ChangePasswordDlg m_changePwdDlg = null;
	private EditCanceledHandler m_editBrandingCancelHandler = null;
	private EditSuccessfulHandler m_editBrandingSuccessHandler = null;
	private EditSuccessfulHandler m_editPersonalPrefsSuccessHandler = null;
	private EditSuccessfulHandler m_editTagsSuccessHandler = null;
	private MainMenuControl m_mainMenuCtrl;
	private MastHead m_mastHead;
	private AdminControl m_adminControl = null;
	private BreadcrumbTreePopup m_breadCrumbBrowser;
	private WorkspaceTreeControl m_wsTreeCtrl;
	private UIStateManager m_uiStateManager;
	private ActivityStreamCtrl m_activityStreamCtrl = null;
	private CollectionPointData m_collectionPointData = null;
	private MainPageInfoRpcResponseData m_mainPageInfo;

	private com.google.gwt.dom.client.Element m_tagPanelElement;

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		// Activity stream events.
		TeamingEvents.ACTIVITY_STREAM,
		TeamingEvents.ACTIVITY_STREAM_ENTER,
		TeamingEvents.ACTIVITY_STREAM_EXIT,
		
		// Administration events.
		TeamingEvents.ADMINISTRATION,
		TeamingEvents.ADMINISTRATION_ACTION,
		TeamingEvents.ADMINISTRATION_EXIT,
		TeamingEvents.ADMINISTRATION_UPGRADE_CHECK,
		
		// Miscellaneous events.
		TeamingEvents.BROWSE_HIERARCHY,
		TeamingEvents.CHANGE_FAVORITE_STATE,
		TeamingEvents.FULL_UI_RELOAD,
		TeamingEvents.INVOKE_SHARE_BINDER,

		// Goto events.
		TeamingEvents.GOTO_CONTENT_URL,
		TeamingEvents.GOTO_MY_WORKSPACE,
		TeamingEvents.GOTO_PERMALINK_URL,

		// Context events.
		TeamingEvents.CONTENT_CHANGED,
		TeamingEvents.CONTEXT_CHANGED,
		TeamingEvents.CONTEXT_CHANGING,

		// Edit events.
		TeamingEvents.EDIT_CURRENT_BINDER_BRANDING,
		TeamingEvents.EDIT_PERSONAL_PREFERENCES,

		// Invoke events.
		TeamingEvents.INVOKE_ADD_NEW_FOLDER,
		TeamingEvents.INVOKE_CHANGE_PASSWORD_DLG,
		TeamingEvents.INVOKE_DOWNLOAD_DESKTOP_APP,
		TeamingEvents.INVOKE_HELP,
		TeamingEvents.INVOKE_SIMPLE_PROFILE,
		
		// Login/out events.
		TeamingEvents.LOGIN,
		TeamingEvents.LOGOUT,
		
		// Masthead events.
		TeamingEvents.MASTHEAD_HIDE,
		TeamingEvents.MASTHEAD_SHOW,
		
		// Menu events
		TeamingEvents.MENU_HIDE,
		TeamingEvents.MENU_SHOW,

		// Search events.
		TeamingEvents.SEARCH_ADVANCED,
		TeamingEvents.SEARCH_RECENT_PLACE,
		TeamingEvents.SEARCH_SAVED,
		TeamingEvents.SEARCH_SIMPLE,
		TeamingEvents.SEARCH_TAG,
		
		// Show events.
		TeamingEvents.SHOW_COLLECTION,
		TeamingEvents.SHOW_CONTENT_CONTROL,
		
		// Sidebar events.
		TeamingEvents.SIDEBAR_HIDE,
		TeamingEvents.SIDEBAR_SHOW,
		
		// Sizing events.
		TeamingEvents.SIZE_CHANGED,
		
		// Tracking events.
		TeamingEvents.TRACK_CURRENT_BINDER,
		TeamingEvents.UNTRACK_CURRENT_BINDER,
		TeamingEvents.UNTRACK_CURRENT_PERSON,
		
		// View events.
		TeamingEvents.VIEW_CURRENT_BINDER_TEAM_MEMBERS,
		TeamingEvents.VIEW_RESOURCE_LIBRARY,
		TeamingEvents.VIEW_TEAMING_FEED,
		
		// Set events.
		TeamingEvents.SET_DESKTOP_DOWNLOAD_APP_CONTROL_VISIBILITY,
	};
	
	/*
	 * Class constructor.
	 *  
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private GwtMainPage()
	{
		// If the UI debug toggle is turned on...
		if (m_requestInfo.isDebugUI()) {
			// ...display the GWT user agent we loaded in the status
			// ...bar and tab.
			AgentBase agent     = GWT.create(Agent.class);
			String    userAgent = ("GWT user.agent: " + agent.getAgentName());
			Window.setStatus(userAgent);
			Window.setTitle( userAgent);
		}
		
		// Initialize browser history handling... 
		HistoryHelper.initializeBrowserHistory();
		
		// ...and construct and initialize the page.
		constructMainPage_Start();
		initWidget( m_mainPanel );
	}
	
	/*
	 * Loads the initial data from the server and continues loading
	 * controls for the page.
	 */
	private void loadInitialData( String binderId )
	{
		// Execute a GWT RPC command asking the server for our initial
		// data requirements.
		GetMainPageInfoCmd cmd = new GetMainPageInfoCmd(
			binderId,
			( GwtClientHelper.getTimeZoneOffsetMillis( new Date() ) * -1 ) );
		
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetMainPageInfo() );
			}//end onFailure()
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				// Store the BinderInfo...
				m_mainPageInfo = ((MainPageInfoRpcResponseData) response.getResponseData());

				// ...the user's avatar URL from the response...
				String userAvatarUrl = m_mainPageInfo.getUserAvatarUrl();
				m_requestInfo.setUserAvatarUrl( ( null == userAvatarUrl ) ? "" : userAvatarUrl );
				
				// ...if the user hasn't hidden the desktop application
				// ...download control permanently...
				if ( m_mainPageInfo.isShowDesktopAppDownloader() )
				{
					// ...check if they've hidden it for the current
					// ...session only...
					m_mainPageInfo.setShowDesktopAppDownloader(
						DesktopAppDownloadControlCookies.getBooleanCookieValue(
							Cookie.HINT_VISIBLE,
							true));
				}
				
				// ...and continue the load process.
				GwtClientHelper.deferCommand( new ScheduledCommand() {
					@Override
					public void execute()
					{
						loadMainMenuControl();
					}// end execute()
				} );
			}// end onSuccess()
		});
	}// end loadInitialData()

	/*
	 * Various control loaders used to load the split points containing
	 * the code for the controls in the main page.
	 * 
	 * Loads the split point for the MainMenuControl and instantiates
	 * an object from it.
	 */
	private void loadMainMenuControl()
	{
		MainMenuControl.createAsync( this, "top", new MainMenuControlClient()
		{			
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}// end onUnavailable()
			
			@Override
			public void onSuccess( MainMenuControl mainMenuCtrl )
			{
				m_mainMenuCtrl = mainMenuCtrl;				
				GwtClientHelper.deferCommand( new ScheduledCommand() {
					@Override
					public void execute()
					{
						loadWorkspaceTreeControl();
					}// end execute()
				} );
			}// end onSuccess()
		} );
	}// end loadMainMenuControl()

	/*
	 * Loads the split point for the WorkspaceTreeControl and
	 * instantiates an object from it.
	 */
	private void loadWorkspaceTreeControl()
	{
		WorkspaceTreeControl.createAsync( this, m_mainPageInfo.getBinderInfo(), false, TreeMode.VERTICAL, new WorkspaceTreeControlClient()
		{			
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}// end onUnavailable()
			
			@Override
			public void onSuccess( WorkspaceTreeControl wsTreeCtrl )
			{
				m_wsTreeCtrl = wsTreeCtrl;				
				GwtClientHelper.deferCommand( new ScheduledCommand() {
					@Override
					public void execute()
					{
						loadContentControl();
					}// end execute()
				} );
			}// end onSuccess()
		} );
	}// end loadWorkspaceTreeControl()

	/*
	 * Loads the split point for the ContentControl and instantiates an
	 * object from it.
	 */
	private void loadContentControl()
	{
		ContentControl.createAsync( this, "gwtContentIframe", new ContentControlClient()
		{			
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}// end onUnavailable()
			
			@Override
			public void onSuccess( ContentControl contentCtrl )
			{
				m_contentCtrl = contentCtrl;				
				GwtClientHelper.deferCommand( new ScheduledCommand() {
					@Override
					public void execute()
					{
						loadActivityStreamCtrl();
					}// end execute()
				} );
			}// end onSuccess()
		} );
	}// end loadContentControl()

	/*
	 * Loads the split point for the ActivityStreamCtrl and
	 * instantiates an object from it.
	 */
	private void loadActivityStreamCtrl()
	{
		ActionsPopupMenu actionsMenu;
		ArrayList<ActionMenuItem> list;
		
		list = new ArrayList<ActionMenuItem>();
		list.add(ActionMenuItem.REPLY );
		list.add( ActionMenuItem.SHARE );
		list.add( ActionMenuItem.VIEW_DETAILS );
		list.add( ActionMenuItem.SUBSCRIBE );
		if ( GwtTeaming.m_requestInfo.isLicenseFilr() == false )
			list.add( ActionMenuItem.TAG );
		list.add( ActionMenuItem.SEPARATOR );
		list.add( ActionMenuItem.MARK_READ );
		list.add( ActionMenuItem.MARK_UNREAD );
		
		actionsMenu = new ActionsPopupMenu( true, true, list.toArray( new ActionMenuItem[list.size()] ) );
		ActivityStreamCtrl.createAsync( ActivityStreamCtrlUsage.STANDALONE, actionsMenu, new ActivityStreamCtrlClient()
		{			
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}// end onUnavailable()
			
			@Override
			public void onSuccess( ActivityStreamCtrl asCtrl )
			{
				m_activityStreamCtrl = asCtrl;
				GwtClientHelper.deferCommand( new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						if ( m_mainPageInfo.isShowDesktopAppDownloader() )
						     loadDesktopAppDownloadCtrl();
						else constructMainPage_Finish();
					}// end execute()
				} );
			}// end onSuccess()
		} );
	}// end ActivityStreamCtrl()

	/*
	 * Loads the split point for the ActivityStreamCtrl and
	 * instantiates an object from it.
	 */
	private void loadDesktopAppDownloadCtrl()
	{
		DesktopAppDownloadControl.createAsync( new DesktopAppDownloadControlClient()
		{			
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}// end onUnavailable()
			
			@Override
			public void onSuccess( DesktopAppDownloadControl dadCtrl )
			{
				m_dadCtrl = dadCtrl;
				GwtClientHelper.deferCommand( new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						constructMainPage_Finish();
					}// end execute()
				} );
			}// end onSuccess()
		} );
	}// end loadDesktopAppDownloadCtrl()

	/*
	 * Adds a native preview handler that we can use to watch for
	 * various keyboard states, ...
	 */
	private void addNativePreviewHandler() {
		Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				// Is the event canceled?
				if ( ! event.isCanceled() )
				{
					// No!  What's the event?  
					switch ( event.getTypeInt() )
					{
					case Event.ONKEYDOWN:
						// A key down!  Is the control key down?
						m_controlKeyDown = event.getNativeEvent().getCtrlKey();
						break;
						
					case Event.ONKEYUP:
						// A key up!  The control key can no longer be
						// down.
						m_controlKeyDown = false;
						break;
					}
				}
			}
		});
	}
	
	/*
	 * Starts the initializations of the main page.
	 */
	private void constructMainPage_Start()
	{
		// Add a native preview handler that we can use to watch for
		// various keyboard states, ...
		addNativePreviewHandler();
		
		// Initialize the flag indicating of we're running Novell Vibe
		// vs. Kablink Vibe...
		m_novellTeaming = m_requestInfo.isNovellTeaming();
		
		// ...create the main Vibe content panel...
		m_mainPanel = new VibeDockLayoutPanel( Style.Unit.PX );
		m_mainPanel.addStyleName( "mainTeamingPagePanel" );
		
		// ...and begin the load process. 
		String riBinderId = m_requestInfo.getBinderId();
		if ( ! GwtClientHelper.hasString( riBinderId ))
		{
			riBinderId = m_requestInfo.getCurrentUserWorkspaceId();
			if ( ! ( GwtClientHelper.hasString( riBinderId ) ) )
			{
				riBinderId = m_requestInfo.getTopWSId();
			}
		}
		loadInitialData( riBinderId );
	}// end constructMainPage_Start();
	
	/*
	 * Finishes the initialization of the main page.
	 */
	private void constructMainPage_Finish()
	{
		Element bodyElement;

		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this);

		// Initialize the various call backs accessed from JavaScript.
		initJSMethods();
		
		// Create a UIStateManager that we will use to save/restore the ui state.
		m_uiStateManager = new UIStateManager();
		
		// Set the class name on the <body> element to "mainGwtTeamingPage"
		bodyElement = RootPanel.getBodyElement();
		bodyElement.setClassName( "mainTeamingPage" );
		
		m_headerPanel = new FlowPanel();
		m_headerPanel.addStyleName( "mainHeaderPanel" );
		m_mainPanel.addNorth( m_headerPanel, GwtConstants.HEADER_HEIGHT );

		// Add the MastHead to the page.
		m_mastHead = new MastHead( m_requestInfo );
		m_headerPanel.add( m_mastHead );

		// Is there an error message to be displayed?
		final String errMsg = m_requestInfo.getErrMsg();
		if ( GwtClientHelper.hasString( errMsg ) )
		{
			// Yes
			// Execute a deferred command the will display it.
			GwtClientHelper.deferCommand( new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					// Is the user logged in?
					if ( m_requestInfo.isUserLoggedIn() )
					{
						// Yes
						// Take the user to their workspace.
						GotoMyWorkspaceEvent.fireOneAsync();
					}
					
					// Tell the user.  We do this as a deferred command
					// so that the UI can continue to render while the
					// message box is displayed.
					Window.alert( errMsg );
				}
			} );
		}
		
		// Add the main menu to the page.
		m_headerPanel.add( m_mainMenuCtrl );
		
		m_splitLayoutPanel = new DockLayoutPanel( Style.Unit.PX );
		m_mainPanel.add( m_splitLayoutPanel );
		
		// If we constructed a desktop application download control...
		if ( null != m_dadCtrl ) {
			// ...add it as the north panel of the split layout.
			m_splitLayoutPanel.addNorth( m_dadCtrl, GwtConstants.DESKTOP_APP_DOWNLOAD_HEIGHT );
		}
		
		// Create the WorkspaceTree control.
		m_wsTreeCtrl.addStyleName( "mainWorkspaceTreeControl" );
		m_splitLayoutPanel.addWest( m_wsTreeCtrl, GwtConstants.SIDEBAR_TREE_WIDTH );
		
		// Create the content control.
		m_contentCtrl.addStyleName( "mainContentControl" );
		
		m_activityStreamCtrl.hide();
		
		// Create a panel that will hold the content.
		m_contentLayoutPanel = new MainContentLayoutPanel( m_contentCtrl, m_activityStreamCtrl );
		m_contentLayoutPanel.addStyleName( "contentLayoutPanel" );
		m_splitLayoutPanel.add( m_contentLayoutPanel );

		// Is the browser being reloaded on an activity stream?
		HistoryInfo reloadInfo       = GwtTeaming.getBrowserReloadInfo();
		boolean     isReload         = ( null != reloadInfo );
		boolean     isReloadWhatsNew = ( isReload && reloadInfo.getItemType().isActivityStream() );
		if ( isReloadWhatsNew )
		{
			// Yes!  Set things in the RequestInfo so that the
			// appropriate stream gets reloaded.
			ActivityStreamInfo asi    = reloadInfo.getActivityStreamInfo().getActivityStreamInfo();
			String[]           asiIds = asi.getBinderIds();
			
			m_requestInfo.setShowWhatsNewOnLogin();
			m_requestInfo.setShowSpecificWhatsNew( asi.getActivityStream() );
			if ( ( null != asiIds ) && ( 0 < asiIds.length ) )
			{
				m_requestInfo.setShowSpecificWhatsNewId( Long.parseLong( asiIds[0] ) );
			}
			m_requestInfo.setShowSpecificWhatsNewHistoryAction();
		}
		
		// Is the browser being reloaded on other than an activity
		// stream?
		if ( isReload && ( ! isReloadWhatsNew ) )
		{
			// Yes!  Process the history data directly to perform the
			// reload.
			HistoryHelper.processHistoryInfoAsync( reloadInfo );
		}
		
		else
		{
			// No, the browser isn't being reloaded or its being
			// reloaded on an activity stream!  Do we have a URL we
			// should set the ContentControl to?  (Note we do this in
			// the reload activity stream case so that we have a
			// context loaded for the activity stream to run on.)
			String url = m_requestInfo.getAdaptedUrl();
			if ( GwtClientHelper.hasString( url ) )
			{
				// Yes!  If we're supposed to show a collection at
				// login...
				String showCollectionOnLogin = m_requestInfo.getShowCollectionOnLogin();
				if ( GwtClientHelper.hasString( showCollectionOnLogin ) )
				{
					// ...add it to the URL...
					url += ( "&showCollection=" + showCollectionOnLogin);
				}
				
				// ...and put the URL into affect.
				gotoUrlAsync( url, isReload );
			}
		}
		
		// Is the user logged in?
		if ( m_requestInfo.isUserLoggedIn() == false )
		{
			final boolean canCancel;
			
			// No!  Should we prompt for login?
			boolean promptForLogin = m_requestInfo.promptForLogin();
			if ( promptForLogin == false )
			{
				// No, are we running Filr?
				if ( GwtTeaming.m_requestInfo.isLicenseFilr() )
				{
					// Yes, we always want to prompt for login.
					promptForLogin = true;
					canCancel = true;
				}
				else
					canCancel = getLoginCanCancel();
			}
			else
				canCancel = getLoginCanCancel();
			
			// Should we invoke the login dialog?
			if ( promptForLogin == true )
			{
				// Yes
				// Hide the workspace tree control and the menu bar.
				m_wsTreeCtrl.setVisible( false );
				m_mainMenuCtrl.setVisible( false );
				
				// invoke the login dialog.
				GwtClientHelper.deferCommand( new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						invokeLoginDlg( canCancel );
					}
				} );
			}
		}
				
		// If we're running GroupWise integrations or otherwise require
		// session captive mode...
		if ((VibeProduct.GW == m_requestInfo.getVibeProduct()) || m_requestInfo.isSessionCaptive())
		{
			// ...we hide the masthead and sidebar by default.
			GwtTeaming.fireEvent( new MastheadHideEvent( false ) );	// false -> Don't resize the content now...
			GwtTeaming.fireEvent( new SidebarHideEvent(  false ) );	// ...will happen when the frame has loaded.
			
			// Have the masthead hide the logout link
			m_mastHead.hideLogoutLink();
		}
		
		// Are we running Filr?
		if ( m_requestInfo.isLicenseFilr() )
		{
			// Yes
			GwtTeaming.fireEvent( new MenuHideEvent() );	// false -> Don't resize the content now...
			GwtTeaming.fireEvent( new SidebarHideEvent(  false ) );	// ...will happen when the frame has loaded.
			
			// Is this the first time the user logged in?
			if ( m_mainPageInfo.isFirstLogin() )
			{
				// Yes
				// Are we dealing wit the admin user?
				if ( m_mainPageInfo.isSuperUser() )
				{
					// Yes
					// Invoke the change password dialog.
					GwtTeaming.fireEvent( new InvokeChangePasswordDlgEvent() );
				}
			}
		}
	}// end constructMainPage_Finish()

	/**
	 * 
	 */
	public AdminControl getAdminControl()
	{
		return m_adminControl;
	}
	
	/**
	 * Get the collection point data and then after the rpc request
	 * returns show the collection point.
	 */
	public void getCollectionPointDataAndShowCollectionPoint( final CollectionType collectionType )
	{
		GetCollectionPointDataCmd cmd;
		AsyncCallback<VibeRpcResponse> getCollectionPointUrlRpcCallback;
		
		getCollectionPointUrlRpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetCollectionPointUrl(),
						collectionType.name() );
			}
			
			/**
			 * 
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				final String url;
				
				m_collectionPointData = (CollectionPointData) response.getResponseData();
				url = m_collectionPointData.getUrl( collectionType );

				if ( url != null && url.length() > 0 )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// Show the collection point.
							gotoUrlAsync( url );
						}
					} );
				}
			}
		};
		
		cmd = new GetCollectionPointDataCmd();
		GwtClientHelper.executeCommand( cmd, getCollectionPointUrlRpcCallback );
	}
	
	/**
	 * Returns the main content panel.
	 * 
	 * @return
	 */
	public MainContentLayoutPanel getMainContentLayoutPanel()
	{
		return m_contentLayoutPanel;
	}
	
	/**
	 * Returns the main menu control.
	 * 
	 * @return
	 */
	public MainMenuControl getMainMenu()
	{
		return m_mainMenuCtrl;
	}//end getMainMenu()

	/**
	 * Returns the main page information object associated with this
	 * GwtMainPage instance.
	 * 
	 * @return
	 */
	public MainPageInfoRpcResponseData getMainPageInfo()
	{
		return m_mainPageInfo;
	}// end getMainPageInfo()

	/**
	 * Return the mast head object
	 */
	public MastHead getMastHead()
	{
		return m_mastHead;
	}
	
	/**
	 * Returns any current search tab ID.
	 * 
	 * @return
	 */
	public String getSearchTabId()
	{
		return m_searchTabId;
	}// end getSearchTabId()
	
	/**
	 * Returns the workspace tree control.
	 * 
	 * @return
	 */
	public WorkspaceTreeControl getWorkspaceTree()
	{
		return m_wsTreeCtrl;
	}//end getWorkspaceTree()
	
	/*
	 * Called to create a JavaScript method that will be invoked from
	 * JSP based content when its layout changes.
	 */
	private native void initJspLayoutChangedJS( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_jspLayoutChanged = function( binderId )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::jspLayoutChanged(Ljava/lang/String;)( binderId );
		}//end ss_jspLayoutChanged()
	}-*/;

	/*
	 * Called to create a JavaScript method that will be invoked from
	 * an administration page when the user presses close or cancel in the administration page.
	 */
	private native void initCloseAdministrationContentPanelJS( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_closeAdministrationContentPanel = function( pageName )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::closeAdministrationContentPanel()();
		}//end ss_closeAdministrationContentPanel()
	}-*/;

	/*
	 * Called to create a JavaScript method that will be invoked from
	 * view_workarea_navbar.jsp when new contexts are loaded.
	 */
	private native void initContextLoadHandlerJS(GwtMainPage gwtMainPage) /*-{
		$wnd.ss_contextLoaded = function( binderId, inSearch, searchTabId )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::contextLoaded(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)( binderId, inSearch, searchTabId );
		}//end ss_contextLoaded()
		
		$wnd.ss_gwtRelayoutPage = function()
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::relayoutPage()();
		}//end ss_gwtRelayoutPage()
		
		$wnd.ss_gotoContentUrl = function( url )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::fireGotoContentUrl(Ljava/lang/String;)( url );
		}
	}-*/;

	/*
	 * Called to create a JavaScript method that will be invoked from
	 * the JSP based UI just before a context switch occurs.
	 */
	private native void initFireContextChangingJS(GwtMainPage gwtMainPage) /*-{
		$wnd.ss_preContextSwitch = function()
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::fireContextChanging()();
		}//end ss_preContextSwitch()
	}-*/;

	/*
	 * Called to create a JavaScript method that will be invoked from
	 * JSP when file(s) are successfully dropped on an applet.
	 */
	private native void initFireFilesDroppedJS(GwtMainPage gwtMainPage) /*-{
		$wnd.ss_filesDropped = function( binderId )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::fireFilesDropped(Ljava/lang/String;)( binderId );
		}//end ss_filesDropped()
	}-*/;

	/*
	 * Called to create a JavaScript method that will be invoked from a page that holds a landing page.
	 * There are options in the landing page settings to hide the masthead, hide the sidebar, etc.
	 * This method will show/hide controls based on these settings.
	 */
	private native void initHandleLandingPageOptionsJS( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_handleLandingPageOptions = function( binderId, hideMasthead, hideSidebar, showBranding, hideMenu )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::handleLandingPageOptions(Ljava/lang/String;ZZZZ)( binderId, hideMasthead, hideSidebar, showBranding, hideMenu );
		}//end ss_handleLandingPageOptions()
	}-*/;

	/*
	 * Called to create a JavaScript method that will be invoked from
	 * any jsp that we don't want loaded the old way, we want to handle the ui
	 * using gwt.  The login page is an example of this.  If login_please.jsp ever gets
	 * loaded we want to invoke the login dialog.
	 */
	private native void initHandlePageWithGWTJS( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_handlePageWithGWT = function( pageName )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::handlePageWithGWT(Ljava/lang/String;)( pageName );
		}//end ss_handlePageWithGWT()
	}-*/;

	/*
	 * Called to create a JavaScript method that can be called to invoke the admin page.
	 */
	private native void initInvokeAdminPageJS( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_invokeAdminPage = function()
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::invokeAdminPage()();
		}
	}-*/;

	/*
	 * Called to create a JavaScript method that can be called to invoke the login dialog.
	 */
	private native void initInvokeLoginDlgJS( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_invokeLoginDlg = function( allowCancel )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::invokeLoginDlg(Z)( allowCancel );
		}
	}-*/;

	/*
	 * Called to create a JavaScript method that will allow independent Content pages that are not 
	 * instantiated in the GWTMainPage to be able fire a simple Vibe event on the EventBus to notify
	 * any listeners.
	 * 
	 * com.google.web.bindery.event.shared.Event
	 */
	private native void initFireVibeEventOnMainEventBusJS( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_fireVibeEventOnMainEventBus = function( eventEnum )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::fireSimpleVibeEvent(Lorg/kablink/teaming/gwt/client/event/TeamingEvents;)( eventEnum );
		}//end ss_fireVibeEventOnMainEventBus
	}-*/;

	/*
	 * Initialize the various call backs accessed from JavaScript.
	 */
	private void initJSMethods()
	{
		// Initialize the context load handler used by the JSP based
		// UI to tell the GWT UI that a context has been loaded.
		initContextLoadHandlerJS( this );
		
		// Initialize the pre context switch handler used by the
		// JSP based UI to tell the GWT UI that a context switch is
		// about to occur.
		initFireContextChangingJS( this );

		// Initialize the JavaScript function that gets called when
		// files are successfully dropped on the file drag and drop
		// applet.
		initFireFilesDroppedJS( this );
		
		// Initialize the JavaScript function that gets called when we want to handle a page using
		// GWT instead of in jsp.
		// For example, we never want the jsp login page to be loaded in the content control.
		initHandlePageWithGWTJS( this );
		
		// Initialize the JavaScript function that gets called when
		// JSP content layout changes.
		initJspLayoutChangedJS( this );
		
		// Initialize the JavaScript function that gets called when we want to close the
		// administration content panel.
		initCloseAdministrationContentPanelJS( this );
		
		// Initialize the JSNI method that will allow any content to
		// fire a Vibe OnPrem event on the main event bus for the
		// application.
		//
		// See GwtClientHelper:jsFireVibeEventOnMainEventBus()
		initFireVibeEventOnMainEventBusJS( this );
		
		// Initializes the callback method used to load a content URL. 
		initGotoContentUrlJS( this );
		
		// Initialize JavaScript to perform Popup for User Profile
		initSimpleUserProfileJS( this );
		
		// Initialize JavaScript that handles the landing page options
		initHandleLandingPageOptionsJS( this );
		
		// Initialize the JavaScript that calls the login dialog.
		initInvokeLoginDlgJS( this );
		
		// Initialize the JavaScript that invokes the Tag dialog
		initInvokeTagDlgJS( this );
		
		// Initialize the JavaScript that invokes the Share dialog
		initInvokeShareDlgJS( this );
		
		// Initialize the JavaScript that invokes the admin page.
		initInvokeAdminPageJS( this );
		
		// Initialize the JavaScript that runs the entry viewer.
		initShowForumEntry( this );
		
		// Initialize the JavaScript that will delete an entry that can
		// be invoked from a JSP page.
		initDeleteForumEntry( this );
	}

	/*
	 * Creates the native JavaScript method used to load a content URL.
	 */
	private native void initGotoContentUrlJS( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_gotoContentUrl = function( url )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::gotoUrlAsync_FromJSP(Ljava/lang/String;)( url );
		}//end ss_gotoContentUrl
	}-*/;
	
	/*
	 * Called to create a JavaScript method that can be called to invoke the Share dialog.
	 */
	private native void initInvokeShareDlgJS( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_invokeShareDlg = function( entryId )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::invokeShareDlg(Ljava/lang/String;)( entryId );
		}
	}-*/;

	/*
	 * Invoke the Simple User Profile or Quick View
	 */
	private native void initSimpleUserProfileJS( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_invokeSimpleProfile = function( element, binderId, userName )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::fireInvokeSimpleProfile(Lcom/google/gwt/dom/client/Element;Ljava/lang/String;Ljava/lang/String;)( element, binderId, userName );
		}//end ss_fireInvokeSimpleProfile
	}-*/;	

	/*
	 * Called to create a JavaScript method that can be called to invoke the Tag dialog.
	 */
	private native void initInvokeTagDlgJS( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_invokeTagDlg = function( entryId, entryTitle, div )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::invokeTagDlg(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/dom/client/Element;)( entryId, entryTitle, div );
		}
	}-*/;
	
	/*
	 * Called to create a JavaScript method that can be called to show
	 * an entry viewer.
	 */
	private native void initShowForumEntry( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_showForumEntryGwt = function( url, isDashboard )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::viewForumEntry(Ljava/lang/String;Ljava/lang/String;)( url, isDashboard );
		}
	}-*/;

	/*
	 * Called to create a JavaScript method that can be called to
	 * delete and entry.
	 */
	private native void initDeleteForumEntry( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_deleteForumEntryGwt = function( folderId, entryId  )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::deleteForumEntry(Ljava/lang/String;Ljava/lang/String;)( folderId, entryId );
		}
	}-*/;

	/*
	 * Copies the <title> text from the GWT content IFRAME to the
	 * main GWT page's <title>.
	 */
	private static native void jsFixupGwtMainTitle() /*-{
		$wnd.top.document.title = $wnd.top.gwtContentIframe.document.title;
	}-*/;

	/*
	 * Called when a the layout of JSP content changes.  Simply fires
	 * an JspLayoutChangedEvent.
	 */
	private void jspLayoutChanged( String binderId )
	{
		JspLayoutChangedEvent are = new JspLayoutChangedEvent( Long.parseLong( binderId ));
		GwtTeaming.fireEvent( are );
	}// end jspLayoutChanged()
	
	/*
	 * This method gets called by administration pages that are implemented in jsp when the user
	 * presses, ok, cancel or close.  We used to close the administration content page.  Now we
	 * show the administration home page.
	 */
	private void closeAdministrationContentPanel()
	{
		AdminControl.showHomePage( m_adminControl );
	}// end closeAdministrationContentPanel()
	
	/*
	 * Puts a context change from the JSP based UI into effect.
	 */
	private void contextLoaded( String binderId, String inSearchS, String searchTabId )
	{
		Instigator instigator = Instigator.JSP_CONTENT_LOADED;
		boolean inSearch = ( (null != inSearchS ) && Boolean.parseBoolean( inSearchS ) );
		if ( inSearch )
		{
			instigator = Instigator.SEARCH_RESULTS;
		}
		else if ( null != m_contentCtrl )
		{
			instigator = m_contentCtrl.getContentInstigator();
			if ( Instigator.UNKNOWN == instigator )
			{
				instigator = Instigator.JSP_CONTENT_LOADED;
			}
		}
		contextLoaded( binderId, instigator, inSearch, searchTabId );
		if ( inSearch )
		{
			relayoutPage();
		}
	}
	
	private void contextLoaded( String binderId, Instigator instigator )
	{
		contextLoaded( binderId, instigator, false, "" );
	}
	
	private void contextLoaded( final String binderId, final Instigator instigator, final boolean inSearch, final String searchTabId )
	{
		// Do we have a binder ID of the context loaded?  (Note that we
		// won't for search results.)
		if ( GwtClientHelper.hasString( binderId ) )
		{
			// Yes!  Get the BinderInfo for the binder...
			BinderInfoHelper.getBinderInfo( binderId, new BinderInfoCallback() {
				@Override
				public void onFailure()
				{
					// Nothing to do!  The user will already have been
					// told about the problem.
				}// end onFailure()

				@Override
				public void onSuccess( final BinderInfo binderInfo )
				{
					// ...and load the context from that.
					GwtClientHelper.deferCommand( new ScheduledCommand() {
						@Override
						public void execute()
						{
							contextLoaded( binderInfo, instigator, inSearch, searchTabId );
						}// end execute()
					} );
				}// end onSuccess()
			});
		}
		
		else {
			// No, we weren't given the ID of the context loaded!
			// Treat it as though the current context was reloaded.
			contextLoaded(
				m_mainPageInfo.getBinderInfo(),
				instigator,
				inSearch,
				searchTabId );
		}
	}
	
	private void contextLoaded( final BinderInfo binderInfo, final Instigator instigator, boolean inSearch, String searchTabId )
	{
		// If the administration control is NOT active...
		if ( !isAdminActive() )
		{
			// ..restore the UI state.
			restoreUIState();
		}

		// If the context was loaded because of the initial login and
		// we're entering activity stream mode by default...
		if ( m_requestInfo.isShowWhatsNewOnLogin() )
		{
			// ...activity stream mode will already have been loaded.
			// ...Clear the flag, tell the menu about this context and
			// ...otherwise ignore this.
			m_requestInfo.clearShowWhatsNewOnLogin();
			m_mainMenuCtrl.setContext( binderInfo, inSearch, searchTabId );
			handleOnActivityStreamEnter(ActivityStreamDataType.OTHER);
			return;
		}
		
		// Is the activity stream visible?
		if ( m_activityStreamCtrl != null && m_activityStreamCtrl.isVisible() )
		{
			// Yes!  If w're handling a search while the activity
			// streams are up...
			if ( !inSearch )
			{
				// ...we assume that we're reading an item so ignore
				// ...the context loaded...
				return;
			}
			
			// ...otherwise, we hide the activity streams control and
			// ...let the search display.
			m_contentLayoutPanel.showContentControl();
		}
		
		m_inSearch    = inSearch;
		m_searchTabId = searchTabId;
		
		jsFixupGwtMainTitle();

		GetBinderPermalinkCmd cmd = new GetBinderPermalinkCmd( binderInfo.getBinderId() );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
					binderInfo.getBinderId() );
			}//end onFailure()
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				String binderPermalink;
				StringRpcResponseData responseData;

				responseData    = ((StringRpcResponseData) response.getResponseData());
				binderPermalink = responseData.getStringValue();
				
				OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(
					binderInfo,
					binderPermalink,
					instigator );
				if ( GwtClientHelper.validateOSBI( osbInfo, binderInfo.isBinderAccessible() ) )
				{
					GwtTeaming.fireEvent( new ContextChangedEvent( osbInfo ) );
				}
			}// end onSuccess()
		});
	}// end contextLoaded()

	/*
	 * Invoke the "Edit Branding" dialog.
	 */
	private void editBranding( GwtBrandingData brandingDataIn, int xPos, int yPos )
	{
		String brandingBinderId;
		int xCalc;
		int yCalc;
		
		// Will the user be editing the site branding?
		if ( brandingDataIn.isSiteBranding() == false )
		{
			GwtBrandingData siteBrandingData;

			// No
			// If the administrator has set the branding rule to be "site branding only", tell the
			// user they can't edit the branding.
			siteBrandingData = m_mastHead.getSiteBrandingData();
			if ( siteBrandingData.getBrandingRule() == GwtBrandingDataExt.BrandingRule.DISPLAY_SITE_BRANDING_ONLY )
			{
				Window.alert( GwtTeaming.getMessages().cantEditBranding() );
				return;
			}
		}
		
		// Is the branding data inherited?  Branding is inherited if it came from a binder other than
		// the binder we are working with.
		brandingBinderId = brandingDataIn.getBinderId();
		if ( brandingDataIn.isSiteBranding() == false && brandingBinderId.equalsIgnoreCase( m_mastHead.getBinderId() ) == false )
		{
			// Yes, start with empty branding data.
			brandingDataIn = new GwtBrandingData();
			brandingDataIn.setBinderId( m_mainPageInfo.getBinderInfo().getBinderId() );
		}
		
		final GwtBrandingData brandingData = brandingDataIn;
		
		// Get the position of the content control.
		if ( xPos == -1 )
		{
			xCalc = m_contentLayoutPanel.getAbsoluteLeft();
			if ( xCalc < 75 )
				xCalc = 75;
		}
		else
			xCalc = xPos;
		
		if ( yPos == -1 )
		{
			yCalc = m_contentLayoutPanel.getAbsoluteTop();
			if ( yCalc < 75 )
				yCalc = 75;
		}
		else
			yCalc = yPos;
		
		final int x = xCalc;
		final int y = yCalc;
		
		// Create a handler that will be called when the user presses the ok button in the dialog.
		if ( m_editBrandingSuccessHandler == null )
		{
			m_editBrandingSuccessHandler = new EditSuccessfulHandler()
			{
				private AsyncCallback<VibeRpcResponse> rpcSaveCallback = null;
				private String binderId = m_mastHead.getBinderId();
				private GwtBrandingData savedBrandingData = null;
				
				/**
				 * This method gets called when user user presses ok in the "Edit Branding" dialog.
				 */
				@Override
				public boolean editSuccessful( Object obj )
				{
					// Create the callback that will be used when we issue an ajax request to save the branding data.
					if ( rpcSaveCallback == null )
					{
						rpcSaveCallback = new AsyncCallback<VibeRpcResponse>()
						{
							/**
							 * 
							 */
							@Override
							public void onFailure( Throwable t )
							{
								GwtClientHelper.handleGwtRPCFailure(
									t,
									GwtTeaming.getMessages().rpcFailure_GetBranding(),
									binderId );
							}// end onFailure()
					
							/**
							 * 
							 * @param result
							 */
							@Override
							public void onSuccess( VibeRpcResponse response )
							{
								// Did we just save site branding?
								if ( savedBrandingData.isSiteBranding() )
								{
									// Yes
									// Tell the masthead to go get the new site branding.
									m_mastHead.refreshSiteBranding();
								}
								else
								{
									// No
									// Tell the masthead to go get the new binder branding.
									m_mastHead.refreshBinderBranding();
								}
							}
						};
					}
			
					// Issue an ajax request to save the branding data.
					{
						SaveBrandingCmd cmd;
						
						// Issue an ajax request to save the branding data to the db.  rpcSaveCallback will
						// be called when we get the response back.
						savedBrandingData = (GwtBrandingData) obj;
						cmd = new SaveBrandingCmd( savedBrandingData.getBinderId(), savedBrandingData );
						GwtClientHelper.executeCommand( cmd, rpcSaveCallback );
					}

					return true;
				}// end editSuccessful()
			};
		}
		
		// Create a handler that will be called when the user presses the cancel button in the dialog.
		if ( m_editBrandingCancelHandler == null )
		{
			m_editBrandingCancelHandler = new EditCanceledHandler()
			{
				/**
				 * This method gets called when the user presses cancel in the "Edit Branding" dialog.
				 */
				@Override
				public boolean editCanceled()
				{
					return true;
				}// end editCanceled()
			};
		}
		
		// Have we already created an "Edit branding" dialog?
		if ( m_editBrandingDlg == null )
		{
			// No, create one.
			EditBrandingDlg.createDlg(
									false,
									true,
									x,
									y,
									null,
									null,
									m_editBrandingSuccessHandler,
									m_editBrandingCancelHandler,
									new EditBrandingDlgClient()
			{				
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( EditBrandingDlg ebDlg )
				{
					Scheduler.ScheduledCommand cmd;
					
					m_editBrandingDlg = ebDlg;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							editBrandingImpl( brandingData, x, y );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		
		else
		{
			editBrandingImpl( brandingData, x, y );
		}
		
	}// end editBranding()

	
	/**
	 * 
	 */
	private void editBrandingImpl( GwtBrandingData brandingData, int x, int y )
	{
		// Run an async cmd to show the dialog.
		EditBrandingDlg.initAndShow(
								m_editBrandingDlg,
								brandingData,
								new Integer( x ),
								new Integer( y ),
								null,
								null,
								null );
	}

	/*
	 * Use JSNI to grab the JavaScript object that holds the information about the request dealing with.
	 */
	private static native RequestInfo jsGetRequestInfo() /*-{
		// Return a reference to the JavaScript variable called, m_requestInfo.
		return $wnd.m_requestInfo;
	}-*/;
	
	/**
	 * 
	 */
	public boolean getLoginCanCancel()
	{
		return m_requestInfo.getLoginCanCancel();
	}
	
	/**
	 * 
	 */
	public String getLoginInvitationUrl()
	{
		return m_requestInfo.getLoginInvitationUrl();
	}
	
	/**
	 * 
	 */
	public Long getLoginExternalUserId()
	{
		String userId;
		
		userId = m_requestInfo.getLoginExternalUserId();
		if ( userId != null && userId.length() > 0 )
		{
			return new Long( userId );
		}
		
		return null;
	}
	
	/**
	 * 
	 */
	public String getLoginExternalUserName()
	{
		String name;
		
		name = m_requestInfo.getLoginExternalUserName();
		
		return name;
	}
	
	/**
	 * Get the name of the openid provider that can be used in the login dialog
	 */
	public String getLoginOpenIdProviderName()
	{
		return m_requestInfo.getLoginOpenIdProviderName();
	}
	
	/**
	 * Get the url of the openid provider that can be used in the login dialog
	 */
	public String getLoginOpenIdProviderUrl()
	{
		return m_requestInfo.getLoginOpenIdProviderUrl();
	}
	
	/**
	 * Get the login status from the request info
	 */
	private LoginStatus getLoginStatus()
	{
		LoginStatus loginStatus = LoginStatus.PromptForLogin;
		String status;
		
		// errorDetails = m_requestInfo.getLoginError();
		status = m_requestInfo.getLoginStatus();
		if ( status != null )
		{
			if ( status.equalsIgnoreCase( "authenticationFailed" ) )
				loginStatus = LoginStatus.AuthenticationFailed;
			else if ( status.equalsIgnoreCase( "webAccessRestricted" ) )
				loginStatus = LoginStatus.WebAccessRestricted;
			else if ( status.equalsIgnoreCase( "registrationRequired" ) )
				loginStatus = LoginStatus.RegistrationRequired;
			else if ( status.equalsIgnoreCase( "promptForLogin" ) )
				loginStatus = LoginStatus.PromptForLogin;
			else if ( status.equalsIgnoreCase( "promptForPwdReset" ) )
				loginStatus = LoginStatus.PromptForPwdReset;
			else if ( status.equalsIgnoreCase( "pwdResetVerified" ) )
				loginStatus = LoginStatus.PwdResetVerified;
		}
			
		return loginStatus;
	}
	
	/**
	 * This method will handle the landing page options such as "hide the masthead", "hide the sidebar", etc.
	 */
	public void handleLandingPageOptions( final String binderId, final boolean hideMasthead, final boolean hideSidebar, final boolean showBranding, final boolean hideMenu )
	{
		// If we're in File mode, we ignore the landing page options.
		if ( GwtClientHelper.isLicenseFilr() )
			return;
		
		// If we are running in captive mode we never want to show the masthead of sidebar.
		// Are we running in captive mode (GroupWise integration)?
		if ( m_requestInfo.isSessionCaptive() == false )
		{
			boolean showMasthead;

			// No
			// Save the current ui state so we can restore it when the user moves to another page.
			saveUIState();
			
			// Hide or show the sidebar.
			if ( hideSidebar )
			     SidebarHideEvent.fireOne();
			else SidebarShowEvent.fireOne();
			
			// Figure out if we should show the masthead.
			if ( hideMasthead == false || showBranding == true )
				 showMasthead = true;
			else showMasthead = false;
			
			// Hide or show the masthead.
			if ( showMasthead )
			     MastheadShowEvent.fireOne();
			else MastheadHideEvent.fireOne();
			
			// Hide or show the menu
			if ( hideMenu )
			{
				CanModifyBinderCmd cmd;
				
				// Issue a command to see if the user has rights to modify this landing page.
				// If they have rights we will not hide the menu.
				cmd = new CanModifyBinderCmd( binderId );
				GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
				{
					/**
					 * 
					 */
					@Override
					public void onFailure( Throwable t )
					{
						GwtClientHelper.handleGwtRPCFailure(
							t,
							GwtTeaming.getMessages().rpcFailure_CanModifyBinder(),
							binderId );
					}
					
					/**
					 * 
					 */
					@Override
					public void onSuccess( VibeRpcResponse response )
					{
						BooleanRpcResponseData responseData;
						Boolean result;

						// Does the user have rights to modify the landing page?
						responseData = (BooleanRpcResponseData) response.getResponseData();
						result = responseData.getBooleanValue();
						if ( result == Boolean.FALSE )
						{
							// No, go ahead and hide the main menu
							GwtClientHelper.deferCommand( new ScheduledCommand()
							{
								/**
								 * 
								 */
								@Override
								public void execute()
								{
									MenuHideEvent.fireOne();
								}
							} );
						}
					}
				});
			}
			else
				MenuShowEvent.fireOne();
		}
	}// end handleLandingPageOptions()
	
	
	/**
	 * Do the work when we enter activity stream mode.
	 */
	private void handleOnActivityStreamEnter( ActivityStreamDataType ss )
	{
		// Hide any popup entry iframe divs.
		GwtClientHelper.jsHideEntryPopupDiv();
		
		m_activityStreamCtrl.setSize( m_contentLayoutPanel.getOffsetWidth(), m_contentLayoutPanel.getOffsetHeight() );
		m_contentLayoutPanel.showActivityStream( ss );
	}

	/*
	 * This method will handle the given page ui in gwt instead of
	 * having the jsp page do the work.
	 */
	private void handlePageWithGWT( String pageName )
	{
		if ( pageName != null && pageName.length() > 0 )
		{
			if ( pageName.equalsIgnoreCase( "login-page" ) )
				LoginEvent.fireOne();
			else
			{
				Window.alert( "In handlePageWithGWT(), unknown page: " + pageName );
			}
		}
	}// end handlePageWithGWT()
	
	
	/**
	 * This method gets called whenever we determine the session has expired.  We will invoke
	 * the login dialog.
	 */
	public void handleSessionExpired()
	{
		// Invoke the login dialog.
		invokeLoginDlg( false );
	}
	
	/**
	 * Invoke the administration page
	 */
	private void invokeAdminPage()
	{
		AdministrationEvent.fireOne();
	}
	
	/**
	 * Invoke the change password dialog
	 */
	private void invokeChangePasswordDlg()
	{
		// Have we instantiated the change password dialog before?
		if ( null == m_changePwdDlg )
		{
			// No!  Instantiate one now.
			ChangePasswordDlg.createAsync( new ChangePasswordDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ChangePasswordDlg cpDlg )
				{
					Scheduler.ScheduledCommand cmd;
					
					m_changePwdDlg = cpDlg;
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							invokeChangePasswordDlg();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			});
		}
		else
		{
			// Yes, we've instantiated change password dialog already!
			// Simply show it.
			m_changePwdDlg.init();
			m_changePwdDlg.show( true );
		}
	
	}
	
	/**
	 * Invoke the "login" dialog.
	 */
	private void invokeLoginDlg( final boolean allowCancel )
	{
		// Is login allowed through our standard login dialog?  This will be disabled if
		// we are running behind a single-sign on product such as NAM.
		if ( m_requestInfo.isFormLoginAllowed() == false )
		{
			// No
			// Redirect the page to the url that is currently in the address bar.
			// This will let single-sign on do its thing. 
			Window.Location.reload();
			return;
		}

		if ( m_loginDlg == null )
		{
			LoginDlgClient dlgClient;
			String refererUrl;
			
			// Get the url to go to after the user logs in.
			refererUrl = m_requestInfo.getLoginRefererUrl();
			
			dlgClient = new LoginDlgClient()
			{
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( LoginDlg dlg )
				{
					m_loginDlg = dlg;
					invokeLoginDlgImpl( allowCancel );
				}
				
			};
			
			LoginDlg.createAsync(
							false, 
							true, 
							0, 
							0, 
							null,
							m_requestInfo.getLoginUrl(),
							refererUrl,
							dlgClient );
		}
		
		else
		{
			invokeLoginDlgImpl( allowCancel );
		}
	}// end invokeLoginDlg()
	
	/**
	 * 
	 */
	private void invokeLoginDlgImpl( final boolean allowCancel )
	{
		if ( m_loginDlg != null )
		{
			LoginStatus loginStatus;
			
			loginStatus = getLoginStatus();
			
			m_loginDlg.showDlg( allowCancel, loginStatus );
		}
		else
		{
			Window.alert( "In invokeLoginDlgImpl() and m_loginDlg is null" );
		}
	}//end involeLoginDlgImpl()
	
	
	/**
	 * This method is used to invoke the "Share this" dialog from jsp code.
	 */
	private void invokeShareDlg( String entryId )
	{
		EntityId entityId;
		
		if ( entryId == null || entryId.length() == 0 )
			return;
		
		entityId = new EntityId();
		entityId.setEntityId( Long.valueOf( entryId ) );
		entityId.setEntityType( EntityId.FOLDER_ENTRY );
		
		BinderViewsHelper.shareEntity( entityId );
	}
	
	/**
	 * This method is used to invoke the "Tag this" dialog from the old jsp code.
	 */
	private void invokeTagDlg( final String entryId, final String entryTitle, com.google.gwt.dom.client.Element tagPanelElement )
	{
		m_tagPanelElement = tagPanelElement;
		
		final int x = m_contentLayoutPanel.getAbsoluteLeft() + 500;
		final int y = m_contentLayoutPanel.getAbsoluteTop() + 25;

		if ( m_tagThisDlg == null )
		{
			if ( m_editTagsSuccessHandler == null )
			{
				m_editTagsSuccessHandler = new EditSuccessfulHandler()
				{
					/**
					 * This method gets called after the user presses ok in the "Tag This" dialog
					 * and the tags have been written to the db.  We will update the "Tags" tab
					 * with the new list of tags.
					 */
					@SuppressWarnings("unchecked")
					@Override
					public boolean editSuccessful( Object obj )
					{
						if ( obj != null && obj instanceof ArrayList )
						{
							// Replace the current list of tags with the new list of tags.
							// Get the <div> that holds the list of tags.
							if ( m_tagPanelElement != null )
							{
								ArrayList<ArrayList<TagInfo>> tagData;
								ArrayList<TagInfo> personalTags;
								ArrayList<TagInfo> globalTags;
								String tagName;
								AnchorElement anchorElement;
								SpanElement spanElement;
								
								// Remove the names of the previous tags
								m_tagPanelElement.setInnerHTML( "" );
								
								tagData = (ArrayList<ArrayList<TagInfo>>) obj;
								personalTags = tagData.get( 0 );
								globalTags = tagData.get( 1 );
								
								if ( personalTags != null )
								{
									for (TagInfo tagInfo : personalTags)
									{
										tagName = tagInfo.getTagName();
										
										// Create an anchor
										anchorElement = m_tagPanelElement.getOwnerDocument().createAnchorElement();
										anchorElement.setTitle( tagName );
										anchorElement.setAttribute( "onclick", "ss_tagSearchObj(this); return false;" );
										anchorElement.setHref( "javascript:;" );
										
										// Create a span
										spanElement = m_tagPanelElement.getOwnerDocument().createSpanElement();
										spanElement.setTitle( tagName );
										spanElement.setClassName( "ss_muted_cloud_tag" );
										spanElement.setInnerText( tagName );
										
										// Add the <span> to the <a>
										anchorElement.appendChild( spanElement );
										
										// Add the <a> to the <div> that holds all the tag names.
										m_tagPanelElement.appendChild( anchorElement );
									}
								}
								
								if ( globalTags != null )
								{
									for (TagInfo tagInfo : globalTags)
									{
										tagName = tagInfo.getTagName();
										
										// Create an anchor
										anchorElement = m_tagPanelElement.getOwnerDocument().createAnchorElement();
										anchorElement.setTitle( tagName );
										anchorElement.setAttribute( "onclick", "ss_tagSearchObj(this); return false;" );
										anchorElement.setHref( "javascript:;" );
										
										// Create a span
										spanElement = m_tagPanelElement.getOwnerDocument().createSpanElement();
										spanElement.setTitle( tagName );
										spanElement.setClassName( "ss_muted_cloud_tag" );
										spanElement.setInnerText( tagName );
										
										// Add the <span> to the <a>
										anchorElement.appendChild( spanElement );
										
										// Add the <a> to the <div> that holds all the tag names.
										m_tagPanelElement.appendChild( anchorElement );
									}
								}
							}
						}
						return true;
					}
				};
			}

			TagThisDlg.createAsync(
					false,
					true,
					m_editTagsSuccessHandler,
					x,
					y,
					GwtTeaming.getMessages().tagThisEntry(),
					new TagThisDlgClient() {						
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}// end onUnavailable()
				
				@Override
				public void onSuccess( TagThisDlg dlg )
				{
					m_tagThisDlg = dlg;
					invokeTagDlgImpl( entryId, entryTitle, x, y );
				}// end onSuccess()
			} );
		}
		
		else
		{
			invokeTagDlgImpl( entryId, entryTitle, x, y );
		}		
	}// end invokeTagDlg()
	
	private void invokeTagDlgImpl( String entryId, String entryTitle, int x, int y )
	{
		TagThisDlg.initAndShow(
			m_tagThisDlg,
			entryId,
			entryTitle,
			x,
			y );
	}// end invokeTagDlgImpl()
	
	/**
	 * This method gets called when the browser gets resized.
	 * 
	 * Overrides ResizeComposite.onResize()
	 */
	@Override
	public void onResize()
	{
		// Adjust the height and width of the controls on this page.
		super.onResize();
		relayoutPage( false );
	}// end onResize()

	
	/*
	 * Runs the GWT code to delete an entry.
	 */
	private void deleteForumEntry( String folderId, String entryId )
	{
		// Fire a DeleteSelectedEntitiesEvent.
		GwtTeaming.fireEventAsync(
			new DeleteSelectedEntitiesEvent(
				m_contentCtrl.getCurrentBinderInfo().getBinderIdAsLong(),
				new EntityId(
					Long.parseLong( folderId ),
					Long.parseLong( entryId  ),
					EntityId.FOLDER_ENTRY) ) );
	}

	/*
	 * Runs the given URL in the entry viewer.
	 */
	private void viewForumEntry( String url )
	{
		// Fire a ViewForumEntryEvent.
		GwtTeaming.fireEventAsync( new ViewForumEntryEvent( url ) );
	}

	private void viewForumEntry( String url, String isDashboard )
	{
		// Always use the initial form of the method.
		viewForumEntry( url );
	}
	
	/*
	 * This method will be called asynchronously goto a URL,
	 * permalink or otherwise, received as a parameter.
	 */
	private void gotoUrlAsync( final String url, final boolean historyAction )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand() {
			@Override
			public void execute()
			{
				gotoUrlNow( url, historyAction );
			}// end execute()
		} );
	}// end gotoUrlAsync()
	
	private void gotoUrlAsync( final String url )
	{
		gotoUrlAsync( url, false );
	}// end gotoUrlAsync()
	
	private void gotoUrlAsync_FromJSP( final String url )
	{
		// Is the admin control active?
		if ( isAdminActive() )
		{
			// Yes!  This is a hack at best to handle when the JSP
			// based 'Add user' page closes so that we don't pull the
			// rug out from under the admin console and/or the manage
			// users page.  If the manage users page is active, we
			// simply ask it to reload and otherwise ignore the URL.
			// Otherwise, the timer will fire and we'll process the URL
			// as normal.
			
			// Create the timer...
			final Timer timer = new Timer()
			{
				@Override
				public void run()
				{
					// ...if the timer fires, handle the URL as
					// ...normal.
					gotoUrlAsync( url );
				}
			};
			timer.schedule(250);
			
			// Fire an event to detect if the manage users facility
			// is active...
			GwtTeaming.fireEvent(
				new CheckManageUsersActiveEvent(
					new ManageUsersActiveCallback()
					{
						@Override
						public void manageUsersActive( boolean active )
						{
							if (active) {
								// ...and if it is, kill the timer and
								// ...tell the manage users to reload.
								timer.cancel();
								FullUIReloadEvent.fireOneAsync();
							}
						}
					} ) );
		}
		
		else
		{
			// No, the admin control is not active!  Simply process
			// the URL.
			gotoUrlAsync( url );
		}
	}
	
	/*
	 * This method will be called synchronously goto a URL,
	 * permalink or otherwise, received as a parameter.
	 */
	private void gotoUrlNow( final String url, final boolean historyAction )
	{
		// Change the browser's URL.
		OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(
			url,
			Instigator.GOTO_CONTENT_URL );
		
		if ( GwtClientHelper.validateOSBI( osbInfo ) )
		{
			ChangeContextEvent ccEvent = new ChangeContextEvent( osbInfo );
			ccEvent.setHistoryAction( historyAction );
			GwtTeaming.fireEvent( ccEvent );
		}
	}//end gotoUrlNow()
	
	/**
	 * Save the current ui state.
	 */
	private void saveUIState()
	{
		UIState uiState;
		
		uiState = m_uiStateManager.new UIState();
		uiState.setMastheadVisibility( m_mastHead.isVisible() );
		uiState.setSidebarVisibility( m_wsTreeCtrl.isVisible() );
		uiState.setMenuVisibility( m_mainMenuCtrl.isVisible() );
		m_uiStateManager.saveUIState( uiState );
	}

	/**
	 * Handles ActivityStreamEvent's received by this class.
	 * 
	 * Implements the ActivityStreamEvent.Handler.onActivityStream() method.
	 * 
	 * @param event
	 */
	@Override
	public void onActivityStream( ActivityStreamEvent event )
	{
		PersistActivityStreamSelectionCmd cmd;
		
		// Restore the UI state (i.e., sidebar, ...)
		restoreUIState();

		// Put the activity stream text in the tab...
		final ActivityStreamInfo asi = event.getActivityStreamInfo();
		GwtClientHelper.jsSetMainTitle( GwtTeaming.getMessages().whatsNewWithName( asi.getTitle() ) );
		
		// ...and persist this activity stream in the user's profile.
		cmd = new PersistActivityStreamSelectionCmd( asi );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_PersistActivityStreamSelection() );
			}// end onFailure()
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				// Note that we're not doing anything with the results
				// good or bad.  If it fails, so what?  The activity
				// stream will simply not persist for the user.
			}// end onSuccess()
		});
	}//end activityStream()

	/**
	 * Handles ActivityStreamEnterEvent's received by this class.
	 * 
	 * Implements the ActivityStreamEnterEvent.Handler.onActivityStreamEnter() method.
	 * 
	 * @param event
	 */
	@Override
	public void onActivityStreamEnter( ActivityStreamEnterEvent event )
	{
		// If we're not entering the activity stream from the
		// history...
		if ( ! event.isHistoryAction() )
		{
			// ...push it into the history cache.
			HistoryHelper.pushHistoryInfoAsync(
				event.getActivityStreamInfo(),
				event.getShowSetting() );
		}
		
		handleOnActivityStreamEnter( event.getShowSetting() );
	}//end onActivityStreamEnter()


	/**
	 * Handles ActivityStreamEnterExit's received by this class.
	 * 
	 * Implements the ActivityStreamExitEvent.Handler.onActivityStreamExit() method.
	 * 
	 * @param event
	 */
	@Override
	public void onActivityStreamExit( ActivityStreamExitEvent event )
	{
		m_contentLayoutPanel.showContentControl();
		m_contentCtrl.setDimensions( m_contentLayoutPanel.getOffsetWidth(), m_contentLayoutPanel.getOffsetHeight() );
	}//end onActivityStreamExit()

	/**
	 * Handles AdministrationActionEvent's received by this class.
	 * 
	 * Implements the AdministrationActionEvent.Handler.onAdministrationAction() method.
	 * 
	 * @param event
	 */
	@Override
	public void onAdministrationAction( AdministrationActionEvent event )
	{
		// If we're not entering the activity stream from the
		// history...
		GwtAdminAction adminAction = event.getAdminAction();
		if ( ! event.isHistoryAction() )
		{
			// ...push it into the history cache.
			HistoryHelper.pushHistoryInfoAsync( adminAction );
		}

		// Is the administration console currently active?
		if ( !isAdminActive() )
		{
			// No!  Activate it.  Save the current ui state.
			saveUIState();
			
			// Hide any popup entry iframe divs...
			GwtClientHelper.jsHideEntryPopupDiv();
			
			// ...and show the admin control.
			showAdminControl( event );
		}
		
	}
	
	/**
	 * Handles AdministrationEvent's received by this class.
	 * 
	 * Implements the AdministrationEvent.Handler.onAdministration() method.
	 * 
	 * @param event
	 */
	@Override
	public void onAdministration( AdministrationEvent event )
	{
		// If we're not entering the activity stream from the
		// history...
		if ( ! event.isHistoryAction() )
		{
			// ...push it into the history cache.
			HistoryHelper.pushHistoryInfoAsync( (GwtAdminAction) null );
		}
		
		// Save the current ui state
		saveUIState();
		
		// Hide any popup entry iframe divs...
		GwtClientHelper.jsHideEntryPopupDiv();
		
		// ...and show the admin control.
		showAdminControl( null );
	}
	
	/**
	 * Handles AdministrationExitEvent's received by this class.
	 * 
	 * Implements the AdministrationExitEvent.Handler.onAdministrationExit() method.
	 * 
	 * @param event
	 */
	@Override
	public void onAdministrationExit( AdministrationExitEvent event )
	{
		onAdministrationExitAsync();
	}
	
	/*
	 * Asynchronously handles AdministrationExitEvent's received by
	 * this class.
	 */
	private void onAdministrationExitAsync()
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				onAdministrationExitNow();
			}// end execute()
		} );
	}
	
	/*
	 * Synchronously handles AdministrationExitEvent's received by
	 * this class.
	 */
	private void onAdministrationExitNow()
	{
		// Hide the administration console and its menu.
		AdminControl.hideControl( m_adminControl );
		m_mainMenuCtrl.hideAdministrationMenubar();
		
		m_splitLayoutPanel.setVisible( true );

		// If the activity stream was showing show it now.
		if ( isActivityStreamActive() )
		{
			m_contentLayoutPanel.showActivityStream();
		}
		else if ( m_contentCtrl.isVisible() == false )
		{
			m_contentLayoutPanel.showContentControl();
		}
		
		// Restore the ui state to what it was before we opened
		// the site administration.
		restoreUIState();
	}
	
	/**
	 * Handles AdministrationUpgradeCheckEvent's received by this class.
	 * 
	 * Implements the AdministrationUpgradeCheckEvent.Handler.onAdministrationUpgradeCheck() method.
	 * 
	 * @param event
	 */
	@Override
	public void onAdministrationUpgradeCheck( AdministrationUpgradeCheckEvent event )
	{
		// Show a list of upgrade tasks that still need to be
		// performed.
		AdminControl.showUpgradeTasks();
	}// end onAdministrationUpgradeCheck()
	
	/**
	 * Handles BrowseHierarchyEvent's received by this class.
	 * 
	 * Implements the BrowseHierarchyEvent.Handler.onBrowseHierarchy() method.
	 * 
	 * @param event
	 */
	@Override
	public void onBrowseHierarchy( final BrowseHierarchyEvent event )
	{
		// If we're already running a bread crumb browser...
		if (( m_breadCrumbBrowser != null ) && m_breadCrumbBrowser.isShowing() )
		{
			// ...we simply ignore requests to open one.
			return;
		}
		
		WorkspaceTreeControl.createAsync(
				this,
				m_mainPageInfo.getBinderInfo(),
				false,
				TreeMode.HORIZONTAL_POPUP,
				new WorkspaceTreeControlClient() {				
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}// end onUnavailable()
			
			@Override
			public void onSuccess( WorkspaceTreeControl wsTreeCtrl )
			{
				OnBrowseHierarchyInfo bhi;
				WorkspaceTreeControl breadCrumbTree;
				
				// A WorkspaceTreeControl in horizontal mode serves as the
				// bread crumb browser.  Create one...
				breadCrumbTree = wsTreeCtrl;
				breadCrumbTree.addStyleName( "mainBreadCrumb_Tree" );
				m_breadCrumbBrowser = new BreadcrumbTreePopup(true);
				GwtClientHelper.scrollUIForPopup( m_breadCrumbBrowser );
				GwtClientHelper.rollDownPopup(    m_breadCrumbBrowser );
				m_breadCrumbBrowser.addStyleName( "mainBreadCrumb_Browser roundcornerSM-bottom" );
				m_breadCrumbBrowser.setWidget( breadCrumbTree );
				
				// ...position it as per the browse hierarchy request...
				bhi = event.getOnBrowseHierarchyInfo();
				m_breadCrumbBrowser.setPopupPosition(bhi.getLeft(), bhi.getTop());

				// ...and play the opening effect.
				m_breadCrumbBrowser.show();
			}// end onSuccess()
		} );
	}// end onBrowseHierarchy()
	
	/**
	 * Handles ChangeFavoriteStateEvent's received by this class.
	 * 
	 * Implements the ChangeFavoriteStateEvent.Handler.onChangeFavoriteState() method.
	 * 
	 * @param event
	 */
	@Override
	public void onChangeFavoriteState( final ChangeFavoriteStateEvent event )
	{
		ChangeFavoriteStateCmd cmd = new ChangeFavoriteStateCmd( event.getBinderId(), event.getMakeFavorite() );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_ChangeFavoriteState() );
			}//end onFailure()
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				// Force the UI to reload so that the menus, ...
				// get updated to reflect the change.
				GwtTeaming.fireEventAsync( new FullUIReloadEvent() );
			}// end onSuccess()
		});
	}// end onChangeFavoriteState()
	
	/**
	 * Handles ContentChangedEvent's received by this class.
	 * 
	 * Implements the ContentChangedEvent.Handler.onContentChanged() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContentChanged( final ContentChangedEvent event )
	{
		// If the content that changed is not known...
		if ( Change.UNKNOWN.equals( event.getChange() ) )
		{
			// ...simply force the entire UI to refresh.
			FullUIReloadEvent.fireOneAsync();
		}
	}// end onContentChanged()
	
	/**
	 * Handles ContextChangedEvent's received by this class.
	 * 
	 * Implements the ContextChangedEvent.Handler.onContextChanged() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContextChanged( final ContextChangedEvent event )
	{
		// If the event data is valid...
		OnSelectBinderInfo osbInfo = event.getOnSelectBinderInfo();
		if (GwtClientHelper.validateOSBI( osbInfo ))
		{
			// ...put it into effect.
			m_mainPageInfo.setBinderInfo(osbInfo.getBinderInfo());			
		}
	}// end onContextChanged()
	
	/**
	 * Handles ContextChangingEvent's received by this class.
	 * 
	 * Implements the ContextChangingEvent.Handler.onContextChanging() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContextChanging( final ContextChangingEvent event )
	{
		// Restore any ui state that may be saved.
		restoreUIState();
	}// end onContextChanging()
	
	/**
	 * Handles EditCurrentBinderBrandingEvent's received by this class.
	 * 
	 * Implements the EditCurrentBinderBrandingEvent.Handler.onEditCurrentBinderBranding() method.
	 * 
	 * @param event
	 */
	@Override
	public void onEditCurrentBinderBranding( EditCurrentBinderBrandingEvent event )
	{
		GwtBrandingData brandingData;
		
		// Get the branding data the masthead is currently working with.
		brandingData = m_mastHead.getBrandingData();

		editBranding( brandingData, -1, -1 );
	}// end onEditCurrentBinderBranding()
	
	/**
	 * Handles EditPersonalPreferencesEvent's received by this class.
	 * 
	 * Implements the EditPersonalPreferencesEvent.Handler.onEditPersonalPreferences() method.
	 * 
	 * @param event
	 */
	@Override
	public void onEditPersonalPreferences( EditPersonalPreferencesEvent event )
	{
		AsyncCallback<VibeRpcResponse> rpcReadCallback;
		
		// Create a callback that will be called when we get the personal preferences.
		rpcReadCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetPersonalPreferences() );
			}// end onFailure()
	
			/**
			 * We successfully retrieved the user's personal preferences.  Now invoke the "edit personal preferences" dialog.
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				int x;
				int y;
				GwtPersonalPreferences personalPrefs;

				personalPrefs = (GwtPersonalPreferences) response.getResponseData();
				
				// Get the position of the content control.
				x = m_contentLayoutPanel.getAbsoluteLeft();
				y = m_contentLayoutPanel.getAbsoluteTop();
				
				// Create a handler that will be called when the user presses the ok button in the dialog.
				if ( m_editPersonalPrefsSuccessHandler == null )
				{
					m_editPersonalPrefsSuccessHandler = new EditSuccessfulHandler()
					{
						private AsyncCallback<VibeRpcResponse> rpcSaveCallback = null;
						private GwtPersonalPreferences personalPrefs = null;
						
						/**
						 * This method gets called when user user presses ok in the "Personal Preferences" dialog.
						 */
						@Override
						public boolean editSuccessful( Object obj )
						{
							personalPrefs = (GwtPersonalPreferences) obj;
							
							// Create the callback that will be used when we issue an ajax request to save the personal preferences.
							if ( rpcSaveCallback == null )
							{
								rpcSaveCallback = new AsyncCallback<VibeRpcResponse>()
								{
									/**
									 * 
									 */
									@Override
									public void onFailure( Throwable t )
									{
										GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_SavePersonalPreferences() );
									}// end onFailure()
							
									/**
									 * 
									 * @param result
									 */
									@Override
									public void onSuccess( VibeRpcResponse response )
									{
										@SuppressWarnings("unused")
										Boolean result;
										
										result = ((BooleanRpcResponseData) response.getResponseData()).getBooleanValue();
										
										// The personal preferences affect how things are displayed in the content frame.
										// So we need to reload the page in the content frame.
										reloadContentPanel();
										
										// The main page has a javascript variable, ss_userDisplayStyle, that needs to
										// be updated with the current entry display style.
										if ( personalPrefs != null )
										{
											GwtClientHelper.jsSetEntryDisplayStyle( personalPrefs.getDisplayStyle() );
										}

										// If public shares are active,
										// for non-guest internal
										// users...
										if ( personalPrefs.publicSharesActive() && ( ! ( GwtClientHelper.isGuestUser() ) ) && ( ! ( GwtClientHelper.isExternalUser() ) ) )
										{
											// ...fire a public
											// ...collection state
											// ...changed event.
											Boolean hidePublicCollection = personalPrefs.getHidePublicCollection();
											GwtTeaming.fireEventAsync(
												new PublicCollectionStateChangedEvent(
													( ( null != hidePublicCollection ) && hidePublicCollection ) ) );
										}
									}// end onSuccess()
								};
							}
					
							// Issue an ajax request to save the personal preferences.
							{
								SavePersonalPrefsCmd cmd;
								
								// Issue an ajax request to save the personal preferences to the db.  rpcSaveCallback will
								// be called when we get the response back.
								cmd = new SavePersonalPrefsCmd( personalPrefs );
								GwtClientHelper.executeCommand( cmd, rpcSaveCallback );
							}
							
							return true;
						}// end editSuccessful()
					};
				}
				
				// Have we already created a "Personal Preferences" dialog?
				if ( m_personalPrefsDlg == null )
				{
					// No, create one.
					m_personalPrefsDlg = new PersonalPreferencesDlg( m_editPersonalPrefsSuccessHandler, null, false, true, x, y );
				}
				
				m_personalPrefsDlg.init( personalPrefs );
				m_personalPrefsDlg.setPopupPosition( x, y );
				m_personalPrefsDlg.show();
				
			}// end onSuccess()
		};

		// Issue an ajax request to get the personal preferences.  When we get the personal preferences
		// we will invoke the "personal preferences" dialog.
		{
			GetPersonalPrefsCmd cmd;
			
			// Issue an ajax request to get the personal preferences from the db.
			cmd = new GetPersonalPrefsCmd();
			GwtClientHelper.executeCommand( cmd, rpcReadCallback );
		}
	}// end onEditPersonalPreferences()
	
	/**
	 * Handles FullUIReloadEvent's received by this class.
	 * 
	 * Implements the FullUIReloadEvent.Handler.onFullUIReload() method.
	 * 
	 * @param event
	 */
	@Override
	public void onFullUIReload( FullUIReloadEvent event )
	{
		if ( ! isAdminActive() )
		{
			reloadContentPanel();
		}
	}// end onFullUIReload()
	
	/**
	 * Handles GotoContentUrlEvent's received by this class.
	 * 
	 * Implements the GotoContentUrlEvent.Handler.onGotoContentUrl() method.
	 * 
	 * @param event
	 */
	@Override
	public void onGotoContentUrl( GotoContentUrlEvent event )
	{
		gotoUrlAsync( event.getContentUrl() );
	}// end onGotoContentUrl()
	
	/**
	 * Handles GotoMyWorkspaceEvent's received by this class.
	 * 
	 * Implements the GotoMyWorkspaceEvent.Handler.onGotoMyWorkspace() method.
	 * 
	 * @param event
	 */
	@Override
	public void onGotoMyWorkspace( GotoMyWorkspaceEvent event )
	{
		// Yes!  If we're currently running site administration...
		if (isAdminActive()) {
			// ...close it as we won't be in the admin console if
			// ...we change contexts.
			onAdministrationExitNow();
		}			

		m_contentLayoutPanel.showContentControl();
		m_requestInfo.setRerootSidebarTree();
		String myWSUrl = m_requestInfo.getMyWorkspaceUrl();
		if ( GwtClientHelper.isLicenseFilr() || event.isViewProfile() )
		{
			myWSUrl = GwtClientHelper.appendUrlParam( myWSUrl, "operation", "showProfile" );
		}
		gotoUrlAsync( myWSUrl );
	}// end onGotoMyWorkspace()
	
	/**
	 * Handles GotoPermalinkUrlEvent's received by this class.
	 * 
	 * Implements the GotoPermalinkUrlEvent.Handler.onGotoPermalinkUrl() method.
	 * 
	 * @param event
	 */
	@Override
	public void onGotoPermalinkUrl( GotoPermalinkUrlEvent event )
	{
		gotoUrlAsync( event.getPermalinkUrl() );
	}// end onGotoPermalinkUrl()
	
	/**
	 * Handles LoginEvent's received by this class.
	 * 
	 * Implements the LoginEvent.Handler.onLogin() method.
	 * 
	 * @param event
	 */
	@Override
	public void onLogin( LoginEvent event )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute() 
			{
				invokeLoginDlg( true );
			}
		} );
	}// end onLogin()
	
	/**
	 * Handles InvokeAddNewFolderEvent's received by this class.
	 * 
	 * Implements the InvokeAddNewFolderEvent.Handler.onInvokeAddNewFolder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeAddNewFolder( final InvokeAddNewFolderEvent event )
	{
		// Have we instantiated a add new file dialog yet?
		if ( null == m_addNewFolderDlg )
		{
			// No!  Instantiate one now.
			AddNewFolderDlg.createAsync( new AddNewFolderDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess( final AddNewFolderDlg anfDlg )
				{
					// ...and show it.
					m_addNewFolderDlg = anfDlg;
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							showAddNewFolderDlgNow(
								event.getBinderId(),
								event.getFolderTemplateId(),
								event.isAllowCloudFolder() );
						}// end execute()
					} );
				}// end onSuccess()
			});
		}
		
		else
		{
			// Yes, we've instantiated a add new file dialog already!
			// Simply show it.
			showAddNewFolderDlgNow(
				event.getBinderId(),
				event.getFolderTemplateId(),
				event.isAllowCloudFolder() );
		}
	}// end onInvokeAddNewFolder()
	
	/**
	 * Handle the InvokeChangePasswordDlgEvent received by this class
	 * 
	 * Implements the InvokeChangePasswordDlgEvent.Handler.onInvokeChangePasswordDlgEvent() method
	 */
	@Override
	public void onInvokeChangePasswordDlgEvent( InvokeChangePasswordDlgEvent event )
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				invokeChangePasswordDlg();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Handles InvokeDownloadDesktopAppEvent's received by this class.
	 * 
	 * Implements the InvokeDownloadDesktopAppEvent.Handler.onInvokeDownloadDesktopApp() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeDownloadDesktopApp( InvokeDownloadDesktopAppEvent event )
	{
		// Simply run the desktop application download dialog.
		runDesktopAppDownloadDlgAsync();
	}// end onInvokeDownloadDesktopApp()
	
	/*
	 * Asynchronously runs the desktop application download dialog
	 * using the supplied information.
	 */
	private void runDesktopAppDownloadDlgAsync()
	{
		GwtClientHelper.deferCommand( new ScheduledCommand() {
			@Override
			public void execute()
			{
				runDesktopAppDownloadDlgNow();
			}// end execute()
		} );
	}
	
	/*
	 * Synchronously runs the desktop application download dialog using
	 * the supplied information.
	 */
	private void runDesktopAppDownloadDlgNow()
	{
		// Instantiate a desktop application download dialog...
		DesktopAppDownloadDlg.createAsync( new DesktopAppDownloadDlgClient()
		{			
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}// end onUnavailable()
			
			@Override
			public void onSuccess( final DesktopAppDownloadDlg dadDlg )
			{
				// ...and show it.
				GwtClientHelper.deferCommand( new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						DesktopAppDownloadDlg.initAndShow( dadDlg );
					}// end execute()
				} );
			}
		});
	}
	
	/**
	 * Handles InvokeHelpEvent's received by this class.
	 * 
	 * Implements the InvokeHelpEvent.Handler.onInvokeHelp() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeHelp( InvokeHelpEvent event )
	{
		Window.open(
			m_requestInfo.getHelpUrl(),
			"teaming_help_window",
			"resizeable,scrollbars" );
	}// end onInvokeHelp()
	
	/**
	 * Handles InvokeSimpleProfileEvent's received by this class.
	 * 
	 * Implements the InvokeSimpleProfileEvent.Handler.onInvokeSimpleProfile() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeSimpleProfile( InvokeSimpleProfileEvent event )
	{
		SimpleProfileParams params = event.getSimpleProfileParams();
		
		final Element element = params.getElement();
		final String binderId = params.getBinderId();
		final String userName = params.getUserName();
		
		if( ! GwtClientHelper.hasString( binderId ) )
		{
			Window.alert( GwtTeaming.getMessages().qViewErrorWorkspaceDoesNotExist() );
			return;
		}
		
		GetUserWorkspaceInfoCmd cmd = new GetUserWorkspaceInfoCmd( Long.parseLong( binderId ) );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetUserWorkspaceInfo(),
					binderId );
			}//end onFailure()
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				UserWorkspaceInfoRpcResponseData responseData = ((UserWorkspaceInfoRpcResponseData) response.getResponseData());
				if (responseData.canAccessUserWorkspace())
				{
					runQuickViewDlgAsync( binderId, userName, element );
				}
				
				else {
					Long userId = responseData.getUserId();
					if ( null == userId )
					     GwtClientHelper.deferredAlert( GwtTeaming.getMessages().qViewErrorNoUserForQuickView() );
					else runProfileEntryDlgAsync( userId );
				}
			}// end onSuccess()
		});
	}// end onInvokeSimpleProfile()

	/*
	 * Asynchronously runs the profile entry dialog using the supplied
	 * information.
	 */
	private void runProfileEntryDlgAsync( final Long userId )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand() {
			@Override
			public void execute()
			{
				runProfileEntryDlgNow( userId );
			}// end execute()
		} );
	}
	
	/*
	 * Synchronously runs the profile entry dialog using the supplied
	 * information.
	 */
	private void runProfileEntryDlgNow( final Long userId )
	{
		// Instantiate a profile entry dialog...
		ProfileEntryDlg.createAsync( new ProfileEntryDlgClient()
		{			
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}// end onUnavailable()
			
			@Override
			public void onSuccess( final ProfileEntryDlg peDlg )
			{
				// ...and show it.
				GwtClientHelper.deferCommand( new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						ProfileEntryDlg.initAndShow( peDlg, userId );
					}// end execute()
				} );
			}
		});
	}
	
	/*
	 * Asynchronously runs the quick view dialog using the supplied
	 * information.
	 */
	private void runQuickViewDlgAsync( final String binderId, final String userName, final Element element )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand() {
			@Override
			public void execute()
			{
				runQuickViewDlgNow( binderId, userName, element );
			}// end execute()
		} );
	}
	
	/*
	 * Synchronously runs the quick view dialog using the supplied
	 * information.
	 */
	private void runQuickViewDlgNow( final String binderId, final String userName, final Element element )
	{
		GwtQuickViewDlg.createAsync(
				0, 0,
				binderId,
				userName,
				element,
				new GwtQuickViewDlgClient()
		{			
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}// end onUnavailable()
			
			@Override
			public void onSuccess( final GwtQuickViewDlg qvd )
			{
				PopupPanel.PositionCallback posCallback = new PopupPanel.PositionCallback()
				{
					@Override
					public void setPosition(int offsetWidth, int offsetHeight)
					{
						int x;
						int y;
						
						x = (Window.getClientWidth() - offsetWidth) / 2;
						y = (Window.getClientHeight() - offsetHeight) / 3;
						
						qvd.setPopupPosition( x, y );
					}// end setPosition()
				};
				qvd.setPopupPositionAndShow( posCallback );
			}// end onSuccess()
		} );		
	}
	
	/**
	 * Handles LogoutEvent's received by this class.
	 * 
	 * Implements the LogoutEvent.Handler.onLogout() method.
	 * 
	 * @param event
	 */
	@Override
	public void onLogout( LogoutEvent event )
	{
		HistoryHelper.clearHistory();
		GwtClientHelper.jsLogout();
	}// end onLogout()
	
	/**
	 * Handles MastheadHideEvent's received by this class.
	 * 
	 * Implements the MastheadHideEvent.Handler.onMastheadHide() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMastheadHide( MastheadHideEvent event )
	{
		// Force the layout panels to resize as appropriate.
		m_mainPanel.setWidgetSize( m_headerPanel, m_mainMenuCtrl.getOffsetHeight() );
		m_mainPanel.forceLayout();
		
		// Note that we don't have to do anything special here to
		// affect our resize processing.  That just happens because of
		// the workings of the layout panels.
	}// end onMastheadHide()
	
	/**
	 * Handles MastheadShowEvent's received by this class.
	 * 
	 * Implements the MastheadShowEvent.Handler.onMastheadShow() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMastheadShow( MastheadShowEvent event )
	{
		// Is the masthead visible?
		if ( !m_mastHead.isVisible() ) {
			// No!  We need to show it so that we can get it's height
			// below to adjust the panel sizes correctly.  It doesn't
			// appear to be deterministic as to which MastheadShowEvent
			// handler gets called first (this one or the one is
			// Masthead.java.)  If it's this one, the masthead will
			// still be hidden and the getHight() on it returns 0.
			m_mastHead.setVisible( true );
		}
		
		// Force the layout panels to resize as appropriate.
		m_mainPanel.setWidgetSize( m_headerPanel, m_mainMenuCtrl.getOffsetHeight() + m_mastHead.getHeight() );	//GwtConstants.HEADER_HEIGHT );
		m_mainPanel.forceLayout();
		
		// Note that we don't have to do anything special here to
		// affect our resize processing.  That just happens because of
		// the workings of the layout panels.
	}// end onMastheadShow()
	
	/**
	 * Handles MenuHideEvent's received by this class.
	 * 
	 * Implements the MenuHideEvent.Handler.onMenuHide() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMenuHide( MenuHideEvent event )
	{
		// Hide the main menu
		m_mainMenuCtrl.setVisible( false );

		// Because the main menu is hidden we need to relayout the page.
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			/**
			 * 
			 */
			@Override
			public void execute()
			{
				relayoutPage( true );
			}
		} );
	}
	
	/**
	 * Handles MenuShowEvent's received by this class.
	 * 
	 * Implements the MenuShowEvent.Handler.onMenuShow() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMenuShow( MenuShowEvent event )
	{
		// Show the main menu
		m_mainMenuCtrl.setVisible( true );

		// Because the main menu is now visible we need to relayout the page.
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			/**
			 * 
			 */
			@Override
			public void execute()
			{
				relayoutPage( true );
			}
		} );
	}
	
	/**
	 * Handles SearchAdvancedEvent's received by this class.
	 * 
	 * Implements the SearchAdvancedEvent.Handler.onSearchAdvanced() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchAdvanced( SearchAdvancedEvent event )
	{
		String searchUrl = ( m_requestInfo.getAdvancedSearchUrl() + "&binderId=" + m_mainPageInfo.getBinderInfo().getBinderId() );
		gotoUrlAsync( searchUrl );
	}// end onSearchAdvanced()
	
	/**
	 * Handles SearchRecentPlaceEvent's received by this class.
	 * 
	 * Implements the SearchRecentPlaceEvent.Handler.onSearchRecentPlace() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchRecentPlace( SearchRecentPlaceEvent event )
	{
		Integer searchFor = event.getSearchTabId();
		String  searchUrl = ( m_requestInfo.getRecentPlaceSearchUrl() + "&tabId=" + String.valueOf( searchFor.intValue() ) );
		gotoUrlAsync( searchUrl );
	}// end onSearchRecentPlace()
	
	/**
	 * Handles SearchSavedEvent's received by this class.
	 * 
	 * Implements the SearchSavedEvent.Handler.onSearchSaved() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchSaved( SearchSavedEvent event )
	{
		String searchFor = event.getSavedSearchName();
		searchFor = ( ( null == searchFor ) ? "" : GwtClientHelper.jsEncodeURIComponent( searchFor ) );
		String searchUrl = ( m_requestInfo.getSavedSearchUrl() + "&ss_queryName=" + searchFor );
		gotoUrlAsync( searchUrl );
	}// end onSearchSaved()
	
	/**
	 * Handles SearchSimpleEvent's received by this class.
	 * 
	 * Implements the SearchSimpleEvent.Handler.onSearchSimple() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchSimple( SearchSimpleEvent event )
	{
		// What's the view currently loaded in the content panel?
		String searchFor = event.getSimpleSearchString();
		final String baseSearchUrl = ( m_requestInfo.getSimpleSearchUrl() + "&searchText=" + GwtClientHelper.jsEncodeURIComponent( searchFor ) );
		GetCurrentViewInfoEvent gcvi = new GetCurrentViewInfoEvent( new ViewInfoCallback()
		{
			@Override
			public void viewInfo( ViewInfo vi )
			{
				boolean	setContext = false;
				String	searchUrl  = baseSearchUrl;
				switch( vi.getViewType() )
				{
				case BINDER:
				case BINDER_WITH_ENTRY_VIEW:
					BinderInfo bi = vi.getBinderInfo();
					switch( bi.getBinderType() )
					{
					case COLLECTION:
						// A collection!  Add the appropriate context
						// information to the search URL.
						searchUrl  = GwtClientHelper.appendUrlParam( searchUrl, "context",           "collection" );
						searchUrl  = GwtClientHelper.appendUrlParam( searchUrl, "contextCollection", String.valueOf( bi.getCollectionType().ordinal() ) );
						setContext = true;
						break;
						
					case FOLDER:
					case WORKSPACE:
						// A folder or workspace!  Add the appropriate
						// context information to the search URL.
						searchUrl  = GwtClientHelper.appendUrlParam( searchUrl, "context",        "binder");
						searchUrl  = GwtClientHelper.appendUrlParam( searchUrl, "contextBinderId", bi.getBinderId() );
						setContext = true;
						break;
					}
					break;
					
				case FOLDER_ENTRY:
					// A folder entry!  Add the appropriate context
					// information to the search URL.
					ViewFolderEntryInfo	vfei = vi.getFolderEntryInfo();
					EntityId			eid  = vfei.getEntityId();
					searchUrl  = GwtClientHelper.appendUrlParam( searchUrl, "context",        "entry");
					searchUrl  = GwtClientHelper.appendUrlParam( searchUrl, "contextEntryId",  String.valueOf( eid.getEntityId() ) );
					searchUrl  = GwtClientHelper.appendUrlParam( searchUrl, "contextBinderId", String.valueOf( eid.getBinderId() ) );
					setContext = true;
					break;
				}

				// Did we add a context to the search URL?
				if ( ! setContext )
				{
					// No!  Do we have a base binder ID stored in the
					// ViewInfo?
					Long baseBinderId = vi.getBaseBinderId();
					if ( null != baseBinderId )
					{
						// Yes!  Add that as the context.
						searchUrl  = GwtClientHelper.appendUrlParam( searchUrl, "context",        "other");
						searchUrl  = GwtClientHelper.appendUrlParam( searchUrl, "contextBinderId", String.valueOf( baseBinderId ) );
						setContext = true;
					}
				}
				
				// Finally, fire the search URL.
				gotoUrlAsync( searchUrl );
			}
		} );
		GwtTeaming.fireEvent( gcvi );
	}// end onSearchSimple()
	
	/**
	 * Handles SearchTagEvent's received by this class.
	 * 
	 * Implements the SearchTagEvent.Handler.onSearchTag() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchTag( SearchTagEvent event )
	{
		String tagName = event.getTagName();
		tagName = ( ( null == tagName ) ? "" : GwtClientHelper.jsEncodeURIComponent( tagName ) );
		String searchUrl = GwtClientHelper.jsBuildTagSearchUrl( tagName );
		gotoUrlAsync( searchUrl );
	}// end onSearchTag()
	
	
	/**
	 * Handles SetDesktopDownloadAppControlVisibilityEvent's received by this class.
	 * 
	 * Implements the SetDesktopDownloadAppControlVisibilityEvent.Handler.onSetDesktopDownloadAppControlVisibility() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSetDesktopDownloadAppControlVisibility( SetDesktopDownloadAppControlVisibilityEvent event )
	{
		// Is the desktop application downloading enabled?
		if ( m_mainPageInfo.isDesktopAppEnabled() )
		{
			// Yes!  Set the state of the control in the main page
			// information block...
			boolean visible = event.isVisible();
			m_mainPageInfo.setShowDesktopAppDownloader( visible );
			
			// ...and if we have a desktop application download
			// ...control...
			if ( null != m_dadCtrl ) {
				// ...set its visibility and re-layout the page.
				m_splitLayoutPanel.setWidgetHidden( m_dadCtrl, (!visible) );
				m_splitLayoutPanel.forceLayout();
				
				onResize();
			}
		}
	}// end onSetDesktopDownloadAppControlVisibility()
	
	
	/**
	 * Handle the InvokeShareBinderEvents received by this class.
	 */
	@Override
	public void onInvokeShareBinder( InvokeShareBinderEvent event )
	{
		BinderInfoHelper.getBinderInfo( event.getBinderId(), new BinderInfoCallback()
		{
			@Override
			public void onFailure()
			{
				// Nothing to do!  The user will already have been
				// told about the problem.
			}// end onFailure()

			@Override
			public void onSuccess( final BinderInfo binderInfo )
			{
				GwtClientHelper.deferCommand( new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						String eidType;
						switch ( binderInfo.getBinderType() )
						{
						case FOLDER:     eidType = EntityId.FOLDER;    break;
						case WORKSPACE:  eidType = EntityId.WORKSPACE; break;
						default:         eidType = "";                 break;
						}
						BinderViewsHelper.shareEntity(
							new EntityId(
								binderInfo.getParentBinderIdAsLong(),
								binderInfo.getBinderIdAsLong(),
								eidType ) );
					}// end execute()
				} );
			}// end onSuccess()
		} );
	}
	
	/**
	 * Handles ShowCollectionEvent's received by this class.
	 * 
	 * Implements the ShowCollectionEvent.Handler.onShowCollection() method.
	 * 
	 */
	@Override
	public void onShowCollection( ShowCollectionEvent event )
	{
		CollectionType collectionType;
		
		collectionType = event.getCollectionType();
		
		// Do we have the url for the collection points?
		if ( m_collectionPointData == null )
		{
			// No, issue an rpc request to get the url.
			getCollectionPointDataAndShowCollectionPoint( collectionType );
		}
		else
		{
			String url = null;

			// Yes
			url = m_collectionPointData.getUrl( collectionType );
			if ( url != null )
				gotoUrlAsync( url );
		}
	}

	/**
	 * Handles ShowContentControlEvent's received by this class.
	 * 
	 * Implements the ShowContentControlEvent.Handler.onShowContentControl() method.
	 * 
	 */
	@Override
	public void onShowContentControl( ShowContentControlEvent event )
	{
		// Display the content control
		m_contentLayoutPanel.showContentControl();
	}
	
	/**
	 * Handles SidebarHideEvent's received by this class.
	 * 
	 * Implements the SidebarHideEvent.Handler.onSidebarHide() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSidebarHide( SidebarHideEvent event )
	{
		m_splitLayoutPanel.setWidgetSize( m_wsTreeCtrl, GwtConstants.PANEL_PADDING );
		m_splitLayoutPanel.forceLayout();
		
		onResize();
	}// end onSidebarHide()
	
	/**
	 * Handles SidebarShowEvent's received by this class.
	 * 
	 * Implements the SidebarShowEvent.Handler.onSidebarShow() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSidebarShow( SidebarShowEvent event )
	{
		m_splitLayoutPanel.setWidgetSize( m_wsTreeCtrl, GwtConstants.SIDEBAR_TREE_WIDTH );
		m_splitLayoutPanel.forceLayout();
		
		onResize();
	}// end onSidebarShow()
	
	/**
	 * Handles TrackCurrentBinderEvent's received by this class.
	 * 
	 * Implements the TrackCurrentBinderEvent.Handler.onTrackCurrentBinder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTrackCurrentBinder( TrackCurrentBinderEvent event )
	{
		TrackBinderCmd cmd;
		final boolean forceUIReload;
		
		forceUIReload = event.getForceUIReload();
		
		cmd = new TrackBinderCmd( m_mainPageInfo.getBinderInfo().getBinderId() );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_TrackingBinder(),
					m_mainPageInfo.getBinderInfo().getBinderId() );
			}//end onFailure()
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				// It's overkill to force a full context reload, which
				// this does, but it's the only way right now to ensure
				// the What's New tab and other information gets fully
				// refreshed.
				if ( forceUIReload )
					FullUIReloadEvent.fireOne();
			}// end onSuccess()
		});
	}// end onTrackCurrentBinder()
	
	/**
	 * Handles SizeChangedEvent's received by this class.
	 * 
	 * Implements the SizeChangedEvent.Handler.onSizeChanged() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSizeChanged( SizeChangedEvent event )
	{
		// Adjust the height and width of the controls on this page.
		relayoutPage( false );
	}// end onSizeChanged()
	
	/**
	 * Handles UntrackCurrentBinderEvent's received by this class.
	 * 
	 * Implements the UntrackCurrentBinderEvent.Handler.onUntrackCurrentBinder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onUntrackCurrentBinder( UntrackCurrentBinderEvent event )
	{
		UntrackBinderCmd cmd;
		
		cmd = new UntrackBinderCmd( m_mainPageInfo.getBinderInfo().getBinderId() );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_UntrackingBinder(),
					m_mainPageInfo.getBinderInfo().getBinderId() );
			}//end onFailure()
			
			@Override
			public void onSuccess( VibeRpcResponse respnse )
			{
				// It's overkill to force a full context reload, which
				// this does, but it's the only way right now to ensure
				// the What's New tab and other information gets fully
				// refreshed.
				FullUIReloadEvent.fireOne();
			}// end onSuccess()
		});
	}// end onUntrackCurrentBinder()
	
	/**
	 * Handles ViewCurrentBinderTeamMembersEvent's received by this class.
	 * 
	 * Implements the ViewCurrentBinderTeamMembersEvent.Handler.onViewCurrentBinderTeamMembers() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewCurrentBinderTeamMembers( ViewCurrentBinderTeamMembersEvent event )
	{
		GetBinderPermalinkCmd cmd;
		
		cmd = new GetBinderPermalinkCmd( m_mainPageInfo.getBinderInfo().getBinderId() );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t ) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
					m_mainPageInfo.getBinderInfo().getBinderId() );
			}//end onFailure()
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				String binderUrl;
				StringRpcResponseData responseData;

				responseData = (StringRpcResponseData) response.getResponseData();
				binderUrl = responseData.getStringValue();
				
				OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(
					m_mainPageInfo.getBinderInfo(),
					binderUrl,
					Instigator.VIEW_TEAM_MEMBERS );
				if ( GwtClientHelper.validateOSBI( osbInfo ) )
				{
					GwtTeaming.fireEvent( new ChangeContextEvent( osbInfo ));
				}
			}// end onSuccess()
		});// end AsyncCallback()
	}// end onViewCurrentBinderTeamMembers()
	
	/**
	 * Handles ViewResourceLibraryEvent's received by this class.
	 * 
	 * Implements the ViewResourceLibraryEvent.Handler.onViewResourceLibrary() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewResourceLibrary( ViewResourceLibraryEvent event )
	{
		Window.open( "http://www.novell.com/products/vibe-onprem/resource-library/", "teaming_resource_library_window", "resizeable,scrollbars" );
	}// end onViewResourceLibrary()
	
	/**
	 * Handles ViewTeamingFeedEvent's received by this class.
	 * 
	 * Implements the ViewTeamingFeedEvent.Handler.onViewTeamingFeed() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewTeamingFeed( ViewTeamingFeedEvent event )
	{
		String teamingFeedUrl;
		
		teamingFeedUrl = m_requestInfo.getTeamingFeedUrl();
		Window.open( teamingFeedUrl, "_teaming_feed", "width=500,height=700,resizable,scrollbars" );
	}// end onViewTeamingFeed()
	
	/**
	 * Handles UntrackCurrentPersonEvent's received by this class.
	 * 
	 * Implements the UntrackCurrentPersonEvent.Handler.onUntrackCurrentPerson() method.
	 * 
	 * @param event
	 */
	@Override
	public void onUntrackCurrentPerson( UntrackCurrentPersonEvent event )
	{
		UntrackPersonCmd cmd;
		
		cmd = new UntrackPersonCmd( m_mainPageInfo.getBinderInfo().getBinderId() );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_TrackingPerson(),
					m_mainPageInfo.getBinderInfo().getBinderId() );
			}//end onFailure()
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				// It's overkill to force a full context reload, which
				// this does, but it's the only way right now to ensure
				// the What's New tab and other information gets fully
				// refreshed.
				FullUIReloadEvent.fireOne();
			}// end onSuccess()
		});
	}// end onUntrackCurrentPerson()
	
	/**
	 * Adjust the height and width of the controls on this page.  Currently the only
	 * control we adjust is the ContentControl.
	 */
	public void relayoutPage( boolean layoutImmediately )
	{
		// Are we supposed to relayout now?
		if ( layoutImmediately == true )
		{
			// Adjust the size of the north panel that holds the masthead and menu bar
			{
				int panelHeight;
				
				panelHeight = 0;
				
				if ( m_mastHead != null && m_mastHead.isVisible() )
					panelHeight += m_mastHead.getOffsetHeight();
				
				if ( m_mainMenuCtrl != null && m_mainMenuCtrl.isVisible() )
					panelHeight += m_mainMenuCtrl.getOffsetHeight();

				m_mainPanel.setWidgetSize( m_headerPanel, panelHeight );
			}
			
			// Are we in administration mode?
			if ( isAdminActive() ) {
				Scheduler.ScheduledCommand cmd;
				
				// Yes
				// Reposition the admin control
				{
					int top;

					// Is the main menu visible?
					if ( m_mainMenuCtrl.isVisible() )
					{
						// Yes, position the admin control relative to the main menu control.
						top = m_mainMenuCtrl.getAbsoluteTop() + m_mainMenuCtrl.getOffsetHeight();
					}
					else
					{
						// No, position the admin control relative to the masthead
						top = m_adminControl.getAbsoluteTop() + m_adminControl.getOffsetHeight();
					}

					top = m_headerPanel.getAbsoluteTop() + m_headerPanel.getOffsetHeight();
					m_adminControl.getElement().getStyle().setTop( top, Unit.PX );
				}

				// Tell the admin console to relayout.
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						AdminControl.relayoutPage( m_adminControl );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
			else
			{
				int width;
				int height;

				// No, we aren't in administration mode!  layout the
				// non-admin content.
				width  = m_contentLayoutPanel.getOffsetWidth();
				height = m_contentLayoutPanel.getOffsetHeight();
				
				// Yes
				if ( m_contentCtrl != null )
					m_contentCtrl.setDimensions( width, height );
				
				if ( isActivityStreamActive() )
					m_activityStreamCtrl.setSize( width, height );
				
				// Do we have a workspace tree control?
				if ( null != m_wsTreeCtrl )
				{
					// Yes
					m_wsTreeCtrl.relayoutPageAsync();
				}
				
				// Set the size and position of the entry popup div.
				GwtClientHelper.jsSetEntryPopupIframeSize();
			}
		}
		else
		{
			GwtClientHelper.deferCommand( new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					relayoutPage( true );
				}
			} );
		}
	}// end relayoutPage()
	
	public void relayoutPage()
	{
		relayoutPage( false );
	}// end relayoutPage()

	
	/**
	 * Reload the page currently displayed in the content panel.
	 */
	public void reloadContentPanel()
	{
		// Is the Activity Stream visible?
		if ( m_activityStreamCtrl != null && m_activityStreamCtrl.isVisible() )
		{
			ActivityStreamInfo asi;
			
			// Yes
			// Get the activity stream currently being used.
			asi = m_activityStreamCtrl.getActivityStreamInfo();
			
			// Refresh the activity stream.
			GwtTeaming.fireEvent( new ActivityStreamEvent( asi ) );
		}
		else
		{
			m_contentCtrl.reload();
		}
	}// end reloadContentPanel()
	
	/**
	 * Restore the previous ui state.
	 */
	private void restoreUIState()
	{
		m_uiStateManager.restoreUIState();
	}
	
	/**
	 * Returns true if the activity stream control is active and
	 * visible and false otherwise.
	 * 
	 * @return
	 */
	public boolean isActivityStreamActive()
	{
		return ((null != m_wsTreeCtrl) && m_wsTreeCtrl.isInActivityStreamMode());
	}// end isActivityStreamActive()

	/**
	 * Returns true if the administration control is active and visible
	 * and false otherwise.
	 * 
	 * @return
	 */
	public boolean isAdminActive()
	{
		return ( ( null != m_adminControl ) && m_adminControl.isShowing() );
	}// end isAdminActive()

	/**
	 * Returns true if the control key is currently pressed and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isControlKeyDown()
	{
		return m_controlKeyDown;
	}
	
	/**
	 * Returns true if we currently processing search results and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isInSearch()
	{
		return m_inSearch;
	}// end isInSearch()
	
	/**
	 * Return whether the user is logged in.
	 */
	public boolean isUserLoggedIn()
	{
		return m_requestInfo.isUserLoggedIn();
	}
	
	private void showAdminControl( final VibeEventBase<?> fireOnLoad )
	{
		// If we've already load the admin control...
		if ( null != m_adminControl ) {
			// ...simply show it.
			showAdminControlImpl( fireOnLoad );
		}
		
		else
		{
			// ...otherwise, we load its split point...
			AdminControl.createDlg(
					this,
					new AdminControlClient() {				
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}// end onUnavailable()
				
				@Override
				public void onSuccess( AdminControl adminCtrl )
				{
					m_adminControl = adminCtrl;
					
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// ...and then show it.
							showAdminControlImpl( fireOnLoad );
						}// end execute()
					} );
				}// end onSuccess()
			} );
		}
	}// end showAdminControl()
	
	private void showAdminControlImpl( final VibeEventBase<?> fireOnLoad )
	{
		// Hide everything on the menu, the workspace tree control and the content control.
		m_mainMenuCtrl.showAdministrationMenubar();
		
		m_splitLayoutPanel.setVisible( false );
		
		// Is the main menu visible?
		if ( m_mainMenuCtrl.isVisible() )
		{
			// Yes, position the admin control relative to the main menu control.
			AdminControl.showControl( m_adminControl, m_mainMenuCtrl, fireOnLoad );
		}
		else
		{
			// No, position the admin control relative to the masthead
			AdminControl.showControl( m_adminControl, m_headerPanel, fireOnLoad );
		}
	}// end showAdminControlImpl()
	
	/*
	 * Fires a ContextChangingEvent from the JSP based UI.
	 */
	private void fireContextChanging()
	{
		ContextChangingEvent.fireOne();
	}// end fireContextChanging()

	/*
	 * Fires a FilesDroppedEvent from JSP.
	 */
	private void fireFilesDropped( String binderId )
	{
		GwtTeaming.fireEvent(new FilesDroppedEvent( Long.parseLong( binderId )));
	}// end fireFilesDropped()
	
	/*
	 * Fires a GotoContentUrlEvent from the JSP based UI.
	 */
	private void fireGotoContentUrl( String url )
	{
		if ( isAdminActive() )
		{
			FullUIReloadEvent.fireOne();
		}
		else
		{
			fireContextChanging();
			GwtTeaming.fireEvent(new GotoContentUrlEvent(url));
		}
	}// end fireGotoContentUrl()
		
	/*
	 * Fires an InvokeSimpleProfileEvent from the JSP based UI.
	 */
	private void fireInvokeSimpleProfile( Element element, String binderId, String userName )
	{
		SimpleProfileParams params = new SimpleProfileParams( element, binderId, userName );
		GwtTeaming.fireEvent( new InvokeSimpleProfileEvent( params ) );
	}// end fireInvokeSimpleProfile()

	/*
	 * Fires an arbitrary simple Vibe event from the JSP based UI.
	 */
	private void fireSimpleVibeEvent( TeamingEvents eventEnum )
	{
		EventHelper.fireSimpleEvent( eventEnum );
	}// end fireSimpleVibeEvent()

	/**
	 * ?
	 */
	public void resetMenuContext()
	{
		m_mainMenuCtrl.resetContext();
	}// end resetMenuContext()
	
	/**
	 * ?
	 * 
	 * @param selectedBinderInfo
	 * @param inSearch
	 * @param searchTabId
	 */
	public void setMenuContext( BinderInfo selectedBinderInfo, boolean inSearch, String searchTabId )
	{
		m_mainMenuCtrl.setContext(selectedBinderInfo, inSearch, searchTabId);
	}// end setMenuContext()
	
	/*
	 * Synchronously shows the add new file dialog.
	 */
	private void showAddNewFolderDlgNow(Long binderId, Long folderTemplateId, boolean allowCloudFolder)
	{
		AddNewFolderDlg.initAndShow( m_addNewFolderDlg, binderId, folderTemplateId, allowCloudFolder );
	}// end showAddNewFolderDlgNow()
	
	/**
	 * Callback interface to interact with the main page asynchronously
	 * after it loads. 
	 */
	public interface GwtMainPageClient {
		void onSuccess(GwtMainPage mainPage);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the GwtMainPage and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
		// Prefetch parameters.  true -> Prefetch only.  false -> Something else.
		final GwtMainPageClient mainPageClient,
		final boolean prefetch )
	{		
		loadControl1(
			// Prefetch parameters.
			mainPageClient,
			prefetch );
	}// end doAsyncOperation()
	
	/*
	 * Various control loaders used to load the split points containing
	 * the code for the controls by the GwtMainPage.
	 * 
	 * Loads the split point for the GwtMainPage.
	 */
	private static void loadControl1(
		// Prefetch parameters.  true -> Prefetch only.  false -> Something else.
		final GwtMainPageClient mainPageClient,
		final boolean prefetch )
	{		
		GWT.runAsync( GwtMainPage.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				initMainPage_Finish(
					// Prefetch parameters.
					mainPageClient,
					prefetch );
			}// end onSuccess()
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_MainPage() );
				mainPageClient.onUnavailable();
			}// end onFailure()
		} );
	}// end doAsyncOperation()
		
	/*
	 * Finishes the initialization of the GwtMainPage object.
	 */
	private static void initMainPage_Finish(
		// Prefetch parameters.  true -> Prefetch only.  false -> Something else.
		final GwtMainPageClient mainPageClient,
		final boolean prefetch )
	{		
		if (prefetch)
		{
			mainPageClient.onSuccess( null );
		}
		
		else
		{
			GwtMainPage newMainPage = new GwtMainPage();
			mainPageClient.onSuccess( newMainPage );
		}
	}// end initMainPage_Finish()
			
	/**
	 * Loads the GwtMainPage split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param mainPageClient
	 */
	public static void createAsync( final GwtMainPageClient mainPageClient )
	{
		doAsyncOperation(
			// Prefetch parameters.  false -> Not a prefetch.  
			mainPageClient,
			false );
	}// end createAsync()	

	/**
	 * Causes the split point for the GwtMainPage to be fetched.
	 * 
	 * @param mainPageClient
	 */
	public static void prefetch ( GwtMainPageClient mainPageClient )
	{
		// If we weren't given a GwtMainPageClient...
		if ( null == mainPageClient )
		{
			// ...create one we can use.
			mainPageClient = new GwtMainPageClient()
			{				
				@Override
				public void onUnavailable()
				{
					// Unused.
				}// end onUnavailable()
				
				@Override
				public void onSuccess( GwtMainPage mainPage )
				{
					// Unused.
				}// end onSuccess()
			};
		}
		
		doAsyncOperation(
			// Prefetch parameters.  true -> Prefetch only.  
			mainPageClient,
			true );
	}// end prefetch()
	
	public static void prefetch()
	{
		// Always use the initial form of the method.
		prefetch(null);
	}// end prefetch()
}// end GwtMainPage
