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

package org.kablink.teaming.gwt.client.event;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;


/**
 * Helper methods to facilitate dealing with client side GWT events.
 * 
 * @author drfoster@novell.com
 */
public class EventHelper {
	/*
	 * Constructor method.
	 */
	private EventHelper() {
		// Inhibits this class from being instantiated.
	}
	
	/**
	 * Given an event that requires no parameters, fires one.
	 * 
	 * @param eventEnum
	 */
	public static void fireSimpleEvent(TeamingEvents eventEnum) {
		VibeEventBase<?> event = createSimpleEvent(eventEnum);
		if (null != event) {
			GwtTeaming.fireEvent(event);
		}
	}
	
	/**
	 * Given an event that requires no parameters, returns a
	 * VibeEventBase<?> of it.
	 * 
	 * @param eventEnum
	 * 
	 * @return
	 */
	public static VibeEventBase<?> createSimpleEvent(TeamingEvents eventEnum) {
		VibeEventBase<?> reply;
		
		switch (eventEnum) {
		case ADMINISTRATION:                    reply = new AdministrationEvent();               break;
		case ADMINISTRATION_EXIT:               reply = new AdministrationExitEvent();           break;
		case ADMINISTRATION_UPGRADE_CHECK:      reply = new AdministrationUpgradeCheckEvent();   break;
		case BROWSE_HIERARCHY_EXIT:             reply = new BrowseHierarchyExitEvent();          break;
		case EDIT_CURRENT_BINDER_BRANDING:      reply = new EditCurrentBinderBrandingEvent();    break;
		case EDIT_LANDING_PAGE_PROPERTIES:		reply = new EditLandingPagePropertiesEvent();	 break;
		case EDIT_PERSONAL_PREFERENCES:         reply = new EditPersonalPreferencesEvent();      break;
		case EDIT_SITE_BRANDING:                reply = new EditSiteBrandingEvent();             break;
		case FULL_UI_RELOAD:                    reply = new FullUIReloadEvent();                 break;
		case GOTO_MY_WORKSPACE:                 reply = new GotoMyWorkspaceEvent();              break;
		case INVOKE_HELP:                       reply = new InvokeHelpEvent();                   break;
		case INVOKE_CONFIGURE_FILE_SYNC_APP_DLG:reply = new InvokeConfigureFileSyncAppDlgEvent();break;
		case INVOKE_MANAGE_GROUPS_DLG:			reply = new InvokeManageGroupsDlgEvent();		 break;
		case LOGIN:                             reply = new LoginEvent();                        break;
		case PRE_LOGOUT:                        reply = new PreLogoutEvent();                    break;
		case MASTHEAD_HIDE:                     reply = new MastheadHideEvent();                 break;
		case MASTHEAD_SHOW:                     reply = new MastheadShowEvent();                 break;
		case MENU_HIDE:							reply = new MenuHideEvent();					 break;
		case MENU_SHOW:							reply = new MenuShowEvent();					 break;
		case SEARCH_ADVANCED:                   reply = new SearchAdvancedEvent();               break;
		case SIDEBAR_HIDE:                      reply = new SidebarHideEvent();                  break;
		case SIDEBAR_RELOAD:                    reply = new SidebarReloadEvent();                break;
		case SIDEBAR_SHOW:                      reply = new SidebarShowEvent();                  break;
		case SIZE_CHANGED:                      reply = new SizeChangedEvent();                  break;
		case TASK_DELETE:                       reply = new TaskDeleteEvent();                   break;
		case TASK_HIERARCHY_DISABLED:			reply = new TaskHierarchyDisabledEvent();        break;
		case TASK_MOVE_DOWN:                    reply = new TaskMoveDownEvent();                 break;
		case TASK_MOVE_LEFT:                    reply = new TaskMoveLeftEvent();                 break;
		case TASK_MOVE_RIGHT:                   reply = new TaskMoveRightEvent();                break;
		case TASK_MOVE_UP:                      reply = new TaskMoveUpEvent();                   break;
		case TASK_NEW_TASK:                     reply = new TaskNewTaskEvent();                  break;
		case TASK_PURGE:                        reply = new TaskPurgeEvent();                    break;
		case TASK_SET_PERCENT_DONE:             reply = new TaskSetPercentDoneEvent();           break;
		case TASK_SET_PRIORITY:                 reply = new TaskSetPriorityEvent();              break;
		case TASK_SET_STATUS:                   reply = new TaskSetStatusEvent();                break;
		case TASK_VIEW:                         reply = new TaskViewEvent();                     break;
		case TRACK_CURRENT_BINDER:              reply = new TrackCurrentBinderEvent();           break;
		case UNTRACK_CURRENT_BINDER:            reply = new UntrackCurrentBinderEvent();         break;
		case UNTRACK_CURRENT_PERSON:            reply = new UntrackCurrentPersonEvent();         break;
		case VIEW_ALL_ENTRIES:                  reply = new ViewAllEntriesEvent();               break;
		case VIEW_CURRENT_BINDER_TEAM_MEMBERS:  reply = new ViewCurrentBinderTeamMembersEvent(); break;
		case VIEW_RESOURCE_LIBRARY:             reply = new ViewResourceLibraryEvent();          break;
		case VIEW_TEAMING_FEED:                 reply = new ViewTeamingFeedEvent();              break;
		case VIEW_UNREAD_ENTRIES:               reply = new ViewUnreadEntriesEvent();            break;
			
		default:
		case UNDEFINED:
			Window.alert(GwtTeaming.getMessages().eventHandling_NonSimpleEvent(eventEnum.name(), EventHelper.class.getName()));
			reply = null;
			break;
		}
		
		return reply;
	}
	
