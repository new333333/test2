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
import org.kablink.teaming.gwt.client.event.DeleteEntryEvent;
import org.kablink.teaming.gwt.client.event.InvokeReplyEvent;
import org.kablink.teaming.gwt.client.event.InvokeSendToFriendEvent;
import org.kablink.teaming.gwt.client.event.InvokeShareEvent;
import org.kablink.teaming.gwt.client.event.InvokeSubscribeEvent;
import org.kablink.teaming.gwt.client.event.InvokeTagEvent;
import org.kablink.teaming.gwt.client.event.MarkEntryReadEvent;
import org.kablink.teaming.gwt.client.event.MarkEntryUnreadEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.event.ViewForumEntryEvent;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.rpc.shared.EventValidationListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetViewFolderEntryUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateEntryEventsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.Agent;
import org.kablink.teaming.gwt.client.util.AgentBase;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.UIObject;

/**
 * This popup menu is used to display the actions a user can take on a given entry.
 * @author jwootton
 *
 */
public class ActionsPopupMenu extends PopupMenu
{
	public enum ActionMenuItem
	{
		DELETE,
		MARK_READ,
		MARK_UNREAD,
		REPLY,
		SEPARATOR,
		SEND_TO_FRIEND,
		SHARE,
		SUBSCRIBE,
		TAG,
		VIEW_DETAILS;
	}
	
	private VibeMenuItem m_replyMenuItem;
	private VibeMenuItem m_sendToFriendMenuItem;
	private VibeMenuItem m_shareMenuItem;
	private VibeMenuItem m_subscribeMenuItem;
	private VibeMenuItem m_tagMenuItem;
	private VibeMenuItem m_markReadMenuItem;
	private VibeMenuItem m_markUnreadMenuItem;
	private VibeMenuItem m_deleteMenuItem;
	private VibeMenuItem m_viewDetailsMenuItem;
	private AsyncCallback<VibeRpcResponse> m_checkRightsCallback = null;
	private List<EventValidation> m_eventValidations;
	private ActivityStreamUIEntry m_entry;
	private UIObject m_actionsMenuTarget;
	
