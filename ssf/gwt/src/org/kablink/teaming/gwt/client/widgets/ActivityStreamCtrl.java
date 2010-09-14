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

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
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
	implements ClickHandler, ActionHandler
{
	private InlineLabel m_sourceName;
	private FlowPanel m_headerPanel;
	private FlowPanel m_searchResultsPanel;
	private FlowPanel m_footerPanel;
	private Object m_selectedObj = null;
	private ActionHandler m_actionHandler;
	private AsyncCallback<GwtSearchResults> m_searchResultsCallback;
	private Timer m_searchTimer = null;
	private GwtSearchCriteria m_searchCriteria;
	private boolean m_searchInProgress = false;
	private Image m_prevDisabledImg;
	private Image m_prevImg;
	private Image m_nextDisabledImg;
	private Image m_nextImg;
	private InlineLabel m_nOfnLabel;
	private FlowPanel m_searchingPanel;
	private int m_searchCountTotal = 0;	// Total number of items found by a search.
	private int m_displayCount = 0;		// Total number of items currently being displayed from a search.
	private int m_width;
	private int m_height;
	private ActivityStreamInfo m_activityStreamInfo = null;
	private GwtRpcServiceAsync m_rpcService = null;
	private String m_binderPermalink = null;		// Permalink to the binder that is the source of the activity stream.

	
	/**
	 * 
	 */
	public ActivityStreamCtrl(
		ActionHandler actionHandler,  // We will call this handler when the user selects an item from the search results.
		GwtSearchCriteria.SearchType searchType )
	{
		FlowPanel mainPanel;

		// Get the rpc service.
		m_rpcService = GwtTeaming.getRpcService();
		
		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "activityStreamCtrl" );
		
		// Remember the handler we should call when the user selects an item from the search results.
		m_actionHandler = actionHandler;
		
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
		m_searchResultsCallback = new AsyncCallback<GwtSearchResults>()
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
			public void onSuccess( GwtSearchResults gwtSearchResults )
			{
				hideSearchingText();

				if ( gwtSearchResults != null )
				{
					// Add the search results to the search results widget.
					addSearchResults( m_searchCriteria, gwtSearchResults );
				}
				
				m_searchInProgress = false;
			}// end onSuccess()
		};
		m_searchInProgress = false;
		
		m_searchCriteria = new GwtSearchCriteria();
		m_searchCriteria.setSearchType( searchType );
		m_searchCriteria.setMaxResults( 10 );
		m_searchCriteria.setPageNumber( 0 );

		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}
	
	
	/**
	 * Add the given search results to the list of search results.
	 */
	private void addSearchResults( GwtSearchCriteria searchCriteria, GwtSearchResults searchResults )
	{
		ArrayList<GwtTeamingItem> results;
		int position;
		int value1;
		String nOfn;

		// Clear any results we may be currently displaying.
		clearCurrentSearchResults();
		
		m_displayCount = 0;
		m_searchCountTotal = searchResults.getCountTotal();
		results = searchResults.getResults();
		if ( results != null )
		{
			//!!! Finish
		/*
			int i;
			SearchResultItemWidget widget;
			GwtTeamingItem item;

			m_displayCount = results.size();
			for (i = 0; i < m_displayCount; ++i)
			{
				item = results.get( i );
				widget = new SearchResultItemWidget( item );
				widget.addClickHandler( this );
				
				m_contentPanel.add( widget );
			}// end for()
		*/
		}

		// Figure out the position of the last result within the total number of results.
		position = (searchCriteria.getPageNumber() * searchCriteria.getMaxResults()) + m_displayCount;
		
		// Construct the string n - n of n based on the number of items found in the search.
		value1 = (searchCriteria.getPageNumber() * searchCriteria.getMaxResults()) + 1;
		if ( m_searchCountTotal == 0 )
			value1 = 0;
		nOfn = GwtTeaming.getMessages().nOfn( value1, position, m_searchCountTotal );
		m_nOfnLabel.setText( nOfn );

		// Hide the previous and next images
		m_prevImg.setVisible( false );
		m_nextImg.setVisible( false );
		
		// Do we need to show the "prev" image?
		if ( position > searchCriteria.getMaxResults() )
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
		if ( m_searchCountTotal > position )
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
	 * Remove any search results we may be displaying. 
	 */
	private void clearCurrentSearchResults()
	{
		m_searchResultsPanel.clear();
		m_searchCountTotal = 0;
		m_displayCount = 0;
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
		
		// Add a refresh button to the header.
		imageResource = GwtTeaming.getImageBundle().refresh();
		img = new Image( imageResource );
		img.addStyleName( "activityStreamCtrlHeaderRefresh" );
		img.setTitle( GwtTeaming.getMessages().refresh() );
		m_headerPanel.add( img );
		
		clickHandler = new ClickHandler()
		{
			public void onClick( ClickEvent clickEvent )
			{
				// Refresh the results of the search query.
				refresh();
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
		GwtRpcServiceAsync rpcService;

		// Clear any results we may be currently displaying.
		clearCurrentSearchResults();

		// Issue an ajax request to search for the specified type of object.
		m_searchInProgress = true;
		rpcService = GwtTeaming.getRpcService();
		rpcService.executeSearch( new HttpRequestInfo(), m_searchCriteria, m_searchResultsCallback );

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
	 * This method gets called when the user clicks on an item from a search result.
	 */
	public void handleAction( TeamingAction ta, Object obj )
	{
		if ( TeamingAction.SELECTION_CHANGED == ta )
		{
			// Make sure we were handed a GwtTeamingItem.
			if ( obj instanceof GwtTeamingItem )
			{
				GwtTeamingItem selectedItem;
				
				selectedItem = (GwtTeamingItem) obj;
				
				// If we were passed an ActionHandler, call it.
				if ( m_actionHandler != null )
					m_actionHandler.handleAction( TeamingAction.SELECTION_CHANGED, selectedItem );
			}
		}
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
				// Did the user click on next?
				if ( id.equalsIgnoreCase( "viewNextPageOfResults" ) )
				{
					// Yes, increment the page number and do another search.
					m_searchCriteria.incrementPageNumber();
					executeSearch();
				}
				// Did the user click on prev?
				else if ( id.equalsIgnoreCase( "viewPreviousPageOfResults" ) )
				{
					// Yes, decrement the page number and do another search.
					m_searchCriteria.decrementPageNumber();
					executeSearch();
				}
			}
		}
	}

	
	/**
	 * Refresh the results of the search query. 
	 */
	private void refresh()
	{
		Window.alert( "Refresh is not implemented yet." );
		return;
		
		//!!! m_searchCriteria.setPageNumber( 0 );
		//!!! executeSearch();
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
	 * Set the activity stream this control is dealing with.
	 */
	public void setActivityStream( ActivityStreamInfo activityStreamInfo )
	{
		m_activityStreamInfo = activityStreamInfo;
		
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
		
		// Change our title to reflect the new activity stream source.
		setTitle();
		
		{
			ActivityStreamTopEntry topEntry;
			
			topEntry = new ActivityStreamTopEntry( m_actionHandler );
			topEntry.setTitle( "This is the title" );
			topEntry.setBinderName( "Workgroup Business Unit" );
			topEntry.setAuthor( "Dennis Foster" );
			topEntry.setDate( "August 30, 2010" );
			topEntry.setDesc( "Now is the time for all good men to come to the aid of their country.  The quick brown fox jumped over something.  Rhoncus in neque neque tortor quia elit, amet neque quis primis sapien, mauris vestibulum adipiscing varius quis suscipit ligula. Hendrerit vitae, donec sem auctor porta habitasse commodo etiam, dolor aliquet in. Mauris ornare neque dictum erat vestibulum volutpat, mi vel nam, ultricies rhoncus, auctor sapien mauris" );
			topEntry.setAvatarUrl( GwtMainPage.m_requestInfo.getImagesPath() + "pics/media.gif" );
			m_searchResultsPanel.add( topEntry );

			topEntry = new ActivityStreamTopEntry( m_actionHandler );
			topEntry.setTitle( "What should the title be for this" );
			topEntry.setBinderName( "Durango Beta" );
			topEntry.setAuthor( "Tracy Smith" );
			topEntry.setDate( "September 3, 2010" );
			topEntry.setDesc( "The quick brown fox jumped over something.  Rhoncus in neque neque tortor quia elit, amet neque quis primis sapien, mauris vestibulum adipiscing varius quis suscipit ligula. Hendrerit vitae, donec sem auctor porta habitasse commodo etiam, dolor aliquet in. Mauris ornare neque dictum erat vestibulum volutpat, mi vel nam, ultricies rhoncus, auctor sapien mauris" );
			topEntry.setAvatarUrl( GwtMainPage.m_requestInfo.getImagesPath() + "pics/tracking20x16.png" );
			m_searchResultsPanel.add( topEntry );
}
	}
	
	
	/**
	 * Set the search criteria for whether or not we are searching only for folders.
	 */
	public void setSearchForFoldersOnly( boolean value )
	{
		m_searchCriteria.setFoldersOnly( value );
	}

	
	/**
	 * Sets the search type of the search being done.
	 * 
	 * @param searchType
	 */
	public void setSearchType( GwtSearchCriteria.SearchType searchType )
	{
		m_searchCriteria.setSearchType( searchType );
	}
	
	
	/**
	 * Set the query we will use for the search.
	 */
	public void setSearchQuery( String query )
	{
		//!!! Finish
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
