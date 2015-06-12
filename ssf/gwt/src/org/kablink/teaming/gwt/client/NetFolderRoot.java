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
	private NetFolderRootType m_rootType = NetFolderRootType.WINDOWS;
	private String m_rootPath;
	private String m_proxyName = "";
	private String m_proxyPwd = "";
	private GwtProxyIdentity m_proxyIdentity;
	private GwtSchedule m_syncSchedule;
	private Boolean m_indexContent;
	private Boolean m_jitsEnabled;
	private long m_jitsResultsMaxAge = 0;
	private long m_jitsAclMaxAge = 0;
	private Boolean m_fullSyncDirOnly;
	private Boolean m_allowDesktopAppToTriggerInitialHomeFolderSync;
	private ArrayList<GwtPrincipal> m_principals;
	private NetFolderRootStatus m_status;
	private String m_statusTicketId;
	private GwtAuthenticationType m_authType;
	
	// This information is specific to WebDAV
	private boolean m_allowSelfSignedCerts = false;
	private boolean m_isSharePointServer = false;
	private String m_hostUrl = "";
	
	
	/**
	 * The different statuses of a net folder root
	 */
	public enum NetFolderRootStatus implements IsSerializable
	{
		SYNC_IN_PROGRESS,
		SYNC_FAILURE,
		READY
	}
	
	/**
	 * This class should mirror ResourceDriverConfig.AuthenticationType
	 */
	public enum GwtAuthenticationType implements IsSerializable
	{
		KERBEROS( (short) 1 ),
		NTLM( (short) 2 ),
		KERBEROS_THEN_NTLM( (short) 3 ),
		NMAS( (short) 4 ),
		UNKNOWN( (short) 10 );
		
		private short m_value;

		/**
		 * 
		 */
		GwtAuthenticationType( short value )
		{
			m_value = value;
		}
		
		/**
		 * 
		 */
		public short getValue()
		{
			return m_value;
		}
		
		/**
		 * 
		 */
		public static GwtAuthenticationType getType( short value )
		{
			switch( value )
			{
			case 1:
				return GwtAuthenticationType.KERBEROS;
				
			case 2:
				return GwtAuthenticationType.NTLM;
				
			case 3:
				return GwtAuthenticationType.KERBEROS_THEN_NTLM;
				
			case 4:
				return GwtAuthenticationType.NMAS;

			default:
				return GwtAuthenticationType.UNKNOWN;
			}
		}
		
		/**
		 * 
		 */
		public static GwtAuthenticationType getType( String value )
		{
			if ( value != null )
			{
				if ( value.equalsIgnoreCase( GwtAuthenticationType.KERBEROS.toString() ) )
					return GwtAuthenticationType.KERBEROS;
				
				if ( value.equalsIgnoreCase( GwtAuthenticationType.NTLM.toString() ) )
					return GwtAuthenticationType.NTLM;
				
				if ( value.equalsIgnoreCase( GwtAuthenticationType.KERBEROS_THEN_NTLM.toString() ) )
					return GwtAuthenticationType.KERBEROS_THEN_NTLM;
				
				if ( value.equalsIgnoreCase( GwtAuthenticationType.NMAS.toString() ) )
					return GwtAuthenticationType.NMAS;
			}
			
			return GwtAuthenticationType.UNKNOWN;
		}
		
	}
	
	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public NetFolderRoot()
	{
		m_status = NetFolderRootStatus.READY;
		m_indexContent = null;
		m_fullSyncDirOnly = null;
		m_authType = null;
		m_jitsEnabled = null;
		m_jitsResultsMaxAge = 0;
		m_jitsAclMaxAge = 0;
		m_allowDesktopAppToTriggerInitialHomeFolderSync = null;
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
		m_proxyIdentity = root.getProxyIdentity();
		m_principals = root.getListOfPrincipals();
		m_syncSchedule = root.getSyncSchedule();
		m_indexContent = root.getIndexContent();
		m_fullSyncDirOnly = root.getFullSyncDirOnly();
		m_authType = root.getAuthType();
		m_jitsEnabled = root.getJitsEnabled();
		m_jitsResultsMaxAge = root.getJitsResultsMaxAge();
		m_jitsAclMaxAge = root.getJitsAclMaxAge();
		m_allowDesktopAppToTriggerInitialHomeFolderSync = root.getAllowDesktopAppToTriggerInitialHomeFolderSync();

		m_statusTicketId = root.getStatusTicketId();

		// Copy WebDAV info
		m_allowSelfSignedCerts = root.getAllowSelfSignedCerts();
		m_isSharePointServer = root.getIsSharePointServer();
		m_hostUrl = root.getHostUrl();
	}
	
	/**
	 * 
	 */
	public Boolean getAllowDesktopAppToTriggerInitialHomeFolderSync()
	{
		return m_allowDesktopAppToTriggerInitialHomeFolderSync;
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
	public GwtAuthenticationType getAuthType()
	{
		return m_authType;
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
		if ( m_principals != null )
		{
			for (GwtPrincipal nextPrincipal : m_principals)
			{
				principalIds.add( nextPrincipal.getIdLong() );
			}
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
	 * 
	 */
	public GwtProxyIdentity getProxyIdentity()
	{
		return m_proxyIdentity;
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
	public NetFolderRootStatus getStatus()
	{
		return m_status;
	}
	
	/**
	 * 
	 */
	public String getStatusTicketId()
	{
		return m_statusTicketId;
	}
	
	/**
	 * 
	 */
	public Boolean getFullSyncDirOnly()
	{
		return m_fullSyncDirOnly;
	}
	
	/**
	 * 
	 */
	public Boolean getIndexContent()
	{
		return m_indexContent;
	}
	
	/**
	 * 
	 */
	public Boolean getJitsEnabled()
	{
		return m_jitsEnabled;
	}
	
	/**
	 * 
	 */
	public long getJitsAclMaxAge()
	{
		return m_jitsAclMaxAge;
	}
	
	/**
	 * 
	 */
	public long getJitsResultsMaxAge()
	{
		return m_jitsResultsMaxAge;
	}
	
	/**
	 * 
	 */
	public GwtSchedule getSyncSchedule()
	{
		return m_syncSchedule;
	}

	/**
	 * Is this net folder root fully configured with server path and proxy credentials.
	 */
	public boolean isConfigured()
	{
		if ( m_rootPath == null || m_rootPath.length() == 0 )
			return false;
		
		if ( (m_proxyName == null || m_proxyName.length() == 0) && ( null == m_proxyIdentity ) )
			return false;
		
		if ( ( m_proxyPwd == null || m_proxyPwd.length() == 0 ) && ( null == m_proxyIdentity ) )
			return false;
		
		if ( m_rootType == null || m_rootType == NetFolderRootType.FAMT || m_rootType == NetFolderRootType.UNKNOWN )
			return false;
		
		return true;
	}
	
	/**
	 * 
	 */
	public void setAllowDesktopAppToTriggerInitialHomeFolderSync( Boolean allow )
	{
		m_allowDesktopAppToTriggerInitialHomeFolderSync = allow;
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
	public void setAuthType( GwtAuthenticationType type )
	{
		m_authType = type;
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
	 *
	 */
	public void setProxyIdentity( GwtProxyIdentity pi )
	{
		m_proxyIdentity = pi;
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

	/**
	 * 
	 */
	public void setStatus( NetFolderRootStatus status )
	{
		m_status = status;
	}
	
	/**
	 * 
	 */
	public void setStatusTicketId( String id )
	{
		m_statusTicketId = id;
	}
	
	/**
	 * 
	 */
	public void setFullSyncDirOnly( Boolean value )
	{
		m_fullSyncDirOnly = value;
	}
	
	/**
	 * 
	 */
	public void setIndexContent( Boolean value )
	{
		m_indexContent = value;
	}
	
	/**
	 * 
	 */
	public void setJitsEnabled( Boolean value )
	{
		m_jitsEnabled = value;
	}
	
	/**
	 * 
	 */
	public void setJitsResultsMaxAge( long value )
	{
		m_jitsResultsMaxAge = value;
	}
	
	/**
	 * 
	 */
	public void setJitsAclMaxAge( long value )
	{
		m_jitsAclMaxAge = value;
	}
	
	/**
	 * 
	 */
	public void setSyncSchedule( GwtSchedule schedule )
	{
		m_syncSchedule = schedule;
	}
}
