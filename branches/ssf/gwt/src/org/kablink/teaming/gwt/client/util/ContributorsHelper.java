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

package org.kablink.teaming.gwt.client.util;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;

import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.HandlerRegistration;


/**
 * Helper methods for working with a binder's contributors.
 *
 * @author drfoster@novell.com
 */
public class ContributorsHelper {
	// The following defines the maximum amount of time we'll wait for
	// a response to a request for contributors.  If we exceed this, we
	// simply give up.
	private final static int MAX_WAIT_FOR_CONTRIBUTORS = 1000;	// 1 second.

	/**
	 * Callback interface to return a List<Long> of contributor IDs. 
	 */
	public interface ContributorsCallback {
		public void onFailure();
		public void onSuccess(List<Long> contributorIds);
	}
	
	/*
	 * Inner class used that wraps the event handling required to
	 * request a binder's contributors. 
	 */
	private static class GetContributors
		implements
		// Event handlers implemented by this class.
			ContributorIdsReplyEvent.Handler
	{
		private ContributorsCallback		m_callback;					// Callback interface to return the contributor IDs.
		private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
		private Long						m_binderId;					// The binder whose contributor list is being requested.
		private Timer						m_waitForContributorsTimer;	// A timer used to control how long we wait for somebody to respond to the request for contributors.
		
		// The following defines the TeamingEvents that are handled by
		// this class.  See EventHelper.registerEventHandlers() for how
		// this array is used.
		private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
			TeamingEvents.CONTRIBUTOR_IDS_REPLY,
		};

		/*
		 * Constructor method.
		 */
		private GetContributors() {
			super();
		}
		
		/*
		 * Cancels and forgets about the timer waiting for contributors.
		 */
		private void clearTimer() {
			if (null != m_waitForContributorsTimer) {
				m_waitForContributorsTimer.cancel();
				m_waitForContributorsTimer = null;
			}
		}
		
		/*
		 * Uses the callback to return a List<Long> of the contributors
		 * from a binder.
		 */
		private void getContributors(Long binderId, ContributorsCallback callback) {
			// Register the global events so we can request the
			// contributors to the binder.
			registerEvents();

			// ...store the parameters...
			m_binderId = binderId;
			m_callback = callback;
			
			// ...setup a timer to wait for the contributors (just in case
			// ...nobody responds)...
			m_waitForContributorsTimer = new Timer() {
				@Override
				public void run() {
					// Clear the timer and unregister the event handlers...
					clearTimer();
					unregisterEvents();

					// ...and tell the callback that we couldn't get
					// ...the contributors.
					m_callback.onFailure();
				}
			};
			m_waitForContributorsTimer.schedule(MAX_WAIT_FOR_CONTRIBUTORS);
			
			// ...and send the request for the contributors.
			GwtTeaming.fireEvent(new ContributorIdsRequestEvent(m_binderId));
		}
		
		/**
		 * Handles ContributorIdsReplyEvent's received by this class.
		 * 
		 * Implements the ContributorIdsReplyEvent.Handler.onContributorIdsReply()
		 * method.
		 * 
		 * @param event
		 */
		@Override
		public void onContributorIdsReply(final ContributorIdsReplyEvent event) {
			// Is this event targeted to the our binder?
			final Long eventBinderId = event.getBinderId();
			if (eventBinderId.equals(m_binderId)) {
				// Yes!  Clear the timer and unregister the event
				// handlers since we've got what we need...
				clearTimer();
				unregisterEvents();

				// ...and send the contributors list back through the
				// ...callback.
				m_callback.onSuccess(event.getContributorIds());
			}
		}

		/*
		 * Registers any global event handlers that need to be registered.
		 */
		private void registerEvents() {
			// If we having allocated a list to track events we've
			// registered yet...
			if (null == m_registeredEventHandlers) {
				// ...allocate one now.
				m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
			}

			// If the list of registered events is empty...
			if (m_registeredEventHandlers.isEmpty()) {
				// ...register the events.
				EventHelper.registerEventHandlers(
					GwtTeaming.getEventBus(),
					m_registeredEvents,
					this,
					m_registeredEventHandlers);
			}
		}

		/*
		 * Unregisters any global event handlers that may be registered.
		 */
		private void unregisterEvents() {
			// If we have a non-empty list of registered events...
			if ((null != m_registeredEventHandlers) && (!(m_registeredEventHandlers.isEmpty()))) {
				// ...unregister them.  (Note that this will also empty the
				// ...list.)
				EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
			}
		}
	}
	
	/*
	 * Constructor method. 
	 */
	private ContributorsHelper() {
		// Inhibits this class from being instantiated.
	}
	
	/**
	 * Uses the callback to return a List<Long> of the contributors
	 * from a binder.
	 * 
	 * @param binderId
	 * @param callback
	 */
	public static void getContributors(Long binderId, ContributorsCallback callback) {
		GetContributors gc = new GetContributors();
		gc.getContributors(binderId, callback);
	}
}
