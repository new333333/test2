/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
import java.util.List;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActivityStreamData;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamParams;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.ShowSetting;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.PagingData;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg;
import org.kablink.teaming.gwt.client.widgets.SubscribeToEntryDlg;
import org.kablink.teaming.gwt.client.widgets.TagThisDlg;
import org.kablink.teaming.gwt.client.widgets.TagThisDlg.TagThisDlgClient;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * This widget will display a list of entries that are the results from a search query.
 * @author jwootton
 *
 */
public class ActivityStreamCtrl extends Composite
	implements ActionHandler, ClickHandler
{
	private int m_width;
	private int m_height;
	private InlineLabel m_sourceName;
	private FlowPanel m_headerPanel;
	private FlowPanel m_searchResultsPanel;
	private FlowPanel m_footerPanel;
	private FlowPanel m_showSettingPanel;
	private Object m_selectedObj = null;
	private ActionHandler m_actionHandler;
	private AsyncCallback<ActivityStreamData> m_searchResultsCallback;
	private AsyncCallback<Boolean> m_checkForChangesCallback = null;
	private AsyncCallback<ActivityStreamParams> m_getActivityStreamParamsCallback = null;
	private PagingData m_pagingData = null;
	private ActivityStreamParams m_activityStreamParams = null;
	private Timer m_searchTimer = null;
	private Timer m_checkForChangesTimer = null;	// This timer is used to check for updates in the current activity stream.
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
	private GwtRpcServiceAsync m_rpcService = null;
	private String m_asSourcePermalink = null;		// Permalink to the binder or user that is the source of the activity stream.
	// This is a list of ui widgets, one for each entry returned by the search.
	// We will reuse these ui widgets every time we get a new page of results.
	// We will NOT create new ui widgets every time we get a new page of results.
	private ArrayList<ActivityStreamTopEntry> m_searchResultsUIWidgets;
	// This menu is used to display an Actions menu for an item in the list.
	private static ActionsPopupMenu m_actionsPopupMenu = null;
	private ShowSettingPopupMenu m_showSettingPopupMenu = null;
	private SubscribeToEntryDlg m_subscribeToEntryDlg = null;
	private TagThisDlg m_tagThisDlg = null;
	private ShareThisDlg m_shareThisDlg = null;
	private ShowSetting m_showSetting = ShowSetting.UNKNOWN;

	
	/*
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ActivityStreamCtrl(
		ActionHandler actionHandler )  // We will call this handler when the user selects an item from the search results.
	{
		FlowPanel mainPanel;

		// Get the rpc service.
		m_rpcService = GwtTeaming.getRpcService();
		
		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "activityStreamCtrl" );
		
		// Remember the handler we should call when the user selects an item from the search results.
		m_actionHandler = actionHandler;
		
		// Create the list that will hold the ui widgets, one for each entry returned by the search.
		m_searchResultsUIWidgets = new ArrayList<ActivityStreamTopEntry>();
		
		// Create the header
		createHeader( mainPanel );

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
		m_searchResultsCallback = new AsyncCallback<ActivityStreamData>()
		{
			/**
			 * 
			 */
			public void onFailure(Throwable t)
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_Search() );
				
				m_searchInProgress = false;
				hideSearchingText();
				showMessage( GwtTeaming.getMessages().noEntriesFound() );
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( final ActivityStreamData activityStreamData )
			{
				if ( activityStreamData != null )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
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
		
		// Create an Actions popup menu.
		createActionsPopupMenu();
		
		// Create the popup menu used to set "show all" or "show unread"
		m_showSettingPopupMenu  = new ShowSettingPopupMenu( true, true, this );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
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
					topEntry = new ActivityStreamTopEntry( this, m_actionHandler );
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
		if ( m_activityStreamParams == null )
		{
			Window.alert( "In checkForChanges(), m_activityStreamParams is null.  This should never happen." );
			return;
		}
		
		// Create the callback that will be used when we issue an ajax call to do check for updates.
		if ( m_checkForChangesCallback == null )
		{
			m_checkForChangesCallback = new AsyncCallback<Boolean>()
			{
				/**
				 * 
				 */
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
				public void onSuccess( Boolean haveChanges )
				{
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
		m_rpcService.hasActivityStreamChanged( HttpRequestInfo.createHttpRequestInfo(), m_activityStreamInfo, m_checkForChangesCallback );
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
		DOM.setElementAttribute( m_prevImg.getElement(), "id", "viewPreviousPageOfResults" );
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
		DOM.setElementAttribute( m_nextImg.getElement(), "id", "viewNextPageOfResults" );
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
				public void onClick( ClickEvent event )
				{
					// Is the activity stream source a binder or person?
					if ( isActivityStreamSourceABinder() || isActivityStreamSourceAPerson() )
					{
						OnSelectBinderInfo binderInfo;
						String asSourceId;
						
						// Yes
						// Take the user to the source of the selected activity stream.
						asSourceId = getActivityStreamSourceBinderId();
						binderInfo = new OnSelectBinderInfo( asSourceId, m_asSourcePermalink, false, Instigator.OTHER );
						m_actionHandler.handleAction( TeamingAction.SELECTION_CHANGED, binderInfo );
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
				public void onClick( ClickEvent clickEvent )
				{
					Scheduler.ScheduledCommand cmd;

					cmd = new Scheduler.ScheduledCommand()
					{
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
				public void onClick( ClickEvent clickEvent )
				{
					Scheduler.ScheduledCommand cmd;
					final int x;
					final int y;
					
					x = clickEvent.getClientX();
					y = clickEvent.getClientY();
					cmd = new Scheduler.ScheduledCommand()
					{
						public void execute()
						{
							m_showSettingPanel.removeStyleName( "activityStreamHover" );
							m_showSettingImg1.setVisible( true );
							m_showSettingImg2.setVisible( false );
	
							// Popup the "show all/show unread" popup menu.
							m_showSettingPopupMenu.showMenu( x, y );
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
	 * Create the actions popup menu
	 */
	private void createActionsPopupMenu()
	{
		// Have we created an ActionsMenu yet?
		if ( m_actionsPopupMenu == null )
		{
			// No, create one.
			m_actionsPopupMenu = new ActionsPopupMenu( true, true, this );
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
	public void executeSearch()
	{
		ActivityStreamDataType asType;
		
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
		if ( m_showSetting == ShowSetting.SHOW_ALL )
			asType = ActivityStreamDataType.ALL;
		else if ( m_showSetting == ShowSetting.SHOW_UNREAD )
			asType = ActivityStreamDataType.UNREAD;
		else
		{
			Window.alert( "in executeSearch() unknown m_showSetting" );
			return;
		}
		m_rpcService.getActivityStreamData( HttpRequestInfo.createHttpRequestInfo(), m_activityStreamParams, m_activityStreamInfo, m_pagingData, asType, m_searchResultsCallback );

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
	public static ActionsPopupMenu getActionsMenu()
	{
		return m_actionsPopupMenu;
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
	 * Return the selected object.  The calling method will need to typecast the return value.
	 */
	public Object getSelectedObject()
	{
		return m_selectedObj;
	}
	
	
	/**
	 * This method gets called when the user selects a menu item from the Actions popup menu.
	 */
	public void handleAction( final TeamingAction action, final Object actionData )
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			/**
			 * 
			 */
			public void execute()
			{
				switch ( action )
				{
				case REPLY:
					if ( actionData instanceof ActivityStreamUIEntry )
					{
						// Tell the entry to display the reply ui.
						((ActivityStreamUIEntry) actionData ).invokeReplyUI();
					}
					break;
					
				case SHARE:
					if ( actionData instanceof ActivityStreamUIEntry )
					{
						// Invoke the Share This dialog
						invokeShareThisDlg( (ActivityStreamUIEntry) actionData );
					}
					break;
				
				case MARK_ENTRY_READ:
					if ( actionData instanceof ActivityStreamUIEntry )
					{
						boolean hide;
						
						// If we are displaying "show unread" we need to hide this entry.
						hide = false;
						if ( m_showSetting == ShowSetting.SHOW_UNREAD )
							hide = true;
						
						// Mark the given entry as read.
						((ActivityStreamUIEntry) actionData ).markEntryAsRead( hide );
					}
					break;
					
				case MARK_ENTRY_UNREAD:
					if ( actionData instanceof ActivityStreamUIEntry )
					{
						// Mark the given entry as unread.
						((ActivityStreamUIEntry) actionData ).markEntryAsUnread();
					}
					break;
					
				case TAG:
					if ( actionData instanceof ActivityStreamUIEntry )
					{
						// Invoke the Tag This dialog.
						invokeTagThisDlg( (ActivityStreamUIEntry) actionData );
					}
					break;
					
				case SUBSCRIBE:
					if ( actionData instanceof ActivityStreamUIEntry )
					{
						// Invoke the Subscribe to Entry dialog.
						invokeSubscribeToEntryDlg( (ActivityStreamUIEntry) actionData );
					}
					break;
					
				case SHOW_ALL_ENTRIES:
					handleNewShowSetting( ShowSetting.SHOW_ALL );
					break;
					
				case SHOW_UNREAD_ENTRIES:
					handleNewShowSetting( ShowSetting.SHOW_UNREAD );
					break;
				}
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	
	/**
	 * Take all the actions necessary to handle the changing of the show setting.
	 * 
	 */
	private void handleNewShowSetting( ShowSetting showSetting )
	{
		m_showSetting = showSetting;
		
		// Update the label that displays what the show setting is.
		updateShowSettingLabel();

		// Do a search based on the new show setting.
		refreshActivityStream();

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
	 * Invoke the Subscribe to Entry dialog for the given entry.
	 */
	private void invokeSubscribeToEntryDlg( final ActivityStreamUIEntry entry )
	{
		PopupPanel.PositionCallback posCallback;
		
		if ( m_subscribeToEntryDlg == null )
		{
			m_subscribeToEntryDlg = new SubscribeToEntryDlg( false, true, 0, 0 );
		}
		
		m_subscribeToEntryDlg.init( entry.getEntryId(), entry.getEntryTitle() );

		posCallback = new PopupPanel.PositionCallback()
		{
			/**
			 * 
			 */
			public void setPosition( int offsetWidth, int offsetHeight )
			{
				int x;
				int y;
				
				x = Window.getClientWidth() - offsetWidth - 75;
				y = entry.getAbsoluteTop();
				
				m_subscribeToEntryDlg.setPopupPosition( x, y );
			}// end setPosition()
		};
		m_subscribeToEntryDlg.setPopupPositionAndShow( posCallback );
	}
	
	
	/**
	 * Invoke the "Share This" dialog for the given entry.
	 */
	private void invokeShareThisDlg( final ActivityStreamUIEntry entry )
	{
		if ( m_shareThisDlg == null )
		{
			m_shareThisDlg = new ShareThisDlg( false, true, 0, 0, GwtTeaming.getMessages().shareCaption() );
		}
		
		m_shareThisDlg.showDlg( entry.getEntryTitle(), entry.getEntryId(), Window.getClientWidth() - 75, entry.getAbsoluteTop() );
	}
	
	
	/**
	 * Invoke the Tag This dialog for the given entry.
	 */
	private void invokeTagThisDlg( final ActivityStreamUIEntry entry )
	{
		if ( m_tagThisDlg == null )
		{
			//!!! Pass in an ActionTrigger
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
	}// end invokeTagThisDlg()
	
	private void invokeTagThisDlgImpl( final ActivityStreamUIEntry entry )
	{
		TagThisDlg.initAndShow(
			m_tagThisDlg,
			entry.getEntryId(),
			entry.getEntryTitle(),
			(Window.getClientWidth() - 75),
			entry.getAbsoluteTop());
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
	 * This method gets called when the user clicks on the "previous" or "next" image in the search results window.
	 */
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
			id = DOM.getElementAttribute( img.getElement(), "id" );
			
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
		m_pauseImg.setVisible( false );
		
		// Show the resume button.
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
		int headerHeight;
		int resultsHeight;

		// Figure out how tall to make the search results panel.
		headerHeight = m_headerPanel.getOffsetHeight();
		footerHeight = m_footerPanel.getOffsetHeight();
		
		// Set the width and height of the panel that holds the results.  We subtract 10 from
		// the width to leave space for a vertical scrollbar.
		resultsHeight = m_height - headerHeight - footerHeight;
		m_searchResultsPanel.setHeight( String.valueOf( resultsHeight ) + "px" );
		m_searchResultsPanel.setWidth( String.valueOf( m_width - 10 ) + "px" );
		
		m_headerPanel.setWidth( String.valueOf( m_width ) + "px" );
		m_footerPanel.setWidth( String.valueOf( m_width-6 ) + "px" );
	}// end relayoutPageNow()

	
	/**
	 * Resume the refreshing of the activity stream.
	 */
	public void resumeActivityStream()
	{
		// Hide the resume button.
		m_resumeImg.setVisible( false );
		
		// Show the pause button.
		m_pauseImg.setVisible( true );

		startCheckForChangesTimer();
	}
	
	
	/**
	 * Issue an ajax request to save the current show setting to the user's properties.
	 */
	private void saveShowSetting()
	{
		AsyncCallback<Boolean> callback;
		
		callback = new AsyncCallback<Boolean>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure( t, GwtTeaming.getMessages().rpcFailure_SaveWhatsNewShowSetting() );
			}
			
			/**
			 * 
			 */
			public void onSuccess( Boolean success )
			{
				// Nothing to do.
			}
		};
		
		// Issue an ajax request to get the permalink of the source of the activity stream.
		m_rpcService.saveWhatsNewShowSetting( HttpRequestInfo.createHttpRequestInfo(), m_showSetting, callback );
	}
	
	
	/**
	 * Set the activity stream this control is dealing with.
	 */
	public void setActivityStream( ActivityStreamInfo activityStreamInfo )
	{
		ActivityStream src;
		
		// No, issue an rpc request to get the ActivityStreamParams.
		HttpRequestInfo ri;
		
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
			m_getActivityStreamParamsCallback = new AsyncCallback<ActivityStreamParams>()
			{
				/**
				 * 
				 */
				public void onFailure(Throwable t)
				{
					GwtClientHelper.handleGwtRPCFailure( t, GwtTeaming.getMessages().rpcFailure_GetActivityStreamParams() );
				}

				
				/**
				 * 
				 */
				public void onSuccess( ActivityStreamParams activityStreamParams )
				{
					Scheduler.ScheduledCommand cmd;
					
					m_activityStreamParams = activityStreamParams;
					m_showSetting = m_activityStreamParams.getShowSetting();
					
					// Check the appropriate menu item to reflect the show setting.
					m_showSettingPopupMenu.updateMenu( m_showSetting );
					
					cmd = new Scheduler.ScheduledCommand()
					{
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
		ri = HttpRequestInfo.createHttpRequestInfo();
		m_rpcService.getActivityStreamParams( ri, m_getActivityStreamParamsCallback );
		
		// Is the source of the activity stream a binder or person?
		m_asSourcePermalink = null;
		if ( isActivityStreamSourceABinder() || isActivityStreamSourceAPerson() )
		{
			final String asSourceId;
			
			// Yes, get the id of the activity stream's source.
			asSourceId = getActivityStreamSourceBinderId();
			if ( asSourceId != null )
			{
				AsyncCallback<String> callback;
				
				callback = new AsyncCallback<String>()
				{
					/**
					 * 
					 */
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
					public void onSuccess( String permalink )
					{
						m_asSourcePermalink = permalink;
					}
				};
				
				// Issue an ajax request to get the permalink of the source of the activity stream.
				ri = HttpRequestInfo.createHttpRequestInfo();
				if ( isActivityStreamSourceAPerson() )
				     m_rpcService.getUserPermalink(   ri, asSourceId, callback );
				else m_rpcService.getBinderPermalink( ri, asSourceId, callback );
			}
		}
	}
	
	
	/**
	 * Set the size of this control.
	 */
	public void setSize( int width, int height )
	{
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

		srcName = m_activityStreamInfo.getTitle();
		m_sourceName.setText( srcName );
	}


	/**
	 * 
	 */
	public void show()
	{
		Scheduler.ScheduledCommand cmd;

		setVisible( true );

		// Restart the "check for changes" timer.
		startCheckForChangesTimer();
		
		cmd = new Scheduler.ScheduledCommand()
		{
			public void execute()
			{
				relayoutPage();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
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
			public void execute()
			{
				int width;
				int x;

				// Center the message
				width = getWidget().getOffsetWidth();
				x = (width - m_msgPanel.getOffsetWidth()) / 2;
				x -= 40;
				DOM.setStyleAttribute( m_msgPanel.getElement(), "left", Integer.toString( x ) + "px" );
			
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
		DOM.setStyleAttribute( m_searchingPanel.getElement(), "left", Integer.toString( x ) + "px" );
		
		// Show the "searching..." text
		m_searchingPanel.setVisible( true );
	}

	
	/**
	 * 
	 */
	private void startCheckForChangesTimer()
	{
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
				m_pauseImg.setVisible( true );
			}
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
		
		m_pauseImg.setTitle( title );
	}
	
	
	/**
	 * Update the label that display the show setting (show all or show unread)
	 */
	private void updateShowSettingLabel()
	{
		String text;
		
		if ( m_showSetting == ShowSetting.SHOW_ALL )
			text = GwtTeaming.getMessages().showAllEntries();
		else if ( m_showSetting == ShowSetting.SHOW_UNREAD )
			text = GwtTeaming.getMessages().showUnreadEntries();
		else
			text = "Unknown show setting";
		
		m_showSettingLabel.setText( text );
	}

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
	 * @param mainPage
	 * @param asCtrlClient
	 */
	public static void createAsync( final GwtMainPage mainPage, final ActivityStreamCtrlClient asCtrlClient )
	{
		GWT.runAsync( ActivityStreamCtrl.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				ActivityStreamCtrl asCtrl = new ActivityStreamCtrl( mainPage );
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
}// end ActivityStreamCtrl
