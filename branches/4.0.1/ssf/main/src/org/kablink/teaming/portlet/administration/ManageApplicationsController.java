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
package org.kablink.teaming.portlet.administration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ObjectExistsException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.MultipartFileSupport;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.springframework.web.portlet.ModelAndView;

public class ManageApplicationsController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		if (formData.containsKey("addBtn") && WebHelper.isMethodPost(request)) {
			
			String name = PortletRequestUtils.getStringParameter(request, "name", "").trim();
			
			try{
				
				if(name.equals(""))
					throw new IllegalArgumentException("Application must have a non-whitespace name");
				//make sure it is present
				MapInputData inputData = new MapInputData(formData);
				Map fileMap=null;
				if (request instanceof MultipartFileSupport) {
					fileMap = ((MultipartFileSupport) request).getFileMap();
				} else {
					fileMap = new HashMap();
				}
				getProfileModule().addApplication(null, inputData, fileMap, null);
			} catch (ObjectExistsException oee) {
				response.setRenderParameter(WebKeys.EXCEPTION, oee.getLocalizedMessage() + ": [" + name + "].");
			}
			  catch (IllegalArgumentException iae) {
				response.setRenderParameter(WebKeys.EXCEPTION, iae.getLocalizedMessage());
			}
			
		} else if ((formData.containsKey("applyBtn") || formData.containsKey("okBtn")) && WebHelper.isMethodPost(request)) {
			Long applicationId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);
			String title = PortletRequestUtils.getStringParameter(request, "title", "", false);
			String description = PortletRequestUtils.getStringParameter(request, "description", "", false);
			String postUrl = PortletRequestUtils.getStringParameter(request, "postUrl", "");
			String timeout = PortletRequestUtils.getStringParameter(request, "timeout", SPropsUtil.getString("remoteapp.timeout"));
			String trusted = PortletRequestUtils.getStringParameter(request, "trusted", "");
			String maxIdleTime = PortletRequestUtils.getStringParameter(request, "maxIdleTime", SPropsUtil.getString("remoteapp.maxIdleTime"));
			String sameAddrPolicy = PortletRequestUtils.getStringParameter(request, "sameAddrPolicy", "");
			Map updates = new HashMap();
			updates.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
			updates.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, description);
			updates.put(ObjectKeys.FIELD_APPLICATION_POST_URL, postUrl);
			updates.put(ObjectKeys.FIELD_APPLICATION_TIMEOUT, timeout);
			updates.put(ObjectKeys.FIELD_APPLICATION_TRUSTED, trusted);
			updates.put(ObjectKeys.FIELD_APPLICATION_MAX_IDLE_TIME, maxIdleTime);
			updates.put(ObjectKeys.FIELD_APPLICATION_SAME_ADDR_POLICY, sameAddrPolicy);
			getProfileModule().modifyEntry(applicationId, new MapInputData(updates));
			response.setRenderParameter(WebKeys.URL_ENTRY_ID, applicationId.toString());

		} else if (formData.containsKey("deleteBtn") && WebHelper.isMethodPost(request)) {
			Long applicationId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);
			getProfileModule().deleteEntry(applicationId, null);
			
		} else {
			response.setRenderParameters(formData);
		}
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
			
		Binder binder = getProfileModule().getProfileBinder();
		List applications = BinderHelper.getAllApplications(this);
		
		Map model = new HashMap();
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.APPLICATION_LIST, applications);
		
		Long applicationId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		if (applicationId != null) {
			String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
			model.put(WebKeys.NAMESPACE, namespace);
			model.put(WebKeys.BINDER_ID, binder.getId());
			Application application = (Application)getProfileModule().getEntry(applicationId);		
			model.put(WebKeys.APPLICATION, application);			
		}
		
		model.put(WebKeys.EXCEPTION, request.getParameter(WebKeys.EXCEPTION));

		return new ModelAndView(WebKeys.VIEW_ADMIN_MANAGE_APPLICATIONS, model);
	}

}
