/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
/*
 * Created on Jun 30, 2005
 *
 */
package org.kablink.teaming.web.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.util.NLT;

/**
 * ?
 * 
 * @author billmers
 */
public class DateHelper {
	public static final int MILIS_IN_THE_DAY = ((1000 * 60 * 60 * 24) - 1000);	// Bugzilla 878940:  -1000 (1 second) vs. -1 (1 millisecond) to account for SQLServer's datetime inaccuracies.
    

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
        
       	return getDateFromDojoWidgetInput (inputData, datePrefix, timePrefix);
    }
    
    private static Date getDateFromDojoWidgetInput(InputDataAccessor inputData, String datePrefix, String timePrefix) {
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
        
		DateTime dateTime = null;
		String skipTime = null;
		if (inputData.exists(datePrefix + "skipTime")) {
			skipTime = inputData.getSingleValue(datePrefix + "skipTime");
		}
        
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        if (!"true".equals(skipTime) && dateTimeZone != null) {
        	dateFormatter = dateFormatter.withZone(dateTimeZone);
        }

        if (!inputData.exists(datePrefix + "fullDate") || 
    			"".equals(inputData.getSingleValue(datePrefix+"fullDate"))) {
            
            //We must be using the old style of date picker
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
            cal.set(Calendar.YEAR, Integer.parseInt(year));
            String month = inputData.getSingleValue(datePrefix + "month");
            if (month.equals("")) return null;
            int mn = Integer.parseInt(month);
            // the first (zero-th) select box is for unselected, or "--"
            // once the year is supplied, we default any other unselected fields
            if (mn != 0) {
                cal.set(Calendar.MONTH, mn-1);
            }

            String date = inputData.getSingleValue(datePrefix + "date");
            if (date.equals("")) return null;
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
            } else if (inputData.exists(timePrefix + "fullTime")) {
    			DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("HH:mm:00");
    			if (dateTimeZone != null) {
    				timeFormatter = timeFormatter.withZone(dateTimeZone);
    			}
    			
    			// Remove time zone information from time string. Time zone is set by dojo TimePicker widget,
    			// but this is user client time zone what is NOT the same like user profile time zone.
    			String timeString = inputData.getSingleValue(timePrefix + "fullTime");
    			//Allow hh:mm format, too
    			Pattern timeP = Pattern.compile("^[0-9]?[0-9]:[0-9]?[0-9]$");
    			Matcher m = timeP.matcher(timeString);
    			if (m.find()) timeString += ":00";
    			if (!"".equals(timeString)) {
    				timeString = timeString.replaceFirst("T", "");
    				if (timeString.indexOf("-") > -1) {
    					timeString = timeString.substring(0, timeString.indexOf("-"));
    				}
    				if (timeString.indexOf("+") > -1) {
    					timeString = timeString.substring(0, timeString.indexOf("+"));
    				}			
    				try {
    					DateTime time = timeFormatter.parseDateTime(timeString);
	                    int hh = time.getHourOfDay();
	                    // note that since 0 is a valid hour, we use 99 to indicate no selection 
	                    if (hh != 99) {
	                        cal.set(Calendar.HOUR_OF_DAY, hh);
	                    }
	                    int mm = time.getMinuteOfHour();
	                    if (mm != 99) {
	                        cal.set(Calendar.MINUTE, mm);
	                    }
    				} catch(Exception e) {}
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
    	
        //This is the dojo date picker
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
			//Allow hh:mm format, too
			Pattern timeP = Pattern.compile("^[0-9]?[0-9]:[0-9]?[0-9]$");
			Matcher m = timeP.matcher(timeString);
			if (m.find()) timeString += ":00";
			if (!"".equals(timeString)) {
				timeString = timeString.replaceFirst("T", "");
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

    public static Date max(Date d1, Date d2) {
        if (d1==null) {
            return d2;
        }
        if (d2==null) {
            return d1;
        }
        if (d1.compareTo(d2)>=0) {
            return d1;
        } else {
            return d2;
        }
    }

    public static Date min(Date d1, Date d2) {
        if (d1==null) {
            return d2;
        }
        if (d2==null) {
            return d1;
        }
        if (d1.compareTo(d2)<=0) {
            return d1;
        } else {
            return d2;
        }
    }
}
