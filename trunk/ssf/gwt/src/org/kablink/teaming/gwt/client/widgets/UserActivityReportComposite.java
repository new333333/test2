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
package org.kablink.teaming.gwt.client.widgets;

import org.kablink.teaming.gwt.client.event.TeamingEvents;

/**
 * Composite that runs a user activity report.
 * 
 * @author drfoster@novell.com
 */
public class UserActivityReportComposite extends ReportCompositeBase {
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
	};
	
	/**
	 * Constructor method.
	 */
	public UserActivityReportComposite() {
		// Simply initialize the super class.
		super();
	}
	
	/**
	 * Creates the content for the report.
	 * 
	 * Overrides the ReportCompositeBase.createContent() method.
	 */
	@Override
	public void createContent() {
		// Let the super class create the initial base content.
		super.createContent();
		
//!		...this needs to be implemented...
		m_rootContent.getElement().setInnerText("UserActivityReportComposite:  ...this needs to be implemented...");
	}
	
	/**
	 * Returns a TeamingEvents[] of the events to be registered for the
	 * composite.
	 *
	 * Implements the ReportCompositeBase.getRegisteredEvents() method.
	 * 
	 * @return
	 */
	@Override
	public TeamingEvents[] getRegisteredEvents() {
		return REGISTERED_EVENTS;
	}
	
	/**
	 * Resets the reports content.
	 * 
	 * Implements the ReportCompositeBase.resetReport() method.
	 */
	@Override
	public void resetReport() {
//!		...this needs to be implemented...
	}
}
