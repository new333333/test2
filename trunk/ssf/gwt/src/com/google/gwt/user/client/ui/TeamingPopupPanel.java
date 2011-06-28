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
package com.google.gwt.user.client.ui;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.BrowseHierarchyExitEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;


/**
 * Implements an extension of GWT's PopupPanel so that Teaming can
 * expose the roll down animation feature from it.
 *  
 * @author drfoster@novell.com
 */
public class TeamingPopupPanel extends PopupPanel
	implements
	// EventBus handlers implemented by this class.
		BrowseHierarchyExitEvent.Handler
{
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
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
	public TeamingPopupPanel(boolean autoHide, boolean modal) {
		super(autoHide, modal);
		
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this);
	}
	
	public TeamingPopupPanel(boolean autoHide) {
		// Always use the initial form of the constructor.
		this(autoHide, false);
	}

	/**
	 * Sets the popup panel's animation type to roll down.
	 */
	public void setAnimationTypeToRollDown() {
		setAnimationType(PopupPanel.AnimationType.ROLL_DOWN);
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
		Widget widget = getWidget();
		if ((null != widget) && (widget instanceof WorkspaceTreeControl)) {
			hide();
		}
	}
	
	/*
	 * Overrides PopupPanel.onPreviewNativeEvent() to address an issue
	 * with PopupPanel's in FF closing with auto hide if the user
	 * scrolls them by clicking the browser's scroll bar.
	 * 
	 * Note:  This was implemented as a fix for Bugzilla bug#628120.
	 */
	protected void onPreviewNativeEvent(NativePreviewEvent nativeEvent) {
		// Is this an auto hide PopupPanel?
		if (isAutoHideEnabled()) {
			// Yes!  Is this mouse event?
		    Event event = Event.as(nativeEvent.getNativeEvent());
		    int type = event.getTypeInt();
		    if (0 != (type & Event.MOUSEEVENTS)) {
				// Yes!  Is it targeted to the root <HTML>?
			    EventTarget target = event.getEventTarget();
			    Element element = Element.as(target);
				String tagName = element.getTagName();
				if (tagName.equalsIgnoreCase("html")) {
					// Yes!  Then we don't want the PopupPanel to have it.
					// On FF, if it gets it, it won't let the user scroll
					// by clicking on the scroll bar.
					nativeEvent.consume();
					nativeEvent.cancel();
					
					return;
				}
		    }
		}
		
		// If we get here, we need to pass the event on through the
		// PopupPanel's handler.
		super.onPreviewNativeEvent(nativeEvent);
	}
}
