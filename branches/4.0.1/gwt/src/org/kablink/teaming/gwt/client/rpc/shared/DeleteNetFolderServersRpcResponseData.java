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
package org.kablink.teaming.gwt.client.rpc.shared;


import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.NetFolderRoot;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data to the "delete net folder servers" rpc command
 * @author jwootton
 *
 */
public class DeleteNetFolderServersRpcResponseData
	implements IsSerializable, VibeRpcResponseData
{
	private List<NetFolderRoot> m_listOfDeletedNetFolderServers;
	private List<NetFolderRoot> m_listOfCouldNotBeDeletedNetFolderServers;
	
	/**
	 * 
	 */
	public DeleteNetFolderServersRpcResponseData()
	{
		m_listOfCouldNotBeDeletedNetFolderServers = null;
		m_listOfDeletedNetFolderServers = null;
	}
	
	/**
	 * Add a net folder server to the "could not be deleted" list.
	 */
	public void addCouldNotBeDeletedNetFolderServer( NetFolderRoot nfServer )
	{
		if ( m_listOfCouldNotBeDeletedNetFolderServers == null )
			m_listOfCouldNotBeDeletedNetFolderServers = new ArrayList<NetFolderRoot>();
		
		m_listOfCouldNotBeDeletedNetFolderServers.add( nfServer );
	}
	
	/**
	 * Add a net folder server to the "deleted" list.
	 */
	public void addDeletedNetFolderServer( NetFolderRoot nfServer )
	{
		if ( m_listOfDeletedNetFolderServers == null )
			m_listOfDeletedNetFolderServers = new ArrayList<NetFolderRoot>();
		
		m_listOfDeletedNetFolderServers.add( nfServer );
	}
	
	/**
	 * Get a list of net folder servers we successfully deleted.
	 */
	public List<NetFolderRoot> getListOfDeletedNetFolderRoots()
	{
		return m_listOfDeletedNetFolderServers;
	}
	
	/**
	 * Get a list of net folder servers that could not be deleted.
	 */
	public List<NetFolderRoot> getListOfCouldNotDeleteNetFolderServers()
	{
		return m_listOfCouldNotBeDeletedNetFolderServers;
	}
}
