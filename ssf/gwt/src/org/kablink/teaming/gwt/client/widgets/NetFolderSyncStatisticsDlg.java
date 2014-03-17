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

import java.text.DateFormat;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.NetFolder;
import org.kablink.teaming.gwt.client.NetFolderSyncStatistics;
import org.kablink.teaming.gwt.client.rpc.shared.GetDateTimeStrCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFolderSyncStatisticsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;


/**
 * 
 * @author jwootton
 *
 */
public class NetFolderSyncStatisticsDlg extends DlgBox
{
	private FlexTable m_table;
	private int m_startDateRow;
	private int m_endDateRow;
	private int m_nodeIpAddressRow;
	private int m_dirOnlyRow;
	private int m_dirEnumRow;
	private int m_fileCountRow;
	private int m_filesAddedRow;
	private int m_filesExpungedRow;
	private int m_filesModifiedRow;
	private int m_filesACLRow;
	private int m_filesOwnershipRow;
	private int m_folderCountRow;
	private int m_foldesAddedRow;
	private int m_foldersExpungedRow;
	private int m_foldersACLRow;
	private int m_foldersOwnershipRow;
	private int m_entriesExpungedRow;
	private int m_countFailureRow;
	private int m_foldersProcessedRow;
	private int m_startRow;
	private int m_endRow;
	private Timer m_timer;
	private boolean m_showDirOnlyRow=false;
	
	/**
	 * Callback interface to interact with the "Net Folder Sync Statistics" dialog
	 * asynchronously after it loads. 
	 */
	public interface NetFolderSyncStatisticsDlgClient
	{
		void onSuccess( NetFolderSyncStatisticsDlg nfssDlg );
		void onUnavailable();
	}

	

