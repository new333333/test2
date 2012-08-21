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


import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.NetFolderRoot;
import org.kablink.teaming.gwt.client.event.NetFolderRootCreatedEvent;
import org.kablink.teaming.gwt.client.event.NetFolderRootModifiedEvent;
import org.kablink.teaming.gwt.client.rpc.shared.CreateNetFolderRootCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ModifyNetFolderRootCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;


/**
 * This dialog can be used to add a net folder root or modify a net folder root.
 * @author jwootton
 *
 */
public class ModifyNetFolderRootDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private NetFolderRoot m_netFolderRoot;	// If we are modifying a net folder this is the net folder.
	private TextBox m_nameTxtBox;
	private TextBox m_rootPathTxtBox;

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
	private ModifyNetFolderRootDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( "", this, null, null ); 
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel;
		FlexTable table;
		Label label;
		int nextRow;
		FlexCellFormatter cellFormatter;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		// Create a table to hold the controls.
		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );
		
		cellFormatter = table.getFlexCellFormatter();
		
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
	public void init( NetFolderRoot netFolderRoot )
	{
		m_netFolderRoot = netFolderRoot;
		
		// Clear existing data in the controls.
		m_nameTxtBox.setText( "" );
		m_rootPathTxtBox.setText( "" );
		
		hideErrorPanel();
		
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
