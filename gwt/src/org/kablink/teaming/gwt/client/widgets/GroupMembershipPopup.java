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

import java.util.List;

import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupMembershipCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupMembershipRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.IsAllUsersGroupCmd;
import org.kablink.teaming.gwt.client.rpc.shared.IsAllUsersGroupRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This class displays the membership of the given group.
 * 
 * @author jwootton@novell.com
 */
public class GroupMembershipPopup extends TeamingPopupPanel
{
	private String m_groupId;
	private FlexTable m_membersTable;
	private FlowPanel m_membersTablePanel;
	private FlexCellFormatter m_cellFormatter;
	private AsyncCallback<VibeRpcResponse> m_getGroupMembershipCallback;
	private AsyncCallback<VibeRpcResponse> m_isAllUsersGroupCallback;
	
	/**
	 * This widget is used to display a group members's name.  If the member is a group
	 * then the user can click on the name and see the members of the group.
	 */
	private class GroupMemberNameWidget extends Composite
		implements ClickHandler, MouseOverHandler, MouseOutHandler
	{
		private InlineLabel m_nameLabel;
		private GroupMembershipPopup m_groupMembershipPopup;
		private GwtGroup m_group;
		
		/**
		 * 
		 */
		public GroupMemberNameWidget( GwtTeamingItem member )
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			
			m_nameLabel = new InlineLabel( member.getShortDisplayName() );
			panel.add( m_nameLabel );
			
			// If we are dealing with a group, let the user click on the group.
			if ( member instanceof GwtGroup )
			{
				m_group = (GwtGroup) member;
				
				m_nameLabel.addClickHandler( this );
				m_nameLabel.addMouseOverHandler( this );
				m_nameLabel.addMouseOutHandler( this );
			}
			
			// All composites must call initWidget() in their constructors.
			initWidget( panel );
		}
		
		/**
		 * Close any popups we may have open.
		 */
		public void closePopups()
		{
			if ( m_groupMembershipPopup != null )
				m_groupMembershipPopup.closePopups();
			
			// Hide this popup.
			hide();
		}

