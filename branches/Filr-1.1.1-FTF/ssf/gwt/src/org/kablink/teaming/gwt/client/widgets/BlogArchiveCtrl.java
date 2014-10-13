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

import org.kablink.teaming.gwt.client.BlogArchiveFolder;
import org.kablink.teaming.gwt.client.BlogArchiveInfo;
import org.kablink.teaming.gwt.client.BlogArchiveMonth;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.BlogArchiveFolderSelectedEvent;
import org.kablink.teaming.gwt.client.event.BlogArchiveMonthSelectedEvent;
import org.kablink.teaming.gwt.client.rpc.shared.GetBlogArchiveInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.BlogGlobalTagsCtrl.BlogGlobalTagsCtrlClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;


/**
 * This control is used to display the months that have entries in them for a given blog folder. 
 * @author jwootton
 *
 */
public class BlogArchiveCtrl extends VibeWidget
{
	private Long m_folderId;
	private FlexTable m_table;
	private BlogArchiveInfo m_blogArchiveInfo;
	private BlogGlobalTagsCtrl m_globalTagsCtrl;
	private ClickHandler m_monthClickHandler;
	private ClickHandler m_folderClickHandler;
	private InlineLabel m_selectedLabel;	// Holds the month or folder label last selected.
	
	
	/**
	 * Callback interface to interact with the blog archive control asynchronously after it loads. 
	 */
	public interface BlogArchiveCtrlClient
	{
		void onSuccess( BlogArchiveCtrl baCtrl );
		void onUnavailable();
	}
	
	
	/**
	 * 
	 */
	private class MonthInlineLabel extends InlineLabel
	{
		private BlogArchiveMonth m_month;
		
		/**
		 * 
		 */
		public MonthInlineLabel( String text, BlogArchiveMonth month )
		{
			super( text );
			
			m_month = month;
		}
		
		/**
		 * 
		 */
		public BlogArchiveMonth getMonth()
		{
			return m_month;
		}
	}
	
	/**
	 * 
	 */
	private class FolderInlineLabel extends InlineLabel
	{
		private BlogArchiveMonth m_month;
		private BlogArchiveFolder m_folder;
		
		/**
		 * 
		 */
		public FolderInlineLabel( String text, BlogArchiveMonth month, BlogArchiveFolder folder )
		{
			super( text );
			
			m_month = month;
			m_folder = folder;
		}
		
		/**
		 * 
		 */
		public BlogArchiveFolder getFolder()
		{
			return m_folder;
		}
		
		/**
		 * 
		 */
		public BlogArchiveMonth getMonth()
		{
			return m_month;
		}
	}


	/**
	 * 
	 */
	private BlogArchiveCtrl()
	{
		final VibeFlowPanel mainPanel;
		InlineLabel label;
		
		m_selectedLabel = null;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "blogArchiveCtrlMainPanel" );
		
		m_table = new FlexTable();
		
		// Add the "Archives" title
		label = new InlineLabel( GwtTeaming.getMessages().blogArchiveTitle() );
		label.addStyleName( "blogArchiveCtrlTitle" );
		m_table.setWidget( 0, 0, label );
		
		mainPanel.add( m_table );
		
