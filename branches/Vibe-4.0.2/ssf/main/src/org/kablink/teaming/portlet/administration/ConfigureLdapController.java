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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.ldap.LdapSchedule;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.XmlUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.ScheduleHelper;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.StringUtil;
import org.kablink.util.Validator;
import org.springframework.web.portlet.ModelAndView;


public class ConfigureLdapController extends  SAbstractController {
	@SuppressWarnings("unchecked")
	@Override
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		Map formData = request.getParameterMap();
		if (formData.containsKey("okBtn") && WebHelper.isMethodPost(request)) {
			LdapSchedule schedule = getLdapModule().getLdapSchedule();
			if (schedule != null)
			{
				boolean syncAllUsersAndGroups;
				String listOfLdapConfigsToSyncGuid;
				
				schedule.getScheduleInfo().setSchedule(ScheduleHelper.getSchedule(request, null));
				schedule.getScheduleInfo().setEnabled(PortletRequestUtils.getBooleanParameter(request,  "enabled", false));	
				schedule.setUserDelete( PortletRequestUtils.getBooleanParameter( request, "notInLdap", false ) );
				
				schedule.setUserWorkspaceDelete(PortletRequestUtils.getBooleanParameter(request, "userWorkspaceDelete", false));
				schedule.setGroupDelete(PortletRequestUtils.getBooleanParameter(request, "groupDelete", false));
				schedule.setUserRegister(PortletRequestUtils.getBooleanParameter(request, "userRegister", false));
				schedule.setGroupRegister(PortletRequestUtils.getBooleanParameter(request, "groupRegister", false));
				schedule.setUserSync(PortletRequestUtils.getBooleanParameter(request, "userSync", false));
				schedule.setGroupSync(PortletRequestUtils.getBooleanParameter(request, "groupSync", false));
				schedule.setMembershipSync(PortletRequestUtils.getBooleanParameter(request, "membershipSync", false));

				AuthenticationConfig authConfig = getAuthenticationModule().getAuthenticationConfig();
				authConfig.setAllowLocalLogin(PortletRequestUtils.getBooleanParameter(request, "allowLocalLogin", false));
				getAuthenticationModule().setAuthenticationConfig(authConfig);

				LinkedList<LdapConnectionConfig> configList = new LinkedList<LdapConnectionConfig>();
				try {
					Document doc = XmlUtil.parseText(PortletRequestUtils.getStringParameter(request, "ldapConfigDoc", "<doc/>", false));
					for(Object o : doc.selectNodes("//ldapConfig")) {
						Node cNode = (Node) o;
						String ldapGuidAttribute = null;
						
						// Get the ldap attribute that uniquely identifies a user or group.
						ldapGuidAttribute = cNode.selectSingleNode( "ldapGuidAttribute" ).getText();
						
						String principal = cNode.selectSingleNode("principal").getText();
						String credentials = cNode.selectSingleNode("credentials").getText();
						String url = cNode.selectSingleNode("url").getText();
						// If the protocol is uppercase, users can't log in.  See bug 823936.
						if ( url != null )
							url = url.toLowerCase();
						String userIdAttribute = cNode.selectSingleNode("userIdAttribute").getText();
						String[] mappings = StringUtil.split(cNode.selectSingleNode("mappings").getText(), "\n");
						LinkedList<LdapConnectionConfig.SearchInfo> userQueries = new LinkedList<LdapConnectionConfig.SearchInfo>();
						List foo = cNode.selectNodes("userSearches/search");
						for(Object o2 : foo) {
							Node sNode = (Node) o2;
							Node someNode;
							String baseDn;
							String filter;
							String ss;

							baseDn = "";
							filter = "";
							ss = "";

							// Get the <baseDn> element.
							someNode = sNode.selectSingleNode("baseDn");
							if ( someNode != null )
								baseDn = someNode.getText();
							
							// Get the <filter> element.
							someNode = sNode.selectSingleNode("filter");
							if ( someNode != null )
								filter = someNode.getText();
							
							// Get the <searchSubtree> element.
							someNode = sNode.selectSingleNode("searchSubtree");
							if ( someNode != null )
								ss = someNode.getText();

							userQueries.add(new LdapConnectionConfig.SearchInfo(baseDn, filter, ss.equals("true")));
						}
						LinkedList<LdapConnectionConfig.SearchInfo> groupQueries = new LinkedList<LdapConnectionConfig.SearchInfo>();
						foo = cNode.selectNodes("groupSearches/search");
						for(Object o2 : foo) {
							Node sNode = (Node) o2;
							Node someNode;
							String baseDn;
							String filter;
							String ss;

							baseDn = "";
							filter = "";
							ss = "";
							
							// Get the <baseDn> element.
							someNode = sNode.selectSingleNode("baseDn");
							if ( someNode != null )
								baseDn = someNode.getText();
							
							// Get the <filter> element.
							someNode = sNode.selectSingleNode("filter");
							if ( someNode != null )
								filter = someNode.getText();
							
							// Get the <searchSubtree> element.
							someNode = sNode.selectSingleNode("searchSubtree");
							if ( someNode != null )
								ss = someNode.getText();

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
							new LdapConnectionConfig(url, userIdAttribute, maps, userQueries, groupQueries, principal, credentials, ldapGuidAttribute );
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
				
				// Get the time zone from the form data.
				String	timeZone;
				String[] value;
				
				value = (String[]) formData.get( WebKeys.DEFAULT_TIME_ZONE );
				if ( value != null )
				{
					timeZone = value[0];
				
					// Save away the time zone as the default time zone used when creating a new user.
					setDefaultTimeZone( timeZone );
				}
				
				// Get the selected locale from the form data.
				String	localeId;
				
				value = (String[]) formData.get( WebKeys.DEFAULT_LOCALE );
				if ( value != null )
				{
					localeId = value[0];
				
					// Save away the locale as the default locale used when creating a new user.
					setDefaultLocale( localeId );
				}
				
				// Save the ldap configuration.
				getLdapModule().setLdapSchedule(schedule);

				// Does the user want to sync all users and groups?
				syncAllUsersAndGroups = PortletRequestUtils.getBooleanParameter(request, "runnow", false);

				// Get the list of ldap configs that we need to sync the guid
				listOfLdapConfigsToSyncGuid = PortletRequestUtils.getStringParameter( request, "listOfLdapConfigsToSyncGuid", "", false );
				
				// Do we need to start a sync?
				if ( (listOfLdapConfigsToSyncGuid != null && listOfLdapConfigsToSyncGuid.length() > 0 ) ||
					  syncAllUsersAndGroups == true )
				{
					// Yes
					// Pass this fact back to the page.  When the page loads it will issue an ajax
					// request to start the sync.
					response.setRenderParameter( "startLdapSync", "true" );
					response.setRenderParameter( "syncAllUsersAndGroups", Boolean.toString( syncAllUsersAndGroups ) );
					if ( listOfLdapConfigsToSyncGuid != null && listOfLdapConfigsToSyncGuid.length() > 0 )
						response.setRenderParameter( "listOfLdapConfigsToSyncGuid", listOfLdapConfigsToSyncGuid );
				}
			}
		} else
			response.setRenderParameters(formData);
		
	}

	@SuppressWarnings({"unchecked"})
	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {

		String timeZone	= null;
		String defaultLocaleId = null;
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

		model.put( "startLdapSync", request.getParameter( "startLdapSync" ) );
		model.put( "syncAllUsersAndGroups", request.getParameter( "syncAllUsersAndGroups" ) );
		
		// Add the list of ldap configs to sync the guid
		{
			String commaSepList;
			ArrayList<String> list;
			
			list = new ArrayList<String>();
			commaSepList = PortletRequestUtils.getStringParameter( request,  "listOfLdapConfigsToSyncGuid", "", false );
			if ( commaSepList != null && commaSepList.length() > 0 )
			{
				String[] listOfLdapConfigsToSyncGuid;
				
				listOfLdapConfigsToSyncGuid = commaSepList.split( "," );
				if ( listOfLdapConfigsToSyncGuid != null && listOfLdapConfigsToSyncGuid.length > 0 )
				{
					for ( String nextLdapUrl : listOfLdapConfigsToSyncGuid )
					{
						list.add( nextLdapUrl );
					}
				}
				else
					list.add( commaSepList );
			}
			model.put( "listOfLdapConfigsToSyncGuid", list );
		}

		model.put(WebKeys.LDAP_CONFIG, getLdapModule().getLdapSchedule());
		model.put(WebKeys.LDAP_CONNECTION_CONFIGS, getAuthenticationModule().getLdapConnectionConfigs());
		model.put(WebKeys.AUTHENTICATION_CONFIG, getAuthenticationModule().getAuthenticationConfig());
		model.put("runnow", request.getParameter("runnow"));
		
		// Add the default time zone to the response.
		timeZone = getDefaultTimeZone();
		model.put( WebKeys.DEFAULT_TIME_ZONE, timeZone );
		
		// Add the default locale to the response.
		defaultLocaleId = getDefaultLocaleId();
		model.put( WebKeys.DEFAULT_LOCALE, defaultLocaleId );

		// Add the product name, Vibe or Filr
		if ( Utils.checkIfFilr() )
			model.put( "productName", "Filr" );
		else
			model.put( "productName", "Vibe" );
		
		return new ModelAndView(WebKeys.VIEW_ADMIN_CONFIGURE_LDAP, model);
		
	}
	
	/**
	 * Return the id of the default locale.  This setting is used to set the locale on a user when
	 * the user is created from an ldap sync.
	 */
	private String getDefaultLocaleId()
	{
		String		defaultLocaleId = "";
		Workspace	topWorkspace;
		
		// Get the top workspace.  That is where global properties are stored.
		topWorkspace = getWorkspaceModule().getTopWorkspace();
		
		// Get the default locale property.
		defaultLocaleId = (String) topWorkspace.getProperty( ObjectKeys.GLOBAL_PROPERTY_DEFAULT_LOCALE );
		if ( defaultLocaleId == null || defaultLocaleId.length() == 0 )
		{
			Locale locale;
			
			// Get the default system locale;
			locale = NLT.getTeamingLocale();
			if ( locale != null )
				defaultLocaleId = locale.toString();
		}
		
		return defaultLocaleId;
	}// end getDefaultLocaleId()
	

	/**
	 * Return the default time zone setting.  This setting is used to set the time zone on a user when
	 * the user is created from an ldap sync.
	 */
	private String getDefaultTimeZone()
	{
		String		defaultTimeZone;
		Workspace	topWorkspace;
		
		// Get the top workspace.  That is where global properties are stored.
		topWorkspace = getWorkspaceModule().getTopWorkspace();
		
		// Get the default time zone property.
		defaultTimeZone = (String) topWorkspace.getProperty( ObjectKeys.GLOBAL_PROPERTY_DEFAULT_TIME_ZONE );
		if ( defaultTimeZone == null || defaultTimeZone.length() == 0 )
			defaultTimeZone = "GMT";
		
		return defaultTimeZone;
	}// end getDefaultTimeZone()
	

	/**
	 * Set the default locale setting.  This setting is used to set the locale  on a user when
	 * the user is created from an ldap sync.
	 */
	private void setDefaultLocale( String localeId )
	{
		Workspace	topWorkspace;
		
		if ( localeId == null || localeId.length() == 0 )
			return;
		
		// Get the top workspace.  That is where global properties are stored.
		topWorkspace = getWorkspaceModule().getTopWorkspace();
		
		// Save the default locale id as a global property
		topWorkspace.setProperty( ObjectKeys.GLOBAL_PROPERTY_DEFAULT_LOCALE, localeId );

	}// end setDefaultTimeZone()
	

	/**
	 * Set the default time zone setting.  This setting is used to set the time zone on a user when
	 * the user is created from an ldap sync.
	 */
	private void setDefaultTimeZone( String timeZone )
	{
		Workspace	topWorkspace;
		
		if ( timeZone == null || timeZone.length() == 0 )
			return;
		
		// Get the top workspace.  That is where global properties are stored.
		topWorkspace = getWorkspaceModule().getTopWorkspace();
		
		// Save the default time zone as a global property
		topWorkspace.setProperty( ObjectKeys.GLOBAL_PROPERTY_DEFAULT_TIME_ZONE, timeZone );

	}// end setDefaultTimeZone()
	
}
