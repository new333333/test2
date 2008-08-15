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
import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.domain.LdapConnectionConfig;
import com.sitescape.team.module.ldap.LdapSchedule;
import com.sitescape.team.util.SZoneConfig;
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
			LdapSchedule schedule = getLdapModule().getLdapSchedule();
			if (schedule != null) {
				schedule.getScheduleInfo().setSchedule(ScheduleHelper.getSchedule(request, null));
				schedule.getScheduleInfo().setEnabled(PortletRequestUtils.getBooleanParameter(request,  "enabled", false));	
				schedule.setUserDelete(PortletRequestUtils.getBooleanParameter(request, "userDelete", false));
				schedule.setUserWorkspaceDelete(PortletRequestUtils.getBooleanParameter(request, "userWorkspaceDelete", false));
				schedule.setGroupDelete(PortletRequestUtils.getBooleanParameter(request, "groupDelete", false));
				schedule.setUserRegister(PortletRequestUtils.getBooleanParameter(request, "userRegister", false));
				schedule.setGroupRegister(PortletRequestUtils.getBooleanParameter(request, "groupRegister", false));
				schedule.setUserSync(PortletRequestUtils.getBooleanParameter(request, "userSync", false));
				schedule.setMembershipSync(PortletRequestUtils.getBooleanParameter(request, "membershipSync", false));
				
				LinkedList<LdapConnectionConfig> configList = new LinkedList<LdapConnectionConfig>();
				try {
					Document doc = DocumentHelper.parseText(PortletRequestUtils.getStringParameter(request, "ldapConfigDoc", "<doc/>"));
					for(Object o : doc.selectNodes("//ldapConfig")) {
						Node cNode = (Node) o;
						String principal = cNode.selectSingleNode("principal").getText();
						String credentials = cNode.selectSingleNode("credentials").getText();
						String url = cNode.selectSingleNode("url").getText();
						String userIdAttribute = cNode.selectSingleNode("userIdAttribute").getText();
						String[] mappings = StringUtil.split(cNode.selectSingleNode("mappings").getText(), "\n");
						LinkedList<LdapConnectionConfig.SearchInfo> userQueries = new LinkedList<LdapConnectionConfig.SearchInfo>();
						List foo = cNode.selectNodes("userSearches/search");
						for(Object o2 : foo) {
							Node sNode = (Node) o2;
							String baseDn = sNode.selectSingleNode("baseDn").getText();
							String filter = sNode.selectSingleNode("filter").getText();
							String ss = sNode.selectSingleNode("searchSubtree").getText();
							userQueries.add(new LdapConnectionConfig.SearchInfo(baseDn, filter, ss.equals("true")));
						}
						LinkedList<LdapConnectionConfig.SearchInfo> groupQueries = new LinkedList<LdapConnectionConfig.SearchInfo>();
						foo = cNode.selectNodes("groupSearches/search");
						for(Object o2 : foo) {
							Node sNode = (Node) o2;
							String baseDn = sNode.selectSingleNode("baseDn").getText();
							String filter = sNode.selectSingleNode("filter").getText();
							String ss = sNode.selectSingleNode("searchSubtree").getText();
							groupQueries.add(new LdapConnectionConfig.SearchInfo(baseDn, filter, ss.equals("true")));
						}
						HashMap<String, String> maps = new HashMap<String, String>();
						for (int i=0; i<mappings.length; ++i) {
							String m = mappings[i];
							if (Validator.isNull(m)) continue;
							String[] vals = StringUtil.split(m, "=");
							if (vals.length != 2) continue;
							maps.put(vals[1].trim(), vals[0].trim());
						}
						LdapConnectionConfig c =
							new LdapConnectionConfig(url, userIdAttribute, maps, userQueries, groupQueries, principal, credentials);
						Node idNode = cNode.selectSingleNode("id");
						if(idNode != null) {
							c.setId(idNode.getText());
						}
						configList.add(c);
					}
				} catch(DocumentException e) {
					// Hmm.  What to do here?
				}
				getAuthenticationModule().setLdapConnectionConfigs(configList);

				boolean runNow = PortletRequestUtils.getBooleanParameter(request, "runnow", false);
				if (runNow) {
					boolean enabled = schedule.getScheduleInfo().isEnabled();
					//disable the schedule first
					schedule.getScheduleInfo().setEnabled(false);
					getLdapModule().setLdapSchedule(schedule);
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
							schedule.getScheduleInfo().setEnabled(enabled);
							getLdapModule().setLdapSchedule(schedule);
						}
					}
				} else {
					getLdapModule().setLdapSchedule(schedule);
					
				}
			}
		} else
			response.setRenderParameters(formData);
		
	}

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {

		Map model = new HashMap();

    	Map attributes = new LinkedHashMap();
    	List mappings  = SZoneConfig.getElements("ldapConfiguration/userMapping/mapping");
    	for(int i=0; i < mappings.size(); i++) {
    		Element next = (Element) mappings.get(i);
    		attributes.put(next.attributeValue("from"), next.attributeValue("to"));
    	}
    	model.put(WebKeys.USER_ATTRIBUTES, attributes);

		model.put(WebKeys.EXCEPTION, request.getParameter(WebKeys.EXCEPTION));
		model.put(WebKeys.LDAP_CONFIG, getLdapModule().getLdapSchedule());
		model.put(WebKeys.LDAP_CONNECTION_CONFIGS, getAuthenticationModule().getLdapConnectionConfigs());
		model.put("runnow", request.getParameter("runnow"));
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_LDAP, model);
		
	}
}
