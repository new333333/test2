/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Base class for constructing report composites.
 * 
 * @author drfoster@novell.com
 */
public abstract class ReportCompositeBase extends Composite {
	protected boolean					m_isFilr;					// true -> We're running as Filr.  false -> We're not.
	protected GwtTeamingMessages		m_messages;					// Access to Vibe's messages.
	private   List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	protected SpinnerPopup				m_busySpinner;              // A popup that contains a spinner class that extend this can use when they're busy.
	protected VibeFlowPanel				m_rootContent;				// The main panel containing the composite's content.

	/**
	 * Constructor method.
	 */
	public ReportCompositeBase() {
		// Initialize the super class...
		super();
	
		// ...initialize everything else...
		m_isFilr      = GwtClientHelper.isLicenseFilr();
		m_messages    = GwtTeaming.getMessages();
		m_busySpinner = new SpinnerPopup();
		
		// ...and create the composite's content.
		createContent();
		initWidget(m_rootContent);
	}

	/**
	 * Constructs and returns an InlineLabel, optionally truncated to a
	 * given length and optionally assigned styles.
	 * 
	 * @param data
	 * @param length
	 * @param styles
	 * 
	 * @return
	 */
	public InlineLabel buildInlineLabel(String data, int length, String styles) {
		if (0 < length) {
			if (data.length() > length) {
				data = (data.substring(0, length) + "...");
			}
		}
		InlineLabel reply = new InlineLabel(data);
		if (GwtClientHelper.hasString(styles)) {
			reply.addStyleName(styles);
		}
		return reply;
	}
	
	public InlineLabel buildInlineLabel(String data) {
		// Always use the initial form of the method.
		return buildInlineLabel(data, (-1), null);
	}

	public InlineLabel buildInlineLabel(String data, int length) {
		// Always use the initial form of the method.
		return buildInlineLabel(data, length, null);
	}

	public InlineLabel buildInlineLabel(String data, String styles) {
		// Always use the initial form of the method.
		return buildInlineLabel(data, (-1), styles);
	}

	/**
	 * Creates the content for the report.
	 * 
	 * Extending classes should override this to create their own
	 * content.
	 */
	public void createContent() {
		// Create the main content panel for the composite... 
		m_rootContent = new VibeFlowPanel();
		m_rootContent.addStyleName("vibe-reportCompositeBase-rootPanel");

	}

	/**
	 * Returns a TeamingEvents[] of the events to be registered for the
	 * composite.
	 * 
	 * Extending class must return the events they need registered for
	 * their composite to operate.
	 * 
	 * @return
	 */
	public abstract TeamingEvents[] getRegisteredEvents();
	
	/**
	 * Called when the composite is attached to the document.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Attach the widget and register the event handlers...
		super.onAttach();
		registerEvents();
		
		// ...and reset the report.
		m_busySpinner.hide();
		resetReport();
	}
	
	/**
	 * Called when the composite is detached from the document.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
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

		// Does the extender want any events registered?
		TeamingEvents[] registeredEvents = getRegisteredEvents();
		if ((null != registeredEvents) && (0 < registeredEvents.length)) {
			// Yes!  If the list of registered events is empty...
			if (m_registeredEventHandlers.isEmpty()) {
				// ...register the events.
				EventHelper.registerEventHandlers(
					GwtTeaming.getEventBus(),
					registeredEvents,
					this,
					m_registeredEventHandlers);
			}
		}
	}

	/**
	 * Resets the reports content.
	 * 
	 * Extending classes must implement this and reset their report's
	 * content when called.
	 */
	public abstract void resetReport();
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if (GwtClientHelper.hasItems(m_registeredEventHandlers)) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
}
