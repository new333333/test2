package com.sitescape.team.ical;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;

import net.fortuna.ical4j.data.ContentHandler;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.UtcOffset;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;

public class ICalContentHandler implements ContentHandler {
	
	protected Log logger = LogFactory.getLog(getClass());
	
	private List addedEntriesIds = new ArrayList();
	
	private FolderModule folderModule;
	
	private BinderModule binderModule;
	
	private Long folderId;
	
	private String entryType;
	
	private String eventName;

	private Map timeZones = new HashMap();
		
	private boolean parseTimeZoneNow = false;
	private boolean parseTimeZoneIdNow = false;
	private boolean parseEventNow = false;
	private boolean parseDateStartNow = false;
	private boolean parseDateEndNow = false;
	private boolean parseUIDNow = false;
	private boolean parseSummaryNow = false;
	private boolean parseDescriptionNow = false;
	private boolean parseRRuleNow = false;
//	private boolean parseRDateNow = false;
	private boolean parseTZOffsetToNow = false;
	private boolean parseTZOffsetFromNow = false;
	private boolean parseTZStandardNow = false;
	private boolean parseTZDayligthNow = false;
	
	private String currentTimeZoneId = null;
	private DateTime currentStartDate = null;
	private DateTime currentEndDate = null;
	private String currentUID = null;
	private String summary = null;
	private String description = null;
	private Recur recur = null;
	
	private TzId timeZoneId = null;
	private Standard timeZoneStandard = null;
	private Daylight timeZoneDaylight = null;
		
	public ICalContentHandler (BinderModule binderModule, FolderModule folderModule, Long folderId) {
		this.folderModule = folderModule;
		this.binderModule = binderModule;
		this.folderId = folderId;
		
		Folder folder = (Folder)binderModule.getBinder(this.folderId);

		Iterator defaultEntryDefinitions = folder.getEntryDefinitions().iterator();
		if (defaultEntryDefinitions.hasNext()) {
			Definition def = (Definition) defaultEntryDefinitions.next();
			this.entryType = def.getId();
			this.eventName= getEventName(def.getDefinition());
		}
	}

	/**
	 * Get first event from definition.
	 * 
	 * @param definition
	 * @return event name
	 */
	private String getEventName(Document definition) {
		Element configRoot = definition.getRootElement();
		Element eventEl = (Element) configRoot.selectSingleNode("//item[@name='event']//property[@name='name']");
		if (eventEl != null) {
			return eventEl.attributeValue("value");
		}
		logger.error("Entry defintion ["+ definition.asXML() +"] has no events. iCalendar import aborted.");
		return null;
	}

	public void startCalendar() {
		logger.debug("startCalendar");
	}
	
	public void endCalendar() {
		logger.debug("endCalendar");
	}

	public void startComponent(String component) {
		logger.debug(" * startComponent ["+component+"]");
		if ("VTIMEZONE".equals(component)) {
			parseTimeZoneNow = true;
		} else if ("VEVENT".equals(component)) {
			parseEventNow = true;
		} else if ("STANDARD".equals(component)) {
			parseTZStandardNow = true;
			timeZoneStandard = new Standard();
		} else if ("DAYLIGHT".equals(component)) {
			parseTZDayligthNow = true;
			timeZoneDaylight = new Daylight();
		}
	}
	
	public void endComponent(String component) {
		logger.debug(" * endComponent ["+component+"]");
		if ("VTIMEZONE".equals(component)) {
			parseTimeZoneNow = false;
			
			
			ComponentList cList = new ComponentList();
			cList.add(timeZoneStandard);
			cList.add(timeZoneDaylight);
			VTimeZone timeZone = new VTimeZone(cList);
			timeZone.getProperties().add(timeZoneId);
			
			timeZones.put(timeZoneId.getValue(), timeZone);

			timeZoneId = null;
			timeZoneStandard = null;
			timeZoneDaylight = null;
			
		} else if ("VEVENT".equals(component)) {
			parseEventNow = false;
			createEntry();
			
			currentStartDate = null;
			currentEndDate = null;
			currentUID = null;
			summary = null;
			description = null;
			recur = null;
			
		} else if ("STANDARD".equals(component)) {
			parseTZStandardNow = false;
		} else if ("DAYLIGHT".equals(component)) {
			parseTZDayligthNow = false;
		}
	}

