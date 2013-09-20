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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.LdapConnectionConfig.SearchInfo;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.gwt.client.GwtLdapConfig;
import org.kablink.teaming.gwt.client.GwtLdapConnectionConfig;
import org.kablink.teaming.gwt.client.GwtLdapSearchInfo;
import org.kablink.teaming.gwt.client.GwtSchedule;
import org.kablink.teaming.gwt.client.rpc.shared.SaveLdapConfigRpcResponseData;
import org.kablink.teaming.jobs.ScheduleInfo;
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
		
    	// Get the default user attribute mappings
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
    			ScheduleInfo scheduleInfo;
    			GwtSchedule gwtSchedule;
    			
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
	    		
	    		// Get the ldap sync schedule
	    		scheduleInfo = ldapSchedule.getScheduleInfo();
	    		gwtSchedule = GwtServerHelper.getGwtSyncSchedule( scheduleInfo );
	    		ldapConfig.setSchedule( gwtSchedule );
    		}
    	}
    	
    	// Add the ldap servers
    	{
    		List<LdapConnectionConfig> listOfLdapServers;
    		
    		listOfLdapServers = ami.getAuthenticationModule().getLdapConnectionConfigs();
    		if ( listOfLdapServers != null )
    		{
    			for ( LdapConnectionConfig config : listOfLdapServers )
    			{
    				GwtLdapConnectionConfig gwtConfig;
    				
    				gwtConfig = new GwtLdapConnectionConfig();
    				gwtConfig.setId( config.getId() );
    				gwtConfig.setServerUrl( config.getUrl() );
    				gwtConfig.setProxyDn( config.getPrincipal() );
    				gwtConfig.setProxyPwd( config.getCredentials() );
    				gwtConfig.setLdapGuidAttribute( config.getLdapGuidAttribute() );
    				gwtConfig.setUserIdAttribute( config.getUserIdAttribute() );
    				gwtConfig.setUserAttributeMappings( config.getMappings() );
    				
    				// Add the user search info
    				{
    					List<SearchInfo> listOfSearches;
    					
    					listOfSearches = config.getUserSearches();
    					if ( listOfSearches != null )
    					{
    						for ( SearchInfo searchInfo : listOfSearches )
    						{
    							GwtLdapSearchInfo gwtSearchInfo;
    							
    							gwtSearchInfo = new GwtLdapSearchInfo();
    							gwtSearchInfo.setBaseDn( searchInfo.getBaseDn() );
    							gwtSearchInfo.setFilter( searchInfo.getFilter() );
    							//!!!gwtSearchInfo.setHomeDirConfig( homeDirConfig );
    							gwtSearchInfo.setSearchSubtree( searchInfo.isSearchSubtree() );
    							
    							gwtConfig.addUserSearchCriteria( gwtSearchInfo );
    						}
    					}
    				}
    				
    				// Add the group search info
    				{
    					List<SearchInfo> listOfSearches;
    					
    					listOfSearches = config.getGroupSearches();
    					if ( listOfSearches != null )
    					{
    						for ( SearchInfo searchInfo : listOfSearches )
    						{
    							GwtLdapSearchInfo gwtSearchInfo;
    							
    							gwtSearchInfo = new GwtLdapSearchInfo();
    							gwtSearchInfo.setBaseDn( searchInfo.getBaseDn() );
    							gwtSearchInfo.setFilter( searchInfo.getFilter() );
    							gwtSearchInfo.setSearchSubtree( searchInfo.isSearchSubtree() );
    							
    							gwtConfig.addGroupSearchCriteria( gwtSearchInfo );
    						}
    					}
    				}
    				
    				ldapConfig.addLdapConnectionConfig( gwtConfig );
    			}
    		}
    	}

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

	/**
	 * Set the default locale setting.  This setting is used to set the locale  on a user when
	 * the user is created from an ldap sync.
	 */
	private static void saveDefaultLocale(
		AllModulesInjected ami,
		String localeId )
	{
		Workspace	topWorkspace;
		
		if ( localeId == null || localeId.length() == 0 )
			return;
		
		// Get the top workspace.  That is where global properties are stored.
		topWorkspace = ami.getWorkspaceModule().getTopWorkspace();
		
		// Save the default locale id as a global property
		topWorkspace.setProperty( ObjectKeys.GLOBAL_PROPERTY_DEFAULT_LOCALE, localeId );
	}
	

	/**
	 * Set the default time zone setting.  This setting is used to set the time zone on a user when
	 * the user is created from an ldap sync.
	 */
	private static void saveDefaultTimeZone(
		AllModulesInjected ami,
		String timeZoneId )
	{
		Workspace	topWorkspace;
		
		if ( timeZoneId == null || timeZoneId.length() == 0 )
			return;
		
		// Get the top workspace.  That is where global properties are stored.
		topWorkspace = ami.getWorkspaceModule().getTopWorkspace();
		
		// Save the default time zone as a global property
		topWorkspace.setProperty( ObjectKeys.GLOBAL_PROPERTY_DEFAULT_TIME_ZONE, timeZoneId );
	}

	/**
	 * 
	 */
	public static SaveLdapConfigRpcResponseData saveLdapConfig(
		AllModulesInjected ami,
		GwtLdapConfig ldapConfig )
	{
		SaveLdapConfigRpcResponseData responseData;
		LdapSchedule schedule;
		
		responseData = new SaveLdapConfigRpcResponseData();
		
		schedule = ami.getLdapModule().getLdapSchedule();
		if ( schedule != null )
		{
			boolean syncAllUsersAndGroups;
			String listOfLdapConfigsToSyncGuid;
			
			// Get the user configuration
			{
				schedule.setUserRegister( ldapConfig.getRegisterUserProfilesAutomatically() );
				schedule.setUserSync( ldapConfig.getSyncUserProfiles() );
				schedule.setUserDelete( ldapConfig.getDeleteLdapUsers() );
				schedule.setUserWorkspaceDelete( ldapConfig.getDeleteUserWorkspace() );
			}

			// Get the group configuration
			{
				schedule.setGroupRegister( ldapConfig.getRegisterGroupProfilesAutomatically() );
				schedule.setGroupSync( ldapConfig.getSyncGroupProfiles() );
				schedule.setMembershipSync( ldapConfig.getSyncGroupMembership() );
				schedule.setGroupDelete( ldapConfig.getDeleteNonLdapGroups() );
			}
			
			// Get the sync schedule
			{
				ScheduleInfo scheduleInfo;
				
				scheduleInfo = GwtServerHelper.getScheduleInfoFromGwtSchedule( ldapConfig.getSchedule() );
				schedule.getScheduleInfo().setSchedule( scheduleInfo.getSchedule() );
				schedule.getScheduleInfo().setEnabled( scheduleInfo.isEnabled() );	
			}
			
			// Get the local user account configuration
			{
				AuthenticationConfig authConfig;

				authConfig = ami.getAuthenticationModule().getAuthenticationConfig();
				authConfig.setAllowLocalLogin( ldapConfig.getAllowLocalLogin() );
				ami.getAuthenticationModule().setAuthenticationConfig( authConfig );
			}

			// Save the ldap configuration info that is stored in the schedule
			ami.getLdapModule().setLdapSchedule( schedule );

			// Gather all the ldap server configurations into one list
			{
				ArrayList<GwtLdapConnectionConfig> listOfGwtLdapConnections;
				LinkedList<LdapConnectionConfig> listOfLdapConnections;

				listOfLdapConnections = new LinkedList<LdapConnectionConfig>();
				
				// Get the list of ldap connections
				listOfGwtLdapConnections = ldapConfig.getListOfLdapConnections();
				if ( listOfGwtLdapConnections != null )
				{
					for ( GwtLdapConnectionConfig nextGwtLdapConnection : listOfGwtLdapConnections )
					{
						LdapConnectionConfig ldapConnection;
						LinkedList<LdapConnectionConfig.SearchInfo> userQueries;
						LinkedList<LdapConnectionConfig.SearchInfo> groupQueries;
						String url;
						
						// If the protocol is uppercase, users can't log in.  See bug 823936.
						url = nextGwtLdapConnection.getServerUrl();
						if ( url != null )
							url = url.toLowerCase();

						// Gather all the user queries into one list
						{
							ArrayList<GwtLdapSearchInfo> listOfGwtUserQueries;

							userQueries = new LinkedList<LdapConnectionConfig.SearchInfo>();

							listOfGwtUserQueries = nextGwtLdapConnection.getListOfUserSearchCriteria();
							if ( listOfGwtUserQueries != null )
							{
								for ( GwtLdapSearchInfo nextSearch : listOfGwtUserQueries )
								{
									LdapConnectionConfig.SearchInfo searchInfo;
									
									searchInfo = new LdapConnectionConfig.SearchInfo(
																				nextSearch.getBaseDn(),
																				nextSearch.getFilter(),
																				nextSearch.getSearchSubtree() );
									userQueries.add( searchInfo );
								}
							}
						}
						
						// Gather all the group queries into one list
						{
							ArrayList<GwtLdapSearchInfo> listOfGwtGroupQueries;

							groupQueries = new LinkedList<LdapConnectionConfig.SearchInfo>();

							listOfGwtGroupQueries = nextGwtLdapConnection.getListOfGroupSearchCriteria();
							if ( listOfGwtGroupQueries != null )
							{
								for ( GwtLdapSearchInfo nextSearch : listOfGwtGroupQueries )
								{
									LdapConnectionConfig.SearchInfo searchInfo;
									
									searchInfo = new LdapConnectionConfig.SearchInfo(
																				nextSearch.getBaseDn(),
																				nextSearch.getFilter(),
																				nextSearch.getSearchSubtree() );
									groupQueries.add( searchInfo );
								}
							}
						}

						ldapConnection = new LdapConnectionConfig(
																url,
																nextGwtLdapConnection.getUserIdAttribute(),
																nextGwtLdapConnection.getUserAttributeMappings(),
																userQueries,
																groupQueries,
																nextGwtLdapConnection.getProxyDn(),
																nextGwtLdapConnection.getProxyPwd(),
																nextGwtLdapConnection.getLdapGuidAttribute() );
						ldapConnection.setId( nextGwtLdapConnection.getId() );
						listOfLdapConnections.add( ldapConnection );
					}
				}

				// Save the ldap server configurations
				ami.getAuthenticationModule().setLdapConnectionConfigs( listOfLdapConnections );
			}
			
			// Get the time zone
			saveDefaultTimeZone( ami, ldapConfig.getTimeZone() );
			
			// Save the selected locale
			saveDefaultLocale( ami, ldapConfig.getLocale() );
			
		//!!! Finish
		/**
			// Does the user want to sync all users and groups?
			syncAllUsersAndGroups = PortletRequestUtils.getBooleanParameter(request, "runnow", false);

			// Get the list of ldap configs that we need to sync the guid
			listOfLdapConfigsToSyncGuid = PortletRequestUtils.getStringParameter( request, "listOfLdapConfigsToSyncGuid", "" );
			
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
		**/
		}
		
		return responseData;
	}
}
