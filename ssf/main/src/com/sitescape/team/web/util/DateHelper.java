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
/*
 * Created on Jun 30, 2005
 *
 */
package com.sitescape.team.web.util;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.calendar.TimeZoneHelper;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.util.NLT;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author billmers
 *
 */
public class DateHelper {
	
	public static final int MILIS_IN_THE_DAY = (1000 * 60 * 60 * 24) - 1;
    

 /*
  * Normally, instances of the datepicker custom tag and the timepicker custom tag
  * are paired, and the sequence number for the timepicker tag will be 0. 
  * Use this call, without the sequence number, for the vanilla case
  */
    static public Date getDateFromInput(InputDataAccessor inputData, String id) 
    throws ConfigurationException {
        return getDateFromInput (inputData, id, "0");
    }
    
/*
 * We also support the case where multiple timepicker tags can be associated with the SAME
 * datepicker tag -- for example, to specify the start and end time on the same day.
 * In this case, the sequence number is passed in to select which timepicker tag is to be used.
 */
    static public Date getDateFromInput (InputDataAccessor inputData, String id, String sequenceNumber) 
    throws ConfigurationException {
        // date fields don't have a sequence number; time fields do
        String datePrefix = id + "_";
        String timePrefix = id + "_" + sequenceNumber + "_";
        
        if (isDojoWidgetDatePickerInUse(inputData, datePrefix)) {
        	return getDateFromDojoWidgetInput (inputData, datePrefix, timePrefix);
        }
        
        
        Date d = new Date();
        
        // if the year isn't present, it probably means no date was entered
        if (!inputData.exists(datePrefix+"year")) {
            return null;
        }
        String year = inputData.getSingleValue(datePrefix + "year");
        // the calendar object will instantiate some date no matter what
        // we arbitrarily say that the date is not entered at all if the year is blank
        if (year.matches("")) {
            return null;
        }

        // check that the fields are there
        // date fields (select boxes) *must* be there
        if (!inputData.exists(datePrefix+"month")) {
            throw new ConfigurationException("errorcode.no.date.field", new String[]{"month."});
        }
        if (!inputData.exists(datePrefix+"date")) {
            throw new ConfigurationException("errorcode.no.date.field", new String[]{"date."});
        }
        
        GregorianCalendar cal = new GregorianCalendar(); 
        cal.setTimeInMillis(0);
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        String month = inputData.getSingleValue(datePrefix + "month");
        int mn = Integer.parseInt(month);
        // the first (zero-th) select box is for unselected, or "--"
        // once the year is supplied, we default any other unselected fields
        if (mn != 0) {
            cal.set(Calendar.MONTH, mn-1);
        }

        String date = inputData.getSingleValue(datePrefix + "date");
        int dd = Integer.parseInt(date);
        if (dd != 0) {
            cal.set(Calendar.DAY_OF_MONTH, dd);
        }

        // now on to the question of whether we use the time on the page...
        // we use time if a set of time fields is present with the specified sequenceNumber
        // if the time fields are missing, they default to present time (should they???) 
        if (inputData.exists(timePrefix + "hour")) {
            String hour = inputData.getSingleValue(timePrefix + "hour");
            int hh = Integer.parseInt(hour);
            // note that since 0 is a valid hour, we use 99 to indicate no selection 
            if (hh != 99) {
                cal.set(Calendar.HOUR_OF_DAY, hh);
            }
            String minute = inputData.getSingleValue(timePrefix + "minute");
            int mm = Integer.parseInt(minute);
            if (mm != 99) {
                cal.set(Calendar.MINUTE, mm);
            }
        }
        
        if (inputData.exists(datePrefix + "timezoneid")) {
            String tzs = inputData.getSingleValue(datePrefix + "timezoneid");
            TimeZone tz = TimeZoneHelper.getTimeZone(tzs);
            cal.setTimeZone(tz);
        }
        d = cal.getTime();
        return d;
    }
    
