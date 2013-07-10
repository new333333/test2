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
package org.kablink.teaming.gwt.client.workspacetree;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.BrowseHierarchyExitEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;

import com.google.gwt.user.client.ui.TeamingPopupPanel;


/**
 * Class used to drive the display of the WorkspaceTreeControl,
 * typically used for Teaming's bread crumbs.
 * 
 * @author drfoster@novell.com
 *
 */
public class BreadcrumbTreePopup extends TeamingPopupPanel
	implements
	// Event handlers implemented by this class.
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
	public BreadcrumbTreePopup(boolean autoHide, boolean modal) {
		super(autoHide, modal);
		
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this);
	}
	
	public BreadcrumbTreePopup(boolean autoHide) {
		// Always use the initial form of the constructor.
		this(autoHide, false);
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
}
