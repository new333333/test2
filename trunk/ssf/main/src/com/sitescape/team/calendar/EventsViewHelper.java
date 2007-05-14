/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.lucene.document.DateTools;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.folder.index.IndexUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.portlet.forum.ListFolderController;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.util.CalendarHelper;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.DateHelper;
import com.sitescape.team.web.util.WebHelper;

public class EventsViewHelper {

	private static Log logger = LogFactory.getLog(EventsViewHelper.class);
	
	public static final String[] monthNames = { 
		NLT.get("calendar.january"),
		NLT.get("calendar.february"),
		NLT.get("calendar.march"),
		NLT.get("calendar.april"),
		NLT.get("calendar.may"),
		NLT.get("calendar.june"),
		NLT.get("calendar.july"),
		NLT.get("calendar.august"),
		NLT.get("calendar.september"),
		NLT.get("calendar.october"),
		NLT.get("calendar.november"),
		NLT.get("calendar.december")
	};
	
	public static final String[] monthNamesShort = { 
		NLT.get("calendar.abbreviation.january"),
		NLT.get("calendar.abbreviation.february"),
		NLT.get("calendar.abbreviation.march"),
		NLT.get("calendar.abbreviation.april"),
		NLT.get("calendar.abbreviation.may"),
		NLT.get("calendar.abbreviation.june"),
		NLT.get("calendar.abbreviation.july"),
		NLT.get("calendar.abbreviation.august"),
		NLT.get("calendar.abbreviation.september"),
		NLT.get("calendar.abbreviation.october"),
		NLT.get("calendar.abbreviation.november"),
		NLT.get("calendar.abbreviation.december")
	};
	
	public static final String EVENT_TYPE_CREATION = "creation";
	
	public static final String EVENT_TYPE_ACTIVITY = "activity";
	
	public static final String EVENT_TYPE_EVENT = "event";

	public static final String EVENT_TYPE_DEFAULT = EVENT_TYPE_EVENT;
	
	

	
	public static final String GRID_DAY = "day";
	
	public static final String GRID_MONTH = "month";
	
	public static final String GRID_DEFAULT = GRID_MONTH;
	
	public static void getEvents(Date currentDate,
			CalendarViewRangeDates calendarViewRangeDates, Binder folder,
			List entrylist, Map model, RenderResponse response, PortletSession portletSession) {

		model.put(WebKeys.CALENDAR_CURRENT_VIEW_STARTDATE,
				calendarViewRangeDates.getStartViewCal().getTime());
		model.put(WebKeys.CALENDAR_CURRENT_VIEW_ENDDATE, calendarViewRangeDates
				.getEndViewCal().getTime());


		List events = EventsViewHelper.getCalendarEvents(entrylist,
				calendarViewRangeDates);
		
		Map calendarViewBean = new HashMap();
		calendarViewBean.put("dayHeaders", getDayHeaders());
		calendarViewBean.put("monthNames", Arrays.asList(EventsViewHelper.monthNames));
		calendarViewBean.put("monthNamesShort", Arrays.asList(EventsViewHelper.monthNamesShort));
		calendarViewBean.put("monthInfo", calendarViewRangeDates.getCurrentDateMonthInfo());
		calendarViewBean.put("today", new Date());
		calendarViewBean.put("events", events);
		calendarViewBean.put("eventType", getCalendarDisplayEventType(portletSession));
		
		
		model.put(WebKeys.CALENDAR_VIEWBEAN, calendarViewBean);
	}


	private static List getCalendarEvents(List entrylist,
			CalendarViewRangeDates viewRangeDates) {
		
		List result = new ArrayList();
		
		User user = RequestContextHolder.getRequestContext().getUser();
		TimeZone timeZone = user.getTimeZone();

		Iterator entryIterator = entrylist.iterator();
		while (entryIterator.hasNext()) {
			Map entry = (HashMap) entryIterator.next();
			int eventsCount = parseEventsCount((String) entry.get(EntityIndexUtils.EVENT_COUNT_FIELD));
			
			// Add the creation date as an event
			Date creationDate = (Date) entry.get(EntityIndexUtils.CREATION_DATE_FIELD);
			if (viewRangeDates.dateInView(creationDate)) {
				result.add(getEventBean(
						createEvent(creationDate, timeZone, EVENT_TYPE_CREATION, (String)entry.get(EntityIndexUtils.DOCID_FIELD)), 
						entry, EVENT_TYPE_CREATION));
			}

			// Add the activity date as an event
			Date lastActivityDate = (Date) entry.get(IndexUtils.LASTACTIVITY_FIELD);
			if (viewRangeDates.dateInView(lastActivityDate)) {
				result.add(getEventBean(
						createEvent(lastActivityDate, timeZone, EVENT_TYPE_ACTIVITY, (String)entry.get(EntityIndexUtils.DOCID_FIELD)), 
						entry, EVENT_TYPE_ACTIVITY));
			}
			
			result.addAll(getEntryEvents(entry, eventsCount, viewRangeDates));
		}
		return result;
	}

