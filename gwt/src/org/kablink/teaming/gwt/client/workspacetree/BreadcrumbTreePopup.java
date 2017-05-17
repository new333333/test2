/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.workspacetree;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.BrowseHierarchyExitEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Class used to drive the display of the WorkspaceTreeControl,
 * typically used for Teaming's bread crumbs.
 * 
 * @author drfoster@novell.com
 */
public class BreadcrumbTreePopup extends TeamingPopupPanel
	implements
		// Event handlers implemented by this class.
		BrowseHierarchyExitEvent.Handler
{
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.

	// The following specifies the adjustment applied to height of the
	// content panel that's applied to the bread crumb tree popup.
	private final static int CONTENT_HEIGHT_ADJUST	= 8;
	
	// The following defines the height to use for the bread crumb tree
	// popup if the height of the main content area could not be
	// determined.
	private final static int DEFAULT_HEIGHT	= 500;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		// Miscellaneous events.
		TeamingEvents.BROWSE_HIERARCHY_EXIT,
	};
	
	/**
	 * Creates an empty popup panel, specifying its auto-hide and modal
	 * properties.
	 * 
	 * @param autoHide
	 * @param modal
	 */
	public BreadcrumbTreePopup(boolean autoHide, boolean modal) {
		// Initialize the super class.
		super(autoHide, modal);
		
		addStyleName("breadCrumbTreePopup");
	}
	
	public BreadcrumbTreePopup(boolean autoHide) {
		// Always use the initial form of the constructor.
		this(autoHide, false);
	}

	/**
	 * Called when the panel is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Attach the widget, register the event handlers...
		super.onAttach();
		registerEvents();
		
		// ...and set the maximum height of the popup.
		setBreadcrumbTreeHeight(GwtTeaming.getMainPage().getContentControlHeight());
	}
	
	/**
	 * Called when the panel is detached.
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

	/**
	 * Handles BrowseHierarchyExitEvent's received by this class.
	 * 
	 * Implements the BrowseHierarchyExitEvent.Handler.onBrowseHierarchyExit() method.
	 * 
	 * @param event
	 */
	@Override
	public void onBrowseHierarchyExit( final BrowseHierarchyExitEvent event ) {
		hide();
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
				REGISTERED_EVENTS,
				this,
				m_registeredEventHandlers);
		}
	}

	/*
	 * Sets the effective height of the bread crumb tree.
	 */
	private void setBreadcrumbTreeHeight(int height) {
		if      (0                     >= height) height  = DEFAULT_HEIGHT;
		else if (CONTENT_HEIGHT_ADJUST <  height) height -= CONTENT_HEIGHT_ADJUST;
	    getElement().getStyle().setProperty("maxHeight", height + "px");
	}
	
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
