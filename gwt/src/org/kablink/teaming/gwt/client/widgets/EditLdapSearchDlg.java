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
import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtHomeDirConfig;
import org.kablink.teaming.gwt.client.GwtHomeDirConfig.GwtHomeDirCreationOption;
import org.kablink.teaming.gwt.client.GwtLdapSearchInfo;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.NetFolderRoot;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.NetFolderRootCreatedEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.ldapbrowser.DirectoryServer;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapObject;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapSearchInfo;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapServer.DirectoryType;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFolderRootsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFolderRootsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.LdapBrowserDlg.LdapBrowserDlgClient;
import org.kablink.teaming.gwt.client.widgets.ModifyNetFolderRootDlg.ModifyNetFolderRootDlgClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * ?
 * 
 * @author jwootton
 */
public class EditLdapSearchDlg extends DlgBox
	implements NetFolderRootCreatedEvent.Handler
{
	private DirectoryServer m_directoryServer;
	private GwtLdapSearchInfo m_ldapSearch;

	private Button m_browseBaseDnBtn;	// LDAP browse button next to m_baseDnTextBox.
	private TextBox m_baseDnTextBox;
	private TextArea m_filterTextArea;
	private CheckBox m_searchSubtreeCheckBox;
	private FlowPanel m_homeDirInfoPanel = null;

	// The following data members are used to specify how a user's home dir net folder will be created.
	private ListBox m_netFolderServersListBox;
	private TextBox m_netFolderPathTextBox;
	private TextBox m_specifiedAttribTextBox;
	private RadioButton m_useCustomCriteriaRB;
	private RadioButton m_useHomeDirAttribRB;
	private RadioButton m_useSpecifiedAttribRB;
	private RadioButton m_dontCreateNetFolderRB;

	private ModifyNetFolderRootDlg m_modifyNetFolderServerDlg;
	private LdapBrowserDlg m_ldapBrowserDlg;

	private List<HandlerRegistration> m_registeredEventHandlers;

	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[]
    {
		TeamingEvents.NET_FOLDER_ROOT_CREATED,
	};

	
	/**
	 * Callback interface to interact with the "edit ldap search" dialog
	 * asynchronously after it loads. 
	 */
	public interface EditLdapSearchDlgClient
	{
		void onSuccess( EditLdapSearchDlg elsDlg );
		void onUnavailable();
	}

	
	/**
	 * 
	 */
	private EditLdapSearchDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		EditSuccessfulHandler editSuccessfulHandler )
	{
		super( autoHide, modal, xPos, yPos, DlgButtonMode.OkCancel );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent(
						GwtTeaming.getMessages().editLdapSearchDlg_Header(),
						editSuccessfulHandler,
						null,
						null );
	}

	/**
	 * Add the given list of Net Folder Servers to the dialog
	 */
	private void addNetFolderServers( List<NetFolderRoot> listOfNetFolderServers )
	{
		m_netFolderServersListBox.clear();
		
		if ( listOfNetFolderServers != null )
		{
			for ( NetFolderRoot nextRoot : listOfNetFolderServers )
			{
				m_netFolderServersListBox.addItem( nextRoot.getName(), nextRoot.getName() );
			}
		}
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		FlexTable table;
		int row = 0;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		table = new FlexTable();
		table.setCellSpacing( 4 );

		mainPanel.add( table );
		
		// Add the base dn controls
		{
			FlowPanel tmpPanel;
			Label label;

			tmpPanel = new FlowPanel();
			label = new Label( messages.editLdapSearchDlg_BaseDnLabel() );
			tmpPanel.add( label );
			table.setHTML( row, 0, tmpPanel.getElement().getInnerHTML()  );
			
			tmpPanel = new FlowPanel();
			m_baseDnTextBox = new TextBox();
			m_baseDnTextBox.setVisibleLength( 40 );
			tmpPanel.add( m_baseDnTextBox );
			
			
			FlowPanel html = new FlowPanel();
			Image btnImg = GwtClientHelper.buildImage( GwtTeaming.getImageBundle().browseLdap().getSafeUri().asString() );
			btnImg.setTitle( GwtTeaming.getMessages().editLdapSearchDlg_BaseDn_Alt() );
			html.add( btnImg );
			m_browseBaseDnBtn = new Button( html.getElement().getInnerHTML() );
			m_browseBaseDnBtn.addStyleName( "editLdapServerConfigDlg_BrowseDN" );
			m_browseBaseDnBtn.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							boolean canBrowse = (( null != m_directoryServer ) && m_directoryServer.isEnoughToConnectAuthenticated() );
							if ( canBrowse ) 
							     browseLdapForBaseDn();
							else GwtClientHelper.deferredAlert( GwtTeaming.getMessages().editLdapSearchDlg_NoServerURL() );
						}
					} );
				}
			} );
			tmpPanel.add( m_browseBaseDnBtn );
			table.setWidget( row, 1, tmpPanel );
			++row;
		}

		// Add the filter controls
		{
			FlowPanel tmpPanel;
			Label label;
			
			tmpPanel = new FlowPanel();
			label = new Label( messages.editLdapSearchDlg_FilterLabel() );
			tmpPanel.add( label );
			table.setHTML( row, 0, tmpPanel.getElement().getInnerHTML() );
			
			m_filterTextArea = new TextArea();
			m_filterTextArea.setVisibleLines( 4 );
			m_filterTextArea.setWidth( "550px" );
			table.setWidget( row, 1, m_filterTextArea );
			++row;
		}
		
		// Add a "search subtree" checkbox
		{
			FlowPanel tmpPanel;
			
			tmpPanel = new FlowPanel();
			tmpPanel.getElement().getStyle().setMarginTop( 8, Unit.PX );
			m_searchSubtreeCheckBox = new CheckBox( messages.editLdapSearchDlg_SearchSubtreeLabel() );
			tmpPanel.add( m_searchSubtreeCheckBox );
			
			mainPanel.add( tmpPanel );
		}
		
		// Add the panel that holds the controls that define how to create a home dir net folder
		if ( GwtClientHelper.isLicenseFilr() )
		{
			Label label;
			FlowPanel homeDirContentPanel;
			
			m_homeDirInfoPanel = new FlowPanel();
			m_homeDirInfoPanel.getElement().getStyle().setMarginTop( 10, Unit.PX );

			label = new Label( messages.editLdapSearchDlg_HomeDirNetFolderHeader() );
			label.addStyleName( "bold" );
			m_homeDirInfoPanel.add( label );
			
			homeDirContentPanel = new FlowPanel();
			homeDirContentPanel.getElement().getStyle().setMarginLeft( 12, Unit.PX );
			m_homeDirInfoPanel.add( homeDirContentPanel );
			
			label = new Label( messages.editLdapSearchDlg_HomeDirNetFolderHint() );
			label.addStyleName( "editLdapSearchDlg_Hint" );
			label.getElement().getStyle().setMarginBottom( 4, Unit.PX );
			homeDirContentPanel.add( label );
			
			// Add the "Use the following criteria"
			{
				FlowPanel rbPanel;
				FlexTable tmpTable;
				
				rbPanel = new FlowPanel();
				homeDirContentPanel.add( rbPanel );
				
				m_useCustomCriteriaRB = new RadioButton(
													"home-dir-net-folder",
													messages.editLdapSearchDlg_CustomCriteriaRB() );
				rbPanel.add( m_useCustomCriteriaRB );
				
				tmpTable = new FlexTable();
				tmpTable.getElement().getStyle().setMarginLeft( 38, Unit.PX );
				rbPanel.add( tmpTable );
				
				// Add the controls for selecting a net folder server
				{
					Button createBtn;
					
					label = new Label( messages.editLdapSearchDlg_NetFolderServerLabel() );
					tmpTable.setHTML( 0, 0, label.getElement().getInnerHTML() );
					
					m_netFolderServersListBox = new ListBox();
					m_netFolderServersListBox.setMultipleSelect( false );
					m_netFolderServersListBox.setVisibleItemCount( 1 );
					tmpTable.setWidget( 0, 1, m_netFolderServersListBox );
					
					// Add "Create net folder server " button
					createBtn = new Button( messages.modifyNetFolderDlg_CreateNetFolderServerLabel() );
					createBtn.addStyleName( "teamingButton" );
					createBtn.addStyleName( "marginleft3" );
					createBtn.addClickHandler( new ClickHandler()
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
									invokeCreateNetFolderServerDlg();
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
						
					} );

					tmpTable.setWidget( 0, 2, createBtn );
				}
				
				// Add the controls for specifying the home dir net folder path
				{
					label = new Label( messages.editLdapSearchDlg_NetFolderPathLabel() );
					tmpTable.setHTML( 1, 0, label.getElement().getInnerHTML() );
					
					m_netFolderPathTextBox = new TextBox();
					m_netFolderPathTextBox.setVisibleLength( 30 );
					tmpTable.setWidget( 1, 1, m_netFolderPathTextBox );
				}
			}
			
			// Add the "Use the ldap home directory attribute" radio button
			{
				FlowPanel rbPanel;
				
				rbPanel = new FlowPanel();
				homeDirContentPanel.add( rbPanel );
				
				m_useHomeDirAttribRB = new RadioButton(
													"home-dir-net-folder",
													messages.editLdapSearchDlg_HomeDirAttribRB() );
				rbPanel.add( m_useHomeDirAttribRB );
			}
			
			// Add the "Use the specified ldap attribute" radio button
			{
				FlowPanel rbPanel;
				FlexTable tmpTable;
				
				rbPanel = new FlowPanel();
				homeDirContentPanel.add( rbPanel );
				
				m_useSpecifiedAttribRB = new RadioButton(
													"home-dir-net-folder",
													messages.editLdapSearchDlg_SpecifiedAttribRB() );
				rbPanel.add( m_useSpecifiedAttribRB );
				
				tmpTable = new FlexTable();
				tmpTable.getElement().getStyle().setMarginLeft( 38, Unit.PX );
				rbPanel.add( tmpTable );
				
				label = new Label( messages.editLdapSearchDlg_AttributeNameLabel() );
				tmpTable.setHTML( 0, 0, label.getElement().getInnerHTML() );
				
				m_specifiedAttribTextBox = new TextBox();
				m_specifiedAttribTextBox.setVisibleLength( 20 );
				tmpTable.setWidget( 0, 1, m_specifiedAttribTextBox );
			}
			
			// Add the "Don't create a home directory net folder" radio button
			{
				FlowPanel rbPanel;
				
				rbPanel = new FlowPanel();
				homeDirContentPanel.add( rbPanel );
				
				m_dontCreateNetFolderRB = new RadioButton(
													"home-dir-net-folder",
													messages.editLdapSearchDlg_DontCreateNetFolderRB() );
				rbPanel.add( m_dontCreateNetFolderRB );
			}
			
			mainPanel.add( m_homeDirInfoPanel );
		}
		
		return mainPanel;
	}

	/*
	 * Runs the LDAP browser for a base DN.
	 */
	private void browseLdapForBaseDn()
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
					// ...save it away...
					m_ldapBrowserDlg = ldapDlg;
					GwtClientHelper.deferCommand(new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// ...and run it.
							browseLdapForBaseDnImpl();
						}
					} );
				}
			} );
		}
		
		else
		{
			// Yes, we've already instantiated an LDAP browser!  Simply
			// run it.
			browseLdapForBaseDnImpl();
		}
	}
	
	private void browseLdapForBaseDnImpl()
	{
		LdapSearchInfo si = new LdapSearchInfo();
		si.setSearchObjectClass(LdapSearchInfo.RETURN_CONTAINERS_ONLY);
		si.setSearchSubTree(false);
		
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
				public void selectionChanged(LdapObject selection, DirectoryType dt)
				{
					m_baseDnTextBox.setValue(selection.getDn());
					m_ldapBrowserDlg.hide();
				}
			},
			m_directoryServer,
			si,
			m_browseBaseDnBtn);
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		GwtTeamingMessages messages;
		String baseDn;
		String filter;
		GwtHomeDirConfig homeDirConfig = null;
		
		messages = GwtTeaming.getMessages();
		
		baseDn = m_baseDnTextBox.getValue();
		filter = m_filterTextArea.getValue();
		
		if ( isFieldValid( baseDn, m_baseDnTextBox, messages.editLdapSearchDlg_ErrorNoBaseDn() ) == false )
			return null;
					
		if ( isFieldValid( filter, m_filterTextArea, messages.editLdapSearchDlg_ErrorNoFilter() ) == false )
			return null;
		
		// Is the home dir panel visible?
		if ( GwtClientHelper.isLicenseFilr() && m_homeDirInfoPanel != null && m_homeDirInfoPanel.isVisible() )
		{
			// Yes, get the information from the controls.
			homeDirConfig = new GwtHomeDirConfig();
			
			if ( m_useCustomCriteriaRB.getValue() )
			{
				String netFolderServerName;
				String netFolderPath;
				
				netFolderServerName = getSelectedNetFolderServerName();
				netFolderPath = m_netFolderPathTextBox.getValue();
				
				if ( isFieldValid( netFolderServerName, m_netFolderServersListBox, messages.editLdapSearchDlg_ErrorNoNetFolderServer() ) == false )
					return null;

				if ( isFieldValid( netFolderPath, m_netFolderPathTextBox, messages.editLdapSearchDlg_ErrorNoNetFolderPath() ) == false )
					return null;
				
				homeDirConfig.setCreationOption( GwtHomeDirCreationOption.USE_CUSTOM_CONFIG );
				homeDirConfig.setNetFolderServerName( netFolderServerName );
				homeDirConfig.setNetFolderPath( netFolderPath );
			}
			else if ( m_useHomeDirAttribRB.getValue() )
				homeDirConfig.setCreationOption( GwtHomeDirCreationOption.USE_HOME_DIRECTORY_ATTRIBUTE );
			else if ( m_useSpecifiedAttribRB.getValue() )
			{
				String attribName;
				
				attribName = m_specifiedAttribTextBox.getValue();
				if ( isFieldValid( attribName, m_specifiedAttribTextBox, messages.editLdapSearchDlg_ErrorNoAttributeName() ) == false )
					return null;
				
				homeDirConfig.setCreationOption( GwtHomeDirCreationOption.USE_CUSTOM_ATTRIBUTE );
				homeDirConfig.setAttributeName( attribName );
			}
			else if ( m_dontCreateNetFolderRB.getValue() )
				homeDirConfig.setCreationOption( GwtHomeDirCreationOption.DONT_CREATE_HOME_DIR_NET_FOLDER );
			else
				homeDirConfig.setCreationOption( GwtHomeDirCreationOption.USE_HOME_DIRECTORY_ATTRIBUTE );
		}

		// Has anything changed?
		{
			boolean isDirty = false;
			
			if ( GwtClientHelper.areStringsEqual( baseDn, m_ldapSearch.getBaseDn() ) == false )
				isDirty = true;
			else if ( GwtClientHelper.areStringsEqual( filter, m_ldapSearch.getFilter() ) == false )
				isDirty = true;
			else if ( m_searchSubtreeCheckBox.getValue() != m_ldapSearch.getSearchSubtree() )
				isDirty = true;
			else if ( homeDirConfig != null && homeDirConfig.isEqualTo( m_ldapSearch.getHomeDirConfig() ) == false )
				isDirty = true;
			
			m_ldapSearch.setIsDirty( isDirty );
		}

		if ( homeDirConfig != null )
			m_ldapSearch.setHomeDirConfig( homeDirConfig );
			
		m_ldapSearch.setBaseDn( baseDn );
		m_ldapSearch.setFilter( filter );
		m_ldapSearch.setSearchSubtree( m_searchSubtreeCheckBox.getValue() );

		return m_ldapSearch;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_baseDnTextBox;
	}
	
	/**
	 * Issue an ajax request to get the list of net folder servers the user has
	 * permission to use. 
	 */
	private void getListOfNetFolderServers()
	{
		GetNetFolderRootsCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;

		// Create the callback that will be used when we issue an ajax call to get the net folder roots.
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetAllNetFolderServers() );
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
						GetNetFolderRootsRpcResponseData responseData;
						
						responseData = (GetNetFolderRootsRpcResponseData) response.getResponseData();
						
						// Add the net folder roots to the ui
						if ( responseData != null )
							addNetFolderServers( responseData.getListOfNetFolderRoots() );
						
						// Are there any net folder roots to select from?
						if ( m_netFolderServersListBox.getItemCount() > 0 )
						{
							// Yes
							if ( m_ldapSearch != null && m_ldapSearch.getHomeDirConfig() != null )
							{
								String netFolderServerName;
								
								// select the appropriate net folder root.
								netFolderServerName = m_ldapSearch.getHomeDirConfig().getNetFolderServerName();
								GwtClientHelper.selectListboxItemByValue(
																	m_netFolderServersListBox,
																	netFolderServerName );
							}
							else
								m_netFolderServersListBox.setSelectedIndex( 0 );
						}
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		// Issue an ajax request to get a list of all the net folder roots.
		cmd = new GetNetFolderRootsCmd();
		GwtClientHelper.executeCommand( cmd, rpcCallback );
	}
	
	/**
	 * Return the selected net folder server name
	 */
	private String getSelectedNetFolderServerName()
	{
		// Are there any net folder servers to select from?
		if ( m_netFolderServersListBox.getItemCount() > 0 )
		{
			int selectedIndex;

			// Yes
			selectedIndex = m_netFolderServersListBox.getSelectedIndex();
			if ( selectedIndex >= 0 )
			{
				return m_netFolderServersListBox.getValue( selectedIndex );
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 */
	public void init( DirectoryServer directoryServer, GwtLdapSearchInfo ldapSearch, boolean showHomeDirInfoControls )
	{
		m_directoryServer = directoryServer;
		m_ldapSearch = ldapSearch;
		
		m_baseDnTextBox.setValue( "" );
		m_filterTextArea.setValue( "" );
		m_searchSubtreeCheckBox.setValue( false );

		if ( GwtClientHelper.isLicenseFilr() && m_homeDirInfoPanel != null )
			m_homeDirInfoPanel.setVisible( showHomeDirInfoControls );

		if ( ldapSearch == null )
			return;
		
		m_baseDnTextBox.setValue( ldapSearch.getBaseDn() );
		m_filterTextArea.setValue( ldapSearch.getFilter() );
		m_searchSubtreeCheckBox.setValue( ldapSearch.getSearchSubtree() );

		// Are we showing the home dir info controls?
		if ( showHomeDirInfoControls && GwtClientHelper.isLicenseFilr() && m_homeDirInfoPanel != null )
		{
			GwtHomeDirConfig homeDirConfig;
			
			// Yes, initialize the controls.
			m_netFolderPathTextBox.setValue( "" );
			m_specifiedAttribTextBox.setValue( "" );

			m_useCustomCriteriaRB.setValue( false );
			m_useSpecifiedAttribRB.setValue( false );
			m_useHomeDirAttribRB.setValue( false );
			m_dontCreateNetFolderRB.setValue( false );
			
			homeDirConfig = ldapSearch.getHomeDirConfig();
			if ( homeDirConfig != null )
			{
				switch ( homeDirConfig.getCreationOption() )
				{
				case USE_CUSTOM_CONFIG:
					m_useCustomCriteriaRB.setValue( true );
					m_netFolderPathTextBox.setValue( homeDirConfig.getNetFolderPath() );
					break;

				case USE_CUSTOM_ATTRIBUTE:
					m_useSpecifiedAttribRB.setValue( true );
					m_specifiedAttribTextBox.setValue( homeDirConfig.getAttributeName() );
					break;
					
				case DONT_CREATE_HOME_DIR_NET_FOLDER:
					m_dontCreateNetFolderRB.setValue( true );
					break;

				case USE_HOME_DIRECTORY_ATTRIBUTE:
				case UNKNOWN:
				default:
					m_useHomeDirAttribRB.setValue( true );
					break;
				}
			}
			else
				m_useHomeDirAttribRB.setValue( true );

			// Issue an ajax request to get the list of net folder servers this user has
			// permission to use.
			getListOfNetFolderServers();
		}
	}
	
	/**
	 * Invoke the "create net folder server" dialog
	 */
	private void invokeCreateNetFolderServerDlg()
	{
		int x;
		int y;
		
		// Get the position of this dialog.
		x = getAbsoluteLeft() + 25;
		y = getAbsoluteTop() - 250;
		
		if ( m_modifyNetFolderServerDlg == null )
		{
			ModifyNetFolderRootDlg.createAsync(
											false, 
											true,
											x, 
											y,
											new ModifyNetFolderRootDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ModifyNetFolderRootDlg mnfrDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_modifyNetFolderServerDlg = mnfrDlg;
							
							m_modifyNetFolderServerDlg.init( null );
							m_modifyNetFolderServerDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_modifyNetFolderServerDlg.init( null );
			m_modifyNetFolderServerDlg.setPopupPosition( x, y );
			m_modifyNetFolderServerDlg.show();
		}
	}
	
	/**
	 * See if the user provided a String value for the given field.
	 */
	private boolean isFieldValid( String value, final FocusWidget inputWidget, String errMsg )
	{
		if ( value != null && value.length() > 0 )
			return true;
		
		Window.alert( errMsg );

		if ( inputWidget != null )
		{
			Scheduler.ScheduledCommand cmd;
			
			cmd = new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					inputWidget.setFocus( true );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
		
		return false;
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
	
	/**
	 * Handles the NetFolderRootCreatedEvent received by this class
	 */
	@Override
	public void onNetFolderRootCreated( NetFolderRootCreatedEvent event )
	{
		NetFolderRoot root;

		// Get the newly created net folder root.
		root = event.getNetFolderRoot();
		
		if ( root != null )
		{
			// Add the net folder root to the listbox
			m_netFolderServersListBox.addItem( root.getName(), root.getName() );
			
			// Select the new net folder root.
			GwtClientHelper.selectListboxItemByValue( m_netFolderServersListBox, root.getName() );
		}
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
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void createDlg(
		final boolean autoHide,
		final boolean modal,
		final int left,
		final int top,
		final EditSuccessfulHandler editSuccessfulHandler,
		final EditLdapSearchDlgClient elsDlgClient )
	{
		GWT.runAsync( EditLdapSearchDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditLdapSearchDlg() );
				if ( elsDlgClient != null )
				{
					elsDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				EditLdapSearchDlg elsDlg;
				
				elsDlg = new EditLdapSearchDlg(
											autoHide,
											modal,
											left,
											top,
											editSuccessfulHandler );
				
				if ( elsDlgClient != null )
					elsDlgClient.onSuccess( elsDlg );
			}
		} );
	}

	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void initAndShow(
		final EditLdapSearchDlg dlg,
		final DirectoryServer directoryServer,
		final GwtLdapSearchInfo searchInfo,
		final boolean showHomeDirInfoControls,
		final EditSuccessfulHandler editSuccessfulHandler,
		final EditLdapSearchDlgClient elsDlgClient )
	{
		GWT.runAsync( EditLdapSearchDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditLdapSearchDlg() );
				if ( elsDlgClient != null )
				{
					elsDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				dlg.init( directoryServer, searchInfo, showHomeDirInfoControls );
				dlg.initHandlers( editSuccessfulHandler, null );
				dlg.show();
			}
		} );
	}
}
