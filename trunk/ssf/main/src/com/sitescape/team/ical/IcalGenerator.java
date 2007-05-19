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
package com.sitescape.team.ical;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.PercentComplete;
import net.fortuna.ical4j.model.property.Priority;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTimeZone;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.util.ResolveIds;
import com.sitescape.util.cal.DayAndPosition;

/**
 * iCalendar generator.
 * 
 * 
 * 
 * @author Pawel Nowicki
 * 
 */
public class IcalGenerator {
	
	protected Log logger = LogFactory.getLog(getClass());
	
	private static final ProdId PROD_ID = new ProdId("-//SiteScape Inc//"
			+ ObjectKeys.PRODUCT_NAME_DEFAULT);

	private static final String PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME = "priority";

	private static final String STATUS_TASK_ENTRY_ATTRIBUTE_NAME = "status";

	private static final String COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME = "completed";

	private static final String ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME = "assignment";

	private static class ComponentType {

		private int type;

		public ComponentType(int i) {
			this.type = i;
		}

		public static final ComponentType Task = new ComponentType(1);

		public static final ComponentType Calendar = new ComponentType(2);

	}

	public Calendar getICalendarForEntryEvents(DefinableEntity entry,
			Collection events, String defaultTimeZoneId) {
		Calendar calendar = createICalendar();

		if (events == null || events.isEmpty()) {
			return calendar;
		}

		ComponentType componentType = getComponentType(entry);

		Iterator eventsIt = events.iterator();
		while (eventsIt.hasNext()) {
			Event event = (Event) eventsIt.next();
			addEventToICalendar(calendar, entry, event, defaultTimeZoneId,
					componentType);
		}

		return calendar;
	}

