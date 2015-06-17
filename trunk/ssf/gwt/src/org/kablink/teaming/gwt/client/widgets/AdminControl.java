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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.datatable.ManageMobileDevicesDlg;
import org.kablink.teaming.gwt.client.datatable.ManageMobileDevicesDlg.ManageMobileDevicesDlgClient;
import org.kablink.teaming.gwt.client.datatable.ManageProxyIdentitiesDlg;
import org.kablink.teaming.gwt.client.datatable.ManageProxyIdentitiesDlg.ManageProxyIdentitiesDlgClient;
import org.kablink.teaming.gwt.client.event.EditSiteBrandingEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.AdministrationActionEvent;
import org.kablink.teaming.gwt.client.event.InvokeConfigureAdhocFoldersDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeConfigureFileSyncAppDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeConfigureMobileAppsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeConfigurePasswordPolicyDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeConfigureShareSettingsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeConfigureUpdateLogsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeConfigureUserAccessDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeEditKeyShieldConfigDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeEditLdapConfigDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeEditNetFolderDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeLimitUserVisibilityDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageAdministratorsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeNetFolderGlobalSettingsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageDatabasePruneDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageMobileDevicesDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageNetFolderRootsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageProxyIdentitiesDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageGroupsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageTeamsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageUsersDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeManageNetFoldersDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeNameCompletionSettingsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokePrincipalDesktopSettingsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokePrincipalMobileSettingsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeRunAReportDlgEvent;
import org.kablink.teaming.gwt.client.event.ManageSharesSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ManageUserVisibilityEvent;
import org.kablink.teaming.gwt.client.event.PreLogoutEvent;
import org.kablink.teaming.gwt.client.event.SidebarHideEvent;
import org.kablink.teaming.gwt.client.event.SidebarShowEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.AdminConsoleInfo;
import org.kablink.teaming.gwt.client.AdminConsoleInfo.AdminConsoleDialogMode;
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
import org.kablink.teaming.gwt.client.binderviews.MobileDevicesView;
import org.kablink.teaming.gwt.client.rpc.shared.GetAdminActionsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetDatabasePruneConfigurationCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFileSyncAppConfigurationCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetSiteBrandingCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetUpgradeInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveBrandingCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HistoryHelper;
import org.kablink.teaming.gwt.client.util.MobileDevicesInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.AdminInfoDlg.AdminInfoDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigureAdhocFoldersDlg.ConfigureAdhocFoldersDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigureFileSyncAppDlg.ConfigureFileSyncAppDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigureMobileAppsDlg.ConfigureMobileAppsDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigureUpdateLogsDlg.ConfigureUpdateLogsDlgClient;
import org.kablink.teaming.gwt.client.widgets.EditBrandingDlg.EditBrandingDlgClient;
import org.kablink.teaming.gwt.client.widgets.EditKeyShieldConfigDlg.EditKeyShieldConfigDlgClient;
import org.kablink.teaming.gwt.client.widgets.EditLdapConfigDlg.EditLdapConfigDlgClient;
import org.kablink.teaming.gwt.client.widgets.EditZoneShareSettingsDlg.EditZoneShareSettingsDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigurePasswordPolicyDlg.ConfigurePasswordPolicyDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigureUserAccessDlg.ConfigureUserAccessDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigureUserFileSyncAppDlg.ConfigureUserFileSyncAppDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfigureUserMobileAppsDlg.ConfigureUserMobileAppsDlgClient;
import org.kablink.teaming.gwt.client.widgets.ContentControl.ContentControlClient;
import org.kablink.teaming.gwt.client.widgets.LimitUserVisibilityDlg.LimitUserVisibilityDlgClient;
import org.kablink.teaming.gwt.client.widgets.ManageAdministratorsDlg.ManageAdministratorsDlgClient;
import org.kablink.teaming.gwt.client.widgets.NetFolderGlobalSettingsDlg.NetFolderGlobalSettingsDlgClient;
import org.kablink.teaming.gwt.client.widgets.ManageDatabasePruneDlg.ManageDatabasePruneDlgClient;
import org.kablink.teaming.gwt.client.widgets.ManageGroupsDlg.ManageGroupsDlgClient;
import org.kablink.teaming.gwt.client.widgets.ManageNetFolderRootsDlg.ManageNetFolderRootsDlgClient;
import org.kablink.teaming.gwt.client.widgets.ManageNetFoldersDlg.ManageNetFoldersDlgClient;
import org.kablink.teaming.gwt.client.widgets.ManageTeamsDlg.ManageTeamsDlgClient;
import org.kablink.teaming.gwt.client.widgets.ManageUsersDlg.ManageUsersDlgClient;
import org.kablink.teaming.gwt.client.widgets.ModifyNetFolderDlg.ModifyNetFolderDlgClient;
import org.kablink.teaming.gwt.client.widgets.NameCompletionSettingsDlg.NameCompletionSettingsDlgClient;
import org.kablink.teaming.gwt.client.widgets.RunAReportDlg.RunAReportDlgClient;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg2.ShareThisDlg2Client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This widget will display the controls that make up the
 * 'Administration' control.  There is a widget that displays the list
 * of administration actions and a widget that displays the page for
 * the selected administration action.
 * 
 * @author drfoster@novell.com
 */
