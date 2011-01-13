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

package org.kablink.teaming.gwt.client.menu;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;


/**
 * This is the menu that holds the actions that can be taken with an item in the
 * Activity Stream control.  ie, Reply, Share, Tag,...
 * @author jwootton
 *
 */
public class PopupMenu extends TeamingPopupPanel
{
	private FlowPanel m_mainPanel = null;
	private FlowPanel m_menuItemsPanel = null;
	
	/**
	 * 
	 */
	public PopupMenu( boolean autoHide, boolean modal )
	{
		super( autoHide, modal );

		FlowPanel topPanel;
		FlowPanel bottomPanel;
		
		// Override the style used for PopupPanel
		setStyleName( "popupMenu" );

		m_mainPanel = new FlowPanel();
		
		// Create a top panel.
		topPanel = new FlowPanel();
		topPanel.addStyleName( "popupMenuTopPanel" );
		m_mainPanel.add( topPanel );
		
		// Create a panel where the menu items will live.
		m_menuItemsPanel = new FlowPanel();
		m_mainPanel.add( m_menuItemsPanel );

		// Create a bottom panel.
		bottomPanel = new FlowPanel();
		bottomPanel.addStyleName( "popupMenuBottomPanel" );
		m_mainPanel.add( bottomPanel );
		
		setWidget( m_mainPanel );
	}
	
	
	/**
	 * Add a menu item to this popup menu
	 */
	public void addMenuItem( PopupMenuItem menuItem )
	{
		m_menuItemsPanel.add( menuItem );
	}
	
	/**
	 * This method gets called when a menu item was selected.
	 */
	public void menuItemSelected( PopupMenuItem menuItem )
	{
		// Close this menu.
		hide();
	}
}
