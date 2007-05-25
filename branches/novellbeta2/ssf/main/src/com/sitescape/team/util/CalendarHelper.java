/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class CalendarHelper {

	/**
	 * Returns calendar with the same like <code>calendar</code> time but in given <code>newTimeZone</code>.
	 * Routine creates a new calendar object so given <code>calendar</code> is untouched.
	 * 
	 * @param calendar
	 * @param newTimeZone
	 * @return
	 */
	public static Calendar convertToTimeZone(Calendar calendar, TimeZone newTimeZone) {
		if (calendar == null) {
			return null;
		}
		if (newTimeZone == null) { 
			newTimeZone = TimeZone.getTimeZone("GMT");
		}
		Calendar result = new GregorianCalendar(newTimeZone);
		result.setTimeInMillis(calendar.getTimeInMillis());
		return result;
	}
	
}
