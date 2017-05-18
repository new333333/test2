/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.server.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;

import org.dom4j.Document;
import org.dom4j.Element;

import org.joda.time.DateMidnight;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.AbstractIntervalView;
import org.kablink.teaming.calendar.EventsViewHelper;
import org.kablink.teaming.calendar.OneMonthView;
import org.kablink.teaming.calendar.EventsViewHelper.Grid;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.rpc.shared.CalendarAppointmentsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.CalendarDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.util.*;
import org.kablink.teaming.gwt.client.util.AssignmentInfo.AssigneeType;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.CalendarHelper;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.EventHelper;
import org.kablink.teaming.web.util.ListFolderHelper;
import org.kablink.teaming.web.util.ListUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.ListFolderHelper.ModeType;
import org.kablink.util.Html;
import org.kablink.util.cal.Duration;
import org.kablink.util.search.Constants;

import com.bradrydzewski.gwt.calendar.client.Appointment;
import com.bradrydzewski.gwt.calendar.client.AppointmentStyle;
import com.bradrydzewski.gwt.calendar.client.Attendee;

/**
 * Helper methods for the GWT calendar handling.
 *
 * @author drfoster@novell.com
 */
@SuppressWarnings("deprecation")
public class GwtCalendarHelper {
	protected static Log m_logger = LogFactory.getLog(GwtCalendarHelper.class);
	
	// The following is used to store a calendar start date in the
	// session cache.
	private static final String	START_DAY	= "gwtCalStartDay";

	// The following is used to Map binder ID's to colors when
	// displaying appointments in the calendar.
	private static final AppointmentStyle[] BINDER_COLORS = new AppointmentStyle[] {
		AppointmentStyle.BLUE,
		AppointmentStyle.RED,
		AppointmentStyle.PINK,
		AppointmentStyle.PURPLE,
		AppointmentStyle.DARK_PURPLE,
		AppointmentStyle.STEELE_BLUE,
		AppointmentStyle.LIGHT_BLUE,
		AppointmentStyle.TEAL,
		AppointmentStyle.LIGHT_TEAL,
		AppointmentStyle.GREEN,
		AppointmentStyle.LIGHT_GREEN,
		AppointmentStyle.YELLOW_GREEN,
		AppointmentStyle.YELLOW,
		AppointmentStyle.ORANGE,
		AppointmentStyle.RED_ORANGE,
		AppointmentStyle.LIGHT_BROWN,
		AppointmentStyle.LIGHT_PURPLE,
		AppointmentStyle.GREY,
		AppointmentStyle.BLUE_GREY,
		AppointmentStyle.YELLOW_GREY,
		AppointmentStyle.BROWN,
	};
	
	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtCalendarHelper() {
		// Nothing to do.
	}

	/*
	 * Adds a recurrence to an appointment based on an event.
	 */
	@SuppressWarnings("unchecked")
	private static CalendarRecurrence getRecurrenceFromEntryMap(Map m, String key) {
		// Does the map contain any recurrence dates?
		String recurrenceDates = GwtEventHelper.getStringFromEntryMapRaw(m, key);
		if (!(MiscUtil.hasString(recurrenceDates))) {
			// No!  Bail.
			return null;
		}

		// Do the dates contain any date ranges?
		String[] dateRanges = recurrenceDates.split(",");
		if ((null == dateRanges) || (0 == dateRanges.length)){
			// No!  Bail.
			return null;
		}

		// Scan the date ranges.
		CalendarRecurrence reply = new CalendarRecurrence();
		for (String dateRange:  dateRanges) {
			// Does this date range contain exactly 2 dates?
			String[] dates = dateRange.split(" ");
			if ((null != dates) && (2 == dates.length)) {
				try {
					// Yes!  Parse them and add them to the recurrence
					// object.
					Date startDate = DateTools.stringToDate(dates[0]);
					Date endDate   = DateTools.stringToDate(dates[1]);
					if ((null != startDate) && (null != endDate)) {
						reply.addRecurrence(startDate, endDate);
					}
				}
				catch (ParseException pe) {
					// Ignore.
				}
			}
		}
		
		// Do we have any dates in the recurrence object?
		if (!(reply.isRecurrent())) {
			// No!  Bail. 
			return null;
		}

		// If we get here, reply refers to the recurrence object
		// containing the recurrence dates from the entry map.  Return
		// it.
		return reply;
	}
	
	/*
	 * Hack to get around the fact that all day events in Vibe do not
	 * factor in a timezone where the gwt-cal calendar widget DOES.
	 * 
	 * We adjust all day event dates so that when the widgets accounts
	 * for the timezone, this adjustment and that one cancel each other
	 * out.
	 */
	private static Date adjustDateForAllDay(Date date, long browserTZOffset) {
		Date reply = new DateMidnight(date).toDateTime().toDate();
		long dtMS = (reply.getTime() - browserTZOffset);
		dtMS += (12l * Duration.MILLIS_PER_HOUR);	// Bugzilla 943340:  Adjusts to noon instead of midnight to account for the browser TZ not accounting for daylight savings offsets.
		reply.setTime(dtMS);
		return reply;
	}
	
	/**
	 * Uses the AssignmentInfo's in the appointments to construct the
	 * attendee list for them.
	 * 
	 * @param appointments
	 */
	public static void buildCalendarAttendeesFromVibeAttendees(List<Appointment> appointments) {
		// Scan the List<Appointment>...
		for (Appointment a:  appointments) {
			// ...building the CalendarAttendee's for each.
			buildCalendarAttendeesFromVibeAttendees(a);
		}		
	}
	
	/**
	 * Uses the AssignmentInfo's in the appointment to construct the
	 * attendee list for it.
	 * 
	 * @param appointment
	 */
	public static void buildCalendarAttendeesFromVibeAttendees(Appointment appointment) {
		// Extract and clear the current attendee list from the
		// appointment.
		List<Attendee> attendees = appointment.getAttendees();
		attendees.clear();
		
		// Scan this Appointment's individual assignees...
		CalendarAppointment ca = ((CalendarAppointment) appointment);
		for (AssignmentInfo ai:  ca.getVibeAttendees()) {
			// ...and add a CalendarAttendee for each to the
			// ...appointment.
			attendees.add(new CalendarAttendee(ai));
		}
		
		// Scan this Appointment's group assignees...
		for (AssignmentInfo ai:  ca.getVibeAttendeeGroups()) {
			// ...and add a CalendarAttendee for each to the
			// ...appointment.
			attendees.add(new CalendarAttendee(ai));
		}
		
		// Scan this Appointment's team assignees...
		for (AssignmentInfo ai:  ca.getVibeAttendeeTeams()) {
			// ...and add a CalendarAttendee for each to the
			// ...appointment.
			attendees.add(new CalendarAttendee(ai));
		}
	}
	
