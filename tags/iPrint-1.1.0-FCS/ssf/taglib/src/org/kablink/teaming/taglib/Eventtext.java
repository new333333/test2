/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
/*
 * Created on Jul 29, 2005
 *
 */
package org.kablink.teaming.taglib;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.kablink.teaming.calendar.EventsViewHelper;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.User;
import org.kablink.util.cal.Duration;
import org.kablink.util.servlet.DynamicServletRequest;
import org.kablink.util.servlet.StringServletResponse;


/**
 * @author billmers ;
 */

@SuppressWarnings("serial")
public class Eventtext extends TagSupport {

	private String contextPath;

	private Event event = null;

	public int doStartTag() throws JspException {

		try {
			if (event == null) {
				throw new JspException("You must provide an event");
			}


			ServletContext ctx = null;
			if (ctx == null) {
				ctx = pageContext.getServletContext();
			}

			ServletRequest req = null;
			req = new DynamicServletRequest((HttpServletRequest) pageContext
					.getRequest());

			String jsp = "/WEB-INF/jsp/tag_jsps/eventtext/eventtext.jsp";
			RequestDispatcher rd = ctx.getRequestDispatcher(jsp);

			User user = RequestContextHolder.getRequestContext().getUser();

			Calendar startDate = event.getDtStart();
			Calendar endDate = event.getDtEnd();

			DateFormat dateFormat = null;
			
			if (event.isAllDayEvent()) {
				dateFormat = DateFormat.getDateInstance(DateFormat.LONG, user
						.getLocale());
				dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			} else if (!event.isTimeZoneSensitive()) {
				dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
						DateFormat.SHORT, user.getLocale());
				dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			} else {
				dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
						DateFormat.SHORT, user.getLocale());
				dateFormat.setTimeZone(user.getTimeZone());
			}


			String startString = "";
			if (startDate != null) startString = dateFormat.format(startDate.getTime());
			String endString = "";
			if (endDate != null) endString = dateFormat.format(endDate.getTime());

			String repeatString = EventsViewHelper.eventToRepeatHumanReadableString(event, user.getLocale());

			Duration eventDur = event.getDuration();
			req.setAttribute("startString", startString);
			req.setAttribute("endString", endString);
			req.setAttribute("repeatString", repeatString);
			req.setAttribute("allDayEvent", event.isAllDayEvent());
			req.setAttribute("hasDuration", eventDur.getInterval() != 0);
			long msDuration = eventDur.getInterval();
			Integer durationDays = Integer.valueOf((int)((msDuration + 24l*60l*60l*1000l/2l)/(24l*60l*60l*1000l)));
			req.setAttribute("durationDays", durationDays);
			req.setAttribute("freeBusy", event.getFreeBusy().name());

			// Is this a non-all day event that doesn't have both a
			// start/end date?
			int days = 0;
			boolean hasStartAndEnd = ((null != event.getDtStart()) && (null != event.getDtEnd()));			
			if ((!(event.isAllDayEvent())) && (!hasStartAndEnd)) {
				// Yes!  Does it only have a day value in the duration?
				if ((0 == eventDur.getWeeks())       &&
						(0 == eventDur.getHours())   &&
						(0 == eventDur.getMinutes()) &&
						(0 == eventDur.getSeconds()) &&
						(0 <  eventDur.getDays())) {
					// Yes!  Pass it through.
					days = eventDur.getDays();
				}
			}
			req.setAttribute("durationDaysOnly", new Integer(days));
			
			StringServletResponse res = new StringServletResponse(
					(HttpServletResponse) pageContext.getResponse());
			// this next line invokes the jsp and captures it into res
			rd.include(req, res);
			// and now dump it out into this response
			pageContext.getOut().print(res.getString());
		}

		catch (Exception e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}



	public int doEndTag() throws JspException {
		return SKIP_BODY;
	}

	public void setEvent(Event ev) {
		this.event = ev;
	}

}
