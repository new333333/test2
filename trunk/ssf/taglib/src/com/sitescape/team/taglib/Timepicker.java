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
 * Created on Jun 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.taglib;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.TimeZone;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

/*
 * Created on Apr 7, 2005
 * 
 * Stuff for the datepicker tag
 * 
 */

/**
 * @author billmers
 * 
 */

public class Timepicker extends TagSupport {

	private String contextPath;
	private String id;
	private String formName;
	private Date initDate;
	private String initTimeString;
	private String sequenceNumber;

	public int doStartTag() throws JspException {
		JspWriter jspOut = pageContext.getOut();

		try {
			if (id == null) {
				throw new JspException("You must provide an Id");
			}
			if (sequenceNumber == null) {
				sequenceNumber = "0";
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

			String jsp = "/WEB-INF/jsp/tag_jsps/timepicker/timepicker.jsp";
			String icon = contextPath + "/images/pics/sym_s_clock.gif";
			RequestDispatcher rd = req.getRequestDispatcher(jsp);

			User user = RequestContextHolder.getRequestContext().getUser();
			TimeZone tz = user.getTimeZone();

			GregorianCalendar cal = new GregorianCalendar(tz);
			Integer hour = new Integer(99); // 99 is used as the "no hour
											// selected" value
			Integer minute = new Integer(99); // 99 is used as the "no hour
												// selected" value
			if (initDate != null) {
				cal.setTime(initDate);
				hour = new Integer(cal.get(Calendar.HOUR_OF_DAY));
				int m = cal.get(Calendar.MINUTE);
				// we need to pass the minutes to the picker as a multiple of 5
				int mm = m % 5;
				m = m - mm;
				minute = new Integer(m);
			}
			

			// any attributes we might want to pass into the jsp go here
			req.setAttribute("tpid", id);
			req.setAttribute("formName", formName);
			req.setAttribute("sequenceNumber", sequenceNumber);
			req.setAttribute("hour", hour);
			req.setAttribute("minute", minute);
			req.setAttribute("icon", icon);

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
		finally {
			id = null;
			formName = null;
			initDate = null;
			initTimeString = null;
			sequenceNumber = null;
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

	public void setInitDate(Date initDate) {
		this.initDate = initDate;
	}

	public void setInitTimeString(String initTimeString) {
		this.initTimeString = initTimeString;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

}
