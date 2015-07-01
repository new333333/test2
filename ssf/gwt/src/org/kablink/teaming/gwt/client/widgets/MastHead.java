/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.widgets;

import org.kablink.teaming.gwt.client.event.ActivityStreamEnterEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent;
import org.kablink.teaming.gwt.client.event.AdministrationUpgradeCheckEvent;
import org.kablink.teaming.gwt.client.event.BrowseHierarchyEvent;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.ContextChangedEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.GetMastHeadLeftEdgeEvent;
import org.kablink.teaming.gwt.client.event.LoginEvent;
import org.kablink.teaming.gwt.client.event.MastheadHideEvent;
import org.kablink.teaming.gwt.client.event.MastheadShowEvent;
import org.kablink.teaming.gwt.client.event.SizeChangedEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.MastheadUnhighlightAllActionsEvent;
import org.kablink.teaming.gwt.client.GwtBrandingDataExt;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.mainmenu.GlobalSearchComposite;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderBrandingCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetSiteAdminUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetSiteBrandingCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetSystemBinderPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetSystemBinderPermalinkCmd.SystemBinderType;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnBrowseHierarchyInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.FilrActionsCtrl.FilrActionType;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget will display the MastHead
 * 
 * @author jwootton@Novell.com
 */
public class MastHead extends Composite
	implements ClickHandler,
		// Event handlers implemented by this class.
		ActivityStreamExitEvent.Handler,
		ContextChangedEvent.Handler,
		GetMastHeadLeftEdgeEvent.Handler,
		MastheadHideEvent.Handler,
		MastheadShowEvent.Handler,
		MastheadUnhighlightAllActionsEvent.Handler
{
	// Controls whether or not a beta note is displayed in Vibe's
	// masthead.
	private final static boolean SHOW_VIBE_BETA_NOTE = true;

	private BrandingPanel m_siteBrandingPanel = null;
	private BrandingPanel m_binderBrandingPanel = null;
	private RequestInfo m_requestInfo = null;
	private String m_mastheadBinderId = null;
	private FlowPanel m_mainMastheadPanel = null;
	private FlowPanel m_leftEdgePanel = null;
	private HorizontalPanel m_globalActionsPanel = null;
	private FlowPanel m_userNamePanel;
	private InlineLabel m_loginLink = null;
	private InlineLabel m_userName = null;
	private Label m_betaLabel = null;
	private UserActionsPopup m_userActionsPopup = null;
	private FilrActionsCtrl m_filrActionsCtrl;
	private String m_personalWorkspacesUrl = null;
	private FlowPanel m_browsePanel = null;

	private GwtBrandingData m_siteBrandingData = null;
	private GwtBrandingData m_binderBrandingData = null;
	
	// m_rpcGetSiteBrandingCallback is our callback that gets called when the ajax request to get the site branding data completes.
	private AsyncCallback<VibeRpcResponse> m_rpcGetSiteBrandingCallback = null;

	// m_rpcGetBinderBrandingCallback is our callback that gets called when the ajax request to get the binder branding data completes.
	private AsyncCallback<VibeRpcResponse> m_rpcGetBinderBrandingCallback = null;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.ACTIVITY_STREAM_EXIT,
		TeamingEvents.GET_MASTHEAD_LEFT_EDGE,

		// Context events.
		TeamingEvents.CONTEXT_CHANGED,
		
		// Masthead events.
		TeamingEvents.MASTHEAD_HIDE,
		TeamingEvents.MASTHEAD_SHOW,
		TeamingEvents.MASTHEAD_UNHIGHLIGHT_ALL_ACTIONS,
	};
		
	
	/**
	 * 
	 */
	public MastHead( RequestInfo requestInfo )
	{
		Scheduler.ScheduledCommand cmd;
		
		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this );
		
        m_requestInfo = requestInfo;
        m_mastheadBinderId = m_requestInfo.getBinderId();

		m_mainMastheadPanel = new FlowPanel();
		m_mainMastheadPanel.addStyleName( "mastHead" );
		
		m_leftEdgePanel = new FlowPanel();
		m_leftEdgePanel.addStyleName( "mastHead-LeftEdge ");
		m_mainMastheadPanel.add( m_leftEdgePanel );
		
		// Create a branding panel that will display "site" branding if it exists.
		m_siteBrandingPanel = new BrandingPanel( requestInfo );
		m_siteBrandingPanel.setVisible( false );
		m_mainMastheadPanel.add( m_siteBrandingPanel );
		
		// Create a branding panel that will display the binder branding.
		m_binderBrandingPanel = new BrandingPanel( requestInfo );
		m_binderBrandingPanel.setVisible( false );
		m_mainMastheadPanel.add( m_binderBrandingPanel );
		
		// We need the appropriate product name for displaying a beta
		// message and/or a licensing error.
		String productName;
		if (m_requestInfo.isLicenseFilr())
		     productName = GwtTeaming.getMessages().novellFilr();
		else productName = m_requestInfo.getProductName();

		// Is the license being used in error?
		boolean isLicenseExpired =    GwtClientHelper.isLicenseExpired();
		boolean isLicenseInvalid = (!(GwtClientHelper.isLicenseValid()));
		boolean licensingError = (isLicenseExpired || isLicenseInvalid);
		if (licensingError) {
			// Yes!  Display a licensing error!
			String licenseError;
			if (isLicenseExpired)
			     licenseError = GwtTeaming.getMessages().licenseExpired(productName);
			else licenseError = GwtTeaming.getMessages().licenseInvalid(productName);
			Label licenseErrorLabel = new Label(licenseError);
			licenseErrorLabel.addStyleName("mastheadLicenseError");
			m_mainMastheadPanel.add(licenseErrorLabel);
		}
		
		// Do we need to show that this is a beta version of Vibe?
		boolean showVibeBetaNote = (SHOW_VIBE_BETA_NOTE && (!licensingError) && (!(m_requestInfo.isLicenseFilr())));
		if (showVibeBetaNote) {
			// Yes!  Create a place for the beta text to go.
			m_betaLabel = new Label(GwtTeaming.getMessages().betaWithProduct(productName));
			m_betaLabel.addStyleName("mastheadBeta");
			m_mainMastheadPanel.add(m_betaLabel);
		}
		
		// Are we running Filr?
		if ( m_requestInfo.isLicenseFilr() )
		{
			GlobalSearchComposite globalSearchWidget;
			FlowPanel globalSearchPanel;
			
			// Yes
			// Create a link for the user to click on that will invoke "Users in the system" page
			if ( GwtTeaming.m_requestInfo.getAllowShowPeople() )
			{
				FlowPanel panel;
				FlowPanel imgPanel;
				Image img;
				
				panel = new FlowPanel();
				panel.getElement().setId( "mastheadFilr_PeoplePanel" );
				panel.addStyleName( "mastheadFilr_PeoplePanel" );
				
				imgPanel = new FlowPanel();
				imgPanel.addStyleName( "mastheadFilr_PeopleImgPanel" );
				img = new Image( GwtTeaming.getImageBundle().userList() );
				img.setTitle( GwtTeaming.getMessages().invokeUserListHint() );
				img.addStyleName( "mastheadFilr_PeopleImg" );
				img.addClickHandler( new ClickHandler()
				{
					@Override
					public void onClick( ClickEvent event )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								invokeUsersPage();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				} );
				imgPanel.add( img );
				panel.add( imgPanel );
				
				m_mainMastheadPanel.add( panel );
			}
			
			// Create a link for the user to click on that will invoke "what's new"
			{
				FlowPanel panel;
				FlowPanel imgPanel;
				Image img;
				
				panel = new FlowPanel();
				panel.getElement().setId( "mastheadFilr_WhatsNewPanel" );
				panel.addStyleName( "mastheadFilr_WhatsNewPanel" );
				
				imgPanel = new FlowPanel();
				imgPanel.addStyleName( "mastheadFilr_WhatsNewImgPanel" );
				img = new Image( GwtTeaming.getImageBundle().masthead_WhatsNew() );
				img.setTitle( GwtTeaming.getMessages().whatsNew() );
				img.addStyleName( "mastheadFilr_WhatsNewImg" );
				img.addClickHandler( new ClickHandler()
				{
					@Override
					public void onClick( ClickEvent event )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								invokeWhatsNew();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				} );
				imgPanel.add( img );
				panel.add( imgPanel );
				
				m_mainMastheadPanel.add( panel );
			}
			
			// Create a link for the user to click on that will display the browse control
			{
				FlowPanel imgPanel;
				Image img;
				
				m_browsePanel = new FlowPanel();
				m_browsePanel.getElement().setId( "mastheadFilr_BrowseFilrPanel" );
				m_browsePanel.addStyleName( "mastheadFilr_BrowseFilrPanel" );
				
				imgPanel = new FlowPanel();
				imgPanel.addStyleName( "mastheadFilr_BrowseFilrImgPanel" );
				img = new Image( GwtTeaming.getImageBundle().mastheadBrowseFilr() );
				img.setTitle( GwtTeaming.getMessages().masthead_BrowseFilr() );
				img.addStyleName( "mastheadFilr_BrowseFilrImg" );
				img.addClickHandler( new ClickHandler()
				{
					@Override
					public void onClick( ClickEvent event )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								displayBrowseControl( m_browsePanel );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				} );
				imgPanel.add( img );
				m_browsePanel.add( imgPanel );
				m_browsePanel.setVisible( false );
				
				m_mainMastheadPanel.add( m_browsePanel );
			}
			
			// Create a Filr Actions panel
			m_filrActionsCtrl = new FilrActionsCtrl();
			m_filrActionsCtrl.addStyleName( "mastheadFilrActionsPanel" );
			m_mainMastheadPanel.add( m_filrActionsCtrl );
			
			// Add the search control
			globalSearchPanel = new FlowPanel();
			globalSearchPanel.addStyleName( "mastheadFilrSearchComposite" );
			globalSearchWidget = new GlobalSearchComposite( false );
			globalSearchPanel.add( globalSearchWidget );
			
			m_mainMastheadPanel.add( globalSearchPanel );
		}
		
		// Create the panel that will hold the global actions such as Administration", "Logout" etc
		{
			m_globalActionsPanel = new HorizontalPanel();
			m_globalActionsPanel.addStyleName( "mastheadGlobalActionsPanel" );
			
			// Create a label that holds the logged-in user's name.
			{
				Image img;
				
				m_userNamePanel = new FlowPanel();
				m_userNamePanel.setStylePrimaryName( "mastheadUserName" );
				
				// Are we dealing with the guest user?
				if ( GwtClientHelper.isCurrentUserGuest() == false )
				{
					// No
					m_userNamePanel.addStyleName( "brandingLink" );
					m_userNamePanel.addDomHandler( this, ClickEvent.getType() );
				}
				else
				{
					// Yes
					m_userNamePanel.addStyleName( "guestLabel" );
				}
				
				m_globalActionsPanel.add( m_userNamePanel );

				String userName = requestInfo.getUserName();
				if ( !GwtClientHelper.hasString( userName ) )
				{
					userName = requestInfo.getUserLoginId();
				}
				m_userName = new InlineLabel( userName );
				m_userName.getElement().setId( "mhUserName" );
				m_userNamePanel.add( m_userName );

				if ( GwtClientHelper.isCurrentUserGuest() == false )
				{
					img = new Image( GwtTeaming.getImageBundle().mastheadActions2() );
					img.getElement().setAttribute( "align", "absmiddle" );
					m_userNamePanel.add( img );
				}
			}
			
			// Add login to the masthead.
			addLoginAction();

			m_mainMastheadPanel.add( m_globalActionsPanel );
		}
		
		// Create the callback that will be used when we issue an ajax call to get the site branding
		m_rpcGetSiteBrandingCallback = new AsyncCallback<VibeRpcResponse>()
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
					"Home Workspace" );
				
				//~JW:  Window.alert( "get site branding failed" );
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			@Override
			public void onSuccess( final VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				
				m_siteBrandingData = (GwtBrandingData) response.getResponseData();
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						// Update the site branding panel with the branding data
						m_siteBrandingPanel.updateBrandingPanel( m_siteBrandingData );

						// Issue an ajax request to get the binder branding
						getBinderBrandingDataFromServer();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}// end onSuccess()
		};

		// Create the callback that will be used when we issue an ajax call to get the binder branding
		m_rpcGetBinderBrandingCallback = new AsyncCallback<VibeRpcResponse>()
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
					m_mastheadBinderId );
				
				//~JW:  Window.alert( "get binder branding failed" );
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				
				m_binderBrandingData = (GwtBrandingData) response.getResponseData();

				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						// Update the binder branding panel with the branding data
						m_binderBrandingPanel.updateBrandingPanel( m_binderBrandingData );
						
						// Display site and binder branding based on the branding rule found in
						// the site branding.
						showBranding();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
				
			}// end onSuccess()
		};

		// Check to see if there are any upgrade actions that need to be performed.
		checkForUpgradeActions();
		
		// All composites must call initWidget() in their constructors.
		initWidget( m_mainMastheadPanel );

		// Issue an ajax request to get the site branding.
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				getSiteBrandingDataFromServer();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}// end MastHead()


	/**
	 * Adjust the height of the branding panels
	 */
	private void adjustBrandingPanelsHeight()
	{
		final int minHeight;
		Scheduler.ScheduledCommand cmd;

		if ( m_filrActionsCtrl != null )
			minHeight = m_filrActionsCtrl.getOffsetHeight() + 12;
		else
			minHeight = 50;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				Scheduler.ScheduledCommand cmd2;

				if ( m_siteBrandingPanel != null )
				{
					m_siteBrandingPanel.setMinHeight( minHeight );
					m_siteBrandingPanel.adjustBrandingPanelHeight();
				}
				
				cmd2 = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						if ( m_binderBrandingPanel != null )
						{
							m_binderBrandingPanel.setMinHeight( minHeight );
							m_binderBrandingPanel.adjustBrandingPanelHeight();
						}
					}
				};
				Scheduler.get().scheduleDeferred( cmd2 );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Add the "login" action to the global actions part of the masthead.
	 */
	private void addLoginAction()
	{
		Element linkElement;

		// Is the user logged in?
		if ( m_requestInfo.isUserLoggedIn() == false )
		{
			FlowPanel panel;
			
			// Add a separator
			{
				InlineLabel separator;
				
				separator = new InlineLabel( "|" );
				separator.addStyleName( "mastheadActionsSeparator" );
				m_globalActionsPanel.add( separator );
			}

			// No, add the "login" action.
			panel = new FlowPanel();
			panel.addStyleName( "brandingLink" );
			
			m_loginLink = new InlineLabel( GwtTeaming.getMessages().loginHint() );
			m_loginLink.addStyleName( "brandingLink" );
			m_loginLink.addClickHandler( this );
			m_loginLink.setTitle( GwtTeaming.getMessages().loginHint() );
			linkElement = m_loginLink.getElement();
			linkElement.setId( "mhLoginAction" );
			
			panel.add( m_loginLink );
			m_globalActionsPanel.add( panel );
		}
	}
	
	
	/**
	 * Issue an ajax request to see if the user has rights to run the "site administration" page.
	 * If they do we will check to see if there are any upgrade actions that need to be done.
	 */
	private void checkForUpgradeActions()
	{
		AsyncCallback<VibeRpcResponse> rpcCallback;

		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure( Throwable t )
			{
				// Note:  We don't pass a string here such as
				//   rpcFailure_GetSiteAdminUrl() because it would
				//   get displayed for guest, and all other
				//   non-admin users.  Not passing a string here
				//   allows the proper exception handling to occur
				//   but will NOT display an error to the user.
				GwtClientHelper.handleGwtRPCFailure( t );
			}
	
			/**
			 * 
			 * @param result
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				String url;
				StringRpcResponseData responseData;
				
				responseData = (StringRpcResponseData) response.getResponseData();
				url = responseData.getStringValue();
				
				// Did we get a url for the "site administration" action?
				if ( url != null && url.length() > 0 )
				{
					Scheduler.ScheduledCommand cmd;
					
					// Yes
					cmd = new Scheduler.ScheduledCommand()
					{
						/**
						 * 
						 */
						@Override
						public void execute()
						{
							// Since the user has administration rights, show them a list of
							// upgrade tasks that still need to be performed.
							// Sent event to check for tasks
							AdministrationUpgradeCheckEvent.fireOne();
							
							// Show the image the admin can click on to invoke the browse control.
							if ( m_browsePanel != null )
								m_browsePanel.setVisible( true );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		};
		
		// Issue an ajax request to get the url for the "site administration" action.
		{
			GetSiteAdminUrlCmd cmd;

			cmd = new GetSiteAdminUrlCmd( getBinderId() );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}

	/**
	 * Display the browse control
	 */
	private void displayBrowseControl( Widget relativeTo )
	{
		OnBrowseHierarchyInfo browseInfo;
		BrowseHierarchyEvent browseEvent;

		// Fire the event that will display the browse panel
		browseInfo = new OnBrowseHierarchyInfo( relativeTo );
		browseEvent = new BrowseHierarchyEvent();
		browseEvent.setOnBrowseHierarchyInfo( browseInfo );

		//GwtTeaming.fireEvent( new InvokeChangePasswordDlgEvent() );
		GwtTeaming.fireEvent( browseEvent );
	}
	
	/**
	 * Return the binder id we are working with.
	 */
	public String getBinderId()
	{
		return m_mastheadBinderId;
	}// end getBinderId()
	
	
	/**
	 * Return the branding data we are working with.
	 */
	public GwtBrandingData getBrandingData()
	{
		return m_binderBrandingPanel.getBrandingData();
	}// end GwtBrandingData()
	
	
	/**
	 * Issue an ajax request to get the binder branding data from the server.  Our AsyncCallback
	 * will be called when this request completes.
	 */
	private void getBinderBrandingDataFromServer()
	{
		// Do we have a binder id?
		if ( m_mastheadBinderId != null && m_mastheadBinderId.length() > 0 )
		{
			GetBinderBrandingCmd cmd;
			
			// Yes, Issue an ajax request to get the branding data for the given binder.
			cmd = new GetBinderBrandingCmd( m_mastheadBinderId );
			cmd.setUseInheritance( true );
			GwtClientHelper.executeCommand( cmd, m_rpcGetBinderBrandingCallback );
		}
	}// end getBinderBrandingDataFromServer()


	/**
	 * Return the height of the masthead
	 */
	public int getHeight()
	{
		int height;
		
		height = 0;
		if ( m_siteBrandingPanel.isVisible() )
			height += m_siteBrandingPanel.getOffsetHeight();
		
		if ( m_binderBrandingPanel.isVisible() )
			height += m_binderBrandingPanel.getOffsetHeight();
		
		// Are we running Filr?
		if ( m_requestInfo.isLicenseFilr() && m_filrActionsCtrl != null )
		{
			int filrActionsCtrlHeight;
			
			filrActionsCtrlHeight = m_filrActionsCtrl.getOffsetHeight() + 10;
			if ( filrActionsCtrlHeight > height )
				height = filrActionsCtrlHeight;
		}

		return height;
	}// end getHeight()
	
	
	/**
	 * Issue an ajax request to get the site branding data from the server.  Our AsyncCallback
	 * will be called when this request completes.
	 */
	private void getSiteBrandingDataFromServer()
	{
		GetSiteBrandingCmd cmd;
		
		// Issue an ajax request to get the site branding data.
		cmd = new GetSiteBrandingCmd();
		GwtClientHelper.executeCommand( cmd, m_rpcGetSiteBrandingCallback );
	}
	
	/**
	 * Hide the logout link.  We will do this if we are running in captive mode.
	 */
	public void hideLogoutLink()
	{
		if ( m_userActionsPopup != null )
			m_userActionsPopup.hideLogoutLink();
	}
	
	/**
	 * Highlight the appropriate panel in the masthead 
	 */
	private void highlightSelectedAction( String actionId )
	{
		Element panel;
		
		// Remove the highlighted style from all of the actions
		MastheadUnhighlightAllActionsEvent.fireOne();
		
		// Highlight the selected action
		panel = Document.get().getElementById( actionId );
		if ( panel != null )
			panel.addClassName( "FilrAction_Selected" );
	}
	


	/**
	 * 
	 */
	private void invokeUserActionsPopup()
	{
		if ( m_userActionsPopup == null )
		{
			m_userActionsPopup = new UserActionsPopup( m_userName.getText(), true, false );
		}

		m_userActionsPopup.setPopupPositionAndShow( new PopupPanel.PositionCallback()
		{
			@Override
			public void setPosition(int offsetWidth, int offsetHeight)
			{
				int right;
				int top;
				Style style;
				
				// Set the position of the popup
				right = 20;
				top = m_userNamePanel.getAbsoluteTop() + m_userNamePanel.getOffsetHeight() + 12;
				style = m_userActionsPopup.getElement().getStyle();
				style.setRight( right, Unit.PX );
				style.clearLeft();
				style.setTop( top, Unit.PX );
			}
		} );
	}
	
	/**
	 * 
	 */
	private void invokeUsersPage()
	{
		// Do we have the url to the "personal workspaces" binder?
		if ( m_personalWorkspacesUrl == null )
		{
			GetSystemBinderPermalinkCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback;

			// No
			rpcCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					// Note:  We don't pass a string here such as
					//   rpcFailure_GetSiteAdminUrl() because it would
					//   get displayed for guest, and all other
					//   non-admin users.  Not passing a string here
					//   allows the proper exception handling to occur
					//   but will NOT display an error to the user.
					GwtClientHelper.handleGwtRPCFailure( t );
				}
		
				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					String url;
					StringRpcResponseData responseData;
					
					responseData = (StringRpcResponseData) response.getResponseData();
					url = responseData.getStringValue();
					
					// Did we get a url for the "personal workspace"?
					if ( url != null && url.length() > 0 )
					{
						Scheduler.ScheduledCommand cmd;
						
						// Yes
						m_personalWorkspacesUrl = url;
						cmd = new Scheduler.ScheduledCommand()
						{
							/**
							 * 
							 */
							@Override
							public void execute()
							{
								invokeUsersPage();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				}
			};
			
			// Issue an ajax request to get the "personal workspaces" url
			cmd = new GetSystemBinderPermalinkCmd( SystemBinderType.PROFILE_ROOT );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
		else
		{
			OnSelectBinderInfo osbInfo;
			
			// Invoke the "Users" page.
			osbInfo = new OnSelectBinderInfo(
										m_personalWorkspacesUrl,
										Instigator.GOTO_CONTENT_URL );
			GwtTeaming.fireEvent( new ChangeContextEvent( osbInfo ) );

			// Highlight the "Show People" panel.
			highlightSelectedAction( "mastheadFilr_PeoplePanel" );
		}
	}
	
	/**
	 * Invoke the Whats New page
	 */
	private void invokeWhatsNew()
	{
		ActivityStreamInfo asi;
		ActivityStream as;
		
		// Highlight the "Whats new" panel.
		highlightSelectedAction( "mastheadFilr_WhatsNewPanel" );
		
		FilrActionsCtrl.closeAdminConsole();

		asi = new ActivityStreamInfo();

		// Get the selected action from the FilrActionsCtrl
		if ( m_filrActionsCtrl != null )
		{
			FilrActionType filrActionType;

			filrActionType = m_filrActionsCtrl.getSelectedActionType();

			// Figure out which collection point is selected and invoke "what's new"
			// on that collection point.
			switch ( filrActionType )
			{
			case MY_FILES:
				as = ActivityStream.MY_FILES;
				break;
			
			case NET_FOLDERS:
				as = ActivityStream.NET_FOLDERS;
				break;
				
			case SHARED_BY_ME:
				as = ActivityStream.SHARED_BY_ME;
				break;
				
			case SHARED_WITH_ME:
				as = ActivityStream.SHARED_WITH_ME;
				break;
			
			case SHARED_PUBLIC:
				as = ActivityStream.SHARED_PUBLIC;
				break;
			
			case UNKNOWN:
			default:
				as = ( GwtClientHelper.isGuestUser() ? ActivityStream.SHARED_PUBLIC : ActivityStream.SHARED_WITH_ME );
				break;
			}
		}
		else
		{
			as = ActivityStream.CURRENT_BINDER;
			//~JW:  asi.setBinderId( m_mastheadBinderId );
		}

		asi.setActivityStream( as );
		
		GwtTeaming.fireEvent( new ActivityStreamEnterEvent( asi, ActivityStreamDataType.OTHER ) );
	}
	
	/**
	 * Handles ActivityStreamExitEvent's received by this class.
	 *
	 * Implements the ActivityStreamExitEvent.Handler.onActivityStreamExit() method.
	 * 
	 * @param event
	 */
	@Override
	public void onActivityStreamExit( ActivityStreamExitEvent event )
	{
		unhighlightAllActions();
	}

	/**
	 * This method gets called when the user clicks on something in the branding panel.
	 */
	@Override
	public void onClick( ClickEvent event )
	{
		Scheduler.ScheduledCommand cmd;
		final Widget eventSource;
		
		// Get the widget that was clicked on.
		eventSource = (Widget) event.getSource();

		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute() 
			{
				if ( eventSource == m_loginLink )
				{
					LoginEvent loginEvent;
					String refererUrl;
					
					// When the user logs in, send them to the base url.
					loginEvent = new LoginEvent();
					refererUrl = Window.Location.getProtocol() + "//" + Window.Location.getHost();
					loginEvent.setRefererUrl( refererUrl );
					
					GwtTeaming.fireEvent( loginEvent );
				}
				else if ( eventSource == m_userName || eventSource == m_userNamePanel )
				{
					invokeUserActionsPopup();
				}
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}// end onClick()
	
	
	/**
	 * Refresh the branding.  We will refresh the site branding first and then refresh the binder
	 * branding.
	 */
	public void refreshBranding()
	{
		// refreshSiteBranding() will call refreshBinderBranding() after it gets the site branding.
		refreshSiteBranding();
	}
	
	/**
	 * Refresh the site branding by issuing an ajax request to get the site branding data . 
	 */
	private void refreshSiteBranding()
	{
		getSiteBrandingDataFromServer();
	}// end refreshSiteBranding()

	
	/**
	 * Set the id of the binder the masthead is dealing with.
	 */
	private void setBinderId( String binderId )
	{
		if ( binderId != null && binderId.length() > 0 )
		{
			// Did the binder id change?
			if ( m_mastheadBinderId == null || m_mastheadBinderId.equalsIgnoreCase( binderId ) == false )
			{
				// Yes
				m_mastheadBinderId = binderId;
				
				// Issue an ajax request to get the binder branding data for the given binder.
				refreshBranding();
			}
		}
	}// end setBinderId()

	
	/**
	 * Set the font color used in the "global actions" panel and in the FilrActions widget.
	 */
	private void setMastheadFontColor()
	{
		Element element;
		Style style;
		String fontColor;
		GwtBrandingData brandingData;
		
		// If the site branding is visible get the font color from it.  Otherwise, get the font color
		// from the binder branding.
		{
			if ( m_siteBrandingPanel.isVisible() )
				brandingData = m_siteBrandingData;
			else
				brandingData = m_binderBrandingData;

			fontColor = brandingData.getFontColor();
		}
		
		// For the given branding data, set the color of the font used in the "global actions" part of the branding panel
		element = m_globalActionsPanel.getElement();
		style = element.getStyle();
			
		// Do we have a font color?
		if ( fontColor != null && fontColor.length() > 0 )
		{
			// Yes
			// Change the color of the font used to display the user's name.
			style.setColor( fontColor );
			
		}
		else
		{
			// Go back to the font color defined in the style sheet.
			style.clearColor();
		}

		// Set the font color used by the FilrActionsCtrl
		if ( m_filrActionsCtrl != null )
			m_filrActionsCtrl.setFontColor( fontColor );

		if ( m_betaLabel != null )
		{
			element = m_betaLabel.getElement();
			style = element.getStyle();

			// Do we have a font color?
			if ( fontColor != null && fontColor.length() > 0 )
			{
				// Yes
				// Change the color of the font used to display the beta text
				style.setColor( fontColor );
			}
			else
			{
				// Go back to the font color defined in the style sheet.
				style.clearColor();
			}
		}
	}
	
	
	/**
	 * 
	 */
	@Override
	public void setVisible( boolean visible )
	{
		super.setVisible( visible );

		if ( visible == true )
		{
			adjustBrandingPanelsHeight();
		}
	}
	
	
	/**
	 * Show the site and binder branding based on the branding rule found in the site branding.
	 */
	private void showBranding()
	{
		// Do we have any site branding?
		if ( m_siteBrandingData.haveBranding() == false )
		{
			// No, just show the binder branding.
			m_siteBrandingPanel.setVisible( false );
			m_binderBrandingPanel.setVisible( true );
			
		}
		else
		{
			String siteBinderId;
			String binderId;
			
			// We have site branding.
			// Did the site branding and the binder branding come from the same binder?
			siteBinderId = m_siteBrandingData.getBinderId();
			binderId = m_binderBrandingData.getBinderId();
			if ( siteBinderId.equalsIgnoreCase( binderId ) )
			{
				// Yes, just display the binder branding.
				m_siteBrandingPanel.setVisible( false );
				m_binderBrandingPanel.setVisible( true );
			}
			else
			{
				GwtBrandingDataExt.BrandingRule brandingRule;
				
				// Display site and binder branding based on the branding rule found
				// in the site branding.
				brandingRule = m_siteBrandingData.getBrandingRule();
				switch ( brandingRule )
				{
				case DISPLAY_SITE_BRANDING_ONLY:
					m_siteBrandingPanel.setVisible( true );
					m_binderBrandingPanel.setVisible( false );
					break;
					
				case DISPLAY_BOTH_SITE_AND_BINDER_BRANDING:
					m_siteBrandingPanel.setVisible( true );
					
					// Do we have binder branding?
					if ( m_binderBrandingData.haveBranding() )
					{
						// Yes
						m_binderBrandingPanel.setVisible( true );
					}
					else
					{
						// No
						m_binderBrandingPanel.setVisible( false );
					}
					break;
					
				case BINDER_BRANDING_OVERRIDES_SITE_BRANDING:
					// Do we have binder branding?
					if ( m_binderBrandingData.haveBranding() )
					{
						// Yes
						m_siteBrandingPanel.setVisible( false );
						m_binderBrandingPanel.setVisible( true );
					}
					else
					{
						m_siteBrandingPanel.setVisible( true );
						m_binderBrandingPanel.setVisible( false );
					}
					break;
					
				case BRANDING_RULE_UNDEFINED:
					m_siteBrandingPanel.setVisible( false );
					m_binderBrandingPanel.setVisible( true );
					break;
					
				default:
					Window.alert( "Unknown branding rule" );
					break;
				}// end switch()
			}
		}
		
		// Set the font color used in the mast head
		setMastheadFontColor();

		// Adjust the height of the branding panels.
		adjustBrandingPanelsHeight();
	}// end showBranding()
	
	/**
	 * Unhighlight all sub-actions
	 */
	private void unhighlightAllActions()
	{
		Element panel;
		
		panel = Document.get().getElementById( "mastheadFilr_PeoplePanel" );
		if ( panel != null )
			panel.removeClassName( "FilrAction_Selected" );
		
		panel = Document.get().getElementById( "mastheadFilr_WhatsNewPanel" );
		if ( panel != null )
			panel.removeClassName( "FilrAction_Selected" );
	}
	
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
		OnSelectBinderInfo osbInfo = event.getOnSelectBinderInfo();
		if ( GwtClientHelper.validateOSBI( osbInfo, false ))
		{
			BinderInfo bi = osbInfo.getBinderInfo();
			if ( GwtClientHelper.isBinderInfoMyFilesHome( bi ) )
			{
				bi = GwtClientHelper.buildMyFilesBinderInfo();
			}
			setBinderId( bi.getBinderId() );
		}
	}// end onContextChanged()
	
	/**
	 * Handles GetMastHeadLeftEdgeEvent's received by this class.
	 * 
	 * Implements the GetMastHeadLeftEdgeEvent.Handler.onGetMastHeadLeftEdge() method.
	 * 
	 * @param event
	 */
	@Override
	public void onGetMastHeadLeftEdge( final GetMastHeadLeftEdgeEvent event )
	{
		event.getMastHeadLeftEdgeCallback().mhLeftEdgeWidget( m_leftEdgePanel );
	}// end onGetMastHeadLeftEdge()
	
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
		setVisible( false );
		
		SizeChangedEvent.fireOne();
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
		setVisible( true );
		
		SizeChangedEvent.fireOne();
	}// end onMastheadShow()
	
	
	/**
	 * Handles the MastheadUnhighlightAllActionsEvent received by this class.
	 */
	@Override
	public void onMastheadUnhighlightAllActions( MastheadUnhighlightAllActionsEvent event )
	{
		unhighlightAllActions();
	}
}// end MastHead
