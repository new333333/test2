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

package org.kablink.teaming.gwt.client.menu;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * This is the menu that holds the actions that can be taken with an item in the
 * Activity Stream control.  ie, Reply, Share, Tag,...
 * @author jwootton
 *
 */
public class PopupMenu extends TeamingPopupPanel
{
	private FlowPanel m_mainPanel = null;
	private FlexTable m_menuItemsTable = null;
	

	/**
	 *
	 */
	public class PopupMenuItem extends Composite
		implements MouseUpHandler, MouseOverHandler, MouseOutHandler
	{
		private VibeEventBase<?> m_event;
		private FlowPanel m_mainPanel;
		private Image m_checkedImg;				// Image used to put a checkmark next to the menu item.
		private Image m_checkedSpacerImg;		// Image used as a spacer if this menu item does not used the checkmark image
		private Image m_img;					// Image used with this menu item.
		private Image m_spacerImg;				// Image used as a spacer if this menu item does not use an image
		
		/**
		 */
		public PopupMenuItem( VibeEventBase<?> event, Image img, String text )
		{
			InlineLabel label;
			ImageResource imageResource;
			
			m_event = event;
			
			m_mainPanel = new FlowPanel();
			m_mainPanel.addStyleName( "popupMenuItem" );

			// Create a checkbox image in case we need it.
			imageResource = GwtTeaming.getImageBundle().check12();
			m_checkedImg = new Image( imageResource );
			m_checkedImg.setVisible( false );
			m_checkedImg.getElement().setAttribute( "align", "absmiddle" );
			m_mainPanel.add( m_checkedImg );

			// Create some spacer images.
			imageResource = GwtTeaming.getImageBundle().spacer1px();
			m_checkedSpacerImg = new Image( imageResource );
			m_checkedSpacerImg.setWidth( "12px" );
			m_checkedSpacerImg.setVisible( false );
			m_mainPanel.add( m_checkedSpacerImg );
			
			m_spacerImg = new Image( imageResource );
			m_spacerImg.setWidth( "12px" );
			m_spacerImg.setVisible( false );
			
			// Do we have an image?
			m_img = img;
			if ( img != null )
				m_mainPanel.add( img );
			else
				m_mainPanel.add( m_spacerImg );
			
			label = new InlineLabel( text );
			m_mainPanel.add( label );
			
			// Add a MouseUp event handler
			addDomHandler( this, MouseUpEvent.getType() );
			
			// Add a mouse over/out event handlers
			addDomHandler( this, MouseOverEvent.getType() );
			addDomHandler( this, MouseOutEvent.getType() );
			
			initWidget( m_mainPanel );
		}
		
		/**
		 * Add the styles needed when the mouse is over this menu item.
		 */
		private void addMouseOverStyles()
		{
			m_mainPanel.addStyleName( "popupMenuItem_Hover" );
		}
		
		
		/**
		 *
		 */
		public void adjustSpacingForChecked( boolean spacingNeeded )
		{
			m_checkedSpacerImg.setVisible( false );
			
			// Do we need to allow for spacing for a check mark?
			if ( spacingNeeded )
			{
				// Yes
				if ( m_checkedImg.isVisible() == false )
					m_checkedSpacerImg.setVisible( true );
			}
			else
				m_checkedSpacerImg.setVisible( false );
		}
		
		/**
		 * If this menu item does not have an image then show the spacer image.
		 */
		public void adjustSpacingForImage()
		{
			// Do we have an image?
			if ( m_img == null )
			{
				// No, show the spacer image.
				m_spacerImg.setVisible( true );
			}
		}
		
		
		/**
		 * 
		 */
		public VibeEventBase<?> getEvent()
		{
			return m_event;
		}
		
		
		/**
		 * This method gets called when this menu item is selected.
		 */
		private void handleMenuItemSelected()
		{
			removeMouseOverStyles();
			
			// Close the menu we are a part of.
			menuItemSelected( this );
			
			fireEvent();
		}
		
		
		/*
		 */
		private void fireEvent()
		{
			if ( m_event != null )
			{
				GwtTeaming.fireEvent( m_event );
			}
		}
		
		/**
		 * Does this menu item have a check mark by it?
		 */
		public boolean isChecked()
		{
			return m_checkedImg.isVisible();
		}
		
		
		/**
		 * This gets called when the user clicks on this menu item.
		 */
		public void onMouseUp( MouseUpEvent event )
		{
			ScheduledCommand cmd = new ScheduledCommand()
			{
				/**
				 * 
				 */
				public void execute()
				{
					handleMenuItemSelected();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
		
		
		/**
		 * 
		 */
		public void onMouseOut( MouseOutEvent event )
		{
			// Remove the style used when the mouse is over this menu item.
			removeMouseOverStyles();
		}


		/**
		 * 
		 */
		public void onMouseOver( MouseOverEvent event )
		{
			// Add the style used when the mouse is over this menu item.
			addMouseOverStyles();
		}


		/**
		 * Remove the styles used when the mouse is over this menu item.
		 */
		private void removeMouseOverStyles()
		{
			m_mainPanel.removeStyleName( "popupMenuItem_Hover" );
		}
		
		
		/**
		 * Set the checked state of this menu item.
		 */
		public void setCheckedState( boolean checked )
		{
			m_checkedImg.setVisible( checked );
		}
	}

	
	/**
	 * 
	 */
	public PopupMenu( boolean autoHide, boolean modal )
	{
		super( autoHide, modal );

		FlowPanel topPanel;
		FlowPanel bottomPanel;
		
		// Tell the menu to 'roll down' when opening. 
		GwtClientHelper.rollDownPopup( this );
		
		// Override the style used for PopupPanel
		setStyleName( "popupMenu" );

		m_mainPanel = new FlowPanel();
		
		// Create a top panel.
		topPanel = new FlowPanel();
		topPanel.addStyleName( "popupMenuTopPanel" );
		m_mainPanel.add( topPanel );
		
		// Create a table where the menu items will live.
		m_menuItemsTable = new FlexTable();
		m_menuItemsTable.setCellPadding( 0 );
		m_menuItemsTable.setCellSpacing( 0 );
		m_mainPanel.add( m_menuItemsTable );
		
		// Create a bottom panel.
		bottomPanel = new FlowPanel();
		bottomPanel.addStyleName( "popupMenuBottomPanel" );
		m_mainPanel.add( bottomPanel );
		
		setWidget( m_mainPanel );
	}
	
	
	/**
	 * Add a menu item to this popup menu
	 * 
	 * @param event
	 * @param img
	 * @param text
	 */
	public PopupMenuItem addMenuItem( VibeEventBase<?> event, Image img, String text )
	{
		PopupMenuItem menuItem;
		int row;

	    menuItem = new PopupMenuItem( event, img, text );
		
		// Add the menu item.
		row = m_menuItemsTable.getRowCount();
		m_menuItemsTable.setWidget( row, 0, menuItem );

		// Does this menu item have an image?
		if ( img != null )
		{
			int i;
			
			// Yes
		    img.addStyleName( "popupMenuItemImg" );
			img.getElement().setAttribute( "align", "absmiddle" );

			// We need to have all menu items that don't have an image, to leave room as if they had an image.
			for (i = 0; i < m_menuItemsTable.getRowCount(); ++i)
			{
				Widget widget;
				
				widget = m_menuItemsTable.getWidget( i, 0 );
				if ( widget instanceof PopupMenuItem )
				{
					PopupMenuItem nextMenuItem;

					nextMenuItem = (PopupMenuItem) widget;
					nextMenuItem.adjustSpacingForImage();
				}
			}
		}
		
		return menuItem;
	}

	/**
	 * Add a separator to this popup menu.
	 */
	public void addSeparator()
	{
		FlowPanel separatorPanel;
		int row;
		
		separatorPanel = new FlowPanel();
		separatorPanel.addStyleName( "popupMenuItemSeparator" );
		
		row = m_menuItemsTable.getRowCount();
		m_menuItemsTable.setWidget( row, 0, separatorPanel );
	}
	

	/**
	 * 
	 */
	public void menuItemSelected( PopupMenuItem menuItem )
	{
		// Close this menu.
		hide();
	}
	
	
	/**
	 * Set the checked state of the given menu item.
	 */
	public void setMenuItemCheckedState( PopupMenuItem menuItem, boolean checked )
	{
		int i;
		boolean areChecked;
		
		menuItem.setCheckedState( checked );

		// See if there are any menu items that are checked.
		areChecked = checked;
		for (i = 0; i < m_menuItemsTable.getRowCount() && areChecked == false; ++i)
		{
			Widget widget;
			
			widget = m_menuItemsTable.getWidget( i, 0 );
			if ( widget instanceof PopupMenuItem )
			{
				PopupMenuItem nextMenuItem;

				nextMenuItem = (PopupMenuItem) widget;
				if ( nextMenuItem.isChecked() )
					areChecked = true;
			}
		}
		
		// Go through all the menu items and adjust the spacing.
		for (i = 0; i < m_menuItemsTable.getRowCount(); ++i)
		{
			Widget widget;
			
			widget = m_menuItemsTable.getWidget( i, 0 );
			if ( widget instanceof PopupMenuItem )
			{
				PopupMenuItem nextMenuItem;

				nextMenuItem = (PopupMenuItem) widget;
				nextMenuItem.adjustSpacingForChecked( areChecked );
			}
		}
	}
	
	/**
	 * Set the visibility of the given menu item.
	 */
	public void setMenuItemVisibility( PopupMenuItem menuItem, boolean visible )
	{
		menuItem.setVisible( visible );
	}
	
	/**
	 * Shows the popup menu.
	 */
	@Override
	public void show() {
		// Tell the super class to show the menu...
		super.show();
		
		// ...and add vertical scrolling to the main frame for the
		// ...duration of the popup.
		GwtClientHelper.scrollUIForPopup(this);
	}	
}
