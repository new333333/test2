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

	public CalendarViewRangeDates(Date currentDate) {
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
				this.startViewExtWindow.getFirstDayOfWeek());
		this.endViewCal.setTime(this.startViewCal.getTime());
		this.endViewCal.add(Calendar.MONTH, 1);
		this.endViewExtWindow.setTime(endViewCal.getTime());
		if (this.endViewExtWindow.get(Calendar.DAY_OF_WEEK) != this.endViewExtWindow
				.getFirstDayOfWeek()) {
			this.endViewExtWindow.set(Calendar.DAY_OF_WEEK,
					this.endViewExtWindow.getFirstDayOfWeek());
			this.endViewExtWindow.add(Calendar.DATE, 7);
		}

		setMidnight(startViewCal);
		setMidnight(startViewExtWindow);
		setBeforeMidnight(endViewCal);
		setBeforeMidnight(endViewExtWindow);

		TimeZone zulu = TimeZone.getTimeZone("GMT");
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

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Calendar date = start;

		while (date.getTimeInMillis() <= end.getTimeInMillis()) {
			result.add(formatter.format(date.getTime()));
			date.add(Calendar.DAY_OF_MONTH, 1);
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
		Map beginView = new HashMap();
		beginView.put("year", startViewExtWindowTemp.get(Calendar.YEAR));
		beginView.put("month", startViewExtWindowTemp.get(Calendar.MONTH));
		beginView.put("dayOfMonth", startViewExtWindowTemp
				.get(Calendar.DAY_OF_MONTH));
		result.put("beginView", beginView);

		Calendar endViewExtWindowTemp = CalendarHelper.convertToTimeZone(
				endViewExtWindow, timeZone);
		Map endView = new HashMap();
		endView.put("year", endViewExtWindowTemp.get(Calendar.YEAR));
		endView.put("month", endViewExtWindowTemp.get(Calendar.MONTH));
		endView.put("dayOfMonth", endViewExtWindowTemp
				.get(Calendar.DAY_OF_MONTH));
		result.put("endView", endView);

		result.put("numberOfDaysInView", calculateDifference(
				endViewExtWindowTemp.getTime(), startViewExtWindowTemp
						.getTime()));

		return result;
	}

	public static int calculateDifference(Date a, Date b) {
		DateMidnight firstDate = new DateMidnight(a);
		DateMidnight secondDate = new DateMidnight(b);
				
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
	public boolean eventInView(long startDate, long endDate) {
		return (!(endDate < startViewExtWindow.getTimeInMillis() ||
				endViewExtWindow.getTimeInMillis() < startDate));
	}

}
