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

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuBar;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

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
 * @author jwootton
 *
 */
public class PopupMenu extends TeamingPopupPanel
{
	private VibeMenuBar m_menu;
	

	/**
	 * 
	 */
	public PopupMenu( boolean autoHide, boolean modal )
	{
		super( autoHide, modal );
		
		// We need to replace gwt-PopupPanel style name because it is causing an empty
		// box to be displayed because initially this control's width and height are 0.
		setStylePrimaryName( "popupMenu" );
		
		m_menu = new VibeMenuBar( true, "popupMenu" );
		
		setWidget( m_menu );
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

		menuItem = new VibeMenuItem( cmd, event, img, text, "popupMenuItem" );
		m_menu.addItem( menuItem );

		return menuItem;
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
	public void removeMenuItem( VibeMenuItem menuItem )
	{
		m_menu.removeItem( menuItem );
	}
	
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
	public void showRelativeToTarget( UIObject target )
	{
		int x;
		int y;
		
		x = target.getAbsoluteLeft();
		y = target.getAbsoluteTop();
		
		showMenu( x, y );
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
			public void setPosition( int offsetWidth, int offsetHeight )
			{
				int left;
				int maxWidth;
				List<MenuItem> menuItems;
				
				// Figure out how wide the menu is.  For some unknown reason calling
				// m_menu.getAbsoluteWidth() doesn't work.
				maxWidth = 0;
				menuItems = m_menu.getItems();
				for (MenuItem menuItem : menuItems)
				{
					int width;
					
					width = menuItem.getOffsetWidth();
					if ( width > maxWidth )
						maxWidth = width;
				}
				
				if ( (x + maxWidth) > Window.getClientWidth() )
					left = Window.getClientWidth() - maxWidth - 5;
				else
					left = x;
				
				setPopupPosition( left, y );
			}
		};
		setPopupPositionAndShow( posCallback );
	}
}