	public void startProperty(String poperty) {
		logger.debug(" * * startProperty ["+poperty+"]");
		if ("TZID".equals(poperty)) {
			parseTimeZoneIdNow = true;
		} else if ("DTSTART".equals(poperty)) {
			parseDateStartNow = true;
		} else if ("DTEND".equals(poperty)) {
			parseDateEndNow = true;
		} else if ("UID".equals(poperty)) {
			parseUIDNow = true;
		} else if ("SUMMARY".equals(poperty)) {
			parseSummaryNow = true;
		} else if ("DESCRIPTION".equals(poperty)) {
			parseDescriptionNow = true;
		} else if ("RRULE".equals(poperty)) {
			parseRRuleNow = true;
//		} else if ("RDATE".equals(poperty)) {
//			parseRDateNow = true;
		} else if ("TZOFFSETFROM".equals(poperty)) {
			parseTZOffsetFromNow = true;
		} else if ("TZOFFSETTO".equals(poperty)) {
			parseTZOffsetToNow = true;
		}
	}
	
	public void endProperty(String poperty) {
		logger.debug(" * * endProperty ["+poperty+"]");
		if ("TZID".equals(poperty)) {
			parseTimeZoneIdNow = false;
		} else if ("DTSTART".equals(poperty)) {
			parseDateStartNow = false;
			currentTimeZoneId = null;
		} else if ("DTEND".equals(poperty)) {
			parseDateEndNow = false;
			currentTimeZoneId = null;
		} else if ("UID".equals(poperty)) {
			parseUIDNow = false;
		} else if ("SUMMARY".equals(poperty)) {
			parseSummaryNow = false;
		} else if ("DESCRIPTION".equals(poperty)) {
			parseDescriptionNow = false;
		} else if ("RRULE".equals(poperty)) {
			parseRRuleNow = false;
//		} else if ("RDATE".equals(poperty)) {
//			parseRDateNow = false;
		} else if ("TZOFFSETFROM".equals(poperty)) {
			parseTZOffsetFromNow = false;
		} else if ("TZOFFSETTO".equals(poperty)) {
			parseTZOffsetToNow = false;
		}
	}

	public void parameter(String name, String value) throws URISyntaxException {
		logger.debug(" * * * parameter ["+name+", "+value+"]");
		if (parseEventNow && parseDateStartNow) {
			if ("TZID".equals(name)) {
				currentTimeZoneId = fixQuote(value);
			}
		}
		if (parseEventNow && parseDateEndNow) {
			if ("TZID".equals(name)) {
				currentTimeZoneId = fixQuote(value);
			}
		}
	}

	private String fixQuote(String value) {
		if (value.startsWith("\"") && value.endsWith("\"")) {
			return value.substring(1, value.length() -1);
		}
		return value;
	}

