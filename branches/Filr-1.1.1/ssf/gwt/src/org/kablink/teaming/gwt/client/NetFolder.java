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

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used in GWT RPC calls to represent a Net Folder object.
 * 
 * @author jwootton@novell.com
 */
public class NetFolder 
	implements IsSerializable, VibeRpcResponseData
{
	private Long m_id;
	private String m_name;
	private String m_displayName;
	private String m_relativePath;
	private String m_netFolderRootName;
	private NetFolderSyncStatus m_status;
	private GwtNetFolderSyncScheduleConfig m_syncScheduleConfig;
	private ArrayList<GwtRole> m_roles;
	private boolean m_isHomeDir;
	private boolean m_indexContent;
	private Boolean m_inheritIndexContentSetting;
	private Boolean m_inheritJitsSettings;
	private NetFolderDataSyncSettings m_dataSyncSettings;
	private GwtJitsNetFolderConfig m_jitsConfig;
	private Boolean m_fullSyncDirOnly;
	
	/**
	 * The different statuses of a net Folder
	 */
	public enum NetFolderSyncStatus implements IsSerializable
	{
		WAITING_TO_BE_SYNCD,
		SYNC_IN_PROGRESS,
		SYNC_STOPPED,		// The sync of a net folder was running and then stopped by the admin
		SYNC_COMPLETED,
		SYNC_NEVER_RUN,
		SYNC_CANCELED,		// The net folder was waiting to be sync'd and the admin canceled the sync.
		DELETE_IN_PROGRESS,
		DELETE_FAILED,
		UNKNOWN
	}
	
	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public NetFolder()
	{
		// Nothing to do.
		m_isHomeDir = false;
		m_indexContent = false;
		m_inheritIndexContentSetting = true;
		m_inheritJitsSettings = true;
		m_fullSyncDirOnly = null;
	}	
	
	/**
	 * 
	 */
	public void copy( NetFolder netFolder )
	{
		m_id = netFolder.getId();
		m_name = netFolder.getName();
		m_displayName = netFolder.getDisplayName();
		m_relativePath = netFolder.getRelativePath();
		m_netFolderRootName = netFolder.getNetFolderRootName();
		m_syncScheduleConfig = netFolder.getSyncScheduleConfig();
		m_isHomeDir = netFolder.getIsHomeDir();
		m_indexContent = netFolder.getIndexContent();
		m_inheritIndexContentSetting = netFolder.getInheritIndexContentSetting();
		m_inheritJitsSettings = netFolder.getInheritJitsSettings();
		
		m_dataSyncSettings = new NetFolderDataSyncSettings();
		m_dataSyncSettings.copy( netFolder.getDataSyncSettings() );
		
		m_jitsConfig = new GwtJitsNetFolderConfig();
		m_jitsConfig.copy( netFolder.getJitsConfig() );
		
		m_fullSyncDirOnly = netFolder.getFullSyncDirOnly();
	}
	
	/**
	 * 
	 */
	public NetFolderDataSyncSettings getDataSyncSettings()
	{
		return m_dataSyncSettings;
	}
	
	/**
	 * 
	 */
	public String getDisplayName()
	{
		if ( m_displayName == null || m_displayName.length() == 0 )
			return m_name;
		
		return m_displayName;
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
	public Long getId()
	{
		return m_id;
	}
	
	/**
	 * 
	 */
	public boolean getIndexContent()
	{
		return m_indexContent;
	}
	
	/**
	 * 
	 */
	public boolean getInheritIndexContentSetting()
	{
		return m_inheritIndexContentSetting;
	}
	
	/**
	 * 
	 */
	public boolean getInheritJitsSettings()
	{
		return m_inheritJitsSettings;
	}
	
	/**
	 * 
	 */
	public boolean getIsHomeDir()
	{
		return m_isHomeDir;
	}
	
	/**
	 * 
	 */
	public GwtJitsNetFolderConfig getJitsConfig()
	{
		return m_jitsConfig;
	}
	
	/**
	 * Returns the Net Folder's name.
	 */
	public String getName()
	{
		return m_name;
	}
	
	/**
	 * 
	 */
	public String getNetFolderRootName()
	{
		return m_netFolderRootName;
	}
	
	/**
	 * Returns the relative path.
	 */
	public String getRelativePath()
	{
		return m_relativePath;
	}
	
	/**
	 * 
	 */
	public ArrayList<GwtRole> getRoles()
	{
		if (m_roles == null) {
			return new ArrayList<GwtRole>();
		} else {
			return m_roles;
		}
	}
	
	/**
	 * 
	 */
	public GwtNetFolderSyncScheduleConfig getSyncScheduleConfig()
	{
		return m_syncScheduleConfig;
	}
	
	/**
	 * 
	 */
	public NetFolderSyncStatus getStatus()
	{
		return m_status;
	}
	
	/**
	 * 
	 */
	public void setDataSyncSettings( NetFolderDataSyncSettings settings )
	{
		m_dataSyncSettings = new NetFolderDataSyncSettings();
		m_dataSyncSettings.copy( settings );
	}
	
	/**
	 * 
	 */
	public void setDisplayName( String value )
	{
		m_displayName = value;
	}
	
	/**
	 * 
	 */
	public void setFullSyncDirOnly( Boolean fullSyncDirOnly )
	{
		m_fullSyncDirOnly = fullSyncDirOnly;
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
	public void setIndexContent( boolean index )
	{
		m_indexContent = index;
	}
	
	/**
	 * 
	 */
	public void setInheritIndexContentSetting( Boolean inherit )
	{
		m_inheritIndexContentSetting = inherit;
	}
	
	/**
	 * 
	 */
	public void setInheritJitsSettings( Boolean inherit )
	{
		m_inheritJitsSettings = inherit;
	}
	
	/**
	 * 
	 */
	public void setIsHomeDir( boolean isHomeDir )
	{
		m_isHomeDir = isHomeDir;
	}

	/**
	 * 
	 */
	public void setJitsConfig( GwtJitsNetFolderConfig config )
	{
		m_jitsConfig = new GwtJitsNetFolderConfig();
		m_jitsConfig.copy( config );
	}
	/**
	 * Stores the Net Folder's name.
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
	public void setNetFolderRootName( String rootName )
	{
		m_netFolderRootName = rootName;
	}
	
	/**
	 * Stores the relative path
	 * 
	 * @param relativePath
	 */
	public void setRelativePath( String relativePath )
	{
		m_relativePath = relativePath;
	}

	/**
	 * Set the roles
	 */
	public void setRoles( ArrayList<GwtRole> roles )
	{
		m_roles = roles;
	}
	
	/**
	 * 
	 */
	public void setSyncScheduleConfig( GwtNetFolderSyncScheduleConfig config )
	{
		m_syncScheduleConfig = config;
	}
	
	/**
	 * 
	 */
	public void setStatus( NetFolderSyncStatus status )
	{
		m_status = status;
	}
}
