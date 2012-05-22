/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.QuickFilterEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.SpecificFolderData;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.whatsnew.ActionsPopupMenu;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl;
import org.kablink.teaming.gwt.client.whatsnew.ActionsPopupMenu.ActionMenuItem;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.ActivityStreamCtrlClient;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.DescViewFormat;

import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Blog folder view.
 * 
 * @author jwootton@novell.com
 */
public class BlogFolderView extends FolderViewBase
	implements
	// Event handlers implemented by this class.
		ContributorIdsRequestEvent.Handler,
		QuickFilterEvent.Handler
{
	private static final int LIST_MIN_HEIGHT = 150;
	
	private List<HandlerRegistration> m_registeredEventHandlers;	// Event handlers that are currently registered.
	private ActivityStreamCtrl m_activityStreamCtrl;
	private String m_quickFilter;

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[]
    {
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST,
		TeamingEvents.QUICK_FILTER
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
		FlexCellFormatter cellFormatter;
		ActionsPopupMenu actionsMenu;
		ActionMenuItem[] menuItems = {  ActionMenuItem.REPLY,
										ActionMenuItem.SEND_TO_FRIEND,
										ActionMenuItem.SUBSCRIBE,
										ActionMenuItem.TAG,
										ActionMenuItem.SEPARATOR,
										ActionMenuItem.MARK_READ,
										ActionMenuItem.MARK_UNREAD };


		getFlowPanel().addStyleName( "vibe-blogFolderFlowPanel" );

		table = new FlexTable();
		table.setCellSpacing( 0 );
		table.addStyleName( "blogFolderView_MainTable" );
		
		cellFormatter = table.getFlexCellFormatter();
		cellFormatter.setWidth( 0, 0, "80%" );
		cellFormatter.setWidth( 0, 1, "20%" );
		
		actionsMenu = new ActionsPopupMenu( true, true, menuItems );
		
		// Create the ActivityStreamCtrl.  It will hold the list of blog entries
		ActivityStreamCtrl.createAsync( false, actionsMenu, new ActivityStreamCtrlClient()
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

				// Search for blog entries
				searchForBlogEntries();
				
				// Call viewReady() when we are finished constructing everything.
				viewReady();
			}
		} );
		
		// Add the Archive control
		{
			table.setText( 0, 1, "This is where the archive control will be" );
		}

		getFlowPanel().add( table );
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
		// 3. List of binders control
		// 4. List of blog entries
		// 5. Footer
		switch ( folderPanel )
		{
		case BREADCRUMB:
		case DESCRIPTION:
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
			ScheduledCommand cmd;
				
			// Yes!  Asynchronously fire the corresponding reply event with the contributor IDs.
			cmd = new ScheduledCommand()
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
			};
			Scheduler.get().scheduleDeferred( cmd );
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

	/*
	 * Asynchronously populates the blog view.
	 */
	private void populateViewAsync()
	{
		Scheduler.ScheduledCommand doPopulate;

		doPopulate = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				populateViewNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
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
		if ( m_activityStreamCtrl != null )
		{
			ActivityStreamInfo asi;
			BinderInfo binderInfo;
	
			binderInfo = getFolderInfo();
			
			// Create the SpecificFolderData that will be used to search the blog
			// folder we are working with.
			{
				SpecificFolderData specificFolderData;
	
				specificFolderData = new SpecificFolderData();
				specificFolderData.setApplyFolderFilters( true );
				specificFolderData.setForcePlainTextDescriptions( false );
				specificFolderData.setReturnComments( false );
				specificFolderData.setQuickFilter( m_quickFilter );
				
				m_activityStreamCtrl.setSpecificFolderData( specificFolderData );
			}
			
			asi = new ActivityStreamInfo();
			asi.setActivityStream( ActivityStream.SPECIFIC_FOLDER );
			asi.setBinderId( binderInfo.getBinderId());
			asi.setTitle( binderInfo.getBinderTitle() );
	
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
		listHeight = (((viewHeight - listTop) - totalBelow) - 20);
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
