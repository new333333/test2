/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.ResourceDriverConfig.DriverType;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtSchedule;
import org.kablink.teaming.gwt.client.GwtSchedule.DayFrequency;
import org.kablink.teaming.gwt.client.GwtSchedule.TimeFrequency;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.NetFolder;
import org.kablink.teaming.gwt.client.NetFolderRoot;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.NetFolder.NetFolderStatus;
import org.kablink.teaming.gwt.client.widgets.ModifyNetFolderRootDlg.NetFolderRootType;
import org.kablink.teaming.jobs.Schedule;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.resourcedriver.RDException;
import org.kablink.teaming.module.resourcedriver.ResourceDriverModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.StatusTicket;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Criterion;
import org.kablink.util.search.Order;
import org.kablink.util.search.Restrictions;


/**
 * Helper methods for the GWT UI server code that services requests dealing with
 * net folder roots and net folders.
 *
 * @author jwootton@novell.com
 */
public class GwtNetFolderHelper 
{
	protected static Log m_logger = LogFactory.getLog( GwtNetFolderHelper.class );

	
	/**
	 * Create a new net folder from the given data
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static NetFolder createNetFolder(
		AllModulesInjected ami,
		NetFolder netFolder ) throws GwtTeamingException
	{
		NetFolder newNetFolder = null;
		
		try
		{
			Binder binder;
			Long templateId = null;
			List<TemplateBinder> listOfTemplateBinders;
			
			// Find the template binder for mirrored folders.
			listOfTemplateBinders = ami.getTemplateModule().getTemplates(Boolean.TRUE);
			if ( listOfTemplateBinders != null )
			{
				for ( TemplateBinder nextTemplateBinder : listOfTemplateBinders )
				{
					String internalId;
					
					internalId = nextTemplateBinder.getInternalId();
					if ( internalId != null && internalId.equalsIgnoreCase( ObjectKeys.DEFAULT_FOLDER_FILR_ROOT_CONFIG ) )
					{
						templateId = nextTemplateBinder.getId();
						break;
					}
				}
			}

			if ( templateId != null )
			{
				// Create the binder
				binder = ami.getTemplateModule().addBinder(
														templateId,
														netFolder.getParentBinderId(),
														netFolder.getName(),
														netFolder.getName() );
				
				// Modify the binder with the additional net folder information.
				{
					Set deleteAtts;
					Map fileMap = null;
					MapInputData mid;
	   				Map formData = null;
					
					deleteAtts = new HashSet();
					fileMap = new HashMap();
	   				formData = new HashMap();
			   		formData.put( ObjectKeys.FIELD_BINDER_LIBRARY, "true" );
			   		formData.put( ObjectKeys.FIELD_BINDER_MIRRORED, "true" );
			   		formData.put( ObjectKeys.FIELD_BINDER_RESOURCE_DRIVER_NAME, netFolder.getNetFolderRootName() );
			   		formData.put( ObjectKeys.FIELD_BINDER_RESOURCE_PATH, netFolder.getRelativePath() );
	   				mid = new MapInputData( formData );
	
		   			ami.getBinderModule().modifyBinder( binder.getId(), mid, fileMap, deleteAtts, null );				
				}
				
				// Set the net folder's sync schedule
				{
					ScheduleInfo scheduleInfo;
					
					scheduleInfo = getScheduleInfoFromGwtSchedule( netFolder.getSyncSchedule() );
					if ( scheduleInfo != null )
					{
						scheduleInfo.setFolderId( binder.getId() );
						ami.getFolderModule().setSynchronizationSchedule( scheduleInfo, binder.getId() );
					}
				}
				
				newNetFolder = new NetFolder();
				newNetFolder.setName( netFolder.getName() );
				newNetFolder.setNetFolderRootName( netFolder.getNetFolderRootName() );
				newNetFolder.setRelativePath( netFolder.getRelativePath() );
				newNetFolder.setParentBinderId( netFolder.getParentBinderId() );
				newNetFolder.setId( binder.getId() );
				newNetFolder.setStatus( NetFolderStatus.READY );
				newNetFolder.setSyncSchedule( netFolder.getSyncSchedule() );
			}
			else
				throw new GwtTeamingException( ExceptionType.UNKNOWN, "Could not find the template binder for a mirrored folder" );
		}
		catch ( Exception ex )
		{
			GwtTeamingException gtEx;
			
			gtEx = GwtServerHelper.getGwtTeamingException( ex );
			throw gtEx;				
		}
		
		return newNetFolder;
	}
	
	
	/**
	 * Create a new net folder root from the given data
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static NetFolderRoot createNetFolderRoot(
		AllModulesInjected ami,
		NetFolderRoot netFolderRoot ) throws GwtTeamingException
	{
		Map options;
		ResourceDriverConfig rdConfig = null;
		NetFolderRoot newRoot = null;
		
		ami.getAdminModule().checkAccess( AdminOperation.manageResourceDrivers );

		options = new HashMap();
		options.put( ObjectKeys.RESOURCE_DRIVER_READ_ONLY, Boolean.FALSE );
		options.put( ObjectKeys.RESOURCE_DRIVER_ACCOUNT_NAME, netFolderRoot.getProxyName() ); 
		options.put( ObjectKeys.RESOURCE_DRIVER_PASSWORD, netFolderRoot.getProxyPwd() );
		
		// Is the root type WebDAV?
		if ( netFolderRoot.getRootType() == NetFolderRootType.WEB_DAV )
		{
			// Yes, get the WebDAV specific values
			options.put(
					ObjectKeys.RESOURCE_DRIVER_HOST_URL,
					netFolderRoot.getHostUrl() );
			options.put(
					ObjectKeys.RESOURCE_DRIVER_ALLOW_SELF_SIGNED_CERTIFICATE,
					netFolderRoot.getAllowSelfSignedCerts() );
			options.put(
					ObjectKeys.RESOURCE_DRIVER_PUT_REQUIRES_CONTENT_LENGTH,
					netFolderRoot.getIsSharePointServer() );
		}

		// Always prevent the top level folder from being deleted
		// This is forced so that the folder could not accidentally be deleted if the 
		// external disk was offline
		options.put( ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE, Boolean.FALSE );

		//Add this resource driver
		try
		{
			DriverType driverType;
			
			driverType = getDriverType( netFolderRoot.getRootType() );
			rdConfig = ami.getResourceDriverModule().addResourceDriver(
															netFolderRoot.getName(),
															driverType, 
															netFolderRoot.getRootPath(),
															netFolderRoot.getListOfPrincipalIds(),
															options );
			if ( rdConfig != null )
			{
				newRoot = new NetFolderRoot();
				newRoot.setId( rdConfig.getId() );
				newRoot.setName( rdConfig.getName() );
				newRoot.setProxyName( rdConfig.getAccountName() );
				newRoot.setProxyPwd( rdConfig.getPassword() );
				newRoot.setRootPath( rdConfig.getRootPath() );
				newRoot.setHostUrl( rdConfig.getHostUrl() );
				newRoot.setAllowSelfSignedCerts( rdConfig.isAllowSelfSignedCertificate() );
				newRoot.setIsSharePointServer( rdConfig.isPutRequiresContentLength() );

				// Get the list of principals that can use the net folder root
				getListOfPrincipals( ami, rdConfig, newRoot );
			}
		}
		catch (Exception ex)
		{
			GwtTeamingException gtEx;
			
			gtEx = GwtServerHelper.getGwtTeamingException( ex );
			throw gtEx;				
		}
		
		return newRoot;
	}
	
	/**
	 * Delete the given list of net folders
	 */
	public static Boolean deleteNetFolders(
		AllModulesInjected ami,
		Set<NetFolder> netFolders ) throws GwtTeamingException
	{
		Boolean result;
		
		result = Boolean.TRUE;
		
		for ( NetFolder nextNetFolder : netFolders )
		{
			try
			{
				boolean deleteSource = false;
				
				ami.getBinderModule().deleteBinder(
												nextNetFolder.getId(),
												deleteSource,
												null );
			}
			catch ( Exception e )
			{
				GwtTeamingException gwtEx;
				
				m_logger.error( "Error deleting next net folder: " + nextNetFolder.getName() + ", " + e.toString() );
				
				gwtEx = GwtServerHelper.getGwtTeamingException( e );
				throw gwtEx;
			}
		}
		
		return result;
	}

