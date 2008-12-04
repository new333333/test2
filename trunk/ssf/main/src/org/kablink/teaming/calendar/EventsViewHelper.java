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
package org.kablink.teaming.calendar;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.portlet.PortletSession;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.PersistentObject;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.teaming.util.CalendarHelper;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.cal.DayAndPosition;
import org.kablink.util.search.Constants;


public class EventsViewHelper {

	private static Log logger = LogFactory.getLog(EventsViewHelper.class);
	
	public static class Grid implements Serializable {
		private static final long serialVersionUID = -7320034020584683226L;
		public String type;
		public Integer size;
		public Grid(String type, Integer size) {
			super();
			this.type = type;
			this.size = size;
		}
		//need to implement this for hibernate equality
	    public boolean equals(Object obj) {
	        if(this == obj)
	            return true;

	        if (obj == null) 
	            return false;
	        
	        if (!(obj instanceof Grid)) return false;
	        Grid grid = (Grid) obj;
	        if (this.type != null) {
	        	if (!this.type.equals(grid.type)) return false;
	        } else if (grid.type != null) return false;
	        if (this.size != null) {
	        	if (!this.size.equals(grid.size)) return false;
	        } else if (grid.size != null) return false;
	    	return true;
	    }
	}
	
	public static final String[] monthNames = { 
		"calendar.january",
		"calendar.february",
		"calendar.march",
		"calendar.april",
		"calendar.may",
		"calendar.june",
		"calendar.july",
		"calendar.august",
		"calendar.september",
		"calendar.october",
		"calendar.november",
		"calendar.december"
	};
	
	private static final String dayNames[] = new String[10];
	
	private static final String nums[] = new String[6];
	
	static {
		dayNames[Calendar.SUNDAY] = "calendar.day.abbrevs.su";
		dayNames[Calendar.MONDAY] = "calendar.day.abbrevs.mo";
		dayNames[Calendar.TUESDAY] = "calendar.day.abbrevs.tu";
		dayNames[Calendar.WEDNESDAY] = "calendar.day.abbrevs.we";
		dayNames[Calendar.THURSDAY] = "calendar.day.abbrevs.th";
		dayNames[Calendar.FRIDAY] = "calendar.day.abbrevs.fr";
		dayNames[Calendar.SATURDAY] = "calendar.day.abbrevs.sa";
		
		nums[1] = "calendar.first";
		nums[2] = "calendar.second";
		nums[3] = "calendar.third";
		nums[4] = "calendar.fourth";
		nums[5] = "calendar.last";
	}
	
	public static final String EVENT_TYPE_CREATION = "creation";
	
	public static final String EVENT_TYPE_ACTIVITY = "activity";
	
	public static final String EVENT_TYPE_EVENT = "event";

	public static final String EVENT_TYPE_DEFAULT = EVENT_TYPE_EVENT;

	
	public static final String GRID_DAY = "day";
	
	public static final String GRID_MONTH = "month";
	
	public static final String GRID_DEFAULT = GRID_MONTH;
	

	public static final String DAY_VIEW_TYPE_WORK = "workday";
	
	public static final String DAY_VIEW_TYPE_FULL = "fullday";

	public static final String DAY_VIEW_TYPE_DEFAULT = DAY_VIEW_TYPE_WORK;
	
	public static Map getEntryEventsBeans( Binder folder,
			List searchResults, RenderResponse response, PortletSession portletSession, boolean onlyTrueEvents) {
		Map result = new HashMap();
		
		List events = EventsViewHelper.getCalendarEventsBeans(searchResults,	null, onlyTrueEvents);
		
		Map calendarViewBean = new HashMap();
		calendarViewBean.put("events", events);
		
		result.put(WebKeys.CALENDAR_VIEWBEAN, calendarViewBean);
		
		return result;
	}
	
	public static Map getEventsBeans(List searchResults, AbstractIntervalView calendarViewRangeDates, 
			PortletSession portletSession, boolean onlyTrueEvents) {
		return getEventsBeans(searchResults, calendarViewRangeDates, 
				getCalendarDisplayEventType(portletSession), 
				getCalendarDayViewType(portletSession), onlyTrueEvents);
	}
	
