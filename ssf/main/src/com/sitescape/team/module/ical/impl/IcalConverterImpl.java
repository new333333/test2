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
package com.sitescape.team.module.ical.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.data.UnfoldingReader;
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
import net.fortuna.ical4j.model.property.DateProperty;
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
import org.dom4j.Document;
import org.dom4j.Element;
import org.joda.time.DateTimeZone;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.ical.IcalConverter;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.task.TaskHelper;
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
public class IcalConverterImpl implements IcalConverter {
	
	protected Log logger = LogFactory.getLog(getClass());
	
	private static final ProdId PROD_ID = new ProdId("-//SiteScape Inc//"
			+ ObjectKeys.PRODUCT_NAME_DEFAULT);

	private static enum ComponentType {

		Task, Calendar
	}

	/**
	 * parseEvents
	 * 
	 * Extracts the VEVENTs from an ical input stream, converts each to an Event, and calls
	 *   the supplied handler with the event, along with the SUMMARY and DESCRIPTION, if any,
	 *   from the VEVENT
	 * 
	 * @param icalData
	 * @param handler
	 * @throws IOException
	 * @throws ParserException
	 */
	public void parseEvents(Reader icalData, EventHandler handler)
		throws IOException, ParserException
	{
		Event event = null;
		try {
			Calendar cal = (new CalendarBuilder()).build(new UnfoldingReader(icalData));
			for(Object comp : cal.getComponents("VEVENT")) {
				VEvent eventComponent = (VEvent) comp;
				event = new Event();
				DateProperty start = eventComponent.getStartDate();
				GregorianCalendar startCal = new GregorianCalendar();
				startCal.setTime(start.getDate());
				event.setDtStart(startCal);
				if(start.getParameter(Value.DATE.getName()) == null) {
					if(eventComponent.getEndDate() != null) {
						GregorianCalendar endCal = new GregorianCalendar();
						endCal.setTime(eventComponent.getEndDate().getDate());
						event.setDtEnd(endCal);
					} else {
						event.setDuration(new com.sitescape.util.cal.Duration(eventComponent.getDuration().toString()));
					}
				}
				RRule recurrence = (RRule) eventComponent.getProperty("RRULE");
				if(recurrence != null && (eventComponent.getRecurrenceId() == null)) {
					Recur recur = recurrence.getRecur();
					event.setFrequency(recur.getFrequency());
					event.setInterval(recur.getInterval());
					if(recur.getUntil() != null) {
						GregorianCalendar untilCal = new GregorianCalendar();
						untilCal.setTime(recur.getUntil());
						event.setUntil(untilCal);
					} else {
						event.setCount(recur.getCount());
					}
					if(recur.getDayList() != null) {
						event.setByDay(recur.getDayList().toString());
					}
					if(recur.getHourList() != null) {
						event.setByHour(recur.getHourList().toString());
					}
					if(recur.getMinuteList() != null) {
						event.setByMinute(recur.getMinuteList().toString());
					}
					if(recur.getMonthDayList() != null) {
						event.setByMonthDay(recur.getMonthDayList().toString());
					}
					if(recur.getMonthList() != null) {
						event.setByMonth(recur.getMonthList().toString());
					}
					if(recur.getSecondList() != null) {
						event.setBySecond(recur.getSecondList().toString());
					}
					if(recur.getWeekNoList() != null) {
						event.setByWeekNo(recur.getWeekNoList().toString());
					}
					if(recur.getYearDayList() != null) {
						event.setByYearDay(recur.getYearDayList().toString());
					}
					if(recur.getWeekStartDay() != null) {
						event.setWeekStart(recur.getWeekStartDay().toString());
					}
				}
				String description = null;
				if(eventComponent.getDescription() != null) {
					description = eventComponent.getDescription().toString();
				}
				String summary = null;
				if(eventComponent.getSummary() != null) {
					summary = eventComponent.getSummary().toString();
				}
				handler.handleEvent(event, description, summary);
			}
		} catch(IOException e) {
			logger.debug("IOException while parsing iCal stream", e);
			throw e;
		} catch(ParserException e) {
			logger.debug("ParserException while parsing iCal stream", e);
			throw e;
		}
	}
	
