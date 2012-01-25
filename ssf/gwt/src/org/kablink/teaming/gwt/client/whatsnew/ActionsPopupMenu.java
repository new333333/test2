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

package org.kablink.teaming.gwt.client.whatsnew;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.InvokeReplyEvent;
import org.kablink.teaming.gwt.client.event.InvokeShareEvent;
import org.kablink.teaming.gwt.client.event.InvokeSubscribeEvent;
import org.kablink.teaming.gwt.client.event.InvokeTagEvent;
import org.kablink.teaming.gwt.client.event.MarkEntryReadEvent;
import org.kablink.teaming.gwt.client.event.MarkEntryUnreadEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.rpc.shared.EventValidationListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateEntryEventsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This popup menu is used to display the actions a user can take on a given entry.
 * @author jwootton
 *
 */
public class ActionsPopupMenu extends PopupMenu
{
	private PopupMenuItem m_replyMenuItem;
	private PopupMenuItem m_shareMenuItem;
	private PopupMenuItem m_subscribeMenuItem;
	private PopupMenuItem m_tagMenuItem;
	private PopupMenuItem m_markReadMenuItem;
	private PopupMenuItem m_markUnreadMenuItem;
	private AsyncCallback<VibeRpcResponse> m_checkRightsCallback = null;
	private List<EventValidation> m_eventValidations;
	private ActivityStreamUIEntry m_entry;
	private int m_x;
	private int m_y;
	
	/**
	 * 
	 */
	public ActionsPopupMenu( boolean autoHide, boolean modal )
	{
		super( autoHide, modal );
		
		// Add all the possible menu items.
		{
			GwtTeamingMessages messages;
			
			messages = GwtTeaming.getMessages();
			
			m_eventValidations = new ArrayList<EventValidation>();
			
			// Create the "Reply" menu item.
			m_replyMenuItem = addMenuItem( new InvokeReplyEvent(), null, messages.reply() );
			
			// Create the "Share" menu item.
			m_shareMenuItem = addMenuItem( new InvokeShareEvent(), null, messages.share() );
			
			// Create the "Subscribe" menu item.
			m_subscribeMenuItem = addMenuItem( new InvokeSubscribeEvent(), null, messages.subscribe() );
			
			// Create the "Tag" menu item.
			m_tagMenuItem = addMenuItem( new InvokeTagEvent(), null, messages.tag() );
			
			// Add a separator
			addSeparator();
			
			// Create the "Mark read" menu item.
			m_markReadMenuItem = addMenuItem( new MarkEntryReadEvent(), null, messages.markRead() );

			// Create the "Mark unread" menu item.
			m_markUnreadMenuItem = addMenuItem( new MarkEntryUnreadEvent(), null, messages.markUnread() );
		}
	}
	

