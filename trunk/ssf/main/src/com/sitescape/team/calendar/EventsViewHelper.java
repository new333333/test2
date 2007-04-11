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

public class EventsViewHelper {

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
	
	private static final String EVENT_TYPE_CREATION = "creation";
	
	private static final String EVENT_TYPE_ACTIVITY = "activity";
	
	private static final String EVENT_TYPE_EVENT = "event";
	
	public static void getEvents(Date currentDate,
			CalendarViewRangeDates calendarViewRangeDates, Binder folder,
			List entrylist, Map model, RenderResponse response) {

		model.put(WebKeys.CALENDAR_CURRENT_VIEW_STARTDATE,
				calendarViewRangeDates.getStartViewCal().getTime());
		model.put(WebKeys.CALENDAR_CURRENT_VIEW_ENDDATE, calendarViewRangeDates
				.getEndViewCal().getTime());

		Map calendarEventDates = EventsViewHelper.getCalendarEvents(entrylist,
				calendarViewRangeDates.getStartViewExtWindow(),
				calendarViewRangeDates.getEndViewExtWindow());
		model.put(WebKeys.CALENDAR_EVENTDATES, calendarEventDates);

		EventsViewHelper.getCalendarViewBean(folder, calendarViewRangeDates, calendarEventDates, model);
	}