	public static Map getEventsBeans(List searchResults, AbstractIntervalView intervalView, 
			String eventType, String dayViewType, boolean onlyTrueEvents) {
		Map result = new HashMap();
		
		result.put(WebKeys.CALENDAR_CURRENT_VIEW_STARTDATE,
				intervalView.getStart());
		result.put(WebKeys.CALENDAR_CURRENT_VIEW_ENDDATE, 
				intervalView.getEnd());

		List events = EventsViewHelper.getCalendarEventsBeans(searchResults,
				intervalView, onlyTrueEvents);
		
		Map calendarViewBean = new HashMap();
		calendarViewBean.put("monthInfo", intervalView.getCurrentDateMonthInfo());
		calendarViewBean.put("today", new Date());
		calendarViewBean.put("events", events);
		calendarViewBean.put("eventType", eventType);
		calendarViewBean.put("dayViewType", dayViewType);
		
		result.put(WebKeys.CALENDAR_VIEWBEAN, calendarViewBean);
		
		return result;
	}
	
	public static Map<Map, List<Event>> getEvents(List searchResults, AbstractIntervalView intervalView) {
		Map<Map, List<Event>> results = new HashMap<Map, List<Event>>();
		
		Iterator entryIterator = searchResults.iterator();
		while (entryIterator.hasNext()) {
			Map entry = (HashMap) entryIterator.next();
			List<Event> events = parseSearchResultEntryToEvents(entry, intervalView);
			results.put(entry, events);
		}
		return results;
	}	

	private static List getCalendarEventsBeans(List searchResults,
			AbstractIntervalView viewRangeDates, boolean onlyTrueEvents) {
		
		List result = new ArrayList();
		
		User user = RequestContextHolder.getRequestContext().getUser();
		TimeZone timeZone = user.getTimeZone();

		Iterator entryIterator = searchResults.iterator();
		while (entryIterator.hasNext()) {
			Map entry = (HashMap) entryIterator.next();
			
			if (!onlyTrueEvents) {
				// Add the creation date as an event
				Date creationDate = (Date) entry.get(Constants.CREATION_DATE_FIELD);
				if (creationDate != null && 
						(viewRangeDates == null || viewRangeDates.dateInView(creationDate))) {
					List events = new ArrayList();
					events.add(createEvent(creationDate, timeZone, EVENT_TYPE_CREATION, 
							(String)entry.get(Constants.DOCID_FIELD)));
					result.addAll(getEventsBeansByEvents(entry, events, EVENT_TYPE_CREATION));
				}
	
				// Add the activity date as an event
				Date lastActivityDate = (Date) entry.get(Constants.LASTACTIVITY_FIELD);
				if (lastActivityDate != null && (viewRangeDates == null || viewRangeDates.dateInView(lastActivityDate))) {
					List events = new ArrayList();
					events.add(createEvent(lastActivityDate, timeZone, EVENT_TYPE_ACTIVITY, 
							(String)entry.get(Constants.DOCID_FIELD)));
					result.addAll(getEventsBeansByEvents(entry, events, EVENT_TYPE_ACTIVITY));
				}
			}
			
			List<Event> events = parseSearchResultEntryToEvents(entry, viewRangeDates);
			
			result.addAll(getEventsBeansByEvents(entry, events, EVENT_TYPE_EVENT));
		}
		return result;
	}
	
