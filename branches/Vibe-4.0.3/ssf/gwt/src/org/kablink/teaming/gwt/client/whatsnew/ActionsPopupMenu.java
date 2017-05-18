/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.event.DeleteActivityStreamUIEntryEvent;
import org.kablink.teaming.gwt.client.event.EditActivityStreamUIEntryEvent;
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
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.UIObject;

/**
 * This popup menu is used to display the actions a user can take on a
 * given entry.
 * 
 * @author drfoster@novell.com
 */
public class ActionsPopupMenu extends PopupMenu {
	/**
	 * Enumeration that defines the various actions that are supported. 
	 */
	public enum ActionMenuItem implements IsSerializable {
		DELETE,
		EDIT,
		MARK_READ,
		MARK_UNREAD,
		REPLY,
		SEND_TO_FRIEND,
		SEPARATOR,
		SHARE,
		SUBSCRIBE,
		TAG,
		VIEW_DETAILS;
	}
	
	private ActivityStreamUIEntry	m_entry;				//
	private GwtTeamingMessages		m_messages;				//
	private List<EventValidation>	m_eventValidations;		//
	private UIObject				m_actionsMenuTarget;	//
	private VibeMenuItem			m_deleteMenuItem;		//
	private VibeMenuItem			m_editMenuItem;			//
	private VibeMenuItem			m_markReadMenuItem;		//
	private VibeMenuItem			m_markUnreadMenuItem;	//
	private VibeMenuItem			m_replyMenuItem;		//
	private VibeMenuItem			m_sendToFriendMenuItem;	//
	private VibeMenuItem			m_shareMenuItem;		//
	private VibeMenuItem			m_subscribeMenuItem;	//
	private VibeMenuItem			m_tagMenuItem;			//
	private VibeMenuItem			m_viewDetailsMenuItem;	//
	
	/**
	 * Constructor method.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param menuItems
	 */
	public ActionsPopupMenu(boolean autoHide, boolean modal, ActionMenuItem[] menuItems) {
		// Initialize the super class.
		super(autoHide, modal, false);

		// Initialize everything else that requires it.
		m_messages = GwtTeaming.getMessages();
		
		// Add the menu items that were passed to us
		if ((null != menuItems) && (0 < menuItems.length)) {
			m_eventValidations = new ArrayList<EventValidation>();
			for (ActionMenuItem menuItem:  menuItems) {
				switch (menuItem) {
				case DELETE:          m_deleteMenuItem       = addMenuItem(new DeleteActivityStreamUIEntryEvent(), null, m_messages.deleteEntry() ); break;
				case EDIT:            m_editMenuItem         = addMenuItem(new EditActivityStreamUIEntryEvent(),   null, m_messages.editEntry()   ); break;
				case MARK_READ:       m_markReadMenuItem     = addMenuItem(new MarkEntryReadEvent(),               null, m_messages.markRead()    ); break;
				case MARK_UNREAD:     m_markUnreadMenuItem   = addMenuItem(new MarkEntryUnreadEvent(),             null, m_messages.markUnread()  ); break;
				case REPLY:           m_replyMenuItem        = addMenuItem(new InvokeReplyEvent(),                 null, m_messages.reply()       ); break;
				case SEND_TO_FRIEND:  m_sendToFriendMenuItem = addMenuItem(new InvokeSendToFriendEvent(),          null, m_messages.sendToFriend()); break;
				case SHARE:           m_shareMenuItem        = addMenuItem(new InvokeShareEvent(),                 null, m_messages.share()       ); break;
				case SUBSCRIBE:       m_subscribeMenuItem    = addMenuItem(new InvokeSubscribeEvent(),             null, m_messages.subscribe()   ); break;
				case TAG:             m_tagMenuItem          = addMenuItem(new InvokeTagEvent(),                   null, m_messages.tag()         ); break;
				case VIEW_DETAILS:    m_viewDetailsMenuItem  = addMenuItem(new ViewForumEntryEvent(""),            null, m_messages.viewDetails() ); break;
				
				case SEPARATOR:
					addSeparator();
					break;
					
				default:
					GwtClientHelper.deferredAlert("ActionsPopupMenu( *Internal Error* - Unknown ActionMenuItem ):  " + menuItem.name());
					break;
				}
			}
		}
	}
	

