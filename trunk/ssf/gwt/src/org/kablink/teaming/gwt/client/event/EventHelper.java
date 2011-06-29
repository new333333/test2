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
	/*
	 * Constructor method.
	 */
	private EventHelper() {
		// Inhibits this class from being instantiated.
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
				// An BrowseHierarchEvent!  Can the event handler we
				// were given handle that?
				if (eventHandler instanceof BrowseHierarchyEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = BrowseHierarchyEvent.registerEvent(eventBus, ((BrowseHierarchyEvent.Handler) eventHandler));
				}
				break;
			
			case BROWSE_HIERARCHY_EXIT:
				// An BrowseHierarchExitEvent!  Can the event handler
				// we were given handle that?
				if (eventHandler instanceof BrowseHierarchyExitEvent.Handler) {
					handlerNotDefined = false;
					registrationHandler = BrowseHierarchyExitEvent.registerEvent(eventBus, ((BrowseHierarchyExitEvent.Handler) eventHandler));
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
}
