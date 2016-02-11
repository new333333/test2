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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.event.AdministrationEvent;
import org.kablink.teaming.gwt.client.event.EditPersonalPreferencesEvent;
import org.kablink.teaming.gwt.client.event.GotoMyWorkspaceEvent;
import org.kablink.teaming.gwt.client.event.InvokeChangePasswordDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeDownloadDesktopAppEvent;
import org.kablink.teaming.gwt.client.event.InvokeHelpEvent;
import org.kablink.teaming.gwt.client.event.LoginEvent;
import org.kablink.teaming.gwt.client.event.LogoutEvent;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.event.ViewResourceLibraryEvent;
import org.kablink.teaming.gwt.client.event.ViewTeamingFeedEvent;
import org.kablink.teaming.gwt.client.profile.DiskUsageInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetDiskUsageInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetPasswordExpirationCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetSiteAdminUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.PasswordExpirationRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.AlertDlg.AlertDlgClient;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class displays the actions a user can perform from the
 * masthead.
 * 
 * @author jwootton
 */
public class UserActionsPopup extends TeamingPopupPanel
{
	private AlertDlg  m_trashInfoDlg;
	private FlowPanel m_namePanel;
	private FlexTable m_passwordExpirationTable;
	private FlexTable m_quotaTable;
	private FlowPanel m_footerPanel;
	private FlowPanel m_contentPanel;
	private Image     m_avatarImg;

	/**
	 * 
	 */
	public UserActionsPopup(
		String userName,
		boolean autoHide,
		boolean modal )
	{
		super( autoHide, modal );
	
		FlowPanel mainPanel;
		FlowPanel allContentPanel;
		FlowPanel topPanel;
		FlowPanel actionsPanel;
	
		// Tell this popup to 'roll down' when opening. 
		GwtClientHelper.rollDownPopup( this );
		
		setStyleName( "userActionsPopup" );
		addStyleName( "teamingPopupPanel_NoClip" );
	
		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "userActionsPopup_OutermostPanel" );
		
		// Add the triangle image to the top of the popup
		{
			FlowPanel topImgPanel;
			Image img;

			topImgPanel = new FlowPanel();
			topImgPanel.addStyleName( "userActionsPopup_TopImgPanel" );

			img = new Image( GwtTeaming.getImageBundle().triangle() );
			topImgPanel.add( img );

			mainPanel.add( topImgPanel );
		}
		
		allContentPanel = new FlowPanel();
		allContentPanel.addStyleName( "userActionsPopup_AllContentPanel" );
		mainPanel.add( allContentPanel );
		
		topPanel = new FlowPanel();
		allContentPanel.add( topPanel );
		
		// Create a place for the user's avatar and name to live in
		{
			FlexTable table;
			String avatarUrl;
			
			table = new FlexTable();
			table.addStyleName( "userActionsPopup_HeaderTable" );
			table.setCellPadding( 5 );
			
			// Create a panel for the avatar to live in
			avatarUrl = GwtTeaming.m_requestInfo.getUserAvatarUrl();
			if ( avatarUrl == null || avatarUrl.length() == 0 )
			{
				avatarUrl = GwtTeaming.getImageBundle().userAvatar().getSafeUri().asString();
			}
			{
				FlowPanel imgPanel;
				
				imgPanel = new FlowPanel();
				m_avatarImg = new Image( avatarUrl );
				m_avatarImg.addStyleName( "userActionsPopup_AvatarImg" );
				imgPanel.add( m_avatarImg );
				
				table.setWidget(0, 0, imgPanel );
				table.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP );
			}
			
