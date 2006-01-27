/*
 * Created on Jun 30, 2005
 *
 */
package com.sitescape.ef.web.util;

import com.sitescape.ef.module.folder.InputDataAccessor;
import com.sitescape.ef.util.NLT;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Date;

/**
 * @author billmers
 *
 */
public class DateHelper {
    

 /*
  * Normally, instances of the datepicker custom tag and the timepicker custom tag
  * are paired, and the sequence number for the timepicker tag will be 0. 
  * Use this call, without the sequence number, for the vanilla case
  */
    static public Date getDateFromInput(InputDataAccessor inputData, String id) 
    throws DatepickerException {
        return getDateFromInput (inputData, id, "0");
    }
    
/*
 * We also support the case where multiple timepicker tags can be associated with the SAME
 * datepicker tag -- for example, to specify the start and end time on the same day.
 * In this case, the sequence number is passed in to select which timepicker tag is to be used.
 */
    static public Date getDateFromInput (InputDataAccessor inputData, String id, String sequenceNumber) 
    throws DatepickerException {
        Date d = new Date();
        // date fields don't have a sequence number; time fields do
        String datePrefix = id + "_";
        String timePrefix = id + "_" + sequenceNumber + "_";
              
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
            throw new DatepickerException("Cannot find required date field: month.");
        }
        if (!inputData.exists(datePrefix+"date")) {
            throw new DatepickerException("Cannot find required date field: date.");
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
            TimeZone tz = TimeZone.getTimeZone(tzs);
            cal.setTimeZone(tz);
        }
        d = cal.getTime();
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

