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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to represent the recurrence information of an appointment
 * in a calendar for passing through a GWT RPC command.
 * 
 * @author drfoster@novell.com
 */
public class CalendarRecurrence implements IsSerializable {
	private List<Date[]> m_recurrenceDates;	// Each Data[] will contain two dates, the start and end dates for the recurrence.
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public CalendarRecurrence() {
		// Initialize the super class.
		super();
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public List<Date[]> getRecurrenceDates() {validateRecurrenceDates(); return    m_recurrenceDates;            }
	public boolean    isRecurrent()          {validateRecurrenceDates(); return (!(m_recurrenceDates.isEmpty()));}

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void addRecurrence(     Date[]       recurrence)              {validateRecurrenceDates(); m_recurrenceDates.add(recurrence);}
	public void addRecurrence(     Date         startDate, Date endDate) {addRecurrence(new Date[]{startDate, endDate});               }
	public void setRecurrenceDates(List<Date[]> recurrenceDates) {       m_recurrenceDates = recurrenceDates;                          }

	/*
	 * Validation methods.
	 */
	private void validateRecurrenceDates() {if (null == m_recurrenceDates) m_recurrenceDates = new ArrayList<Date[]>();}
}
