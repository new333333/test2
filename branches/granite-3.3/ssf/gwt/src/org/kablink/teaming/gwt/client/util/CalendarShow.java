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
package org.kablink.teaming.gwt.client.util;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Enumeration value that specifies the what events are to be shown in
 * a calendar.
 * 
 * @author drfoster@novell.com
 */
public enum CalendarShow implements IsSerializable {
	VIRTUAL,
	
	PHYSICAL_EVENTS,
	PHYSICAL_BY_CREATION,
	PHYSICAL_BY_ACTIVITY;
	
	/**
	 * Converts the ordinal value of a CardinalShow to its enumeration
	 * equivalent.
	 * 
	 * @param eventOrdinal
	 * 
	 * @return
	 */
	public static CalendarShow getEnum(int eventOrdinal) {
		CalendarShow event;
		try {
			event = CalendarShow.values()[eventOrdinal];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			event = CalendarShow.PHYSICAL_EVENTS;
		}
		return event;
	}

	/**
	 * Returns true if this CalendarShow value is a physical by date
	 * show and false otherwise.
	 * 
	 * @return
	 */
	public boolean isPhysicalByDate() {
		return (this.equals(PHYSICAL_BY_ACTIVITY) || this.equals(PHYSICAL_BY_CREATION));
	}
}
