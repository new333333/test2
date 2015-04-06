/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.portlet.workspaceTree;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WorkspaceTreeHelper;

import org.springframework.web.portlet.ModelAndView;

/**
 * ?
 * 
 * @author Peter Hurley
 */
public class WorkspaceTreeController extends SAbstractController  {
	@Override
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		try {
			response.setWindowState(request.getWindowState());
		} catch(Exception e) {
			logger.debug("WorkspaceTreeController.handleActionRequestAfterValidation(Exception:  '" + MiscUtil.exToString(e) + "'):  Ignored");
		};
        User user = RequestContextHolder.getRequestContext().getUser();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		if (op.equals(WebKeys.OPERATION_SET_DISPLAY_STYLE)) {
			Map<String,Object> updates = new HashMap<String,Object>();
			String newDisplayStyle = PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,"");
			//Only allow "word" characters (such as a-z_0-9 )
			if (newDisplayStyle.equals("") || !newDisplayStyle.matches("^.*[\\W]+.*$")) {
				updates.put(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE, newDisplayStyle);
			}
			try {
				getProfileModule().modifyEntry(user.getId(), new MapInputData(updates));
			} catch(WriteEntryDataException e) {
	    		response.setRenderParameter(WebKeys.ENTRY_DATA_PROCESSING_ERRORS, e.getMessage());
	    		return;
			}
		
		} else if (op.equals(WebKeys.OPERATION_CLEAR_UNSEEN)) {
			Set<Long> ids = LongIdUtil.getIdsAsLongSet(request.getParameterValues(WebKeys.URL_IDS));
			getProfileModule().setSeenIds(null, ids);
		}
	}
	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		
        User user = RequestContextHolder.getRequestContext().getUser();
		boolean accessible_simple_ui = SPropsUtil.getBoolean("accessibility.simple_ui", false);
        if (user != null && ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId()) && 
        		accessible_simple_ui && 
        		ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(user.getCurrentDisplayStyle())) {
			Map<String,Object> updates = new HashMap<String,Object>();
			updates.put(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE, BinderHelper.getDefaultViewDisplayStyle());
        	MapInputData  inputData = new MapInputData (updates);
        	updates.put(ObjectKeys.FIELD_USER_DISPLAYSTYLE, BinderHelper.getDefaultViewDisplayStyle());
			try {
				getProfileModule().modifyEntry(user.getId(), inputData);
			} catch(WriteEntryDataException e) {
			}
        }

		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		if (binderId == null) return prepBeans(request, BinderHelper.CommonPortletDispatch(this, request, response));
		
		boolean showTrash = PortletRequestUtils.getBooleanParameter(request, WebKeys.URL_SHOW_TRASH, false);
		return prepBeans(request, WorkspaceTreeHelper.setupWorkspaceBeans(this, binderId, request, response, showTrash));

	}
	
	/*
	 * Ensures the beans in the ModelAndView are ready to go.
	 */
	private static ModelAndView prepBeans(RenderRequest request, ModelAndView mv) {
		return GwtUIHelper.cacheToolbarBeans(request, mv);
	}
}
