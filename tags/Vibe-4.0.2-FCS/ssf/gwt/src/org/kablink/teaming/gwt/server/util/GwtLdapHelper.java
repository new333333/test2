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
package org.kablink.teaming.gwt.server.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Element;

import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.LdapConnectionConfig.HomeDirConfig;
import org.kablink.teaming.domain.LdapConnectionConfig.HomeDirCreationOption;
import org.kablink.teaming.domain.LdapConnectionConfig.SearchInfo;
import org.kablink.teaming.gwt.client.GwtADLdapObject;
import org.kablink.teaming.gwt.client.GwtHomeDirConfig;
import org.kablink.teaming.gwt.client.GwtHomeDirConfig.GwtHomeDirCreationOption;
import org.kablink.teaming.gwt.client.GwtLdapConfig;
import org.kablink.teaming.gwt.client.GwtLdapConnectionConfig;
import org.kablink.teaming.gwt.client.GwtLdapSearchInfo;
import org.kablink.teaming.gwt.client.GwtLdapSyncResult.GwtEntityType;
import org.kablink.teaming.gwt.client.GwtLdapSyncResult.GwtLdapSyncAction;
import org.kablink.teaming.gwt.client.GwtLdapSyncResults;
import org.kablink.teaming.gwt.client.GwtLdapSyncResults.GwtLdapSyncError;
import org.kablink.teaming.gwt.client.GwtLdapSyncResults.GwtLdapSyncStatus;
import org.kablink.teaming.gwt.client.GwtSchedule;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SaveLdapConfigRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StartLdapSyncRpcResponseData;
import org.kablink.teaming.gwt.client.widgets.EditLdapConfigDlg.GwtLdapSyncMode;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.ldap.ADLdapObject;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.ldap.LdapModule.LdapSyncMode;
import org.kablink.teaming.module.ldap.LdapSchedule;
import org.kablink.teaming.module.ldap.LdapSyncResults;
import org.kablink.teaming.module.ldap.LdapSyncResults.PartialLdapSyncResults;
import org.kablink.teaming.module.ldap.LdapSyncResults.SyncStatus;
import org.kablink.teaming.module.ldap.LdapSyncThread;
import org.kablink.teaming.util.AllModulesInjected;
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
	 * 
	 */
	private static void copyLdapSyncResults( GwtLdapSyncResults gwtLdapSyncResults, LdapSyncResults ldapSyncResults )
	{
		PartialLdapSyncResults partialSyncResults;

		partialSyncResults = ldapSyncResults.getAddedGroups();
		gwtLdapSyncResults.addLdapSyncResults(
									GwtLdapSyncAction.ADDED_ENTITY,
									GwtEntityType.GROUP,
									partialSyncResults.getResults() );
		
		partialSyncResults = ldapSyncResults.getAddedUsers();
		gwtLdapSyncResults.addLdapSyncResults(
									GwtLdapSyncAction.ADDED_ENTITY,
									GwtEntityType.USER,
									partialSyncResults.getResults() );
		
		partialSyncResults = ldapSyncResults.getDeletedGroups();
		gwtLdapSyncResults.addLdapSyncResults(
									GwtLdapSyncAction.DELETED_ENTITY,
									GwtEntityType.GROUP,
									partialSyncResults.getResults() );
		
		partialSyncResults = ldapSyncResults.getDeletedUsers();
		gwtLdapSyncResults.addLdapSyncResults(
									GwtLdapSyncAction.DELETED_ENTITY,
									GwtEntityType.USER,
									partialSyncResults.getResults() );
		
		partialSyncResults = ldapSyncResults.getDisabledUsers();
		gwtLdapSyncResults.addLdapSyncResults(
									GwtLdapSyncAction.DISABLED_ENTITY,
									GwtEntityType.USER,
									partialSyncResults.getResults() );
		
		partialSyncResults = ldapSyncResults.getModifiedGroups();
		gwtLdapSyncResults.addLdapSyncResults(
									GwtLdapSyncAction.MODIFIED_ENTITY,
									GwtEntityType.GROUP,
									partialSyncResults.getResults() );
		
		partialSyncResults = ldapSyncResults.getModifiedUsers();
		gwtLdapSyncResults.addLdapSyncResults(
									GwtLdapSyncAction.MODIFIED_ENTITY,
									GwtEntityType.USER,
									partialSyncResults.getResults() );
		
		gwtLdapSyncResults.setSyncStatus( GwtLdapSyncStatus.STATUS_ABORTED_BY_ERROR );
		if ( ldapSyncResults.getStatus() != null )
		{
			switch ( ldapSyncResults.getStatus() )
			{
			case STATUS_ABORTED_BY_ERROR:
				gwtLdapSyncResults.setSyncStatus( GwtLdapSyncStatus.STATUS_ABORTED_BY_ERROR );
				gwtLdapSyncResults.setErrorDesc( ldapSyncResults.getErrorDesc() );
				gwtLdapSyncResults.setErrorLdapServerId( ldapSyncResults.getErrorLdapConfigId() );
				break;
				
			case STATUS_COLLECT_RESULTS:
				gwtLdapSyncResults.setSyncStatus( GwtLdapSyncStatus.STATUS_IN_PROGRESS );
				break;
				
			case STATUS_COMPLETED:
				gwtLdapSyncResults.setSyncStatus( GwtLdapSyncStatus.STATUS_COMPLETED );
				break;
				
			case STATUS_STOP_COLLECTING_RESULTS:
				gwtLdapSyncResults.setSyncStatus( GwtLdapSyncStatus.STATUS_STOP_COLLECTING_RESULTS );
				break;
				
			case STATUS_SYNC_ALREADY_IN_PROGRESS:
				gwtLdapSyncResults.setSyncStatus( GwtLdapSyncStatus.STATUS_SYNC_ALREADY_IN_PROGRESS );
				break;
			}
		}
		else
			m_logger.error( "In GwtLdapHelper.copyLdapSyncResults(), getStatus() returned null" );
			
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
    		else
    		{
	    		ldapConfig.setSyncUserProfiles( true );
	    		ldapConfig.setRegisterUserProfilesAutomatically( true );
    		}
    	}
    	
    	// Add the ldap servers
    	{
    		List<LdapConnectionConfig> listOfLdapServers;
    		
    		listOfLdapServers = ami.getAuthenticationModule().getLdapConnectionConfigs();
    		if ( listOfLdapServers != null )
    		{
    			boolean supportsExternalUsers = ami.getLdapModule().getLdapSupportsExternalUserImport();
    			for ( LdapConnectionConfig config : listOfLdapServers )
    			{
    				// If don't support external user imports and we've
    				// got an external user configuration...
    				if ((!supportsExternalUsers) && config.getImportUsersAsExternalUsers()) {
    					// ...ignore it.
    					continue;
    				}
    				
    				GwtLdapConnectionConfig gwtConfig = new GwtLdapConnectionConfig();
    				gwtConfig.setId( config.getId() );
    				gwtConfig.setServerUrl( config.getUrl() );
    				gwtConfig.setProxyDn( config.getPrincipal() );
    				gwtConfig.setProxyPwd( config.getCredentials() );
    				gwtConfig.setOrigLdapGuidAttribute( config.getLdapGuidAttribute() );
    				gwtConfig.setLdapGuidAttribute( config.getLdapGuidAttribute() );
    				gwtConfig.setUserIdAttribute( config.getUserIdAttribute() );
    				gwtConfig.setUserAttributeMappings( config.getMappings() );
    				gwtConfig.setImportUsersAsExternalUsers( config.getImportUsersAsExternalUsers() );
    				
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
    							
    							// Get the home directory net folder creation configuration
    							{
    								HomeDirConfig homeDirConfig;
    								GwtHomeDirConfig gwtHomeDirConfig;

    								gwtHomeDirConfig = new GwtHomeDirConfig();
    								gwtHomeDirConfig.setCreationOption( GwtHomeDirCreationOption.USE_HOME_DIRECTORY_ATTRIBUTE );
    								
    								homeDirConfig = searchInfo.getHomeDirConfig();
    								if ( homeDirConfig != null )
    								{
        								switch( homeDirConfig.getCreationOption() )
        								{
        								case USE_CUSTOM_CONFIG:
            								gwtHomeDirConfig.setCreationOption( GwtHomeDirCreationOption.USE_CUSTOM_CONFIG );
            								gwtHomeDirConfig.setNetFolderPath( homeDirConfig.getPath() );
            								gwtHomeDirConfig.setNetFolderServerName( homeDirConfig.getNetFolderServerName() );
        									break;
        								
        								case USE_CUSTOM_ATTRIBUTE:
            								gwtHomeDirConfig.setCreationOption( GwtHomeDirCreationOption.USE_CUSTOM_ATTRIBUTE );
            								gwtHomeDirConfig.setAttributeName( homeDirConfig.getAttributeName() );
        									break;
        								
        								case DONT_CREATE_HOME_DIR_NET_FOLDER:
            								gwtHomeDirConfig.setCreationOption( GwtHomeDirCreationOption.DONT_CREATE_HOME_DIR_NET_FOLDER );
            								break;
            								
        								case USE_HOME_DIRECTORY_ATTRIBUTE:
        								case UNKNOWN:
        								default:
            								gwtHomeDirConfig.setCreationOption( GwtHomeDirCreationOption.USE_HOME_DIRECTORY_ATTRIBUTE );
        									break;
        								}
    								}

    								gwtSearchInfo.setHomeDirConfig( gwtHomeDirConfig );
    							}
    							
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
		timeZone = ami.getLdapModule().getDefaultTimeZone();
		ldapConfig.setTimeZone( timeZone );
		
		// Add the default locale to the response.
		defaultLocaleId = ami.getLdapModule().getDefaultLocaleId();
		ldapConfig.setLocale( defaultLocaleId );

		return ldapConfig;
	}
	
	/**
	 * Return a GwtADLdapObject for the given fqdn
	 */
	public static GwtADLdapObject getLdapObjectFromAD(
		AllModulesInjected ami,
		String fqdn )
	{
		GwtADLdapObject gwtLdapObj = null;
		
		if ( fqdn == null || fqdn.length() == 0 )
			return null;
		
		try
		{
			ADLdapObject ldapObj; 

			ldapObj = ami.getLdapModule().getLdapObjectFromAD( fqdn );
			if ( ldapObj != null )
			{
				gwtLdapObj = getGwtADLdapObjectFromADLdapObject( ldapObj );
			}
		}
		catch ( Exception ex )
		{
			m_logger.error( "in getLdapObjectFromAD()", ex );
		}
		
		return gwtLdapObj;
	}
	
	/*
	 */
	private static GwtADLdapObject getGwtADLdapObjectFromADLdapObject( ADLdapObject ldapObj )
	{
		GwtADLdapObject gwtLdapObj;
		
		if ( ldapObj == null )
			return null;
		
		gwtLdapObj = new GwtADLdapObject();
		gwtLdapObj.setDomainName( ldapObj.getDomainName() );
		gwtLdapObj.setFQDN( ldapObj.getFQDN() );
		gwtLdapObj.setNetbiosName( ldapObj.getNetbiosName() );
		gwtLdapObj.setSamAccountName( ldapObj.getSamAccountName() );

		return gwtLdapObj;
	}
	
	/**
	 * Return true if importing users as external users is supported
	 * and false otherwise.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData getLdapSupportsExternalUserImport(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			BooleanRpcResponseData reply = new BooleanRpcResponseData();
			reply.setBooleanValue(Boolean.valueOf(bs.getLdapModule().getLdapSupportsExternalUserImport()));
			return reply;
		}
		
		catch (Exception e) {
			GwtLogHelper.error(m_logger, "GwtLdapHelper.getLdapSupportsExternalUserImport( SOURCE EXCEPTION ):  ", e);
			throw GwtLogHelper.getGwtClientException(m_logger, e);
		}
	}
	
	/**
	 * Get the ldap sync results that have happened since we last retrieved the results.
	 */
	public static GwtLdapSyncResults getLdapSyncResults(
		HttpServletRequest request,
		String syncId )
	{
		GwtLdapSyncResults gwtSyncResults;
		LdapSyncResults syncResults;
		SyncStatus syncStatus;

		gwtSyncResults = new GwtLdapSyncResults();
		if ( syncId == null || syncId.length() == 0 )
		{
			gwtSyncResults.setSyncError( GwtLdapSyncError.SYNC_ID_IS_NULL );
			gwtSyncResults.setSyncStatus( GwtLdapSyncStatus.STATUS_ABORTED_BY_ERROR );
			m_logger.error( "in getLdapSyncResults(), syncId is empty" );
			return gwtSyncResults;
		}
		
		// Get the ldap sync results.
		syncResults = LdapSyncThread.getLdapSyncResults( request, syncId );
		
		if ( syncResults == null )
		{
			// If we can't find a sync results it means that the ldap sync has finished.
			gwtSyncResults.setSyncStatus( GwtLdapSyncStatus.STATUS_COMPLETED );
			m_logger.info( "in GwtLdapHelper.getLdapSyncResults(), LdapSyncThread.getLdapSyncResults() returned null.  syncId: " + syncId );
			return gwtSyncResults;
		}
		
		copyLdapSyncResults( gwtSyncResults, syncResults );
		
		// Clear the results so we start fresh.
		syncResults.clearResults();

		// Remove the LdapSyncThread from the session if the sync is finished.
		syncStatus = syncResults.getStatus();
		if ( syncStatus != null )
		{
			switch ( syncStatus )
			{
			case STATUS_SYNC_ALREADY_IN_PROGRESS:
			case STATUS_ABORTED_BY_ERROR:
			case STATUS_COMPLETED:
			{
				LdapSyncThread	syncThread;

				// Get the ldap sync thread object we are looking for.
				syncThread = LdapSyncThread.getLdapSyncThread( request, syncId );

				// Remove the ldap sync thread from the session.  This won't stop the ldap sync.  Just cleaning up.
				if ( syncThread != null )
					syncThread.removeFromSession();
				break;
			}
				
			case STATUS_COLLECT_RESULTS:
			case STATUS_STOP_COLLECTING_RESULTS:
			default:
				break;
			}
		}
		
		return gwtSyncResults;
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
									GwtHomeDirConfig gwtHomeDirConfig;
									
									searchInfo = new LdapConnectionConfig.SearchInfo(
																				nextSearch.getBaseDn(),
																				nextSearch.getFilter(),
																				nextSearch.getSearchSubtree() );
									
									// Do we have home dir configuration?
									gwtHomeDirConfig = nextSearch.getHomeDirConfig();
									if ( gwtHomeDirConfig != null )
									{
										HomeDirConfig homeDirConfig;

										// Yes
										homeDirConfig = new HomeDirConfig();
										
										switch ( gwtHomeDirConfig.getCreationOption() )
										{
										case USE_CUSTOM_CONFIG:
											homeDirConfig.setCreationOption( HomeDirCreationOption.USE_CUSTOM_CONFIG );
											homeDirConfig.setNetFolderServerName( gwtHomeDirConfig.getNetFolderServerName() );
											homeDirConfig.setPath( gwtHomeDirConfig.getNetFolderPath() );
											break;
										
										case USE_CUSTOM_ATTRIBUTE:
											homeDirConfig.setCreationOption( HomeDirCreationOption.USE_CUSTOM_ATTRIBUTE );
											homeDirConfig.setAttributeName( gwtHomeDirConfig.getAttributeName() );
											break;
											
										case DONT_CREATE_HOME_DIR_NET_FOLDER:
											homeDirConfig.setCreationOption( HomeDirCreationOption.DONT_CREATE_HOME_DIR_NET_FOLDER );
											break;
											
										case USE_HOME_DIRECTORY_ATTRIBUTE:
										case UNKNOWN:
										default:
											homeDirConfig.setCreationOption( HomeDirCreationOption.USE_HOME_DIRECTORY_ATTRIBUTE );
											break;
										}
										
										searchInfo.setHomeDirConfig( homeDirConfig );
									}
									
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
																nextGwtLdapConnection.getLdapGuidAttribute(),
																nextGwtLdapConnection.isImportUsersAsExternalUsers() );
						ldapConnection.setId( nextGwtLdapConnection.getId() );
						listOfLdapConnections.add( ldapConnection );
					}
				}

				// Save the ldap server configurations
				ami.getAuthenticationModule().setLdapConnectionConfigs( listOfLdapConnections );
			}
			
			// Get the time zone
            ami.getLdapModule().setDefaultTimeZone(ldapConfig.getTimeZone() );
			
			// Save the selected locale
            ami.getLdapModule().setDefaultLocale(ldapConfig.getLocale());
		}
		
		return responseData;
	}
	
	/**
	 * Start an ldap sync
	 */
	public static StartLdapSyncRpcResponseData startLdapSync(
		AllModulesInjected ami,
		HttpServletRequest request,
		String syncId,
		boolean syncUsersAndGroups,
		String[] listOfLdapConfigsToSyncGuid,
		ArrayList<GwtLdapConnectionConfig> listOfLdapServers,
		GwtLdapSyncMode gwtSyncMode )
	{
		StartLdapSyncRpcResponseData response;
		LdapSyncResults syncResults;
		LdapSyncThread	ldapSyncThread;
		LdapModule		ldapModule;
		LdapSyncMode syncMode;
		
		response = new StartLdapSyncRpcResponseData();

		syncMode = LdapSyncMode.PERFORM_SYNC;
		if ( gwtSyncMode == GwtLdapSyncMode.PREVIEW_ONLY )
			syncMode = LdapSyncMode.PREVIEW_ONLY;
		
		ldapModule = ami.getLdapModule();

		// Create an LdapSyncThread object that will do the sync work.
		// Currently doing the sync on a separate thread does not work.  When doing work on a separate thread
		// works, replace the call to doLdapSync() with start().
		ldapSyncThread = LdapSyncThread.createLdapSyncThread(
														request,
														syncId,
														ldapModule,
														syncUsersAndGroups,
														listOfLdapConfigsToSyncGuid,
														syncMode );
		if ( ldapSyncThread != null )
		{
			GwtLdapSyncResults gwtLdapSyncResults;
			
			syncResults = ldapSyncThread.doLdapSync();
			
			gwtLdapSyncResults = new GwtLdapSyncResults();
			copyLdapSyncResults( gwtLdapSyncResults, syncResults );
			
			response.setLdapSyncResults( gwtLdapSyncResults );
		}
		
		return response;
	}
}
