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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelReady;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.rpc.shared.GetListOfChildBindersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetListOfChildBindersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * This class will display a list of the child binders of the given binder.
 * 
 * @author jwootton
 */
public class ChildBindersWidget extends ToolPanelBase
{
	private VibeFlowPanel m_mainPanel;
	private VibeFlowPanel m_iconListPanel;
	private final static int NUM_COLS = 3;


	/**
	 * This class is used to create the ui that displays a binder's name, number of unread
	 * items and its description.
	 */
	private class BinderPanel extends VibeWidget implements ClickHandler
	{
		InlineLabel m_unreadLabel;
		InlineLabel m_binderLabel;
		BinderInfo m_binderInfo;
		String m_binderUrl;
		
		/**
		 * 
		 */
		public BinderPanel( BinderInfo binderInfo, String binderUrl, Image img, boolean displayChildBinders )
		{
			super();
			
			m_binderInfo = binderInfo;
			m_binderUrl = binderUrl;
			
			if ( m_binderInfo != null )
				buildPanel( img, displayChildBinders );
		}
		
		/**
		 * Build a panel to hold the binder's description if the binder has a description
		 */
		private VibeFlowPanel buildDescPanel()
		{
			VibeFlowPanel descPanel;
			String desc;
			
			descPanel = null;
			
			// Does the binder have a description?
			desc = m_binderInfo.getBinderDesc();
			if ( desc != null && desc.length() > 0 )
			{
				// Yes
				// Strip out <p> and </p> to save space
				desc = desc.replaceAll( "<p>", "" );
				desc = desc.replaceAll( "</p>", "" );
				
				descPanel = new VibeFlowPanel();
				descPanel.getElement().setInnerHTML( desc );
			}

			return descPanel;
		}
		
		/**
		 * Contruct the ui that will hold the list of child binders
		 */
		private void buildChildBindersUI( VibeFlowPanel childBindersPanel, ArrayList<TreeInfo> listOfChildBinders )
		{
			// Go through the list of binders and add a link for each one.
			for (TreeInfo childBinder : listOfChildBinders)
			{
				BinderInfo binderInfo;
				BinderPanel binderPanel;
				
				// Is this binder's type the type we are looking for?
				binderInfo = childBinder.getBinderInfo();
					
				binderPanel = new BinderPanel( binderInfo, childBinder.getBinderPermalink(), null, false );
				childBindersPanel.add( binderPanel );
			}
		}
		
		/**
		 * 
		 */
		private void buildPanel( Image img, boolean displayChildBinders )
		{
			VibeFlowPanel binderPanel;
			VibeFlowPanel descPanel;
			Long numUnread;
			
			// Yes
			binderPanel = new VibeFlowPanel();
			binderPanel.addStyleName( "childBindersWidget_ListOfFoldersPanel_FolderPanel" );

			// Do we have an image to display?
			if ( img != null )
			{
				// Yes
				img.getElement().setAttribute( "align", "absmiddle" );
				binderPanel.add( img );
			}
			
			m_binderLabel = new InlineLabel( m_binderInfo.getBinderTitle() );
			m_binderLabel.addStyleName( "childBindersWidget_ListOfFoldersPanel_folderLabel" );
			m_binderLabel.addClickHandler( this );
			binderPanel.add( m_binderLabel );

			// Create a label with the number of unread entries. ie (5 unread)
			numUnread = m_binderInfo.getNumUnread();
			if ( ( null != numUnread ) && ( numUnread > 0 ) )
			{
				m_unreadLabel = new InlineLabel( " " + GwtTeaming.getMessages().unreadEntries( numUnread ) );
				m_unreadLabel.addStyleName( "childBindersWidget_ListOfFoldersPanel_unreadLabel" );
				m_unreadLabel.addStyleName( "childBindersWidget_ListOfFoldersPanel_unreadLabelRed" );
				m_unreadLabel.addClickHandler( this );
				binderPanel.add( m_unreadLabel );
			}

			// Does this workspace have a description?
			descPanel = buildDescPanel();
			if ( descPanel != null )
			{
				// Yes
				descPanel.addStyleName( "childBindersWidget_ListOfWorkspacesPanel_FolderDescPanel" );
				binderPanel.add( descPanel );
			}
			
			if ( displayChildBinders )
			{
				VibeFlowPanel childBindersPanel;
				
				childBindersPanel = new VibeFlowPanel();
				childBindersPanel.addStyleName( "childBindersWidget_ListOfWorkspacesPanel_ChildBindersPanel" );
				binderPanel.add( childBindersPanel );
				
				// Get the list of child binders and display them.
				getListOfChildBinders( childBindersPanel );
			}
			
			initWidget( binderPanel );
		}

