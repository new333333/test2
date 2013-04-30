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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractControllerRetry;
import org.kablink.teaming.web.util.Clipboard;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.springframework.web.portlet.ModelAndView;

/**
 * Controller to handle ajax requests for the clipboard
 * @author Janet
 *
 */
public class ClipboardController extends SAbstractControllerRetry {
	//caller will retry on OptimisiticLockExceptions
	public void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		if (WebHelper.isUserLoggedIn(request)) {
			String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
			if (op.equals(WebKeys.OPERATION_ADD_TO_CLIPBOARD)) {
				ajaxAddToClipboard(request, response);
			} else if (op.equals(WebKeys.OPERATION_CLEAR_CLIPBOARD)) {
				ajaxClearClipboard(request, response);
			} else if (op.equals(WebKeys.OPERATION_REMOVE_FROM_CLIPBOARD)) {
				ajaxRemoveFromClipboard(request, response);
			}
		}
	}
	
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		response.setContentType("text/json");

		if (!WebHelper.isUserLoggedIn(request)) {
			Map model = new HashMap();
			model.put(WebKeys.AJAX_ERROR_MESSAGE, "general.notLoggedIn");	
			return new ModelAndView("common/json_ajax_return", model);
		}
		
		//The user is logged in
		if (op.equals(WebKeys.OPERATION_GET_CLIPBOARD_USERS)) {
			return ajaxGetClipboardUsers(request, response);
		} else {
			return new ModelAndView("common/json_ajax_return");			
		}
	}
	private void ajaxAddToClipboard(ActionRequest request, 
			ActionResponse response) throws Exception {
		String musterClass = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MUSTER_CLASS, "");
		List musterIds = PortletRequestUtils.getLongListParameters(request, WebKeys.URL_MUSTER_IDS);
		
		Clipboard clipboard = new Clipboard(request);
		clipboard.add(musterClass, musterIds);
				
		Boolean addTeamMembers = PortletRequestUtils.getBooleanParameter(request, "add_team_members", false);
		if (addTeamMembers) {
			Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
			try {
				Collection teamMemberIds = getBinderModule().getTeamMemberIds(binderId, true);
				clipboard.add(Clipboard.USERS, teamMemberIds);				
			} catch (AccessControlException ac) {} //no access, just skip
		}
	}

	private void ajaxRemoveFromClipboard(ActionRequest request, 
			ActionResponse response) throws Exception {
		String musterClass = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MUSTER_CLASS, "");
		String[] musterIds = new String[0];
		if (PortletRequestUtils.getStringParameters(request, WebKeys.URL_MUSTER_IDS) != null) {
			musterIds = PortletRequestUtils.getStringParameters(request, WebKeys.URL_MUSTER_IDS);
		}
		Clipboard clipboard = new Clipboard(request);		
		clipboard.remove(musterClass, LongIdUtil.getIdsAsLongSet(musterIds));
	}
	
	private void ajaxClearClipboard(ActionRequest request, 
			ActionResponse response) throws Exception {
		Clipboard clipboard = new Clipboard(request);
		String musterClass = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MUSTER_CLASS, "");
		String[] musterClasses = musterClass.split(" ");
		for (int i = 0; i < musterClasses.length; i++) {
			if (!musterClasses[i].equals("")) {
				clipboard.clear(musterClasses[i]);
			}
		}
	}
	
	private ModelAndView ajaxGetClipboardUsers(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		
		Clipboard clipboard = new Clipboard(request);
		Set clipboardUsers = clipboard.get(Clipboard.USERS);
		model.put(WebKeys.CLIPBOARD_PRINCIPALS , getProfileModule().getUsersFromPrincipals(
					clipboardUsers));
		
		return new ModelAndView("forum/clipboard_users", model);
	}


}