	/**
	 * One entry can contain many events.
	 */
	private static List<Event> parseSearchResultEntryToEvents(Map entry, AbstractIntervalView viewRangeDates) {
		User user = RequestContextHolder.getRequestContext().getUser();
		TimeZone timeZone = user.getTimeZone();
		
		List<Event> events = new ArrayList<Event>();
		
		// Add the events
		// look through the custom attrs of this entry for any of type EVENT
		int eventsCount = parseEventsCount((String) entry.get(Constants.EVENT_COUNT_FIELD));
		for (int j = 0; j < eventsCount; j++) {
			String name = (String) entry.get(Constants.EVENT_FIELD + j);

			String recurrenceDatesField = (String) entry.get(name + BasicIndexUtils.DELIMITER + Constants.EVENT_RECURRENCE_DATES_FIELD);
			String eventId = (String) entry.get(name + BasicIndexUtils.DELIMITER + Constants.EVENT_ID);
			String timeZoneID = (String) entry.get(name + BasicIndexUtils.DELIMITER + Constants.EVENT_FIELD_TIME_ZONE_ID);
			boolean timeZoneSensitive = true;
			String timeZoneSensitiveString = (String) entry.get(name + BasicIndexUtils.DELIMITER + Constants.EVENT_FIELD_TIME_ZONE_SENSITIVE);
			if ("false".equals(timeZoneSensitiveString)) {
				timeZoneSensitive = false;
			}
			String freeBusyString = (String) entry.get(name + BasicIndexUtils.DELIMITER + Constants.EVENT_FIELD_FREE_BUSY);
			Event.FreeBusyType freeBusy = null;
			try {
				freeBusy = Event.FreeBusyType.valueOf(freeBusyString);
			} catch (NullPointerException e) {				
			} catch (IllegalArgumentException e) {}
			if (recurrenceDatesField != null) {
				String[] recurrenceDates = recurrenceDatesField.split(",");
				for (int recCounter = 0; recCounter < recurrenceDates.length; recCounter++) {
					String[] recurrenceStartEndTime = recurrenceDates[recCounter].split(" ");
					Date evStartDate = null;
					Date evEndDate = null;
					try {
						evStartDate = DateTools
								.stringToDate(recurrenceStartEndTime[0]);
						evEndDate = DateTools
								.stringToDate(recurrenceStartEndTime[1]);
					} catch (ParseException parseExc) {
						logger.warn("Event recurrence date in search index has wrong format [" + evStartDate + "] or [" + evEndDate + "].");
						evStartDate = new Date();
						evEndDate = new Date();
					}
					
					// in results we have only entries with events in view range but some entries can have
					// events out of view, so we have to test each event date
					if (viewRangeDates != null && !viewRangeDates.intervalInView(evStartDate.getTime(), evEndDate.getTime())) {
						continue;
					}

					Event event = new Event();
					event.setId(eventId);
					if (timeZoneID != null) {
						event.setTimeZone(TimeZoneHelper.getTimeZone(timeZoneID));
					}
					event.setTimeZoneSensitive(timeZoneSensitive);
					event.setFreeBusy(freeBusy);
					Calendar startCal = Calendar.getInstance();
					startCal.setTime(evStartDate);
					
					Calendar endCal = Calendar.getInstance();
					endCal.setTime(evEndDate);

					startCal = CalendarHelper.convertToTimeZone(startCal,
							timeZone);						
					endCal = CalendarHelper.convertToTimeZone(endCal,
							timeZone);
					
					event.setDtStart(startCal);
					event.setDtEnd(endCal);
					
					events.add(event);
				}
			}
		}
		
		return events;
	}

	private static int parseEventsCount(String eventCountField) {
		int count = 0;
		if (eventCountField != null && !eventCountField.equals("")) {
			count = Integer.parseInt(eventCountField);
		}
		return count;
	}