public class AdminControl extends TeamingPopupPanel
	implements 
		// Event handlers implemented by this class.
		AdministrationActionEvent.Handler,
		EditSiteBrandingEvent.Handler,
		InvokeConfigureAdhocFoldersDlgEvent.Handler,
		InvokeConfigureFileSyncAppDlgEvent.Handler,
		InvokeConfigureMobileAppsDlgEvent.Handler,
		InvokeConfigurePasswordPolicyDlgEvent.Handler,
		InvokeConfigureShareSettingsDlgEvent.Handler,
		InvokeConfigureUserAccessDlgEvent.Handler,
		InvokeConfigureUpdateLogsDlgEvent.Handler,
		InvokeEditKeyShieldConfigDlgEvent.Handler,
		InvokeEditLdapConfigDlgEvent.Handler,
		InvokeEditNetFolderDlgEvent.Handler,
		InvokeLimitUserVisibilityDlgEvent.Handler,
		InvokeNetFolderGlobalSettingsDlgEvent.Handler,
		InvokeManageAdministratorsDlgEvent.Handler,
		InvokeManageDatabasePruneDlgEvent.Handler,
		InvokeManageNetFoldersDlgEvent.Handler,
		InvokeManageNetFolderRootsDlgEvent.Handler,
		InvokeManageGroupsDlgEvent.Handler,
		InvokeManageMobileDevicesDlgEvent.Handler,
		InvokeManageProxyIdentitiesDlgEvent.Handler,
		InvokeManageTeamsDlgEvent.Handler,
		InvokeManageUsersDlgEvent.Handler,
		InvokeNameCompletionSettingsDlgEvent.Handler,
		InvokePrincipalDesktopSettingsDlgEvent.Handler,
		InvokePrincipalMobileSettingsDlgEvent.Handler,
		InvokeRunAReportDlgEvent.Handler,
		ManageSharesSelectedEntitiesEvent.Handler,
		ManageUserVisibilityEvent.Handler,
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
	private AdminControlGlassPanel m_glassPanel;
	private AdminConsoleHomePage m_homePage = null;
	private ConfigureFileSyncAppDlg m_configureFileSyncAppDlg = null;
	private ConfigureMobileAppsDlg m_configureMobileAppsDlg = null;
	private ConfigureUserMobileAppsDlg m_configureUserMobileAppsDlg = null;
	private ConfigureUserFileSyncAppDlg m_configureUserFileSyncAppDlg = null;
	private ManageAdministratorsDlg m_manageAdministratorsDlg = null;
	private ManageDatabasePruneDlg m_manageDatabasePruneDlg = null;
	private ManageGroupsDlg m_manageGroupsDlg = null;
	private ManageNetFoldersDlg m_manageNetFoldersDlg = null;
	private ManageNetFolderRootsDlg m_manageNetFolderRootsDlg = null;
	private ManageMobileDevicesDlg m_manageMobileDevicesDlg = null;
	private ManageProxyIdentitiesDlg m_manageProxyIdentitiesDlg = null;
	private ManageTeamsDlg m_manageTeamsDlg = null;
	private ManageUsersDlg m_manageUsersDlg = null;
	private RunAReportDlg m_runAReportDlg = null;
	private ConfigurePasswordPolicyDlg m_configurePasswordPolicyDlg = null;
	private ConfigureUpdateLogsDlg m_configureUpdateLogsDlg = null;
	private ConfigureUserAccessDlg m_configureUserAccessDlg = null;
	private ConfigureAdhocFoldersDlg m_configureAdhocFoldersDlg = null;
	private EditZoneShareSettingsDlg m_editZoneShareSettingsDlg = null;
	private LimitUserVisibilityDlg m_limitUserVisibilityDlg = null;
	private ModifyNetFolderDlg m_modifyNetFolderDlg = null;
	private ShareThisDlg2 m_shareDlg = null;
	private NetFolderGlobalSettingsDlg m_nfGlobalSettingsDlg = null;
	private EditBrandingDlg m_editSiteBrandingDlg = null;
	private EditLdapConfigDlg m_editLdapConfigDlg = null;
	private NameCompletionSettingsDlg m_nameCompletionSettingsDlg = null;
	private EditKeyShieldConfigDlg m_editKeyShieldConfigDlg = null;
	private EditSuccessfulHandler m_editBrandingSuccessHandler = null;
	private List<HandlerRegistration> m_registeredEventHandlers;
	private AdminConsoleDialogMode m_acDlgMode = AdminConsoleDialogMode.MODAL; 
	private Map<AdminAction, Boolean> m_modalActionMap = null;	// Maps AdminAction's to whether they should display in a modal dialog.
	
	// The following are used to coordinate firing an 'on layout' event
	// AFTER all the layout requests in a sequence of them have
	// finished.
	private Timer				m_onLayoutEventTimer;
	private VibeEventBase<?>	m_onLayoutEvent;
	private final static int	ON_LAYOUT_EVENT_DELAY = 250;

	private final static int CONTENT_CONTROL_Z_INDEX	= 1010;
	private final static int GLASS_PANEL_Z_INDEX		= 1000;

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		// Administration events.
		TeamingEvents.ADMINISTRATION_ACTION,
		TeamingEvents.EDIT_SITE_BRANDING,
		TeamingEvents.INVOKE_CONFIGURE_ADHOC_FOLDERS_DLG,
		TeamingEvents.INVOKE_CONFIGURE_FILE_SYNC_APP_DLG,
		TeamingEvents.INVOKE_CONFIGURE_MOBILE_APPS_DLG,
		TeamingEvents.INVOKE_CONFIGURE_PASSWORD_POLICY_DLG,
		TeamingEvents.INVOKE_CONFIGURE_SHARE_SETTINGS_DLG,
		TeamingEvents.INVOKE_CONFIGURE_UPDATE_LOGS_DLG,
		TeamingEvents.INVOKE_CONFIGURE_USER_ACCESS_DLG,
		TeamingEvents.INVOKE_EDIT_KEYSHIELD_CONFIG_DLG,
		TeamingEvents.INVOKE_EDIT_LDAP_CONFIG_DLG,
		TeamingEvents.INVOKE_EDIT_NET_FOLDER_DLG,
		TeamingEvents.INVOKE_LIMIT_USER_VISIBILITY_DLG,
		TeamingEvents.INVOKE_NET_FOLDER_GLOBAL_SETTINGS_DLG,
		TeamingEvents.INVOKE_MANAGE_ADMINISTRATORS_DLG,
		TeamingEvents.INVOKE_MANAGE_DATABASE_PRUNE_DLG,
		TeamingEvents.INVOKE_MANAGE_NET_FOLDERS_DLG,
		TeamingEvents.INVOKE_MANAGE_NET_FOLDER_ROOTS_DLG,
		TeamingEvents.INVOKE_MANAGE_PROXY_IDENTITIES_DLG,
		TeamingEvents.INVOKE_MANAGE_GROUPS_DLG,
		TeamingEvents.INVOKE_MANAGE_USERS_DLG,
		TeamingEvents.INVOKE_MANAGE_TEAMS_DLG,
		TeamingEvents.INVOKE_NAME_COMPLETION_SETTINGS_DLG,
		TeamingEvents.INVOKE_PRINCIPAL_DESKTOP_SETTINGS_DLG,
		TeamingEvents.INVOKE_PRINCIPAL_MOBILE_SETTINGS_DLG,
		TeamingEvents.INVOKE_MANAGE_MOBILE_DEVICES_DLG,
		TeamingEvents.INVOKE_RUN_A_REPORT_DLG,
		TeamingEvents.MANAGE_SHARES_SELECTED_ENTITIES,
		TeamingEvents.MANAGE_USER_VISIBILITY,
		
		// Login/out events.
		TeamingEvents.PRE_LOGOUT,
		
		// Sidebar events.
		TeamingEvents.SIDEBAR_HIDE,
		TeamingEvents.SIDEBAR_SHOW,
	};

	/*
	 */
	private class AdminControlGlassPanel extends PopupPanel
	{
		private boolean m_glassModal;	//
		
		AdminControlGlassPanel() {
			super(false, false);
		}
		
		/*
		 * Intercepts mouse down events.  For non-modal JSP dialogs, it
		 * closes them.
		 * 
		 * Overrides PopupPanel.onPreviewNativeEvent().
		 */
		@Override
		protected void onPreviewNativeEvent(NativePreviewEvent nativeEvent) {
			// Is this a mouse down event?
		    Event event = Event.as(nativeEvent.getNativeEvent());
		    int type = event.getTypeInt();
		    if (0 != (type & Event.ONMOUSEDOWN)) {
				// Yes!  Is this a non-modal dialog?
				if ( ! m_glassModal )
				{
					// Yes!  Show the home page...
					showHomePageAsync();

					// ...and consume the event.
					nativeEvent.consume();
					nativeEvent.cancel();
					
					return;
				}
		    }
			
			// If we get here, we need to pass the event on through the
			// PopupPanel's handler.
			super.onPreviewNativeEvent(nativeEvent);
		}
		
		/**
		 */
		@Override
		public Element getGlassElement() {
			return super.getGlassElement();
		}

		/**
		 * Sets whether the glass panel is currently used for a modal
		 * dialog or not.
		 * 
		 * @param glassModal
		 */
		public void setGlassModal(boolean glassModal) {
			m_glassModal = glassModal;
			if (glassModal)
			     setGlassStyleName("teamingDlgBox_Glass"     );
			else setGlassStyleName("teamingDlgBox_GlassClear");
		}
	}
	
	/*
	 * Inner class used for the UI for an administration action.
	 */
	private class AdminActionControl extends Composite implements ClickHandler, MouseOverHandler, MouseOutHandler {
		private GwtAdminAction	m_adminAction;	//
		private InlineLabel		m_actionName;	//
		
		/**
		 */
		public AdminActionControl(GwtAdminAction adminAction) {
			// Remember the action we are associated with.
			m_adminAction = adminAction;
			
			FlowPanel mainPanel = new FlowPanel();
			
			m_actionName = new InlineLabel(adminAction.getLocalizedName());
			m_actionName.addClickHandler(    this);
			m_actionName.addMouseOverHandler(this);
			m_actionName.addMouseOutHandler( this);
			m_actionName.addStyleName("adminActionControl");
			m_actionName.addStyleName("cursorPointer"     );
			
			// For automation purposes, give the label a unique id.
			String id = ("adminAction-" + String.valueOf(adminAction.getActionType().ordinal()));
			m_actionName.getElement().setId(id);
			
			mainPanel.add(m_actionName);
			
			// All composites must call initWidget() in their
			// constructors.
			initWidget(mainPanel);
		}
		
		/**
		 */
		@Override
		public void onClick(ClickEvent event) {
			// Push it into the history cache...
			HistoryHelper.pushHistoryInfoAsync(m_adminAction);
			
			// ...and tell the AdminControl that an action was
			// ...selected.
			adminActionSelected(m_adminAction);
		}
		
		/**
		 */
		@Override
		public void onMouseOver(MouseOverEvent event) {
			m_actionName.addStyleName("adminActionControlMouseOver");
		}
		
		/**
		 */
		@Override
		public void onMouseOut(MouseOutEvent event) {
			m_actionName.removeStyleName("adminActionControlMouseOver");
		}
	}
	
	/*
	 * Inner class used for the UI for an administration category.
	 */
	private class AdminCategoryControl extends Composite implements ClickHandler {
		private FlexTable	m_actionsTable;	//
		private FlexTable	m_mainTable;	//
		private Image		m_collapsedImg;	//
		private Image		m_expandedImg;	//
		
		/**
		 */
		public AdminCategoryControl(GwtAdminCategory category) {
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
			cellFormatter.setColSpan(row, 1, 2);
			m_mainTable.setWidget(row, 1, m_actionsTable);
			
			// Add a UI widget for all of the actions associated with
			// this category.
			actions = category.getActions();
			if (null != actions) {
				//! DRF:  Add controls here to limit things shown while
				//!       they're being implemented.
				boolean showManageMobileDevices = MobileDevicesView.SHOW_MOBILE_DEVICES_SYSTEM;
				boolean showPasswordPolicy      = GwtClientHelper.isPasswordPolicyEnabled();
				for (GwtAdminAction action:  actions) {
					if      (action.getActionType().equals(AdminAction.MANAGE_MOBILE_DEVICES)     && (!showManageMobileDevices)) continue;
					else if (action.getActionType().equals(AdminAction.CONFIGURE_PASSWORD_POLICY) && (!showPasswordPolicy))      continue;
					
					// Add a UI widget for this administration action.
					AdminActionControl adminActionControl = new AdminActionControl( action );
					m_actionsTable.setWidget( m_actionsTable.getRowCount(), 0, adminActionControl );
				}
			}
			
			// All composites must call initWidget() in their
			// constructors.
			initWidget(m_mainTable);
		}
		
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
		@SuppressWarnings("unused")
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
							GwtClientHelper.deferCommand( new ScheduledCommand()
							{
								@Override
								public void execute()
								{
									// Fire the event that closes the admin console.
									FilrActionsCtrl.closeAdminConsole();
								}
							} );
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
					m_acDlgMode = adminConsoleInfo.getAdminConsoleDialogMode();
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
					
					upgradeInfo = (GwtUpgradeInfo) response.getResponseData();
					
					GwtClientHelper.deferCommand( new ScheduledCommand()
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
					} );
				}// end onSuccess()
			};

			// Issue a deferred command to get the administration actions the user has rights to run.
			{
				GwtClientHelper.deferCommand( new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						getAdminActionsFromServer();
					}
				} );
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
		ContentControl.createControl(
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
				m_contentControl.getElement().getStyle().setZIndex( CONTENT_CONTROL_Z_INDEX );
				m_contentControl.setVisible( false );
				m_contentControl.addStyleName( "adminContentControl" );
				mainPanel.add( m_contentControl );
				relayoutPage();
			}// end onSuccess()
		} );
		
		// Issue an rpc request to get the info needed to update the home page
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute() 
			{
				updateHomePage();
			}
		} );
		
		// Create a glass panel that will be used when a jsp implementation of a dialog is used
		{
			Style style;
			
			m_glassPanel = new AdminControlGlassPanel();
			m_glassPanel.getElement().setId( "AdminControlGlassPanel" );
			m_glassPanel.setGlassStyleName( "teamingDlgBox_Glass" );

			style = m_glassPanel.getElement().getStyle();
			style.setZIndex( GLASS_PANEL_Z_INDEX + 2);
			style.setOpacity( 0 );
			style.setLeft( 0, Unit.PX );
			style.setTop( 0, Unit.PX );
			style.setHeight( 0, Unit.PX );
			style.setWidth( 0, Unit.PX );
		}
		
		setWidget( mainPanel );
	}// end AdminControl()

	/**
	 * This method gets called when the user selects one of the administration actions.
	 */
	private void adminActionSelected( final GwtAdminAction adminAction )
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
		else if ( adminAction.getActionType() == AdminAction.CONFIGURE_PASSWORD_POLICY )
		{
			// Fire the event to invoke the "Configure Password Policy" dialog
			InvokeConfigurePasswordPolicyDlgEvent.fireOne();
		}
		else if ( adminAction.getActionType() == AdminAction.CONFIGURE_FOLDER_UPDATE_LOGS )
		{
			// Fire the event to invoke the "Configure Update Logs" dialog
			InvokeConfigureUpdateLogsDlgEvent.fireOne();
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
		else if ( adminAction.getActionType() == AdminAction.MANAGE_ADMINISTRATORS )
		{
			// Fire the event to invoke the "Manage Administrators" dialog.
			GwtTeaming.fireEvent( new InvokeManageAdministratorsDlgEvent() );
		}
		else if ( adminAction.getActionType() == AdminAction.MANAGE_GROUPS )
		{
			// Fire the event to invoke the "Manage Groups" dialog.
			InvokeManageGroupsDlgEvent.fireOne();
		}
		else if ( adminAction.getActionType() == AdminAction.MANAGE_TEAMS )
		{
			// Fire the event to invoke the "Manage Teams" dialog.
			GwtTeaming.fireEvent( new InvokeManageTeamsDlgEvent( false ) );	// false -> Not a trash view.
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
		
		else if ( adminAction.getActionType() == AdminAction.MANAGE_USER_VISIBILITY )
		{
			// Fire the event to invoke the limit user visibility
			// dialog.
			ManageUserVisibilityEvent.fireOne();
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
			GwtTeaming.fireEvent( new InvokeManageUsersDlgEvent( false ) );	// false -> Not a trash view.
		}
		
		else if ( adminAction.getActionType() == AdminAction.MANAGE_MOBILE_DEVICES )
		{
			// Fire the event to invoke the "Manage mobile devices" dialog.
			InvokeManageMobileDevicesDlgEvent.fireOne();
		}
		
		else if ( adminAction.getActionType() == AdminAction.MANAGE_PROXY_IDENTITIES )
		{
			// Fire the event to invoke the "Manage proxy identities" dialog.
			InvokeManageProxyIdentitiesDlgEvent.fireOne();
		}
		
		else if ( adminAction.getActionType() == AdminAction.RUN_A_REPORT )
		{
			// Fire the event to invoke the "Run a Report" dialog.
			InvokeRunAReportDlgEvent.fireOne();
		}
		
		else if ( adminAction.getActionType() == AdminAction.NET_FOLDER_GLOBAL_SETTINGS )
		{
			// Fire the event to invoke the "Net Folder Global Settings" dialog.
			InvokeNetFolderGlobalSettingsDlgEvent.fireOne();
		}
		else if ( adminAction.getActionType() == AdminAction.KEYSHIELD_CONFIG )
		{
			// Fire the event to invoke the "Edit KeyShield configuration" dialog.
			InvokeEditKeyShieldConfigDlgEvent.fireOne();
		}
		else if ( adminAction.getActionType() == AdminAction.LDAP_CONFIG )
		{
			// Fire the event to invoke the "Edit ldap configuration" dialog.
			InvokeEditLdapConfigDlgEvent.fireOne();
		}
		else if ( adminAction.getActionType() == AdminAction.CONFIGURE_NAME_COMPLETION )
		{
			// Fire the event to invoke the "Name Completion Settings" dialog.
			InvokeNameCompletionSettingsDlgEvent.fireOne();
		}
		else
		{
			String url;
			
			// Get the url used by the selected action.
			url = adminAction.getUrl();
			if ( url != null && url.length() > 0 )
			{
				// Clear the iframe's content 
				ContentControl.clear( m_contentControl );
				
				// Set the iframe's content to the selected administration page.
				ContentControl.setContentFrameUrl( m_contentControl, url, Instigator.ADMINISTRATION_CONSOLE );
				
				GwtClientHelper.deferCommand( new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						// Show an position the content control.
						showContentPanel( isActionModal( adminAction.getActionType() ) );
						relayoutPage();
					}
				} );
			}
		}
	}// end adminActionSelected()

	
	/**
	 * For some reason if we try to logout while the "configure ldap" page is still loaded
	 * we see an error in IE.  So clear the content panel.
	 */
	private void doPreLogoutCleanup()
	{
		if ( isShowing() == true )
		{
			// Clear the iframe's content 
			ContentControl.clear( m_contentControl );
		
			// Set the iframe's content to nothing.
			ContentControl.setContentFrameUrl( m_contentControl, "", Instigator.ADMINISTRATION_CONSOLE );
		}
	}// end doPreLogoutCleanup()
	
	
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
	public int getContentControlHeight()
	{
		return (m_contentControlHeight + 20);
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
	private void hideControl()
	{
		if ( isShowing() )
		{
			hide();
		}
	}// end hideControl()
	
	
	/**
	 * 
	 */
	private void hideTreeControl()
	{
		if ( m_adminActionsTreeControl.isVisible() )
		{
			m_adminActionsTreeControl.setVisible( false );
		}
	}// end hideTreeControl()
	
	
	/**
	 * 
	 */
	private void invokeEditKeyShieldConfigDlg()
	{
		// Have we created the dialog yet?
		if ( m_editKeyShieldConfigDlg == null )
		{
			// Create the dialog.
			EditKeyShieldConfigDlg.createDlg(
									isActionAutoHide( AdminAction.KEYSHIELD_CONFIG ),
									isActionModal(    AdminAction.KEYSHIELD_CONFIG ),
									m_contentControlX,
									m_contentControlY,
									m_dlgWidth,
									m_dlgHeight,
									new EditKeyShieldConfigDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( EditKeyShieldConfigDlg ekcDlg )
				{
					m_editKeyShieldConfigDlg = ekcDlg;

					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							invokeEditKeyShieldConfigDlg();
						}
					} );
				}
			} );
		}
		else
		{
			// Show the dialog.
			EditKeyShieldConfigDlg.initAndShow(
										m_editKeyShieldConfigDlg,
										m_dlgWidth,
										m_dlgHeight,
										m_contentControlX,
										m_contentControlY,
										null );
		}
	}
	
	/**
	 * 
	 */
	private void invokeEditLdapConfigDlg()
	{
		// Have we created the dialog yet?
		if ( m_editLdapConfigDlg == null )
		{
			// Create the dialog.
			EditLdapConfigDlg.createDlg(
									isActionAutoHide( AdminAction.LDAP_CONFIG ),
									isActionModal(    AdminAction.LDAP_CONFIG ),
									m_contentControlX,
									m_contentControlY,
									m_dlgWidth,
									m_dlgHeight,
									new EditLdapConfigDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( EditLdapConfigDlg elcDlg )
				{
					m_editLdapConfigDlg = elcDlg;

					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							invokeEditLdapConfigDlg();
						}
					} );
				}
			} );
		}
		else
		{
			// Show the dialog.
			EditLdapConfigDlg.initAndShow(
										m_editLdapConfigDlg,
										m_dlgWidth,
										m_dlgHeight,
										m_contentControlX,
										m_contentControlY,
										null );
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
					GwtClientHelper.deferCommand( new ScheduledCommand()
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
					} );
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
			// No, create it.
			ModifyNetFolderDlg.createDlg(
										false,
										true,
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
					m_modifyNetFolderDlg = mnfDlg;

					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							invokeEditNetFolderDlg( netFolder, showRelativeTo );
						}
					} );
				}
			} );
		}
		else
		{
			ModifyNetFolderDlg.initAndShow(
										m_modifyNetFolderDlg,
										m_contentControlX,
										m_contentControlY,
										showRelativeTo,
										netFolder,
										null );
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
								GwtClientHelper.deferCommand( new ScheduledCommand()
								{
									@Override
									public void execute()
									{
										MastHead mastHead;
										
										mastHead = GwtTeaming.getMainPage().getMastHead();
										
										// Tell the masthead to go get the new site branding.
										if ( mastHead != null )
											mastHead.refreshBranding();
									}
								} );
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
			// No, create it.
			EditBrandingDlg.createDlg(
									isActionAutoHide( AdminAction.SITE_BRANDING ),
									isActionModal(    AdminAction.SITE_BRANDING ),
									x,
									y,
									m_dlgWidth,
									m_dlgHeight,
									m_editBrandingSuccessHandler,
									null,
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
					
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							invokeEditSiteBrandingDlgImpl( brandingData, x, y );
						}
					} );
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
		// Run an async cmd to show the dialog.
		EditBrandingDlg.initAndShow(
								m_editSiteBrandingDlg,
								brandingData,
								new Integer( m_contentControlX ),
								new Integer( m_contentControlY ),
								new Integer( m_dlgWidth ),
								new Integer( m_dlgHeight ),
								null );
	}

	/*
	 * Invokes the Share dialog in administrative mode.
	 */
	private void invokeManageSharesDlg()
	{
		// Have we created a share dialog yet?
		if ( m_shareDlg == null )
		{
			ShareThisDlg2Client client;
			
			// No, create it.
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
					
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							invokeManageSharesDlg();
						}
					} );
				}
			};

			ShareThisDlg2.createDlg(
								isActionAutoHide( AdminAction.MANAGE_SHARE_ITEMS ),
								isActionModal(    AdminAction.MANAGE_SHARE_ITEMS ),
								m_contentControlX,
								m_contentControlY,
								new Integer( m_dlgWidth ),
								new Integer( m_dlgHeight ),
								ShareThisDlg2.ShareThisDlgMode.MANAGE_ALL,
								client );
		}
		else
		{
			// Yes, we've already create a share dialog!  Simply show it.
			GwtClientHelper.deferCommand( new ScheduledCommand()
			{
				@Override
				public void execute() 
				{
					// Run the async command to show the dialog
					ShareThisDlg2.initAndShow(
											m_shareDlg,
											GwtTeaming.getMessages().shareDlg_manageShares(),
											null,
											ShareThisDlg2.ShareThisDlgMode.MANAGE_ALL,
											null,
											null,
											new Integer( m_dlgWidth ),
											new Integer( m_dlgHeight ),
											new Boolean( false ),
											null );
				}
			} );
		}
	}
	
	/**
	 * 
	 */
	private void invokeNameCompletionSettingsDlg()
	{
		int x;
		int y;
		
		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		if ( m_nameCompletionSettingsDlg == null )
		{
			Integer width;
			Integer height;
			
			height = new Integer( m_dlgHeight );
			width = new Integer( m_dlgWidth );

			// Run an async cmd to create the dialog.
			NameCompletionSettingsDlg.createDlg(
											isActionAutoHide( AdminAction.CONFIGURE_NAME_COMPLETION ),
											isActionModal(    AdminAction.CONFIGURE_NAME_COMPLETION ),
											new Integer( x ),
											new Integer( y ),
											width,
											height,
											new NameCompletionSettingsDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final NameCompletionSettingsDlg ncsDlg )
				{
					m_nameCompletionSettingsDlg = ncsDlg;
					
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							// Now that we have created the dialog,
							// fire the event to invoke the "Name Completion Settings" dialog.
							InvokeNameCompletionSettingsDlgEvent.fireOne();
						}
					} );
				}
			} );

		}
		else
		{
			// Run an async cmd to show the dialog.
			NameCompletionSettingsDlg.initAndShow(
												m_nameCompletionSettingsDlg,
												new Integer( x ),
												new Integer( y ),
												new Integer( m_dlgWidth ),
												new Integer( m_dlgHeight ),
												null );
		}
	}

	/**
	 * 
	 */
	private void relayoutPage( final VibeEventBase<?> fireOnLayout )
	{
		// If the AdminControl is visible...
		if ( isShowing() )
		{		
			GwtClientHelper.deferCommand( new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					// ...update the page's layout.
					relayoutPageNow( fireOnLayout );
				}
			} );
		}
	}// end relayoutPage()
	
	private void relayoutPage()
	{
		VibeEventBase<?> fireOnLayout = null;
		
		// Always use the initial form of the method.
		relayoutPage( fireOnLayout );	// null -> No event needs to be fired when the layout is complete.
	}// end relayoutPage()
	
	
	/*
	 */
	private void relayoutPageNow( final VibeEventBase<?> fireOnLayout )
	{
		int width;
		int height;
		int x;
		Style style;
		
		// If we have a Timer running to fire a fireOnLayout event...
		if ( null != m_onLayoutEventTimer )
		{
			// ...cancel it while we process this layout request.
			// ...(We'll restart it below.)
			m_onLayoutEventTimer.cancel();
			m_onLayoutEventTimer = null;
		}

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
		ContentControl.setDimensions( m_contentControl, m_contentControlWidth, m_contentControlHeight );
		
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

		// If we have a fireOnLayout event...
		if ( null != fireOnLayout )
		{
			// ...save it.
			m_onLayoutEvent = fireOnLayout;
		}
		
		// If we have a saved fireOnLayout event...
		if ( null != m_onLayoutEvent )
		{
			// ...start a Timer to fire it after a short delay. 
			m_onLayoutEventTimer = new Timer()
			{
				@Override
				public void run()
				{
					GwtTeaming.fireEventAsync( m_onLayoutEvent );
					m_onLayoutEvent = null;
				}
			};
			m_onLayoutEventTimer.schedule( ON_LAYOUT_EVENT_DELAY );
		}
	}// end relayoutPageNow()

	
	/*
	 */
	private void showContentPanel( boolean modalContent )
	{
		if ( !m_contentControl.isVisible() )
		{
			m_contentControl.setVisible( true );
			
			// Show the glass panel
			if ( null != m_glassPanel )
			{
				m_glassPanel.setGlassEnabled( true );
				Element glassElement = m_glassPanel.getGlassElement();
				if ( null != glassElement )
				{
					glassElement.getStyle().setZIndex( GLASS_PANEL_Z_INDEX );
				}
				m_glassPanel.show();
				m_glassPanel.setGlassModal( modalContent );
			}
		}
	}// end showContentPanel()
	
	/*
	 */
	private void showControl( final UIObject target, final VibeEventBase<?> fireOnShow )
	{
		if ( isShowing() )
		{
			if ( null == fireOnShow )
			     showHomePage();
			else GwtTeaming.fireEventAsync( fireOnShow );
		}
		
		else
		{
			// Set the position of the content control.
			GwtClientHelper.deferCommand( new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					relayoutPage();
					
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							showRelativeTo( target );
							showHomePage();
							showTreeControl();
							relayoutPage( fireOnShow );
						}
					} );
				}
			} );
	
			// Issue an ajax request to get the upgrade information from the server.
			GwtClientHelper.deferCommand( new ScheduledCommand()
			{
				@Override
				public void execute()
				{
	            	// Get the upgrade info from the server.  If there are upgrade tasks that
					// need to be performed, AdminInfoDlg will display them.
					showUpgradeTasks();
					updateHomePage();
				}
			} );
		}
	}// end showControl()
	
	/**
	 * 
	 */
	private void showControl( final UIObject target )
	{
		// Always use the initial form of the method.
		showControl( target, null );	// null -> No event needs to be fired when shown.
	}
	
	/*
	 * Asynchronously show the page that gives the user some
	 * information about the administration console.
	 */
	private void showHomePageAsync() {
		GwtClientHelper.deferCommand(
			new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					showHomePage();
				}
			} );
	}
	
	/*
	 * Synchronously show the page that gives the user some information
	 * about the administration console.
	 */
	private void showHomePage()
	{
		if ( m_homePage != null )
		{
			ContentControl.clear( m_contentControl );
			m_contentControl.setVisible( false );
			m_homePage.setVisible( true );
		}
		
		// Hide the glass panel
		if ( m_glassPanel != null )
			m_glassPanel.hide();
	}
	
	/**
	 * 
	 */
	private void showTreeControl()
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
	 * Handles AdministrationActionEvent's received by this class.
	 * 
	 * Implements the AdministrationActionEvent.Handler.onAdministrationAction() method.
	 * 
	 * @param event
	 */
	@Override
	public void onAdministrationAction( AdministrationActionEvent event )
	{
		GwtAdminAction adminAction = event.getAdminAction();
		if (null == adminAction)
		{
			GwtClientHelper.deferredAlert("AdminControl.onAdministrationAction( *Internal Error* ):  There was no GwtAdminAction supplied with the event.");
			return;
		}
		adminActionSelected( adminAction );
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
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				AsyncCallback<VibeRpcResponse> getSiteBrandingCallback;
				GetSiteBrandingCmd cmd;

				// Create the callback that will be used when we issue an ajax call to get the site branding
				getSiteBrandingCallback = new AsyncCallback<VibeRpcResponse>()
				{
					/**
					 * 
					 */
					@Override
					public void onFailure( final Throwable t )
					{
						Scheduler.ScheduledCommand cmd;

						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								String[] patches = null;

								GwtClientHelper.handleGwtRPCFailure(
										t,
										GwtTeaming.getMessages().rpcFailure_GetBranding(),
										patches );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
			
					/**
					 * 
					 * @param result
					 */
					@Override
					public void onSuccess( VibeRpcResponse response )
					{
						final GwtBrandingData siteBrandingData;
						Scheduler.ScheduledCommand cmd;
						
						siteBrandingData = (GwtBrandingData) response.getResponseData();

						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								invokeEditSiteBrandingDlg( siteBrandingData );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				};
				
				// Issue an ajax request to get the site branding data.
				cmd = new GetSiteBrandingCmd();
				GwtClientHelper.executeCommand( cmd, getSiteBrandingCallback );
			}
		} );
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
		final int x;
		final int y;
		
		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		// Have we already created a "Configure Adhoc Folders" dialog?
		if ( m_configureAdhocFoldersDlg == null )
		{
			int width;
			int height;
			
			height = m_dlgHeight;
			width = m_dlgWidth;
			
			// No, create one.
			ConfigureAdhocFoldersDlg.createDlg(
											isActionAutoHide( AdminAction.CONFIGURE_ADHOC_FOLDERS ),
											isActionModal(    AdminAction.CONFIGURE_ADHOC_FOLDERS ),
											new Integer( x ),
											new Integer( y ),
											new Integer( width ),
											new Integer( height ),
											new ConfigureAdhocFoldersDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ConfigureAdhocFoldersDlg cafDlg )
				{
					m_configureAdhocFoldersDlg = cafDlg;
					
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							invokeConfigureAdhocFoldersDlg( x, y );
						}
					} );
				}
			} );
		}
		else
		{
			invokeConfigureAdhocFoldersDlg( x, y );
		}
	}
	
	/**
	 * 
	 */
	private void invokeConfigureAdhocFoldersDlg( int x, int y )
	{
		if ( m_configureAdhocFoldersDlg != null )
		{
			// Run an async cmd to show the dialog.
			ConfigureAdhocFoldersDlg.initAndShow(
											m_configureAdhocFoldersDlg,
											new Integer( x ),
											new Integer( y ),
											new Integer( m_dlgWidth ),
											new Integer( m_dlgHeight ),
											null );
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
				final GwtFileSyncAppConfiguration fileSyncAppConfiguration;
				fileSyncAppConfiguration = (GwtFileSyncAppConfiguration) response.getResponseData();
				
				GwtClientHelper.deferCommand( new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						final int x;
						final int y;

						// Get the position of the content control.
						x = m_contentControlX;
						y = m_contentControlY;
						
						// Have we already created a "Configure File Sync App" dialog?
						if ( m_configureFileSyncAppDlg == null )
						{
							int width;
							int height;
							
							// No, create it.
							height = m_dlgHeight;
							width = m_dlgWidth;

							// Run an async cmd to create the dialog.
							ConfigureFileSyncAppDlg.createDlg(
															isActionAutoHide( AdminAction.CONFIGURE_FILE_SYNC_APP ),
															isActionModal(    AdminAction.CONFIGURE_FILE_SYNC_APP ),
															new Integer( x ),
															new Integer( y ),
															new Integer( width ),
															new Integer( height ),
															new ConfigureFileSyncAppDlgClient()
							{			
								@Override
								public void onUnavailable()
								{
									// Nothing to do.  Error handled in asynchronous provider.
								}
								
								@Override
								public void onSuccess( ConfigureFileSyncAppDlg cfsaDlg )
								{
									m_configureFileSyncAppDlg = cfsaDlg;

									GwtClientHelper.deferCommand( new ScheduledCommand()
									{
										@Override
										public void execute() 
										{
											invokeConfigureFileSyncAppDlg( x, y, fileSyncAppConfiguration );
										}
									} );
								}
							} );
						}
						else
						{
							invokeConfigureFileSyncAppDlg( x, y, fileSyncAppConfiguration );
						}
					}
				} );
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
	 * 
	 */
	private void invokeConfigureFileSyncAppDlg( int x, int y, GwtFileSyncAppConfiguration fileSyncAppConfig )
	{
		// Run an async cmd to show the dialog.
		ConfigureFileSyncAppDlg.initAndShow(
										m_configureFileSyncAppDlg,
										fileSyncAppConfig,
										new Integer( x ),
										new Integer( y ),
										new Integer( m_dlgWidth ),
										new Integer( m_dlgHeight ),
										null );
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
											isActionAutoHide( AdminAction.CONFIGURE_MOBILE_APPS ),
											isActionModal(    AdminAction.CONFIGURE_MOBILE_APPS ),
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
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_configureMobileAppsDlg = cmaDlg;
							
							m_configureMobileAppsDlg.init();
							m_configureMobileAppsDlg.show();
						}
					} );
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
	 * Handles InvokeConfigurePasswordPolicyDlgEvent received by this
	 * class.
	 * 
	 * Implements the InvokeConfigurePasswordPolicyDlgEvent.Handler.onInvokeConfigurePasswordPolicyDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeConfigurePasswordPolicyDlg( InvokeConfigurePasswordPolicyDlgEvent event )
	{
		// Get the position of the content control.
		int x = m_contentControlX;
		int y = m_contentControlY;
		
		// Have we already created a "Configure Password Policy" dialog?
		if ( null == m_configurePasswordPolicyDlg )
		{
			int width;
			int height;
			
			// No, create one.
			height = m_dlgHeight;
			width = m_dlgWidth;
			ConfigurePasswordPolicyDlg.createAsync(
											isActionAutoHide( AdminAction.CONFIGURE_PASSWORD_POLICY ),
											isActionModal(    AdminAction.CONFIGURE_PASSWORD_POLICY ),
											x, 
											y,
											width,
											height,
											new ConfigurePasswordPolicyDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ConfigurePasswordPolicyDlg cppDlg )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_configurePasswordPolicyDlg = cppDlg;
							
							m_configurePasswordPolicyDlg.init();
							m_configurePasswordPolicyDlg.show();
						}
					} );
				}
			} );
		}
		else
		{
			m_configurePasswordPolicyDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			m_configurePasswordPolicyDlg.init();
			m_configurePasswordPolicyDlg.setPopupPosition( x, y );
			m_configurePasswordPolicyDlg.show();
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
			EditZoneShareSettingsDlg.createDlg(
											isActionAutoHide( AdminAction.CONFIGURE_SHARE_SETTINGS ),
											isActionModal(    AdminAction.CONFIGURE_SHARE_SETTINGS ),
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
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_editZoneShareSettingsDlg = ezsrDlg;
							
							EditZoneShareSettingsDlg.initAndShow( m_editZoneShareSettingsDlg );
						}
					} );
				}
			} );
		}
		else
		{
			m_editZoneShareSettingsDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			m_editZoneShareSettingsDlg.setPopupPosition( x, y );
			EditZoneShareSettingsDlg.initAndShow( m_editZoneShareSettingsDlg );
		}
	}
	

	/**
	 * Handles InvokeConfigureUpdateLogsDlgEvent received by this
	 * class.
	 * 
	 * Implements the InvokeConfigureUpdateLogsDlgEvent.Handler.onInvokeConfigureUpdateLogsDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeConfigureUpdateLogsDlg( InvokeConfigureUpdateLogsDlgEvent event )
	{
		// Get the position of the content control.
		int x = m_contentControlX;
		int y = m_contentControlY;
		
		// Have we already created a "Configure Update Logs" dialog?
		if ( null == m_configureUpdateLogsDlg )
		{
			int width;
			int height;
			
			// No, create one.
			height = m_dlgHeight;
			width = m_dlgWidth;
			ConfigureUpdateLogsDlg.createAsync(
											isActionAutoHide( AdminAction.CONFIGURE_FOLDER_UPDATE_LOGS ),
											isActionModal(    AdminAction.CONFIGURE_FOLDER_UPDATE_LOGS ),
											x, 
											y,
											width,
											height,
											new ConfigureUpdateLogsDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ConfigureUpdateLogsDlg culDlg )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_configureUpdateLogsDlg = culDlg;
							
							m_configureUpdateLogsDlg.init();
							m_configureUpdateLogsDlg.show();
						}
					} );
				}
			} );
		}
		else
		{
			m_configureUpdateLogsDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			m_configureUpdateLogsDlg.init();
			m_configureUpdateLogsDlg.setPopupPosition( x, y );
			m_configureUpdateLogsDlg.show();
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
											isActionAutoHide( AdminAction.CONFIGURE_USER_ACCESS ),
											isActionModal(    AdminAction.CONFIGURE_USER_ACCESS ),
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
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_configureUserAccessDlg = cuaDlg;
							
							m_configureUserAccessDlg.init();
							m_configureUserAccessDlg.show();
						}
					} );
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
	 * Handles InvokeEditKeyShieldConfigDlgEvent received by this class.
	 * 
	 * Implements the InvokeEditKeyShieldConfigDlgEvent.Handler.onInvokeEditKeyShieldConfigDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeEditKeyShieldConfigDlg( InvokeEditKeyShieldConfigDlgEvent event )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				invokeEditKeyShieldConfigDlg();
			}
		} );
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
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				invokeEditLdapConfigDlg();
			}
		} );
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
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				invokeEditNetFolderDlgById( event.getNetFolderId(), event.getShowRelativeTo() );
			}
		} );
	}
	
	/**
	 * Handles InvokeLimitUserVisibilityDlgEvent received by this
	 * class.
	 * 
	 * Implements the InvokeLimitUserVisibilityDlgEvent.Handler.onInvokeLimitUserVisibilityDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeLimitUserVisibilityDlg(InvokeLimitUserVisibilityDlgEvent event) {
		// Get the position of the content control.
		final int x = m_contentControlX;
		final int y = m_contentControlY;
		
		// Have we already created a 'Limit User Visibility' dialog?
		if (null == m_limitUserVisibilityDlg) {
			// No!  Create one now.
			LimitUserVisibilityDlg.createAsync(new LimitUserVisibilityDlgClient() {			
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
				
				@Override
				public void onSuccess(final LimitUserVisibilityDlg luvDlg) {
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							m_limitUserVisibilityDlg = luvDlg;
							LimitUserVisibilityDlg.initAndShow(m_limitUserVisibilityDlg, x, y, m_dlgWidth, m_dlgHeight);
						}
					} );
				}
			},
			isActionAutoHide(AdminAction.MANAGE_USER_VISIBILITY),
			isActionModal(   AdminAction.MANAGE_USER_VISIBILITY),
			x, 
			y,
			m_dlgWidth,
			m_dlgHeight );
		}
		
		else
		{
			// Yes, we've already created a 'Limit User Visibility'
			// dialog!  Simply initialize and show it.
			m_limitUserVisibilityDlg.setPixelSize(m_dlgWidth, m_dlgHeight);
			LimitUserVisibilityDlg.initAndShow(m_limitUserVisibilityDlg, x, y, m_dlgWidth, m_dlgHeight);
		}
	}
	
	/**
	 * Handles the InvokeNetFolderGlobalSettingsDlgEvent received by this class.
	 * 
	 * Implements the InvokeNetFolderGlobalSettingsDlgEvent.Handler.onInvokeNetFolderGlobalSettingsDlg() method.
	 *  
	 */
	@Override
	public void onInvokeNetFolderGlobalSettingsDlg( InvokeNetFolderGlobalSettingsDlgEvent event )
	{
		int x;
		int y;

		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		// Have we already created a net folder global settings dialog?
		if ( m_nfGlobalSettingsDlg == null )
		{
			int width;
			int height;
			
			// No, create one.
			height = m_dlgHeight;
			width = m_dlgWidth;
			NetFolderGlobalSettingsDlg.createAsync(
										isActionAutoHide( AdminAction.NET_FOLDER_GLOBAL_SETTINGS ),
										isActionModal(    AdminAction.NET_FOLDER_GLOBAL_SETTINGS ),
										x, 
										y,
										width,
										height,
										new NetFolderGlobalSettingsDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final NetFolderGlobalSettingsDlg jzcDlg )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_nfGlobalSettingsDlg = jzcDlg;
							
							m_nfGlobalSettingsDlg.init();
							m_nfGlobalSettingsDlg.show();
						}
					} );
				}
			} );
		}
		else
		{
			m_nfGlobalSettingsDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			m_nfGlobalSettingsDlg.init();
			m_nfGlobalSettingsDlg.setPopupPosition( x, y );
			m_nfGlobalSettingsDlg.show();
		}
	}
	
	/**
	 * Handles InvokeManageAdministratorsDlgEvent received by this class.
	 * 
	 * Implements the InvokeManageAdministratorsDlgEvent.Handler.onInvokeManageAdministratorsDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeManageAdministratorsDlg( final InvokeManageAdministratorsDlgEvent event )
	{
		// Get the position of the content control.
		final int x = m_contentControlX;
		final int y = m_contentControlY;
		
		// Have we already created a "Manage Administrators" dialog?
		if ( m_manageAdministratorsDlg == null )
		{
			// No, create one.
			ManageAdministratorsDlg.createAsync( new ManageAdministratorsDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ManageAdministratorsDlg muDlg )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_manageAdministratorsDlg = muDlg;
							ManageAdministratorsDlg.initAndShow( m_manageAdministratorsDlg, x, y, m_dlgWidth, m_dlgHeight );
						}
					} );
				}
			},
			isActionAutoHide( AdminAction.MANAGE_ADMINISTRATORS ),
			isActionModal(    AdminAction.MANAGE_ADMINISTRATORS ),
			x, 
			y,
			m_dlgWidth,
			m_dlgHeight );
		}
		
		else
		{
			// Yes, we've already created a "Manage Administrators" dialog!
			// Simply initialize and show it.
			m_manageAdministratorsDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			ManageAdministratorsDlg.initAndShow( m_manageAdministratorsDlg, x, y, m_dlgWidth, m_dlgHeight );
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
							isActionAutoHide( AdminAction.MANAGE_DATABASE_PRUNE ),
							isActionModal(    AdminAction.MANAGE_DATABASE_PRUNE ),
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
							GwtClientHelper.deferCommand( new ScheduledCommand()
							{
								@Override
								public void execute() 
								{
									m_manageDatabasePruneDlg = cfsaDlg;
									
									m_manageDatabasePruneDlg.init( databasePruneConfiguration );
									m_manageDatabasePruneDlg.show();
								}
							} );
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
											isActionAutoHide( AdminAction.MANAGE_NET_FOLDERS ),
											isActionModal(    AdminAction.MANAGE_NET_FOLDERS ),
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
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_manageNetFoldersDlg = mnfDlg;
							
							m_manageNetFoldersDlg.init();
							m_manageNetFoldersDlg.show();
						}
					} );
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
											isActionAutoHide( AdminAction.MANAGE_RESOURCE_DRIVERS ),
											isActionModal(    AdminAction.MANAGE_RESOURCE_DRIVERS ),
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
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_manageNetFolderRootsDlg = mfsrDlg;
							
							m_manageNetFolderRootsDlg.init();
							m_manageNetFolderRootsDlg.show();
						}
					} );
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
									isActionAutoHide( AdminAction.MANAGE_GROUPS ),
									isActionModal(    AdminAction.MANAGE_GROUPS ),
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
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_manageGroupsDlg = mgDlg;
							
							m_manageGroupsDlg.init();
							m_manageGroupsDlg.show();
						}
					} );
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
	 * Handles InvokeManageMobileDevicesDlgEvent received by this class.
	 * 
	 * Implements the InvokeManageMobileDevicesDlgEvent.Handler.onInvokeManageMobileDevicesDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeManageMobileDevicesDlg( InvokeManageMobileDevicesDlgEvent event )
	{
		// Get the position of the content control.
		final boolean autoHide = isActionAutoHide( AdminAction.MANAGE_MOBILE_DEVICES );
		final boolean modal    = isActionModal(    AdminAction.MANAGE_MOBILE_DEVICES );
		final int     x        = m_contentControlX;
		final int     y        = m_contentControlY;
		
		// Have we already created a "Manage Mobile Devices" dialog?
		if ( m_manageMobileDevicesDlg == null )
		{
			// No, create one.
			ManageMobileDevicesDlg.createAsync( new ManageMobileDevicesDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ManageMobileDevicesDlg mmdDlg )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_manageMobileDevicesDlg = mmdDlg;
							ManageMobileDevicesDlg.initAndShow( m_manageMobileDevicesDlg, new MobileDevicesInfo(), autoHide, modal, x, y, m_dlgWidth, m_dlgHeight );
						}
					} );
				}
			},
			isActionAutoHide( AdminAction.MANAGE_MOBILE_DEVICES ),
			isActionModal(    AdminAction.MANAGE_MOBILE_DEVICES ),
			x, 
			y,
			m_dlgWidth,
			m_dlgHeight );
		}
		
		else
		{
			// Yes, we've already created a "Manage Mobile Devices" dialog!
			// Simply initialize and show it.
			m_manageMobileDevicesDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			ManageMobileDevicesDlg.initAndShow( m_manageMobileDevicesDlg, new MobileDevicesInfo(), autoHide, modal, x, y, m_dlgWidth, m_dlgHeight );
		}
	}
	
	/**
	 * Handles InvokeManageProxyIdentitiesDlgEvent received by this class.
	 * 
	 * Implements the InvokeManageProxyIdentitiesDlgEvent.Handler.onInvokeManageProxyIdentitiesDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeManageProxyIdentitiesDlg(InvokeManageProxyIdentitiesDlgEvent event) {
		// Get the position of the content control.
		final boolean autoHide = isActionAutoHide(AdminAction.MANAGE_PROXY_IDENTITIES);
		final boolean modal    = isActionModal(   AdminAction.MANAGE_PROXY_IDENTITIES);
		final int     x        = m_contentControlX;
		final int     y        = m_contentControlY;
		
		// Have we already created a 'Manage Proxy Identities' dialog?
		if (null == m_manageProxyIdentitiesDlg) {
			// No!  Create one now.
			ManageProxyIdentitiesDlg.createAsync(new ManageProxyIdentitiesDlgClient() {			
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
				
				@Override
				public void onSuccess(final ManageProxyIdentitiesDlg mpiDlg) {
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							m_manageProxyIdentitiesDlg = mpiDlg;
							ManageProxyIdentitiesDlg.initAndShow(m_manageProxyIdentitiesDlg, autoHide, modal, x, y, m_dlgWidth, m_dlgHeight);
						}
					} );
				}
			},
			autoHide,
			modal,
			x, 
			y,
			m_dlgWidth,
			m_dlgHeight );
		}
		
		else {
			// Yes, we've already created a 'Manage Proxy Identities'
			// dialog!  Simply initialize and show it.
			m_manageProxyIdentitiesDlg.setPixelSize(m_dlgWidth, m_dlgHeight);
			ManageProxyIdentitiesDlg.initAndShow(m_manageProxyIdentitiesDlg, autoHide, modal, x, y, m_dlgWidth, m_dlgHeight);
		}
	}
	
	/**
	 * Handles InvokeManageTeamsDlgEvent received by this class.
	 * 
	 * Implements the InvokeManageTeamsDlgEvent.Handler.onInvokeManageTeamsDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeManageTeamsDlg( final InvokeManageTeamsDlgEvent event )
	{
		// Get the position of the content control.
		final int x = m_contentControlX;
		final int y = m_contentControlY;
		
		// Have we already created a "Manage Teams" dialog?
		if ( m_manageTeamsDlg == null )
		{
			// No, create one.
			ManageTeamsDlg.createAsync( new ManageTeamsDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ManageTeamsDlg muDlg )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_manageTeamsDlg = muDlg;
							ManageTeamsDlg.initAndShow( m_manageTeamsDlg, event.isTrashView(), x, y, m_dlgWidth, m_dlgHeight );
						}
					} );
				}
			},
			isActionAutoHide( AdminAction.MANAGE_TEAMS ),
			isActionModal(    AdminAction.MANAGE_TEAMS ),
			x, 
			y,
			m_dlgWidth,
			m_dlgHeight );
		}
		
		else
		{
			// Yes, we've already created a "Manage Teams" dialog!
			// Simply initialize and show it.
			m_manageTeamsDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			ManageTeamsDlg.initAndShow( m_manageTeamsDlg, event.isTrashView(), x, y, m_dlgWidth, m_dlgHeight );
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
	public void onInvokeManageUsersDlg( final InvokeManageUsersDlgEvent event )
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
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_manageUsersDlg = muDlg;
							ManageUsersDlg.initAndShow( m_manageUsersDlg, event.isTrashView(), x, y, m_dlgWidth, m_dlgHeight );
						}
					} );
				}
			},
			isActionAutoHide( AdminAction.ADD_USER ),
			isActionModal(    AdminAction.ADD_USER ),
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
			ManageUsersDlg.initAndShow( m_manageUsersDlg, event.isTrashView(), x, y, m_dlgWidth, m_dlgHeight );
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
	 * Handles InvokeNameCompletionSettingsDlgEvent received by this class.
	 * 
	 * Implements the InvokeNameCompletionSettingsDlgEvent.Handler.onInvokeNameCompletionSettingsDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeNameCompletionSettingsDlg( InvokeNameCompletionSettingsDlgEvent event )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				invokeNameCompletionSettingsDlg();
			}
		} );
	}
	

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
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_runAReportDlg = muDlg;
							RunAReportDlg.initAndShow( m_runAReportDlg, x, y );
						}
					} );
				}
			},
			isActionAutoHide( AdminAction.RUN_A_REPORT ),
			isActionModal(    AdminAction.RUN_A_REPORT ),
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
	 * Handles the InvokePrincipalDesktopSettingsDlgEvent
	 */
	@Override
	public void onInvokePrincipalDesktopSettingsDlg( final InvokePrincipalDesktopSettingsDlgEvent event )
	{
		int x;
		int y;
		final List<Long> principalIds;

		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		principalIds = event.getPrincipalIds();
		
		// Have we already created a "Configure User Desktop App" dialog?
		if ( m_configureUserFileSyncAppDlg == null )
		{
			int width;
			int height;
			
			// No, create one.
			height = m_dlgHeight;
			width = m_dlgWidth;
			ConfigureUserFileSyncAppDlg.createAsync(
											false,
											true, 
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
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_configureUserFileSyncAppDlg = cufsaDlg;
							
							m_configureUserFileSyncAppDlg.init( principalIds, event.getPrincipalsAreUsers() );
							m_configureUserFileSyncAppDlg.show();
						}
					} );
				}
			} );
		}
		else
		{
			m_configureUserFileSyncAppDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			m_configureUserFileSyncAppDlg.init( principalIds, event.getPrincipalsAreUsers() );
			m_configureUserFileSyncAppDlg.setPopupPosition( x, y );
			m_configureUserFileSyncAppDlg.show();
		}
	}
	
	/**
	 * Handles the InvokePrincipalMobileSettingsDlgEvent
	 */
	@Override
	public void onInvokePrincipalMobileSettingsDlg( final InvokePrincipalMobileSettingsDlgEvent event )
	{
		int x;
		int y;
		final List<Long> principalIds;

		// Get the position of the content control.
		x = m_contentControlX;
		y = m_contentControlY;
		
		principalIds = event.getPrincipalIds();
		
		// Have we already created a "Configure User Mobile Apps" dialog?
		if ( m_configureUserMobileAppsDlg == null )
		{
			int width;
			int height;
			
			// No, create one.
			height = m_dlgHeight;
			width = m_dlgWidth;
			ConfigureUserMobileAppsDlg.createAsync(
											false,
											true, 
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
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_configureUserMobileAppsDlg = cumaDlg;
							
							m_configureUserMobileAppsDlg.init( principalIds, event.getPrincipalsAreUsers() );
							m_configureUserMobileAppsDlg.show();
						}
					} );
				}
			} );
		}
		else
		{
			m_configureUserMobileAppsDlg.setPixelSize( m_dlgWidth, m_dlgHeight );
			m_configureUserMobileAppsDlg.init( principalIds, event.getPrincipalsAreUsers() );
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
		// Invoke the Manage Shares dialog
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute() 
			{
				invokeManageSharesDlg();
			}
		} );
	}
	
	/**
	 * Handles ManageUserVisibilityEvent's received by this class.
	 * 
	 * Implements the ManageUserVisibilityEvent.Handler.onManageUserVisibility() method.
	 * 
	 * @param event
	 */
	@Override
	public void onManageUserVisibility(ManageUserVisibilityEvent event) {
		// Fire the event to invoke the 'Limit User Visibility' dialog.
		InvokeLimitUserVisibilityDlgEvent.fireOneAsync();
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
				
				upgradeInfo = (GwtUpgradeInfo) response.getResponseData();
				
				GwtClientHelper.deferCommand( new ScheduledCommand()
				{
					@Override
					public void execute() 
					{
						// Update the admin console home page with the latest info.
						if ( m_homePage != null )
							m_homePage.init( upgradeInfo );
					}
				} );
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
	public static void createDlg( final GwtMainPage mainPage, final AdminControlClient adminCtrlClient )
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

	/**
	 * Put hideControl() behind a split point
	 */
	public static void hideControl( final AdminControl adminControl )
	{
		GWT.runAsync( AdminControl.class, new RunAsyncCallback()
		{
			@Override
			public void onSuccess()
			{
				if ( adminControl != null )
					adminControl.hideControl();
			}
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_AdminControl() );
			}
		});
	}

	/**
	 * Put relayoutPage() behind a split point.
	 */
	public static void relayoutPage( final AdminControl adminControl )
	{
		GWT.runAsync( AdminControl.class, new RunAsyncCallback()
		{
			@Override
			public void onSuccess()
			{
				if ( adminControl != null )
					adminControl.relayoutPage();
			}
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_AdminControl() );
			}
		});
	}

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
				GwtClientHelper.deferCommand( new ScheduledCommand() {
					@Override
					public void execute()
					{
						showAdminInfoDlgImpl( adminInfoDlg, upgradeInfo );
					}// end execute()
				} );
			}// onSuccess()
		} );
	}// end showAdminInfoDlg()

	/**
	 * 
	 */
	private static void showAdminInfoDlgImpl( final AdminInfoDlg adminInfoDlg, final GwtUpgradeInfo upgradeInfo )
	{
		AdminInfoDlg.initAndShow( adminInfoDlg, upgradeInfo );
	}
	
	/**
	 * Put showControl() behind a split point.
	 */
	public static void showControl(
		final AdminControl adminControl,
		final UIObject target,
		final VibeEventBase<?> fireOnShow ) 
	{
		GWT.runAsync( AdminControl.class, new RunAsyncCallback()
		{
			@Override
			public void onSuccess()
			{
				if ( adminControl != null )
					adminControl.showControl( target, fireOnShow );
			}
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_AdminControl() );
			}
		});
	}

	/**
	 * Put showControl() behind a split point.
	 */
	public static void showControl(
		final AdminControl adminControl,
		final UIObject target )
	{
		GWT.runAsync( AdminControl.class, new RunAsyncCallback()
		{
			@Override
			public void onSuccess()
			{
				if ( adminControl != null )
					adminControl.showControl( target );
			}
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_AdminControl() );
			}
		});
	}

	/**
	 * Put showHomePage() behind a split point.
	 */
	public static void showHomePage( final AdminControl adminControl )
	{
		GWT.runAsync( AdminControl.class, new RunAsyncCallback()
		{
			@Override
			public void onSuccess()
			{
				if ( adminControl != null )
					adminControl.showHomePage();
			}
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_AdminControl() );
			}
		});
	}

	/**
	 * Issue an ajax request to get information about the upgrade tasks that need to be performed.
	 * If there are upgrade tasks that need to be performed show the list of tasks.
	 */
	public static void showUpgradeTasks()
	{
		GWT.runAsync( AdminControl.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_AdminControl() );
			}
			
			@Override
			public void onSuccess()
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
						GwtClientHelper.deferCommand( new ScheduledCommand()
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
						} );
					}
				};

				// When we get the upgrade info from the server our callback will check to
		    	// see if upgrade tasks exists.  If they do, the callback will invoke the
		    	// AdminInfoDlg which will show the user the tasks they need to do.
				getUpgradeInfoFromServer( rpcGetUpgradeInfoCallback );
			}
		} );
	}

	/*
	 * Returns true if the AdminAction should be in an auto hide dialog
	 * and false otherwise.
	 */
	private boolean isActionAutoHide(AdminAction aa) {
		return (!(isActionModal(aa)));
	}
	
	/*
	 * Returns true if the AdminAction should be in a modal dialog and
	 * false otherwise.
	 */
	private boolean isActionModal(AdminAction aa) {
		populateModalActionsMap();
		Boolean modal = m_modalActionMap.get(aa);
		return ((null == modal) || modal);
	}
	
	/*
	 * Populates the modal actions map.
	 */
	private void populateModalActionsMap() {
		// If we haven't populated the modal actions map yet...
		if (null == m_modalActionMap) {
			// ...populate it now.
			m_modalActionMap = new HashMap<AdminAction, Boolean>();
			for (AdminAction aa:  AdminAction.values()) {
				switch (aa) {
				// Non modal (i.e., simple Close button) actions.
				case ADD_USER:
				case CONFIGURE_ROLE_DEFINITIONS:
				case CONFIGURE_SEARCH_INDEX:
				case FORM_VIEW_DESIGNER:
				case MANAGE_ADMINISTRATORS:
				case MANAGE_APPLICATIONS:
				case MANAGE_APPLICATION_GROUPS:
				case MANAGE_EXTENSIONS:
				case MANAGE_GROUPS:
				case MANAGE_LICENSE:
				case MANAGE_MOBILE_DEVICES:
				case MANAGE_NET_FOLDERS:
				case MANAGE_PROXY_IDENTITIES:
				case MANAGE_RESOURCE_DRIVERS:
				case MANAGE_TEAMS:
				case MANAGE_USER_VISIBILITY:
				case MANAGE_WORKSPACE_AND_FOLDER_TEMPLATES:
				case MANAGE_ZONES:
				case RUN_A_REPORT:
					// These are modeless unless all dialogs are being
					// forced to be modal.
					m_modalActionMap.put(aa, new Boolean(m_acDlgMode.isModal()));
					break;
	
				default:
					GwtClientHelper.debugAlert("AdminControl.m_modalActionMap():  Mapping missing for " + aa.name());
					
					// * * * * * * * * * * * * * * * * * * * * * * * //
					// Fall through and treat it as a modal action.  //
					// * * * * * * * * * * * * * * * * * * * * * * * //
					
				// Modal (i.e., Apply/OK/Cancel button) actions.
				case ACCESS_CONTROL_FOR_ZONE_ADMIN_FUNCTIONS:
				case CONFIGURE_ADHOC_FOLDERS:
				case CONFIGURE_EMAIL:
				case CONFIGURE_FILE_SYNC_APP:
				case CONFIGURE_FILE_VERSION_AGING:
				case CONFIGURE_FOLDER_INDEX:
				case CONFIGURE_FOLDER_SEARCH_NODES:
				case CONFIGURE_FOLDER_UPDATE_LOGS:
				case CONFIGURE_HOME_PAGE:
				case CONFIGURE_MOBILE_ACCESS:
				case CONFIGURE_MOBILE_APPS:
				case CONFIGURE_NAME_COMPLETION:
				case CONFIGURE_PASSWORD_POLICY:
				case CONFIGURE_SCHEDULE:
				case CONFIGURE_SHARE_SETTINGS:
				case CONFIGURE_USER_ACCESS:
				case IMPORT_PROFILES:
				case KEYSHIELD_CONFIG:
				case LDAP_CONFIG:
				case NET_FOLDER_GLOBAL_SETTINGS:
				case MANAGE_DATABASE_PRUNE:
				case MANAGE_QUOTAS:
				case MANAGE_FILE_UPLOAD_LIMITS:
				case MANAGE_SHARE_ITEMS:
				case MANAGE_USER_ACCOUNTS:
				case REPORT_ACTIVITY_BY_USER:
				case REPORT_CREDITS:
				case REPORT_DATA_QUOTA_EXCEEDED:
				case REPORT_DATA_QUOTA_HIGHWATER_EXCEEDED:
				case REPORT_DISK_USAGE:
				case REPORT_EMAIL:
				case REPORT_LICENSE:
				case REPORT_LOGIN:
				case REPORT_USER_ACCESS:
				case REPORT_XSS:
				case REPORT_VIEW_CHANGELOG:
				case REPORT_VIEW_CREDITS:
				case REPORT_VIEW_SYSTEM_ERROR_LOG:
				case SITE_BRANDING:
				case UNDEFINED:
					// These are modal unless all dialogs are being
					// forced to be modeless.
					m_modalActionMap.put(aa, (!(m_acDlgMode.isModeless())));
					break;
				}
			}
		}
	}
}