			// Create a panel for the name to live in.
			{
				String quotaTitle;
				String usedTitle;
				Label quotaTitleLabel;
				Label quotaUsedLabel;
				
				m_namePanel = new FlowPanel();
				m_namePanel.addStyleName( "userActionsPopup_NamePanel" );
				m_namePanel.getElement().setInnerText( userName );

				// Add a table that will be used to hold information
				// about the user's password expiring.  When this popup
				// is shown we will issue an rpc request to get that
				// information.
				m_passwordExpirationTable = new FlexTable();
				m_passwordExpirationTable.setCellPadding( 0 );
				m_passwordExpirationTable.setCellSpacing( 0 );
				m_passwordExpirationTable.addStyleName( "statsTable" );
				m_passwordExpirationTable.setVisible( false );
				m_namePanel.add( m_passwordExpirationTable );
				
				String passwordExpirationTitle = GwtTeaming.getMessages().profileDataPasswordExpires();
				Label passwordExpirationTitleLabel = new Label( passwordExpirationTitle );
				m_passwordExpirationTable.setHTML( 0, 0, passwordExpirationTitleLabel.toString() );

				// Add a table that will be used to hold quota information.
				// When this popup is shown we will issue an rpc request to get the quota info
				m_quotaTable = new FlexTable();
				m_quotaTable.setCellPadding( 0 );
				m_quotaTable.setCellSpacing( 0 );
				m_quotaTable.addStyleName( "statsTable" );
				m_quotaTable.setVisible( false );
				m_namePanel.add( m_quotaTable );

				quotaTitle = GwtTeaming.getMessages().profileDataQuota();
				quotaTitleLabel = new Label( quotaTitle );
				m_quotaTable.setHTML( 0, 0, quotaTitleLabel.toString() );
				
				// Add the Quota used in the 2nd row.
				usedTitle = GwtTeaming.getMessages().profileQuotaUsed();
				quotaUsedLabel = new Label( usedTitle );
				m_quotaTable.setHTML( 1, 0, quotaUsedLabel.toString() );
				
				final Image trashInfoButton = GwtClientHelper.buildImage(GwtTeaming.getImageBundle().info2().getSafeUri().asString());
				trashInfoButton.addStyleName( "userActionsPopup_TrashInfoButton" );
				trashInfoButton.addClickHandler( new ClickHandler() {
					@Override
					public void onClick( ClickEvent event )
					{
						showTrashInfoAsync( trashInfoButton );
					}
				} );
				m_quotaTable.setWidget( 1, 2, trashInfoButton );
				m_quotaTable.getFlexCellFormatter().addStyleName( 1, 2, "userActionsPopup_TrashInfoCell" );

				table.setWidget( 0, 1, m_namePanel );
			}
			