	/**
	 * 
	 * @param event
	 * @param img
	 * @param text
	 * 
	 * @return
	 */
	@Override
	public PopupMenuItem addMenuItem( VibeEventBase<?> event, Image img, String text )
	{
		EventValidation eventValidation;
		PopupMenuItem menuItem;

	    menuItem = super.addMenuItem( event, img, text );

		eventValidation = new EventValidation();
		eventValidation.setEventOrdinal( event.getEventEnum().ordinal() );
		
		m_eventValidations.add( eventValidation );
		
		return menuItem;
	}
	
	
	/**
	 * Issue an ajax request to get the rights the user has for the given entry.
	 * After the ajax request returns we will hide menu items that the user does not
	 * have the rights to run.  We will then show this popup menu.
	 */
	private void checkRights()
	{
		if ( m_entry == null )
			return;
		
		if ( m_checkRightsCallback == null )
		{
			m_checkRightsCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable caught )
				{
					Window.alert( "call to validateEntryEvents() failed: " + caught.toString() );
					//!!! Finish
					//GwtClientHelper.handleGwtRPCFailure(
					//	t,
					//	GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
					//	m_parentBinderId );
				}// end onFailure()

				@Override
				public void onSuccess( VibeRpcResponse result )
				{
					EventValidationListRpcResponseData responseData = ((EventValidationListRpcResponseData) result.getResponseData());
					List<EventValidation> eventValidations = responseData.getEventValidationListResults();
					
					for( EventValidation nextValidation : eventValidations )
					{
						if ( nextValidation.isValid() == false )
						{
							TeamingEvents event;
							
							event = TeamingEvents.getEnum(nextValidation.getEventOrdinal());
							
							if ( event.equals( TeamingEvents.INVOKE_REPLY ) )
								setMenuItemVisibility( m_replyMenuItem, false );
							else if ( event.equals( TeamingEvents.INVOKE_SUBSCRIBE ) )
								setMenuItemVisibility( m_subscribeMenuItem, false );
							else if ( event.equals( TeamingEvents.INVOKE_SHARE ) )
								setMenuItemVisibility( m_shareMenuItem, false );
							else if ( event.equals( TeamingEvents.INVOKE_TAG ) )
								setMenuItemVisibility( m_tagMenuItem, false );
						}
					}
					
					// Now that we have validated all the events, show this menu.
					showMenu();
				}// end onSuccess()
			};
		}
		
		// Issue an ajax request to check the rights the user has for the given entry.
		ValidateEntryEventsCmd cmd = new ValidateEntryEventsCmd( m_entry.getEntryId(), m_eventValidations );
		GwtClientHelper.executeCommand( cmd, m_checkRightsCallback );
	}

	
	/**
	 * Show this menu.  Make an rpc request and see what rights the user has for the given entry.
	 * Then based on those rights show/hide the appropriate menu items.  Then show the menu.
	 */
	public void showActionsMenu( ActivityStreamUIEntry entry, int x, int y )
	{
		// Remember the entry we are dealing with.
		m_entry = entry;
		
		// Remember where we should position this popup menu.
		m_x = x;
		m_y = y;
		
		// Associate the given entry with each menu item.
		InvokeReplyEvent reply = ((InvokeReplyEvent) m_replyMenuItem.getEvent());
		reply.setUIEntry( entry );
		
		InvokeShareEvent share = ((InvokeShareEvent) m_shareMenuItem.getEvent());
		share.setUIEntry( entry );
		
		InvokeSubscribeEvent subscribe = ((InvokeSubscribeEvent) m_subscribeMenuItem.getEvent());
		subscribe.setUIEntry( entry );
		
		InvokeTagEvent tag = ((InvokeTagEvent) m_tagMenuItem.getEvent());
		tag.setUIEntry( entry );
		
		MarkEntryReadEvent markRead = ((MarkEntryReadEvent) m_markReadMenuItem.getEvent());
		markRead.setUIEntry( entry );
		
		MarkEntryUnreadEvent markUnread = ((MarkEntryUnreadEvent) m_markUnreadMenuItem.getEvent());
		markUnread.setUIEntry( entry );
		
		// Make sure all the menu items are visible.
		setMenuItemVisibility( m_replyMenuItem, true );
		setMenuItemVisibility( m_shareMenuItem, true );
		setMenuItemVisibility( m_subscribeMenuItem, true );
		setMenuItemVisibility( m_tagMenuItem, true );
		setMenuItemVisibility( m_markReadMenuItem, true );
		setMenuItemVisibility( m_markUnreadMenuItem, true );
		
		// Hide "Mark read" or "Mark unread" depending on whether or not the entry has been read.
		if ( entry.isEntryUnread() )
			setMenuItemVisibility( m_markUnreadMenuItem, false );
		else
			setMenuItemVisibility( m_markReadMenuItem, false );

		// Make an ajax request to see what rights the user has for the given entry.
		// After the ajax request returns we will display this menu.
		checkRights();
	}
	
	
	/**
	 * Show this popup menu.
	 */
	private void showMenu()
	{
		PopupPanel.PositionCallback posCallback;

		posCallback = new PopupPanel.PositionCallback()
		{
			/**
			 * 
			 */
			public void setPosition( int offsetWidth, int offsetHeight )
			{
				setPopupPosition( m_x - offsetWidth, m_y );
			}// end setPosition()
		};
		setPopupPositionAndShow( posCallback );
	}
}
