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

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * The VibeEventBase is used as the base class for all Vibe GWT based
 * events.
 * 
 * @author drfoster@novell.com
 */
public abstract class VibeEventBase<H extends EventHandler> extends GwtEvent<H> {
	/**
	 * Returns the TeamingEvents enumeration value corresponding to the
	 * event.
	 * 
	 * @return
	 */
	public abstract TeamingEvents getEventEnum();

	/**
	 * Returns true if the event should be dispatched to the given
	 * handler or false otherwise.
	 *
	 * We check whether an even should be dispatched to a handler by
	 * whether the event has a source that's an instance of
	 * EventsHandledBySourceMarker.  If it has a source that's an
	 * instance of that class, the handler and the source must be the
	 * same for the event to be dispatched.  If the event has no source
	 * or the source isn't an instance of EventsHandledBySourceMarker,
	 * it's always dispatched.
	 * 
	 * Note:  We currently check for the source to be the EXACT same
	 * object as the handler.  If, instead, we'd rather check that
	 * they're simply the same class, the comparison of the source with
	 * the handler should be changed to:
	 * 
	 *    if (!(evSource.getClass().equals(handler.getClass()))) {
	 *       ...
	 *    }
	 *    
	 * @param handler
	 * 
	 * @return
	 */
	final public boolean dispatchToThisHandler(EventHandler handler) {
		// If the event has a source...
		Object evSource = getSource();
		if (null != evSource) {
			// ...and the source implements EventsHandledBySourceMarker...
			if (evSource instanceof EventsHandledBySourceMarker) {
				// ...and the handler is not of the same object as the source...
				if (evSource != handler) {
					// ...we don't forward the event to the handler.
					return false;
				}
			}
		}
		
		// In all other cases, we forward the event to the handler.
		return true;
	}
}
