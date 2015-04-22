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
package org.kablink.teaming.calendar;

import java.io.Serializable;
import java.text.DateFormat;
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
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.portlet.PortletSession;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.CalendarHelper;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.cal.DayAndPosition;
import org.kablink.util.search.Constants;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings({"unchecked", "unused"})
public class EventsViewHelper {
	private static Log logger = LogFactory.getLog(EventsViewHelper.class);

	/**
	 */
	public static class Grid implements Serializable {
		private static final long serialVersionUID = -7320034020584683226L;
		public String type;
		public Integer size;
		public Grid(String type, Integer size) {
			super();
			this.type = type;
			this.size = size;
		}
		

		/**
		 * ?
		 * 
		 * Need to implement this for hibernate equality.
		 * 
		 * @param obj
		 * 
		 * @return
		 */
	    @Override
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
	private static final String nums[]     = new String[ 7];
	static {
		dayNames[Calendar.SUNDAY]    = "calendar.day.abbrevs.su";
		dayNames[Calendar.MONDAY]    = "calendar.day.abbrevs.mo";
		dayNames[Calendar.TUESDAY]   = "calendar.day.abbrevs.tu";
		dayNames[Calendar.WEDNESDAY] = "calendar.day.abbrevs.we";
		dayNames[Calendar.THURSDAY]  = "calendar.day.abbrevs.th";
		dayNames[Calendar.FRIDAY]    = "calendar.day.abbrevs.fr";
		dayNames[Calendar.SATURDAY]  = "calendar.day.abbrevs.sa";
		
		nums[1] = "calendar.first";
		nums[2] = "calendar.second";
		nums[3] = "calendar.third";
		nums[4] = "calendar.fourth";
		nums[5] = "calendar.fifth";
		nums[6] = "calendar.last";
	}
	
	public static final String EVENT_TYPE_CREATION = "creation";
	public static final String EVENT_TYPE_ACTIVITY = "activity";
	public static final String EVENT_TYPE_EVENT    = "event";
	public static final String EVENT_TYPE_VIRTUAL  = "virtual";
	public static final String EVENT_TYPE_DEFAULT  = EVENT_TYPE_EVENT;
	
	public static final String GRID_DAY     = "day";
	public static final String GRID_MONTH   = "month";
	public static final String GRID_DEFAULT = GRID_MONTH;

	public static final String DAY_VIEW_TYPE_WORK    = "workday";
	public static final String DAY_VIEW_TYPE_FULL    = "fullday";
	public static final String DAY_VIEW_TYPE_DEFAULT = DAY_VIEW_TYPE_WORK;

	/**
	 * ?
	 * 
	 * @param folder
	 * @param searchResults
	 * @param response
	 * @param portletSession
	 * @param onlyTrueEvents
	 * 
	 * @return
	 */
	public static Map getEntryEventsBeans(Binder folder, List searchResults, RenderResponse response, PortletSession portletSession, boolean onlyTrueEvents) {
		Map result = new HashMap();
		
		List events = EventsViewHelper.getCalendarEventsBeans(searchResults,	null, onlyTrueEvents);
		
		Map calendarViewBean = new HashMap();
		calendarViewBean.put("events", events);
		calendarViewBean.put("eventBinderIds", getEventBinderIds(events));
		
		result.put(WebKeys.CALENDAR_VIEWBEAN, calendarViewBean);
		
		return result;
	}

	/**
	 * ?
	 * 
	 * @param bs
	 * @param userId
	 * @param binderId
	 * @param searchResults
	 * @param calendarViewRangeDates
	 * @param portletSession
	 * @param onlyTrueEvents
	 * 
	 * @return
	 */
	public static Map getEventsBeans(AllModulesInjected bs, Long userId, Long binderId, List searchResults, AbstractIntervalView calendarViewRangeDates, PortletSession portletSession, boolean onlyTrueEvents) {
		return
			getEventsBeans(
				searchResults,
				calendarViewRangeDates,
				getCalendarDisplayEventType(bs, userId, binderId), 
				getCalendarDayViewType(portletSession),
				onlyTrueEvents);
	}

