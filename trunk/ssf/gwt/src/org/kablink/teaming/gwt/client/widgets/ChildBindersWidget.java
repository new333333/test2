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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ActivityStreamEnterEvent;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.rpc.shared.GetListOfChildBindersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetListOfChildBindersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.ShowSetting;
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



/**
 * This class will display a list of the child binders of the given binder.
 * @author jwootton
 */
public class ChildBindersWidget  extends VibeWidget
{
	private BinderInfo m_binderInfo;
	private VibeFlowPanel m_mainPanel;
	private VibeFlowPanel m_iconListPanel;
	private final static int NUM_COLS = 3;

	
	/**
	 * Callback interface to interact with the ChildBindersWidget asynchronously after it loads. 
	 */
	public interface ChildBindersWidgetClient
	{
		void onSuccess( ChildBindersWidget lpe );
		void onUnavailable();
	}
	

	/**
	 * This class is used as the click handler when the user clicks on a binder.
	 *
	 */
	private class BinderClickHandler implements ClickHandler
	{
		private String m_binderId;
		private String m_binderUrl;
		
		/**
		 * 
		 */
		public BinderClickHandler( String binderId, String binderUrl )
		{
			super();
			
			m_binderId = binderId;
			m_binderUrl = binderUrl;
		}

		/**
		 * 
		 */
		private void handleClickOnLink()
		{
			if ( GwtClientHelper.hasString( m_binderUrl ) )
			{
				OnSelectBinderInfo binderInfo;
				
				binderInfo = new OnSelectBinderInfo( m_binderId, m_binderUrl, false, Instigator.UNKNOWN );
				GwtTeaming.fireEvent( new ChangeContextEvent( binderInfo ) );
			}
			
		}
		
		/**
		 * 
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
					handleClickOnLink();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}

	
	/**
	 * This class is used as the click handler when the user clicks on a binder's unread label.
	 *
	 */
	private class UnreadClickHandler implements ClickHandler
	{
		private String m_binderId;
		private String m_binderTitle;
		
		/**
		 * 
		 */
		public UnreadClickHandler( String binderId, String binderTitle )
		{
			super();
			
			m_binderId = binderId;
			m_binderTitle = binderTitle;
		}

		/**
		 * 
		 */
		private void handleClickOnLink()
		{
			if ( GwtClientHelper.hasString( m_binderId ) )
			{
				ActivityStreamInfo asi;

				// Invoke the What's New page on the given binder.
				asi = new ActivityStreamInfo();
				asi.setActivityStream( ActivityStream.CURRENT_BINDER );
				//asi.setBinderId( m_binderId );
				//asi.setTitle( m_binderTitle );
				GwtTeaming.fireEvent( new ActivityStreamEnterEvent( asi, ShowSetting.SHOW_UNREAD ) );
			}
		}
		