	/**
	 * Delete the given list of net folder roots
	 */
	public static Boolean deleteNetFolderRoots(
		AllModulesInjected ami,
		Set<NetFolderRoot> netFolderRoots )
	{
		Boolean result;
		ResourceDriverModule rdModule;
		
		ami.getAdminModule().checkAccess( AdminOperation.manageResourceDrivers );

		result = Boolean.TRUE;
		rdModule = ami.getResourceDriverModule();
		
		for ( NetFolderRoot nextRoot : netFolderRoots )
		{
			try
			{
				rdModule.deleteResourceDriver( nextRoot.getName() );
			}
			catch ( RDException rde )
			{
				m_logger.error( "Error deleting next folder root: " + nextRoot.getName() + ", " + rde.toString() );
			}
		}
		
		return result;
	}

	/**
	 * Return a list of all the net folders 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<NetFolder> getAllNetFolders( AllModulesInjected ami )
	{
		Criteria criteria;
		Criterion criterion;
		String topWSId;
		int start;
		int maxHits;
		boolean sortAscend;
		String  sortBy;
		List<Map> searchEntries;
		Map searchResults;
		BinderModule binderModule;
		ArrayList<NetFolder> listOfNetFolders;
		
		listOfNetFolders = new ArrayList<NetFolder>();
		
		// Can we access the ID of the top workspace?
		topWSId = GwtUIHelper.getTopWSIdSafely( ami, false );
		if ( MiscUtil.hasString( topWSId ) == false )
		{
			// No!  Then we can't search for net folders.  Bail.
			return listOfNetFolders;
		}

		binderModule = ami.getBinderModule();
		
		criteria = new Criteria();

		start = 0;
		maxHits = ObjectKeys.SEARCH_MAX_HITS_LIMIT;

		// Add the criteria for top level net folders that have been configured.
		criterion = Restrictions.in( Constants.DOC_TYPE_FIELD, new String[]{Constants.DOC_TYPE_BINDER} );
		criteria.add( criterion );
		criterion = Restrictions.in(Constants.ENTRY_ANCESTRY, new String[]{topWSId} );
		criteria.add( criterion );
		criterion = Restrictions.in(Constants.FAMILY_FIELD, new String[]{Definition.FAMILY_FILE} );
		criteria.add( criterion );
		criterion = Restrictions.in(Constants.IS_MIRRORED_FIELD, new String[]{Constants.TRUE} );
		criteria.add( criterion );
		criterion = Restrictions.in(Constants.HAS_RESOURCE_DRIVER_FIELD, new String[]{Constants.TRUE} );
		criteria.add( criterion );

		// Add in the sort information...
		sortAscend = false;
		sortBy = Constants.SORT_TITLE_FIELD;
		criteria.addOrder( new Order( Constants.ENTITY_FIELD, sortAscend ) );
		criteria.addOrder( new Order( sortBy, sortAscend ) );

		searchResults = binderModule.executeSearchQuery(
													criteria,
													Constants.SEARCH_MODE_NORMAL,
													start,
													maxHits );
		searchEntries = ((List<Map>) searchResults.get( ObjectKeys.SEARCH_ENTRIES ) );
		//totalRecords = ((Integer) searchResults.get( ObjectKeys.SEARCH_COUNT_TOTAL ) ).intValue();
		
		for ( Map entryMap:  searchEntries )
		{
			NetFolder netFolder;
			String binderId;
			
			binderId = GwtServerHelper.getStringFromEntryMap( entryMap, Constants.DOCID_FIELD );
			netFolder = GwtNetFolderHelper.getNetFolder( ami, Long.valueOf( binderId ) );
			
			listOfNetFolders.add( netFolder );
		}
		
		return listOfNetFolders;
	}
	
	/**
	 * Return a list of all the net folder roots
	 */
	public static List<NetFolderRoot> getAllNetFolderRoots( AllModulesInjected ami )
	{
		List<NetFolderRoot> listOfNetFolderRoots;
		AdminModule adminModule;
		
		listOfNetFolderRoots = new ArrayList<NetFolderRoot>();
		adminModule = ami.getAdminModule();
		
		if ( adminModule.testAccess( AdminOperation.manageResourceDrivers ) )
		{
			List<ResourceDriverConfig> drivers;

			//Get a list of the currently defined Net Folder Roots
			drivers = ami.getResourceDriverModule().getAllResourceDriverConfigs();
			for ( ResourceDriverConfig driver : drivers )
			{
				NetFolderRoot nfRoot;
				DriverType driverType;
				
				nfRoot = new NetFolderRoot();
				nfRoot.setId( driver.getId() );
				nfRoot.setName( driver.getName() );
				
				driverType = driver.getDriverType();
				if ( driverType == DriverType.filesystem )
					nfRoot.setRootType( NetFolderRootType.FILE_SYSTEM );
				else if ( driverType == DriverType.webdav )
					nfRoot.setRootType( NetFolderRootType.WEB_DAV );
				else if ( driverType == DriverType.famt )
					nfRoot.setRootType( NetFolderRootType.FAMT );
				else
					nfRoot.setRootType( NetFolderRootType.UNKNOWN );
				
				nfRoot.setRootPath( driver.getRootPath() );
				nfRoot.setProxyName( driver.getAccountName() );
				nfRoot.setProxyPwd( driver.getPassword() );
				nfRoot.setHostUrl( driver.getHostUrl() );
				nfRoot.setAllowSelfSignedCerts( driver.isAllowSelfSignedCertificate() );
				nfRoot.setIsSharePointServer( driver.isPutRequiresContentLength() );
				
				// Get the list of principals that can use the net folder root
				getListOfPrincipals( ami, driver, nfRoot );
				
				listOfNetFolderRoots.add( nfRoot );
			}
		}
		
		return listOfNetFolderRoots;
	}
	