		// Add the Global tags control
		{
			BlogGlobalTagsCtrl.createAsync( new BlogGlobalTagsCtrlClient()
			{
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in the asyncronous provider.
				}
				
				@Override
				public void onSuccess( BlogGlobalTagsCtrl bgtCtrl )
				{
					// Add some space between the archive control and the global tags control.
					bgtCtrl.addStyleName( "margintop3" );
					
					m_globalTagsCtrl = bgtCtrl;
					mainPanel.add( bgtCtrl );
				}
			} );
		}

		// Create a click handler that will be used for every month
		m_monthClickHandler = new ClickHandler()
		{
			/**
			 * 
			 */
			@Override
			public void onClick( ClickEvent event )
			{
				Object src;
				
				// Get the month the user clicked on.
				src = event.getSource();
				if ( src != null && src instanceof MonthInlineLabel )
				{
					Scheduler.ScheduledCommand cmd;
					MonthInlineLabel label;
					final BlogArchiveMonth month;
					
					// Clear any selected month or folder
					clearCurrentSelection();
					
					label = (MonthInlineLabel) src;
					m_selectedLabel = label;
					m_selectedLabel.addStyleName( "blogArchiveCtrlSelected" );
					month = label.getMonth();
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							handleClickOnMonth( month );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		};
		
		// Create a click handler that will be used for every folder
		m_folderClickHandler = new ClickHandler()
		{
			/**
			 * 
			 */
			@Override
			public void onClick( ClickEvent event )
			{
				Object src;
				
				// Get the folder the user clicked on.
				src = event.getSource();
				if ( src != null && src instanceof FolderInlineLabel )
				{
					Scheduler.ScheduledCommand cmd;
					FolderInlineLabel label;
					final BlogArchiveFolder folder;
					final BlogArchiveMonth month;
					
					// Clear any selected month or folder
					clearCurrentSelection();
					
					label = (FolderInlineLabel) src;
					m_selectedLabel = label;
					m_selectedLabel.addStyleName( "blogArchiveCtrlSelected" );
					folder = label.getFolder();
					month = label.getMonth();
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							handleClickOnFolder( month, folder );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		};
		
		initWidget( mainPanel );
	}

	/**
	 * Add the archive information to this control.
	 */
	private void addArchiveInfo( BlogArchiveInfo info )
	{
		if ( info != null )
		{
			ArrayList<BlogArchiveMonth> listOfMonths;
			
			// Get a list of months that have blog entries
			listOfMonths = info.getListOfMonths();
			
			if ( listOfMonths != null )
			{
				for (BlogArchiveMonth nextMonth: listOfMonths)
				{
					addArchiveMonth( nextMonth );
				}
			}
		}
	}
	
	/**
	 * Add the given folder to the display.
	 */
	private void addArchiveFolder( int row, BlogArchiveMonth month, BlogArchiveFolder folder )
	{
		if ( month != null && folder != null )
		{
			FolderInlineLabel folderLabel;
			
			row = m_table.insertRow( row );

			// Insert this folder into the given row.
			folderLabel = new FolderInlineLabel( folder.getName(), month, folder );
			folderLabel.addStyleName( "blogArchiveCtrlFolderLabel" );
			m_table.setWidget( row, 0, folderLabel );
			
			// Add the number of blog entries to the table.
			m_table.setText( row, 1, "(" + String.valueOf( folder.getNumEntries() ) + ")" );
			
			folderLabel.addClickHandler( m_folderClickHandler );
		}
	}
	
	/**
	 * Add the given month to the list of months
	 */
	private void addArchiveMonth( BlogArchiveMonth month )
	{
		if ( month != null )
		{
			MonthInlineLabel monthLabel;
			int row;
			
			row = m_table.getRowCount();
			
			// Add the name of the month to the list.
			monthLabel = new MonthInlineLabel( month.getName(), month );
			monthLabel.addStyleName( "blogArchiveCtrlMonthLabel" );
			m_table.setWidget( row, 0, monthLabel );
			
			// Add the number of blog entries to the list.
			m_table.setText( row, 1, "(" + String.valueOf( month.getNumEntries() ) + ")" );
			
			monthLabel.addClickHandler( m_monthClickHandler );
		}
	}
	
	/**
	 * If a month or folder is selected, deselect it. 
	 */
	private void clearCurrentSelection()
	{
		// Is a month or folder currently selected?
		if ( m_selectedLabel != null )
		{
			// Yes, remove the "selected" style from the label.
			m_selectedLabel.removeStyleName( "blogArchiveCtrlSelected" );
		}
	}
	
	/**
	 * Clear all selections, selected month or folder or tag
	 */
	public void clearAllSelections()
	{
		// Clear current selection of a month or folder
		clearCurrentSelection();
		
		// Clear any global tag that is selected.
		m_globalTagsCtrl.clearSelectedTags();
		
	}
	
	/**
	 * Close the given month by not showing the folders that have blog entries for the
	 * given month.
	 */
	private void closeMonth( BlogArchiveMonth month )
	{
		if ( month != null )
		{
			int row;
			
			// Find the row this month lives in.
			row = findMonth( month );
			if ( row != -1 )
			{
				ArrayList<BlogArchiveFolder> listOfFolders;
				
				listOfFolders = month.getFolders();
				if ( listOfFolders != null )
				{
					int i;
					
					// Remove the folders from the table we added for this month.
					for (i = 0; i < listOfFolders.size(); ++i)
					{
						m_table.removeRow( row + 1 );
					}

					month.setIsMonthOpen( false );
				}
			}
		}
	}
	
	/**
	 * Loads the BlogArchiveCtrl split point and returns an instance of it via the callback.
	 */
	public static void createAsync( final BlogArchiveCtrlClient baCtrlClient )
	{
		GWT.runAsync( BlogArchiveCtrl.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				BlogArchiveCtrl baCtrl;

				baCtrl = new BlogArchiveCtrl();
				baCtrlClient.onSuccess( baCtrl );
			}
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_BlogArchiveCtrl() );
				baCtrlClient.onUnavailable();
			}
		} );
	}
	
	/**
	 * Find the given month in the table displays the list of the months
	 */
	private int findMonth( BlogArchiveMonth month )
	{
		int i;
		String name;
		
		name = month.getName();
		
		// Spin through the table that displays the list of months
		for (i = 0; i < m_table.getRowCount(); ++i)
		{
			Widget widget;
			
			widget = m_table.getWidget( i, 0 );
			if ( widget instanceof MonthInlineLabel )
			{
				String text;
				MonthInlineLabel label;
				
				// Get the text that is displayed in this row.
				label = (MonthInlineLabel) widget;
				text = label.getText();
				
				// Is this the month we are looking for?
				if ( text != null && text.equalsIgnoreCase( name ) )
				{
					// Yes
					return i;
				}
			}
		}
		
		// If we get here we did not find the month.  This should never happen.
		return -1;
	}

	/**
	 * 
	 */
	private void handleClickOnFolder( BlogArchiveMonth month, BlogArchiveFolder folder )
	{
		BlogArchiveFolderSelectedEvent event;

		// Clear any global tag that is selected.
		m_globalTagsCtrl.clearSelectedTags();
		
		// Fire the BlogArchiveFolderSelectedEvent so interested parties will know
		// that the given folder was selected.
		event = new BlogArchiveFolderSelectedEvent( month, folder );
		GwtTeaming.fireEvent( event );
	}
	
	/**
	 * Open or close the given month.
	 */
	private void handleClickOnMonth( BlogArchiveMonth month )
	{
		if ( month != null )
		{
			if ( month.getIsMonthOpen() )
			{
				closeMonth( month );
			}
			else
			{
				openMonth( month );
			}
			
			// Clear any global tag that is selected.
			m_globalTagsCtrl.clearSelectedTags();
			
			// Fire the BlogArchiveMonthSelectedEvent so interested parties will know
			// that this month was selected.
			{
				BlogArchiveMonthSelectedEvent event;
				
				event = new BlogArchiveMonthSelectedEvent( month );
				GwtTeaming.fireEvent( event );
			}
		}
	}
	
	/**
	 * Initialize this control for the given blog folder
	 */
	public void init( Long folderId )
	{
		GetBlogArchiveInfoCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;

		m_folderId = folderId;
		
		// Create the callback used by the rpc request.
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure(Throwable t)
			{
				GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetBlogArchiveInfo(),
						m_folderId );
			}
			
			/**
			 * 
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				m_blogArchiveInfo = (BlogArchiveInfo) response.getResponseData();
				
				if ( m_blogArchiveInfo != null )
				{
					Scheduler.ScheduledCommand schCmd;

					schCmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// Add the archive information to this control.
							addArchiveInfo( m_blogArchiveInfo );
							
							// Add the Global tags information
							m_globalTagsCtrl.init( m_blogArchiveInfo.getListOfGlobalTags() );
						}
					};
					Scheduler.get().scheduleDeferred( schCmd );
				}
			}
		};
		
		// Issue an rpc request to get the archive information for this blog folder.
		cmd = new GetBlogArchiveInfoCmd( m_folderId );
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * For the given month show the folders that have blog entries
	 */
	private void openMonth( BlogArchiveMonth month )
	{
		int row;
		
		// Find the given month in the table that holds the months
		row = findMonth( month );
		
		// Did we find the month?
		if ( row != -1 )
		{
			ArrayList<BlogArchiveFolder> listOfFolders;
			
			// Yes
			// Get the list of folders that have blog entries for the given month.
			listOfFolders = month.getFolders();
			
			if ( listOfFolders != null )
			{
				// We want the first folder inserted after the month
				++row;
				
				for (BlogArchiveFolder nextFolder : listOfFolders)
				{
					addArchiveFolder( row, month, nextFolder );
					++row;
				}

				// Mark this month as open.
				month.setIsMonthOpen( true );
			}
		}
	}
}
