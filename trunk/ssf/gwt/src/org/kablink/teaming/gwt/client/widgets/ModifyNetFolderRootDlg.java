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
import java.util.HashSet;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtADLdapObject;
import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtPrincipal;
import org.kablink.teaming.gwt.client.GwtProxyIdentity;
import org.kablink.teaming.gwt.client.GwtSchedule;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.NetFolderRoot;
import org.kablink.teaming.gwt.client.NetFolderRoot.GwtAuthenticationType;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.NetFolderRootCreatedEvent;
import org.kablink.teaming.gwt.client.event.NetFolderRootModifiedEvent;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapObject;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapServer.DirectoryType;
import org.kablink.teaming.gwt.client.rpc.shared.CreateNetFolderRootCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetLdapObjectFromADCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ModifyNetFolderRootCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SyncNetFolderServerCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TestNetFolderConnectionCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TestNetFolderConnectionResponse;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.FindCtrl;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;
import org.kablink.teaming.gwt.client.widgets.LdapBrowserDlg.LdapBrowseListCallback;
import org.kablink.teaming.gwt.client.widgets.LdapBrowserDlg.LdapBrowserDlgClient;
import org.kablink.teaming.gwt.client.widgets.SelectPrincipalsWidget.SelectPrincipalsWidgetClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This dialog can be used to add a Net Folder Root or modify a Net
 * Folder Root.
 * 
 * @author drfoster@novell.com
 */
