package com.sitescape.team.ical;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.CalendarParser;
import net.fortuna.ical4j.data.CalendarParserFactory;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.data.UnfoldingReader;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.parameter.TzId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.security.AccessControlException;

public class IcalParser {

	protected static Log logger = LogFactory.getLog(IcalParser.class);
	
	/*
	 * Users of parseEvents need to provide an EventHandler implementation that
	 *  processes the Events as they are parsed.
	 */
	public static interface EventHandler
	{
		/**
		 * handleEvent
		 * 
		 * Called each time a VEVENT in the ical input is converted to an Event.  The
		 *  DESCRIPTION and SUMMARY from the VEVENT are passed along, but will be null
		 *  if they are absent from the VEVENT.
		 * 
		 * @param event
		 * @param description
		 * @param summary
		 */
		void handleEvent(Event event, String description, String summary);
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
	public static void parseEvents(Reader icalData, EventHandler handler)
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
	public static List parseToEntries (final BinderModule binderModule, final FolderModule folderModule, final Long folderId, InputStream icalFile) throws IOException, ParserException
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
	
	private static String getEventName(Document definition) {
		Element configRoot = definition.getRootElement();
		Element eventEl = (Element) configRoot.selectSingleNode("//item[@name='event']//property[@name='name']");
		if (eventEl != null) {
			return eventEl.attributeValue("value");
		}
		logger.error("Entry defintion ["+ definition.asXML() +"] has no events. iCalendar import aborted.");
		return null;
	}
	

/*
 * This is test code that I put in because the junit stuff wasn't working at the time I wrote this class

	public static void main(String[] args) {
		EventHandler myHandler = new EventHandler() {
			public void handleEvent(Event event, String description, String summary)
			{
				System.out.println("Got event: " + event + ", description '" + description + "', summary '" + summary + "'");
			}
		};
		
		java.io.StringReader data = new java.io.StringReader("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//hacksw/handcal//NONSGML v1.0//EN\nBEGIN:VEVENT\nDTSTART;TZID=America/Los_Angeles:19970714T130000\nDTEND;TZID=America/New_York:19970715T170000\nSUMMARY:Event Number 1\nEND:VEVENT\nEND:VCALENDAR");
		parseEvents(data, myHandler);
		
		data = new java.io.StringReader("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//hacksw/handcal//NONSGML v1.0//EN\nBEGIN:VEVENT\nDTSTART:19970714T170000Z\nDURATION:PT2H\nSUMMARY:Event Number 2\nEND:VEVENT\nEND:VCALENDAR");
		parseEvents(data, myHandler);

		data = new java.io.StringReader("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//hacksw/handcal//NONSGML v1.0//EN\nBEGIN:VEVENT\nDTSTART:19970714T170000Z\nDURATION:PT2H\nSUMMARY:Event Number 3\nRRULE:FREQ=DAILY;COUNT=10;INTERVAL=2\nEND:VEVENT\nEND:VCALENDAR");
		parseEvents(data, myHandler);

		data = new java.io.StringReader("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//hacksw/handcal//NONSGML v1.0//EN\nBEGIN:VEVENT\nDTSTART:19970714T170000Z\nDURATION:PT2H\nSUMMARY:Event Number 4\nRRULE:FREQ=YEARLY;UNTIL=20070715T140000Z;INTERVAL=1\nEND:VEVENT\nEND:VCALENDAR");
		parseEvents(data, myHandler);

		data = new java.io.StringReader("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//hacksw/handcal//NONSGML v1.0//EN\nBEGIN:VEVENT\nDTSTART:19970714T170000Z\nDURATION:PT2H\nSUMMARY:Event Number 5\nRRULE:FREQ=WEEKLY;BYDAY=TU,TH;UNTIL=20070715T140000Z;INTERVAL=1\nEND:VEVENT\nEND:VCALENDAR");
		parseEvents(data, myHandler);

		data = new java.io.StringReader("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:-//hacksw/handcal//NONSGML v1.0//EN\nBEGIN:VEVENT\nDTSTART:19970714T170000Z\nDURATION:PT2H\nSUMMARY:Event Number 6\nRRULE:FREQ=WEEKLY;BYDAY=TU,TH;UNTIL=20070715T140000Z;INTERVAL=1\nEND:VEVENT\nBEGIN:VEVENT\nDTSTART:19970714T170000Z\nDURATION:PT2H\nSUMMARY:Event Number 7\nEND:VEVENT\nEND:VCALENDAR");
		parseEvents(data, myHandler);

		java.util.Calendar cal = java.util.Calendar.getInstance();
		 cal.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
		 cal.set(java.util.Calendar.DAY_OF_MONTH, 25);

		 VEvent christmas = new VEvent(new Date(cal.getTime()), "Christmas Day");
		 
		 // initialise as an all-day event..
		 christmas.getProperties().getProperty(Property.DTSTART).getParameters().add(
		         Value.DATE);
		 christmas.getProperties().add(new Uid());
		 TimeZone tz = TimeZone.getDefault();
		 TzId tzParam = new TzId(tz.getID());
		 christmas.getProperties().getProperty(Property.DTSTART).getParameters().add(
		         tzParam);
		 
		 Calendar calendar = new Calendar();
		 calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
		 calendar.getProperties().add(Version.VERSION_2_0);
		 calendar.getProperties().add(CalScale.GREGORIAN);
		 calendar.getComponents().add(christmas);
		 java.io.StringWriter writer = new java.io.StringWriter();
		 CalendarOutputter out = new CalendarOutputter();
		 try {
			 out.output(calendar, writer);
		 }catch(Exception ex){
			 System.out.println("help me");
		 }
		 data = new java.io.StringReader(writer.toString());
		 parseEvents(data, myHandler);
	}
*/
}
