/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.InvokeSendEmailToTeamEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.HandlerRegistration;


/**
 * Class used to wrap the handling of sending an email to a list of the
 * contributors to a binder (or folder.)
 *  
 * @author drfoster@novell.com
 */
public class SendEmailToContributors
	implements
	// Event handlers implemented by this class.
		ContributorIdsReplyEvent.Handler
{
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private Long						m_binderId;					// The ID of the binder whose contributors are to be mailed to.
	private String						m_baseSendUrl;				// The base URL for sending email to contributors.  This gets patched with the contributor IDs to send to.
	private Timer						m_waitForContributorsTimer;	//
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.CONTRIBUTOR_IDS_REPLY,
	};
	
	/**
	 * Constructor method.
	 * 
	 * @param baseSendUrl
	 */
	public SendEmailToContributors(String baseSendUrl) {
		// Initialize the super class...
		super();

		// ...and store the parameters.
		m_baseSendUrl = baseSendUrl;
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
	
	/**
	 * Runs the send email to contributors popup using the contributors
	 * to the binder.
	 * 
	 * @param binderId
	 */
	public void doSend(Long binderId) {
		// Register the global events so we can request the
		// contributors to the binder.
		registerEvents();

		// ...store the binder ID...
		m_binderId = binderId;

		// ...setup a timer to wait for the contributors (just in case
		// ...nobody responds)...
		m_waitForContributorsTimer = new Timer() {
			@Override
			public void run() {
				// Clear the timer and unregister the event handlers...
				clearTimer();
				unregisterEvents();

				// ...and tell the user we couldn't get the contributors.
				Window.alert(GwtTeaming.getMessages().mainMenuErrorNoContributorsToEmail());
			}
		};
		m_waitForContributorsTimer.schedule(1000);	// 1 second.
		
		// ...and send the request for the contributors.
		GwtTeaming.fireEvent(new ContributorIdsRequestEvent(m_binderId));
	}

	/**
	 * Handles ContributorIdsRequestEvent's received by this class.
	 * 
	 * Implements the ContributorIdsRequestEvent.Handler.onContributorIdsRequest() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContributorIdsReply(final ContributorIdsReplyEvent event) {
		// Is this event targeted to the our binder?
		final Long eventBinderId = event.getBinderId();
		if (eventBinderId.equals(m_binderId)) {
			// Yes!  Clear the timer and unregister the event handlers.
			// We've got what we need.
			clearTimer();
			unregisterEvents();

			// Concatenate the contributor IDs into a string...
			StringBuffer contributors = new StringBuffer("");
			int c = 0;
			for (Long contributorId:  event.getContributorIds()) {
				if (0 < c) {
					contributors.append(",");
				}
				contributors.append(String.valueOf(contributorId));
				c += 1;
			}
			
			// ...and patch them into the URL and launch that in a
			// ...popup window.
			GwtClientHelper.jsLaunchUrlInWindow(
				GwtClientHelper.replace(
					m_baseSendUrl,
					InvokeSendEmailToTeamEvent.CONTRIBUTOR_IDS_PLACEHOLER,
					contributors.toString()),
				TeamManagementInfo.POPUP_WINDOW_NAME,
				TeamManagementInfo.POPUP_HEIGHT,
				TeamManagementInfo.POPUP_WIDTH);
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