	private static List<Map> getEventsBeansByEvents(Map entry, List<Event> events, String eventType) {
		List<Map> result = new ArrayList<Map>();
		
		if (events == null) {
			return result;
		}
		
		User user = RequestContextHolder.getRequestContext().getUser();
		TimeZone timeZone = user.getTimeZone();
		
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
		sdf2.setTimeZone(timeZone);

		for (Event event : events) {
			Map eventBean = new HashMap();
			eventBean.put("entry", entry);
			eventBean.put("eventType", eventType);
			eventBean.put("eventid", event.getId() + "_" + event.getDtStart().getTimeInMillis() +"_" + event.getDtEnd().getTimeInMillis());
			eventBean.put(WebKeys.CALENDAR_STARTTIMESTRING, sdf2
					.format(event.getDtStart().getTime()));
			eventBean.put(WebKeys.CALENDAR_ENDTIMESTRING, sdf2.format(event
					.getDtEnd().getTime()));
			eventBean.put("cal_starttime", event.getDtStart().getTime());
			eventBean.put("cal_endtime", event.getDtEnd().getTime());
			eventBean.put("cal_oneDayEvent", event.isOneDayEvent());
			eventBean.put("cal_allDay", event.isAllDayEvent());
			eventBean.put("cal_timeZoneSensitive", event.isTimeZoneSensitive());
			eventBean.put("cal_freeBusy", event.getFreeBusy().name());
			eventBean.put("cal_duration", (event.getDtEnd().getTime()
					.getTime() - event.getDtStart().getTime()
					.getTime()) / 60000);
			result.add(eventBean);
		}
		
		return result;
	}

	private static Event createEvent(Date eventDate, TimeZone timeZone, String type, String entryId) {
		Event event = new Event();
		Calendar gcal = Calendar.getInstance();
		gcal.setTime(eventDate);
		gcal = CalendarHelper.convertToTimeZone(gcal, timeZone);
		event.setDtStart(gcal);
		event.setDtEnd(gcal);
		event.setId(entryId + "-" + type);
		event.setTimeZone(timeZone);
		return event;
	}



	
	/**
	 * CurrentDate is the date selected by the user; we make sure this date is in view.
	 * Current date is always saved in session.
	 * 
	 * Dafault current date is today.
	 *    
	 * @param portletSession
	 * @return
	 */
	public static Date getCalendarCurrentDate(PortletSession portletSession) {
		Date currentDate = null;
		if (portletSession != null) {
			currentDate = (Date) portletSession.getAttribute(WebKeys.CALENDAR_CURRENT_DATE);
		}
		if (currentDate == null) {
			currentDate = new Date();
			if (portletSession != null) {
				portletSession.setAttribute(WebKeys.CALENDAR_CURRENT_DATE, currentDate);	
			}
		}
		
		return currentDate;
	}
	
	public static void setCalendarCurrentDate(PortletSession portletSession, Date currentDate) {
		if (currentDate == null) {
			currentDate = new Date();
		}
		portletSession.setAttribute(WebKeys.CALENDAR_CURRENT_DATE, currentDate);
	}
	
	public static String getCalendarDisplayEventType(PortletSession portletSession) {
		String eventType = null;
		if (portletSession != null) {
			eventType = (String) portletSession.getAttribute(WebKeys.CALENDAR_CURRENT_EVENT_TYPE);
		}
		if (eventType == null) {
			eventType = EVENT_TYPE_DEFAULT;
			if (portletSession != null) {
				portletSession.setAttribute(WebKeys.CALENDAR_CURRENT_EVENT_TYPE, eventType);
			}
		}
		
		return eventType;
	}
	
	public static void setCalendarDisplayEventType(PortletSession portletSession, String eventType) {
		if (eventType == null || !(eventType.equals(EVENT_TYPE_EVENT) ||
				eventType.equals(EVENT_TYPE_ACTIVITY) ||
				eventType.equals(EVENT_TYPE_CREATION))) {
			eventType = EVENT_TYPE_DEFAULT;
		}
		portletSession.setAttribute(WebKeys.CALENDAR_CURRENT_EVENT_TYPE, eventType);
	}

	public static Map setCalendarGrid(PortletSession portletSession, UserProperties userProperties, String stickyComponentId, String gridType, Integer gridSize) {
		Grid currentGrid = getCalendarGrid(portletSession, userProperties, stickyComponentId);
		if (gridType == null || !(gridType.equals(GRID_DAY) ||
				gridType.equals(GRID_MONTH))) {
			if (currentGrid != null) {
				gridType = currentGrid.type;
			} else {
				gridType = GRID_DEFAULT;
			}
		}
		if (gridSize == -1) {
			if (currentGrid != null) { 
				gridSize = currentGrid.size;
			} else {
				gridSize = -1;
			}
		}		
		Map grids = (Map)portletSession.getAttribute(WebKeys.CALENDAR_CURRENT_GRID);
		if (grids == null) {
			grids = new HashMap();
		}
		grids.put(stickyComponentId, new Grid(gridType, gridSize));
		
		portletSession.setAttribute(WebKeys.CALENDAR_CURRENT_GRID, grids);
		return grids;
	}
	
