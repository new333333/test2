/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.PortletRequestUtils;

import org.springframework.web.portlet.ModelAndView;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class ViewChangeLogController  extends  SAbstractController {
	@Override
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {

		Map model = new HashMap();
		
		Long binderId = PortletRequestUtils.getLongParameter(request,  WebKeys.URL_BINDER_ID);
		String entityIdStr = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTITY_ID, "");
		Long entityId = null;
		if (!entityIdStr.equals("")) entityId = new Long(entityIdStr);
		if ((binderId == null) && (entityId == null)) {
			//not ajax request
			return new ModelAndView(WebKeys.VIEW_ADMIN_CHANGELOG, model);
		}
//~JW:	response.setContentType("text/xml");
		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, null);
		List changes = null;
		if (getAdminModule().testAccess(AdminOperation.manageFunction)) {
			if (binderId != null && entityId == null) {
				//get all changes for a binder
				changes = getAdminModule().getChanges(binderId, operation);
			} else if (entityId != null) {
				String entityType = PortletRequestUtils.getStringParameter(request,  "entityType", "folderEntry");
				EntityIdentifier entityIdentifier = new EntityIdentifier(entityId, EntityIdentifier.EntityType.valueOf(entityType));
				if (entityIdentifier != null) {
					changes = getAdminModule().getChanges(entityIdentifier, operation);
				}
			}
		}
			
		model.put("changeLogs", changes);
			
		return new ModelAndView(WebKeys.VIEW_ADMIN_UPDATE_CHANGELOG, model);
	}
}