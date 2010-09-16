/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActivityStreamData;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamParams;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.PagingData;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;


/**
 * This widget will display a list of entries that are the results from a search query.
 * @author jwootton
 *
 */
public class ActivityStreamCtrl extends Composite
	implements ClickHandler
{
	private InlineLabel m_sourceName;
	private FlowPanel m_headerPanel;
	private FlowPanel m_searchResultsPanel;
	private FlowPanel m_footerPanel;
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
	private Image m_pauseImg;
	private Image m_resumeImg;
	private Image m_prevDisabledImg;
	private Image m_prevImg;
	private Image m_nextDisabledImg;
	private Image m_nextImg;
	private InlineLabel m_nOfnLabel;
	private FlowPanel m_searchingPanel;
	private int m_width;
	private int m_height;
	private ActivityStreamInfo m_activityStreamInfo = null;
	private GwtRpcServiceAsync m_rpcService = null;
	private String m_binderPermalink = null;		// Permalink to the binder that is the source of the activity stream.
	// This is a list of ui widgets, one for each entry returned by the search.
	// We will reuse these ui widgets every time we get a new page of results.
	// We will NOT create new ui widgets every time we get a new page of results.
	private ArrayList<ActivityStreamTopEntry> m_searchResultsUIWidgets;

	
	/**
	 * 
	 */
	public ActivityStreamCtrl(
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
		
		// Create the callback that will be used when we issue an ajax call to do a search.
		m_searchResultsCallback = new AsyncCallback<ActivityStreamData>()
		{
			/**
			 * 
			 */
			public void onFailure(Throwable t)
			{
				GwtClientHelper.handleGwtRPCFailure(
					GwtTeaming.getMessages().rpcFailure_Search() );
				
				m_searchInProgress = false;
				hideSearchingText();
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( ActivityStreamData activityStreamData )
			{
				hideSearchingText();

				if ( activityStreamData != null )
				{
					// Add the search results to the search results widget.
					addSearchResults( activityStreamData );
				}
				
				m_searchInProgress = false;
			}// end onSuccess()
		};
		m_searchInProgress = false;
		
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
					topEntry = new ActivityStreamTopEntry( m_actionHandler );
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
					GwtClientHelper.handleGwtRPCFailure(
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
						// Refresh the activity stream.
						refreshActivityStream();
					}
				}// end onSuccess()
			};
		}
		
		// Issue an ajax request to see if there is anything new.
		//!!!m_rpcService.???( new HttpRequestInfo(), m_activityStreamParams, m_activityStreamInfo, m_pagingData, m_checkForChangesCallback );
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
		}
		
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
		Image img;
		ClickHandler clickHandler;
		InlineLabel whatsNewLabel;

		m_headerPanel = new FlowPanel();
		m_headerPanel.addStyleName( "activityStreamCtrlHeader" );
		
		// Create a label where the name of the activity stream source will go.
		m_sourceName = new InlineLabel( " " );
		m_sourceName.addStyleName( "activityStreamCtrlHeaderTitle" );
		m_headerPanel.add( m_sourceName );
		
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
					// Is the activity stream source a binder?
					if ( isActivityStreamSourceABinder() )
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
					// Is the activity stream source a binder?
					if ( isActivityStreamSourceABinder() )
					{
						OnSelectBinderInfo binderInfo;
						String binderId;
						
						// Yes
						// Take the user to the selected binder.
						binderId = getActivityStreamSourceBinderId();
						binderInfo = new OnSelectBinderInfo( binderId, m_binderPermalink, false, Instigator.OTHER );
						m_actionHandler.handleAction( TeamingAction.SELECTION_CHANGED, binderInfo );
					}
				}
				
			};
			m_sourceName.addClickHandler( ch );
		}
		
		// Create a label for "What's New"
		whatsNewLabel = new InlineLabel( GwtTeaming.getMessages().whatsNew() );
		whatsNewLabel.addStyleName( "activityStreamCtrlHeaderSubtitle" );
		m_headerPanel.add( whatsNewLabel );
		
		// Add a pause button to the header.
		imageResource = GwtTeaming.getImageBundle().pauseActivityStream();
		m_pauseImg = new Image( imageResource );
		m_pauseImg.addStyleName( "activityStreamCtrlHeaderPausePlay" );
		m_pauseImg.setTitle( GwtTeaming.getMessages().pauseActivityStream() );
		m_pauseImg.setVisible( false );
		m_headerPanel.add( m_pauseImg );
		
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
		m_headerPanel.add( m_resumeImg );
		
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
		imageResource = GwtTeaming.getImageBundle().refresh();
		img = new Image( imageResource );
		img.addStyleName( "activityStreamCtrlHeaderRefresh" );
		img.setTitle( GwtTeaming.getMessages().refresh() );
		m_headerPanel.add( img );
		
		// Add a click handler for the refresh button.
		clickHandler = new ClickHandler()
		{
			public void onClick( ClickEvent clickEvent )
			{
				// Refresh the results of the search query.
				refreshActivityStream();
			}
		};
		img.addClickHandler( clickHandler );
		
		mainPanel.add( m_headerPanel );
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
		if ( m_activityStreamParams == null )
		{
			Window.alert( "In executeSearch(), m_activityStreamParams is null.  This should never happen." );
			return;
		}
		
		// Clear any results we may be currently displaying.
		clearCurrentSearchResults();

		// Issue an ajax request to search for the specified type of object.
		m_searchInProgress = true;
		m_rpcService.getActivityStreamData( new HttpRequestInfo(), m_activityStreamParams, m_activityStreamInfo, m_pagingData, m_searchResultsCallback );

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
		
		m_searchTimer.schedule( 500 );
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
	 * 
	 */
	public void hide()
	{
		setVisible( false );
	}
	
	
	/**
	 * Hide the "Searching..." text.
	 */
	public void hideSearchingText()
	{
		m_searchingPanel.setVisible( false );
	}
	
	
	/**
	 * Is the source of the activity stream a binder?
	 */
	private boolean isActivityStreamSourceABinder()
	{
		switch ( m_activityStreamInfo.getActivityStream() )
		{
		case FOLLOWED_PERSON:
		case FOLLOWED_PLACE:
		case MY_FAVORITE:
		case MY_TEAM:
		case CURRENT_BINDER:
			return true;
			
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

		// Have we created a timer?
		if ( m_checkForChangesTimer != null )
		{
			// Yes, cancel it.
			m_checkForChangesTimer.cancel();
		}
	}
	
	
	/**
	 * Issue a new search. 
	 */
	private void refreshActivityStream()
	{
		m_pagingData = null;
		executeSearch();
	}
	

	/**
	 * 
	 */
	private void relayout()
	{
		int footerHeight;
		int headerHeight;
		int resultsHeight;
		
		// Figure out how tall to make the search rusults panel.
		headerHeight = m_headerPanel.getOffsetHeight();
		footerHeight = m_footerPanel.getOffsetHeight();
		
		resultsHeight = m_height - headerHeight - footerHeight;
		m_searchResultsPanel.setHeight( String.valueOf( resultsHeight ) + "px" );
		m_searchResultsPanel.setWidth( String.valueOf( m_width ) + "px" );
		
		m_footerPanel.setWidth( String.valueOf( m_width ) + "px" );
	}
	
	
	/**
	 * Resume the refreshing of the activity stream.
	 */
	public void resumeActivityStream()
	{
		// Hide the resume button.
		m_resumeImg.setVisible( false );
		
		// Show the pause button.
		m_pauseImg.setVisible( true );

		// Have we created a timer?
		if ( m_checkForChangesTimer != null )
		{
			// Yes
			// Do we have an activity stream parameter object?
			if ( m_activityStreamParams != null )
			{
				int minutes;
				
				// Yes
				minutes = m_activityStreamParams.getClientRefresh();
				if ( minutes > 0 )
				{
					//!!! Should we refresh immediately?
					m_checkForChangesTimer.scheduleRepeating( (minutes * 60 * 1000) );
				}
			}
		}
	}
	
	
	/**
	 * Set the activity stream this control is dealing with.
	 */
	public void setActivityStream( ActivityStreamInfo activityStreamInfo )
	{
		m_activityStreamInfo = activityStreamInfo;
		
		// Change our title to reflect the new activity stream source.
		setTitle();
		
		m_pagingData = null;
		
		// Do we have an ActivityStreamParams object?
		if ( m_activityStreamParams == null )
		{
			// No, issue an rpc request to get the ActivityStreamParams.
			HttpRequestInfo ri;
			
			if ( m_getActivityStreamParamsCallback == null )
			{
				m_getActivityStreamParamsCallback = new AsyncCallback<ActivityStreamParams>()
				{
					/**
					 * 
					 */
					public void onFailure(Throwable t)
					{
						GwtClientHelper.handleGwtRPCFailure( GwtTeaming.getMessages().rpcFailure_GetActivityStreamParams() );
					}

					
					/**
					 * 
					 */
					public void onSuccess( ActivityStreamParams activityStreamParams )
					{
						Command cmd;
						
						m_activityStreamParams = activityStreamParams;
						
						cmd = new Command()
						{
							/**
							 * 
							 */
							public void execute()
							{
								int minutes;
								
								// Now that we have the activity stream parameters, execute the search.
								executeSearch();
								
								// Get the refresh interval in minutes.
								minutes = m_activityStreamParams.getClientRefresh(); 
								if ( minutes > 0 )
								{
									// Have we already created a timer that is used to check for changes?
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

									// Show the pause button.
									m_pauseImg.setVisible( true );
									
									// Start a timer.  When the timer goes off we will check for changes and
									// update the activity stream if there is something new.
									m_checkForChangesTimer.scheduleRepeating( (minutes * 60 * 1000) );
								}
							}
						};
						DeferredCommand.addCommand( cmd );
					}
				};
			}
			
			// Issue an ajax request to get the activity stream params.
			ri = new HttpRequestInfo();
			m_rpcService.getActivityStreamParams( ri, m_getActivityStreamParamsCallback );
		}
		else
		{
			// Yes, execute a search on the given activity stream.
			executeSearch();
		}
		
		// Is the source of the activity stream a binder?
		m_binderPermalink = null;
		if ( isActivityStreamSourceABinder() )
		{
			String binderId;
			
			// Yes, get the id of the binder.
			binderId = getActivityStreamSourceBinderId();
			if ( binderId != null )
			{
				HttpRequestInfo ri;
				AsyncCallback<String> callback;
				
				callback = new AsyncCallback<String>()
				{
					/**
					 * 
					 */
					public void onFailure(Throwable t)
					{
						GwtClientHelper.handleGwtRPCFailure(
							GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
							getActivityStreamSourceBinderId() );
					}
					
					/**
					 * 
					 */
					public void onSuccess( String binderPermalink )
					{
						m_binderPermalink = binderPermalink;
					}
				};
				
				// Issue an ajax request to get the permalink of the binder that is the source of the activity stream.
				ri = new HttpRequestInfo();
				m_rpcService.getBinderPermalink( ri, binderId, callback );
			}
		}
	}
	
	
	/**
	 * 
	 */
	public void setSize( int width, int height )
	{
		m_width = width;
		m_height = height - 30;	// I don't know why we need to subtract 30 but we do.
		
		super.setSize( String.valueOf( m_width ) + "px", String.valueOf( m_height ) + "px" );
		relayout();
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
		Command cmd;

		setVisible( true );
		
		cmd = new Command()
		{
			public void execute()
			{
				relayout();
			}
		};
		DeferredCommand.addCommand( cmd );
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

}// end ActivityStreamCtrl
