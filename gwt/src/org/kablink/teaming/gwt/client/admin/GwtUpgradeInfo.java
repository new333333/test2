/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.admin;



import java.util.ArrayList;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class represents upgrade information such as the tasks that the administrator needs to perform.
 * @author jwootton
 *
 */
public class GwtUpgradeInfo
	implements IsSerializable, VibeRpcResponseData
{
	private String m_releaseInfo;
	private ArrayList<UpgradeTask> m_upgradeTasks = null;
	private boolean m_upgradeTasksExist = false;
	private boolean m_isAdmin = false;
	
	
	/**
	 * 
	 */
	public GwtUpgradeInfo()
	{
	}// end GwtUpgradeInfo()
	
	
	/**
	 * Add an upgrade task to the list of upgrade tasks.
	 */
	public void addUpgradeTask( UpgradeTask upgradeTask )
	{
		if ( m_upgradeTasks == null )
			m_upgradeTasks = new ArrayList<UpgradeTask>();
		
		// See if this UpgradeTask is already in our list.
		for ( UpgradeTask task : m_upgradeTasks )
		{
			if ( task == upgradeTask )
				return;
		}
		
		m_upgradeTasksExist = true;
		
		// If we get here the upgrade task is not in our list, add it.
		m_upgradeTasks.add( upgradeTask );
	}// end addUpgradeTask()
	
	
	/**
	 * Return true if there are upgrade tasks to be performed.
	 */
	public boolean doUpgradeTasksExist()
	{
		return m_upgradeTasksExist;
	}// end doUpgradeTasksExist()

	
	/**
	 * 
	 */
	public boolean getIsAdmin()
	{
		return m_isAdmin;
	}// end getIsAdmin()
	
	
	/**
	 * 
	 */
	public String getReleaseInfo()
	{
		return m_releaseInfo;
	}// end getReleaseInfo()
	
	
	/**
	 * 
	 */
	public ArrayList<UpgradeTask> getUpgradeTasks()
	{
		return m_upgradeTasks;
	}// end getUpgradeTasks()
	

	/**
	 * 
	 */
	public void setIsAdmin( boolean isAdmin )
	{
		m_isAdmin = isAdmin;
	}// end setIsAdmin()
	
	
	/**
	 * 
	 */
	public void setReleaseInfo( String releaseInfo )
	{
		m_releaseInfo = releaseInfo;
	}// end setReleaseInfo()
	
	
	/**
	 * 
	 */
	public void setUpgradeTasksExist( boolean exist )
	{
		m_upgradeTasksExist = exist;
	}// end setUpgradeTasksExist()
	

	/**
	 * This class represents all of the possible upgrade tasks. 
	 */
	public enum UpgradeTask implements IsSerializable
	{
		UPGRADE_DEFINITIONS,
		UPGRADE_SEARCH_INDEX,
		UPGRADE_TEMPLATES;
	}// end UpgradeTask

}// end GwtUpgradeInfo
