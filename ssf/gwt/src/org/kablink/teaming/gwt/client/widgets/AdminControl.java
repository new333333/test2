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
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.event.EditSiteBrandingEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.InvokeConfigureAdhocFoldersDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeConfigureFileSyncAppDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeConfigureUserAccessDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageNetFolderRootsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageGroupsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageNetFoldersDlgEvent;
import org.kablink.teaming.gwt.client.event.PreLogoutEvent;
import org.kablink.teaming.gwt.client.event.SidebarHideEvent;
import org.kablink.teaming.gwt.client.event.SidebarShowEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.AdminConsoleInfo;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtFileSyncAppConfiguration;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.admin.AdminAction;
import org.kablink.teaming.gwt.client.admin.GwtAdminAction;
import org.kablink.teaming.gwt.client.admin.GwtAdminCategory;
import org.kablink.teaming.gwt.client.admin.GwtUpgradeInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetAdminActionsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFileSyncAppConfigurationCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetUpgradeInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveFileSyncAppConfigurationCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.AdminInfoDlg.AdminInfoDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigureAdhocFoldersDlg.ConfigureAdhocFoldersDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigureFileSyncAppDlg.ConfigureFileSyncAppDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigureUserAccessDlg.ConfigureUserAccessDlgClient;
import org.kablink.teaming.gwt.client.widgets.ContentControl.ContentControlClient;
import org.kablink.teaming.gwt.client.widgets.ManageGroupsDlg.ManageGroupsDlgClient;
import org.kablink.teaming.gwt.client.widgets.ManageNetFolderRootsDlg.ManageNetFolderRootsDlgClient;
import org.kablink.teaming.gwt.client.widgets.ManageNetFoldersDlg.ManageNetFoldersDlgClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.gwt.user.client.ui.UIObject;

/**
 * This widget will display the controls that make up the "Administration" control.
 * There is a widget that displays the list of administration actions and a widget
 * that displays the page for the selected administration action.
 * 
 * @author jwootton@novell.com
 */