	/*
	 * Returns true of an event is an an array of events and false
	 * otherwise.
	 */
	private static boolean isTEInA(TeamingEvents event, TeamingEvents[] events) {
		int c = ((null == events) ? 0 : events.length);
		for (int i = 0; i < c; i += 1) {
			if (event.ordinal() == events[i].ordinal()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Registers event handlers on the event bus
	 * 
	 * Note:  This method must be maintained to support ALL the events
	 *    defined in TeamingEvent.java.
	 * 
	 * @param eventBus
	 * @param registerdEvents
	 * @param eventHandler
	 * @param registeredEventHandlers	Optional.  May be null.
	 */
	public static void registerEventHandlers(SimpleEventBus eventBus, TeamingEvents[] eventsToBeRegistered, Object eventHandler, List<HandlerRegistration> registeredEventHandlers) {
		// Validate what's being asked for vs. what the object is
		// defined to support.
		validateEvents(eventsToBeRegistered, eventHandler);
		
		// Scan the events we were given to register.
		boolean returnRegisteredEventHandlers = (null != registeredEventHandlers);
		int events = ((null == eventsToBeRegistered) ? 0 : eventsToBeRegistered.length);
		for (int i = 0; i < events; i += 1) {
			// Which event is this?
			boolean handlerNotDefined = true;
			HandlerRegistration registrationHandler = null;
			TeamingEvents te = eventsToBeRegistered[i];
			switch (te) {
			case ACTIVITY_STREAM:
				// An ActivityStreamEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ActivityStreamEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ActivityStreamEvent.registerEvent(eventBus, ((ActivityStreamEvent.Handler) eventHandler));
				}
				break;
				
			case ACTIVITY_STREAM_ENTER:
				// An ActivityStreamEnterEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ActivityStreamEnterEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ActivityStreamEnterEvent.registerEvent(eventBus, ((ActivityStreamEnterEvent.Handler) eventHandler));
				}
				break;
				
			case ACTIVITY_STREAM_EXIT:
				// An ActivityStreamExitEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ActivityStreamExitEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ActivityStreamExitEvent.registerEvent(eventBus, ((ActivityStreamExitEvent.Handler) eventHandler));
				}
				break;
				
			case ADMINISTRATION:
				// An AdministrationEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof AdministrationEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = AdministrationEvent.registerEvent(eventBus, ((AdministrationEvent.Handler) eventHandler));
				}
				break;
				
			case ADMINISTRATION_EXIT:
				// An AdministrationExitEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof AdministrationExitEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = AdministrationExitEvent.registerEvent(eventBus, ((AdministrationExitEvent.Handler) eventHandler));
				}
				break;
			
			case ADMINISTRATION_UPGRADE_CHECK:
				// An AdministrationUpgradeCheckEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof AdministrationUpgradeCheckEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = AdministrationUpgradeCheckEvent.registerEvent(eventBus, ((AdministrationUpgradeCheckEvent.Handler) eventHandler));
				}
				break;
			
