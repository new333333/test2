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

import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActionRequestor;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnBrowseHierarchyInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.ContentControl;
import org.kablink.teaming.gwt.client.widgets.EditBrandingDlg;
import org.kablink.teaming.gwt.client.widgets.MainMenuControl;
import org.kablink.teaming.gwt.client.widgets.MastHead;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl.TreeMode;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * This widget will display the main Teaming page
 */
public class GwtMainPage extends Composite
	implements ActionHandler, ResizeHandler
{
	public static boolean m_novellTeaming = true;
	public static RequestInfo m_requestInfo;
	
	private ContentControl m_contentCtrl;
	private EditBrandingDlg m_editBrandingDlg;
	private EditCanceledHandler m_editBrandingCancelHandler;
	private EditSuccessfulHandler m_editBrandingSuccessHandler;
	private FlowPanel m_contentPanel;
	private FlowPanel m_teamingRootPanel;
	private MainMenuControl m_mainMenuCtrl;
	private MastHead m_mastHead;
	private PopupPanel m_breadCrumbBrowser;
	private String m_selectedBinderId;
	private WorkspaceTreeControl m_wsTreeCtrl;

	
	/**
	 * Class constructor. 
	 */
	public GwtMainPage()
	{
		Element bodyElement;

		// Initialize the context load handler used by the traditional
		// UI to tell the GWT UI that a context has been loaded.
		initContextLoadHandlerJS(this);
		
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
		
		// Add the main menu to the page.
		m_mainMenuCtrl = new MainMenuControl();
		registerActionHandler( m_mainMenuCtrl );
		m_teamingRootPanel.add( m_mainMenuCtrl );
		
		// Create a panel to hold the WorkspaceTree control and the content control
		m_contentPanel = new FlowPanel();
		m_contentPanel.addStyleName( "mainContentPanel" );
		
		// Create the WorkspaceTree control.
		m_wsTreeCtrl = new WorkspaceTreeControl( m_requestInfo, m_selectedBinderId, TreeMode.VERTICAL );
		m_wsTreeCtrl.addStyleName( "mainWorkspaceTreeControl" );
		registerActionHandler( m_wsTreeCtrl );
		m_contentPanel.add( m_wsTreeCtrl );
		
		// Create the content control.
		m_contentCtrl = new ContentControl();
		m_contentCtrl.addStyleName( "mainContentControl" );
		m_contentCtrl.setUrl( m_requestInfo.getAdaptedUrl() + "&captive=true" );
		m_contentPanel.add( m_contentCtrl );
		
		m_teamingRootPanel.add( m_contentPanel );
		
		// Add a ResizeHandler to the browser so we'll know when the user resizes the browser.
		Window.addResizeHandler( this );
		
		// All composites must call initWidget() in their constructors.
		initWidget( m_teamingRootPanel );

	}// end GwtMainPage()

	/*
	 * Called to create a JavaScript method that will be invoked from
	 * view_workarea_navbar.jsp when new contexts are loaded.
	 */
	private native void initContextLoadHandlerJS(GwtMainPage gwtMainPage) /*-{
		$wnd.ss_contextLoaded = function( binderId )
		{
			gwtMainPage.@org.kablink.teaming.gwt.client.GwtMainPage::contextLoaded(Ljava/lang/String;)( binderId );
		}//end ss_contextLoaded()
	}-*/;

	/*
	 * Puts a context change from the traditional UI into effect.
	 */
	@SuppressWarnings("unused")
	private void contextLoaded( final String binderId )
	{
		GwtTeaming.getRpcService().getBinderPermalink( binderId, new AsyncCallback<String>()
		{
			public void onFailure( Throwable t )
			{
				Window.alert( t.toString() );
			}//end onFailure()
			
			public void onSuccess( String binderPermalink )
			{
				OnSelectBinderInfo osbInfo;
				
				osbInfo = new OnSelectBinderInfo( binderId, binderPermalink, false, Instigator.CONTENT_CONTEXT_CHANGE );
				selectionChanged(osbInfo);
			}// end onSuccess()
		});
	}// end contextLoaded()

	/**
	 * Invoke the "Edit Branding" dialog.
	 */
	private void editBranding()
	{
		GwtBrandingData brandingData;
		int x;
		int y;
		
		// Get the branding data the masthead is currently working with.
		brandingData = m_mastHead.getBrandingData();
		
		// Get the position of the content control.
		x = m_contentCtrl.getAbsoluteLeft();
		y = m_contentCtrl.getAbsoluteTop();
		
		// Hide the content control.
		m_contentCtrl.setVisible( false );
		
		// Create a handler that will be called when the user presses the ok button in the dialog.
		if ( m_editBrandingSuccessHandler == null )
		{
			m_editBrandingSuccessHandler = new EditSuccessfulHandler()
			{
				private AsyncCallback<Boolean> rpcSaveCallback = null;
				private String binderId = m_mastHead.getBinderId();
				
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
								String errMsg;
								String cause;
								GwtTeamingMessages messages;
								
								messages = GwtTeaming.getMessages();
								
								if ( t instanceof GwtTeamingException )
								{
									ExceptionType type;
								
									// Determine what kind of exception happened.
									type = ((GwtTeamingException)t).getExceptionType();
									if ( type == ExceptionType.ACCESS_CONTROL_EXCEPTION )
										cause = messages.errorAccessToFolderDenied( binderId );
									else if ( type == ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION )
										cause = messages.errorFolderDoesNotExist( binderId );
									else
										cause = messages.errorUnknownException();
								}
								else
								{
									cause = t.getLocalizedMessage();
									if ( cause == null )
										cause = t.toString();
								}
								
								errMsg = messages.getBrandingRPCFailed( cause );
								Window.alert( errMsg );
							}// end onFailure()
					
							/**
							 * 
							 * @param result
							 */
							public void onSuccess( Boolean result )
							{
								// Update the masthead with the new branding data.
								m_mastHead.refreshMasthead();
							}// end onSuccess()
						};
					}
			
					// Issue an ajax request to save the branding data.
					{
						GwtRpcServiceAsync rpcService;
						
						rpcService = GwtTeaming.getRpcService();
						
						// Issue an ajax request to save the branding data to the db.  rpcSaveCallback will
						// be called when we get the response back.
						rpcService.saveBrandingData( m_mastHead.getBinderId(), (GwtBrandingData)obj, rpcSaveCallback );
					}

					// Show the content control again.
					m_contentCtrl.setVisible( true );
					
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
					// Show the content control again.
					m_contentCtrl.setVisible( true );
					
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
	 * Use JSNI to grab the JavaScript object that holds the information about the request dealing with.
	 */
	private native RequestInfo getRequestInfo() /*-{
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
			Window.alert( "Administration is not implemented yet." );
			break;
			
		case EDIT_BRANDING:
			editBranding();
			break;
			
		case HELP:
			Window.alert( "Help is not implemented yet." );
			editBranding();
			break;

		case LOGOUT:
			Window.alert( "Logout is not implemented yet." );
			break;
			
		case MY_WORKSPACE:
			// Change the browser's url.
			Window.Location.replace( m_requestInfo.getMyWorkspaceUrl() );
			break;
			
		case SELECTION_CHANGED:
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
			
		case HIDE_LEFT_NAVIGATION:
		case HIDE_MASTHEAD:
		case SHOW_LEFT_NAVIGATION:
		case SHOW_MASTHEAD:
//!			...these need to be implemented...
			Window.alert( "Action not implemented:  " + action.getUnlocalizedDesc() );
			break;
			
		default:
			Window.alert( "Unknown action selected: " + action.getUnlocalizedDesc() );
			break;
		}
	}// end handleAction()
	
	
	/*
	 * This method will be called when the user selects a binder from
	 * the workspace tree control.
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
			if ( Instigator.SIDEBAR_TREE != instigator )
			{
				// Tell the WorkspaceTreeControl to change contexts.
				m_wsTreeCtrl.setSelectedBinder( binderInfo );
			}

			// Are we handling a context change in the content panel?
			if ( Instigator.CONTENT_CONTEXT_CHANGE == instigator )
			{
				// Yes!  Update the menu bar accordingly.
				m_mainMenuCtrl.contextLoaded( m_selectedBinderId );
			}
			else
			{
				// No, we aren't handling a context change in the
				// content panel! Tell the content panel to view the
				// selected binder.
				m_contentCtrl.setUrl( binderInfo.getBinderUrl() );
			}
		}
		else
			Window.alert( "in onSelect() and obj is not an OnSelectBinderInfo object" );
	}// end selectionChanged()

	
	/**
	 * This method gets called when the browser gets resized.
	 */
	public void onResize( ResizeEvent event )
	{
		// Adjust the height and width of the controls on this page.
		relayoutPage( true );
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
	 */
	private void toggleGwtUI()
	{
		GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();
		rpcService.getUserWorkspacePermalink( new AsyncCallback<String>()
		{
			public void onFailure( Throwable t ) {}
			public void onSuccess( String userWorkspaceURL )
			{
				jsToggleGwtUI();
				jsLoadUserWorkspaceURL( userWorkspaceURL + "&captive=false" );
			}// end onSuccess()
			
			private native void jsToggleGwtUI()
			/*-{
				// Toggle the GWT UI state.
				window.top.ss_toggleGwtUI( false );
			}-*/; // end jsToggleGwtUI()

			private native void jsLoadUserWorkspaceURL( String userWorkspaceURL )
			/*-{
				// Give the GWT UI state toggling 1/2
				// second to complete and reload the user
				// workspace.
				window.setTimeout( function(){window.top.location.href = userWorkspaceURL;}, 500 );
			}-*/; // end jsLoadUserWorkspace()
		});// end AsyncCallback()
	}// end toggleGwtUI()
	
	
	/*
	 * Called to run the Teaming hierarchy (i.e., bread crumb) browser.
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
			breadCrumbTree = new WorkspaceTreeControl( m_requestInfo, m_selectedBinderId, TreeMode.HORIZONTAL );
			breadCrumbTree.addStyleName( "mainBreadCrumb_Tree" );
			registerActionHandler( breadCrumbTree );

			m_breadCrumbBrowser = new PopupPanel(true);
			m_breadCrumbBrowser.setAnimationEnabled(true);
//!			m_breadCrumbBrowser.setAnimationType(PopupPanel.AnimationType.ROLL_DOWN);
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
	 */
	private void viewTeamMembers()
	{
		GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();
		rpcService.getBinderPermalink( m_selectedBinderId, new AsyncCallback<String>()
		{
			public void onFailure( Throwable t ) {
				Window.alert( t.toString() );
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
		}
		else
		{
			Timer timer;

			// No, set a timer and then relayout.
			timer = new Timer()
			{
				/**
				 * 
				 */
				@Override
				public void run()
				{
					relayoutPage( true );
				}// end run()
			};
			
			timer.schedule( 250 );
		}
	}// end relayoutPage()

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
}// end GwtMainPage
