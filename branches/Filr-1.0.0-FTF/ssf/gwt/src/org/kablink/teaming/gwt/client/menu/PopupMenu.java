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
package org.kablink.teaming.gwt.client.menu;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuBar;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.gwt.user.client.ui.UIObject;

/**
 * This is the menu that holds the actions that can be taken with an item in the
 * Activity Stream control.  ie, Reply, Share, Tag,...
 * 
 * @author jwootton
 */
public class PopupMenu extends TeamingPopupPanel
{
	private VibeMenuBar m_menu;
	private boolean m_canHaveCheckedMenuItems;	// Can this pop-up menu have menu items that are checked?

	/**
	 * 
	 */
	public PopupMenu( boolean autoHide, boolean modal, boolean canHaveCheckedMenuItems )
	{
		super( autoHide, modal );
		
		m_canHaveCheckedMenuItems = canHaveCheckedMenuItems;
		
		// We need to replace gwt-PopupPanel style name because it is causing an empty
		// box to be displayed because initially this control's width and height are 0.
		addStyleName( "vibe-popupMenu" );
		
		setMenu( new VibeMenuBar( true, "vibe-mainMenuPopup" ) );
	}
	

	/**
	 * 
	 */
	public VibeMenuItem addMenuItem( final VibeEventBase<?> event, Image img, String text )
	{
		VibeMenuItem menuItem;
		Command cmd;

		cmd = new Command()
		{
			@Override
			public void execute()
			{
				// Close this menu.
				hide();
				
				GwtTeaming.fireEvent( event );
			}
		};

		menuItem = new VibeMenuItem( cmd, event, img, text, "vibe-mainMenuPopup_Item", m_canHaveCheckedMenuItems );
		m_menu.addItem( menuItem );

		return menuItem;
	}
	
	/**
	 * 
	 */
	public VibeMenuItem addMenuItem( final Command cmd, Image img, String text )
	{
		VibeMenuItem menuItem;
		Command cmd2;
		
		cmd2 = new Command()
		{
			@Override
			public void execute()
			{
				// Close this menu.
				hide();
				
				cmd.execute();
			};
		};
		
		menuItem = new VibeMenuItem(
								cmd2,
								null,
								img,
								text,
								"vibe-mainMenuPopup_Item",
								m_canHaveCheckedMenuItems );
		m_menu.addItem( menuItem );
		
		return menuItem;
	}

	/**
	 * 
	 */
	public void addMenuItem( MenuItem menuItem )
	{
		final ScheduledCommand cmd = menuItem.getScheduledCommand();
		menuItem.setScheduledCommand( new Command ()
		{
			@Override
			public void execute()
			{
				// Close this menu.
				hide();
				
				Scheduler.get().scheduleDeferred( cmd );
			}// end execute()
		} );
		m_menu.addItem( menuItem );
	}

	/**
	 * 
	 */
	public void addMenuItems( List<MenuItem> miList )
	{
		if ( ( null != miList ) && ( ! ( miList.isEmpty() ) ) )
		{
			for ( MenuItem mi:  miList)
			{
				addMenuItem( mi );
			}
		}
	}
	
	/**
	 * 
	 */
	public MenuItemSeparator addSeparator()
	{
		return m_menu.addSeparator();
	}

	/**
	 * Remove the given menu item from the menu.
	 */
	public void removeMenuItem( MenuItem menuItem )
	{
		m_menu.removeItem( menuItem );
	}

	/**
	 * Sets/Replaces the VibeMenuBar in the Popup menu.
	 * 
	 * @param menu
	 */
	public void setMenu( VibeMenuBar menu )
	{
		m_menu = menu;
		setWidget( m_menu );
		
		List<MenuItem> miList = ( ( null == m_menu ) ? null : m_menu.getItems() );
		if ( ( null != miList ) && ( ! ( miList.isEmpty() ) ) )
		{
			for ( MenuItem mi:  miList )
			{
				final ScheduledCommand cmd = mi.getScheduledCommand();
				mi.setScheduledCommand( new Command ()
				{
					@Override
					public void execute()
					{
						// Close this menu.
						hide();
						
						Scheduler.get().scheduleDeferred( cmd );
					}// end execute()
				} );
			}
		}
	}
	
	/*
	 * Asynchronously gives the menu the focus.
	 */
	private void setMenuFocusAsync()
	{
		Command cmd = new Command()
		{
			@Override
			public void execute()
			{
				setMenuFocusNow();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}// end setMenuFocusAsync()
	
	/*
	 * Synchronously gives the menu the focus.
	 */
	private void setMenuFocusNow()
	{
		// Give the menu bar the focus.
		m_menu.focus();
	}// end setMenuFocusNow()
	
	/**
	 * 
	 */
	public void setMenuItemCheckedState( VibeMenuItem menuItem, boolean checked )
	{
		menuItem.setCheckedState( checked );
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

	/**
	 * 
	 */
	public void showMenu( final int x, final int y )
	{
		PopupPanel.PositionCallback posCallback;

		// Create a callback that will be called when this menu is shown.
		posCallback = new PopupPanel.PositionCallback()
		{
			/**
			 * 
			 */
			@Override
			public void setPosition( int offsetWidth, int offsetHeight )
			{
			    int windowTop;
			    int windowBottom;
			    int left;
			    int top;
				
			    left = x;
			    top = y;
			    
			    if ( top > Window.getClientHeight() )
			    	top = Window.getClientHeight();
			    
				if ( (left + offsetWidth) > Window.getClientWidth() )
					left = Window.getClientWidth() - offsetWidth - 25;
				
				windowTop = Window.getScrollTop();
			    windowBottom = windowTop + Window.getClientHeight();

			    // Calculate how far over the bottom 
				if ( (top + offsetHeight) > windowBottom )
					top -= offsetHeight;
				
				setPopupPosition( left, top );
				setMenuFocusAsync();
			}
		};
		setPopupPositionAndShow( posCallback );
	}
	
	/**
	 * Shows the popup menu relative to a UIObject.
	 * 
	 * @param target
	 */
	public void showRelativeToTarget( final UIObject target )
	{
		showRelativeTo( target );
		setMenuFocusAsync();
	}
	
	public void showRelativeToTarget( final Element target )
	{
		// Always use the initial form of the method.
		showRelativeToTarget( GwtClientHelper.getUIObjectFromElement( target ) );
	}
}
