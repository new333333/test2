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

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.datatable.VibeCellTable;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupMembershipCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupMembershipCmd.MembershipFilter;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupMembershipRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

/**
 * This dialog can be used to view the dynamic membership of a group.
 * 
 * @author jwootton
 */
public class ShowDynamicMembershipDlg extends DlgBox
{
	private TabPanel m_tabPanel;
	private CellTable<GwtTeamingItem> m_userTable;
	private AsyncDataProvider<GwtTeamingItem> m_userDataProvider;
	private VibeSimplePager m_userPager;
	private Long m_groupId;

	/**
	 * 
	 */
	public ShowDynamicMembershipDlg(
			boolean autoHide,
			boolean modal,
			int xPos, 
			int yPos ) 
	{
		super( autoHide, modal, xPos, yPos, DlgButtonMode.Close );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().showDynamicMembershipDlgHeader(), null, null, null );
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props ) 
	{
		GwtTeamingMessages messages;
		Panel mainPanel;

		messages = GwtTeaming.getMessages();

		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		m_tabPanel = new TabPanel();
		m_tabPanel.addStyleName( "vibe-tabPanel" );

		// Add the "User" tab
		{
			Panel userPanel;

			// Create the panel used to add/remove users
			userPanel = createUserPanel();

			m_tabPanel.add( userPanel, messages.showDynamicMembershipDlgUserTab() );
		}

		mainPanel.add( m_tabPanel );

		return mainPanel;
	}

	/**
	 * Create the panel that holds the ui for adding/removing users from the
	 * group
	 */
	private Panel createUserPanel() 
	{
		VerticalPanel mainPanel;
		GwtTeamingMessages messages;
		CellTable.Resources cellTableResources;
		TextColumn<GwtTeamingItem> nameCol;

		messages = GwtTeaming.getMessages();

		mainPanel = new VerticalPanel();

		// Create the CellTable that will display the list of users.
		cellTableResources = GWT.create( VibeCellTable.VibeCellTableResources.class );
		m_userTable = new CellTable<GwtTeamingItem>( 10, cellTableResources );
		m_userTable.setWidth( "100%" );

		// Set the widget that will be displayed when there are no groups
		{
			FlowPanel flowPanel;
			InlineLabel noGroupsLabel;

			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "noObjectsFound" );
			noGroupsLabel = new InlineLabel( GwtTeaming.getMessages().modifyStaticMembershipDlgNoUsersLabel() );
			flowPanel.add( noGroupsLabel );

			m_userTable.setEmptyTableWidget( flowPanel );
		}

		// Add the "Name" column
		nameCol = new TextColumn<GwtTeamingItem>() 
		{
			@Override
			public String getValue( GwtTeamingItem teamingItem ) 
			{
				String name = "";

				if ( teamingItem instanceof GwtUser )
				{
					GwtUser user;
					
					user = (GwtUser) teamingItem;
					name = getUserDisplayName( user );
				}

				return name;
			}
		};
		m_userTable.addColumn( nameCol, messages.modifyStaticMembershipDlgNameCol() );

		// Create a pager
		{
			m_userPager = new VibeSimplePager();
			m_userPager.setDisplay( m_userTable );
		}

		mainPanel.add( m_userTable );
		mainPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
		mainPanel.add( m_userPager );

		return mainPanel;
	}

	/**
	 * Create a list of all the users and groups.
	 */
	@Override
	public Object getDataFromDlg() 
	{
		return Boolean.TRUE;
	}

	/**
	 * Return the widget that should get the focus when the dialog is shown.
	 */
	@Override
	public FocusWidget getFocusWidget() 
	{
		return null;
	}

	/**
	 * Get the display name for the given user
	 */
	private static String getUserDisplayName( GwtUser user ) 
	{
		String name;

		name = user.getTitle();
		if ( name == null || name.length() == 0 )
			name = user.getName();

		return name;
	}

	/**
	 * Issue an rpc request to get a list of users in the given range.
	 */
	private void getListOfUsers( final int start, int length )
	{
		if ( m_groupId != null )
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
					List<GwtTeamingItem> membershipList;
					
					responseData = ((GetGroupMembershipRpcResponseData) result.getResponseData());
					membershipList = responseData.getMembers();
					
					// Push the list to the displays.
					m_userDataProvider.updateRowData( start, membershipList );

					// Tell the table how many users we have.
					m_userTable.setRowCount( responseData.getTotalNumberOfMembers(), true );
				}						
			};
			
			cmd = new GetGroupMembershipCmd( String.valueOf( m_groupId ) );
			cmd.setOffset( start );
			cmd.setNumResults( length );
			cmd.setFilter( MembershipFilter.RETRIEVE_USERS_ONLY );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}		
	}
	
	/**
	 * 
	 */
	public void init( String groupName, Long groupId ) 
	{
		m_groupId = groupId;
		
		// Initialize the user CellTable information
		{
			if ( m_userDataProvider == null ) 
			{
				m_userDataProvider = new AsyncDataProvider<GwtTeamingItem>()
				{
					@Override
					protected void onRangeChanged( HasData<GwtTeamingItem> display ) 
					{
						Range range;
						
						range = display.getVisibleRange();
						
						// Issue an rpc request to get the list of users for the given range.
						getListOfUsers( range.getStart(), range.getLength() );
					}
					
				};
				m_userDataProvider.addDataDisplay( m_userTable );
			} 
			else 
			{
				Range[] ranges;
				
				ranges = m_userDataProvider.getRanges();
				if ( ranges != null && ranges.length == 1 )
				{
					// Issue an rpc request to get the list of users.
					getListOfUsers( 0, ranges[0].getLength() );
				}
			}

			// Go to the first page
			m_userPager.firstPage();
		}

		// Select the "Users" tab
		m_tabPanel.selectTab( 0 );
	}
}
