/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.servlet.forum;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.ical.util.ICalUtils;
import org.kablink.teaming.module.mail.MailModule;
import org.kablink.teaming.util.XmlFileUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.servlet.SAbstractController;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;


public class ViewICalController extends SAbstractController {
	
	private MailModule mailModule;
	
	@SuppressWarnings("unchecked")
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		Long binderId = new Long(ServletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
		Long entryId = new Long(ServletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_ENTRY_ID));
		Map folderEntries = getFolderModule().getEntryTree(binderId, entryId);
		FolderEntry entry = (FolderEntry)folderEntries.get(ObjectKeys.FOLDER_ENTRY);
		
		User user = (User)request.getUserPrincipal();
		
		if(user == null) {
			// The request object has no information about authenticated user.
			// Note: It means that this is not a request made by the portal
			// through cross-context dispatch targeted to a SSF portlet. 
			HttpSession ses = request.getSession(false);

			if(ses != null) {
				user = (User) ses.getAttribute(WebKeys.USER_PRINCIPAL);
				
				if (user == null) {
					// No principal object is cached in the session.
					// Note: This occurs when a SSF web component (either a servlet
					// or an adapted portlet) is accessed BEFORE at least one SSF
					// portlet is invoked  by the portal through regular cross-context
					// dispatch. 
					user = RequestContextHolder.getRequestContext().getUser();
				}
			}
			else {
				throw new ServletException("No session in place - Illegal request sequence.");
			}
		}
		

		response.resetBuffer();
		response.setContentType(MailModule.CONTENT_TYPE_CALENDAR + "; charset=" + XmlFileUtil.FILE_ENCODING);
		response.setHeader("Cache-Control", "private");
		
		CalendarOutputter calendarOutputter = ICalUtils.getCalendarOutputter();
		Calendar calendar = getIcalModule().generate(entry, entry.getEvents(), 
				mailModule.getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.DEFAULT_TIMEZONE));
		calendarOutputter.output(calendar, response.getWriter());
		
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
