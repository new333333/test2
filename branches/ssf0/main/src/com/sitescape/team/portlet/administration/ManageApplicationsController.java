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
package com.sitescape.team.portlet.administration;

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
import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.ObjectExistsException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Application;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;
public class ManageApplicationsController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		if (formData.containsKey("addBtn")) {
			
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
				getProfileModule().addApplication(binderId, null, inputData, fileMap, null);
			} catch (ObjectExistsException oee) {
				response.setRenderParameter(WebKeys.EXCEPTION, oee.getLocalizedMessage() + ": [" + name + "].");
			}
			  catch (IllegalArgumentException iae) {
				response.setRenderParameter(WebKeys.EXCEPTION, iae.getLocalizedMessage());
			}
			
		} else if (formData.containsKey("applyBtn") || formData.containsKey("okBtn")) {
			Long applicationId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);
			String title = PortletRequestUtils.getStringParameter(request, "title", "");
			String description = PortletRequestUtils.getStringParameter(request, "description", "");
			String postUrl = PortletRequestUtils.getStringParameter(request, "postUrl", "");
			Map updates = new HashMap();
			updates.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
			updates.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, description);
			updates.put(ObjectKeys.FIELD_APPLICATION_POST_URL, postUrl);
			getProfileModule().modifyEntry(binderId, applicationId, new MapInputData(updates));
			response.setRenderParameter(WebKeys.URL_ENTRY_ID, applicationId.toString());

		} else if (formData.containsKey("deleteBtn")) {
			Long applicationId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);
			getProfileModule().deleteEntry(binderId, applicationId, null);
			
		} else if (formData.containsKey("closeBtn") || formData.containsKey("cancelBtn")) {
			response.setRenderParameter("redirect", "true");
		} else {
			response.setRenderParameters(formData);
		}
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
			
		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		Binder binder = getProfileModule().getProfileBinder();
		
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_SORT_BY, EntityIndexUtils.SORT_TITLE_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);
		//get them all
		options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE-1);

		Document searchFilter = DocumentHelper.createDocument();
		Element field = searchFilter.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.ENTRY_TYPE_FIELD);
    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(EntityIndexUtils.ENTRY_TYPE_APPLICATION);
    	options.put(ObjectKeys.SEARCH_FILTER_AND, searchFilter);
    	
		Map searchResults = getProfileModule().getApplications(binder.getId(), options);
		List applications = (List) searchResults.get(ObjectKeys.SEARCH_ENTRIES);
		Map model = new HashMap();
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.APPLICATION_LIST, applications);
		
		Long applicationId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		if (applicationId != null) {
			String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
			model.put(WebKeys.NAMESPACE, namespace);
			model.put(WebKeys.BINDER_ID, binder.getId());
			Application application = (Application)getProfileModule().getEntry(binder.getId(), applicationId);		
			model.put(WebKeys.APPLICATION, application);			
		}
		
		model.put(WebKeys.EXCEPTION, request.getParameter(WebKeys.EXCEPTION));

		return new ModelAndView(WebKeys.VIEW_ADMIN_MANAGE_APPLICATIONS, model);
	}

}
