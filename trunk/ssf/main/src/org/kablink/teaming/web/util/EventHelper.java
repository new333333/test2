/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.web.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.joda.time.DateTime;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.WeekendsAndHolidaysConfig;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.util.cal.DayAndPosition;
import org.kablink.util.cal.Duration;

/**
 * Helper methods for code that services event requests.
 *
 * @author drfoster@novell.com
 */
@SuppressWarnings("unchecked")
public class EventHelper {
	private static Log m_logger = LogFactory.getLog(EventHelper.class);
	
	// Attribute names used for items related to calendar entries.
	public static final String ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME        = "attendee";
	public static final String ASSIGNMENT_GROUPS_CALENDAR_ENTRY_ATTRIBUTE_NAME = "attendee_groups";
	public static final String ASSIGNMENT_TEAMS_CALENDAR_ENTRY_ATTRIBUTE_NAME  = "attendee_teams";
	public static final String ASSIGNMENT_EXTERNAL_ENTRY_ATTRIBUTE_NAME        = "responsible_external";
	
	public final static String CALENDAR_ATTENEE        = "attendee";
	public final static String CALENDAR_ATTENEE_GROUPS = "attendeeGroups";
	public final static String CALENDAR_ATTENEE_TEAMS  = "attendeeTeams";	
	    
	/**
	 * Called to adjust a date by a duration.  The date can be adjusted
	 * forward or backwards.
	 * 
	 * Takes into account the site wide weekend/holiday schedule
	 * configured into the system.
	 * 
	 * @param dateIn
	 * @param duration
	 * @param forward
	 * 
	 * @return
	 */
	public static Date adjustDate(Date dateIn, Duration duration, boolean forward) {
		// If we don't have a duration to adjust the date by...
		Date dateOut;
		long dateInMS     = dateIn.getTime();
		long adjustmentMS = duration.getInterval();
		if (0 == adjustmentMS) {
			// ..return a clone of the date we were given.
			dateOut = new Date(dateInMS);
			if (debugEnabled()) {
				StringBuffer out = new StringBuffer("EventHelper.adjustDate():");
				out.append("\n\tDate in:  "  + getDateTimeString(dateIn));
				out.append("\n\tDate out:  " + getDateTimeString(dateOut));
				out.append("\n\tAdjustments:  None specified");
				debugLog(out.toString());
			}
			return dateOut;
		}
		
		// If we're adjusting the date backwards...
		if (!forward) {
			// ...negate the adjustment milliseconds.
			adjustmentMS = -adjustmentMS;
		}
		
		// If we're going to end up with an adjusted milliseconds
		// that's less than 0...
		long adjustedMS = (dateInMS + adjustmentMS);
		if (0 > adjustedMS) {
			// ...just use 0.
			adjustedMS = 0;
		}
		dateOut = new Date(adjustedMS);
		
		// Does the duration we were given consist only of a number of
		// days?
		if (!(duration.hasDaysOnly())) {
			// No!  No other adjustments are necessary.
			if (debugEnabled()) {
				StringBuffer out = new StringBuffer("EventHelper.adjustDate():");
				out.append("\n\tDate in:  "  + getDateTimeString(dateIn));
				out.append("\n\tDate out:  " + getDateTimeString(dateOut));
				out.append("\n\tAdjustments:");
				out.append("\n\t\tDuration MS:  " + adjustmentMS);
				out.append("\n\t\tWeekend Days:  None.  Duration not in days.");
				out.append("\n\t\tHolidays:  None.  Duration not in days.");
				debugLog(out.toString());
			}
			return dateOut;
		}
		
		// We may need to account for the weekend/holiday schedule.
		// Are there any weekend days or holidays defined?
	    AdminModule adminModule = ((AdminModule) SpringContextUtil.getBean("adminModule"));
		WeekendsAndHolidaysConfig wahConfig = adminModule.getWeekendsAndHolidaysConfig();
		List<Integer> weekends = wahConfig.getWeekendDaysList(); boolean hasWeekends = ((null != weekends) && (!(weekends.isEmpty())));
		List<Date>    holidays = wahConfig.getHolidayList();     boolean hasHolidays = ((null != holidays) && (!(holidays.isEmpty())));
		if ((!hasWeekends) && (!hasHolidays)) {
			// No!  No other adjustments are necessary.
			if (debugEnabled()) {
				StringBuffer out = new StringBuffer("EventHelper.adjustDate():");
				out.append("\n\tDate in:  "  + getDateTimeString(dateIn));
				out.append("\n\tDate out:  " + getDateTimeString(dateOut));
				out.append("\n\tAdjustments:");
				out.append("\n\t\tDuration:  " + duration.getDays() + " days");
				out.append("\n\t\tWeekend Days:  None No weekends or holidays scheduled.");
				out.append("\n\t\tHolidays:  None.  No weekends or holidays scheduled.");
				debugLog(out.toString());
			}
			return dateOut;
		}
		
		// Yes, there are weekend days or holidays defined!
		int weDays_Total = 0;
		int hDays_Total  = 0;
		while (true) {
			// Adjust for the weekends and holidays.
			weedWeekendsOutOfHolidays(weekends, holidays);
			int weDays_New = adjustForWeekends(dateIn, dateOut, forward, weekends, weDays_Total);
			int hDays_New  = adjustForHolidays(dateIn, dateOut, forward, holidays, hDays_Total );
			
			// Did we make any more adjustments?
			if ((weDays_New == weDays_Total) && (hDays_New == hDays_Total)) {
				// No!  Then we're done.
				break;
			}

			// Track the new days that we've adjusted for and try again.
			weDays_Total = weDays_New;
			hDays_Total  = hDays_New;
		}
		
		// If we get here, dateOut contains the adjusted Date.  Return
		// it.
		if (debugEnabled()) {
			StringBuffer out = new StringBuffer("EventHelper.adjustDate():");
			out.append("\n\tDate in:  "  + getDateTimeString(dateIn));
			out.append("\n\tDate out:  " + getDateTimeString(dateOut));
			out.append("\n\tAdjustments");
			out.append("\n\t\tDuration Days:  " + duration.getDays() + " days");
			out.append("\n\t\tWeekend Days:  "  + weDays_Total);
			out.append("\n\t\tHolidays:  "      + hDays_Total);
			debugLog(out.toString());
		}
		return dateOut;
	}
	