			case BROWSE_HIERARCHY:
				// An BrowseHierarchyEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof BrowseHierarchyEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = BrowseHierarchyEvent.registerEvent(eventBus, ((BrowseHierarchyEvent.Handler) eventHandler));
				}
				break;
			
			case BROWSE_HIERARCHY_EXIT:
				// An BrowseHierarchyExitEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof BrowseHierarchyExitEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = BrowseHierarchyExitEvent.registerEvent(eventBus, ((BrowseHierarchyExitEvent.Handler) eventHandler));
				}
				break;
			
			case CHANGE_CONTEXT:
				// A ChangeContextEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof ChangeContextEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ChangeContextEvent.registerEvent(eventBus, ((ChangeContextEvent.Handler) eventHandler));
				}
				break;
			
			case CONTEXT_CHANGED:
				// A ContextChangedEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ContextChangedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ContextChangedEvent.registerEvent(eventBus, ((ContextChangedEvent.Handler) eventHandler));
				}
				break;
			
			case CONTEXT_CHANGING:
				// A ContextChangingEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ContextChangingEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ContextChangingEvent.registerEvent(eventBus, ((ContextChangingEvent.Handler) eventHandler));
				}
				break;
			
			case EDIT_CURRENT_BINDER_BRANDING:
				// A EditCurrentBinderBrandingEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof EditCurrentBinderBrandingEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = EditCurrentBinderBrandingEvent.registerEvent(eventBus, ((EditCurrentBinderBrandingEvent.Handler) eventHandler));
				}
				break;
			
			case EDIT_LANDING_PAGE_PROPERTIES:
				// A EditLandingPagePropertiesEvent.  Can the event handler we were given handle that?
				if ( eventHandler instanceof EditLandingPagePropertiesEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = EditLandingPagePropertiesEvent.registerEvent( eventBus, ((EditLandingPagePropertiesEvent.Handler) eventHandler) );
				}
				break;
			
			case EDIT_PERSONAL_PREFERENCES:
				// A EditPersonalPreferencesEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof EditPersonalPreferencesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = EditPersonalPreferencesEvent.registerEvent(eventBus, ((EditPersonalPreferencesEvent.Handler) eventHandler));
				}
				break;
			
			case EDIT_SITE_BRANDING:
				// A EditSiteBrandingEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof EditSiteBrandingEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = EditSiteBrandingEvent.registerEvent(eventBus, ((EditSiteBrandingEvent.Handler) eventHandler));
				}
				break;
			
			case FULL_UI_RELOAD:
				// An FullUIReloadEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof FullUIReloadEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = FullUIReloadEvent.registerEvent(eventBus, ((FullUIReloadEvent.Handler) eventHandler));
				}
				break;
				
			case GOTO_CONTENT_URL:
				// An GotoContentUrlEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof GotoContentUrlEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = GotoContentUrlEvent.registerEvent(eventBus, ((GotoContentUrlEvent.Handler) eventHandler));
				}
				break;
				
			case GOTO_MY_WORKSPACE:
				// An GotoMyWorkspaceEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof GotoMyWorkspaceEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = GotoMyWorkspaceEvent.registerEvent(eventBus, ((GotoMyWorkspaceEvent.Handler) eventHandler));
				}
				break;
				
			case GOTO_PERMALINK_URL:
				// An GotoPermalinkUrlEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof GotoPermalinkUrlEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = GotoPermalinkUrlEvent.registerEvent(eventBus, ((GotoPermalinkUrlEvent.Handler) eventHandler));
				}
				break;
				
			case GROUP_CREATED:
				// A GroupCreatedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof GroupCreatedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = GroupCreatedEvent.registerEvent(
																	eventBus,
																	((GroupCreatedEvent.Handler) eventHandler ) );
				}
				break;
				
			case GROUP_CREATION_STARTED:
				// A GroupCreationStartedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof GroupCreationStartedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = GroupCreationStartedEvent.registerEvent(
																			eventBus,
																			((GroupCreationStartedEvent.Handler) eventHandler ) );
				}
				break;
				
			case GROUP_CREATION_FAILED:
				// A GroupCreationFailedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof GroupCreationFailedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = GroupCreationFailedEvent.registerEvent(
																			eventBus,
																			((GroupCreationFailedEvent.Handler) eventHandler ) );
				}
				break;
				
			case GROUP_MODIFICATION_FAILED:
				// A GroupModificationFailedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof GroupModificationFailedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = GroupModificationFailedEvent.registerEvent(
																			eventBus,
																			((GroupModificationFailedEvent.Handler) eventHandler ) );
				}
				break;
				
			case GROUP_MODIFICATION_STARTED:
				// A GroupModificationStartedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof GroupModificationStartedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = GroupModificationStartedEvent.registerEvent(
																			eventBus,
																			((GroupModificationStartedEvent.Handler) eventHandler ) );
				}
				break;
				
			case GROUP_MODIFIED:
				// A GroupModifiedEvent  Can the event handler we  were given handle that?
				if ( eventHandler instanceof GroupModifiedEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = GroupModifiedEvent.registerEvent( eventBus, ((GroupModifiedEvent.Handler) eventHandler ) );
				}
				break;
				
			case INVOKE_HELP:
				// An InvokeHelpEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof InvokeHelpEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeHelpEvent.registerEvent(eventBus, ((InvokeHelpEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_CONFIGURE_FILE_SYNC_APP_DLG:
				// An InvokeConfigureFileSyncAppDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeConfigureFileSyncAppDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeConfigureFileSyncAppDlgEvent.registerEvent( eventBus, ((InvokeConfigureFileSyncAppDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_MANAGE_GROUPS_DLG:
				// An InvokeManageGroupsDlgEvent!  Can the event handler we were given handle that?
				if ( eventHandler instanceof InvokeManageGroupsDlgEvent.Handler)
				{
					handlerNotDefined = false;
					registrationHandler = InvokeManageGroupsDlgEvent.registerEvent( eventBus, ((InvokeManageGroupsDlgEvent.Handler) eventHandler));
				}
				break;
			
			case INVOKE_REPLY:
				// An InvokeReplyEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof InvokeReplyEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeReplyEvent.registerEvent(eventBus, ((InvokeReplyEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_SHARE:
				// An InvokeShareEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof InvokeShareEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeShareEvent.registerEvent(eventBus, ((InvokeShareEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_SIMPLE_PROFILE:
				// An InvokeSimpleProfileEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof InvokeSimpleProfileEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeSimpleProfileEvent.registerEvent(eventBus, ((InvokeSimpleProfileEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_SUBSCRIBE:
				// An InvokeSubscribeEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof InvokeSubscribeEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeSubscribeEvent.registerEvent(eventBus, ((InvokeSubscribeEvent.Handler) eventHandler));
				}
				break;
				
			case INVOKE_TAG:
				// An InvokeTagEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof InvokeTagEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = InvokeTagEvent.registerEvent(eventBus, ((InvokeTagEvent.Handler) eventHandler));
				}
				break;
				
			case LOGIN:
				// An LoginEvent!  Can the event handler we were given
				// handle that?
				if (eventHandler instanceof LoginEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = LoginEvent.registerEvent(eventBus, ((LoginEvent.Handler) eventHandler));
				}
				break;
				
			case LOGOUT:
				// An LogoutEvent!  Can the event handler we were given
				// handle that?
				if (eventHandler instanceof LogoutEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = LogoutEvent.registerEvent(eventBus, ((LogoutEvent.Handler) eventHandler));
				}
				break;
				
			case PRE_LOGOUT:
				// A PreLogoutEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof PreLogoutEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = PreLogoutEvent.registerEvent(eventBus, ((PreLogoutEvent.Handler) eventHandler));
				}
				break;
				
			case MARK_ENTRY_READ:
				// An MarkEntryReadEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof MarkEntryReadEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MarkEntryReadEvent.registerEvent(eventBus, ((MarkEntryReadEvent.Handler) eventHandler));
				}
				break;
				
			case MARK_ENTRY_UNREAD:
				// An MarkEntryUnreadEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof MarkEntryUnreadEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MarkEntryUnreadEvent.registerEvent(eventBus, ((MarkEntryUnreadEvent.Handler) eventHandler));
				}
				break;

			case MASTHEAD_HIDE:
				// A MastheadHideEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof MastheadHideEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MastheadHideEvent.registerEvent(eventBus, ((MastheadHideEvent.Handler) eventHandler));
				}
				break;
			
			case MASTHEAD_SHOW:
				// A MastheadShowEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof MastheadShowEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = MastheadShowEvent.registerEvent(eventBus, ((MastheadShowEvent.Handler) eventHandler));
				}
				break;
			
			case MENU_HIDE:
				// A MenuHideEvent.  Can the event handler we were given handle that?
				if ( eventHandler instanceof MenuHideEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = MenuHideEvent.registerEvent( eventBus, ((MenuHideEvent.Handler) eventHandler) );
				}
				break;
			
			case MENU_SHOW:
				// A MenuShowEvent.  Can the event handler we were given handle that?
				if ( eventHandler instanceof MenuShowEvent.Handler )
				{
					handlerNotDefined = false;
					registrationHandler = MenuShowEvent.registerEvent( eventBus, ((MenuShowEvent.Handler) eventHandler) );
				}
				break;
			
			case SEARCH_ADVANCED:
				// A SearchAdvancedEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof SearchAdvancedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SearchAdvancedEvent.registerEvent(eventBus, ((SearchAdvancedEvent.Handler) eventHandler));
				}
				break;
			
			case SEARCH_FIND_RESULTS:
				// A SearchFindResultsEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof SearchFindResultsEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SearchFindResultsEvent.registerEvent(eventBus, ((SearchFindResultsEvent.Handler) eventHandler));
				}
				break;
			
			case SEARCH_RECENT_PLACE:
				// A SearchRecentPlaceEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof SearchRecentPlaceEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SearchRecentPlaceEvent.registerEvent(eventBus, ((SearchRecentPlaceEvent.Handler) eventHandler));
				}
				break;
			
			case SEARCH_SAVED:
				// A SearchSavedEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof SearchSavedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SearchSavedEvent.registerEvent(eventBus, ((SearchSavedEvent.Handler) eventHandler));
				}
				break;
			
			case SEARCH_SIMPLE:
				// A SearchSimpleEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof SearchSimpleEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SearchSimpleEvent.registerEvent(eventBus, ((SearchSimpleEvent.Handler) eventHandler));
				}
				break;
			
			case SEARCH_TAG:
				// A SearchTagEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof SearchTagEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SearchTagEvent.registerEvent(eventBus, ((SearchTagEvent.Handler) eventHandler));
				}
				break;
			
			case SIDEBAR_HIDE:
				// A SidebarHideEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof SidebarHideEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SidebarHideEvent.registerEvent(eventBus, ((SidebarHideEvent.Handler) eventHandler));
				}
				break;
			
			case SIDEBAR_RELOAD:
				// A SidebarReloadEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof SidebarReloadEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SidebarReloadEvent.registerEvent(eventBus, ((SidebarReloadEvent.Handler) eventHandler));
				}
				break;
			
			case SIDEBAR_SHOW:
				// A SidebarShowEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof SidebarShowEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SidebarShowEvent.registerEvent(eventBus, ((SidebarShowEvent.Handler) eventHandler));
				}
				break;
			
			case SIZE_CHANGED:
				// A SizeChangedEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof SizeChangedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SizeChangedEvent.registerEvent(eventBus, ((SizeChangedEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_DELETE:
				// A TaskDeleteEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskDeleteEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskDeleteEvent.registerEvent(eventBus, ((TaskDeleteEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_HIERARCHY_DISABLED:
				// A TaskHierarchyDisabledEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof TaskHierarchyDisabledEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskHierarchyDisabledEvent.registerEvent(eventBus, ((TaskHierarchyDisabledEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_MOVE_DOWN:
				// A TaskMoveDownEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskMoveDownEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskMoveDownEvent.registerEvent(eventBus, ((TaskMoveDownEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_MOVE_LEFT:
				// A TaskMoveLeftEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskMoveLeftEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskMoveLeftEvent.registerEvent(eventBus, ((TaskMoveLeftEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_MOVE_RIGHT:
				// A TaskMoveRightEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskMoveRightEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskMoveRightEvent.registerEvent(eventBus, ((TaskMoveRightEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_MOVE_UP:
				// A TaskMoveUpEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskMoveUpEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskMoveUpEvent.registerEvent(eventBus, ((TaskMoveUpEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_NEW_TASK:
				// A TaskNewTaskEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskNewTaskEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskNewTaskEvent.registerEvent(eventBus, ((TaskNewTaskEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_PICK_DATE:
				// A TaskPickDateEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskPickDateEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskPickDateEvent.registerEvent(eventBus, ((TaskPickDateEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_PURGE:
				// A TaskPurgeEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskPurgeEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskPurgeEvent.registerEvent(eventBus, ((TaskPurgeEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_QUICK_FILTER:
				// A TaskQuickFilterEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof TaskQuickFilterEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskQuickFilterEvent.registerEvent(eventBus, ((TaskQuickFilterEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_SET_PERCENT_DONE:
				// A TaskSetPercentDoneEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof TaskSetPercentDoneEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskSetPercentDoneEvent.registerEvent(eventBus, ((TaskSetPercentDoneEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_SET_PRIORITY:
				// A TaskSetPriorityEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof TaskSetPriorityEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskSetPriorityEvent.registerEvent(eventBus, ((TaskSetPriorityEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_SET_STATUS:
				// A TaskSetStatusEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskSetStatusEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskSetStatusEvent.registerEvent(eventBus, ((TaskSetStatusEvent.Handler) eventHandler));
				}
				break;
			
			case TASK_VIEW:
				// A TaskViewEvent!  Can the event handler we were
				// given handle that?
				if (eventHandler instanceof TaskViewEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TaskViewEvent.registerEvent(eventBus, ((TaskViewEvent.Handler) eventHandler));
				}
				break;
			
			case TRACK_CURRENT_BINDER:
				// A TrackCurrentBinderEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof TrackCurrentBinderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TrackCurrentBinderEvent.registerEvent(eventBus, ((TrackCurrentBinderEvent.Handler) eventHandler));
				}
				break;
			
			case UNTRACK_CURRENT_BINDER:
				// A UntrackCurrentBinderEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof UntrackCurrentBinderEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = UntrackCurrentBinderEvent.registerEvent(eventBus, ((UntrackCurrentBinderEvent.Handler) eventHandler));
				}
				break;
			
			case UNTRACK_CURRENT_PERSON:
				// A UntrackCurrentPersonEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof UntrackCurrentPersonEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = UntrackCurrentPersonEvent.registerEvent(eventBus, ((UntrackCurrentPersonEvent.Handler) eventHandler));
				}
				break;
			
			case VIEW_ALL_ENTRIES:
				// A ViewAllEntriesEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ViewAllEntriesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewAllEntriesEvent.registerEvent(eventBus, ((ViewAllEntriesEvent.Handler) eventHandler));
				}
				break;
			
			case VIEW_CURRENT_BINDER_TEAM_MEMBERS:
				// A ViewCurrentBinderTeamMembersEvent!  Can the event
				// handler we were given handle that?
				if (eventHandler instanceof ViewCurrentBinderTeamMembersEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewCurrentBinderTeamMembersEvent.registerEvent(eventBus, ((ViewCurrentBinderTeamMembersEvent.Handler) eventHandler));
				}
				break;
			
			case VIEW_FOLDER_ENTRY:
				// A ViewFolderEntryEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ViewFolderEntryEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewFolderEntryEvent.registerEvent(eventBus, ((ViewFolderEntryEvent.Handler) eventHandler));
				}
				break;
			
			case VIEW_FORUM_ENTRY:
				// A ViewForumEntryEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ViewForumEntryEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewForumEntryEvent.registerEvent(eventBus, ((ViewForumEntryEvent.Handler) eventHandler));
				}
				break;
			
			case VIEW_RESOURCE_LIBRARY:
				// A ViewResourceLibraryEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof ViewResourceLibraryEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewResourceLibraryEvent.registerEvent(eventBus, ((ViewResourceLibraryEvent.Handler) eventHandler));
				}
				break;
			
			case VIEW_TEAMING_FEED:
				// A ViewTeamingFeedEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ViewTeamingFeedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewTeamingFeedEvent.registerEvent(eventBus, ((ViewTeamingFeedEvent.Handler) eventHandler));
				}
				break;
			
			case VIEW_UNREAD_ENTRIES:
				// A ViewUnreadEntriesEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof ViewUnreadEntriesEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = ViewUnreadEntriesEvent.registerEvent(eventBus, ((ViewUnreadEntriesEvent.Handler) eventHandler));
				}
				break;
			
			default:
			case UNDEFINED:
				// Whatever it is, we can't handle it!  Tell the user
				// about the problem.
				Window.alert(GwtTeaming.getMessages().eventHandling_UnhandledEvent(te.name(), EventHelper.class.getName()));
				handlerNotDefined = false;
				registrationHandler = null;
				break;
			}

			// Was the event handler we were given able to handle this
			// event?
			if (handlerNotDefined) {
				// No!  Tell the user about the problem.
				Window.alert(
					GwtTeaming.getMessages().eventHandling_UnhandledEvent(
						te.name(),
						eventHandler.getClass().getName()));
			}
			
			// Yes, the event handler we were given was able to handle
			// this event!  We're we able to register it and is the
			// caller wanting to keep track of them?
			else if ((null != registrationHandler) && returnRegisteredEventHandlers) {
				// Yes!  Add the HandlerRegistration to the caller's
				// list of them.
				final HandlerRegistration finalRegistrationHandler = registrationHandler;
				registeredEventHandlers.add(new HandlerRegistration() {
					@Override
					public void removeHandler() {
						finalRegistrationHandler.removeHandler();
					}
				});
			}
		}
	}
	
	public static void registerEventHandlers(SimpleEventBus eventBus, TeamingEvents[] eventsToBeRegistered, Object eventHandler) {
		// Always use the initial form of the method.
		registerEventHandlers(
			eventBus,
			eventsToBeRegistered,
			eventHandler,
			null);
	}

	/**
	 * Unregisters the HandlerRegistration's from a list of them.
	 * 
	 * @param registeredEventHandlers
	 */
	public static void unregisterEventHandlers(List<HandlerRegistration> registeredEventHandlers) {
		// If we were given a list of HandlerRegistration's...
		if ((null != registeredEventHandlers) && (!(registeredEventHandlers.isEmpty()))) {
			// ...scan them...
			for (HandlerRegistration hr: registeredEventHandlers) {
				// ...removing the handler for each...
				hr.removeHandler();
			}
			
			// ...and emptying the list.
			registeredEventHandlers.clear();
		}
	}

	/*
	 * Validates that the event handlers implemented by eventHandler
	 * are include in a TeamingEvents[].
	 * 
	 * The code actually only perform validation checking if the UI is
	 * in debug mode.  I decided to take this tact rather than always
	 * enumerate the enumeration values in the registerEventHandlers()
	 * for performance reasons.  I didn't want each class that
	 * registers events to pay the price of checking whether perhaps
	 * 100's of events were implemented.
	 * 
	 * This should NEVER be done in shipping code !!!
	 */
	private static void validateEvents(TeamingEvents[] eventsToCheck, Object eventHandler) {
		// If we're not validating things...
		if (!GwtClientHelper.isDebugUI()) {
			// ...bail.
			return;
		}

		// If we don't have any events to validate or a handler to
		// validate them against...
		int events = ((null == eventsToCheck) ? 0 : eventsToCheck.length);
		if ((0 == events) || (null == eventHandler)) {
			// ...bail.
			return;
		}

		// Scan the defined TeamingEvents looking for what's defined
		// and what should be defined.
		for (TeamingEvents te:  TeamingEvents.values() ) {
			boolean needsHandler = isTEInA(te, eventsToCheck);
			boolean hasHandler   = false;
			switch (te) {
			case ACTIVITY_STREAM:                   hasHandler = (eventHandler instanceof ActivityStreamEvent.Handler);               break;
			case ACTIVITY_STREAM_ENTER:             hasHandler = (eventHandler instanceof ActivityStreamEnterEvent.Handler);          break;
			case ACTIVITY_STREAM_EXIT:              hasHandler = (eventHandler instanceof ActivityStreamExitEvent.Handler);           break;
			
			case ADMINISTRATION:                    hasHandler = (eventHandler instanceof AdministrationEvent.Handler);               break;
			case ADMINISTRATION_EXIT:               hasHandler = (eventHandler instanceof AdministrationExitEvent.Handler);           break;
			case ADMINISTRATION_UPGRADE_CHECK:      hasHandler = (eventHandler instanceof AdministrationUpgradeCheckEvent.Handler);   break;
			
			case BROWSE_HIERARCHY:                  hasHandler = (eventHandler instanceof BrowseHierarchyEvent.Handler);              break;
			case BROWSE_HIERARCHY_EXIT:             hasHandler = (eventHandler instanceof BrowseHierarchyExitEvent.Handler);          break;
			
			case CHANGE_CONTEXT:                    hasHandler = (eventHandler instanceof ChangeContextEvent.Handler);                break;
			case CONTEXT_CHANGED:                   hasHandler = (eventHandler instanceof ContextChangedEvent.Handler);               break;
			case CONTEXT_CHANGING:                  hasHandler = (eventHandler instanceof ContextChangingEvent.Handler);              break;
			
			case EDIT_CURRENT_BINDER_BRANDING:      hasHandler = (eventHandler instanceof EditCurrentBinderBrandingEvent.Handler);    break;
			case EDIT_PERSONAL_PREFERENCES:         hasHandler = (eventHandler instanceof EditPersonalPreferencesEvent.Handler);      break;
			case EDIT_SITE_BRANDING:                hasHandler = (eventHandler instanceof EditSiteBrandingEvent.Handler);             break;
			
			case EDIT_LANDING_PAGE_PROPERTIES:      hasHandler = (eventHandler instanceof EditLandingPagePropertiesEvent.Handler);    break;
			
			case MASTHEAD_HIDE:                     hasHandler = (eventHandler instanceof MastheadHideEvent.Handler);                 break;
			case MASTHEAD_SHOW:                     hasHandler = (eventHandler instanceof MastheadShowEvent.Handler);                 break;
			
			case MENU_HIDE:                     	hasHandler = (eventHandler instanceof MenuHideEvent.Handler);                 	  break;
			case MENU_SHOW:                     	hasHandler = (eventHandler instanceof MenuShowEvent.Handler);                     break;

			case FULL_UI_RELOAD:                    hasHandler = (eventHandler instanceof FullUIReloadEvent.Handler);                 break;
			
			case GOTO_CONTENT_URL:                  hasHandler = (eventHandler instanceof GotoContentUrlEvent.Handler);               break;
			case GOTO_MY_WORKSPACE:                 hasHandler = (eventHandler instanceof GotoMyWorkspaceEvent.Handler);              break;
			case GOTO_PERMALINK_URL:                hasHandler = (eventHandler instanceof GotoPermalinkUrlEvent.Handler);             break;

			case GROUP_CREATED:                		hasHandler = (eventHandler instanceof GroupCreatedEvent.Handler);             	  break;
			case GROUP_CREATION_FAILED:       		hasHandler = (eventHandler instanceof GroupCreationFailedEvent.Handler);     	  break;
			case GROUP_CREATION_STARTED:       		hasHandler = (eventHandler instanceof GroupCreationStartedEvent.Handler);     	  break;
			case GROUP_MODIFICATION_FAILED:       	hasHandler = (eventHandler instanceof GroupModificationFailedEvent.Handler);      break;
			case GROUP_MODIFICATION_STARTED:       	hasHandler = (eventHandler instanceof GroupModificationStartedEvent.Handler);     break;
			case GROUP_MODIFIED:                	hasHandler = (eventHandler instanceof GroupModifiedEvent.Handler);             	  break;

			case INVOKE_HELP:                       hasHandler = (eventHandler instanceof InvokeHelpEvent.Handler);                   break;
			case INVOKE_CONFIGURE_FILE_SYNC_APP_DLG:hasHandler = (eventHandler instanceof InvokeConfigureFileSyncAppDlgEvent.Handler);break;
			case INVOKE_MANAGE_GROUPS_DLG:			hasHandler = (eventHandler instanceof InvokeManageGroupsDlgEvent.Handler);		  break;
			case INVOKE_REPLY:                      hasHandler = (eventHandler instanceof InvokeReplyEvent.Handler);                  break;
			case INVOKE_SHARE:                      hasHandler = (eventHandler instanceof InvokeShareEvent.Handler);                  break;
			case INVOKE_SIMPLE_PROFILE:             hasHandler = (eventHandler instanceof InvokeSimpleProfileEvent.Handler);          break;
			case INVOKE_SUBSCRIBE:                  hasHandler = (eventHandler instanceof InvokeSubscribeEvent.Handler);              break;
			case INVOKE_TAG:                        hasHandler = (eventHandler instanceof InvokeTagEvent.Handler);                    break;
			
			case LOGIN:                             hasHandler = (eventHandler instanceof LoginEvent.Handler);                        break;
			case LOGOUT:                            hasHandler = (eventHandler instanceof LogoutEvent.Handler);                       break;
			case PRE_LOGOUT:                        hasHandler = (eventHandler instanceof PreLogoutEvent.Handler);                    break;
			
			case MARK_ENTRY_READ:                   hasHandler = (eventHandler instanceof MarkEntryReadEvent.Handler);                break;
			case MARK_ENTRY_UNREAD:                 hasHandler = (eventHandler instanceof MarkEntryUnreadEvent.Handler);              break;
			
			case SEARCH_ADVANCED:                   hasHandler = (eventHandler instanceof SearchAdvancedEvent.Handler);               break;
			case SEARCH_FIND_RESULTS:               hasHandler = (eventHandler instanceof SearchFindResultsEvent.Handler);            break;
			case SEARCH_RECENT_PLACE:               hasHandler = (eventHandler instanceof SearchRecentPlaceEvent.Handler);            break;
			case SEARCH_SAVED:                      hasHandler = (eventHandler instanceof SearchSavedEvent.Handler);                  break;
			case SEARCH_SIMPLE:                     hasHandler = (eventHandler instanceof SearchSimpleEvent.Handler);                 break;
			case SEARCH_TAG:                        hasHandler = (eventHandler instanceof SearchTagEvent.Handler);                    break;
			
			case SIDEBAR_HIDE:                      hasHandler = (eventHandler instanceof SidebarHideEvent.Handler);                  break;
			case SIDEBAR_RELOAD:                    hasHandler = (eventHandler instanceof SidebarReloadEvent.Handler);                break;
			case SIDEBAR_SHOW:                      hasHandler = (eventHandler instanceof SidebarShowEvent.Handler);                  break;
			
			case SIZE_CHANGED:                      hasHandler = (eventHandler instanceof SizeChangedEvent.Handler);                  break;
			
			case TASK_DELETE:                       hasHandler = (eventHandler instanceof TaskDeleteEvent.Handler);                   break;
			case TASK_HIERARCHY_DISABLED:           hasHandler = (eventHandler instanceof TaskHierarchyDisabledEvent.Handler);        break;
			case TASK_MOVE_DOWN:                    hasHandler = (eventHandler instanceof TaskMoveDownEvent.Handler);                 break;
			case TASK_MOVE_LEFT:                    hasHandler = (eventHandler instanceof TaskMoveLeftEvent.Handler);                 break;
			case TASK_MOVE_RIGHT:                   hasHandler = (eventHandler instanceof TaskMoveRightEvent.Handler);                break;
			case TASK_MOVE_UP:                      hasHandler = (eventHandler instanceof TaskMoveUpEvent.Handler);                   break;
			case TASK_NEW_TASK:                     hasHandler = (eventHandler instanceof TaskNewTaskEvent.Handler);                  break;
			case TASK_PICK_DATE:                    hasHandler = (eventHandler instanceof TaskPickDateEvent.Handler);                 break;
			case TASK_PURGE:                        hasHandler = (eventHandler instanceof TaskPurgeEvent.Handler);                    break;
			case TASK_QUICK_FILTER:                 hasHandler = (eventHandler instanceof TaskQuickFilterEvent.Handler);              break;
			case TASK_SET_PERCENT_DONE:             hasHandler = (eventHandler instanceof TaskSetPercentDoneEvent.Handler);           break;
			case TASK_SET_PRIORITY:                 hasHandler = (eventHandler instanceof TaskSetPriorityEvent.Handler);              break;
			case TASK_SET_STATUS:                   hasHandler = (eventHandler instanceof TaskSetStatusEvent.Handler);                break;
			case TASK_VIEW:                         hasHandler = (eventHandler instanceof TaskViewEvent.Handler);                     break;
			
			case TRACK_CURRENT_BINDER:              hasHandler = (eventHandler instanceof TrackCurrentBinderEvent.Handler);           break;
			case UNTRACK_CURRENT_BINDER:            hasHandler = (eventHandler instanceof UntrackCurrentBinderEvent.Handler);         break;
			case UNTRACK_CURRENT_PERSON:            hasHandler = (eventHandler instanceof UntrackCurrentPersonEvent.Handler);         break;
			
			case VIEW_ALL_ENTRIES:                  hasHandler = (eventHandler instanceof ViewAllEntriesEvent.Handler);               break;
			case VIEW_CURRENT_BINDER_TEAM_MEMBERS:  hasHandler = (eventHandler instanceof ViewCurrentBinderTeamMembersEvent.Handler); break;
			case VIEW_FOLDER_ENTRY:                 hasHandler = (eventHandler instanceof ViewFolderEntryEvent.Handler);              break;
			case VIEW_FORUM_ENTRY:                  hasHandler = (eventHandler instanceof ViewForumEntryEvent.Handler);               break;
			case VIEW_RESOURCE_LIBRARY:             hasHandler = (eventHandler instanceof ViewResourceLibraryEvent.Handler);          break;
			case VIEW_TEAMING_FEED:                 hasHandler = (eventHandler instanceof ViewTeamingFeedEvent.Handler);              break;
			case VIEW_UNREAD_ENTRIES:               hasHandler = (eventHandler instanceof ViewUnreadEntriesEvent.Handler);            break;
			
			case UNDEFINED:
				// Ignore.
				continue;
				
			default:
				// Somebody forget to add a validation handler for
				// this!
				Window.alert(GwtTeaming.getMessages().eventHandling_Validation_NoValidator(te.name()));
				continue;
			}

			// Is something wrong with this (i.e., we need a handler
			// but don't have one or we have a handler that wasn't in
			// the list?
			if (needsHandler != hasHandler) {
				// Yes!  Tell the user about the problem.
				String teName = te.name();
				String className = eventHandler.getClass().getName();
				String error = "*???*";
				if      (needsHandler) error = GwtTeaming.getMessages().eventHandling_Validation_NoHandler(teName, className);
				else if (hasHandler)   error = GwtTeaming.getMessages().eventHandling_Validation_NotListed(teName, className);
				Window.alert(error);
			}
		}
	}
}
