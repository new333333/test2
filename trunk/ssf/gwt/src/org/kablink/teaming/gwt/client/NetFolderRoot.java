/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.widgets.ModifyNetFolderRootDlg.NetFolderRootType;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used in GWT RPC calls to represent a Net Folder Root object.
 * 
 * @author jwootton@novell.com
 */
public class NetFolderRoot 
	implements IsSerializable, VibeRpcResponseData
{
	private Long m_id;
	private String m_name;
	private NetFolderRootType m_rootType = NetFolderRootType.FAMT;
	private String m_rootPath;
	private String m_proxyName = "";
	private String m_proxyPwd = "";
	private ArrayList<GwtPrincipal> m_principals;
	
	// This information is specific to WebDAV
	private boolean m_allowSelfSignedCerts = false;
	private boolean m_isSharePointServer = false;
	private String m_hostUrl = "";
	
	
	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public NetFolderRoot()
	{
		// Nothing to do.
	}	
	
	/**
	 * 
	 */
	public void addPrincipal( GwtPrincipal principal )
	{
		if ( m_principals == null )
			m_principals = new ArrayList<GwtPrincipal>();
		
		m_principals.add( principal );
	}
	
	/**
	 * Copy the info from the given NetFolderRoot
	 */
	public void copy( NetFolderRoot root )
	{
		m_id = root.getId();
		m_name = root.getName();
		m_rootType = root.getRootType();
		m_rootPath = root.getRootPath();
		m_proxyName = root.getProxyName();
		m_proxyPwd = root.getProxyPwd();
		m_principals = root.getListOfPrincipals();
		
		// Copy WebDAV info
		m_allowSelfSignedCerts = root.getAllowSelfSignedCerts();
		m_isSharePointServer = root.getIsSharePointServer();
		m_hostUrl = root.getHostUrl();
	}
	
	/**
	 * 
	 */
	public boolean getAllowSelfSignedCerts()
	{
		return m_allowSelfSignedCerts;
	}
	
	/**
	 * 
	 */
	public String getHostUrl()
	{
		return m_hostUrl;
	}
	
	/**
	 * 
	 */
	public Long getId()
	{
		return m_id;
	}
	
	/**
	 * 
	 */
	public boolean getIsSharePointServer()
	{
		return m_isSharePointServer;
	}
	
	/**
	 * Returns the Net Folder Root's name.
	 */
	public String getName()
	{
		return m_name;
	}
	
	/**
	 * 
	 */
	public ArrayList<GwtPrincipal> getListOfPrincipals()
	{
		return m_principals;
	}
	
	/**
	 * 
	 */
	public Set<Long> getListOfPrincipalIds()
	{
		HashSet<Long> principalIds;
		
		principalIds = new HashSet<Long>();
		for (GwtPrincipal nextPrincipal : m_principals)
		{
			principalIds.add( nextPrincipal.getIdLong() );
		}
		
		return principalIds;
	}
	
	/**
	 * 
	 */
	public String getProxyName()
	{
		return m_proxyName;
	}
	
	/**
	 * 
	 */
	public String getProxyPwd()
	{
		return m_proxyPwd;
	}
	
	/**
	 * Returns the root path
	 */
	public String getRootPath()
	{
		return m_rootPath;
	}
	
	/**
	 * 
	 */
	public NetFolderRootType getRootType()
	{
		return m_rootType;
	}
	
	/**
	 * 
	 */
	public void setAllowSelfSignedCerts( boolean allow )
	{
		m_allowSelfSignedCerts = allow;
	}
	
	/**
	 * 
	 */
	public void setHostUrl( String url )
	{
		m_hostUrl = url;
	}
	
	/**
	 * 
	 */
	public void setId( Long id )
	{
		m_id = id;
	}

	/**
	 * 
	 */
	public void setIsSharePointServer( boolean isSharePointServer )
	{
		m_isSharePointServer = isSharePointServer;
	}
	
	/**
	 * 
	 */
	public void setListOfPrincipals( ArrayList<GwtPrincipal> listOfPrincipals )
	{
		m_principals = listOfPrincipals;
	}
	
	/**
	 * Stores the Net Folder Root's name.
	 * 
	 * @param name
	 */
	public void setName( String name )
	{
		m_name = name;
	}
	
	/**
	 * 
	 */
	public void setProxyName( String name )
	{
		m_proxyName = name;
	}
	
	/**
	 * 
	 */
	public void setProxyPwd( String pwd )
	{
		m_proxyPwd = pwd;
	}
	
	/**
	 * Stores the root path
	 * 
	 * @param rootPath
	 */
	public void setRootPath( String rootPath )
	{
		m_rootPath = rootPath;
	}
	
	/**
	 * 
	 */
	public void setRootType( NetFolderRootType type )
	{
		m_rootType = type;
	}
}
