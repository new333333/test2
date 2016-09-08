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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtNameCompletionSettings;
import org.kablink.teaming.gwt.client.GwtNameCompletionSettings.GwtDisplayField;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchScope;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.rpc.shared.ExecuteSearchCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNameCompletionSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget will allow the user to type into a text field and will use what is typed to search Teaming for
 * the requested object type, entry, folder, etc.
 * 
 * @author jwootton
 */
public class FindCtrl extends Composite
	implements ClickHandler, Event.NativePreviewHandler, KeyUpHandler, KeyDownHandler
{
	private long	m_searchSequence		= 1L;	// Used to track issued searches...
	private long	m_lastResultsSequence	= 0L;	// ...and responses.
	
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
			FlowPanel topPanel;
			FlowPanel infoPanel;
			FlexTable table;
			Anchor anchor;
			String imgUrl;
			
			m_item = item;
			
			topPanel = new FlowPanel();
			topPanel.addStyleName( "findSearchResultItemWidget" );
			
			table = new FlexTable();
			table.getCellFormatter().setVerticalAlignment( 0, 0, HasVerticalAlignment.ALIGN_TOP );
			table.getCellFormatter().setVerticalAlignment( 0, 1, HasVerticalAlignment.ALIGN_TOP );
			topPanel.add( table );
			
			// Is this a group?
			if ( item instanceof GwtGroup )
			{
				// Yes
				imgUrl = GwtTeaming.getFilrImageBundle().filrGroup48().getSafeUri().asString();
			}
			else
			{
				// Get the image associated with the item.
				imgUrl = item.getImageUrl();
			}
			
			// Does this item have an image associated with it?
			if ( imgUrl != null && imgUrl.length() > 0 )
			{
				Image img;
				
				// Yes
				img = new Image( imgUrl );
				img.setHeight( "25px" );
				img.setWidth( "25px" );
				table.setWidget( 0, 0, img );
			}

			infoPanel = new FlowPanel();
			infoPanel.getElement().getStyle().setMarginLeft( 4, Unit.PX );
			table.setWidget( 0, 1, infoPanel );
			
			// Add the name of the item as an anchor.
			{
				String name;
				
				name = item.getShortDisplayName();
				if ( item instanceof GwtGroup )
				{
					GwtDisplayField field;
					
					// Are we dealing with the "all external users" or the "all internal users" group?
					name = item.getName();
					if ( name != null &&
						 (name.equalsIgnoreCase( "allextusers" ) || name.equalsIgnoreCase( "allusers" )) )
					{
						// Yes
						// Always display the title
						name = item.getTitle();
					}
					else
					{
						// What field are we supposed to use as the primary display?
						field = m_nameCompletionSettings.getGroupPrimaryDisplayField();
						if ( field == GwtDisplayField.NAME )
							name = item.getShortDisplayName();
						else if ( field == GwtDisplayField.TITLE )
							name = item.getTitle();
					}
				}
				
				anchor = new Anchor( name );
				anchor.setWordWrap( false );
				anchor.addStyleName( "noTextDecoration" );
				anchor.addStyleName( "bold" );
				infoPanel.add( anchor );
			}
			
			// Add any additional information about this item.
			{
				String secondaryDisplayText;
				
				secondaryDisplayText = item.getSecondaryDisplayText();
				if ( item instanceof GwtGroup )
				{
					GwtDisplayField field;
					GwtGroup group;
					
					group = (GwtGroup) item;
					
					// What field are we supposed to use for the secondary info?
					field = m_nameCompletionSettings.getGroupSecondaryDisplayField();
					if ( field == GwtDisplayField.FQDN )
						secondaryDisplayText = group.getDn();
					else if ( field == GwtDisplayField.DESCRIPTION )
						secondaryDisplayText = group.getDesc();
				}
				
				if ( GwtClientHelper.hasString( secondaryDisplayText ) )
				{
					Label secondaryText;
	
					secondaryText = new Label( secondaryDisplayText );
					secondaryText.addStyleName( "fontSize85em" );
					secondaryText.addStyleName( "gwt-label" );
					secondaryText.setWordWrap( false );
					infoPanel.add( secondaryText );
				}
			}

			// All composites must call initWidget() in their constructors.
			initWidget( topPanel );
		}// end SearchResultItemWidget()
		
		
		/**
		 * 
		 */
		@Override
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
		
		/**
		 * 
		 */
		public void turnHighlightOff()
		{
			Widget widget;
			
			widget = getWidget();
			if ( widget != null )
				widget.removeStyleName( "findSearchResultItemWidget_HighlightOn" );
		}

		/**
		 * 
		 */
		public void turnHighlightOn()
		{
			Widget widget;
			
			widget = getWidget();
			if ( widget != null )
				widget.addStyleName( "findSearchResultItemWidget_HighlightOn" );
		}
		
	}// end SearchResultItemWidget
	
	
	/**
	 * This widget is used to hold search results.
	 */
	public class SearchResultsWidget extends Composite
		implements ClickHandler
	{
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
		private int m_indexOfHighlightedItem = -1;
		
		
		/**
		 * 
		 */
		public SearchResultsWidget()
		{
			FlowPanel footer;
			InlineLabel searching;
			Image spinnerImg;

			m_mainPanel = new FlowPanel();
			m_mainPanel.addStyleName( "findSearchResults" );
			
			// Create a panel to hold "Searching..."
			m_searchingPanel = new FlowPanel();
			m_searchingPanel.addStyleName( "findSearchingPanel" );
			m_mainPanel.add( m_searchingPanel );
			searching = new InlineLabel( GwtTeaming.getMessages().searching() );
			m_searchingPanel.add( searching );
			spinnerImg = new Image(GwtTeaming.getImageBundle().spinner16());
			m_searchingPanel.add( spinnerImg );
			m_searchingPanel.setVisible( false );
			
			// Create the panel that will hold the content.
			m_contentPanel = createContentPanel();
			m_mainPanel.add( m_contentPanel );
			
			// Create the footer
			footer = createFooter();
			m_mainPanel.add( footer );

			m_nameCompletionSettings = new GwtNameCompletionSettings();

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
			
			m_indexOfHighlightedItem = -1;
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
			
			// Do we have any results?
			if ( m_displayCount == 0 )
			{
				FlowPanel topPanel;
				Label label;
				
				// No, add "No items found" message.
				topPanel = new FlowPanel();
				topPanel.addStyleName( "findSearchResults_NoItemsFound" );
				label = new Label( GwtTeaming.getMessages().findCtrl_NoItemsFound() );
				topPanel.add( label );
				m_contentPanel.add( topPanel );
			}

			// Figure out the position of the last result within the total number of results.
			position = (searchCriteria.getPageNumber() * searchCriteria.getMaxResults()) + m_displayCount;
			
			// Construct the string n - n of n based on the number of items found in the search.
			value1 = (searchCriteria.getPageNumber() * searchCriteria.getMaxResults()) + 1;
			if ( m_searchCountTotal == 0 )
				value1 = 0;
			nOfn = GwtTeaming.getMessages().nOfn_Exact( value1, position, m_searchCountTotal );
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
			ImageResource imageResource;
			
			panel = new FlowPanel();
			panel.addStyleName( "findSearchResultsFooter" );

			table = new FlexTable();
			table.addStyleName( "findSearchResultsFooterImages" );
			panel.add( table );
			imgPanel = new FlowPanel();
			table.setWidget( 0, 0, imgPanel );
			
			// Add the previous images to the footer.
			imageResource = GwtTeaming.getImageBundle().previousDisabled16();
			m_prevDisabledImg = new Image(imageResource);
			imgPanel.add( m_prevDisabledImg );
			imageResource = GwtTeaming.getImageBundle().previous16();
			m_prevImg = new Image(imageResource);
			m_prevImg.addStyleName( "cursorPointer" );
			m_prevImg.getElement().setAttribute( "id", "viewPreviousPageOfResults" );
			imgPanel.add( m_prevImg );
			m_prevImg.setVisible( false );
			
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
			m_nextImg.getElement().setAttribute( "id", "viewNextPageOfResults" );
			imgPanel.add( m_nextImg );
			m_nextImg.setVisible( false );

			return panel;
		}// end createFooter()
		
		/**
		 * 
		 */
		public SearchResultItemWidget getHightlightedItem()
		{
			Widget widget;
			
			if ( m_indexOfHighlightedItem == -1 )
				return null;
			
			widget = m_contentPanel.getWidget( m_indexOfHighlightedItem );
			if ( widget != null && widget instanceof SearchResultItemWidget )
				return (SearchResultItemWidget) widget;
			
			return null;
		}

		/**
		 * Issue an rpc request to get the name completion settings from the server.
		 */
		private void getNameCompletionSettingsFromServer()
		{
			GetNameCompletionSettingsCmd cmd;

			// Execute a GWT RPC command asking the server for the name completion settings
			cmd = new GetNameCompletionSettingsCmd();
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
												t,
												GwtTeaming.getMessages().rpcFailure_GetNameCompletionSettings() );
				}
				
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					if ( response.getResponseData() != null && response.getResponseData() instanceof GwtNameCompletionSettings )
					{
						m_nameCompletionSettings = (GwtNameCompletionSettings) response.getResponseData();
					}
				}
			});
		}
		
		/**
		 * 
		 */
		public int getNumResults()
		{
			return m_displayCount;
		}
		
		/**
		 * Hide the "Searching..." text.
		 */
		public void hideSearchingText()
		{
			m_searchingPanel.setVisible( false );
		}// end hideSearchingText()
		
		
		/**
		 * Highlight the next item in the list.
		 */
		public void highlightNextItem()
		{
			Widget widget;
			
			if ( m_displayCount == 0 )
				return;

			// Is there an item that is currently highlighted
			if ( m_indexOfHighlightedItem != -1 )
			{
				// Yes
				// Is there another item in the list?
				if ( (m_indexOfHighlightedItem + 1) < m_displayCount )
				{
					// Yes
					// unhighlight the currently highlighted item.
					widget = m_contentPanel.getWidget( m_indexOfHighlightedItem );
					
					if ( widget != null && widget instanceof SearchResultItemWidget )
					{
						SearchResultItemWidget searchResultWidget;
						
						searchResultWidget = (SearchResultItemWidget) widget;
						searchResultWidget.turnHighlightOff();
					}
					
					++m_indexOfHighlightedItem;
				}
			}
			else
				m_indexOfHighlightedItem = 0;
			
			widget = m_contentPanel.getWidget( m_indexOfHighlightedItem );
			if ( widget != null && widget instanceof SearchResultItemWidget )
			{
				SearchResultItemWidget searchResultWidget;
				
				searchResultWidget = (SearchResultItemWidget) widget;
				searchResultWidget.turnHighlightOn();
				
				searchResultWidget.getElement().scrollIntoView();
			}
		}
		
		/**
		 * Highlight the previous item in the list.
		 */
		public void highlightPreviousItem()
		{
			Widget widget;
			
			if ( m_displayCount == 0 )
				return;

			// Is there an item that is currently highlighted
			if ( m_indexOfHighlightedItem != -1 )
			{
				// Yes
				// Is there a previous item in the list?
				if ( (m_indexOfHighlightedItem - 1) >= 0 )
				{
					// Yes
					// unhighlight the currently highlighted item.
					widget = m_contentPanel.getWidget( m_indexOfHighlightedItem );
					
					if ( widget != null && widget instanceof SearchResultItemWidget )
					{
						SearchResultItemWidget searchResultWidget;
						
						searchResultWidget = (SearchResultItemWidget) widget;
						searchResultWidget.turnHighlightOff();
					}
					
					--m_indexOfHighlightedItem;
				}
			}
			else
				m_indexOfHighlightedItem = 0;
			
			widget = m_contentPanel.getWidget( m_indexOfHighlightedItem );
			if ( widget != null && widget instanceof SearchResultItemWidget )
			{
				SearchResultItemWidget searchResultWidget;
				
				searchResultWidget = (SearchResultItemWidget) widget;
				searchResultWidget.turnHighlightOn();

				searchResultWidget.getElement().scrollIntoView();
			}
		}
		
		/**
		 * 
		 */
		@Override
		public void onAttach()
		{
			Scheduler.ScheduledCommand cmd;
			
			super.onAttach();
			
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					// Issue an rpc request to get the name completion settings from the server
					getNameCompletionSettingsFromServer();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
		
		/**
		 * This method gets called when the user clicks on an item from the list of search results.
		 */
		@Override
		public void onClick( ClickEvent clickEvent )
		{
			// Get the item selected by the user.
			if ( clickEvent.getSource() instanceof SearchResultItemWidget )
			{
				SearchResultItemWidget tmp;
				final GwtTeamingItem selectedItem;
				
				tmp = (SearchResultItemWidget) clickEvent.getSource();
				selectedItem = tmp.getTeamingItem();

				// Put the selected item into affect.
				setSelectedItemAsync( selectedItem );
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
			m_searchingPanel.getElement().getStyle().setLeft( x, Unit.PX );
			
			// Show the "searching..." text
			m_searchingPanel.setVisible( true );
		}// end showSearchingText()
	}// end SearchResultsWidget
	
	
	
	
	private Label m_floatingHintLabel;
	private TextBox m_txtBox;
	private SearchResultsWidget m_searchResultsWidget;
	private Object m_selectedObj = null;
	private AsyncCallback<VibeRpcResponse> m_searchResultsCallback;
	private Timer m_timer = null;
	private Timer m_searchTimer = null;
	private GwtSearchCriteria m_searchCriteria;
	private boolean m_searchInProgress = false;
	private String m_prevSearchString = null;
	private FlowPanel m_scopePanel;
	private RadioButton m_searchSiteRb;
	private RadioButton m_searchBinderRb;
	private Widget m_containerWidget;
	private GwtNameCompletionSettings m_nameCompletionSettings;

	private static int m_count = 0;

	
	/*
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private FindCtrl(
		Widget containerWidget,
		SearchType searchType,
		int visibleLength )
	{
		m_containerWidget = containerWidget;
		
		++m_count;
		
		m_searchCriteria = new GwtSearchCriteria();
		m_searchCriteria.setSearchType( searchType );
		m_searchCriteria.setMaxResults( 10 );
		m_searchCriteria.setPageNumber( 0 );
		m_searchCriteria.setSearchScope( SearchScope.SEARCH_LOCAL );

		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName( "gwtFindCtrl" );
		
		// Create a panel where the controls for setting the scope of the search will live.
		{
			FlowPanel panel;
			ClickHandler clickHandler;
			String rbGroupName;
			
			m_scopePanel = new FlowPanel();
			m_scopePanel.setVisible( false );
			m_scopePanel.addStyleName( "findCtrlScopePanel" );
			
			// Add a "Search entire site" radio button
			panel = new FlowPanel();
			rbGroupName = "searchScope" + String.valueOf( m_count );
			m_searchSiteRb = new RadioButton( rbGroupName, GwtTeaming.getMessages().searchEntireSiteLabel() );
			panel.add( m_searchSiteRb );

			// Add a click handler for the "search entire site" rb
			clickHandler = new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent clickEvent )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// Set the search scope to "search entire site"
							m_searchCriteria.setSearchScope( SearchScope.SEARCH_ENTIRE_SITE );
							
							// Hide the search results.
							hideSearchResults();
						}
					} );
				}
			};
			m_searchSiteRb.addClickHandler( clickHandler );
			m_scopePanel.add( panel );
			
			// Add a "Search current folder/workspace" radio button.
			panel = new FlowPanel();
			m_searchBinderRb = new RadioButton( rbGroupName, GwtTeaming.getMessages().searchCurrentFolderWorkspaceLabel() );
			panel.add( m_searchBinderRb );
			m_scopePanel.add( panel );

			// Add a click handler for the "search current folder/workspace" rb
			clickHandler = new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent clickEvent )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// Set the search scope to "search local"
							m_searchCriteria.setSearchScope( SearchScope.SEARCH_LOCAL );
							
							// Hide the search results.
							hideSearchResults();
						}
					} );
				}
			};
			m_searchBinderRb.addClickHandler( clickHandler );
			
			mainPanel.add( m_scopePanel );
		}

		// Create a text box for the user to type in.
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			panel.getElement().getStyle().setPosition( Position.RELATIVE );

			m_txtBox = new TextBox();
			m_txtBox.setVisibleLength( visibleLength );
			m_txtBox.addKeyUpHandler( this );
			m_txtBox.addKeyDownHandler( this );
			m_txtBox.addKeyDownHandler( new KeyDownHandler()
			{
				@Override
				public void onKeyDown( KeyDownEvent event )
				{
					// Hide the hint.
					hideFloatingHint();
				}
			} );
			panel.add( m_txtBox );
			
			// Create a hint we will put over the top of the text box
			m_floatingHintLabel = new Label( "" );
			m_floatingHintLabel.addStyleName( "findCtrl_FloatingHint" );
			m_floatingHintLabel.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							m_txtBox.setFocus( true );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} ); 
			panel.add( m_floatingHintLabel );

			mainPanel.add( panel );
		}
		
		// Create a widget where the search results will live.
		{
			FlowPanel searchResultsPanel;
			
			searchResultsPanel = new FlowPanel();
			searchResultsPanel.addStyleName( "findSearchResultsPanel" );
			mainPanel.add( searchResultsPanel );
			
			m_searchResultsWidget = new SearchResultsWidget();
			searchResultsPanel.add( m_searchResultsWidget );
			hideSearchResults();
		}
		
		// Add handlers that will be called when the user clicks on the "previous" or "next" images.
		m_searchResultsWidget.addClickHandlerOnPrevImg( this );
		m_searchResultsWidget.addClickHandlerOnNextImg( this );

		// Register a preview-event handler.  We do this so we can see the mouse-down event
		// and close the search results.
		Event.addNativePreviewHandler( this );
		
		// Create the callback that will be used when we issue an ajax call to do a search.
		m_searchResultsCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure(Throwable t)
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_Search() );
				
				m_searchInProgress = false;
				m_searchResultsWidget.hideSearchingText();
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				GwtSearchResults gwtSearchResults;
				
				gwtSearchResults = (GwtSearchResults)response.getResponseData();
				
				m_searchResultsWidget.hideSearchingText();
				
				if ( gwtSearchResults != null )
				{
					// Yes
					// Show the search-results widget.
					showSearchResults();

					// Is this a newer set of search results than the
					// last ones we processed?
					long searchSequence = gwtSearchResults.getSearchSequence();
					if ( searchSequence > m_lastResultsSequence )
					{
						// Yes!  We need to process them.
						m_lastResultsSequence = searchSequence;
						
						// Add the search results to the search results widget.
						m_searchResultsWidget.addSearchResults( m_searchCriteria, gwtSearchResults );
					}
					else {
						// No, we've processed newer results than
						// these!  Ignore them.
						if ( ExecuteSearchCmd.DEBUG_SEARCH_SEQUENCE )
						{ 
							GwtClientHelper.deferredAlert( "FindCtrl.m_searchResultsCallback:  Ignored out of sequence search results.  Sequence number:  " + searchSequence );
						}
					}
				}
				
				m_searchInProgress = false;
			}// end onSuccess()
		};
		m_searchInProgress = false;
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}// end FindCtrl()
	
	/**
	 * Add a key press handler
	 */
	public HandlerRegistration addKeyPressHandler( KeyPressHandler handler )
	{
		return m_txtBox.addKeyPressHandler( handler );
	}
	
	/**
	 * Add a change handler
	 */
	public HandlerRegistration addChangeHandler( ChangeHandler handler )
	{
		return m_txtBox.addChangeHandler( handler );
	}
	
	/**
	 * Add a key up handler
	 */
	public HandlerRegistration addKeyUpHandler( KeyUpHandler handler )
	{
		return m_txtBox.addKeyUpHandler( handler );
	}
	
	/**
	 * Clear the text in the text box.
	 */
	public void clearText()
	{
		m_txtBox.setText( "" );
	}
	
	
	/**
	 * Allow the user to specify the scope of the search
	 */
	public void enableScope( String binderId, boolean scopePanelVisible )
	{
		if ( m_searchCriteria.getSearchScope() == SearchScope.SEARCH_LOCAL )
			m_searchBinderRb.setValue( true );
		else
			m_searchSiteRb.setValue( true );
		
		m_scopePanel.setVisible( scopePanelVisible );
		m_searchCriteria.setBinderId( binderId );
		m_searchCriteria.setSearchSubfolders( true );
	}
	
	public void enableScope( String binderId )
	{
		enableScope( binderId, true );
	}
	
	
	/**
	 * 
	 */
	public void executeSearch()
	{
		// Clear any results we may be currently displaying.
		m_searchResultsWidget.clearCurrentContent();

		// Issue an ajax request to search for the specified type of object.
		{
			ExecuteSearchCmd cmd;
			
			m_searchInProgress = true;
			cmd = new ExecuteSearchCmd( m_searchSequence, m_searchCriteria );
			m_searchSequence += 1;	// Bump the search sequence number so we can properly sequence the responses.
			GwtClientHelper.executeCommand( cmd, m_searchResultsCallback );
		}

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
	 * Return the text that is currently in the textbox.
	 */
	public String getText()
	{
		return m_txtBox.getText();
	}
	
	/**
	 * 
	 */
	public void hideFloatingHint()
	{
		m_floatingHintLabel.setVisible( false );
	}
	
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
		left = m_searchResultsWidget.getAbsoluteLeft();
		top = m_searchResultsWidget.getAbsoluteTop();
		height = m_searchResultsWidget.getOffsetHeight();
		width = m_searchResultsWidget.getOffsetWidth();
		
		// Factor scrolling into the mouse position.
		mouseY += Window.getScrollTop();
		mouseX += Window.getScrollLeft();
		
		//GWT.log( "left: " + left + " top: " + top + " height: " + height + " width: " + width + " mouseY: " + mouseY + " mouseX: " + mouseX, null );

		// Is the mouse over this control?
		if ( mouseY >= top && mouseY <= (top + height) && mouseX >= left && (mouseX <= left + width) )
			return true;
		
		//GWT.log( "isMouseOver() about to return false", null );
		
		return false;
	}// end isMouseOver()

	
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
	 * Handles the KeyDownEvent
	 */
	@Override
	public void onKeyDown( KeyDownEvent event )
	{
		// Does the search results widget have any results?
		if ( m_searchResultsWidget.getNumResults() == 0 )
		{
			// No
			return;
		}
		
		if ( event.isDownArrow() )
		{
			// Yes
			m_searchResultsWidget.highlightNextItem();
		}
		else if ( event.isUpArrow() )
		{
			m_searchResultsWidget.highlightPreviousItem();
		}
		else
		{
	        int keyCode;

	        // Get the key the user pressed
	        keyCode = event.getNativeEvent().getKeyCode();

	        // Did the user press Enter?
	        if ( keyCode == KeyCodes.KEY_ENTER )
	        {
	        	SearchResultItemWidget highlightedItem;
	        	
				// Yes
	        	if ( m_searchResultsWidget.isVisible() )
	        	{
		        	// Is there a search result item that is highlighted?
		        	highlightedItem = m_searchResultsWidget.getHightlightedItem();
		        	if ( highlightedItem != null )
		        	{
						// Yes
						// Kill the keystroke.
			        	event.stopPropagation();
			        	event.preventDefault();
			        	
			        	// Close the search results panel
			        	hideSearchResults();
		        		
		        		// Put the selected item into affect.
						setSelectedItemAsync( highlightedItem.getTeamingItem() );
		        	}
	        	}
	        }
		}
	}
	
	/**
	 * Handles the KeyUpEvent
	 */
	@Override
	public void onKeyUp( KeyUpEvent event )
	{
		String tmp;
        int keyCode;

        // Get the key the user pressed
        keyCode = event.getNativeEvent().getKeyCode();

        // Did the user press Enter?
        if ( keyCode == KeyCodes.KEY_ENTER )
        {
			// Yes
        	event.stopPropagation();
        	event.preventDefault();
        	return;
        }

        // Get the search criteria the user entered.
		tmp = m_txtBox.getText();
		
		// Did the search string change?
		if ( m_prevSearchString == null || tmp == null || !m_prevSearchString.equalsIgnoreCase( tmp ) )
		{
			// Yes
			m_prevSearchString = tmp;
			
			if (( tmp == null ) || ( 0 == tmp.trim().length() ))
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
	@Override
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
	 * 
	 */
	public void setContainerWidget( Widget containerWidget )
	{
		m_containerWidget = containerWidget;
	}
	
	/**
	 * 
	 */
	public void setFloatingHintText( String txt )
	{
		m_floatingHintLabel.setText( txt );
	}
	
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
	 * 
	 */
	public void setIsSendingEmail( boolean isSendingEmail )
	{
		m_searchCriteria.setIsSendingEmail( isSendingEmail );
	}
	
	
	/**
	 * Set the search criteria for whether or not we are searching only for folders.
	 */
	public void setSearchForFoldersOnly( boolean value )
	{
		m_searchCriteria.setFoldersOnly( value );
	}// end setSearchForFoldersOnly()

	/**
	 * Set when searching for principals, should we search for external principals.
	 */
	public void setSearchForExternalPrincipals( boolean external )
	{
		m_searchCriteria.setSearchForExternalPrincipals( external );
	}
	
	/**
	 * Set when searching for principals, should we search for internal principals.
	 */
	public void setSearchForInternalPrincipals( boolean internal )
	{
		m_searchCriteria.setSearchForInternalPrincipals( internal );
	}
	
	/*
	 * When searching for groups or principals, should we include ldap containers
	 */
	public void setSearchForLdapContainers( boolean search )
	{
		m_searchCriteria.setSearchForLdapContainers( search );
	}
	
	/**
	 * Set when searching for principals, should we include ldap groups.
	 */
	public void setSearchForLdapGroups( boolean search )
	{
		m_searchCriteria.setSearchForLdapGroups( search );
	}
	
	/**
	 * Should we include team groups in the search
	 */
	public void setSearchForTeamGroups( boolean search )
	{
		m_searchCriteria.setSearchForTeamGroups( search );
	}
	
	/**
	 * Sets the search type of the search being done.
	 * 
	 * @param searchType
	 */
	public void setSearchType( SearchType searchType )
	{
		m_searchCriteria.setSearchType( searchType );
	}// end setSearchType()

	
	/**
	 * Returns the current search type.
	 * 
	 * @return
	 */
	public SearchType getSearchType()
	{
		return m_searchCriteria.getSearchType();
	}// end getSearchType()

	/**
	 * Asynchronously sets the focus in the FindCtrl's TextBox.
	 */
	public void setFocusAsync(final boolean focused) {
		if (null != m_txtBox) {
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					setFocus(focused);
				}
			});
		}
	}
	
	/**
	 * Synchronously sets the focus in the FindCtrl's TextBox.
	 */
	public void setFocus(final boolean focused) {
		if (null != m_txtBox) {
			m_txtBox.setFocus(focused);
		}
	}
	
	/*
	 * Sets the selected item in the FindCtrl.
	 */
	private void setSelectedItemAsync( final GwtTeamingItem selectedItem )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute() 
			{
				setSelectedItemNow( selectedItem );
			}
		} );
	}
	
	/*
	 * Sets the selected item in the FindCtrl.
	 */
	private void setSelectedItemNow( GwtTeamingItem selectedItem )
	{
		updateTextBoxWithSelectedItem( selectedItem );
		GwtTeaming.fireEvent( new SearchFindResultsEvent( m_containerWidget, selectedItem ) );
	}
	
	/**
	 * 
	 */
	public void showFloatingHint()
	{
		m_floatingHintLabel.setVisible( true );
	}
	
	/**
	 * Show the search results. 
	 */
	public void showSearchResults()
	{
		// Make the search results widget as wide as the text box.  We subtract 4 because of the border around the search results widget.
		m_searchResultsWidget.setWidthInt( m_txtBox.getOffsetWidth() - 4 );
		m_searchResultsWidget.setVisible( true );
	}// end showSearchResults()
	
	
	/*
	 * Update the text box with the name of the selected item.
	 */
	private void updateTextBoxWithSelectedItem( GwtTeamingItem selectedItem )
	{
		String name;

		// Get the name of the selected item.
		name = selectedItem.getShortDisplayName();
		
		// Put the name of the selected item in the text box.
		m_txtBox.setText( name );
		
		// Put the focus in the text box.
		m_txtBox.setFocus( true );
	}// end updateTextBoxWithSelectedItem()


	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the FindCtrl and perform some operation on it.                */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the find control
	 * asynchronously after it loads. 
	 */
	public interface FindCtrlClient
	{
		void onSuccess( FindCtrl findCtrl );
		void onUnavailable();
	}// FindCtrlClient


	/*
	 * Asynchronously loads the TagThisDialog and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
		// Prefetch parameters.
		final FindCtrlClient	findCtrlClient,
		final boolean			prefetch,	// true -> Only a prefetch is performed. 
		
		// Creation parameters.
		final Widget		containerWidget,
		final SearchType	searchType,
		final int			visibleLength,
		
		// Set selected item parameters.
		final FindCtrl			findCtrl,
		final GwtTeamingItem	selectedItem)
	{
		loadControl1(
			// Prefetch parameters.
			findCtrlClient,
			prefetch,
			
			// Creation parameters.
			containerWidget,
			searchType,
			visibleLength,
			
			// Set selected item parameters.
			findCtrl,
			selectedItem );
	}// end doAsyncOperation()


	/*
	 * Various control loaders used to load the split points containing
	 * the code for the controls in the find control.
	 * 
	 * Load the split point for the FindCtrl.
	 */
	private static void loadControl1(
		// Prefetch parameters.
		final FindCtrlClient	findCtrlClient,
		final boolean			prefetch,	// true -> Only a prefetch is performed.
		
		// Creation parameters.
		final Widget		containerWidget,
		final SearchType	searchType,
		final int			visibleLength,
		
		// Set selected item parameters.
		final FindCtrl			findCtrl,
		final GwtTeamingItem	selectedItem )
	{
		GWT.runAsync( FindCtrl.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				initFindCtrl_Finish(
					// Prefetch parameters.
					findCtrlClient,
					prefetch,
					
					// Creation parameters.
					containerWidget,
					searchType,
					visibleLength,
					
					// Set selected item parameters.
					findCtrl,
					selectedItem );
			}// end onSuccess()
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_FindCtrl() );
				findCtrlClient.onUnavailable();
			}// end onFailure()
		} );
	}// end doAsyncOperation()


	/*
	 * Finishes the initialization of the FindCtrl object.
	 */
	private static void initFindCtrl_Finish(
		// Prefetch parameters.
		final FindCtrlClient	findCtrlClient,
		final boolean			prefetch,	// true -> only a prefetch is performed.
		
		// Creation parameters.
		final Widget		containerWidget,
		final SearchType	searchType,
		final int			visibleLength,
		
		// Set selected item parameters.
		final FindCtrl			findCtrl,
		final GwtTeamingItem	selectedItem )
	{
		FindCtrl reply;
		if ( prefetch )
		{
			// Operation:  Prefetch.
			reply = null;
		}
		else if ( null != findCtrl )
		{
			// Operation:  Set selected item.
			reply = findCtrl;
			findCtrl.setSelectedItemAsync( selectedItem );
		}
		else
		{
			// Operation:  Create.
			reply = new FindCtrl( containerWidget, searchType, visibleLength );
		}
		
		// If we have a FindCtrlClient...
		if ( null != findCtrlClient )
		{
			// ...call its success handler.
			findCtrlClient.onSuccess( reply );
		}
	}// end initFindCtrl_Finish()


	/**
	 * Loads the FindCtrl split point and returns an instance of it
	 * via the callback.
	 *
	 * @param searchType
	 * @param visibleLength
	 * @param findCtrlClient
	 */
	public static void createAsync(
		final Widget			containerWidget,
		final SearchType		searchType,
		final int				visibleLength,
		final FindCtrlClient	findCtrlClient )
	{
		doAsyncOperation(
			// Prefetch parameters.
			findCtrlClient,
			false,	// false -> Not a prefetch.
			
			// Required creation parameters.
			containerWidget,
			searchType,
			visibleLength,
			
			// Set selected item parameters.
			null,
			null );
	}// end createAsync()
	
	public static void createAsync(
		final Widget			containerWidget,
		final SearchType		searchType,
		final FindCtrlClient	findCtrlClient )
	{
		doAsyncOperation(
			// Prefetch parameters.
			findCtrlClient,
			false,	// false -> Not a prefetch.
			
			// Creation parameters.
			containerWidget,
			searchType,
			40,
			
			// Set selected item parameters.
			null,
			null );
	}// end createAsync()


	/**
	 * Causes the split point for the FindCtrl to be fetched.
	 * 
	 * @param findCtrlClient
	 */
	public static void prefetch(FindCtrlClient findCtrlClient)
	{
		// If we weren't given a FindCtrlClient...
		if (null == findCtrlClient) {
			// ...create one we can use.
			findCtrlClient = new FindCtrlClient() {			
				@Override
				public void onUnavailable()
				{
					// Unused.
				}// end onUnavailable()
				
				@Override
				public void onSuccess( FindCtrl findCtrl )
				{
					// Unused.
				}// end onSuccess()
			};
		}
		
		doAsyncOperation(
			// Prefetch parameters.
			findCtrlClient,
			true,	// true -> Only a prefetch is performed.
			
			// Creation parameters.
			null,
			null,
			-1,
			
			// Set selected item parameters.
			null,
			null );
	}// end prefetch()
	
	public static void prefetch()
	{
		// Always use the initial form of the method.
		prefetch( null );
	}// end prefetch()

	
	/**
	 * Sets the selected item in the FindCtrl.
	 * 
	 * @param findCtrl
	 * @param selectedItem
	 */
	public static void setSelectedItem( FindCtrl findCtrl, GwtTeamingItem selectedItem )
	{
		doAsyncOperation(
			// Prefetch parameters.
			null,
			false,	// false -> Not a prefetch.
			
			// Creation parameters.
			null,
			null,
			-1,
			
			// Set selected item parameters.
			findCtrl,
			selectedItem );
	}// end setSelectedItem()
}// end FindCtrl