	/**
	 * Return the appropriate DriverType from the given NetFolderRootType
	 */
	private static DriverType getDriverType( NetFolderRootType type )
	{
		if ( type == NetFolderRootType.FILE_SYSTEM )
			return DriverType.filesystem;
		
		if ( type == NetFolderRootType.WEB_DAV )
			return DriverType.webdav;
		
		if ( type == NetFolderRootType.FAMT )
			return DriverType.famt;
		
		return DriverType.famt;
	}

	/**
	 * For the given Binder, return a GwtSchedule object that represents the binder's
	 * sync schedule.
	 */
	private static GwtSchedule getGwtSyncSchedule(
		AllModulesInjected ami,
		Binder binder )
	{
		Long zoneId;
		ScheduleInfo scheduleInfo;
		GwtSchedule gwtSchedule;
		
		if ( binder == null )
			return null;
		
		gwtSchedule = new GwtSchedule();
		
		// Get the ScheduleInfo for the given binder.
		zoneId = RequestContextHolder.getRequestContext().getZoneId();
		scheduleInfo = ami.getFolderModule().getSynchronizationSchedule( zoneId, binder.getId() );
		
		if ( scheduleInfo != null )
		{
			Schedule schedule;

			gwtSchedule.setEnabled( scheduleInfo.isEnabled() );
			
			schedule = scheduleInfo.getSchedule();
			if ( schedule != null )
			{
				if ( schedule.isDaily() )
				{
					gwtSchedule.setDayFrequency( DayFrequency.EVERY_DAY );
				}
				else
				{
					gwtSchedule.setDayFrequency( DayFrequency.ON_SELECTED_DAYS );
					gwtSchedule.setOnMonday( schedule.isOnMonday() );
					gwtSchedule.setOnTuesday( schedule.isOnTuesday() );
					gwtSchedule.setOnWednesday( schedule.isOnWednesday() );
					gwtSchedule.setOnThursday( schedule.isOnThursday() );
					gwtSchedule.setOnFriday( schedule.isOnFriday() );
					gwtSchedule.setOnSaturday( schedule.isOnSaturday() );
					gwtSchedule.setOnSunday( schedule.isOnSunday() );
				}
				
				if ( schedule.isRepeatMinutes() )
				{
					int minutes;
					
					gwtSchedule.setTimeFrequency( TimeFrequency.REPEAT_EVERY_MINUTE );
					minutes = Integer.valueOf( schedule.getMinutesRepeat() );
					gwtSchedule.setRepeatEveryValue( minutes );
				}
				else if ( schedule.isRepeatHours() )
				{
					int hours;
					
					gwtSchedule.setTimeFrequency( TimeFrequency.REPEAT_EVERY_HOUR );
					hours = Integer.valueOf( schedule.getHoursRepeat() );
					gwtSchedule.setRepeatEveryValue( hours );
				}
				else
				{
					int minutes;
					int hours;
					
					gwtSchedule.setTimeFrequency( TimeFrequency.AT_SPECIFIC_TIME );
					
					minutes = Integer.valueOf( schedule.getMinutes() );
					gwtSchedule.setAtMinutes( minutes );
					
					hours = Integer.valueOf( schedule.getHours() );
					gwtSchedule.setAtHours( hours );
				}
			}
		}
		
		return gwtSchedule;
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static void getListOfPrincipals(
		AllModulesInjected ami,
		ResourceDriverConfig driver,
		NetFolderRoot nfRoot )
	{
		List<Function> functions;
		List<WorkAreaFunctionMembership> memberships;
		WorkAreaFunctionMembership membership = null;
		List<Principal> members;
		AdminModule adminModule;

		adminModule = ami.getAdminModule();
		functions = adminModule.getFunctions( ObjectKeys.ROLE_TYPE_ZONE );
		memberships = adminModule.getWorkAreaFunctionMemberships( driver );
		membership = null;
		for ( Function f : functions )
		{
			if ( ObjectKeys.FUNCTION_CREATE_FILESPACES_INTERNALID.equals( f.getInternalId() ) )
			{
				for ( WorkAreaFunctionMembership m : memberships )
				{
					if ( f.getId().equals( m.getFunctionId() ) )
					{
						membership = m;
						break;
					}
				}
			}
		}
		
		members = new ArrayList<Principal>();

		if ( membership != null )
		{
			members = ResolveIds.getPrincipals( membership.getMemberIds() );
		}
		
		for ( Principal p : members ) 
		{
			if ( p instanceof User )
			{
				GwtUser gwtUser;
				
				gwtUser = new GwtUser();
				gwtUser.setUserId( p.getId() );
				gwtUser.setName( p.getName() );
				gwtUser.setTitle( Utils.getUserTitle( p ) );
				gwtUser.setWorkspaceTitle( ((User)p).getWSTitle() );
				
				nfRoot.addPrincipal( gwtUser );
			}
			else if ( p instanceof Group )
			{
				GwtGroup gwtGroup;
				
				gwtGroup = new GwtGroup();
				gwtGroup.setId( p.getId().toString() );
				gwtGroup.setName( p.getName() );
				gwtGroup.setTitle( p.getTitle() );
				
				nfRoot.addPrincipal( gwtGroup );
			}
		}
	}
	
	/**
	 * Return a NetFolder object for the given net folder id 
	 */
	public static NetFolder getNetFolder(
		AllModulesInjected ami,
		Long id )
	{
		NetFolder netFolder;
		Binder binder;
		GwtSchedule gwtSchedule;
		
		netFolder = new NetFolder();
		netFolder.setId( id );
		
		binder = ami.getBinderModule().getBinder( id );
		netFolder.setName( binder.getTitle() );
		netFolder.setNetFolderRootName( binder.getResourceDriverName() );
		netFolder.setRelativePath( binder.getResourcePath() );
		netFolder.setParentBinderId( binder.getParentBinder().getId() );
		netFolder.setStatus( NetFolderStatus.READY );
		
		// Get the net folder's sync schedule.
		gwtSchedule = getGwtSyncSchedule( ami, binder );
		netFolder.setSyncSchedule( gwtSchedule );

		return netFolder;
	}
	
	/**
	 * For the given GwtSchedule, return a ScheduleInfo that represents the GwtSchedule.
	 * This code is patterned after the code in ScheduleHelper.getSchedule()
	 */
	private static ScheduleInfo getScheduleInfoFromGwtSchedule( GwtSchedule gwtSchedule )
	{
		Long zoneId;
		ScheduleInfo scheduleInfo;
		
		// Get the ScheduleInfo for this net folder.
		zoneId = RequestContextHolder.getRequestContext().getZoneId();
		scheduleInfo = new ScheduleInfo( zoneId );
		scheduleInfo.setSchedule( new Schedule( "" ) );
		
		// Does the net folder have a GwtSchedule that we need to take data from and
		// update the ScheduleInfo?
		if ( gwtSchedule != null )
		{
			Schedule schedule;
			DayFrequency dayFrequency;
			TimeFrequency timeFrequency;
			Random randomMinutes;
			
			// Yes
			randomMinutes = new Random();
			
			scheduleInfo.setEnabled( gwtSchedule.getEnabled() );
			
			schedule = scheduleInfo.getSchedule();
			
			dayFrequency = gwtSchedule.getDayFrequency(); 
			if (  dayFrequency == DayFrequency.EVERY_DAY )
			{
				schedule.setDaily( true );
			}
			else if ( dayFrequency == DayFrequency.ON_SELECTED_DAYS )
			{
				schedule.setDaily( false );
				schedule.setOnMonday( gwtSchedule.getOnMonday() );
				schedule.setOnTuesday( gwtSchedule.getOnTuesdy() );
				schedule.setOnWednesday( gwtSchedule.getOnWednesday() );
				schedule.setOnThursday( gwtSchedule.getOnThursday() );
				schedule.setOnFriday( gwtSchedule.getOnFriday() );
				schedule.setOnSaturday( gwtSchedule.getOnSaturday() );
				schedule.setOnSunday( gwtSchedule.getOnSunday() );
			}
			
			timeFrequency = gwtSchedule.getTimeFrequency(); 
			if ( timeFrequency == TimeFrequency.AT_SPECIFIC_TIME )
			{
				schedule.setHours( gwtSchedule.getAtHoursAsString() );
				schedule.setMinutes( gwtSchedule.getAtMinutesAsString() );
			}
			else if ( timeFrequency == TimeFrequency.REPEAT_EVERY_MINUTE )
			{
				int repeatValue;
				
				schedule.setHours( "*" );
				
				repeatValue = gwtSchedule.getRepeatEveryValue();
				if ( repeatValue == 15 || repeatValue == 30 )
				{
					schedule.setMinutes( randomMinutes.nextInt( repeatValue ) + "/" + repeatValue );
				}
				else if ( repeatValue == 45 )
				{
					schedule.setMinutes( "0/45" );
				}
			}
			else if ( timeFrequency == TimeFrequency.REPEAT_EVERY_HOUR )
			{
				schedule.setMinutes( Integer.toString( randomMinutes.nextInt( 60 ) ) );
				schedule.setHours( "0/" + gwtSchedule.getRepeatEveryValue() );
			}
		}
		
		return scheduleInfo;
	}
	
	/**
	 * Modify the net folder from the given data
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static NetFolder modifyNetFolder(
		AllModulesInjected ami,
		NetFolder netFolder ) throws GwtTeamingException
	{
		try
		{
			Set deleteAtts;
			Map fileMap = null;
			MapInputData mid;
			Map formData = null;
			
			deleteAtts = new HashSet();
			fileMap = new HashMap();
			formData = new HashMap();
	   		formData.put( ObjectKeys.FIELD_BINDER_LIBRARY, "true" );
	   		formData.put( ObjectKeys.FIELD_BINDER_MIRRORED, "true" );
	   		formData.put( ObjectKeys.FIELD_BINDER_RESOURCE_DRIVER_NAME, netFolder.getNetFolderRootName() );
	   		formData.put( ObjectKeys.FIELD_BINDER_RESOURCE_PATH, netFolder.getRelativePath() );
			mid = new MapInputData( formData );

			// Modify the binder with the net folder information.
   			ami.getBinderModule().modifyBinder( netFolder.getId(), mid, fileMap, deleteAtts, null );				

			// Set the net folder's sync schedule
			{
				ScheduleInfo scheduleInfo;
				
				scheduleInfo = getScheduleInfoFromGwtSchedule( netFolder.getSyncSchedule() );
				if ( scheduleInfo != null )
				{
					scheduleInfo.setFolderId( netFolder.getId() );
					ami.getFolderModule().setSynchronizationSchedule( scheduleInfo, netFolder.getId() );
				}
			}
		}
		catch ( Exception ex )
		{
			GwtTeamingException gtEx;
			
			gtEx = GwtServerHelper.getGwtTeamingException( ex );
			throw gtEx;				
		}
		
		return netFolder;
	}
	
	
	/**
	 * Modify the net folder root from the given data
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static NetFolderRoot modifyNetFolderRoot(
		AllModulesInjected ami,
		NetFolderRoot netFolderRoot ) throws GwtTeamingException
	{
		Map options;

		ami.getAdminModule().checkAccess( AdminOperation.manageResourceDrivers );

		options = new HashMap();
		options.put( ObjectKeys.RESOURCE_DRIVER_READ_ONLY, Boolean.FALSE );
		options.put( ObjectKeys.RESOURCE_DRIVER_ACCOUNT_NAME, netFolderRoot.getProxyName() ); 
		options.put( ObjectKeys.RESOURCE_DRIVER_PASSWORD, netFolderRoot.getProxyPwd() );

		// Always prevent the top level folder from being deleted
		// This is forced so that the folder could not accidentally be deleted if the 
		// external disk was offline
		options.put( ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE, Boolean.FALSE );

		// Is the root type WebDAV?
		if ( netFolderRoot.getRootType() == NetFolderRootType.WEB_DAV )
		{
			// Yes, get the WebDAV specific values
			options.put(
					ObjectKeys.RESOURCE_DRIVER_HOST_URL,
					netFolderRoot.getHostUrl() );
			options.put(
					ObjectKeys.RESOURCE_DRIVER_ALLOW_SELF_SIGNED_CERTIFICATE,
					netFolderRoot.getAllowSelfSignedCerts() );
			options.put(
					ObjectKeys.RESOURCE_DRIVER_PUT_REQUIRES_CONTENT_LENGTH,
					netFolderRoot.getIsSharePointServer() );
		}

		//Add this resource driver
		try
		{
			DriverType driverType;
			
			driverType = getDriverType( netFolderRoot.getRootType() );
			ami.getResourceDriverModule().modifyResourceDriver(
														netFolderRoot.getName(),
														driverType, 
														netFolderRoot.getRootPath(),
														netFolderRoot.getListOfPrincipalIds(),
														options );
		}
		catch ( Exception ex )
		{
			GwtTeamingException gtEx;
			
			gtEx = GwtServerHelper.getGwtTeamingException( ex );
			throw gtEx;				
		}
		
		return netFolderRoot;
	}

	/**
	 * Sync the given list of net folders
	 */
	public static Set<NetFolder> syncNetFolders(
		AllModulesInjected ami,
		Set<NetFolder> netFolders ) throws GwtTeamingException
	{
		for ( NetFolder nextNetFolder : netFolders )
		{
			try
			{
				StatusTicket statusTicket = null;
				String statusTicketId;

				statusTicketId = "sync_net_folder_" + nextNetFolder.getId();
				//statusTicket = WebStatusTicket.newStatusTicket( statusTicketId, request);
				if( ami.getFolderModule().synchronize( nextNetFolder.getId(), statusTicket ) )
				{
					// The binder was not deleted (typical situation).
					nextNetFolder.setStatus( NetFolderStatus.SYNC_IN_PROGRESS );
					nextNetFolder.setStatusTicketId( statusTicketId );
				}
				else 
				{
					// The binder was indeed deleted.
					nextNetFolder.setStatus( NetFolderStatus.DELETED_BY_SYNC_PROCESS );
				}
			}
			catch ( Exception e )
			{
				GwtTeamingException gwtEx;
				
				m_logger.error( "Error syncing next net folder: " + nextNetFolder.getName() + ", " + e.toString() );
				
				gwtEx = GwtServerHelper.getGwtTeamingException( e );
				throw gwtEx;
			}
		}
		
		return netFolders;
	}
}
