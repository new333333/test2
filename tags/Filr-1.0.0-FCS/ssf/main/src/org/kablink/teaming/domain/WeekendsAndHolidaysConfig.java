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
package org.kablink.teaming.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.StringUtil;

/**
 * Encapsulates weekend and holiday schedule information.  A component of
 * zoneConfig.
 *  
 * @author drfoster@novell.com
 */
public class WeekendsAndHolidaysConfig  {
	private String m_holidays    = null;	// Database size:  4096.  Allows for 256 holidays.
	private String m_weekendDays = null;	// Database size:   128.
	
	/**
	 * Class constructors.
	 */
	public WeekendsAndHolidaysConfig() {
	}
	
	public WeekendsAndHolidaysConfig(WeekendsAndHolidaysConfig weekendsAndHolidaysConfig) {
		if (null != weekendsAndHolidaysConfig) {
			m_holidays    = weekendsAndHolidaysConfig.m_holidays;
			m_weekendDays = weekendsAndHolidaysConfig.m_weekendDays;
		}
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public String getHolidays()    {return m_holidays;   }
	public String getWeekendDays() {return m_weekendDays;}

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setHolidays(   String holidays)    {m_holidays    = holidays;   }
	public void setWeekendDays(String weekendDays) {m_weekendDays = weekendDays;}

	/**
	 * Returns the holidays as List<Date>.
	 * 
	 * @return
	 */
	public List<Date> getHolidayList() {
		List<Date> reply = new ArrayList<Date>();
		
		if (MiscUtil.hasString(m_holidays)) {
			String[] holidays = StringUtil.unpack(m_holidays);
			for (int i = 0; i < holidays.length; i += 1) {
				reply.add(new Date(Long.parseLong(holidays[i])));
			}
			
			Collections.sort(reply);
		}
		
		return reply;
	}

	/**
	 * Returns the weekend days as a List<Integer>.
	 * 
	 * @return
	 */
	public List<Integer> getWeekendDaysList() {
		List<Integer> reply = new ArrayList<Integer>();
		
		if (MiscUtil.hasString(m_weekendDays)) {
			String[] weekendDays = StringUtil.unpack(m_weekendDays);
			for (int i = 0; i < weekendDays.length; i += 1) {
				reply.add(Integer.parseInt(weekendDays[i]));
			}
		}
		
		return reply;
	}

	/**
	 * Set the holidays from a List<Date>.
	 * 
	 * @param holidays
	 */
	public void setHolidaysFromList(List<Date> holidays) {
		if (null == holidays) {
			m_holidays = null;
			return;
		}
		
		String[] holidaysStrings = new String[holidays.size()];
		int i = 0;
		for (Date holiday:  holidays) {
			holidaysStrings[i++] = String.valueOf(holiday.getTime());
		}
		
		m_holidays = StringUtil.pack(holidaysStrings);
	}

	/**
	 * Set the weekend days from a List<Integer>.
	 * 
	 * @param weekendDays
	 */
	public void setWeekendDaysFromList(List<Integer> weekendDays) {
		if (null == weekendDays) {
			m_weekendDays = null;
			return;
		}
		
		String[] weekendDaysStrings = new String[weekendDays.size()];
		int i = 0;
		for (Integer weekendDay:  weekendDays) {
			weekendDaysStrings[i++] = String.valueOf(weekendDay.intValue());
		}
		
		m_weekendDays = StringUtil.pack(weekendDaysStrings);
	}
}