	public void propertyValue(String value) throws URISyntaxException,
			ParseException, IOException {
		logger.debug(" * * * propertyValue ["+value+"]");
		if (parseTimeZoneNow && parseTimeZoneIdNow) {
			timeZoneId = new TzId(value);
		}
		
		if (parseEventNow && parseDateStartNow) {
			currentStartDate = parseDateTime(value, currentTimeZoneId);
			logger.debug(" ============= start date ["+currentStartDate.toString("yyyy-MM-dd HH:mm:ss")+"]");
		}
		
		if (parseEventNow && parseDateEndNow) {
			currentEndDate = parseDateTime(value, currentTimeZoneId);
			logger.debug(" ============= end date ["+currentEndDate.toString("yyyy-MM-dd HH:mm:ss")+"]");
		}
		
		if (parseEventNow && parseUIDNow) {
			currentUID = value;
			logger.debug(" ============= UID ["+currentUID+"]");
		}
		
		if (parseEventNow && parseSummaryNow) {
			summary = value;
			logger.debug(" ============= summary ["+summary+"]");
		}
		
		if (parseEventNow && parseDescriptionNow) {
			description = value;
			logger.debug(" ============= description ["+description+"]");
		}
		
		if (parseEventNow && parseRRuleNow) {
			recur = new Recur(value);
			logger.debug(" ============= recur ["+recur+"]");
		}
		
		if (parseTZStandardNow && parseTZOffsetToNow) {
			timeZoneStandard.getProperties().add(new TzOffsetTo(new UtcOffset(value)));
			logger.debug(" ============= offset to ["+value+"]");
		}
		
		if (parseTZStandardNow && parseTZOffsetFromNow) {
			timeZoneStandard.getProperties().add(new TzOffsetFrom(new UtcOffset(value)));
			logger.debug(" ============= offset from ["+value+"]");
		}
		
		if (parseTZDayligthNow && parseTZOffsetToNow) {
			timeZoneDaylight.getProperties().add(new TzOffsetTo(new UtcOffset(value)));
			logger.debug(" ============= offset to ["+value+"]");
		}
		
		if (parseTZDayligthNow && parseTZOffsetFromNow) {
			timeZoneDaylight.getProperties().add(new TzOffsetFrom(new UtcOffset(value)));
			logger.debug(" ============= offset from ["+value+"]");
		}
		
		if (parseTZStandardNow && parseDateStartNow) {
			timeZoneStandard.getProperties().add(new DtStart(value));
//			currentStartDate = parseDateTime(value, null);
			logger.debug(" ============= standard start date ["+value+"]");
		}
		
		if (parseTZDayligthNow && parseDateStartNow) {
			timeZoneDaylight.getProperties().add(new DtStart(value));
			logger.debug(" ============= Dayligth ["+value+"]");
		}
		
		if (parseTZStandardNow && parseRRuleNow) {
			timeZoneStandard.getProperties().add(new RRule(new Recur(value)));
			logger.debug(" =============  time zone standard rrule ["+value+"]");
		}
		
		if (parseTZDayligthNow && parseRRuleNow) {
			timeZoneDaylight.getProperties().add(new RRule(new Recur(value)));
			logger.debug(" ============= time zone Daylight rrule ["+value+"]");
		}
		
//		if (parseTZStandardNow && parseRDateNow) {
//			timeZoneStandard.getProperties().add(new RDate(value));
//			logger.debug(" ============= recur ["+recur+"]");
//		}

	}

	private static DateTimeFormatter dateTimeFormater = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss");
	private static DateTimeFormatter dateTimeUTCFormater = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss'Z'");
	private static DateTimeFormatter dateFormater = DateTimeFormat.forPattern("yyyyMMdd");

	private DateTime parseDateTime(String dateTime, String timeZoneId) {
		if (dateTime.indexOf("T") > -1) {
			if (!dateTime.endsWith("Z") && timeZoneId != null) {
				return dateTimeFormater.withZone(DateTimeZone.forTimeZone((new TimeZone(((VTimeZone)timeZones.get(timeZoneId)))))).parseDateTime(dateTime);
			} else {
				return dateTimeUTCFormater.parseDateTime(dateTime);
			}
		} else {
			return dateFormater.parseDateTime(dateTime);
		}
	}
	
