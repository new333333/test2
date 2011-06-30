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

import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;


/**
 * Helper methods to facilitate dealing with client side GWT events.
 * 
 * @author drfoster@novell.com
 */
public class EventHelper {
	// The following controls whether the validateEvents() method
	// actually does anything.  true -> It performs full validation,
	// false -> It performs no validate.
	//
	// I decided to take this tact rather than always enumerate the
	// enumeration values in the registerEventHandlers() for
	// performance reasons.  I didn't want each class that registers
	// events to pay the price of checking whether perhaps 100's of
	// events were implemented.
	//
	// Prior to shipping, this needs to be set false !!!
	private final static boolean VALIDATE_EVENT_HANDLERS = true;
	
	/*
	 * Constructor method.
	 */
	private EventHelper() {
		// Inhibits this class from being instantiated.
	}
	
	/**
	 * Given an event that requires no parameters, fires one.
	 * 
	 * @param event
	 */
	public static void fireSimpleEvent(TeamingEvents event) {
		switch (event) {
		case ACTIVITY_STREAM_EXIT:          ActivityStreamExitEvent.fireOne();         break;
		case ADMINISTRATION:                AdministrationEvent.fireOne();             break;
		case ADMINISTRATION_EXIT:           AdministrationExitEvent.fireOne();         break;
		case ADMINISTRATION_UPGRADE_CHECK:  AdministrationUpgradeCheckEvent.fireOne(); break;
		case BROWSE_HIERARCHY_EXIT:         BrowseHierarchyExitEvent.fireOne();        break;
		case CONTEXT_CHANGING:              ContextChangingEvent.fireOne();            break;
		case FULL_UI_RELOAD:                FullUIReloadEvent.fireOne();               break;
		case GOTO_MY_WORKSPACE:             GotoMyWorkspaceEvent.fireOne();            break;
		case LOGIN:                         LoginEvent.fireOne();                      break;
		case LOGOUT:                        LogoutEvent.fireOne();                     break;
		case MASTHEAD_HIDE:                 MastheadHideEvent.fireOne();               break;
		case MASTHEAD_SHOW:                 MastheadShowEvent.fireOne();               break;
		case SEARCH_ADVANCED:               SearchAdvancedEvent.fireOne();             break;
		case SIDEBAR_HIDE:                  SidebarHideEvent.fireOne();                break;
		case SIDEBAR_RELOAD:                SidebarReloadEvent.fireOne();              break;
		case SIDEBAR_SHOW:                  SidebarShowEvent.fireOne();                break;
			
		default:
		case UNDEFINED:
			Window.alert(GwtTeaming.getMessages().eventHandling_NonSimpleEvent(event.name(), EventHelper.class.getName()));
			break;
		}
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
			case TEAMING_ACTION:
				// A TeamingAction event!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof TeamingActionEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = TeamingActionEvent.registerEvent(eventBus, ((TeamingActionEvent.Handler) eventHandler));
				}
				break;
				
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
			
			case FULL_UI_RELOAD:
				// An FullUIReloadEvent!  Can the event handler we
				// were given handle that?
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
				