	/**
	 * ?
	 * 
	 * @param searchResults
	 * @param intervalView
	 * @param eventType
	 * @param dayViewType
	 * @param onlyTrueEvents
	 * 
	 * @return
	 */
	public static Map getEventsBeans(List searchResults, AbstractIntervalView intervalView, String eventType, String dayViewType, boolean onlyTrueEvents) {
		Map result = new HashMap();
		
		result.put(WebKeys.CALENDAR_CURRENT_VIEW_STARTDATE, intervalView.getStart());
		result.put(WebKeys.CALENDAR_CURRENT_VIEW_ENDDATE,   intervalView.getEnd());

		List events = EventsViewHelper.getCalendarEventsBeans(searchResults, intervalView, onlyTrueEvents);
		
		Map calendarViewBean = new HashMap();
		calendarViewBean.put("monthInfo", intervalView.getCurrentDateMonthInfo());
		calendarViewBean.put("today", new Date());
		calendarViewBean.put("events", events);
		calendarViewBean.put("eventBinderIds", getEventBinderIds(events));
		calendarViewBean.put("eventType", eventType);
		calendarViewBean.put("dayViewType", dayViewType);
		
		result.put(WebKeys.CALENDAR_VIEWBEAN, calendarViewBean);
		
		return result;
	}

	/**
	 * ?
	 * 
	 * @param events
	 * 
	 * @return
	 */
	public static Set getEventBinderIds(List<Map> events) {
		Set<String> eventBinderIds = new TreeSet<String>();
		for (Map e : events) {
			Map entry = (Map) e.get("entry");
			if (entry.get("_binderId") != null) eventBinderIds.add((String)entry.get("_binderId"));
		}
		return eventBinderIds;
	}
	
	/**
	 * ?
	 * 
	 * @param searchResults
	 * @param intervalView
	 * 
	 * @return
	 */
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

	/**
	 * ?
	 * 
	 * @param searchResults
	 * @param viewRangeDates
	 * @param onlyTrueEvents
	 * 
	 * @return
	 */
	public static List getCalendarEventsBeans(List searchResults, AbstractIntervalView viewRangeDates, boolean onlyTrueEvents) {
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
					events.add(createEvent(creationDate, timeZone, EVENT_TYPE_CREATION, ((String) entry.get(Constants.DOCID_FIELD))));
					result.addAll(getEventsBeansByEvents(entry, events, EVENT_TYPE_CREATION));
				}
	
				// Add the activity date as an event
				Date lastActivityDate = (Date) entry.get(Constants.LASTACTIVITY_FIELD);
				if (lastActivityDate != null && (viewRangeDates == null || viewRangeDates.dateInView(lastActivityDate))) {
					List events = new ArrayList();
					events.add(createEvent(lastActivityDate, timeZone, EVENT_TYPE_ACTIVITY, ((String) entry.get(Constants.DOCID_FIELD))));
					result.addAll(getEventsBeansByEvents(entry, events, EVENT_TYPE_ACTIVITY));
				}
			}
			
			List<Event> events = parseSearchResultEntryToEvents(entry, viewRangeDates);
			
