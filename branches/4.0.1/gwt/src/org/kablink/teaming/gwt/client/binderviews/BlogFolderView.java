/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.binderviews;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.BlogArchiveFolder;
import org.kablink.teaming.gwt.client.BlogArchiveMonth;
import org.kablink.teaming.gwt.client.BlogPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.event.BlogArchiveFolderSelectedEvent;
import org.kablink.teaming.gwt.client.event.BlogArchiveMonthSelectedEvent;
import org.kablink.teaming.gwt.client.event.BlogGlobalTagSelectedEvent;
import org.kablink.teaming.gwt.client.event.BlogPageCreatedEvent;
import org.kablink.teaming.gwt.client.event.BlogPageSelectedEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.DownloadFolderAsCSVFileEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.InvokeCopyFiltersDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeDropBoxEvent;
import org.kablink.teaming.gwt.client.event.MarkFolderContentsReadEvent;
import org.kablink.teaming.gwt.client.event.MarkFolderContentsUnreadEvent;
import org.kablink.teaming.gwt.client.event.QuickFilterEvent;
import org.kablink.teaming.gwt.client.event.ResetEntryMenuEvent;
import org.kablink.teaming.gwt.client.event.SetFolderSortEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderSortSettingRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderSortSettingCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveFolderSortCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.SpecificFolderData;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.whatsnew.ActionsPopupMenu;
import org.kablink.teaming.gwt.client.whatsnew.ActionsPopupMenu.ActionMenuItem;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.ActivityStreamCtrlClient;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.ActivityStreamCtrlUsage;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.DescViewFormat;
import org.kablink.teaming.gwt.client.widgets.BlogArchiveCtrl;
import org.kablink.teaming.gwt.client.widgets.BlogPageCtrl;
import org.kablink.teaming.gwt.client.widgets.BlogArchiveCtrl.BlogArchiveCtrlClient;
import org.kablink.teaming.gwt.client.widgets.BlogPageCtrl.BlogPageCtrlClient;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Blog folder view.
 * 
 * @author jwootton@novell.com
 */
