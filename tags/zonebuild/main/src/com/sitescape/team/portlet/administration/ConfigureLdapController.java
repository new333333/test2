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
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.naming.NamingException;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.module.ldap.LdapConfig;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.ScheduleHelper;
import com.sitescape.util.StringUtil;
import com.sitescape.util.Validator;

public class ConfigureLdapController extends  SAbstractController {
	
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn")) {
			LdapConfig config = getLdapModule().getLdapConfig();
			if (config != null) {
				config.setSchedule(ScheduleHelper.getSchedule(request, null));
				config.setEnabled(PortletRequestUtils.getBooleanParameter(request,  "enabled", false));	
				config.setUserDelete(PortletRequestUtils.getBooleanParameter(request, "userDelete", false));
				config.setUserWorkspaceDelete(PortletRequestUtils.getBooleanParameter(request, "userWorkspaceDelete", false));
				config.setGroupDelete(PortletRequestUtils.getBooleanParameter(request, "groupDelete", false));
				config.setUserRegister(PortletRequestUtils.getBooleanParameter(request, "userRegister", false));
				config.setGroupRegister(PortletRequestUtils.getBooleanParameter(request, "groupRegister", false));
				config.setUserSync(PortletRequestUtils.getBooleanParameter(request, "userSync", false));
				config.setGroupsBasedn(PortletRequestUtils.getStringParameter(request, "groupBasedn", ""));
				config.setMembershipSync(PortletRequestUtils.getBooleanParameter(request, "membershipSync", false));
				config.setUserUrl(PortletRequestUtils.getStringParameter(request, "userUrl", ""));
				config.setUserPrincipal(PortletRequestUtils.getStringParameter(request, "userPrincipal", ""));
				config.setUserCredential(PortletRequestUtils.getStringParameter(request, "userCredentials", ""));
				config.setUserIdMapping(PortletRequestUtils.getStringParameter(request, "userIdMapping", ""));
				String[] mappings = StringUtil.split(PortletRequestUtils.getStringParameter(request, "userMappings", ""), "\n");
				Map maps = new HashMap();
				for (int i=0; i<mappings.length; ++i) {
					String m = mappings[i];
					if (Validator.isNull(m)) continue;
					String[] vals = StringUtil.split(m, "=");
					if (vals.length != 2) continue;
					maps.put(vals[1].trim(), vals[0].trim());
				}
				config.setUserMappings(maps);
				boolean runNow = PortletRequestUtils.getBooleanParameter(request, "runnow", false);
				if (runNow) {
					boolean enabled = config.isEnabled();
					//disable the schedule first
					config.setEnabled(false);
					getLdapModule().setLdapConfig(config);
					try {
						getLdapModule().syncAll();
					} catch (NamingException ne) {
						if (ne.getCause() != null)
							response.setRenderParameter(WebKeys.EXCEPTION, ne.getCause().getLocalizedMessage() != null ? ne.getCause().getLocalizedMessage() : ne.getCause().getMessage());
						else 
							response.setRenderParameter(WebKeys.EXCEPTION, ne.getLocalizedMessage() != null ? ne.getLocalizedMessage() : ne.getMessage());
						response.setRenderParameter("runnow", Boolean.TRUE.toString());
					} finally {
						//set it back
						if (enabled) {
							config.setEnabled(enabled);
							getLdapModule().setLdapConfig(config);
						}
					}
				} else {
					getLdapModule().setLdapConfig(config);
					
				}
			}
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			response.setRenderParameter("redirect", "true");
		} else
			response.setRenderParameters(formData);
		
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {

		if (!Validator.isNull(request.getParameter("redirect"))) {
			return new ModelAndView(WebKeys.VIEW_ADMIN_REDIRECT);
		}
		Map model = new HashMap();
		model.put(WebKeys.EXCEPTION, request.getParameter(WebKeys.EXCEPTION));
		model.put(WebKeys.LDAP_CONFIG, getLdapModule().getLdapConfig());
		model.put("runnow", request.getParameter("runnow"));
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_LDAP, model);
		
	}
}
