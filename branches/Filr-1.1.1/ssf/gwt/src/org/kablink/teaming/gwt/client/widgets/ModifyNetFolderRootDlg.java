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
import java.util.HashSet;
import java.util.List;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtPrincipal;
import org.kablink.teaming.gwt.client.GwtSchedule;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.NetFolderRoot;
import org.kablink.teaming.gwt.client.NetFolderRoot.GwtAuthenticationType;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.NetFolderRootCreatedEvent;
import org.kablink.teaming.gwt.client.event.NetFolderRootModifiedEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapObject;
import org.kablink.teaming.gwt.client.rpc.shared.CreateNetFolderRootCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ModifyNetFolderRootCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SyncNetFolderServerCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TestNetFolderConnectionCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TestNetFolderConnectionResponse;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.LdapBrowserDlg.LdapBrowseListCallback;
import org.kablink.teaming.gwt.client.widgets.LdapBrowserDlg.LdapBrowserDlgClient;
import org.kablink.teaming.gwt.client.widgets.SelectPrincipalsWidget.SelectPrincipalsWidgetClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
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
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This dialog can be used to add a net folder root or modify a net folder root.
 * 
 * @author jwootton
 */
public class ModifyNetFolderRootDlg extends DlgBox
	implements
		EditCanceledHandler,
		EditSuccessfulHandler
{
	private Button m_browseProxyDnBtn;	// LDAP browse button next to m_proxyNameTxtBox.
	private NetFolderRoot m_netFolderRoot;	// If we are modifying a net folder this is the net folder.
	private TextBox m_nameTxtBox;
	private ListBox m_rootTypeListbox;
	private FlowPanel m_serverPathHintPanel;
	private TextBox m_rootPathTxtBox;
	private TextBox m_proxyNameTxtBox;
	private PasswordTextBox m_proxyPwdTxtBox;
	private InlineLabel m_authTypeLabel;
	private ListBox m_authTypeListbox;
	private FlowPanel m_webDavSpacerPanel;
	private InlineLabel m_hostUrlLabel;
	private TextBox m_hostUrlTxtBox;
	private CheckBox m_allowSelfSignedCertsCkbox;
	private CheckBox m_isSharePointServerCkbox;
	private SelectPrincipalsWidget m_selectPrincipalsWidget;
	private ScheduleWidget m_scheduleWidget;
	private FlowPanel m_inProgressPanel;
	private List<HandlerRegistration> m_registeredEventHandlers;
	private CheckBox m_fullSyncDirOnlyCB = null;
	private Label m_oesProxyNameHint1;
	private Label m_oesProxyNameHint;
	private Label m_windowsProxyNameHint;
	private TabPanel m_tabPanel;
	private LdapBrowserDlg m_ldapBrowserDlg;
	private CheckBox m_indexContentCB;
	private CheckBox m_jitsEnabledCkbox;
	private TextBox m_jitsResultsMaxAge;
	private TextBox m_jitsAclMaxAge;
	private CheckBox m_allowDesktopAppToTriggerSyncCB;
	
	private List<LdapBrowseSpec> m_ldapServerList;	// List of LDAP servers obtained the first time m_browseProxyDnBtn is clicked.
	
	private static boolean m_showPrivilegedUsersUI = false;
	private static boolean m_showNetFolderServerType = true;
	private static boolean m_showWebDavControls = false;

	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[]
    {
	};

	
	/**
	 * Callback interface to interact with the "modify net folder root" dialog
	 * asynchronously after it loads. 
	 */
	public interface ModifyNetFolderRootDlgClient
	{
		void onSuccess( ModifyNetFolderRootDlg mnfrDlg );
		void onUnavailable();
	}


	/**
	 * 
	 */
	public enum NetFolderRootType implements IsSerializable
	{
		WINDOWS,
		CLOUD_FOLDERS,
		FAMT,
		FILE_SYSTEM,
		NETWARE,
		OES,
		SHARE_POINT_2010,
		SHARE_POINT_2013,
		WEB_DAV,
		UNKNOWN;

		public static NetFolderRootType getType( String type )
		{
			if ( type == null )
				return NetFolderRootType.FAMT;
			
			if ( type.equalsIgnoreCase( NetFolderRootType.WINDOWS.toString() ) )
				return NetFolderRootType.WINDOWS;
			
			if ( type.equalsIgnoreCase( NetFolderRootType.CLOUD_FOLDERS.toString() ) )
				return NetFolderRootType.CLOUD_FOLDERS;
			
			if ( type.equalsIgnoreCase( NetFolderRootType.FAMT.toString() ) )
				return NetFolderRootType.FAMT;
			
			if ( type.equalsIgnoreCase( NetFolderRootType.FILE_SYSTEM.toString() ) )
				return NetFolderRootType.FILE_SYSTEM;
			
			if ( type.equalsIgnoreCase( NetFolderRootType.NETWARE.toString() ) )
				return NetFolderRootType.NETWARE;

			if ( type.equalsIgnoreCase( NetFolderRootType.OES.toString() ) )
				return NetFolderRootType.OES;

			if ( type.equalsIgnoreCase( NetFolderRootType.SHARE_POINT_2010.toString() ) )
				return NetFolderRootType.SHARE_POINT_2010;
			
			if ( type.equalsIgnoreCase( NetFolderRootType.SHARE_POINT_2013.toString() ) )
				return NetFolderRootType.SHARE_POINT_2013;
			
			if ( type.equalsIgnoreCase( NetFolderRootType.WEB_DAV.toString() ) )
				return NetFolderRootType.WEB_DAV;
			
			return NetFolderRootType.UNKNOWN;
		}
	}
	
	/**
	 * 
	 */
	private ModifyNetFolderRootDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( "", this, this, null ); 
	}

	/**
	 * Create the panel that holds the authentication controls.
	 */
	private Panel createAuthenticationPanel()
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel;
		final FlexTable table;
		Label label;
		int nextRow;
		FlexCellFormatter cellFormatter;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();

		// Create a table to hold the controls.
		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );
		
		mainPanel.add( table );
		
		cellFormatter = table.getFlexCellFormatter();
		
		nextRow = 0;
		
		// Create the controls used to enter proxy information
		{
			// Add some instructions on the format that should be used when entering the proxy name
			{
				FlowPanel panel;
				
				panel = new FlowPanel();
				panel.addStyleName( "margintop1" );
				panel.addStyleName( "modifyNetFolderServerDlg_ProxyNameHint" );

				// Add a hint that describes the unc syntax
				m_oesProxyNameHint1 = new Label( messages.modifyNetFolderServerDlg_ProxyNameHint1() );
				panel.add( m_oesProxyNameHint1 );
				m_oesProxyNameHint = new Label( messages.modifyNetFolderServerDlg_ProxyNameHint2() );
				panel.add( m_oesProxyNameHint );
				m_windowsProxyNameHint = new Label( messages.modifyNetFolderServerDlg_ProxyNameHint3() );
				panel.add( m_windowsProxyNameHint );
				
				cellFormatter.setColSpan( nextRow, 0, 2 );
				table.setWidget( nextRow, 0, panel );
				++nextRow;
			}
			
			label = new InlineLabel( messages.modifyNetFolderServerDlg_ProxyNameLabel() );
			table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
			
			FlowPanel tmpPanel = new FlowPanel();
			m_proxyNameTxtBox = new TextBox();
			m_proxyNameTxtBox.setVisibleLength( 30 );
			tmpPanel.add(m_proxyNameTxtBox);
			Image btnImg = GwtClientHelper.buildImage( GwtTeaming.getImageBundle().browseLdap().getSafeUri().asString() );
			btnImg.setTitle( messages.modifyNetFolderServerDlg_ProxyName_Alt() );
			FlowPanel html = new FlowPanel();
			html.add( btnImg );
			m_browseProxyDnBtn = new Button( html.getElement().getInnerHTML() );
			m_browseProxyDnBtn.addStyleName( "modifyNetFolderServerDlg_BrowseProxyDN" );
			m_browseProxyDnBtn.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					browseLdapForProxyNameAsync();
				}
			} );
			tmpPanel.add( m_browseProxyDnBtn );
			table.setWidget( nextRow, 1, tmpPanel );
			++nextRow;
			
			label = new InlineLabel( messages.modifyNetFolderServerDlg_ProxyPwdLabel() );
			table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
			
			m_proxyPwdTxtBox = new PasswordTextBox();
			m_proxyPwdTxtBox.setVisibleLength( 30 );
			table.setWidget( nextRow, 1, m_proxyPwdTxtBox );
			++nextRow;
			
			// Add a "test connection" button
			{
				Button testConnectionBtn;
				
				// Add "Test connection" button
				testConnectionBtn = new Button( messages.modifyNetFolderServerDlg_TestConnectionLabel() );
				testConnectionBtn.addStyleName( "teamingButton" );
				testConnectionBtn.addClickHandler( new ClickHandler()
				{
					/**
					 * 
					 */
					@Override
					public void onClick( ClickEvent event )
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
								testConnection();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
					
				} );
				
				table.setWidget( nextRow, 0, testConnectionBtn );

				// Add a panel that will display "Testing connection..." message
				{
					ImageResource imgResource;
					Image img;
					
					m_inProgressPanel = new FlowPanel();
					m_inProgressPanel.addStyleName( "testConnection_InProgress" );
					m_inProgressPanel.setVisible( false );

					imgResource = GwtTeaming.getImageBundle().spinner16();
					img = new Image( imgResource );
					img.getElement().setAttribute( "align", "absmiddle" );
					m_inProgressPanel.add( img );

					label = new InlineLabel( messages.testConnection_InProgressLabel() );
					m_inProgressPanel.add( label );
					
					table.setWidget( nextRow, 1, m_inProgressPanel );
				}
				
				++nextRow;
			}
		}
		
		// Add controls for the authentication type
		{
			FlowPanel spacerPanel;
			
			spacerPanel = new FlowPanel();
			spacerPanel.getElement().getStyle().setMarginTop( 16, Unit.PX );
			table.setWidget( nextRow, 0, spacerPanel );
			++nextRow;
			
			m_authTypeLabel = new InlineLabel( messages.modifyNetFolderServerDlg_AuthTypeLabel() );
			table.setWidget( nextRow, 0, m_authTypeLabel );

			// Add the listbox where the user can select the authentication
			m_authTypeListbox = new ListBox( false );
			m_authTypeListbox.setVisibleItemCount( 1 );
			
			m_authTypeListbox.addItem(
									messages.modifyNetFolderServerDlg_AuthType_Kerberos(),
									GwtAuthenticationType.KERBEROS.toString() );
		
			m_authTypeListbox.addItem(
									messages.modifyNetFolderServerDlg_AuthType_Ntlm(),
									GwtAuthenticationType.NTLM.toString() );

			m_authTypeListbox.addItem(
									messages.modifyNetFolderServerDlg_AuthType_NMAS(),
									GwtAuthenticationType.NMAS.toString() );

			m_authTypeListbox.addItem(
									messages.modifyNetFolderServerDlg_AuthType_KerberosThenNtlm(),
									GwtAuthenticationType.KERBEROS_THEN_NTLM.toString() );

			m_authTypeListbox.setSelectedIndex( 0 );

			table.setWidget( nextRow, 1, m_authTypeListbox );
			++nextRow;
		}

		return mainPanel;
	}
	
	/**
	 * Create the panel that holds the configuration controls.
	 */
	private Panel createConfigPanel()
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel;
		final FlexTable table;
		FlowPanel spacerPanel;
		Label label;
		int nextRow;
		FlexCellFormatter cellFormatter;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();

		// Create a table to hold the controls.
		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );
		
		mainPanel.add( table );
		
		cellFormatter = table.getFlexCellFormatter();
		
		nextRow = 0;
		
		// Create the controls for "Name"
		{
			label = new InlineLabel( messages.modifyNetFolderServerDlg_NameLabel() );
			table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
			
			m_nameTxtBox = new TextBox();
			m_nameTxtBox.setVisibleLength( 30 );
			table.setWidget( nextRow, 1, m_nameTxtBox );
			++nextRow;
		}
		
		// Create a select control for selecting the type of net folder root
		if ( m_showNetFolderServerType )
		{
			label = new InlineLabel( messages.modifyNetFolderServerDlg_TypeLabel() );
			table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
			
			// Add the listbox where the user can select the type of net folder root
			m_rootTypeListbox = new ListBox( false );
			m_rootTypeListbox.setVisibleItemCount( 1 );
			
			m_rootTypeListbox.addItem(
					GwtTeaming.getMessages().modifyNetFolderServerDlg_Type_Windows(),
					NetFolderRootType.WINDOWS.toString() );
		
			m_rootTypeListbox.addItem(
						GwtTeaming.getMessages().modifyNetFolderServerDlg_Type_OES(),
						NetFolderRootType.OES.toString() );
			
			m_rootTypeListbox.addItem(
					GwtTeaming.getMessages().modifyNetFolderServerDlg_Type_Netware(),
					NetFolderRootType.NETWARE.toString() );

			if ( GwtMainPage.m_requestInfo.getAllowSharePointAsAServerType() == true )
			{
				if ( GwtMainPage.m_requestInfo.getAllowSharePoint2010AsAServerType() == true )
				{
					m_rootTypeListbox.addItem(
							GwtTeaming.getMessages().modifyNetFolderServerDlg_Type_SharePoint2010(),
							NetFolderRootType.SHARE_POINT_2010.toString() );
				}
			
				if ( GwtMainPage.m_requestInfo.getAllowSharePoint2013AsAServerType() == true )
				{
					m_rootTypeListbox.addItem(
							GwtTeaming.getMessages().modifyNetFolderServerDlg_Type_SharePoint2013(),
							NetFolderRootType.SHARE_POINT_2013.toString() );
				}
			}
			
			m_rootTypeListbox.setSelectedIndex( 0 );

			m_rootTypeListbox.addChangeHandler( new ChangeHandler()
			{
				@Override
				public void onChange( ChangeEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							handleRootTypeSelected();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			table.setWidget( nextRow, 1, m_rootTypeListbox );
			++nextRow;
		}

		// Create the controls for "root path"
		{
			m_serverPathHintPanel = new FlowPanel();
			m_serverPathHintPanel.addStyleName( "margintop1" );
			m_serverPathHintPanel.addStyleName( "modifyNetFolderServerDlg_ServerPathHint" );

			// Add a hint that describes the unc syntax
			label = new Label( messages.modifyNetFolderServerDlg_ServerPathHint1() );
			m_serverPathHintPanel.add( label );
			
			cellFormatter.setColSpan( nextRow, 0, 2 );
			table.setWidget( nextRow, 0, m_serverPathHintPanel );
			++nextRow;
			
			label = new InlineLabel( messages.modifyNetFolderServerDlg_ServerPathLabel() );
			table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
			
			m_rootPathTxtBox = new TextBox();
			m_rootPathTxtBox.setVisibleLength( 50 );
			table.setWidget( nextRow, 1, m_rootPathTxtBox );
			++nextRow;
		}
		
		// Create the WebDAV specific controls
		if ( m_showWebDavControls )
		{
			// Add some space
			m_webDavSpacerPanel = new FlowPanel();
			m_webDavSpacerPanel.getElement().getStyle().setMarginTop( 10, Unit.PX );
			m_webDavSpacerPanel.setVisible( false );
			table.setWidget( nextRow, 0, m_webDavSpacerPanel );
			++nextRow;
			
			m_hostUrlLabel = new InlineLabel( messages.modifyNetFolderServerDlg_HostUrlLabel() );
			m_hostUrlLabel.setVisible( false );
			table.setWidget( nextRow, 0, m_hostUrlLabel );
			
			m_hostUrlTxtBox = new TextBox();
			m_hostUrlTxtBox.setVisibleLength( 50 );
			m_hostUrlTxtBox.setVisible( false );
			table.setWidget( nextRow, 1, m_hostUrlTxtBox );
			++nextRow;
			
			cellFormatter.setColSpan( nextRow, 0, 2 );
			m_allowSelfSignedCertsCkbox = new CheckBox( messages.modifyNetFolderServerDlg_AllowSelfSignedCertsLabel() );
			m_allowSelfSignedCertsCkbox.setVisible( false );
			table.setWidget( nextRow, 0, m_allowSelfSignedCertsCkbox );
			++nextRow;

			cellFormatter.setColSpan( nextRow, 0, 2 );
			m_isSharePointServerCkbox = new CheckBox( messages.modifyNetFolderServerDlg_IsSharePointServerLabel() );
			m_isSharePointServerCkbox.setVisible( false );
			table.setWidget( nextRow, 0, m_isSharePointServerCkbox );
			++nextRow;
		}
		
		// Create the controls used to select who can create net folders using this
		// net folder root.
		if ( m_showPrivilegedUsersUI )
		{
			final int selectPrincipalsWidgetRow;
			
			// Add some space
			spacerPanel = new FlowPanel();
			spacerPanel.getElement().getStyle().setMarginTop( 16, Unit.PX );
			table.setWidget( nextRow, 0, spacerPanel );
			++nextRow;
			
			// Add a hint
			cellFormatter.setColSpan( nextRow, 0, 2 );
			cellFormatter.setWordWrap( nextRow, 0, false );
			cellFormatter.addStyleName( nextRow, 0, "modifyNetFolderRootDlg_SelectPrivelegedUsersHint" );
			label = new InlineLabel( messages.modifyNetFolderServerDlg_PrivilegedPrincipalsHint() );
			table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
			++nextRow;
			
			cellFormatter.setColSpan( nextRow, 0, 2 );
			selectPrincipalsWidgetRow = nextRow;
			++nextRow;
			
			// Create a widget that lets the user select users and groups.
			SelectPrincipalsWidget.createAsync( new SelectPrincipalsWidgetClient() 
			{
				@Override
				public void onUnavailable() 
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( SelectPrincipalsWidget widget )
				{
					m_selectPrincipalsWidget = widget;
					table.setWidget( selectPrincipalsWidgetRow, 0, m_selectPrincipalsWidget );
				}
			} );
		}

		return mainPanel;
	}
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		m_tabPanel = new TabPanel();
		m_tabPanel.addStyleName( "vibe-tabPanel" );

		mainPanel.add( m_tabPanel );

		// Create the panel that holds the basic net folder server configuration
		{
			Panel configPanel;
			
			configPanel = createConfigPanel();
			m_tabPanel.add( configPanel, messages.modifyNetFolderServerDlg_ConfigTab() );
		}
		
		// Create the panel that holds the authentication information
		{
			Panel authPanel;
			
			authPanel = createAuthenticationPanel();
			m_tabPanel.add( authPanel, messages.modifyNetFolderServerDlg_AuthenticationTab() );
		}
		
		// Create the panel that holds the controls for the schedule
		{
			Panel schedPanel;
			
			schedPanel = createSchedulePanel();
			m_tabPanel.add( schedPanel, messages.modifyNetFolderServerDlg_ScheduleTab() );
		}
		
		// Create the panel that holds the controls for data synch
		{
			Panel syncPanel;
			
			syncPanel = createSyncPanel();
			m_tabPanel.add( syncPanel, messages.modifyNetFolderServerDlg_SyncTab() );
		}
		
		m_tabPanel.selectTab( 0 );

		return mainPanel;
	}
	
	/**
	 * Create the panel that holds the sync schedule controls.
	 */
	private Panel createSchedulePanel()
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		
		m_scheduleWidget = new ScheduleWidget( messages.modifyNetFolderDlg_EnableSyncScheduleLabel() );
		m_scheduleWidget.addStyleName( "modifyNetFolderServerDlg_ScheduleWidget" );
		mainPanel.add( m_scheduleWidget );

		return mainPanel;
	}
	
	/**
	 * Create the panel that holds the sync controls.
	 */
	private Panel createSyncPanel()
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel;
		FlowPanel tmpPanel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		
		tmpPanel = new FlowPanel();
		m_indexContentCB = new CheckBox( messages.modifyNetFolderServerDlg_IndexContentCB() );
		tmpPanel.add( m_indexContentCB );
		mainPanel.add( tmpPanel );
		
		// Add the controls needed to define Jits settings
		{
			FlowPanel jitsPanel;

			jitsPanel = new FlowPanel();
			mainPanel.add( jitsPanel );
			
			m_jitsEnabledCkbox = new CheckBox( messages.modifyNetFolderDlg_EnableJitsLabel() );
			tmpPanel = new FlowPanel();
			tmpPanel.add( m_jitsEnabledCkbox );
			jitsPanel.add( tmpPanel );
			
			// Add a panel that holds all the max age controls.
			{
				FlowPanel maxAgePanel;
				
				maxAgePanel = new FlowPanel();
				maxAgePanel.addStyleName( "marginleft3" );
				
				// Add the controls for "results max age"
				{
					HorizontalPanel hPanel;
					Label intervalLabel;

					hPanel = new HorizontalPanel();
					hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
					hPanel.setSpacing( 0 );
					
					intervalLabel = new Label( messages.modifyNetFolderDlg_JitsResultsMaxAgeLabel() );
					hPanel.add( intervalLabel );
					
					m_jitsResultsMaxAge = new TextBox();
					m_jitsResultsMaxAge.addKeyPressHandler( new KeyPressHandler()
					{
						@Override
						public void onKeyPress( KeyPressEvent event )
						{
					        int keyCode;

					        // Get the key the user pressed
					        keyCode = event.getNativeEvent().getKeyCode();
					        
					        if ( GwtClientHelper.isKeyValidForNumericField( event.getCharCode(), keyCode ) == false )
					        {
				        		// Suppress the current keyboard event.
					        	m_jitsResultsMaxAge.cancelKey();
					        }
						}
					} );
					m_jitsResultsMaxAge.setVisibleLength( 3 );
					hPanel.add( m_jitsResultsMaxAge );
					
					intervalLabel = new Label( messages.netFolderGlobalSettingsDlg_SecondsLabel() );
					intervalLabel.addStyleName( "marginleft2px" );
					intervalLabel.addStyleName( "gray3" );
					hPanel.add( intervalLabel );

					maxAgePanel.add( hPanel );
				}

				// Add the controls for "acl max age"
				{
					HorizontalPanel hPanel;
					Label intervalLabel;

					hPanel = new HorizontalPanel();
					hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
					hPanel.setSpacing( 0 );
					
					intervalLabel = new Label( messages.modifyNetFolderDlg_JitsAclMaxAgeLabel() );
					hPanel.add( intervalLabel );
					
					m_jitsAclMaxAge = new TextBox();
					m_jitsAclMaxAge.addKeyPressHandler( new KeyPressHandler()
					{
						@Override
						public void onKeyPress( KeyPressEvent event )
						{
					        int keyCode;

					        // Get the key the user pressed
					        keyCode = event.getNativeEvent().getKeyCode();
					        
					        if ( GwtClientHelper.isKeyValidForNumericField( event.getCharCode(), keyCode ) == false )
					        {
				        		// Suppress the current keyboard event.
					        	m_jitsAclMaxAge.cancelKey();
					        }
						}
					} );
					m_jitsAclMaxAge.setVisibleLength( 3 );
					hPanel.add( m_jitsAclMaxAge );
					
					intervalLabel = new Label( messages.netFolderGlobalSettingsDlg_SecondsLabel() );
					intervalLabel.addStyleName( "marginleft2px" );
					intervalLabel.addStyleName( "gray3" );
					hPanel.add( intervalLabel );

					maxAgePanel.add( hPanel );
				}

				jitsPanel.add( maxAgePanel );
			}
			
			mainPanel.add( jitsPanel );
		}
		
		// Add the control for "allow desktop app to trigger initial home folder sync
		{
			tmpPanel = new FlowPanel();
			
			m_allowDesktopAppToTriggerSyncCB = new CheckBox( messages.modifyNetFolderServerDlg_AllowDesktopAppToTriggerSync() );
			
			tmpPanel.add( m_allowDesktopAppToTriggerSyncCB );
			mainPanel.add( tmpPanel );
		}
		
		if ( GwtMainPage.m_requestInfo.getShowSyncOnlyDirStructureUI() )
		{
			tmpPanel = new FlowPanel();
			m_fullSyncDirOnlyCB = new CheckBox( messages.modifyNetFolderServerDlg_SyncOnlyDirStructureCB() );
			tmpPanel.add( m_fullSyncDirOnlyCB );
			mainPanel.add( tmpPanel );
		}
		
		return mainPanel;
	}
	
	/**
	 * Issue an rpc request to create a net folder root.  If the rpc request is successful
	 * close this dialog.
	 */
	private void createNetFolderRootAndClose()
	{
		CreateNetFolderRootCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback;
		
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				FlowPanel errorPanel;
				Label label;
				String errMsg;
				
				hideStatusMsg();
				setOkEnabled( true );

				// Get the panel that holds the errors.
				errorPanel = getErrorPanel();
				errorPanel.clear();
				
				errMsg = GwtTeaming.getMessages().modifyNetFolderServerDlg_ErrorCreatingNetFolderServer( caught.toString() );
				if ( caught instanceof GwtTeamingException )
				{
					GwtTeamingException ex;
					
					ex = (GwtTeamingException) caught;
					if ( ex.getExceptionType() == ExceptionType.NET_FOLDER_ROOT_ALREADY_EXISTS )
					{
						String desc;
						
						desc = GwtTeaming.getMessages().modifyNetFolderServerDlg_ServerAlreadyExists();
						errMsg =GwtTeaming.getMessages().modifyNetFolderServerDlg_ErrorModifyingNetFolderServer( desc );
					}
				}
				label = new Label( errMsg );
				label.addStyleName( "dlgErrorLabel" );
				errorPanel.add( label );
				
				showErrorPanel();
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				NetFolderRootCreatedEvent event;
				NetFolderRoot netFolderRoot;
				
				netFolderRoot = (NetFolderRoot) result.getResponseData();
				
				hideStatusMsg();
				setOkEnabled( true );

				// Fire an event that lets everyone know a net folder root was created.
				event = new NetFolderRootCreatedEvent( netFolderRoot );
				GwtTeaming.fireEvent( event );

				// Close this dialog.
				hide();
			}						
		};
		
		// Issue an rpc request to create the net folder root.
		{
			NetFolderRoot netFolderRoot;
			
			netFolderRoot = getNetFolderRootFromDlg();
			
			showStatusMsg( GwtTeaming.getMessages().modifyNetFolderServerDlg_CreatingNetFolderServer() );
			
			cmd = new CreateNetFolderRootCmd( netFolderRoot );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}

	/**
	 * Show/hide the appropriate controls based on the selected root type.
	 */
	private void danceDlg( boolean setFocus )
	{
		int selectedIndex;
		NetFolderRootType type = NetFolderRootType.WINDOWS;
		
		if ( m_rootTypeListbox != null )
		{
			selectedIndex = m_rootTypeListbox.getSelectedIndex();
			if ( selectedIndex >= 0 )
			{
				boolean visible;
				
				// Get the selected root type;
				type = getSelectedRootType();
	
				visible = false;
				if ( type == NetFolderRootType.WEB_DAV )
					visible = true;
				
				// Show/hide the controls that are WebDAV specific
				if ( m_webDavSpacerPanel != null )
					m_webDavSpacerPanel.setVisible( visible );
				
				if ( m_hostUrlLabel != null )
					m_hostUrlLabel.setVisible( visible );
				
				if ( m_hostUrlTxtBox != null )
					m_hostUrlTxtBox.setVisible( visible );
				
				if ( m_allowSelfSignedCertsCkbox != null )
					m_allowSelfSignedCertsCkbox.setVisible( visible );
				
				if ( m_isSharePointServerCkbox != null )
					m_isSharePointServerCkbox.setVisible( visible );
				
				// Do the following work:
				//	- Update the server path and proxy name hint
				//	- show/hide controls.
				//	- Update the options in the authentication type listbox.
				{
					GwtTeamingMessages messages;
					Label label;
					
					m_serverPathHintPanel.clear();
					
					messages = GwtTeaming.getMessages();
					
					switch( type )
					{
					case OES:
					case NETWARE:
						label = new Label( messages.modifyNetFolderServerDlg_ServerPathHint1() );
						m_serverPathHintPanel.add( label );

						label = new Label( messages.modifyNetFolderServerDlg_ServerPathOESHint() );
						m_serverPathHintPanel.add( label );
						
						m_windowsProxyNameHint.setVisible( false );
						m_oesProxyNameHint1.setVisible( true );
						m_oesProxyNameHint.setVisible( true );
						
						m_authTypeLabel.setVisible( true );
						m_authTypeListbox.setVisible( true );
						
						// Remove NTLM, Kerberos and "auto detect" and add "nmas" to the authentication type listbox
						{
							int index;
							
							// Is NTLM in the listbox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.NTLM.toString() );
							if ( index != -1 )
							{
								// Yes, remove it.
								m_authTypeListbox.removeItem( index );
							}
							
							// Is Kerberos in the listbox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.KERBEROS.toString() );
							if ( index != -1 )
							{
								// Yes, remove it.
								m_authTypeListbox.removeItem( index );
							}
							
							// Is "auto detect" in the listbox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.KERBEROS_THEN_NTLM.toString() );
							if ( index != -1 )
							{
								// Yes, remove it.
								m_authTypeListbox.removeItem( index );
							}
							
							// Is "NMAS" in the listbox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.NMAS.toString() );
							if ( index == -1 )
							{
								// No, add it
								m_authTypeListbox.insertItem(
														messages.modifyNetFolderServerDlg_AuthType_NMAS(),
														GwtAuthenticationType.NMAS.toString(),
														1 );
							}
						}
						break;
						
					case WINDOWS:
						label = new Label( messages.modifyNetFolderServerDlg_ServerPathHint1() );
						m_serverPathHintPanel.add( label );

						label = new Label( messages.modifyNetFolderServerDlg_ServerPathWindowsHint() );
						m_serverPathHintPanel.add( label );
						
						m_windowsProxyNameHint.setVisible( true );
						m_oesProxyNameHint1.setVisible( false );
						m_oesProxyNameHint.setVisible( false );
						m_authTypeLabel.setVisible( true );
						m_authTypeListbox.setVisible( true );
						
						// Remove "nmas" and add "ntlm", "kerberos" and "auto detect" to the authentication type listbox
						{
							int index;
							
							// Is NTLM in the listbox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.NTLM.toString() );
							if ( index == -1 )
							{
								// No, add it
								m_authTypeListbox.insertItem(
														messages.modifyNetFolderServerDlg_AuthType_Ntlm(),
														GwtAuthenticationType.NTLM.toString(),
														0 );
							}
							
							// Is Kerberos in the listbox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.KERBEROS.toString() );
							if ( index == -1 )
							{
								// No, add it
								m_authTypeListbox.insertItem(
														messages.modifyNetFolderServerDlg_AuthType_Kerberos(),
														GwtAuthenticationType.KERBEROS.toString(),
														0 );
							}
							
							// Is "auto detect" in the listbox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.KERBEROS_THEN_NTLM.toString() );
							if ( index == -1 )
							{
								// No, add it.
								m_authTypeListbox.insertItem(
														messages.modifyNetFolderServerDlg_AuthType_KerberosThenNtlm(),
														GwtAuthenticationType.KERBEROS_THEN_NTLM.toString(),
														2 );
							}
							
							// Is "NMAS" in the listbox?
							index = GwtClientHelper.doesListboxContainValue(
																		m_authTypeListbox,
																		GwtAuthenticationType.NMAS.toString() );
							if ( index != -1 )
							{
								// Yes, remove it
								m_authTypeListbox.removeItem( index );
							}
						}
						break;
						
					case FAMT:
						m_windowsProxyNameHint.setVisible( false );
						m_oesProxyNameHint1.setVisible( false );
						m_oesProxyNameHint.setVisible( false );
						m_authTypeLabel.setVisible( false );
						m_authTypeListbox.setVisible( false );
						break;
						
					case SHARE_POINT_2010:
					case SHARE_POINT_2013:
						label = new Label( messages.modifyNetFolderServerDlg_SharePointPathHint() );
						m_serverPathHintPanel.add( label );
						
						m_windowsProxyNameHint.setVisible( true );
						m_oesProxyNameHint1.setVisible( false );
						m_oesProxyNameHint.setVisible( false );
						m_authTypeLabel.setVisible( false );
						m_authTypeListbox.setVisible( false );
						break;
						
					default:
						break;
					}
				}
			}
		}
		
		// Is the server type Windows?
		if ( type == NetFolderRootType.WINDOWS )
		{
			// Yes
			GwtClientHelper.selectListboxItemByValue( m_authTypeListbox, GwtAuthenticationType.KERBEROS_THEN_NTLM.toString() );
		}
		else
		{
			// No, select the first one.
			m_authTypeListbox.setItemSelected( 0, true );
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
	public boolean editCanceled()
	{
		if ( m_showPrivilegedUsersUI )
		{
			m_selectPrincipalsWidget.closePopups();
		}
		
		// Simply return true to allow the dialog to close.
		return true;
	}

	/**
	 * This gets called when the user presses ok.  If we are editing an existing net folder root
	 * we will issue an rpc request to save the net folder root and then throw a "net folder root modified"
	 * event.
	 * If we are creating a new net folder root we will issue an rpc request to create the new net folder root
	 * and then throw a "net folder root created" event.
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		NetFolderRootType serverType;
		
		// Is the root type WebDAV?
		serverType = getSelectedRootType();
		if ( serverType == NetFolderRootType.WEB_DAV )
		{
			// Yes, make sure they entered the host url
			if ( isHostUrlValid() == false )
			{
				m_hostUrlTxtBox.setFocus( true );
				return false;
			}
		}
		
		// Is the server type "famt" or undefined?
		if ( serverType == NetFolderRootType.FAMT )
		{
			Scheduler.ScheduledCommand cmd;
			
			// Yes, tell the user they need to pick a server type.
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					Window.alert( GwtTeaming.getMessages().modifyNetFolderServerDlg_SelectServerTypePrompt() );
					m_tabPanel.selectTab( 0 );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );

			return false;
		}
		
		clearErrorPanel();
		hideErrorPanel();
		setOkEnabled( false );

		// Are we editing an existing net folder root?
		if ( m_netFolderRoot != null )
		{
			// Yes, issue an rpc request to modify the net folder root.  If the rpc request is
			// successful, close this dialog.
			modifyNetFolderRootAndClose();
		}
		else
		{
			// No, we are creating a new net folder root.
			
			// Is the name entered by the user valid?
			if ( isNameValid() == false )
			{
				m_nameTxtBox.setFocus( true );
				return false;
			}
			
			// Issue an rpc request to create the net folder root.  If the rpc request is successful,
			// close this dialog.
			createNetFolderRootAndClose();
		}
		
		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully create/modify a net folder root.
		return false;
	}
	
	/*
	 * Runs the LDAP browser for the proxy name.
	 */
	private void browseLdapForProxyNameAsync()
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				browseLdapForProxyNameNow();
			}
		} );
	}
	
	private void browseLdapForProxyNameNow()
	{
		// Have we instantiated an LDAP browser yet?
		if ( null == m_ldapBrowserDlg )
		{
			// No!  Create one now...
			LdapBrowserDlg.createAsync( new LdapBrowserDlgClient()
			{
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess( LdapBrowserDlg ldapDlg )
				{
					// ...save it away and run it.
					m_ldapBrowserDlg = ldapDlg;
					getLdapServersAndRunLdapBrowserAsync();
				}
			} );
		}
		
		else
		{
			// Yes, we've already instantiated an LDAP browser!  Simply
			// run it.
			getLdapServersAndRunLdapBrowserNow();
		}
	}

	/**
	 * 
	 */
	private Boolean getAllowDesktopAppToTriggerSync()
	{
		return m_allowDesktopAppToTriggerSyncCB.getValue();
	}
	
	/**
	 * 
	 */
	private long getJitsAclMaxAge()
	{
		String maxAgeStr;
		long maxAge = 0;
		
		maxAgeStr = m_jitsAclMaxAge.getText();
		if ( maxAgeStr != null && maxAgeStr.length() > 0 )
			maxAge = Long.parseLong( maxAgeStr );
		
		maxAge *= 1000;
		return maxAge;
	}
	
	/**
	 * 
	 */
	private boolean getJitsEnabled()
	{
		if ( m_jitsEnabledCkbox.getValue() == Boolean.TRUE )
			return true;

		return false;
	}
	
	/**
	 * 
	 */
	private long getJitsResultsMaxAge()
	{
		String maxAgeStr;
		long maxAge = 0;
		
		maxAgeStr = m_jitsResultsMaxAge.getText();
		if ( maxAgeStr != null && maxAgeStr.length() > 0 )
			maxAge = Long.parseLong( maxAgeStr );
		
		maxAge *= 1000;
		return maxAge;
	}
	
	/*
	 * Gets the list of LDAP servers and runs the browser on them.
	 */
	private void getLdapServersAndRunLdapBrowserAsync()
	{
		GwtClientHelper.deferCommand(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				// ...and run it.
				getLdapServersAndRunLdapBrowserNow();
			}
		} );
	}
	
	private void getLdapServersAndRunLdapBrowserNow()
	{
		// Have we obtained the list of LDAP servers yet?
		if ( null == m_ldapServerList )
		{
			// No!  Read them now...
			LdapBrowserDlg.getLdapServerList( new LdapBrowseListCallback()
			{
				@Override
				public void onFailure()
				{
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess( List<LdapBrowseSpec> serverList )
				{
					// ...save them away and run the dialog.
					m_ldapServerList = serverList;
					runLdapBrowserAsync();
				}
			} );
		}
		
		else
		{
			// Yes, we've already obtained the list of LDAP servers!
			// Simply run the dialog.
			runLdapBrowserNow();
		}
	}

	/*
	 * Runs the LDAP browser.
	 */
	private void runLdapBrowserAsync()
	{
		GwtClientHelper.deferCommand(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				runLdapBrowserNow();
			}
		} );
	}
	
	private void runLdapBrowserNow()
	{
		// Do we have any LDAP servers to browse?
		int c = ( ( null == m_ldapServerList ) ? 0 : m_ldapServerList.size() );
		if ( 0 == c )
		{
			// No!  Tell the user about the problem and bail.
			GwtClientHelper.deferredAlert( GwtTeaming.getMessages().modifyNetFolderServerDlg_NoLdapServers() );
			return;
		}
		
		// Run the LDAP browser using the list of LDAP servers.
		LdapBrowserDlg.initAndShow( 
			m_ldapBrowserDlg,
			new LdapBrowserCallback()
			{
				@Override
				public void closed()
				{
					// Ignored.  We don't care if the user closes
					// the browser.
				}

				@Override
				public void selectionChanged( LdapObject selection )
				{
					// Since we're browsing for user DN, it will ONLY
					// be a leaf node.  Ignore non-leaf selections.
					if ( selection.isLeaf() )
					{
						m_proxyNameTxtBox.setValue( selection.getDn() );
						m_ldapBrowserDlg.hide();
					}
				}
			},
			m_ldapServerList,		// List of LDAP servers that can be browsed.
			m_browseProxyDnBtn );	// The dialog is positioned relative to this.
	}
	
	/**
	 * 
	 */
	private boolean getAllowSelfSignedCerts()
	{
		return m_allowSelfSignedCertsCkbox.getValue();
	}
	
	/**
	 * 
	 */
	private GwtAuthenticationType getAuthType()
	{
		int selectedIndex;
		
		if ( m_authTypeListbox.isVisible() == false )
			return null;
		
		selectedIndex = m_authTypeListbox.getSelectedIndex();
		if ( selectedIndex >= 0 )
		{
			String value;

			value = m_authTypeListbox.getValue( selectedIndex );
			if ( value != null )
				return GwtAuthenticationType.getType( value );
		}
		
		return GwtAuthenticationType.NTLM;
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		// Return something.  Doesn't matter what because editSuccessful() does the work.
		return Boolean.TRUE;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		if ( m_netFolderRoot == null )
			return m_nameTxtBox;
		
		return m_rootPathTxtBox;
	}
	
	/**
	 * Return the value of the "Synchronize only the directory structure"
	 */
	public Boolean getFullSyncDirOnly()
	{
		if ( GwtMainPage.m_requestInfo.getShowSyncOnlyDirStructureUI() )
			return m_fullSyncDirOnlyCB.getValue();
		
		return Boolean.FALSE;
	}
	
	/**
	 * 
	 */
	@Override
	public HelpData getHelpData()
	{
		HelpData helpData;
		
		helpData = new HelpData();
		helpData.setGuideName( HelpData.ADMIN_GUIDE );
		helpData.setPageId( "netfolders_servers" );
		
		return helpData;
	}
	
	/**
	 * 
	 */
	private String getHostUrl()
	{
		return m_hostUrlTxtBox.getValue();
	}
	
	/**
	 * 
	 */
	private Boolean getIndexContent()
	{
		return m_indexContentCB.getValue();
	}
	
	/**
	 * 
	 */
	private boolean getIsSharePointServer()
	{
		return m_isSharePointServerCkbox.getValue();
	}
	
	/**
	 * Return the list of principals that have rights to use this net folder root
	 */
	private ArrayList<GwtPrincipal> getListOfPrivilegedPrincipals()
	{
		ArrayList<GwtPrincipal> listOfPrincipals;

		if ( m_showPrivilegedUsersUI && m_selectPrincipalsWidget != null )
			listOfPrincipals = m_selectPrincipalsWidget.getListOfSelectedPrincipals();
		else
			listOfPrincipals = new ArrayList<GwtPrincipal>();
		
		return listOfPrincipals;
	}
	
	/**
	 * Create a NetFolderRoot object that holds the id of the net folder root being edited,
	 * and the net folder root's new info
	 */
	private NetFolderRoot getNetFolderRootFromDlg()
	{
		NetFolderRoot netFolderRoot = null;
		
		netFolderRoot = new NetFolderRoot();
		netFolderRoot.setName( getName() );
		netFolderRoot.setRootType( getSelectedRootType() );
		netFolderRoot.setRootPath( getRootPath() );
		netFolderRoot.setProxyName( getProxyName() );
		netFolderRoot.setProxyPwd( getProxyPwd() );
		netFolderRoot.setAuthType( getAuthType() );
		netFolderRoot.setFullSyncDirOnly( getFullSyncDirOnly() );
		netFolderRoot.setIndexContent( getIndexContent() );
		netFolderRoot.setJitsEnabled( getJitsEnabled() );
		netFolderRoot.setJitsResultsMaxAge( getJitsResultsMaxAge() );
		netFolderRoot.setJitsAclMaxAge( getJitsAclMaxAge() );
		netFolderRoot.setAllowDesktopAppToTriggerInitialHomeFolderSync( getAllowDesktopAppToTriggerSync() );
		
		if ( m_showPrivilegedUsersUI && m_selectPrincipalsWidget != null )
			netFolderRoot.setListOfPrincipals( getListOfPrivilegedPrincipals() );
		
		if ( getSelectedRootType() == NetFolderRootType.WEB_DAV )
		{
			netFolderRoot.setHostUrl( getHostUrl() );
			netFolderRoot.setAllowSelfSignedCerts( getAllowSelfSignedCerts() );
			netFolderRoot.setIsSharePointServer( getIsSharePointServer() );
		}

		netFolderRoot.setSyncSchedule( getSyncSchedule() );

		if ( m_netFolderRoot != null )
			netFolderRoot.setId( m_netFolderRoot.getId() );
		
		return netFolderRoot;
	}
	
	
	/**
	 * Return the name entered by the user.
	 */
	private String getName()
	{
		return m_nameTxtBox.getValue();
	}
	
	/**
	 * 
	 */
	private String getProxyName()
	{
		return m_proxyNameTxtBox.getValue();
	}
	
	/**
	 * 
	 */
	private String getProxyPwd()
	{
		return m_proxyPwdTxtBox.getValue();
	}
	
	/**
	 * Return the root path entered by the user.
	 */
	private String getRootPath()
	{
		return m_rootPathTxtBox.getValue();
	}
	
	/**
	 * Return the selected root type
	 */
	private NetFolderRootType getSelectedRootType()
	{
		int selectedIndex;

		if ( m_rootTypeListbox != null )
		{
			selectedIndex = m_rootTypeListbox.getSelectedIndex();
			if ( selectedIndex >= 0 )
			{
				String value;
	
				value = m_rootTypeListbox.getValue( selectedIndex );
				if ( value != null )
					return NetFolderRootType.getType( value );
			}
			
			return NetFolderRootType.UNKNOWN;
		}
		
		return NetFolderRootType.UNKNOWN;
	}

	/**
	 * Return the sync schedule
	 */
	private GwtSchedule getSyncSchedule()
	{
		return m_scheduleWidget.getSchedule( );
	}

	/**
	 * This method gets called when the user selects the root type
	 * Show/hide the appropriate controls based on the selected root type.
	 */
	private void handleRootTypeSelected()
	{
		int index;
		
		// Does the server types listbox have an "undefined" item?
		index = GwtClientHelper.doesListboxContainValue( m_rootTypeListbox, NetFolderRootType.FAMT.toString() );
		if ( index != -1 )
		{
			// Yes, remove it.
			m_rootTypeListbox.removeItem( index );
		}

		danceDlg( true );
	}
	
	/**
	 * 
	 */
	public void init( NetFolderRoot netFolderRoot )
	{
		hideErrorPanel();
		
		m_netFolderRoot = netFolderRoot;

		clearErrorPanel();
		hideErrorPanel();
		hideStatusMsg();
		setOkEnabled( true );

		// Clear existing data in the controls.
		m_nameTxtBox.setValue( "" );
		
		if ( m_rootTypeListbox != null )
			GwtClientHelper.selectListboxItemByValue( m_rootTypeListbox, NetFolderRootType.WINDOWS.toString() );
		
		m_rootPathTxtBox.setValue( "" );
		m_proxyNameTxtBox.setValue( "" );
		m_proxyPwdTxtBox.setValue( "" );
		GwtClientHelper.selectListboxItemByValue( m_authTypeListbox, GwtAuthenticationType.NTLM.toString() );
		if ( m_hostUrlTxtBox != null)
			m_hostUrlTxtBox.setValue( "" );
		if ( m_allowSelfSignedCertsCkbox != null )
			m_allowSelfSignedCertsCkbox.setValue( false );
		if ( m_isSharePointServerCkbox != null )
			m_isSharePointServerCkbox.setValue( false );
		m_inProgressPanel.setVisible( false );

		if ( m_showPrivilegedUsersUI && m_selectPrincipalsWidget != null )
			m_selectPrincipalsWidget.init( null );//!!! Finish

		// Clear out the sync schedule controls
		m_scheduleWidget.init( null );
		
		m_indexContentCB.setValue( false );
		if ( GwtMainPage.m_requestInfo.getShowSyncOnlyDirStructureUI() )
			m_fullSyncDirOnlyCB.setValue( false );
		
		m_allowDesktopAppToTriggerSyncCB.setValue( true );
		
		// Forget about any list of LDAP servers.  The list may have
		// changed since this dialog was last run and setting this to
		// null will cause it to be reloaded when needed.
		m_ldapServerList = null;
		
		// Are we modifying an existing net folder root?
		if ( m_netFolderRoot != null )
		{
			// Yes
			// Update the dialog's header to say "Edit Net Folder Root"
			setCaption( GwtTeaming.getMessages().modifyNetFolderServerDlg_EditHeader( m_netFolderRoot.getName() ) );
			
			// Don't let the user edit the name.
			m_nameTxtBox.setValue( netFolderRoot.getName() );
			m_nameTxtBox.setEnabled( false );
			
			// Select the appropriate root type.
			if ( m_rootTypeListbox != null )
				initServerType( netFolderRoot.getRootType() );

			// If the root type is WebDAV, initialize the WebDAV specific controls
			if ( netFolderRoot.getRootType() == NetFolderRootType.WEB_DAV )
			{
				m_hostUrlTxtBox.setValue( netFolderRoot.getHostUrl() );
				m_allowSelfSignedCertsCkbox.setValue( netFolderRoot.getAllowSelfSignedCerts() );
				m_isSharePointServerCkbox.setValue( netFolderRoot.getIsSharePointServer() );
			}
			
			m_rootPathTxtBox.setValue( netFolderRoot.getRootPath() );
			m_proxyNameTxtBox.setValue( netFolderRoot.getProxyName() );
			m_proxyPwdTxtBox.setValue( netFolderRoot.getProxyPwd() );
		
			if ( m_showPrivilegedUsersUI && m_selectPrincipalsWidget != null )
				m_selectPrincipalsWidget.init( m_netFolderRoot.getListOfPrincipals() );

			// Initialize the sync schedule controls
			m_scheduleWidget.init( m_netFolderRoot.getSyncSchedule() );
			
			// Initialize the "index content" control
			{
				Boolean value;
				
				value = m_netFolderRoot.getIndexContent();
				if ( value != null )
					m_indexContentCB.setValue( value );
			}
			
			// Initialize the "sync only the directory structure" control
			if ( GwtMainPage.m_requestInfo.getShowSyncOnlyDirStructureUI() )
			{
				Boolean value;
				
				value = m_netFolderRoot.getFullSyncDirOnly();
				if ( value != null )
					m_fullSyncDirOnlyCB.setValue( value );
			}
			
			// Initialize the "allow desktop app to trigger initial home folder sync" control
			{
				Boolean value;
				
				value = m_netFolderRoot.getAllowDesktopAppToTriggerInitialHomeFolderSync();
				if ( value != null )
					m_allowDesktopAppToTriggerSyncCB.setValue( value );
			}
		}
		else
		{
			// No
			// Update the dialog's header to say "Add Net Folder Root"
			setCaption( GwtTeaming.getMessages().modifyNetFolderServerDlg_AddHeader() );
			
			// Enable the "Name" field.
			m_nameTxtBox.setEnabled( true );
			
			m_tabPanel.selectTab( 0 );
		}
		
		initJits();
		
		danceDlg( false );

		{
			GwtAuthenticationType authType;
			
			if ( netFolderRoot != null )
				authType = netFolderRoot.getAuthType();
			else
			{
				NetFolderRootType serverType;

				// Get the selected server type
				serverType = getSelectedRootType();

				if ( serverType == NetFolderRootType.WINDOWS )
					authType = GwtAuthenticationType.KERBEROS_THEN_NTLM;
				else
					authType = GwtAuthenticationType.NMAS;
			}
			
		
			// initAuthType() must be called after danceDlg() because danceDlg() will add/remove
			// items from the authentication type listbox depending on the selected server type.
			// Select the appropriate auth type
			initAuthType( authType );
		}
	}

	/**
	 * Initialize the authentication type
	 */
	private void initAuthType( GwtAuthenticationType authType )
	{
		if ( authType != null )
			GwtClientHelper.selectListboxItemByValue( m_authTypeListbox, authType.toString() );
	}
	
	/**
	 * Initialize the controls used for the jits settings
	 */
	private void initJits()
	{
		m_jitsEnabledCkbox.setValue( false );
		m_jitsAclMaxAge.setValue( "" );
		m_jitsResultsMaxAge.setValue( "" );
		
		if ( m_netFolderRoot != null )
		{
			m_jitsEnabledCkbox.setValue( m_netFolderRoot.getJitsEnabled() );
			m_jitsAclMaxAge.setValue( String.valueOf( m_netFolderRoot.getJitsAclMaxAge() / 1000 ) );
			m_jitsResultsMaxAge.setValue( String.valueOf( m_netFolderRoot.getJitsResultsMaxAge() / 1000 ) );
		}
		else
		{
			Long value;
			
			m_jitsEnabledCkbox.setValue( false );
			
			value = GwtMainPage.m_requestInfo.getDefaultJitsAclMaxAge() / 1000;
			m_jitsAclMaxAge.setValue( value.toString() );
			
			value = GwtMainPage.m_requestInfo.getDefaultJitsResultsMaxAge() / 1000;
			m_jitsResultsMaxAge.setValue( value.toString() );
		}
	}

	/**
	 * Initialize the server type
	 */
	private void initServerType( NetFolderRootType serverType )
	{
		int index;
		
		// Does the server types listbox already have an "undefined" item?
		index = GwtClientHelper.doesListboxContainValue( m_rootTypeListbox, NetFolderRootType.FAMT.toString() );

		if ( serverType == NetFolderRootType.FAMT )
		{
			// A server type of famt means that this net folder server was created pre Filr 1.1
			// We need the user to select the server type.
			
			// Does the server types listbox already have an "undefined" item?
			if ( index == -1 )
			{
				// No
				// Add an Undefined item to the server types listbox.
				m_rootTypeListbox.addItem(
										GwtTeaming.getMessages().modifyNetFolderServerDlg_Type_Undefined(),
										NetFolderRootType.FAMT.toString() );
			}
		}
		else
		{
			// Does the server types listbox have an "undefined" item?
			if ( index != -1 )
			{
				// Yes, remove it.
				m_rootTypeListbox.removeItem( index );
			}
		}
		
		GwtClientHelper.selectListboxItemByValue( m_rootTypeListbox, serverType.toString() );
	}

	/**
	 * Check to see if all the of the information needed to test the connection has been entered.
	 */
	private boolean isDataValidNeededToTestConnection()
	{
		NetFolderRootType serverType;
		String value;
		Scheduler.ScheduledCommand cmd;
		
		serverType = getSelectedRootType();

		// Is the server type "famt" or undefined?
		if ( serverType == NetFolderRootType.FAMT )
		{
			// Yes, tell the user they need to pick a server type.
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					Window.alert( GwtTeaming.getMessages().modifyNetFolderServerDlg_SelectServerTypePrompt() );
					m_tabPanel.selectTab( 0 );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );

			return false;
		}
		
		// Did the user enter a proxy name?
		value = getProxyName();
		if ( value == null || value.length() == 0 )
		{
			// No
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					Window.alert( GwtTeaming.getMessages().modifyNetFolderServerDlg_EnterProxyNamePrompt() );
					m_tabPanel.selectTab( 1 );
					m_proxyNameTxtBox.setFocus( true );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );

			return false;
		}
		
		// Did the user enter a proxy pwd?
		value = getProxyPwd();
		if ( value == null || value.length() == 0 )
		{
			// No
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					Window.alert( GwtTeaming.getMessages().modifyNetFolderServerDlg_EnterProxyPwdPrompt() );
					m_tabPanel.selectTab( 1 );
					m_proxyPwdTxtBox.setFocus( true );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );

			return false;
		}
	
		// If we get here everything is ok
		return true;
	}
	
	/**
	 * Is the host url entered by the user valid?
	 */
	private boolean isHostUrlValid()
	{
		String value;
		
		value = m_hostUrlTxtBox.getValue();
		if ( value == null || value.length() == 0 )
		{
			Window.alert( GwtTeaming.getMessages().modifyNetFolderServerDlg_HostUrlRequired() );
			return false;
		}
		
		return true;
	}
	
	/**
	 * Is the name entered by the user valid?
	 */
	private boolean isNameValid()
	{
		String value;
		
		value = m_nameTxtBox.getValue();
		if ( value == null || value.length() == 0 )
		{
			Window.alert( GwtTeaming.getMessages().modifyNetFolderServerDlg_NameRequired() );
			return false;
		}
		
		return true;
	}
	
	/**
	 * Issue an rpc request to modify the net folder root.  If the rpc request was successful
	 * close this dialog.
	 */
	private void modifyNetFolderRootAndClose()
	{
		final NetFolderRoot newNetFolderRoot;
		ModifyNetFolderRootCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback;

		// Create a NetFolderRoot object that holds the information about the net folder root
		newNetFolderRoot = getNetFolderRootFromDlg();
		
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				FlowPanel errorPanel;
				Label label;
				String errMsg;
				
				hideStatusMsg();
				setOkEnabled( true );

				// Get the panel that holds the errors.
				errorPanel = getErrorPanel();
				errorPanel.clear();
				
				errMsg = GwtTeaming.getMessages().modifyNetFolderServerDlg_ErrorModifyingNetFolderServer( caught.toString() );
				if ( caught instanceof GwtTeamingException )
				{
					GwtTeamingException ex;
					
					ex = (GwtTeamingException) caught;
					if ( ex.getExceptionType() == ExceptionType.ACCESS_CONTROL_EXCEPTION )
					{
						String desc;
						
						desc = GwtTeaming.getMessages().modifyNetFolderServerDlg_InsufficientRights();
						errMsg =GwtTeaming.getMessages().modifyNetFolderServerDlg_ErrorModifyingNetFolderServer( desc );
					}
				}
				label = new Label( errMsg );
				label.addStyleName( "dlgErrorLabel" );
				errorPanel.add( label );
				
				showErrorPanel();
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						NetFolderRootModifiedEvent event;
						
						// When a "home directory" net folder is created during the ldap sync process, a net
						// folder root may have been created without proxy credentials.  Did the user enter the
						// proxy credentials for the first time for a net folder root that already existed?
						if ( m_netFolderRoot != null )
						{
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
							if ( (newProxyName != null && newProxyName.length() > 0 && newProxyName.equalsIgnoreCase( origProxyName ) == false) ||
								 (newProxyPwd != null && newProxyPwd.length() > 0 && newProxyPwd.equalsIgnoreCase( origProxyPwd ) == false) )
							{
								// Yes
								// Ask the user if they want to sync all the net folders associated with
								// this net folder root.
								if ( Window.confirm( GwtTeaming.getMessages().modifyNetFolderServerDlg_SyncAllNetFoldersPrompt() ) )
								{
									// Sync this net folder server by syncing all the net folders
									// associated with this net folder server.
									syncNetFolderServer();
								}
							}
						}
						
						hideStatusMsg();
						setOkEnabled( true );

						// Fire an event that lets everyone know this net folder root was modified.
						event = new NetFolderRootModifiedEvent( newNetFolderRoot );
						GwtTeaming.fireEvent( event );

						// Close this dialog.
						hide();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}						
		};
		
		showStatusMsg( GwtTeaming.getMessages().modifyNetFolderServerDlg_ModifyingNetFolderServer() );
		
		// Issue an rpc request to update the net folder root.
		cmd = new ModifyNetFolderRootCmd( newNetFolderRoot ); 
		GwtClientHelper.executeCommand( cmd, rpcCallback );
	}

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
		// Let the widget detach and then unregister our event handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents()
	{
		// If we haven't allocated a list to track events we've registered yet...
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
											REGISTERED_EVENTS,
											this,
											m_registeredEventHandlers );
		}
	}
	
	/**
	 * Issue an rpc request to sync this net folder server by syncing all the list of net folders
	 * associated with this net folder server.
	 */
	private void syncNetFolderServer()
	{
		SyncNetFolderServerCmd cmd;
		HashSet<NetFolderRoot> toBeSyncd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;
		
		// Create the callback that will be used when we issue an ajax call
		// to sync the net folder server.
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( final Throwable t )
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_SyncNetFolderServer() );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
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
						// Tell the user the synchronization of the net folder server has started.
						//!!!Window.alert( GwtTeaming.getMessages().modifyNetFolderServerDlg_SyncOfNetFolderServerStarted() );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		// Issue an ajax request to sync the net folder server.
		toBeSyncd = new HashSet<NetFolderRoot>();
		toBeSyncd.add( m_netFolderRoot );
		cmd = new SyncNetFolderServerCmd( toBeSyncd );
		GwtClientHelper.executeCommand( cmd, rpcCallback );
	}
	
	/**
	 * Test the connection to the server to see if the information they have entered is valid.
	 */
	private void testConnection()
	{
		NetFolderRoot netFolderRoot;
		AsyncCallback<VibeRpcResponse> rpcCallback;
		TestNetFolderConnectionCmd cmd;

		// Is there a "test connection" request currently running?
		if ( m_inProgressPanel.isVisible() )
		{
			// Yes, bail
			return;
		}
		
		// Is the data needed to test the connection valid?
		if ( isDataValidNeededToTestConnection() == false )
		{
			// No, the user has already been told what to do.
			return;
		}
		
		m_inProgressPanel.setVisible( true );
		
		netFolderRoot = getNetFolderRootFromDlg();
		
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				String errMsg;
				
				m_inProgressPanel.setVisible( false );
				errMsg = GwtTeaming.getMessages().rpcFailure_ErrorTestingNetFolderServerConnection();
				Window.alert( errMsg );
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				TestNetFolderConnectionResponse response;
				String msg;
				
				response = (TestNetFolderConnectionResponse) result.getResponseData();
				switch ( response.getStatusCode() )
				{
				case NETWORK_ERROR:
					msg = GwtTeaming.getMessages().testConnection_NetworkError();
					break;
				
				case NORMAL:
					msg = GwtTeaming.getMessages().testConnection_Normal();
					break;
				
				case PROXY_CREDENTIALS_ERROR:
					msg = GwtTeaming.getMessages().testConnection_ProxyCredentialsError();
					break;
				
				case UNKNOWN:
				default:
					msg = GwtTeaming.getMessages().testConnection_UnknownStatus();
					break;
				}
				
				m_inProgressPanel.setVisible( false );
				Window.alert( msg );
			}						
		};
		
		// Issue an rpc request to test net folder root connection
		cmd = new TestNetFolderConnectionCmd(
										netFolderRoot.getName(),
										netFolderRoot.getRootType(),
										netFolderRoot.getRootPath(),
										"",
										netFolderRoot.getProxyName(),
										netFolderRoot.getProxyPwd() ); 
		GwtClientHelper.executeCommand( cmd, rpcCallback );
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( ( null != m_registeredEventHandlers ) && ( ! ( m_registeredEventHandlers.isEmpty() ) ) )
		{
			// ...unregister them.  (Note that this will also empty the list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}

	
	
	/**
	 * Loads the ModifyNetFolderRootDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final int left,
							final int top,
							final ModifyNetFolderRootDlgClient mnfrDlgClient )
	{
		GWT.runAsync( ModifyNetFolderRootDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ModifyNetFolderServerDlg() );
				if ( mnfrDlgClient != null )
				{
					mnfrDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ModifyNetFolderRootDlg mnfrDlg;
				
				mnfrDlg = new ModifyNetFolderRootDlg(
											autoHide,
											modal,
											left,
											top );
				mnfrDlgClient.onSuccess( mnfrDlg );
			}
		});
	}
}