	public static Date adjustDate(Date dateIn, int days) {
		// If we don't have a number of days to adjust the date by...
		if (0 == days) {
			// ...return a clone of the date we were given.
			return new Date(dateIn.getTime());
		}

		// Construct a Duration using the days...
		Duration duration = new Duration();
		boolean forward = (0 < days);
		if (!forward) {
			days = -days;
		}		
		duration.setDays(days);
		
		// ...and use that to adjust the date forward or backwards, as
		// ...appropriate.
		return adjustDate(dateIn, duration, forward);
	}

	/*
	 * Adjusts dateOut forwards or backwards to account for holidays
	 * beyond those accounted for by hDays.
	 */
	private static int adjustForHolidays(Date dateIn, Date dateOut, boolean forward, List<Date> holidays, int hDays) {
		while (true) {
			// What's the earlier and later date?
			Date baseDateIn  = stripTimeFromDate(dateIn );
			Date baseDateOut = stripTimeFromDate(dateOut);
			long earlierDate;
			long laterDate;
			if (forward) {earlierDate = baseDateIn.getTime();  laterDate = baseDateOut.getTime();}
			else         {earlierDate = baseDateOut.getTime(); laterDate = baseDateIn.getTime(); }

			// Scan the holidays.
			int adjustmentDays = 0;
			for (Date holiday:  holidays) {
				// Does this holiday occur between the earlier and
				// later dates?
				long hDate = holiday.getTime();
				if ((hDate >= earlierDate) && (hDate <= laterDate)) {
					// Yes!  Then we need to adjust for it. 
					adjustmentDays += 1;
				}
			}

			// If we've already adjusted for the correct number of
			// days...
			if (adjustmentDays <= hDays) {
				// ... we're done.
				break;
			}

			// Adjust by the new number of days and try again.
			long adjustmentMS = ((adjustmentDays - hDays) * Duration.MILLIS_PER_DAY);
			if (forward)
				 dateOut.setTime(dateOut.getTime() + adjustmentMS);
			else dateOut.setTime(dateOut.getTime() - adjustmentMS);
			hDays = adjustmentDays;
		}

		// If we get here, hDays contains the total number of holidays
		// that we've adjusted for.  Return it.
		return hDays;
	}

