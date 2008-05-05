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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectExistsException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.GroupPrincipal;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.profile.index.ProfileIndexUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;
import com.sitescape.util.search.Constants;
public abstract class ManageGroupPrincipalsController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		if (formData.containsKey("addBtn")) {
			
			String name = PortletRequestUtils.getStringParameter(request, "name", "").trim();
			
			try{
				
				if(name.equals(""))
					throw new IllegalArgumentException("Group must have a non-whitespace name");
				//make sure it is present
				MapInputData inputData = new MapInputData(formData);
				Map fileMap=null;
				if (request instanceof MultipartFileSupport) {
					fileMap = ((MultipartFileSupport) request).getFileMap();
				} else {
					fileMap = new HashMap();
				}
				addGroupPrincipal(binderId, inputData, fileMap);
			} catch (ObjectExistsException oee) {
				response.setRenderParameter(WebKeys.EXCEPTION, oee.getLocalizedMessage() + ": [" + name + "].");
			}
			  catch (IllegalArgumentException iae) {
				response.setRenderParameter(WebKeys.EXCEPTION, iae.getLocalizedMessage());
			}
			
		} else if (formData.containsKey("applyBtn") || formData.containsKey("okBtn")) {
			Long groupId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);
			String title = PortletRequestUtils.getStringParameter(request, "title", "");
			String description = PortletRequestUtils.getStringParameter(request, "description", "");
			Set ids = LongIdUtil.getIdsAsLongSet(request.getParameterValues("users"));
			ids.addAll(LongIdUtil.getIdsAsLongSet(request.getParameterValues("groups")));
			SortedSet principals = getProfileModule().getPrincipals(ids);
			Map updates = new HashMap();
			updates.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
			updates.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, description);
			updates.put(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, principals);
			getProfileModule().modifyEntry(binderId, groupId, new MapInputData(updates));
			response.setRenderParameter(WebKeys.URL_ENTRY_ID, groupId.toString());

		} else if (formData.containsKey("deleteBtn")) {
			Long groupId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);
			getProfileModule().deleteEntry(binderId, groupId, null);
			
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
		//Exclude allUsers from the search 
		Document searchFilter = DocumentHelper.createDocument();
		Element rootElement = searchFilter.addElement(Constants.NOT_ELEMENT);
		Element field = rootElement.addElement(Constants.FIELD_ELEMENT);
    	field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,ProfileIndexUtils.GROUPNAME_FIELD);
    	Element child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
    	child.setText(allIndividualsGroupName());
    	options.put(ObjectKeys.SEARCH_FILTER_AND, searchFilter);

		Map searchResults = getGroupPrincipals(binder.getId(), options);
		List groups = (List) searchResults.get(ObjectKeys.SEARCH_ENTRIES);
		//remove allUsers from list
		Map model = new HashMap();
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.GROUP_LIST, groups);
		
		Long groupId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);
		if (groupId != null) {
			String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
			model.put(WebKeys.NAMESPACE, namespace);
			model.put(WebKeys.BINDER_ID, binder.getId());
			GroupPrincipal group = (GroupPrincipal)getProfileModule().getEntry(binder.getId(), groupId);		
			model.put(WebKeys.GROUP, group);
			List memberList = group.getMembers();
			Set ids = new HashSet();
			Iterator itUsers = memberList.iterator();
			while (itUsers.hasNext()) {
				Principal member = (Principal) itUsers.next();
				ids.add(member.getId());
			}
			model.put(WebKeys.USERS, getProfileModule().getIndividualPrincipals(ids));
			model.put(WebKeys.GROUPS, getProfileModule().getGroupPrincipals(ids));
		}
		
		model.put(WebKeys.EXCEPTION, request.getParameter(WebKeys.EXCEPTION));

		return new ModelAndView(getViewName(), model);
	}

	abstract protected void addGroupPrincipal(Long binderId, MapInputData inputData, Map fileMap)
	throws AccessControlException, WriteFilesException;
	
	abstract protected String allIndividualsGroupName();

	abstract protected Map getGroupPrincipals(Long binderId, Map options);

	abstract String getViewName();
}
