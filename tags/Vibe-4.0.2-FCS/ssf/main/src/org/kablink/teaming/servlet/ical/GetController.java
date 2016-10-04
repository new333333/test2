/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.servlet.ical;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.ical.util.ICalUtils;
import org.kablink.teaming.module.mail.MailModule;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.XmlFileUtil;
import org.kablink.teaming.web.servlet.SAbstractController;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 * Outputs iCalendar for given entry. The output contains all entry events or empty calendar (only iCalendar header) 
 * if entry doesn't have any events. 
 * 
 * @author Pawel Nowicki
 */
public class GetController extends SAbstractController {
	private MailModule mailModule;

	/**
	 * ?
	 * 
	 * @param request
	 * @param response
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long binderId = new Long(ServletRequestUtils.getRequiredStringParameter(request, "bi"));
		
		response.resetBuffer();
		response.setContentType(MailModule.CONTENT_TYPE_CALENDAR + MailModule.CONTENT_TYPE_CHARSET_SUFFIX + XmlFileUtil.FILE_ENCODING);
		boolean isHttps = request.getScheme().equalsIgnoreCase("https");
		String cacheControl = "private";
		if (isHttps) {
			response.setHeader("Pragma", "public");
			cacheControl += ", proxy-revalidate, s-maxage=0";
		}
		response.setHeader("Cache-Control", cacheControl);
		
		Long entryId = ServletRequestUtils.getLongParameter(request, "entry");
		if (entryId != null) {
			FolderEntry entry  = getFolderModule().getEntry(binderId, entryId);
			CalendarOutputter calendarOutputter = ICalUtils.getCalendarOutputter();
			Calendar calendar = getIcalModule().generate(entry, entry.getEvents(), mailModule.getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.DEFAULT_TIMEZONE));
			calendarOutputter.output(calendar, response.getWriter());
		} else {
			Folder folder = getFolderModule().getFolder(binderId);
			Map options = new HashMap();
			options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(SPropsUtil.getInt("ical.export.max", 250)));
			Map entries = getFolderModule().getFullEntries(binderId, options);
			List folderEntries = (List)entries.get(ObjectKeys.FULL_ENTRIES);
			
			CalendarOutputter calendarOutputter = ICalUtils.getCalendarOutputter();
			Calendar calendar = getIcalModule().generate(folder.getTitle(), folderEntries, mailModule.getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.DEFAULT_TIMEZONE));
			calendarOutputter.output(calendar, response.getWriter());
		}
		
		response.flushBuffer();
		return null;
	}

	/**
	 * ?
	 * 
	 * @return
	 */
	public MailModule getMailModule() {
		return mailModule;
	}
	
	/**
	 * ?
	 * 
	 * @param mailModule
	 */
	public void setMailModule(MailModule mailModule) {
		this.mailModule = mailModule;
	}
}
