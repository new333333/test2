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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.naming.NamingException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.ldap.LdapSchedule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.ScheduleHelper;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.StringUtil;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;


public class ConfigureUserAccessController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			AuthenticationConfig authConfig = getAuthenticationModule().getAuthenticationConfig();
			authConfig.setAllowAnonymousAccess(PortletRequestUtils.getBooleanParameter(request, "allowAnonymous", false));
			authConfig.setAnonymousReadOnly(PortletRequestUtils.getBooleanParameter(request, "anonymousReadOnly", false));
			authConfig.setAllowSelfRegistration(PortletRequestUtils.getBooleanParameter(request, "allowSelfRegistration", false));
			getAuthenticationModule().setAuthenticationConfig(authConfig);
		} else
			response.setRenderParameters(formData);
		
	}

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		boolean		guestUserHasAddRights;

		model.put(WebKeys.AUTHENTICATION_CONFIG, getAuthenticationModule().getAuthenticationConfig());

		// If the guest user doesn't not have rights to add an entry to the profile binder and the user
		// enables the "Allow people to create their own accounts" option, we want to tell the user that
		// they need to give the guest user rights to add an entry to the profile binder.
		// Can the logged in user add an entry to the profile binder?
		guestUserHasAddRights = MiscUtil.doesGuestUserHaveAddRightsToProfileBinder( this );
		model.put( "guestUserHasAddRightsToProfileBinder", Boolean.toString( guestUserHasAddRights ) );
		
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_USER_ACCESS, model);
		
	}
}
