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

import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * 
 * @author jwootton
 *
 */
public class PopupMenuItem extends Composite
	implements MouseUpHandler, MouseOverHandler, MouseOutHandler
{
	private ActionHandler m_actionHandler;
	private TeamingAction m_action;
	private Object m_actionData;
	private FlowPanel m_mainPanel;
	private PopupMenu m_popupMenu = null;	// The menu this menu item belongs to.
	
	/**
	 * 
	 */
	public PopupMenuItem( PopupMenu popupMenu, ActionHandler actionHandler, TeamingAction action, Object actionData, Image img, String text )
	{
		InlineLabel label;
		
		m_popupMenu = popupMenu;
		m_actionHandler = actionHandler;
		m_action = action;
		m_actionData = actionData;
		
		m_mainPanel = new FlowPanel();
		m_mainPanel.addStyleName( "popupMenuItem" );
		m_mainPanel.addStyleName( "smalltext" );

		// Do we have an image?
		if ( img != null )
			m_mainPanel.add( img );
		
		label = new InlineLabel( text );
		m_mainPanel.add( label );
		
		// Add a MouseDown event handler
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
	public TeamingAction getAction()
	{
		return m_action;
	}
	
	
	/**
	 * This method gets called when this menu item is selected.
	 */
	private void handleMenuItemSelected()
	{
		removeMouseOverStyles();
		
		// Close the menu we are a part of.
		if ( m_popupMenu != null )
			m_popupMenu.menuItemSelected( this );
		
		invokeActionHandler();
	}
	
	
	/**
	 * 
	 */
	private void invokeActionHandler()
	{
		// Invoke the handler for the action associated with this menu item.
		if ( m_actionHandler != null )
		{
			m_actionHandler.handleAction( m_action, m_actionData );
		}
	}
	
	
	/**
	 * This gets called when the user clicks on this menu item.
	 */
	public void onMouseUp( MouseUpEvent event )
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
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
	 * Set the data that will be passed to the action handler when this menu item is selected. 
	 */
	public void setActionData( Object actionData )
	{
		m_actionData = actionData;
	}
}
