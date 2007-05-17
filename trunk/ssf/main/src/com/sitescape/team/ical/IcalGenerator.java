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

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.UtcOffset;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;

import net.fortuna.ical4j.model.parameter.TzId;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.Version;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.joda.time.DateTimeZone;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.util.ConfigPropertyNotFoundException;
import com.sitescape.team.util.Constants;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.XmlFileUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebUrlUtil;
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
	private static final ProdId PROD_ID = new ProdId("-//SiteScape Inc//ICEcore//EN");
		
	protected Log logger = LogFactory.getLog(getClass());

	/**
	 * Creates new iCalendar object and sets fields.
	 */
	public Calendar createICalendar() {
		Calendar calendar = new Calendar();
		calendar.getProperties().add(IcalGenerator.PROD_ID);
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);
		return calendar;
	}
	
	public void addEventToICalendar (Calendar calendar, DefinableEntity entry, Event event) {

		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();

		// there is probably a bug in iCal4j or in Java: for some time zones
		// the date after setting the time zone is wrong, it means: the other
		// time offset is supplied as it should be...
		TimeZone timeZone = getTimeZone(event.getTimeZone(), registry);
		if (timeZone != null) {
			VTimeZone tz = timeZone.getVTimeZone();
			if (!calendar.getComponents(Component.VTIMEZONE).contains(tz)) {
				calendar.getComponents().add(tz);
			}
		}

		VEvent vEvent = createVEvent(entry, event, timeZone);
		calendar.getComponents().add(vEvent);
	}
	
	/**
	 * Generates iCalendar for all events in given entry. If entry has no events then output object doesn't contains any events.
	 * 
	 * @param entry 
	 * @return 
	 */
	public Calendar getICalendarForEntryEvents(DefinableEntity entry) {
		Calendar calendar = createICalendar();

		if (entry.getEvents() == null || entry.getEvents().isEmpty()) {
			return calendar;
		}

		Iterator eventsIt = entry.getEvents().iterator();
		while (eventsIt.hasNext()) {
			Event event = (Event) eventsIt.next();
			addEventToICalendar (calendar, entry, event);
		}
		
		return calendar;
	}

	private TimeZone getTimeZone(java.util.TimeZone timeZone,
			TimeZoneRegistry registry) {
		DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(timeZone);
		return registry.getTimeZone(dateTimeZone.getID());
	}

	private VEvent createVEvent(DefinableEntity entry, Event event, TimeZone timeZone) {
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
			vEvent.getProperties().getProperty(Property.DTSTART).getParameters().add(Value.DATE_TIME);
		} else {
			Date d = new Date(event.getDtStart().getTime());
			vEvent = new VEvent(d, entry.getTitle());
			vEvent.getProperties().getProperty(Property.DTSTART).getParameters().add(Value.DATE);			
		}


		vEvent.getProperties().add(
				new Description(entry.getDescription().getText()));
		
		vEvent.getProperties().add(new Uid(entry.getParentBinder().getId().toString() + "-" +
											entry.getId().toString() + "-" +
											event.getId().toString()));

		addRecurrences(vEvent, event);

		return vEvent;
	}

	private void addRecurrences(VEvent vEvent, Event event) {
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
				// if time is before start time, repeat doesn't occures in calendar view - ??
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
			vEvent.getProperties().add(rrule);

		} catch (ParseException e) {
			logger.error("Error by creating RRule by iCal export. ", e);
			return;
		}
	}

}