		/**
		 * 
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
					handleClickOnLink();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}

	
	
	/**
	 * 
	 */
	private ChildBindersWidget( final BinderInfo binderInfo )
	{
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
				buildUI( binderInfo );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Build a panel to hold the binder's description if the binder has a description
	 */
	private VibeFlowPanel buildDescPanel( BinderInfo binderInfo )
	{
		VibeFlowPanel descPanel;
		String desc;
		
		descPanel = null;
		
		// Does the binder have a description?
		desc = binderInfo.getBinderDesc();
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
		
		// Create a panel for each folder type
		{
			FolderType[] folderTypes;
			
			folderTypes = FolderType.values();
			for (FolderType folderType : folderTypes)
			{
				// Create a panel that holds the given folder type
				panel = buildListOfFoldersPanel( listOfChildBinders, folderType );
				if ( panel != null )
					m_iconListPanel.add( panel );
			}
		}
		
		return m_iconListPanel;
	}
	
	/**
	 * Create a panel that displays all of the binders that are folders of the given folder
	 * type from the given list of child binders.
	 * If there are no binders of the given type we will return null.
	 */
	private VibeFlowPanel buildListOfFoldersPanel( ArrayList<TreeInfo> listOfChildBinders, FolderType folderType )
	{
		VibeFlowPanel panel;
		FlexTable table;
		int total;
		
		panel = new VibeFlowPanel();
		panel.addStyleName( "childBindersWidget_ListOfFoldersPanel" );
		
		// Add a header to this panel.
		{
			VibeFlowPanel headerPanel;
			Image img;
			InlineLabel headerText;
			String folderTypeName;

			headerPanel = new VibeFlowPanel();
			headerPanel.addStyleName( "childBindersWidget_ListOfFoldersPanel_HeaderPanel" );
			panel.add( headerPanel );
			
			// Add an image for the given folder type.
			img = getFolderImageForHeader( folderType );
			img.getElement().setAttribute( "align", "absmiddle" ); 
			headerPanel.add( img );
			
			// Add the name of this type of folder.
			folderTypeName = getFolderTypeName( folderType );
			headerText = new InlineLabel( folderTypeName );
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
			if ( binderInfo.isBinderFolder() && binderInfo.getFolderType() == folderType )
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
				if ( binderInfo.isBinderFolder() && binderInfo.getFolderType() == folderType )
				{
					VibeFlowPanel folderPanel;
					InlineLabel folderLabel;
					BinderClickHandler clickHandler;
					VibeFlowPanel descPanel;
					Long numUnread;
					
					// Yes
					folderPanel = new VibeFlowPanel();
					folderPanel.addStyleName( "childBindersWidget_ListOfFoldersPanel_FolderPanel" );
					
					folderLabel = new InlineLabel( childBinder.getBinderTitle() );
					folderLabel.addStyleName( "childBindersWidget_ListOfFoldersPanel_folderLabel" );
					clickHandler = new BinderClickHandler( binderInfo.getBinderId(), childBinder.getBinderPermalink() );
					folderLabel.addClickHandler( clickHandler );
					folderPanel.add( folderLabel );

					// Create a label with the number of unread entries. ie (5 unread)
					numUnread = binderInfo.getNumUnread();
					if ( numUnread > 0 )
					{
						InlineLabel unreadLabel;
						UnreadClickHandler unreadClickHandler;
						
						unreadLabel = new InlineLabel( " " + GwtTeaming.getMessages().unreadEntries( numUnread ) );
						unreadLabel.addStyleName( "childBindersWidget_ListOfFoldersPanel_unreadLabel" );
						unreadLabel.addStyleName( "childBindersWidget_ListOfFoldersPanel_unreadLabelRed" );
						unreadClickHandler = new UnreadClickHandler( binderInfo.getBinderId(), binderInfo.getBinderTitle() );
						unreadLabel.addClickHandler( unreadClickHandler );
						folderPanel.add( unreadLabel );
					}

					// Does this workspace have a description?
					descPanel = buildDescPanel( binderInfo );
					if ( descPanel != null )
					{
						// Yes
						descPanel.addStyleName( "childBindersWidget_ListOfWorkspacesPanel_FolderDescPanel" );
						folderPanel.add( descPanel );
					}
					
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
					
					table.setWidget( totalAddedToCol, col, folderPanel );
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
			ImageResource imageResource;
			Image img;
			InlineLabel headerText;

			headerPanel = new VibeFlowPanel();
			headerPanel.addStyleName( "childBindersWidget_ListOfWorkspacesPanel_HeaderPanel" );
			panel.add( headerPanel );
			
			// Add a workspace image.
			imageResource = GwtTeaming.getImageBundle().workspaceImgLarge();
			img = new Image( imageResource );
			img.getElement().setAttribute( "align", "absmiddle" ); 
			headerPanel.add( img );
			
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
					InlineLabel workspaceLabel;
					Image wsImg;
					VibeFlowPanel wsPanel;
					VibeFlowPanel descPanel;
					BinderClickHandler clickHandler;
					Long numUnread;
					
					// Yes
					wsPanel = new VibeFlowPanel();
					wsPanel.addStyleName( "childBindersWidget_ListOfWorkspacesPanel_workspacePanel" );
					
					wsImg = getWorkspaceImage( childBinder );
					wsImg.getElement().setAttribute( "align", "absmiddle" );
					wsPanel.add( wsImg );
					
					workspaceLabel = new InlineLabel( childBinder.getBinderTitle() );
					workspaceLabel.addStyleName( "childBindersWidget_ListOfWorkspacesPanel_workspaceLabel" );
					clickHandler = new BinderClickHandler( binderInfo.getBinderId(), childBinder.getBinderPermalink() );
					workspaceLabel.addClickHandler( clickHandler );
					wsPanel.add( workspaceLabel );
					
					// Create a label with the number of unread entries. ie (5 unread)
					numUnread = binderInfo.getNumUnread();
					if ( numUnread > 0 )
					{
						InlineLabel unreadLabel;
						UnreadClickHandler unreadClickHandler;

						unreadLabel = new InlineLabel( " " + GwtTeaming.getMessages().unreadEntries( numUnread ) );
						unreadLabel.addStyleName( "childBindersWidget_ListOfWorkspacesPanel_unreadLabel" );
						unreadLabel.addStyleName( "childBindersWidget_ListOfWorkspacesPanel_unreadLabelRed" );
						unreadClickHandler = new UnreadClickHandler( binderInfo.getBinderId(), binderInfo.getBinderTitle() );
						unreadLabel.addClickHandler( unreadClickHandler );
						wsPanel.add( unreadLabel );
					}

					// Does this workspace have a description?
					descPanel = buildDescPanel( binderInfo );
					if ( descPanel != null )
					{
						// Yes
						descPanel.addStyleName( "childBindersWidget_ListOfWorkspacesPanel_WorkspaceDescPanel" );
						wsPanel.add( descPanel );
					}
					
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
					
					table.setWidget( totalAddedToCol, col, wsPanel );
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
	private void buildUI( BinderInfo binderInfo )
	{
		m_binderInfo = binderInfo;

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
	public static void createAsync( final BinderInfo binderInfo, final ChildBindersWidgetClient client )
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
				
				cbWidget = new ChildBindersWidget( binderInfo );
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
		imgName = treeInfo.getBinderIconName();
		if ( GwtClientHelper.hasString( imgName ) )
		{
			// Yes
			wsImg = new Image( GwtClientHelper.getRequestInfo().getImagesPath() + imgName );
		}
		else
		{
			wsImg = new Image( treeInfo.getBinderImage() );
		}
		
		return wsImg;
	}
}