	public static Grid getCalendarGrid(PortletSession portletSession, UserProperties userProperties, String stickyComponentId) {
		Map grids = (Map)portletSession.getAttribute(WebKeys.CALENDAR_CURRENT_GRID);
		if (grids != null && grids.containsKey(stickyComponentId)) {
			return (Grid)grids.get(stickyComponentId);
		}
		grids = (Map)userProperties.getProperty(WebKeys.CALENDAR_CURRENT_GRID);
		if (grids != null && grids.containsKey(stickyComponentId)) {
			return (Grid)grids.get(stickyComponentId);
		}
	
		return null;
	}
		
	public static Date getDate(int year, int month, int dayOfMonth, Date defaultValue) {
		User user = RequestContextHolder.getRequestContext().getUser();
		TimeZone timeZone = user.getTimeZone();
		
		Calendar calendar = new GregorianCalendar(timeZone);
		if (year != -1) {
			calendar.set(Calendar.YEAR, year);
			if (month != -1) {
				calendar.set(Calendar.MONTH, month - 1);
				if (dayOfMonth != -1) {
					calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				}
			}
		} else {
			return defaultValue;
		}
		
		return calendar.getTime();
	}

	public static String getCalendarDayViewType(PortletSession portletSession) {
		String dayViewType = null;
		if (portletSession != null) {
			dayViewType = (String) portletSession.getAttribute(WebKeys.CALENDAR_CURRENT_DAY_VIEW_TYPE);
		}
		if (dayViewType == null) {
			dayViewType = DAY_VIEW_TYPE_DEFAULT;
			if (portletSession != null) {
				portletSession.setAttribute(WebKeys.CALENDAR_CURRENT_DAY_VIEW_TYPE, dayViewType);
			}
		}
		
		return dayViewType;
	}
	
	public static void setCalendarDayViewType(PortletSession portletSession, String dayViewType) {
		if (dayViewType == null || !(dayViewType.equals(DAY_VIEW_TYPE_WORK) ||
				dayViewType.equals(DAY_VIEW_TYPE_FULL))) {
			dayViewType = DAY_VIEW_TYPE_DEFAULT;
		}
		portletSession.setAttribute(WebKeys.CALENDAR_CURRENT_DAY_VIEW_TYPE, dayViewType);
	}
	