	/*
	 * Map: key - event start date value - list of maps: key: "event", value:
	 * Event object (event start time and end time) key: "entry", value: entry
	 * (from search result)
	 * 
	 * Entry modifications are also added as events (start date = modification
	 * date).
	 */
	private static Map getCalendarEvents(List entrylist,
			Calendar startViewDate, Calendar endViewDate) {

		User user = RequestContextHolder.getRequestContext().getUser();
		TimeZone timeZone = user.getTimeZone();

		long startMilis = startViewDate.getTime().getTime();
		long endMilis = endViewDate.getTime().getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.setTimeZone(timeZone);
		Map results = new HashMap();

		Iterator entryIterator = entrylist.iterator();
		while (entryIterator.hasNext()) {
			Map e = (HashMap) entryIterator.next();

			// parse event counter field
			String ec = (String) e.get(EntityIndexUtils.EVENT_COUNT_FIELD);
			int count = 0;
			if (ec == null || ec.equals(""))
				ec = "0";
			count = new Integer(ec).intValue();

			// Add the modification date as an event
			Date creationDate = (Date) e
					.get(EntityIndexUtils.CREATION_DATE_FIELD);
			
			Date lastActivityDate = (Date) e
					.get(IndexUtils.LASTACTIVITY_FIELD);
			
			long thisDateMillis = creationDate.getTime();
			if (startMilis < thisDateMillis && thisDateMillis < endMilis) {
				String dateKey = sdf.format(creationDate);
				List entryList = (List) results.get(dateKey);
				if (entryList == null) {
					entryList = new ArrayList();
				}
				
				Map creation = new HashMap();
				creation.put("event", createEvent(creationDate, timeZone, EVENT_TYPE_CREATION, (String)e.get(EntityIndexUtils.DOCID_FIELD)));
				creation.put("entry", e);
				creation.put("eventType", EVENT_TYPE_CREATION);
				entryList.add(creation);

				results.put(dateKey, entryList);
			}
			
			thisDateMillis = lastActivityDate.getTime();
			if (startMilis < thisDateMillis && thisDateMillis < endMilis) {
				String dateKey = sdf.format(creationDate);
				List entryList = (List) results.get(dateKey);
				if (entryList == null) {
					entryList = new ArrayList();
				}
				
				Map activity = new HashMap();
				activity.put("event", createEvent(lastActivityDate, timeZone, EVENT_TYPE_ACTIVITY, (String)e.get(EntityIndexUtils.DOCID_FIELD)));
				activity.put("entry", e);
				activity.put("eventType", EVENT_TYPE_ACTIVITY);
				entryList.add(activity);

				results.put(dateKey, entryList);
			}			


			// Add the events
			// look through the custom attrs of this entry for any of type EVENT
			for (int j = 0; j < count; j++) {
				String name = (String) e.get(EntityIndexUtils.EVENT_FIELD + j);

				String recurrenceDatesField = (String) e.get(name
						+ BasicIndexUtils.DELIMITER
						+ EntityIndexUtils.EVENT_RECURRENCE_DATES_FIELD);
				String eventId = (String) e.get(name
						+ BasicIndexUtils.DELIMITER
						+ EntityIndexUtils.EVENT_ID);
				if (recurrenceDatesField != null) {
					String[] recurrenceDates = recurrenceDatesField.split(",");
					for (int recCounter = 0; recCounter < recurrenceDates.length; recCounter++) {
						String[] recurrenceStartEndTime = recurrenceDates[recCounter]
								.split(" ");
						Date evStartDate = null;
						Date evEndDate = null;
						try {
							evStartDate = DateTools
									.stringToDate(recurrenceStartEndTime[0]);
							evEndDate = DateTools
									.stringToDate(recurrenceStartEndTime[1]);
						} catch (ParseException parseExc) {
							evStartDate = new Date();
							evEndDate = new Date();
						}

						Event ev = new Event();
						ev.setId(eventId);
						Calendar startCal = new GregorianCalendar();
						startCal.setTime(evStartDate);
						
						Calendar endCal = new GregorianCalendar();
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
						
						ev.setDtStart(startCal);
						ev.setDtEnd(endCal);
						
						long startDateMillis = evStartDate.getTime();
						long endDateMillis = evEndDate.getTime();
						if (!(endDateMillis < startMilis ||
								endMilis < startDateMillis)) {
							String dateKey = sdf.format(evStartDate);
							List entryList = (List) results.get(dateKey);
							if (entryList == null) {
								entryList = new ArrayList();
							}							
							
							Map res = new HashMap();
							res.put("event", ev);
							res.put("entry", e);
							res.put("eventType", EVENT_TYPE_EVENT);
							entryList.add(res);

							results.put(dateKey, entryList);
						}
					}
				}
			}
		}
		return results;
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

	/**
	 * Populate the bean for monthly calendar view.
	 * Bean contains: day headers, month info, today date and list of events.
	 * 
	 */
	private static void getCalendarViewBean(Binder folder, CalendarViewRangeDates calendarViewRangeDates, Map eventDates, Map model) {
		User user = RequestContextHolder.getRequestContext().getUser();
		TimeZone timeZone = user.getTimeZone();

		Map monthBean = new HashMap();
		monthBean.put("dayHeaders", getDayHeaders());
		monthBean.putAll(getMonthNames());
		monthBean.put("monthInfo", calendarViewRangeDates.getCurrentDateMonthInfo());
		
		Map today = new HashMap();
		today.put("date", new Date());
		monthBean.put("today", today);
		
		List eventsList = new ArrayList();
		Iterator eventsIt = eventDates.entrySet().iterator();
		while (eventsIt.hasNext()) {
			Map.Entry mapEntry = (Map.Entry)eventsIt.next(); 
			String dateKey = (String)mapEntry.getKey();
			List evList = (List) mapEntry.getValue();
				
			Iterator evIt = evList.iterator();
			while (evIt.hasNext()) {
				// thisMap is the next entry, event pair
				HashMap thisMap = (HashMap) evIt.next();
				// dataMap is the map of data for the bean, to be keyed by
				// the time
				HashMap dataMap = new HashMap();
				HashMap e = (HashMap) thisMap.get("entry");
				Event ev = (Event) thisMap.get("event");
				SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
				sdf2.setTimeZone(timeZone);
				// we build up the dataMap for this instance
				
				long duration = ((ev.getDtEnd().getTime()
						.getTime() - ev.getDtStart().getTime()
						.getTime()) / 60000);
				
				dataMap.put("entry", e);
				dataMap.put("eventType", thisMap.get("eventType"));
				dataMap.put("eventid", ev.getId());
				dataMap.put("entry_tostring", e.get(
						BasicIndexUtils.UID_FIELD).toString());
				dataMap.put(WebKeys.CALENDAR_STARTTIMESTRING, sdf2
						.format(ev.getDtStart().getTime()));
				dataMap.put(WebKeys.CALENDAR_ENDTIMESTRING, sdf2.format(ev
						.getDtEnd().getTime()));
				dataMap.put("cal_starttime", ev.getDtStart().getTime());
				dataMap.put("cal_endtime", ev.getDtEnd().getTime());
				dataMap
						.put("cal_duration", duration);
				
				eventsList.add(dataMap);
			}
		}
		monthBean.put("events", eventsList);
		
		model.put(WebKeys.CALENDAR_VIEWBEAN, monthBean);
	}

	private static Map getMonthNames() {
		Map monthNames = new HashMap();
		monthNames.put("monthNames", Arrays.asList(EventsViewHelper.monthNames));
		monthNames.put("monthNamesShort", Arrays.asList(EventsViewHelper.monthNamesShort));
		return monthNames;
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

	public static Date getCalendarDate(int year, int month, int dayOfMonth, Date defaultValue) {
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
