/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;


/**
 * Calculates start and end dates used to find and display events. Start date is
 * the first date of the month. End date is the last day of the month.
 * 
 * @author Pawel Nowicki
 * 
 */
public class OneMonthView extends AbstractIntervalView {

	public OneMonthView(Date dateInMonth, int firstDayOfWeek) {
		
		int firstDayOfWeekJoda;
		switch (firstDayOfWeek) {
	        case Calendar.SUNDAY: firstDayOfWeekJoda = DateTimeConstants.SUNDAY; break;
	        case Calendar.MONDAY: firstDayOfWeekJoda = DateTimeConstants.MONDAY; break;
	        case Calendar.TUESDAY: firstDayOfWeekJoda = DateTimeConstants.TUESDAY; break;
	        case Calendar.WEDNESDAY: firstDayOfWeekJoda = DateTimeConstants.WEDNESDAY; break;
	        case Calendar.THURSDAY: firstDayOfWeekJoda = DateTimeConstants.THURSDAY; break;
	        case Calendar.FRIDAY: firstDayOfWeekJoda = DateTimeConstants.FRIDAY; break;
	        case Calendar.SATURDAY: firstDayOfWeekJoda = DateTimeConstants.SATURDAY; break;
	        default: firstDayOfWeekJoda = DateTimeConstants.SUNDAY; break;
	    }

		User user = RequestContextHolder.getRequestContext().getUser();
		TimeZone timeZone = user.getTimeZone();
		
		DateMidnight firstDayOfMonth = (new DateMidnight(dateInMonth, DateTimeZone.forTimeZone(timeZone))).withDayOfMonth(1);
		DateMidnight firstDayOfNextMonth = firstDayOfMonth.plusMonths(1);
		this.interval = new Interval(firstDayOfMonth, firstDayOfNextMonth);
		
		DateMidnight monthViewStart = firstDayOfMonth.dayOfWeek().setCopy(firstDayOfWeekJoda);
		if (monthViewStart.getDayOfMonth() > 1 && monthViewStart.getDayOfMonth() < 8) {
			monthViewStart = monthViewStart.minusWeeks(1);
		}
		
		DateMidnight monthViewEnd = firstDayOfNextMonth;
			
		int daysInViewOfPrevMonth = Days.daysBetween(monthViewStart, firstDayOfMonth).getDays();
		int daysInViewToEndOfMonth = daysInViewOfPrevMonth + firstDayOfNextMonth.minusDays(1).getDayOfMonth();
		int missingDays = 7 - (daysInViewToEndOfMonth % 7);
		if (missingDays < 7) {
			monthViewEnd = monthViewEnd.plusDays(missingDays);
		}
		
		this.visibleInterval = new Interval(monthViewStart, monthViewEnd);
	}

}