	public static String eventToRepeatHumanReadableString (Event event, Locale locale) {
		User user = RequestContextHolder.getRequestContext().getUser();
		
		// in addition to the raw event, we disintangle some of the
		// recurrence stuff
		// to make the jsp page less complex
		DayAndPosition dpa[] = event.getByDay();
		int dpalen = 0;
		// what we'll actually pass in is a list of ints representing the
		// days
		ArrayList bydays = new ArrayList();
		Integer bynum = new Integer(0);
		if (dpa != null) {
			dpalen = dpa.length;
		}
		for (int i = 0; i < dpalen; i++) {
			Integer dd = new Integer(event.getByDay()[i].getDayOfWeek());
			Integer nn = new Integer(event.getByDay()[i].getDayPosition());
			bydays.add(dd);
			bynum = nn;
		}
		
		String freqString = event.getFrequencyString();
		String onString = "";
		String untilString = "";
		String onStringSeparator = "";
		if (freqString == null) {
			// freqString = "does not repeat";
			// freqString = NLT.get("event.no_repeat", locale);
		} else {
			freqString = freqString.toLowerCase();
			if (event.getInterval() > 1) {
				freqString = NLT.get("event.every", locale) + " " + event.getInterval();
				if (event.getFrequency() == Event.DAILY) {
					freqString += " " + NLT.get("event.days", locale);
				}
				if (event.getFrequency() == Event.WEEKLY) {
					freqString += " " + NLT.get("event.weeks", locale);
				}
				if (event.getFrequency() == Event.MONTHLY) {
					freqString += " " + NLT.get("event.months", locale);
				}
				if (event.getFrequency() == Event.YEARLY) {
					freqString += " " + NLT.get("event.years", locale);
				}
			}
			Iterator byDaysIt = bydays.listIterator();

			// format weekly events as comma-separated list of ondays
			if (event.getFrequency() == Event.WEEKLY && byDaysIt.hasNext()) {
				onString += NLT.get("event.occurson", locale) + " ";
				while (byDaysIt.hasNext()) {
					Integer ii = (Integer) byDaysIt.next();
					onString += onStringSeparator + NLT.get(dayNames[ii.intValue()], locale);
					onStringSeparator = ", ";
				}
			}
			// monthly events include the ondaycard stuff
			// note that bydays will now only have one entry (it may be
			// "weekday")
			// and bynum will be meaningful here (again, it is a singleton,
			// not a list)
			if (event.getFrequency() == Event.MONTHLY && byDaysIt.hasNext()) {
				Integer ii = (Integer) byDaysIt.next();
				onString += NLT.get("event.occurson", locale) + " " + NLT.get(nums[bynum.intValue()], locale) + " ";
				onString += NLT.get(dayNames[ii.intValue()], locale);
			}

			if (event.getFrequency() == Event.YEARLY) {
				if (event.getByMonthDay() != null
						&& event.getByMonthDay().length > 0
						&& event.getByMonth() != null
						&& event.getByMonth().length > 0) {
					for (int i = 0; i < event.getByMonthDay().length; i++) {
						onString += NLT.get("event.occurson", locale) + " "
								+ event.getByMonthDay()[i]
								+ (getNumberSuffix(event.getByMonthDay()[i]))
								+ (i < (event.getByMonthDay().length - 1) ? ", "
										: " ");
					}
					onString += "of ";
					for (int i = 0; i < event.getByMonth().length; i++) {
						onString += monthNames[event.getByMonth()[i]]
								+ (i < (event.getByMonth().length - 1) ? ", "
										: " ");
					}
				}

				if (byDaysIt.hasNext() && event.getByMonth() != null
						&& event.getByMonth().length > 0) {
					Integer ii = (Integer) byDaysIt.next();
					onString += NLT.get("event.occurson", locale) + " " + NLT.get(nums[bynum.intValue()], locale) + " ";
					onString += NLT.get(dayNames[ii.intValue()], locale) + " ";

					onString += "of ";
					for (int i = 0; i < event.getByMonth().length; i++) {
						onString += monthNames[event.getByMonth()[i]]
								+ (i < (event.getByMonth().length - 1) ? ", "
										: " ");
					}
				}
			}

		}
		if (event.getFrequencyString() != null) {
			// This is used by text mail, cannot add html untilString += "<br>";
			if (event.getCount() == 0) {
				untilString += NLT.get("event.repeat_forever", locale);
			} else if (event.getCount() == -1) {
				DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG,
						locale);
				dateFormat.setTimeZone(TimeZoneHelper.getTimeZone("GMT"));

				untilString += NLT.get("event.repeat_until", locale) + " " + dateFormat.format(event.getUntil().getTime());
			} else {
				untilString += NLT.get("event.repeat", locale) + " " + event.getCount() + " " + NLT.get("event.times", locale);
			}
		}
		
		if ((freqString != null && !"".equals(freqString)) ||
				(onString != null && !"".equals(onString)) ||
				(untilString != null && !"".equals(untilString))) {
			return freqString + " " + onString + " " + untilString;	
		}
		
		return null;
	}
	
	private static String getNumberSuffix(int i) {
		if (i == 1) {
			return "st";
		} else if (i == 2) {
			return "nd";
		} else if (i == 3) {
			return "rd";
		}
		return "th";
	}

	
}