	private void createEntry() {
		if (this.eventName == null) {
			return;
		}
		
		Map formData = new HashMap();
		
		formData.put("binderId", new String[] {this.folderId.toString()});
		formData.put("description", new String[] {this.description != null ? this.description : ""});
		formData.put("entryType", new String[] {this.entryType});
		formData.put("title", new String[] {this.summary != null ? this.summary : ""});
		
//		// all day event?
		if (this.currentStartDate.plusDays(1).equals(this.currentEndDate)) {
			this.currentEndDate = this.currentStartDate;
		}
	
		formData.putAll(dateTimeToFormData("dp", this.currentStartDate));
		formData.putAll(dateTimeToFormData("dp2", this.currentEndDate));
		
		if (this.recur == null) {
			formData.put(eventName + "_repeatUnit", new String[] {"none"});
		} else {
			if (recur.getFrequency().equals(Recur.DAILY)) {
				formData.put(eventName + "_repeatUnit", new String[] {"day"});
			} else if (recur.getFrequency().equals(Recur.WEEKLY)) {
				formData.put(eventName + "_repeatUnit", new String[] {"week"});
			} else if (recur.getFrequency().equals(Recur.MONTHLY)) {
				formData.put(eventName + "_repeatUnit", new String[] {"month"});
			} else if (recur.getFrequency().equals(Recur.YEARLY)) {
				formData.put(eventName + "_repeatUnit", new String[] {"year"});
			}
			
			if (recur.getInterval() == -1) {
				recur.setInterval(1);
			}

			formData.put(eventName + "_everyN", new String[] {Integer.toString(recur.getInterval())});
			
//			Iterator weekNoListIt = recur.getWeekNoList().iterator();
//			while (weekNoListIt.hasNext()) {
//				Integer number = (Integer)weekNoListIt.next();
//				formData.put(eventName + "_day" + number, new String[] {"on"});
//			}
			
			if (!recur.getDayList().isEmpty()) {
				Iterator dayListIt = recur.getDayList().iterator();
				while (dayListIt.hasNext()) {
					WeekDay weekDay = (WeekDay)dayListIt.next();
					String dayOfWeek = weekDay.getDay();
										
					if (!recur.getSetPosList().isEmpty()) {
						int offset = 0;
						Iterator setPosListIt = recur.getSetPosList().iterator();
						if (setPosListIt.hasNext()) {
							offset = ((Integer)setPosListIt.next()).intValue();
						}
						
						String offsetParam = "";
		                if (offset == 1) {
		                    offsetParam = "first";
		                } else if (offset == 2) {
		                    offsetParam = "second";
		                } else if (offset == 3) {
		                    offsetParam = "third";
		                } else if (offset == 4) {
		                    offsetParam = "fourth";
		                } else if (offset == 5) {
		                    offsetParam = "last";
		                }
	                
						String dayOfWeekParam = "";
		                if (dayOfWeek.equals("SU")) {
		                    dayOfWeekParam = "Sunday";
		                } else if (dayOfWeek.equals("MO")) {
		                    dayOfWeekParam = "Monday";
		                } else if (dayOfWeek.equals("TU")) {
		                    dayOfWeekParam = "Tuesday";
		                } else if (dayOfWeek.equals("WE")) {
		                    dayOfWeekParam = "Wednesday";
		                } else if (dayOfWeek.equals("TH")) {
		                    dayOfWeekParam = "Thursday";
		                } else if (dayOfWeek.equals("FR")) {
		                    dayOfWeekParam = "Friday";
		                } else if (dayOfWeek.equals("SA")) {
		                    dayOfWeekParam = "Saturday";
		                }
						
		                formData.put(eventName + "_onDayCard", new String[] {offsetParam});
						formData.put(eventName + "_dow", new String[] {dayOfWeekParam});
					} else if (recur.getSetPosList().isEmpty()) {
		                if (dayOfWeek.equals("SU")) {
		                    formData.put(eventName + "_day" + 0, new String[] {"on"});
		                } else if (dayOfWeek.equals("MO")) {
		                   	formData.put(eventName + "_day" + 1, new String[] {"on"});
		                } else if (dayOfWeek.equals("TU")) {
		                   	formData.put(eventName + "_day" + 2, new String[] {"on"});
		                } else if (dayOfWeek.equals("WE")) {
		                   	formData.put(eventName + "_day" + 3, new String[] {"on"});
		                } else if (dayOfWeek.equals("TH")) {
		                   	formData.put(eventName + "_day" + 4, new String[] {"on"});
		                } else if (dayOfWeek.equals("FR")) {
		                   	formData.put(eventName + "_day" + 5, new String[] {"on"});
		                } else if (dayOfWeek.equals("SA")) {
		                   	formData.put(eventName + "_day" + 6, new String[] {"on"});
		                }
					}
				}
			}

			if (!recur.getMonthDayList().isEmpty()) {
				List monthDayParam = new ArrayList();
				
				Iterator monthDayListIt = recur.getMonthDayList().iterator();
				while (monthDayListIt.hasNext()) {
					Integer monthDay = (Integer)monthDayListIt.next();
					monthDayParam.add(monthDay.toString());
				}
				formData.put(eventName + "_dom", monthDayParam.toArray(new String[]{}));
			}
			
			if (!recur.getMonthList().isEmpty()) {
				List monthListParam = new ArrayList();
				
				Iterator monthListIt = recur.getMonthList().iterator();
				while (monthListIt.hasNext()) {
					int month = ((Integer)monthListIt.next()).intValue();
					month--;
					monthListParam.add(Integer.toString(month));
				}
				formData.put(eventName + "_month", monthListParam.toArray(new String[]{}));
			}
			
			if (recur.getUntil() != null) {
				formData.put(eventName + "_rangeSel", new String[] {"until"});
				formData.putAll(dateToFormData("endRange", new DateTime(recur.getUntil())));
			} else if (recur.getCount() != -1) {
				formData.put(eventName + "_rangeSel", new String[] {"count"});
				formData.put(eventName + "_repeatCount", new String[] {Integer.toString(recur.getCount())});
			} else {
				formData.put(eventName + "_rangeSel", new String[] {"forever"});
			}

		}

		
		MapInputData inputData = new MapInputData(formData);
		Long entryId = null;
		try {
			entryId = folderModule.addEntry(folderId, entryType, inputData, new HashMap());
			addedEntriesIds.add(entryId);
		} catch (AccessControlException e) {
			logger.error("Can not create entry from iCal file.", e);
		} catch (WriteFilesException e) {
			logger.error("Can not create entry from iCal file.", e);
		}
		
		logger.error("New entry id created from iCal file [" + entryId + "]");// TODO: change to debug

	}

