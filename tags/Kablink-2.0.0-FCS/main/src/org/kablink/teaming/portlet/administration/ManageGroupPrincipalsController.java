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
import org.kablink.teaming.ObjectExistsException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.MultipartFileSupport;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.springframework.web.portlet.ModelAndView;

public abstract class ManageGroupPrincipalsController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		if (formData.containsKey("addBtn") && WebHelper.isMethodPost(request)) {
			
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
			
		} else if ((formData.containsKey("applyBtn") || formData.containsKey("okBtn")) && WebHelper.isMethodPost(request)) {
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
			getProfileModule().modifyEntry( groupId, new MapInputData(updates));
			response.setRenderParameter(WebKeys.URL_ENTRY_ID, groupId.toString());

		} else if (formData.containsKey("deleteBtn") && WebHelper.isMethodPost(request)) {
			Long groupId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID);
			getProfileModule().deleteEntry(groupId, null);
			
		} else {
			response.setRenderParameters(formData);
		}
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
			
		Binder binder = getProfileModule().getProfileBinder();
		
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);
		//get them all
		options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE-1);
		//Exclude allUsers from the search 
		Document searchFilter = DocumentHelper.createDocument();
		Element rootElement = searchFilter.addElement(Constants.NOT_ELEMENT);
		Element field = rootElement.addElement(Constants.FIELD_ELEMENT);
    	field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,Constants.GROUPNAME_FIELD);
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
			GroupPrincipal group = (GroupPrincipal)getProfileModule().getEntry(groupId);		
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
	throws AccessControlException, WriteFilesException, WriteEntryDataException;
	
	abstract protected String allIndividualsGroupName();

	abstract protected Map getGroupPrincipals(Long binderId, Map options);

	abstract String getViewName();
}