public class AdminControl extends TeamingPopupPanel
	implements 
	// Event handlers implemented by this class.
		InvokeConfigureAdhocFoldersDlgEvent.Handler,
		InvokeConfigureFileSyncAppDlgEvent.Handler,
		InvokeConfigureUserAccessDlgEvent.Handler,
		InvokeManageNetFoldersDlgEvent.Handler,
		InvokeManageNetFolderRootsDlgEvent.Handler,
		InvokeManageGroupsDlgEvent.Handler,
		PreLogoutEvent.Handler,
		SidebarHideEvent.Handler,
		SidebarShowEvent.Handler
{
	private AdminActionsTreeControl m_adminActionsTreeControl = null;
	private ContentControl m_contentControl = null;
	private int m_contentControlX;
	private int m_contentControlY;
	private int m_contentControlWidth;
	private int m_contentControlHeight;
	private int m_dlgWidth;
	private int m_dlgHeight;
	private String m_homePageUrl = null;
	private ConfigureFileSyncAppDlg m_configureFileSyncAppDlg = null;
	private ManageGroupsDlg m_manageGroupsDlg = null;
	private ManageNetFoldersDlg m_manageNetFoldersDlg = null;
	private ManageNetFolderRootsDlg m_manageNetFolderRootsDlg = null;
	private ConfigureUserAccessDlg m_configureUserAccessDlg = null;
	private ConfigureAdhocFoldersDlg m_configureAdhocFoldersDlg = null;

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		// Administration events.
		TeamingEvents.INVOKE_CONFIGURE_ADHOC_FOLDERS_DLG,
		TeamingEvents.INVOKE_CONFIGURE_FILE_SYNC_APP_DLG,
		TeamingEvents.INVOKE_CONFIGURE_USER_ACCESS_DLG,
		TeamingEvents.INVOKE_MANAGE_NET_FOLDERS_DLG,
		TeamingEvents.INVOKE_MANAGE_NET_FOLDER_ROOTS_DLG,
		TeamingEvents.INVOKE_MANAGE_GROUPS_DLG,
		
		// Login/out events.
		TeamingEvents.PRE_LOGOUT,
		
		// Sidebar events.
		TeamingEvents.SIDEBAR_HIDE,
		TeamingEvents.SIDEBAR_SHOW,
	};
	
	/**
	 * Class used for the ui for an administration action.
	 */
	private class AdminActionControl extends Composite
		implements ClickHandler, MouseOverHandler, MouseOutHandler
	{
		private GwtAdminAction m_adminAction;
		private InlineLabel m_actionName;
		
		/**
		 * 
		 */
		public AdminActionControl( GwtAdminAction adminAction )
		{
			FlowPanel mainPanel;
			String id;
			
			// Remember the action we are associated with.
			m_adminAction = adminAction;
			
			mainPanel = new FlowPanel();
			
			m_actionName = new InlineLabel( adminAction.getLocalizedName() );
			m_actionName.addClickHandler( this );
			m_actionName.addMouseOverHandler( this );
			m_actionName.addMouseOutHandler( this );
			m_actionName.addStyleName( "adminActionControl" );
			m_actionName.addStyleName( "cursorPointer" );
			
			// For automation purposes, give the label a unique id.
			id = "adminAction-" + String.valueOf( adminAction.getActionType().ordinal() );
			m_actionName.getElement().setId( id );
			
			mainPanel.add( m_actionName );
			
			// All composites must call initWidget() in their constructors.
			initWidget( mainPanel );
			
		}// end AdminActionControl()
		
		/**
		 * 
		 */
		@Override
		public void onClick( ClickEvent event )
		{
			// Tell the AdminControl that an action was selected.
			adminActionSelected( m_adminAction );
		}// end onClick()
		
		/**
		 * 
		 */
		@Override
		public void onMouseOver( MouseOverEvent event )
		{
			m_actionName.addStyleName( "adminActionControlMouseOver" );
		}// end onMouseOver()
		
		
		/**
		 * 
		 */
		@Override
		public void onMouseOut( MouseOutEvent event )
		{
			m_actionName.removeStyleName( "adminActionControlMouseOver" );
		}// end onMouseOut()
	}// end AdminActionControl
	
	
	/**
	 * Class used for the ui for an administration category.
	 */
	private class AdminCategoryControl extends Composite
		implements ClickHandler
	{
		private FlexTable m_mainTable;
		private FlexTable m_actionsTable;
		private Image m_expandedImg;
		private Image m_collapsedImg;
		
		/**
		 * 
		 */
		public AdminCategoryControl( GwtAdminCategory category )
		{
			ArrayList<GwtAdminAction> actions = null;
			ImageResource imgResource;
			FlexTable.FlexCellFormatter cellFormatter; 
			int row = 0;

			// Create the "expanded" and "collapsed" images.
			imgResource = GwtTeaming.getWorkspaceTreeImageBundle().tree_closer();
			m_expandedImg = new Image( imgResource );
			m_expandedImg.addStyleName( "cursorPointer" );
			m_expandedImg.addClickHandler( this );
			imgResource = GwtTeaming.getWorkspaceTreeImageBundle().tree_opener();
			m_collapsedImg = new Image( imgResource );
			m_collapsedImg.addStyleName( "cursorPointer" );
			m_collapsedImg.addClickHandler( this );

			m_mainTable = new FlexTable();
			cellFormatter = m_mainTable.getFlexCellFormatter();
			
			// Add the category image and name.
			{
				InlineLabel categoryName;
				Image img;
				GwtAdminCategory.GwtAdminCategoryType categoryType;
				
				m_mainTable.setWidget( row, 0, m_expandedImg );
				
				// Add the image associated with this category.
				categoryType = category.getCategoryType();
				if ( categoryType == GwtAdminCategory.GwtAdminCategoryType.MANAGEMENT )
				{
					imgResource = GwtTeaming.getImageBundle().management16();
					img = new Image( imgResource );
				}
				else if ( categoryType == GwtAdminCategory.GwtAdminCategoryType.REPORTS )
				{
					imgResource = GwtTeaming.getImageBundle().report16();
					img = new Image( imgResource );
				}
				else if ( categoryType == GwtAdminCategory.GwtAdminCategoryType.SYSTEM )
				{
					imgResource = GwtTeaming.getImageBundle().system16();
					img = new Image( imgResource );
				}
				else
				{
					imgResource = GwtTeaming.getImageBundle().management16();
					img = new Image( imgResource );
				}
				
				m_mainTable.setWidget( row, 1, img );
				
				// Add the category name
				categoryName = new InlineLabel( category.getLocalizedName() );
				categoryName.addStyleName( "adminCategoryName" );
				m_mainTable.setWidget( row, 2, categoryName );
				cellFormatter.setWidth( row, 2, "100%" );
				
				imgResource = GwtTeaming.getImageBundle().spacer1px();
				img = new Image( imgResource );
				m_mainTable.setWidget( row, 3, img );
				cellFormatter.setWidth( row, 3, "100%" );
				
				++row;
			}

			// Create a table to hold all the actions.
			m_actionsTable = new FlexTable();
			cellFormatter.setColSpan( row, 1, 2 );
			m_mainTable.setWidget( row, 1, m_actionsTable );
			
			// Add a ui widget for all of the actions associated with this category.
			actions = category.getActions();
			if ( actions != null )
			{
				for (GwtAdminAction action : actions )
				{
					AdminActionControl adminActionControl;
					
					// Add a ui widget for this administration action.
					adminActionControl = new AdminActionControl( action );
					m_actionsTable.setWidget( m_actionsTable.getRowCount(), 0, adminActionControl );
				}
			}
			
			// All composites must call initWidget() in their constructors.
			initWidget( m_mainTable );

		}// end AdminCategoryControl()
		
		/**
		 * Hide all the actions associated with this category.
		 */
		public void hideActions()
		{
			if ( m_actionsTable.isVisible() )
			{
				m_actionsTable.setVisible( false );
				relayoutPage();
			}
		}// end hideActions()
		
		/**
		 * This method gets called when the user clicks on the "expand" or "collapse" image.
		 */
		@Override
		public void onClick( ClickEvent event )
		{
			if ( event.getSource() == m_expandedImg )
			{
				m_mainTable.setWidget( 0, 0, m_collapsedImg );
				hideActions();
			}
			else if ( event.getSource() == m_collapsedImg )
			{
				m_mainTable.setWidget( 0, 0, m_expandedImg );
				showActions();
			}
		}// end onClick()
		
		
		/**
		 * Show all the actions associated with this category.
		 */
		public void showActions()
		{
			if ( !m_actionsTable.isVisible() )
			{
				m_actionsTable.setVisible( true );
				relayoutPage();
			}
		}// end showActions()
	}// end AdminCategoryControl
	
	
	/**
	 * 
	 */
	private class AdminActionsTreeControl extends Composite
	{
		// m_rpcGetAdminActionsCallback is our callback that gets called when the ajax request to get the administration actions completes.
		private AsyncCallback<VibeRpcResponse> m_rpcGetAdminActionsCallback = null;
		private AsyncCallback<VibeRpcResponse> m_rpcGetUpgradeInfoCallback2 = null;
		private FlexTable m_mainTable;
		
		/**
		 * 
		 */
		public AdminActionsTreeControl()
		{
			FlowPanel mainPanel;
			
			mainPanel = new FlowPanel();
			mainPanel.addStyleName( "adminActionsTreeControl" );
			
			// Create a table that will hold all the top level administration categories
			m_mainTable = new FlexTable();
			m_mainTable.addStyleName( "adminCategoriesTable" );
			m_mainTable.setCellPadding( 0 );
			m_mainTable.setCellSpacing( 0 );
			m_mainTable.setWidth("100%");
			
			mainPanel.add( m_mainTable );
			
			// Add the header
			{
				Label header;
				FlexTable table;
				FlexTable.FlexCellFormatter cellFormatter;
				
				table = new FlexTable();
				table.addStyleName( "adminActionsTreeControlHeader" );
				table.setCellPadding( 0 );
				table.setCellSpacing( 0 );
				
				cellFormatter = table.getFlexCellFormatter();
				
				header = new Label( GwtTeaming.getMessages().administrationHeader() );
				header.addStyleName( "adminActionsTreeControlHeader2" );
				table.setWidget( 0, 0, header );
				cellFormatter.setWordWrap( 0, 0, false );
				
				// Add an image the user can click on to get administration information
				{
					ImageResource imgResource;
					Image img;

					imgResource = GwtTeaming.getImageBundle().info2();
					img = new Image( imgResource );
					img.addStyleName( "cursorPointer" );
					if ( GwtClientHelper.jsIsIE() )
						img.addStyleName( "margin-right-22" );
					else
						img.addStyleName( "margin-right-5" );
					img.addClickHandler( new ClickHandler()
					{
						@Override
						public void onClick( ClickEvent event  )
						{
							Scheduler.ScheduledCommand cmd;
							
							cmd = new Scheduler.ScheduledCommand()
							{
								/**
								 * 
								 */
								@Override
								public void execute()
								{
									// Issue an ajax request to get the upgrade information from the server.
									// When we get the response the callback will open the AdminInfoDlg.
									getUpgradeInfoFromServer( m_rpcGetUpgradeInfoCallback2 );
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}// end onClick()
					}
					);
					table.setWidget( 0, 1, img );
					cellFormatter.setHorizontalAlignment( 0, 1, HasHorizontalAlignment.ALIGN_LEFT );
					cellFormatter.setWidth( 0, 1, "100%" );
				}
				
				// Add a close button
				if ( GwtTeaming.m_requestInfo.isLicenseFilr() )
				{
					InlineLabel label;
					
					label = new InlineLabel( GwtTeaming.getMessages().close() );
					label.addStyleName( "adminControl_CloseLabel" );
					label.addClickHandler( new ClickHandler()
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
									// Fire the event that closes the admin console.
									FilrActionsCtrl.closeAdminConsole();
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					} );
					
					table.setWidget( 0, 2, label );
					cellFormatter.setHorizontalAlignment( 0, 2, HasHorizontalAlignment.ALIGN_RIGHT );
				}
				
				m_mainTable.setWidget( 0, 0, table );
			}
			
			// Create the callback that will be used when we issue an ajax call to get the administration actions.
			m_rpcGetAdminActionsCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetAdminActions(),
						GwtMainPage.m_requestInfo.getBinderId() );
				}// end onFailure()
		
				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					ArrayList<GwtAdminCategory> adminCategories;
					AdminConsoleInfo adminConsoleInfo;
					
					adminConsoleInfo = (AdminConsoleInfo) response.getResponseData();
					adminCategories = adminConsoleInfo.getCategories();
					for ( GwtAdminCategory category : adminCategories )
					{
						// Add this administration category to the page.
						addCategory( category );
					}
					
					m_homePageUrl = adminConsoleInfo.getHomePageUrl();
					showHomePage();
				}
			};

			// Create the callback that will be used when we issue an ajax call to get upgrade information
			m_rpcGetUpgradeInfoCallback2 = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetUpgradeInfo() );
				}// end onFailure()
		
				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					int x;
					int y;
					GwtUpgradeInfo upgradeInfo;
					
					upgradeInfo = (GwtUpgradeInfo) response.getResponseData();
					
					// Show the AdminInfoDlg
					x = m_adminActionsTreeControl.getAbsoluteLeft() + m_adminActionsTreeControl.getOffsetWidth();
					y = m_adminActionsTreeControl.getAbsoluteTop();
					showAdminInfoDlg( upgradeInfo, x, y );
				}// end onSuccess()
			};

			// Issue a deferred command to get the administration actions the user has rights to run.
			{
				Scheduler.ScheduledCommand cmd;

				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						getAdminActionsFromServer();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
			
			initWidget( mainPanel );
		}// end AdminActionsTreeControl()
		
		
		/**
		 * Add an administration category (ie Reports) to the page along with all the actions
		 * in the category.
		 */
		private void addCategory( GwtAdminCategory category )
		{
			AdminCategoryControl categoryControl;
			int row;
			
			// Create a ui control for this category.
			categoryControl = new AdminCategoryControl( category );
			
			// Add the ui control to the table.
			row = m_mainTable.getRowCount();
			m_mainTable.setWidget( row, 0, categoryControl );
		}// end addCategory()
		
		/**
		 * Issue an ajax request to get the list of administration actions the user
		 * has rights to run.
		 */
		public void getAdminActionsFromServer()
		{
			String binderId;
			GetAdminActionsCmd cmd;
			
			// Issue an ajax request to get the administration actions the user has rights to perform.
			binderId = GwtMainPage.m_requestInfo.getBinderId();
			cmd = new GetAdminActionsCmd( binderId );
			GwtClientHelper.executeCommand( cmd, m_rpcGetAdminActionsCallback );
		}
	}// end AdminActionsTreeControl

	
	/*
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private AdminControl( GwtMainPage mainPage )
	{
		super( false, false );
		
		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this );
		
		final FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName( "adminControl" );
		
		// Create the control that holds all of the administration actions
		m_adminActionsTreeControl = new AdminActionsTreeControl();
		mainPanel.add( m_adminActionsTreeControl );
		
		// We need to replace gwt-PopupPanel style name because it is causing an empty
		// box to be displayed because initially this control's width and height are 0.
		setStylePrimaryName( "adminControlPopup" );
		
		// Create a control to hold the administration page for the selection administration action.
		ContentControl.createAsync(
				mainPage,
				"adminContentControl",
				new ContentControlClient()
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
				m_contentControl = contentCtrl;
				m_contentControl.setVisible( false );
				m_contentControl.addStyleName( "adminContentControl" );
				mainPanel.add( m_contentControl );
				relayoutPage();
			}// end onSuccess()
		} );
		
		setWidget( mainPanel );
	}// end AdminControl()

	/**
	 * This method gets called when the user selects one of the administration actions.
	 */
	private void adminActionSelected( GwtAdminAction adminAction )
	{
		// Are we dealing with the "Site Branding" action?
		if ( adminAction.getActionType() == AdminAction.SITE_BRANDING )
		{
			EditSiteBrandingEvent esbEvent;
			int x;
			int y;

			// Position the Edit Branding dialog at the top, left corner of the content control.
			x = m_contentControlX;
			y = m_contentControlY;
			
			// Yes, inform all registered action handlers that the user wants to edit the site branding.
			esbEvent = new EditSiteBrandingEvent( x, y );
			GwtTeaming.fireEvent( esbEvent );
		}
		else if ( adminAction.getActionType() == AdminAction.CONFIGURE_ADHOC_FOLDERS )
		{
			// Fire the event to invoke the "Configure Adhoc folders" dialog
			InvokeConfigureAdhocFoldersDlgEvent.fireOne();
			
		}
		else if ( adminAction.getActionType() == AdminAction.CONFIGURE_FILE_SYNC_APP )
		{
			// Fire the event to invoke the "Configure File Sync" dialog.
			InvokeConfigureFileSyncAppDlgEvent.fireOne();
		}
		else if ( adminAction.getActionType() == AdminAction.CONFIGURE_USER_ACCESS )
		{
			// Fire the event to invoke the "Configure User Access" dialog
			InvokeConfigureUserAccessDlgEvent.fireOne();
			
		}
		else if ( adminAction.getActionType() == AdminAction.MANAGE_GROUPS )
		{
			// Fire the event to invoke the "Manage Groups" dialog.
			InvokeManageGroupsDlgEvent.fireOne();
		}
		else if ( adminAction.getActionType() == AdminAction.MANAGE_RESOURCE_DRIVERS )
		{
			// Fire the event to invoke the "Manage net folder roots" dialog.
			InvokeManageNetFolderRootsDlgEvent.fireOne();
		}
		else if ( adminAction.getActionType() == AdminAction.MANAGE_NET_FOLDERS )
		{
			// Fire the event to invoke the "Manage net folders" dialog.
			InvokeManageNetFoldersDlgEvent.fireOne();
		}
		else
		{
			String url;
			
			// Get the url used by the selected action.
			url = adminAction.getUrl();
			if ( url != null && url.length() > 0 )
			{
				Scheduler.ScheduledCommand cmd;

				// Clear the iframe's content 
				m_contentControl.clear();
				
				// Set the iframe's content to the selected administration page.
				m_contentControl.setUrl( url, Instigator.ADMINISTRATION_CONSOLE );
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						// Show an position the content control.
						showContentPanel();
						relayoutPage();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		}
	}// end adminActionSelected()

	
	/**
	 * For some reason if we try to logout while the "configure ldap" page is still loaded
	 * we see an error in IE.  So clear the content panel.
	 */
	public void doPreLogoutCleanup()
	{
		if ( isShowing() == true )
		{
			// Clear the iframe's content 
			m_contentControl.clear();
		
			// Set the iframe's content to nothing.
			m_contentControl.setUrl( "", Instigator.ADMINISTRATION_CONSOLE );
		}
	}// end doPreLogoutCleanup()
	
	
	/**
	 * Issue an ajax request to get information about the upgrade tasks that need to be performed.
	 * If there are upgrade tasks that need to be performed show the list of tasks.
	 */
	public static void showUpgradeTasks()
	{
		AsyncCallback<VibeRpcResponse> rpcGetUpgradeInfoCallback = null;

		// Create the callback that will be used when we issue an ajax call to get upgrade information
		rpcGetUpgradeInfoCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetUpgradeInfo() );
			}
	
			/**
			 * 
			 * @param result
			 */
			@Override
			public void onSuccess( final VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					/**
					 * 
					 */
					@Override
					public void execute()
					{
						GwtUpgradeInfo upgradeInfo;
						boolean upgradeTasksExist;
						boolean filrAdminTasksExist;
						
						upgradeInfo = (GwtUpgradeInfo) response.getResponseData();
						
						// Are there upgrade tasks that need to be performed?
						upgradeTasksExist = upgradeInfo.doUpgradeTasksExist();
						
						filrAdminTasksExist = false;
						if ( GwtTeaming.m_requestInfo.isLicenseFilr() && upgradeInfo.doFilrAdminTasksExist() )
							filrAdminTasksExist = true;
						
						if ( upgradeTasksExist || filrAdminTasksExist )
						{
							// Yes, invoke the AdminInfoDlg.
							showAdminInfoDlg( upgradeInfo, 250, 100 );
						}
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		// When we get the upgrade info from the server our callback will check to
    	// see if upgrade tasks exists.  If they do, the callback will invoke the
    	// AdminInfoDlg which will show the user the tasks they need to do.
		getUpgradeInfoFromServer( rpcGetUpgradeInfoCallback );
	}
	
	
	/**
	 * Issue an ajax request to get the upgrade information from the server.
	 */
	public static void getUpgradeInfoFromServer( AsyncCallback<VibeRpcResponse> callback )
	{
		GetUpgradeInfoCmd cmd;
		
		// Issue an ajax request to get the upgrade information
		cmd = new GetUpgradeInfoCmd();
		GwtClientHelper.executeCommand( cmd, callback );
	}

	
	/**
	 * 
	 */
	public void hideContentPanel()
	{
		if ( m_contentControl.isVisible() )
		{
			m_contentControl.setVisible( false );
		}
	}// end hideContentPanel()
	
	
	/**
	 * 
	 */
	public void hideControl()
	{
		if ( isShowing() )
		{
			hide();
		}
	}// end hideControl()
	
	
	/**
	 * 
	 */
	public void hideTreeControl()
	{
		if ( m_adminActionsTreeControl.isVisible() )
		{
			m_adminActionsTreeControl.setVisible( false );
		}
	}// end hideTreeControl()
	
	
	/**
	 * 
	 */
	public void relayoutPage()
	{
		// If the AdminControl is visible...
		if ( isShowing() )
		{		
			Scheduler.ScheduledCommand cmd;
	
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					// ...update the page's layout.
					relayoutPageNow();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}// end relayoutPage()
	
	
	/**
	 * 
	 */
	private void relayoutPageNow()
	{
		int width;
		int height;
		int x;
		Style style;

		// Calculate where the content control should be positioned.
		x = m_adminActionsTreeControl.getAbsoluteLeft() + m_adminActionsTreeControl.getOffsetWidth() + 8;
		
		// Calculate how wide the ContentControl should be.
		{
			int clientWidth;
			
			// Get the width of the browser window's client area.
			clientWidth = Window.getClientWidth();
			
			width = clientWidth - x - 10; 
		}
		
		// Calculate how high the ContentControl should be.
		{
			int clientHeight;
			
			// Get the height of the browser window's client area.
			clientHeight = Window.getClientHeight();
			
			height = clientHeight - m_adminActionsTreeControl.getAbsoluteTop() - 20;
		}

		m_contentControlX = x;
		m_contentControlY = m_adminActionsTreeControl.getAbsoluteTop();

		// Set the width and height of the content control.
		m_contentControlWidth = width;
		m_contentControlHeight = height + GwtConstants.PANEL_PADDING;
		m_contentControl.setDimensions( m_contentControlWidth, m_contentControlHeight );
		
		// Set the width and height that should be used by GWT dialogs
		m_dlgWidth = m_contentControlWidth - 12;
		m_dlgHeight = m_contentControlHeight - 10;

		// Set the left position of the content control.
		style = m_contentControl.getElement().getStyle();
		style.setLeft( x, Style.Unit.PX );

		// Set the height of the tree control.
		style = m_adminActionsTreeControl.getElement().getStyle();
		style.setHeight( height, Style.Unit.PX );
	}// end relayoutPageNow()

	
	/**
	 * 
	 */
	public static void showAdminInfoDlg( final GwtUpgradeInfo upgradeInfo, final int x, final int y )
	{
		AdminInfoDlg.createAsync( false, true, x, y, new AdminInfoDlgClient()
		{			
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}// end onUnavailable()
			
			@Override
			public void onSuccess( final AdminInfoDlg adminInfoDlg )
			{
				ScheduledCommand initAndShowDlg = new ScheduledCommand() {
					@Override
					public void execute()
					{
						showAdminInfoDlgImpl( adminInfoDlg, upgradeInfo );
					}// end execute()
				};
				Scheduler.get().scheduleDeferred( initAndShowDlg );
			}// onSuccess()
		} );
	}// end showAdminInfoDlg()
	
	private static void showAdminInfoDlgImpl( final AdminInfoDlg adminInfoDlg, final GwtUpgradeInfo upgradeInfo ) {
		AdminInfoDlg.initAndShow( adminInfoDlg, upgradeInfo );
	}
	
	
	/**
	 * 
	 */
	public void showContentPanel()
	{
		if ( !m_contentControl.isVisible() )
		{
			m_contentControl.setVisible( true );
		}
	}// end showContentPanel()
	
	
	/**
	 * 
	 */
	public void showControl( final UIObject target )
	{
		if ( !isShowing() )
		{
			Scheduler.ScheduledCommand cmd;
	
			// Set the position of the content control.
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					Scheduler.ScheduledCommand cmd2;
	            	
					relayoutPage();
					
					cmd2 = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							showRelativeTo( target );
							showHomePage();
							showTreeControl();
							relayoutPage();
						}
					};
					Scheduler.get().scheduleDeferred( cmd2 );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
	
			// Issue an ajax request to get the upgrade information from the server.
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
	            	// Get the upgrade info from the server.  If there are upgrade tasks that
					// need to be performed, AdminInfoDlg will display them.
					showUpgradeTasks();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}// end showControl()
	
	/**
	 * Show the page that gives the user some information about the administration console.
	 */
	public void showHomePage()
	{
		if ( m_homePageUrl != null )
		{
			// Clear the iframe's content 
			m_contentControl.clear();
			
			// Set the iframe's content to the instructions page.
			m_contentControl.setUrl( m_homePageUrl, Instigator.ADMINISTRATION_CONSOLE );
			
			showContentPanel();
			relayoutPage();
		}
	}
	
	/**
	 * 
	 */
	public void showTreeControl()
	{
		if ( !m_adminActionsTreeControl.isVisible() )
		{
			m_adminActionsTreeControl.setVisible( true );
		}
	}// end showTreeControl()

	
	/**
	 * Handles InvokeConfigureAdhocFoldersDlgEvent received by this class.
	 * 
	 * Implements the InvokeConfigureAdhocFoldersDlgEvent.Handler.onInvokeConfigureAdhocFoldersDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeConfigureAdhocFoldersDlg( InvokeConfigureAdhocFoldersDlgEvent event )
	{
		int x;
		int y;
		
		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		// Have we already created a "Configure Adhoc Folders" dialog?
		if ( m_configureAdhocFoldersDlg == null )
		{
			int width;
			int height;
			
			// No, create one.
			height = m_dlgHeight;
			width = m_dlgWidth;
			ConfigureAdhocFoldersDlg.createAsync(
											false, 
											true,
											x, 
											y,
											width,
											height,
											new ConfigureAdhocFoldersDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ConfigureAdhocFoldersDlg cafDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_configureAdhocFoldersDlg = cafDlg;
							
							m_configureAdhocFoldersDlg.init( null );
							m_configureAdhocFoldersDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_configureAdhocFoldersDlg.init( null );
			m_configureAdhocFoldersDlg.setPopupPosition( x, y );
			m_configureAdhocFoldersDlg.show();
		}
	}
	

	/**
	 * Handles InvokeConfigureFileSyncAppDlgEvent received by this class.
	 * 
	 * Implements the InvokeConfigureFileSyncAppDlgEvent.Handler.onInvokeConfigureFileSyncAppDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeConfigureFileSyncAppDlg( InvokeConfigureFileSyncAppDlgEvent event )
	{
		AsyncCallback<VibeRpcResponse> rpcReadCallback;
		
		// Create a callback that will be called when we get the File Sync App configuration.
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
					GwtTeaming.getMessages().rpcFailure_GetFileSyncAppConfiguration() );
			}
	
			/**
			 * We successfully retrieved the File Sync App configuration.  Now invoke the "Configure File Sync App" dialog.
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				int x;
				int y;
				final GwtFileSyncAppConfiguration fileSyncAppConfiguration;
				EditSuccessfulHandler editSuccessfullHandler;


				fileSyncAppConfiguration = (GwtFileSyncAppConfiguration) response.getResponseData();
				
				// Create a handler that will be called when the user presses the ok button in the dialog.
				editSuccessfullHandler = new EditSuccessfulHandler()
				{
					/**
					 * This method gets called when user user presses ok in the "Configure File Sync App" dialog.
					 */
					@Override
					public boolean editSuccessful( Object obj )
					{
						AsyncCallback<VibeRpcResponse> rpcSaveCallback = null;
						GwtFileSyncAppConfiguration fileSyncAppConfig;

						fileSyncAppConfig = (GwtFileSyncAppConfiguration) obj;
						
						// Create the callback that will be used when we issue an ajax request to save the file sync app configuration.
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
									GwtTeaming.getMessages().rpcFailure_SaveFileSyncAppConfiguration() );
							}
					
							/**
							 * 
							 * @param result
							 */
							@Override
							public void onSuccess( VibeRpcResponse response )
							{
								// Nothing to do.
							}
						};
				
						// Issue an ajax request to save the File Sync App configuration.
						{
							SaveFileSyncAppConfigurationCmd cmd;
							
							// Issue an ajax request to save the File Sync App configuration to the db.  rpcSaveCallback will
							// be called when we get the response back.
							cmd = new SaveFileSyncAppConfigurationCmd( fileSyncAppConfig );
							GwtClientHelper.executeCommand( cmd, rpcSaveCallback );
						}
						
						return true;
					}
				};
				
				// Get the position of the content control.
				x = m_contentControlX;
				y = m_contentControlY;
				
				// Have we already created a "Configure File Sync App" dialog?
				if ( m_configureFileSyncAppDlg == null )
				{
					int width;
					int height;
					
					// No, create one.
					height = m_dlgHeight;
					width = m_dlgWidth;
					ConfigureFileSyncAppDlg.createAsync(
							editSuccessfullHandler,
							null,
							false, 
							true,
							x, 
							y,
							width,
							height,
							new ConfigureFileSyncAppDlgClient()
					{			
						@Override
						public void onUnavailable()
						{
							// Nothing to do.  Error handled in asynchronous provider.
						}
						
						@Override
						public void onSuccess( final ConfigureFileSyncAppDlg cfsaDlg )
						{
							ScheduledCommand cmd;
							
							cmd = new ScheduledCommand()
							{
								@Override
								public void execute() 
								{
									m_configureFileSyncAppDlg = cfsaDlg;
									
									m_configureFileSyncAppDlg.init( fileSyncAppConfiguration );
									m_configureFileSyncAppDlg.show();
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					} );
				}
				else
				{
					m_configureFileSyncAppDlg.init( fileSyncAppConfiguration );
					m_configureFileSyncAppDlg.setPopupPosition( x, y );
					m_configureFileSyncAppDlg.show();
				}
			}
		};

		// Issue an ajax request to get the File Sync App configuration.  When we get the File Sync App configuration
		// we will invoke the "Configure File Sync App" dialog.
		{
			GetFileSyncAppConfigurationCmd cmd;
			
			// Issue an ajax request to get the File Sync App configuration from the db.
			cmd = new GetFileSyncAppConfigurationCmd();
			GwtClientHelper.executeCommand( cmd, rpcReadCallback );
		}
	}
	

	/**
	 * Handles InvokeConfigureUserAccessDlgEvent received by this class.
	 * 
	 * Implements the InvokeConfigureUserAccessDlgEvent.Handler.onInvokeConfigureUserAccessDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeConfigureUserAccessDlg( InvokeConfigureUserAccessDlgEvent event )
	{
		int x;
		int y;
		
		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		// Have we already created a "Manage Net Folders" dialog?
		if ( m_configureUserAccessDlg == null )
		{
			int width;
			int height;
			
			// No, create one.
			height = m_dlgHeight;
			width = m_dlgWidth;
			ConfigureUserAccessDlg.createAsync(
											false, 
											true,
											x, 
											y,
											width,
											height,
											new ConfigureUserAccessDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ConfigureUserAccessDlg cuaDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_configureUserAccessDlg = cuaDlg;
							
							m_configureUserAccessDlg.init();
							m_configureUserAccessDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_configureUserAccessDlg.init();
			m_configureUserAccessDlg.setPopupPosition( x, y );
			m_configureUserAccessDlg.show();
		}
	}
	

	/**
	 * Handles InvokeManageNetFoldersDlgEvent received by this class.
	 * 
	 * Implements the InvokeManageNetFoldersDlgEvent.Handler.onInvokeManageNetFoldersDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeManageNetFoldersDlg( InvokeManageNetFoldersDlgEvent event )
	{
		int x;
		int y;
		
		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		// Have we already created a "Manage Net Folders" dialog?
		if ( m_manageNetFoldersDlg == null )
		{
			int width;
			int height;
			
			// No, create one.
			height = m_dlgHeight;
			width = m_dlgWidth;
			ManageNetFoldersDlg.createAsync(
											false, 
											true,
											x, 
											y,
											width,
											height,
											new ManageNetFoldersDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ManageNetFoldersDlg mnfDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_manageNetFoldersDlg = mnfDlg;
							
							m_manageNetFoldersDlg.init();
							m_manageNetFoldersDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_manageNetFoldersDlg.init();
			m_manageNetFoldersDlg.setPopupPosition( x, y );
			m_manageNetFoldersDlg.show();
		}
	}
	
	/**
	 * Handles InvokeManageNetFolderRootsDlgEvent received by this class.
	 * 
	 * Implements the InvokeManageNetFolderRootsDlgEvent.Handler.onInvokeManageNetFolderRootsDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeManageNetFolderRootsDlg( InvokeManageNetFolderRootsDlgEvent event )
	{
		int x;
		int y;
		
		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		// Have we already created a "Manage Net Folder Roots" dialog?
		if ( m_manageNetFolderRootsDlg == null )
		{
			int width;
			int height;
			
			// No, create one.
			height = m_dlgHeight;
			width = m_dlgWidth;
			ManageNetFolderRootsDlg.createAsync(
											false, 
											true,
											x, 
											y,
											width,
											height,
											new ManageNetFolderRootsDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ManageNetFolderRootsDlg mfsrDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_manageNetFolderRootsDlg = mfsrDlg;
							
							m_manageNetFolderRootsDlg.init();
							m_manageNetFolderRootsDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_manageNetFolderRootsDlg.init();
			m_manageNetFolderRootsDlg.setPopupPosition( x, y );
			m_manageNetFolderRootsDlg.show();
		}
	}
	
	/**
	 * Handles InvokeManageGroupsDlgEvent received by this class.
	 * 
	 * Implements the InvokeManageGroupsDlgEvent.Handler.onInvokeManageGroupsDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeManageGroupsDlg( InvokeManageGroupsDlgEvent event )
	{
		int x;
		int y;
		
		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		// Have we already created a "Manage Groups" dialog?
		if ( m_manageGroupsDlg == null )
		{
			int width;
			int height;
			
			// No, create one.
			height = m_dlgHeight;
			width = m_dlgWidth;
			ManageGroupsDlg.createAsync(
									false, 
									true,
									x, 
									y,
									width,
									height,
									new ManageGroupsDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ManageGroupsDlg mgDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_manageGroupsDlg = mgDlg;
							
							m_manageGroupsDlg.init();
							m_manageGroupsDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_manageGroupsDlg.init();
			m_manageGroupsDlg.setPopupPosition( x, y );
			m_manageGroupsDlg.show();
		}
	}
	
	/**
	 * Handles PreLogoutEvent's received by this class.
	 * 
	 * Implements the PreLogoutEvent.Handler.onLogout() method.
	 * 
	 * @param event
	 */
	@Override
	public void onPreLogout( PreLogoutEvent event )
	{
		doPreLogoutCleanup();
	}// end onPreLogout()
	
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
		if ( isShowing() )
		{
			hideTreeControl();
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
		if ( isShowing() )
		{
			showTreeControl();
		}
	}// end onSidebarShow()
	
	/**
	 * Callback interface to interact with the admin control
	 * asynchronously after it loads. 
	 */
	public interface AdminControlClient {
		void onSuccess(AdminControl adminCtrl);
		void onUnavailable();
	}

	/**
	 * Loads the AdminControl split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param adminCtrlClient
	 */
	public static void createAsync( final GwtMainPage mainPage, final AdminControlClient adminCtrlClient )
	{
		GWT.runAsync( AdminControl.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				AdminControl adminCtrl = new AdminControl( mainPage );
				adminCtrlClient.onSuccess( adminCtrl );
			}// end onSuccess()
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_AdminControl() );
				adminCtrlClient.onUnavailable();
			}// end onFailure()
		} );
	}// end createAsync()
}// end AdminControl