	private Map dateTimeToFormData(String keyPrefix, DateTime dateTime) {
		DateTime dt = dateTime.withZone(DateTimeZone.UTC);
		
		Map result = new HashMap();
		result.put(keyPrefix + "_" + eventName + "_month", new String[] {Integer.toString(dt.getMonthOfYear())});
		result.put(keyPrefix + "_" + eventName + "_date", new String[] {Integer.toString(dt.getDayOfMonth())});
		result.put(keyPrefix + "_" + eventName + "_year", new String[] {Integer.toString(dt.getYear())});
		result.put(keyPrefix + "_" + eventName + "_0_hour", new String[] {Integer.toString(dt.getHourOfDay())});
		result.put(keyPrefix + "_" + eventName + "_0_minute", new String[] {Integer.toString(dt.getMinuteOfHour())});
		result.put(keyPrefix + "_" + eventName + "_timezoneid", new String[] {dt.getZone().getID()});
		result.put(keyPrefix + "_" + eventName + "_hidden", new String[] {dt.toString("yyyy-MM-dd'T'HH:mm:ss")});
		return result; 
	}
	
	private Map dateToFormData(String keyPrefix, DateTime dateTime) {
		DateTime dt = dateTime.withZone(DateTimeZone.UTC);
		
		Map result = new HashMap();
		result.put(keyPrefix + "_" + eventName + "_month", new String[] {Integer.toString(dt.getMonthOfYear())});
		result.put(keyPrefix + "_" + eventName + "_date", new String[] {Integer.toString(dt.getDayOfMonth())});
		result.put(keyPrefix + "_" + eventName + "_year", new String[] {Integer.toString(dt.getYear())});
		result.put(keyPrefix + "_" + eventName + "_timezoneid", new String[] {dt.getZone().getID()});
		result.put(keyPrefix + "_" + eventName + "_hidden", new String[] {dt.toString("yyyy-MM-dd'T'00:00:00")});
		return result;
	}

	public List getAddedEntriesIds() {
		return addedEntriesIds;
	}



}
