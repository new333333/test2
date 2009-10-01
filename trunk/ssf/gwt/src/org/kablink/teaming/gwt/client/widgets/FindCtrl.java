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



import org.kablink.teaming.gwt.client.GwtTeaming;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;


/**
 * This widget will allow the user to type into a text field and will use what is typed to search Teaming for
 * the requested object type, entry, folder, etc.
 * @author jwootton
 *
 */
public class FindCtrl extends Composite
	implements KeyUpHandler, Event.NativePreviewHandler
{
	/**
	 * This widget is used to hold search results.
	 */
	public class SearchResults extends Composite
	{
		FlowPanel m_mainPanel;
		Image m_prevDisabledImg;
		Image m_prevImg;
		Image m_nextDisabledImg;
		Image m_nextImg;
		
		/**
		 * 
		 */
		public SearchResults()
		{
			FlowPanel footer;
			FlowPanel contentPanel;
			
			m_mainPanel = new FlowPanel();
			m_mainPanel.addStyleName( "findSearchResults" );
			
			// Create the panel that will hold the content.
			contentPanel = createContentPanel();
			m_mainPanel.add( contentPanel );
			
			// Create the footer
			footer = createFooter();
			m_mainPanel.add( footer );
			
			// All composites must call initWidget() in their constructors.
			initWidget( m_mainPanel );
		}// end SearchResults()


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
			imgPanel.add( m_prevImg );
			
			// Add the next images to the footer.
			abstractImg = GwtTeaming.getImageBundle().nextDisabled16();
			m_nextDisabledImg = abstractImg.createImage();
			imgPanel.add( m_nextDisabledImg );
			abstractImg = GwtTeaming.getImageBundle().next16();
			m_nextImg = abstractImg.createImage();
			imgPanel.add( m_nextImg );
			
			return panel;
		}// end createFooter()
	}// end SearchResults
	
	
	private TextBox m_txtBox;
	private SearchResults m_searchResults;
	private Object m_selectedObj = null;
	private OnSelectHandler m_onSelectHandler;
	private Timer m_timer;
	
	/**
	 * 
	 */
	public FindCtrl(
		OnSelectHandler onSelectHandler ) // We will call this handler when the user selects an item from the search results.
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
		m_searchResults = new SearchResults();
		hideSearchResults();
		mainPanel.add( m_searchResults );

		// Register a preview-event handler.  We do this so we can see the mouse-down event
		// and close the search results.
		Event.addNativePreviewHandler( this );
		
		// Remember the handler we should call when the user selects an item from the search results.
		m_onSelectHandler = onSelectHandler;
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}// end FindCtrl()
	
	
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
		Element element;
		
		element = m_searchResults.getElement();
		DOM.setStyleAttribute( element, "display", "none" );
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
	 * Handles the KeyUpEvent
	 */
	public void onKeyUp( KeyUpEvent event )
	{
		String tmp;
		
		// Is there anything in the text box?
		tmp = m_txtBox.getText();
		if ( tmp != null && tmp.length() > 0 )
		{
			// Yes
			// Show the search-results widget.
			showSearchResults();
		}
		else
		{
			// No, hide the search-results widget.
			hideSearchResults();
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
	 * 
	 */
	public void setInitialSearchString( String searchString )
	{
		if ( searchString == null )
			searchString = "";
		
		m_txtBox.setText( searchString );
	}// end setInitialSearchString()
	
	
	/**
	 * Show the search results. 
	 */
	public void showSearchResults()
	{
		Element element;
		
		element = m_searchResults.getElement();
		DOM.setStyleAttribute( element, "display", "block" );
	}// end showSearchResults()		
}// end FindCtrl
