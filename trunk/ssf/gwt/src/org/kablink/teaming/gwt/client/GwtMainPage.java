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

package org.kablink.teaming.gwt.client;

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.UIStateManager.UIState;
import org.kablink.teaming.gwt.client.event.ActivityStreamEnterEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamEvent;
import org.kablink.teaming.gwt.client.event.AdministrationEvent;
import org.kablink.teaming.gwt.client.event.AdministrationExitEvent;
import org.kablink.teaming.gwt.client.event.BrowseHierarchyEvent;
import org.kablink.teaming.gwt.client.event.ContextChangedEvent;
import org.kablink.teaming.gwt.client.event.ContextChangingEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.LoginEvent;
import org.kablink.teaming.gwt.client.event.LogoutEvent;
import org.kablink.teaming.gwt.client.event.MastheadHideEvent;
import org.kablink.teaming.gwt.client.event.MastheadShowEvent;
import org.kablink.teaming.gwt.client.event.SearchRecentPlaceEvent;
import org.kablink.teaming.gwt.client.event.SidebarHideEvent;
import org.kablink.teaming.gwt.client.event.SidebarShowEvent;
import org.kablink.teaming.gwt.client.event.TeamingActionEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.profile.widgets.GwtQuickViewDlg;
import org.kablink.teaming.gwt.client.profile.widgets.GwtQuickViewDlg.GwtQuickViewDlgClient;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetPersonalPrefsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveBrandingCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SavePersonalPrefsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.OnBrowseHierarchyInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.SimpleProfileParams;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.VibeProduct;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.ActivityStreamCtrlClient;
import org.kablink.teaming.gwt.client.widgets.AdminControl;
import org.kablink.teaming.gwt.client.widgets.AdminControl.AdminControlClient;
import org.kablink.teaming.gwt.client.widgets.ContentControl;
import org.kablink.teaming.gwt.client.widgets.ContentControl.ContentControlClient;
import org.kablink.teaming.gwt.client.widgets.EditBrandingDlg;
import org.kablink.teaming.gwt.client.widgets.EditBrandingDlg.EditBrandingDlgClient;
import org.kablink.teaming.gwt.client.widgets.LoginDlg;
import org.kablink.teaming.gwt.client.widgets.LoginDlg.LoginDlgClient;
import org.kablink.teaming.gwt.client.widgets.MainMenuControl;
import org.kablink.teaming.gwt.client.widgets.MainMenuControl.MainMenuControlClient;
import org.kablink.teaming.gwt.client.widgets.MastHead;
import org.kablink.teaming.gwt.client.widgets.PersonalPreferencesDlg;
import org.kablink.teaming.gwt.client.widgets.TagThisDlg;
import org.kablink.teaming.gwt.client.widgets.TagThisDlg.TagThisDlgClient;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl.TreeMode;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl.WorkspaceTreeControlClient;
import org.kablink.teaming.gwt.client.workspacetree.BreadcrumbTreePopup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.Event;


/**
 * This widget will display the main Teaming page
 */
