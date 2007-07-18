package com.sitescape.team.util;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

import com.sitescape.team.search.filter.SearchFilterKeys;

public class DateComparer {

	public static boolean isOverdue(Date date) {
		DateTime dateTime = new DateTime(date);
		return dateTime.isBeforeNow();
	}
	
}
