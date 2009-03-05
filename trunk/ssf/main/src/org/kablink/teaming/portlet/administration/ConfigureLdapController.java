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
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.module.ldap.LdapSchedule;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.ScheduleHelper;
import org.kablink.util.StringUtil;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;


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

				AuthenticationConfig authConfig = getAuthenticationModule().getAuthenticationConfig();
				authConfig.setAllowLocalLogin(PortletRequestUtils.getBooleanParameter(request, "allowLocalLogin", false));
				getAuthenticationModule().setAuthenticationConfig(authConfig);

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

							// If the base dn has a '&', '<', or '>' in it, the xml will not parse when we read it from the db
							// and try to create an xml document.  Wrap the base dn with <![CDATA[]]>.
							String baseDn = wrapWithCDATA( sNode.selectSingleNode("baseDn").getText() );

							// If the filter has a '&', '<', or '>' in it, the xml will not parse when we read it from the db
							// and try to create an xml document.  Wrap the filter with <![CDATA[]]>.
							String filter = wrapWithCDATA( sNode.selectSingleNode("filter").getText() );
							String ss = sNode.selectSingleNode("searchSubtree").getText();
							userQueries.add(new LdapConnectionConfig.SearchInfo(baseDn, filter, ss.equals("true")));
						}
						LinkedList<LdapConnectionConfig.SearchInfo> groupQueries = new LinkedList<LdapConnectionConfig.SearchInfo>();
						foo = cNode.selectNodes("groupSearches/search");
						for(Object o2 : foo) {
							Node sNode = (Node) o2;
							// If the base dn has a '&', '<', or '>' in it, the xml will not parse when we read it from the db
							// and try to create an xml document.  Wrap the base dn with <![CDATA[]]>.
							String baseDn = wrapWithCDATA( sNode.selectSingleNode("baseDn").getText() );

							// If the filter has a '&', '<', or '>' in it, the xml will not parse when we read it from the db
							// and try to create an xml document.  Wrap the filter with <![CDATA[]]>.
							String filter = wrapWithCDATA( sNode.selectSingleNode("filter").getText() );
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
					String	msg;
					
					// Hmm.  What to do here?
					// This should never happen.  Tell the user there is something wrong with the ldap filter.
					msg = e.getMessage();
					response.setRenderParameter( WebKeys.EXCEPTION, msg );
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
    	model.put(WebKeys.DEFAULT_USER_FILTER, SZoneConfig.getString("ldapConfiguration/userFilter"));
    	model.put(WebKeys.DEFAULT_GROUP_FILTER, SZoneConfig.getString("ldapConfiguration/groupFilter"));
    
		model.put(WebKeys.EXCEPTION, request.getParameter(WebKeys.EXCEPTION));
		model.put(WebKeys.LDAP_CONFIG, getLdapModule().getLdapSchedule());
		model.put(WebKeys.LDAP_CONNECTION_CONFIGS, getAuthenticationModule().getLdapConnectionConfigs());
		model.put(WebKeys.AUTHENTICATION_CONFIG, getAuthenticationModule().getAuthenticationConfig());
		model.put("runnow", request.getParameter("runnow"));
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_LDAP, model);
		
	}
	
	/**
	 * Wrap the given text with <![CDATA[ ]]>
	 */
	private String wrapWithCDATA( String str )
	{
		StringBuffer	wrappedStr;
		
		wrappedStr = new StringBuffer( "<![CDATA[" );
		if ( str != null && str.length() > 0 )
			wrappedStr.append( str );
		
		wrappedStr.append( "]]>" );
		
		return wrappedStr.toString();
	}// end wrapWithCDATA()
}
