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
package org.kablink.teaming.gwt.client.whatsnew;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.binderviews.util.DeleteEntitiesHelper.DeleteEntitiesCallback;
import org.kablink.teaming.gwt.client.event.ActivityStreamEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamCommentDeletedEvent;
import org.kablink.teaming.gwt.client.event.DeleteActivityStreamUIEntryEvent;
import org.kablink.teaming.gwt.client.event.EditActivityStreamUIEntryEvent;
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
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
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
import org.kablink.teaming.gwt.client.rpc.shared.GetTopLevelEntryIdCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetTopLevelEntryIdRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetUserPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.HasActivityStreamChangedCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveWhatsNewSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
 * @author drfoster@novell.com
 */
public class ActivityStreamCtrl extends ResizeComposite
	implements ClickHandler,
		// Event handlers implemented by this class.
		ActivityStreamEvent.Handler,
		ActivityStreamExitEvent.Handler,
		DeleteActivityStreamUIEntryEvent.Handler,
		EditActivityStreamUIEntryEvent.Handler,
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
	/**
	 * Enumeration class that specifies the usage context of this
	 * ActivityStreamCtrl. 
	 */
	public enum ActivityStreamCtrlUsage {
		BLOG,
		COMMENTS,
		UNREAD_ENTRIES,
		STANDALONE;
		
		public boolean isEmbedded()   {return (!(STANDALONE.equals(this)));}
		public boolean isStandalone() {return    STANDALONE.equals(this);  }
	}

	/**
	 * Enumeration class that defines the format of a description.
	 */
	public enum DescViewFormat {
		FULL,
		PARTIAL
	}
	
	private ActionsPopupMenu				m_actionsPopupMenu;									// This menu is used to display an Actions menu for an item in the list.
	private ActivityStreamCtrlUsage			m_usage;											// How this ActivityStreamCtrl is being used.
	private ActivityStreamDataType			m_showSetting = ActivityStreamDataType.OTHER;		//
	private ActivityStreamInfo				m_activityStreamInfo;								//
	private ActivityStreamParams			m_activityStreamParams;								//
	private ASCLayoutPanel					m_mainLayoutPanel;									//
	private AsyncCallback<VibeRpcResponse>	m_checkForChangesCallback;							//
	private AsyncCallback<VibeRpcResponse>	m_getActivityStreamParamsCallback;					//
	private AsyncCallback<VibeRpcResponse>	m_searchResultsCallback;							//
	private boolean							m_checkForChanges = true;							//
	private boolean							m_searchInProgress;									//
	private DescViewFormat					m_defaultDescViewFormat = DescViewFormat.PARTIAL;	//
	private FlowPanel						m_footerPanel;										//
	private FlowPanel						m_headerPanel;										//
	private FlowPanel						m_msgPanel;											//
	private FlowPanel						m_searchingPanel;									//
	private FlowPanel						m_searchResultsPanel;								//
	private FlowPanel						m_showSettingPanel;									//
	private GwtTeamingImageBundle			m_images;											// Access to our image resources.
	private GwtTeamingMessages				m_messages;											// Access to our localized string resources.
	private Image							m_nextDisabledImg;									//
	private Image							m_nextImg;											//
	private Image							m_pauseImg;											//
	private Image							m_prevDisabledImg;									//
	private Image							m_prevImg;											//
	private Image							m_resumeImg;										//
	private Image							m_showSettingImg1;									//
	private Image							m_showSettingImg2;									//
	private InlineLabel						m_nOfnLabel;										//
	private InlineLabel						m_msgText;											//
	private InlineLabel						m_showSettingLabel;									//
	private InlineLabel						m_sourceName;										//
	private int								m_height;											//
	private int								m_width;											//
	private List<HandlerRegistration>		m_asc_registeredEventHandlers;						// Event handlers that are currently registered.
	private Object							m_selectedObj;										//
	private PagingData						m_pagingData;										//
	private ShareThisDlg2					m_shareThisDlg;										//
	private ShowSettingPopupMenu			m_showSettingPopupMenu;								//
	private SpecificFolderData				m_specificFolderData;								//
	private String							m_asSourcePermalink;								// Permalink to the binder or user that is the source of the activity stream.
	private TagThisDlg						m_tagThisDlg;										//
	private Timer							m_checkForChangesTimer;								// This timer is used to check for updates in the current activity stream.
	private Timer							m_searchTimer;										//
	
	// This is a list of UI widgets, one for each entry returned by the
	// search.  We will reuse these ui widgets every time we get a new
	// page of results.  We will NOT create new ui widgets every time
	// we get a new page of results.
	private ArrayList<ActivityStreamTopEntry> m_searchResultsUIWidgets;
	
	// Used to adjust the size and position of things to account for
	// the padding the footer's style.
	private final static int FOOTER_PADDING_ADJUST	= 6;

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] asc_REGISTERED_EVENTS = new TeamingEvents[] {
		// Activity stream events.
		TeamingEvents.ACTIVITY_STREAM,
		TeamingEvents.ACTIVITY_STREAM_EXIT,
		
		// Delete and edit events.
		TeamingEvents.DELETE_ACTIVITY_STREAM_UI_ENTRY,
		TeamingEvents.EDIT_ACTIVITY_STREAM_UI_ENTRY,
		
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
	
	/*
	 */
	private class ASCLayoutPanel extends VibeDockLayoutPanel {
		/**
		 * Constructor method.
		 * 
		 * @param asCtrl
		 */
		public ASCLayoutPanel(ActivityStreamCtrl asCtrl) {
			super(Style.Unit.PX);
		}
		
		/**
		 */
		@Override
		public void onResize() {
			super.onResize();
		}
	}
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ActivityStreamCtrl(ActivityStreamCtrlUsage usage, boolean createHeader, ActionsPopupMenu actionsMenu) {
		super();
		
		m_usage            = usage;
		m_actionsPopupMenu = actionsMenu;
		
		m_images   = GwtTeaming.getImageBundle();
		m_messages = GwtTeaming.getMessages();
		
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName("activityStreamCtrl");
		
		// Create the list that will hold the UI widgets, one for each
		// entry returned by the search.
		m_searchResultsUIWidgets = new ArrayList<ActivityStreamTopEntry>();
		
		if (createHeader) {
			// Create the header
			createHeader(mainPanel);
		}

		// Create a panel where the search results will live.
		createSearchResultsPanel(mainPanel);

		// Create the footer.
		createFooter(mainPanel);
		
		// Create a panel to hold 'Searching...'
		m_searchingPanel = new FlowPanel();
		m_searchingPanel.addStyleName("activityStreamSearchingPanel");
		mainPanel.add(m_searchingPanel);
		InlineLabel searching = new InlineLabel(m_messages.searching());
		m_searchingPanel.add(searching);
		Image spinnerImg = new Image(m_images.spinner16());
		m_searchingPanel.add(spinnerImg);
		m_searchingPanel.setVisible(false);
		
		// Create a panel to hold 'No entries found'.
		m_msgPanel = new FlowPanel();
		m_msgPanel.addStyleName("activityStreamNoEntriesFoundPanel");
		mainPanel.add(m_msgPanel);
		m_msgText = new InlineLabel("");
		m_msgPanel.add(m_msgText);
		m_msgPanel.setVisible(false);

		// Create the callback that will be used when we issue a GWT
		// RPC call to do a search.
		m_searchResultsCallback = new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_Search() );
				
				m_searchInProgress = false;
				hideSearchingText();
				showMessage(m_messages.noEntriesFound());
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				ActivityStreamDataRpcResponseData asDataResponse = ((ActivityStreamDataRpcResponseData) result.getResponseData());
				final ActivityStreamData activityStreamData = asDataResponse.getActivityStreamDataResults();
				
				if (activityStreamData != null) {
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							m_searchInProgress = false;
							hideSearchingText();

							// Add the search results to the search results widget.
							addSearchResults(activityStreamData);
						}
					});
				}
			}
		};
		m_searchInProgress = false;
		
		// Create the popup menu used to set 'show all' or 'show
		// unread'.
		m_showSettingPopupMenu = new ShowSettingPopupMenu(true, true);
		
		m_mainLayoutPanel = new ASCLayoutPanel(this);
		m_mainLayoutPanel.addStyleName("activityStreamLayoutPanel");
		m_mainLayoutPanel.add(mainPanel);
		
		// All composites must call initWidget() in their constructors.
		initWidget(m_mainLayoutPanel);
	}
	
	/*
	 * Add the given search results to the list of search results.
	 */
	private void addSearchResults(ActivityStreamData activityStreamData) {
		m_pagingData = activityStreamData.getPagingData();
		
		// Get the list of entries from the search.
		List<ActivityStreamEntry> entries = activityStreamData.getEntries();
		
		int displayCount     = 0;
		int searchCountTotal = m_pagingData.getTotalRecords();

		if (null != entries) {
			displayCount = entries.size();
			for (int i = 0; i < displayCount; i += 1) {
				// Get the next entry from the search results.
				ActivityStreamEntry item = entries.get(i);
				
				// We recycle ActivityStreamTopEntry objects.
				// Do we have an old one we can use?
				ActivityStreamTopEntry topEntry;
				if (i < m_searchResultsUIWidgets.size())
				     topEntry = m_searchResultsUIWidgets.get(i);
				else topEntry = null;
				if (null == topEntry) {
					// No, create a new one.
					topEntry = new ActivityStreamTopEntry(this, getDefaultDescViewFormat());
					m_searchResultsUIWidgets.add(topEntry);
				}
				
				// Set the data the ui widget will display.
				topEntry.setData(item);
				
				// Add this ui widget to the search results panel.
				m_searchResultsPanel.add(topEntry);
			}
		}

		// Figure out the position of the last result within the total
		// number of results.
		int position = ((m_pagingData.getPageIndex() * m_pagingData.getEntriesPerPage()) + displayCount);
		
		// Construct the string n - n of n based on the number of items
		// found in the search.
		int value1 = (m_pagingData.getPageIndex() * m_pagingData.getEntriesPerPage()) + 1;
		if (0 == searchCountTotal) {
			value1 = 0;
		}
		
		String nOfn;
		if (m_pagingData.isTotalApproximate())
		     nOfn = m_messages.nOfn_Approximate(value1, position, searchCountTotal);
		else nOfn = m_messages.nOfn_Exact(      value1, position, searchCountTotal);
		m_nOfnLabel.setText(nOfn);

		// Hide the previous and next images.
		m_prevImg.setVisible(false);
		m_nextImg.setVisible(false);
		
		// Do we need to show the 'previous' image?
		if (position > m_pagingData.getEntriesPerPage()) {
			// Yes!
			m_prevDisabledImg.setVisible(false);
			m_prevImg.setVisible(        true );
		}
		else {
			// No!
			m_prevDisabledImg.setVisible(true );
			m_prevImg.setVisible(        false);
		}
		
		// Do we need to show the 'next' image?
		if (searchCountTotal > position) {
			// Yes!
			m_nextDisabledImg.setVisible(false);
			m_nextImg.setVisible(        true );
		}
		else {
			// No!
			m_nextDisabledImg.setVisible(true );
			m_nextImg.setVisible(        false);
		}
		
		// Do we have any results?
		if (0 == displayCount)
		     showMessage(m_messages.noEntriesFound());
		else hideMessage();
	}
	
	/**
	 */
	public void cancelCheckForChangesTimer() {
		if (null != m_checkForChangesTimer) {
			// Cancel the timer.
			m_checkForChangesTimer.cancel();
		}
	}
	
	/**
	 * Issue a GWT RPC request to check for anything new.  If there is
	 * something new we will issue a new search request.
	 */
	public void checkForChanges() {
		// Should we be checking for changes?
		if (!m_checkForChanges) {
			// No, bail
			return;
		}
		
		if (null == m_activityStreamParams) {
			GwtClientHelper.deferredAlert("ActivityStreamCtrl.checkForChanges( *Internal Error* ):  m_activityStreamParams is null.  This should never happen." );
			return;
		}
		
		// Create the callback that will be used when we issue a GWT RPC call to do check for updates.
		if (null == m_checkForChangesCallback) {
			m_checkForChangesCallback = new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					// We don't want to keep checking for changes.
					cancelCheckForChangesTimer();
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_CheckForActivityStreamChanges());
				}
		
				@Override
				public void onSuccess(VibeRpcResponse response) {
					BooleanRpcResponseData responseData = ((BooleanRpcResponseData) response.getResponseData());
					if (responseData.getBooleanValue()) {
						// Is the user composing a reply?
						if (!(isReplyInProgress())) {
							// No!  Refresh the activity stream.
							GwtClientHelper.deferCommand(new ScheduledCommand() {
								@Override
								public void execute() {
									refreshActivityStream();
								}
							});
						}
					}
				}
			};
		}
		
		// Update the text that indicates when we will check for
		// changes.
		updatePauseTitle();
		
		// Issue a GWT RPC request to see if there is anything new.
		HasActivityStreamChangedCmd cmd = new HasActivityStreamChangedCmd(m_activityStreamInfo);
		GwtClientHelper.executeCommand(cmd, m_checkForChangesCallback);
	}
	
	/*
	 * Remove any search results we may be displaying. 
	 */
	private void clearCurrentSearchResults() {
		// Remove all ui widgets we've added.
		m_searchResultsPanel.clear();
		
		// We recycle the ActivityStreamTopEntry objects.
		// Clear the data from the ActivityStreamTopEntry we have
		// created objects.
		for (ActivityStreamTopEntry nextEntry:  m_searchResultsUIWidgets) {
			// Sometimes we hide an entry.  Make sure each entry is
			// visible.
			nextEntry.clearEntrySpecificInfo();
			nextEntry.setVisible(true);
		}
		
		m_nOfnLabel.setText("");
		m_prevImg.setVisible(false);
		m_nextImg.setVisible(false);
		m_prevDisabledImg.setVisible(true);
		m_nextDisabledImg.setVisible(true);
	}
	
	/*
	 * Create the footer that holds the pagination controls.
	 */
	private void createFooter(FlowPanel mainPanel) {
		m_footerPanel = new FlowPanel();
		m_footerPanel.addStyleName("activityStreamCtrlFooter");

		FlexTable table = new FlexTable();
		table.addStyleName("activityStreamFooterImages");
		m_footerPanel.add(table);
		FlowPanel imgPanel = new FlowPanel();
		table.setWidget(0, 0, imgPanel);
		
		// Add the previous images to the footer.
		ImageResource imageResource = m_images.previousDisabled16();
		m_prevDisabledImg = new Image(imageResource);
		m_prevDisabledImg.addStyleName("viewPreviousDisabledImg");
		imgPanel.add(m_prevDisabledImg);
		imageResource = m_images.previous16();
		m_prevImg = new Image(imageResource);
		m_prevImg.addStyleName("cursorPointer");
		m_prevImg.getElement().setAttribute("id", "viewPreviousPageOfResults");
		imgPanel.add(m_prevImg);
		m_prevImg.setVisible(false);
		m_prevImg.addClickHandler(this);
		
		// Add a label that we'll use to display 4-10 of 128
		m_nOfnLabel = new InlineLabel();
		m_nOfnLabel.addStyleName("marginLeftPoint25em"  );
		m_nOfnLabel.addStyleName("marginRightPoint25em" );
		m_nOfnLabel.addStyleName("marginBottomPoint25em");
		imgPanel.add(m_nOfnLabel);

		// Add the next images to the footer.
		imageResource = m_images.nextDisabled16();
		m_nextDisabledImg = new Image(imageResource);
		m_nextDisabledImg.addStyleName("viewNextDisabledImg");
		imgPanel.add(m_nextDisabledImg);
		imageResource = m_images.next16();
		m_nextImg = new Image(imageResource);
		m_nextImg.addStyleName("cursorPointer");
		m_nextImg.getElement().setAttribute("id", "viewNextPageOfResults");
		imgPanel.add(m_nextImg);
		m_nextImg.setVisible(false);
		m_nextImg.addClickHandler(this);
		
		mainPanel.add(m_footerPanel);
	}
	
	/*
	 */
	private void createHeader(FlowPanel mainPanel) {
		m_headerPanel = new FlowPanel();
		m_headerPanel.addStyleName( "activityStreamCtrlHeader" );
		
		FlowPanel header2 = new FlowPanel();
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
		
		// Create a label for 'What's New'.
		InlineLabel whatsNewLabel = new InlineLabel(m_messages.whatsNew());
		whatsNewLabel.addStyleName("activityStreamCtrlHeaderSubtitle");
		header2.add(whatsNewLabel);
		
		// Create a table that will hold Show all/unread, pause/resume and refresh
		FlexTable table = new FlexTable();
		table.addStyleName( "activityStreamCtrlHeaderActionsTable" );
		int col = 0;
		
		// Add the text that displays what the show setting is (show all or show unread)
		// and an image for the user to click on to invoke the 'Show setting' popup menu.
		addShowSettingWidgets( table, 0, col );
		col += 1;
		
		// Add a pause button to the header.
		FlowPanel pauseResumePanel = new FlowPanel();
		ImageResource imageResource = m_images.pauseActivityStream();
		m_pauseImg = new Image( imageResource );
		m_pauseImg.addStyleName( "activityStreamCtrlHeaderPausePlay" );
		m_pauseImg.setVisible( false );
		pauseResumePanel.add( m_pauseImg );
		table.setWidget( 0, col, pauseResumePanel );
		col += 1;
		
		// Add a click handler for the pause button.
		ClickHandler clickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent clickEvent) {
				// Pause the refreshing of the activity stream.
				pauseActivityStream();
			}
		};
		m_pauseImg.addClickHandler(clickHandler);
		
		// Add a resume button to the header.
		imageResource = m_images.resumeActivityStream();
		m_resumeImg = new Image( imageResource );
		m_resumeImg.addStyleName( "activityStreamCtrlHeaderPausePlay" );
		m_resumeImg.setTitle( m_messages.resumeActivityStream() );
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
			
			imageResource = m_images.refresh();
			img = new Image( imageResource );
			img.addStyleName( "activityStreamCtrlHeaderRefresh" );
			img.setTitle( m_messages.refresh() );
			table.setWidget( 0, col, img );
			col += 1;

			// Add a click handler for the refresh button.
			clickHandler = new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent clickEvent )
				{
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							// Issue a request to refresh the activity stream.
							refreshActivityStream();
						}
					});
				}
			};
			img.addClickHandler( clickHandler );
		}
		
		m_headerPanel.add( table );
		
		mainPanel.add( m_headerPanel );
	}
	
	/*
	 * Add the ui widgets needed to allow the user to 'show all' and 'show unread'.
	 */
	private void addShowSettingWidgets( FlexTable table, int row, int col )
	{
		m_showSettingPanel = new FlowPanel();
		m_showSettingPanel.setTitle( m_messages.selectEntryDisplayStyle() );
		
		// Add the label that shows what the current selection is, 'show all' or 'show unread'
		{
			m_showSettingLabel = new InlineLabel();
			m_showSettingLabel.addStyleName( "activityStreamCtrlHeaderShowSettingLabel" );

			m_showSettingPanel.add( m_showSettingLabel );
		}
		
		// Add a 'show all/show unread' image the user can click on.
		{
			ImageResource imageResource;
			
			imageResource = m_images.activityStreamActions1();
			m_showSettingImg1 = new Image( imageResource );
			m_showSettingImg1.addStyleName( "activityStreamCtrlHeaderShowImg" );
			m_showSettingImg1.getElement().setAttribute( "align", "absmiddle" );
			m_showSettingPanel.add( m_showSettingImg1 );
	
			imageResource = m_images.activityStreamActions2();
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
		
		// Add a click handler for the 'show all/show unread' image.
		{
			ClickHandler clickHandler;
			
			clickHandler = new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent clickEvent )
				{
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							m_showSettingPanel.removeStyleName( "activityStreamHover" );
							m_showSettingImg1.setVisible( true );
							m_showSettingImg2.setVisible( false );
	
							// Popup the 'show all/show unread' popup menu.
							m_showSettingPopupMenu.showRelativeToTarget( m_showSettingImg1 );
						}
					});
				}
			};
			m_showSettingImg1.addClickHandler( clickHandler );
			m_showSettingImg2.addClickHandler( clickHandler );
			m_showSettingLabel.addClickHandler( clickHandler );
		}
	}

	/*
	 * Create the panel that will hold the search results. 
	 */
	private void createSearchResultsPanel( FlowPanel mainPanel )
	{
		m_searchResultsPanel = new FlowPanel();
		m_searchResultsPanel.addStyleName( "activityStreamCtrlSearchResultsPanel" );
		
		mainPanel.add( m_searchResultsPanel );
	}
	
	/*
	 * Asynchronously deletes the ActivityStreamUIEntry.
	 */
	private void doDeleteAsync(final ActivityStreamUIEntry uiEntry) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				doDeleteNow(uiEntry);
			}
		});
	}
	
	/*
	 * Synchronously deletes the ActivityStreamUIEntry.
	 */
	private void doDeleteNow(final ActivityStreamUIEntry uiEntry) {
		// Are we deleting a folder entry?
		final EntityId eid = uiEntry.getEntryEntityId();
		if (eid.isFolderEntry()) {
			// Yes!  Can we get its top level entry ID, if it's a
			// comment?
			GetTopLevelEntryIdCmd cmd = new GetTopLevelEntryIdCmd(eid);
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					// No!  Display the error...
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetTopLevelEntryId());
					
					// ...and perform the delete without the top level
					// ...entry ID.  The worst that could happen would
					// ...that the comment counts are off when deleting
					// ...a comment.
					doDeleteImplAsync(uiEntry, null);	// null -> No top level entry to notify about.
				}

				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Yes, we have its top level entry ID, if it was a
					// comment!  Do the delete.
					GetTopLevelEntryIdRpcResponseData reply = ((GetTopLevelEntryIdRpcResponseData) response.getResponseData());
					doDeleteImplAsync(uiEntry, reply.getTopLevelEntityId());
				}
			});
		}
		
		else {
			// No, we aren't deleting a folder entry!  There can be no
			// top level entry ID.
			doDeleteImplNow(uiEntry, null);	// null -> No top level entry to notify about.
		}
	}

	/*
	 * Asynchronously performs the delete and sends a delete
	 * notification about a top level entry having a comment deleted.
	 */
	private void doDeleteImplAsync(final ActivityStreamUIEntry uiEntry, final EntityId topLevelEid) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				doDeleteImplNow(uiEntry, topLevelEid);
			}
		});
	}
	
	/*
	 * Synchronously performs the delete and sends a delete
	 * notification about a top level entry having a comment deleted.
	 */
	private void doDeleteImplNow(final ActivityStreamUIEntry uiEntry, final EntityId topLevelEid) {
		// Perform the delete.
		List<EntityId> delEIDs = new ArrayList<EntityId>();
		delEIDs.add(uiEntry.getEntryEntityId());
		BinderViewsHelper.deleteSelections(delEIDs, new DeleteEntitiesCallback() {
			@Override
			public void operationCanceled() {
				// Nothing to do!
			}

			@Override
			public void operationComplete() {
				// Hide the ActivityStreamUIEntry...
				uiEntry.setVisible(false);

				// ...and if we have a top level entry ID...
				if (null != topLevelEid) {
					// ...send a notification that one of its comments
					// ...was deleted.  This will allow comment counts,
					// ...to be cleaned up to reflect the deletion.
					GwtTeaming.fireEventAsync(new ActivityStreamCommentDeletedEvent(topLevelEid));
				}
			}
			
			@Override
			public void operationFailed() {
				// Nothing to do.  The delete call will have told
				// the user about the failure.
			}
		});
	}

	/*
	 */
	private void executeSearch()
	{
		if ( m_activityStreamParams == null )
		{
			GwtClientHelper.deferredAlert("ActivityStreamCtrl.executeSearch( *Internal Error* ):  m_activityStreamParams is null.  This should never happen.");
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

		// Issue a GWT RPC request to search for the specified type of object.
		m_searchInProgress = true;
		switch (m_showSetting) {
		case ALL:
		case UNREAD:
			break;
			
		default:
			GwtClientHelper.deferredAlert("ActivityStreamCtrl.executeSearch( Unknown m_showSetting ):  " + m_showSetting.name());
			return;
		}
		
		GetActivityStreamDataCmd cmd = new GetActivityStreamDataCmd(m_showSetting, m_activityStreamInfo, m_activityStreamParams, m_pagingData, m_specificFolderData);
		GwtClientHelper.executeCommand(cmd, m_searchResultsCallback);
		
		// We only want to show 'Searching...' after the search has taken more than .5 seconds.
		// Have we already created a timer?
		if (null == m_searchTimer) {
			m_searchTimer = new Timer() {
				@Override
				public void run() {
					// If the search is still in progress show
					// 'Searching...'
					if (m_searchInProgress) {
						showSearchingText();
					}
				}
			};
		}
		m_searchTimer.schedule(250);
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
	
	/*
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
		for (i = 0; i < numEntries; i += 1)
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
	
	/*
	 * Take all the actions necessary to handle the changing of the show setting.
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
	 * Hide the 'Searching...' text.
	 */
	public void hideSearchingText()
	{
		m_searchingPanel.setVisible( false );
	}

	/*
	 * Invoke the 'Send to friend' dialog for the given entry.
	 */
	private void invokeSendToFriendDlg( final ActivityStreamUIEntry entry )
	{
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				GetSendToFriendUrlCmd cmd;
				AsyncCallback<VibeRpcResponse> callback;
				
				callback = new AsyncCallback<VibeRpcResponse>()
				{
					@Override
					public void onFailure(Throwable t)
					{
						GwtClientHelper.handleGwtRPCFailure(
								t,
								m_messages.rpcFailure_GetSendToFriendUrl() );
					}
					
					@Override
					public void onSuccess( VibeRpcResponse response )
					{
						StringRpcResponseData responseData;
						final String url;

						responseData = (StringRpcResponseData) response.getResponseData();
						url = responseData.getStringValue();
						
						if ( url != null )
						{
							GwtClientHelper.deferCommand(new ScheduledCommand() {
								@Override
								public void execute() {
									String features;
									
									// Open a new window for the 'send to friend' page to live in.
									features = "directories=no,location=no,menubar=yes,resizable=yes,scrollbars=yes,status=no,toolbar=no,width=630,height=780";
									Window.open( url, "sendToFriend", features );
								}
							});
						}
					}
				};
				
				// Issue a GWT RPC request to get the url needed to open the 'send to friend' page.
				cmd = new GetSendToFriendUrlCmd( entry.getEntryId() );
				GwtClientHelper.executeCommand( cmd, callback );
			}
		});
	}
	
	/*
	 * Invoke the Subscribe to Entry dialog for the given entry.
	 */
	private void invokeSubscribeToEntryDlg( final ActivityStreamUIEntry entry )
	{
		List<EntityId> entityIds = new ArrayList<EntityId>();
		entityIds.add( entry.getEntryEntityId() );
		BinderViewsHelper.subscribeToEntries( entityIds, entry );
	}
	
	/*
	 * Invoke the 'Share This' dialog for the given entry.
	 */
	private void invokeShareThisDlg( final ActivityStreamUIEntry entry )
	{
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
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
						}
						
						@Override
						public void onSuccess( ShareThisDlg2 stDlg )
						{
							m_shareThisDlg = stDlg;
							GwtClientHelper.deferCommand(new ScheduledCommand() {
								@Override
								public void execute() {
									showShareThisDlg(entry);
								}
							});
						}
					});
				}
			}
		});
	}

	/**
	 * Set the information that is used when searching a specific folder 
	 */
	public void setSpecificFolderData( SpecificFolderData specificFolderData )
	{
		m_specificFolderData = specificFolderData;
	}
	
	/*
	 * Shows an existing 'Share This' dialog for the given entry.
	 */
	private void showShareThisDlg( final ActivityStreamUIEntry entry )
	{
		List<EntityId> entityIds = new ArrayList<EntityId>();
		entityIds.add( entry.getEntryEntityId() );
		ShareThisDlg2.initAndShow(
							m_shareThisDlg,
							entry,
							m_messages.shareCaption(),
							entityIds,
							ShareThisDlgMode.NORMAL,
							null );
	}
	
	/*
	 * Invoke the Tag This dialog for the given entry.
	 */
	private void invokeTagThisDlg( final ActivityStreamUIEntry entry )
	{
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				if ( m_tagThisDlg == null )
				{
					TagThisDlg.createAsync(
							false,
							true,
							null,
							0,
							0,
							m_messages.tagThisEntry(),
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
		});
	}
	
	/*
	 */
	private void invokeTagThisDlgImpl( final ActivityStreamUIEntry entry )
	{
		int y;
		
		y = entry.getAbsoluteTop();
		
		// Sometimes in Firefox getAbsoluteTop() returns the value that would
		// normally be returned by getOffsetTop()
		// Make sure the y value is reasonable.
		if (y > Window.getClientHeight()) {
			y = Window.getClientHeight();
		}

		TagThisDlg.initAndShow(
			m_tagThisDlg,
			entry.getEntryId(),
			entry.getEntryTitle(),
			(Window.getClientWidth() - 75),
			y);
	}	
	
	/*
	 * Is the source of the activity stream a binder?
	 */
	private boolean isActivityStreamSourceABinder() {
		switch (m_activityStreamInfo.getActivityStream()) {
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

	/*
	 * Is the source of the activity stream a person?
	 */
	private boolean isActivityStreamSourceAPerson()
	{
		return ActivityStream.FOLLOWED_PERSON == m_activityStreamInfo.getActivityStream();
	}
	
	/*
	 * Go through all the entries and see if the user has the 'Reply to Entry' widget open
	 */
	private boolean isReplyInProgress()
	{
		boolean inProgress;
		int numEntries;
		int i;
		
		inProgress = false;
		
		// Go through each entry on the page and see if the user has the 'Reply to entry' widget open.
		numEntries = m_searchResultsPanel.getWidgetCount();
		for (i = 0; i < numEntries && inProgress == false; i += 1)
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
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * This method gets called when the user clicks on the 'previous'
	 * or 'next' image in the search results window.
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
						pgIndex += 1;
						m_pagingData.setPageIndex( pgIndex );
					}
					executeSearch();
				}
				
				// Did the user click on previous?
				else if (id.equalsIgnoreCase("viewPreviousPageOfResults")) {
					// Yes, decrement the page number and do another search.
					if (null != m_pagingData) {
						pgIndex -= 1;
						m_pagingData.setPageIndex(pgIndex);
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
	
	/*
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
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				relayoutPageNow();
			}
		});
	}
	
	/*
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
	}

	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we haven't allocated a list to track events we've
		// registered yet...
		if (null == m_asc_registeredEventHandlers) {
			// ...allocate one now.
			m_asc_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if (m_asc_registeredEventHandlers.isEmpty()) {
			// ...register the events.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				asc_REGISTERED_EVENTS,
				this,
				m_asc_registeredEventHandlers);
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
	 * Issue a GWT RPC request to save the current show setting to the
	 * user's properties.
	 */
	private void saveShowSetting()
	{
		AsyncCallback<VibeRpcResponse> callback;
		SaveWhatsNewSettingsCmd cmd;
		
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure( t, m_messages.rpcFailure_SaveWhatsNewShowSetting() );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				// Nothing to do.
			}
		};
		
		// Issue a GWT RPC request to get the permalink of the source of the activity stream.
		cmd = new SaveWhatsNewSettingsCmd( m_showSetting );
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * Set the activity stream this control is dealing with.
	 */
	public void setActivityStream( ActivityStreamInfo activityStreamInfo, final ActivityStreamDataType showSetting )
	{
		ActivityStream src;
		
		// No, issue a GWT RPC request to get the ActivityStreamParams.
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
					msg = m_messages.noFavorites();
				}
				else if ( src == ActivityStream.MY_TEAMS )
				{
					msg = m_messages.noTeams();
				}
				else if ( src == ActivityStream.FOLLOWED_PEOPLE )
				{
					msg = m_messages.noPeopleFollowed();
				}
				else if ( src == ActivityStream.FOLLOWED_PLACES )
				{
					msg = m_messages.noPlacesFollowed();
				}

				// Clear any results we may be currently displaying.
				clearCurrentSearchResults();

				// Display the appropriate message.  i.e., 'you are not a member of any teams'
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
				@Override
				public void onFailure(Throwable t)
				{
					GwtClientHelper.handleGwtRPCFailure( t, m_messages.rpcFailure_GetActivityStreamParams() );
				}

				
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					m_activityStreamParams = (ActivityStreamParams) response.getResponseData();
					
					if ( showSetting != null )
						m_showSetting = showSetting;
					else
						m_showSetting = m_activityStreamParams.getShowSetting();
					
					// Check the appropriate menu item to reflect the show setting.
					m_showSettingPopupMenu.updateMenu( m_showSetting );
					
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							// Now that we have the activity stream parameters, execute the search.
							executeSearch();
							
							// Start the timer that we will use to check for changes.
							startCheckForChangesTimer();
							
							// Update the label that shows whether we are displaying all or unread.
							updateShowSettingLabel();
						}
					});
				}
			};
		}
		
		// Issue a GWT RPC request to get the activity stream
		// parameters.
		GwtClientHelper.executeCommand(new GetActivityStreamParamsCmd(), m_getActivityStreamParamsCallback);
		
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
					@Override
					public void onFailure(Throwable t)
					{
						String msg;
						
						if ( isActivityStreamSourceAPerson() )
						     msg = m_messages.rpcFailure_GetUserPermalink();
						else msg = m_messages.rpcFailure_GetBinderPermalink();
						
						GwtClientHelper.handleGwtRPCFailure( t, msg, asSourceId );
					}
					
					@Override
					public void onSuccess( VibeRpcResponse response )
					{
						StringRpcResponseData responseData;

						responseData = (StringRpcResponseData) response.getResponseData();
						m_asSourcePermalink = responseData.getStringValue();
					}
				};
				
				// Issue a GWT RPC request to get the permalink of the source of the activity stream.
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
		// Register handlers for all the events we are interested in.
		registerEvents();

		if ( ActivityStreamDataType.OTHER != ss )
		{
			handleNewShowSetting( ss, false );
		}
		
		setVisible( true );
		
		// Restart the 'check for changes' timer.
		startCheckForChangesTimer();
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				relayoutPage();
			}
		});
	}
	
	public void show()
	{
		show( ActivityStreamDataType.OTHER );
	}
	
	/**
	 * Show the given message, i.e., 'No entries found'.
	 */
	public void showMessage( String msg )
	{
		m_msgText.setText( msg );
		
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
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
		});
	}

	/**
	 * Show the 'Searching...' text.
	 */
	public void showSearchingText()
	{
		int width;
		int x;
	
		// Center the 'searching...' text
		width = getWidget().getOffsetWidth();
		x = (width - m_searchingPanel.getOffsetWidth()) / 2;
		x -= 40;
		m_searchingPanel.getElement().getStyle().setProperty( "left", Integer.toString( x ) + "px" );
		
		// Show the 'searching...' text
		m_searchingPanel.setVisible( true );
	}

	/*
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
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if (GwtClientHelper.hasItems(m_asc_registeredEventHandlers)) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_asc_registeredEventHandlers);
		}
	}
	
	/*
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
				title = m_messages.pauseActivityStream( seconds );
		}
		
		// Get the current time.
		date = new Date();
		milliSeconds = date.getTime();

		// Add the number of minutes the refresh rate is set to.
		milliSeconds += (seconds * 1000);
		date.setTime( milliSeconds );
		
		dateTimeFormat = DateTimeFormat.getShortTimeFormat();
		text = dateTimeFormat.format( date );
		
		title += m_messages.nextRefresh( text );
		
		if ( m_pauseImg != null )
			m_pauseImg.setTitle( title );
	}
	
	/*
	 * Update the label that display the show setting (show all or show
	 * unread.)
	 */
	private void updateShowSettingLabel() {
		if (null != m_showSettingLabel) {
			String text;
			switch (m_showSetting) {
			case ALL:     text = m_messages.showAllEntries();    break;
			case UNREAD:  text = m_messages.showUnreadEntries(); break;
			default:      text = "Unknown show setting";         break;
			}
			m_showSettingLabel.setText(text);
		}
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
	}
	
	/**
	 * Handles ActivityStreamExitEvent's received by this class.
	 * 
	 * Implements the ActivityStreamExitEvent.Handler.onActivityStreamExit() method.
	 * 
	 * @param event
	 */
	@Override
	public void onActivityStreamExit(ActivityStreamExitEvent event) {
		if (m_usage.isStandalone()) {
			// Unregister all the events we have registered for.
			unregisterEvents();
			hide();
		}
	}

	/**
	 * Handles DeleteActivityStreamUIEntryEvent's received by this class.
	 * 
	 * Implements the DeleteActivityStreamUIEntryEvent.Handler.onDeleteActivityStreamUIEntry() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDeleteActivityStreamUIEntry(DeleteActivityStreamUIEntryEvent event) {
		// If this activity stream control is not visible...
		if (!(isVisible())) {
			// ...ignore this request.
			return;
		}

		// Can we find the entry to delete?
		final ActivityStreamUIEntry uiEntry = event.getUIEntry();
		if (null != uiEntry) {
			// Yes!  Attempt to delete it.
			doDeleteAsync(uiEntry);
		}
	}

	/**
	 * Handles EditActivityStreamUIEntryEvent's received by this class.
	 * 
	 * Implements the EditActivityStreamUIEntryEvent.Handler.onEditActivityStreamUIEntry() method.
	 * 
	 * @param event
	 */
	@Override
	public void onEditActivityStreamUIEntry(EditActivityStreamUIEntryEvent event) {
		// If this activity stream control is not visible...
		if (!(isVisible())) {
			// ...ignore this request.
			return;
		}

		// Can we find the UI entry to be edited?
		final ActivityStreamUIEntry uiEntry = event.getUIEntry();
		if (null != uiEntry) {
			// Yes!  Tell the entry to display the edit UI.
			uiEntry.invokeEditUI();
		}
	}

	/**
	 * Called when widget is detached from the document.
	 * 
	 * Overrides Widget.onDetach()
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/**
	 * Handles InvokeReplyEvent's received by this class.
	 * 
	 * Implements the InvokeReplyEvent.Handler.onInvokeReply() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeReply(InvokeReplyEvent event) {
		ActivityStreamUIEntry uiEntry = event.getUIEntry();
		if (null != uiEntry) {
			// Tell the entry to display the reply ui.
			uiEntry.invokeReplyUI();
		}
	}
	
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
			// Invoke the 'Send to friend' dialog.
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
	}
	
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
	}
	
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
		// Do we have the entry to invoke the tag dialog on?
		ActivityStreamUIEntry uiEntry = event.getUIEntry();
		if ( null != uiEntry )
		{
			// Yes!  Is it from this ActivityStreamCtrl?
			ActivityStreamCtrl asCtrl = uiEntry.getActivityStreamCtrl();
			if ( ( null != asCtrl ) && ( asCtrl.equals( this ) ))
			{
				// Yes!  Invoke the Tag this Entry dialog.
				invokeTagThisDlg( uiEntry );
			}
		}
	}
	
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
			
			// If we are displaying 'show unread' we need to hide this entry.
			hide = false;
			if ( m_showSetting == ActivityStreamDataType.UNREAD )
				hide = true;
			
			// Mark the given entry as read.
			uiEntry.markEntryAsRead( hide );
		}
	}
	
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
	}
	
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
	}
	
	/**
	 * Handles ViewUnreadEntriesEvent's received by this class.
	 * 
	 * Implements the ViewUnreadEntriesEvent.Handler.onViewUnreadEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewUnreadEntries(ViewUnreadEntriesEvent event) {
		handleNewShowSetting(ActivityStreamDataType.UNREAD, true);
	}

	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the activity stream control and perform some operation on it. */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
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
	public static void createAsync(final ActivityStreamCtrlUsage usage, final boolean createHeader, final ActionsPopupMenu actionsMenu, final ActivityStreamCtrlClient asCtrlClient) {
		GWT.runAsync(ActivityStreamCtrl.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				ActivityStreamCtrl asCtrl = new ActivityStreamCtrl(usage, createHeader, actionsMenu);
				asCtrlClient.onSuccess(asCtrl);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_ActivityStreamCtrl());
				asCtrlClient.onUnavailable();
			}
		} );
	}

	public static void createAsync(final ActivityStreamCtrlUsage usage, final ActionsPopupMenu actionsMenu, final ActivityStreamCtrlClient asCtrlClient) {
		// Always use the initial form of the method.
		createAsync(usage, true, actionsMenu, asCtrlClient);	// true -> Create header.
	}
}
