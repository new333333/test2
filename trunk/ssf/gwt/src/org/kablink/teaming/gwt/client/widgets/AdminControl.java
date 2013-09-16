/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
import java.util.List;

import org.kablink.teaming.gwt.client.event.EditSiteBrandingEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.InvokeConfigureAdhocFoldersDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeConfigureFileSyncAppDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeConfigureMobileAppsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeConfigureShareSettingsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeConfigureUserAccessDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeEditLdapConfigDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeEditNetFolderDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeJitsZoneConfigDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageDatabasePruneDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageNetFolderRootsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageGroupsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageUsersDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageNetFoldersDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeRunAReportDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeUserDesktopSettingsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeUserMobileSettingsDlgEvent;
import org.kablink.teaming.gwt.client.event.ManageSharesSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.PreLogoutEvent;
import org.kablink.teaming.gwt.client.event.SidebarHideEvent;
import org.kablink.teaming.gwt.client.event.SidebarShowEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.AdminConsoleInfo;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtDatabasePruneConfiguration;
import org.kablink.teaming.gwt.client.GwtFileSyncAppConfiguration;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.NetFolder;
import org.kablink.teaming.gwt.client.admin.AdminAction;
import org.kablink.teaming.gwt.client.admin.AdminConsoleHomePage;
import org.kablink.teaming.gwt.client.admin.GwtAdminAction;
import org.kablink.teaming.gwt.client.admin.GwtAdminCategory;
import org.kablink.teaming.gwt.client.admin.GwtUpgradeInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetAdminActionsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetDatabasePruneConfigurationCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFileSyncAppConfigurationCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetUpgradeInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveBrandingCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.AdminInfoDlg.AdminInfoDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigureAdhocFoldersDlg.ConfigureAdhocFoldersDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigureFileSyncAppDlg.ConfigureFileSyncAppDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigureMobileAppsDlg.ConfigureMobileAppsDlgClient;
import org.kablink.teaming.gwt.client.widgets.EditBrandingDlg.EditBrandingDlgClient;
import org.kablink.teaming.gwt.client.widgets.EditLdapConfigDlg.EditLdapConfigDlgClient;
import org.kablink.teaming.gwt.client.widgets.EditZoneShareSettingsDlg.EditZoneShareSettingsDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigureUserAccessDlg.ConfigureUserAccessDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigureUserFileSyncAppDlg.ConfigureUserFileSyncAppDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigureUserMobileAppsDlg.ConfigureUserMobileAppsDlgClient;
import org.kablink.teaming.gwt.client.widgets.ContentControl.ContentControlClient;
import org.kablink.teaming.gwt.client.widgets.JitsZoneConfigDlg.JitsZoneConfigDlgClient;
import org.kablink.teaming.gwt.client.widgets.ManageDatabasePruneDlg.ManageDatabasePruneDlgClient;
import org.kablink.teaming.gwt.client.widgets.ManageGroupsDlg.ManageGroupsDlgClient;
import org.kablink.teaming.gwt.client.widgets.ManageNetFolderRootsDlg.ManageNetFolderRootsDlgClient;
import org.kablink.teaming.gwt.client.widgets.ManageNetFoldersDlg.ManageNetFoldersDlgClient;
import org.kablink.teaming.gwt.client.widgets.ManageUsersDlg.ManageUsersDlgClient;
import org.kablink.teaming.gwt.client.widgets.ModifyNetFolderDlg.ModifyNetFolderDlgClient;
import org.kablink.teaming.gwt.client.widgets.RunAReportDlg.RunAReportDlgClient;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg2.ShareThisDlg2Client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.web.bindery.event.shared.HandlerRegistration;

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
		EditSiteBrandingEvent.Handler,
		InvokeConfigureAdhocFoldersDlgEvent.Handler,
		InvokeConfigureFileSyncAppDlgEvent.Handler,
		InvokeConfigureMobileAppsDlgEvent.Handler,
		InvokeConfigureShareSettingsDlgEvent.Handler,
		InvokeConfigureUserAccessDlgEvent.Handler,
		InvokeEditLdapConfigDlgEvent.Handler,
		InvokeEditNetFolderDlgEvent.Handler,
		InvokeJitsZoneConfigDlgEvent.Handler,
		InvokeManageDatabasePruneDlgEvent.Handler,
		InvokeManageNetFoldersDlgEvent.Handler,
		InvokeManageNetFolderRootsDlgEvent.Handler,
		InvokeManageGroupsDlgEvent.Handler,
		InvokeManageUsersDlgEvent.Handler,
		InvokeRunAReportDlgEvent.Handler,
		InvokeUserDesktopSettingsDlgEvent.Handler,
		InvokeUserMobileSettingsDlgEvent.Handler,
		ManageSharesSelectedEntitiesEvent.Handler,
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
	private AdminConsoleHomePage m_homePage = null;
	private ConfigureFileSyncAppDlg m_configureFileSyncAppDlg = null;
	private ConfigureMobileAppsDlg m_configureMobileAppsDlg = null;
	private ConfigureUserMobileAppsDlg m_configureUserMobileAppsDlg = null;
	private ConfigureUserFileSyncAppDlg m_configureUserFileSyncAppDlg = null;
	private ManageDatabasePruneDlg m_manageDatabasePruneDlg = null;
	private ManageGroupsDlg m_manageGroupsDlg = null;
	private ManageNetFoldersDlg m_manageNetFoldersDlg = null;
	private ManageNetFolderRootsDlg m_manageNetFolderRootsDlg = null;
	private ManageUsersDlg m_manageUsersDlg = null;
	private RunAReportDlg m_runAReportDlg = null;
	private ConfigureUserAccessDlg m_configureUserAccessDlg = null;
	private ConfigureAdhocFoldersDlg m_configureAdhocFoldersDlg = null;
	private EditZoneShareSettingsDlg m_editZoneShareSettingsDlg = null;
	private ModifyNetFolderDlg m_modifyNetFolderDlg = null;
	private ShareThisDlg2 m_shareDlg = null;
	private JitsZoneConfigDlg m_jitsDlg = null;
	private EditBrandingDlg m_editSiteBrandingDlg = null;
	private EditLdapConfigDlg m_editLdapConfigDlg = null;
	private EditSuccessfulHandler m_editBrandingSuccessHandler = null;
	private List<HandlerRegistration> m_registeredEventHandlers;

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		// Administration events.
		TeamingEvents.EDIT_SITE_BRANDING,
		TeamingEvents.INVOKE_CONFIGURE_ADHOC_FOLDERS_DLG,
		TeamingEvents.INVOKE_CONFIGURE_FILE_SYNC_APP_DLG,
		TeamingEvents.INVOKE_CONFIGURE_MOBILE_APPS_DLG,
		TeamingEvents.INVOKE_CONFIGURE_SHARE_SETTINGS_DLG,
		TeamingEvents.INVOKE_CONFIGURE_USER_ACCESS_DLG,
		TeamingEvents.INVOKE_EDIT_LDAP_CONFIG_DLG,
		TeamingEvents.INVOKE_EDIT_NET_FOLDER_DLG,
		TeamingEvents.INVOKE_JITS_ZONE_CONFIG_DLG,
		TeamingEvents.INVOKE_MANAGE_DATABASE_PRUNE_DLG,
		TeamingEvents.INVOKE_MANAGE_NET_FOLDERS_DLG,
		TeamingEvents.INVOKE_MANAGE_NET_FOLDER_ROOTS_DLG,
		TeamingEvents.INVOKE_MANAGE_GROUPS_DLG,
		TeamingEvents.MANAGE_SHARES_SELECTED_ENTITIES,
		TeamingEvents.INVOKE_MANAGE_USERS_DLG,
		TeamingEvents.INVOKE_RUN_A_REPORT_DLG,
		TeamingEvents.INVOKE_USER_DESKTOP_SETTINGS_DLG,
		TeamingEvents.INVOKE_USER_MOBILE_SETTINGS_DLG,
		
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
					imgResource = GwtTeaming.getImageBundle().adminConsole36();
					img = new Image( imgResource );
				}
				else if ( categoryType == GwtAdminCategory.GwtAdminCategoryType.REPORTS )
				{
					imgResource = GwtTeaming.getImageBundle().report16();
					img = new Image( imgResource );
				}
				else if ( categoryType == GwtAdminCategory.GwtAdminCategoryType.SYSTEM )
				{
					imgResource = GwtTeaming.getImageBundle().adminSystem36();
					img = new Image( imgResource );
				}
				else
				{
					imgResource = GwtTeaming.getImageBundle().management16();
					img = new Image( imgResource );
				}
				
				m_mainTable.setWidget( row, 1, img );
				cellFormatter.setWidth( row, 1, "20px" );
				
				// Add the category name
				categoryName = new InlineLabel( category.getLocalizedName() );
				categoryName.addStyleName( "adminCategoryName" );
				m_mainTable.setWidget( row, 2, categoryName );
				cellFormatter.setWidth( row, 2, "" );
				
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
						// If we're not showing the JSP reports...
						if ( ! RunAReportDlg.SHOW_JSP_ADMIN_REPORTS )
						{
							// ...and this is the JSP reports
							// ...category...
							if ( GwtAdminCategory.GwtAdminCategoryType.REPORTS.equals( category.getCategoryType() ) )
							{
								// ...ignore it.
								continue;
							}
						}
						
						// Add this administration category to the page.
						addCategory( category );
					}
					
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
					final GwtUpgradeInfo upgradeInfo;
					Scheduler.ScheduledCommand cmd;
					
					upgradeInfo = (GwtUpgradeInfo) response.getResponseData();
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							int x;
							int y;

							// Update the admin console home page with the latest info.
							if ( m_homePage != null )
								m_homePage.init( upgradeInfo );
							
							// Show the AdminInfoDlg
							x = m_adminActionsTreeControl.getAbsoluteLeft() + m_adminActionsTreeControl.getOffsetWidth();
							y = m_adminActionsTreeControl.getAbsoluteTop();
							showAdminInfoDlg( upgradeInfo, x, y );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
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
		
		Scheduler.ScheduledCommand cmd;
		final FlowPanel mainPanel = new FlowPanel();
		
		mainPanel.addStyleName( "adminControl" );

		// Create the home page that will be displayed when nothing is selected.
		m_homePage = new AdminConsoleHomePage();
		mainPanel.add( m_homePage );

		// Create the control that holds all of the administration actions
		m_adminActionsTreeControl = new AdminActionsTreeControl();
		mainPanel.add( m_adminActionsTreeControl );
		
		// We need to replace gwt-PopupPanel style name because it is causing an empty
		// box to be displayed because initially this control's width and height are 0.
		setStylePrimaryName( "adminControlPopup" );
		
		// Create a control to hold the administration page for the selected administration action.
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
		
		// Issue an rpc request to get the info needed to update the home page
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute() 
			{
				updateHomePage();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
		
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
		else if ( adminAction.getActionType() == AdminAction.CONFIGURE_MOBILE_APPS )
		{
			// Fire the event to invoke the "Configure Mobile apps" dialog.
			InvokeConfigureMobileAppsDlgEvent.fireOne();
		}
		else if ( adminAction.getActionType() == AdminAction.CONFIGURE_SHARE_SETTINGS )
		{
			// Fire the event to invoke the "Configure Share Settings" dialog.
			InvokeConfigureShareSettingsDlgEvent.fireOne();
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
		else if ( adminAction.getActionType() == AdminAction.MANAGE_SHARE_ITEMS )
		{
			ManageSharesSelectedEntitiesEvent event;
			
			// Fire the event to invoke the Manage Shares dialog.
			event = new ManageSharesSelectedEntitiesEvent();
			GwtTeaming.fireEvent( event );
		}
				
		else if ( adminAction.getActionType() == AdminAction.MANAGE_DATABASE_PRUNE )
		{
			// Fire the event to invoke the "Manage database prune" dialog.
			InvokeManageDatabasePruneDlgEvent.fireOne();
		}

		else if ( adminAction.getActionType() == AdminAction.ADD_USER )
		{
			// Fire the event to invoke the "Manage users" dialog.
			InvokeManageUsersDlgEvent.fireOne();
		}
		
		else if ( adminAction.getActionType() == AdminAction.RUN_A_REPORT )
		{
			// Fire the event to invoke the "Run a Report" dialog.
			InvokeRunAReportDlgEvent.fireOne();
		}
		
		else if ( adminAction.getActionType() == AdminAction.JITS_ZONE_CONFIG )
		{
			// Fire the event to invoke the "Configure Jits" dialog.
			InvokeJitsZoneConfigDlgEvent.fireOne();
		}
		else if ( adminAction.getActionType() == AdminAction.LDAP_CONFIG && Window.confirm( "Show new ldap dialog?" ) )
		{
			// Fire the event to invoke the "Edit ldap configuration" dialog.
			InvokeEditLdapConfigDlgEvent.fireOne();
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
						
						if ( upgradeTasksExist || filrAdminTasksExist || upgradeInfo.getIsLicenseExpired() )
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
	 * 
	 */
	public int getContentHeight()
	{
		return m_dlgHeight;
	}
	
	/**
	 * 
	 */
	public int getContentWidth()
	{
		return m_dlgWidth;
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
	private void invokeEditLdapConfigDlg()
	{
		int x;
		int y;
		
		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		if ( m_editLdapConfigDlg == null )
		{
			Integer width;
			Integer height;
			
			height = new Integer( m_dlgHeight );
			width = new Integer( m_dlgWidth );

			EditLdapConfigDlg.createAsync(
										true, 
										false,
										x, 
										y,
										width,
										height,
										new EditLdapConfigDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final EditLdapConfigDlg elcDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_editLdapConfigDlg = elcDlg;
							
							m_editLdapConfigDlg.init();
							m_editLdapConfigDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_editLdapConfigDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			m_editLdapConfigDlg.init();
			m_editLdapConfigDlg.setPopupPosition( x, y );
			m_editLdapConfigDlg.show();
		}
	}

	/**
	 * 
	 */
	private void invokeEditNetFolderDlgById( Long id, final UIObject showRelativeTo )
	{
		if ( id != null )
		{
			GetNetFolderCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback = null;

			// Yes
			// Create the callback that will be used when we issue an ajax call to get the net folder.
			rpcCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetNetFolder() );
				}
		
				@Override
				public void onSuccess( final VibeRpcResponse response )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							NetFolder netFolder;
							
							netFolder = (NetFolder) response.getResponseData();
							
							// Invoke the modify net folder dialog
							if ( netFolder != null )
							{
								invokeEditNetFolderDlg( netFolder, showRelativeTo );
							}
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};

			// Issue an ajax request to get the net folder.
			cmd = new GetNetFolderCmd();
			cmd.setId( id );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}

	/**
	 * 
	 */
	private void invokeEditNetFolderDlg( final NetFolder netFolder, final UIObject showRelativeTo )
	{
		int x;
		int y;
		
		// Get the position of this dialog.
		x = getAbsoluteLeft() + 50;
		y = getAbsoluteTop() + 50;
		
		if ( m_modifyNetFolderDlg == null )
		{
			ModifyNetFolderDlg.createAsync(
										true, 
										false,
										x, 
										y,
										new ModifyNetFolderDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ModifyNetFolderDlg mnfDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_modifyNetFolderDlg = mnfDlg;
							
							m_modifyNetFolderDlg.init( netFolder );
							if ( showRelativeTo != null )
								m_modifyNetFolderDlg.showRelativeTo( showRelativeTo );
							else
								m_modifyNetFolderDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_modifyNetFolderDlg.init( netFolder );
			if ( showRelativeTo != null )
				m_modifyNetFolderDlg.showRelativeTo( showRelativeTo );
			else
			{
				m_modifyNetFolderDlg.setPopupPosition( x, y );
				m_modifyNetFolderDlg.show();
			}
		}
	}

	/*
	 * Invoke the "Edit Branding" dialog.
	 */
	private void invokeEditSiteBrandingDlg( GwtBrandingData brandingDataIn )
	{
		final int x;
		final int y;
		
		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		// Create a handler that will be called when the user presses the ok button in the dialog.
		if ( m_editBrandingSuccessHandler == null )
		{
			m_editBrandingSuccessHandler = new EditSuccessfulHandler()
			{
				private AsyncCallback<VibeRpcResponse> rpcSaveCallback = null;
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
																GwtTeaming.getMainPage().getMastHead().getBinderId() );
							}
					
							/**
							 * 
							 * @param result
							 */
							@Override
							public void onSuccess( VibeRpcResponse response )
							{
								Scheduler.ScheduledCommand cmd;
								
								cmd = new Scheduler.ScheduledCommand()
								{
									@Override
									public void execute()
									{
										MastHead mastHead;
										
										mastHead = GwtTeaming.getMainPage().getMastHead();
										
										// Tell the masthead to go get the new site branding.
										if ( mastHead != null )
											mastHead.refreshSiteBranding();
									}
								};
								Scheduler.get().scheduleDeferred( cmd );
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
				}
			};
		}

		final GwtBrandingData brandingData = brandingDataIn;

		// Have we already created an "Edit branding" dialog?
		if ( m_editSiteBrandingDlg == null )
		{
			Integer width;
			Integer height;
			
			height = new Integer( m_dlgHeight );
			width = new Integer( m_dlgWidth );

			// No, create one.
			EditBrandingDlg.createAsync(
									m_editBrandingSuccessHandler,
									null,
									false,
									true,
									x,
									y,
									width,
									height,
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
					m_editSiteBrandingDlg = ebDlg;
					invokeEditSiteBrandingDlgImpl( brandingData, x, y );
				}
			} );
		}
		else
		{
			invokeEditSiteBrandingDlgImpl( brandingData, x, y );
		}
		
	}

	/**
	 * 
	 */
	private void invokeEditSiteBrandingDlgImpl( GwtBrandingData brandingData, int x, int y )
	{
		m_editSiteBrandingDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
		m_editSiteBrandingDlg.init( brandingData );
		m_editSiteBrandingDlg.setPopupPosition( x, y );
		m_editSiteBrandingDlg.show();
	}

	/**
	 * Invokes the Share dialog in administrative mode.
	 */
	public void invokeManageSharesDlg()
	{
		// Have we created a share dialog yet?
		if ( m_shareDlg == null )
		{
			ShareThisDlg2Client client;
			
			// No!  Create one now...
			client = new ShareThisDlg2Client()
			{
				@Override
				public void onUnavailable() 
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ShareThisDlg2 stDlg )
				{
					// ...and show it with the given entity IDs.
					m_shareDlg = stDlg;
					invokeManageSharesDlg();
				}
			};
			ShareThisDlg2.createAsync(
									true,
									false,
									m_contentControlX,
									m_contentControlY,
									new Integer( m_dlgWidth ),
									new Integer( m_dlgHeight ),
									client );
		}
		else
		{
			Scheduler.ScheduledCommand cmd;
			
			// Yes, we've already create a share dialog!  Simply show it.
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute() 
				{
					m_shareDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
					m_shareDlg.init(
								GwtTeaming.getMessages().shareDlg_manageShares(),
								null,
								ShareThisDlg2.ShareThisDlgMode.MANAGE_ALL );
					m_shareDlg.setPopupPosition( m_contentControlX, m_contentControlY );
					m_shareDlg.showDlg();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
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

		// Set the position and width of the admin home page
		style = m_homePage.getElement().getStyle();
		style.setLeft( m_contentControlX, Unit.PX );
		style.setTop( 8, Unit.PX );
		style.setWidth( m_contentControlWidth, Unit.PX );
		style.setHeight( m_contentControlHeight, Unit.PX );

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
					updateHomePage();
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
		if ( m_homePage != null )
		{
			m_contentControl.clear();
			m_contentControl.setVisible( false );
			m_homePage.setVisible( true );
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
	 * Called when the dialog is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach()
	{
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the dialog is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach()
	{
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/**
	 * Handles EditSiteBrandingEvent's received by this class.
	 * 
	 * Implements the EditSiteBrandingEvent.Handler.onEditSiteBranding() method.
	 * 
	 * @param event
	 */
	@Override
	public void onEditSiteBranding( EditSiteBrandingEvent event )
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				MastHead mastHead;
				
				mastHead = GwtTeaming.getMainPage().getMastHead();
				if ( mastHead != null )
				{
					GwtBrandingData siteBrandingData;
					
					siteBrandingData = mastHead.getSiteBrandingData();
					invokeEditSiteBrandingDlg( siteBrandingData );
				}
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
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
											true, 
											false,
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
			m_configureAdhocFoldersDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
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


				fileSyncAppConfiguration = (GwtFileSyncAppConfiguration) response.getResponseData();
				
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
							true, 
							false,
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
					m_configureFileSyncAppDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
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
	 * Handles InvokeConfigureMobileAppsDlgEvent received by this class.
	 * 
	 * Implements the InvokeConfigureMobileAppsDlgEvent.Handler.onInvokeConfigureMobileAppsDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeConfigureMobileAppsDlg( InvokeConfigureMobileAppsDlgEvent event )
	{
		int x;
		int y;

		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		// Have we already created a "Configure Mobile Apps" dialog?
		if ( m_configureMobileAppsDlg == null )
		{
			int width;
			int height;
			
			// No, create one.
			height = m_dlgHeight;
			width = m_dlgWidth;
			ConfigureMobileAppsDlg.createAsync(
											true, 
											false,
											x, 
											y,
											width,
											height,
											new ConfigureMobileAppsDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ConfigureMobileAppsDlg cmaDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_configureMobileAppsDlg = cmaDlg;
							
							m_configureMobileAppsDlg.init();
							m_configureMobileAppsDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_configureMobileAppsDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			m_configureMobileAppsDlg.init();
			m_configureMobileAppsDlg.setPopupPosition( x, y );
			m_configureMobileAppsDlg.show();
		}
	}
	

	/**
	 * Handles InvokeConfigureShareSettingsDlgEvent received by this class.
	 * 
	 * Implements the InvokeConfigureShareSettingsDlgEvent.Handler.onInvokeConfigureShareSettingsDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeConfigureShareSettingsDlg( InvokeConfigureShareSettingsDlgEvent event )
	{
		int x;
		int y;

		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		// Have we already created a "Configure Share Settings" dialog?
		if ( m_editZoneShareSettingsDlg == null )
		{
			int width;
			int height;
			
			// No, create one.
			height = m_dlgHeight;
			width = m_dlgWidth;
			EditZoneShareSettingsDlg.createAsync(
											true, 
											false,
											x, 
											y,
											width,
											height,
											new EditZoneShareSettingsDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final EditZoneShareSettingsDlg ezsrDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_editZoneShareSettingsDlg = ezsrDlg;
							
							m_editZoneShareSettingsDlg.init();
							m_editZoneShareSettingsDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_editZoneShareSettingsDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			m_editZoneShareSettingsDlg.init();
			m_editZoneShareSettingsDlg.setPopupPosition( x, y );
			m_editZoneShareSettingsDlg.show();
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
											true, 
											false,
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
			m_configureUserAccessDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			m_configureUserAccessDlg.init();
			m_configureUserAccessDlg.setPopupPosition( x, y );
			m_configureUserAccessDlg.show();
		}
	}
	
	/**
	 * Handles InvokeEditLdapConfigDlgEvent received by this class.
	 * 
	 * Implements the InvokeEditLdapConfigDlgEvent.Handler.onInvokeEditLdapConfigDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeEditLdapConfigDlg( InvokeEditLdapConfigDlgEvent event )
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				invokeEditLdapConfigDlg();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	

	/**
	 * Handle the InvokeEditNetFolderDlgEvent received by this class
	 * 
	 * Implements the InvokeEditNetFolderDlgEvent.Handler.onInvokeEditNetFolderDlg() method
	 * 
	 */
	@Override
	public void onInvokeEditNetFolderDlg( final InvokeEditNetFolderDlgEvent event )
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				invokeEditNetFolderDlgById( event.getNetFolderId(), event.getShowRelativeTo() );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Handles the InvokeJitsZoneConfigDlgEvent received by this class.
	 * 
	 * Implements the InvokeJitsZoneConfigDlgEvent.Handler.onInvokeJitsZoneConfigDlg() method.
	 *  
	 */
	@Override
	public void onInvokeJitsZoneConfigDlg( InvokeJitsZoneConfigDlgEvent event )
	{
		int x;
		int y;

		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		// Have we already created a Jits configuration" dialog?
		if ( m_jitsDlg == null )
		{
			int width;
			int height;
			
			// No, create one.
			height = m_dlgHeight;
			width = m_dlgWidth;
			JitsZoneConfigDlg.createAsync(
										true, 
										false,
										x, 
										y,
										width,
										height,
										new JitsZoneConfigDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final JitsZoneConfigDlg jzcDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_jitsDlg = jzcDlg;
							
							m_jitsDlg.init();
							m_jitsDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_jitsDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			m_jitsDlg.init();
			m_jitsDlg.setPopupPosition( x, y );
			m_jitsDlg.show();
		}
	}
	
	/**
	 * Handles InvokeManageDatabasePruneDlgEvent received by this class.
	 * 
	 * Implements the InvokeManageDatabasePruneDlgEvent.Handler.onInvokeManageDatabasePruneDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeManageDatabasePruneDlg( InvokeManageDatabasePruneDlgEvent event )
	{
		AsyncCallback<VibeRpcResponse> rpcReadCallback;
		
		// Create a callback that will be called when we get the Database Prune configuration.
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
					GwtTeaming.getMessages().rpcFailure_GetDatabasePruneDlgConfiguration() );
			}
	
			/**
			 * We successfully retrieved the Database Prune configuration.  Now invoke the "Database Prune" dialog.
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				int x;
				int y;
				final GwtDatabasePruneConfiguration databasePruneConfiguration;


				databasePruneConfiguration = (GwtDatabasePruneConfiguration) response.getResponseData();
				
				// Get the position of the content control.
				x = m_contentControlX;
				y = m_contentControlY;
				
				// Have we already created a "Manage Database Prune" dialog?
				if ( m_manageDatabasePruneDlg == null )
				{
					int width;
					int height;
					
					// No, create one.
					height = m_dlgHeight;
					width = m_dlgWidth;
					ManageDatabasePruneDlg.createAsync(
							true, 
							false,
							x, 
							y,
							width,
							height,
							new ManageDatabasePruneDlgClient()
					{			
						@Override
						public void onUnavailable()
						{
							// Nothing to do.  Error handled in asynchronous provider.
						}
						
						@Override
						public void onSuccess( final ManageDatabasePruneDlg cfsaDlg )
						{
							ScheduledCommand cmd;
							
							cmd = new ScheduledCommand()
							{
								@Override
								public void execute() 
								{
									m_manageDatabasePruneDlg = cfsaDlg;
									
									m_manageDatabasePruneDlg.init( databasePruneConfiguration );
									m_manageDatabasePruneDlg.show();
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					} );
				}
				else
				{
					m_manageDatabasePruneDlg.init( databasePruneConfiguration );
					m_manageDatabasePruneDlg.setPopupPosition( x, y );
					m_manageDatabasePruneDlg.show();
				}
			}
		};

		// Issue an ajax request to get the Database Prune configuration.  When we get the Database Prune configuration
		// we will invoke the "Manage database prune" dialog.
		{
			GetDatabasePruneConfigurationCmd cmd;
			
			// Issue an ajax request to get the Database Prune configuration from the db.
			cmd = new GetDatabasePruneConfigurationCmd();
			GwtClientHelper.executeCommand( cmd, rpcReadCallback );
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
											true, 
											false,
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
			m_manageNetFoldersDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
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
											true, 
											false,
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
			m_manageNetFolderRootsDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
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
									true, 
									false,
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
			m_manageGroupsDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			m_manageGroupsDlg.init();
			m_manageGroupsDlg.setPopupPosition( x, y );
			m_manageGroupsDlg.show();
		}
	}
	
	/**
	 * Handles InvokeManageUsersDlgEvent received by this class.
	 * 
	 * Implements the InvokeManageUsersDlgEvent.Handler.onInvokeManageUsersDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeManageUsersDlg( InvokeManageUsersDlgEvent event )
	{
		// Get the position of the content control.
		final int x = m_contentControlX;
		final int y = m_contentControlY;
		
		// Have we already created a "Manage Users" dialog?
		if ( m_manageUsersDlg == null )
		{
			// No, create one.
			ManageUsersDlg.createAsync( new ManageUsersDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ManageUsersDlg muDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_manageUsersDlg = muDlg;
							ManageUsersDlg.initAndShow( m_manageUsersDlg, x, y, m_dlgWidth, m_dlgHeight );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			},
			true,	// true -> auto hide.
			false,	// false -> not Modal.
			x, 
			y,
			m_dlgWidth,
			m_dlgHeight );
		}
		
		else
		{
			// Yes, we've already created a "Manage Users" dialog!
			// Simply initialize and show it.
			m_manageUsersDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			ManageUsersDlg.initAndShow( m_manageUsersDlg, x, y, m_dlgWidth, m_dlgHeight );
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
	 * Handles InvokeRunAReportDlgEvent received by this class.
	 * 
	 * Implements the InvokeRunAReportDlgEvent.Handler.onInvokeRunAReportDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeRunAReportDlg( InvokeRunAReportDlgEvent event )
	{
		// Get the position of the content control.
		final int x = m_contentControlX;
		final int y = m_contentControlY;
		
		// Have we already created a "Run a Report" dialog?
		if ( m_runAReportDlg == null )
		{
			// No, create one.
			RunAReportDlg.createAsync( new RunAReportDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final RunAReportDlg muDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_runAReportDlg = muDlg;
							RunAReportDlg.initAndShow( m_runAReportDlg, x, y );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			},
			true,	// true -> auto hide.
			false,	// false -> not Modal.
			x, 
			y,
			m_dlgWidth,
			m_dlgHeight );
		}
		
		else
		{
			// Yes, we've already created a "Run a Report" dialog!
			// Simply initialize and show it.
			m_runAReportDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			RunAReportDlg.initAndShow( m_runAReportDlg, x, y );
		}
	}
	
	/**
	 * Handles the InvokeUserDesktopSettingsDlgEvent
	 */
	@Override
	public void onInvokeUserDesktopSettingsDlg( InvokeUserDesktopSettingsDlgEvent event )
	{
		int x;
		int y;
		final List<Long> userIds;

		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		userIds = event.getUserIds();
		
		// Have we already created a "Configure User Desktop App" dialog?
		if ( m_configureUserFileSyncAppDlg == null )
		{
			int width;
			int height;
			
			// No, create one.
			height = m_dlgHeight;
			width = m_dlgWidth;
			ConfigureUserFileSyncAppDlg.createAsync(
											true, 
											false,
											x, 
											y,
											width,
											height,
											new ConfigureUserFileSyncAppDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ConfigureUserFileSyncAppDlg cufsaDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_configureUserFileSyncAppDlg = cufsaDlg;
							
							m_configureUserFileSyncAppDlg.init( userIds );
							m_configureUserFileSyncAppDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_configureUserFileSyncAppDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			m_configureUserFileSyncAppDlg.init( userIds );
			m_configureUserFileSyncAppDlg.setPopupPosition( x, y );
			m_configureUserFileSyncAppDlg.show();
		}
	}
	
	/**
	 * Handles the InvokeUserMobileSettingsDlgEvent
	 */
	@Override
	public void onInvokeUserMobileSettingsDlg( InvokeUserMobileSettingsDlgEvent event )
	{
		int x;
		int y;
		final List<Long> userIds;

		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		userIds = event.getUserIds();
		
		// Have we already created a "Configure User Mobile Apps" dialog?
		if ( m_configureUserMobileAppsDlg == null )
		{
			int width;
			int height;
			
			// No, create one.
			height = m_dlgHeight;
			width = m_dlgWidth;
			ConfigureUserMobileAppsDlg.createAsync(
											true, 
											false,
											x, 
											y,
											width,
											height,
											new ConfigureUserMobileAppsDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ConfigureUserMobileAppsDlg cumaDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_configureUserMobileAppsDlg = cumaDlg;
							
							m_configureUserMobileAppsDlg.init( userIds );
							m_configureUserMobileAppsDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_configureUserMobileAppsDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			m_configureUserMobileAppsDlg.init( userIds );
			m_configureUserMobileAppsDlg.setPopupPosition( x, y );
			m_configureUserMobileAppsDlg.show();
		}
	}
	
	/**
	 * Handles ManageSharesSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the ManageSharesSelectedEntitiesEvent.Handler.onManageSharesSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onManageSharesSelectedEntities( ManageSharesSelectedEntitiesEvent event )
	{
		Scheduler.ScheduledCommand cmd;

		// Invoke the Manage Shares dialog
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute() 
			{
				invokeManageSharesDlg();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
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
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents()
	{
		// If we having allocated a list to track events we've
		// registered yet...
		if ( null == m_registeredEventHandlers )
		{
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if ( m_registeredEventHandlers.isEmpty() )
		{
			// ...register the events.
			EventHelper.registerEventHandlers(
										GwtTeaming.getEventBus(),
										m_registeredEvents,
										this,
										m_registeredEventHandlers );
		}
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( ( null != m_registeredEventHandlers ) && ( ! ( m_registeredEventHandlers.isEmpty() ) ) )
		{
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}
	
	/**
	 * Update the information found on the home page
	 */
	private void updateHomePage()
	{
		AsyncCallback<VibeRpcResponse> callback;
		
		callback = new AsyncCallback<VibeRpcResponse>()
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
			public void onSuccess( VibeRpcResponse response )
			{
				final GwtUpgradeInfo upgradeInfo;
				Scheduler.ScheduledCommand cmd;
				
				upgradeInfo = (GwtUpgradeInfo) response.getResponseData();
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute() 
					{
						// Update the admin console home page with the latest info.
						if ( m_homePage != null )
							m_homePage.init( upgradeInfo );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}// end onSuccess()
		};

		getUpgradeInfoFromServer( callback );
	}
	
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
