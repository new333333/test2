/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.YearMonthDay;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.team.util.CalendarHelper;
import com.sitescape.team.web.WebKeys;

/**
 * Calculates start and end dates used to find and display events. Start date is
 * the first date of the month. End date is the last day of the month. All dates
 * are in GMT time zone.
 * 
 * @author Pawel Nowicki
 * 
 */
public class CalendarViewRangeDates {

	private Calendar startViewCal;

	private Calendar endViewCal;

	/* starts on first day of the week */
	private Calendar startViewExtWindow;

	/* ends on the last day of the week */
	private Calendar endViewExtWindow;

	public CalendarViewRangeDates(Date currentDate, int firstDayOfWeek) {
		super();

		User user = RequestContextHolder.getRequestContext().getUser();
		TimeZone timeZone = user.getTimeZone();

		// calculate the start and end of the range as defined by current date
		// and current view
		this.startViewCal = new GregorianCalendar(timeZone);
		this.endViewCal = new GregorianCalendar(timeZone);

		// Allow the pruning of events to extend beyond the prescribed dates so
		// we
		// can display a grid.
		this.startViewExtWindow = new GregorianCalendar(timeZone);
		this.endViewExtWindow = new GregorianCalendar(timeZone);

		// this trick zeros the low order parts of the time
		this.startViewCal.setTimeInMillis(0);
		this.startViewCal.setTime(currentDate);
		this.startViewExtWindow.setTime(startViewCal.getTime());
		this.endViewCal.setTimeInMillis(0);
		this.endViewCal.setTime(currentDate);
		this.endViewExtWindow.setTime(endViewCal.getTime());

		this.startViewCal.set(Calendar.DAY_OF_MONTH, 1);
		this.startViewExtWindow.setTime(startViewCal.getTime());
		this.startViewExtWindow.set(Calendar.DAY_OF_WEEK,
				firstDayOfWeek);
		// fix for Saturday
		if (this.startViewExtWindow.get(Calendar.MONTH) == this.startViewCal.get(Calendar.MONTH) &&
				this.startViewExtWindow.get(Calendar.DAY_OF_MONTH) > 1) {
			this.startViewExtWindow.add(Calendar.DATE, -7);
		}
		this.endViewCal.setTime(this.startViewCal.getTime());
		this.endViewCal.add(Calendar.MONTH, 1);
		this.endViewExtWindow.setTime(endViewCal.getTime());
		if (this.endViewExtWindow.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek) {
			this.endViewExtWindow.set(Calendar.DAY_OF_WEEK,
					firstDayOfWeek);
			// fix for Saturday
			if (this.endViewExtWindow.get(Calendar.MONTH) != this.endViewCal.get(Calendar.MONTH)) {
				this.endViewExtWindow.add(Calendar.DATE, 7);
			}
		}

		setMidnight(startViewCal);
		setMidnight(startViewExtWindow);
		setBeforeMidnight(endViewCal);
		setBeforeMidnight(endViewExtWindow);

		TimeZone zulu = TimeZoneHelper.getTimeZone("GMT");
		this.startViewCal = CalendarHelper.convertToTimeZone(this.startViewCal,
				zulu);
		this.endViewCal = CalendarHelper.convertToTimeZone(this.endViewCal,
				zulu);
		this.startViewExtWindow = CalendarHelper.convertToTimeZone(
				this.startViewExtWindow, zulu);
		this.endViewExtWindow = CalendarHelper.convertToTimeZone(
				this.endViewExtWindow, zulu);
	}

	private void setMidnight(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	private void setBeforeMidnight(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.MILLISECOND, -1);
	}

	public List getExtViewDayDates() {
		Calendar start = (Calendar) startViewExtWindow.clone();
		Calendar end = (Calendar) endViewExtWindow.clone();

		List result = new ArrayList();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
		Calendar dayStart = start;
		
		Calendar dayEnd = (Calendar)dayStart.clone();
		dayEnd.add(Calendar.DAY_OF_MONTH, 1);
		dayEnd.add(Calendar.MILLISECOND, -1);
		
		while (dayStart.getTimeInMillis() <= end.getTimeInMillis()) {
			result.add(new String[] {formatter.format(dayStart.getTime()), formatter.format(dayEnd.getTime())});
			
			dayStart.add(Calendar.DAY_OF_MONTH, 1);
			
			dayEnd = (Calendar)dayStart.clone();
			dayEnd.add(Calendar.DAY_OF_MONTH, 1);
			dayEnd.add(Calendar.MILLISECOND, -1);
		}

		return result;
	}

	public Calendar getEndViewCal() {
		return endViewCal;
	}

	public Calendar getEndViewExtWindow() {
		return endViewExtWindow;
	}

	public Calendar getStartViewCal() {
		return startViewCal;
	}

	public Calendar getStartViewExtWindow() {
		return startViewExtWindow;
	}

	public Map getCurrentDateMonthInfo() {
		Map result = new HashMap();

		User user = RequestContextHolder.getRequestContext().getUser();
		TimeZone timeZone = user.getTimeZone();

		Calendar startViewCalTemp = CalendarHelper.convertToTimeZone(
				startViewCal, timeZone);

		result.put("year", startViewCalTemp.get(Calendar.YEAR));
		result.put("month", startViewCalTemp.get(Calendar.MONTH));

		Calendar startViewExtWindowTemp = CalendarHelper.convertToTimeZone(
				startViewExtWindow, timeZone);
		result.put("beginView", startViewExtWindowTemp.getTime());

		Calendar endViewExtWindowTemp = CalendarHelper.convertToTimeZone(
				endViewExtWindow, timeZone);
		result.put("endView", endViewExtWindowTemp.getTime());
		
		int daysInMonthView = calculateDifference(
				endViewExtWindow.getTime(), startViewExtWindow
				.getTime());
		// there is a bug on some systems, I can't test it so small workaround
		if (daysInMonthView == 27 || daysInMonthView == 29) {
			daysInMonthView = 28; // == 4 weeks * 7
		}
		if (daysInMonthView == 34 || daysInMonthView == 36) {
			daysInMonthView = 35; // == 5 weeks * 7
		}	
		if (daysInMonthView == 41 || daysInMonthView == 43) {
			daysInMonthView = 42; // == 6 weeks * 7
		}
		result.put("numberOfDaysInView", daysInMonthView);

		return result;
	}

	public static int calculateDifference(Date a, Date b) {
		YearMonthDay firstDate = new DateTime(a).toYearMonthDay();
		YearMonthDay secondDate = new DateTime(b).toYearMonthDay();
				
		if (!firstDate.isBefore(secondDate)) {
			return Days.daysBetween(secondDate, firstDate).getDays();
		}
		return Days.daysBetween(firstDate, secondDate).getDays();
	}

	public boolean dateInView(Date dateToTest) {
		if (dateToTest == null) {
			return false;
		}
		long dateToTestInMilis = dateToTest.getTime();
		return startViewExtWindow.getTimeInMillis() < dateToTestInMilis && 
				dateToTestInMilis < endViewExtWindow.getTimeInMillis();
	}
	
	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @return true if at least a part of event is in view
	 */
	public boolean periodInView(long startDate, long endDate) {
		return (!(endDate < startViewExtWindow.getTimeInMillis() ||
				endViewExtWindow.getTimeInMillis() < startDate));
	}

}
