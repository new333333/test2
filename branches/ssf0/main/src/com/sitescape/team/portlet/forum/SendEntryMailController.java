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
package com.sitescape.team.portlet.forum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.module.workflow.WorkflowUtils;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.StringUtil;

/**
 * @author Peter Hurley
 *
 */
public class SendEntryMailController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		
		//See if the form was submitted
		if (formData.containsKey("okBtn")) {
			String subject = PortletRequestUtils.getStringParameter(request, "subject", "");	
			String[] to = StringUtil.split(PortletRequestUtils.getStringParameter(request, "addresses", ""));
			Set emailAddress = new HashSet();
			for (int i=0; i<to.length; ++i) {
				emailAddress.add(to[i]);				
			}
			boolean self = PortletRequestUtils.getBooleanParameter(request, "self", false);
			String body = PortletRequestUtils.getStringParameter(request, "mailBody", "");
			Set memberIds = new HashSet();
			if (self) memberIds.add(RequestContextHolder.getRequestContext().getUserId());
			if (formData.containsKey("users")) memberIds.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("users")));
			if (formData.containsKey("groups")) memberIds.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("groups")));
			
			boolean sendAttachments = PortletRequestUtils.getBooleanParameter(request, "attachments", false);
			FolderEntry entry  = getFolderModule().getEntry(folderId, entryId);
			
			Map status = getAdminModule().sendMail(entry, memberIds, emailAddress, subject, 
					new Description(body, Description.FORMAT_HTML), sendAttachments);
			
			String result = (String)status.get(ObjectKeys.SENDMAIL_STATUS);
			List errors = (List)status.get(ObjectKeys.SENDMAIL_ERRORS);
			List addrs = (List)status.get(ObjectKeys.SENDMAIL_DISTRIBUTION);
			if (ObjectKeys.SENDMAIL_STATUS_SENT.equals(result)) {
				errors.add(0, NLT.get("sendMail.mailSent"));
				response.setRenderParameter(WebKeys.EMAIL_ADDRESSES, (String[])addrs.toArray( new String[0]));
			} else if (ObjectKeys.SENDMAIL_STATUS_SCHEDULED.equals(result)) {
				errors.add(0, NLT.get("sendMail.mailQueued"));
				response.setRenderParameter(WebKeys.EMAIL_ADDRESSES, (String[])addrs.toArray( new String[0]));
			} else {
				errors.add(0, NLT.get("sendMail.mailFailed"));
			}
			response.setRenderParameter(WebKeys.ERROR_LIST, (String[])errors.toArray( new String[0]));
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CLOSE_WINDOW);			
		} else {
			response.setRenderParameters(formData);
		}
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String [] errors = request.getParameterValues(WebKeys.ERROR_LIST);
		if (errors != null) {
			model.put(WebKeys.ERROR_LIST, errors);
			model.put(WebKeys.EMAIL_ADDRESSES, request.getParameterValues(WebKeys.EMAIL_ADDRESSES));
			return new ModelAndView(WebKeys.VIEW_BINDER_SENDMAIL, model);
		}
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));	
		FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
		Binder folder = entry.getParentFolder();
		model.put(WebKeys.ENTRY, entry);
		model.put(WebKeys.BINDER, folder);

		List userIds = PortletRequestUtils.getLongListParameters(request, WebKeys.USER_IDS_TO_ADD);
		model.put(WebKeys.USERS, getProfileModule().getUsers(new HashSet(userIds)));
		
		return new ModelAndView(WebKeys.VIEW_BINDER_SENDMAIL, model);
	}

}