	/*
	 * Adjusts dateOut forwards or backwards to account for weekend
	 * days beyond those accounted for by weDays.
	 */
	private static int adjustForWeekends(Date dateIn, Date dateOut, boolean forward, List<Integer> weekendDays, int weDays) {
		GregorianCalendar earlierDay = new GregorianCalendar();
		GregorianCalendar laterDay   = new GregorianCalendar();
		while (true) {
			// What's the earlier and later days?
			if (forward) {earlierDay.setTime(dateIn);  laterDay.setTime(dateOut);}
			else         {earlierDay.setTime(dateOut); laterDay.setTime(dateIn); }

			// Scan the weekend days.
			int adjustmentDays = 0;
			for (Integer weekendDay:  weekendDays) {
				// Summing up the total number of days we need to
				// adjust for.
				adjustmentDays += countDayOccurrences(earlierDay, laterDay, weekendDay.intValue());
			}

			// If we've already adjusted for the correct number of
			// days...
			if (adjustmentDays <= weDays) {
				// ...we're done.
				break;
			}
			
			// Adjust by the new number of days and try again.
			long adjustmentMS = ((adjustmentDays - weDays) * Duration.MILLIS_PER_DAY);
			if (forward)
				 dateOut.setTime(dateOut.getTime() + adjustmentMS);
			else dateOut.setTime(dateOut.getTime() - adjustmentMS);
			weDays = adjustmentDays;
		}
		
		// If we get here, weDays contains the total number of holidays
		// that we've adjusted for.  Return it.
		return weDays;
	}
	
    /**
     * @param baseFilter
     * @param request
     * @param modeType
     * @param folderIds
     * @param binder
     * @param assigneeType
     * 
     * @return
     */
	public static SearchFilter buildSearchFilter(Document baseFilter, PortletRequest request, 
			ListFolderHelper.ModeType modeType, Collection folderIds, Binder binder, 
			SearchUtils.AssigneeType assigneeType) {
		SearchFilter searchFilter;
		User user = RequestContextHolder.getRequestContext().getUser();
		
		// Are we only showing events assigned to something?
		if (ListFolderHelper.ModeType.VIRTUAL == modeType) {
  		   	String searchAsUser = "";
  		   	String searchAsTeam = "";
  		   	
  		   	// Yes!  If the binder's workspace is a Team workspace...
  			searchFilter = new SearchFilter(true);
   	       	Workspace binderWs = BinderHelper.getBinderWorkspace(binder);
  		   	if (BinderHelper.isBinderTeamWorkspace(binderWs)) {
  	  		   	// ...we search for that Team's events...
	   			searchAsTeam = String.valueOf(binderWs.getId());
  		   	}
  		   	else {
  		   		// ...otherwise, we search for owner of the workspace's
  		   		// ...events...
  		   		searchAsUser = String.valueOf(binderWs.getOwnerId());
  		   	}

  		   	// Setup the assignees to search for.
			SearchUtils.setupAssignees(
				searchFilter,
				null,	// null -> No model.
				searchAsUser,
				"",	// No special 'group assignee' setup required.
				searchAsTeam,
				assigneeType);
			
		} else if (ListFolderHelper.ModeType.MY_EVENTS == modeType) {
  		   	String searchAsUser = String.valueOf(user.getId());
  			searchFilter = new SearchFilter(true);
  		   	
  		   	// Setup the assignees to search for.
			SearchUtils.setupAssignees(
				searchFilter,
				null,	// null -> No model.
				searchAsUser,
				"",	// No special 'group assignee' setup required.
				"",	// No special 'team assignee' setup required.
				assigneeType);
			
		} else {
			// No, we are showing physical events!
			String filterName = ((null == request) ? "" : PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterNameField, ""));
			searchFilter = new SearchFilter();
			searchFilter.addFilterName(filterName);

			for (Object id : folderIds) {
				searchFilter.addFolderId((String) id);
			}
		}
		
