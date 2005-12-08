/*
 * Created on Jun 30, 2005
 *
 */
package com.sitescape.ef.web.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Map;
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
    static public Date getDateFromMap (Map formData, String formName, String id) 
    throws DatepickerException {
        return getDateFromMap (formData, formName, id, "0");
    }
    
/*
 * We also support the case where multiple timepicker tags can be associated with the SAME
 * datepicker tag -- for example, to specify the start and end time on the same day.
 * In this case, the sequence number is passed in to select which timepicker tag is to be used.
 */
    static public Date getDateFromMap (Map formData, String formName, String id, String sequenceNumber) 
    throws DatepickerException {
        Date d = new Date();
        // date fields don't have a sequence number; time fields do
        String datePrefix = formName + "_" + id + "_";
        String timePrefix = formName + "_" + id + "_" + sequenceNumber + "_";
        
        // check that the fields are there
        // date fields (select boxes) *must* be there
        if (!formData.containsKey(datePrefix+"month")) {
            throw new DatepickerException("Cannot find required date field: month.");
        }
        if (!formData.containsKey(datePrefix+"date")) {
            throw new DatepickerException("Cannot find required date field: date.");
        }
        
        // if the year isn't present, it probably means no date was entered
        if (!formData.containsKey(datePrefix+"year")) {
            return null;
        }
        String year = ((String[])formData.get(datePrefix + "year"))[0];
        // the calendar object will instantiate some date no matter what
        // we arbitrarily say that the date is not entered at all if the year is blank
        if (year.matches("")) {
            return null;
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(0);
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        String month = ((String[])formData.get(datePrefix + "month"))[0];
        int mn = Integer.parseInt(month);
        // the first (zero-th) select box is for unselected, or "--"
        // once the year is supplied, we default any other unselected fields
        if (mn != 0) {
            cal.set(Calendar.MONTH, mn-1);
        }

        String date = ((String[])formData.get(datePrefix + "date"))[0];
        int dd = Integer.parseInt(date);
        if (dd != 0) {
            cal.set(Calendar.DAY_OF_MONTH, dd);
        }

        // now on to the question of whether we use the time on the page...
        // we use time if a set of time fields is present with the specified sequenceNumber
        // if the time fields are missing, they default to present time (should they???) 
        if (formData.containsKey(timePrefix + "hour")) {
            String hour = ((String[])formData.get(timePrefix + "hour"))[0];
            int hh = Integer.parseInt(hour);
            // note that since 0 is a valid hour, we use 99 to indicate no selection 
            if (hh != 99) {
                cal.set(Calendar.HOUR_OF_DAY, hh);
            }
            String minute = ((String[])formData.get(timePrefix + "minute"))[0];
            int mm = Integer.parseInt(minute);
            if (mm != 99) {
                cal.set(Calendar.MINUTE, mm);
            }
        }
        if (formData.containsKey(datePrefix + "timezoneid")) {
            String tzs = ((String[])formData.get(datePrefix + "timezoneid"))[0];
            TimeZone tz = TimeZone.getTimeZone(tzs);
            cal.setTimeZone(tz);
        }
        d = cal.getTime();
        return d;
    }
    }
