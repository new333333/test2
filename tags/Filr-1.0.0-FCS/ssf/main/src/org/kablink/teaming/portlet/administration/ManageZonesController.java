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

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.NameMissingException;
import org.kablink.teaming.ZoneNameMissingException;
import org.kablink.teaming.ZoneVirtualHostMissingException;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;

public class ManageZonesController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		//response.setRenderParameters(formData);
		if (formData.containsKey("addBtn") && WebHelper.isMethodPost(request)) {
			String zoneName=PortletRequestUtils.getStringParameter(request, WebKeys.URL_ZONE_NAME, null);
			if(Validator.isNull(zoneName))
				throw new ZoneNameMissingException();
			String virtualHost = PortletRequestUtils.getStringParameter(request, WebKeys.URL_VIRTUAL_HOST, null);
			if(Validator.isNull(virtualHost))
				throw new ZoneVirtualHostMissingException();
			getZoneModule().addZone(zoneName, virtualHost, null);
		} else if (formData.containsKey("deleteBtn") && WebHelper.isMethodPost(request)) {
			String zoneName = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ZONE_NAME, null);
			if(Validator.isNotNull(zoneName))
				getZoneModule().deleteZone(zoneName);
		} else if (formData.containsKey("modifyBtn") && WebHelper.isMethodPost(request)) {
			String zoneName = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ZONE_NAME, null);
			if(Validator.isNull(zoneName))
				throw new ZoneNameMissingException();
			String virtualHost = PortletRequestUtils.getStringParameter(request, WebKeys.URL_VIRTUAL_HOST, null);
			if(Validator.isNull(virtualHost))
				throw new ZoneVirtualHostMissingException();
			getZoneModule().modifyZone(zoneName, virtualHost, null);
		} else {
			response.setRenderParameters(formData);
		}
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		return new ModelAndView(WebKeys.VIEW_ADMIN_MANAGE_ZONES, WebKeys.ZONE_INFO_LIST, getZoneModule().getZoneInfos());
	}

}