		searchFilter.appendFilter(baseFilter);
		return searchFilter;
	}

	public static SearchFilter buildSearchFilter(Document baseFilter, PortletRequest request, Collection folderIds, SearchUtils.AssigneeType assigneeType) {
		return buildSearchFilter(baseFilter, request, ListFolderHelper.ModeType.PHYSICAL, folderIds, null, assigneeType);
	}

	/**
	 * @param baseFilter
	 * @param request
	 * @param folderIds
	 * @param assigneeType
	 * 
	 * @return
	 */
	public static Document buildSearchFilterDoc(Document baseFilter, PortletRequest request, Collection folderIds, SearchUtils.AssigneeType assigneeType) {
		return buildSearchFilter(baseFilter, request, folderIds, assigneeType).getFilter();
	}
	
	public static Document buildSearchFilterDoc(Document baseFilter, PortletRequest request, ListFolderHelper.ModeType modeType, Collection folderIds, Binder binder, SearchUtils.AssigneeType assigneeType) {
		return buildSearchFilter(baseFilter, request, modeType, folderIds, binder, assigneeType).getFilter();
	}

	/*
	 */
    private static Duration checkDuration(InputDataAccessor inputData, String prefix) {
    	Duration reply = null;
        String durationDaysS = inputData.getSingleValue(prefix + "durationDays");
        if (MiscUtil.hasString(durationDaysS)) {
        	try {
        		int days = Integer.parseInt(durationDaysS);
        		if (0 < days) {
	        		reply = new Duration();
	        		reply.setDays(days);
        		}
        	}
        	catch (Exception ex) {}
        }
        return reply;
    }

    /*
     * Returns the number of times a particular day occurs between two
     * given days.
     */
    private static int countDayOccurrences(GregorianCalendar earlierDay, GregorianCalendar laterDay, int dayToCount) {
    	// Scan the days, starting with the earliest.
    	int      count      = 0; 
    	Calendar currentDay = ((Calendar) earlierDay.clone()); 
    	Calendar lastDay    = ((Calendar) laterDay.clone()); 
    	lastDay.add(Calendar.DATE, 1);	// Bumped by one so that we count the last day if it's a hit. 
    	while (!(currentDay.equals(lastDay))) {
    		// Is this the day that we're counting?
	    	if (currentDay.get(Calendar.DAY_OF_WEEK) == dayToCount) {
	    		// Yes!  Count it.
	    		count += 1; 
	    	}
	    	
	    	// Move to the next day.
	    	currentDay.add(Calendar.DATE, 1); 
    	}
    	
    	// If we get here, count contains the number of times
    	// dayToCount occurs between earlierDay and laterDay.  Return
    	// it.
    	return count; 
    }

    /**
     * Returns true if EventHelper has debug logging enabled and false
     * otherwise.
     * 
     * @return
     */
    public static boolean debugEnabled() {
    	return m_logger.isDebugEnabled();
    }
    
    /**
     * Writes a String on behalf of EventHelper to the debug log.
     *  
     * @param s
     */
    public static void debugLog(String s) {
    	m_logger.debug(s);
    }
    
	/**
	 * Returns the String representation for the given date, formatted
	 * based on the current user's locale and time zone.
	 * 
	 * @param date
	 * @param allDayEventDate
	 * 
	 * @return
	 */
	public static String getDateTimeString(Date date, boolean allDayEventDate) {
		String reply;
		if (null == date) {
			reply = "";
		}
		else {
			User   user       = RequestContextHolder.getRequestContext().getUser();
			Locale userLocale = user.getLocale();
			
			DateFormat df;
			if (allDayEventDate) {
				df = DateFormat.getDateInstance(DateFormat.SHORT, userLocale);
				df.setTimeZone(TimeZone.getTimeZone("GMT"));
			}
			else {
				df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, userLocale);
				df.setTimeZone(user.getTimeZone());
			}
			reply = df.format(date);
		}
		return reply;
	}
	
	public static String getDateTimeString(Date date) {
		// Always use the initial form of the method.
		return getDateTimeString(date, false);
	}
	
    /**
     * @param inputData
     * @param id
     * @param hasDuration
     * @param hasRecurrence
     * 
     * @return
     */
    public static Event getEventFromMap (InputDataAccessor inputData, String id, Boolean hasDuration, Boolean hasRecurrence) {
        // We make the id match what the event editor would do.
    	String prefix = id + "_";
    	
        Event event = new Event();
        
        String uid =  inputData.getSingleValue(prefix + "event_uid");
        if (uid != null && uid.length() > 0) {
        	event.setUid(uid);
        }
        
        // duration present means there is a start and end id
        String startId = ("dp_"  + id);
        String endId   = ("dp2_" + id);

        Duration eventDur = checkDuration(inputData, prefix);        
        Date     start    = inputData.getDateValue(startId);
        Date     end      = inputData.getDateValue(endId);
        
        if (start == null && end == null) {
        	if (null == eventDur) {
        		event = null;
        	}
        	else {
        		event.setTimeZone(getTimeZone(inputData, id));
        		event.setDuration(eventDur);
        	}
            return event;
        } else if (start == null && end != null) {
        	DateTime startDt = new DateTime();
        	start = startDt.plusMinutes(5).minusMinutes(startDt.getMinuteOfHour() % 5).withSecondOfMinute(0).withMillisOfSecond(0).toDate();
        }
        GregorianCalendar startc = new GregorianCalendar();
        startc.setTime(  start);
        event.setDtStart(startc);
        
        if (end != null) {
        	if (end.before(start)) {
        		end = new Date(start.getTime());
        	}
        	
        	GregorianCalendar endc = new GregorianCalendar();
        	endc.setTime(end);
        	event.setDtEnd(endc);
        } else if (eventDur!=null) {
			event.setCalculatedEndDate(startc, eventDur);
		}
        
        if (!isAllDayEvent(inputData, id)) {
        	event.setTimeZone(getTimeZone(inputData, id));
        }
        else {
        	event.allDaysEvent();
        	if (event.getDtStart() != null) {
        		DateTime startEvent = new DateTime(event.getDtStart());
        		event.setDtStart(startEvent.withMillisOfDay(0).toGregorianCalendar());
        	}
        	
        	if (event.getDtEnd() != null) {
        		DateTime endEvent = new DateTime(event.getDtEnd());
        		event.setDtEnd(endEvent.withMillisOfDay(DateHelper.MILIS_IN_THE_DAY).toGregorianCalendar());
        	}
        }
        
        String timeZoneSensitive = inputData.getSingleValue("timeZoneSensitive_" + id);
       	event.setTimeZoneSensitive((null != timeZoneSensitive) && "true".equals(timeZoneSensitive));
        try {
        	String freeBusy = inputData.getSingleValue(prefix + "freeBusy");
        	event.setFreeBusy(Event.FreeBusyType.valueOf(freeBusy));
        }
        catch (IllegalArgumentException e) {}
        catch (NullPointerException     e) {}
        if (hasRecurrence.booleanValue()) {
            String repeatUnit  = inputData.getSingleValue(prefix + "repeatUnit");
            String intervalStr = inputData.getSingleValue(prefix + "everyN"    );            
            String rangeSel    = inputData.getSingleValue(prefix + "rangeSel"  );	// rangeSel is the count/until/forever radio button.

            if (repeatUnit == null || repeatUnit.equals("none")) {
                event.setFrequency(Event.NO_RECURRENCE);
            }
            else {
	            if (repeatUnit.equals("day")) {
	                event.setFrequency(Event.DAILY);
	                event.setInterval(intervalStr);

	                parseOnDaysOfWeek(event, inputData, prefix);	// It's not yet implemented in UI.
	            }
	            
	            if (repeatUnit.equals("week")) {
	                event.setFrequency(Event.WEEKLY);
	                event.setInterval(intervalStr);
	                
	                parseOnDaysOfWeek(event, inputData, prefix);
	            }
	            
	            if (repeatUnit.equals("month")) {
	                event.setFrequency(Event.MONTHLY);
	                event.setInterval(intervalStr);
	                
	                parseDaysOfWeekWithPositions(event, inputData, prefix);
	                parseDaysOfMonth(event, inputData, prefix);
	                parseMonths(event, inputData, prefix);
	                
	            }
	            
	            else if (repeatUnit.equals("year")) {
	                event.setFrequency(Event.YEARLY);
	                event.setInterval(intervalStr);
	                
	                parseDaysOfWeekWithPositions(event, inputData, prefix);
	                parseDaysOfMonth(event, inputData, prefix);
	                parseMonths(event, inputData, prefix);
	            }
	            
	            if (rangeSel != null && rangeSel.equals("count")) {
	                String repeatCount = inputData.getSingleValue(prefix+"repeatCount");
	                event.setCount(repeatCount);
	            }
	            
	            else if (rangeSel != null && rangeSel.equals("until")) {
	                String untilId = "endRange_" + id;
	                Date until = inputData.getDateValue(untilId);
	                GregorianCalendar untilCal = new GregorianCalendar();
	                untilCal.setTime(until);
	                event.setUntil(untilCal);
	            }
	            
	            else if (rangeSel != null && rangeSel.equals("forever")) {
	            	event.setCount(0);
	            }
	            
	            else {
	            	event.setCount(1);
	            }
            }
        }
        else {
            event.setFrequency(Event.NO_RECURRENCE);
        }
               
    	if (null != eventDur) {
    		event.setDuration(eventDur);
    	}
    	
        return event;
    }
    
    public static Event getEventFromMap (InputDataAccessor inputData, String id) {
    	// Assumes duration and recurrence patterns.
        return getEventFromMap(inputData, id, true, true);
    }    

    /*
     */
	private static TimeZone getTimeZone(InputDataAccessor inputData, String id) {
		if (inputData.exists("timeZone_" + id)) {
			return TimeZoneHelper.getTimeZone(inputData.getSingleValue("timeZone_" + id));
		}
      	User user = RequestContextHolder.getRequestContext().getUser();
		return user.getTimeZone();
	}

	/*
	 */
	private static boolean isAllDayEvent(InputDataAccessor inputData, String id) {
		return inputData.exists("allDayEvent_" + id);
	}

	/*
	 */
	private static void parseDaysOfWeekWithPositions(Event e, InputDataAccessor inputData, String prefix) {
        String onDayCard = inputData.getSingleValue(prefix + "onDayCard");
        String dow       = inputData.getSingleValue(prefix + "dow"      );
        
        if ((null != onDayCard) && (null != dow)) {        
            int dayNum = 0;
            if      (onDayCard.equals("first" )) dayNum = 1;
            else if (onDayCard.equals("second")) dayNum = 2;
            else if (onDayCard.equals("third" )) dayNum = 3;
            else if (onDayCard.equals("fourth")) dayNum = 4;
            else if (onDayCard.equals("fifth" )) dayNum = 5;
            else if (onDayCard.equals("last"  )) dayNum = -1;

            int day = 0;
            if      (dow.equals("Sunday"   )) day = Calendar.SUNDAY;
            else if (dow.equals("Monday"   )) day = Calendar.MONDAY;
            else if (dow.equals("Tuesday"  )) day = Calendar.TUESDAY;
            else if (dow.equals("Wednesday")) day = Calendar.WEDNESDAY;
            else if (dow.equals("Thursday" )) day = Calendar.THURSDAY;
            else if (dow.equals("Friday"   )) day = Calendar.FRIDAY;
            else if (dow.equals("Saturday" )) day = Calendar.SATURDAY;

            if (!onDayCard.equals("none")) {
                DayAndPosition dpa[] = new DayAndPosition[1];
                dpa[0] = new DayAndPosition();
                dpa[0].setDayOfWeek(day);
                dpa[0].setDayPosition(dayNum);
                e.setByDay(dpa);
            }        
        }
	}

	/*
	 */
	private static void parseDaysOfMonth(Event e, InputDataAccessor inputData, String prefix) {
        String[] doms = inputData.getValues(prefix+"dom");
        if (doms != null) {
        	int[] domsArray = new int[doms.length];
        	for (int i = 0; i < doms.length; i += 1) {
        		domsArray[i] = Integer.parseInt(doms[i]);
        	}
        	e.setByMonthDay(domsArray);
        }
	}
	
	/*
	 */
	private static void parseMonths(Event e, InputDataAccessor inputData, String prefix) {
        String[] months = inputData.getValues(prefix+"month");
        if (months != null) {
        	int[] monthsArray = new int[months.length];
        	for (int i = 0; i < months.length; i += 1) {
        		monthsArray[i] = Integer.parseInt(months[i]);
        	}
        	e.setByMonth(monthsArray);
        }
	}
	
	/*
	 */
	private static void parseOnDaysOfWeek(Event e, InputDataAccessor inputData, String prefix) {
        // This array maps the form checkboxes to the day-and-position
		// constants.
        int daysints[] = { 
        	Calendar.SUNDAY,
        	Calendar.MONDAY,
        	Calendar.TUESDAY,
        	Calendar.WEDNESDAY, 
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY 
        };
        
        // First we count the number of checkboxes so that we can make
        // the array of the correct size (setByDay will try to clone
        // the array, so any nulls inside will throw an exception.)
        String days[] = new String[7];
        if (inputData.exists(prefix + "day0"))
             days[0] = inputData.getSingleValue(prefix + "day0");
        else days[0] = "";

        if (inputData.exists(prefix + "day1"))
             days[1] = inputData.getSingleValue(prefix + "day1");
        else days[1] = "";

        if (inputData.exists(prefix + "day2"))
             days[2] = inputData.getSingleValue(prefix + "day2");
        else days[2] = "";

        if (inputData.exists(prefix + "day3"))
             days[3] = inputData.getSingleValue(prefix + "day3");
        else days[3] = "";

        if (inputData.exists(prefix + "day4"))
             days[4] = inputData.getSingleValue(prefix + "day4");
        else days[4] = "";

        if (inputData.exists(prefix + "day5"))
             days[5] = inputData.getSingleValue(prefix + "day5");
        else days[5] = "";

        if (inputData.exists(prefix + "day6"))
             days[6] = inputData.getSingleValue(prefix + "day6");
        else days[6] = "";

        int arraysz = 0;
        for (int ct = 0; ct < 7; ct += 1) {
            if (days[ct].equals("on")) {
                arraysz += 1;
            }
        }
        if (arraysz > 0) {
            DayAndPosition dpa[] = new DayAndPosition[arraysz];
            for (int i = 0, j = 0; i < 7; i += 1) {
                if (days[i].equals("on")) {
                    dpa[j] = new DayAndPosition();
                    dpa[j++].setDayOfWeek(daysints[i]);
                }
            }
            e.setByDay(dpa);
        }
	}
	
	/*
	 * Returns a Date with the time stripped from it.
	 */
	private static Date stripTimeFromDate(Date d) {
		// Construct a Calendar using the Date...
		Calendar dCal = Calendar.getInstance();
		dCal.setTime(new Date(d.getTime()));
		
		// ...set its time fields to 0...
		dCal.set(Calendar.HOUR_OF_DAY, 0);
		dCal.set(Calendar.MINUTE,      0);
		dCal.set(Calendar.SECOND,      0);
		dCal.set(Calendar.MILLISECOND, 0);

		// ...and return its new Date portion.
		return dCal.getTime();
	}

	/*
	 * Scan the List<Date> of holidays and any that fall on one of the
	 * weekend days are discarded.
	 */
	private static void weedWeekendsOutOfHolidays(List<Integer> weekends, List<Date> holidays) {
		// If we don't have any weekend days or holidays...
		if (weekends.isEmpty() || holidays.isEmpty()) {
			// ...there's nothing to weed out.
			return;
		}

		// Scan the holidays.
		GregorianCalendar gc = new GregorianCalendar();;
		int c = holidays.size();
		for (int i = (c - 1); i >= 0; i -= 1) {
			// Is this holiday on a weekend day?
			gc.setTime(holidays.get(i));
			int dayOfWeek = gc.get(Calendar.DAY_OF_WEEK);
			if (weekends.contains(dayOfWeek)) {
				// Yes!  Remove it from the holidays list.
				holidays.remove(i);
			}
		}
	}
}