public class BlogFolderView extends FolderViewBase
	implements
		// Event handlers implemented by this class.
		BlogArchiveFolderSelectedEvent.Handler,
		BlogArchiveMonthSelectedEvent.Handler,
		BlogGlobalTagSelectedEvent.Handler,
		BlogPageCreatedEvent.Handler,
		BlogPageSelectedEvent.Handler,
		ContributorIdsRequestEvent.Handler,
		DownloadFolderAsCSVFileEvent.Handler,
		InvokeCopyFiltersDlgEvent.Handler,
		InvokeDropBoxEvent.Handler,
		MarkFolderContentsReadEvent.Handler,
		MarkFolderContentsUnreadEvent.Handler,
		QuickFilterEvent.Handler,
		SetFolderSortEvent.Handler
{
	private static final int LIST_MIN_HEIGHT = 150;

	private String m_binderId;
	private String m_binderTitle;
	
	private List<HandlerRegistration> m_registeredEventHandlers;	// Event handlers that are currently registered.
	private ActivityStreamCtrl m_activityStreamCtrl;
	private BlogArchiveCtrl m_archiveCtrl;
	private String m_quickFilter;

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[]
    {
		TeamingEvents.BLOG_ARCHIVE_FOLDER_SELECTED,
		TeamingEvents.BLOG_ARCHIVE_MONTH_SELECTED,
		TeamingEvents.BLOG_GLOBAL_TAG_SELECTED,
		TeamingEvents.BLOG_PAGE_CREATED,
		TeamingEvents.BLOG_PAGE_SELECTED,
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST,
		TeamingEvents.DOWNLOAD_FOLDER_AS_CSV_FILE,
		TeamingEvents.INVOKE_COPY_FILTERS_DLG,
		TeamingEvents.INVOKE_DROPBOX,
		TeamingEvents.MARK_FOLDER_CONTENTS_READ,
		TeamingEvents.MARK_FOLDER_CONTENTS_UNREAD,
		TeamingEvents.QUICK_FILTER,
		TeamingEvents.SET_FOLDER_SORT
	};
	
	/**
	 * Constructor method.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 */
	private BlogFolderView( BinderInfo folderInfo, ViewReady viewReady )
	{
		// Simply initialize the super class.
		super( folderInfo, viewReady, "vibe-blogFolder", false );
		
		m_binderId = folderInfo.getBinderId();
		m_binderTitle = folderInfo.getBinderTitle();
	}
	
	/**
	 * Called to construct the view.
	 * 
	 * Implements the FolderViewBase.constructView() method.
	 */
	@Override
	public void constructView()
	{
		final FlexTable table;
		final VibeFlowPanel rightPanel;
		FlexCellFormatter cellFormatter;
		ActionsPopupMenu actionsMenu;
		ActionMenuItem[] menuItems = {  ActionMenuItem.REPLY,
										ActionMenuItem.SHARE,
										ActionMenuItem.SEND_TO_FRIEND,
										ActionMenuItem.SUBSCRIBE,
										ActionMenuItem.TAG,
										ActionMenuItem.DELETE,
										ActionMenuItem.SEPARATOR,
										ActionMenuItem.MARK_READ,
										ActionMenuItem.MARK_UNREAD };


		getFlowPanel().addStyleName( "vibe-blogFolderFlowPanel" );

		table = new FlexTable();
		table.setWidth( "99%" );
		table.setCellSpacing( 0 );
		table.setCellPadding( 0 );
		table.addStyleName( "blogFolderView_MainTable" );
		
		cellFormatter = table.getFlexCellFormatter();
		cellFormatter.setWidth( 0, 0, "80%" );
		cellFormatter.setWidth( 0, 1, "20%" );
		cellFormatter.setVerticalAlignment( 0, 0, HasVerticalAlignment.ALIGN_TOP );
		cellFormatter.setVerticalAlignment( 0, 1, HasVerticalAlignment.ALIGN_TOP );
		
		actionsMenu = new ActionsPopupMenu( true, true, menuItems );
		
		// Create the ActivityStreamCtrl.  It will hold the list of blog entries
		ActivityStreamCtrl.createAsync( ActivityStreamCtrlUsage.BLOG, false, actionsMenu, new ActivityStreamCtrlClient()
		{			
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess( ActivityStreamCtrl asCtrl )
			{
				m_activityStreamCtrl = asCtrl;
				m_activityStreamCtrl.setCheckForChanges( false );
				m_activityStreamCtrl.setDefaultDescViewFormat( DescViewFormat.FULL );
				
				table.setWidget( 0, 0, asCtrl );

				// Issue an rpc request to get the folder's sort setting.  After we get
				// the sort setting we will search for blog entries in this folder.
				getFolderSortSetting();
			}
		} );
		
		// Create a panel for the Archive control and the global tags control to live in.
		rightPanel = new VibeFlowPanel();
		rightPanel.addStyleName( "blogFolderViewRightPanel" );
		table.setWidget( 0, 1, rightPanel );
		
		// Add the Blog page control.
		BlogPageCtrl.createAsync( new BlogPageCtrlClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in the asyncronous provider.
			}
			
			@Override
			public void onSuccess( BlogPageCtrl bpCtrl )
			{
				bpCtrl.init( getFolderId() );
				rightPanel.add( bpCtrl );
			}
		} );
		
		// Add the Archive control
		BlogArchiveCtrl.createAsync( new BlogArchiveCtrlClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in the asyncronous provider.
			}
			
			@Override
			public void onSuccess( BlogArchiveCtrl baCtrl )
			{
				m_archiveCtrl = baCtrl;
				baCtrl.init( getFolderId() );
				rightPanel.add( baCtrl );

				// Add some space between the blog pages control and the archive control.
				baCtrl.addStyleName( "margintop3" );
				
				// Call viewReady() when we are finished constructing everything.
				viewReady();
			}
		} );
		
		getFlowPanel().add( table );
	}

	/**
	 * Fire the ContextChangedEvent to let everyone know we are working with the given folder.
	 */
	private void fireContextChangedEvent( final boolean refreshSidebarTree )
	{
		if ( m_binderId != null )
		{
			GetBinderPermalinkCmd cmd;
			AsyncCallback<VibeRpcResponse> callback;
			
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
						GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
						m_binderId );
				}
				
				/**
				 * 
				 */
				@Override
				public void onSuccess(  VibeRpcResponse response )
				{
					StringRpcResponseData responseData;
					final String binderPermalink;

					responseData = (StringRpcResponseData) response.getResponseData();
					binderPermalink = responseData.getStringValue();
					
					GwtClientHelper.deferCommand(new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// Tell the side-bar to refresh so it picks up the new folder
							// and selects it.
							if ( refreshSidebarTree )
							{
								GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
							}

							EventHelper.fireContextChangedEventAsync(
									m_binderId,
									binderPermalink,
									Instigator.BLOG_PAGE_SELECT );

						}
					});
				}
			};
			
			// Issue an rpc request to get the binder's permalink
			cmd = new GetBinderPermalinkCmd( m_binderId );
			GwtClientHelper.executeCommand( cmd, callback );
		}
	}

	/**
	 * Issue an rpc request to get the sort setting for this folder.  After we get
	 * the sort setting we will search for blog entries.
	 */
	private void getFolderSortSetting()
	{
		GetFolderSortSettingCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;
		
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
					GwtTeaming.getMessages().rpcFailure_GetFolderSortSetting(),
					m_binderId );
			}
			
			/**
			 * 
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				final GetFolderSortSettingRpcResponseData responseData;

				responseData = (GetFolderSortSettingRpcResponseData) response.getResponseData();
				
				GwtClientHelper.deferCommand(new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						// Search for blog entries in the given folder using the sort
						// setting we just read.
						searchForBlogEntries( responseData.getSortKey(), responseData.getSortDescending() );
					}
				});
			}
		};
		
		// Issue an rpc request to get the folder's sort setting
		cmd = new GetFolderSortSettingCmd( getFolderId() );
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * Returns true for panels that are to be included and false
	 * otherwise.
	 * 
	 * Overrides the FolderViewBase.includePanel() method.
	 * 
	 * @param folderPanel
	 * 
	 * @return
	 */
	@Override
	protected boolean includePanel( FolderPanels folderPanel )
	{
		boolean reply;

		// In the blog folder view, we show the following:
		// 1. Breadcrumb
		// 2. Description
		// 3. Download
		// 4. List of binders control
		// 5. List of blog entries
		// 6. Footer
		switch ( folderPanel )
		{
		case BREADCRUMB:
		case DESCRIPTION:
		case DOWNLOAD:
		case ENTRY_MENU:
		case FOOTER:
			reply = true;
			break;
			
		default:
			reply = false;
			break;
		}
		
		return reply;
	}

	/**
	 * Called when the blog folder view is attached.
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
	 * Handles the BlogArchiveFolderSelectedEvent received by this class.
	 * 
	 * Implements the BlogArchiveFolderSelectedEvent.onBlogArchiveFolderSelectedEvent() method.
	 * 
	 */
	@Override
	public void onBlogArchiveFolderSelected( BlogArchiveFolderSelectedEvent event )
	{
		final BlogArchiveMonth month;
		final BlogArchiveFolder folder;
		
		// Get the month/year and the folder that was selected.
		month = event.getMonth();
		folder = event.getFolder();
		if ( month != null && folder != null )
		{
			GwtClientHelper.deferCommand(new ScheduledCommand()
			{
				@Override
				public void execute() 
				{
					// Find all the blog entries in the folder we are working with that
					// were created in the given month and year.
					m_binderId = folder.getFolderId().toString();
					m_binderTitle = folder.getName();

					// Fire the ContextChangedEvent to notify all interested parties that we
					// should be working with the selected month.
					fireContextChangedEvent( false );
					
					searchForBlogEntries( month.getCreationStartTime(), month.getCreationEndTime() );
				}
			});
		}
	}
	
	/**
	 * Handles the BlogArchiveMonthSelectedEvent received by this class.
	 * 
	 * Implements the BlogArchiveMonthSelectedEvent.onBlogArchiveMonthSelectedEvent() method.
	 * 
	 */
	@Override
	public void onBlogArchiveMonthSelected( BlogArchiveMonthSelectedEvent event )
	{
		final BlogArchiveMonth month;
		
		// Get the month that was selected.
		month = event.getMonth();
		
		if ( month != null )
		{
			GwtClientHelper.deferCommand(new ScheduledCommand()
			{
				@Override
				public void execute() 
				{
					BinderInfo binderInfo;
					
					// Get the binder info for the binder that this view is dealing with.
					binderInfo = getFolderInfo();
					m_binderId = binderInfo.getBinderId();
					m_binderTitle = binderInfo.getBinderTitle();

					// Fire the ContextChangedEvent to notify all interested parties that we
					// should be working with the selected month.
					fireContextChangedEvent( false );
					
					// Find all the blog entries in the folder we are working with that
					// were created in the given month and year.
					searchForBlogEntries( month.getCreationStartTime(), month.getCreationEndTime() );
				}
			});
		}
	}
	
	/**
	 * Handles the BlogGlobalTagSelectedEvent received by this class.
	 * 
	 * Implements the BlogGlobalTagSelectedEvent.onBlogGlobalTagSelectedEvent() method.
	 * 
	 */
	@Override
	public void onBlogGlobalTagSelected( BlogGlobalTagSelectedEvent event )
	{
		final TagInfo tagInfo;
		
		// Get the tag that was selected.
		tagInfo = event.getTagInfo();
		
		if ( tagInfo != null )
		{
			GwtClientHelper.deferCommand(new ScheduledCommand()
			{
				@Override
				public void execute() 
				{
					// Find all the blog entries in the folder we are working with that
					// have the selected tag.
					searchForBlogEntries( tagInfo );
				}
			});
		}
	}
	
	/**
	 * Handles the BlogPageCreatedEvent received by this class.
	 * 
	 * Implements the BlogPageCreatedEvent.onBlogPageCreatedEvent() method.
	 * 
	 */
	@Override
	public void onBlogPageCreated( BlogPageCreatedEvent event )
	{
		final Long folderId;
		
		// Get the id of the newly created blog page
		folderId = event.getFolderId();
		if ( folderId != null )
		{
			GwtClientHelper.deferCommand(new ScheduledCommand()
			{
				@Override
				public void execute() 
				{
					m_binderId = folderId.toString();

					// Fire the ContextChangedEvent to notify all interested parties that we
					// should be working with the selected blog page.
					fireContextChangedEvent( true );
				}
			});
		}
	}
	
	/**
	 * Handles the BlogPageSelectedEvent received by this class.
	 * 
	 * Implements the BlogPageSelectedEvent.onBlogPageSelectedEvent() method.
	 * 
	 */
	@Override
	public void onBlogPageSelected( BlogPageSelectedEvent event )
	{
		final BlogPage blogPage;
		
		// Get the selected blog page.
		blogPage = event.getBlogPage();
		if ( blogPage != null )
		{
			GwtClientHelper.deferCommand(new ScheduledCommand()
			{
				@Override
				public void execute() 
				{
					m_binderId = blogPage.getFolderId();
					m_binderTitle = blogPage.getFolderName();

					// Fire the ContextChangedEvent to notify all interested parties that we
					// should be working with the selected blog page.
					fireContextChangedEvent( false );
					
					// Find all the blog entries in the selected blog folder
					searchForBlogEntries();
					
					// Tell the archive control to clear any selected month/folder/tag
					if ( m_archiveCtrl != null )
						m_archiveCtrl.clearAllSelections();
				}
			});
		}
	}
	
	/**
	 * Handles ContributorIdsRequestEvent's received by this class.
	 * 
	 * Implements the ContributorIdsRequestEvent.Handler.onContributorIdsRequest() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContributorIdsRequest( ContributorIdsRequestEvent event )
	{
		// Is the event targeted to this folder?
		final Long eventBinderId = event.getBinderId();

		// Is this request for the workspace we are working with?
		if ( eventBinderId.equals( getFolderInfo().getBinderIdAsLong() ) )
		{
			// Yes!  Asynchronously fire the corresponding reply event with the contributor IDs.
			GwtClientHelper.deferCommand(new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					ContributorIdsReplyEvent replyEvent;
					ArrayList<Long> contributorIds;
					
					contributorIds = m_activityStreamCtrl.getTopLevelContributorIds();
					
					replyEvent = new ContributorIdsReplyEvent(
														eventBinderId,
														contributorIds ); 
					GwtTeaming.fireEvent( replyEvent );
				}
			});
		}
	}
	
	/**
	 * Called when the blog folder view is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach()
	{
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/**
	 * Handles DownloadFolderAsCSVFileEvent's received by this class.
	 * 
	 * Implements the DownloadFolderAsCSVFileEvent.Handler.onDownloadFolderAsCSVFile() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDownloadFolderAsCSVFile( DownloadFolderAsCSVFileEvent event )
	{
		// Is the event targeted to this folder?
		Long dlFolderId    = event.getFolderId();
		Long eventFolderId = event.getHandleByFolderId();
		if ( null == eventFolderId )
		{
			eventFolderId = dlFolderId;
		}
		if ( eventFolderId.equals( getFolderId() ) )
		{
			// Yes!  Invoke the download.
			BinderViewsHelper.downloadFolderAsCSVFile(
				getDownloadPanel().getForm(),
				dlFolderId );
		}
	}
	
	/**
	 * Handles InvokeCopyFiltersDlgEvent's received by this class.
	 * 
	 * Implements the InvokeCopyFiltersDlgEvent.Handler.onInvokeCopyFiltersDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeCopyFiltersDlg(InvokeCopyFiltersDlgEvent event) {
		// Is the event targeted to this folder?
		BinderInfo eventFolderInfo = event.getFolderInfo();
		if (eventFolderInfo.isEqual(getFolderInfo())) {
			// Yes!  Invoke the copy filters dialog on the folder.
			onInvokeCopyFiltersDlgAsync(eventFolderInfo);
		}
	}

	/*
	 * Asynchronously invokes the copy filters dialog.
	 */
	private void onInvokeCopyFiltersDlgAsync(final BinderInfo folderInfo) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onInvokeCopyFiltersDlgNow(folderInfo);
			}
		} );
	}
	
	/*
	 * Synchronously invokes the copy filters dialog.
	 */
	private void onInvokeCopyFiltersDlgNow(final BinderInfo folderInfo) {
		BinderViewsHelper.invokeCopyFiltersDlg(folderInfo);
	}
		
	/**
	 * Handles InvokeDropBoxEvent's received by this class.
	 * 
	 * Implements the InvokeDropBoxEvent.Handler.onInvokeDropBox() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeDropBox( InvokeDropBoxEvent event )
	{
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if ( eventFolderId.equals( getFolderInfo().getBinderIdAsLong() ) )
		{
			// Yes!  Invoke the files drop box on the folder.
			BinderViewsHelper.invokeDropBox(
				getFolderInfo(),
				getEntryMenuPanel().getAddFilesMenuItem() );
		}
	}
	
	/**
	 * Handles MarkFolderContentsReadEvent's received by this class.
	 * 
	 * Implements the MarkFolderContentsReadEvent.Handler.onMarkFolderContentsRead() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkFolderContentsRead( MarkFolderContentsReadEvent event )
	{
		// Is the event targeted to this folder?
		Long folderId    = event.getFolderId();
		Long eventFolderId = event.getHandleByFolderId();
		if ( null == eventFolderId )
		{
			eventFolderId = folderId;
		}
		if ( eventFolderId.equals( getFolderId() ) )
		{
			// Yes!  Mark the folder contents as having been read.
			BinderViewsHelper.markFolderContentsRead( folderId );
		}
	}
	
	/**
	 * Handles MarkFolderContentsUnreadEvent's received by this class.
	 * 
	 * Implements the MarkFolderContentsUnreadEvent.Handler.onMarkFolderContentsUnread() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkFolderContentsUnread( MarkFolderContentsUnreadEvent event )
	{
		// Is the event targeted to this folder?
		Long folderId    = event.getFolderId();
		Long eventFolderId = event.getHandleByFolderId();
		if ( null == eventFolderId )
		{
			eventFolderId = folderId;
		}
		if ( eventFolderId.equals( getFolderId() ) )
		{
			// Yes!  Mark the folder contents as having been unread.
			BinderViewsHelper.markFolderContentsUnread( folderId );
		}
	}
	
	/**
	 * Handles QuickFilterEvent's received by this class.
	 * 
	 * Implements the QuickFilterEvent.Handler.onQuickFilter() method.
	 * 
	 * @param event
	 */
	@Override
	public void onQuickFilter( QuickFilterEvent event )
	{
		// Is the event is targeted to the folder we're viewing?
		if ( event.getFolderId().equals( getFolderInfo().getBinderIdAsLong() ) )
		{
			// Yes.  Search for blog entries using the quick filter.
			m_quickFilter = event.getQuickFilter();
			searchForBlogEntries();
		}
	}

	/**
	 * Handles SetFolderSortEvent's received by this class.
	 * 
	 * Implements the SetFolderSortEvent.Handler.onSetFolderSort() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSetFolderSort( SetFolderSortEvent event )
	{
		// Is the event is targeted to the folder we're viewing?
		if ( event.getFolderId().equals( getFolderInfo().getBinderIdAsLong() ) )
		{
			String sortBy;
			boolean sortDescending;
			
			// Yes.
			sortBy = event.getSortKey();
			sortDescending = event.getSortDescending();
			
			// Put the filter into affect.
			searchForBlogEntries( sortBy, Boolean.valueOf( sortDescending ) );
			
			// Save the sort setting on the folder
			{
				AsyncCallback<VibeRpcResponse> callback;
				SaveFolderSortCmd cmd;
				
				callback = new AsyncCallback<VibeRpcResponse>()
				{
					@Override
					public void onFailure( Throwable caught )
					{
						GwtClientHelper.handleGwtRPCFailure(
							caught,
							GwtTeaming.getMessages().rpcFailure_SaveFolderSort());
					}
		
					@Override
					public void onSuccess( VibeRpcResponse result )
					{
						GwtTeaming.fireEvent( new ResetEntryMenuEvent( Long.valueOf( m_binderId ) ) );
					}
				};
 
				cmd = new SaveFolderSortCmd( getFolderInfo(), sortBy, (!sortDescending) );
				GwtClientHelper.executeCommand( cmd, callback );
			}
		}
	}

	/*
	 * Asynchronously populates the blog view.
	 */
	private void populateViewAsync()
	{
		GwtClientHelper.deferCommand(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				populateViewNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the the task view.
	 */
	private void populateViewNow()
	{
		viewReady();
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents()
	{
		// If we having allocated a list to track events we've
		// registered yet...
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
										m_registeredEvents,
										this,
										m_registeredEventHandlers );
		}
	}

	/**
	 * Called from the base class to reset the content of this blog folder view
	 * 
	 * Implements the FolderViewBase.resetView() method.
	 */
	@Override
	public void resetView()
	{
		populateViewAsync();
	}
	
	/**
	 * Synchronously sets the size of the view.
	 * 
	 * Implements the FolderViewBase.resizeView() method.
	 */
	@Override
	public void resizeView()
	{
		// Set the size of the list of blog entries
		setListOfBlogEntriesSize();
	}
	
	/**
	 * Do a search for blog entries
	 */
	private void searchForBlogEntries()
	{
		searchForBlogEntries( null, null, null, null, null );
	}

	/**
	 * Do a search for blog entries using the given sort key
	 */
	private void searchForBlogEntries( String sortKey, Boolean sortDescending )
	{
		searchForBlogEntries( sortKey, sortDescending, null, null, null );
	}
	
	/**
	 * Do a search for blog entries using the given creation start/end times.
	 */
	private void searchForBlogEntries( Long creationStartTime, Long creationEndTime )
	{
		searchForBlogEntries( null, null, creationStartTime, creationEndTime, null );
	}
	
	/**
	 * Do a search for blog entries that have the given global tag.
	 */
	private void searchForBlogEntries( TagInfo tagInfo )
	{
		searchForBlogEntries( null, null, null, null, tagInfo );
	}
	
	/**
	 * Do a search for blog entries
	 */
	private void searchForBlogEntries(
								String sortKey, 
								Boolean sortDescending, 
								Long creationStartTime, 
								Long creationEndTime,
								TagInfo tagInfo )
	{
		if ( m_activityStreamCtrl != null )
		{
			ActivityStreamInfo asi;
			
			// Create the SpecificFolderData that will be used to search the blog
			// folder we are working with.
			{
				SpecificFolderData specificFolderData;
	
				specificFolderData = new SpecificFolderData();
				specificFolderData.setApplyFolderFilters( true );
				specificFolderData.setForcePlainTextDescriptions( false );
				specificFolderData.setReturnComments( false );
				specificFolderData.setQuickFilter( m_quickFilter );
				
				// Was a sort key specified?
				if ( sortKey != null && sortKey.length() > 0 )
				{
					// Yes
					specificFolderData.setSortKey( sortKey );
					if ( sortDescending != null )
					{
						specificFolderData.setSortDescending( sortDescending.booleanValue() );
					}
				}
				
				// Was a creation start/end time specified?
				if ( creationStartTime != null && creationEndTime != null )
				{
					// Yes
					specificFolderData.setCreationStartTime( creationStartTime );
					specificFolderData.setCreationEndTime( creationEndTime );
				}
				
				// Was a global tag specified?
				if ( tagInfo != null )
				{
					// Yes
					specificFolderData.setGlobalTagInfo( tagInfo );
				}
				
				m_activityStreamCtrl.setSpecificFolderData( specificFolderData );
			}
			
			asi = new ActivityStreamInfo();
			asi.setActivityStream( ActivityStream.SPECIFIC_FOLDER );
			asi.setBinderId( m_binderId );
			asi.setTitle( m_binderTitle );
	
			m_activityStreamCtrl.setActivityStream( asi, ActivityStreamDataType.ALL );
		}
	}
	
	/**
	 * Set the size of the list of blog entries
	 */
	private void setListOfBlogEntriesSize()
	{
		FooterPanel footerPanel;
		int viewHeight;
		int viewTop;		
		int listTop;		
		int footerPanelHeight;
		int totalBelow;
		int listHeight;

		// Get the sizes we need to calculate the height of the list of blog entries
		footerPanel = getFooterPanel();
		viewHeight = getOffsetHeight();
		viewTop = getAbsoluteTop();		
		listTop = m_activityStreamCtrl.getAbsoluteTop() - viewTop;	// Top of the blog listing relative to the top of the view.
		if ( footerPanel != null )
			footerPanelHeight = footerPanel.getOffsetHeight();
		else
			footerPanelHeight = 0;
		
		totalBelow = footerPanelHeight;

		// Get the optimum height for the task listing so we don't get a vertical scroll bar?
		listHeight = (((viewHeight - listTop) - totalBelow) - 25);
		if ( LIST_MIN_HEIGHT > listHeight)
		{
			// Too small!  Use the minimum even though this will turn on the vertical scroll bar.
			listHeight = LIST_MIN_HEIGHT;
		}
		
		// Set the height of the list of blog entries
		m_activityStreamCtrl.resize( m_activityStreamCtrl.getOffsetWidth(), listHeight );
		m_activityStreamCtrl.show();
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( null != m_registeredEventHandlers && !m_registeredEventHandlers.isEmpty() )
		{
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}
	
	/**
	 * Called when everything about the view (tool panels, ...) is
	 * complete.
	 * 
	 * Overrides the FolderViewBase.viewComplete() method.
	 */
	@Override
	public void viewComplete()
	{
		// Set the size of the list of blog entries
		setListOfBlogEntriesSize();
	}
	
	/**
	 * Loads the BlogFolderView split point and returns an instance of
	 * it via the callback.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync( final BinderInfo folderInfo, final ViewReady viewReady, final ViewClient vClient )
	{
		GWT.runAsync( BlogFolderView.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				BlogFolderView blogFolderView;
				
				blogFolderView = new BlogFolderView( folderInfo, viewReady );
				vClient.onSuccess( blogFolderView );
			}
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( m_messages.codeSplitFailure_BlogFolderView() );
				vClient.onUnavailable();
			}
		});
	}
}
