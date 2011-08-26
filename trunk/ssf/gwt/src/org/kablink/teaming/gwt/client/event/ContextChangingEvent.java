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

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent.ExitMode;
import org.kablink.teaming.gwt.client.widgets.MainMenuControl;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * The ContextChaingingEvent tells the UI that a context change is
 * about take place, but is not yet in process.
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
	 * Implements GwtEvent.dispatch()
	 * 
	 * @param handler
	 */
	@Override
	protected void dispatch(Handler handler) {
		handler.onContextChanging(this);
	}
	
	/**
	 * Fires a new one of these events.
	 */
	public static void fireOne() {
		// We'll never been in activity steam mode if we change
		// context.  Exit it if we're in it.
		GwtTeaming.fireEvent(new ActivityStreamExitEvent(ExitMode.EXIT_FOR_CONTEXT_SWITCH));
		
		// Fire the event.
		GwtTeaming.fireEvent(new ContextChangingEvent());
		
		// Do we have access to the main menu off the main page?
		GwtMainPage mp = GwtTeaming.getMainPage();
		MainMenuControl mmc = ((null == mp) ? null : mp.getMainMenu());
		if (null != mmc) {
			// Yes!  Tell it to clear its context menus.  Note that we
			// do this directly (synchronously) rather than off the
			// ContextChangingEvent because that event is handled
			// asynchronously and may not happen until too late in
			// the flow to effectively clear the context menus BEFORE
			// the context switch actually occurs.
			mmc.clearContextMenus();
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