			topPanel.add( table );
		}

		// Create the actions panel
		actionsPanel = createActionsPanel( userName );
		topPanel.add( actionsPanel );
		
		// Add a footer
		m_footerPanel = createFooterPanel();
		allContentPanel.add( m_footerPanel );
		
		setWidget( mainPanel );
	}

	/**
	 * 
	 */
	private FlowPanel addAction(
		String name,
		ImageResource imgResource,
		final VibeEventBase<?> actionEvent )
	{
		FlowPanel actionPanel;
		Image img;
		InlineLabel actionLabel;
		ClickHandler clickHandler;
		
		actionPanel = new FlowPanel();
		actionPanel.addStyleName( "userActionsPopup_ActionPanel" );
		
		img = new Image( imgResource );
		img.addStyleName( "userActionsPopup_ActionImg" );
		actionPanel.add( img );
		
		actionLabel = new InlineLabel( name );
		actionLabel.addStyleName( "userActionsPopup_ActionLabel" );
		actionPanel.add( actionLabel );

		clickHandler = new ClickHandler()
		{
			@Override
			public void onClick( ClickEvent event )
			{
				GwtClientHelper.deferCommand( new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						hide();
						
						GwtTeaming.fireEvent( actionEvent );
					}
				} );
			}
		};
		actionPanel.addDomHandler( clickHandler, ClickEvent.getType() );

		return actionPanel;
	}
	
	/**
	 * Issue an ajax request to see if the user has rights to run the "site administration" page.
	 * If they don't we will remove the "administration" menu item from the menu.
	 */
	private void checkAdminRights()
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
					// Yes
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						/**
						 * 
						 */
						@Override
						public void execute()
						{
							FlowPanel actionPanel;
							
							// Add "Administration"
							actionPanel = addAction(
												GwtTeaming.getMessages().adminMenuItem(),
												GwtTeaming.getImageBundle().userActionsPanel_Admin(),
												new AdministrationEvent() );
							m_contentPanel.insert( actionPanel, 0 );
							
							// Add the "Resource Library" menu item
							if ( GwtClientHelper.isLicenseFilr() == false )
							{
								actionPanel = addAction(
													GwtTeaming.getMessages().resourceLibMenuItem(),
													GwtTeaming.getImageBundle().resourceLibMenuImg(),
													new ViewResourceLibraryEvent() );
								m_contentPanel.insert( actionPanel, m_contentPanel.getWidgetCount()-1 );
							}
						}
					} );
				}
			}
		};
		
		// Issue an ajax request to get the url for the "site administration" action.
		{
			GetSiteAdminUrlCmd cmd;

			cmd = new GetSiteAdminUrlCmd( GwtTeaming.m_requestInfo.getBinderId() );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}

	/**
	 * 
	 */
	private FlowPanel createActionsPanel( String userName )
	{
		FlowPanel panel;
		FlowPanel actionPanel;
		
		panel = new FlowPanel();
		panel.addStyleName( "userActionsPopup_ActionsPanel" );
		
		m_contentPanel = new FlowPanel();
		m_contentPanel.addStyleName( "userActionsPopup_ActionsPanel_ContentPanel" );
		panel.add( m_contentPanel );

		// If the user can access their own personal workspace...
		if ( GwtClientHelper.getRequestInfo().getMyWorkspaceAccessible() )
		{
			// ...add "View Profile"
			actionPanel = addAction(
								GwtTeaming.getMessages().userActionsPanel_ViewProfile(),
								GwtTeaming.getImageBundle().userActionsPanel_ViewProfile(),
								new GotoMyWorkspaceEvent( true ) );	// true -> Force this to be a View Profile, even in Vibe.
			m_contentPanel.add( actionPanel );
		}
		
		// Add "Personal Preferences"
		actionPanel = addAction(
							GwtTeaming.getMessages().userActionsPanel_PersonalPreferences(),
							GwtTeaming.getImageBundle().userActionsPanel_PersonalPreferences(),
							new EditPersonalPreferencesEvent() );
		m_contentPanel.add( actionPanel );

		// Is this the user actions popup for other than the Guest or
		// an LDAP user?
		RequestInfo ri = GwtClientHelper.getRequestInfo();
		if ( ( null != ri ) && ( ! ( ri.isGuestUser() ) ) && ( ! ( ri.isLdapUser() )) )
		{
			// Yes!  Add "Change Password"
			actionPanel = addAction(
								GwtTeaming.getMessages().userActionsPanel_ChangePassword(),
								GwtTeaming.getImageBundle().userActionsPanel_ChangePassword(),
								new InvokeChangePasswordDlgEvent( null ) );	// null -> Don't show a password change hint.
			m_contentPanel.add( actionPanel );
		}
		
		// Add the "News Feed" option
		if ( GwtClientHelper.isLicenseFilr() == false )
		{
			actionPanel = addAction(
							GwtTeaming.getMessages().newsFeedMenuItem(),
							GwtTeaming.getImageBundle().newsFeedMenuImg(),
							new ViewTeamingFeedEvent() );
			m_contentPanel.add( actionPanel );
		}
		
		// Add "Help"
		actionPanel = addAction(
							GwtTeaming.getMessages().helpMenuItem(),
							GwtTeaming.getImageBundle().userActionsPanel_Help(),
							new InvokeHelpEvent() );
		m_contentPanel.add( actionPanel );

		// If the desktop application access in enabled...
		if ( GwtTeaming.getMainPage().getMainPageInfo().isDesktopAppEnabled() )
		{
			// ...add a link to download it.
			actionPanel = addAction(
								(GwtClientHelper.isLicenseFilr()              ?
									GwtTeaming.getMessages().downloadFilrDesktopApp() :
									GwtTeaming.getMessages().downloadVibeDesktopApp()),
								GwtTeaming.getImageBundle().userActionsPanel_DownloadDesktopApp(),
								new InvokeDownloadDesktopAppEvent() );
			m_contentPanel.add( actionPanel );
		}

		// Issue an ajax request to see if the user has rights to run the administration page.
		checkAdminRights();
		
		return panel;
	}
	
	/**
	 * 
	 */
	private FlowPanel createFooterPanel()
	{
		FlowPanel panel;
		
		panel = new FlowPanel();
		panel.addStyleName( "userActionsPopup_FooterPanel" );
		
		if ( GwtTeaming.m_requestInfo.isUserLoggedIn() )
		{
			InlineLabel signOutLabel;

			signOutLabel = new InlineLabel( GwtTeaming.getMessages().signOut() );
			signOutLabel.addStyleName( "userActionsPopup_SignOutLabel" );
			signOutLabel.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							hide();
							LogoutEvent.fireOne();
						}
					} );
				}
			} );
			panel.add( signOutLabel );
		}
		else
		{
			InlineLabel signInLabel;

			signInLabel = new InlineLabel( GwtTeaming.getMessages().login() );
			signInLabel.addStyleName( "userActionsPopup_SignOutLabel" );
			signInLabel.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							hide();
							LoginEvent.fireOne();
						}
					} );
				}
			} );
			panel.add( signInLabel );
		}
		
		return panel;
	}
	
	/**
	 * Issue an rpc request to get the quota data for the logged in user.
	 */
	private void getDiskQuotaDataFromServer()
	{
		GetDiskUsageInfoCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;

		final String binderId = GwtClientHelper.getRequestInfo().getCurrentUserWorkspaceId();
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				// display error
				GwtClientHelper.handleGwtRPCFailure(
												t,
												GwtTeaming.getMessages().rpcFailure_GetDiskUsageInfo(),
												binderId );
			}

			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				DiskUsageInfo info = null;
				
				if ( response.getResponseData() != null )
					info = (DiskUsageInfo) response.getResponseData();
				
				initQuotaDataUI( info );
			}
		};
		
		cmd = new GetDiskUsageInfoCmd( binderId );
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * Issue an rpc request to get the password expiration for the
	 * logged in user.
	 */
	private void getPasswordExpirationFromServer()
	{
		GwtClientHelper.executeCommand( new GetPasswordExpirationCmd(), new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				// display error
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetPasswordExpiration() );
			}

			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				initPasswordExpirationUI( (PasswordExpirationRpcResponseData ) response.getResponseData() );
			}
		} );
	}
	
	/**
	 * Hide the logout link.  We will do this if we are running in captive mode.
	 */
	public void hideLogoutLink()
	{
		if ( m_footerPanel != null )
			m_footerPanel.setVisible( false );
	}

	
	/**
	 * 
	 */
	public void init()
	{
		
	}
	
	/**
	 * Initialize the ui with the given quota data
	 */
	private void initQuotaDataUI( DiskUsageInfo quotaInfo )
	{
		if ( quotaInfo == null )
		{
			m_quotaTable.setVisible( false );
			return;
		}
		
		// Are quotas enabled for this user?
		if ( quotaInfo.isEnabled() )
		{
			InlineLabel quotaValueLabel;
			InlineLabel quotaUsedValueLabel;

			// Yes, display the quota and how much has been used.
			// Add the Data quota in the first row.
			quotaValueLabel = new InlineLabel( GwtTeaming.getMessages().profileQuotaMegaBytes( quotaInfo.getMaxQuota() ) );
			quotaValueLabel.addStyleName( "bold" );
			m_quotaTable.setHTML( 0, 1, quotaValueLabel.toString() );
			
			// Add the Quota used in the 2nd row.
			quotaUsedValueLabel = new InlineLabel( GwtTeaming.getMessages().profileQuotaMegaBytes( quotaInfo.getUsedQuota() ) );
			quotaUsedValueLabel.addStyleName( "bold" );
			m_quotaTable.setHTML( 1, 1, quotaUsedValueLabel.toString() );

			m_quotaTable.setVisible( true );
		}
		else
		{
			m_quotaTable.setVisible( false );
		}
	}
	
	/**
	 * Initialize the ui with the given password expiration
	 */
	private void initPasswordExpirationUI( PasswordExpirationRpcResponseData passwordExpiration )
	{
		String dateString = passwordExpiration.getDateString();
		if ( ! ( GwtClientHelper.hasString( dateString )))
		{
			m_passwordExpirationTable.setVisible( false );
			return;
		}
		
		// Add the expiration in the first row.
		InlineLabel l = new InlineLabel( dateString );
		l.addStyleName( "bold" );
		if ( passwordExpiration.isAboutToExpire() )
		{
			l.addStyleName( "red" );
		}
		m_passwordExpirationTable.setHTML( 0, 1, l.toString() );
		m_passwordExpirationTable.setVisible( true );
	}
	
	/*
	 * Asynchronously shows the trash information when the trash button
	 * is clicked.
	 */
	private void showTrashInfoAsync( final Widget trashInfoButton )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				showTrashInfoNow( trashInfoButton );
			}
		} );
	}
	
	/*
	 * Synchronously shows the trash information when the trash button
	 * is clicked.
	 */
	private void showTrashInfoNow( final Widget trashInfoButton )
	{
		// Have we created the trash information dialog yet?
		if ( null == m_trashInfoDlg )
		{
			// No!  Create it now...
			AlertDlg.createAsync( new AlertDlgClient()
			{
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
				
				@Override
				public void onSuccess( AlertDlg aDlg )
				{
					// ...and show it.
					m_trashInfoDlg = aDlg;
					m_trashInfoDlg.addStyleName( "userActionsPopup_TrashInfoDlg" );
					showTrashInfoImpl( trashInfoButton );
				}
			},
			true,		// true  -> Auto hide the dialog. 
			false );	// false -> The dialog is not modal.
		}
		
		else
		{
			// Yes, we've already created the trash information dialog!
			// Simply show it.
			showTrashInfoImpl( trashInfoButton );
		}
	}

	/*
	 * Implementation method that shows the trash information dialog.
	 */
	private void showTrashInfoImpl( final Widget trashInfoButton )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				AlertDlg.initAndShow(
					m_trashInfoDlg,
					GwtTeaming.getMessages().trashInformation(),
					trashInfoButton );
			}
		});
	}
	
	/**
	 * Shows this popup.
	 */
	@Override
	public void show()
	{
		super.show();

		// ...and add vertical scrolling to the main frame for the
		// ...duration of the popup.
		GwtClientHelper.scrollUIForPopup( this );
		
		// Issue an rpc request to get the quota information
		{
			GwtClientHelper.deferCommand( new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					getDiskQuotaDataFromServer();
				}
			} );
		}
		
		// Issue an rpc request to get the password expiration
		// information.
		{
			GwtClientHelper.deferCommand( new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					getPasswordExpirationFromServer();
				}
			} );
		}
		
		String avatarUrl = GwtTeaming.m_requestInfo.getUserAvatarUrl();
		if ( avatarUrl == null || avatarUrl.length() == 0 )
		     m_avatarImg.setUrl( GwtTeaming.getImageBundle().userAvatar().getSafeUri() );
		else m_avatarImg.setUrl( avatarUrl );
	}

	/**
	 * Hides this popup.
	 */
	@Override
	public void hide( boolean autoClosed )
	{
		// If we don't have a trash information dialog or the trash
		// information dialog is not attached to the DOM...
		if ( ( null == m_trashInfoDlg ) || ( ! ( m_trashInfoDlg.isAttached() ) ) )
		{
			// ...allow the popup to hide.
			super.hide( autoClosed );
		}
	}
}