	/**
	 * parseToEntries
	 * 
	 * Creates an entry in the given folder for each VEVENT in the given ical input stream, returning a list of
	 *  the added IDs.
	 * 
	 * @param binderModule
	 * @param folderModule
	 * @param folderId
	 * @param icalFile
	 * @return id list of created entries
	 * @throws IOException
	 * @throws ParserException
	 */
	public List parseToEntries (final BinderModule binderModule, final FolderModule folderModule, final Long folderId, InputStream icalFile) throws IOException, ParserException
	{
		final Folder folder = (Folder)binderModule.getBinder(folderId);
		final List<Long> entries = new ArrayList<Long>();

		Iterator defaultEntryDefinitions = folder.getEntryDefinitions().iterator();
		if (!defaultEntryDefinitions.hasNext()) {
			return entries;
		}
		Definition def = (Definition) defaultEntryDefinitions.next();
		final String entryType = def.getId();
		final String eventName= getEventName(def.getDefinition());
		if(eventName == null) {
			return entries;
		}

		EventHandler entryCreator = new EventHandler() {
			public void handleEvent(Event event, String description, String summary) {
				Map<String, Object> formData = new HashMap<String, Object>();
				
				formData.put("binderId", new String[] {folderId.toString()});
				formData.put("description", new String[] {description != null ? description : ""});
				formData.put("entryType", new String[] {entryType});
				formData.put("title", new String[] {summary != null ? summary : ""});
				formData.put(eventName, event);
				
				MapInputData inputData = new MapInputData(formData);
				try {
					Long entryId = folderModule.addEntry(folderId, entryType, inputData, new HashMap());
					entries.add(entryId);
					logger.info("New entry id created from iCal file [" + entryId + "]");
				} catch (AccessControlException e) {
					logger.warn("Can not create entry from iCal file.", e);
				} catch (WriteFilesException e) {
					logger.warn("Can not create entry from iCal file.", e);
				}
			}
		};

		parseEvents(new InputStreamReader(icalFile), entryCreator);
		return entries;
	}
	
	private String getEventName(Document definition) {
		Element configRoot = definition.getRootElement();
		Element eventEl = (Element) configRoot.selectSingleNode("//item[@name='event']//property[@name='name']");
		if (eventEl != null) {
			return eventEl.attributeValue("value");
		}
		logger.error("Entry defintion ["+ definition.asXML() +"] has no events. iCalendar export aborted.");
		return null;
	}

	
	public Calendar generate(DefinableEntity entry,
			Collection events, String defaultTimeZoneId) {
		Calendar calendar = createICalendar();

		if (events == null || events.isEmpty()) {
			return calendar;
		}

		ComponentType componentType = getComponentType(entry);

		Iterator eventsIt = events.iterator();
		while (eventsIt.hasNext()) {
			Event event = (Event) eventsIt.next();
			TimeZone timeZone = addTimeZoneToICalendar(calendar, event, defaultTimeZoneId);
			Component comp = null;
			if (componentType.equals(ComponentType.Task)) {
				comp = createVTodo(entry, event, timeZone);
			} else if (componentType.equals(ComponentType.Calendar)) {
				comp = createVEvent(entry, event, timeZone);
			}
			calendar.getComponents().add(comp);
		}

		return calendar;
	}

	/**
	 * Creates new iCalendar object and sets fields.
	 */
	private Calendar createICalendar() {
		Calendar calendar = new Calendar();
		calendar.getProperties().add(PROD_ID);
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

	private TimeZone addTimeZoneToICalendar(Calendar calendar, Event event, String defaultTimeZoneId) {
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
		
		return timeZone;
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
				.getCustomAttribute(TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME);

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
				.getCustomAttribute(TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME);

		if (customAttribute == null) {
			return;
		}

		Set value = (Set) customAttribute.getValue();

		if (value != null) {
			return;
		}

		int completed = 0;

		if (value.contains("c0")) {
			completed = 0;
		} else if (value.contains("c10")) {
			completed = 10;
		} else if (value.contains("c20")) {
			completed = 20;
		} else if (value.contains("c30")) {
			completed = 30;
		} else if (value.contains("c40")) {
			completed = 40;
		} else if (value.contains("c50")) {
			completed = 50;
		} else if (value.contains("c60")) {
			completed = 60;
		} else if (value.contains("c70")) {
			completed = 70;
		} else if (value.contains("c80")) {
			completed = 80;
		} else if (value.contains("c90")) {
			completed = 90;
		} else if (value.contains("c100")) {
			completed = 100;
		} else {
			logger.error("The task compleded has wrong value [" + value + "].");
			return;
		}

		toDo.getProperties().add(new PercentComplete(completed));
	}

	private void addToDoStatus(VToDo toDo, DefinableEntity entry) {
		CustomAttribute customAttribute = entry
				.getCustomAttribute(TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME);

		if (customAttribute == null) {
			return;
		}

		Set value = (Set) customAttribute.getValue();

		if (value == null) {
			return;
		}

		String status = null;

		if (value.contains("needsAction")) {
			status = "NEEDS-ACTION";
		} else if (value.contains("inProcess")) {
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
				.getCustomAttribute(TaskHelper.PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME);

		if (customAttribute == null) {
			return;
		}

		Set value = (Set) customAttribute.getValue();
		

		int priority = 0;
		if (value != null) {
			if (value.contains("trivial")) {
				priority = 9;
			} else if (value.contains("low")) {
				priority = 8;
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
