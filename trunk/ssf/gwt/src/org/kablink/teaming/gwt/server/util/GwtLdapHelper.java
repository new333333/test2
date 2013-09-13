/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.server.util;

import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.gwt.client.GwtLdapConfig;
import org.kablink.teaming.module.ldap.LdapSchedule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SZoneConfig;


/**
 * LDAP Helper methods used by GWT server-side code
 *
 * @author jwootton@novell.com
 */
public class GwtLdapHelper
{
	protected static Log m_logger = LogFactory.getLog( GwtLdapHelper.class );
	
	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtLdapHelper()
	{
		// Nothing to do.
	}

	/**
	 * Return the id of the default locale.  This setting is used to set the locale on a user when
	 * the user is created from an ldap sync.
	 */
	private static String getDefaultLocaleId( AllModulesInjected ami )
	{
		String		defaultLocaleId = "";
		Workspace	topWorkspace;
		
		// Get the top workspace.  That is where global properties are stored.
		topWorkspace = ami.getWorkspaceModule().getTopWorkspace();
		
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
	}
	

	/**
	 * Return the default time zone setting.  This setting is used to set the time zone on a user when
	 * the user is created from an ldap sync.
	 */
	private static String getDefaultTimeZone( AllModulesInjected ami )
	{
		String		defaultTimeZone;
		Workspace	topWorkspace;
		
		// Get the top workspace.  That is where global properties are stored.
		topWorkspace = ami.getWorkspaceModule().getTopWorkspace();
		
		// Get the default time zone property.
		defaultTimeZone = (String) topWorkspace.getProperty( ObjectKeys.GLOBAL_PROPERTY_DEFAULT_TIME_ZONE );
		if ( defaultTimeZone == null || defaultTimeZone.length() == 0 )
			defaultTimeZone = "GMT";
		
		return defaultTimeZone;
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public static GwtLdapConfig getLdapConfig(
		AllModulesInjected ami )
	{
		GwtLdapConfig ldapConfig;
		String timeZone	= null;
		String defaultLocaleId = null;

		ldapConfig = new GwtLdapConfig();
		
    	// Get the user attribute mappings
    	{
        	List defaultMappings;
        	int i;

	    	defaultMappings  = SZoneConfig.getElements( "ldapConfiguration/userMapping/mapping" );
	    	
	    	if ( defaultMappings != null )
	    	{
		    	for( i=0; i < defaultMappings.size(); i++ )
		    	{
		    		Element next;
		
		    		next = (Element) defaultMappings.get( i );
		    		ldapConfig.addDefaultUserAttributeMapping( next.attributeValue( "from" ), next.attributeValue( "to" ) );
		    	}
	    	}
    	}

    	// Get the default user filter.
    	ldapConfig.setDefaultUserFilter( SZoneConfig.getString( "ldapConfiguration/userFilter" ) );

    	// Get the default group filter.
    	ldapConfig.setDefaultGroupFilter( SZoneConfig.getString( "ldapConfiguration/groupFilter" ) );
    
    	// Get the information stored in LdapSchedule
    	{
    		LdapSchedule ldapSchedule;
    		
    		ldapSchedule = ami.getLdapModule().getLdapSchedule();
    		
    		if ( ldapSchedule != null )
    		{
	    		// Get the user information
	    		ldapConfig.setSyncUserProfiles( ldapSchedule.isUserSync() );
	    		ldapConfig.setRegisterUserProfilesAutomatically( ldapSchedule.isUserRegister() );
	    		ldapConfig.setDeleteLdapUsers( ldapSchedule.isUserDelete() );
	    		ldapConfig.setDeleteUserWorkspace( ldapSchedule.isUserWorkspaceDelete() );
	    		
	    		// Get the group information
	    		ldapConfig.setSyncGroupProfiles( ldapSchedule.isGroupSync() );
	    		ldapConfig.setRegisterGroupProfilesAutomatically( ldapSchedule.isGroupRegister() );
	    		ldapConfig.setSyncGroupMembership( ldapSchedule.isMembershipSync() );
	    		ldapConfig.setDeleteNonLdapGroups( ldapSchedule.isGroupDelete() );
    		}
    	}
    	
//		model.put(WebKeys.LDAP_CONNECTION_CONFIGS, getAuthenticationModule().getLdapConnectionConfigs());

    	// Get the "allow local login" setting
    	{
    		AuthenticationConfig authConfig;
    		
    		authConfig = ami.getAuthenticationModule().getAuthenticationConfig();
    		
    		if ( authConfig != null )
    		{
    			ldapConfig.setAllowLocalLogin( authConfig.isAllowLocalLogin() );
    		}
    	}
    	
		// Add the default time zone to the response.
		timeZone = getDefaultTimeZone( ami );
		ldapConfig.setTimeZone( timeZone );
		
		// Add the default locale to the response.
		defaultLocaleId = getDefaultLocaleId( ami );
		ldapConfig.setLocale( defaultLocaleId );

		return ldapConfig;
	}
}