	/**
	 * Returns the date string to display on a calendar's navigation bar.
	 * 
	 * @param user
	 * @param firstDay
	 * @param startDay
	 * @param dayView
	 * @param weekFirstDay
	 * 
	 * @return
	 */
	public static String buildCalendarDateDisplay(User user, Calendar firstDay, Calendar startDay, CalendarDayView dayView, int weekFirstDay) {
		String		firstDayToShow = GwtServerHelper.getDateString(firstDay.getTime(), DateFormat.MEDIUM);
		String		reply;
		switch (dayView) {
		default:
		case MONTH:      reply = (NLT.get(EventsViewHelper.monthNames[startDay.get(Calendar.MONTH)]) + ", " + String.valueOf(startDay.get(Calendar.YEAR)));     break;
		case ONE_DAY:    reply =                           GwtServerHelper.getDateString(startDay.getTime(),                               DateFormat.MEDIUM);  break;
		case THREE_DAYS: reply = (firstDayToShow + " - " + GwtServerHelper.getDateString(getLastDayToShow(user, firstDay, +  3).getTime(), DateFormat.MEDIUM)); break; 
		case FIVE_DAYS:  reply = (firstDayToShow + " - " + GwtServerHelper.getDateString(getLastDayToShow(user, firstDay, +  5).getTime(), DateFormat.MEDIUM)); break;
		case WEEK:       reply = (firstDayToShow + " - " + GwtServerHelper.getDateString(getLastDayToShow(user, firstDay, +  7).getTime(), DateFormat.MEDIUM)); break;
		case TWO_WEEKS:  reply = (firstDayToShow + " - " + GwtServerHelper.getDateString(getLastDayToShow(user, firstDay, + 14).getTime(), DateFormat.MEDIUM)); break;
		}
		return reply;
	}

	/*
	 * Stores a calendar start day in the session cache.
	 */
	private static void cacheCalendarStartDay(HttpServletRequest request, Date startDay, Long folderId) {
		WebHelper.getRequiredSession(request).setAttribute(
			START_DAY,
			(String.valueOf(folderId) + ":" + String.valueOf(startDay.getTime())));
	}
	
	/*
	 * When initially built, the AssignmentInfo's in the appointments
	 * only contain the assignee IDs.  We need to complete them with
	 * each assignee's title, ...
	 */
	private static void completeAIs(AllModulesInjected bs, HttpServletRequest request, ArrayList<Appointment> appointments) {
		// If we don't have any appointments to complete...
		if (!(MiscUtil.hasItems(appointments))) {
			// ..bail.
			return;
		}
		
		// Allocate List<Long>'s to track the assignees that need to be
		// completed.
		List<Long> principalIds = new ArrayList<Long>();
		List<Long> teamIds      = new ArrayList<Long>();

		// Scan the List<Appointment>.
		for (Appointment a:  appointments) {
			// Scan this Appointment's individual attendees...
			CalendarAppointment ca = ((CalendarAppointment) a);
			for (AssignmentInfo ai:  ca.getVibeAttendees()) {
				// ...tracking each unique ID.
				ListUtil.addLongToListLongIfUnique(principalIds, ai.getId());
			}
			
			// Scan this Appointment's group attendees...
			for (AssignmentInfo ai:  ca.getVibeAttendeeGroups()) {
				// ...tracking each unique ID.
				ListUtil.addLongToListLongIfUnique(principalIds, ai.getId());
			}
			
			// Scan this Appointment's team attendees...
			for (AssignmentInfo ai:  ca.getVibeAttendeeTeams()) {
				// ...tracking each unique ID.
				ListUtil.addLongToListLongIfUnique(teamIds, ai.getId());
			}
		}
		
		// If we don't have any assignees to complete...
		boolean hasPrincipals = (!(principalIds.isEmpty()));
		boolean hasTeams      = (!(teamIds.isEmpty()));		
		if ((!hasPrincipals) && (!hasTeams)) {
			// ...bail.
			return;
		}

		// Construct Maps, mapping the IDs to their titles, membership
		// counts, ...
		Map<Long, String>			avatarUrls        = new HashMap<Long, String>();
		Map<Long, String>			principalEMAs     = new HashMap<Long, String>();
		Map<Long, String>			principalTitles   = new HashMap<Long, String>();
		Map<Long, Integer>			groupCounts       = new HashMap<Long, Integer>();
		Map<Long, GwtPresenceInfo>	userPresence      = new HashMap<Long, GwtPresenceInfo>();
		Map<Long, Boolean>			userExternal      = new HashMap<Long, Boolean>();
		Map<Long, Long>				presenceUserWSIds = new HashMap<Long, Long>();
		Map<Long, String>			teamTitles        = new HashMap<Long, String>();
		Map<Long, Integer>			teamCounts        = new HashMap<Long, Integer>();
		GwtEventHelper.readEventStuffFromDB(
			// Uses these...
			bs,
			request,
			principalIds,
			teamIds,

			// ...to complete these.
			principalEMAs,
			principalTitles,
			groupCounts,
			userPresence,
			userExternal,
			presenceUserWSIds,
			
			teamTitles,
			teamCounts,
			
			avatarUrls);
		
		// Scan the List<Appointment> again.
		for (Appointment a:  appointments) {
			// The removeList is used to handle cases where an ID could
			// not be resolved (e.g., an 'Assigned To' user has been
			// deleted.)
			List<AssignmentInfo> removeList = new ArrayList<AssignmentInfo>();
			
			// Scan this Appointment's individual assignees again...
			CalendarAppointment ca = ((CalendarAppointment) a);
			for (AssignmentInfo ai:  ca.getVibeAttendees()) {
				// ...setting each one's title.
				if (GwtEventHelper.setAssignmentInfoTitle(           ai, principalTitles )) {
					GwtEventHelper.setAssignmentInfoEmailAddress(    ai, principalEMAs    );
					GwtEventHelper.setAssignmentInfoPresence(        ai, userPresence     );
					GwtEventHelper.setAssignmentInfoExternal(        ai, userExternal     );
					GwtEventHelper.setAssignmentInfoPresenceUserWSId(ai, presenceUserWSIds);
					GwtEventHelper.setAssignmentInfoAvatarUrl(       ai, avatarUrls       );
				}
				else {
					removeList.add(ai);
				}
			}
			GwtServerHelper.removeUnresolvedAssignees(ca.getVibeAttendees(), removeList);
			
			// Scan this Appointment's group assignees again...
			for (AssignmentInfo ai:  ca.getVibeAttendeeGroups()) {
				// ...setting each one's title and membership count.
				if (GwtEventHelper.setAssignmentInfoTitle(  ai, principalTitles)) {
					GwtEventHelper.setAssignmentInfoMembers(ai, groupCounts     );
					ai.setPresenceDude("pics/group_20.png");
				}
				else {
					removeList.add(ai);
				}
			}
			GwtServerHelper.removeUnresolvedAssignees(ca.getVibeAttendeeGroups(), removeList);
			
			// Scan this Appointment's team assignees again...
			for (AssignmentInfo ai:  ca.getVibeAttendeeTeams()) {
				// ...setting each one's title and membership count.
				if (GwtEventHelper.setAssignmentInfoTitle(  ai, teamTitles)) {
					GwtEventHelper.setAssignmentInfoMembers(ai, teamCounts );
					ai.setPresenceDude("pics/team_16.png");
				}
				else {
					removeList.add(ai);
				}
			}
			GwtServerHelper.removeUnresolvedAssignees(ca.getVibeAttendeeTeams(), removeList);
		}

		// Finally, use the AssignmentInfo's in the appointments to
		// construct the attendee list for the appointment.
		buildCalendarAttendeesFromVibeAttendees(appointments);
	}