	/**
	 * Adds an item to the popup menu.
	 * 
	 * @param event
	 * @param img
	 * @param text
	 * 
	 * @return
	 */
	@Override
	public VibeMenuItem addMenuItem(VibeEventBase<?> event, Image img, String text) {
		VibeMenuItem menuItem = super.addMenuItem(event, img, text);
		EventValidation eventValidation = new EventValidation();
		eventValidation.setEventOrdinal(event.getEventEnum().ordinal());
		m_eventValidations.add(eventValidation);
		return menuItem;
	}
	
	
	/*
	 * Issues a GWT RPC request to get the rights the user has for the
	 * given entry.  After the request returns we will hide menu items
	 * that the user does not have the rights to run.  We will then
	 * show this popup menu.
	 */
	private void checkRights() {
		// If we don't have an entry...
		if (null == m_entry) {
			// ...there's nothing we can validate.
			return;
		}

		// Issue a GWT RPC request to check the rights the user has for
		// the given entry.
		ValidateEntryEventsCmd cmd = new ValidateEntryEventsCmd(m_entry.getEntryId(), m_eventValidations);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_ValidateEntryEvents());
			}

			@Override
			public void onSuccess(final VibeRpcResponse result) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						EventValidationListRpcResponseData responseData = ((EventValidationListRpcResponseData) result.getResponseData());
						List<EventValidation> eventValidations = responseData.getEventValidationListResults();
						for(EventValidation nextValidation: eventValidations) {
							// Is this menu item valid?
							if (!(nextValidation.isValid())) {
								// No!  Hide it.
								switch (TeamingEvents.getEnum(nextValidation.getEventOrdinal())) {
								case DELETE_ACTIVITY_STREAM_UI_ENTRY:  if (null != m_deleteMenuItem)       m_deleteMenuItem.setVisible(      false); break;
								case EDIT_ACTIVITY_STREAM_UI_ENTRY:    if (null != m_editMenuItem)         m_editMenuItem.setVisible(        false); break;
								case INVOKE_REPLY:                     if (null != m_replyMenuItem)        m_replyMenuItem.setVisible(       false); break;
								case INVOKE_SEND_TO_FRIEND:            if (null != m_sendToFriendMenuItem) m_sendToFriendMenuItem.setVisible(false); break;
								case INVOKE_SHARE:                     if (null != m_shareMenuItem)        m_shareMenuItem.setVisible(       false); break;
								case INVOKE_SUBSCRIBE:                 if (null != m_subscribeMenuItem)    m_subscribeMenuItem.setVisible(   false); break;
								case INVOKE_TAG:                       if (null != m_tagMenuItem)          m_tagMenuItem.setVisible(         false); break;
								case MARK_ENTRY_READ:                  if (null != m_markReadMenuItem)     m_markReadMenuItem.setVisible(    false); break;
								case MARK_ENTRY_UNREAD:                if (null != m_markUnreadMenuItem)   m_markUnreadMenuItem.setVisible(  false); break;
								case VIEW_FORUM_ENTRY:                 if (null != m_viewDetailsMenuItem)  m_viewDetailsMenuItem.setVisible( false); break;
								}
							}
						}
						
						// Now that we have validated all the events, show this menu.
						// Are we running in Firefox?
						AgentBase agent = GWT.create(Agent.class);
						String userAgent = agent.getAgentName();
						if ((null != userAgent) && (userAgent.equalsIgnoreCase("gecko1_8"))) {

							// Yes
							int x =  m_actionsMenuTarget.getAbsoluteLeft();
							int y = (m_actionsMenuTarget.getAbsoluteTop() + m_actionsMenuTarget.getOffsetHeight());

							// Get the panel the menu will be displayed in.
							FlowPanel panel = m_entry.getActivityStreamCtrl().getSearchResultsPanel();
							int scrollTop;
							if (panel != null)
							     scrollTop = panel.getElement().getScrollTop();
							else scrollTop = 0;

							// Show the menu.
							showMenu(x, y);
							
							// If the menu is being displayed in a
							// <DIV> that has been scrolled,
							// Firefox resets the scroll position
							// to 0.  We need to restore the scroll
							// position.
							if (panel != null) {
								panel.getElement().setScrollTop(scrollTop);
							}
						}
						
						else {
							// No, it's not Firefox!
							showRelativeToTarget(m_actionsMenuTarget);
						}
					}
				});
			}
		});
	}

	
	/**
	 * Show this menu.  Make a GWT RPC request and see what rights the
	 * user has for the given entry.  Then based on those rights
	 * show/hide the appropriate menu items.  Then show the menu.
	 * 
	 * @param entry
	 * @param target
	 */
	public void showActionsMenu(ActivityStreamUIEntry entry, UIObject target) {
		// Remember the entry we are dealing with.
		m_entry = entry;
		
		// Remember where we should position this popup menu.
		m_actionsMenuTarget = target;
		
		// Associate the given entry with each menu item.
		if (null != m_replyMenuItem) {
			InvokeReplyEvent reply = ((InvokeReplyEvent) m_replyMenuItem.getEvent());
			reply.setUIEntry(entry);
			m_replyMenuItem.setVisible(true);
		}
		
		if (null != m_sendToFriendMenuItem) {
			InvokeSendToFriendEvent reply = ((InvokeSendToFriendEvent) m_sendToFriendMenuItem.getEvent());
			reply.setUIEntry(entry);
			m_sendToFriendMenuItem.setVisible(true);
		}
		
		if (null != m_shareMenuItem) {
			InvokeShareEvent share = ((InvokeShareEvent) m_shareMenuItem.getEvent());
			share.setUIEntry(entry);
			if (entry instanceof ActivityStreamTopEntry)
			     m_shareMenuItem.setVisible(true );
			else m_shareMenuItem.setVisible(false);
		}
		
		if (null != m_subscribeMenuItem) {
			InvokeSubscribeEvent subscribe = ((InvokeSubscribeEvent) m_subscribeMenuItem.getEvent());
			subscribe.setUIEntry(entry);
			m_subscribeMenuItem.setVisible(true);
		}
		
		if (null != m_tagMenuItem) {
			InvokeTagEvent tag = ((InvokeTagEvent) m_tagMenuItem.getEvent());
			tag.setUIEntry(entry);
			m_tagMenuItem.setVisible(true);
		}
		
		if (null != m_deleteMenuItem) {
			DeleteActivityStreamUIEntryEvent tag = ((DeleteActivityStreamUIEntryEvent) m_deleteMenuItem.getEvent());
			tag.setUIEntry(entry);
			m_deleteMenuItem.setVisible(true);
		}
		
		if (null != m_editMenuItem) {
			EditActivityStreamUIEntryEvent tag = ((EditActivityStreamUIEntryEvent) m_editMenuItem.getEvent());
			tag.setUIEntry(entry);
			m_editMenuItem.setVisible(true);
		}
		
		if (null != m_markReadMenuItem) {
			MarkEntryReadEvent markRead = ((MarkEntryReadEvent) m_markReadMenuItem.getEvent());
			markRead.setUIEntry(entry);
		    m_markReadMenuItem.setVisible(true);
		}
		
		if (null != m_markUnreadMenuItem) {
			MarkEntryUnreadEvent markUnread = ((MarkEntryUnreadEvent) m_markUnreadMenuItem.getEvent());
			markUnread.setUIEntry(entry);
			m_markUnreadMenuItem.setVisible(true);
		}
		
		// Hide 'Mark read' or 'Mark unread' depending on whether or
		// not the entry has been read.
		if (entry.isEntryUnread()) {
			if (null != m_markUnreadMenuItem) {
				m_markUnreadMenuItem.setVisible(false);
			}
		}
		else {
			if (null != m_markReadMenuItem) {
				m_markReadMenuItem.setVisible(false);
			}
		}

		if (null != m_viewDetailsMenuItem) {
			final EntityId entityId = m_entry.getEntryEntityId();
			if (entry instanceof ActivityStreamTopEntry && entityId!=null) {
				m_viewDetailsMenuItem.setVisible(true);

				GetViewFolderEntryUrlCmd cmd = new GetViewFolderEntryUrlCmd(entityId.getBinderId(), entityId.getEntityId());
				GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable caught) {
						GwtClientHelper.handleGwtRPCFailure(
							caught,
							m_messages.rpcFailure_GetViewFolderEntryUrl(),
							String.valueOf(entityId.getEntityId()));
					}

					@Override
					public void onSuccess(VibeRpcResponse response) {
						ViewForumEntryEvent viewEntryEvent = ((ViewForumEntryEvent) m_viewDetailsMenuItem.getEvent());
						StringRpcResponseData responseData = ((StringRpcResponseData) response.getResponseData());
						String viewUrl = responseData.getStringValue();
						if (GwtClientHelper.hasString(viewUrl))
						     viewEntryEvent.setViewForumEntryUrl(viewUrl);
						else viewEntryEvent.setViewForumEntryUrl(""     );
					}
				});
			}
			
			else {
				m_viewDetailsMenuItem.setVisible(false);
			}
		}
		
		// Make a GWT RPC request to see what rights the user has for
		// the given entry.  After the request returns we will display
		// this menu.
		checkRights();
	}
}