public class GwtMainPage extends Composite
	implements ActionHandler, ResizeHandler,
	// EventBus handlers implemented by this class.
		TeamingActionEvent.Handler,
		ActivityStreamEvent.Handler,
		ActivityStreamEnterEvent.Handler,
		AdministrationEvent.Handler,
		AdministrationExitEvent.Handler,
		BrowseHierarchyEvent.Handler,
		ContextChangedEvent.Handler,
		ContextChangingEvent.Handler,
		FullUIReloadEvent.Handler,
		LoginEvent.Handler,
		LogoutEvent.Handler,
		MastheadHideEvent.Handler,
		MastheadShowEvent.Handler,
		SearchRecentPlaceEvent.Handler,
		SidebarHideEvent.Handler,
		SidebarShowEvent.Handler
{
	public static boolean m_novellTeaming = true;
	public static RequestInfo m_requestInfo;
	public static ContentControl m_contentCtrl;

	private boolean m_inSearch = false;
	private String m_searchTabId = "";
	private EditBrandingDlg m_editBrandingDlg = null;
	private PersonalPreferencesDlg m_personalPrefsDlg = null;
	private LoginDlg m_loginDlg = null;
	private TagThisDlg m_tagThisDlg = null;
	private EditCanceledHandler m_editBrandingCancelHandler = null;
	private EditSuccessfulHandler m_editBrandingSuccessHandler = null;
	private EditSuccessfulHandler m_editPersonalPrefsSuccessHandler = null;
	private EditSuccessfulHandler m_editTagsSuccessHandler = null;
	private FlowPanel m_contentPanel;
	private FlowPanel m_teamingRootPanel;
	private MainMenuControl m_mainMenuCtrl;
	private MastHead m_mastHead;
	private AdminControl m_adminControl = null;
	private BreadcrumbTreePopup m_breadCrumbBrowser;
	private String m_selectedBinderId;
	private WorkspaceTreeControl m_wsTreeCtrl;
	private UIStateManager m_uiStateManager;
	private ActivityStreamCtrl m_activityStreamCtrl = null;

	private com.google.gwt.dom.client.Element m_tagPanelElement;

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		// Event whose pay load is one of our original TeamingActions.
		TeamingEvents.TEAMING_ACTION,
		
		// Activity stream events.
		TeamingEvents.ACTIVITY_STREAM,
		TeamingEvents.ACTIVITY_STREAM_ENTER,
		
		// Administration events.
		TeamingEvents.ADMINISTRATION,
		TeamingEvents.ADMINISTRATION_EXIT,
		
		// Miscellaneous events.
		TeamingEvents.BROWSE_HIERARCHY,
		TeamingEvents.FULL_UI_RELOAD,

		// Context events.
		TeamingEvents.CONTEXT_CHANGED,
		TeamingEvents.CONTEXT_CHANGING,

		// Login/out events.
		TeamingEvents.LOGIN,
		TeamingEvents.LOGOUT,
		
		// Masthead events.
		TeamingEvents.MASTHEAD_HIDE,
		TeamingEvents.MASTHEAD_SHOW,

		// Search events.
		TeamingEvents.SEARCH_RECENT_PLACE,
		
		// Sidebar events.
		TeamingEvents.SIDEBAR_HIDE,
		TeamingEvents.SIDEBAR_SHOW,
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
		constructMainPage_Start();
		
		// All composites must call initWidget() in their constructors.
		initWidget( m_teamingRootPanel );
	}
	
	/*
	 * Various control loaders used to load the split points containing
	 * the code for the controls in the main page.
	 * 
	 * Loads the split point for the MainMenuControl and instantiates
	 * an object from it.
	 */
	private void loadMainMenuControl()
	{
		MainMenuControl.createAsync( this, new MainMenuControlClient()
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
				ScheduledCommand loadNextControl = new ScheduledCommand() {
					@Override
					public void execute()
					{
						loadWorkspaceTreeControl();
					}// end execute()
				};
				Scheduler.get().scheduleDeferred( loadNextControl );
			}// end onSuccess()
		} );
	}// end loadMainMenuControl()

	/*
	 * Loads the split point for the WorkspaceTreeControl and
	 * instantiates an object from it.
	 */
	private void loadWorkspaceTreeControl()
	{
		WorkspaceTreeControl.createAsync( this, m_selectedBinderId, TreeMode.VERTICAL, new WorkspaceTreeControlClient()
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
				ScheduledCommand loadNextControl = new ScheduledCommand() {
					@Override
					public void execute()
					{
						loadContentControl();
					}// end execute()
				};
				Scheduler.get().scheduleDeferred( loadNextControl );
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
				ScheduledCommand loadNextControl = new ScheduledCommand() {
					@Override
					public void execute()
					{
						ActivityStreamCtrl();
					}// end execute()
				};
				Scheduler.get().scheduleDeferred( loadNextControl );
			}// end onSuccess()
		} );
	}// end loadContentControl()

	/*
	 * Loads the split point for the ActivityStreamCtrl and
	 * instantiates an object from it.
	 */
	private void ActivityStreamCtrl()
	{
		ActivityStreamCtrl.createAsync( this, new ActivityStreamCtrlClient()
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
				constructMainPage_Finish();
			}// end onSuccess()
		} );
	}// end ActivityStreamCtrl()
	
	/*
	 * Starts the initializations of the main page.
	 */
	private void constructMainPage_Start()
	{
		// Get information about the request we are dealing with.
		m_requestInfo = getRequestInfo();
		m_selectedBinderId = m_requestInfo.getBinderId();
		if ( ! ( GwtClientHelper.hasString( m_selectedBinderId ) ) )
		{
			m_selectedBinderId = m_requestInfo.getCurrentUserWorkspaceId();
			if ( ! ( GwtClientHelper.hasString( m_selectedBinderId ) ) )
			{
				m_selectedBinderId = m_requestInfo.getTopWSId();
			}
		}
		m_novellTeaming = m_requestInfo.isNovellTeaming();
		
		m_teamingRootPanel = new FlowPanel();
		m_teamingRootPanel.addStyleName( "mainTeamingPagePanel" );

		ScheduledCommand loadControls = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				loadMainMenuControl();
			}// end execute()
		};
		Scheduler.get().scheduleDeferred( loadControls );		
	}// end constructMainPage_Start();
	
	/*
	 * Finishes the initialization of the main page.
	 */
	private void constructMainPage_Finish()
	{
		Element bodyElement;
		String url;

		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this);
		
		// Initialize the context load handler used by the JSP based
		// UI to tell the GWT UI that a context has been loaded.
		initContextLoadHandlerJS(this);
		
		// Initialize the pre context switch handler used by the
		// JSP based UI to tell the GWT UI that a context switch is
		// about to occur.
		initFireContextChangingJS(this);
		
		// Initialize the JavaScript function that gets called when we want to handle a page using
		// GWT instead of in jsp.
		// For example, we never want the jsp login page to be loaded in the content control.
		initHandlePageWithGWTJS( this );
		
		// Initialize the JavaScript function that gets called when we want to close the
		// administration content panel.
		initCloseAdministrationContentPanelJS( this );
		
		// Initialize the native JavaScript to allow any content to fire an event on the main event bus for the application
		// See GwtClientHelper:jsFireEvent
		initFireEventOnEventBusJS( this );
		
		// Initialize JavaScript to perform Popup for User Profile
		initSimpleUserProfileJS( this );
		
		// Initialize JavaScript that handles the landing page options
		initHandleLandingPageOptionsJS( this );
		
		// Initialize the JavaScript that calls the login dialog.
		initInvokeLoginDlgJS( this );
		
		// Initialize the JavaScript that invokes the Tag dialog
		initInvokeTagDlgJS( this );
		
		// Initialize the JavaScript that invokes the admin page.
		initInvokeAdminPageJS( this );
		
		// Create a UIStateManager that we will use to save/restore the ui state.
		m_uiStateManager = new UIStateManager();
		
		// Set the class name on the <body> element to "mainGwtTeamingPage"
		bodyElement = RootPanel.getBodyElement();
		bodyElement.setClassName( "mainTeamingPage" );
		
		// Add the MastHead to the page.
		m_mastHead = new MastHead( m_requestInfo );
		m_teamingRootPanel.add( m_mastHead );

		// Is there an error message to be displayed?
		final String errMsg = m_requestInfo.getErrMsg();
		if ( GwtClientHelper.hasString( errMsg ) )
		{
			// Yes
			// Execute a deferred command the will display it.
			ScheduledCommand cmd = new ScheduledCommand()
			{
				public void execute()
				{
					// Is the user logged in?
					if ( m_requestInfo.isUserLoggedIn() )
					{
						// Yes
						// Take the user to their workspace.
						handleActionImpl( TeamingAction.MY_WORKSPACE, null );
					}
					
					// Tell the user.  We do this as a deferred command
					// so that the UI can continue to render while the
					// message box is displayed.
					Window.alert( errMsg );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
		
		// Add the main menu to the page.
		m_teamingRootPanel.add( m_mainMenuCtrl );
		
		// Create a panel to hold the WorkspaceTree control and the content control
		m_contentPanel = new FlowPanel();
		m_contentPanel.addStyleName( "mainContentPanel" );
		
		// Create the WorkspaceTree control.
		m_wsTreeCtrl.addStyleName( "mainWorkspaceTreeControl" );
		m_contentPanel.add( m_wsTreeCtrl );
		
		// Create the content control.
		m_contentCtrl.addStyleName( "mainContentControl" );
		m_contentPanel.add( m_contentCtrl );
		
		// Create an activity stream control.
		m_activityStreamCtrl.hide();
		m_contentPanel.add( m_activityStreamCtrl );
		
		// Do we have a url we should set the ContentControl to?
		url = m_requestInfo.getAdaptedUrl();
		if ( url != null && url.length() > 0 )
		{
			// Yes
			m_contentCtrl.setUrl( m_requestInfo.getAdaptedUrl() + "&captive=true" );
		}
		
		m_teamingRootPanel.add( m_contentPanel );
		
		// Add a ResizeHandler to the browser so we'll know when the user resizes the browser.
		Window.addResizeHandler( this );
		
		// Is the user logged in?
		if ( m_requestInfo.isUserLoggedIn() == false )
		{
			// No
			// Should we invoke the login dialog?
			if ( m_requestInfo.promptForLogin() == true )
			{
				// Yes
				// Hide the workspace tree control and the menu bar.
				m_wsTreeCtrl.setVisible( false );
				m_mainMenuCtrl.setVisible( false );
				
				// invoke the login dialog.
				ScheduledCommand cmd = new ScheduledCommand()
				{
					public void execute()
					{
						invokeLoginDlg( false );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		}
				
		// If we're running GroupWise integrations or otherwise require
		// session captive mode...
		if ((VibeProduct.GW == m_requestInfo.getVibeProduct()) || m_requestInfo.isSessionCaptive())
		{
			// ...we hide the masthead and sidebar by default.
			GwtTeaming.fireEvent( new MastheadHideEvent( false ) );	// false -> Done resize the content now...
			GwtTeaming.fireEvent( new SidebarHideEvent(  false ) );	// ...will happen when the frame has loaded.
			
			// Have the masthead hide the logout link
			m_mastHead.hideLogoutLink();
		}
	}// end constructMainPage_Finish()

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
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::gotoContentUrl(Ljava/lang/String;)( url );
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
	 * Called to create a JavaScript method that will be invoked from a page that holds a landing page.
	 * There are options in the landing page settings to hide the masthead, hide the sidebar, etc.
	 * This method will show/hide controls based on these settings.
	 */
	private native void initHandleLandingPageOptionsJS( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_handleLandingPageOptions = function( hideMasthead, hideSidebar, showBranding )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::handleLandingPageOptions(ZZZ)( hideMasthead, hideSidebar, showBranding );
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
	 * instantiated in the GWTMainPage to be able fire a event on the EventBus to notify any listeners.
	 * 
	 * com.google.web.bindery.event.shared.Event
	 */
	private native void initFireEventOnEventBusJS( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_fireEvent = function( event )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::fireVibeEvent(Lcom/google/web/bindery/event/shared/Event;)( event );
		}//end ss_fireEvent
	}-*/;

	/*
	 * Invoke the Simple User Profile or Quick View
	 */
	private native void initSimpleUserProfileJS( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_invokeSimpleProfile = function( element, binderId, userName )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::invokeSimpleProfile(Lcom/google/gwt/user/client/Element;Ljava/lang/String;Ljava/lang/String;)( element, binderId, userName );
		}//end ss_invokeSimpleProfile
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
	 * Copies the <title> text from the GWT content IFRAME to the
	 * main GWT page's <title>.
	 */
	private static native void jsFixupGwtMainTitle() /*-{
		$wnd.top.document.title = $wnd.top.gwtContentIframe.document.title;
	}-*/;
	
	/**
	 * This method will close the administration content panel.
	 */
	private void closeAdministrationContentPanel()
	{
		if ( m_adminControl != null )
		{
			m_adminControl.hideContentPanel();
		}
	}// end closeAdministrationContentPanel()
	
	/*
	 * Puts a context change from the JSP based UI into effect.
	 */
	private void contextLoaded( String binderId, String inSearch, String searchTabId ) {
		contextLoaded(
			binderId,
			Instigator.CONTENT_AREA_CHANGED,
			((null != inSearch) && Boolean.parseBoolean( inSearch )),
			searchTabId );
	}
	
	private void contextLoaded( String binderId, Instigator instigator ) {
		contextLoaded( binderId, instigator, false, "" );
	}
	
	private void contextLoaded( String binderId, final Instigator instigator, boolean inSearch, String searchTabId )
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
			m_mainMenuCtrl.setContext( binderId, inSearch, searchTabId );			
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
			m_activityStreamCtrl.hide();
		}
		
		m_inSearch    = inSearch;
		m_searchTabId = searchTabId;
		
		jsFixupGwtMainTitle();
		
		final boolean forceSidebarReload = m_requestInfo.forceSidebarReload();
		if (forceSidebarReload) {
			m_requestInfo.clearSidebarReload();
		}

		// If we're in a search panel, we always show the root
		// workspace in the sidebar tree.  That's the way it worked
		// in the JSP based UI so I kept that functionality intact.
		final String contextBinderId;
		if      ( m_inSearch )                            contextBinderId = m_selectedBinderId; // Teaming 2.x equivalent would have been:  m_requestInfo.getTopWSId();
		else if ( GwtClientHelper.hasString( binderId ) ) contextBinderId = binderId;
		else                                              contextBinderId = m_selectedBinderId;
		
		GwtTeaming.getRpcService().getBinderPermalink( HttpRequestInfo.createHttpRequestInfo(), contextBinderId, new AsyncCallback<String>()
		{
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
					contextBinderId );
			}//end onFailure()
			
			public void onSuccess( String binderPermalink )
			{
				OnSelectBinderInfo osbInfo;
				osbInfo = new OnSelectBinderInfo(
					contextBinderId,
					binderPermalink,
					false,	// false -> Not trash.
					instigator );
				if (GwtClientHelper.validateOSBI(osbInfo))
				{
					if (forceSidebarReload) {
						osbInfo.setForceSidebarReload(forceSidebarReload);
					}
					GwtTeaming.fireEvent( new ContextChangedEvent( osbInfo ) );
				}
			}// end onSuccess()
		});
	}// end contextLoaded()

	/*
	 * Called to handle TeamingAction's directed to the GWT task folder
	 * listing.
	 */
	private void handleTaskAction( TeamingAction taskAction, Object obj )
	{
		Window.alert(GwtTeaming.getMessages().taskInternalError_UnexpectedAction(taskAction.toString()));
	}// end handleTaskAction()
	
	/*
	 * Invoke the "Edit Branding" dialog.
	 */
	private void editBranding( GwtBrandingData brandingDataIn )
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
			brandingDataIn.setBinderId( m_selectedBinderId );
		}
		
		final GwtBrandingData brandingData = brandingDataIn;
		
		// Get the position of the content control.
		xCalc = m_contentCtrl.getAbsoluteLeft();
		if ( xCalc < 75 )
			xCalc = 75;
		yCalc = m_contentCtrl.getAbsoluteTop();
		if ( yCalc < 75 )
			yCalc = 75;
		
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
			EditBrandingDlg.createAsync(
					m_editBrandingSuccessHandler,
					m_editBrandingCancelHandler,
					false,
					true,
					x,
					y,
					new EditBrandingDlgClient() {				
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}// end onUnavailable()
				
				@Override
				public void onSuccess( EditBrandingDlg ebDlg )
				{
					m_editBrandingDlg = ebDlg;
					editBrandingImpl( brandingData, x, y );
				}// end onSuccess()
			} );
		}
		
		else
		{
			editBrandingImpl( brandingData, x, y );
		}
		
		
	}// end editBranding()
	
	private void editBrandingImpl( GwtBrandingData brandingData, int x, int y ) {
		m_editBrandingDlg.init( brandingData );
		m_editBrandingDlg.setPopupPosition( x, y );
		m_editBrandingDlg.show();
	}

	
	/**
	 * Invoke the "Edit Personal Preferences" dialog.
	 */
	private void editPersonalPreferences()
	{
		AsyncCallback<VibeRpcResponse> rpcReadCallback;
		
		// Create a callback that will be called when we get the personal preferences.
		rpcReadCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetPersonalPreferences() );
			}// end onFailure()
	
			/**
			 * We successfully retrieved the user's personal preferences.  Now invoke the "edit personal preferences" dialog.
			 */
			public void onSuccess( VibeRpcResponse response )
			{
				int x;
				int y;
				GwtPersonalPreferences personalPrefs;

				personalPrefs = (GwtPersonalPreferences) response.getResponseData();
				
				// Get the position of the content control.
				x = m_contentCtrl.getAbsoluteLeft();
				y = m_contentCtrl.getAbsoluteTop();
				
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
									public void onSuccess( VibeRpcResponse response )
									{
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
	}// end editPersonalPreferences()

	/**
	 * Use JSNI to grab the JavaScript object that holds the information about the request dealing with.
	 */
	public native RequestInfo getRequestInfo() /*-{
		// Return a reference to the JavaScript variable called, m_requestInfo.
		return $wnd.m_requestInfo;
	}-*/;
	
	/**
	 * Handle the action that was requested by the user somewhere in the main page.
	 * For example, the user clicked on "My Workspace" in the masthead.
	 * 
	 * Implements ActionHandler.handleActionImpl()
	 * 
	 * @param action
	 * @param obj
	 */
	public void handleAction( TeamingAction action, Object obj )
	{
		handleAction( this, action, obj );
	}// end handleAction()
	
	/*
	 * Handles actions requested by the user somewhere.
	 */
	private void handleActionImpl( TeamingAction action, Object obj )
	{
		switch (action)
		{
		case EDIT_BRANDING:
			GwtBrandingData brandingData;
			
			// Get the branding data the masthead is currently working with.
			brandingData = m_mastHead.getBrandingData();
			
			editBranding( brandingData );
			break;
			
		case EDIT_PERSONAL_PREFERENCES:
			editPersonalPreferences();
			break;
			
		case EDIT_SITE_BRANDING:
			GwtBrandingData siteBrandingData;
			
			siteBrandingData = m_mastHead.getSiteBrandingData();
			editBranding( siteBrandingData );
			break;
			
		case HELP:
			String url;
			
			url = m_requestInfo.getHelpUrl();
			Window.open( url, "teaming_help_window", "resizeable,scrollbars" );
			break;

		case MY_WORKSPACE:
			// If we're currently running site administration...
			if ( isAdminActive() )
			{
				// ...close it first.
				GwtTeaming.fireEvent( new AdministrationExitEvent() );
			}
			
			// Change the browser's URL.
			EventHelper.fireActivityStreamExit();
			EventHelper.fireContextChanging();
			gotoUrl( m_requestInfo.getMyWorkspaceUrl() );
			break;
			
		case SIZE_CHANGED:
			sizeChanged( obj );
			break;
			
		case VIEW_TEAM_MEMBERS:
			viewTeamMembers();
			break;
			
		case GOTO_CONTENT_URL:
			EventHelper.fireContextChanging();
			gotoUrl( obj );
			break;

		case GOTO_PERMALINK_URL:
			EventHelper.fireContextChanging();
			gotoUrl( obj, false );
			break;

		case TRACK_BINDER:
			trackCurrentBinder();
			break;
			
		case UNTRACK_BINDER:
			untrackCurrentBinder();
			break;
			
		case UNTRACK_PERSON:
			untrackCurrentPerson();
			break;
			
		case SIMPLE_SEARCH:
			EventHelper.fireActivityStreamExit();
			EventHelper.fireContextChanging();
			simpleSearch( obj );
			break;
			
		case ADVANCED_SEARCH:
			EventHelper.fireActivityStreamExit();
			EventHelper.fireContextChanging();
			advancedSearch();
			break;
			
		case SAVED_SEARCH:
			EventHelper.fireActivityStreamExit();
			EventHelper.fireContextChanging();
			savedSearch( obj );
			break;
			
		case RELOAD_LEFT_NAVIGATION:
			reloadLeftNavigation();
			break;
			
		case TAG_SEARCH:
			EventHelper.fireContextChanging();
			tagSearch( obj );
			break;
			
		case TEAMING_FEED:
			String teamingFeedUrl;
			
			teamingFeedUrl = m_requestInfo.getTeamingFeedUrl();
			Window.open( teamingFeedUrl, "_teaming_feed", "width=500,height=700,resizable,scrollbars" );
			break;
			
		case INVOKE_SIMPLE_PROFILE:
			if ( obj instanceof SimpleProfileParams )
			{
				SimpleProfileParams params;
				
				params = (SimpleProfileParams) obj;
				invokeSimpleProfile( params.getElement(), params.getBinderId(), params.getUserName() );
			}
			else
			{
				Window.alert( "In handleActionImpl( INVOKE_SIMPLE_PROFILE, obj ) obj is not a SimpleProfileParams object." );
			}
			break;
			
		case VIEW_FOLDER_ENTRY:
			if ( obj instanceof String )
			{
				viewFolderEntry( (String) obj );
			}
			else
			{
				Window.alert( "In handleActionImpl( VIEW_FOLDER_ENTRY, obj ) obj is not a String object" );
			}
			break;
			
		case SHOW_FORUM_ENTRY:
			if ( obj instanceof String )
			{
				GwtClientHelper.jsShowForumEntry( (String) obj );
			}
			else
			{
				Window.alert( "In handleActionImpl( SHOW_FORUM_ENTRY, obj ) obj is not a String object" );
			}
			break;
			
		case SHOW_RESOURCE_LIBRARY:
			Window.open( "http://www.novell.com/products/vibe-onprem/resource-library/", "teaming_resource_library_window", "resizeable,scrollbars" );
			break;

		case TASK_DELETE:
		case TASK_MOVE_DOWN:
		case TASK_MOVE_LEFT:
		case TASK_MOVE_RIGHT:
		case TASK_MOVE_UP:
		case TASK_NEW_TASK:
		case TASK_PURGE:
		case TASK_QUICK_FILTER:
		case TASK_SET_PERCENT_DONE:
		case TASK_SET_PRIORITY:
		case TASK_SET_STATUS:
		case TASK_VIEW:
			handleTaskAction( action, obj );
			break;

		case UNDEFINED:
		default:
			Window.alert( "Unknown action selected: " + action.getUnlocalizedDesc() );
			break;
		}
	}// end handleActionImpl()
	
	
	/**
	 * This method will handle the landing page options such as "hide the masthead", "hide the sidebar", etc.
	 */
	private void handleLandingPageOptions( boolean hideMasthead, boolean hideSidebar, boolean showBranding )
	{
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
			     GwtTeaming.fireEvent( new SidebarHideEvent() );
			else GwtTeaming.fireEvent( new SidebarShowEvent() );
			
			// Figure out if we should show the masthead.
			if ( hideMasthead == false || showBranding == true )
				 showMasthead = true;
			else showMasthead = false;
			
			// Hide or show the masthead.
			if ( showMasthead )
			     GwtTeaming.fireEvent( new MastheadShowEvent() );
			else GwtTeaming.fireEvent( new MastheadHideEvent() );
		}
	}// end handleLandingPageOptions()
	

	/*
	 * This method will handle the given page ui in gwt instead of
	 * having the jsp page do the work.
	 */
	private void handlePageWithGWT( String pageName )
	{
		if ( pageName != null && pageName.length() > 0 )
		{
			if ( pageName.equalsIgnoreCase( "login-page" ) )
				GwtTeaming.fireEvent( new LoginEvent() );
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
		GwtTeaming.fireEvent( new AdministrationEvent() );
	}
	
	
	/**
	 * Invoke the "login" dialog.
	 */
	private void invokeLoginDlg( final boolean allowCancel )
	{
		if ( m_loginDlg == null )
		{
			String refererUrl;
			
			// Get the url to go to after the user logs in.
			refererUrl = m_requestInfo.getLoginRefererUrl();
			
			// Create the login dialog.
			LoginDlg.createAsync(
				false,
				true,
				0,
				0,
				null,
				m_requestInfo.getLoginUrl(),
				refererUrl,
				new LoginDlgClient()
				{					
					@Override
					public void onUnavailable()
					{
						// Nothing to do.  Error handled in
						// asynchronous provider.
					}// end onUnavailable()
					
					@Override
					public void onSuccess( LoginDlg dlg )
					{
						m_loginDlg = dlg;
						invokeLoginDlgImpl( allowCancel );
					}// end onSuccess()
				} );
		}
		
		else
		{
			invokeLoginDlgImpl( allowCancel );
		}
	}// end invokeLoginDlg()
	
	private void invokeLoginDlgImpl( final boolean allowCancel )
	{
		String loginErr = m_requestInfo.getLoginError();
		boolean showLoginFailedMsg = ( loginErr != null && loginErr.length() > 0 );
		
		LoginDlg.initAndShow( m_loginDlg, allowCancel, showLoginFailedMsg );
	}//end involeLoginDlgImpl()
	
	
	/**
	 * This method is used to invoke the "Tag this" dialog from the old jsp code.
	 */
	private void invokeTagDlg( final String entryId, final String entryTitle, com.google.gwt.dom.client.Element tagPanelElement )
	{
		m_tagPanelElement = tagPanelElement;
		
		final int x = m_contentCtrl.getAbsoluteLeft() + 500;
		final int y = m_contentCtrl.getAbsoluteTop() + 25;

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
	 */
	public void onResize( ResizeEvent event )
	{
		// Adjust the height and width of the controls on this page.
		relayoutPage( false );
	}// end onResize()
	
	
	/**
	 * This method will be called when one of the controls on this page changes size.
	 */
	private void sizeChanged( Object obj )
	{
		// Adjust the height and width of the controls on this page.
		relayoutPage( false );
	}// end sizeChanged()

	/*
	 * Called to view the membership of the currently selected binder.
	 * 
	 * Implements the VIEW_TEAM_MEMBERS teaming action.
	 */
	private void viewTeamMembers()
	{
		GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();
		rpcService.getBinderPermalink( HttpRequestInfo.createHttpRequestInfo(), m_selectedBinderId, new AsyncCallback<String>()
		{
			public void onFailure( Throwable t ) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
					m_selectedBinderId );
			}//end onFailure()
			
			public void onSuccess( String binderUrl )
			{
				OnSelectBinderInfo osbInfo;
				
				binderUrl = GwtClientHelper.appendUrlParam( binderUrl, "operation", "show_team_members" );
				osbInfo = new OnSelectBinderInfo( m_selectedBinderId, binderUrl, false, Instigator.VIEW_TEAM_MEMBERS );
				if (GwtClientHelper.validateOSBI( osbInfo ))
				{
					GwtTeaming.fireEvent( new ContextChangedEvent( osbInfo ) );
				}
			}// end onSuccess()
		});// end AsyncCallback()
	}// end viewTeamMembers()
	
	
	/**
	 * 
	 */
	private native void viewFolderEntry( String url ) /*-{
		if ( $wnd.ss_showForumEntry !== undefined )
			$wnd.ss_showForumEntry( url, 'no' );
		else
			alert( 'ss_showForumEntry() is undefined' );
	}-*/;


	/**
	 * 
	 */
	private void gotoContentUrl( String url )
	{
		EventHelper.fireActivityStreamExit();
		EventHelper.fireContextChanging();
		gotoUrl( url, true );
	}
	
	
	/*
	 * This method will be called to goto a URL, permalink or
	 * otherwise, received as a parameter.
	 * 
	 * Implements the GOTO_CONTENT_URL, GOTO_PERMALINK_URL and
	 * MY_WORKSPACE teaming actions.
	 */
	private void gotoUrl( Object obj )
	{
		// Default to submitting the URL to the content frame.
		gotoUrl( obj, true );
	}//end gotoUrl()
	
	private void gotoUrl( Object obj, boolean submitToContentFrame )
	{
		if ( obj instanceof String )
		{
			String url = ((String) obj);
			if (submitToContentFrame)
				 GwtClientHelper.loadUrlInContentFrame( url );
			else Window.Location.replace(                 url );
		}
		else
		{
			Window.alert( "in gotoUrl() and obj is not a String object" );
		}
	}//end gotoUrl()

	/*
	 * This method will be called to track the current binder.
	 * 
	 * Implements the TRACK_BINDER teaming action.
	 */
	private void trackCurrentBinder() {
		GwtTeaming.getRpcService().trackBinder( HttpRequestInfo.createHttpRequestInfo(), m_selectedBinderId, new AsyncCallback<Boolean>()
		{
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_TrackingBinder(),
					m_selectedBinderId );
			}//end onFailure()
			
			public void onSuccess( Boolean success )
			{
				// It's overkill to force a full context reload, which
				// this does, but it's the only way right now to ensure
				// the What's New tab and other information gets fully
				// refreshed.
				EventHelper.fireFullUIReload();
			}// end onSuccess()
		});
	}
	
	/*
	 * This method will be called to remove the tracking on the current
	 * binder.
	 * 
	 * Implements the UNTRACK_BINDER teaming action.
	 */
	private void untrackCurrentBinder() {
		GwtTeaming.getRpcService().untrackBinder( HttpRequestInfo.createHttpRequestInfo(), m_selectedBinderId, new AsyncCallback<Boolean>()
		{
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_UntrackingBinder(),
					m_selectedBinderId );
			}//end onFailure()
			
			public void onSuccess( Boolean success )
			{
				// It's overkill to force a full context reload, which
				// this does, but it's the only way right now to ensure
				// the What's New tab and other information gets fully
				// refreshed.
				EventHelper.fireFullUIReload();
			}// end onSuccess()
		});
	}
	
	/*
	 * This method will be called to remove the tracking on the person
	 * whose workspace is the current binder.
	 * 
	 * Implements the UNTRACK_PERSON teaming action.
	 */
	private void untrackCurrentPerson() {
		GwtTeaming.getRpcService().untrackPerson( HttpRequestInfo.createHttpRequestInfo(), m_selectedBinderId, new AsyncCallback<Boolean>()
		{
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_TrackingPerson(),
					m_selectedBinderId );
			}//end onFailure()
			
			public void onSuccess( Boolean success )
			{
				// It's overkill to force a full context reload, which
				// this does, but it's the only way right now to ensure
				// the What's New tab and other information gets fully
				// refreshed.
				EventHelper.fireFullUIReload();
			}// end onSuccess()
		});
	}
	
	/*
	 * This method will be called to perform a simple search on a
	 * string received as a parameter.
	 * 
	 * Implements the SIMPLE_SEARCH teaming action.
	 */
	private void simpleSearch( Object obj )
	{
		if ( ( null == obj ) || ( obj instanceof String ))
		{
			// What are we searching for?
			String searchFor = ((null == obj ) ? "" : ((String) obj));
			if (GwtClientHelper.jsHasSimpleSearchForm()) {
				GwtClientHelper.jsInvokeSimpleSearch( searchFor );
			}
			
			else {
				String searchUrl = (m_requestInfo.getSimpleSearchUrl() + "&searchText=" + GwtClientHelper.jsEncodeURIComponent( searchFor ));
				GwtClientHelper.loadUrlInContentFrame( searchUrl );
			}
		}
		else
			Window.alert( "in simpleSearch() and obj is not a String object" );
	}//end simpleSearch()

	/*
	 * This method will be called to run advanced search in the content
	 * panel.
	 * 
	 * Implements the ADVANCED_SEARCH teaming action.
	 */
	private void advancedSearch()
	{
		String searchUrl = (m_requestInfo.getAdvancedSearchUrl() + "&binderId=" + m_selectedBinderId);
		GwtClientHelper.loadUrlInContentFrame(searchUrl);
	}//end advancedSearch()
	
	/*
	 * This method will be called to perform a saved search on a
	 * string received as a parameter.
	 * 
	 * Implements the SAVED_SEARCH teaming action.
	 */
	private void savedSearch( Object obj )
	{
		if ( ( null == obj ) || ( obj instanceof String ))
		{
			String searchFor;

			// What's the name of the saved search?
			searchFor = ((null == obj ) ? "" : GwtClientHelper.jsEncodeURIComponent((String) obj));
			String searchUrl = (m_requestInfo.getSavedSearchUrl() + "&ss_queryName=" + searchFor);
			GwtClientHelper.loadUrlInContentFrame(searchUrl);
		}
		else
			Window.alert( "in savedSearch() and obj is not a String object" );
	}//end savedSearch()

	/**
	 * Save the current ui state.
	 */
	private void saveUIState()
	{
		UIState uiState;
		
		uiState = m_uiStateManager.new UIState();
		uiState.setMastheadVisibility( m_mastHead.isVisible() );
		uiState.setSidebarVisibility( m_wsTreeCtrl.isVisible() );
		m_uiStateManager.saveUIState( uiState );
	}

	/*
	 * Forces the workspace tree to reload itself.
	 * 
	 * Implements the RELOAD_LEFT_NAVIGATION teaming action.
	 */
	private void reloadLeftNavigation()
	{
		contextLoaded(m_selectedBinderId, Instigator.FORCE_SIDEBAR_RELOAD);
	}// end reloadLeftNavigation()

	/*
	 * This method will be called to perform a search on a tag name
	 * received as a parameter.
	 * 
	 * Implements the TAG_SEARCH teaming action.
	 */
	private void tagSearch( Object obj )
	{
		if ( ( null == obj ) || ( obj instanceof String ))
		{
			String tagName;

			// What's the tag to be searched?
			tagName = ((null == obj ) ? "" : GwtClientHelper.jsEncodeURIComponent((String) obj));
			String searchUrl = GwtClientHelper.jsBuildTagSearchUrl(tagName);
			GwtClientHelper.loadUrlInContentFrame(searchUrl);
		}
		else
			Window.alert( "in tagSearch() and obj is not a String object" );
	}//end tagSearch()

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
		// Restore the UI state (i.e., sidebar, ...)
		restoreUIState();

		// Put the activity stream text in the tab...
		final ActivityStreamInfo asi = event.getActivityStreamInfo();
		GwtClientHelper.jsSetMainTitle( GwtTeaming.getMessages().whatsNewWithName( asi.getTitle() ) );
		
		// ...and persist this activity stream in the user's profile.
		GwtTeaming.getRpcService().persistActivityStreamSelection( HttpRequestInfo.createHttpRequestInfo(), asi, new AsyncCallback<Boolean>()
		{
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_PersistActivityStreamSelection() );
			}// end onFailure()
			
			public void onSuccess( Boolean   result )
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
	public void onActivityStreamEnter( ActivityStreamEnterEvent event )
	{
		// Hide any popup entry iframe divs.
		GwtClientHelper.jsHideEntryPopupDiv();
	}//end enterActivityStreamMode()

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
		// Save the current ui state
		saveUIState();
		
		// Hide any popup entry iframe divs...
		GwtClientHelper.jsHideEntryPopupDiv();
		
		// ...and show the admin control.
		showAdminControl();
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
		ScheduledCommand cmd = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				// Restore the ui state to what it was before we opened
				// the site administration.
				restoreUIState();
				relayoutPage( true );
			}// end execute()
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
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
				m_selectedBinderId,
				TreeMode.HORIZONTAL,
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
			m_selectedBinderId = osbInfo.getBinderId().toString();
			
			EventHelper.fireActivityStreamExit();
			EventHelper.fireContextChanging();
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
	 * Handles FullUIReloadEvent's received by this class.
	 * 
	 * Implements the FullUIReloadEvent.Handler.onFullUIReload() method.
	 * 
	 * @param event
	 */
	@Override
	public void onFullUIReload( FullUIReloadEvent event )
	{
		EventHelper.fireFullUIReload();
	}// end onFullUIReload()
	
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
		invokeLoginDlg( true );
	}// end onLogin()
	
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
		if ( event.getResizeContentImmediately() )
		{
			relayoutPage( true );
		}
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
		relayoutPage( true );
	}// end onMastheadShow()
	
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
		// Tell everybody that a context switch is about to happen...
		EventHelper.fireContextChanging();

		// ...and perform the search.
		Integer searchFor = event.getSearchTabId();
		String searchUrl = (m_requestInfo.getRecentPlaceSearchUrl() + "&tabId=" + String.valueOf(searchFor.intValue()));
		GwtClientHelper.loadUrlInContentFrame(searchUrl);
	}// end onSearchRecentPlace()
	
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
		if ( event.getResizeContentImmediately() )
		{
			relayoutPage( true );
		}
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
		relayoutPage( true );
	}// end onSidebarShow()
	
	/**
	 * Adjust the height and width of the controls on this page.  Currently the only
	 * control we adjust is the ContentControl.
	 */
	public void relayoutPage( boolean layoutImmediately )
	{
		int width;
		int height;

		// Are we supposed to relayout now?
		if ( layoutImmediately == true )
		{
			// Yes
			// Calculate how wide the ContentControl should be.
			{
				int clientWidth;
				
				// Get the width of the browser window's client area.
				clientWidth = Window.getClientWidth();
				
				width = clientWidth - m_contentCtrl.getAbsoluteLeft() - 10; 
			}
			
			// Calculate how high the ContentControl should be.
			{
				int clientHeight;
				
				// Get the height of the browser window's client area.
				clientHeight = Window.getClientHeight();
				
				height = clientHeight - m_contentPanel.getAbsoluteTop() - 20;
			}
			
			m_contentCtrl.setDimensions( width, height );
			
			// Tell the activity stream control to relayout.
			if ( m_activityStreamCtrl != null )
				m_activityStreamCtrl.setSize( width, height );
			
			// Do we have an Administration control?
			if ( m_adminControl != null )
			{
				// Yes
				m_adminControl.relayoutPage();
			}
			
			// Do we have a workspace tree control?
			if ( null != m_wsTreeCtrl )
			{
				// Yes
				m_wsTreeCtrl.relayoutPageAsync();
			}
			
			// Set the size and position of the entry popup div.
			GwtClientHelper.jsSetEntryPopupIframeSize();
		}
		else
		{
			ScheduledCommand cmd = new ScheduledCommand()
			{
				public void execute()
				{
					relayoutPage( true );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
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
		m_contentCtrl.reload();
	}// end reloadContentPanel()
	
	/**
	 * Restore the previous ui state.
	 */
	private void restoreUIState()
	{
		m_uiStateManager.restoreUIState();
	}
	
	/*
	 * Invoke the Simple Profile Dialog
	 */
	private void invokeSimpleProfile(Element element, String binderId, String userName ) {

		if(!GwtClientHelper.hasString(binderId)) {
			Window.alert(GwtTeaming.getMessages().qViewErrorWorkspaceDoesNotExist());
			return;
		}
		
		GwtQuickViewDlg.createAsync(
				false,
				true,
				0,
				0,
				binderId,
				userName,
				element,
				new GwtQuickViewDlgClient() {			
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

	/*
	 * Returns defaultValue if actionParam is null or not a Boolean.
	 * Otherwise, returns the boolean value.
	 */
	private static boolean getBooleanActionParam( Object actionParam, boolean defaultValue )
	{
		boolean reply;
		if ( ( null != actionParam ) && ( actionParam instanceof Boolean ))
		     reply = ((Boolean) actionParam).booleanValue();
		else reply = defaultValue;
		return reply;
	}// end getBooleanActionParam()
	
	/**
	 * Returns true if the administration control is active and visible
	 * and false otherwise.
	 * 
	 * @return
	 */
	public boolean isAdminActive()
	{
		return ( ( null != m_adminControl ) && m_adminControl.isVisible() );
	}// end isAdminActive()

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
	
	private void showAdminControl()
	{
		// If we've already load the admin control...
		if ( null != m_adminControl ) {
			// ...simply show it.
			showAdminControlImpl();
		}
		
		else
		{
			// ...otherwise, we load its split point...
			AdminControl.createAsync(
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
					m_contentPanel.add( m_adminControl );
					
					ScheduledCommand showAdminControl = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// ...and then show it.
							showAdminControlImpl();
						}// end execute()
					};
					Scheduler.get().scheduleDeferred( showAdminControl );
				}// end onSuccess()
			} );
		}
	}// end showAdminControl()
	
	private void showAdminControlImpl()
	{
		// Hide everything on the menu, the workspace tree control and the content control.
		m_mainMenuCtrl.showAdministrationMenubar();
		m_wsTreeCtrl.setVisible( false );
		m_contentCtrl.setVisible( false );
		m_activityStreamCtrl.hide();
		
		m_adminControl.showControl();
		relayoutPage( false );
	}// end showAdminControlImpl()
	
	@Override
	public void onTeamingAction(TeamingActionEvent event) {
		if(event != null) {
			//call the old action handler
			handleActionImpl(event.getAction(), event.getActionData());
		}
	}

	/*
	 * Fires a ContextChangingEvent from the JSP based UI.
	 */
	private void fireContextChanging()
	{
		EventHelper.fireContextChanging();
	}// end fireContextChanging()
	
	/*
	 * Fires an event on the event bus from the JSP based UI.
	 */
	private void fireVibeEvent( Event<?> event )
	{
		GwtTeaming.fireEvent( event );
	}// end fireVibeEvent()

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
	 * @param selectedBinderId
	 * @param inSearch
	 * @param searchTabId
	 */
	public void setMenuContext( String selectedBinderId, boolean inSearch, String searchTabId )
	{
		m_mainMenuCtrl.setContext(selectedBinderId, inSearch, searchTabId);
	}// end setMenuContext()
	
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
		final boolean prefetch,
		
		// Creation parameters (there aren't any.)
		
		// Teaming action parameters.
		final GwtMainPage mainPage,
		final TeamingAction action,
		final Object actionParam )
	{		
		loadControl1(
			// Prefetch parameters.
			mainPageClient,
			prefetch,
				
			// Creation parameters (there aren't any.)
				
			// Teaming action parameters.
			mainPage,
			action,
			actionParam );
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
		final boolean prefetch,
		
		// Creation parameters (there aren't any.)
		
		// Teaming action parameters.
		final GwtMainPage mainPage,
		final TeamingAction action,
		final Object actionParam )
	{		
		GWT.runAsync( GwtMainPage.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				initMainPage_Finish(
					// Prefetch parameters.
					mainPageClient,
					prefetch,
						
					// Creation parameters (there aren't any.)
						
					// Teaming action parameters.
					mainPage,
					action,
					actionParam );
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
		final boolean prefetch,
		
		// Creation parameters (there aren't any.)
		
		// Teaming action parameters.
		final GwtMainPage mainPage,
		final TeamingAction action,
		final Object actionParam )
	{		
		if (prefetch)
		{
			mainPageClient.onSuccess( null );
		}
		
		else if ( null == mainPage )
		{
			GwtMainPage newMainPage = new GwtMainPage();
			mainPageClient.onSuccess( newMainPage );
		}
		
		else
		{
			mainPage.handleActionImpl( action, actionParam );
			mainPageClient.onSuccess( mainPage );
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
			false,
			
			// Creation parameters (there aren't any.)
			
			// Teaming action parameters ignored.
			null,
			null,
			null );
	}// end createAsync()	

	/**
	 * Handle the action that was requested by the user somewhere in
	 * the main page.  For example, the user clicked on "My Workspace"
	 * in the masthead.
	 * 
	 * @param mainPageClient
	 * @param mainPage
	 * @param action
	 * @param actionParam
	 */
	public static void handleAction(
		GwtMainPageClient mainPageClient,
		final GwtMainPage mainPage,
		final TeamingAction action,
		final Object actionParam )
	{
		if ( null == mainPageClient )
		{
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
			// Prefetch parameters.  false -> Not a prefetch.  
			mainPageClient,
			false,
			
			// Creation parameters (there aren't any.)
			
			// Teaming action parameters.
			mainPage,
			action,
			actionParam);
	}// end handleAction()

	public static void handleAction(
		final GwtMainPage mainPage,
		final TeamingAction action,
		final Object actionParam )
	{
		// Always use the initial form of the method.
		handleAction( null, mainPage, action, actionParam );
	}// end handleAction()
	
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
			true,
			
			// Creation parameters (there aren't any.)
			
			// Teaming action parameters ignored.
			null,
			null,
			null );
	}// end prefetch()
	
	public static void prefetch()
	{
		// Always use the initial form of the method.
		prefetch(null);
	}// end prefetch()
}// end GwtMainPage
