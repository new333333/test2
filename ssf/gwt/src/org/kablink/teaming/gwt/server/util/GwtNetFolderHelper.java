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
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.ResourceDriverConfig.DriverType;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.NetFolderRoot;
import org.kablink.teaming.gwt.client.widgets.ModifyNetFolderRootDlg.NetFolderRootType;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.resourcedriver.RDException;
import org.kablink.teaming.module.resourcedriver.ResourceDriverModule;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.Utils;


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
					Boolean.FALSE );
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
	 * Delete the given list of net folder roots
	 */
	public static Boolean deleteNetFolderRoots(
		AllModulesInjected ami,
		Set<NetFolderRoot> netFolderRoots )
	{
		Boolean result;
		ResourceDriverModule rdModule;
		
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
	 * Return a list of all the net folder roots
	 */
	@SuppressWarnings("unchecked")
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
				
				// Get the list of principals that can use the net folder root
				{
					List<Function> functions;
					List<WorkAreaFunctionMembership> memberships;
					WorkAreaFunctionMembership membership = null;
					List<Principal> members;

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
					Boolean.FALSE );
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
	
}
