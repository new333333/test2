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
/*
 * Created on Jun 10, 2005
 *
 */
package org.kablink.teaming.taglib;

import java.util.Calendar;
import java.util.Date;
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
import org.kablink.util.servlet.DynamicServletRequest;
import org.kablink.util.servlet.StringServletResponse;


/**
 * @author billmers ;
 */

public class Eventeditor extends TagSupport {

	private String contextPath;
	private String id;
	private String formName;
	private Event initEvent = null;
	private Boolean hasDuration = false;
	private Boolean hasRecurrence = true;
	private Boolean isTimeZoneSensitiveActive = false;
	private Boolean isFreeBusyActive = false;
	private Boolean required = false;
	private Boolean mobile = false;

	public int doStartTag() throws JspException {
		try {
			if (id == null) {
				throw new JspException("You must provide an element name");
			}
			if (formName == null) {
				throw new JspException("You must provide a form name");
			}

			HttpServletRequest httpReq = (HttpServletRequest) pageContext
					.getRequest();
			contextPath = httpReq.getContextPath();
			if (contextPath.endsWith("/"))
				contextPath = contextPath
						.substring(0, contextPath.length() - 1);

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
				Calendar startCal = initEvent.getDtStart();
				Calendar endCal = initEvent.getDtEnd();
				// if the start or end dates were never initialized, set to
				// today
				if (startCal.getTime().getTime() == 0) {
					startCal.setTime(startDate);
				}
				if (endCal.getTime().getTime() == 0) {
					endCal.setTime(endDate);
				}

				startDate = startCal.getTime();
				endDate = endCal.getTime();
			} else if (required) {
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
			attMap.put("hasRecur", hasRecurrence);
			attMap.put("isTimeZoneSensitiveActive", isTimeZoneSensitiveActive);
			attMap.put("isFreeBusyActive", isFreeBusyActive);
			req.setAttribute("attMap", attMap);

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

	public void setHasDuration(Boolean hasDuration) {
		this.hasDuration = hasDuration;
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
