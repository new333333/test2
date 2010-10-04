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

package org.kablink.teaming.gwt.client;

import org.kablink.teaming.gwt.client.UIStateManager.UIState;
import org.kablink.teaming.gwt.client.profile.widgets.GwtQuickViewDlg;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.OnBrowseHierarchyInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.SimpleProfileParams;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.ActivityStreamCtrl;
import org.kablink.teaming.gwt.client.widgets.AdminControl;
import org.kablink.teaming.gwt.client.widgets.ContentControl;
import org.kablink.teaming.gwt.client.widgets.EditBrandingDlg;
import org.kablink.teaming.gwt.client.widgets.LoginDlg;
import org.kablink.teaming.gwt.client.widgets.MainMenuControl;
import org.kablink.teaming.gwt.client.widgets.MastHead;
import org.kablink.teaming.gwt.client.widgets.PersonalPreferencesDlg;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl.TreeMode;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;


/**
 * This widget will display the main Teaming page
 */
public class GwtMainPage extends Composite
	implements ActionHandler, ResizeHandler
{
	public static boolean m_novellTeaming = true;
	public static RequestInfo m_requestInfo;
	public static ContentControl m_contentCtrl;

	private boolean m_inSearch = false;
	private String m_searchTabId = "";
	private EditBrandingDlg m_editBrandingDlg = null;
	private PersonalPreferencesDlg m_personalPrefsDlg = null;
	private LoginDlg m_loginDlg = null;
	private EditCanceledHandler m_editBrandingCancelHandler = null;
	private EditSuccessfulHandler m_editBrandingSuccessHandler = null;
	private EditSuccessfulHandler m_editPersonalPrefsSuccessHandler = null;
	private FlowPanel m_contentPanel;
	private FlowPanel m_teamingRootPanel;
	private MainMenuControl m_mainMenuCtrl;
	private MastHead m_mastHead;
	private AdminControl m_adminControl = null;
	private TeamingPopupPanel m_breadCrumbBrowser;
	private String m_selectedBinderId;
	private WorkspaceTreeControl m_wsTreeCtrl;
	private UIStateManager m_uiStateManager;
	private ActivityStreamCtrl m_activityStreamCtrl = null;

	
	/**
	 * Class constructor. 
	 */
	public GwtMainPage()
	{
		Element bodyElement;
		String url;

		// Initialize the context load handler used by the traditional
		// UI to tell the GWT UI that a context has been loaded.
		initContextLoadHandlerJS(this);
		
		// Initialize the pre context switch handler used by the
		// traditional UI to tell the GWT UI that a context switch is
		// about to occur.
		initPreContextSwitchJS(this);
		
		// Initialize the JavaScript function that gets called when we want to handle a page using
		// GWT instead of in jsp.
		// For example, we never want the jsp login page to be loaded in the content control.
		initHandlePageWithGWTJS( this );
		
		// Initialize the JavaScript function that gets called when we want to close the
		// administration content panel.
		initCloseAdministrationContentPanelJS( this );
		
		// Initialize ReguestActionHandler as native JavaScript to allow any content to register
		//as an ActionRequestor - See GwtClientHelper:jsRegisterActionHandler
		initRequestActionHandler( this );
		
		// Initialize JavaScript to perform Popup for User Profile
		initSimpleUserProfileJS( this );
		
		// Initialize JavaScript that handles the landing page options
		initHandleLandingPageOptionsJS( this );
		
		// Initialize the JavaScript that calls the login dialog.
		initInvokeLoginDlgJS( this );
		
		// Create a UIStateManager that we will use to save/restore the ui state.
		m_uiStateManager = new UIStateManager();
		m_uiStateManager.addActionHandler( this );
		
		// Set the class name on the <body> element to "mainGwtTeamingPage"
		bodyElement = RootPanel.getBodyElement();
		bodyElement.setClassName( "mainTeamingPage" );
		
		m_teamingRootPanel = new FlowPanel();
		m_teamingRootPanel.addStyleName( "mainTeamingPagePanel" );

		// Get information about the request we are dealing with.
		m_requestInfo = getRequestInfo();
		m_selectedBinderId = m_requestInfo.getBinderId();
		m_novellTeaming = m_requestInfo.isNovellTeaming();
		
		// Add the MastHead to the page.
		m_mastHead = new MastHead( m_requestInfo );
		registerActionHandler( m_mastHead );
		m_teamingRootPanel.add( m_mastHead );

		// Is there an error message to be displayed?
		final String errMsg = m_requestInfo.getErrMsg();
		if ( GwtClientHelper.hasString( errMsg ) )
		{
			// Yes
			// Execute a deferred command the will display it.
			DeferredCommand.addCommand( new Command()
			{
				public void execute()
				{
					// Is the user logged in?
					if ( m_requestInfo.isUserLoggedIn() )
					{
						// Yes
						// Take the user to their workspace.
						handleAction( TeamingAction.MY_WORKSPACE, null );
					}
					
					// Tell the user.  We do this as a deferred command
					// so that the UI can continue to render while the
					// message box is displayed.
					Window.alert( errMsg );
				}
			} );
		}
		
		// Add the main menu to the page.
		m_mainMenuCtrl = new MainMenuControl( this );
		registerActionHandler( m_mainMenuCtrl );
		m_teamingRootPanel.add( m_mainMenuCtrl );
		
		// Create a panel to hold the WorkspaceTree control and the content control
		m_contentPanel = new FlowPanel();
		m_contentPanel.addStyleName( "mainContentPanel" );
		
		// Create the WorkspaceTree control.
		m_wsTreeCtrl = new WorkspaceTreeControl( this, m_selectedBinderId, TreeMode.VERTICAL );
		m_wsTreeCtrl.addStyleName( "mainWorkspaceTreeControl" );
		registerActionHandler( m_wsTreeCtrl );
		m_contentPanel.add( m_wsTreeCtrl );
		
		// Create the content control.
		m_contentCtrl = new ContentControl( "gwtContentIframe" );
		m_contentCtrl.addStyleName( "mainContentControl" );
		m_contentPanel.add( m_contentCtrl );
		
		// Create an activity stream control.
		m_activityStreamCtrl = new ActivityStreamCtrl( this );
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
				Command cmd;
				
				// Yes
				// Hide the workspace tree control and the menu bar.
				m_wsTreeCtrl.setVisible( false );
				m_mainMenuCtrl.setVisible( false );
				
				// invoke the login dialog.
		        cmd = new Command()
		        {
		        	/**
		        	 * 
		        	 */
		            public void execute()
		            {
						invokeLoginDlg( false );
		            }
		        };
		        DeferredCommand.addCommand( cmd );
			}
		}
		
		// All composites must call initWidget() in their constructors.
		initWidget( m_teamingRootPanel );

	}// end GwtMainPage()

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
	 * Returns the workspace tree control.
	 * 
	 * @return
	 */
	public WorkspaceTreeControl getWorkspaceTree()
	{
		return m_wsTreeCtrl;
	}//end getWorkspaceTree()
	
	/*
	 * Hide the popup entry iframe div if one exists.
	 */
	private native void hideEntryPopupDiv() /*-{
		if ( $wnd.ss_hideEntryDivOnLoad !== undefined )
		{
			$wnd.ss_hideEntryDivOnLoad();
		}
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
	}-*/;

	/*
	 * Called to create a JavaScript method that will be invoked from
	 * the traditional UI just before a context switch occurs.
	 */
	private native void initPreContextSwitchJS(GwtMainPage gwtMainPage) /*-{
		$wnd.ss_preContextSwitch = function()
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::preContextSwitch()();
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
	 * instantiated in the GWTMainPage to be able to register as a actionRequestor.
	 */
	private native void initRequestActionHandler( GwtMainPage gwtMainPage ) /*-{
		$wnd.ss_registerActionHandler = function( actionRequestor )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::registerActionHandler(Lorg/kablink/teaming/gwt/client/util/ActionRequestor;)( actionRequestor );
		}//end ss_registerActionHanler
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
	 * Copies the <title> text from the GWT content IFRAME to the
	 * main GWT page's <title>.
	 */
	private static native void jsFixupGwtMainTitle() /*-{
		$wnd.top.document.title = $wnd.top.gwtContentIframe.document.title;
	}-*/;
	
	/**
	 * This method will close the administration content panel.
	 */
	@SuppressWarnings("unused")
	private void closeAdministrationContentPanel()
	{
		if ( m_adminControl != null )
		{
			m_adminControl.hideContentPanel();
		}
	}// end closeAdministrationContentPanel()
	
	/*
	 * Puts a context change from the traditional UI into effect.
	 */
	@SuppressWarnings("unused")
	private void contextLoaded( String binderId, String inSearch, String searchTabId ) {
		contextLoaded(
			binderId,
			Instigator.CONTENT_CONTEXT_CHANGE,
			((null != inSearch) && Boolean.parseBoolean( inSearch )),
			searchTabId );
	}
	
	private void contextLoaded( String binderId, Instigator instigator ) {
		contextLoaded( binderId, instigator, false, "" );
	}
	
	private void contextLoaded( String binderId, final Instigator instigator, boolean inSearch, String searchTabId )
	{
		restoreUIState();

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
		// in the traditional UI so I kept that functionality intact.
		final String contextBinderId;
		if      ( m_inSearch )                            contextBinderId = m_selectedBinderId; // Teaming 2.x equivalent would have been:  m_requestInfo.getTopWSId();
		else if ( GwtClientHelper.hasString( binderId ) ) contextBinderId = binderId;
		else                                              contextBinderId = m_selectedBinderId;
		
		GwtTeaming.getRpcService().getBinderPermalink( new HttpRequestInfo(), binderId, new AsyncCallback<String>()
		{
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
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
				if (forceSidebarReload) {
					osbInfo.setForceSidebarReload(forceSidebarReload);
				}
				selectionChanged(osbInfo);
			}// end onSuccess()
		});
	}// end contextLoaded()

	/**
	 * Invoke the "Edit Branding" dialog.
	 */
	private void editBranding( GwtBrandingData brandingData )
	{
		String brandingBinderId;
		int x;
		int y;
		
		// Will the user be editing the site branding?
		if ( brandingData.isSiteBranding() == false )
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
		brandingBinderId = brandingData.getBinderId();
		if ( brandingData.isSiteBranding() == false && brandingBinderId.equalsIgnoreCase( m_mastHead.getBinderId() ) == false )
		{
			// Yes, start with empty branding data.
			brandingData = new GwtBrandingData();
			brandingData.setBinderId( m_selectedBinderId );
		}
		
		// Get the position of the content control.
		x = m_contentCtrl.getAbsoluteLeft();
		if ( x < 75 )
			x = 75;
		y = m_contentCtrl.getAbsoluteTop();
		if ( y < 75 )
			y = 75;
		
		// Create a handler that will be called when the user presses the ok button in the dialog.
		if ( m_editBrandingSuccessHandler == null )
		{
			m_editBrandingSuccessHandler = new EditSuccessfulHandler()
			{
				private AsyncCallback<Boolean> rpcSaveCallback = null;
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
						rpcSaveCallback = new AsyncCallback<Boolean>()
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
							public void onSuccess( Boolean result )
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
							}// end onSuccess()
						};
					}
			
					// Issue an ajax request to save the branding data.
					{
						GwtRpcServiceAsync rpcService;
						
						rpcService = GwtTeaming.getRpcService();
						
						// Issue an ajax request to save the branding data to the db.  rpcSaveCallback will
						// be called when we get the response back.
						savedBrandingData = (GwtBrandingData) obj;
						rpcService.saveBrandingData( savedBrandingData.getBinderId(), (GwtBrandingData)obj, rpcSaveCallback );
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
			m_editBrandingDlg = new EditBrandingDlg( m_editBrandingSuccessHandler, m_editBrandingCancelHandler, false, true, x, y );
		}
		
		m_editBrandingDlg.init( brandingData );
		m_editBrandingDlg.setPopupPosition( x, y );
		m_editBrandingDlg.show();
		
	}// end editBranding()

	
	/**
	 * Invoke the "Edit Personal Preferences" dialog.
	 */
	private void editPersonalPreferences()
	{
		AsyncCallback<GwtPersonalPreferences> rpcReadCallback;
		
		// Create a callback that will be called when we get the personal preferences.
		rpcReadCallback = new AsyncCallback<GwtPersonalPreferences>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					GwtTeaming.getMessages().rpcFailure_GetPersonalPreferences() );
			}// end onFailure()
	
			/**
			 * We successfully retrieved the user's personal preferences.  Now invoke the "edit personal preferences" dialog.
			 */
			public void onSuccess( GwtPersonalPreferences personalPrefs )
			{
				int x;
				int y;

				// Get the position of the content control.
				x = m_contentCtrl.getAbsoluteLeft();
				y = m_contentCtrl.getAbsoluteTop();
				
				// Create a handler that will be called when the user presses the ok button in the dialog.
				if ( m_editPersonalPrefsSuccessHandler == null )
				{
					m_editPersonalPrefsSuccessHandler = new EditSuccessfulHandler()
					{
						private AsyncCallback<Boolean> rpcSaveCallback = null;
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
								rpcSaveCallback = new AsyncCallback<Boolean>()
								{
									/**
									 * 
									 */
									public void onFailure( Throwable t )
									{
										GwtClientHelper.handleGwtRPCFailure(
											GwtTeaming.getMessages().rpcFailure_SavePersonalPreferences() );
									}// end onFailure()
							
									/**
									 * 
									 * @param result
									 */
									public void onSuccess( Boolean result )
									{
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
								GwtRpcServiceAsync rpcService;
								
								rpcService = GwtTeaming.getRpcService();
								
								// Issue an ajax request to save the personal preferences to the db.  rpcSaveCallback will
								// be called when we get the response back.
								rpcService.savePersonalPreferences( personalPrefs, rpcSaveCallback );
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
			GwtRpcServiceAsync rpcService;
			
			rpcService = GwtTeaming.getRpcService();
			
			// Issue an ajax request to get the personal preferences from the db.
			rpcService.getPersonalPreferences( rpcReadCallback );
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
	 */
	public void handleAction( TeamingAction action, Object obj )
	{
		switch (action)
		{
		case ADMINISTRATION:
			// Save the current ui state
			saveUIState();
			
			// Hide any popup entry iframe divs.
			hideEntryPopupDiv();
			
			// Hide everything on the menu, the workspace tree control and the content control.
			m_mainMenuCtrl.showAdministrationMenubar();
			m_wsTreeCtrl.setVisible( false );
			m_contentCtrl.setVisible( false );
			m_activityStreamCtrl.hide();
			
			// Have we already created an AdminControl?
			if ( m_adminControl == null )
			{
				// No, create it.
				m_adminControl = new AdminControl();
				registerActionHandler( m_adminControl );
				m_contentPanel.add( m_adminControl );
			}
			
			m_adminControl.showControl();
			relayoutPage( false );
			break;
		
		case CLOSE_ADMINISTRATION:
			Command cmd;
			
			// Hide the AdminControl.
			if ( m_adminControl != null )
				m_adminControl.hideControl();
			
			// Should we go back into activity stream mode?
			if ( m_wsTreeCtrl.isInActivityStreamMode() )
			{
				// Yes
				if ( m_activityStreamCtrl != null )
					m_activityStreamCtrl.show();
			}

			// Show the content control.
			m_contentCtrl.setVisible( true );

			// Hide the administration menu bar.
			m_mainMenuCtrl.hideAdministrationMenubar();

			// Restore the ui state to what it was before we opened the site administration.
			cmd = new Command()
			{
				public void execute()
				{
					restoreUIState();
				}
			};
			DeferredCommand.addCommand( cmd );
			
			cmd = new Command()
			{
				public void execute()
				{
					relayoutPage( true );
				}
			};
			DeferredCommand.addCommand( cmd );
			break;
			
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
			
			url = "http://www.novell.com/documentation/teaming3/team3_user/data/bookinfo.html";
			Window.open( url, "teaming_help_window", "resizeable,scrollbars" );
			break;

		case LOGIN:
			invokeLoginDlg( true );
			break;
			
		case LOGOUT:
			// If the user has gone into the administration page, tell the administration page
			// to do whatever cleanup it needs to do.
			if ( m_adminControl != null )
				m_adminControl.doPreLogoutCleanup();
			
			GwtClientHelper.jsLogout();
			break;
			
		case MY_WORKSPACE:
			// If we're currently running site administration...
			if ((null != m_adminControl) && m_adminControl.isVisible()) {
				// ...close it first.
				handleAction( TeamingAction.CLOSE_ADMINISTRATION, null );
			}
			
			// If we're currently in "activity stream" mode
			if ( m_activityStreamCtrl != null && m_activityStreamCtrl.isVisible())
			{
				// exit out of "activity stream" mode.
				handleAction( TeamingAction.EXIT_ACTIVITY_STREAM_MODE, null );
			}
			
			// Change the browser's URL.
			preContextSwitch();
			gotoUrl( m_requestInfo.getMyWorkspaceUrl() );
			break;
			
		case SELECTION_CHANGED:
			// If we're currently in "activity stream" mode
			if ( m_activityStreamCtrl != null && m_activityStreamCtrl.isVisible())
			{
				// exit out of "activity stream" mode.
				handleAction( TeamingAction.EXIT_ACTIVITY_STREAM_MODE, null );
			}
			
			preContextSwitch();
			selectionChanged( obj );
			break;
		
		case SIZE_CHANGED:
			sizeChanged( obj );
			break;
			
		case TOGGLE_GWT_UI:
			toggleGwtUI();
			break;

		case BROWSE_HIERARCHY:
			runBreadCrumbBrowser( obj );
			break;
			
		case HIERARCHY_BROWSER_CLOSED:
			closeBreadCrumbBrowser();
			break;
			
		case VIEW_TEAM_MEMBERS:
			viewTeamMembers();
			break;
			
		case GOTO_CONTENT_URL:
			preContextSwitch();
			gotoUrl( obj );
			break;

		case GOTO_PERMALINK_URL:
			preContextSwitch();
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
			preContextSwitch();
			simpleSearch( obj );
			break;
			
		case ADVANCED_SEARCH:
			preContextSwitch();
			advancedSearch();
			break;
			
		case SAVED_SEARCH:
			preContextSwitch();
			savedSearch( obj );
			break;
			
		case RECENT_PLACE_SEARCH:
			preContextSwitch();
			recentPlaceSearch( obj );
			break;
			
		case PRE_CONTEXT_SWITCH:
			preContextSwitch();
			break;
			
		case RELOAD_LEFT_NAVIGATION:
			reloadLeftNavigation();
			break;
			
		case TAG_SEARCH:
			preContextSwitch();
			tagSearch( obj );
			break;
			
		case HIDE_MASTHEAD:
			m_mastHead.setVisible( false );
			m_mainMenuCtrl.setMastheadSliderMenuItemState( TeamingAction.SHOW_MASTHEAD );
			relayoutPage( true );
			break;
			
		case SHOW_MASTHEAD:
			m_mastHead.setVisible( true );
			m_mainMenuCtrl.setMastheadSliderMenuItemState( TeamingAction.HIDE_MASTHEAD );
			relayoutPage( true );
			break;
			
		case HIDE_LEFT_NAVIGATION:
			// Are we displaying the administration page?
			if ( m_adminControl != null && m_adminControl.isVisible() == true )
			{
				// Yes
				m_adminControl.hideTreeControl();
			}
			else
			{
				// Hide the tree control
				m_wsTreeCtrl.setVisible( false );
				
				// Reposition the content control to where the tree control used to be.
				m_contentCtrl.addStyleName( "mainWorkspaceTreeControl" );
				m_activityStreamCtrl.addStyleName( "mainWorkspaceTreeControl" );
			}

			m_mainMenuCtrl.setWorkspaceTreeSliderMenuItemState( TeamingAction.SHOW_LEFT_NAVIGATION );
			
			// Relayout the content panel.
			relayoutPage( false );
			break;
			
		case SHOW_LEFT_NAVIGATION:
			// Are we displaying the administration page?
			if ( m_adminControl != null && m_adminControl.isVisible() == true )
			{
				// Yes
				m_adminControl.showTreeControl();
			}
			else
			{
				// Reposition the content control to its original position.
				m_contentCtrl.removeStyleName( "mainWorkspaceTreeControl" );
				m_activityStreamCtrl.removeStyleName( "mainWorkspaceTreeControl" );
				
				// Show the tree control.
				m_wsTreeCtrl.setVisible( true );
			}
			
			m_mainMenuCtrl.setWorkspaceTreeSliderMenuItemState( TeamingAction.HIDE_LEFT_NAVIGATION );

			// Relayout the content panel.
			relayoutPage( false );
			break;
		
		case TEAMING_FEED:
			String teamingFeedUrl;
			
			teamingFeedUrl = m_requestInfo.getTeamingFeedUrl();
			Window.open( teamingFeedUrl, "_teaming_feed", "width=500,height=700,resizable,scrollbars" );
			break;
			
		case ACTIVITY_STREAM:
			activityStream( obj );
			break;
			
		case ENTER_ACTIVITY_STREAM_MODE:
			enterActivityStreamMode( obj );
			break;
			
		case EXIT_ACTIVITY_STREAM_MODE:
			exitActivityStreamMode();
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
				Window.alert( "In handleAction( INVOKE_SIMPLE_PROFILE, obj ) obj is not a SimpleProfileParams object." );
			}
			break;
			
		case VIEW_FOLDER_ENTRY:
			if ( obj instanceof String )
			{
				viewFolderEntry( (String) obj );
			}
			else
			{
				Window.alert( "In handleAction( VIEW_FOLDER_ENTRY, obj ) obj is not a String object" );
			}
			break;
			
		case UNDEFINED:
		default:
			Window.alert( "Unknown action selected: " + action.getUnlocalizedDesc() );
			break;
		}
	}// end handleAction()
	
	
	/**
	 * This method will handle the landing page options such as "hide the masthead", "hide the sidebar", etc.
	 */
	@SuppressWarnings("unused")
	private void handleLandingPageOptions( boolean hideMasthead, boolean hideSidebar, boolean showBranding )
	{
		boolean showMasthead;
		
		// Save the current ui state so we can restore it when the user moves to another page.
		saveUIState();
		
		// Hide or show the sidebar.
		if ( hideSidebar )
			handleAction( TeamingAction.HIDE_LEFT_NAVIGATION, null );
		else
			handleAction( TeamingAction.SHOW_LEFT_NAVIGATION, null );
		
		// Figure out if we should show the masthead.
		if ( hideMasthead == false || showBranding == true )
			showMasthead = true;
		else
			showMasthead = false;
		
		// Hide or show the masthead.
		if ( showMasthead )
			handleAction( TeamingAction.SHOW_MASTHEAD, null );
		else
			handleAction( TeamingAction.HIDE_MASTHEAD, null );
	}// end handleLandingPageOptions()
	

	/**
	 * This method will handle the given page ui in gwt instead of having the jsp page do the work.
	 */
	@SuppressWarnings("unused")
	private void handlePageWithGWT( String pageName )
	{
		if ( pageName != null && pageName.length() > 0 )
		{
			if ( pageName.equalsIgnoreCase( "login-page" ) )
				handleAction( TeamingAction.LOGIN, null );
			else
			{
				Window.alert( "In handlePageWithGWT(), unknown page: " + pageName );
			}
		}
	}// end handlePageWithGWT()
	
	/**
	 * Invoke the "login" dialog.
	 */
	private void invokeLoginDlg( boolean allowCancel )
	{
		PopupPanel.PositionCallback posCallback;
		String loginErr;
		
		if ( m_loginDlg == null )
		{
			String refererUrl;
			
			// Get the url to go to after the user logs in.
			refererUrl = m_requestInfo.getLoginRefererUrl();
			
			// Create the login dialog.
			m_loginDlg = new LoginDlg( false, true, 0, 0, null, m_requestInfo.getLoginUrl(), refererUrl );
		}
		
		// Tell the login dialog if we allow cancel.
		m_loginDlg.setAllowCancel( allowCancel );
		
		// Was there an error from a previous login attempt?
		// Is there an error from a previous login attempt?
		loginErr = m_requestInfo.getLoginError();
		if ( loginErr != null && loginErr.length() > 0 )
		{
			// Yes, tell the user the login failed.
			m_loginDlg.showLoginFailedMsg();
		}
		else
		{
			// No, clear the login failed message.
			m_loginDlg.hideLoginFailedMsg();
		}
		
		m_loginDlg.hideAuthenticatingMsg();
		
		// Show the login dialog.
		posCallback = new PopupPanel.PositionCallback()
		{
			/**
			 * 
			 */
			public void setPosition(int offsetWidth, int offsetHeight)
			{
				int x;
				int y;
				
				x = (Window.getClientWidth() - offsetWidth) / 2;
				y = (Window.getClientHeight() - offsetHeight) / 3;
				
				m_loginDlg.setPopupPosition( x, y );
			}// end setPosition()
		};
		m_loginDlg.setPopupPositionAndShow( posCallback );
	}// end invokeLoginDlg()
	
	
	/*
	 * This method will be called when the user selects a binder from
	 * the workspace tree control.
	 * 
	 * Implements the SELECTION_CHANGED teaming action.
	 */
	private void selectionChanged( Object obj )
	{
		if ( obj instanceof OnSelectBinderInfo )
		{
			Instigator instigator;
			OnSelectBinderInfo binderInfo;

			// Tell the masthead to update the branding for the newly selected binder.
			binderInfo = (OnSelectBinderInfo) obj;
			m_selectedBinderId = binderInfo.getBinderId().toString();
			m_mastHead.setBinderId( m_selectedBinderId );
			
			// If we're not coming from a WorkspaceTreeControl context
			// change...
			instigator = binderInfo.getInstigator();
			if (( Instigator.SIDEBAR_TREE   != instigator ) ||
			    ( Instigator.SIDEBAR_RELOAD == instigator ) ||
			      binderInfo.getForceSidebarReload() )
			{
				// Tell the WorkspaceTreeControl to change contexts.
				m_wsTreeCtrl.setSelectedBinder( binderInfo );
			}

			// Are we handling a context change in the content panel?
			if ( Instigator.CONTENT_CONTEXT_CHANGE == instigator )
			{
				// Yes!  Update the menu bar accordingly.
				m_mainMenuCtrl.contextLoaded( m_selectedBinderId, m_inSearch, m_searchTabId );
				m_wsTreeCtrl.contextLoaded( m_selectedBinderId );
			}
			else if ( Instigator.SIDEBAR_RELOAD != instigator )
			{
				// No, we aren't handling a context change in the
				// content panel! Tell the content panel to view the
				// selected binder.
				m_wsTreeCtrl.showBinderBusy( binderInfo );
				m_contentCtrl.setUrl( binderInfo.getBinderUrl() );
			}
		}
		else
			Window.alert( "in selectionChanged() and obj is not an OnSelectBinderInfo object" );
	}// end selectionChanged()

	
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
	 * Toggles the state of the GWT UI.
	 * 
	 * Implements the TOGGLE_GWT_UI teaming action.
	 */
	private void toggleGwtUI()
	{
		GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();
		rpcService.getUserWorkspacePermalink( new HttpRequestInfo(), new AsyncCallback<String>()
		{
			public void onFailure( Throwable t ) {}
			public void onSuccess( String userWorkspaceURL )
			{
				jsToggleGwtUI();
				jsLoadUserWorkspaceURL( userWorkspaceURL + "/captive/false/seen_by_gwt/0" );
			}// end onSuccess()
			
			private native void jsToggleGwtUI()
			/*-{
				// Toggle the GWT UI state.
				$wnd.top.ss_toggleGwtUI( false );
			}-*/; // end jsToggleGwtUI()

			private native void jsLoadUserWorkspaceURL( String userWorkspaceURL )
			/*-{
				// Give the GWT UI state toggling 1/2
				// second to complete and reload the user
				// workspace.
				$wnd.setTimeout( function(){$wnd.top.location.href = userWorkspaceURL;}, 500 );
			}-*/; // end jsLoadUserWorkspace()
		});// end AsyncCallback()
	}// end toggleGwtUI()
	
	
	/*
	 * Called to run the Teaming hierarchy (i.e., bread crumb) browser.
	 * 
	 * Implements the BROWSE_HIERARCHY teaming action.
	 */
	private void runBreadCrumbBrowser( Object obj )
	{
		// If we're already running a bread crumb browser...
		if (( m_breadCrumbBrowser != null ) && m_breadCrumbBrowser.isShowing() )
		{
			// ...we simply ignore requests to open one.
			return;
			
		}
		
		if ( obj instanceof OnBrowseHierarchyInfo )
		{
			OnBrowseHierarchyInfo bhi;
			WorkspaceTreeControl breadCrumbTree;
			
			// A WorkspaceTreeControl in horizontal mode serves as the
			// bread crumb browser.  Create one...
			breadCrumbTree = new WorkspaceTreeControl( this, m_selectedBinderId, TreeMode.HORIZONTAL );
			breadCrumbTree.addStyleName( "mainBreadCrumb_Tree" );
			registerActionHandler( breadCrumbTree );
			m_breadCrumbBrowser = new TeamingPopupPanel(true);
			GwtClientHelper.scrollUIForPopup( m_breadCrumbBrowser );
			GwtClientHelper.rollDownPopup(    m_breadCrumbBrowser );
			m_breadCrumbBrowser.addStyleName( "mainBreadCrumb_Browser roundcornerSM-bottom" );
			m_breadCrumbBrowser.setWidget( breadCrumbTree );
			
			// ...position it as per the browse hierarchy request...
			bhi = ((OnBrowseHierarchyInfo) obj);
			m_breadCrumbBrowser.setPopupPosition(bhi.getLeft(), bhi.getTop());

			// ...and play the opening effect.
			m_breadCrumbBrowser.show();
		}
		else
			Window.alert( "in runBreadCrumbBrowser() and obj is not an OnBrowseHierarchyInfo object" );
	}// end runBreadCrumbBrowser()
	
	/*
	 * Called when the current Teaming hierarchy (i.e., bread crumb)
	 * browser has been closed.
	 * 
	 * Implements the HIERARCHY_BROWSER_CLOSED teaming action.
	 */
	private void closeBreadCrumbBrowser()
	{
		if (null != m_breadCrumbBrowser)
		{
			m_breadCrumbBrowser.hide();
		}
	}// end closeBreadCrumbBrowser()
	
	/*
	 * Called to view the membership of the currently selected binder.
	 * 
	 * Implements the VIEW_TEAM_MEMBERS teaming action.
	 */
	private void viewTeamMembers()
	{
		GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();
		rpcService.getBinderPermalink( new HttpRequestInfo(), m_selectedBinderId, new AsyncCallback<String>()
		{
			public void onFailure( Throwable t ) {
				GwtClientHelper.handleGwtRPCFailure(
					GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
					m_selectedBinderId );
			}//end onFailure()
			
			public void onSuccess( String binderUrl )
			{
				OnSelectBinderInfo osbInfo;
				
				binderUrl = GwtClientHelper.appendUrlParam( binderUrl, "operation", "show_team_members" );
				osbInfo = new OnSelectBinderInfo( m_selectedBinderId, binderUrl, false, Instigator.OTHER );
				selectionChanged( osbInfo );
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
		GwtTeaming.getRpcService().trackBinder( m_selectedBinderId, new AsyncCallback<Boolean>()
		{
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					GwtTeaming.getMessages().rpcFailure_TrackingBinder(),
					m_selectedBinderId );
			}//end onFailure()
			
			public void onSuccess( Boolean success )
			{
				// It's overkill to force a full context reload, which
				// this does, but it's the only way right now to ensure
				// the What's New tab and other information gets fully
				// refreshed.
				contextLoaded( m_selectedBinderId, Instigator.OTHER );
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
		GwtTeaming.getRpcService().untrackBinder( m_selectedBinderId, new AsyncCallback<Boolean>()
		{
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					GwtTeaming.getMessages().rpcFailure_UntrackingBinder(),
					m_selectedBinderId );
			}//end onFailure()
			
			public void onSuccess( Boolean success )
			{
				// It's overkill to force a full context reload, which
				// this does, but it's the only way right now to ensure
				// the What's New tab and other information gets fully
				// refreshed.
				contextLoaded( m_selectedBinderId, Instigator.OTHER );
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
		GwtTeaming.getRpcService().untrackPerson( m_selectedBinderId, new AsyncCallback<Boolean>()
		{
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					GwtTeaming.getMessages().rpcFailure_TrackingPerson(),
					m_selectedBinderId );
			}//end onFailure()
			
			public void onSuccess( Boolean success )
			{
				// It's overkill to force a full context reload, which
				// this does, but it's the only way right now to ensure
				// the What's New tab and other information gets fully
				// refreshed.
				contextLoaded( m_selectedBinderId, Instigator.OTHER );
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
			String searchFor;

			// What are we searching for?
			searchFor = ((null == obj ) ? "" : GwtClientHelper.jsEncodeURIComponent((String) obj));
			String searchUrl = (m_requestInfo.getSimpleSearchUrl() + "&searchText=" + searchFor);
			GwtClientHelper.loadUrlInContentFrame(searchUrl);
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
	 * This method will be called to perform a recent place search on
	 * an integer received as a parameter.
	 * 
	 * Implements the RECENT_PLACE_SEARCH teaming action.
	 */
	private void recentPlaceSearch( Object obj )
	{
		if ( ( null == obj ) || ( obj instanceof Integer ))
		{
			Integer searchFor;

			// What tab is the recent place search for?
			searchFor = ((Integer) obj);
			String searchUrl = (m_requestInfo.getRecentPlaceSearchUrl() + "&tabId=" + String.valueOf(searchFor.intValue()));
			GwtClientHelper.loadUrlInContentFrame(searchUrl);
		}
		else
			Window.alert( "in recentPlaceSearch() and obj is not an Integer object" );
	}//end recentPlaceSearch()

	/*
	 * This method will do whatever needs to be done from a UI
	 * perspective to prepare for an pending context switch.
	 * 
	 * Implements the PRE_CONTEXT_SWITCH teaming action.
	 */
	private void preContextSwitch() {
		// Restore any ui state that may be saved.
		restoreUIState();
		
		if ( null != m_mainMenuCtrl )
		{
			m_mainMenuCtrl.clearContextMenus();
		}
	}// end preContextSwitch()
	
	/*
	 * Forces the workspace tree to reload itself.
	 * 
	 * Implements the RELOAD_LEFT_NAVIGATION teaming action.
	 */
	private void reloadLeftNavigation()
	{
		contextLoaded(m_selectedBinderId, Instigator.SIDEBAR_RELOAD);
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

	/*
	 * This method will be called to load an activity stream based on
	 * the Object received as a parameter.
	 * 
	 * Implements the ACTIVITY_STREAM teaming action.
	 */
	private void activityStream( Object obj )
	{
		// Do we have an ActivityStreamInfo parameter?
		if ( obj instanceof ActivityStreamInfo )
		{
			// Yes!  Load the activity stream control...
			final ActivityStreamInfo asi = ((ActivityStreamInfo) obj);
			m_activityStreamCtrl.setActivityStream( asi );
			m_activityStreamCtrl.show();
			
			// ...tell the sidebar to display the appropriate
			// ...content and/or selection...
			m_wsTreeCtrl.setActivityStream( asi );

			// ...and persist this activity stream in the user's profile.
			GwtTeaming.getRpcService().persistActivityStreamSelection( new HttpRequestInfo(), asi, new AsyncCallback<Boolean>()
			{
				// Note that we're not doing anything with the results
				// good or bad.  If it fails, so what?  The activity
				// stream will simply not persist for the user.
				public void onFailure( Throwable t      ) {}
				public void onSuccess( Boolean   result ) {}
			});
		}
		else
			Window.alert( "in activityStream() and obj is not an ActivityStreamInfo object" );
	}//end activityStream()

	/*
	 * This method will be called to enter activity stream mode.
	 * 
	 * Implements the ENTER_ACTIVITY_STREAM_MODE teaming action.
	 */
	private void enterActivityStreamMode( Object obj )
	{
		// Hide any popup entry iframe divs.
		hideEntryPopupDiv();
		
		// Do we have no parameter or an ActivityStreamInfo parameter?
		if ( ( null == obj) || ( obj instanceof ActivityStreamInfo ))
		{
			// Yes!  Pass the request to the workspace tree control.
			//
			// Note:  If this is passed a default activity stream to
			//    load, a separate ACTIVITY_STREAM teaming action will
			//    be triggered by the workspace tree control AFTER it
			//    has entered activity stream mode.
			m_wsTreeCtrl.enterActivityStreamMode( (ActivityStreamInfo) obj );
		}
		else
			Window.alert( "in enterActivityStreamMode() and obj is not null and is not an ActivityStreamInfo object" );
	}//end enterActivityStreamMode()

	/*
	 * This method will be called to exit activity stream mode.
	 * 
	 * Implements the EXIT_ACTIVITY_STREAM_MODE teaming action.
	 */
	private void exitActivityStreamMode()
	{
		// Hide the activity stream control and pass the request to
		// the workspace tree control.
		m_activityStreamCtrl.hide();
		m_wsTreeCtrl.exitActivityStreamMode();
	}//end exitActivityStreamMode()

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
				m_activityStreamCtrl.relayoutPage();
			
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
				m_wsTreeCtrl.relayoutPage();
			}
			
			// Set the size and position of the entry popup div.
			GwtClientHelper.jsSetEntryPopupIframeSize();
		}
		else
		{
			Command cmd;
			
	        cmd = new Command()
	        {
	        	/**
	        	 * 
	        	 */
	            public void execute()
	            {
					relayoutPage( true );
	            }
	        };
	        DeferredCommand.addCommand( cmd );
		}
	}// end relayoutPage()

	
	/**
	 * Reload the page currently displayed in the content panel.
	 */
	public void reloadContentPanel()
	{
		m_contentCtrl.reload();
	}// end reloadContentPanel()
	
	
	/*
	 * Does what's necessary to wire the GwtMainPage to an
	 * ActionRequestor.
	 */
	private void registerActionHandler( ActionRequestor actionRequestor )
	{
		// For now, all we need to do is add the GwtMainPage as an
		// ActionHandler to the ActionRequestor.
		actionRequestor.addActionHandler( this );
	}// end registerActionHandler()
	
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
		
		final GwtQuickViewDlg dlg;
		PopupPanel.PositionCallback posCallback;
		
		dlg = new GwtQuickViewDlg(false, true, 0, 0, binderId, userName, element);
		this.registerActionHandler(dlg);
		
		posCallback = new PopupPanel.PositionCallback()
		{
			public void setPosition(int offsetWidth, int offsetHeight)
			{
				int x;
				int y;
				
				x = (Window.getClientWidth() - offsetWidth) / 2;
				y = (Window.getClientHeight() - offsetHeight) / 3;
				
				dlg.setPopupPosition( x, y );
			}// end setPosition()
		};
		dlg.setPopupPositionAndShow( posCallback );
	}
}// end GwtMainPage
