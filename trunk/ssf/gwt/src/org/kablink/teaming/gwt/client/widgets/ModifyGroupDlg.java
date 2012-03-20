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

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtDynamicGroupMembershipCriteria;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.GroupCreatedEvent;
import org.kablink.teaming.gwt.client.event.GroupCreationFailedEvent;
import org.kablink.teaming.gwt.client.event.GroupCreationStartedEvent;
import org.kablink.teaming.gwt.client.event.GroupModificationFailedEvent;
import org.kablink.teaming.gwt.client.event.GroupModificationStartedEvent;
import org.kablink.teaming.gwt.client.event.GroupModifiedEvent;
import org.kablink.teaming.gwt.client.mainmenu.GroupInfo;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.CreateGroupCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetDynamicMembershipCriteriaCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupMembershipCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupMembershipRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupMembershipTypeCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetIsDynamicGroupMembershipAllowedCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNumberOfMembersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.IntegerRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ModifyGroupCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;


/**
 * This dialog can be used to add a group or modify a group.
 * @author jwootton
 *
 */
public class ModifyGroupDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private GroupInfo m_groupInfo;	// If we are modifying a group this is the group.
	private List<GwtTeamingItem> m_groupMembership;
	private InlineLabel m_nameLabel;
	private TextBox m_nameTxtBox;
	private TextBox m_titleTxtBox;
	private TextArea m_descTextArea;
	private RadioButton m_staticRb;
	private RadioButton m_dynamicRb;
	private Button m_editMembershipBtn;
	private ModifyStaticMembershipDlg m_staticMembershipDlg;
	private ModifyDynamicMembershipDlg m_dynamicMembershipDlg;
	private GwtDynamicGroupMembershipCriteria m_dynamicMembershipCriteria;
	private int m_numDynamicMembers;
	private boolean m_dynamicMembershipAllowed;
	
	/**
	 * 
	 */
	public ModifyGroupDlg(
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
			m_nameLabel = new InlineLabel( messages.modifyGroupDlgNameLabel() );
			table.setWidget( nextRow, 0, m_nameLabel );
			
			m_nameTxtBox = new TextBox();
			m_nameTxtBox.setVisibleLength( 30 );
			table.setWidget( nextRow, 1, m_nameTxtBox );
			++nextRow;
		}
		
		// Create the controls for "Title"
		{
			table.setText( nextRow, 0, messages.modifyGroupDlgTitleLabel() );
			
			m_titleTxtBox = new TextBox();
			m_titleTxtBox.setVisibleLength( 30 );
			table.setWidget( nextRow, 1, m_titleTxtBox );
			++nextRow;
		}
		
		// Create the controls for "Description"
		{
			table.setText( nextRow, 0, messages.modifyGroupDlgDescriptionLabel() );
			++nextRow;
			
			m_descTextArea = new TextArea();
			m_descTextArea.setCharacterWidth( 50 );
			m_descTextArea.setVisibleLines( 5 );
			table.setWidget( nextRow, 0, m_descTextArea );
			cellFormatter.setColSpan( nextRow, 0, 2 );
			
			++nextRow;
		}
		
		// Create the controls for static and dynamic group membership
		{
			FlexTable table2;
			FlexCellFormatter cellFormatter2;
			ClickHandler clickHandler;

			table2 = new FlexTable();
			cellFormatter2 = table2.getFlexCellFormatter();
			
			// Add the radio buttons
			m_staticRb = new RadioButton( "membershipType", messages.modifyGroupDlgStaticLabel() );
			table2.setWidget( 0, 0, m_staticRb );
			m_dynamicRb = new RadioButton( "membershipType", messages.modifyGroupDlgDynamicLabel() );
			table2.setWidget( 1, 0, m_dynamicRb );
			
			// Add "Edit group membership" button
			m_editMembershipBtn = new Button( messages.modifyGroupDlgEditGroupMembershipLabel() );
			m_editMembershipBtn.addStyleName( "teamingButton" );
			m_editMembershipBtn.getElement().getStyle().setMarginLeft( 10, Unit.PX );
			table2.setWidget( 0, 1, m_editMembershipBtn );
			cellFormatter2.setRowSpan( 0, 1, 2 );
			
			clickHandler = new ClickHandler()
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
							// Invoke the "edit group membership" dialog.
							invokeEditGroupMembershipDlg();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
				
			};
			m_editMembershipBtn.addClickHandler( clickHandler );
			
			table.setWidget( nextRow, 0, table2 );
			cellFormatter.setColSpan( nextRow, 0, 2 );
			
			++nextRow;
		}
		
		mainPanel.add( table );

		return mainPanel;
	}
	
	/**
	 * Issue an rpc request to create a group.  If the rpc request is successful
	 * close this dialog.
	 */
	private void createGroupAndClose()
	{
		CreateGroupCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback;
		
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				GroupCreationFailedEvent event;
				GroupInfo newGroupInfo;
				
				// Create a GroupInfo object that will represent the group we are going to create.
				newGroupInfo = new GroupInfo();
				newGroupInfo.setId( Long.valueOf( -1 ) );
				newGroupInfo.setName( getGroupName() );
				newGroupInfo.setTitle( getGroupTitle() );
				newGroupInfo.setDesc( getGroupDesc() );
				
				// Fire an event that lets everyone know the group creation failed.
				event = new GroupCreationFailedEvent( newGroupInfo, caught );
				GwtTeaming.fireEvent( event );
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				GroupCreatedEvent event;
				GroupInfo groupInfo;
				
				groupInfo = (GroupInfo) result.getResponseData();
				
				// Fire an event that lets everyone know a group was created.
				event = new GroupCreatedEvent( groupInfo );
				GwtTeaming.fireEvent( event );
			}						
		};
		
		// Close this dialog.
		hide();

		// Fire an event that indicates we have started the process of creating a group
		{
			GroupCreationStartedEvent event;
			GroupInfo newGroupInfo;
			
			// Create a GroupInfo object that will represent the group we are going to create.
			newGroupInfo = new GroupInfo();
			newGroupInfo.setId( Long.valueOf( -1 ) );
			newGroupInfo.setName( getGroupName() );
			newGroupInfo.setTitle( getGroupTitle() );
			newGroupInfo.setDesc( getGroupDesc() );
			
			event = new GroupCreationStartedEvent( newGroupInfo );
			GwtTeaming.fireEvent( event );
		}
		
		// Issue an rpc request to create the group.
		cmd = new CreateGroupCmd( getGroupName(), getGroupTitle(), getGroupDesc(), getIsMembershipDynamic(), m_groupMembership, m_dynamicMembershipCriteria );
		GwtClientHelper.executeCommand( cmd, rpcCallback );
	}

	/**
	 * Disable the "edit group membership" button.  We will wait to enable this button until
	 * we have all the information we need.
	 */
	private void disableEditMembershipButton()
	{
		m_editMembershipBtn.setEnabled( false );
	}

	/**
	 * This gets called when the user presses ok.  If we are editing an existing group
	 * we will issue an rpc request to save the group and then call m_editSuccessfulHandler.
	 * If we are creating a new group we will issue an rpc request to create the new group
	 * and then call m_editSuccessfulHandler.
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		// Are we editing an existing group?
		if ( m_groupInfo != null )
		{
			// Yes, issue an rpc request to modify the group.  If the rpc request is
			// successful, close this dialog.
			modifyGroupAndClose();
		}
		else
		{
			// No, we are creating a new group.
			
			// Is the name entered by the user valid?
			if ( isNameValid() == false )
			{
				m_nameTxtBox.setFocus( true );
				return false;
			}
			
			// Issue an rpc request to create the group.  If the rpc request is successful,
			// close this dialog.
			createGroupAndClose();
		}
		
		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully create/modify a group.
		return false;
	}
	
	/**
	 * Enable the "edit group membership" button.  We will wait to enable this button until
	 * we have all the information we need.
	 */
	private void enableEditMembershipButton()
	{
		m_editMembershipBtn.setEnabled( true );
	}

	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		// Return something.  Doesn't matter what since we only have a close button.
		return Boolean.TRUE;
	}
	
	
	/**
	 * Issue an ajax request to get the ldap query of the group we are working with.
	 */
	private void getDynamicMembershipCriteria()
	{
		if ( m_groupInfo != null )
		{
			GetDynamicMembershipCriteriaCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback;
			
			rpcCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable caught )
				{
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_GetGroupLdapQuery() );
				}
	
				@Override
				public void onSuccess( VibeRpcResponse result )
				{
					m_dynamicMembershipCriteria = ((GwtDynamicGroupMembershipCriteria) result.getResponseData());
				}						
			};
			
			cmd = new GetDynamicMembershipCriteriaCmd( m_groupInfo.getId() );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		if ( m_groupInfo == null )
			return m_nameTxtBox;
		
		return m_titleTxtBox;
	}
	
	/**
	 * Return the description entered by the user.
	 */
	private String getGroupDesc()
	{
		return m_descTextArea.getText();
	}
	
	/**
	 * Create a GroupInfo object that holds the id of the group being edited, the group's
	 * new title and description.
	 */
	private GroupInfo getGroupInfo()
	{
		GroupInfo groupInfo = null;
		
		if ( m_groupInfo != null )
		{
			groupInfo = new GroupInfo();

			groupInfo.setId( m_groupInfo.getId() );
			groupInfo.setTitle( getGroupTitle() );
			groupInfo.setDesc( getGroupDesc() );
		}
		
		return groupInfo;
	}
	
	/**
	 * Issue an ajax request to get the membership of the group we are working with.
	 */
	private void getGroupMembership()
	{
		if ( m_groupInfo != null )
		{
			GetGroupMembershipCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback;
			
			rpcCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable caught )
				{
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_GetGroupMembership() );
				}
	
				@Override
				public void onSuccess( VibeRpcResponse result )
				{
					GetGroupMembershipRpcResponseData responseData;
					
					responseData = ((GetGroupMembershipRpcResponseData) result.getResponseData());
					m_groupMembership = responseData.getMembers();
					
					// Enable the "edit group membership" button.
					enableEditMembershipButton();
				}						
			};
			
			cmd = new GetGroupMembershipCmd( String.valueOf( m_groupInfo.getId() ) );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}
	
	/**
	 * Return the name entered by the user.
	 */
	private String getGroupName()
	{
		return m_nameTxtBox.getText();
	}
	
	/**
	 * Return the title entered by the user.
	 */
	private String getGroupTitle()
	{
		return m_titleTxtBox.getText();
	}
	
	/**
	 * Return whether membership is dynamic.
	 */
	private boolean getIsMembershipDynamic()
	{
		return m_dynamicRb.getValue();
	}
	
	/**
	 * Issue an ajax request to get the membership type (static or dynamic) for the group
	 * we are working with.
	 */
	private void getMembershipType()
	{
		if ( m_groupInfo != null )
		{
			GetGroupMembershipTypeCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback;
			
			rpcCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable caught )
				{
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_GetGroupMembershipType() );
				}
	
				@Override
				public void onSuccess( VibeRpcResponse result )
				{
					BooleanRpcResponseData responseData;
					
					responseData = ((BooleanRpcResponseData) result.getResponseData());
					m_groupInfo.setIsMembershipDynamic( responseData.getBooleanValue() );

					// Is the membership dynamic?
					if ( m_groupInfo.getIsMembershipDynamic() )
					{
						// Yes
						m_staticRb.setValue( false );
						m_dynamicRb.setValue( true );
						
						// Schedule an ajax request to get the membership criteria
						{
							Scheduler.ScheduledCommand cmd;
							
							cmd = new Scheduler.ScheduledCommand()
							{
								@Override
								public void execute()
								{
									getDynamicMembershipCriteria();
									getNumberOfMembers();
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					}
					else
					{
						// No
						m_staticRb.setValue( true );
						m_dynamicRb.setValue( false );
						
						// Schedule an ajax request to get the group membership
						{
							Scheduler.ScheduledCommand cmd;
							
							cmd = new Scheduler.ScheduledCommand()
							{
								@Override
								public void execute()
								{
									getGroupMembership();
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					}
				}						
			};
			
			cmd = new GetGroupMembershipTypeCmd( m_groupInfo.getId() );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}
	
	/**
	 * Issue an ajax request to get the number of members in this group.
	 */
	private void getNumberOfMembers()
	{
		if ( m_groupInfo != null )
		{
			GetNumberOfMembersCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback;
			
			rpcCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable caught )
				{
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_NumberOfMembers() );
				}
	
				@Override
				public void onSuccess( VibeRpcResponse result )
				{
					IntegerRpcResponseData responseData;
					
					responseData = (IntegerRpcResponseData) result.getResponseData();
					m_numDynamicMembers = responseData.getIntegerValue();
					
					// Enable the "edit group membership" button.
					enableEditMembershipButton();
				}						
			};
			
			cmd = new GetNumberOfMembersCmd( m_groupInfo.getId() );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}
	
	/**
	 * 
	 */
	public void init( GroupInfo groupInfo )
	{
		m_groupInfo = groupInfo;
		m_groupMembership = new ArrayList<GwtTeamingItem>();
		m_dynamicMembershipCriteria = new GwtDynamicGroupMembershipCriteria();
		m_numDynamicMembers = 0;
		m_dynamicMembershipAllowed = true;
		
		// Disable the "edit group membership" button.  We will enable it once we have more
		// information about this group.
		disableEditMembershipButton();
		
		// Issue an rpc request to see if dynamic group membership is allowed.
		isDynamicGroupMembershipAllowed();
		
		// Clear existing data in the controls.
		m_nameTxtBox.setText( "" );
		m_titleTxtBox.setText( "" );
		m_descTextArea.setText( "" );
		
		// Are we modifying an existing group?
		if ( m_groupInfo != null )
		{
			// Yes
			// Schedule an ajax request to get the membership type (dynamic or static).
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						getMembershipType();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
			
			// Update the dialog's header to say "Edit Group"
			setCaption( GwtTeaming.getMessages().modifyGroupDlgHeader( m_groupInfo.getTitle() ) );
			
			// Hide the "Name" field.
			m_nameLabel.setVisible( false );
			m_nameTxtBox.setVisible( false );
			
			m_titleTxtBox.setText( groupInfo.getTitle() );
			m_descTextArea.setText( groupInfo.getDesc() );
		}
		else
		{
			// Update the dialog's header to say "Add Group"
			setCaption( GwtTeaming.getMessages().addGroupDlgHeader() );
			
			// Show the "Name" field.
			m_nameLabel.setVisible( true );
			m_nameTxtBox.setVisible( true );
			
			// Default the membership to "static"
			m_staticRb.setValue( true );
			m_dynamicRb.setValue( false );
		}
	}
	
	/**
	 * 
	 */
	public void invokeEditGroupMembershipDlg()
	{
		// Is the group membership dynamic?
		if ( getIsMembershipDynamic() == false )
		{
			int x;
			int y;
			
			// No
			// Get the position of this dialog.
			x = getAbsoluteLeft() + 50;
			y = getAbsoluteTop() + 50;
			
			if ( m_staticMembershipDlg == null )
			{
				EditSuccessfulHandler handler;
				
				// Create a handler that will be called when the user presses Ok in the
				// ModifyStaticMembershipDlg.
				handler = new EditSuccessfulHandler()
				{
					@SuppressWarnings("unchecked")
					@Override
					public boolean editSuccessful( Object obj )
					{
						m_groupMembership = (List<GwtTeamingItem>) obj;
						return true;
					}
				};
				m_staticMembershipDlg = new ModifyStaticMembershipDlg( false, true, handler, null, x, y );
			}
			
			m_staticMembershipDlg.init( getGroupName(), m_groupMembership );
			m_staticMembershipDlg.setPopupPosition( x, y );
			m_staticMembershipDlg.show();
		}
		else
		{
			int x;
			int y;
			
			// Yes
			// Is dynamic group membership allowed?
			if ( m_dynamicMembershipAllowed == false )
			{
				// No, warn the user and let them proceed.
				Window.alert( GwtTeaming.getMessages().modifyGroupDlgDynamicGroupMembershipNotAllowed() );
			}

			x = getAbsoluteLeft() + 50;
			y = getAbsoluteTop() + 50;
			
			if ( m_dynamicMembershipDlg == null )
			{
				EditSuccessfulHandler handler;
				
				// Create a handler that will be called when the user presses ok in the
				// Modify dynamic membership dialog.
				handler = new EditSuccessfulHandler()
				{
					@Override
					public boolean editSuccessful( Object obj )
					{
						if ( obj instanceof GwtDynamicGroupMembershipCriteria )
							m_dynamicMembershipCriteria = (GwtDynamicGroupMembershipCriteria) obj;
						
						return true;
					}
				};
				
				m_dynamicMembershipDlg = new ModifyDynamicMembershipDlg( false, true, handler, null, x, y );
			}
			
			m_dynamicMembershipDlg.init( m_dynamicMembershipCriteria, m_numDynamicMembers );
			m_dynamicMembershipDlg.setPopupPosition( x, y );
			m_dynamicMembershipDlg.show();
		}
	}
	
	/**
	 * Issue an rpc request to see if dynamic group membership is allowed.
	 */
	private void isDynamicGroupMembershipAllowed()
	{
		GetIsDynamicGroupMembershipAllowedCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback;
		
		// Yes
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_GetIsDynamicGroupMembershipAllowed() );
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				BooleanRpcResponseData responseData;
				
				responseData = (BooleanRpcResponseData) result.getResponseData();
				m_dynamicMembershipAllowed = responseData.getBooleanValue();
			}						
		};
		
		// Issue an rpc request to see if dynamic group membership is allowed.
		cmd = new GetIsDynamicGroupMembershipAllowedCmd();
		GwtClientHelper.executeCommand( cmd, rpcCallback );
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
			Window.alert( GwtTeaming.getMessages().modifyGroupDlgNameRequired() );
			return false;
		}
		
		if ( value.length() > 128 )
		{
			Window.alert( GwtTeaming.getMessages().modifyGroupDlgNameTooLong() );
			return false;
		}
		
		return true;
	}
	
	/**
	 * Issue an rpc request to modify the group.  If the rpc request was successful
	 * close this dialog.
	 */
	private void modifyGroupAndClose()
	{
		final GroupInfo newGroupInfo;
		ModifyGroupCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback;

		// Create a GroupInfo object that holds the new title and description
		newGroupInfo = getGroupInfo();
		
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				GroupModificationFailedEvent event;
				
				// Fire an event that lets everyone know the group modification failed.
				event = new GroupModificationFailedEvent( newGroupInfo, caught );
				GwtTeaming.fireEvent( event );
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				GroupModifiedEvent event;
				
				// Fire an event that lets everyone know this group was modified.
				event = new GroupModifiedEvent( newGroupInfo );
				GwtTeaming.fireEvent( event );
			}						
		};
		
		// Close this dialog.
		hide();

		// Fire an event that indicates we have started the group modification
		{
			GroupModificationStartedEvent event;
			
			event = new GroupModificationStartedEvent( newGroupInfo );
			GwtTeaming.fireEvent( event );
		}
		
		// Issue an rpc request to update the group.
		cmd = new ModifyGroupCmd( m_groupInfo.getId(), getGroupTitle(), getGroupDesc(), getIsMembershipDynamic(), m_groupMembership, m_dynamicMembershipCriteria );
		GwtClientHelper.executeCommand( cmd, rpcCallback );
	}
}
