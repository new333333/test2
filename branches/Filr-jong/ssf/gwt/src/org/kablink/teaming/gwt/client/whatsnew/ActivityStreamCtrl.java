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
package org.kablink.teaming.gwt.client.whatsnew;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.event.ActivityStreamEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent;
import org.kablink.teaming.gwt.client.event.DeleteActivityStreamUIEntryEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.InvokeReplyEvent;
import org.kablink.teaming.gwt.client.event.InvokeSendToFriendEvent;
import org.kablink.teaming.gwt.client.event.InvokeShareEvent;
import org.kablink.teaming.gwt.client.event.InvokeSubscribeEvent;
import org.kablink.teaming.gwt.client.event.InvokeTagEvent;
import org.kablink.teaming.gwt.client.event.MarkEntryReadEvent;
import org.kablink.teaming.gwt.client.event.MarkEntryUnreadEvent;
import org.kablink.teaming.gwt.client.event.ViewAllEntriesEvent;
import org.kablink.teaming.gwt.client.event.ViewUnreadEntriesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActivityStreamData;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamParams;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HistoryHelper;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.PagingData;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.SpecificFolderData;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.ConfirmCallback;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg2;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg2.ShareThisDlgMode;
import org.kablink.teaming.gwt.client.widgets.TagThisDlg;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg2.ShareThisDlg2Client;
import org.kablink.teaming.gwt.client.widgets.TagThisDlg.TagThisDlgClient;
import org.kablink.teaming.gwt.client.widgets.VibeDockLayoutPanel;
import org.kablink.teaming.gwt.client.rpc.shared.ActivityStreamDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetActivityStreamDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetActivityStreamParamsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetSendToFriendUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetUserPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.HasActivityStreamChangedCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveWhatsNewSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This widget will display a list of entries that are the results from
 * a search query.
 * 
 * @author jwootton
 */
