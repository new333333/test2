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
package com.sitescape.team.module.ical.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
import net.fortuna.ical4j.model.Parameter;
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
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Due;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.RecurrenceId;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.joda.time.DateTimeZone;
import org.joda.time.YearMonthDay;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.calendar.TimeZoneHelper;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.ical.AttendedEntries;
import com.sitescape.team.module.ical.IcalModule;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.task.TaskHelper;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.util.Validator;
import com.sitescape.util.cal.DayAndPosition;
/**
 * iCalendar generator.
 * 
 * 
 * 
 * @author Pawel Nowicki
 * 
 */
public class IcalModuleImpl extends CommonDependencyInjection implements IcalModule {
	protected Log logger = LogFactory.getLog(getClass());
	
	private static final ProdId PROD_ID = new ProdId("-//SiteScape Inc//"
			+ ObjectKeys.PRODUCT_NAME_DEFAULT);

	private BinderModule binderModule;
	private FolderModule folderModule;
	
	protected BinderModule getBinderModule() {
		return binderModule;
	}

	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}

	protected FolderModule getFolderModule() {
		return folderModule;
	}

	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}

	private static enum ComponentType {

		Task, Calendar;
	}

	/**
	 * parseEvents
	 * 
	 * Extracts the VEVENTs and VTODOs from an ical input stream, converts each to an Event, and calls
	 *   the supplied handler with the event, along with the SUMMARY and DESCRIPTION, if any,
	 *   from the VEVENT (VTODO).
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
		Map<String, TimeZone> timeZones = new HashMap();
		try {
			CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
			Calendar cal = (new CalendarBuilder()).build(new UnfoldingReader(icalData));
			for(Object comp : cal.getComponents("VTIMEZONE")) {
				VTimeZone timeZoneComponent = (VTimeZone) comp;
				timeZones.put(timeZoneComponent.getTimeZoneId().getValue(), new TimeZone(timeZoneComponent));
			}
			for(Object comp : cal.getComponents("VEVENT")) {
				VEvent eventComponent = (VEvent) comp;

				event = parseEvent(eventComponent.getStartDate(), eventComponent.getEndDate(), null, 
									eventComponent.getDuration(), (RRule) eventComponent.getProperty("RRULE"),
									eventComponent.getRecurrenceId(), eventComponent.getUid(), timeZones);
				
				String description = null;
				if(eventComponent.getDescription() != null) {
					description = eventComponent.getDescription().getValue();
				}
				String summary = null;
				if(eventComponent.getSummary() != null) {
					summary = eventComponent.getSummary().getValue();
				}
								
				handler.handleEvent(event, description, summary);
			}
			for(Object comp : cal.getComponents("VTODO")) {
				VToDo todoComponent = (VToDo) comp;
				
				event = parseEvent(todoComponent.getStartDate(), null, todoComponent.getDue(), 
						todoComponent.getDuration(), (RRule) todoComponent.getProperty("RRULE"),
						todoComponent.getRecurrenceId(), todoComponent.getUid(), timeZones);
	
				String description = null;
				if(todoComponent.getDescription() != null) {
					description = todoComponent.getDescription().getValue();
				}
				
				String summary = null;
				if(todoComponent.getSummary() != null) {
					summary = todoComponent.getSummary().getValue();
				}
				
				com.sitescape.team.ical.util.Priority priority = com.sitescape.team.ical.util.Priority.fromIcalPriority(todoComponent.getPriority());
				com.sitescape.team.ical.util.Status status = com.sitescape.team.ical.util.Status.fromIcalStatus(todoComponent.getStatus());		
				com.sitescape.team.ical.util.PercentComplete percentComplete = com.sitescape.team.ical.util.PercentComplete.fromIcalPercentComplete(todoComponent.getPercentComplete());
				
				String location = null;
				if(todoComponent.getLocation() != null) {
					location = todoComponent.getLocation().getValue();
				}	
							
				handler.handleTodo(event, description, summary, 
						priority!=null?priority.name():null, 
						status!=null?status.name():null, 
						percentComplete!=null?percentComplete.name():null, location);
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
	 * Parse VEVENT or VTODO.
	 * 
	 * @param start
	 * @param end
	 * @param due
	 * @param duration
	 * @param recurrence
	 * @param recurrenceId
	 * @param timeZones
	 * @return
	 */
	private Event parseEvent(DtStart start, DtEnd end, Due due, Duration duration, RRule recurrence,
			RecurrenceId recurrenceId, Uid uid, Map<String, TimeZone> timeZones) {	
		if (start == null && end == null && due == null && duration == null && uid == null) {
			return null;
		}
		
		Event event = new Event();
		
		if (uid != null) {
			event.setUid(uid.getValue());
		}
		if (start != null) {
			GregorianCalendar startCal = new GregorianCalendar();
			startCal.setTime(start.getDate());
			event.setDtStart(startCal);
		}
		
		if (isAllDaysEvent(start)) {
			event.allDaysEvent();
		} else {
			Parameter tzId = start.getParameter(Parameter.TZID);
			if (start.getParameter(Parameter.TZID) != null) {
				TimeZone tz = timeZones.get(tzId.getValue());
				event.setTimeZone(tz);
			}
		}
		if(end != null) {
			java.util.Date endDate = end.getDate();
			if (end.getParameter(Value.DATE.getName()) != null) {
				// only date (no time) so it's all days event
				// intern we store the date of last event's day - so get one day before
	 			endDate = new org.joda.time.DateTime(endDate).minusDays(1).toDate();
			}
			GregorianCalendar endCal = new GregorianCalendar();
			endCal.setTime(endDate);
			event.setDtEnd(endCal);
		} else if (due != null) {
			java.util.Date endDate = due.getDate();
			if (due.getParameter(Value.DATE.getName()) != null) {
				// only date (no time) so it's all days event
				// intern we store the date of last event's day - so get one day before
				endDate = new org.joda.time.DateTime(endDate).minusDays(1).toDate();
			}
			GregorianCalendar endCal = new GregorianCalendar();
			endCal.setTime(endDate);
			event.setDtEnd(endCal);			
		} else if (duration != null) {
			event.setDuration(new com.sitescape.util.cal.Duration(duration.toString()));
		}
		if (recurrence != null && (recurrenceId == null)) {
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
		return event;
	}

	private boolean isAllDaysEvent(DtStart start) {
		return start != null && !((start.getParameter(Parameter.TZID) != null) || 
				(start.getParameter(Parameter.TZID) == null && 
					start.getParameter(Value.DATE.getName()) == null));
	}

	/**
	 * parseEvents
	 * 
	 * Convenience method that returns the parsed events as a list, rather than calling a handler.
	 *  SUMMARY and DESCRIPTION from the VEVENTS is discarded.
	 * 
	 * @param icalData
	 * @param handler
	 * @throws IOException
	 * @throws ParserException
	 */
	public List<Event> parseEvents(Reader icalData)
		throws IOException, ParserException
	{
		final List<Event> events = new LinkedList<Event>();
		
		EventHandler myHandler = new EventHandler() {
			public void handleEvent(Event e, String description, String summary)
			{
				events.add(e);
			}

			public void handleTodo(Event event, String description, String summary, String priority, String status, String completed, String location) {
				events.add(event);
			}
		};

		parseEvents(icalData, myHandler);
		
		return events;
	}
	/**
	 * parseToEntries
	 * 
	 * Creates an entry in the given folder for each VEVENT in the given ical input stream, returning a list of
	 *  the added IDs.
	 * 
	 * @param folderId
	 * @param icalFile
	 * @return id list of created entries
	 * @throws IOException
	 * @throws ParserException
	 */
	public AttendedEntries parseToEntries (final Long folderId, InputStream icalFile) throws IOException, ParserException
	{
		Folder folder = (Folder)folderModule.getFolder(folderId);
		return parseToEntries(folder, null, icalFile);
	}
	public AttendedEntries parseToEntries (final Folder folder, Definition def, InputStream icalFile) throws IOException, ParserException {

		final AttendedEntries attendedEntries = new AttendedEntries();
		if (def == null) {
			def = folder.getDefaultEntryDef();
			if (def == null) return attendedEntries;
		} 
		final String entryType = def.getId();
		final String eventName= getEventName(def.getDefinition());
		if(eventName == null) {
			return attendedEntries;
		}

		EventHandler entryCreator = new EventHandler() {
			
			public void handleEvent(Event event, String description, String summary) {
				Map<String, Object> formData = new HashMap<String, Object>();
				
				shorterSummary(formData, description, summary);
				formData.put(eventName, event);
				
				addOrModifyEntry(event, new MapInputData(formData));
			}

			public void handleTodo(Event event, String description, String summary, String priority, String status, String completed, String location) {
				Map<String, Object> formData = new HashMap<String, Object>();
				
				shorterSummary(formData, description, summary);
				formData.put(eventName, event);
				formData.put("priority", new String[] {priority});
				formData.put("status", new String[] {status});
				formData.put("completed", new String[] {completed});
				formData.put("location", new String[] {location});
				
				// TODO: add attachments support
				// TODO: alert's support
				
				addOrModifyEntry(event, new MapInputData(formData));
			}
			
			private void shorterSummary(Map<String, Object> formData, String description, String summary) {
				if (summary != null && summary.length() > 255) {
					String summmaryTemp = summary.substring(0, 252);
					int indexLastAllowedSpace = summmaryTemp.lastIndexOf(" ");
					summmaryTemp = summary.substring(0, indexLastAllowedSpace) + "...";
					description = "..." + summary.substring(indexLastAllowedSpace, summary.length()) + "\n\n" + (description != null ? description : "");
					summary = summmaryTemp;
				}
				formData.put("description", new String[] {description != null ? description : ""});
				formData.put("title", new String[] {summary != null ? summary : ""});
			}
			
			private void addOrModifyEntry(Event event, MapInputData inputData) {
				try {
					Event eventToUpdate = findEventByUid(folder.getId(), event);
					if (eventToUpdate != null && eventToUpdate.getOwner() != null &&
							eventToUpdate.getOwner().getEntity() != null) {
						DefinableEntity entity = eventToUpdate.getOwner().getEntity();
						folderModule.modifyEntry(folder.getId(), entity.getId(), inputData, null, null, null, null);
						attendedEntries.modified.add(entity.getId());
					} else {
						Long entryId = folderModule.addEntry(folder.getId(), entryType, inputData, null, null);
						attendedEntries.added.add(entryId);
					}
				} catch (AccessControlException e) {
					logger.warn("Can not create entry from iCal file.", e);
				} catch (WriteFilesException e) {
					logger.warn("Can not create entry from iCal file.", e);
				}
			}			
		};

		parseEvents(new InputStreamReader(icalFile), entryCreator);
		return attendedEntries;
	}
	
	protected Event findEventByUid(Long folderId, Event event) {
		if (event == null || event.getUid() == null) {
			return null;
		}
		FilterControls fc = new FilterControls("uid", event.getUid());
		fc.add("owner.owningBinderId", folderId);
		List events = getCoreDao().loadObjects(Event.class, fc, RequestContextHolder.getRequestContext().getZoneId());
		if (events.size() > 0) {
			return (Event)events.iterator().next();
		}
		
		// check if this is update of v1.0 event (uid is not stored)
		// find event by folder id and event id
		Event.UidBuilder uidBuilder = event.parseUid();
		if (uidBuilder.getBinderId() == null || 
				uidBuilder.getEntryId() == null || 
				uidBuilder.getEventId() == null) {
			// uid is not intern uid
			return null;
		}
		if (!uidBuilder.getBinderId().equals(folderId)) {
			// wrong folder
			return null;
		}
		
		fc = new FilterControls("id", uidBuilder.getEventId());
		fc.add("owner.owningBinderId", uidBuilder.getBinderId());
		events = getCoreDao().loadObjects(Event.class, fc, RequestContextHolder.getRequestContext().getZoneId());
		if (events.size() > 0) {
			return (Event)events.iterator().next();
		}
		
		return null;
	}

	private String getEventName(Document definition) {
		Element configRoot = definition.getRootElement();
		Element eventEl = (Element) configRoot.selectSingleNode("//item[@name='event']//property[@name='name']");
		if (eventEl != null) {
			return eventEl.attributeValue("value");
		}
		logger.debug("Entry defintion ["+ definition.asXML() +"] has no events. iCalendar import aborted.");
		return null;
	}
	
	public Calendar generate(DefinableEntity entry,
			Collection events, String defaultTimeZoneId) {
		Calendar calendar = createICalendar(null);
		generate(calendar, entry, events, defaultTimeZoneId);
		return calendar;
	}
	
	public Calendar generate(String calendarName,  List folderEntries, String defaultTimeZoneId) {
		Calendar calendar = createICalendar(calendarName);
		
		if (folderEntries == null) {
			return calendar;
		}
		
		Iterator it = folderEntries.iterator();
		while (it.hasNext()) {
			DefinableEntity entry = (DefinableEntity)it.next();
			generate(calendar, entry, entry.getEvents(), defaultTimeZoneId);
		}
		
		// Calendar without any components can not exists
		// so put time zone
		if (calendar.getComponents().isEmpty()) {
			TimeZone timeZone = getTimeZone(null, defaultTimeZoneId);
				
			calendar.getComponents().add(timeZone.getVTimeZone());
		}
	
		return calendar;
	}
	
	protected void generate(Calendar calendar, DefinableEntity entry,
			Collection events, String defaultTimeZoneId) {
		if (calendar == null) {
			throw new InvalidParameterException("'calendar' can't be null.");
		}
		
		if (entry == null) {
			throw new InvalidParameterException("'entry' can't be null.");
		}

		if (events == null || events.isEmpty()) {
			return;
		}

		ComponentType componentType = getComponentType(entry);

		Iterator eventsIt = events.iterator();
		while (eventsIt.hasNext()) {
			Event event = (Event) eventsIt.next();
			addEventToICalendar(calendar, entry, event, defaultTimeZoneId,
					componentType);
		}
	}

	/**
	 * Creates new iCalendar object and sets fields.
	 */
	private Calendar createICalendar(String calendarName) {
		Calendar calendar = new Calendar();
		calendar.getProperties().add(PROD_ID);
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);
		calendar.getProperties().add(Method.REQUEST);
		if (calendarName != null) {
			calendar.getProperties().add(new XProperty("X-WR-CALNAME", calendarName));
		}
		return calendar;
	}

	private ComponentType getComponentType(DefinableEntity entry) {
		Definition entryDef = entry.getEntryDef();
		
		String family = DefinitionUtils.getFamily(entryDef.getDefinition());

		if (family != null && family.equals(ObjectKeys.FAMILY_TASK)) {
			return ComponentType.Task;
		} else if (family != null && family.equals(ObjectKeys.FAMILY_CALENDAR)) {
			return ComponentType.Calendar;
		}
		return ComponentType.Calendar;
	}

	private void addEventToICalendar(Calendar calendar, DefinableEntity entry,
			Event event, String defaultTimeZoneId, ComponentType componentType) {
		// there is probably a bug in iCal4j or in Java: for some time zones
		// the date after setting the time zone is wrong, it means: the other
		// time offset is supplied as it should be...
		TimeZone timeZone = getTimeZone(event.getTimeZone(), defaultTimeZoneId);
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
		
		if (!event.isAllDayEvent()) {
			DateTime start = new DateTime(event.getDtStart().getTime());
			if (timeZone != null) {
				start.setTimeZone(timeZone);
			}

			Dur duration = null;
			if (event.getDuration().getWeeks() > 0) {
				duration = new Dur(event.getDuration().getWeeks());
			} else {
				duration = new Dur(event.getDuration().getDays(), event
						.getDuration().getHours(), event.getDuration()
						.getMinutes(), event.getDuration().getSeconds());
			}
			vToDo = new VToDo(start, duration, entry.getTitle());
			vToDo.getProperties().getProperty(Property.DTSTART)
					.getParameters().add(Value.DATE_TIME);
		} else {
			Date start = new Date(event.getDtStart().getTime());
			Date end = (Date)start.clone();
			if (event.getDtEnd() != null) {
				end = new Date(event.getDtEnd().getTime());
			}
			end = new Date(new org.joda.time.DateTime(end).plusDays(1).toDate());
			vToDo = new VToDo(start, end, entry.getTitle());
			vToDo.getProperties().getProperty(Property.DTSTART)
					.getParameters().add(Value.DATE);
			vToDo.getProperties().getProperty(Property.DUE)
					.getParameters().add(Value.DATE);			
		}
		
		setComponentDescription(vToDo, entry.getDescription().getText());
		setComponentUID(vToDo, entry, event);

		addToDoPriority(vToDo, entry);
		addToDoStatus(vToDo, entry);
		addComponentCompleted(vToDo, entry);
		setComponentAttendee(vToDo, entry);
		setComponentOrganizer(vToDo, entry);
		setComponentLocation(vToDo, entry);
		
		addRecurrences(vToDo, event);

		return vToDo;
	}
	private List findUserListAttributes(Document definitionConfig) {
		List result = new ArrayList();
		
    	List nodes = definitionConfig.selectNodes("//item[@type='form']//item[@name='entryFormForm']//item[@type='data' and @name='user_list']/properties/property[@name='name']/@value");
    	if (nodes == null) {
    		return result;
    	}
    	
    	Iterator it = nodes.iterator();
    	while (it.hasNext()) {
    		Node node = (Node)it.next();
    		result.add(node.getStringValue());
    	}
    	return result;
	}

	private void setComponentAttendee(Component component, DefinableEntity entry) {
		Iterator userListsIt = findUserListAttributes(entry.getEntryDef().getDefinition()).iterator();
		while (userListsIt.hasNext()) {
			String attributeName = (String)userListsIt.next();
		
			CustomAttribute customAttribute = entry.getCustomAttribute(attributeName);
	
			if (customAttribute == null) {
				return;
			}
			Set<Long>ids = LongIdUtil.getIdsAsLongSet(customAttribute.getValueSet());
			List<Principal> principals = getProfileDao().loadPrincipals(ids, entry.getZoneId(), true);
			for (Principal principal:principals) {
				ParameterList attendeeParams = new ParameterList();
				attendeeParams.add(new Cn(principal.getTitle()));
				attendeeParams.add(Role.REQ_PARTICIPANT);
	
				String uri = "MAILTO:" + principal.getEmailAddress();
				try {
					component.getProperties().add(new Attendee(attendeeParams, uri));
				} catch (URISyntaxException e) {
					logger.warn("Can not add attendee because of URI [" + uri
							+ "] parsing problem");
				}
			}
		
		}

	}
	
	private void setComponentOrganizer(Component component, DefinableEntity entry) {

		Principal principal = (Principal) entry.getCreation().getPrincipal();
		ParameterList organizerParams = new ParameterList();
		organizerParams.add(new Cn(principal.getTitle()));

		String uri = "MAILTO:" + principal.getEmailAddress();
		try {
			component.getProperties().add(new Organizer(organizerParams, uri));
		} catch (URISyntaxException e) {
			logger.warn("Can not add organizer because of URI [" + uri
					+ "] parsing problem");
		}

	}
	
	private void setComponentLocation(Component component, DefinableEntity entry) {

		CustomAttribute customAttribute = entry
			.getCustomAttribute("location");

		if (customAttribute == null) {
			return;
		}
		
		String value = (String) customAttribute.getValue();		
		if (Validator.isNull(value)) return;

		component.getProperties().add(new Location(value));


	}

	private void addComponentCompleted(VToDo toDo, DefinableEntity entry) {
		CustomAttribute customAttribute = entry
				.getCustomAttribute(TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME);

		if (customAttribute == null) {
			return;
		}

		Set value = (Set) customAttribute.getValueSet();

		if (value == null) {
			return;
		}

		com.sitescape.team.ical.util.PercentComplete completed = null;

		if (value.contains("c000")) {
			completed = com.sitescape.team.ical.util.PercentComplete.c000;
		} else if (value.contains("c010")) {
			completed = com.sitescape.team.ical.util.PercentComplete.c010;
		} else if (value.contains("c020")) {
			completed = com.sitescape.team.ical.util.PercentComplete.c020;
		} else if (value.contains("c030")) {
			completed = com.sitescape.team.ical.util.PercentComplete.c030;
		} else if (value.contains("c040")) {
			completed = com.sitescape.team.ical.util.PercentComplete.c040;
		} else if (value.contains("c050")) {
			completed = com.sitescape.team.ical.util.PercentComplete.c050;
		} else if (value.contains("c060")) {
			completed = com.sitescape.team.ical.util.PercentComplete.c060;
		} else if (value.contains("c070")) {
			completed = com.sitescape.team.ical.util.PercentComplete.c070;
		} else if (value.contains("c080")) {
			completed = com.sitescape.team.ical.util.PercentComplete.c080;
		} else if (value.contains("c090")) {
			completed = com.sitescape.team.ical.util.PercentComplete.c090;
		} else if (value.contains("c100")) {
			completed = com.sitescape.team.ical.util.PercentComplete.c100;
		} else {
			logger.error("The task compleded has wrong value [" + value + "].");
			return;
		}

		toDo.getProperties().add(completed.toIcalPercentComplete());
	}

	private void addToDoStatus(VToDo toDo, DefinableEntity entry) {
		CustomAttribute customAttribute = entry
				.getCustomAttribute(TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME);

		if (customAttribute == null) {
			return;
		}

		Set value = (Set) customAttribute.getValueSet();

		if (value == null) {
			return;
		}

		com.sitescape.team.ical.util.Status status = null;

		if (value.contains("s1")) {
			status = com.sitescape.team.ical.util.Status.s1;
		} else if (value.contains("s2")) {
			status = com.sitescape.team.ical.util.Status.s2;
		} else if (value.contains("s3")) {
			status = com.sitescape.team.ical.util.Status.s3;
		} else if (value.contains("s4")) {
			status = com.sitescape.team.ical.util.Status.s4;
		} else {
			logger.error("The task status has wrong value [" + value + "].");
			return;
		}

		if (status == null) {
			logger.error("The task status is not defined.");
			return;
		}

		toDo.getProperties().add(status.toIcalStatus());
	}

	private void addToDoPriority(VToDo toDo, DefinableEntity entry) {
		CustomAttribute customAttribute = entry
				.getCustomAttribute(TaskHelper.PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME);

		if (customAttribute == null) {
			return;
		}

		Set value = (Set) customAttribute.getValueSet();
		
		com.sitescape.team.ical.util.Priority priority = com.sitescape.team.ical.util.Priority.p1;
		if (value != null) {
			if (value.contains("p5")) {
				priority = com.sitescape.team.ical.util.Priority.p5;
			} else if (value.contains("p4")) {
				priority = com.sitescape.team.ical.util.Priority.p4;
			} else if (value.contains("p3")) {
				priority = com.sitescape.team.ical.util.Priority.p3;
			} else if (value.contains("p2")) {
				priority = com.sitescape.team.ical.util.Priority.p2;
			} else if (value.contains("p1")) {
				priority = com.sitescape.team.ical.util.Priority.p1;
			}
		}
		toDo.getProperties().add(priority.toIcalPriority());
	}

	private VEvent createVEvent(DefinableEntity entry, Event event,
			TimeZone timeZone) {
		VEvent vEvent = null;
		if (!event.isAllDayEvent()) {
			DateTime start = new DateTime(event.getDtStart().getTime());
			if (timeZone != null) {
				start.setTimeZone(timeZone);
			}

			Dur duration = null;
			if (event.getDuration().getWeeks() > 0) {
				duration = new Dur(event.getDuration().getWeeks());
			} else {
				duration = new Dur(event.getDuration().getDays(), event
						.getDuration().getHours(), event.getDuration()
						.getMinutes(), event.getDuration().getSeconds());
			}
			vEvent = new VEvent(start, duration, entry.getTitle());
			vEvent.getProperties().getProperty(Property.DTSTART)
					.getParameters().add(Value.DATE_TIME);
			vEvent.getProperties().add(Transp.OPAQUE);
		} else {
			Date start = new Date(event.getDtStart().getTime());
			Date end = (Date)start.clone();
			if (event.getDtEnd() != null) {
				end = new Date(event.getDtEnd().getTime());
			}
			end = new Date(new org.joda.time.DateTime(end).plusDays(1).toDate());
			vEvent = new VEvent(start, end, entry.getTitle());
			
			// one day events mark as TRANSPARENT
			// An 'event on a day' - anniversaries and birthdays
			if ((new YearMonthDay(start)).equals((new YearMonthDay(end)).minusDays(1))) {
				vEvent.getProperties().add(Transp.TRANSPARENT);
			} else {
				vEvent.getProperties().add(Transp.OPAQUE);
			}
		}

		setComponentDescription(vEvent, entry.getDescription().getText());
		setComponentUID(vEvent, entry, event);
		setComponentAttendee(vEvent, entry);
		setComponentOrganizer(vEvent, entry);
		setComponentLocation(vEvent, entry);
		addRecurrences(vEvent, event);

		return vEvent;
	}

	public static TimeZone getTimeZone(java.util.TimeZone timeZone, String defaultTimeZone) {
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		
		TimeZone iCalTimeZone = null;
		
		if (timeZone != null) {
			java.util.TimeZone fixedTimeZone = TimeZoneHelper.fixTimeZone(timeZone);
			// use jodatime to convert 3-characters zone ids to ical names 
			DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(fixedTimeZone);
			if (dateTimeZone != null) {
				iCalTimeZone = registry.getTimeZone(dateTimeZone.getID());
			}
		}
		
		if (iCalTimeZone == null) {
			// get current user time zone
			User user = RequestContextHolder.getRequestContext().getUser();
			// use jodatime to convert 3-characters zone ids to ical names 
			DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(user.getTimeZone());
			if (dateTimeZone != null) {
				iCalTimeZone = registry.getTimeZone(dateTimeZone.getID());
			}
		}
		
		if (iCalTimeZone == null && defaultTimeZone != null) {
			iCalTimeZone = registry.getTimeZone(defaultTimeZone);
		}
		return iCalTimeZone;
	}

	public static void addRecurrences(CalendarComponent component, Event event) {
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
			//logger.error("Error by creating RRule by iCal export. ", e);
			return;
		}
	}

	private void setComponentDescription(CalendarComponent component,
			String descr) {
		//grwise was ignore mail without a description
		if (Validator.isNotNull(descr))
			component.getProperties().add(new Description(descr));
	}

	private void setComponentUID(CalendarComponent component,
			DefinableEntity entry, Event event) {
		String uid = event.getUid();
		if (uid == null) {
			// v1.0 compatibility - old events don't have stored uid
			uid = event.generateUid(entry);
		}

		component.getProperties().add(new Uid(uid));
	}

}
