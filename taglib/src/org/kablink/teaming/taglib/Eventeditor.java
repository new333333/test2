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
 * Created on Jun 10, 2005
 *
 */
package org.kablink.teaming.taglib;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.joda.time.DateTime;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Event;
import org.kablink.util.cal.Duration;
import org.kablink.util.servlet.DynamicServletRequest;
import org.kablink.util.servlet.StringServletResponse;


/**
 * @author billmers ;
 */

@SuppressWarnings({"serial", "unchecked"})
public class Eventeditor extends TagSupport {

	private String contextPath;
	private String id;
	private String formName;
	private Event initEvent = null;
	private Boolean hasDuration = false;
	private Boolean hasDurationDays = false;
	private Boolean hasRecurrence = true;
	private Boolean isTimeZoneSensitiveActive = false;
	private Boolean isFreeBusyActive = false;
	private Boolean required = false;
	private Boolean leaveDateEmpty = false;
	private Boolean mobile = false;

	public int doStartTag() throws JspException {
		try {
			if (id == null) {
				throw new JspException("You must provide an element name");
			}
			if (formName == null) {
				throw new JspException("You must provide a form name");
			}

			ServletRequest req = null;
			req = new DynamicServletRequest((HttpServletRequest) pageContext
					.getRequest());

			String jsp = "/WEB-INF/jsp/tag_jsps/eventeditor/eventeditor.jsp";
			if (this.mobile) jsp = "/WEB-INF/jsp/tag_jsps/eventeditor/mobile_eventeditor.jsp";
			RequestDispatcher rd = req.getRequestDispatcher(jsp);

			// if initEvent is provided, take it apart and pass in two dates
			Date startDate = null;
			Date endDate = null;
			// initialize the event, if none was provided
			if (initEvent != null) {
				// if the start or end dates were never initialized, set to
				// today
				Calendar startCal = initEvent.getDtStart();
				if ((null == startCal) && required) {
					startDate = new Date();
				}
				else if (null != startCal) {
					if (0 == startCal.getTime().getTime()) {
						startCal.setTime(startDate);
					}
					startDate = startCal.getTime();
				}
				
				Calendar endCal = initEvent.getDtEnd();
				if ((null == endCal) && required) {
					endDate = new Date();
				}
				else if (null != endCal) {
					if (0 == endCal.getTime().getTime()) {
						endCal.setTime(endDate);
					}
					endDate = endCal.getTime();
				}
			} else if (required && !leaveDateEmpty) {
				initEvent = new Event();
				initEvent.setTimeZone(RequestContextHolder.getRequestContext()
						.getUser().getTimeZone());

				DateTime startDateTime = new DateTime();
				startDateTime = startDateTime.plusMinutes(startDateTime
						.getMinuteOfHour() > 30 ? 60 - startDateTime
						.getMinuteOfHour() : 30 - startDateTime
						.getMinuteOfHour());
				startDate = startDateTime.toDate();
				initEvent.setDtStart(startDateTime.toGregorianCalendar());
				if (hasDuration.booleanValue()) {
					startDateTime = startDateTime.plusMinutes(30);
					endDate = startDateTime.toDate();
					initEvent.setDtEnd(startDateTime.toGregorianCalendar());
				}
			}

			// any attributes we might want to pass into the jsp go here
			req.setAttribute("initEvent", initEvent);
			req.setAttribute("required",required);
			req.setAttribute("leaveDateEmpty",leaveDateEmpty);
			// these need to be beans because the jsp page will pass them on to
			// other tags
			req.setAttribute("evid", id);
			req.setAttribute("formName", formName);
			req.setAttribute("startDate", startDate);
			req.setAttribute("endDate", endDate);
			// any other miscellaneous pieces can go here, for access by JSTL on
			// the JSP page
			HashMap attMap = new HashMap();
			attMap.put("hasDur", hasDuration);
			attMap.put("hasDurDays", hasDurationDays);
			attMap.put("hasRecur", hasRecurrence);
			attMap.put("isTimeZoneSensitiveActive", isTimeZoneSensitiveActive);
			attMap.put("isFreeBusyActive", isFreeBusyActive);
			req.setAttribute("attMap", attMap);

			// Does this even support a duration days field?
			if (hasDurationDays) {
				// Yes!  Does the event contain a duration?
				int days = 0;
				Duration eventDur = ((null == initEvent) ? null : initEvent.getDuration());
				if (null != eventDur) {
					// Yes!  Is it a non-all day event that doesn't
					// have both a start/end date?
					boolean hasStartAndEnd = ((null != initEvent.getDtStart()) && (null != initEvent.getDtEnd()));
					if ((!(initEvent.isAllDayEvent())) && (!hasStartAndEnd)) {
						// Yes!  Does it only have a day value in the
						// duration?
						if ((0 == eventDur.getWeeks())       &&
								(0 == eventDur.getHours())   &&
								(0 == eventDur.getMinutes()) &&
								(0 == eventDur.getSeconds()) &&
								(0 <  eventDur.getDays())) {
							// Yes!  Pass it through.
							days = eventDur.getDays();
						}
					}
				}
				req.setAttribute("durationDays", new Integer(days));
			}

			StringServletResponse res = new StringServletResponse(
					(HttpServletResponse) pageContext.getResponse());
			// this next line invokes the jsp and captures it into res
			rd.include(req, res);
			// and now dump it out into this response
			pageContext.getOut().print(res.getString());
		}

		catch (Exception e) {
			throw new JspException(e);
		} finally {
			id = null;
			formName = null;
			initEvent = null;
			hasDuration = false;
			hasDurationDays = false;
			hasRecurrence = true;
			isTimeZoneSensitiveActive = false;
			isFreeBusyActive = false;
			required = false;
			mobile = false;
		}
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		return SKIP_BODY;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public void setLeaveDateEmpty(Boolean leaveDateEmpty) {
		this.leaveDateEmpty = leaveDateEmpty;
	}

	public void setHasDuration(Boolean hasDuration) {
		this.hasDuration = hasDuration;
	}

	public void setHasDurationDays(Boolean hasDurationDays) {
		this.hasDurationDays = hasDurationDays;
	}

	public void setHasRecurrence(Boolean hasRecurrence) {
		this.hasRecurrence = hasRecurrence;
	}

	public void setIsTimeZoneSensitiveActive(Boolean isTimeZoneSensitiveActive) {
		this.isTimeZoneSensitiveActive = isTimeZoneSensitiveActive;
	}
	
	public void setIsFreeBusyActive(Boolean isFreeBusyActive) {
		this.isFreeBusyActive = isFreeBusyActive;
	}

	public void setInitEvent(Event initEvent) {
		this.initEvent = initEvent;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public void setMobile(Boolean mobile) {
		this.mobile = mobile;
	}

}