	private static List getEntryEvents(Map entry, int eventsCount, CalendarViewRangeDates viewRangeDates) {
		User user = RequestContextHolder.getRequestContext().getUser();
		TimeZone timeZone = user.getTimeZone();
		
		List events = new ArrayList();
		
		
		// Add the events
		// look through the custom attrs of this entry for any of type EVENT
		for (int j = 0; j < eventsCount; j++) {
			String name = (String) entry.get(EntityIndexUtils.EVENT_FIELD + j);

			String recurrenceDatesField = (String) entry.get(name + BasicIndexUtils.DELIMITER + EntityIndexUtils.EVENT_RECURRENCE_DATES_FIELD);
			String eventId = (String) entry.get(name + BasicIndexUtils.DELIMITER + EntityIndexUtils.EVENT_ID);
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

					Event event = new Event();
					event.setId(eventId);
					Calendar startCal = Calendar.getInstance();
					startCal.setTime(evStartDate);
					
					Calendar endCal = Calendar.getInstance();
					endCal.setTime(evEndDate);

					long duration = ((endCal.getTime()
							.getTime() - startCal.getTime()
							.getTime()) / 60000);
					
					if (duration > 0) {
						// no duration -> all day event, no time, no time zone
						startCal = CalendarHelper.convertToTimeZone(startCal,
								timeZone);						
						endCal = CalendarHelper.convertToTimeZone(endCal,
								timeZone);
					}
					
					event.setDtStart(startCal);
					event.setDtEnd(endCal);
					
					
					// in results we have only entries with events in view range but some entries can have
					// events out of view, so we have to test each event date
					if (viewRangeDates.eventInView(evStartDate.getTime(), evEndDate.getTime())) {
						events.add(getEventBean(event, entry, EVENT_TYPE_EVENT));
					}
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


	private static Map getEventBean(Event event, Map entry, String eventType) {
		Map eventBean = new HashMap();
		
		User user = RequestContextHolder.getRequestContext().getUser();
		TimeZone timeZone = user.getTimeZone();
		
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
		sdf2.setTimeZone(timeZone);
			
		eventBean.put("entry", entry);
		eventBean.put("eventType", eventType);
		eventBean.put("eventid", event.getId() + "_" + event.getDtStart().getTimeInMillis() +"_" + event.getDtEnd().getTimeInMillis());
		eventBean.put("entry_tostring", entry.get(BasicIndexUtils.UID_FIELD).toString());
		eventBean.put(WebKeys.CALENDAR_STARTTIMESTRING, sdf2
				.format(event.getDtStart().getTime()));
		eventBean.put(WebKeys.CALENDAR_ENDTIMESTRING, sdf2.format(event
				.getDtEnd().getTime()));
		eventBean.put("cal_starttime", event.getDtStart().getTime());
		eventBean.put("cal_endtime", event.getDtEnd().getTime());
		eventBean.put("cal_duration", (event.getDtEnd().getTime()
				.getTime() - event.getDtStart().getTime()
				.getTime()) / 60000);
		
		return eventBean;
	}

	private static Event createEvent(Date eventDate, TimeZone timeZone, String type, String entryId) {
		Event event = new Event();
		Calendar gcal = Calendar.getInstance();
		gcal.setTime(eventDate);
		gcal = CalendarHelper.convertToTimeZone(gcal, timeZone);
		event.setDtStart(gcal);
		event.setDtEnd(gcal);
		event.setId(entryId + "-" + type);
		return event;
	}

	private static List getDayHeaders() {
		List dayheaders = new ArrayList();
		
		User user = RequestContextHolder.getRequestContext().getUser();
		TimeZone timeZone = user.getTimeZone();
		
		Calendar loopCal = new GregorianCalendar(timeZone);
		int j = loopCal.getFirstDayOfWeek();
		for (int i = 0; i < 7; i++) {
			dayheaders.add(DateHelper.getDayAbbrevString(j));
			// we don't know for sure that the d-o-w won't wrap, so prepare to
			// wrap it
			if (j++ == 7) {
				j = 0;
			}
		}
		
		return dayheaders;
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
			portletSession.setAttribute(WebKeys.CALENDAR_CURRENT_DATE, currentDate);	
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
			portletSession.setAttribute(WebKeys.CALENDAR_CURRENT_EVENT_TYPE, eventType);	
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

	public static String setCalendarGridType(PortletSession portletSession, String gridType) {
		if (gridType == null || !(gridType.equals(GRID_DAY) ||
				gridType.equals(GRID_MONTH))) {
			
			gridType = getCalendarGridType(portletSession);
			if (gridType == null) {
				gridType = GRID_DEFAULT;
			}
		}
		portletSession.setAttribute(WebKeys.CALENDAR_CURRENT_GRID_TYPE, gridType);
		return gridType;
	}
	
	public static String getCalendarGridType(PortletSession portletSession) {
		return (String)portletSession.getAttribute(WebKeys.CALENDAR_CURRENT_GRID_TYPE);
	}
	
	public static int setCalendarGridSize(PortletSession portletSession, Integer gridSize) {
		if (gridSize == -1) {
			gridSize = getCalendarGridSize(portletSession);
			if (gridSize == null) {
				gridSize = -1;
			}
		}
		portletSession.setAttribute(WebKeys.CALENDAR_CURRENT_GRID_SIZE, gridSize);
		return gridSize;
	}
	
	public static Integer getCalendarGridSize(PortletSession portletSession) {
		return (Integer)portletSession.getAttribute(WebKeys.CALENDAR_CURRENT_GRID_SIZE);
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
	
}