	/**
	 * 
	 */
	public ActionsPopupMenu( boolean autoHide, boolean modal, ActionMenuItem[] menuItems )
	{
		super( autoHide, modal, false );
		
		// Add the menu items that were passed to us
		if ( menuItems != null && menuItems.length > 0 )
		{
			GwtTeamingMessages messages;
			
			messages = GwtTeaming.getMessages();
			
			m_eventValidations = new ArrayList<EventValidation>();
			
			for ( ActionMenuItem menuItem: menuItems )
			{
				switch (menuItem)
				{
				case DELETE:
					// Create the "Delete" menu item.
					m_deleteMenuItem = addMenuItem( new DeleteEntryEvent(), null, messages.deleteEntry() );
					break;
					
				case MARK_READ:
					// Create the "Mark read" menu item.
					m_markReadMenuItem = addMenuItem( new MarkEntryReadEvent(), null, messages.markRead() );
					break;
				
				case MARK_UNREAD:
					// Create the "Mark unread" menu item.
					m_markUnreadMenuItem = addMenuItem( new MarkEntryUnreadEvent(), null, messages.markUnread() );
					break;
					
				case REPLY:
					// Create the "Reply" menu item.
					m_replyMenuItem = addMenuItem( new InvokeReplyEvent(), null, messages.reply() );
					break;
				
				case SEND_TO_FRIEND:
					m_sendToFriendMenuItem = addMenuItem( new InvokeSendToFriendEvent(), null, messages.sendToFriend() );
					break;
					
				case SEPARATOR:
					// Add a separator
					addSeparator();
					break;
					
				case SHARE:
					// Create the "Share" menu item.
					m_shareMenuItem = addMenuItem( new InvokeShareEvent(), null, messages.share() );
					break;
				
				case SUBSCRIBE:
					// Create the "Subscribe" menu item.
					m_subscribeMenuItem = addMenuItem( new InvokeSubscribeEvent(), null, messages.subscribe() );
					break;
				
				case TAG:
					// Create the "Tag" menu item.
					m_tagMenuItem = addMenuItem( new InvokeTagEvent(), null, messages.tag() );
					break;
					
				case VIEW_DETAILS:
					// Create the "View Details" menu item.
					m_viewDetailsMenuItem = addMenuItem( new ViewForumEntryEvent( "" ), null, messages.viewDetails() );
					break;
				
				default:
					Window.alert( "Unknown menu item in ActionsPopupMenu()" );
					break;
				}
			}
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
	public VibeMenuItem addMenuItem( VibeEventBase<?> event, Image img, String text )
	{
		EventValidation eventValidation;
		VibeMenuItem menuItem;

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
				public void onSuccess( final VibeRpcResponse result )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							EventValidationListRpcResponseData responseData = ((EventValidationListRpcResponseData) result.getResponseData());
							List<EventValidation> eventValidations = responseData.getEventValidationListResults();
							AgentBase agent;
							String userAgent;
							
							for( EventValidation nextValidation : eventValidations )
							{
								// Is this menu item valid?
								if ( nextValidation.isValid() == false )
								{
									TeamingEvents event;
									
									// No
									
									event = TeamingEvents.getEnum(nextValidation.getEventOrdinal());
									
									switch (event)
									{
									case INVOKE_REPLY:
										if ( m_replyMenuItem != null )
											m_replyMenuItem.setVisible( false );
										break;
									
									case INVOKE_SEND_TO_FRIEND:
										if ( m_sendToFriendMenuItem != null )
											m_sendToFriendMenuItem.setVisible( false );
										break;
										
									case INVOKE_SUBSCRIBE:
										if ( m_subscribeMenuItem != null )
											m_subscribeMenuItem.setVisible( false );
										break;
									
									case INVOKE_SHARE:
										if ( m_shareMenuItem != null )
											m_shareMenuItem.setVisible( false );
										break;
										
									case INVOKE_TAG:
										if ( m_tagMenuItem != null )
											m_tagMenuItem.setVisible( false );
										break;
									
									case DELETE_ENTRY:
										if ( m_deleteMenuItem != null )
											m_deleteMenuItem.setVisible( false );
										break;
										
									case VIEW_FORUM_ENTRY:
										if ( m_viewDetailsMenuItem != null )
											m_viewDetailsMenuItem.setVisible( false );
										break;
									}
								}
							}
							
							// Now that we have validated all the events, show this menu.
							// Are we running in Firefox?
							agent = GWT.create( Agent.class );
							userAgent = agent.getAgentName();
							if ( userAgent != null && userAgent.equalsIgnoreCase( "gecko1_8" ) )
							{
								FlowPanel panel;
								int x;
								int y;
								int scrollTop = 0;

								// Yes
								x = m_actionsMenuTarget.getAbsoluteLeft();
								y = m_actionsMenuTarget.getAbsoluteTop() + m_actionsMenuTarget.getOffsetHeight();

								// Get the panel the menu will be displayed in.
								panel = m_entry.getActivityStreamCtrl().getSearchResultsPanel();
								if ( panel != null )
								{
									scrollTop = panel.getElement().getScrollTop(); 
								}

								// Show the menu.
								showMenu( x, y );
								
								// If the menu is being displayed in a <div> that has
								// been scrolled, Firefox resets the scroll position to 0.
								// We need to restore the scroll position.
								if ( panel != null )
								{
									panel.getElement().setScrollTop( scrollTop );
								}
							}
							else
							{
								// No
								showRelativeToTarget( m_actionsMenuTarget );
							}
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
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
	public void showActionsMenu( ActivityStreamUIEntry entry, UIObject target )
	{
		// Remember the entry we are dealing with.
		m_entry = entry;
		
		// Remember where we should position this popup menu.
		m_actionsMenuTarget = target;
		
		// Associate the given entry with each menu item.
		
		if ( m_replyMenuItem != null )
		{
			InvokeReplyEvent reply = ((InvokeReplyEvent) m_replyMenuItem.getEvent());
			reply.setUIEntry( entry );

			m_replyMenuItem.setVisible( true );
		}
		
		if ( m_sendToFriendMenuItem != null )
		{
			InvokeSendToFriendEvent reply = ((InvokeSendToFriendEvent) m_sendToFriendMenuItem.getEvent());
			reply.setUIEntry( entry );
			
			m_sendToFriendMenuItem.setVisible( true );
		}
		
		if ( m_shareMenuItem != null )
		{
			InvokeShareEvent share = ((InvokeShareEvent) m_shareMenuItem.getEvent());
			share.setUIEntry( entry );

			if ( entry instanceof ActivityStreamTopEntry )
				m_shareMenuItem.setVisible( true );
			else
				m_shareMenuItem.setVisible( false );
		}
		
		if ( m_subscribeMenuItem != null )
		{
			InvokeSubscribeEvent subscribe = ((InvokeSubscribeEvent) m_subscribeMenuItem.getEvent());
			subscribe.setUIEntry( entry );

			m_subscribeMenuItem.setVisible( true );
		}
		
		if ( m_tagMenuItem != null )
		{
			InvokeTagEvent tag = ((InvokeTagEvent) m_tagMenuItem.getEvent());
			tag.setUIEntry( entry );

			m_tagMenuItem.setVisible( true );
		}
		
		if ( m_deleteMenuItem != null )
		{
			DeleteEntryEvent tag = ((DeleteEntryEvent) m_deleteMenuItem.getEvent());
			tag.setUIEntry( entry );

			m_deleteMenuItem.setVisible( true );
		}
		
		if ( m_markReadMenuItem != null )
		{
			MarkEntryReadEvent markRead = ((MarkEntryReadEvent) m_markReadMenuItem.getEvent());
			markRead.setUIEntry( entry );

			if ( entry instanceof ActivityStreamTopEntry )
				m_markReadMenuItem.setVisible( true );
			else
				m_markReadMenuItem.setVisible( false );
		}
		
		if ( m_markUnreadMenuItem != null )
		{
			MarkEntryUnreadEvent markUnread = ((MarkEntryUnreadEvent) m_markUnreadMenuItem.getEvent());
			markUnread.setUIEntry( entry );

			if ( entry instanceof ActivityStreamTopEntry )
				m_markUnreadMenuItem.setVisible( true );
			else
				m_markUnreadMenuItem.setVisible( false );
		}
		
		// Hide "Mark read" or "Mark unread" depending on whether or not the entry has been read.
		if ( entry.isEntryUnread() )
		{
			if ( m_markUnreadMenuItem != null )
				m_markUnreadMenuItem.setVisible( false );
		}
		else
		{
			if ( m_markReadMenuItem != null )
				m_markReadMenuItem.setVisible( false );
		}

		if ( m_viewDetailsMenuItem != null )
		{
			if ( entry instanceof ActivityStreamTopEntry )
			{
				final EntityId entityId;
				GetViewFolderEntryUrlCmd cmd;

				m_viewDetailsMenuItem.setVisible( true );

				entityId = m_entry.getEntryEntityId();

				cmd = new GetViewFolderEntryUrlCmd( entityId.getBinderId(), entityId.getEntityId() );
				GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
				{
					@Override
					public void onFailure( Throwable caught )
					{
						GwtClientHelper.handleGwtRPCFailure(
													caught,
													GwtTeaming.getMessages().rpcFailure_GetViewFolderEntryUrl(),
													String.valueOf( entityId.getEntityId() ) );
					}

					@Override
					public void onSuccess( VibeRpcResponse response )
					{
						ViewForumEntryEvent viewEntryEvent;
						StringRpcResponseData responseData;
						String viewUrl;

						viewEntryEvent = (ViewForumEntryEvent) m_viewDetailsMenuItem.getEvent();
						viewEntryEvent.setViewForumEntryUrl( "" );
						
						responseData = (StringRpcResponseData) response.getResponseData();
						viewUrl = responseData.getStringValue();
						if ( GwtClientHelper.hasString( viewUrl ) )
						{
							viewEntryEvent.setViewForumEntryUrl( viewUrl );
						}
					}
				} );
			}
			else
				m_viewDetailsMenuItem.setVisible( false );
		}
		
		// Make an ajax request to see what rights the user has for the given entry.
		// After the ajax request returns we will display this menu.
		checkRights();
	}
}
