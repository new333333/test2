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
 * Created on Jul 29, 2005
 *
 */
package com.sitescape.team.taglib;

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

import com.sitescape.team.calendar.EventsViewHelper;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.User;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

/**
 * @author billmers ;
 */

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
			HttpServletRequest req2 = (HttpServletRequest) pageContext
					.getRequest();
			contextPath = req2.getContextPath();
			if (contextPath.endsWith("/"))
				contextPath = contextPath
						.substring(0, contextPath.length() - 1);

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
						DateFormat.MEDIUM, user.getLocale());
				dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			} else {
				dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
						DateFormat.MEDIUM, user.getLocale());
				dateFormat.setTimeZone(user.getTimeZone());
			}


			String startString = dateFormat.format(startDate.getTime());
			String endString = dateFormat.format(endDate.getTime());

			String repeatString = EventsViewHelper.eventToRepeatHumanReadableString(event, user.getLocale());

			req.setAttribute("startString", startString);
			req.setAttribute("endString", endString);
			req.setAttribute("repeatString", repeatString);
			req.setAttribute("allDayEvent", event.isAllDayEvent());
			req.setAttribute("hasDuration", event.getDuration().getInterval() != 0);

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
