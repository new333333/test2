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
import java.util.List;
import java.util.Set;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.gwt.client.NetFolderRoot;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.resourcedriver.RDException;
import org.kablink.teaming.module.resourcedriver.ResourceDriverModule;
import org.kablink.teaming.util.AllModulesInjected;


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
	public static List<NetFolderRoot> getAllNetFolderRoots( AllModulesInjected ami )
	{
		List<NetFolderRoot> listOfNetFolderRoots;
		
		listOfNetFolderRoots = new ArrayList<NetFolderRoot>();
		
		if ( ami.getAdminModule().testAccess( AdminOperation.manageResourceDrivers ) )
		{
			List<ResourceDriverConfig> drivers;

			//Get a list of the currently defined Net Folder Roots
			drivers = ami.getResourceDriverModule().getAllResourceDriverConfigs();
			for ( ResourceDriverConfig driver : drivers )
			{
				NetFolderRoot fsRoot;
				
				fsRoot = new NetFolderRoot();
				fsRoot.setId( driver.getId() );
				fsRoot.setName( driver.getName() );
				fsRoot.setRootPath( driver.getRootPath() );
				listOfNetFolderRoots.add( fsRoot );
			}
		}
		
		return listOfNetFolderRoots;
	}
	
}