	/**
	 * Creates new iCalendar object and sets fields.
	 */
	private Calendar createICalendar() {
		Calendar calendar = new Calendar();
		calendar.getProperties().add(IcalGenerator.PROD_ID);
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);
		return calendar;
	}

	private ComponentType getComponentType(DefinableEntity entry) {
		Definition entryDef = entry.getEntryDef();
		String entryDefId = entryDef.getId();

		if (entryDefId.equals(ObjectKeys.DEFAULT_ENTRY_TASK_CONFIG)) {
			return ComponentType.Task;
		} else if (entryDefId.equals(ObjectKeys.DEFAULT_ENTRY_CALENDAR_CONFIG)) {
			return ComponentType.Calendar;
		}
		return ComponentType.Calendar;
	}

	private void addEventToICalendar(Calendar calendar, DefinableEntity entry,
			Event event, String defaultTimeZoneId, ComponentType componentType) {
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance()
				.createRegistry();
		// there is probably a bug in iCal4j or in Java: for some time zones
		// the date after setting the time zone is wrong, it means: the other
		// time offset is supplied as it should be...
		TimeZone timeZone = getTimeZone(event.getTimeZone(), registry,
				defaultTimeZoneId);
		if (timeZone != null) {
			VTimeZone tz = timeZone.getVTimeZone();
			if (!calendar.getComponents(Component.VTIMEZONE).contains(tz)) {
				calendar.getComponents().add(tz);
			}
		}

		if (componentType.equals(ComponentType.Task)) {
			calendar.getComponents().add(createVTodo(entry, event, timeZone));
		} else if (componentType.equals(ComponentType.Calendar)) {
			calendar.getComponents().add(createVEvent(entry, event, timeZone));
		}
	}

	private VToDo createVTodo(DefinableEntity entry, Event event,
			TimeZone timeZone) {
		VToDo vToDo = null;
		if (event.hasDuration()) {
			DateTime dt = new DateTime(event.getDtStart().getTime());
			dt.setTimeZone(timeZone);

			Dur duration = null;
			if (event.getDuration().getWeeks() > 0) {
				duration = new Dur(event.getDuration().getWeeks());
			} else {
				duration = new Dur(event.getDuration().getDays(), event
						.getDuration().getHours(), event.getDuration()
						.getMinutes(), event.getDuration().getSeconds());
			}
			vToDo = new VToDo(dt, duration, entry.getTitle());
			vToDo.getProperties().getProperty(Property.DTSTART).getParameters()
					.add(Value.DATE_TIME);
		} else {
			Date d = new Date(event.getDtStart().getTime());
			vToDo = new VToDo(d, entry.getTitle());
			vToDo.getProperties().getProperty(Property.DTSTART).getParameters()
					.add(Value.DATE);
		}

		setComponentDescription(vToDo, entry.getDescription().getText());
		setComponentUID(vToDo, entry, event);

		addToDoPriority(vToDo, entry);
		addToDoStatus(vToDo, entry);
		addComponentCompleted(vToDo, entry);
		setComponentAttendee(vToDo, entry);

		// TODO: organizer ?

		addRecurrences(vToDo, event);

		return vToDo;
	}

	private void setComponentAttendee(VToDo toDo, DefinableEntity entry) {
		CustomAttribute customAttribute = entry
				.getCustomAttribute(ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME);

		if (customAttribute == null) {
			return;
		}

		Iterator principalsIt = ResolveIds.getPrincipals(customAttribute)
				.iterator();
		while (principalsIt.hasNext()) {
			Principal principal = (Principal) principalsIt.next();
			ParameterList parameterList = new ParameterList();
			parameterList.add(new Cn(principal.getTitle()));

			String uri = "MAILTO:" + principal.getEmailAddress();
			try {
				toDo.getProperties().add(new Attendee(parameterList, uri));
			} catch (URISyntaxException e) {
				logger.error("Can not add attendee because of URI [" + uri
						+ "] parsing problem");
			}
		}

	}

	private void addComponentCompleted(VToDo toDo, DefinableEntity entry) {
		CustomAttribute customAttribute = entry
				.getCustomAttribute(COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME);

		if (customAttribute == null) {
			return;
		}

		Set value = (Set) customAttribute.getValue();

		if (value != null) {
			return;
		}

		int completed = 0;

		if (value.contains("c_0")) {
			completed = 0;
		} else if (value.contains("c_10")) {
			completed = 10;
		} else if (value.contains("c_20")) {
			completed = 20;
		} else if (value.contains("c_30")) {
			completed = 30;
		} else if (value.contains("c_40")) {
			completed = 40;
		} else if (value.contains("c_50")) {
			completed = 50;
		} else if (value.contains("c_60")) {
			completed = 60;
		} else if (value.contains("c_70")) {
			completed = 70;
		} else if (value.contains("c_80")) {
			completed = 80;
		} else if (value.contains("c_90")) {
			completed = 90;
		} else if (value.contains("c_100")) {
			completed = 100;
		} else {
			logger.error("The task compleded has wrong value [" + value + "].");
			return;
		}

		toDo.getProperties().add(new PercentComplete(completed));
	}

	private void addToDoStatus(VToDo toDo, DefinableEntity entry) {
		CustomAttribute customAttribute = entry
				.getCustomAttribute(STATUS_TASK_ENTRY_ATTRIBUTE_NAME);

		if (customAttribute == null) {
			return;
		}

		Set value = (Set) customAttribute.getValue();

		if (value != null) {
			return;
		}

		String status = null;

		if (value.contains("needs_action")) {
			status = "NEEDS-ACTION";
		} else if (value.contains("in_process")) {
			status = "IN-PROCESS";
		} else if (value.contains("completed")) {
			status = "COMPLETED";
		} else if (value.contains("cancelled")) {
			status = "CANCELLED";
		} else {
			logger.error("The task status has wrong value [" + value + "].");
			return;
		}

		if (status == null) {
			logger.error("The task status is not defined.");
			return;
		}

		toDo.getProperties().add(new Status(status));
	}

	private void addToDoPriority(VToDo toDo, DefinableEntity entry) {
		CustomAttribute customAttribute = entry
				.getCustomAttribute(PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME);

		if (customAttribute == null) {
			return;
		}

		Set value = (Set) customAttribute.getValue();

		int priority = 0;
		if (value != null) {
			if (value.contains("low")) {
				priority = 9;
			} else if (value.contains("medium")) {
				priority = 5;
			} else if (value.contains("high")) {
				priority = 2;
			} else if (value.contains("critical")) {
				priority = 1;
			}
		}
		toDo.getProperties().add(new Priority(priority));
	}

	private VEvent createVEvent(DefinableEntity entry, Event event,
			TimeZone timeZone) {
		VEvent vEvent = null;
		if (event.hasDuration()) {
			DateTime dt = new DateTime(event.getDtStart().getTime());
			if (timeZone != null) {
				// must be if has duration...
				dt.setTimeZone(timeZone);
			}

			Dur duration = null;
			if (event.getDuration().getWeeks() > 0) {
				duration = new Dur(event.getDuration().getWeeks());
			} else {
				duration = new Dur(event.getDuration().getDays(), event
						.getDuration().getHours(), event.getDuration()
						.getMinutes(), event.getDuration().getSeconds());
			}
			vEvent = new VEvent(dt, duration, entry.getTitle());
			vEvent.getProperties().getProperty(Property.DTSTART)
					.getParameters().add(Value.DATE_TIME);
		} else {
			Date d = new Date(event.getDtStart().getTime());
			vEvent = new VEvent(d, entry.getTitle());
			vEvent.getProperties().getProperty(Property.DTSTART)
					.getParameters().add(Value.DATE);
		}

		setComponentDescription(vEvent, entry.getDescription().getText());
		setComponentUID(vEvent, entry, event);
		addRecurrences(vEvent, event);

		return vEvent;
	}

	private TimeZone getTimeZone(java.util.TimeZone timeZone,
			TimeZoneRegistry registry, String defaultTimeZone) {
		DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(timeZone);
		TimeZone iCalTimeZone = registry.getTimeZone(dateTimeZone.getID());
		if (iCalTimeZone == null) {
			iCalTimeZone = registry.getTimeZone(defaultTimeZone);
		}
		return iCalTimeZone;
	}

	private void addRecurrences(CalendarComponent component, Event event) {
		if (event.getFrequency() == Event.NO_RECURRENCE) {
			return;
		}

		try {
			Recur recur = null;

			if (event.getFrequency() == Event.DAILY) {
				recur = new Recur("FREQ;=" + Recur.DAILY);
			} else if (event.getFrequency() == Event.HOURLY) {
				recur = new Recur("FREQ;=" + Recur.HOURLY);
			} else if (event.getFrequency() == Event.MINUTELY) {
				recur = new Recur("FREQ;=" + Recur.MINUTELY);
			} else if (event.getFrequency() == Event.MONTHLY) {
				recur = new Recur("FREQ;=" + Recur.MONTHLY);
			} else if (event.getFrequency() == Event.SECONDLY) {
				recur = new Recur("FREQ;=" + Recur.SECONDLY);
			} else if (event.getFrequency() == Event.WEEKLY) {
				recur = new Recur("FREQ;=" + Recur.WEEKLY);
			} else if (event.getFrequency() == Event.YEARLY) {
				recur = new Recur("FREQ;=" + Recur.YEARLY);
			}

			if (event.getCount() > 0) {
				recur.setCount(event.getCount());
			} else {
				java.util.Calendar until = event.getUntilWithMaxLoopsIfNeeded();
				// if time is before start time, repeat doesn't occures in
				// calendar view - ??
				// TODO: test it again
				until.set(java.util.Calendar.HOUR_OF_DAY, 23);
				until.set(java.util.Calendar.MINUTE, 59);
				until.set(java.util.Calendar.SECOND, 59);
				recur.setUntil(new DateTime(until.getTime()));
			}
			recur.setInterval(event.getInterval());

			if (event.getByDay() != null && event.getByDay().length > 0) {
				for (int i = 0; i < event.getByDay().length; i++) {
					DayAndPosition dp = event.getByDay()[i];
					String day = "";
					if (dp.getDayOfWeek() == java.util.Calendar.SUNDAY) {
						day = "SU";
					} else if (dp.getDayOfWeek() == java.util.Calendar.MONDAY) {
						day = "MO";
					} else if (dp.getDayOfWeek() == java.util.Calendar.TUESDAY) {
						day = "TU";
					} else if (dp.getDayOfWeek() == java.util.Calendar.WEDNESDAY) {
						day = "WE";
					} else if (dp.getDayOfWeek() == java.util.Calendar.THURSDAY) {
						day = "TH";
					} else if (dp.getDayOfWeek() == java.util.Calendar.FRIDAY) {
						day = "FR";
					} else if (dp.getDayOfWeek() == java.util.Calendar.SATURDAY) {
						day = "SA";
					}

					recur.getDayList().add(
							new WeekDay(day, dp.getDayPosition()));
				}
			}

			if (event.getByHour() != null && event.getByHour().length > 0) {
				for (int i = 0; i < event.getByHour().length; i++) {
					recur.getHourList().add(event.getByHour()[i]);
				}
			}

			if (event.getByMinute() != null && event.getByMinute().length > 0) {
				for (int i = 0; i < event.getByMinute().length; i++) {
					recur.getMinuteList().add(event.getByMinute()[i]);
				}
			}

			if (event.getByMonth() != null && event.getByMonth().length > 0) {
				for (int i = 0; i < event.getByMonth().length; i++) {
					recur.getMonthList().add(event.getByMonth()[i]);
				}
			}

			if (event.getByMonthDay() != null
					&& event.getByMonthDay().length > 0) {
				for (int i = 0; i < event.getByMonthDay().length; i++) {
					recur.getMonthDayList().add(event.getByMonthDay()[i]);
				}
			}

			if (event.getBySecond() != null && event.getBySecond().length > 0) {
				for (int i = 0; i < event.getBySecond().length; i++) {
					recur.getSecondList().add(event.getBySecond()[i]);
				}
			}

			if (event.getByWeekNo() != null && event.getByWeekNo().length > 0) {
				for (int i = 0; i < event.getByWeekNo().length; i++) {
					recur.getWeekNoList().add(event.getByWeekNo()[i]);
				}
			}

			if (event.getByYearDay() != null && event.getByYearDay().length > 0) {
				for (int i = 0; i < event.getByYearDay().length; i++) {
					recur.getYearDayList().add(event.getByYearDay()[i]);
				}
			}

			// TODO: recur.setWeekStartDay(arg0);
			// TODO: recur.setPosList ?

			RRule rrule = new RRule(recur);
			component.getProperties().add(rrule);

		} catch (ParseException e) {
			logger.error("Error by creating RRule by iCal export. ", e);
			return;
		}
	}

	private void setComponentDescription(CalendarComponent component,
			String descr) {
		component.getProperties().add(new Description(descr));
	}

	private void setComponentUID(CalendarComponent component,
			DefinableEntity entry, Event event) {
		component.getProperties()
				.add(
						new Uid(entry.getParentBinder().getId().toString()
								+ "-"
								+ entry.getId().toString()
								+ (event != null ? "-"
										+ event.getId().toString() : "")));
	}

}
