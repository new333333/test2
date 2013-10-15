/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.GwtPrincipal;
import org.kablink.teaming.gwt.client.GwtSchedule;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.NetFolderRoot;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.NetFolderRootCreatedEvent;
import org.kablink.teaming.gwt.client.event.NetFolderRootModifiedEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.CreateNetFolderRootCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ModifyNetFolderRootCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SyncNetFolderServerCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TestNetFolderConnectionCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TestNetFolderConnectionResponse;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
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
 * @author jwootton
 *
 */
public class ModifyNetFolderRootDlg extends DlgBox
	implements
		EditCanceledHandler,
		EditSuccessfulHandler
{
	private NetFolderRoot m_netFolderRoot;	// If we are modifying a net folder this is the net folder.
	private TextBox m_nameTxtBox;
	private ListBox m_rootTypeListbox;
	private FlowPanel m_serverPathHintPanel;
	private TextBox m_rootPathTxtBox;
	private TextBox m_proxyNameTxtBox;
	private PasswordTextBox m_proxyPwdTxtBox;
	private FlowPanel m_webDavSpacerPanel;
	private InlineLabel m_hostUrlLabel;
	private TextBox m_hostUrlTxtBox;
	private CheckBox m_allowSelfSignedCertsCkbox;
	private CheckBox m_isSharePointServerCkbox;
	private SelectPrincipalsWidget m_selectPrincipalsWidget;
	private ScheduleWidget m_scheduleWidget;
	private FlowPanel m_inProgressPanel;
	private List<HandlerRegistration> m_registeredEventHandlers;
	
	private static boolean m_showPrivilegedUsersUI = false;
	private static boolean m_showNetFolderServerType = GwtTeaming.m_requestInfo.getAllowSelectNetFolderServerDataSource();
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
		CIFS,
		CLOUD_FOLDERS,
		FAMT,
		FILE_SYSTEM,
		NCP_NETWARE,
		NCP_OES,
		SHARE_POINT_2010,
		SHARE_POINT_2013,
		WEB_DAV,
		UNKNOWN;

		public static NetFolderRootType getType( String type )
		{
			if ( type == null )
				return NetFolderRootType.FAMT;
			
			if ( type.equalsIgnoreCase( NetFolderRootType.CIFS.toString() ) )
				return NetFolderRootType.CIFS;
			
			if ( type.equalsIgnoreCase( NetFolderRootType.CLOUD_FOLDERS.toString() ) )
				return NetFolderRootType.CLOUD_FOLDERS;
			
			if ( type.equalsIgnoreCase( NetFolderRootType.FAMT.toString() ) )
				return NetFolderRootType.FAMT;
			
			if ( type.equalsIgnoreCase( NetFolderRootType.FILE_SYSTEM.toString() ) )
				return NetFolderRootType.FILE_SYSTEM;
			
			if ( type.equalsIgnoreCase( NetFolderRootType.NCP_NETWARE.toString() ) )
				return NetFolderRootType.NCP_NETWARE;

			if ( type.equalsIgnoreCase( NetFolderRootType.NCP_OES.toString() ) )
				return NetFolderRootType.NCP_OES;

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
						GwtTeaming.getMessages().modifyNetFolderServerDlg_Type_Famt(),
						NetFolderRootType.FAMT.toString() );
			
			m_rootTypeListbox.addItem(
					GwtTeaming.getMessages().modifyNetFolderServerDlg_Type_SharePoint2010(),
					NetFolderRootType.SHARE_POINT_2010.toString() );
		
			m_rootTypeListbox.addItem(
					GwtTeaming.getMessages().modifyNetFolderServerDlg_Type_SharePoint2013(),
					NetFolderRootType.SHARE_POINT_2013.toString() );
		
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
			label = new Label( messages.modifyNetFolderServerDlg_ServerPathHint2() );
			m_serverPathHintPanel.add( label );
			label = new Label( messages.modifyNetFolderServerDlg_ServerPathHint3() );
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
		
		// Create the controls used to enter proxy information
		{
			// Add some space
			spacerPanel = new FlowPanel();
			spacerPanel.getElement().getStyle().setMarginTop( 10, Unit.PX );
			table.setWidget( nextRow, 0, spacerPanel );
			++nextRow;

			// Add some instructions on the format that should be used when entering the proxy name
			{
				FlowPanel panel;
				
				panel = new FlowPanel();
				panel.addStyleName( "margintop1" );
				panel.addStyleName( "modifyNetFolderServerDlg_ProxyNameHint" );

				// Add a hint that describes the unc syntax
				label = new Label( messages.modifyNetFolderServerDlg_ProxyNameHint1() );
				panel.add( label );
				label = new Label( messages.modifyNetFolderServerDlg_ProxyNameHint2() );
				panel.add( label );
				label = new Label( messages.modifyNetFolderServerDlg_ProxyNameHint3() );
				panel.add( label );
				
				cellFormatter.setColSpan( nextRow, 0, 2 );
				table.setWidget( nextRow, 0, panel );
				++nextRow;
			}
			
			label = new InlineLabel( messages.modifyNetFolderServerDlg_ProxyNameLabel() );
			table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
			
			m_proxyNameTxtBox = new TextBox();
			m_proxyNameTxtBox.setVisibleLength( 30 );
			table.setWidget( nextRow, 1, m_proxyNameTxtBox );
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

					label = new InlineLabel( GwtTeaming.getMessages().testConnection_InProgressLabel() );
					m_inProgressPanel.add( label );
					
					table.setWidget( nextRow, 1, m_inProgressPanel );
				}
				
				++nextRow;
			}
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
		TabPanel tabPanel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		tabPanel = new TabPanel();
		tabPanel.addStyleName( "vibe-tabPanel" );

		mainPanel.add( tabPanel );

		// Create the panel that holds the basic net folder server configuration
		{
			Panel configPanel;
			
			configPanel = createConfigPanel();
			tabPanel.add( configPanel, messages.modifyNetFolderServerDlg_ConfigTab() );
		}
		
		// Create the panel that holds the controls for the schedule
		{
			Panel schedPanel;
			
			schedPanel = createSchedulePanel();
			tabPanel.add( schedPanel, messages.modifyNetFolderServerDlg_ScheduleTab() );
		}
		
		tabPanel.selectTab( 0 );

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
		
		if ( m_rootTypeListbox != null )
		{
			selectedIndex = m_rootTypeListbox.getSelectedIndex();
			if ( selectedIndex >= 0 )
			{
				NetFolderRootType type;
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
				
				// Update the server path hint
				{
					GwtTeamingMessages messages;
					Label label;
					
					m_serverPathHintPanel.clear();
					
					messages = GwtTeaming.getMessages();
					
					switch( type )
					{
					case FAMT:
						label = new Label( messages.modifyNetFolderServerDlg_ServerPathHint1() );
						m_serverPathHintPanel.add( label );
						label = new Label( messages.modifyNetFolderServerDlg_ServerPathHint2() );
						m_serverPathHintPanel.add( label );
						label = new Label( messages.modifyNetFolderServerDlg_ServerPathHint3() );
						m_serverPathHintPanel.add( label );
						break;
						
					case SHARE_POINT_2010:
					case SHARE_POINT_2013:
						label = new Label( messages.modifyNetFolderServerDlg_SharePointPathHint() );
						m_serverPathHintPanel.add( label );
						break;
						
					default:
						break;
					}
				}
			}
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
		// Is the root type WebDAV?
		if ( getSelectedRootType() == NetFolderRootType.WEB_DAV )
		{
			// Yes, make sure they entered the host url
			if ( isHostUrlValid() == false )
			{
				m_hostUrlTxtBox.setFocus( true );
				return false;
			}
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
	
	/**
	 * 
	 */
	private boolean getAllowSelfSignedCerts()
	{
		return m_allowSelfSignedCertsCkbox.getValue();
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
		
		return NetFolderRootType.FAMT;
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
			GwtClientHelper.selectListboxItemByValue( m_rootTypeListbox, NetFolderRootType.FAMT.toString() );
		m_rootPathTxtBox.setValue( "" );
		m_proxyNameTxtBox.setValue( "" );
		m_proxyPwdTxtBox.setValue( "" );
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
				GwtClientHelper.selectListboxItemByValue( m_rootTypeListbox, netFolderRoot.getRootType().toString() );

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
		}
		else
		{
			// No
			// Update the dialog's header to say "Add Net Folder Root"
			setCaption( GwtTeaming.getMessages().modifyNetFolderServerDlg_AddHeader() );
			
			// Enable the "Name" field.
			m_nameTxtBox.setEnabled( true );
		}
		
		danceDlg( false );
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
