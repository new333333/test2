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
package com.sitescape.team.ical.servlet;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.ical.IcalModule;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.util.XmlFileUtil;
import com.sitescape.team.web.servlet.SAbstractController;

/**
 * Outputs iCalendar for given entry. The output contains all entry events or empty calendar (only iCalendar header) 
 * if entry doesn't have any events. 
 * 
 * @author Pawel Nowicki
 */
public class GetController extends SAbstractController {
	
	private MailModule mailModule;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Long binderId = new Long(RequestUtils.getRequiredStringParameter(request, "bi"));
		if (binderId == null) {
			return null;
		}
		
		response.resetBuffer();
		response.setContentType("text/calendar; charset=" + XmlFileUtil.FILE_ENCODING);
		response.setHeader("Cache-Control", "private");
		response.setHeader("Pragma", "no-cache");
		
		Long entryId = RequestUtils.getLongParameter(request, "entry");
		if (entryId != null) {
			FolderEntry entry  = getFolderModule().getEntry(binderId, entryId);
			if (getFolderModule().testAccess(entry, "getEntry")) {				
				CalendarOutputter calendarOutputter = new CalendarOutputter();
				Calendar calendar = getIcalModule().generate(entry, entry.getEvents(), mailModule.getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.DEFAULT_TIMEZONE));
				calendarOutputter.output(calendar, response.getWriter());
			}
		} else {
//			Map entries = getFolderModule().getFullEntries(binderId);
//			List folderEntries = (List)entries.get(ObjectKeys.FULL_ENTRIES);
//			System.out.println(folderEntries);
		}
		
		response.flushBuffer();
		return null;
	}
	
	public MailModule getMailModule() {
		return mailModule;
	}
	public void setMailModule(MailModule mailModule) {
		this.mailModule = mailModule;
	}
}
