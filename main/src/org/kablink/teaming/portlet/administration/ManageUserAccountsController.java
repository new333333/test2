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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.search.Constants;
import org.springframework.web.portlet.ModelAndView;




public class ManageUserAccountsController extends SAbstractController {

	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		Map formData = request.getParameterMap();
		if ((formData.containsKey("okBtn") || formData.containsKey("deleteBtn")) && 
				WebHelper.isMethodPost(request)) {
			//Get the list of users to disable (or delete) from the individual selections
			Set<Long> disableUserIds = LongIdUtil.getIdsAsLongSet(request.getParameterValues("addUsers"));
			
			//Also check for selections from the full list
			Iterator itFormData = formData.keySet().iterator();
			while (itFormData.hasNext()) {
				String key = (String)itFormData.next();
				if (key.indexOf("disableUser_") == 0) {
					String userId = key.substring(12, key.length());
					try {
						if (!disableUserIds.contains(Long.valueOf(userId))) {
							disableUserIds.add(Long.valueOf(userId));
						}
					} catch(Exception e) {}
				}
			}
			//Now disable the selected accounts
			for (Long id : disableUserIds) {
				List ids = new ArrayList();
				ids.add(id);
				if (id != null) {
					if (formData.containsKey("deleteBtn")) {
						//This is a request to delete accounts
						Map options = new HashMap();
						try {
							getProfileModule().deleteEntry(id, options);
						} catch(NotSupportedException e) {
							//Ignore attempts to disable accounts such as _emailPostingAgent
						}
					} else {
						//This is a request just to disable the accounts
						try {
							getProfileModule().disableEntry(id, true);
						} catch(NotSupportedException e) {
							//Ignore attempts to disable accounts such as _emailPostingAgent
						}
					}
				}
			}

			//Check for individual user changes
			Set<Long> enableUserIds = new HashSet<Long>();
			itFormData = formData.keySet().iterator();
			while (itFormData.hasNext()) {
				String key = (String)itFormData.next();
				if (key.indexOf("enableUser_") == 0) {
					String userId = key.substring(11, key.length());
					enableUserIds.add(Long.valueOf(userId));
				}
			}
			for (Long id : enableUserIds) {
				List ids = new ArrayList();
				ids.add(id);
				if (id != null) 
					getProfileModule().disableEntry(id, false);
			}

		} else {
			response.setRenderParameters(formData);
		}
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Map formData = request.getParameterMap();

		if (formData.containsKey("okBtn")) {
			//return new ModelAndView("forum/close_window", model);
		}
		
		String page = PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE, "");
		boolean showUserList = false;
		if (page.equals("")) {
			page = "0";
		} else {
			showUserList = true;
		}
		Integer pageNumber = Integer.valueOf(page);
		Integer maxHits = new Integer(ObjectKeys.SEARCH_MAX_HITS_ALL_USERS);
		model.put(WebKeys.PAGE_NUMBER, pageNumber);
		model.put(WebKeys.PAGE_SIZE, maxHits);
		model.put(WebKeys.SHOW_USER_USER_LIST, new Boolean(showUserList));

		List users = getProfileModule().getDisabledUserAccounts();
		
    	Map options = new HashMap();
    	options.put(ObjectKeys.SEARCH_OFFSET, pageNumber * maxHits);
    	options.put(ObjectKeys.SEARCH_MAX_HITS, maxHits);
		options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD);
		options.put(ObjectKeys.SEARCH_IS_PERSON, new Boolean(true));
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));
		Map allUsers = getProfileModule().getUsers(options);
		
		SortedSet<Principal> principals = getProfileModule().getPrincipals(users);
		model.put(WebKeys.DISABLED_USER_ACCOUNTS, principals);
		model.put(WebKeys.ACTIVE_USER_ACCOUNTS, allUsers.get(ObjectKeys.SEARCH_ENTRIES));
		model.put(WebKeys.SEARCH_TOTAL_HITS, allUsers.get(ObjectKeys.SEARCH_COUNT_TOTAL));

		try {
			ProfileBinder profilesBinder = getProfileModule().getProfileBinder();
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
			if (entryType.equals("")) {
				List defaultEntryDefinitions = profilesBinder.getEntryDefinitions();
				if (!defaultEntryDefinitions.isEmpty()) {
					// Only one option
					Definition def = (Definition) defaultEntryDefinitions.get(0);
					entryType = def.getId();
				}
			}
			model.put(WebKeys.FOLDER, profilesBinder);
			model.put(WebKeys.ENTRY_TYPE, entryType);
		} catch(AccessControlException e) {}

		return new ModelAndView(WebKeys.VIEW_ADMIN_MANAGE_USER_ACCOUNTS, model);

	}

}
