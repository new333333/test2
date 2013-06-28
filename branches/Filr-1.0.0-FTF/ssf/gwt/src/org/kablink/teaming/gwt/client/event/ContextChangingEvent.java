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
package org.kablink.teaming.gwt.client.event;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent.ExitMode;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * The ContextChaingingEvent tells the UI that a context change is
 * about take place, but is not yet in progress.
 * 
 * @author drfoster@novell.com
 */
public class ContextChangingEvent extends VibeEventBase<ContextChangingEvent.Handler> {
	public static Type<Handler> TYPE = new Type<Handler>();

	/**
	 * Handler interface for this event.
	 */
	public interface Handler extends EventHandler {
		void onContextChanging(ContextChangingEvent event);
	}
	
	/*
	 * Class constructor.
	 * 
	 * Note:  This constructor is private to prevent it from being
	 * instantiated outside this class.  The ONLY way it should be
	 * used is via its static fireOne() method.  See that method for
	 * why that's the case.
	 */
	private ContextChangingEvent() {
		super();
	}
	
	/**
	 * Dispatches this event when one is triggered.
	 * 
	 * Implements the VibeEventBase.doDispatch() method.
	 * 
	 * @param handler
	 */
	@Override
	protected void doDispatch(Handler handler) {
		handler.onContextChanging(this);
	}
	
	/**
	 * Fires a new one of these events.
	 * 
	 * This method is the ONLY way to fire one of these events as it
	 * takes care of other 'pre-context switch' operations that must
	 * occur before a context switch takes place.
	 */
	public static void fireOne() {
		// Do we have access to to a main page (we won't if we're in an
		// IFRAME)?
		GwtMainPage mp = GwtTeaming.getMainPage();
		if (null != mp) {
			// Yes!  If we're currently running site administration...
			if (mp.isAdminActive()) {
				// ...close it as we won't be in the admin console if
				// ...we change contexts.
				AdministrationExitEvent.fireOne();
			}			
	
			// If we're currently in activity stream mode...
			if (mp.isActivityStreamActive()) {
				// ...close it as we won't be in activity steam mode if
				// ...we change contexts.
				ActivityStreamExitEvent.fireOne(ExitMode.EXIT_FOR_CONTEXT_SWITCH);
			}
			
			// Fire the event.
		    GwtTeaming.fireEvent(new ContextChangingEvent());
		}
		
		else {
			// No, we don't have access to a main page (we must be in
			// an IFRAME)!  Fire the event through the top level event
			// bus.  Note that this will end up going through the above
			// if branch when the callback though this JSNI method
			// occurs.
			GwtClientHelper.jsFireVibeEventOnMainEventBus(TeamingEvents.CONTEXT_CHANGING);
		}
	}
	
	/**
	 * Returns the GwtEvent.Type of this event.
	 *
	 * Implements GwtEvent.getAssociatedType()
	 * 
	 * @return
	 */
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * Returns the TeamingEvents enumeration value corresponding to
	 * this event.
	 * 
	 * Implements VibeBaseEvent.getEventEnum()
	 * 
	 * @return
	 */
	@Override
	public TeamingEvents getEventEnum() {
		return TeamingEvents.CONTEXT_CHANGING;
	}
		
	/**
	 * Registers this event on the given event bus and returns its
	 * HandlerRegistration.
	 * 
	 * @param eventBus
	 * @param handler
	 * 
	 * @return
	 */
	public static HandlerRegistration registerEvent(SimpleEventBus eventBus, Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
}
