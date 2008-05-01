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
package com.sitescape.team.servlet.forum;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.util.XmlFileUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.servlet.SAbstractController;

public class ViewICalController extends SAbstractController {
	
	private MailModule mailModule;
	
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest request,
            HttpServletResponse response) throws Exception {		

		Long binderId = new Long(RequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
		Long entryId = new Long(RequestUtils.getRequiredStringParameter(request, WebKeys.URL_ENTRY_ID));
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
		response.setHeader("Pragma", "no-cache");
		
		CalendarOutputter calendarOutputter = new CalendarOutputter();
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
