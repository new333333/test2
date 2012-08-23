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
import java.util.List;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.GwtUser.IdentitySource;
import org.kablink.teaming.gwt.client.NetFolderRoot;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.NetFolderRootCreatedEvent;
import org.kablink.teaming.gwt.client.event.NetFolderRootModifiedEvent;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.CreateNetFolderRootCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ModifyNetFolderRootCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;


/**
 * This dialog can be used to add a net folder root or modify a net folder root.
 * @author jwootton
 *
 */
public class ModifyNetFolderRootDlg extends DlgBox
	implements
		EditCanceledHandler,
		EditSuccessfulHandler,
		SearchFindResultsEvent.Handler
{
	private int m_numCols = 0;
	private NetFolderRoot m_netFolderRoot;	// If we are modifying a net folder this is the net folder.
	private TextBox m_nameTxtBox;
	private TextBox m_rootPathTxtBox;
	private TextBox m_proxyNameTxtBox;
	private PasswordTextBox m_proxyPwdTxtBox;
	private FindCtrl m_findCtrl;
	private FlexTable m_privilegedPrincipalsTable;
	private FlowPanel m_privilegedPrincipalsTablePanel;
	private List<HandlerRegistration> m_registeredEventHandlers;
	private FlexCellFormatter m_privilegedPrincipalsCellFormatter;
	private HTMLTable.RowFormatter m_privilegedPrincipalsRowFormatter;
	private ImageResource m_deleteImgR;

	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[]
    {
		// Search events.
		TeamingEvents.SEARCH_FIND_RESULTS,
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
	private enum PrincipalType
	{
		USER,
		GROUP
	}
	
	/**
	 * 
	 */
	private class PrivilegedPrincipal
	{
		String m_name;
		Long m_id;
		PrincipalType m_type;
		
		/**
		 * 
		 */
		public PrivilegedPrincipal( String name, Long id, PrincipalType type )
		{
			m_name = name;
			m_id = id;
			m_type = type;
		}
		
		/**
		 * 
		 */
		public boolean equals( PrivilegedPrincipal principal )
		{
			Long id;
			
			id = principal.getId();
			if ( m_id != null && id != null && m_id.compareTo( id ) == 0 )
				return true;
			
			return false;
		}
		
		/**
		 * 
		 */
		public Long getId()
		{
			return m_id;
		}
		
		/**
		 * 
		 */
		public String getName()
		{
			return m_name;
		}
		
		/**
		 * 
		 */
		public PrincipalType getType()
		{
			return m_type;
		}
		
		/**
		 * 
		 */
		public String getTypeAsString()
		{
			if ( m_type == PrincipalType.USER )
				return GwtTeaming.getMessages().modifyNetFolderRootDlg_User();

			if ( m_type == PrincipalType.GROUP )
				return GwtTeaming.getMessages().modifyNetFolderRootDlg_Group();
			
			return "Unknown principal type";
		}
	}
	
	/**
	 * This widget is used to display a principal's name.  If the principal is a group
	 * then the user can click on the name and see the members of the group.
	 */
	private class PrincipalNameWidget extends Composite
		implements ClickHandler, MouseOverHandler, MouseOutHandler
	{
		private PrivilegedPrincipal m_principal;
		private InlineLabel m_nameLabel;
		private GroupMembershipPopup m_groupMembershipPopup;
		
		/**
		 * 
		 */
		public PrincipalNameWidget( PrivilegedPrincipal principal )
		{
			FlowPanel panel;
			
			m_principal = principal;
			
			panel = new FlowPanel();
			
			m_nameLabel = new InlineLabel( principal.getName() );
			m_nameLabel.setTitle( principal.getName() );
			m_nameLabel.addStyleName( "modifyNetFolderRootDlg_PrincipalNameLabel" );
			panel.add( m_nameLabel );
			
			// If we are dealing with a group, let the user click on the group.
			if ( principal.getType() == PrincipalType.GROUP )
			{
				m_nameLabel.addClickHandler( this );
				m_nameLabel.addMouseOverHandler( this );
				m_nameLabel.addMouseOutHandler( this );
			}
			
			// All composites must call initWidget() in their constructors.
			initWidget( panel );
		}
		
		/**
		 * Close the group membership popup if it is open.
		 */
		public void closePopups()
		{
			if ( m_groupMembershipPopup != null )
				m_groupMembershipPopup.closePopups();
		}
		
		/**
		 * This gets called when the user clicks on the principal's name.  This will only
		 * be called if the principal is a group.
		 */
		@Override
		public void onClick( ClickEvent event )
		{
			// Create a popup that will display the membership of this group.
			if ( m_groupMembershipPopup == null )
			{
				m_groupMembershipPopup = new GroupMembershipPopup(
															false,
															false,
															m_principal.getName(),
															m_principal.getId().toString() );
			}
			
			m_groupMembershipPopup.setPopupPosition( getAbsoluteLeft(), getAbsoluteTop() );
			m_groupMembershipPopup.show();
		}
		
		/**
		 * Remove the mouse-over style from the name. 
		 */
		@Override
		public void onMouseOut( MouseOutEvent event )
		{
			m_nameLabel.removeStyleName( "modifyNetFolderRootDlg_NameHover" );
		}

		
		/**
		 * Add the mouse-over style to the name.
		 */
		@Override
		public void onMouseOver( MouseOverEvent event )
		{
			m_nameLabel.addStyleName( "modifyNetFolderRootDlg_NameHover" );
		}
	}
	

	/**
	 * This widget is used to remove a privileged principal from the list
	 */
	private class RemovePrincipalWidget extends Composite
		implements ClickHandler
	{
		private PrivilegedPrincipal m_principal;
		
		/**
		 * 
		 */
		public RemovePrincipalWidget( PrivilegedPrincipal principal )
		{
			FlowPanel panel;
			Image delImg;
			
			m_principal = principal;
			
			panel = new FlowPanel();
			panel.addStyleName( "modifyNetFolderRootDlg_RemovePrincipalPanel" );
			
			delImg = new Image( m_deleteImgR );
			delImg.addStyleName( "cursorPointer" );
			delImg.getElement().setAttribute( "title", GwtTeaming.getMessages().modifyNetFolderRootDlg_RemovePrincipalHint() );
			delImg.addClickHandler( this );
			
			panel.add( delImg );
			
			// All composites must call initWidget() in their constructors.
			initWidget( panel );
		}
		
		/**
		 * 
		 */
		public PrivilegedPrincipal getPrincipal()
		{
			return m_principal;
		}

		/**
		 * This gets called when the user clicks on the remove principal image.
		 */
		@Override
		public void onClick( ClickEvent event )
		{
			Scheduler.ScheduledCommand cmd;
			
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute() 
				{
					removePrincipal( m_principal );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
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
	 * Add the "No users or groups have been granted the right to use this net folder root" 
	 * text to the table that holds the list of shares.
	 */
	private void addNoPrivilegedPrincipalsMessage()
	{
		int row;
		
		row = 1;
		m_privilegedPrincipalsCellFormatter.setColSpan( row, 0, m_numCols );
		m_privilegedPrincipalsCellFormatter.setWordWrap( row, 0, false );
		m_privilegedPrincipalsCellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_privilegedPrincipalsCellFormatter.addStyleName( row, 0, "oltBorderRight" );
		m_privilegedPrincipalsCellFormatter.addStyleName( row, 0, "oltContentPadding" );
		m_privilegedPrincipalsCellFormatter.addStyleName( row, 0, "oltLastRowBorderBottom" );

		m_privilegedPrincipalsTable.setText( row, 0, GwtTeaming.getMessages().modifyNetFolderRootDlg_NoPrivilegedPrincipalsHint() );
	}
	
	/**
	 * Add the given principal to the first of the table that holds the list of privileged principals
	 */
	private void addPrivilegedPrincipal( PrivilegedPrincipal principal, boolean highlight )
	{
		String type;
		int row;
		int col;
		int i;
		RemovePrincipalWidget removeWidget;
		PrincipalNameWidget principalNameWidget;
		
		row = m_privilegedPrincipalsTable.getRowCount();
		
		// Do we have any principals in the table?
		if ( row == 2 )
		{
			String text;
			
			// Maybe
			// The first row might be the message, "No on has been given rights...""
			// Get the text from the first row.
			text = m_privilegedPrincipalsTable.getText( 1, 0 );
			
			// Does the first row contain a message?
			if ( text != null && text.equalsIgnoreCase( GwtTeaming.getMessages().modifyNetFolderRootDlg_NoPrivilegedPrincipalsHint() ) )
			{
				// Yes
				m_privilegedPrincipalsTable.removeRow( 1 );
			}
		}
		
		// Remove any highlight that may be on the first row.
		unhighlightPrincipal( 1 );
		
		// Add the principal as the first item in the table.
		row = 1;
		m_privilegedPrincipalsTable.insertRow( row );
		
		// Should we highlight the row?
		if ( highlight )
		{
			// Yes
			highlightPrincipal( row );
		}
		
		col = 0;
		
		// Add the principal name
		m_privilegedPrincipalsCellFormatter.setColSpan( row, col, 1 );
		m_privilegedPrincipalsCellFormatter.setWordWrap( row, col, false );
		m_privilegedPrincipalsCellFormatter.addStyleName( row, col, "modifyNetFolderRootDlg_PrivilegedPrincipalsTable_Cell" );
		principalNameWidget = new PrincipalNameWidget( principal );
		m_privilegedPrincipalsTable.setWidget( row, col,  principalNameWidget );
		++col;

		// Add the recipient type
		m_privilegedPrincipalsCellFormatter.setWordWrap( row, col, false );
		m_privilegedPrincipalsCellFormatter.addStyleName( row, col, "modifyNetFolderRootDlg_PrivilegedPrincipalsTable_Cell" );
		type = principal.getTypeAsString();
		m_privilegedPrincipalsTable.setText( row, col, type );
		++col;
		
		// Add the "remove principal" widget
		{
			removeWidget = new RemovePrincipalWidget( principal );
			m_privilegedPrincipalsTable.setWidget( row, col, removeWidget );
			++col;
		}
		
		// Add the necessary styles to the cells in the row.
		m_privilegedPrincipalsCellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_privilegedPrincipalsCellFormatter.addStyleName( row, m_numCols-1, "oltBorderRight" );
		for (i = 0; i < m_numCols; ++i)
		{
			m_privilegedPrincipalsCellFormatter.addStyleName( row, i, "oltContentBorderBottom" );
			m_privilegedPrincipalsCellFormatter.addStyleName( row, i, "oltContentPadding" );
		}
		
		adjustPrivilegedPrincipalsTablePanelHeight();
	}
	
	/**
	 * 
	 */
	private void adjustPrivilegedPrincipalsTablePanelHeight()
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				int height;
				
				// Get the height of the table that holds the list of shares.
				height = m_privilegedPrincipalsTable.getOffsetHeight();
				
				// If the height is greater than 200 pixels put an overflow auto on the panel
				// and give the panel a fixed height of 200 pixels.
				if ( height >= 200 )
					m_privilegedPrincipalsTablePanel.addStyleName( "modifyNetFolderRootDlg_PrivilegedPrincipalsTablePanelHeight" );
				else
					m_privilegedPrincipalsTablePanel.removeStyleName( "modifyNetFolderRootDlg_PrivilegedPrincipalsTablePanelHeight" );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel;
		final FlexTable table;
		FlowPanel spacerPanel;
		Label label;
		int nextRow;
		HTMLTable.RowFormatter rowFormatter;
		FlexCellFormatter cellFormatter;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		// Create a table to hold the controls.
		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );
		
		cellFormatter = table.getFlexCellFormatter();
		rowFormatter = table.getRowFormatter();
		
		nextRow = 0;
		
		// Create the controls for "Name"
		{
			label = new InlineLabel( messages.modifyNetFolderRootDlg_NameLabel() );
			table.setWidget( nextRow, 0, label );
			
			m_nameTxtBox = new TextBox();
			m_nameTxtBox.setVisibleLength( 30 );
			table.setWidget( nextRow, 1, m_nameTxtBox );
			++nextRow;
		}
		
		// Create the controls for "root path"
		{
			label = new InlineLabel( messages.modifyNetFolderRootDlg_RootPathLabel() );
			table.setWidget( nextRow, 0, label );
			
			m_rootPathTxtBox = new TextBox();
			m_rootPathTxtBox.setVisibleLength( 50 );
			table.setWidget( nextRow, 1, m_rootPathTxtBox );
			++nextRow;
		}
		
		// Create the controls used to enter proxy information
		{
			// Add some space
			spacerPanel = new FlowPanel();
			spacerPanel.getElement().getStyle().setMarginTop( 10, Unit.PX );
			table.setWidget( nextRow, 0, spacerPanel );
			++nextRow;
			
			label = new InlineLabel( messages.modifyNetFolderRootDlg_ProxyNameLabel() );
			table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
			
			m_proxyNameTxtBox = new TextBox();
			m_proxyNameTxtBox.setVisibleLength( 30 );
			table.setWidget( nextRow, 1, m_proxyNameTxtBox );
			++nextRow;
			
			label = new InlineLabel( messages.modifyNetFolderRootDlg_ProxyPwdLabel() );
			table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
			
			m_proxyPwdTxtBox = new PasswordTextBox();
			m_proxyPwdTxtBox.setVisibleLength( 30 );
			table.setWidget( nextRow, 1, m_proxyPwdTxtBox );
			++nextRow;
		}
		
		// Create the controls used to select who can create net folders using this
		// net folder root.
		{
			// Create the FindCtrl
			{
				final int findCtrlRow;
				
				// Add some space
				spacerPanel = new FlowPanel();
				spacerPanel.getElement().getStyle().setMarginTop( 10, Unit.PX );
				table.setWidget( nextRow, 0, spacerPanel );
				++nextRow;
				
				// Add a hint
				cellFormatter.setColSpan( nextRow, 0, 2 );
				cellFormatter.setWordWrap( nextRow, 0, false );
				cellFormatter.addStyleName( nextRow, 0, "modifyNetFolderRootDlg_SelectPrivelegedUsersHint" );
				label = new InlineLabel( messages.modifyNetFolderRootDlg_PrivilegedPrincipalsHint() );
				table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
				++nextRow;
				
				findCtrlRow = nextRow;
				cellFormatter.setColSpan( nextRow, 0, 2 );
				++nextRow;
				FindCtrl.createAsync( this, GwtSearchCriteria.SearchType.PRINCIPAL, new FindCtrlClient()
				{			
					@Override
					public void onUnavailable()
					{
						// Nothing to do.  Error handled in asynchronous provider.
					}
					
					@Override
					public void onSuccess( FindCtrl findCtrl )
					{
						m_findCtrl = findCtrl;
						m_findCtrl.setIsSendingEmail( true );
	
						// Set the filter of the Find Control to only search for users and groups.
						m_findCtrl.setSearchType( SearchType.PRINCIPAL );
	
						table.setWidget( findCtrlRow, 0, m_findCtrl );
					}
				});
			}
			
			// Create a table to hold the list of privileged principals
			{
				m_privilegedPrincipalsTablePanel = new FlowPanel();
				m_privilegedPrincipalsTablePanel.addStyleName( "modifyNetFolderRootDlg_PrivilegedPrincipalsTablePanel" );

				m_privilegedPrincipalsTable = new FlexTable();
				m_privilegedPrincipalsTable.addStyleName( "modifyNetFolderRootDlg_PrivilegedPrincipalsTable" );
				m_privilegedPrincipalsTable.setCellSpacing( 0 );

				m_privilegedPrincipalsTablePanel.add( m_privilegedPrincipalsTable );

				m_privilegedPrincipalsCellFormatter = m_privilegedPrincipalsTable.getFlexCellFormatter();

				rowFormatter.setVerticalAlign( nextRow, HasVerticalAlignment.ALIGN_TOP );
				table.setWidget( nextRow, 0, m_privilegedPrincipalsTablePanel );
				cellFormatter.setColSpan( nextRow, 0, 2 );
				++nextRow;

				setColumnHeaders();
			}
		}
		
		// Create an image resource for the delete image.
		m_deleteImgR = GwtTeaming.getImageBundle().delete();

		mainPanel.add( table );

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
				
				// Get the panel that holds the errors.
				errorPanel = getErrorPanel();
				errorPanel.clear();
				
				errMsg = GwtTeaming.getMessages().modifyNetFolderRootDlg_ErrorCreatingNetFolderRoot( caught.toString() );
				if ( caught instanceof GwtTeamingException )
				{
					GwtTeamingException ex;
					
					ex = (GwtTeamingException) caught;
					if ( ex.getExceptionType() == ExceptionType.NET_FOLDER_ROOT_ALREADY_EXISTS )
					{
						String desc;
						
						desc = GwtTeaming.getMessages().modifyNetFolderRootDlg_RootAlreadyExists();
						errMsg =GwtTeaming.getMessages().modifyNetFolderRootDlg_ErrorModifyingNetFolderRoot( desc );
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
			
			cmd = new CreateNetFolderRootCmd( netFolderRoot );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
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
		int i;
		
		// Go through the list of privileged principals and close any "Group Membership" popups that may be open.
		for (i = 1; i < m_privilegedPrincipalsTable.getRowCount(); ++i)
		{
			Widget widget;
			
			if ( m_privilegedPrincipalsTable.getCellCount( i ) > 2 )
			{
				// Get the PrincipalNameWidget from the first column.
				widget = m_privilegedPrincipalsTable.getWidget( i, 0 );
				if ( widget != null && widget instanceof PrincipalNameWidget )
				{
					// Close any group membership popup that this widget may have open.
					((PrincipalNameWidget) widget).closePopups();
				}
			}
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
	 * Find the given principal in the table that holds the privileged principals.
	 */
	private int findPrivilegedPrincipal( PrivilegedPrincipal principal )
	{
		int i;

		if ( principal == null )
			return -1;
		
		// Look through the table for the given principal.
		// Recipients start in row 1.
		for (i = 1; i < m_privilegedPrincipalsTable.getRowCount() && m_privilegedPrincipalsTable.getCellCount( i ) == m_numCols; ++i)
		{
			Widget widget;
			
			// Get the RemovePrincipalWidget from the last column.
			widget = m_privilegedPrincipalsTable.getWidget( i, m_numCols-1 );
			if ( widget != null && widget instanceof RemovePrincipalWidget )
			{
				PrivilegedPrincipal nextPrincipal;
				
				nextPrincipal = ((RemovePrincipalWidget) widget).getPrincipal();
				if ( nextPrincipal != null )
				{
					if ( principal.equals( nextPrincipal ) )
					{
						// We found the principal
						return i;
					}
				}
			}
		}// end for()
		
		// If we get here we did not find the principal.
		return -1;
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
	 * Create a NetFolderRoot object that holds the id of the net folder root being edited,
	 * and the net folder root's new info
	 */
	private NetFolderRoot getNetFolderRootFromDlg()
	{
		NetFolderRoot netFolderRoot = null;
		
		netFolderRoot = new NetFolderRoot();
		netFolderRoot.setName( getName() );
		netFolderRoot.setRootPath( getRootPath() );

		if ( m_netFolderRoot != null )
			netFolderRoot.setId( m_netFolderRoot.getId() );
		
		return netFolderRoot;
	}
	
	
	/**
	 * Return the name entered by the user.
	 */
	private String getName()
	{
		return m_nameTxtBox.getText();
	}
	
	/**
	 * Return the root path entered by the user.
	 */
	private String getRootPath()
	{
		return m_rootPathTxtBox.getText();
	}
	
	/**
	 * 
	 */
	private void highlightPrincipal( int row )
	{
		if ( row < m_privilegedPrincipalsTable.getRowCount() )
			m_privilegedPrincipalsRowFormatter.addStyleName( row, "modifyNetFolderRootDlg_PrivilegedPrincipalsTable_highlightRow" );
	}
	
	/**
	 * 
	 */
	public void init( NetFolderRoot netFolderRoot )
	{
		m_netFolderRoot = netFolderRoot;

		m_findCtrl.setInitialSearchString( "" );

		// Clear existing data in the controls.
		m_nameTxtBox.setText( "" );
		m_rootPathTxtBox.setText( "" );
		m_proxyNameTxtBox.setText( "" );
		m_proxyPwdTxtBox.setText( "" );
		
		hideErrorPanel();
		
		// Remove all of the rows from the table.
		// We start at row 1 so we don't delete the header.
		while ( m_privilegedPrincipalsTable.getRowCount() > 1 )
		{
			// Remove the 1st row that holds share information.
			m_privilegedPrincipalsTable.removeRow( 1 );
		}
		
		// Add a message to the table telling the user there are no privileged principals.
		addNoPrivilegedPrincipalsMessage();
		adjustPrivilegedPrincipalsTablePanelHeight();

		// Are we modifying an existing net folder root?
		if ( m_netFolderRoot != null )
		{
			// Yes
			// Update the dialog's header to say "Edit Net Folder Root"
			setCaption( GwtTeaming.getMessages().modifyNetFolderRootDlg_EditHeader( m_netFolderRoot.getName() ) );
			
			// Don't let the user edit the name.
			m_nameTxtBox.setText( netFolderRoot.getName() );
			m_nameTxtBox.setEnabled( false );
			
			m_rootPathTxtBox.setText( netFolderRoot.getRootPath() );
			m_proxyNameTxtBox.setText( netFolderRoot.getProxyName() );
			m_proxyPwdTxtBox.setText( netFolderRoot.getProxyPwd() );
		}
		else
		{
			// No
			// Update the dialog's header to say "Add Net Folder Root"
			setCaption( GwtTeaming.getMessages().modifyNetFolderRootDlg_AddHeader() );
			
			// Enable the "Name" field.
			m_nameTxtBox.setEnabled( true );
		}
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
			Window.alert( GwtTeaming.getMessages().modifyNetFolderRootDlg_NameRequired() );
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
				
				// Get the panel that holds the errors.
				errorPanel = getErrorPanel();
				errorPanel.clear();
				
				errMsg = GwtTeaming.getMessages().modifyNetFolderRootDlg_ErrorModifyingNetFolderRoot( caught.toString() );
				if ( caught instanceof GwtTeamingException )
				{
					GwtTeamingException ex;
					
					ex = (GwtTeamingException) caught;
					if ( ex.getExceptionType() == ExceptionType.ACCESS_CONTROL_EXCEPTION )
					{
						String desc;
						
						desc = GwtTeaming.getMessages().modifyNetFolderRootDlg_InsufficientRights();
						errMsg =GwtTeaming.getMessages().modifyNetFolderRootDlg_ErrorModifyingNetFolderRoot( desc );
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
				NetFolderRootModifiedEvent event;
				
				// Fire an event that lets everyone know this net folder root was modified.
				event = new NetFolderRootModifiedEvent( newNetFolderRoot );
				GwtTeaming.fireEvent( event );

				// Close this dialog.
				hide();
			}						
		};
		
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
	
	/**
	 * Handles SearchFindResultsEvent's received by this class.
	 * 
	 * Implements the SearchFindResultsEvent.Handler.onSearchFindResults() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchFindResults( SearchFindResultsEvent event )
	{
		final GwtTeamingItem selectedObj;
		Scheduler.ScheduledCommand cmd;

		// If the find results aren't for this share this dialog...
		if ( !((Widget) event.getSource()).equals( this ) )
		{
			// ...ignore the event.
			return;
		}
		
		selectedObj = event.getSearchResults();
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute() 
			{
				PrivilegedPrincipal principal = null;
				
				// Hide the search-results widget.
				m_findCtrl.hideSearchResults();
				
				// Clear the text from the find control.
				m_findCtrl.clearText();
				
				// Are we dealing with a User?
				if ( selectedObj instanceof GwtUser )
				{
					GwtUser user;
					
					// Yes
					user = (GwtUser) selectedObj;
					
					// Is this an external user?
					if ( user.getIdentitySource() == IdentitySource.EXTERNAL )
					{
						// Yes, tell the user they can't do this.
						Window.alert( GwtTeaming.getMessages().modifyNetFolderRootDlg_CantSelectExternalUser() );
						return;
					}
					
					principal = new PrivilegedPrincipal(
													user.getName(),
													Long.valueOf( user.getUserId() ),
													PrincipalType.USER );
				}
				// Are we dealing with a group?
				else if ( selectedObj instanceof GwtGroup )
				{
					GwtGroup group;
					
					// Yes
					group = (GwtGroup) selectedObj;

					principal = new PrivilegedPrincipal(
													group.getName(),
													Long.valueOf( group.getId() ),
													PrincipalType.GROUP );
				}

				// Do we have an principal to add to our list of privileged principals?
				if ( principal != null )
				{
					// Yes
					// Is this principal already in the list?
					if ( findPrivilegedPrincipal( principal ) == -1 )
					{
						// No
						// Add the principal to our list of privileged principals
						addPrivilegedPrincipal( principal, true );
					}
					else
					{
						// Yes, tell the user
						Window.alert( GwtTeaming.getMessages().modifyNetFolderRootDlg_AlreadyAPrivilegedPrincipal( principal.getName() ) );
					}
				}
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
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
	 * Remove the given privileged user from the table
	 */
	public void removePrincipal( PrivilegedPrincipal principal )
	{
		int row;
		
		// Find the row this principal lives in.
		row = findPrivilegedPrincipal( principal );
		
		// Did we find the principal in the table?
		if ( row > 0 )
		{
			// Yes
			// Remove the share from the table.
			m_privilegedPrincipalsTable.removeRow( row );

			// Did we remove the last share from the table?
			if ( m_privilegedPrincipalsTable.getRowCount() == 1 )
			{
				// Yes
				// Add the "no privileged users..." message to the table.
				addNoPrivilegedPrincipalsMessage();
			}
			
			adjustPrivilegedPrincipalsTablePanelHeight();
		}
	}
	
	/**
	 * Set the text in each of the header of each column.
	 */
	private void setColumnHeaders()
	{
		int col;

		// On IE calling m_cellFormatter.setWidth( 0, 2, "*" ); throws an exception.
		// That is why we are calling DOM.setElementAttribute(...) instead.

		m_privilegedPrincipalsRowFormatter = m_privilegedPrincipalsTable.getRowFormatter();
		m_privilegedPrincipalsRowFormatter.addStyleName( 0, "oltHeader" );

		m_privilegedPrincipalsCellFormatter = m_privilegedPrincipalsTable.getFlexCellFormatter();

		col = 0;
		m_privilegedPrincipalsTable.setText( 0, col, GwtTeaming.getMessages().modifyNetFolderRootDlg_NameCol() );
		DOM.setElementAttribute( m_privilegedPrincipalsCellFormatter.getElement( 0, col ), "width", "75%" );
		++col;
		
		m_privilegedPrincipalsTable.setText( 0, col, GwtTeaming.getMessages().modifyNetFolderRootDlg_TypeCol() );
		DOM.setElementAttribute( m_privilegedPrincipalsCellFormatter.getElement( 0, col ), "width", "20%" );
		++col;
		
		m_privilegedPrincipalsTable.setHTML( 0, col, "&nbsp;" );	// The delete image will go in this column.
		DOM.setElementAttribute( m_privilegedPrincipalsCellFormatter.getElement( 0, col ), "width", "14px" );
		++col;

		m_numCols = col;
		
		m_privilegedPrincipalsCellFormatter.addStyleName( 0, 0, "oltBorderLeft" );
		for (col=0; col < m_numCols; ++col)
		{
			m_privilegedPrincipalsCellFormatter.addStyleName( 0, col, "oltHeaderBorderTop" );
			m_privilegedPrincipalsCellFormatter.addStyleName( 0, col, "oltHeaderBorderBottom" );
			m_privilegedPrincipalsCellFormatter.addStyleName( 0, col, "oltHeaderPadding" );
		}
		m_privilegedPrincipalsCellFormatter.addStyleName( 0, m_numCols-1, "oltBorderRight" );
	}
	
	/**
	 * Unlighlight the given row in the table that holds the list of privileged principals
	 */
	private void unhighlightPrincipal( int row )
	{
		if ( row < m_privilegedPrincipalsTable.getRowCount() )
			m_privilegedPrincipalsRowFormatter.removeStyleName( row, "modifyNetFolderRootDlg_PrivilegedPrincipalsTable_highlightRow" );
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
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ModifyNetFolderRootDlg() );
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