		/**
		 * Issue an ajax call to get the list of binders that are children of the given binder.
		 */
		private void getListOfChildBinders( final VibeFlowPanel childBindersPanel )
		{
			GetListOfChildBindersCmd cmd;
			
			// Get a TreeInfo object for each of the child binders.
			cmd = new GetListOfChildBindersCmd( m_binderInfo.getBinderId() );
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure(Throwable t)
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetListOfChildBinders(),
						m_binderInfo.getBinderId() );
				}
				
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					Scheduler.ScheduledCommand cmd;
					GetListOfChildBindersRpcResponseData responseData;
					final ArrayList<TreeInfo> listOfChildBinders;
					
					responseData = (GetListOfChildBindersRpcResponseData) response.getResponseData();
					listOfChildBinders = responseData.getListOfChildBinders();
					cmd = new Scheduler.ScheduledCommand()
					{
						/**
						 * 
						 */
						@Override
						public void execute()
						{
							// Build the ui based on the information we read.
							buildChildBindersUI( childBindersPanel, listOfChildBinders );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			});
		}
		
		/**
		 * 
		 */
		private void handleClickOnBinder()
		{
			if ( GwtClientHelper.hasString( m_binderUrl ) )
			{
				OnSelectBinderInfo binderInfo;
				
				binderInfo = new OnSelectBinderInfo( m_binderInfo, m_binderUrl, Instigator.GOTO_CONTENT_URL );
				GwtTeaming.fireEvent( new ChangeContextEvent( binderInfo ) );
			}
			
		}
		
		/**
		 * 
		 */
		private void handleClickOnUnread()
		{
			if ( GwtClientHelper.hasString( m_binderInfo.getBinderId() ) )
			{
				UnreadEntriesDlg dlg;
				
				dlg = new UnreadEntriesDlg( null, null, true, true, m_binderLabel.getAbsoluteLeft(), m_binderLabel.getAbsoluteTop() );
				dlg.init( m_binderInfo );
				dlg.show();
			}
		}
		
		/**
		 * 
		 */
		@Override
		public void onClick( ClickEvent event )
		{
			Scheduler.ScheduledCommand cmd = null;
			Object src;

			src = event.getSource();
			
			// Did the user click on the binder name?
			if ( src == m_binderLabel )
			{
				// Yes
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						handleClickOnBinder();
					}
				};
			}
			// Did the user click on "unread"?
			else if ( src == m_unreadLabel )
			{
				// Yes
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						handleClickOnUnread();
					}
				};
			}
			
			if ( cmd != null )
				Scheduler.get().scheduleDeferred( cmd );
		}
	}
	

	
	/**
	 * 
	 */
	private ChildBindersWidget( RequiresResize containerResizer, BinderInfo binderInfo, ToolPanelReady toolPanelReady )
	{
		super( containerResizer, binderInfo, toolPanelReady );
		
		Scheduler.ScheduledCommand cmd;

		init();
		
		cmd = new Scheduler.ScheduledCommand()
		{
			/**
			 * 
			 */
			@Override
			public void execute()
			{
				// Initialize this widget for the given binder.
				buildUI();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Display the given list of child binders using an icon view
	 */
	private VibeFlowPanel buildIconListUI( ArrayList<TreeInfo> listOfChildBinders )
	{
		VibeFlowPanel panel;
		
		if ( m_iconListPanel != null )
			m_iconListPanel.clear();
		else
		{
			m_iconListPanel = new VibeFlowPanel();
			m_iconListPanel.addStyleName( "childBindersWidget_IconListPanel" );
		}
		
		// Create a panel that holds all the workspaces.
		panel = buildListOfWorkspacesPanel( listOfChildBinders );
		if ( panel != null )
			m_iconListPanel.add( panel );

		panel = buildListOfFoldersPanel( listOfChildBinders );
		if ( panel != null )
			m_iconListPanel.add( panel );

		return m_iconListPanel;
	}
	
	/**
	 * Create a panel that displays all of the binders that are folders of the given folder
	 * type from the given list of child binders.
	 * If there are no binders of the given type we will return null.
	 */
	private VibeFlowPanel buildListOfFoldersPanel( ArrayList<TreeInfo> listOfChildBinders )
	{
		VibeFlowPanel panel;
		FlexTable table;
		int total;
		
		panel = new VibeFlowPanel();
		panel.addStyleName( "childBindersWidget_ListOfFoldersPanel" );
		
		// Add a header to this panel.
		{
			VibeFlowPanel headerPanel;
			InlineLabel headerText;

			headerPanel = new VibeFlowPanel();
			headerPanel.addStyleName( "childBindersWidget_ListOfFoldersPanel_HeaderPanel" );
			panel.add( headerPanel );
			
			headerText = new InlineLabel( GwtTeaming.getMessages().folders() );
			headerText.addStyleName( "childBindersWidget_ListOfFoldersPanel_HeaderText" );
			headerPanel.add( headerText );
		}
		
		// Count how many folders there are of the given type.
		total = 0;
		for (TreeInfo childBinder : listOfChildBinders)
		{
			BinderInfo binderInfo;
			
			// Is this binder a folder?
			binderInfo = childBinder.getBinderInfo();
			if ( binderInfo.isBinderFolder() )
			{
				// Yes
				++total;
			}
		}
		
		if ( total > 0 )
		{
			int col;
			int leftOver;
			int totalAddedToCol;
			int[] numPerCol;
			FlexCellFormatter cellFormatter;
			
			numPerCol = new int[NUM_COLS];
			
			// Calculate how many folders go in each column.
			leftOver = total % NUM_COLS;
			for (col = 0; col < NUM_COLS; ++col)
			{
				numPerCol[col] = total / NUM_COLS;
				if ( leftOver > 0 )
				{
					// Does this column need an extra?
					if ( (leftOver - col) > 0 )
					{
						// Yes
						++numPerCol[col];
					}
				}
			}
			
			col = 0;
			totalAddedToCol = 0;
			
			// Create a table for the folder names to go in.
			table = new FlexTable();
			table.addStyleName( "childBindersWidget_ListOfFoldersPanel_Table" );
			cellFormatter = table.getFlexCellFormatter();
			panel.add( table );
			
			// Go through the list of binders and find the folders with the given type and add their
			// names to the table.  We will fill up column 1, then column 2, then column 3
			for (TreeInfo childBinder : listOfChildBinders)
			{
				BinderInfo binderInfo;
				
				// Is this binder's type the type we are looking for?
				binderInfo = childBinder.getBinderInfo();
				if ( binderInfo.isBinderFolder() )
				{
					BinderPanel binderPanel;
					Image img;
					
					// Yes
					img = GwtClientHelper.buildImage(childBinder, BinderIconSize.LARGE, null);
					if (img==null) {
						img = getFolderImageForHeader( binderInfo.getFolderType() );
					}
					img.getElement().setAttribute( "align", "absmiddle" ); 
					binderPanel = new BinderPanel( binderInfo, childBinder.getBinderPermalink(), img, false );
					
					// Does the current column already have enough?
					if ( totalAddedToCol < numPerCol[col] )
					{
						// No, add it to this column.
					}
					else
					{
						// Yes, increment the column
						++col;
						totalAddedToCol = 0;
					}
					
					table.setWidget( totalAddedToCol, col, binderPanel );
					cellFormatter.setVerticalAlignment( totalAddedToCol, col, HasVerticalAlignment.ALIGN_TOP );
					++totalAddedToCol;
				}
			}
		}
		
		// Did we find any folders?
		if ( total == 0 )
		{
			// No
			panel = null;
		}
		
		return panel;
	}
	
	/**
	 * Create a panel that displays all the workspaces from the given list of child binders.
	 * If there are no workspaces in the list we will return null;
	 */
	private VibeFlowPanel buildListOfWorkspacesPanel( ArrayList<TreeInfo> listOfChildBinders )
	{
		VibeFlowPanel panel;
		FlexTable table;
		int total;
		
		panel = new VibeFlowPanel();
		panel.addStyleName( "childBindersWidget_ListOfWorkspacesPanel" );

		// Add a header to this panel.
		{
			VibeFlowPanel headerPanel;
			InlineLabel headerText;

			headerPanel = new VibeFlowPanel();
			headerPanel.addStyleName( "childBindersWidget_ListOfWorkspacesPanel_HeaderPanel" );
			panel.add( headerPanel );
			
			// Add the text "Workspaces"
			headerText = new InlineLabel( GwtTeaming.getMessages().workspacesHeader() );
			headerText.addStyleName( "childBindersWidget_ListOfWorkspacesPanel_HeaderText" );
			headerPanel.add( headerText );
		}
		
		// Count how many workspaces there are.
		total = 0;
		for (TreeInfo childBinder : listOfChildBinders)
		{
			// Is this binder a workspace?
			if ( childBinder.getBinderInfo().isBinderWorkspace() )
			{
				// Yes
				++total;
			}
		}
		
		if ( total > 0 )
		{
			int col;
			int leftOver;
			int totalAddedToCol;
			int[] numPerCol;
			FlexCellFormatter cellFormatter;
			
			numPerCol = new int[NUM_COLS];
			
			// Calculate how many workspaces go in each column.
			leftOver = total % NUM_COLS;
			for (col = 0; col < NUM_COLS; ++col)
			{
				numPerCol[col] = total / NUM_COLS;
				if ( leftOver > 0 )
				{
					// Does this column need an extra?
					if ( (leftOver - col) > 0 )
					{
						// Yes
						++numPerCol[col];
					}
				}
			}
			
			col = 0;
			totalAddedToCol = 0;
			
			// Create a table for the workspace names to go in.
			table = new FlexTable();
			table.addStyleName( "childBindersWidget_ListOfWorkspacesPanel_Table" );
			cellFormatter = table.getFlexCellFormatter();
			panel.add( table );
			
			// Go through the list of binders and find the workspaces and add their
			// names to the table.  We will fill up column 1, then column 2, then column 3
			for (TreeInfo childBinder : listOfChildBinders)
			{
				BinderInfo binderInfo;
				
				binderInfo = childBinder.getBinderInfo();
				
				// Is this binder a workspace?
				if ( binderInfo.isBinderWorkspace() )
				{
					BinderPanel binderPanel;
					Image wsImg;
					
					// Yes
					wsImg = getWorkspaceImage( childBinder );
					binderPanel = new BinderPanel( binderInfo, childBinder.getBinderPermalink(), wsImg, true );
					
					// Does the current column already have enough?
					if ( totalAddedToCol < numPerCol[col] )
					{
						// No, add it to this column.
					}
					else
					{
						// Yes, increment the column
						++col;
						totalAddedToCol = 0;
					}
					
					table.setWidget( totalAddedToCol, col, binderPanel );
					cellFormatter.setVerticalAlignment( totalAddedToCol, col, HasVerticalAlignment.ALIGN_TOP );
					++totalAddedToCol;
				}
			}
		}
		
		// Did we find any workspaces?
		if ( total == 0 )
		{
			// No
			panel = null;
		}
		
		return panel;
	}
	
	/**
	 * Build the ui of this widget with the given data.
	 */
	private void buildUI( ArrayList<TreeInfo> listOfChildBinders )
	{
		VibeFlowPanel panel;
		
		panel = buildIconListUI( listOfChildBinders );
		m_mainPanel.add( panel );
	}
	
	/**
	 * Build the ui by reading the list of binders that are children of the given binder.
	 * 
	 * @param binderInfo
	 */
	private void buildUI()
	{
		// Read the list of binders that are children of the given binder.
		getListOfChildBinders();
	}
	
	/**
	 * Loads the ChildBindersWidget split point and returns an instance of it
	 * via the callback.
	 *
	 * @param binderInfo
	 * @param vClient
	 */
	public static void createAsync( final RequiresResize containerResizer, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient client )
	{
		GWT.runAsync( ChildBindersWidget.class, new RunAsyncCallback()
		{			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ChildBindersWidget() );
				client.onUnavailable();
			}

			@Override
			public void onSuccess()
			{
				ChildBindersWidget cbWidget;
				
				cbWidget = new ChildBindersWidget( containerResizer, binderInfo, toolPanelReady );
				client.onSuccess( cbWidget );
			}
		} );
	}
	
	/**
	 * 
	 */
	private void init()
	{
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName( "childBindersWidget_MainPanel" );
		
		initWidget( m_mainPanel );
	}

	/**
	 * Return an image that will be used in the "list of folders" header.
	 */
	private Image getFolderImageForHeader( FolderType folderType )
	{
		ImageResource imageResource;
		
		switch ( folderType )
		{
		case BLOG:
			imageResource = GwtTeaming.getImageBundle().blogFolderLarge();
			break;
			
		case CALENDAR:
			imageResource = GwtTeaming.getImageBundle().calendarFolderLarge();
			break;
			
		case DISCUSSION:
			imageResource = GwtTeaming.getImageBundle().discussionFolderLarge();
			break;
			
		case FILE:
			imageResource = GwtTeaming.getImageBundle().fileFolderLarge();
			break;
			
		case GUESTBOOK:
			imageResource = GwtTeaming.getImageBundle().guestbookFolderLarge();
			break;
			
		case MILESTONE:
			imageResource = GwtTeaming.getImageBundle().milestoneFolderLarge();
			break;
			
		case MINIBLOG:
			imageResource = GwtTeaming.getImageBundle().miniblogFolderLarge();
			break;
			
		case MIRROREDFILE:
			imageResource = GwtTeaming.getImageBundle().mirroredFileFolderLarge();
			break;
			
		case PHOTOALBUM:
			imageResource = GwtTeaming.getImageBundle().photoAlbumFolderLarge();
			break;
			
		case SURVEY:
			imageResource = GwtTeaming.getImageBundle().surveyFolderLarge();
			break;
			
		case TASK:
			imageResource = GwtTeaming.getImageBundle().taskFolderLarge();
			break;
			
		case TRASH:
			imageResource = GwtTeaming.getImageBundle().trashFolderLarge();
			break;
			
		case WIKI:
			imageResource = GwtTeaming.getImageBundle().wikiFolderLarge();
			break;
		
		case NOT_A_FOLDER:
		case OTHER:
		default:
			imageResource = GwtTeaming.getImageBundle().genericFolderLarge();
			break;
		}
		
		return new Image( imageResource );
	}
	
	
	/**
	 * Return name of the given folder type.
	 */
	private String getFolderTypeName( FolderType folderType )
	{
		String name;
		
		switch ( folderType )
		{
		case BLOG:
			name = GwtTeaming.getMessages().hoverFolderBlog();
			break;
			
		case CALENDAR:
			name = GwtTeaming.getMessages().hoverFolderCalendar();
			break;
			
		case DISCUSSION:
			name = GwtTeaming.getMessages().hoverFolderDiscussion();
			break;
			
		case FILE:
			name = GwtTeaming.getMessages().hoverFolderFile();
			break;
			
		case GUESTBOOK:
			name = GwtTeaming.getMessages().hoverFolderGuestbook();
			break;
			
		case MILESTONE:
			name = GwtTeaming.getMessages().hoverFolderMilestones();
			break;
			
		case MINIBLOG:
			name = GwtTeaming.getMessages().hoverFolderMiniBlog();
			break;
			
		case MIRROREDFILE:
			name = GwtTeaming.getMessages().hoverFolderMirroredFiles();
			break;
			
		case PHOTOALBUM:
			name = GwtTeaming.getMessages().hoverFolderPhotoAlbum();
			break;
			
		case SURVEY:
			name = GwtTeaming.getMessages().hoverFolderSurvey();
			break;
			
		case TASK:
			name = GwtTeaming.getMessages().hoverFolderTask();
			break;
			
		case TRASH:
			name = GwtTeaming.getMessages().hoverFolderTrash();
			break;
			
		case WIKI:
			name = GwtTeaming.getMessages().hoverFolderWiki();
			break;
		
		case NOT_A_FOLDER:
		case OTHER:
		default:
			name = "Unknown folder type";
			break;
		}
		
		return name;
	}
	
	
	/**
	 * Issue an ajax call to get the list of binders that are children of the given binder.
	 */
	private void getListOfChildBinders()
	{
		GetListOfChildBindersCmd cmd;
		
		// Get a TreeInfo object for each of the child binders.
		cmd = new GetListOfChildBindersCmd( m_binderInfo.getBinderId() );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure(Throwable t)
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetListOfChildBinders(),
					m_binderInfo.getBinderId() );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				GetListOfChildBindersRpcResponseData responseData;
				final ArrayList<TreeInfo> listOfChildBinders;
				
				responseData = (GetListOfChildBindersRpcResponseData) response.getResponseData();
				listOfChildBinders = responseData.getListOfChildBinders();
				cmd = new Scheduler.ScheduledCommand()
				{
					/**
					 * 
					 */
					@Override
					public void execute()
					{
						// Build the ui based on the information we read.
						buildUI( listOfChildBinders );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		});
	}
	
	/**
	 * Construct the appropriate image for the given workspace
	 */
	private Image getWorkspaceImage( TreeInfo treeInfo )
	{
		Image wsImg;
		String imgName;

		// Do we have the name of the image for this workspace?
		imgName = treeInfo.getBinderIcon(BinderIconSize.getChildBindersWidgetIconSize());
		if ( GwtClientHelper.hasString( imgName ) )
		{
			// Yes
			wsImg = new Image( GwtClientHelper.getRequestInfo().getImagesPath() + imgName );
		}
		else
		{
			// Using a URL here allows for the image to be scaled.
			wsImg = new Image( treeInfo.getBinderImage( BinderIconSize.getChildBindersWidgetIconSize() ).getSafeUri() );
		}

		// Scale the image, as necessary.
		int width  = BinderIconSize.getChildBindersWidgetIconSize().getBinderIconWidth();
		if ( (-1) != width )
		{
			wsImg.setWidth( width + "px" );
		}
		int height = BinderIconSize.getChildBindersWidgetIconSize().getBinderIconHeight();
		if ( (-1) != height )
		{
			wsImg.setHeight( height + "px" );
		}
		
		return wsImg;
	}

	/**
	 * Called from the binder view to allow the panel to do any work required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel()
	{
		// Reset the widgets and reload the description.
		m_mainPanel.clear();
		buildUI();
	}
}