public class ActivityStreamCtrl extends ResizeComposite
	implements ClickHandler,
	// Event handlers implemented by this class.
		ActivityStreamEvent.Handler,
		ActivityStreamExitEvent.Handler,
		DeleteActivityStreamUIEntryEvent.Handler,
		InvokeReplyEvent.Handler,
		InvokeSendToFriendEvent.Handler,
		InvokeShareEvent.Handler,
		InvokeSubscribeEvent.Handler,
		InvokeTagEvent.Handler,
		MarkEntryReadEvent.Handler,
		MarkEntryUnreadEvent.Handler,
		ViewAllEntriesEvent.Handler,
		ViewUnreadEntriesEvent.Handler
{
	public enum ActivityStreamCtrlUsage
	{
		BLOG,
		COMMENTS,
		UNREAD_ENTRIES,
		STANDALONE;
		
		public boolean isEmbedded()   { return ( !( STANDALONE.equals( this ) ) ); }
		public boolean isStandalone() { return      STANDALONE.equals( this );     }
	}
	
	public enum DescViewFormat
	{
		FULL,
		PARTIAL
	}
	
	private DescViewFormat m_defaultDescViewFormat = DescViewFormat.PARTIAL;
	private int m_width;
	private int m_height;
	private InlineLabel m_sourceName;
	private ASCLayoutPanel m_mainLayoutPanel;
	private FlowPanel m_headerPanel;
	private FlowPanel m_searchResultsPanel;
	private FlowPanel m_footerPanel;
	private FlowPanel m_showSettingPanel;
	private Object m_selectedObj = null;
	private AsyncCallback<VibeRpcResponse> m_searchResultsCallback;
	private AsyncCallback<VibeRpcResponse> m_checkForChangesCallback = null;
	private AsyncCallback<VibeRpcResponse> m_getActivityStreamParamsCallback = null;
	private PagingData m_pagingData = null;
	private ActivityStreamParams m_activityStreamParams = null;
	private SpecificFolderData m_specificFolderData = null;
	private Timer m_searchTimer = null;
	private Timer m_checkForChangesTimer = null;	// This timer is used to check for updates in the current activity stream.
	private boolean m_checkForChanges = true;
	private boolean m_searchInProgress = false;
	private InlineLabel m_showSettingLabel;
	private Image m_pauseImg;
	private Image m_resumeImg;
	private Image m_prevDisabledImg;
	private Image m_prevImg;
	private Image m_nextDisabledImg;
	private Image m_nextImg;
	private Image m_showSettingImg1;
	private Image m_showSettingImg2;
	private InlineLabel m_nOfnLabel;
	private FlowPanel m_searchingPanel;
	private FlowPanel m_msgPanel;
	private InlineLabel m_msgText;
	private ActivityStreamInfo m_activityStreamInfo = null;
	private String m_asSourcePermalink = null;		// Permalink to the binder or user that is the source of the activity stream.
	// This is a list of ui widgets, one for each entry returned by the search.
	// We will reuse these ui widgets every time we get a new page of results.
	// We will NOT create new ui widgets every time we get a new page of results.
	private ArrayList<ActivityStreamTopEntry> m_searchResultsUIWidgets;
	// This menu is used to display an Actions menu for an item in the list.
	private ActionsPopupMenu m_actionsPopupMenu = null;
	private ShowSettingPopupMenu m_showSettingPopupMenu = null;
	private TagThisDlg m_tagThisDlg = null;
	private ShareThisDlg2 m_shareThisDlg = null;
	private ActivityStreamDataType m_showSetting = ActivityStreamDataType.OTHER;
	private ActivityStreamCtrlUsage m_usage;	// How this ActivityStreamCtrl is being used.
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.

	// Used to adjust the size and position of things to account for
	// the padding the footer's style.
	private final static int FOOTER_PADDING_ADJUST	= 6;

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		// Activity stream events.
		TeamingEvents.ACTIVITY_STREAM,
		TeamingEvents.ACTIVITY_STREAM_EXIT,
		
		// Delete events
		TeamingEvents.DELETE_ACTIVITY_STREAM_UI_ENTRY,
		
		// Invoke events.
		TeamingEvents.INVOKE_REPLY,
		TeamingEvents.INVOKE_SEND_TO_FRIEND,
		TeamingEvents.INVOKE_SHARE,
		TeamingEvents.INVOKE_SUBSCRIBE,
		TeamingEvents.INVOKE_TAG,

		// Marker events.
		TeamingEvents.MARK_ENTRY_READ,
		TeamingEvents.MARK_ENTRY_UNREAD,
		
		// View events.
		TeamingEvents.VIEW_ALL_ENTRIES,
		TeamingEvents.VIEW_UNREAD_ENTRIES,
	};
	
	/**
	 * 
	 */
	private class ASCLayoutPanel extends VibeDockLayoutPanel
	{
		@SuppressWarnings("unused")
		ActivityStreamCtrl m_asCtrl;
		
		/**
		 * 
		 */
		public ASCLayoutPanel( ActivityStreamCtrl asCtrl )
		{
			super( Style.Unit.PX );
			
			m_asCtrl = asCtrl;
		}
		
		/**
		 * 
		 */
		@Override
		public void onResize()
		{
			super.onResize();
			//!!!m_asCtrl.setSize( getOffsetWidth(), getOffsetHeight() );
		}
	}
	
	/*
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ActivityStreamCtrl( ActivityStreamCtrlUsage usage, boolean createHeader, ActionsPopupMenu actionsMenu )
	{
		m_usage = usage;
		m_actionsPopupMenu = actionsMenu;
		
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName( "activityStreamCtrl" );
		
		// Create the list that will hold the ui widgets, one for each entry returned by the search.
		m_searchResultsUIWidgets = new ArrayList<ActivityStreamTopEntry>();
		
		if ( createHeader )
		{
			// Create the header
			createHeader( mainPanel );
		}

		// Create a panel where the search results will live.
		createSearchResultsPanel( mainPanel );

		// Create the footer
		createFooter( mainPanel );
		
		// Create a panel to hold "Searching..."
		{
			InlineLabel searching;
			Image spinnerImg;
			
			m_searchingPanel = new FlowPanel();
			m_searchingPanel.addStyleName( "activityStreamSearchingPanel" );
			mainPanel.add( m_searchingPanel );
			searching = new InlineLabel( GwtTeaming.getMessages().searching() );
			m_searchingPanel.add( searching );
			spinnerImg = new Image( GwtTeaming.getImageBundle().spinner16() );
			m_searchingPanel.add( spinnerImg );
			m_searchingPanel.setVisible( false );
		}
		
		// Create a panel to hold "No entries found"
		{
			m_msgPanel = new FlowPanel();
			m_msgPanel.addStyleName( "activityStreamNoEntriesFoundPanel" );
			mainPanel.add( m_msgPanel );
			m_msgText = new InlineLabel( "" );
			m_msgPanel.add( m_msgText );
			m_msgPanel.setVisible( false );
		}

		// Create the callback that will be used when we issue an ajax call to do a search.
		m_searchResultsCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_Search() );
				
				m_searchInProgress = false;
				hideSearchingText();
				showMessage( GwtTeaming.getMessages().noEntriesFound() );
			}// end onFailure()

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				ActivityStreamDataRpcResponseData asDataResponse = ((ActivityStreamDataRpcResponseData) result.getResponseData());
				final ActivityStreamData activityStreamData = asDataResponse.getActivityStreamDataResults();
				
				if ( activityStreamData != null )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							m_searchInProgress = false;
							hideSearchingText();

							// Add the search results to the search results widget.
							addSearchResults( activityStreamData );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}// end onSuccess()
		};
		m_searchInProgress = false;
		
		// Create the popup menu used to set "show all" or "show unread"
		m_showSettingPopupMenu  = new ShowSettingPopupMenu( true, true );
		
		m_mainLayoutPanel = new ASCLayoutPanel( this );
		m_mainLayoutPanel.addStyleName( "activityStreamLayoutPanel" );
		m_mainLayoutPanel.add( mainPanel );
		
		// All composites must call initWidget() in their constructors.
		initWidget( m_mainLayoutPanel );
	}
	
	
	/**
	 * Add the given search results to the list of search results.
	 */
	private void addSearchResults( ActivityStreamData activityStreamData )
	{
		List<ActivityStreamEntry> entries;
		int position;
		int value1;
		int searchCountTotal;
		int displayCount;
		String nOfn;

		m_pagingData = activityStreamData.getPagingData();
		
		// Get the list of entries from the search.
		entries = activityStreamData.getEntries();
		
		displayCount = 0;
		searchCountTotal = m_pagingData.getTotalRecords();

		if ( entries != null )
		{
			int i;

			displayCount = entries.size();
			for (i = 0; i < displayCount; ++i)
			{
				ActivityStreamEntry item;
				ActivityStreamTopEntry topEntry;
				
				// Get the next entry from the search results.
				item = entries.get( i );
				
				// We recycle ActivityStreamTopEntry objects.
				// Do we have an old one we can use?
				topEntry = null;
				if ( i < m_searchResultsUIWidgets.size() )
					topEntry = m_searchResultsUIWidgets.get( i );
				if ( topEntry == null )
				{
					// No, create a new one.
					topEntry = new ActivityStreamTopEntry( this, getDefaultDescViewFormat() );
					m_searchResultsUIWidgets.add( topEntry );
				}
				
				// Set the data the ui widget will display.
				topEntry.setData( item );
				
				// Add this ui widget to the search results panel.
				m_searchResultsPanel.add( topEntry );
			}// end for()
		}

		// Figure out the position of the last result within the total number of results.
		position = (m_pagingData.getPageIndex() * m_pagingData.getEntriesPerPage()) + displayCount;
		
		// Construct the string n - n of n based on the number of items found in the search.
		value1 = (m_pagingData.getPageIndex() * m_pagingData.getEntriesPerPage()) + 1;
		if ( searchCountTotal == 0 )
			value1 = 0;
		
		nOfn = GwtTeaming.getMessages().nOfn( value1, position, searchCountTotal );
		m_nOfnLabel.setText( nOfn );

		// Hide the previous and next images
		m_prevImg.setVisible( false );
		m_nextImg.setVisible( false );
		
		// Do we need to show the "prev" image?
		if ( position > m_pagingData.getEntriesPerPage() )
		{
			// Yes
			m_prevDisabledImg.setVisible( false );
			m_prevImg.setVisible( true );
		}
		else
		{
			// No
			m_prevDisabledImg.setVisible( true );
			m_prevImg.setVisible( false );
		}
		
		// Do we need to show the "next" image?
		if ( searchCountTotal > position )
		{
			// Yes
			m_nextDisabledImg.setVisible( false );
			m_nextImg.setVisible( true );
		}
		else
		{
			// No
			m_nextDisabledImg.setVisible( true );
			m_nextImg.setVisible( false );
		}
		
		// Do we have any results?
		if ( displayCount == 0 )
		{
			// No
			showMessage( GwtTeaming.getMessages().noEntriesFound() );
		}
		else
		{
			// Yes
			hideMessage();
		}
	}
	
	
	/**
	 * 
	 */
	private void cancelCheckForChangesTimer()
	{
		if ( m_checkForChangesTimer != null )
		{
			// Cancel the timer.
			m_checkForChangesTimer.cancel();
		}
	}
	
	
	/**
	 * Issue an rpc request to check for anything new.  If there is something new we will issue a
	 * new search request.
	 */
	public void checkForChanges()
	{
		// Should we be checking for changes?
		if ( !m_checkForChanges )
		{
			// No, bail
			return;
		}
		
		if ( m_activityStreamParams == null )
		{
			Window.alert( "In checkForChanges(), m_activityStreamParams is null.  This should never happen." );
			return;
		}
		
		// Create the callback that will be used when we issue an ajax call to do check for updates.
		if ( m_checkForChangesCallback == null )
		{
			m_checkForChangesCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure(Throwable t)
				{
					// We don't want to keep checking for changes.
					cancelCheckForChangesTimer();
					
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_CheckForActivityStreamChanges() );
				}// end onFailure()
		
				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					Boolean haveChanges;
					BooleanRpcResponseData responseData;
					
					responseData = (BooleanRpcResponseData) response.getResponseData();
					haveChanges = responseData.getBooleanValue();
					
					if ( haveChanges )
					{
						// Is the user composing a reply?
						if ( isReplyInProgress() == false )
						{
							Scheduler.ScheduledCommand cmd;
							
							// No
							// Refresh the activity stream.
							cmd = new Scheduler.ScheduledCommand()
							{
								@Override
								public void execute()
								{
									refreshActivityStream();
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					}
				}// end onSuccess()
			};
		}
		
		// Update the text that indicates when we will check for changes.
		updatePauseTitle();
		
		// Issue an ajax request to see if there is anything new.
		{
			HasActivityStreamChangedCmd cmd;
			
			cmd = new HasActivityStreamChangedCmd( m_activityStreamInfo );
			GwtClientHelper.executeCommand( cmd, m_checkForChangesCallback );
		}
	}
	
	
	/**
	 * Remove any search results we may be displaying. 
	 */
	private void clearCurrentSearchResults()
	{
		// Remove all ui widgets we've added.
		m_searchResultsPanel.clear();
		
		// We recycle the ActivityStreamTopEntry objects.
		// Clear the data from the ActivityStreamTopEntry we have created objects.
		for ( ActivityStreamTopEntry nextEntry : m_searchResultsUIWidgets)
		{
			nextEntry.clearEntrySpecificInfo();
			
			// Sometimes we hide an entry.  Make sure each entry is visible.
			nextEntry.setVisible( true );
		}
		
		m_nOfnLabel.setText( "" );
		m_prevImg.setVisible( false );
		m_nextImg.setVisible( false );
		m_prevDisabledImg.setVisible( true );
		m_nextDisabledImg.setVisible( true );
	}
	
	
	/**
	 * Create the footer that holds the pagination controls.
	 */
	private void createFooter( FlowPanel mainPanel )
	{
		FlexTable table;
		FlowPanel imgPanel;
		ImageResource imageResource;
		
		m_footerPanel = new FlowPanel();
		m_footerPanel.addStyleName( "activityStreamCtrlFooter" );

		table = new FlexTable();
		table.addStyleName( "activityStreamFooterImages" );
		m_footerPanel.add( table );
		imgPanel = new FlowPanel();
		table.setWidget( 0, 0, imgPanel );
		
		// Add the previous images to the footer.
		imageResource = GwtTeaming.getImageBundle().previousDisabled16();
		m_prevDisabledImg = new Image(imageResource);
		m_prevDisabledImg.addStyleName( "viewPreviousDisabledImg" );
		imgPanel.add( m_prevDisabledImg );
		imageResource = GwtTeaming.getImageBundle().previous16();
		m_prevImg = new Image(imageResource);
		m_prevImg.addStyleName( "cursorPointer" );
		m_prevImg.getElement().setAttribute( "id", "viewPreviousPageOfResults" );
		imgPanel.add( m_prevImg );
		m_prevImg.setVisible( false );
		m_prevImg.addClickHandler( this );
		
		// Add a label that we'll use to display 4-10 of 128
		m_nOfnLabel = new InlineLabel();
		m_nOfnLabel.addStyleName( "marginLeftPoint25em" );
		m_nOfnLabel.addStyleName( "marginRightPoint25em" );
		m_nOfnLabel.addStyleName( "marginBottomPoint25em" );
		imgPanel.add( m_nOfnLabel );

		// Add the next images to the footer.
		imageResource = GwtTeaming.getImageBundle().nextDisabled16();
		m_nextDisabledImg = new Image(imageResource);
		m_nextDisabledImg.addStyleName( "viewNextDisabledImg" );
		imgPanel.add( m_nextDisabledImg );
		imageResource = GwtTeaming.getImageBundle().next16();
		m_nextImg = new Image(imageResource);
		m_nextImg.addStyleName( "cursorPointer" );
		m_nextImg.getElement().setAttribute( "id", "viewNextPageOfResults" );
		imgPanel.add( m_nextImg );
		m_nextImg.setVisible( false );
		m_nextImg.addClickHandler( this );
		
		mainPanel.add( m_footerPanel );
	}
	
	
	/**
	 * 
	 */
	private void createHeader( FlowPanel mainPanel )
	{
		ImageResource imageResource;
		ClickHandler clickHandler;
		InlineLabel whatsNewLabel;
		FlowPanel header2;
		FlowPanel pauseResumePanel;
		FlexTable table;
		int col;

		m_headerPanel = new FlowPanel();
		m_headerPanel.addStyleName( "activityStreamCtrlHeader" );
		
		header2 = new FlowPanel();
		header2.addStyleName( "activityStreamCtrlHeader2" );
		m_headerPanel.add( header2 );
		
		// Create a label where the name of the activity stream source will go.
		m_sourceName = new InlineLabel( " " );
		m_sourceName.addStyleName( "activityStreamCtrlHeaderTitle" );
		header2.add( m_sourceName );
		
		// Add a mouse-over handler for the activity stream source name.
		{
			MouseOverHandler mouseOverHandler;
			
			mouseOverHandler = new MouseOverHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onMouseOver( MouseOverEvent event )
				{
					// Is the activity stream source a binder or person?
					if ( isActivityStreamSourceABinder() || isActivityStreamSourceAPerson() )
					{
						// Yes
						m_sourceName.addStyleName( "activityStreamHover" );
					}
				}
				
			};
			m_sourceName.addMouseOverHandler( mouseOverHandler );
		}
		
		// Add a mouse-out handler for the activity stream source name
		{
			MouseOutHandler mouseOutHandler;
			
			mouseOutHandler = new MouseOutHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onMouseOut( MouseOutEvent event )
				{
					m_sourceName.removeStyleName( "activityStreamHover" );
				}
				
			};
			m_sourceName.addMouseOutHandler( mouseOutHandler );
		}
		
		// Add a click handler for the activity stream source name
		{
			ClickHandler ch;
			
			ch = new ClickHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onClick( ClickEvent event )
				{
					// Is the activity stream source a binder or person?
					if ( isActivityStreamSourceABinder() || isActivityStreamSourceAPerson() )
					{
						String asSourceId;
						
						// Yes
						// Take the user to the source of the selected activity stream.
						asSourceId = getActivityStreamSourceBinderId();
						EventHelper.fireChangeContextEventAsync( asSourceId, m_asSourcePermalink, Instigator.ACTIVITY_STREAM_SOURCE_SELECT );
					}
				}
				
			};
			m_sourceName.addClickHandler( ch );
		}
		
		// Create a label for "What's New"
		whatsNewLabel = new InlineLabel( GwtTeaming.getMessages().whatsNew() );
		whatsNewLabel.addStyleName( "activityStreamCtrlHeaderSubtitle" );
		header2.add( whatsNewLabel );
		
		// Create a table that will hold Show all/unread, pause/resume and refresh
		table = new FlexTable();
		table.addStyleName( "activityStreamCtrlHeaderActionsTable" );
		col = 0;
		
		// Add the text that displays what the show setting is (show all or show unread)
		// and an image for the user to click on to invoke the "Show setting" popup menu.
		addShowSettingWidgets( table, 0, col );
		++col;
		
		// Add a pause button to the header.
		pauseResumePanel = new FlowPanel();
		imageResource = GwtTeaming.getImageBundle().pauseActivityStream();
		m_pauseImg = new Image( imageResource );
		m_pauseImg.addStyleName( "activityStreamCtrlHeaderPausePlay" );
		m_pauseImg.setVisible( false );
		pauseResumePanel.add( m_pauseImg );
		table.setWidget( 0, col, pauseResumePanel );
		++col;
		
		// Add a click handler for the pause button.
		clickHandler = new ClickHandler()
		{
			@Override
			public void onClick( ClickEvent clickEvent )
			{
				// Pause the refreshing of the activity stream.
				pauseActivityStream();
			}
		};
		m_pauseImg.addClickHandler( clickHandler );
		
		// Add a resume button to the header.
		imageResource = GwtTeaming.getImageBundle().resumeActivityStream();
		m_resumeImg = new Image( imageResource );
		m_resumeImg.addStyleName( "activityStreamCtrlHeaderPausePlay" );
		m_resumeImg.setTitle( GwtTeaming.getMessages().resumeActivityStream() );
		m_resumeImg.setVisible( false );
		pauseResumePanel.add( m_resumeImg );
		
		// Add a click handler for the resume button.
		clickHandler = new ClickHandler()
		{
			@Override
			public void onClick( ClickEvent clickEvent )
			{
				// Restart the refreshing of the activity stream.
				resumeActivityStream();
			}
		};
		m_resumeImg.addClickHandler( clickHandler );
		
		// Add a refresh button to the header.
		{
			Image img;
			
			imageResource = GwtTeaming.getImageBundle().refresh();
			img = new Image( imageResource );
			img.addStyleName( "activityStreamCtrlHeaderRefresh" );
			img.setTitle( GwtTeaming.getMessages().refresh() );
			table.setWidget( 0, col, img );
			++col;

			// Add a click handler for the refresh button.
			clickHandler = new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent clickEvent )
				{
					Scheduler.ScheduledCommand cmd;

					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// Issue a request to refresh the activity stream.
							refreshActivityStream();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
			img.addClickHandler( clickHandler );
		}
		
		m_headerPanel.add( table );
		
		mainPanel.add( m_headerPanel );
	}
	
	
	/**
	 * Add the ui widgets needed to allow the user to "show all" and "show unread"
	 */
	private void addShowSettingWidgets( FlexTable table, int row, int col )
	{
		m_showSettingPanel = new FlowPanel();
		m_showSettingPanel.setTitle( GwtTeaming.getMessages().selectEntryDisplayStyle() );
		
		// Add the label that shows what the current selection is, "show all" or "show unread"
		{
			m_showSettingLabel = new InlineLabel();
			m_showSettingLabel.addStyleName( "activityStreamCtrlHeaderShowSettingLabel" );

			m_showSettingPanel.add( m_showSettingLabel );
		}
		
		// Add a "show all/show unread" image the user can click on.
		{
			ImageResource imageResource;
			
			imageResource = GwtTeaming.getImageBundle().activityStreamActions1();
			m_showSettingImg1 = new Image( imageResource );
			m_showSettingImg1.addStyleName( "activityStreamCtrlHeaderShowImg" );
			m_showSettingImg1.getElement().setAttribute( "align", "absmiddle" );
			m_showSettingPanel.add( m_showSettingImg1 );
	
			imageResource = GwtTeaming.getImageBundle().activityStreamActions2();
			m_showSettingImg2 = new Image( imageResource );
			m_showSettingImg2.addStyleName( "activityStreamCtrlHeaderShowImg" );
			m_showSettingImg2.getElement().setAttribute( "align", "absmiddle" );
			m_showSettingImg2.setVisible( false );
			m_showSettingPanel.add( m_showSettingImg2 );
		}

		table.setWidget( row, col, m_showSettingPanel );

		// Add a mouse-over handler for the show setting panel.
		{
			MouseOverHandler mouseOverHandler;
			
			mouseOverHandler = new MouseOverHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onMouseOver( MouseOverEvent event )
				{
					m_showSettingPanel.addStyleName( "activityStreamHover" );
					
					m_showSettingImg1.setVisible( false );
					m_showSettingImg2.setVisible( true );
				}
				
			};
			m_showSettingPanel.addDomHandler( mouseOverHandler, MouseOverEvent.getType() );
		}
		
		// Add a mouse-out handler for the show setting label
		{
			MouseOutHandler mouseOutHandler;
			
			mouseOutHandler = new MouseOutHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onMouseOut( MouseOutEvent event )
				{
					m_showSettingPanel.removeStyleName( "activityStreamHover" );
					
					m_showSettingImg1.setVisible( true );
					m_showSettingImg2.setVisible( false );
				}
				
			};
			m_showSettingPanel.addDomHandler( mouseOutHandler, MouseOutEvent.getType() );
		}
		
		// Add a click handler for the "show all/show unread" image.
		{
			ClickHandler clickHandler;
			
			clickHandler = new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent clickEvent )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							m_showSettingPanel.removeStyleName( "activityStreamHover" );
							m_showSettingImg1.setVisible( true );
							m_showSettingImg2.setVisible( false );
	
							// Popup the "show all/show unread" popup menu.
							m_showSettingPopupMenu.showRelativeToTarget( m_showSettingImg1 );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
			m_showSettingImg1.addClickHandler( clickHandler );
			m_showSettingImg2.addClickHandler( clickHandler );
			m_showSettingLabel.addClickHandler( clickHandler );
		}
	}


	/**
	 * Create the panel that will hold the search results. 
	 */
	private void createSearchResultsPanel( FlowPanel mainPanel )
	{
		m_searchResultsPanel = new FlowPanel();
		m_searchResultsPanel.addStyleName( "activityStreamCtrlSearchResultsPanel" );
		
		mainPanel.add( m_searchResultsPanel );
	}
	
	
	/**
	 * 
	 */
	private void executeSearch()
	{
		if ( m_activityStreamParams == null )
		{
			Window.alert( "In executeSearch(), m_activityStreamParams is null.  This should never happen." );
			return;
		}
		
		// Is a search already in progress?
		if ( m_searchInProgress )
		{
			// Yes.
			return;
		}
		
		// Clear any results we may be currently displaying.
		clearCurrentSearchResults();

		// Issue an ajax request to search for the specified type of object.
		m_searchInProgress = true;
		switch ( m_showSetting )
		{
		case ALL:
		case UNREAD:
			break;
			
		default:
			Window.alert( "in executeSearch() unknown m_showSetting" );
			return;
		}
		
		GetActivityStreamDataCmd cmd = new GetActivityStreamDataCmd( m_showSetting, m_activityStreamInfo, m_activityStreamParams, m_pagingData, m_specificFolderData );
		GwtClientHelper.executeCommand( cmd, m_searchResultsCallback );
		
		// We only want to show "Searching..." after the search has taken more than .5 seconds.
		// Have we already created a timer?
		if ( m_searchTimer == null )
		{
			m_searchTimer = new Timer()
			{
				/**
				 * 
				 */
				@Override
				public void run()
				{
					// If the search is still in progress show "Searching..."
					if ( m_searchInProgress )
						showSearchingText();
				}// end run()
			};
		}
		
		m_searchTimer.schedule( 250 );
	}
	
	
	/**
	 * Return the Actions menu that is used with items in the list.
	 */
	public ActionsPopupMenu getActionsMenu()
	{
		return m_actionsPopupMenu;
	}
	
	
	/**
	 * Return the ActivityStreamInfo object we are currently using.
	 */
	public ActivityStreamInfo getActivityStreamInfo()
	{
		return m_activityStreamInfo;
	}
	
	/**
	 * Return the id of the binder that is the source of the activity stream
	 */
	private String getActivityStreamSourceBinderId()
	{
		String[] binderIds;
		String binderId = null;
		
		binderIds = m_activityStreamInfo.getBinderIds();
		if ( binderIds != null && binderIds.length > 0 )
		{
			binderId = binderIds[0];
		}
		
		return binderId;
	}
	
	/**
	 * Return the style used to display the description of an entry
	 */
	public DescViewFormat getDefaultDescViewFormat()
	{
		return m_defaultDescViewFormat;
	}
	
	/**
	 * 
	 */
	public FlowPanel getSearchResultsPanel()
	{
		return m_searchResultsPanel;
	}
	
	/**
	 * Return the selected object.  The calling method will need to typecast the return value.
	 */
	public Object getSelectedObject()
	{
		return m_selectedObj;
	}
	
	/**
	 * Return the ids of all the contributors of top-level entries
	 */
	public ArrayList<Long> getTopLevelContributorIds()
	{
		HashMap<Long,Long> listOfContributorIds;
		ArrayList<Long> returnValue;
		int numEntries;
		int i;
		
		listOfContributorIds = new HashMap<Long,Long>();
		
		// Go through each entry on the page and get the id of the creator of the entry
		numEntries = m_searchResultsPanel.getWidgetCount();
		for (i = 0; i < numEntries; ++i)
		{
			Widget nextWidget;
			
			nextWidget = m_searchResultsPanel.getWidget( i );
			if ( nextWidget instanceof ActivityStreamTopEntry )
			{
				ActivityStreamTopEntry topEntry;
				Long id;
				
				topEntry = (ActivityStreamTopEntry) nextWidget;
				id = topEntry.getAuthorId();
				listOfContributorIds.put( id, id );
			}
		}
		
		// Create an ArrayList from the HashMap
		{
			Set<Map.Entry<Long, Long>> set;
			Iterator<Map.Entry<Long, Long>> iterator;
			
			returnValue = new ArrayList<Long>();
			
			set = listOfContributorIds.entrySet();
			iterator = set.iterator();
			
			while ( iterator.hasNext() )
			{
				Map.Entry<Long, Long> nextEntry;
				Long nextId;
				
				nextEntry = iterator.next();
				nextId = nextEntry.getKey();
				returnValue.add( nextId );
			}
		}
		
		return returnValue;
	}
	
	
	/**
	 * Take all the actions necessary to handle the changing of the show setting.
	 * 
	 */
	private void handleNewShowSetting( ActivityStreamDataType showSetting, boolean doRefresh )
	{
		m_showSetting = showSetting;
		
		// Update the label that displays what the show setting is.
		updateShowSettingLabel();

		if ( doRefresh )
		{
			// Do a search based on the new show setting.
			refreshActivityStream();
		}

		// Check the appropriate menu item to reflect the show setting.
		m_showSettingPopupMenu.updateMenu( m_showSetting );
		
		// Save the show setting to the user's properties
		saveShowSetting();
	}
	
	
	/**
	 * 
	 */
	public void hide()
	{
		cancelCheckForChangesTimer();
		setVisible( false );
	}
	
	
	/**
	 * Hide the panel that displays the message.
	 */
	public void hideMessage()
	{
		m_msgPanel.setVisible( false );
	}
	
	
	/**
	 * Hide the "Searching..." text.
	 */
	public void hideSearchingText()
	{
		m_searchingPanel.setVisible( false );
	}
	

	/**
	 * Invoke the "Send to friend" dialog for the given entry.
	 */
	private void invokeSendToFriendDlg( final ActivityStreamUIEntry entry )
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				GetSendToFriendUrlCmd cmd;
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
								GwtTeaming.getMessages().rpcFailure_GetSendToFriendUrl() );
					}
					
					/**
					 * 
					 */
					@Override
					public void onSuccess( VibeRpcResponse response )
					{
						StringRpcResponseData responseData;
						final String url;

						responseData = (StringRpcResponseData) response.getResponseData();
						url = responseData.getStringValue();
						
						if ( url != null )
						{
							Scheduler.ScheduledCommand schCmd;

							schCmd = new Scheduler.ScheduledCommand()
							{
								@Override
								public void execute()
								{
									String features;
									
									// Open a new window for the "send to friend" page to live in.
									features = "directories=no,location=no,menubar=yes,resizable=yes,scrollbars=yes,status=no,toolbar=no,width=630,height=780";
									Window.open( url, "sendToFriend", features );
								}
							};
							Scheduler.get().scheduleDeferred( schCmd );
						}
					}
				};
				
				// Issue an ajax request to get the url needed to open the "send to friend" page.
				cmd = new GetSendToFriendUrlCmd( entry.getEntryId() );
				GwtClientHelper.executeCommand( cmd, callback );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Invoke the Subscribe to Entry dialog for the given entry.
	 */
	private void invokeSubscribeToEntryDlg( final ActivityStreamUIEntry entry )
	{
		List<EntityId> entityIds = new ArrayList<EntityId>();
		entityIds.add( entry.getEntryEntityId() );
		BinderViewsHelper.subscribeToEntries( entityIds, entry );
	}
	
	
	/*
	 * Invoke the "Share This" dialog for the given entry.
	 */
	private void invokeShareThisDlg( final ActivityStreamUIEntry entry )
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				// If we've already created the dialog... 
				if ( m_shareThisDlg != null )
				{
					// ...simply show it again with the entry.
					showShareThisDlg( entry );
				}
				else
				{
					// Otherwise, we need to create it!
					ShareThisDlg2.createDlg(
										new Boolean( false ),
										new Boolean( true ),
										0,
										0,
										null,
										null,
										ShareThisDlg2.ShareThisDlgMode.NORMAL,
										new ShareThisDlg2Client()
					{
						@Override
						public void onUnavailable()
						{
							// Nothing to do.  Error handled in
							// asynchronous provider.
						}// end onUnavailable()
						
						@Override
						public void onSuccess( ShareThisDlg2 stDlg )
						{
							Scheduler.ScheduledCommand cmd;
							
							m_shareThisDlg = stDlg;
							cmd = new Scheduler.ScheduledCommand()
							{
								@Override
								public void execute()
								{
									showShareThisDlg( entry );
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}// end onSuccess()
					});
				}
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}// end invokeShareThisDlg()

	
	/**
	 * Set the information that is used when searching a specific folder 
	 */
	public void setSpecificFolderData( SpecificFolderData specificFolderData )
	{
		m_specificFolderData = specificFolderData;
	}
	
	
	/*
	 * Shows an existing "Share This" dialog for the given entry.
	 */
	private void showShareThisDlg( final ActivityStreamUIEntry entry )
	{
		List<EntityId> entityIds = new ArrayList<EntityId>();
		entityIds.add( entry.getEntryEntityId() );
		ShareThisDlg2.initAndShow(
							m_shareThisDlg,
							entry,
							GwtTeaming.getMessages().shareCaption(),
							entityIds,
							ShareThisDlgMode.NORMAL,
							null );
	}// end showShareThisDlg()
	
	
	/**
	 * Invoke the Tag This dialog for the given entry.
	 */
	private void invokeTagThisDlg( final ActivityStreamUIEntry entry )
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				if ( m_tagThisDlg == null )
				{
					TagThisDlg.createAsync(
							false,
							true,
							null,
							0,
							0,
							GwtTeaming.getMessages().tagThisEntry(),
						new TagThisDlgClient() {						
							@Override
							public void onUnavailable() {
								// Nothing to do.  Error handled in
								// asynchronous provider.
							}
							
							@Override
							public void onSuccess(TagThisDlg dlg) {
								m_tagThisDlg = dlg;
								invokeTagThisDlgImpl( entry );
							}
						});
				}
				else
				{
					invokeTagThisDlgImpl( entry );
				}
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}// end invokeTagThisDlg()
	
	/**
	 * 
	 */
	private void invokeTagThisDlgImpl( final ActivityStreamUIEntry entry )
	{
		int y;
		
		y = entry.getAbsoluteTop();
		
		// Sometimes in Firefox getAbsoluteTop() returns the value that would
		// normally be returned by getOffsetTop()
		// Make sure the y value is reasonable.
		if ( y > Window.getClientHeight() )
			y = Window.getClientHeight();

		TagThisDlg.initAndShow(
				m_tagThisDlg,
				entry.getEntryId(),
				entry.getEntryTitle(),
				(Window.getClientWidth() - 75),
				y );
	}// end invokeTagThisDlgImpl()	
	
	/**
	 * Is the source of the activity stream a binder?
	 */
	private boolean isActivityStreamSourceABinder()
	{
		switch ( m_activityStreamInfo.getActivityStream() )
		{
		case FOLLOWED_PLACE:
		case MY_FAVORITE:
		case MY_TEAM:
		case CURRENT_BINDER:
		case SPECIFIC_BINDER:
			return true;
			
		case FOLLOWED_PERSON:
		case FOLLOWED_PEOPLE:
		case FOLLOWED_PLACES:
		case MY_FAVORITES:
		case MY_TEAMS:
		case SITE_WIDE:
		case UNKNOWN:
		default:
			return false;
		}
	}

	
	/**
	 * Is the source of the activity stream a person?
	 */
	private boolean isActivityStreamSourceAPerson()
	{
		return ActivityStream.FOLLOWED_PERSON == m_activityStreamInfo.getActivityStream();
	}
	
	
	/**
	 * Go through all the entries and see if the user has the "Reply to Entry" widget open
	 */
	private boolean isReplyInProgress()
	{
		boolean inProgress;
		int numEntries;
		int i;
		
		inProgress = false;
		
		// Go through each entry on the page and see if the user has the "Reply to entry" widget open.
		numEntries = m_searchResultsPanel.getWidgetCount();
		for (i = 0; i < numEntries && inProgress == false; ++i)
		{
			Widget nextWidget;
			
			nextWidget = m_searchResultsPanel.getWidget( i );
			if ( nextWidget instanceof ActivityStreamTopEntry )
			{
				ActivityStreamTopEntry topEntry;
				
				topEntry = (ActivityStreamTopEntry) nextWidget;
				inProgress = topEntry.checkForReplyInProgress();
			}
		}
		
		return inProgress;
	}
	
	/**
	 * Called when this widget is attached to the document.
	 * 
	 * Overrides Widget.onAttach()
	 */
	@Override
	public void onAttach()
	{
		// Let the widget attach and then register our event handlers.
		super.onAttach();
	
		// Register handlers for all the events we are interested in.
		registerEvents();
	}
	
	/**
	 * This method gets called when the user clicks on the "previous" or "next" image in the search results window.
	 */
	@Override
	public void onClick( ClickEvent clickEvent )
	{
		// If there is already a search in progress, ignore the click.
		if ( m_searchInProgress )
			return;
		
		// Make sure we are receiving this event because the user clicked on an image.
		if ( clickEvent.getSource() instanceof Image )
		{
			Image img;
			String id;
			
			// Get the id of the image that was clicked on.
			img = (Image) clickEvent.getSource();
			id = img.getElement().getAttribute( "id" );
			
			if ( id != null )
			{
				int pgIndex;
				
				pgIndex = m_pagingData.getPageIndex();

				// Did the user click on next?
				if ( id.equalsIgnoreCase( "viewNextPageOfResults" ) )
				{
					// Yes, increment the page number and do another search.
					if ( m_pagingData != null )
					{
						++pgIndex;
						m_pagingData.setPageIndex( pgIndex );
					}
					executeSearch();
				}
				// Did the user click on prev?
				else if ( id.equalsIgnoreCase( "viewPreviousPageOfResults" ) )
				{
					// Yes, decrement the page number and do another search.
					if ( m_pagingData != null )
					{
						--pgIndex;
						m_pagingData.setPageIndex( pgIndex );
					}
					executeSearch();
				}
			}
		}
	}

	
	/**
	 * Pause the refreshing of the activity stream.
	 */
	public void pauseActivityStream()
	{
		// Hide the pause button.
		if ( m_pauseImg != null )
			m_pauseImg.setVisible( false );
		
		// Show the resume button.
		if ( m_resumeImg != null )
			m_resumeImg.setVisible( true );

		cancelCheckForChangesTimer();
	}
	
	
	/**
	 * Issue a new search. 
	 */
	private void refreshActivityStream()
	{
		// Update the label that indicates when we will check for changes.
		updatePauseTitle();
		
		m_pagingData = null;
		executeSearch();
	}
	

	/**
	 * 
	 */
	public void relayoutPage()
	{
		Scheduler.ScheduledCommand cmd;

		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				relayoutPageNow();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}// end relayoutPage()
	
	

	/**
	 * 
	 */
	private void relayoutPageNow()
	{
		int footerHeight;
		int headerHeight = 0;
		int resultsHeight;

		if ( m_width == 0 || m_height == 0 )
			return;
		
		// Figure out how tall to make the search results panel.
		if ( m_headerPanel != null )
			headerHeight = m_headerPanel.getOffsetHeight();
		footerHeight = m_footerPanel.getOffsetHeight();
		
		// Set the width and height of the panel that holds the results.  We subtract 10 from
		// the width to leave space for a vertical scrollbar.
		resultsHeight = (((m_height - headerHeight) - footerHeight) - FOOTER_PADDING_ADJUST);
		m_searchResultsPanel.setHeight( String.valueOf( resultsHeight ) + "px" );
		m_searchResultsPanel.setWidth( String.valueOf( m_width - 10 ) + "px" );
		
		if ( m_headerPanel != null )
			m_headerPanel.setWidth( String.valueOf( m_width ) + "px" );
		m_footerPanel.setWidth( String.valueOf( m_width-6 ) + "px" );
	}// end relayoutPageNow()

	
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
										REGISTERED_EVENTS,
										this,
										m_registeredEventHandlers );
		}
	}
	
	/**
	 * Resume the refreshing of the activity stream.
	 */
	public void resumeActivityStream()
	{
		// Hide the resume button.
		if ( m_resumeImg != null )
			m_resumeImg.setVisible( false );
		
		// Show the pause button.
		if ( m_pauseImg != null )
			m_pauseImg.setVisible( true );

		startCheckForChangesTimer();
	}
	
	
	/*
	 * Issue an ajax request to save the current show setting to the
	 * user's properties.
	 */
	private void saveShowSetting()
	{
		AsyncCallback<VibeRpcResponse> callback;
		SaveWhatsNewSettingsCmd cmd;
		
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure( t, GwtTeaming.getMessages().rpcFailure_SaveWhatsNewShowSetting() );
			}
			
			/**
			 * 
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				// Nothing to do.
			}
		};
		
		// Issue an ajax request to get the permalink of the source of the activity stream.
		cmd = new SaveWhatsNewSettingsCmd( m_showSetting );
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	
	/**
	 * Set the activity stream this control is dealing with.
	 */
	public void setActivityStream( ActivityStreamInfo activityStreamInfo, final ActivityStreamDataType showSetting )
	{
		ActivityStream src;
		
		// No, issue an rpc request to get the ActivityStreamParams.
		m_activityStreamInfo = activityStreamInfo;
		
		// Change our title to reflect the new activity stream source.
		setTitle();
		
		// Hide any message we may be displaying
		hideMessage();
		
		src = m_activityStreamInfo.getActivityStream();
		switch ( src )
		{
		case MY_FAVORITES:
		case MY_TEAMS:
		case FOLLOWED_PEOPLE:
		case FOLLOWED_PLACES:
			String[] binderIds;
			
			// For the selected activity stream, do we have any binders?  For example, is the
			// user a member of any teams?
			binderIds = m_activityStreamInfo.getBinderIds(); 
			if ( binderIds == null || binderIds.length == 0 )
			{
				String msg;
				
				// We don't have any binder ids.
				// Get the appropriate message to display to the user.
				msg = "Unknown";
				if ( src == ActivityStream.MY_FAVORITES )
				{
					msg = GwtTeaming.getMessages().noFavorites();
				}
				else if ( src == ActivityStream.MY_TEAMS )
				{
					msg = GwtTeaming.getMessages().noTeams();
				}
				else if ( src == ActivityStream.FOLLOWED_PEOPLE )
				{
					msg = GwtTeaming.getMessages().noPeopleFollowed();
				}
				else if ( src == ActivityStream.FOLLOWED_PLACES )
				{
					msg = GwtTeaming.getMessages().noPlacesFollowed();
				}

				// Clear any results we may be currently displaying.
				clearCurrentSearchResults();

				// Display the appropriate message.  ie, "you are not a member of any teams"
				showMessage( msg );
				
				return;
			}
			break;
		}

		m_pagingData = null;
		
		if ( m_getActivityStreamParamsCallback == null )
		{
			m_getActivityStreamParamsCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure(Throwable t)
				{
					GwtClientHelper.handleGwtRPCFailure( t, GwtTeaming.getMessages().rpcFailure_GetActivityStreamParams() );
				}

				
				/**
				 * 
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					Scheduler.ScheduledCommand cmd;
					
					m_activityStreamParams = (ActivityStreamParams) response.getResponseData();
					
					if ( showSetting != null )
						m_showSetting = showSetting;
					else
						m_showSetting = m_activityStreamParams.getShowSetting();
					
					// Check the appropriate menu item to reflect the show setting.
					m_showSettingPopupMenu.updateMenu( m_showSetting );
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// Now that we have the activity stream parameters, execute the search.
							executeSearch();
							
							// Start the timer that we will use to check for changes.
							startCheckForChangesTimer();
							
							// Update the label that shows whether we are displaying all or unread.
							updateShowSettingLabel();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
		}
		
		// Issue an ajax request to get the activity stream params.
		{
			GetActivityStreamParamsCmd cmd;
			
			cmd = new GetActivityStreamParamsCmd();
			GwtClientHelper.executeCommand( cmd, m_getActivityStreamParamsCallback );
		}
		
		// Is the source of the activity stream a binder or person?
		m_asSourcePermalink = null;
		if ( isActivityStreamSourceABinder() || isActivityStreamSourceAPerson() )
		{
			final String asSourceId;
			
			// Yes, get the id of the activity stream's source.
			asSourceId = getActivityStreamSourceBinderId();
			if ( asSourceId != null )
			{
				AsyncCallback<VibeRpcResponse> callback;
				
				callback = new AsyncCallback<VibeRpcResponse>()
				{
					/**
					 * 
					 */
					@Override
					public void onFailure(Throwable t)
					{
						String msg;
						
						if ( isActivityStreamSourceAPerson() )
						     msg = GwtTeaming.getMessages().rpcFailure_GetUserPermalink();
						else msg = GwtTeaming.getMessages().rpcFailure_GetBinderPermalink();
						
						GwtClientHelper.handleGwtRPCFailure( t, msg, asSourceId );
					}
					
					/**
					 * 
					 */
					@Override
					public void onSuccess( VibeRpcResponse response )
					{
						StringRpcResponseData responseData;

						responseData = (StringRpcResponseData) response.getResponseData();
						m_asSourcePermalink = responseData.getStringValue();
					}
				};
				
				// Issue an ajax request to get the permalink of the source of the activity stream.
				if ( isActivityStreamSourceAPerson() ) {
					GetUserPermalinkCmd cmd;
					
					cmd = new GetUserPermalinkCmd( asSourceId );
					GwtClientHelper.executeCommand( cmd, callback );
				}
				else {
					GetBinderPermalinkCmd cmd;

					cmd = new GetBinderPermalinkCmd( asSourceId );
					GwtClientHelper.executeCommand( cmd, callback );
				}
			}
		}
	}
	
	
	/**
	 * Reset the size of this control.
	 */
	public void resize( int width, int height )
	{
		// Set the width and height
		setSize( String.valueOf( width ) + "px", String.valueOf( height ) + "px" );
		m_width = width;
		m_height = height;
		relayoutPage();
	}
	
	/**
	 * Set the flag that tells us if we should check for changes in the activity stream.
	 */
	public void setCheckForChanges( boolean checkForChanges )
	{
		m_checkForChanges = checkForChanges;
	}
	
	/**
	 * Set the default style used to display an entry's description
	 */
	public void setDefaultDescViewFormat( DescViewFormat viewFormat )
	{
		m_defaultDescViewFormat = viewFormat;
	}
	
	/**
	 * Set the size of this control.
	 */
	public void setSize( int width, int height )
	{
		// Adjust the width and height for proper spacing.
		width  += GwtConstants.CONTENT_WIDTH_ADJUST;
		height += (GwtConstants.CONTENT_HEIGHT_ADJUST + FOOTER_PADDING_ADJUST);
		
		// Set the width and height
		setSize( String.valueOf( width ) + "px", String.valueOf( height ) + "px" );
		m_width = width;
		m_height = height;
		
		relayoutPage();
	}
	
	
	/**
	 * Set the text in the title.
	 */
	public void setTitle()
	{
		String srcName;

		if ( m_sourceName != null )
		{
			srcName = m_activityStreamInfo.getTitle();
			m_sourceName.setText( srcName );
			m_sourceName.setTitle( srcName );
		}
	}


	/**
	 * 
	 */
	public void show( ActivityStreamDataType ss )
	{
		Scheduler.ScheduledCommand cmd;

		// Register handlers for all the events we are interested in.
		registerEvents();

		if ( ActivityStreamDataType.OTHER != ss )
		{
			handleNewShowSetting( ss, false );
		}
		
		setVisible( true );
		
		// Restart the "check for changes" timer.
		startCheckForChangesTimer();
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				relayoutPage();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	public void show()
	{
		show( ActivityStreamDataType.OTHER );
	}
	
	
	/**
	 * Show the given message, ie "No entries found"
	 */
	public void showMessage( String msg )
	{
		Scheduler.ScheduledCommand cmd;
		
		m_msgText.setText( msg );
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				int width;
				int x;

				// Center the message
				width = getWidget().getOffsetWidth();
				x = (width - m_msgPanel.getOffsetWidth()) / 2;
				x -= 40;
				m_msgPanel.getElement().getStyle().setProperty( "left", Integer.toString( x ) + "px" );
			
				// Show the message
				m_msgPanel.setVisible( true );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}

	
	/**
	 * Show the "Searching..." text.
	 */
	public void showSearchingText()
	{
		int width;
		int x;
	
		// Center the "searching..." text
		width = getWidget().getOffsetWidth();
		x = (width - m_searchingPanel.getOffsetWidth()) / 2;
		x -= 40;
		m_searchingPanel.getElement().getStyle().setProperty( "left", Integer.toString( x ) + "px" );
		
		// Show the "searching..." text
		m_searchingPanel.setVisible( true );
	}

	
	/**
	 * 
	 */
	private void startCheckForChangesTimer()
	{
		// Should we be checking for changes?
		if ( !m_checkForChanges )
		{
			// No, bail
			return;
		}
		
		// Do we have an activity stream parameter object?
		if ( m_activityStreamParams != null )
		{
			int seconds;
			
			// Yes, is auto-refresh turned on?
			seconds = m_activityStreamParams.getClientRefresh();
			if ( seconds > 0 )
			{
				// Yes
				// Have we already created a timer?
				if ( m_checkForChangesTimer == null )
				{
					// No, create one.
					m_checkForChangesTimer = new Timer()
					{
						/**
						 * 
						 */
						@Override
						public void run()
						{
							// Check for changes.
							checkForChanges();
						}
					};
				}

				// Update the label that indicates when we will check for changes.
				updatePauseTitle();
				
				// Start a timer.  When the timer goes off we will check for changes and
				// update the activity stream if there is something new.
				m_checkForChangesTimer.scheduleRepeating( (seconds * 1000) );
	
				// Show the pause button.
				if ( m_pauseImg != null )
					m_pauseImg.setVisible( true );
				
				if ( m_resumeImg != null )
					m_resumeImg.setVisible( false );
			}
		}
	}
	
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( (null != m_registeredEventHandlers) && (!(m_registeredEventHandlers.isEmpty())) )
		{
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}
	
	/**
	 * Update the mouse over text on the pause image
	 */
	@SuppressWarnings("deprecation")
	private void updatePauseTitle()
	{
		String title = "";
		String text;
		Date date;
		DateTimeFormat dateTimeFormat;
		long milliSeconds;
		int seconds = 0;

		if ( m_activityStreamParams != null )
		{
			seconds = m_activityStreamParams.getClientRefresh();
			if ( seconds > 0 )
				title = GwtTeaming.getMessages().pauseActivityStream( seconds );
		}
		
		// Get the current time.
		date = new Date();
		milliSeconds = date.getTime();

		// Add the number of minutes the refresh rate is set to.
		milliSeconds += (seconds * 1000);
		date.setTime( milliSeconds );
		
		dateTimeFormat = DateTimeFormat.getShortTimeFormat();
		text = dateTimeFormat.format( date );
		
		title += GwtTeaming.getMessages().nextRefresh( text );
		
		if ( m_pauseImg != null )
			m_pauseImg.setTitle( title );
	}
	
	
	/**
	 * Update the label that display the show setting (show all or show unread)
	 */
	private void updateShowSettingLabel()
	{
		String text;
		
		if ( m_showSetting == ActivityStreamDataType.ALL )
			text = GwtTeaming.getMessages().showAllEntries();
		else if ( m_showSetting == ActivityStreamDataType.UNREAD )
			text = GwtTeaming.getMessages().showUnreadEntries();
		else
			text = "Unknown show setting";
		
		if ( m_showSettingLabel != null )
			m_showSettingLabel.setText( text );
	}

	/**
	 * Handles ActivityStreamEvent's received by this class.
	 * 
	 * Implements the ActivityStreamEvent.Handler.onActivityStream() method.
	 * 
	 * @param event
	 */
	@Override
	public void onActivityStream( ActivityStreamEvent event )
	{
		// If this is...
		if ( ( ! ( event.isHistoryAction()  ) ) &&	// ...not the result of a history action and...
		     ( ! ( event.isFromEnterEvent() ) ) &&	// ...it's not from an activity stream enter event and...
		     m_usage.isStandalone() )				// ...this is a stand alone activity stream control...
		{
			// ...push the change into the history cache.
			HistoryHelper.pushHistoryInfoAsync(
				event.getActivityStreamInfo(),
				ActivityStreamDataType.OTHER );
		}
		
		setActivityStream( event.getActivityStreamInfo(), null );
		show();
	}// end onActivityStream()
	
	/**
	 * Handles ActivityStreamExitEvent's received by this class.
	 * 
	 * Implements the ActivityStreamExitEvent.Handler.onActivityStreamExit() method.
	 * 
	 * @param event
	 */
	@Override
	public void onActivityStreamExit( ActivityStreamExitEvent event )
	{
		if (m_usage.isStandalone())
		{
			// Unregister all the events we have registered for.
			unregisterEvents();
			
			hide();
		}
	}// end onActivityStreamExit()

	/**
	 * Handles DeleteActivityStreamUIEntryEvent's received by this class.
	 * 
	 * Implements the DeleteActivityStreamUIEntryEvent.Handler.onDeleteActivityStreamUIEntry() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDeleteActivityStreamUIEntry( DeleteActivityStreamUIEntryEvent event )
	{
		final ActivityStreamUIEntry uiEntry = event.getUIEntry();
		if ( null != uiEntry )
		{
			// Ask the user if they really want to delete this entry
			ConfirmDlg.createAsync( new ConfirmDlgClient()
			{
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ConfirmDlg cDlg )
				{
					ConfirmDlg.initAndShow(
						cDlg,
						new ConfirmCallback() 
						{
							@Override
							public void dialogReady() 
							{
								// Ignored.  We don't really care when the dialog is ready.
							}

							@Override
							public void accepted() 
							{
								// Yes, they're sure!
								// Issue an rpc request to delete this entry
								uiEntry.deleteEntry();
							}

							@Override
							public void rejected() 
							{
								// No, they're not sure!
							}
						},
						GwtTeaming.getMessages().confirmDeleteEntry() );
				}
			});
		
		}
	}
	

	/**
	 * Called when widget is detached from the document.
	 * 
	 * Overrides Widget.onDetach()
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
	
	/**
	 * Handles InvokeReplyEvent's received by this class.
	 * 
	 * Implements the InvokeReplyEvent.Handler.onInvokeReply() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeReply( InvokeReplyEvent event )
	{
		ActivityStreamUIEntry uiEntry = event.getUIEntry();
		if ( null != uiEntry )
		{
			// Tell the entry to display the reply ui.
			uiEntry.invokeReplyUI();
		}
	}// end onInvokeReply()
	
	/**
	 * Handles InvokeSendToFriendEvent's received by this class.
	 * 
	 * Implements the InvokeShareEvent.Handler.onInvokeSendToFriend() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeSendToFriend( InvokeSendToFriendEvent event )
	{
		ActivityStreamUIEntry uiEntry = event.getUIEntry();
		if ( null != uiEntry )
		{
			// Invoke the "Send to friend" dialog.
			invokeSendToFriendDlg( uiEntry );
		}
	}
	
	/**
	 * Handles InvokeShareEvent's received by this class.
	 * 
	 * Implements the InvokeShareEvent.Handler.onInvokeShare() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeShare( InvokeShareEvent event )
	{
		ActivityStreamUIEntry uiEntry = event.getUIEntry();
		if ( null != uiEntry )
		{
			// Invoke the Share this Entry dialog.
			invokeShareThisDlg( uiEntry );
		}
	}// end onInvokeShare()
	
	/**
	 * Handles InvokeSubscribeEvent's received by this class.
	 * 
	 * Implements the InvokeSubscribeEvent.Handler.onInvokeSubscribe() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeSubscribe( InvokeSubscribeEvent event )
	{
		ActivityStreamUIEntry uiEntry = event.getUIEntry();
		if ( null != uiEntry )
		{
			// Invoke the Subscribe to Entry dialog.
			invokeSubscribeToEntryDlg( uiEntry );
		}
	}// end onInvokeSubscribe()
	
	/**
	 * Handles InvokeTagEvent's received by this class.
	 * 
	 * Implements the InvokeTagEvent.Handler.onInvokeTag() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeTag( InvokeTagEvent event )
	{
		ActivityStreamUIEntry uiEntry = event.getUIEntry();
		if ( null != uiEntry )
		{
			// Invoke the Tag this Entry dialog.
			invokeTagThisDlg( uiEntry );
		}
	}// end onInvokeTag()
	
	/**
	 * Handles MarkEntryReadEvent's received by this class.
	 * 
	 * Implements the MarkEntryReadEvent.Handler.onMarkEntryRead() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkEntryRead( MarkEntryReadEvent event )
	{
		ActivityStreamUIEntry uiEntry = event.getUIEntry();
		if ( null != uiEntry )
		{
			boolean hide;
			
			// If we are displaying "show unread" we need to hide this entry.
			hide = false;
			if ( m_showSetting == ActivityStreamDataType.UNREAD )
				hide = true;
			
			// Mark the given entry as read.
			uiEntry.markEntryAsRead( hide );
		}
	}// end onMarkEntryRead()
	
	/**
	 * Handles MarkEntryUnreadEvent's received by this class.
	 * 
	 * Implements the MarkEntryUnreadEvent.Handler.onMarkEntryUnread() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkEntryUnread( MarkEntryUnreadEvent event )
	{
		ActivityStreamUIEntry uiEntry = event.getUIEntry();
		if ( null != uiEntry )
		{
			// Mark the given entry as unread.
			uiEntry.markEntryAsUnread();
		}
	}// end onMarkEntryUnread()
	
	/**
	 * Handles ViewAllEntriesEvent's received by this class.
	 * 
	 * Implements the ViewAllEntriesEvent.Handler.onViewAllEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewAllEntries( ViewAllEntriesEvent event )
	{
		handleNewShowSetting( ActivityStreamDataType.ALL, true );
	}// end onViewAllEntries()
	
	/**
	 * Handles ViewUnreadEntriesEvent's received by this class.
	 * 
	 * Implements the ViewUnreadEntriesEvent.Handler.onViewUnreadEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewUnreadEntries( ViewUnreadEntriesEvent event )
	{
		handleNewShowSetting( ActivityStreamDataType.UNREAD, true );
	}// end onViewUnreadEntries()
	
	/**
	 * Callback interface to interact with the content control
	 * asynchronously after it loads. 
	 */
	public interface ActivityStreamCtrlClient {
		void onSuccess(ActivityStreamCtrl asCtrl);
		void onUnavailable();
	}

	/**
	 * Loads the ActivityStreamCtrl split point and returns an instance of
	 * it via the callback.
	 *
	 * @param usage
	 * @param createHeader
	 * @param actionMenu
	 * @param asCtrlClient
	 */
	public static void createAsync( final ActivityStreamCtrlUsage usage, final boolean createHeader, final ActionsPopupMenu actionsMenu, final ActivityStreamCtrlClient asCtrlClient )
	{
		GWT.runAsync( ActivityStreamCtrl.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				ActivityStreamCtrl asCtrl = new ActivityStreamCtrl( usage, createHeader, actionsMenu );
				asCtrlClient.onSuccess( asCtrl );
			}// end onSuccess()
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ActivityStreamCtrl() );
				asCtrlClient.onUnavailable();
			}// end onFailure()
		} );
	}// end createAsync()

	public static void createAsync( final ActivityStreamCtrlUsage usage, final ActionsPopupMenu actionsMenu, final ActivityStreamCtrlClient asCtrlClient )
	{
		// Always use the initial form of the method.
		createAsync( usage, true, actionsMenu, asCtrlClient );	// true -> Create header.
	}// end createAsync()
}// end ActivityStreamCtrl
