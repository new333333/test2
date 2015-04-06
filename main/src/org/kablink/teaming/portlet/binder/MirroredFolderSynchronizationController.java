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
package org.kablink.teaming.portlet.binder;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.tree.MailTreeHelper;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.NetFolderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.ScheduleHelper;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;


public class MirroredFolderSynchronizationController extends  SAbstractController  {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		response.setRenderParameters(formData);		
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			ScheduleInfo config = NetFolderHelper.getMirroredFolderSynchronizationSchedule(binderId);			
			config.setSchedule(ScheduleHelper.getSchedule(request, "sync"));
			config.setEnabled(PortletRequestUtils.getBooleanParameter(request, "enabled", false));
			getFolderModule().setSynchronizationSchedule(config, binderId);			
    	} else if (formData.containsKey("closeBtn") || (formData.containsKey("cancelBtn"))) {
	    	response.setRenderParameter("redirect", "true");
    	} else
    		response.setRenderParameters(formData);	
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		HashMap model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		ScheduleInfo config = NetFolderHelper.getMirroredFolderSynchronizationSchedule(binderId);		
		
		model.put(WebKeys.SCHEDULE_INFO, config);
		model.put(WebKeys.EXCEPTION, request.getParameter(WebKeys.EXCEPTION));
		
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));
		Folder folder = getFolderModule().getFolder(folderId);
		model.put(WebKeys.BINDER, folder);
		model.put(WebKeys.DEFINITION_ENTRY, folder);
		
		//	Build the navigation beans
		BinderHelper.buildNavigationLinkBeans(this, folder, model, new MailTreeHelper());
		
		return new ModelAndView(WebKeys.VIEW_SCHEDULE_SYNCHRONIZATION, model);		
	}
}