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
		result.put("numberOfDays", startViewCalTemp
				.getActualMaximum(Calendar.DAY_OF_MONTH));

		SimpleDateFormat sdfMonthShortName = new SimpleDateFormat("MMM");
		sdfMonthShortName.setTimeZone(timeZone);
		result.put("shortName", sdfMonthShortName.format(startViewCalTemp
				.getTime()));

		SimpleDateFormat sdfMonthName = new SimpleDateFormat("MMMM");
		sdfMonthName.setTimeZone(timeZone);
		result.put("name", sdfMonthName.format(startViewCalTemp.getTime()));

		startViewCalTemp.set(Calendar.DAY_OF_MONTH, 1);
		result.put("beginsOnDayOfWeek", startViewCalTemp
				.get(Calendar.DAY_OF_WEEK));

		Calendar startViewExtWindowTemp = CalendarHelper.convertToTimeZone(
				startViewExtWindow, timeZone);
		Map beginView = new HashMap();
		beginView.put("year", startViewExtWindowTemp.get(Calendar.YEAR));
		beginView.put("month", startViewExtWindowTemp.get(Calendar.MONTH));
		beginView.put("dayOfWeek", startViewExtWindowTemp
				.get(Calendar.DAY_OF_WEEK));
		beginView.put("dayOfMonth", startViewExtWindowTemp
				.get(Calendar.DAY_OF_MONTH));
		beginView.put("daysInMonth", startViewExtWindowTemp
				.getActualMaximum(Calendar.DAY_OF_MONTH));
		beginView.put("shortName", sdfMonthShortName
				.format(startViewExtWindowTemp.getTime()));
		beginView.put("name", sdfMonthName.format(startViewExtWindowTemp
				.getTime()));
		result.put("beginView", beginView);

		Calendar endViewExtWindowTemp = CalendarHelper.convertToTimeZone(
				endViewExtWindow, timeZone);
		Map endView = new HashMap();
		endView.put("year", endViewExtWindowTemp.get(Calendar.YEAR));
		endView.put("month", endViewExtWindowTemp.get(Calendar.MONTH));
		endView
				.put("dayOfWeek", endViewExtWindowTemp
						.get(Calendar.DAY_OF_WEEK));
		endView.put("dayOfMonth", endViewExtWindowTemp
				.get(Calendar.DAY_OF_MONTH));
		endView.put("daysInMonth", endViewExtWindowTemp
				.getActualMaximum(Calendar.DAY_OF_MONTH));
		endView.put("shortName", sdfMonthShortName.format(endViewExtWindowTemp
				.getTime()));
		endView
				.put("name", sdfMonthName
						.format(endViewExtWindowTemp.getTime()));

		result.put("endView", endView);

		result.put("numberOfDaysInView", calculateDifference(
				endViewExtWindowTemp.getTime(), startViewExtWindowTemp
						.getTime()));

		return result;
	}

	public static int calculateDifference(Date a, Date b) {
		int tempDifference = 0;
		int difference = 0;
		Calendar earlier = Calendar.getInstance();
		Calendar later = Calendar.getInstance();

		if (a.compareTo(b) < 0) {
			earlier.setTime(a);
			later.setTime(b);
		} else {
			earlier.setTime(b);
			later.setTime(a);
		}

		while (earlier.get(Calendar.YEAR) != later.get(Calendar.YEAR)) {
			tempDifference = 365 * (later.get(Calendar.YEAR) - earlier
					.get(Calendar.YEAR));
			difference += tempDifference;

			earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
		}

		if (earlier.get(Calendar.DAY_OF_YEAR) != later
				.get(Calendar.DAY_OF_YEAR)) {
			tempDifference = later.get(Calendar.DAY_OF_YEAR)
					- earlier.get(Calendar.DAY_OF_YEAR);
			difference += tempDifference;
		}

		return difference;
	}

}
