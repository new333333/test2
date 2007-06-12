package com.sitescape.team.util;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

import com.sitescape.team.search.filter.SearchFilterKeys;

public class DateComparer {

	public static boolean isOverdueDate(Date date) {
		DateTime dateTime = new DateTime(date);
		return isOverdueDateTime(dateTime.withMillisOfDay(SearchFilterKeys.MILIS_IN_THE_DAY).toDate());
	}
	
	public static boolean isOverdueDateTime(Date date) {
		DateTime dateTime = new DateTime(date);
		return dateTime.isBeforeNow();
	}
	
}
