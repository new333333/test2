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

import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;


/**
 * This widget will allow the user to type into a text field and will use what is typed to search Teaming for
 * the requested object type, entry, folder, etc.
 * @author jwootton
 *
 */
public class FindCtrl extends Composite
	implements ClickHandler, Event.NativePreviewHandler, KeyUpHandler, OnSelectHandler
{
	/**
	 * This widget is used to hold an item from a search result.
	 */
	public class SearchResultItemWidget extends Composite
		implements HasClickHandlers
	{
		private GwtTeamingItem m_item;
		
		/**
		 * 
		 */
		public SearchResultItemWidget( GwtTeamingItem item )
		{
			FlowPanel panel;
			Anchor anchor;
			InlineLabel secondaryText;
			
			m_item = item;
			
			panel = new FlowPanel();
			panel.addStyleName( "findSearchResultItemWidget" );
			
			// Add the name of the item as an anchor.
			anchor = new Anchor( item.getShortDisplayName() );
			anchor.setWordWrap( false );
			anchor.addStyleName( "noTextDecoration" );
			anchor.addStyleName( "bold" );
			panel.add( anchor );
			
			// Add any additional information about this item.
			secondaryText = new InlineLabel( item.getSecondaryDisplayText() );
			secondaryText.addStyleName( "fontSize75em" );
			secondaryText.setWordWrap( false );
			panel.add( secondaryText );
			
			// All composites must call initWidget() in their constructors.
			initWidget( panel );
		}// end SearchResultItemWidget()
		
		
		/**
		 * 
		 */
		public HandlerRegistration addClickHandler( ClickHandler handler )
		{
			return addDomHandler( handler, ClickEvent.getType() );
		}// end addClickHandler()
		
		
		/**
		 * Return the GwtTeamingItem that is associated with this object.
		 */
		public GwtTeamingItem getTeamingItem()
		{
			return m_item;
		}// end getTeamingItem()
	}// end SearchResultItemWidget
	
	
	/**
	 * This widget is used to hold search results.
	 */
	public class SearchResultsWidget extends Composite
		implements ClickHandler
	{
		private OnSelectHandler m_onSelectHandler;
		private FlowPanel m_mainPanel;
		private FlowPanel m_contentPanel;
		private Image m_prevDisabledImg;
		private Image m_prevImg;
		private Image m_nextDisabledImg;
		private Image m_nextImg;
		private InlineLabel m_nOfnLabel;
		private FlowPanel m_searchingPanel;
		private int m_searchCountTotal = 0;	// Total number of items found by a search.
		private int m_displayCount = 0;		// Total number of items currently being displayed from a search.
		
		
		/**
		 * 
		 */
		public SearchResultsWidget( OnSelectHandler onSelectHandler )
		{
			FlowPanel footer;
			InlineLabel searching;
			Image spinnerImg;

			// Remember the OnSelectHandler we need to call when the user selects an item from the list of search results.
			m_onSelectHandler = onSelectHandler;
			
			m_mainPanel = new FlowPanel();
			m_mainPanel.addStyleName( "findSearchResults" );
			
			// Create a panel to hold "Searching..."
			m_searchingPanel = new FlowPanel();
			m_searchingPanel.addStyleName( "findSearchingPanel" );
			m_mainPanel.add( m_searchingPanel );
			searching = new InlineLabel( GwtTeaming.getMessages().searching() );
			m_searchingPanel.add( searching );
			spinnerImg = GwtTeaming.getImageBundle().spinner16().createImage();
			m_searchingPanel.add( spinnerImg );
			m_searchingPanel.setVisible( false );
			
			// Create the panel that will hold the content.
			m_contentPanel = createContentPanel();
			m_mainPanel.add( m_contentPanel );
			
			// Create the footer
			footer = createFooter();
			m_mainPanel.add( footer );

			// All composites must call initWidget() in their constructors.
			initWidget( m_mainPanel );
		}// end SearchResultsWidget()


		/**
		 * Add a click handler that will get called when the user clicks on the "next" image.
		 */
		public HandlerRegistration addClickHandlerOnNextImg( ClickHandler clickHandler )
		{
			return m_nextImg.addClickHandler( clickHandler );
		}// end addClickHandlerOnNextImg()
		
		
		/**
		 * Add a click handler that will get called when the user clicks on the "previous" image.
		 */
		public HandlerRegistration addClickHandlerOnPrevImg( ClickHandler clickHandler )
		{
			return m_prevImg.addClickHandler( clickHandler );
		}// end addClickHandlerOnPrevImg()
		
		
		/**
		 * Add the given search results to the list of search results.
		 */
		public void addSearchResults( GwtSearchCriteria searchCriteria, GwtSearchResults searchResults )
		{
			ArrayList<GwtTeamingItem> results;
			int position;
			int value1;
			String nOfn;

			// Clear any results we may be currently displaying.
			clearCurrentContent();
			
			m_displayCount = 0;
			m_searchCountTotal = searchResults.getCountTotal();
			results = searchResults.getResults();
			if ( results != null )
			{
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
		}// end addSearchResults()
		
		
		/**
		 * Remove any search results we may be displaying. 
		 */
		public void clearCurrentContent()
		{
			m_contentPanel.clear();
			m_searchCountTotal = 0;
			m_displayCount = 0;
			m_prevImg.setVisible( false );
			m_nextImg.setVisible( false );
			m_prevDisabledImg.setVisible( true );
			m_nextDisabledImg.setVisible( true );
		}// end clearCurrentContent()
		
		
		/**
		 * Create the panel where the search results will live.
		 */
		public FlowPanel createContentPanel()
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			panel.addStyleName( "findSearchResultsContent" );
			
			return panel;
		}// end createContentPanel()
		
		
		/*
		 * Create the footer panel for the search results.
		 */
		public FlowPanel createFooter()
		{
			FlowPanel panel;
			FlexTable table;
			FlowPanel imgPanel;
			AbstractImagePrototype abstractImg;
			
			panel = new FlowPanel();
			panel.addStyleName( "findSearchResultsFooter" );

			table = new FlexTable();
			table.addStyleName( "findSearchResultsFooterImages" );
			panel.add( table );
			imgPanel = new FlowPanel();
			table.setWidget( 0, 0, imgPanel );
			
			// Add the previous images to the footer.
			abstractImg = GwtTeaming.getImageBundle().previousDisabled16();
			m_prevDisabledImg = abstractImg.createImage();
			imgPanel.add( m_prevDisabledImg );
			abstractImg = GwtTeaming.getImageBundle().previous16();
			m_prevImg = abstractImg.createImage();
			m_prevImg.addStyleName( "cursorPointer" );
			DOM.setElementAttribute( m_prevImg.getElement(), "id", "viewPreviousPageOfResults" );
			imgPanel.add( m_prevImg );
			m_prevImg.setVisible( false );
			
			// Add a label that we'll use to display 4-10 of 128
			m_nOfnLabel = new InlineLabel();
			m_nOfnLabel.addStyleName( "marginLeftPoint25em" );
			m_nOfnLabel.addStyleName( "marginRightPoint25em" );
			m_nOfnLabel.addStyleName( "marginBottomPoint25em" );
			imgPanel.add( m_nOfnLabel );

			// Add the next images to the footer.
			abstractImg = GwtTeaming.getImageBundle().nextDisabled16();
			m_nextDisabledImg = abstractImg.createImage();
			imgPanel.add( m_nextDisabledImg );
			abstractImg = GwtTeaming.getImageBundle().next16();
			m_nextImg = abstractImg.createImage();
			m_nextImg.addStyleName( "cursorPointer" );
			DOM.setElementAttribute( m_nextImg.getElement(), "id", "viewNextPageOfResults" );
			imgPanel.add( m_nextImg );
			m_nextImg.setVisible( false );

			return panel;
		}// end createFooter()
		
		
		/**
		 * Hide the "Searching..." text.
		 */
		public void hideSearchingText()
		{
			m_searchingPanel.setVisible( false );
		}// end hideSearchingText()
		
		
		/**
		 * This method gets called when the user clicks on an item from the list of search results.
		 */
		public void onClick( ClickEvent clickEvent )
		{
			// If we have an OnSelectHandler, call it.
			if ( m_onSelectHandler != null )
			{
				GwtTeamingItem selectedItem;
				
				// Get the item selected by the user.
				if ( clickEvent.getSource() instanceof SearchResultItemWidget )
				{
					SearchResultItemWidget tmp;
					
					tmp = (SearchResultItemWidget) clickEvent.getSource();
					selectedItem = tmp.getTeamingItem();
					
					m_onSelectHandler.onSelect( selectedItem );
				}
			}
		}// end onClick()
		
		/**
		 * Set the width of this widget.
		 */
		public void setWidthInt( int width )
		{
			m_mainPanel.setWidth( width + "px" );
			m_contentPanel.setWidth( width + "px" );
		}// end setWidthInt()
		
		
		/**
		 * Show the "Searching..." text.
		 */
		public void showSearchingText()
		{
			int width;
			int x;
			
			// Center the "searching..." text
			width = m_mainPanel.getOffsetWidth();
			x = (width - m_searchingPanel.getOffsetWidth()) / 2;
			x -= 40;
			DOM.setStyleAttribute( m_searchingPanel.getElement(), "left", Integer.toString( x ) + "px" );
			
			// Show the "searching..." text
			m_searchingPanel.setVisible( true );
		}// end showSearchingText()
	}// end SearchResultsWidget
	
	
	
	
	private TextBox m_txtBox;
	private SearchResultsWidget m_searchResultsWidget;
	private Object m_selectedObj = null;
	private OnSelectHandler m_onSelectHandler;
	private AsyncCallback<GwtSearchResults> m_searchResultsCallback;
	private Timer m_timer = null;
	private Timer m_searchTimer = null;
	private GwtSearchCriteria m_searchCriteria;
	private boolean m_searchInProgress = false;
	private String m_prevSearchString = null;
	
	/**
	 * 
	 */
	public FindCtrl(
		OnSelectHandler onSelectHandler,  // We will call this handler when the user selects an item from the search results.
		GwtSearchCriteria.SearchType searchType )
	{
		FlowPanel mainPanel;

		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "gwtFindCtrl" );

		// Create a text box for the user to type in.
		m_txtBox = new TextBox();
		m_txtBox.setVisibleLength( 40 );
		m_txtBox.addKeyUpHandler( this );
		mainPanel.add( m_txtBox );
		
		// Create a widget where the search results will live.
		m_searchResultsWidget = new SearchResultsWidget( this );
		hideSearchResults();
		mainPanel.add( m_searchResultsWidget );
		
		// Add handlers that will be called when the user clicks on the "previous" or "next" images.
		m_searchResultsWidget.addClickHandlerOnPrevImg( this );
		m_searchResultsWidget.addClickHandlerOnNextImg( this );

		// Register a preview-event handler.  We do this so we can see the mouse-down event
		// and close the search results.
		Event.addNativePreviewHandler( this );
		
		// Remember the handler we should call when the user selects an item from the search results.
		m_onSelectHandler = onSelectHandler;
		
		// Create the callback that will be used when we issue an ajax call to do a search.
		m_searchResultsCallback = new AsyncCallback<GwtSearchResults>()
		{
			/**
			 * 
			 */
			public void onFailure(Throwable t)
			{
				//!!! Do something here.
				Window.alert( "The request to execute a search failed." );
				m_searchInProgress = false;
				m_searchResultsWidget.hideSearchingText();
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( GwtSearchResults gwtSearchResults )
			{
				m_searchResultsWidget.hideSearchingText();

				if ( gwtSearchResults != null )
				{
					// Add the search results to the search results widget.
					m_searchResultsWidget.addSearchResults( m_searchCriteria, gwtSearchResults );
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
	}// end FindCtrl()
	
	
	/**
	 * 
	 */
	public void executeSearch()
	{
		GwtRpcServiceAsync rpcService;

		// Clear any results we may be currently displaying.
		m_searchResultsWidget.clearCurrentContent();

		// Issue an ajax request to search for the specified type of object.
		m_searchInProgress = true;
		rpcService = GwtTeaming.getRpcService();
		rpcService.executeSearch( m_searchCriteria, m_searchResultsCallback );

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
						m_searchResultsWidget.showSearchingText();
				}// end run()
			};
		}
		
		m_searchTimer.schedule( 500 );
	}// end executeSearch()
	
	
	/**
	 * Return the widget that should be given the focus.
	 */
	public FocusWidget getFocusWidget()
	{
		return m_txtBox;
	}// end getFocusWidget()
	
	
	/**
	 * Return the selected object.  The calling method will need to typecast the return value.
	 */
	public Object getSelectedObject()
	{
		return m_selectedObj;
	}// end getSelectedObject()
	
	
	/**
	 * Hide the search results.
	 */
	public void hideSearchResults()
	{
		m_searchResultsWidget.setVisible( false );
	}// end hideSearchResults()
	
	
	/**
	 * Determine if the given coordinates are over this control.
	 */
	public boolean isMouseOver( int mouseX, int mouseY )
	{
		int left;
		int top;
		int width;
		int height;
		
		// Get the position and dimensions of this control.
		left = getAbsoluteLeft();
		top = getAbsoluteTop();
		height = getOffsetHeight();
		width = getOffsetWidth();
		
		// Factor scrolling into the mouse position.
		mouseY += Window.getScrollTop();
		mouseX += Window.getScrollLeft();
		
		// Is the mouse over this control?
		if ( mouseY >= top && mouseY <= (top + height) && mouseX >= left && (mouseX <= left + width) )
			return true;
		
		return false;
	}// end isMouseOver()

	
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
	}// end onClick()

	
	/**
	 * Handles the KeyUpEvent
	 */
	public void onKeyUp( KeyUpEvent event )
	{
		String tmp;
		
		// Get the search criteria the user entered.
		tmp = m_txtBox.getText();
		
		// Did the search string change?
		if ( m_prevSearchString == null || tmp == null || !m_prevSearchString.equalsIgnoreCase( tmp ) )
		{
			// Yes
			m_prevSearchString = tmp;
			
			// Show the search-results widget.
			showSearchResults();
	
			if ( tmp == null )
				tmp = "";
			
			if ( tmp.length() > 0 )
			{
				// Append the wildcard character '*'.
				tmp += "*";
			}
			
			// Issue an ajax request to do a search based on the text entered by the user.
			m_searchCriteria.setPageNumber( 0 );
			m_searchCriteria.setSearchText( tmp );
			executeSearch();
		}
	}// end onKeyUp()
	
	
	/**
	 * 
	 */
	public void onPreviewNativeEvent( Event.NativePreviewEvent previewEvent )
	{
		int eventType;
		NativeEvent nativeEvent;

		eventType = previewEvent.getTypeInt();
		
		// We are only interested in mouse-down events.
		if ( eventType != Event.ONMOUSEDOWN )
			return;
		
		nativeEvent = previewEvent.getNativeEvent();

		// If the user clicked outside of this control, hide the search results.
		if ( !isMouseOver( nativeEvent.getClientX(), nativeEvent.getClientY() ) )
		{
			// We can't hide the search results right away because if the user clicked on a
			// button and then we change the size of the panel that holds us the button
			// won't know it was clicked on.
			// Have we already created a timer?
			if ( m_timer == null )
			{
				m_timer = new Timer()
				{
					/**
					 * 
					 */
					@Override
					public void run()
					{
						hideSearchResults();
					}// end run()
				};
			}
			
			m_timer.schedule( 100 );
		}
	}// end onPreviewNativeEvent()
	
	
	/**
	 * This method gets called when the user clicks on an item from a search result.
	 */
	public void onSelect( Object obj )
	{
		// Make sure we were handed a GwtTeamingItem.
		if ( obj instanceof GwtTeamingItem )
		{
			String name;
			GwtTeamingItem selectedItem;
			
			selectedItem = (GwtTeamingItem) obj;
			
			// Get the name of the selected item.
			name = selectedItem.getShortDisplayName();
			
			// Put the name of the selected item in the text box.
			m_txtBox.setText( name );
			
			// If we were passed an OnSelectHandler, call it.
			if ( m_onSelectHandler != null )
				m_onSelectHandler.onSelect( selectedItem );
		}
	}// end onSelect()
	
	
	/**
	 * 
	 */
	public void setInitialSearchString( String searchString )
	{
		if ( searchString == null )
			searchString = "";
		
		m_txtBox.setText( searchString );
		m_prevSearchString = searchString;
	}// end setInitialSearchString()
	
	
	/**
	 * Set the search criteria for whether or not we are searching only for folders.
	 */
	public void setSearchForFoldersOnly( boolean value )
	{
		m_searchCriteria.setFoldersOnly( value );
	}// end setSearchForFoldersOnly()
	
	
	/**
	 * Show the search results. 
	 */
	public void showSearchResults()
	{
		// Make the search results widget as wide as the text box.  We subtract 4 because of the border around the search results widget.
		m_searchResultsWidget.setWidthInt( m_txtBox.getOffsetWidth() - 4 );
		m_searchResultsWidget.setVisible( true );
	}// end showSearchResults()		
}// end FindCtrl