		/**
		 * This gets called when the user clicks on the group member's name.  This will only
		 * be called if the group member is a group.
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
															m_group.getName(),
															m_group.getId() );
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
			m_nameLabel.removeStyleName( "groupMembershipNameHover" );
		}

		
		/**
		 * Add the mouse-over style to the name.
		 */
		@Override
		public void onMouseOver( MouseOverEvent event )
		{
			m_nameLabel.addStyleName( "groupMembershipNameHover" );
		}
	}
	
	/**
	 * 
	 */
	public GroupMembershipPopup( boolean autoHide, boolean modal, String groupName, String groupId )
	{
		super( autoHide, modal );
	
		FlowPanel mainPanel;
	
		m_groupId = groupId;
		
		// Tell this popup to 'roll down' when opening. 
		GwtClientHelper.rollDownPopup( this );
		
		// Override the style used for PopupPanel
		setStyleName( "groupMembershipPopup" );
	
		mainPanel = new FlowPanel();
		
		// Create a top panel.
		{
			FlowPanel topPanel;
			InlineLabel groupNameLabel;

			topPanel = new FlowPanel();
			topPanel.addStyleName( "paddingBottom8px" );
			
			// Add a label that will hold the groups name.
			groupNameLabel = new InlineLabel( groupName );
			groupNameLabel.addStyleName( "groupMembershipGroupNameLabel" );
			topPanel.add( groupNameLabel );
			
			// Add an image the user can click on to close this popup
			{
				Image closeImg;
				ImageResource closeImgResource;
				ClickHandler clickHandler;
				
				// Create an image the user can click on to close this popup
				closeImgResource = GwtTeaming.getImageBundle().closeX();
				closeImg = new Image( closeImgResource );
				closeImg.addStyleName( "closeGroupMembershipPopupImg" );
				closeImg.getElement().setAttribute( "title", GwtTeaming.getMessages().close() );

				// Add a click handler to the close image.
				clickHandler = new ClickHandler()
				{
					@Override
					public void onClick( ClickEvent clickEvent )
					{
						GwtClientHelper.deferCommand( new ScheduledCommand()
						{
							@Override
							public void execute()
							{
								hide();
							}
						} );
					}
				};
				closeImg.addClickHandler( clickHandler );
				
				topPanel.add( closeImg );
			}
		
			mainPanel.add( topPanel );
		}
		
		// Create a table to hold the list of group members
		{
			HTMLTable.RowFormatter rowFormatter;
			
			m_membersTablePanel = new FlowPanel();
			m_membersTablePanel.addStyleName( "groupMembershipTablePanel" );
			
			m_membersTable = new FlexTable();
			m_membersTable.addStyleName( "groupMembershipTable" );
			m_membersTable.setCellSpacing( 0 );

			// Add the column headers.
			{
				m_membersTable.setText( 0, 0, GwtTeaming.getMessages().shareName() );
				m_membersTable.setText( 0, 1, GwtTeaming.getMessages().shareRecipientType() );
				m_membersTable.setHTML( 0, 2, "&nbsp;" );
				
				rowFormatter = m_membersTable.getRowFormatter();
				rowFormatter.addStyleName( 0, "oltHeader" );

				m_cellFormatter = m_membersTable.getFlexCellFormatter();
				// On IE calling m_cellFormatter.setWidth( 0, 2, "*" ); throws an exception.
				// That is why we are calling DOM.setElementAttribute(...) instead.
				//~JW:  m_cellFormatter.setWidth( 0, 2, "*" );
				m_cellFormatter.getElement( 0, 2 ).setAttribute( "width", "*" );
				
				m_cellFormatter.addStyleName( 0, 0, "oltBorderLeft" );
				m_cellFormatter.addStyleName( 0, 0, "oltHeaderBorderTop" );
				m_cellFormatter.addStyleName( 0, 0, "oltHeaderBorderBottom" );
				m_cellFormatter.addStyleName( 0, 0, "oltHeaderPadding" );
				m_cellFormatter.addStyleName( 0, 1, "oltHeaderBorderTop" );
				m_cellFormatter.addStyleName( 0, 1, "oltHeaderBorderBottom" );
				m_cellFormatter.addStyleName( 0, 1, "oltHeaderPadding" );
				m_cellFormatter.addStyleName( 0, 2, "oltBorderRight" );
				m_cellFormatter.addStyleName( 0, 2, "oltHeaderBorderTop" );
				m_cellFormatter.addStyleName( 0, 2, "oltHeaderBorderBottom" );
				m_cellFormatter.addStyleName( 0, 2, "oltHeaderPadding" );
			}
			
			m_membersTablePanel.add( m_membersTable );
			mainPanel.add( m_membersTablePanel );
		}
		
		// Schedule an ajax request to get the group membership
		{
			GwtClientHelper.deferCommand( new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					getGroupMembership();
				}
			} );
		}
		
		setWidget( mainPanel );
	}

	/**
	 * Add the given group member to the end of the table that holds the list of group members.
	 */
	private void addGroupMember( GwtTeamingItem member )
	{
		GroupMemberNameWidget nameWidget;
		String type;
		int row;
		
		// Don't add disabled users to the list.
		if ( member instanceof GwtUser )
		{
			if ( ((GwtUser)member).isDisabled() )
				return;
		}
		
		row = m_membersTable.getRowCount();
		
		// Do we have any members in the table?
		if ( row == 2 )
		{
			String text;
			
			// Maybe
			// The first row might be the message, "No members"
			// Get the text from the first row.
			text = m_membersTable.getText( 1, 0 );
			
			// Does the first row contain a message?
			if ( text != null && text.equalsIgnoreCase( GwtTeaming.getMessages().noGroupMembers() ) )
			{
				// Yes
				m_membersTable.removeRow( 1 );
				--row;
			}
		}
		
		// Add the member as the first member in the table.
		row = 1;
		m_membersTable.insertRow( row );
		
		// Add the member name in the first column.
		m_cellFormatter.setColSpan( row, 0, 1 );
		nameWidget = new GroupMemberNameWidget( member );
		m_membersTable.setWidget( row, 0,  nameWidget );

		// Add the member type in the second column.
		if ( member instanceof GwtUser )
			type = GwtTeaming.getMessages().shareRecipientTypeUser();
		else if ( member instanceof GwtGroup )
			type = GwtTeaming.getMessages().shareRecipientTypeGroup();
		else
			type = GwtTeaming.getMessages().unknownGroupMemberType();
		m_membersTable.setText( row, 1, type );

		// Add the necessary styles to the cells in the row.
		m_cellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_cellFormatter.addStyleName( row, 2, "oltBorderRight" );
		m_cellFormatter.addStyleName( row, 0, "oltContentBorderBottom" );
		m_cellFormatter.addStyleName( row, 1, "oltContentBorderBottom" );
		m_cellFormatter.addStyleName( row, 2, "oltContentBorderBottom" );
		m_cellFormatter.addStyleName( row, 0, "oltContentPadding" );
		m_cellFormatter.addStyleName( row, 1, "oltContentPadding" );
		m_cellFormatter.addStyleName( row, 2, "oltContentPadding" );
		
		adjustGroupMembershipTablePanelHeight();
	}
	
	/**
	 * Add the text "The "All users" group contains a list of all registered users, not including the "Guest" user account" 
	 * to the table that holds the list of group members.
	 */
	private void addAllInternalUsersGroupMessage()
	{
		int row;
		
		row = 1;
		m_cellFormatter.setColSpan( row, 0, 3 );
		m_cellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_cellFormatter.addStyleName( row, 0, "oltBorderRight" );
		m_cellFormatter.addStyleName( row, 0, "oltContentPadding" );
		m_cellFormatter.addStyleName( row, 0, "oltLastRowBorderBottom" );

		m_membersTable.setText( row, 0, GwtTeaming.getMessages().allUsersGroupDesc() );
	}
	
	
	/**
	 * Add the text "The "All external users" group contains a list of all external users" 
	 * to the table that holds the list of group members.
	 */
	private void addAllExternalUsersGroupMessage()
	{
		int row;
		
		row = 1;
		m_cellFormatter.setColSpan( row, 0, 3 );
		m_cellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_cellFormatter.addStyleName( row, 0, "oltBorderRight" );
		m_cellFormatter.addStyleName( row, 0, "oltContentPadding" );
		m_cellFormatter.addStyleName( row, 0, "oltLastRowBorderBottom" );

		m_membersTable.setText( row, 0, GwtTeaming.getMessages().allExtUsersGroupDesc() );
	}
	
	
	/**
	 * Add the "This group does not have any members" text to the table
	 * that holds the list of group memembers.
	 */
	private void addNoMembersMessage()
	{
		int row;
		
		row = 1;
		m_cellFormatter.setColSpan( row, 0, 3 );
		m_cellFormatter.addStyleName( row, 0, "oltBorderLeft" );
		m_cellFormatter.addStyleName( row, 0, "oltBorderRight" );
		m_cellFormatter.addStyleName( row, 0, "oltContentPadding" );
		m_cellFormatter.addStyleName( row, 0, "oltLastRowBorderBottom" );

		m_membersTable.setText( row, 0, GwtTeaming.getMessages().noGroupMembers() );
	}
	
	
	/**
	 * 
	 */
	private void adjustGroupMembershipTablePanelHeight()
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				int height;
				
				// Get the height of the table that holds the list of recipients.
				height = m_membersTable.getOffsetHeight();
				
				// If the height is greater than 150 pixels put an overflow auto on the panel
				// and give the panel a fixed height of 150 pixels.
				if ( height >= 150 )
					m_membersTablePanel.addStyleName( "groupMembershipTablePanelHeight" );
				else
					m_membersTablePanel.removeStyleName( "groupMembershipTablePanelHeight" );
			}
		} );
	}
	
	/**
	 * Close this popup but first close any popups that we may have opened.
	 */
	public void closePopups()
	{
		int i;
		
		// Go through the list of members and close any "Group Membership" popups that may be open.
		for (i = 1; i < m_membersTable.getRowCount(); ++i)
		{
			Widget widget;
			
			if ( m_membersTable.getCellCount( i ) > 1 )
			{
				// Get the GroupMemberNameWidget from the first column.
				widget = m_membersTable.getWidget( i, 0 );
				if ( widget != null && widget instanceof GroupMemberNameWidget )
				{
					// Close any group membership popup that this widget may have open.
					((GroupMemberNameWidget) widget).closePopups();
				}
			}
		}
		
		// Hide this popup.
		hide();
	}
	
	/**
	 * Issue an ajax request to get the membership of the group we are working with.
	 */
	private void getGroupMembership()
	{
		if ( m_getGroupMembershipCallback == null )
		{
			m_getGroupMembershipCallback = new AsyncCallback<VibeRpcResponse>()
			{

				@Override
				public void onFailure( Throwable caught )
				{
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_GetGroupMembership() );
				}// end onFailure()

				@Override
				public void onSuccess( VibeRpcResponse result )
				{
					GetGroupMembershipRpcResponseData responseData = ((GetGroupMembershipRpcResponseData) result.getResponseData());
					List<GwtTeamingItem> results = responseData.getMembers();
					boolean haveMember = false;
					
					// Add each member to this popup
					for ( GwtTeamingItem nextMember : results )
					{
						addGroupMember( nextMember );
						haveMember = true;
					}
					
					// If there aren't any members in this group, add some text indicating that.
					if ( !haveMember )
						addNoMembersMessage();
				}// end onSuccess()						
			}; 
		}
		
		if ( m_isAllUsersGroupCallback == null )
		{
			m_isAllUsersGroupCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable caught )
				{
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_IsAllUsersGroup() );
				}// end onFailure()

				@Override
				public void onSuccess( VibeRpcResponse result )
				{
					// Are we dealing with an "all users" group?
					IsAllUsersGroupRpcResponseData responseData = ((IsAllUsersGroupRpcResponseData) result.getResponseData());
					if ( responseData.isAllExternalUsersGroup() )
					{
						addAllExternalUsersGroupMessage();
					}
					else if ( responseData.isAllInternalUsersGroup() )
					{
						addAllInternalUsersGroupMessage();
					}
					else
					{
						GwtClientHelper.deferCommand( new ScheduledCommand()
						{
							@Override
							public void execute()
							{
								// No
								// Issue an ajax request to get the membership of this group.
								GetGroupMembershipCmd cmd = new GetGroupMembershipCmd( m_groupId );
								GwtClientHelper.executeCommand( cmd, m_getGroupMembershipCallback );
							}// end execute()
						} );
					}
				}// end onSuccess()			
			}; 
		}
		
		// Issue an ajax request to see if this group is the "all users" or the "all external users" group.  If it is
		// If it isn't, we will make another ajax request to get the group membership.
		IsAllUsersGroupCmd cmd = new IsAllUsersGroupCmd( m_groupId );
		GwtClientHelper.executeCommand( cmd, m_isAllUsersGroupCallback );		
	}
	
	/**
	 * Shows this popup.
	 */
	@Override
	public void show()
	{
		super.show();

		// ...and add vertical scrolling to the main frame for the
		// ...duration of the popup.
		GwtClientHelper.scrollUIForPopup( this );
	}	
}