public class ModifyNetFolderRootDlg extends DlgBox
	implements
		EditCanceledHandler,
		EditSuccessfulHandler,
		SearchFindResultsEvent.Handler
{
	private Button						m_browseProxyDnBtn;					// LDAP browse button next to m_proxyNameTxtBox.
	private CheckBox					m_allowDesktopAppToTriggerSyncCB;	//
	private CheckBox					m_allowSelfSignedCertsCkbox;		//
	private CheckBox					m_fullSyncDirOnlyCB;				//
	private CheckBox					m_indexContentCB;					//
	private CheckBox					m_isSharePointServerCkbox;			//
	private CheckBox					m_jitsEnabledCkbox;					//
	private FindCtrl					m_proxyIdentityFindControl;			//
	private FlowPanel					m_inProgressPanel;					//
	private FlowPanel					m_serverPathHintPanel;				//
	private FlowPanel					m_webDavSpacerPanel;				//
	private GwtProxyIdentity			m_proxyIdentity;					//
	private GwtTeamingImageBundle		m_images;							//
	private GwtTeamingMessages			m_messages;							//
	private InlineLabel					m_authTypeLabel;					//
	private InlineLabel					m_hostUrlLabel;						//
	private Label						m_oesProxyNameHint;					//
	private Label						m_oesProxyNameHint1;				//
	private Label						m_windowsProxyNameHint;				//
	private LdapBrowserDlg				m_ldapBrowserDlg;					//
	private ListBox						m_authTypeListbox;					//
	private ListBox						m_rootTypeListbox;					//
	private List<HandlerRegistration>	m_mnfrDlg_registeredEventHandlers;	//
	private List<LdapBrowseSpec>		m_ldapServerList;					// List of LDAP servers obtained the first time m_browseProxyDnBtn is clicked.
	private NetFolderRoot				m_netFolderRoot;					// If we are modifying a net folder this is the net folder.
	private PasswordTextBox				m_proxyPwdTxtBox;					//
	private RadioButton					m_proxyTypeIdentityRB;				//
	private RadioButton					m_proxyTypeManualRB;				//
	private SelectPrincipalsWidget		m_selectPrincipalsWidget;			//
	private ScheduleWidget				m_scheduleWidget;					//
	private TabPanel					m_tabPanel;							//
	private TextBox						m_hostUrlTxtBox;					//
	private TextBox						m_jitsResultsMaxAge;				//
	private TextBox						m_jitsAclMaxAge;					//
	private TextBox						m_nameTxtBox;						//
	private TextBox						m_proxyNameTxtBox;					//
	private TextBox						m_rootPathTxtBox;					//
	
	private static final boolean SHOW_PRIVILEGED_USERS_UI		= false;	//
	private static final boolean SHOW_NET_FOLDER_SERVER_TYPE	= true;		//
	private static final boolean SHOW_WEBDAV_CONTROLS			= false;	//

	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static final TeamingEvents[] mnfrDlg_REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.SEARCH_FIND_RESULTS,
	};
	
	/**
	 * Enumeration to specify the type of a Net Folder Root. 
	 */
	public enum NetFolderRootType implements IsSerializable {
		WINDOWS,
		CLOUD_FOLDERS,
		FAMT,
		FILE_SYSTEM,
		NETWARE,
		OES,
		OES2015,
		SHARE_POINT_2010,
		SHARE_POINT_2013,
		WEB_DAV,
		UNKNOWN;

		/**
		 * Parses a string representation of an NetFolderRootType.
		 * 
		 * @param type
		 * 
		 * @return
		 */
		public static NetFolderRootType getType(String type) {
			if (type == null)                                                     return NetFolderRootType.FAMT;
			if (type.equalsIgnoreCase(NetFolderRootType.WINDOWS.name()))          return NetFolderRootType.WINDOWS;
			if (type.equalsIgnoreCase(NetFolderRootType.CLOUD_FOLDERS.name()))    return NetFolderRootType.CLOUD_FOLDERS;
			if (type.equalsIgnoreCase(NetFolderRootType.FAMT.name()))             return NetFolderRootType.FAMT;
			if (type.equalsIgnoreCase(NetFolderRootType.FILE_SYSTEM.name()))      return NetFolderRootType.FILE_SYSTEM;
			if (type.equalsIgnoreCase(NetFolderRootType.NETWARE.name()))          return NetFolderRootType.NETWARE;
			if (type.equalsIgnoreCase(NetFolderRootType.OES.name()))              return NetFolderRootType.OES;
			if (type.equalsIgnoreCase(NetFolderRootType.OES2015.name()))          return NetFolderRootType.OES2015;
			if (type.equalsIgnoreCase(NetFolderRootType.SHARE_POINT_2010.name())) return NetFolderRootType.SHARE_POINT_2010;
			if (type.equalsIgnoreCase(NetFolderRootType.SHARE_POINT_2013.name())) return NetFolderRootType.SHARE_POINT_2013;
			if (type.equalsIgnoreCase(NetFolderRootType.WEB_DAV.name()))          return NetFolderRootType.WEB_DAV;
			return NetFolderRootType.UNKNOWN;
		}
	}
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ModifyNetFolderRootDlg(boolean autoHide, boolean modal, int xPos, int yPos, ModifyNetFolderRootDlgClient mnfrDlgClient) {
		// Initialize the super class...
		super(autoHide, modal, xPos, yPos);

		// ...initialize everything else that requires it....
		m_images   = GwtTeaming.getImageBundle();
		m_messages = GwtTeaming.getMessages();

		// ...and being asynchronously loading the dialog. 
		loadPart1Async(mnfrDlgClient);
	}
	
	/*
	 * Changes the state of the proxy type radio buttons to identity.
	 */
	private void checkProxyTypeIdentity() {
		m_proxyTypeManualRB.setValue(  false);
		m_proxyTypeIdentityRB.setValue(true );
	}

	/*
	 * Changes the state of the proxy type radio buttons to manual.
	 */
	private void checkProxyTypeManual() {
		m_proxyTypeIdentityRB.setValue(false);
		m_proxyTypeManualRB.setValue(  true );
	}

	/*
	 * Asynchronously loads the next part of the dialog.
	 */
	private void loadPart1Async(final ModifyNetFolderRootDlgClient mnfrDlgClient) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now(mnfrDlgClient);
			}
		});
	}
	
	/*
	 * Synchronously loads the next asynchronous part of the dialog.
	 */
	private void loadPart1Now(final ModifyNetFolderRootDlgClient mnfrDlgClient) {
		FindCtrl.createAsync(this, SearchType.PROXY_IDENTITY, new FindCtrlClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(FindCtrl findCtrl) {
				// Store and style the FindCtrl...
				m_proxyIdentityFindControl = findCtrl;
				m_proxyIdentityFindControl.addStyleName("modifyNetFolderServerDlg_FindProxyIdentity");
				m_proxyIdentityFindControl.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						GwtClientHelper.deferCommand(new ScheduledCommand() {
							@Override
							public void execute() {
								// The FindCtrl doesn't have a search
								// string...
								String s = m_proxyIdentityFindControl.getText();
								if (null != s) {
									s = s.trim();
								}
								if (!(GwtClientHelper.hasString(s))) {
									// ...it can't be referencing a
									// ...proxy identity.
									m_proxyIdentity = null;
								}
							}
						});
					}
				});
				
				// ...and continue loading the dialog.
				loadPart2Async(mnfrDlgClient);
			}
		});
	}
	
	/*
	 * Asynchronously loads the next asynchronous part of the dialog.
	 */
	private void loadPart2Async(final ModifyNetFolderRootDlgClient mnfrDlgClient) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now(mnfrDlgClient);
			}
		});
	}
	
	/*
	 * Synchronously loads the next asynchronous part of the dialog.
	 */
	private void loadPart2Now(final ModifyNetFolderRootDlgClient mnfrDlgClient) {
		// Create a widget that lets the user select users and groups.
		SelectPrincipalsWidget.createAsync(new SelectPrincipalsWidgetClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(SelectPrincipalsWidget widget) {
				// Store the SelectPrincipalsWidget...
				m_selectPrincipalsWidget = widget;
				
				// ...and continue loading the dialog.
				loadPart3Async(mnfrDlgClient);
			}
		});
	}
	
	/*
	 * Asynchronously loads the next asynchronous part of the dialog.
	 */
	private void loadPart3Async(final ModifyNetFolderRootDlgClient mnfrDlgClient) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart3Now(mnfrDlgClient);
			}
		});
	}
	
	/*
	 * Synchronously loads the next asynchronous part of the dialog.
	 */
	private void loadPart3Now(final ModifyNetFolderRootDlgClient mnfrDlgClient) {
		// Create the header, content and footer of this dialog box.
		createAllDlgContent("", this, this, null);
		mnfrDlgClient.onSuccess(this);
	}


	/*
	 * Create the panel that holds the authentication controls.
	 */
	private Panel createAuthenticationPanel() {
		FlowPanel mainPanel = new FlowPanel();

		// Create a table to hold the controls.
		final FlexTable table = new FlexTable();
		FlexCellFormatter cellFormatter = table.getFlexCellFormatter();
		table.setCellSpacing(4);
		table.addStyleName("dlgContent");
		mainPanel.add(table);
		int nextRow = 0;
		
		// Create the controls used to enter proxy information
		{
			// Add some instructions on the format that should be used when entering the proxy name
			{
				FlowPanel panel = new FlowPanel();
				panel.addStyleName("margintop1");
				panel.addStyleName("modifyNetFolderServerDlg_ProxyNameHint");

				// Add a hint that describes the UNC syntax.
				m_oesProxyNameHint1 = new Label(m_messages.modifyNetFolderServerDlg_ProxyNameHint1());
				panel.add(m_oesProxyNameHint1);
				m_oesProxyNameHint = new Label(m_messages.modifyNetFolderServerDlg_ProxyNameHint2());
				panel.add(m_oesProxyNameHint);
				m_windowsProxyNameHint = new Label(m_messages.modifyNetFolderServerDlg_ProxyNameHint3());
				panel.add(m_windowsProxyNameHint);
				
				cellFormatter.setColSpan(nextRow, 0, 2);
				table.setWidget(nextRow, 0, panel);
				nextRow += 1;
			}
			
			Label label;
			m_proxyTypeIdentityRB = new RadioButton("proxyType", m_messages.modifyNetFolderServerDlg_ProxyTypeIdentity());
			table.setWidget(nextRow, 0, m_proxyTypeIdentityRB);
			cellFormatter.setColSpan(nextRow, 0, 2);
			nextRow += 1;
			label = new InlineLabel(m_messages.modifyNetFolderServerDlg_ProxyIdentityLabel());
			table.setHTML(nextRow, 0, label.getElement().getInnerHTML());
			cellFormatter.setStyleName(nextRow, 0, "modifyNetFolderServerDlg_PadWithProxyIdentities");
			
			table.setWidget (nextRow, 1, m_proxyIdentityFindControl);
			nextRow += 1;
			
			m_proxyTypeManualRB = new RadioButton("proxyType", m_messages.modifyNetFolderServerDlg_ProxyTypeManual());
			table.setWidget(nextRow, 0, m_proxyTypeManualRB);
			cellFormatter.setColSpan(nextRow, 0, 2);
			nextRow += 1;
			
			label = new InlineLabel(m_messages.modifyNetFolderServerDlg_ProxyNameLabel());
			table.setHTML(nextRow, 0, label.getElement().getInnerHTML());
			cellFormatter.setStyleName(nextRow, 0, "modifyNetFolderServerDlg_PadWithProxyIdentities");
			
			FlowPanel tmpPanel = new FlowPanel();
			m_proxyNameTxtBox = new TextBox();
			m_proxyNameTxtBox.setVisibleLength(30);
			tmpPanel.add(m_proxyNameTxtBox);
			m_proxyNameTxtBox.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					String s = m_proxyNameTxtBox.getValue();
					if (GwtClientHelper.hasString(s)) {
						checkProxyTypeManual();
					}
				}
			});
			Image btnImg = GwtClientHelper.buildImage(m_images.browseLdap().getSafeUri().asString());
			btnImg.setTitle(m_messages.modifyNetFolderServerDlg_ProxyName_Alt());
			FlowPanel html = new FlowPanel();
			html.add(btnImg);
			m_browseProxyDnBtn = new Button(html.getElement().getInnerHTML());
			m_browseProxyDnBtn.addStyleName("modifyNetFolderServerDlg_BrowseProxyDN");
			m_browseProxyDnBtn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					browseLdapForProxyNameAsync();
				}
			});
			tmpPanel.add(m_browseProxyDnBtn);
			table.setWidget(nextRow, 1, tmpPanel);
			nextRow += 1;
			
			label = new InlineLabel(m_messages.modifyNetFolderServerDlg_ProxyPwdLabel());
			table.setHTML(nextRow, 0, label.getElement().getInnerHTML());
			cellFormatter.setStyleName(nextRow, 0, "modifyNetFolderServerDlg_PadWithProxyIdentities");
			
			m_proxyPwdTxtBox = new PasswordTextBox();
			m_proxyPwdTxtBox.setVisibleLength(30);
			table.setWidget(nextRow, 1, m_proxyPwdTxtBox);
			nextRow += 1;
			
			// Add a 'test connection' button.
			{
				// Add 'Test connection' button
				Button testConnectionBtn = new Button(m_messages.modifyNetFolderServerDlg_TestConnectionLabel());
				testConnectionBtn.addStyleName("teamingButton");
				testConnectionBtn.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						GwtClientHelper.deferCommand(new ScheduledCommand() {
							@Override
							public void execute() {
								testConnection();
							}
						});
					}
				});
				table.setWidget(nextRow, 0, testConnectionBtn);

				// Add a panel that will display 'Testing
				// connection...' message.
				{
					m_inProgressPanel = new FlowPanel();
					m_inProgressPanel.addStyleName("testConnection_InProgress");
					m_inProgressPanel.setVisible(false);

					ImageResource imgResource = m_images.spinner16();
					Image img = new Image(imgResource);
					img.getElement().setAttribute("align", "absmiddle");
					m_inProgressPanel.add(img);

					label = new InlineLabel(m_messages.testConnection_InProgressLabel());
					m_inProgressPanel.add(label);
					
					table.setWidget(nextRow, 1, m_inProgressPanel);
				}
				
				nextRow += 1;
			}
		}
		
		// Add controls for the authentication type
		{
			FlowPanel spacerPanel = new FlowPanel();
			spacerPanel.getElement().getStyle().setMarginTop(16, Unit.PX);
			table.setWidget(nextRow, 0, spacerPanel);
			nextRow += 1;
			
			m_authTypeLabel = new InlineLabel(m_messages.modifyNetFolderServerDlg_AuthTypeLabel());
			table.setWidget(nextRow, 0, m_authTypeLabel);

			// Add the ListBox where the user can select the authentication
			m_authTypeListbox = new ListBox();
			m_authTypeListbox.setMultipleSelect(false);
			m_authTypeListbox.setVisibleItemCount(1);
			
			m_authTypeListbox.addItem(
				m_messages.modifyNetFolderServerDlg_AuthType_Kerberos(),
				GwtAuthenticationType.KERBEROS.name());
		
			m_authTypeListbox.addItem(
				m_messages.modifyNetFolderServerDlg_AuthType_Ntlm(),
				GwtAuthenticationType.NTLM.name());

			m_authTypeListbox.addItem(
				m_messages.modifyNetFolderServerDlg_AuthType_NMAS(),
				GwtAuthenticationType.NMAS.name());

			m_authTypeListbox.addItem(
				m_messages.modifyNetFolderServerDlg_AuthType_KerberosThenNtlm(),
				GwtAuthenticationType.KERBEROS_THEN_NTLM.name());

			m_authTypeListbox.setSelectedIndex(0);

			table.setWidget(nextRow, 1, m_authTypeListbox);
			nextRow += 1;
		}

		return mainPanel;
	}
	
	/*
	 * Create the panel that holds the configuration controls.
	 */
	private Panel createConfigPanel() {
		FlowPanel mainPanel;
		final FlexTable table;
		FlowPanel spacerPanel;
		Label label;
		int nextRow;
		FlexCellFormatter cellFormatter;
		
		mainPanel = new FlowPanel();

		// Create a table to hold the controls.
		table = new FlexTable();
		table.setCellSpacing(4);
		table.addStyleName("dlgContent");
		
		mainPanel.add(table);
		
		cellFormatter = table.getFlexCellFormatter();
		
		nextRow = 0;
		
		// Create the controls for 'Name'.
		{
			label = new InlineLabel(m_messages.modifyNetFolderServerDlg_NameLabel());
			table.setHTML(nextRow, 0, label.getElement().getInnerHTML());
			
			m_nameTxtBox = new TextBox();
			m_nameTxtBox.setVisibleLength(30);
			table.setWidget(nextRow, 1, m_nameTxtBox);
			nextRow += 1;
		}
		
		// Create a select control for selecting the type of net folder root
		if (SHOW_NET_FOLDER_SERVER_TYPE) {
			label = new InlineLabel(m_messages.modifyNetFolderServerDlg_TypeLabel());
			table.setHTML(nextRow, 0, label.getElement().getInnerHTML());
			
			// Add the ListBox where the user can select the type of net folder root
			m_rootTypeListbox = new ListBox();
			m_rootTypeListbox.setMultipleSelect(false);
			m_rootTypeListbox.setVisibleItemCount(1);
			
			m_rootTypeListbox.addItem(
				m_messages.modifyNetFolderServerDlg_Type_Windows(),
				NetFolderRootType.WINDOWS.name());
		
			m_rootTypeListbox.addItem(
				m_messages.modifyNetFolderServerDlg_Type_OES(),
				NetFolderRootType.OES.name());
			
			m_rootTypeListbox.addItem(
				m_messages.modifyNetFolderServerDlg_Type_OES2015(),
				NetFolderRootType.OES2015.name());
		
			m_rootTypeListbox.addItem(
				m_messages.modifyNetFolderServerDlg_Type_Netware(),
				NetFolderRootType.NETWARE.name());

			if (GwtMainPage.m_requestInfo.getAllowSharePointAsAServerType() == true) {
				if (GwtMainPage.m_requestInfo.getAllowSharePoint2010AsAServerType() == true) {
					m_rootTypeListbox.addItem(
						m_messages.modifyNetFolderServerDlg_Type_SharePoint2010(),
						NetFolderRootType.SHARE_POINT_2010.name());
				}
			
				if (GwtMainPage.m_requestInfo.getAllowSharePoint2013AsAServerType() == true) {
					m_rootTypeListbox.addItem(
						m_messages.modifyNetFolderServerDlg_Type_SharePoint2013(),
						NetFolderRootType.SHARE_POINT_2013.name());
				}
			}
			
			m_rootTypeListbox.setSelectedIndex(0);

			m_rootTypeListbox.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							handleRootTypeSelected();
						}
					});
				}
			});
			table.setWidget(nextRow, 1, m_rootTypeListbox);
			nextRow += 1;
		}

		// Create the controls for 'root path'.
		{
			m_serverPathHintPanel = new FlowPanel();
			m_serverPathHintPanel.addStyleName("margintop1");
			m_serverPathHintPanel.addStyleName("modifyNetFolderServerDlg_ServerPathHint");

			// Add a hint that describes the UNC syntax.
			label = new Label(m_messages.modifyNetFolderServerDlg_ServerPathHint1());
			m_serverPathHintPanel.add(label);
			
			cellFormatter.setColSpan(nextRow, 0, 2);
			table.setWidget(nextRow, 0, m_serverPathHintPanel);
			nextRow += 1;
			
			label = new InlineLabel(m_messages.modifyNetFolderServerDlg_ServerPathLabel());
			table.setHTML(nextRow, 0, label.getElement().getInnerHTML());
			
			m_rootPathTxtBox = new TextBox();
			m_rootPathTxtBox.setVisibleLength(50);
			table.setWidget(nextRow, 1, m_rootPathTxtBox);
			nextRow += 1;
		}
		
		// Create the WebDAV specific controls
		if (SHOW_WEBDAV_CONTROLS) {
			// Add some space
			m_webDavSpacerPanel = new FlowPanel();
			m_webDavSpacerPanel.getElement().getStyle().setMarginTop(10, Unit.PX);
			m_webDavSpacerPanel.setVisible(false);
			table.setWidget(nextRow, 0, m_webDavSpacerPanel);
			nextRow += 1;
			
			m_hostUrlLabel = new InlineLabel(m_messages.modifyNetFolderServerDlg_HostUrlLabel());
			m_hostUrlLabel.setVisible(false);
			table.setWidget(nextRow, 0, m_hostUrlLabel);
			
			m_hostUrlTxtBox = new TextBox();
			m_hostUrlTxtBox.setVisibleLength(50);
			m_hostUrlTxtBox.setVisible(false);
			table.setWidget(nextRow, 1, m_hostUrlTxtBox);
			nextRow += 1;
			
			cellFormatter.setColSpan(nextRow, 0, 2);
			m_allowSelfSignedCertsCkbox = new CheckBox(m_messages.modifyNetFolderServerDlg_AllowSelfSignedCertsLabel());
			m_allowSelfSignedCertsCkbox.setVisible(false);
			table.setWidget(nextRow, 0, m_allowSelfSignedCertsCkbox);
			nextRow += 1;

			cellFormatter.setColSpan(nextRow, 0, 2);
			m_isSharePointServerCkbox = new CheckBox(m_messages.modifyNetFolderServerDlg_IsSharePointServerLabel());
			m_isSharePointServerCkbox.setVisible(false);
			table.setWidget(nextRow, 0, m_isSharePointServerCkbox);
			nextRow += 1;
		}
		
		// Create the controls used to select who can create net folders using this
		// net folder root.
		if (SHOW_PRIVILEGED_USERS_UI) {
			// Add some space
			spacerPanel = new FlowPanel();
			spacerPanel.getElement().getStyle().setMarginTop(16, Unit.PX);
			table.setWidget(nextRow, 0, spacerPanel);
			nextRow += 1;
			
			// Add a hint
			cellFormatter.setColSpan(nextRow, 0, 2);
			cellFormatter.setWordWrap(nextRow, 0, false);
			cellFormatter.addStyleName(nextRow, 0, "modifyNetFolderRootDlg_SelectPrivelegedUsersHint");
			label = new InlineLabel(m_messages.modifyNetFolderServerDlg_PrivilegedPrincipalsHint());
			table.setHTML(nextRow, 0, label.getElement().getInnerHTML());
			nextRow += 1;
			
			cellFormatter.setColSpan(nextRow, 0, 2);
			table.setWidget(nextRow, 0, m_selectPrincipalsWidget);
			nextRow += 1;
		}

		return mainPanel;
	}
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent(Object props) {
		FlowPanel mainPanel;
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName("teamingDlgBoxContent");

		m_tabPanel = new TabPanel();
		m_tabPanel.addStyleName("vibe-tabPanel");

		mainPanel.add(m_tabPanel);

		// Create the panel that holds the basic net folder server configuration
		{
			Panel configPanel;
			
			configPanel = createConfigPanel();
			m_tabPanel.add(configPanel, m_messages.modifyNetFolderServerDlg_ConfigTab());
		}
		
		// Create the panel that holds the authentication information
		{
			Panel authPanel;
			
			authPanel = createAuthenticationPanel();
			m_tabPanel.add(authPanel, m_messages.modifyNetFolderServerDlg_AuthenticationTab());
		}
		
		// Create the panel that holds the controls for the schedule
		{
			Panel schedPanel;
			
			schedPanel = createSchedulePanel();
			m_tabPanel.add(schedPanel, m_messages.modifyNetFolderServerDlg_ScheduleTab());
		}
		
		// Create the panel that holds the controls for data synch
		{
			Panel syncPanel;
			
			syncPanel = createSyncPanel();
			m_tabPanel.add(syncPanel, m_messages.modifyNetFolderServerDlg_SyncTab());
		}
		
		m_tabPanel.selectTab(0);

		return mainPanel;
	}
	
	/*
	 * Create the panel that holds the sync schedule controls.
	 */
	private Panel createSchedulePanel() {
		FlowPanel mainPanel = new FlowPanel();
		
		m_scheduleWidget = new ScheduleWidget(m_messages.modifyNetFolderDlg_EnableSyncScheduleLabel());
		m_scheduleWidget.addStyleName("modifyNetFolderServerDlg_ScheduleWidget");
		mainPanel.add(m_scheduleWidget);

		return mainPanel;
	}
	
	/*
	 * Create the panel that holds the sync controls.
	 */
	private Panel createSyncPanel() {
		FlowPanel mainPanel;
		FlowPanel tmpPanel;
		
		mainPanel = new FlowPanel();
		
		tmpPanel = new FlowPanel();
		m_indexContentCB = new CheckBox(m_messages.modifyNetFolderServerDlg_IndexContentCB());
		tmpPanel.add(m_indexContentCB);
		mainPanel.add(tmpPanel);
		
		// Add the controls needed to define Jits settings
		{
			FlowPanel jitsPanel;

			jitsPanel = new FlowPanel();
			mainPanel.add(jitsPanel);
			
			m_jitsEnabledCkbox = new CheckBox(m_messages.modifyNetFolderDlg_EnableJitsLabel());
			tmpPanel = new FlowPanel();
			tmpPanel.add(m_jitsEnabledCkbox);
			jitsPanel.add(tmpPanel);
			
			// Add a panel that holds all the max age controls.
			{
				FlowPanel maxAgePanel;
				
				maxAgePanel = new FlowPanel();
				maxAgePanel.addStyleName("marginleft3");
				
				// Add the controls for 'results max age'.
				{
					HorizontalPanel hPanel;
					Label intervalLabel;

					hPanel = new HorizontalPanel();
					hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
					hPanel.setSpacing(0);
					
					intervalLabel = new Label(m_messages.modifyNetFolderDlg_JitsResultsMaxAgeLabel());
					hPanel.add(intervalLabel);
					
					m_jitsResultsMaxAge = new TextBox();
					m_jitsResultsMaxAge.addKeyPressHandler(new KeyPressHandler() {
						@Override
						public void onKeyPress(KeyPressEvent event) {
					        // Get the key the user pressed.
					        int keyCode = event.getNativeEvent().getKeyCode();
					        if (GwtClientHelper.isKeyValidForNumericField(event.getCharCode(), keyCode) == false) {
				        		// Suppress the current keyboard event.
					        	m_jitsResultsMaxAge.cancelKey();
					        }
						}
					});
					m_jitsResultsMaxAge.setVisibleLength(3);
					hPanel.add(m_jitsResultsMaxAge);
					
					intervalLabel = new Label(m_messages.netFolderGlobalSettingsDlg_SecondsLabel());
					intervalLabel.addStyleName("marginleft2px");
					intervalLabel.addStyleName("gray3");
					hPanel.add(intervalLabel);

					maxAgePanel.add(hPanel);
				}

				// Add the controls for 'ACL max age'.
				{
					HorizontalPanel hPanel;
					Label intervalLabel;

					hPanel = new HorizontalPanel();
					hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
					hPanel.setSpacing(0);
					
					intervalLabel = new Label(m_messages.modifyNetFolderDlg_JitsAclMaxAgeLabel());
					hPanel.add(intervalLabel);
					
					m_jitsAclMaxAge = new TextBox();
					m_jitsAclMaxAge.addKeyPressHandler(new KeyPressHandler() {
						@Override
						public void onKeyPress(KeyPressEvent event) {
					        // Get the key the user pressed.
					        int keyCode = event.getNativeEvent().getKeyCode();
					        if (GwtClientHelper.isKeyValidForNumericField(event.getCharCode(), keyCode) == false) {
				        		// Suppress the current keyboard event.
					        	m_jitsAclMaxAge.cancelKey();
					        }
						}
					});
					m_jitsAclMaxAge.setVisibleLength(3);
					hPanel.add(m_jitsAclMaxAge);
					
					intervalLabel = new Label(m_messages.netFolderGlobalSettingsDlg_SecondsLabel());
					intervalLabel.addStyleName("marginleft2px");
					intervalLabel.addStyleName("gray3");
					hPanel.add(intervalLabel);

					maxAgePanel.add(hPanel);
				}

				jitsPanel.add(maxAgePanel);
			}
			
			mainPanel.add(jitsPanel);
		}
		
		// Add the control for 'allow desktop application to trigger
		// initial home folder sync'.
		{
			tmpPanel = new FlowPanel();
			
			m_allowDesktopAppToTriggerSyncCB = new CheckBox(m_messages.modifyNetFolderServerDlg_AllowDesktopAppToTriggerSync());
			
			tmpPanel.add(m_allowDesktopAppToTriggerSyncCB);
			mainPanel.add(tmpPanel);
		}
		
		if (GwtMainPage.m_requestInfo.getShowSyncOnlyDirStructureUI()) {
			tmpPanel = new FlowPanel();
			m_fullSyncDirOnlyCB = new CheckBox(m_messages.modifyNetFolderServerDlg_SyncOnlyDirStructureCB());
			tmpPanel.add(m_fullSyncDirOnlyCB);
			mainPanel.add(tmpPanel);
		}
		
		return mainPanel;
	}
	
	/*
	 * Issue a GWT RPC request to create a net folder root.  If the rpc request is successful
	 * close this dialog.
	 */
	private void createNetFolderRootAndClose() {
		CreateNetFolderRootCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback;
		
		rpcCallback = new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				FlowPanel errorPanel;
				Label label;
				String errMsg;
				
				hideStatusMsg();
				setOkEnabled(true);

				// Get the panel that holds the errors.
				errorPanel = getErrorPanel();
				errorPanel.clear();
				
				errMsg = GwtTeaming.getMessages().modifyNetFolderServerDlg_ErrorCreatingNetFolderServer(caught.toString());
				if (caught instanceof GwtTeamingException) {
					GwtTeamingException ex = ((GwtTeamingException) caught);
					if (ex.getExceptionType().equals(ExceptionType.NET_FOLDER_ROOT_ALREADY_EXISTS)) {
						String desc = GwtTeaming.getMessages().modifyNetFolderServerDlg_ServerAlreadyExists();
						errMsg =GwtTeaming.getMessages().modifyNetFolderServerDlg_ErrorModifyingNetFolderServer(desc);
					}
				}
				label = new Label(errMsg);
				label.addStyleName("dlgErrorLabel");
				errorPanel.add(label);
				
				showErrorPanel();
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				NetFolderRoot netFolderRoot = ((NetFolderRoot) result.getResponseData());
				
				hideStatusMsg();
				setOkEnabled(true);

				// Fire an event that lets everyone know a net folder root was created.
				NetFolderRootCreatedEvent event = new NetFolderRootCreatedEvent(netFolderRoot);
				GwtTeaming.fireEvent(event);

				// Close this dialog.
				hide();
			}						
		};
		
		// Issue a GWT RPC request to create the net folder root.
		{
			NetFolderRoot netFolderRoot;
			
			netFolderRoot = getNetFolderRootFromDlg();
			
			showStatusMsg(GwtTeaming.getMessages().modifyNetFolderServerDlg_CreatingNetFolderServer(), "dlgBox_statusPanel_relative");
			
			cmd = new CreateNetFolderRootCmd(netFolderRoot);
			GwtClientHelper.executeCommand(cmd, rpcCallback);
		}
	}

	/*
	 * Show/hide the appropriate controls based on the selected root type.
	 */
	private void danceDlg(boolean setFocus) {
		int selectedIndex;
		NetFolderRootType type = NetFolderRootType.WINDOWS;
		
		if (m_rootTypeListbox != null) {
			selectedIndex = m_rootTypeListbox.getSelectedIndex();
			if (selectedIndex >= 0) {
				boolean visible;
				
				// Get the selected root type;
				type = getSelectedRootType();
	
				visible = false;
				if (type.equals(NetFolderRootType.WEB_DAV))
					visible = true;
				
				// Show/hide the controls that are WebDAV specific
				if (m_webDavSpacerPanel != null)
					m_webDavSpacerPanel.setVisible(visible);
				
				if (m_hostUrlLabel != null)
					m_hostUrlLabel.setVisible(visible);
				
				if (m_hostUrlTxtBox != null)
					m_hostUrlTxtBox.setVisible(visible);
				
				if (m_allowSelfSignedCertsCkbox != null)
					m_allowSelfSignedCertsCkbox.setVisible(visible);
				
				if (m_isSharePointServerCkbox != null)
					m_isSharePointServerCkbox.setVisible(visible);
				
				// Do the following work:
				//	- Update the server path and proxy name hint
				//	- show/hide controls.
				//	- Update the options in the authentication type ListBox.
				{
					Label label;
					
					m_serverPathHintPanel.clear();
					
					switch(type) {
					case OES:
					case NETWARE:
						label = new Label(m_messages.modifyNetFolderServerDlg_ServerPathHint1());
						m_serverPathHintPanel.add(label);

						label = new Label(m_messages.modifyNetFolderServerDlg_ServerPathOESHint());
						m_serverPathHintPanel.add(label);
						
						m_windowsProxyNameHint.setVisible(false);
						m_oesProxyNameHint1.setVisible(true);
						m_oesProxyNameHint.setVisible(true);
						
						m_authTypeLabel.setVisible(true);
						m_authTypeListbox.setVisible(true);
						
						// Remove NTLM, Kerberos and "auto detect" and
						// add 'NMAS' to the authentication type ListBox.
						{
							int index;
							
							// Is NTLM in the ListBox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.NTLM.name());
							if (index != (-1)) {
								// Yes, remove it.
								m_authTypeListbox.removeItem(index);
							}
							
							// Is Kerberos in the ListBox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.KERBEROS.name());
							if (index != (-1)) {
								// Yes, remove it.
								m_authTypeListbox.removeItem(index);
							}
							
							// Is 'auto detect' in the ListBox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.KERBEROS_THEN_NTLM.name());
							if (index != (-1)) {
								// Yes, remove it.
								m_authTypeListbox.removeItem(index);
							}
							
							// Is 'NMAS' in the ListBox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.NMAS.name());
							if (index == (-1)) {
								// No, add it
								m_authTypeListbox.insertItem(
									m_messages.modifyNetFolderServerDlg_AuthType_NMAS(),
									GwtAuthenticationType.NMAS.name(),
									1);
							}
						}
						break;
						
					case OES2015:
						label = new Label(m_messages.modifyNetFolderServerDlg_ServerPathHint1());
						m_serverPathHintPanel.add(label);

						label = new Label(m_messages.modifyNetFolderServerDlg_ServerPathOESHint());
						m_serverPathHintPanel.add(label);
						
						m_windowsProxyNameHint.setVisible(false);
						m_oesProxyNameHint1.setVisible(true);
						m_oesProxyNameHint.setVisible(true);
						
						m_authTypeLabel.setVisible(true);
						m_authTypeListbox.setVisible(true);
						
						// Remove NTLM, Kerberos and NMAS and add 'auto
						// detect' to the authentication type ListBox.
						{
							int index;
							
							// Is NTLM in the ListBox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.NTLM.name());
							if (index != (-1)) {
								// Yes, remove it.
								m_authTypeListbox.removeItem(index);
							}
							
							// Is Kerberos in the ListBox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.KERBEROS.name());
							if (index != (-1)) {
								// Yes, remove it.
								m_authTypeListbox.removeItem(index);
							}
							
							// Is 'auto detect' in the ListBox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.KERBEROS_THEN_NTLM.name());
							if (index == (-1)) {
								// No, add it.
								m_authTypeListbox.insertItem(
									m_messages.modifyNetFolderServerDlg_AuthType_KerberosThenNtlm(),
									GwtAuthenticationType.KERBEROS_THEN_NTLM.name(),
									1);
							}
							
							// Is 'NMAS' in the ListBox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.NMAS.name());
							if (index != (-1)) {
								// Yes, remove it
								m_authTypeListbox.removeItem(index);
							}
						}
						break;
						
					case WINDOWS:
						label = new Label(m_messages.modifyNetFolderServerDlg_ServerPathHint1());
						m_serverPathHintPanel.add(label);

						label = new Label(m_messages.modifyNetFolderServerDlg_ServerPathWindowsHint());
						m_serverPathHintPanel.add(label);
						
						m_windowsProxyNameHint.setVisible(true);
						m_oesProxyNameHint1.setVisible(false);
						m_oesProxyNameHint.setVisible(false);
						m_authTypeLabel.setVisible(true);
						m_authTypeListbox.setVisible(true);
						
						// Remove 'NMAS' and add 'NTLM', 'kerberos' and
						// 'auto detect' to the authentication type
						// ListBox.
						{
							int index;
							
							// Is NTLM in the ListBox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.NTLM.name());
							if (index == (-1)) {
								// No, add it
								m_authTypeListbox.insertItem(
									m_messages.modifyNetFolderServerDlg_AuthType_Ntlm(),
									GwtAuthenticationType.NTLM.name(),
									0);
							}
							
							// Is Kerberos in the ListBox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.KERBEROS.name());
							if (index == (-1)) {
								// No, add it
								m_authTypeListbox.insertItem(
									m_messages.modifyNetFolderServerDlg_AuthType_Kerberos(),
									GwtAuthenticationType.KERBEROS.name(),
									0);
							}
							
							// Is 'auto detect' in the ListBox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.KERBEROS_THEN_NTLM.name());
							if (index == (-1)) {
								// No, add it.
								m_authTypeListbox.insertItem(
									m_messages.modifyNetFolderServerDlg_AuthType_KerberosThenNtlm(),
									GwtAuthenticationType.KERBEROS_THEN_NTLM.name(),
									2);
							}
							
							// Is 'NMAS' in the ListBox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.NMAS.name());
							if (index != (-1)) {
								// Yes, remove it
								m_authTypeListbox.removeItem(index);
							}
						}
						break;
						
					case FAMT:
						m_windowsProxyNameHint.setVisible(false);
						m_oesProxyNameHint1.setVisible(false);
						m_oesProxyNameHint.setVisible(false);
						m_authTypeLabel.setVisible(false);
						m_authTypeListbox.setVisible(false);
						break;
						
					case SHARE_POINT_2010:
					case SHARE_POINT_2013:
						label = new Label(m_messages.modifyNetFolderServerDlg_SharePointPathHint());
						m_serverPathHintPanel.add(label);
						
						m_windowsProxyNameHint.setVisible(true);
						m_oesProxyNameHint1.setVisible(false);
						m_oesProxyNameHint.setVisible(false);
						m_authTypeLabel.setVisible(false);
						m_authTypeListbox.setVisible(false);
						break;
						
					default:
						break;
					}
				}
			}
		}
		
		// Is the server type Windows?
		if (type.equals(NetFolderRootType.WINDOWS)) {
			// Yes
			GwtClientHelper.selectListboxItemByValue(m_authTypeListbox, GwtAuthenticationType.KERBEROS_THEN_NTLM.name());
		}
		else {
			// No, select the first one.
			m_authTypeListbox.setItemSelected(0, true);
		}
	}
	
	/**
	 * This method gets called when user user presses the Cancel push
	 * button.
	 * 
	 * Implements the EditCanceledHandler.editCanceled() interface
	 * method.
	 * 
	 * @return
	 */
	@Override
	public boolean editCanceled() {
		if (SHOW_PRIVILEGED_USERS_UI) {
			m_selectPrincipalsWidget.closePopups();
		}
		
		// Simply return true to allow the dialog to close.
		return true;
	}

	/**
	 * This gets called when the user presses OK.
	 * 
	 * If we are editing an existing net folder root, we will issue a
	 * GWT RPC request to save the net folder root and then throw a
	 * 'net folder root modified' event.
	 * 
	 * If we are creating a new net folder root we will issue a GWT RPC
	 * request to create the new net folder root and then throw a 'net
	 * folder root created' event.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() method.
	 * 
	 * @param obj
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object obj) {
		// Is the root type WebDAV?
		NetFolderRootType serverType = getSelectedRootType();
		if (serverType.equals(NetFolderRootType.WEB_DAV)) {
			// Yes, make sure they entered the host url
			if (isHostUrlValid() == false) {
				m_hostUrlTxtBox.setFocus(true);
				return false;
			}
		}
		
		// Is the server type 'FAMT' or undefined?
		if (serverType.equals(NetFolderRootType.FAMT)) {
			// Yes, tell the user they need to pick a server type.
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					GwtClientHelper.deferredAlert(GwtTeaming.getMessages().modifyNetFolderServerDlg_SelectServerTypePrompt());
					m_tabPanel.selectTab(0);
				}
			});

			return false;
		}
		
		// Is the 'use proxy identity' radio button selected?
		if (m_proxyTypeIdentityRB.getValue()) {
			// Yes!  Is there a proxy identity selected?
			if (null == m_proxyIdentity) {
				// No!  Tell the user about the problem and bail.
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().modifyNetFolderServerDlg_SelectProxyIdentityPrompt());
				m_tabPanel.selectTab(1);
				return false;
			}
		}
		
		clearErrorPanel();
		hideErrorPanel();
		setOkEnabled(false);

		// Are we editing an existing net folder root?
		if (m_netFolderRoot != null) {
			// Yes, issue a GWT RPC request to modify the net folder root.  If the rpc request is
			// successful, close this dialog.
			modifyNetFolderRootAndClose();
		}
		
		else {
			// No, we are creating a new net folder root.
			
			// Is the name entered by the user valid?
			if (isNameValid() == false) {
				m_nameTxtBox.setFocus(true);
				return false;
			}
			
			// Issue a GWT RPC request to create the net folder root.  If the rpc request is successful,
			// close this dialog.
			createNetFolderRootAndClose();
		}
		
		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully create/modify a net folder root.
		return false;
	}
	
	/*
	 * Asynchronously runs the LDAP browser for the proxy name.
	 */
	private void browseLdapForProxyNameAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				browseLdapForProxyNameNow();
			}
		});
	}
	
	/*
	 * Synchronously runs the LDAP browser for the proxy name.
	 */
	private void browseLdapForProxyNameNow() {
		// Have we instantiated an LDAP browser yet?
		if (null == m_ldapBrowserDlg) {
			// No!  Create one now...
			LdapBrowserDlg.createAsync(new LdapBrowserDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(LdapBrowserDlg ldapDlg) {
					// ...save it away and run it.
					m_ldapBrowserDlg = ldapDlg;
					getLdapServersAndRunLdapBrowserAsync();
				}
			});
		}
		
		else {
			// Yes, we've already instantiated an LDAP browser!  Simply
			// run it.
			getLdapServersAndRunLdapBrowserNow();
		}
	}

	/*
	 */
	private Boolean getAllowDesktopAppToTriggerSync() {
		return m_allowDesktopAppToTriggerSyncCB.getValue();
	}
	
	/*
	 */
	private long getJitsAclMaxAge() {
		String maxAgeStr;
		long maxAge = 0;
		
		maxAgeStr = m_jitsAclMaxAge.getText();
		if (maxAgeStr != null && maxAgeStr.length() > 0)
			maxAge = Long.parseLong(maxAgeStr);
		
		maxAge *= 1000;
		return maxAge;
	}
	
	/*
	 */
	private boolean getJitsEnabled() {
		if (m_jitsEnabledCkbox.getValue()) {
			return true;
		}
		return false;
	}
	
	/*
	 */
	private long getJitsResultsMaxAge() {
		String maxAgeStr;
		long maxAge = 0;
		
		maxAgeStr = m_jitsResultsMaxAge.getText();
		if (maxAgeStr != null && maxAgeStr.length() > 0)
			maxAge = Long.parseLong(maxAgeStr);
		
		maxAge *= 1000;
		return maxAge;
	}
	
	/*
	 * Gets the list of LDAP servers and runs the browser on them.
	 */
	private void getLdapServersAndRunLdapBrowserAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				// ...and run it.
				getLdapServersAndRunLdapBrowserNow();
			}
		});
	}
	
	private void getLdapServersAndRunLdapBrowserNow() {
		// Have we obtained the list of LDAP servers yet?
		if (null == m_ldapServerList) {
			// No!  Read them now...
			LdapBrowserDlg.getLdapServerList(new LdapBrowseListCallback() {
				@Override
				public void onFailure() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(List<LdapBrowseSpec> serverList) {
					// ...save them away and run the dialog.
					m_ldapServerList = serverList;
					runLdapBrowserAsync();
				}
			});
		}
		
		else {
			// Yes, we've already obtained the list of LDAP servers!
			// Simply run the dialog.
			runLdapBrowserNow();
		}
	}

	/*
	 * Runs the LDAP browser.
	 */
	private void runLdapBrowserAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				runLdapBrowserNow();
			}
		});
	}
	
	private void runLdapBrowserNow() {
		// Do we have any LDAP servers to browse?
		int c = ((null == m_ldapServerList) ? 0 : m_ldapServerList.size());
		if (0 == c) {
			// No!  Tell the user about the problem and bail.
			GwtClientHelper.deferredAlert(GwtTeaming.getMessages().modifyNetFolderServerDlg_NoLdapServers());
			return;
		}
		
		// Run the LDAP browser using the list of LDAP servers.
		LdapBrowserDlg.initAndShow(m_ldapBrowserDlg, new LdapBrowserCallback() {
			@Override
			public void closed() {
				// Ignored.  We don't care if the user closes
				// the browser.
			}

			@Override
			public void selectionChanged(LdapObject selection, DirectoryType dt) {
				// Since we're browsing for user DN, it will ONLY
				// be a leaf node.  Ignore non-leaf selections.
				if (selection.isLeaf()) {
					final String fqdn = selection.getDn();
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							setProxyNameFromFQDN(fqdn);
						}
					});
					m_ldapBrowserDlg.hide();
				}
			}
		},
		m_ldapServerList,		// List of LDAP servers that can be browsed.
		m_browseProxyDnBtn);	// The dialog is positioned relative to this.
	}

	/*
	 * If the selected server type is SharePoint or Windows, get the proxy name in the format
	 * domain\samAccountName and store that in the proxy name text box.
	 */
	private void setProxyNameFromFQDN(String fqdn) {
		NetFolderRootType selectedServerType;
		
		selectedServerType = getSelectedRootType();

		if (selectedServerType.equals(NetFolderRootType.SHARE_POINT_2013) ||
				selectedServerType.equals(NetFolderRootType.SHARE_POINT_2010) ||
				selectedServerType.equals(NetFolderRootType.WINDOWS)) {
			setProxyNameUsingWindowsFormat(fqdn);
		}
		
		else {
			setProxyName(fqdn);
		}
	}
	
	/*
	 */
	private void setProxyName(String name) {
		if (GwtClientHelper.hasString(name) && (null != m_proxyTypeManualRB)) {
			checkProxyTypeManual();
		}
		m_proxyNameTxtBox.setValue(name);
	}
	
	/*
	 * For the given fqdn, get the proxy name in the Windows format of domain-name\samAccountName
	 * and stick that proxy name in the text box.
	 */
	private void setProxyNameUsingWindowsFormat(final String fqdn) {
		AsyncCallback<VibeRpcResponse> rpcCallback = new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				setProxyName(fqdn);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				String proxyName = fqdn;
				if (result.getResponseData() != null) {
					GwtADLdapObject ldapObject = ((GwtADLdapObject) result.getResponseData());
					String headPart = ldapObject.getNetbiosName();
					if (!(GwtClientHelper.hasString(headPart))) {
						headPart = ldapObject.getDomainName();
					}
					String tailPart = ldapObject.getSamAccountName();
					if (headPart != null && headPart.length() > 0 &&
							tailPart != null && tailPart.length() > 0) {
						proxyName = (headPart + "\\" + tailPart);
					}
				}
				
				setProxyName(proxyName);
			}						
		};
		
		// Issue a GWT RPC request to get an ldap object from AD.  We will get the
		// domain-name and samAccountName from the ldap object.
		GetLdapObjectFromADCmd cmd = new GetLdapObjectFromADCmd(fqdn);
		GwtClientHelper.executeCommand(cmd, rpcCallback);
	}
	
	/*
	 */
	private boolean getAllowSelfSignedCerts() {
		return m_allowSelfSignedCertsCkbox.getValue();
	}
	
	/*
	 */
	private GwtAuthenticationType getAuthType() {
		if (m_authTypeListbox.isVisible() == false)
			return null;
		
		int selectedIndex = m_authTypeListbox.getSelectedIndex();
		if (selectedIndex >= 0) {
			String value = m_authTypeListbox.getValue(selectedIndex);
			if (value != null)
				return GwtAuthenticationType.getType(value);
		}
		
		return GwtAuthenticationType.NTLM;
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 * 
	 * Implements the DlgBox.getDataFromDlg() method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		// Return something.  Doesn't matter what because editSuccessful() does the work.
		return Boolean.TRUE;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog
	 * is shown.
	 * 
	 * Implements the DlgBox.getFocusWidget() method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		if (m_netFolderRoot == null)
			return m_nameTxtBox;
		
		return m_rootPathTxtBox;
	}
	
	/**
	 * Return the value of the 'Synchronize only the directory
	 * structure'.
	 * 
	 * @return
	 */
	public Boolean getFullSyncDirOnly() {
		if (GwtMainPage.m_requestInfo.getShowSyncOnlyDirStructureUI())
			return m_fullSyncDirOnlyCB.getValue();
		
		return Boolean.FALSE;
	}
	
	/**
	 * 
	 */
	@Override
	public HelpData getHelpData() {
		HelpData helpData;
		
		helpData = new HelpData();
		helpData.setGuideName(HelpData.ADMIN_GUIDE);
		helpData.setPageId("netfolders_servers");
		
		return helpData;
	}
	
	/*
	 */
	private String getHostUrl() {
		return m_hostUrlTxtBox.getValue();
	}
	
	/*
	 */
	private Boolean getIndexContent() {
		return m_indexContentCB.getValue();
	}
	
	/*
	 */
	private boolean getIsSharePointServer() {
		return m_isSharePointServerCkbox.getValue();
	}
	
	/*
	 * Return the list of principals that have rights to use this net folder root
	 */
	private ArrayList<GwtPrincipal> getListOfPrivilegedPrincipals() {
		ArrayList<GwtPrincipal> listOfPrincipals;

		boolean spuUI = SHOW_PRIVILEGED_USERS_UI;
		if (spuUI && m_selectPrincipalsWidget != null)
			listOfPrincipals = m_selectPrincipalsWidget.getListOfSelectedPrincipals();
		else
			listOfPrincipals = new ArrayList<GwtPrincipal>();
		
		return listOfPrincipals;
	}
	
	/*
	 * Create a NetFolderRoot object that holds the id of the net folder root being edited,
	 * and the net folder root's new info
	 */
	private NetFolderRoot getNetFolderRootFromDlg() {
		NetFolderRoot netFolderRoot = new NetFolderRoot();
		netFolderRoot.setName(getName());
		netFolderRoot.setRootType(getSelectedRootType());
		netFolderRoot.setRootPath(getRootPath());
		netFolderRoot.setProxyName(getProxyName());
		netFolderRoot.setProxyPwd(getProxyPwd());
		netFolderRoot.setUseProxyIdentity(m_proxyTypeIdentityRB.getValue());
		netFolderRoot.setProxyIdentity(m_proxyIdentity);
		netFolderRoot.setAuthType(getAuthType());
		netFolderRoot.setFullSyncDirOnly(getFullSyncDirOnly());
		netFolderRoot.setIndexContent(getIndexContent());
		netFolderRoot.setJitsEnabled(getJitsEnabled());
		netFolderRoot.setJitsResultsMaxAge(getJitsResultsMaxAge());
		netFolderRoot.setJitsAclMaxAge(getJitsAclMaxAge());
		netFolderRoot.setAllowDesktopAppToTriggerInitialHomeFolderSync(getAllowDesktopAppToTriggerSync());
		
		boolean spuUI = SHOW_PRIVILEGED_USERS_UI;
		if (spuUI && m_selectPrincipalsWidget != null)
			netFolderRoot.setListOfPrincipals(getListOfPrivilegedPrincipals());
		
		if (getSelectedRootType().equals(NetFolderRootType.WEB_DAV)) {
			netFolderRoot.setHostUrl(getHostUrl());
			netFolderRoot.setAllowSelfSignedCerts(getAllowSelfSignedCerts());
			netFolderRoot.setIsSharePointServer(getIsSharePointServer());
		}

		netFolderRoot.setSyncSchedule(getSyncSchedule());

		if (m_netFolderRoot != null)
			netFolderRoot.setId(m_netFolderRoot.getId());
		
		return netFolderRoot;
	}
	
	
	/*
	 * Return the name entered by the user.
	 */
	private String getName() {
		return m_nameTxtBox.getValue();
	}
	
	/*
	 */
	private String getProxyName() {
		return m_proxyNameTxtBox.getValue();
	}
	
	/*
	 */
	private String getProxyPwd() {
		return m_proxyPwdTxtBox.getValue();
	}
	
	/*
	 * Return the root path entered by the user.
	 */
	private String getRootPath() {
		String path = m_rootPathTxtBox.getValue();
		if (null == path)
		     path = "";
		else path = path.trim();
		boolean strippedTrailingSlash = false;
		while (path.endsWith("\\")) {
			strippedTrailingSlash = true;
			path = path.substring(0, (path.length() - 1));
		}
		if (strippedTrailingSlash) {
			GwtClientHelper.deferredAlert(m_messages.modifyNetFolderServerDlg_ServerPathCleaned(path));
		}
		return path;
	}
	
	/*
	 * Return the selected root type
	 */
	private NetFolderRootType getSelectedRootType() {

		if (m_rootTypeListbox != null) {
			int selectedIndex = m_rootTypeListbox.getSelectedIndex();
			if (selectedIndex >= 0) {
				String value = m_rootTypeListbox.getValue(selectedIndex);
				if (value != null)
					return NetFolderRootType.getType(value);
			}
			
			return NetFolderRootType.UNKNOWN;
		}
		
		return NetFolderRootType.UNKNOWN;
	}

	/*
	 * Return the sync schedule
	 */
	private GwtSchedule getSyncSchedule() {
		return m_scheduleWidget.getSchedule();
	}

	/*
	 * This method gets called when the user selects the root type
	 * Show/hide the appropriate controls based on the selected root type.
	 */
	private void handleRootTypeSelected() {
		// Does the server types ListBox have an 'undefined' item?
		int index = GwtClientHelper.doesListboxContainValue(m_rootTypeListbox, NetFolderRootType.FAMT.name());
		if (index != (-1)) {
			// Yes, remove it.
			m_rootTypeListbox.removeItem(index);
		}

		danceDlg(true);
	}
	
	/**
	 * @param netFolderRoot 
	 */
	public void init(NetFolderRoot netFolderRoot) {
		hideErrorPanel();
		
		m_netFolderRoot = netFolderRoot;

		clearErrorPanel();
		hideErrorPanel();
		hideStatusMsg();
		setOkEnabled(true);

		// Clear existing data in the controls.
		m_nameTxtBox.setValue("");
		
		if (m_rootTypeListbox != null) {
			GwtClientHelper.selectListboxItemByValue(m_rootTypeListbox, NetFolderRootType.WINDOWS.name());
		}
		
		m_rootPathTxtBox.setValue( "");
		m_proxyNameTxtBox.setValue("");
		m_proxyPwdTxtBox.setValue( "");
		GwtClientHelper.selectListboxItemByValue(m_authTypeListbox, GwtAuthenticationType.NTLM.name());
		if (m_hostUrlTxtBox != null) {
			m_hostUrlTxtBox.setValue("");
		}
		if (m_allowSelfSignedCertsCkbox != null) {
			m_allowSelfSignedCertsCkbox.setValue(false);
		}
		if (m_isSharePointServerCkbox != null) {
			m_isSharePointServerCkbox.setValue(false);
		}
		m_inProgressPanel.setVisible(false);

		boolean spuUI = SHOW_PRIVILEGED_USERS_UI;
		if (spuUI && (m_selectPrincipalsWidget != null)) {
			m_selectPrincipalsWidget.init(null);//~JW:  Finish
		}

		// Clear out the sync schedule controls.
		m_scheduleWidget.init(null);
		
		m_indexContentCB.setValue(false);
		if (GwtMainPage.m_requestInfo.getShowSyncOnlyDirStructureUI()) {
			m_fullSyncDirOnlyCB.setValue(false);
		}
		
		m_allowDesktopAppToTriggerSyncCB.setValue(true);
		
		// Forget about any list of LDAP servers.  The list may have
		// changed since this dialog was last run and setting this to
		// null will cause it to be reloaded when needed.
		m_ldapServerList = null;
		
		// Are we modifying an existing net folder root?
		if (m_netFolderRoot != null) {
			// Yes!  Update the dialog's header to say 'Edit Net Folder
			// Root'.
			setCaption(GwtTeaming.getMessages().modifyNetFolderServerDlg_EditHeader(m_netFolderRoot.getName()));
			
			// Don't let the user edit the name.
			m_nameTxtBox.setValue(netFolderRoot.getName());
			m_nameTxtBox.setEnabled(false);
			
			// Select the appropriate root type.
			if (m_rootTypeListbox != null) {
				initServerType(netFolderRoot.getRootType());
			}

			// If the root type is WebDAV, initialize the WebDAV specific controls
			if (netFolderRoot.getRootType().equals(NetFolderRootType.WEB_DAV)) {
				m_hostUrlTxtBox.setValue(            netFolderRoot.getHostUrl()             );
				m_allowSelfSignedCertsCkbox.setValue(netFolderRoot.getAllowSelfSignedCerts());
				m_isSharePointServerCkbox.setValue(  netFolderRoot.getIsSharePointServer()  );
			}
			
			m_rootPathTxtBox.setValue(netFolderRoot.getRootPath());
			m_proxyNameTxtBox.setValue(netFolderRoot.getProxyName());
			m_proxyPwdTxtBox.setValue(netFolderRoot.getProxyPwd());
			m_proxyIdentity = netFolderRoot.getProxyIdentity();
			if (null != m_proxyIdentity)
			     m_proxyIdentityFindControl.setInitialSearchString(m_proxyIdentity.getTitle());
			else m_proxyIdentityFindControl.clearText();
			
			if (netFolderRoot.getUseProxyIdentity())
			     checkProxyTypeIdentity();
			else checkProxyTypeManual();
		
			if (spuUI && (m_selectPrincipalsWidget != null)) {
				m_selectPrincipalsWidget.init(m_netFolderRoot.getListOfPrincipals());
			}

			// Initialize the sync schedule controls.
			m_scheduleWidget.init(m_netFolderRoot.getSyncSchedule());
			
			// Initialize the 'index content' control.
			Boolean value = m_netFolderRoot.getIndexContent();
			if (value != null) {
				m_indexContentCB.setValue(value);
			}
			
			// Initialize the 'sync only the directory structure'
			// control.
			if (GwtMainPage.m_requestInfo.getShowSyncOnlyDirStructureUI()) {
				value = m_netFolderRoot.getFullSyncDirOnly();
				if (value != null) {
					m_fullSyncDirOnlyCB.setValue(value);
				}
			}
			
			// Initialize the 'allow desktop application to trigger
			// initial home folder sync' control.
			value = m_netFolderRoot.getAllowDesktopAppToTriggerInitialHomeFolderSync();
			if (value != null) {
				m_allowDesktopAppToTriggerSyncCB.setValue(value);
			}
		}
		
		else {
			// No!  Update the dialog's header to say 'Add Net Folder
			// Root'.
			setCaption(GwtTeaming.getMessages().modifyNetFolderServerDlg_AddHeader());

			// Default to using a proxy name and password.
			checkProxyTypeManual();
			m_proxyIdentityFindControl.clearText();
			m_proxyIdentity = null;
			
			// Enable the 'Name' field.
			m_nameTxtBox.setEnabled(true);
			m_tabPanel.selectTab(0);
		}
		
		initJits();
		danceDlg(false);

		GwtAuthenticationType authType;
		if (netFolderRoot != null) {
			authType = netFolderRoot.getAuthType();
		}
		
		else {
			// Get the selected server type
			NetFolderRootType serverType = getSelectedRootType();
			if (serverType.equals(NetFolderRootType.WINDOWS))
			     authType = GwtAuthenticationType.KERBEROS_THEN_NTLM;
			else authType = GwtAuthenticationType.NMAS;
		}
		
	
		// initAuthType() must be called after danceDlg() because
		// danceDlg() will add/remove items from the authentication
		// type ListBox depending on the selected server type.
		// Select the appropriate authType.
		initAuthType(authType);
	}

	/*
	 * Initialize the authentication type.
	 */
	private void initAuthType(GwtAuthenticationType authType) {
		if (authType != null) {
			GwtClientHelper.selectListboxItemByValue(m_authTypeListbox, authType.name());
		}
	}
	
	/*
	 * Initialize the controls used for the JITS settings.
	 */
	private void initJits() {
		m_jitsEnabledCkbox.setValue( false);
		m_jitsAclMaxAge.setValue(    ""   );
		m_jitsResultsMaxAge.setValue(""   );
		
		if (m_netFolderRoot != null) {
			m_jitsEnabledCkbox.setValue( m_netFolderRoot.getJitsEnabled()                             );
			m_jitsAclMaxAge.setValue(    String.valueOf(m_netFolderRoot.getJitsAclMaxAge()     / 1000));
			m_jitsResultsMaxAge.setValue(String.valueOf(m_netFolderRoot.getJitsResultsMaxAge() / 1000));
		}
		
		else {
			m_jitsEnabledCkbox.setValue(false);
			
			Long value = (GwtMainPage.m_requestInfo.getDefaultJitsAclMaxAge() / 1000);
			m_jitsAclMaxAge.setValue(value.toString());
			
			value = (GwtMainPage.m_requestInfo.getDefaultJitsResultsMaxAge() / 1000);
			m_jitsResultsMaxAge.setValue(value.toString());
		}
	}

	/*
	 * Initialize the server type.
	 */
	private void initServerType(NetFolderRootType serverType) {
		// Does the server types ListBox already have an 'undefined'
		// item?
		int index = GwtClientHelper.doesListboxContainValue(m_rootTypeListbox, NetFolderRootType.FAMT.name());
		if (serverType.equals(NetFolderRootType.FAMT)) {
			// A server type of FAMT means that this net folder server
			// was created before Filr 1.1.  We need the user to select
			// the server type.
			
			// Does the server types ListBox already have an
			// 'undefined' item?
			if (index == (-1)) {
				// No!  Add an Undefined item to the server types
				// ListBox.
				m_rootTypeListbox.addItem(
					GwtTeaming.getMessages().modifyNetFolderServerDlg_Type_Undefined(),
					NetFolderRootType.FAMT.name());
			}
		}
		
		else {
			// Does the server types ListBox have an 'undefined'
			// item?
			if (index != (-1)) {
				// Yes, remove it.
				m_rootTypeListbox.removeItem(index);
			}
		}
		
		GwtClientHelper.selectListboxItemByValue(m_rootTypeListbox, serverType.name());
	}

	/*
	 * Check to see if all the of the information needed to test the
	 * connection has been entered.
	 */
	private boolean isDataValidNeededToTestConnection() {
		NetFolderRootType serverType;
		String value;
		
		serverType = getSelectedRootType();

		// Is the server type 'FAMT' or undefined?
		if (serverType.equals(NetFolderRootType.FAMT)) {
			// Yes, tell the user they need to pick a server type.
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					GwtClientHelper.deferredAlert(GwtTeaming.getMessages().modifyNetFolderServerDlg_SelectServerTypePrompt());
					m_tabPanel.selectTab(0);
				}
			});

			return false;
		}

		// Do we need manual proxy identity settings?
		if (m_proxyTypeManualRB.getValue()) {
			// Yes!  Did the user enter a proxy name?
			value = getProxyName();
			if (value == null || (value.length() == 0)) {
				// No
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						GwtClientHelper.deferredAlert(GwtTeaming.getMessages().modifyNetFolderServerDlg_EnterProxyNamePrompt());
						m_tabPanel.selectTab(1);
						m_proxyNameTxtBox.setFocus(true);
					}
				});
	
				return false;
			}
			
			// Did the user enter a proxy password?
			value = getProxyPwd();
			if (value == null || (value.length() == 0)) {
				// No
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						GwtClientHelper.deferredAlert(GwtTeaming.getMessages().modifyNetFolderServerDlg_EnterProxyPwdPrompt());
						m_tabPanel.selectTab(1);
						m_proxyPwdTxtBox.setFocus(true);
					}
				});
	
				return false;
			}
		}
		
		else {
			// No, we don't we need manual proxy identity settings!  We
			// need a proxy identity.  Was one specified?
			if (null == m_proxyIdentity) {
				// No
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						GwtClientHelper.deferredAlert(GwtTeaming.getMessages().modifyNetFolderServerDlg_SelectProxyIdentityPrompt());
						m_tabPanel.selectTab(1);
						m_proxyIdentityFindControl.setFocus(true);
					}
				});
	
				return false;
			}
		}
	
		// If we get here everything is ok
		return true;
	}
	
	/*
	 * Is the host url entered by the user valid?
	 */
	private boolean isHostUrlValid() {
		String value = m_hostUrlTxtBox.getValue();
		if (value == null || value.length() == 0) {
			GwtClientHelper.deferredAlert(GwtTeaming.getMessages().modifyNetFolderServerDlg_HostUrlRequired());
			return false;
		}
		
		return true;
	}
	
	/*
	 * Is the name entered by the user valid?
	 */
	private boolean isNameValid() {
		String value = m_nameTxtBox.getValue();
		if (value == null || value.length() == 0) {
			GwtClientHelper.deferredAlert(GwtTeaming.getMessages().modifyNetFolderServerDlg_NameRequired());
			return false;
		}
		
		return true;
	}
	
	/*
	 * Issue a GWT RPC request to modify the net folder root.  If the rpc request was successful
	 * close this dialog.
	 */
	private void modifyNetFolderRootAndClose() {
		final NetFolderRoot newNetFolderRoot;
		ModifyNetFolderRootCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback;

		// Create a NetFolderRoot object that holds the information about the net folder root
		newNetFolderRoot = getNetFolderRootFromDlg();
		
		rpcCallback = new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				FlowPanel errorPanel;
				Label label;
				String errMsg;
				
				hideStatusMsg();
				setOkEnabled(true);

				// Get the panel that holds the errors.
				errorPanel = getErrorPanel();
				errorPanel.clear();
				
				errMsg = GwtTeaming.getMessages().modifyNetFolderServerDlg_ErrorModifyingNetFolderServer(caught.toString());
				if (caught instanceof GwtTeamingException) {
					GwtTeamingException ex = ((GwtTeamingException) caught);
					if (ex.getExceptionType().equals(ExceptionType.ACCESS_CONTROL_EXCEPTION)) {
						String desc = GwtTeaming.getMessages().modifyNetFolderServerDlg_InsufficientRights();
						errMsg =GwtTeaming.getMessages().modifyNetFolderServerDlg_ErrorModifyingNetFolderServer(desc);
					}
				}
				label = new Label(errMsg);
				label.addStyleName("dlgErrorLabel");
				errorPanel.add(label);
				
				showErrorPanel();
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						NetFolderRootModifiedEvent event;
						
						// When a 'home directory' net folder is
						// created during the LDAP sync process, a net
						// folder root may have been created without
						// proxy credentials.  Did the user enter the
						// proxy credentials for the first time for a
						// net folder root that already existed?
						if (m_netFolderRoot != null) {
							String origProxyName;
							String origProxyPwd;
							String newProxyName;
							String newProxyPwd;
							
							// Get the original proxy credentials
							origProxyName = m_netFolderRoot.getProxyName();
							origProxyPwd = m_netFolderRoot.getProxyPwd();

							// Get the new proxy credentials
							newProxyName = newNetFolderRoot.getProxyName();
							newProxyPwd = newNetFolderRoot.getProxyPwd();
							
							// Have the proxy credentials changed?
							if ((newProxyName != null && newProxyName.length() > 0 && newProxyName.equalsIgnoreCase(origProxyName) == false) ||
								 (newProxyPwd != null && newProxyPwd.length() > 0 && newProxyPwd.equalsIgnoreCase(origProxyPwd) == false)) {
								// Yes
								// Ask the user if they want to sync all the net folders associated with
								// this net folder root.
								if (Window.confirm(GwtTeaming.getMessages().modifyNetFolderServerDlg_SyncAllNetFoldersPrompt())) {
									// Sync this net folder server by syncing all the net folders
									// associated with this net folder server.
									syncNetFolderServer();
								}
							}
						}
						
						hideStatusMsg();
						setOkEnabled(true);

						// Fire an event that lets everyone know this net folder root was modified.
						event = new NetFolderRootModifiedEvent(newNetFolderRoot);
						GwtTeaming.fireEvent(event);

						// Close this dialog.
						hide();
					}
				});
			}						
		};
		
		showStatusMsg(GwtTeaming.getMessages().modifyNetFolderServerDlg_ModifyingNetFolderServer(), "dlgBox_statusPanel_relative");
		
		// Issue a GWT RPC request to update the net folder root.
		cmd = new ModifyNetFolderRootCmd(newNetFolderRoot); 
		GwtClientHelper.executeCommand(cmd, rpcCallback);
	}

	/**
	 * Called when the dialog is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
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
	public void onDetach() {
		// Let the widget detach and then unregister our event handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/**
	 * Handles SearchFindResultsEvent's received by this class.
	 * 
	 * Implements the SearchFindResultsEvent.Handler.onSearchFindResults()
	 * method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchFindResults(SearchFindResultsEvent event) {
		// If the find results aren't for the limit user visibility
		// dialog...
		if (!(((Widget) event.getSource()).equals(this))) {
			// ...ignore the event.
			return;
		}
		
		// Hide the find widgets.
		m_proxyIdentityFindControl.hideSearchResults();

		// If the search result is a GwtPrincipal, add the appropriate
		// limit user visibility rights to it.
		GwtTeamingItem obj = event.getSearchResults();
		if (obj instanceof GwtProxyIdentity) {
			m_proxyIdentity = ((GwtProxyIdentity) obj);
			checkProxyTypeIdentity();
		}
		else {
			GwtClientHelper.deferredAlert(GwtTeaming.getMessages().modifyNetFolderServerDlg_ErrorInvalidSearchResult());
		}
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we haven't allocated a list to track events we've registered yet...
		if (null == m_mnfrDlg_registeredEventHandlers) {
			// ...allocate one now.
			m_mnfrDlg_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if (m_mnfrDlg_registeredEventHandlers.isEmpty()) {
			// ...register the events.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				mnfrDlg_REGISTERED_EVENTS,
				this,
				m_mnfrDlg_registeredEventHandlers);
		}
	}
	
	/*
	 * Issue a GWT RPC request to sync this net folder server by
	 * sync'ing all the list of net folders associated with this net
	 * folder server.
	 */
	private void syncNetFolderServer() {
		// Issue GWT RPC request to sync the net folder server.
		HashSet<NetFolderRoot> toBeSyncd = new HashSet<NetFolderRoot>();
		toBeSyncd.add(m_netFolderRoot);
		SyncNetFolderServerCmd cmd = new SyncNetFolderServerCmd(toBeSyncd);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(final Throwable t) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						GwtClientHelper.handleGwtRPCFailure(
							t,
							GwtTeaming.getMessages().rpcFailure_SyncNetFolderServer());
					}
				});
			}
	
			@Override
			public void onSuccess(final VibeRpcResponse response) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						// Tell the user the synchronization of the net folder server has started.
						//~JW:  GwtClientHelper.deferredAlert(GwtTeaming.getMessages().modifyNetFolderServerDlg_SyncOfNetFolderServerStarted());
					}
				});
			}
		});
	}
	
	/*
	 * Test the connection to the server to see if the information they
	 * have entered is valid.
	 */
	private void testConnection() {
		// Is there a 'test connection' request currently running?
		if (m_inProgressPanel.isVisible()) {
			// Yes!  Bail!
			return;
		}
		
		// Is the data needed to test the connection valid?
		if (!(isDataValidNeededToTestConnection())) {
			// No, the user has already been told what to do.
			return;
		}

		// Show the 'in progress' panel since we'll now be starting
		// a connection test.
		m_inProgressPanel.setVisible(true);
		
		// Issue a GWT RPC request to test the Net Folder Root
		// connection.
		TestNetFolderConnectionCmd cmd;
		NetFolderRoot netFolderRoot = getNetFolderRootFromDlg();
		if (netFolderRoot.getUseProxyIdentity())
		     cmd = new TestNetFolderConnectionCmd(netFolderRoot.getName(), netFolderRoot.getRootType(), netFolderRoot.getRootPath(), "", netFolderRoot.getProxyIdentity()                         ); 
		else cmd = new TestNetFolderConnectionCmd(netFolderRoot.getName(), netFolderRoot.getRootType(), netFolderRoot.getRootPath(), "", netFolderRoot.getProxyName(), netFolderRoot.getProxyPwd()); 
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				m_inProgressPanel.setVisible(false);
				String errMsg = GwtTeaming.getMessages().rpcFailure_ErrorTestingNetFolderServerConnection();
				GwtClientHelper.deferredAlert(errMsg);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				TestNetFolderConnectionResponse response = ((TestNetFolderConnectionResponse) result.getResponseData());
				String msg;
				switch (response.getStatusCode()) {
				case NETWORK_ERROR:            msg = GwtTeaming.getMessages().testConnection_NetworkError();          break;
				case NORMAL:                   msg = GwtTeaming.getMessages().testConnection_Normal();                break;
				case PROXY_CREDENTIALS_ERROR:  msg = GwtTeaming.getMessages().testConnection_ProxyCredentialsError(); break;
				
				default:
				case UNKNOWN:
					msg = GwtTeaming.getMessages().testConnection_UnknownStatus();
					break;
				}
				
				m_inProgressPanel.setVisible(false);
				GwtClientHelper.deferredAlert(msg);
			}						
		});
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if ((null != m_mnfrDlg_registeredEventHandlers) && (! (m_mnfrDlg_registeredEventHandlers.isEmpty()))) {
			// ...unregister them.  (Note that this will also empty the list.)
			EventHelper.unregisterEventHandlers(m_mnfrDlg_registeredEventHandlers);
		}
	}

	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the modify net folder root dialog and perform some operation  */
	/* on it.                                                        */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the 'modify net folder root'
	 * dialog asynchronously after it loads. 
	 */
	public interface ModifyNetFolderRootDlgClient {
		void onSuccess(ModifyNetFolderRootDlg mnfrDlg);
		void onUnavailable();
	}

	/**
	 * Loads the ModifyNetFolderRootDlg split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param left
	 * @param top
	 * @param mnfrDlgClient
	 */
	public static void createAsync(
			final boolean						autoHide,
			final boolean						modal,
			final int							left,
			final int							top,
			final ModifyNetFolderRootDlgClient	mnfrDlgClient) {
		GWT.runAsync(ModifyNetFolderRootDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_ModifyNetFolderServerDlg());
				if (mnfrDlgClient != null) {
					mnfrDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				new ModifyNetFolderRootDlg(
					autoHide,
					modal,
					left,
					top,
					mnfrDlgClient);
			}
		});
	}
}