			case LOGIN:
				// An LoginEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof LoginEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = LoginEvent.registerEvent(eventBus, ((LoginEvent.Handler) eventHandler));
				}
				break;
				
			case LOGOUT:
				// An LogoutEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof LogoutEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = LogoutEvent.registerEvent(eventBus, ((LogoutEvent.Handler) eventHandler));
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
			
			case SEARCH_ADVANCED:
				// A SearchAdvancedEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof SearchAdvancedEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = SearchAdvancedEvent.registerEvent(eventBus, ((SearchAdvancedEvent.Handler) eventHandler));
				}
				break;
			
			case SEARCH_FIND_RESULTS:
				// A SearchFindResultsEvent!  Can the event handler we were
				// given handle that?
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
				Window.alert(GwtTeaming.getMessages().eventHandling_UnhandledEvent(te.name(), eventHandler.getClass().getName()));
			}
			
			// Yes, the event handler we were given was able to handle
			// this event!  We're we able to register it and is the
			// wanting to keep track of them?
			else if ((null != registrationHandler) && returnRegisteredEventHandlers) {
				// Yes!  Add its HandlerRegistration to the list of
				// them.
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
	 * Unregisters the HandlerRegistration objects from a list of them.
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
	 * Validate that the event handlers implemented by eventHandler are
	 * include in the events array.
	 */
	private static void validateEvents(TeamingEvents[] eventsToCheck, Object eventHandler) {
		// If we're not validating things...
		if (!VALIDATE_EVENT_HANDLERS) {
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

		// Scan the defined events looking for what's defined and
		// what should be defined.
		for (TeamingEvents te:  TeamingEvents.values() ) {
			boolean needsHandler = isTEInA(te, eventsToCheck);
			boolean hasHandler   = false;
			switch (te) {
			case TEAMING_ACTION:                hasHandler = (eventHandler instanceof TeamingActionEvent.Handler);              break;
			
			case ACTIVITY_STREAM:               hasHandler = (eventHandler instanceof ActivityStreamEvent.Handler);             break;
			case ACTIVITY_STREAM_ENTER:         hasHandler = (eventHandler instanceof ActivityStreamEnterEvent.Handler);        break;
			case ACTIVITY_STREAM_EXIT:          hasHandler = (eventHandler instanceof ActivityStreamExitEvent.Handler);         break;
			
			case ADMINISTRATION:                hasHandler = (eventHandler instanceof AdministrationEvent.Handler);             break;
			case ADMINISTRATION_EXIT:           hasHandler = (eventHandler instanceof AdministrationExitEvent.Handler);         break;
			case ADMINISTRATION_UPGRADE_CHECK:  hasHandler = (eventHandler instanceof AdministrationUpgradeCheckEvent.Handler); break;
			
			case BROWSE_HIERARCHY:              hasHandler = (eventHandler instanceof BrowseHierarchyEvent.Handler);            break;
			case BROWSE_HIERARCHY_EXIT:         hasHandler = (eventHandler instanceof BrowseHierarchyExitEvent.Handler);        break;
			
			case CONTEXT_CHANGED:               hasHandler = (eventHandler instanceof ContextChangedEvent.Handler);             break;
			case CONTEXT_CHANGING:              hasHandler = (eventHandler instanceof ContextChangingEvent.Handler);            break;
			
			case MASTHEAD_HIDE:                 hasHandler = (eventHandler instanceof MastheadHideEvent.Handler);               break;
			case MASTHEAD_SHOW:                 hasHandler = (eventHandler instanceof MastheadShowEvent.Handler);               break;
			
			case FULL_UI_RELOAD:                hasHandler = (eventHandler instanceof FullUIReloadEvent.Handler);               break;
			
			case GOTO_CONTENT_URL:              hasHandler = (eventHandler instanceof GotoContentUrlEvent.Handler);             break;
			case GOTO_MY_WORKSPACE:             hasHandler = (eventHandler instanceof GotoMyWorkspaceEvent.Handler);            break;
			case GOTO_PERMALINK_URL:            hasHandler = (eventHandler instanceof GotoPermalinkUrlEvent.Handler);           break;
			
			case LOGIN:                         hasHandler = (eventHandler instanceof LoginEvent.Handler);                      break;
			case LOGOUT:                        hasHandler = (eventHandler instanceof LogoutEvent.Handler);                     break;
			
			case SEARCH_ADVANCED:               hasHandler = (eventHandler instanceof SearchAdvancedEvent.Handler);             break;
			case SEARCH_FIND_RESULTS:           hasHandler = (eventHandler instanceof SearchFindResultsEvent.Handler);          break;
			case SEARCH_RECENT_PLACE:           hasHandler = (eventHandler instanceof SearchRecentPlaceEvent.Handler);          break;
			case SEARCH_SAVED:                  hasHandler = (eventHandler instanceof SearchSavedEvent.Handler);                break;
			case SEARCH_SIMPLE:                 hasHandler = (eventHandler instanceof SearchSimpleEvent.Handler);               break;
			case SEARCH_TAG:                    hasHandler = (eventHandler instanceof SearchTagEvent.Handler);                  break;
			
			case SIDEBAR_HIDE:                  hasHandler = (eventHandler instanceof SidebarHideEvent.Handler);                break;
			case SIDEBAR_RELOAD:                hasHandler = (eventHandler instanceof SidebarReloadEvent.Handler);              break;
			case SIDEBAR_SHOW:                  hasHandler = (eventHandler instanceof SidebarShowEvent.Handler);                break;
			
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