			result.addAll(getEventsBeansByEvents(entry, events, EVENT_TYPE_EVENT));
		}
		return result;
	}
	
	/*
	 * One entry can contain many events.
	 */
	private static List<Event> parseSearchResultEntryToEvents(Map entry, AbstractIntervalView viewRangeDates) {
		User user = RequestContextHolder.getRequestContext().getUser();
		TimeZone timeZone = user.getTimeZone();
		String dtfPattern = "yyyyMMddHHmm";
		DateTimeFormatter dtfRaw = DateTimeFormat.forPattern(dtfPattern);
		DateTimeFormatter dtfInTZ = DateTimeFormat.forPattern(dtfPattern).withZone(DateTimeZone.forTimeZone(timeZone));
		
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
			
			// If we have any recurrence dates..
			if (recurrenceDatesField != null) {
				// ...scan them...
				String[] recurrenceDates = recurrenceDatesField.split(",");
				for (int recCounter = 0; recCounter < recurrenceDates.length; recCounter++) {
					// ...and parse their start and end dates in both
					// ...raw and time zone based formats.
					String recurrenceDatesInstance = recurrenceDates[recCounter];
					if ((null == recurrenceDatesInstance) || (0 == recurrenceDatesInstance.length())) {
						continue;
					}
					String[] recurrenceStartEndTime = recurrenceDatesInstance.split(" ");
					
					String evStartTime = recurrenceStartEndTime[0];
					Date evStartDateInTZ = null;
					Date evStartDateRaw = null;

					String evEndTime = recurrenceStartEndTime[1];
					Date evEndDateInTZ = null;
					Date evEndDateRaw = null;
					
					Exception ex = null;
					try {
						evStartDateInTZ = dtfInTZ.parseDateTime(evStartTime).toDate();
						evStartDateRaw = dtfRaw.parseDateTime(evStartTime).toDate();
						
						evEndDateInTZ = dtfInTZ.parseDateTime(evEndTime).toDate();
						evEndDateRaw = dtfRaw.parseDateTime(evEndTime).toDate();
					} catch (UnsupportedOperationException e) {
						ex = e;
					} catch (IllegalArgumentException e) {
						ex = e;
					}
					if (null != ex) {
						logger.warn("Event recurrence date in search index has wrong format [" + evStartTime + "] or [" + evEndTime + "].");
						
						evStartDateInTZ =
						evStartDateRaw =
						evEndDateInTZ =
						evEndDateRaw = new Date();
					}
					
					// In results we have only entries with events in
					// view range but some entries can have events out
					// of view, so we have to test each event date.
					if (viewRangeDates != null && !viewRangeDates.intervalInViewInTZ(evStartDateInTZ.getTime(), evEndDateInTZ.getTime())) {
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
					startCal.setTime(evStartDateRaw);
					
					Calendar endCal = Calendar.getInstance();
					endCal.setTime(evEndDateRaw);

					startCal = CalendarHelper.convertToTimeZone(startCal, timeZone);						
					endCal = CalendarHelper.convertToTimeZone(endCal, timeZone);
					
					event.setDtStart(startCal);
					event.setDtEnd(endCal);
					
					events.add(event);
				}
			}
		}
		
		return events;
	}

	/*
	 */
	private static int parseEventsCount(String eventCountField) {
		int count = 0;
		if (eventCountField != null && !eventCountField.equals("")) {
			count = Integer.parseInt(eventCountField);
		}
		return count;
	}

	/*
	 */
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
			// If the event doesn't have both a start and end date...
			Calendar start = event.getLogicalStart();
			Calendar end   = event.getLogicalEnd();
			if ((null == start) || (null == end)) {
				// ...ignore it as we don't know where to display it in
				// ...a calendar.
				continue;
			}
			
			Map eventBean = new HashMap();
			eventBean.put("entry", entry);
			eventBean.put("eventType", eventType);
			eventBean.put("eventid", event.getId() + "_" + start.getTimeInMillis() +"_" + end.getTimeInMillis());
			eventBean.put(WebKeys.CALENDAR_STARTTIMESTRING, sdf2
					.format(start.getTime()));
			eventBean.put(WebKeys.CALENDAR_ENDTIMESTRING, sdf2.format(event
					.getLogicalEnd().getTime()));
			eventBean.put("cal_starttime", start.getTime());
			eventBean.put("cal_endtime", end.getTime());
			eventBean.put("cal_oneDayEvent", event.isOneDayEvent());
			eventBean.put("cal_allDay", event.isAllDayEvent());
			eventBean.put("cal_timeZoneSensitive", event.isTimeZoneSensitive());
			eventBean.put("cal_freeBusy", event.getFreeBusy().name());
			eventBean.put("cal_duration", (end.getTime()
					.getTime() - start.getTime()
					.getTime()) / 60000);
			result.add(eventBean);
		}
		
		return result;
	}

	/*
	 */
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
	 * Default current date is today.
	 *    
	 * @param portletSession
	 * 
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

	/**
	 * ?
	 * 
	 * @param portletSession
	 * @param currentDate
	 */
	public static void setCalendarCurrentDate(PortletSession portletSession, Date currentDate) {
		if (currentDate == null) {
			currentDate = new Date();
		}
		portletSession.setAttribute(WebKeys.CALENDAR_CURRENT_DATE, currentDate);
	}

	/**
	 * Get calendar display event type.
	 * 
	 * @param bs
	 * @param userId
	 * @param binderId
	 * 
	 * @return
	 */
	public static String getCalendarDisplayEventType(AllModulesInjected bs, Long userId, Long binderId) {
		UserProperties userProps = bs.getProfileModule().getUserProperties(userId, binderId);
		String eventType = (String)userProps.getProperty(WebKeys.CALENDAR_MODE_PREF);
		if (eventType == null) {
			eventType = EVENT_TYPE_DEFAULT;
			setCalendarDisplayEventType(bs, userId, binderId, eventType);
		}
		
		return eventType;
	}
	
	/**
	 * Store calendar display event type.
	 * 
	 * @param bs
	 * @param userId
	 * @param binderId
	 * @param eventType
	 */
	public static void setCalendarDisplayEventType(AllModulesInjected bs, Long userId, Long binderId, String eventType) {
		if (eventType == null) {
			eventType = getCalendarDisplayEventType(bs, userId, binderId);
			if (eventType == null || !(eventType.equals(EVENT_TYPE_EVENT) ||
					eventType.equals(EVENT_TYPE_ACTIVITY) ||
					eventType.equals(EVENT_TYPE_CREATION) ||
					eventType.equals(EVENT_TYPE_VIRTUAL))) {
				eventType = EVENT_TYPE_DEFAULT;
			}
		}
		bs.getProfileModule().setUserProperty(userId, binderId, WebKeys.CALENDAR_MODE_PREF, eventType);
	}

	/**
	 * ?
	 * 
	 * @param request
	 * @param userProperties
	 * @param stickyComponentId
	 * @param gridType
	 * @param gridSize
	 * 
	 * @return
	 */
	public static Map setCalendarGrid(HttpServletRequest request, UserProperties userProperties, String stickyComponentId, String gridType, Integer gridSize) {
		return setCalendarGridImpl(null, WebHelper.getRequiredSession(request), userProperties, stickyComponentId, gridType, gridSize);
	}

	/**
	 * ?
	 * 
	 * @param ps
	 * @param userProperties
	 * @param stickyComponentId
	 * @param gridType
	 * @param gridSize
	 * 
	 * @return
	 */
	public static Map setCalendarGrid(PortletSession ps, UserProperties userProperties, String stickyComponentId, String gridType, Integer gridSize) {
		return setCalendarGridImpl(ps, null, userProperties, stickyComponentId, gridType, gridSize);
	}

	/*
	 */
	private static Map setCalendarGridImpl(PortletSession ps, HttpSession hs, UserProperties userProperties, String stickyComponentId, String gridType, Integer gridSize) {
		Grid currentGrid = getCalendarGridImpl(ps, hs, userProperties, stickyComponentId);
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
		Map grids = null;
		if      (null != ps) grids = ((Map) ps.getAttribute(WebKeys.CALENDAR_CURRENT_GRID, PortletSession.APPLICATION_SCOPE));
		else if (null != hs) grids = ((Map) hs.getAttribute(WebKeys.CALENDAR_CURRENT_GRID                                  ));
		if (null == grids) {
			grids = new HashMap();
		}
		grids.put(stickyComponentId, new Grid(gridType, gridSize));
		
		if      (null != ps) ps.setAttribute(WebKeys.CALENDAR_CURRENT_GRID, grids, PortletSession.APPLICATION_SCOPE);
		else if (null != hs) hs.setAttribute(WebKeys.CALENDAR_CURRENT_GRID, grids                                  );
		
		return grids;
	}

	/**
	 * ?
	 * 
	 * @param request
	 * @param userProperties
	 * @param stickyComponentId
	 * 
	 * @return
	 */
	public static Grid getCalendarGrid(HttpServletRequest request, UserProperties userProperties, String stickyComponentId) {
		return getCalendarGridImpl(null, WebHelper.getRequiredSession(request), userProperties, stickyComponentId);
	}

	/**
	 * ? 
	 * @param ps
	 * @param userProperties
	 * @param stickyComponentId
	 * 
	 * @return
	 */
	public static Grid getCalendarGrid(PortletSession ps, UserProperties userProperties, String stickyComponentId) {
		return getCalendarGridImpl(ps, null, userProperties, stickyComponentId);
	}
	
	/*
	 */
	private static Grid getCalendarGridImpl(PortletSession ps, HttpSession hs, UserProperties userProperties, String stickyComponentId) {
		Map grids = null;
		if      (null != ps) grids = ((Map) ps.getAttribute(WebKeys.CALENDAR_CURRENT_GRID, PortletSession.APPLICATION_SCOPE));
		else if (null != hs) grids = ((Map) hs.getAttribute(WebKeys.CALENDAR_CURRENT_GRID                                  ));
		if ((null != grids) && grids.containsKey(stickyComponentId)) {
			return ((Grid) grids.get(stickyComponentId));
		}
		
		grids = ((Map) userProperties.getProperty(WebKeys.CALENDAR_CURRENT_GRID));
		if ((null != grids) && grids.containsKey(stickyComponentId)) {
			return ((Grid) grids.get(stickyComponentId));
		}
	
		return null;
	}

	/**
	 * ?
	 * 
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @param defaultValue
	 * 
	 * @return
	 */
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

	/**
	 * ?
	 * 
	 * @param request
	 * 
	 * @return
	 */
	public static String getCalendarDayViewType(HttpServletRequest request) {
		return getCalendarDayViewTypeImpl(null, WebHelper.getRequiredSession(request));
	}

	/**
	 * ?
	 * 
	 * @param ps
	 * 
	 * @return
	 */
	public static String getCalendarDayViewType(PortletSession ps) {
		return getCalendarDayViewTypeImpl(ps, null);
	}
	
	/*
	 */
	private static String getCalendarDayViewTypeImpl(PortletSession ps, HttpSession hs) {
		String dayViewType = null;
		if      (null != null) dayViewType = ((String) ps.getAttribute(WebKeys.CALENDAR_CURRENT_DAY_VIEW_TYPE, PortletSession.APPLICATION_SCOPE));
		else if (null != hs)   dayViewType = ((String) hs.getAttribute(WebKeys.CALENDAR_CURRENT_DAY_VIEW_TYPE                                  ));
		if (dayViewType == null) {
			dayViewType = DAY_VIEW_TYPE_DEFAULT;
			if      (null != ps) ps.setAttribute(WebKeys.CALENDAR_CURRENT_DAY_VIEW_TYPE, dayViewType, PortletSession.APPLICATION_SCOPE);
			else if (null != hs) hs.setAttribute(WebKeys.CALENDAR_CURRENT_DAY_VIEW_TYPE, dayViewType                                  ); 
		}
		
		return dayViewType;
	}

	/**
	 * ?
	 * 
	 * @param request
	 * @param dayViewType
	 */
	public static void setCalendarDayViewType(HttpServletRequest request, String dayViewType) {
		setCalendarDayViewTypeImpl(null, WebHelper.getRequiredSession(request), dayViewType);
	}

	/**
	 * ?
	 * 
	 * @param ps
	 * @param dayViewType
	 */
	public static void setCalendarDayViewType(PortletSession ps, String dayViewType) {
		setCalendarDayViewTypeImpl(ps, null, dayViewType);
	}
	
	/*
	 */
	private static void setCalendarDayViewTypeImpl(PortletSession ps, HttpSession hs, String dayViewType) {
		// If we weren't given a day view or we don't recognize it...
		if ((null == dayViewType) ||
				(!(dayViewType.equals(DAY_VIEW_TYPE_WORK)) && (!(dayViewType.equals(DAY_VIEW_TYPE_FULL))))) {
			// ...use the default...
			dayViewType = DAY_VIEW_TYPE_DEFAULT;
		}
		
		// ...and save it.
		if      (null != ps) ps.setAttribute(WebKeys.CALENDAR_CURRENT_DAY_VIEW_TYPE, dayViewType, PortletSession.APPLICATION_SCOPE);
		else if (null != hs) hs.setAttribute(WebKeys.CALENDAR_CURRENT_DAY_VIEW_TYPE, dayViewType                                  );
	}

	/**
	 * ?
	 * 
	 * @param event
	 * @param locale
	 * 
	 * @return
	 */
	public static String eventToRepeatHumanReadableString(Event event, Locale locale) {
		User user = RequestContextHolder.getRequestContext().getUser();
		
		// In addition to the raw event, we disintangle some of the
		// recurrence stuff to make the jsp page less complex.
		DayAndPosition dpa[] = event.getByDay();
		int dpalen = ((null == dpa) ? 0 : dpa.length);
		
		// What we'll actually pass in is a list of integers
		// representing the days.
		ArrayList bydays = new ArrayList();
		Integer bynum = new Integer(0);
		for (int i = 0; i < dpalen; i += 1) {
			Integer dd = new Integer(event.getByDay()[i].getDayOfWeek());
			Integer nn = new Integer(event.getByDay()[i].getDayPosition());
			bydays.add(dd);
			bynum = nn;
		}
		
		String freqString        = event.getFrequencyString();
		String onString          = "";
		String untilString       = "";
		String onStringSeparator = "";
		if (null == freqString) {
			// freqString = "does not repeat";
			// freqString = NLT.get("event.no_repeat", locale);
		}
		
		else {
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
			else {
				String tag = ("event.editor.frequency." + freqString);
				String l10nFreq = NLT.get(tag, locale);
				if (!(l10nFreq.equalsIgnoreCase(tag))) {
					freqString = l10nFreq;
				}
			}

			// Format weekly events as comma-separated list of onDays.
			Iterator byDaysIt = bydays.listIterator();
			if (event.getFrequency() == Event.WEEKLY && byDaysIt.hasNext()) {
				onString += NLT.get("event.occurson", locale) + " ";
				while (byDaysIt.hasNext()) {
					Integer ii = (Integer) byDaysIt.next();
					onString += onStringSeparator + NLT.get(dayNames[ii.intValue()], locale);
					onStringSeparator = ", ";
				}
			}
			
			// Monthly events include the onDayCard stuff.  Note that
			// byDays will now only have one entry (it may be weekday)
			// and byNum will be meaningful here (again, it is a
			// singleton, not a list.)
			if (event.getFrequency() == Event.MONTHLY && byDaysIt.hasNext()) {
				Integer ii = (Integer) byDaysIt.next();
				onString += NLT.get("event.occurson", locale) + " " + NLT.get(nums[getNumsIndex(bynum.intValue())], locale) + " ";
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

				if (byDaysIt.hasNext() && (event.getByMonth() != null) && (event.getByMonth().length > 0)) {
					Integer ii = (Integer) byDaysIt.next();
					onString += NLT.get("event.occurson", locale) + " " + NLT.get(nums[getNumsIndex(bynum.intValue())], locale) + " ";
					onString += NLT.get(dayNames[ii.intValue()], locale) + " ";

					onString += "of ";
					for (int i = 0; i < event.getByMonth().length; i++) {
						onString += monthNames[event.getByMonth()[i]] + (i < (event.getByMonth().length - 1) ? ", " : " ");
					}
				}
			}
		}

		if (event.getFrequencyString() != null) {
			// This is used by text mail, cannot add HTML
			// untilString += "<br>";
			if (event.getCount() == 0) {
				untilString += NLT.get("event.repeat_forever", locale);
			}
			else if (event.getCount() == -1) {
				DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, locale);
				dateFormat.setTimeZone(TimeZoneHelper.getTimeZone("GMT"));
				untilString += NLT.get("event.repeat_until", locale) + " " + dateFormat.format(event.getUntil().getTime());
			}
			else {
				untilString += NLT.get("event.repeat", locale) + " " + event.getCount() + " " + NLT.get("event.times", locale);
			}
		}
		
		if (    (freqString  != null && !"".equals(freqString)) ||
				(onString    != null && !"".equals(onString))   ||
				(untilString != null && !"".equals(untilString))) {
			return (freqString + " " + onString + " " + untilString);	
		}
		
		return null;
	}
	
	/*
	 */
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

	/*
	 */
	private static int getNumsIndex(int byNum) {
		if ((-1) == byNum) {
			byNum = 6;
		}
		return byNum;
	}
}