	/**
	 * 
	 */
	private NetFolderSyncStatisticsDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos, DlgButtonMode.Close );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( "", null, null, null ); 
	}
	

	/**
	 * Clear all the values from this dialog.
	 */
	private void clearAllValues()
	{
		int i;
		
		for ( i = m_startRow; i <= m_endRow; ++i )
		{
			m_table.setHTML( i, 1, "" );
		}
	}
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		int nextRow;

		messages = GwtTeaming.getMessages();
		
		// Create a table to hold the sync statistics.
		m_table = new FlexTable();
		m_table.setCellSpacing( 4 );
		m_table.addStyleName( "dlgContent" );
		
		nextRow = 0;
		m_startRow = nextRow;

		// Add the controls for the miscellaneous statistics
		{
			FlowPanel labelPanel;
			InlineLabel label;

			// Add a "Miscellaneous" heading
			{
				InlineLabel heading;
				FlowPanel headingPanel;
				
				heading = new InlineLabel( messages.netFolderSyncStatisticsDlg_MiscHeading() );
				heading.addStyleName( "netFolderSyncStatisticsDlg_StatisticsHeading" );
				headingPanel = new FlowPanel();
				headingPanel.add( heading );
				m_table.setHTML( nextRow, 0, headingPanel.getElement().getInnerHTML() );
				
				++nextRow;
			}
			
			label = new InlineLabel();
			label.addStyleName( "netFolderSyncStatisticsDlg_MiscStatisticsLabel" );
			labelPanel = new FlowPanel();
			labelPanel.add( label );
	
			// Create the controls for "Start Date"
			{
				m_startDateRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_StartDate() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );
	
				++nextRow;
			}
			
			// Create the controls for "End Date"
			{
				m_endDateRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_EndDate() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );
	
				++nextRow;
			}
			
			// Create the controls for "Node IP Address"
			{
				m_nodeIpAddressRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_NodeIpAddress() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );
				
				++nextRow;
			}
			
			// Create the controls for "Directory only"
			if ( m_showDirOnlyRow )
			{
				m_dirOnlyRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_DirOnly() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );
	
				++nextRow;
			}
			
			// Create the controls for "directory enumeration"
			{
				m_dirEnumRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_DirEnum() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );
	
				++nextRow;
			}

			// Create the controls for "Entries expunged"
			{
				m_entriesExpungedRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_EntriesExpunged() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );

				++nextRow;
			}

			// Create the controls for "Count failure"
			{
				m_countFailureRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_CountFailure() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );

				++nextRow;
			}
		}
		
		// Add the controls for the file statistics
		{
			FlowPanel labelPanel;
			InlineLabel label;

			// Add a "Files" heading
			{
				InlineLabel heading;
				FlowPanel headingPanel;
				
				heading = new InlineLabel( messages.netFolderSyncStatisticsDlg_FilesHeading() );
				heading.addStyleName( "netFolderSyncStatisticsDlg_StatisticsHeading" );
				headingPanel = new FlowPanel();
				headingPanel.add( heading );
				m_table.setHTML( nextRow, 0, headingPanel.getElement().getInnerHTML() );
				
				++nextRow;
			}
			
			label = new InlineLabel();
			label.addStyleName( "netFolderSyncStatisticsDlg_FileStatisticsLabel" );
			labelPanel = new FlowPanel();
			labelPanel.add( label );
	
			// Create the controls for "file count"
			{
				m_fileCountRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_FileCount() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );
	
				++nextRow;
			}
			
			// Create the controls for "Files added"
			{
				m_filesAddedRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_FilesAdded() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );
	
				++nextRow;
			}
	
			// Create the controls for "Files expunged"
			{
				m_filesExpungedRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_FilesExpunged() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );
	
				++nextRow;
			}
	
			// Create the controls for "Files modified"
			{
				m_filesModifiedRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_FilesModified() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );
	
				++nextRow;
			}
	
			// Create the controls for "Files with ACLs set/updated"
			{
				m_filesACLRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_FilesSetAcl() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );
	
				++nextRow;
			}
			
			// Create the controls for "Files with ownership set/updated"
			{
				m_filesOwnershipRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_FilesSetOwnership() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );
	
				++nextRow;
			}
		}

		// Add the controls for folder statistics
		{
			FlowPanel labelPanel;
			InlineLabel label;
			
			// Add a "Folders" heading
			{
				InlineLabel heading;
				FlowPanel headingPanel;
				
				heading = new InlineLabel( messages.netFolderSyncStatisticsDlg_FoldersHeading() );
				heading.addStyleName( "netFolderSyncStatisticsDlg_StatisticsHeading" );
				headingPanel = new FlowPanel();
				headingPanel.add( heading );
				m_table.setHTML( nextRow, 0, headingPanel.getElement().getInnerHTML() );
				
				++nextRow;
			}

			label = new InlineLabel();
			label.addStyleName( "netFolderSyncStatisticsDlg_FolderStatisticsLabel" );
			labelPanel = new FlowPanel();
			labelPanel.add( label );
	
			// Create the controls for "Folder count"
			{
				m_folderCountRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_FolderCount() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );
	
				++nextRow;
			}
	
			// Create the controls for "Folders added"
			{
				m_foldesAddedRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_FoldersAdded() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );
	
				++nextRow;
			}
	
			// Create the controls for "Folders expunged"
			{
				m_foldersExpungedRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_FoldersExpunged() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );
	
				++nextRow;
			}
	
			// Create the controls for "Folders with ACLs set/updated"
			{
				m_foldersACLRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_FoldersSetAcl() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );
	
				++nextRow;
			}
	
			// Create the controls for "Folders with ownership set/updated"
			{
				m_foldersOwnershipRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_FoldersSetOwnership() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );
	
				++nextRow;
			}

			// Create the controls for "Folders processed"
			{
				m_foldersProcessedRow = nextRow;
				label.setText( messages.netFolderSyncStatisticsDlg_FoldersProcessed() );
				m_table.setHTML( nextRow, 0, labelPanel.getElement().getInnerHTML() );

				++nextRow;
			}
		}
		
		m_endRow = nextRow - 1;

		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		mainPanel.add( m_table );

		return mainPanel;
	}

	/**
	 * This method will never get called. 
	 */
	@Override
	public Object getDataFromDlg()
	{
		return null;
	}


	/**
	 * 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
	}
	
	/**
	 * 
	 */
	private void getSyncStatistics( final Long netFolderId )
	{
		AsyncCallback<VibeRpcResponse> rpcReadCallback;
		
		// Create a callback that will be called when we receive the net folder sync statistics.
		rpcReadCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
												t,
												GwtTeaming.getMessages().rpcFailure_GetNetFolderSyncStatistics() );
			}
	
			/**
			 * We successfully retrieved the sync statistics for the given net folder
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				if ( response.getResponseData() != null && response.getResponseData() instanceof NetFolderSyncStatistics )
				{
					Scheduler.ScheduledCommand cmd;
					final NetFolderSyncStatistics syncStatistics;
					
					syncStatistics = (NetFolderSyncStatistics) response.getResponseData();
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							init( syncStatistics );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		};

		// Issue an ajax request to get the sync statistics for the given net folder.
		{
			GetNetFolderSyncStatisticsCmd cmd;
			
			cmd = new GetNetFolderSyncStatisticsCmd();
			cmd.setNetFolderId( netFolderId );
			GwtClientHelper.executeCommand( cmd, rpcReadCallback );
		}
	}

	/**
	 * 
	 */
	public void init( final NetFolder netFolder )
	{
		clearErrorPanel();
		hideErrorPanel();
		hideStatusMsg();
		
		// Clear all values currently displayed in the dialog.
		clearAllValues();
		
		getSyncStatistics( netFolder.getId() );
		
		// Update the dialog's header to say "Edit Net Folder"
		setCaption( GwtTeaming.getMessages().netFolderSyncStatisticsDlg_Header( netFolder.getName() ) );

		// Get the sync statistics every 7 seconds.
		m_timer = new Timer()
		{
			@Override
			public void run()
			{
				getSyncStatistics( netFolder.getId() );
			}
		};
		m_timer.scheduleRepeating( 7000 );
	}
	
	/**
	 * Initialize the UI with the sync statistics
	 */
	private void init( NetFolderSyncStatistics syncStatistics )
	{
		if ( syncStatistics == null )
			return;
		
		// Update the miscellaneous statistics
		{
			// Date sync started
			updateDateValue( m_startDateRow, syncStatistics.getStartDate() );
			
			// Date sync stopped
			updateDateValue( m_endDateRow, syncStatistics.getEndDate() );
			
			// Node IP address
			updateStringValue( m_nodeIpAddressRow, syncStatistics.getStatusIpv4Address() );
			
			// Directory only
			if ( m_showDirOnlyRow )
				updateBoolValue( m_dirOnlyRow, syncStatistics.getDirOnly() );
			
			// Directory enumeration
			updateBoolValue( m_dirEnumRow, syncStatistics.getEnumerationFailed() );
			
			// Number of entries expunged
			updateIntValue( m_entriesExpungedRow, syncStatistics.getCountEntryExpunge() );
			
			// Number of operations that failed
			updateIntValue( m_countFailureRow, syncStatistics.getCountFailure() );
		}
		
		// Update the file statistics
		{
			// Files found
			updateIntValue( m_fileCountRow, syncStatistics.getCountFiles() );
			
			// Files added
			updateIntValue( m_filesAddedRow, syncStatistics.getCountFileAdd() );
			
			// Files expunged
			updateIntValue( m_filesExpungedRow, syncStatistics.getCountFileExpunge() );
			
			// Files modified
			updateIntValue( m_filesModifiedRow, syncStatistics.getCountFileModify() );
			
			// Files with ACLs set/updated
			updateIntValue( m_filesACLRow, syncStatistics.getCountFileSetAcl() );
			
			// Files with ownership set/updated
			updateIntValue( m_filesOwnershipRow, syncStatistics.getCountFileSetOwnership() );
		}
		
		// Update the folder statistics
		{
			// Folders found
			updateIntValue( m_folderCountRow, syncStatistics.getCountFolders() );

			// Folders added
			updateIntValue( m_foldesAddedRow, syncStatistics.getCountFolderAdd() );
			
			// Folders expunged
			updateIntValue( m_foldersExpungedRow, syncStatistics.getCountFolderExpunge() );

			// Folders with ACLs set/updated
			updateIntValue( m_foldersACLRow, syncStatistics.getCountFolderSetAcl() );
			
			// Folders with ownership set/updated
			updateIntValue( m_foldersOwnershipRow, syncStatistics.getCountFolderSetOwnership() );
			
			// Folders synchronized
			updateIntValue( m_foldersProcessedRow, syncStatistics.getCountFolderProcessed() );
		}
	}
	
	/**
	 * Called when the dialog is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach()
	{
		// Let the widget detach
		super.onDetach();
		
		// Kill the timer.
		if ( m_timer != null )
		{
			m_timer.cancel();
			m_timer = null;
		}
	}

	/**
	 * Update the given boolean value in the dialog
	 */
	private void updateBoolValue( int row, Boolean value )
	{
		FlowPanel valuePanel;
		InlineLabel valueLabel;
		
		if ( row > m_endRow )
			return;
		
		valueLabel = new InlineLabel( GwtTeaming.getMessages().no() );
		valueLabel.addStyleName( "netFolderSyncStatisticsDlg_StatisticsValue" );
		if ( value != null && value == Boolean.TRUE )
			valueLabel.setText( GwtTeaming.getMessages().yes() );
		
		valuePanel = new FlowPanel();
		valuePanel.add( valueLabel );
		m_table.setHTML( row, 1, valuePanel.getElement().getInnerHTML() );
	}
	
	/**
	 * Update the given date value in the dialog
	 */
	private void updateDateValue( final int row, Long value )
	{
		if ( row > m_endRow )
			return;
		
		if ( value != null )
		{
			GetDateTimeStrCmd cmd;
			AsyncCallback<VibeRpcResponse> getDateStrCallback = null;
			
			getDateStrCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
												t,
												GwtTeaming.getMessages().rpcFailure_GetDateStr() );
				}
		
				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					StringRpcResponseData responseData = null;
					
					if ( response.getResponseData() instanceof StringRpcResponseData )
						responseData = (StringRpcResponseData) response.getResponseData();
					
					if ( responseData != null )
					{
						String dateTimeStr;

						dateTimeStr = responseData.getStringValue();
						if ( dateTimeStr != null )
						{
							FlowPanel valuePanel;
							InlineLabel valueLabel;

							valueLabel = new InlineLabel( dateTimeStr );
							valueLabel.addStyleName( "netFolderSyncStatisticsDlg_StatisticsValue" );
							
							valuePanel = new FlowPanel();
							valuePanel.add( valueLabel );
							m_table.setHTML( row, 1, valuePanel.getElement().getInnerHTML() );
						}
					}
				}
			};
			
			// Issue an rpc request to get the date/time string.
			cmd = new GetDateTimeStrCmd( value, DateFormat.LONG, DateFormat.LONG );
			GwtClientHelper.executeCommand( cmd, getDateStrCallback );
		}
	}
	
	/**
	 * Update the given integer value in the dialog
	 */
	private void updateIntValue( int row, Integer value )
	{
		FlowPanel valuePanel;
		InlineLabel valueLabel;
		
		if ( row > m_endRow )
			return;
		
		valueLabel = new InlineLabel( "0" );
		valueLabel.addStyleName( "netFolderSyncStatisticsDlg_StatisticsValue" );
		if ( value != null )
			valueLabel.setText( value.toString() );
		
		valuePanel = new FlowPanel();
		valuePanel.add( valueLabel );
		m_table.setHTML( row, 1, valuePanel.getElement().getInnerHTML() );
	}
	
	/**
	 * Update the given string value in the dialog
	 */
	private void updateStringValue( int row, String value )
	{
		FlowPanel valuePanel;
		InlineLabel valueLabel;
		
		if ( row > m_endRow )
			return;
		
		if ( value != null )
			valueLabel = new InlineLabel( value );
		else
			valueLabel = new InlineLabel( "" );
		valueLabel.addStyleName( "netFolderSyncStatisticsDlg_StatisticsValue" );
		
		valuePanel = new FlowPanel();
		valuePanel.add( valueLabel );
		m_table.setHTML( row, 1, valuePanel.getElement().getInnerHTML() );
	}
	
	/**
	 * Loads the NetFolderSyncStatisticsDlg split point and returns an instance
	 * of it via the callback.
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final int left,
							final int top,
							final NetFolderSyncStatisticsDlgClient nfssDlgClient )
	{
		GWT.runAsync( NetFolderSyncStatisticsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_NetFolderSyncStatisticsDlg() );
				if ( nfssDlgClient != null )
				{
					nfssDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				NetFolderSyncStatisticsDlg nfssDlg;
				
				nfssDlg = new NetFolderSyncStatisticsDlg(
													autoHide,
													modal,
													left,
													top );
				nfssDlgClient.onSuccess( nfssDlg );
			}
		});
	}
}
