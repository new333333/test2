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
		Calendar result = new GregorianCalendar(newTimeZone);
		result.setTimeInMillis(calendar.getTimeInMillis());
		return result;
	}
	
}