    private static Date getDateFromDojoWidgetInput(InputDataAccessor inputData, String datePrefix, String timePrefix) {
    	
    	if (!inputData.exists(datePrefix + "fullDate") || 
    			"".equals(inputData.getSingleValue(datePrefix+"fullDate"))) {
    		return null;
    	}
    	
        DateTimeZone dateTimeZone = null;
        if (inputData.exists(datePrefix + "timezoneid") ||
        		inputData.exists(datePrefix + "timeZoneSensitive")) {
            String timeZoneString = inputData.getSingleValue(datePrefix + "timezoneid");
            String timeZoneSensitiveString = inputData.getSingleValue(datePrefix + "timeZoneSensitive");
            if ("".equals(timeZoneString) || 
            		"false".equals(timeZoneSensitiveString)) {
            	timeZoneString = DateTimeZone.UTC.getID();
            }
            dateTimeZone = DateTimeZone.forTimeZone(TimeZoneHelper.getTimeZone(timeZoneString));
        }
        
		String skipTime = null;
		if (inputData.exists(datePrefix + "skipTime")) {
			skipTime = inputData.getSingleValue(datePrefix + "skipTime");
		}
        
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        if (!"true".equals(skipTime) && dateTimeZone != null) {
        	dateFormatter = dateFormatter.withZone(dateTimeZone);
        }
		DateTime dateTime = null;
		if (inputData.exists(datePrefix + "fullDate")) {
			dateTime = dateFormatter.parseDateTime(inputData.getSingleValue(datePrefix+"fullDate"));
		}

		if (!"true".equals(skipTime) && inputData.exists(timePrefix + "fullTime")) {
			DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm:ss");
			if (dateTimeZone != null) {
				timeFormatter = timeFormatter.withZone(dateTimeZone);
			}
			
			// Remove time zone information from time string. Time zone is set by dojo TimePicker widget,
			// but this is user client time zone what is NOT the same like user profile time zone.
			String timeString = inputData.getSingleValue(timePrefix + "fullTime");
			if (!"".equals(timeString)) {
				if (timeString.indexOf("-") > -1) {
					timeString = timeString.substring(0, timeString.indexOf("-"));
				}
				if (timeString.indexOf("+") > -1) {
					timeString = timeString.substring(0, timeString.indexOf("+"));
				}			
				DateTime time = timeFormatter.parseDateTime(timeString);
				dateTime = dateTime.withTime(time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute(), time.getMillisOfSecond());
			}
		}
		return dateTime.toDate();
	}

	private static boolean isDojoWidgetDatePickerInUse(InputDataAccessor inputData, String datePrefix) {
    	return inputData.exists(datePrefix + "fullDate");
    }

	static public String getDateStringFromDMY (String day, String month, String year) 
			throws ConfigurationException {
        User user = RequestContextHolder.getRequestContext().getUser();
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(user.getTimeZone());
		
		int iDay, iMonth, iYear;
		
		// If the year isn't present, assume this year
		if (year.equals("")) {
		    iYear = cal.get(Calendar.YEAR);
		} else {
			iYear = Integer.parseInt(year);
		}
		// If the month isn't present, assume January
		if (month.equals("")) {
		    iMonth = 0;
		} else {
			iMonth = Integer.parseInt(month)-1;
		}
		// If the day isn't present, assume 1
		if (year.equals("")) {
		    iDay = 1;
		} else {
			iDay = Integer.parseInt(day);
		}
		cal.set(Calendar.YEAR, iYear);
		cal.set(Calendar.MONTH, iMonth);
		cal.set(Calendar.DAY_OF_MONTH, iDay);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String s = formatter.format(cal.getTime());
		return s;
	}

    static public Date getDateFromDMY (String day, String month, String year) 
			throws ConfigurationException {
        User user = RequestContextHolder.getRequestContext().getUser();
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(user.getTimeZone());
		
		int iDay, iMonth, iYear;
		
		// If the year isn't present, assume this year
		if (year.equals("")) {
		    iYear = cal.get(Calendar.YEAR);
		} else {
			iYear = Integer.parseInt(year);
		}
		// If the month isn't present, assume January
		if (month.equals("")) {
		    iMonth = 0;
		} else {
			iMonth = Integer.parseInt(month)-1;
		}
		// If the day isn't present, assume 1
		if (year.equals("")) {
		    iDay = 1;
		} else {
			iDay = Integer.parseInt(day);
		}
		cal.set(Calendar.YEAR, iYear);
		cal.set(Calendar.MONTH, iMonth);
		cal.set(Calendar.DAY_OF_MONTH, iDay);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		Date d = cal.getTime();
		return d;
	}

    // map day of week numbers into strings representing full day name
    static public String getDayNameString (int dayNum) {
    	switch (dayNum) {
    	case Calendar.SUNDAY: 
    		return NLT.get("calendar.day.names.su");
    	case Calendar.MONDAY:
    		return NLT.get("calendar.day.names.mo");
    	case Calendar.TUESDAY:
    		return NLT.get("calendar.day.names.tu");
    	case Calendar.WEDNESDAY:
    		return NLT.get("calendar.day.names.we");
    	case Calendar.THURSDAY:
    		return NLT.get("calendar.day.names.th");
    	case Calendar.FRIDAY:
    		return NLT.get("calendar.day.names.fr");
    	case Calendar.SATURDAY:
    		return NLT.get("calendar.day.names.sa");
    	default: 
    		return "";
    	}    		
    }
    // map day of week numbers into strings representing two letter abbrevs
    static public String getDayAbbrevString (int dayNum) {
    	switch (dayNum) {
    	case Calendar.SUNDAY: 
    		return NLT.get("calendar.day.abbrevs.su");
    	case Calendar.MONDAY:
    		return NLT.get("calendar.day.abbrevs.mo");
    	case Calendar.TUESDAY:
    		return NLT.get("calendar.day.abbrevs.tu");
    	case Calendar.WEDNESDAY:
    		return NLT.get("calendar.day.abbrevs.we");
    	case Calendar.THURSDAY:
    		return NLT.get("calendar.day.abbrevs.th");
    	case Calendar.FRIDAY:
    		return NLT.get("calendar.day.abbrevs.fr");
    	case Calendar.SATURDAY:
    		return NLT.get("calendar.day.abbrevs.sa");
    	default: 
    		return "";
    	}    		
    }
}