	/*
	 * Logs the contents of a List<Appointment>.
	 */
	private static void dumpAppointmentList(List<Appointment> appointments) {		
		// If debug logging is disabled...
		if (!(GwtLogHelper.isDebugEnabled(m_logger))) {
			// ...bail.
			return;
		}

		// If there are no appointments in the list...
		if (appointments.isEmpty()) {
			// ...log that fact and bail.
			GwtLogHelper.debug(m_logger, "GwtCalendarHelper.dumpAppointmentList( EMPTY )");
			return;
		}

		// Dump the appointments.
		GwtLogHelper.debug(m_logger, "GwtCalendarHelper.dumpAppointmentList( START: " + String.valueOf(appointments.size()) + " )");
		for (Appointment a:  appointments) {
			CalendarAppointment ca = ((CalendarAppointment) a);
			StringBuffer buf = new StringBuffer("");
			buf.append(GwtEventHelper.buildDumpString("\n\tEntry ID",         ca.getId()                       ));
			buf.append(GwtEventHelper.buildDumpString("\n\tFolder ID",        ca.getFolderId()                 ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tTitle",          ca.getTitle()                    ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tDescription: ",  ca.getDescription()              ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tStart",          ca.getStart()                    ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tEnd",            ca.getEnd()                      ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tCreator: ",      ca.getCreatedBy()                ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\t\tCreator ID: ", ca.getCreatorId()                ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tIsTask",         ca.isTask()                      ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tIsAllDay",       ca.isAllDay()                    ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tAttendees",      ca.getVibeAttendees(),      false));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tAttendeeGroups", ca.getVibeAttendeeGroups(), true ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tAttendeeTeams",  ca.getVibeAttendeeTeams(),  true ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tCanModify",      ca.canModify()                   ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tCanPurge",       ca.canPurge()                    ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tCanTrash",       ca.canTrash()                    ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tSeen",           ca.isSeen()                      ));
			GwtLogHelper.debug(m_logger, "GwtCalendarHelper.dumpAssignmentList()" + buf.toString());
		}
		GwtLogHelper.debug(m_logger, "GwtCalendarHelper.dumpAssignmentList( END )");
	}

	/*
	 * Scans the List<Appointment> and does the following:
	 * 1) Sets the access rights for the current user for each
	 *    appointment; and
	 * 2) Validates that any recurrence information stored on the
	 *    appointment should be there.
	 */
	private static void fixupAppointments(AllModulesInjected bs, List<Appointment> appointments) {
		// If we don't have any Appointment's to complete...
		if (!(MiscUtil.hasItems(appointments))) {
			// ..bail.
			return;
		}

		// Collect the entry IDs of the appointments from the
		//List<Appointment>.
		List<Long> entryIds  = new ArrayList<Long>();
		for (Appointment appointment:  appointments) {
			entryIds.add(((CalendarAppointment) appointment).getEntityId().getEntityId());
		}
		
		try {
			// Read the FolderEntry's for the appointments...
			FolderModule fm = bs.getFolderModule();
			SortedSet<FolderEntry> entries = fm.getEntries(entryIds);
			
			// ...mapping each FolderEntry to its ID.
			Map<Long, FolderEntry> entryMap = new HashMap<Long, FolderEntry>();
			for (FolderEntry entry: entries) {
				entryMap.put(entry.getId(), entry);
			}

			// Scan the List<Appointment> again.
			for (Appointment appointment:  appointments) {
				// Do we have the FolderEntry for this appointment?
				CalendarAppointment ca = ((CalendarAppointment) appointment);
				FolderEntry entry = entryMap.get(ca.getEntityId().getEntityId());
				if (null != entry) {
					// Yes!  Store the user's rights to that
					// FolderEntry.
					ca.setCanModify(fm.testAccess(entry, FolderOperation.modifyEntry   ));
					ca.setCanPurge( fm.testAccess(entry, FolderOperation.deleteEntry   ));
					ca.setCanTrash( fm.testAccess(entry, FolderOperation.preDeleteEntry));

					// If the entry is not a recurrent event...
					String attr = (ca.isTask() ? TaskHelper.TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME : "event");
					CustomAttribute customAttribute = entry.getCustomAttribute(attr);
					Event dbEvent = ((null == customAttribute) ? null : ((Event) customAttribute.getValue()));
					if ((null == dbEvent) || (Event.NO_RECURRENCE == dbEvent.getFrequency())) {
						// ...make sure we're not tracking any
						// ...recurrence information for it.
						ca.setServerRecurrence(null);
					}
				}
			}
		}
		
		catch (Exception ex) {/* Ignored. */}
	}

	/**
	 * Returns a List<CalendarAppointment> of the appointments in the
	 * calendar based on the given calendar display data.
	 * 
	 * Note:  The logic used by this method is based of that used in
	 *    AjaxController.ajaxFindCalendarEvents()
	 * 
	 * @param bs
	 * @param request
	 * @param browserTZOffset
	 * @param folderId
	 * @param cdd
	 * @param quickFilter
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static CalendarAppointmentsRpcResponseData getCalendarAppointments(AllModulesInjected bs, HttpServletRequest request, long browserTZOffset, Long folderId, CalendarDisplayDataRpcResponseData cdd, String quickFilter) throws GwtTeamingException {
		try {
			// Access the objects we'll need to process the
			// appointments from this folder.
			Folder			folder               = bs.getFolderModule().getFolder(folderId);
			User			user                 = GwtServerHelper.getCurrentUser();
			Long			userId               = user.getId();
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(userId, folderId);

			// Setup the search options to return up to 10,000 items...
			Map options = new HashMap();
			options.put(ObjectKeys.SEARCH_MAX_HITS, 10000);

			// ...we'll read appointments spanning 1 month...
			List intervals = new ArrayList();
			AbstractIntervalView calendarViewRangeDates = new OneMonthView(cdd.getFirstDay(), cdd.getWeekFirstDay());
			intervals.add(calendarViewRangeDates.getVisibleIntervalRaw());
	       	options.put(ObjectKeys.SEARCH_EVENT_DAYS, intervals);

	       	// ...add any specific start/end dates we need to read...
	       	String start = DateTools.dateToString(calendarViewRangeDates.getVisibleStart(), DateTools.Resolution.SECOND);
	       	String end   = DateTools.dateToString(calendarViewRangeDates.getVisibleEnd(),   DateTools.Resolution.SECOND);
	       	CalendarShow show = cdd.getShow();
	       	boolean physicalByActivity = show.equals(CalendarShow.PHYSICAL_BY_ACTIVITY);
	       	boolean physicalByCreation = show.equals(CalendarShow.PHYSICAL_BY_CREATION);
	       	boolean physicalByDate     = (physicalByActivity || physicalByCreation);
	       	if (physicalByActivity) {
		       	options.put(ObjectKeys.SEARCH_LASTACTIVITY_DATE_START, start);
		       	options.put(ObjectKeys.SEARCH_LASTACTIVITY_DATE_END,   end  );
	       	}
	       	else if (physicalByCreation) {
		       	options.put(ObjectKeys.SEARCH_CREATION_DATE_START, start);
		       	options.put(ObjectKeys.SEARCH_CREATION_DATE_END,   end  );
	       	}
	       	
	       	// ...add in any search filter...
			options.putAll(ListFolderHelper.getSearchFilter(bs, null, folder, userFolderProperties));
			GwtServerHelper.addQuickFilterToSearch(options, quickFilter);
			
			// ...and if the list is filtered...
			Document baseFilter = ((Document) options.get(ObjectKeys.SEARCH_SEARCH_FILTER));
			boolean  filtered   = (null != baseFilter); 
			if (filtered) {
				// ...and the filter is for trashed items...
				Element preDeletedOnlyTerm = (Element)baseFilter.getRootElement().selectSingleNode("//filterTerms/filterTerm[@preDeletedOnly='true']");
				if (preDeletedOnlyTerm != null) {
					// ...maintain the search for trashed items.
					options.put(ObjectKeys.SEARCH_PRE_DELETED, Boolean.TRUE);
				}
			}
			
			// Are we searching with a filter for physical events? 
			List<Map> events;
			BinderModule bm = bs.getBinderModule();
			boolean virtual = show.equals(CalendarShow.VIRTUAL);
			if ((!virtual) && filtered) {
				// Yes!  Simply perform the search using the filter.
				Map retMap = bm.executeSearchQuery(baseFilter, Constants.SEARCH_MODE_NORMAL, options);
				events = ((List) retMap.get(ObjectKeys.SEARCH_ENTRIES));
			}
			
			else {
				// No, we aren't searching with a filter for physical
				// events!  Is it a search for virtual events using a
				// filter?
				if (virtual && filtered) {
					// Yes!  Instead of searching the current binder,
					// which the filter should have been setup for, we
					// need to search the entire tree.  Adjust the
					// filter accordingly.
					Element foldersListFilterTerm = ((Element) baseFilter.getRootElement().selectSingleNode("//filterTerms/filterTerm[@filterType='foldersList']"));
					Element filterFolderId        = ((Element) baseFilter.getRootElement().selectSingleNode("//filterTerms/filterTerm[@filterType='foldersList']/filterFolderId"));
					if ((null != foldersListFilterTerm) && (null != filterFolderId)) {
						foldersListFilterTerm.addAttribute("filterType", "ancestriesList");
						filterFolderId.setText(String.valueOf(bs.getWorkspaceModule().getTopWorkspaceId()));
					}
				}

				// Perform the search using any filter.
				List<String> folderIds = new ArrayList<String>();
				folderIds.add(String.valueOf(folderId));
				ModeType modeType = (virtual ? ModeType.VIRTUAL : ModeType.PHYSICAL); 
				Document searchFilter = EventHelper.buildSearchFilterDoc(baseFilter, null, modeType, folderIds, folder, SearchUtils.AssigneeType.CALENDAR);
				Map retMap = bm.executeSearchQuery(searchFilter, Constants.SEARCH_MODE_NORMAL, options);
				events = ((List) retMap.get(ObjectKeys.SEARCH_ENTRIES));

				// Was the search for virtual events?
				if (virtual) {
					// Yes!  Are there any task events matching the
					// search criteria?
					searchFilter = EventHelper.buildSearchFilterDoc(baseFilter, null, modeType, folderIds, folder, SearchUtils.AssigneeType.TASK);
					retMap = bm.executeSearchQuery(searchFilter, Constants.SEARCH_MODE_NORMAL, options);
					List<Map> tasks = ((List) retMap.get(ObjectKeys.SEARCH_ENTRIES));
					if (MiscUtil.hasItems(tasks)) {
						// Yes!  Build a list of appointment IDs we're
						// already tracking.
						Set<String> appointmentIds = new HashSet();
						for (Map event:  events) {
							String docId = ((String) event.get(Constants.DOCID_FIELD));
							appointmentIds.add(docId);
						}
						
						// Scan the task entries we just read.
						for (Map task:  tasks) {
							// Are we tracking an event with this
							// task's ID?
							String docId = ((String) task.get(Constants.DOCID_FIELD));
							if (!(appointmentIds.contains(docId))) {
								// No!  Add the task to the event
								// list. 
								events.add(task);
							}
						}
					}
				}
			}
			
			// Do we have any events?
			CalendarAppointmentsRpcResponseData reply = new CalendarAppointmentsRpcResponseData();
			if (MiscUtil.hasItems(events)) {
				// Yes!  We need to construct Appointment's for each.
				// Scan the events
				SeenMap seenMap = bs.getProfileModule().getUserSeenMap(null);
				Map<Long, AppointmentStyle> binderColorMap = new HashMap<Long, AppointmentStyle>();
				int nextColor = 0;
				for (Map event:  events) {
					CalendarAppointment appointment = buildCalendarAppointmentFromMap(event, request, browserTZOffset, physicalByActivity, physicalByDate, seenMap);

					// Set the appointment's style.
					AppointmentStyle thisColor = binderColorMap.get(appointment.getFolderId());
					if (null == thisColor) {
						thisColor = BINDER_COLORS[nextColor];
						if (nextColor == (BINDER_COLORS.length - 1))
							nextColor  = 0;
						else nextColor += 1;
						binderColorMap.put(appointment.getFolderId(), thisColor);
					}
					appointment.setStyle(thisColor);

					reply.addAppointment(appointment);
				}
			}

			// At this point, the CalendarAppointment's in the response
			// data are not complete.  They're missing things like the
			// details about the appointements's attendees.  Complete
			// their content.  Note that we do this AFTER collecting
			// data from the search index so that we only have to
			// perform a single DB read for each type of information we
			// need to complete the CalendarAppointment's details.
			completeAIs(bs, request, reply.getAppointments());
			
			// Walk the appointments one more time performing any
			// remaining fixups on each as necessary.
			fixupAppointments(bs, reply.getAppointments());
			
			if (GwtLogHelper.isDebugEnabled(m_logger)) {
				GwtLogHelper.debug(m_logger, "GwtCalendarHelper.getCalendarAppointments( Read ArrayList<Appointment> for binder ): " + String.valueOf(folderId));
				dumpAppointmentList(reply.getAppointments());
			}

			// If we get here, reply refers to a
			// CalendarAppointmentsRpcRequestData containing the
			// appointments to be shown.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtCalendarHelper.getCalendarAppointments( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtLogHelper.getGwtClientException(e);
		}
	}

	private static CalendarAppointment buildCalendarAppointmentFromMap(Map event, HttpServletRequest request, long browserTZOffset, boolean physicalByActivity, boolean physicalByDate, SeenMap seenMap) {
		// What attributes do we use for accessing
		// information about this event?
		String  fieldName = GwtEventHelper.getStringFromEntryMapRaw(event, (Constants.EVENT_FIELD + "0"));
		boolean isTask    = fieldName.equals(Constants.EVENT_FIELD_START_END);

		String  attrGroups;
		String  attrIndividuals;
		String  attrTeams;
		if (isTask) {
            attrGroups      = TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME;
            attrIndividuals = TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME;
            attrTeams       = TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME;
        }
        else {
            attrGroups      = EventHelper.ASSIGNMENT_GROUPS_CALENDAR_ENTRY_ATTRIBUTE_NAME;
            attrIndividuals = EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME;
            attrTeams       = EventHelper.ASSIGNMENT_TEAMS_CALENDAR_ENTRY_ATTRIBUTE_NAME;
        }

		// Create a base CalendarAppointment for this event
		// and added it to the response data.
		CalendarAppointment appointment = new CalendarAppointment(isTask);

		boolean allDay;
		if (!physicalByDate) {
            // Set whether this is an all day appointment.
            String tz = GwtEventHelper.getStringFromEntryMapRaw(
                event,
                GwtEventHelper.buildEventFieldName(
                    fieldName,
                    Constants.EVENT_FIELD_TIME_ZONE_ID));
            allDay = (!(MiscUtil.hasString(tz)));
            appointment.setAllDay(allDay);
        }
        else {
            // When viewing by date, we never treat things
            // as an all day event.
            allDay = false;
        }

		// Set appointment's attendees.
		setVibeAttendees(     appointment, GwtEventHelper.getAssignmentInfoListFromEntryMap(event, attrIndividuals, AssigneeType.INDIVIDUAL));
		setVibeAttendeeGroups(appointment, GwtEventHelper.getAssignmentInfoListFromEntryMap(event, attrGroups,      AssigneeType.GROUP     ));
		setVibeAttendeeTeams( appointment, GwtEventHelper.getAssignmentInfoListFromEntryMap(event, attrTeams,       AssigneeType.TEAM      ));

		// Set the appointment's created by.
		appointment.setCreatedBy(GwtEventHelper.getStringFromEntryMapRaw(event, Constants.CREATOR_TITLE_FIELD));
		appointment.setCreatorId(GwtEventHelper.getLongFromEntryMap(     event, Constants.CREATORID_FIELD    ));

		// Set the appointment's description.
		String value = GwtViewHelper.getEntryDescriptionFromMap(request, event);
		if ((Description.FORMAT_HTML == getEntryDescFormatFromEM(event)) && MiscUtil.hasString(value)) {
            appointment.setDescriptionHtml(value);
            value = Html.stripHtml(value);
        }
		appointment.setDescription(value);

		// Are we viewing physical events by a date?
		if (physicalByDate) {
            // Yes!  Can we get the appropriate date?
            String field = (physicalByActivity ? Constants.LASTACTIVITY_FIELD : Constants.CREATION_DATE_FIELD);
            Date date = GwtEventHelper.getDateFromEntryMap(event, field);
            if (null != date) {
                // Yes!  Use it as the start and end dates
                // of the event.
                appointment.setEnd(  date);
                appointment.setStart(date);
            }
        }
        else {
			// Set the appointment's start date.
			Date startDay = GwtEventHelper.getDateFromEntryMap(
					event,
					GwtEventHelper.buildEventFieldName(
							fieldName,
							Constants.EVENT_FIELD_LOGICAL_START_DATE));
			if (null != startDay) {
				if (allDay) {
					startDay = adjustDateForAllDay(startDay, browserTZOffset);
				}
				appointment.setStart(startDay);
			}

            // No, we aren't viewing physical events by a
            // date!  Set the appointment's end date.
			Date endDate = getEndDateFromMap(event, startDay, fieldName);
			if (null != endDate) {
                if (allDay) {
                    endDate = adjustDateForAllDay(endDate, browserTZOffset);
                }
                appointment.setEnd(endDate);
            }
        }

		// Set the appointment's IDs.
		Long   eventFolderId = GwtEventHelper.getLongFromEntryMap(     event, Constants.BINDER_ID_FIELD);
		String eventId       = GwtEventHelper.getStringFromEntryMapRaw(event, Constants.DOCID_FIELD    );
		appointment.setEntityId(new EntityId(eventFolderId, Long.parseLong(eventId), EntityId.FOLDER_ENTRY));
		appointment.setId(eventId);

		// Set the appointment's seen flag.
		appointment.setSeen(seenMap.checkIfSeen(event));

		// Set the appointment's title.
		value= GwtServerHelper.getStringFromEntryMap(event, Constants.TITLE_FIELD);
		appointment.setTitle(MiscUtil.hasString(value) ? value : ("--" + NLT.get("entry.noTitle") + "--"));

		// Does the entry have any recurrence dates?
		CalendarRecurrence cr = getRecurrenceFromEntryMap(
            event,
            GwtEventHelper.buildEventFieldName(
                fieldName,
                Constants.EVENT_RECURRENCE_DATES_FIELD));

		if ((null != cr) && cr.isRecurrent()) {
            // Yes!  Add them to the appointment.
            appointment.setServerRecurrence(cr);
        }
		return appointment;
	}

	private static Date getEndDateFromMap(Map event, Date startDate, String fieldName) {
		Date endDate = GwtEventHelper.getDateFromEntryMap(
				event,
				GwtEventHelper.buildEventFieldName(
						fieldName,
						Constants.EVENT_FIELD_LOGICAL_END_DATE));
		if (endDate==null && startDate!=null) {
			TaskListItem.TaskDuration duration = GwtTaskHelper.buildTaskDuration(event);
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(startDate);
			calendar.add(Calendar.DAY_OF_YEAR, duration.getDays());
			calendar.add(Calendar.HOUR_OF_DAY, duration.getHours());
			calendar.add(Calendar.MINUTE, duration.getMinutes());
			calendar.add(Calendar.SECOND, duration.getSeconds());
			calendar.add(Calendar.WEEK_OF_YEAR, duration.getWeeks());
			endDate = calendar.getTime();
		}
		return endDate;
	}

	/**
	 * Reads the current user's display data for a calendar and returns
	 * it as a CalendarDisplayDataRpcResponseData.
	 * 
	 * @param bs
	 * @param request
	 * @param browserTZOffset
	 * @param folderInfo
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static CalendarDisplayDataRpcResponseData getCalendarDisplayData(AllModulesInjected bs, HttpServletRequest request, long browserTZOffset, BinderInfo folderInfo) throws GwtTeamingException {
		try {
			BinderModule	bm = bs.getBinderModule();
			FolderModule	fm = bs.getFolderModule();
			ProfileModule	pm = bs.getProfileModule();
			
			Long			folderId       = folderInfo.getBinderIdAsLong();
			User			user           = GwtServerHelper.getCurrentUser();
			UserProperties	userProperties = bs.getProfileModule().getUserProperties(user.getId());

			// What's the current user's view of a work week?
			int weekFirstDay = getWeekFirstDay(pm);
			int workDayStart = getWorkDayStart(pm);
			
			// What's the user's current view of the calendar?
			CalendarDayView	dayView = CalendarDayView.MONTH;
			Grid grid = EventsViewHelper.getCalendarGrid(request, userProperties, (folderInfo.getBinderId() + "_"));
			if (null == grid) {
				grid = new Grid(EventsViewHelper.GRID_MONTH, (-1));
			}
			String  gridType = grid.type;
			Integer gridSize = grid.size;
			if (MiscUtil.hasString(gridType)) {
				if (gridType.equals(EventsViewHelper.GRID_DAY)) {
					int days = ((null == gridSize) ? 1 : gridSize.intValue());
					switch (days) {
					case  1:  dayView = CalendarDayView.ONE_DAY;    break;
					case  3:  dayView = CalendarDayView.THREE_DAYS; break;
					case  5:  dayView = CalendarDayView.FIVE_DAYS;  break;
					case  7:  dayView = CalendarDayView.WEEK;       break;
					case 14:  dayView = CalendarDayView.TWO_WEEKS;  break;
					}
				}
				else if (gridType.equals(EventsViewHelper.GRID_MONTH)) {
					gridSize = (-1);
				}
			}
			
			// What day to we start viewing the calendar at.
			Calendar startDay = new GregorianCalendar(user.getTimeZone(), user.getLocale());
			Date cachedStartDay = loadCalendarStartDayFromCache(request, folderId);
			if (null != cachedStartDay) {
				startDay.setTime(cachedStartDay);
			}
			Calendar firstDay = getFirstDayToShow(user, dayView, startDay, weekFirstDay);
			
			// What's the user's current hours viewed setting?
			CalendarHours hours = CalendarHours.WORK_DAY;
			String hoursType = EventsViewHelper.getCalendarDayViewType(request);
			if (MiscUtil.hasString(hoursType)) {
				if (hoursType.equals(EventsViewHelper.DAY_VIEW_TYPE_FULL)) {
					hours = CalendarHours.FULL_DAY;
				}
			}

			// What type of events are we to show in the calendar?
			CalendarShow show = CalendarShow.PHYSICAL_EVENTS;
			String showType = EventsViewHelper.getCalendarDisplayEventType(bs, user.getId(), folderId);
			if (MiscUtil.hasString(showType)) {
				if      (showType.equals(EventsViewHelper.EVENT_TYPE_ACTIVITY)) show = CalendarShow.PHYSICAL_BY_ACTIVITY;
				else if (showType.equals(EventsViewHelper.EVENT_TYPE_CREATION)) show = CalendarShow.PHYSICAL_BY_CREATION;
				else if (showType.equals(EventsViewHelper.EVENT_TYPE_VIRTUAL))  show = CalendarShow.VIRTUAL;
			}

			// Generate a URL to use to add an entry of the default
			// type to this folder.
			AdaptedPortletURL addEntryUrl = new AdaptedPortletURL(request, "ss_forum", true);
			addEntryUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ENTRY);
			addEntryUrl.setParameter(WebKeys.URL_BINDER_ID, String.valueOf(folderId));
			Folder folder = fm.getFolder(folderId);
			List defaultEntryDefs = folder.getEntryDefinitions();
			int defaultEntryDefsCount = ((null == defaultEntryDefs) ? 0 : defaultEntryDefs.size());
			if (0 < defaultEntryDefsCount) {
				int	defaultEntryDefIndex = ListFolderHelper.getDefaultFolderEntryDefinitionIndex(
					user.getId(),
					pm,
					folder,
					defaultEntryDefs);

				addEntryUrl.setParameter(
					WebKeys.URL_ENTRY_TYPE,
					((Definition) defaultEntryDefs.get(defaultEntryDefIndex)).getId());
			}
			
			// Use the data we obtained to create a
			// CalendarDisplayDataRpcResponseData.
			CalendarDisplayDataRpcResponseData reply =
				new CalendarDisplayDataRpcResponseData(
					firstDay.getTime(),
					startDay.getTime(),
					dayView,
					hours,
					show,
					weekFirstDay,
					workDayStart,
					addEntryUrl.toString(),
					buildCalendarDateDisplay(
						user,
						firstDay,
						startDay,
						dayView,
						weekFirstDay));
			cacheCalendarStartDay(request, reply.getStartDay(), folderId);

			// Store the user's rights to the folder.
			reply.setCanAddFolderEntry(fm.testAccess(folder, FolderOperation.addEntry       ));
			reply.setCanModifyFolder(  bm.testAccess(folder, BinderOperation.modifyBinder   ));
			reply.setCanPurgeFolder(   bm.testAccess(folder, BinderOperation.deleteBinder   ));
			reply.setCanTrashFolder(   bm.testAccess(folder, BinderOperation.preDeleteBinder));

			// Return the CalendarDisplayDataRpcResponseData that we
			// just constructed.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtCalendarHelper.getCalendarDisplayData( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtLogHelper.getGwtClientException(e);
		}
	}
	
	/**
	 * Reads the current user's display data for a calendar and returns
	 * it as a CalendarDisplayDataRpcResponseData.
	 * 
	 * @param bs
	 * @param request
	 * @param browserTZOffset
	 * @param folderId
	 * @param calendarDisplayData
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static CalendarDisplayDataRpcResponseData getCalendarDisplayDate(AllModulesInjected bs, HttpServletRequest request, long browserTZOffset, Long folderId, CalendarDisplayDataRpcResponseData calendarDisplayData) throws GwtTeamingException {
		try {
			setCalendarDisplayDataDates(
				calendarDisplayData,
				calendarDisplayData.getFirstDay(),
				calendarDisplayData.getStartDay());
			
			cacheCalendarStartDay(request, calendarDisplayData.getStartDay(), folderId);
			return calendarDisplayData;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtCalendarHelper.getCalendarDisplayDate( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtLogHelper.getGwtClientException(e);
		}
	}
	
	/**
	 * Modifies the given CalendarDisplayDataRpcResponseData to the
	 * next or previous time period and returns it.
	 * 
	 * @param bs
	 * @param request
	 * @param browserTZOffset
	 * @param folderId
	 * @param calendarDisplayData
	 * @param next
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static CalendarDisplayDataRpcResponseData getCalendarNextPreviousPeriod(AllModulesInjected bs, HttpServletRequest request, long browserTZOffset, Long folderId, CalendarDisplayDataRpcResponseData calendarDisplayData, boolean next) throws GwtTeamingException {
		try {
			// Calculate the increment to use for the next/previous
			// period...
			int unit = (-1);
			int incr = (-1);
			switch (calendarDisplayData.getDayView()) {
			case ONE_DAY:     unit = Calendar.DAY_OF_YEAR;  incr = 1; break;
			case THREE_DAYS:  unit = Calendar.DAY_OF_YEAR;  incr = 3; break;
			case FIVE_DAYS:
			case WEEK:        unit = Calendar.WEEK_OF_YEAR; incr = 1; break;
			case TWO_WEEKS:   unit = Calendar.WEEK_OF_YEAR; incr = 2; break;
			case MONTH:       unit = Calendar.MONTH;        incr = 1; break;
			}
			if (!next) {
				incr = (-incr);
			}

			// ...add the increment to the first and start days in the
			// ...given display data...
			GregorianCalendar firstDay = new GregorianCalendar();
			firstDay.setTime(calendarDisplayData.getFirstDay());
			firstDay.add(unit, incr);
			
			GregorianCalendar startDay = new GregorianCalendar();
			startDay.setTime(calendarDisplayData.getStartDay());
			startDay.add(unit, incr);

			// ...and modify the CalendarDisplayDataRpcResponseData
			// ...with these values.
			setCalendarDisplayDataDates(
				calendarDisplayData,
				firstDay.getTime(),
				startDay.getTime());

			// Finally, return the modified
			// CalendarDisplayDataRpcResponseData. 
			cacheCalendarStartDay(request, calendarDisplayData.getStartDay(), folderId);
			return calendarDisplayData;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtCalendarHelper.getCalendarNextPreviousPeroid( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtLogHelper.getGwtClientException(e);
		}
	}
	
	/*
	 * Returns the format of an entry description from a search results
	 * Map.
	 */
	@SuppressWarnings("unchecked")
	private static int getEntryDescFormatFromEM(Map em) {
		int reply = Description.FORMAT_HTML;
		String descFmt = GwtServerHelper.getStringFromEntryMap(em, Constants.DESC_FORMAT_FIELD);
		if (MiscUtil.hasString(descFmt)) {
			try {
				reply = Integer.parseInt(descFmt);
			}
			catch (Exception ex) {}
		}
		return reply;
	}
	
	/*
	 * Returns the first day to show based on a start date and the
	 * user's first day of the work week. 
	 */
	private static Calendar getFirstDayToShow(User user, CalendarDayView dayView, Calendar firstDay, int weekFirstDay) {
		Calendar firstDayToShow;
		switch(dayView) {
		case WEEK:
		case TWO_WEEKS: {
			int firstDayOffset = (firstDay.get(Calendar.DAY_OF_WEEK) - weekFirstDay);
			if       ( 6  == firstDayOffset) firstDayOffset = (-1);
			else if ((-6) == firstDayOffset) firstDayOffset =   1;
			else if  ( 5  == firstDayOffset) firstDayOffset = (-2);
			else if ((-5) == firstDayOffset) firstDayOffset =   2;
			firstDayToShow = firstDay;
			firstDayToShow.set(Calendar.DAY_OF_WEEK, (firstDay.get(Calendar.DAY_OF_WEEK) - firstDayOffset));
			
			break;
		}
		
		case FIVE_DAYS: {
			weekFirstDay  = Calendar.MONDAY;	// We always start a 5 day view on Monday.
			int dayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK);
			int firstDayOffset;
			if      (dayOfWeek > weekFirstDay) firstDayOffset = ( dayOfWeek      - weekFirstDay);
			else if (dayOfWeek < weekFirstDay) firstDayOffset = ((dayOfWeek + 7) - weekFirstDay);
			else                               firstDayOffset = 0;
			firstDayToShow = firstDay;
			firstDayToShow.set(Calendar.DAY_OF_WEEK, (firstDay.get(Calendar.DAY_OF_WEEK) - firstDayOffset));
			
			break;
		}
			
		default:
    		firstDayToShow = new GregorianCalendar(user.getTimeZone(), user.getLocale());
    		firstDayToShow.setTime(firstDay.getTime());
    		break;
		}
		
		return firstDayToShow;
	}
	
	/*
	 * Returns a last date given a first day and a number of days.
	 */
	private static Calendar getLastDayToShow(User user, Calendar firstDay, int days) {
		Calendar lastDay = new GregorianCalendar(user.getTimeZone(), user.getLocale());
		lastDay.setTime(firstDay.getTime());
		lastDay.set(Calendar.DAY_OF_YEAR, lastDay.get(Calendar.DAY_OF_YEAR) + (days - 1));
		return lastDay;
	}

	/*
	 * Returns the first day of the week for the current user.
	 */
	private static int getWeekFirstDay(ProfileModule pm) {
		int reply;
		
		// Does the user have a week start day saved in their
		// preferences?
		Long    userId    = GwtServerHelper.getCurrentUserId();
		Integer weekStart = ((Integer) pm.getUserProperties(userId).getProperty(ObjectKeys.USER_PROPERTY_CALENDAR_FIRST_DAY_OF_WEEK));
		if (null == weekStart) {
			// No!  Use an appropriate default.
			reply = CalendarHelper.getFirstDayOfWeek();
			pm.setUserProperty(userId, ObjectKeys.USER_PROPERTY_CALENDAR_FIRST_DAY_OF_WEEK, new Integer(reply));
		}
		
		else {
			// Yes, the user has a setting saved!  Use it.
			reply = weekStart;
		}

		// If we get here, reply contains the week start day for the
		// current user.  Return it.
		return reply;
	}

	/*
	 * Returns the time the work day starts for the current user.
	 */
	private static int getWorkDayStart(ProfileModule pm) {
		int reply;

		// Does the user have a work day start hour saved in their
		// preferences?
		Long userId = GwtServerHelper.getCurrentUserId();
		Integer workDayStart = ((Integer) pm.getUserProperties(userId).getProperty(ObjectKeys.USER_PROPERTY_CALENDAR_WORK_DAY_START));
		if (null == workDayStart) {
			// No!  Use an appropriate default.
			reply = 6;
			pm.setUserProperty(userId, ObjectKeys.USER_PROPERTY_CALENDAR_WORK_DAY_START, new Integer(reply));
		}
		
		else {
			// Yes, the user has a setting saved.  Use it.
			reply = workDayStart;
		}

		// If we get here, reply contains the work day start hour for
		// the current user.  Return it.
		return reply;
	}
	
	/*
	 * Loads the calendar start day from the session cache.  Returns
	 * null if a date hasn't been cached.
	 */
	private static Date loadCalendarStartDayFromCache(HttpServletRequest request, Long folderId) {
		// Does the session contain a start day value?
		HttpSession	hSession  = WebHelper.getRequiredSession(request);
		String		startDayS = ((String) hSession.getAttribute(START_DAY));
		if (MiscUtil.hasString(startDayS)) {
			// Yes!  Is if for this folder?
			String[] parts = startDayS.split(":");
			String   fid   = String.valueOf(folderId);
			if (fid.equals(parts[0])) {
				// Yes!  Return it as a Date.
				return new Date(Long.parseLong(parts[1]));
			}
			
			// No, it isn't for this folder!  Remove it from the
			// session.
			hSession.removeAttribute(START_DAY);
		}
		
		// If we get here, we couldn't find a start day for the folder
		// in question.  Return null.
		return null;
	}
	
	/**
	 * Saves a user's CalendarDayView selection and returns the
	 * appropriate CalendarDisplayDataRpcResponseData based on the
	 * change.
	 * 
	 * @param bs
	 * @param request
	 * @param browserTZOffset
	 * @param folderInfo
	 * @param dayView
	 * @param date
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static CalendarDisplayDataRpcResponseData saveCalendarDayView(AllModulesInjected bs, HttpServletRequest request, long browserTZOffset, BinderInfo folderInfo, CalendarDayView dayView, Date date) throws GwtTeamingException {
		try {
			// Store the new day view...
			String gridType;
			int    gridSize;
			switch (dayView) {
			default:
			case MONTH:       gridType = EventsViewHelper.GRID_MONTH; gridSize = (-1); break;
			case ONE_DAY:     gridType = EventsViewHelper.GRID_DAY;   gridSize =   1;  break;
			case THREE_DAYS:  gridType = EventsViewHelper.GRID_DAY;   gridSize =   3;  break;
			case FIVE_DAYS:   gridType = EventsViewHelper.GRID_DAY;   gridSize =   5;  break;
			case WEEK:        gridType = EventsViewHelper.GRID_DAY;   gridSize =   7;  break;
			case TWO_WEEKS:   gridType = EventsViewHelper.GRID_DAY;   gridSize =  14;  break;
			}

			User			user           = GwtServerHelper.getCurrentUser();
			Long			userId         = user.getId();
			UserProperties	userProperties = bs.getProfileModule().getUserProperties(userId);
			Map				grids          = EventsViewHelper.setCalendarGrid(request, userProperties, (folderInfo.getBinderId() + "_"), gridType, gridSize);
			if (!(user.isShared())) {
				bs.getProfileModule().setUserProperty(user.getId(), WebKeys.CALENDAR_CURRENT_GRID, grids);
			}
			
			// ...and return a CalendarDisplayDataRpcData with the
			// ...changes.
			CalendarDisplayDataRpcResponseData reply = getCalendarDisplayData(bs, request, browserTZOffset, folderInfo);
			if (null != date) {
				setCalendarDisplayDataDates(reply, date, date);
			}
			
			cacheCalendarStartDay(request, reply.getStartDay(), folderInfo.getBinderIdAsLong());
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtCalendarHelper.saveCalendarDayView( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtLogHelper.getGwtClientException(e);
		}
	}
	
	/**
	 * Saves a user's CalendarHours selection and returns the
	 * appropriate CalendarDisplayDataRpcResponseData based on the
	 * change.
	 * 
	 * @param bs
	 * @param request
	 * @param browserTZOffset
	 * @param folderInfo
	 * @param hours
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static CalendarDisplayDataRpcResponseData saveCalendarHours(AllModulesInjected bs, HttpServletRequest request, long browserTZOffset, BinderInfo folderInfo, CalendarHours hours) throws GwtTeamingException {
		try {
			// Store the new hours...
			String hoursType;
			switch (hours) {
			default:
			case WORK_DAY:  hoursType = EventsViewHelper.DAY_VIEW_TYPE_WORK; break;
			case FULL_DAY:  hoursType = EventsViewHelper.DAY_VIEW_TYPE_FULL; break;
			}
			EventsViewHelper.setCalendarDayViewType(request, hoursType);
			
			// ...and return a CalendarDisplayDataRpcData with the
			// ...changes.
			CalendarDisplayDataRpcResponseData reply = getCalendarDisplayData(bs, request, browserTZOffset, folderInfo);
			cacheCalendarStartDay(request, reply.getStartDay(), folderInfo.getBinderIdAsLong());
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtCalendarHelper.saveCalendarHours( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtLogHelper.getGwtClientException(e);
		}
	}

	/**
	 * Saves a user's calendar setting selections.
	 * 
	 * @param bs
	 * @param request
	 * @param folderId
	 * @param weekStart
	 * @param workDayStart
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean saveCalendarSettings(AllModulesInjected bs, HttpServletRequest request, Long folderId, int weekStart, int workDayStart) throws GwtTeamingException {
		try {
			// If we have a week starting day to save...
			Long userId = GwtServerHelper.getCurrentUserId();
			ProfileModule pm = bs.getProfileModule();
			if ((-1) != weekStart) {
				// ...save it.
				pm.setUserProperty(userId, ObjectKeys.USER_PROPERTY_CALENDAR_FIRST_DAY_OF_WEEK, new Integer(weekStart));
			}

			// If we have a work day start hour to save...
			if ((-1) != workDayStart) {
				// ...save it.
				pm.setUserProperty(userId, ObjectKeys.USER_PROPERTY_CALENDAR_WORK_DAY_START, new Integer(workDayStart));
			}

			// We always return true.
			return Boolean.TRUE;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtCalendarHelper.saveCalendarSettings( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtLogHelper.getGwtClientException(e);
		}
	}
	
	/**
	 * Saves a user's CalendarShow selection and returns the
	 * appropriate CalendarDisplayDataRpcResponseData based on the
	 * change.
	 * 
	 * @param bs
	 * @param request
	 * @param browserTZOffset
	 * @param folderInfo
	 * @param show
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static CalendarDisplayDataRpcResponseData saveCalendarShow(AllModulesInjected bs, HttpServletRequest request, long browserTZOffset, BinderInfo folderInfo, CalendarShow show) throws GwtTeamingException {
		try {
			// Map the show setting to the appropriate mode and event
			// types...
			ModeType mode;
			String eventType;
			switch (show) {
			default:
			case PHYSICAL_EVENTS:       mode = ModeType.PHYSICAL; eventType = EventsViewHelper.EVENT_TYPE_EVENT;    break;
			case PHYSICAL_BY_ACTIVITY:  mode = ModeType.PHYSICAL; eventType = EventsViewHelper.EVENT_TYPE_ACTIVITY; break;
			case PHYSICAL_BY_CREATION:  mode = ModeType.PHYSICAL; eventType = EventsViewHelper.EVENT_TYPE_CREATION; break;
			case VIRTUAL:               mode = ModeType.VIRTUAL;  eventType = EventsViewHelper.EVENT_TYPE_VIRTUAL;  break;
			}
			
			// ...and store them.
			Long userId   = GwtServerHelper.getCurrentUserId();
			Long folderId = folderInfo.getBinderIdAsLong();
			ListFolderHelper.setFolderModeType(          bs, userId, folderId, mode     );
			EventsViewHelper.setCalendarDisplayEventType(bs, userId, folderId, eventType);			
			
			// Return a CalendarDisplayDataRpcData with the changes.
			CalendarDisplayDataRpcResponseData reply = getCalendarDisplayData(bs, request, browserTZOffset, folderInfo);
			cacheCalendarStartDay(request, reply.getStartDay(), folderId);
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtCalendarHelper.saveCalendarShow( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtLogHelper.getGwtClientException(e);
		}
	}

	/**
	 * Sets the display dates in a CalendarDisplayDataRpcData object.
	 * 
	 * @param cdd
	 * @param firstDate
	 * @param startDay
	 */
	public static void setCalendarDisplayDataDates(CalendarDisplayDataRpcResponseData cdd, Date firstDate, Date startDate) {
		GregorianCalendar firstDay = new GregorianCalendar();
		firstDay.setTime(firstDate);
		
		GregorianCalendar startDay = new GregorianCalendar();
		startDay.setTime(startDate);

		// ...and modify the CalendarDisplayDataRpcResponseData
		// ...with these values.
		cdd.setFirstDay(firstDay.getTime());
		cdd.setStartDay(startDay.getTime());
		cdd.setDisplayDate(
			buildCalendarDateDisplay(
				GwtServerHelper.getCurrentUser(),
				firstDay,
				startDay,
				cdd.getDayView(),
				cdd.getWeekFirstDay()));
	}
	
	/**
	 * Stores a collection of attendees to individuals in an
	 * appointment.
	 * 
	 * @param appointment
	 * @param vibeAttendees
	 */
	public static void setVibeAttendees(CalendarAppointment appointment, List<AssignmentInfo> vibeAttendees) {
		// Store the Vibe attendees.
		appointment.setVibeAttendees(vibeAttendees);
	}
	
	/**
	 * Stores a collection of attendees to groups in an appointment.
	 * 
	 * @param appointment
	 * @param vibeAttendeeGroups
	 */
	public static void setVibeAttendeeGroups(CalendarAppointment appointment, List<AssignmentInfo> vibeAttendeeGroups) {
		// Store the Vibe attendees.
		appointment.setVibeAttendeeGroups(vibeAttendeeGroups);
	}
	
	/**
	 * Stores a collection of attendees to teams in an appointment.
	 * 
	 * @param appointment
	 * @param vibeAttendeeTeams
	 */
	public static void setVibeAttendeeTeams(CalendarAppointment appointment, List<AssignmentInfo> vibeAttendeeTeams) {
		// Store the Vibe attendees.
		appointment.setVibeAttendeeTeams(vibeAttendeeTeams);
	}
	
	/**
	 * Updates an event's dates based on the appointment being dragged
	 * and dropped in the calendar folder..
	 * 
	 * @param bs
	 * @param request
	 * @param browserTZOffset
	 * @param folderIdFromView
	 * @param event
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static Boolean updateCalendarEvent(AllModulesInjected bs, HttpServletRequest request, long browserTZOffset, Long folderIdFromView, CalendarAppointment event) throws GwtTeamingException {
		try {
			// Read the FolderEntry...
			Long folderId = event.getFolderId();
			Long entryId  = event.getEntryId();
			FolderModule fm = bs.getFolderModule();
			FolderEntry  fe = fm.getEntry(folderId, entryId);
			
			// ...and the Event from it.
			String attr = (event.isTask() ? TaskHelper.TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME : "event");
			CustomAttribute customAttribute = fe.getCustomAttribute(attr);
			Event dbEvent = ((Event) customAttribute.getValue());
			if (null == dbEvent) {
				dbEvent = new Event();
			}

			// Store the new start...
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(event.getStart());
			dbEvent.setDtStart(gc);

			// ...and end dates.
			gc = new GregorianCalendar();
			gc.setTime(event.getEnd());
			dbEvent.setDtEnd(gc);

			// Has the user already seen this event?
			ProfileModule pm = bs.getProfileModule();
			boolean eventSeen = pm.getUserSeenMap(null).checkIfSeen(fe);
			
			Map formData = new HashMap(); 
			formData.put(attr, dbEvent);
			fm.modifyEntry(folderId, entryId, new MapInputData(formData), null, null, null, null);
			
			// If the user had seen the event before the update...
			if (eventSeen) {
				// ...maintain its seen state.
				pm.setSeen(null, fe);
			}

			// We always return true.  Failure conditions will throw an
			// exception.
			return Boolean.TRUE;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtLogHelper.isDebugEnabled())) && GwtLogHelper.isDebugEnabled(m_logger)) {
			     GwtLogHelper.debug(m_logger, "GwtCalendarHelper.updateCalendarEvent( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtLogHelper.getGwtClientException(e);
		}
	}
}
