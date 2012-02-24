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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.mainmenu.GroupInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetAllGroupsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;


/**
 * 
 * @author jwootton
 *
 */
public class ManageGroupsDlg extends DlgBox
{
	private CellTable<GroupInfo> m_groupsTable;
    private MultiSelectionModel<GroupInfo> m_selectionModel;
	private ListDataProvider<GroupInfo> m_dataProvider;
	private SimplePager m_pager;
    private int m_width;
    private int m_height;
	
	/**
	 * 
	 */
	public ManageGroupsDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height )
	{
		super( autoHide, modal, xPos, yPos, DlgButtonMode.Close );
		
		// Create the header, content and footer of this dialog box.
		m_width = width;
		m_height = height;
		createAllDlgContent( GwtTeaming.getMessages().manageGroupsDlgHeader(), null, null, null ); 
	}

	/**
	 * Add the given list of groups to the dialog
	 */
	private void addGroups( List<GroupInfo> listOfGroups )
	{
		if ( listOfGroups == null )
		{
			m_groupsTable.setRowCount( 0 );
			return;
		}
	
		if ( m_dataProvider == null )
		{
			m_dataProvider = new ListDataProvider<GroupInfo>( listOfGroups );
			m_dataProvider.addDataDisplay( m_groupsTable );
		}
		else
		{
			m_dataProvider.setList( listOfGroups );
			m_dataProvider.refresh();
		}
		
		m_pager.firstPage();
		m_groupsTable.setRowCount( listOfGroups.size(), true );
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		VerticalPanel mainPanel = null;
		TextColumn<GroupInfo> titleCol;
		FlowPanel menuPanel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		// Create a menu
		{
			InlineLabel label;
			
			menuPanel = new FlowPanel();
			menuPanel.addStyleName( "groupManagementMenuPanel" );
			
			label = new InlineLabel( messages.manageGroupsDlgAddGroupLabel() );
			label.addStyleName( "groupManagementBtn" );
			label.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							invokeAddGroupDlg();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
			
			label = new InlineLabel( messages.manageGroupsDlgDeleteGroupLabel() );
			label.addStyleName( "groupManagementBtn" );
			label.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							deleteSelectedGroups();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
		}
		
		m_groupsTable = new CellTable<GroupInfo>();
		m_groupsTable.getElement().getStyle().setWidth( m_width, Unit.PX );
		m_groupsTable.getElement().getStyle().setHeight( m_height, Unit.PX );
		
	    // Add a selection model so we can select groups.
	    m_selectionModel = new MultiSelectionModel<GroupInfo>();
	    m_groupsTable.setSelectionModel( m_selectionModel, DefaultSelectionEventManager.<GroupInfo> createCheckboxManager() );

		// Add a checkbox in the first column
		{
			Column<GroupInfo, Boolean> ckboxColumn;
			CheckboxCell ckboxCell;
			
            ckboxCell = new CheckboxCell( true, false );
		    ckboxColumn = new Column<GroupInfo, Boolean>( ckboxCell )
            {
            	@Override
		        public Boolean getValue( GroupInfo groupInfo )
		        {
            		// Get the value from the selection model.
		            return m_selectionModel.isSelected( groupInfo );
		        }
		    };
	        m_groupsTable.addColumn( ckboxColumn, SafeHtmlUtils.fromSafeConstant( "<br/>" ) );
		    m_groupsTable.setColumnWidth( ckboxColumn, 20, Unit.PX );			
		}
		
		// Add the "Title" column
		titleCol = new TextColumn<GroupInfo>()
		{
			@Override
			public String getValue( GroupInfo groupInfo )
			{
				String name;
				String title;
				String value;
				
				title = groupInfo.getTitle();
				if ( title == null )
					value = "";
				else
					value = title;
				
				name = groupInfo.getName();
				if ( name != null )
					value += " (" + name + ")";
				
				return value;
			}
		};
		m_groupsTable.addColumn( titleCol, messages.manageGroupsDlgTitleCol() );
		
		// Create a pager
		{
			SimplePager.Resources pagerResources;

			pagerResources = GWT.create( SimplePager.Resources.class );
			m_pager = new SimplePager( TextLocation.CENTER, pagerResources, false, 0, true );
			m_pager.setDisplay( m_groupsTable );
		}

		mainPanel.add( menuPanel );
		mainPanel.add( m_groupsTable );
		mainPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
		mainPanel.add( m_pager );

		return mainPanel;
	}
	

	/**
	 * Delete the selected groups.
	 */
	private void deleteSelectedGroups()
	{
		Set<GroupInfo> selectedGroups;
		
		selectedGroups = getSelectedGroups();
		if ( selectedGroups != null )
		{
			Iterator<GroupInfo> groupIterator;

			groupIterator = selectedGroups.iterator();
			while ( groupIterator.hasNext() )
			{
				GroupInfo nextGroup;
				
				nextGroup = groupIterator.next();
				Window.alert( "selected group title: " + nextGroup.getTitle() );
			}
		}
	}
	
	/**
	 * Issue an ajax request to get a list of all the groups.
	 */
	private void getAllGroupsFromServer()
	{
		GetAllGroupsCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;

		// Create the callback that will be used when we issue an ajax call to get all the groups.
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetAllGroups() );
			}
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( final VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					/**
					 * 
					 */
					public void execute()
					{
						GetGroupsRpcResponseData responseData;
						
						responseData = (GetGroupsRpcResponseData) response.getResponseData();
						
						// Add the groups to the ui
						if ( responseData != null )
							addGroups( responseData.getGroups() );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		// Issue an ajax request to get a list of all the groups.
		cmd = new GetAllGroupsCmd();
		GwtClientHelper.executeCommand( cmd, rpcCallback );
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 */
	public Object getDataFromDlg()
	{
		// Return something.  Doesn't matter what since we only have a close button.
		return Boolean.TRUE;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return null;
	}
	
	/**
	 * Return a list of selected groups.
	 */
	public Set<GroupInfo> getSelectedGroups()
	{
		return m_selectionModel.getSelectedSet();
	}
	
	/**
	 * 
	 */
	public void init()
	{
		// Issue an ajax request to get a list of all the groups
		getAllGroupsFromServer();
	}
	
	/**
	 * Invoke the "Add Group" dialog.
	 */
	private void invokeAddGroupDlg()
	{
		Window.alert( "Not yet implemented." );
	}
}
