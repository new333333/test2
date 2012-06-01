/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.calendar.EventsViewHelper;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.gwt.client.rpc.shared.CalendarDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.CalendarAppointment;
import org.kablink.teaming.gwt.client.util.CalendarAttendee;
import org.kablink.teaming.gwt.client.util.CalendarDayView;
import org.kablink.teaming.util.NLT;

import com.bradrydzewski.gwt.calendar.client.Appointment;
import com.bradrydzewski.gwt.calendar.client.Attendee;

/**
 * Helper methods for the GWT calendar handling.
 *
 * @author drfoster@novell.com
 */
public class GwtCalendarHelper {
	protected static Log m_logger = LogFactory.getLog(GwtCalendarHelper.class);

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtCalendarHelper() {
		// Nothing to do.
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
	 * Returns a last date given a first day and a number of days.
	 */
	private static Calendar getLastDayToShow(User user, Calendar firstDay, int days) {
		Calendar lastDay = new GregorianCalendar(user.getTimeZone(), user.getLocale());
		lastDay.setTime(firstDay.getTime());
		lastDay.set(Calendar.DAY_OF_YEAR, lastDay.get(Calendar.DAY_OF_YEAR) + (days - 1));
		return lastDay;
	}

	/**
	 * Sets the display dates in a CalendarDisplayDataRpcData object.
	 * 
	 * @param reply
	 * @param firstDate
	 * @param startDate
	 */
	public static void setCalendarDisplayDataDates(CalendarDisplayDataRpcResponseData reply, Date firstDate, Date startDate) {
		GregorianCalendar firstDay = new GregorianCalendar();
		firstDay.setTime(firstDate);
		
		GregorianCalendar startDay = new GregorianCalendar();
		startDay.setTime(startDate);

		// ...and modify the CalendarDisplayDataRpcResponseData
		// ...with these values.
		reply.setFirstDay(firstDay.getTime());
		reply.setStartDay(startDay.getTime());
		reply.setDisplayDate(
			buildCalendarDateDisplay(
				GwtServerHelper.getCurrentUser(),
				firstDay,
				startDay,
				reply.getDayView(),
				reply.getWeekFirstDay()));
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
}
