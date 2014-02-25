/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.domain;

import org.kablink.teaming.domain.Binder.SyncScheduleOption;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManagerUtil;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.NetFolderHelper;

/**
 * @author jong
 *
 */
public class NetFolderConfig {

	protected Long id;
	protected String name;
	protected Long folderId;
	protected Long netFolderServerId;
	protected String resourcePath;
    protected Boolean homeDir = Boolean.FALSE;
    protected Boolean allowDesktopAppToSyncData = Boolean.TRUE;
    protected Boolean allowMobileAppsToSyncData = Boolean.TRUE;
    protected Boolean indexContent = Boolean.TRUE;
    protected Boolean jitsEnabled; // Applicable only to mirrored folders
    protected Long jitsMaxAge; // in milliseconds
    protected Long jitsAclMaxAge; // in milliseconds
    protected Boolean fullSyncDirOnly; // Applicable only to mirrored folders
    protected Short syncScheduleOption;	// SyncScheduleOption
    protected Boolean useInheritedIndexContent = Boolean.TRUE;
    protected Boolean useInheritedJitsSettings = Boolean.TRUE;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getFolderId() {
		return folderId;
	}

	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}

	public Long getNetFolderServerId() {
		return netFolderServerId;
	}

	public void setNetFolderServerId(Long netFolderServerId) {
		this.netFolderServerId = netFolderServerId;
	}

    public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public boolean isHomeDir() {
    	if(homeDir == null)
    		return false;
    	else
    		return homeDir.booleanValue();
    }
    public void setHomeDir(boolean homeDir) {
    	this.homeDir = homeDir;
    }
    
    /**
     * Return whether the desktop app can sync data from this binder
     * @return
     */
    public boolean getAllowDesktopAppToSyncData()
    {
    	if ( allowDesktopAppToSyncData == null )
    		return true;
    	else
    		return allowDesktopAppToSyncData.booleanValue();
    }
    
    public void setAllowDesktopAppToSyncData( boolean allow )
    {
   		allowDesktopAppToSyncData = new Boolean( allow );
    }
    
    /**
     * Return whether mobile apps can sync data from this binder
     * @return
     */
    public boolean getAllowMobileAppsToSyncData()
    {
    	if ( allowMobileAppsToSyncData == null )
    		return true;
    	else
    		return allowMobileAppsToSyncData.booleanValue();
    }
    
    public void setAllowMobileAppsToSyncData( boolean allow )
    {
   		allowMobileAppsToSyncData = new Boolean( allow );
    }
    
    /**
     * Return whether the contents of this binder should be indexed.
     * @return
     */
    public boolean getIndexContent()
    {
    	if ( indexContent == null )
    		return true;
    	else
    		return indexContent .booleanValue();
    }

    /**
     * 
     */
    public void setIndexContent( boolean index )
    {
   		indexContent = new Boolean( index );
    }

    /**
     * Return the computed value of "index content".  If this binder is inheriting the value
     * of "index content" then we will get the value of "index content" from the net folder server
     * this binder is pointing to.  Otherwise, we will use the value of "index content" from
     * this binder.
     */
    public boolean getComputedIndexContent()
    {
    	ResourceDriver resourceDriver;
    	
    	if ( getUseInheritedIndexContent() == false )
    		return getIndexContent();
    	
    	resourceDriver = getResourceDriver();
    	if ( resourceDriver != null )
    	{
    		ResourceDriverConfig rdConfig;
    		
    		rdConfig = resourceDriver.getConfig();
    		if ( rdConfig != null )
    			return rdConfig.getIndexContent();
    	}
    	
    	return false;
    }
    
    /**
     * Return whether the the "index content" setting should be inherited from the net folder server.
     * @return
     */
    public boolean getUseInheritedIndexContent()
    {
    	boolean useInherited;
    	
    	// If the useInheritedIndexContent field is null that means this binder existed
    	// before we added this field.
    	if ( useInheritedIndexContent == null )
    	{
    		// If the content of this binder should be indexed, then we will say not to inherit
    		// the "index content" setting from the net folder server.
    		if ( getIndexContent() == true )
    			useInherited = false;
    		else
    			useInherited = true;
    	}
    	else
    	{
    		useInherited = useInheritedIndexContent.booleanValue();
    	}
    	
    	return useInherited;
    }

    /**
     * 
     */
    public void setUseInheritedIndexContent( boolean inherit )
    {
   		useInheritedIndexContent = new Boolean( inherit );
    }
    

    /**
     * Return whether the the jits settings should be inherited from the net folder server.
     * @return
     */
    public boolean getUseInheritedJitsSettings()
    {
    	boolean useInherited;
    	
    	// If the useInheritedJitsSettings field is null that means this binder existed
    	// before we added this field.
    	if ( useInheritedJitsSettings == null )
    	{
    		// Are we dealing with a home dir net folder?
    		if ( isHomeDir() )
    		{
    			// Yes
    			// Has the value of "enable jits" changed from the default?
    			// The default is true.
    			if ( isJitsEnabled() == false )
    			{
    				// Yes
    				useInherited = false;
    			}
    			else
    				useInherited = true;
    		}
    		else
    		{
    			// No
    			useInherited = false;
    		}
    	}
    	else
    	{
    		useInherited = useInheritedJitsSettings.booleanValue();
    	}
    	
    	return useInherited;
    }

    /**
     * 
     */
    public void setUseInheritedJitsSettings( boolean inherit )
    {
   		useInheritedJitsSettings = new Boolean( inherit );
    }
    
    /**
     * Return the computed value of "Enable Jits".  If this binder is inheriting the jits
     * settings then we will get the value of "enable jits" from the net folder server
     * this binder is pointing to.  Otherwise, we will use the value of "enable jits" from
     * this binder.
     */
    public boolean getComputedIsJitsEnabled()
    {
    	ResourceDriver resourceDriver;
    	
    	if ( getUseInheritedJitsSettings() == false )
    		return isJitsEnabled();
    	
    	resourceDriver = getResourceDriver();
    	if ( resourceDriver != null )
    	{
    		ResourceDriverConfig rdConfig;
    		
    		rdConfig = resourceDriver.getConfig();
    		if ( rdConfig != null )
    			return rdConfig.isJitsEnabled();
    	}
    	
    	return false;
    }
    
    /**
     * Return the computed value of "Jits max age".  If this binder is inheriting the jits
     * settings then we will get the value of "jits max age" from the net folder server
     * this binder is pointing to.  Otherwise, we will use the value of "jits max age" from
     * this binder.
     */
    public long getComputedJitsMaxAge()
    {
    	ResourceDriver resourceDriver;
    	
    	if ( getUseInheritedJitsSettings() == false )
    		return getJitsMaxAge();
    	
    	resourceDriver = getResourceDriver();
    	if ( resourceDriver != null )
    	{
    		ResourceDriverConfig rdConfig;
    		
    		rdConfig = resourceDriver.getConfig();
    		if ( rdConfig != null )
    			return rdConfig.getJitsMaxAge();
    	}
    	
    	return getJitsMaxAge();
    }
    
    /**
     * Return the computed value of "Jits max acl age".  If this binder is inheriting the jits
     * settings then we will get the value of "jits max acl age" from the net folder server
     * this binder is pointing to.  Otherwise, we will use the value of "jits max acl age" from
     * this binder.
     */
    public long getComputedJitsAclMaxAge()
    {
    	ResourceDriver resourceDriver;
    	
    	if ( getUseInheritedJitsSettings() == false )
    		return getJitsAclMaxAge();
    	
    	resourceDriver = getResourceDriver();
    	if ( resourceDriver != null )
    	{
    		ResourceDriverConfig rdConfig;
    		
    		rdConfig = resourceDriver.getConfig();
    		if ( rdConfig != null )
    			return rdConfig.getJitsAclMaxAge();
    	}
    	
    	return getJitsAclMaxAge();
    }
    
    public boolean isJitsEnabled() {
    	if(jitsEnabled == null)
    		return SPropsUtil.getBoolean("nf.jits.enabled", true);
    	else
    		return jitsEnabled.booleanValue();
    }
    public void setJitsEnabled(boolean jitsEnabled) {
    	this.jitsEnabled = jitsEnabled;
    }
    
	public long getJitsMaxAge() {
		if(jitsMaxAge == null)
			return NetFolderHelper.getDefaultJitsResultsMaxAge();
		else 
			return jitsMaxAge.longValue();
	}
	public void setJitsMaxAge(long jitsMaxAge) {
		this.jitsMaxAge = Long.valueOf(jitsMaxAge);
	}
    
	public long getJitsAclMaxAge() {
		if(jitsAclMaxAge == null)
			return NetFolderHelper.getDefaultJitsAclMaxAge();
		else 
			return jitsAclMaxAge.longValue();
	}
	public void setJitsAclMaxAge(long jitsAclMaxAge) {
		this.jitsAclMaxAge = Long.valueOf(jitsAclMaxAge);
	}
    
	/**
	 * 
	 */
	public Boolean getFullSyncDirOnly()
	{
		return fullSyncDirOnly;
	}
	
	public boolean isFullSyncDirOnly() {
		if(fullSyncDirOnly == null)
			return SPropsUtil.getBoolean("nf.full.sync.dir.only", false);
		else
			return fullSyncDirOnly.booleanValue();
	}
	public void setFullSyncDirOnly( Boolean fullSyncDirOnly ) {
		this.fullSyncDirOnly = fullSyncDirOnly;
	}
	
    /**
     * Return the sync schedule option.  Currently there are 2 possible values:
     * "Use sync schedule from net folder server" and "Use sync schedule from net folder"
     * @return
     */
    public SyncScheduleOption getSyncScheduleOption()
    {
    	if ( syncScheduleOption == null )
    		return null;
    	
    	return SyncScheduleOption.valueOf( syncScheduleOption.shortValue() );
    }

    /**
     * 
     */
    public void setSyncScheduleOption( SyncScheduleOption option )
    {
    	if ( option == null )
    		syncScheduleOption = null;
    	else
    		syncScheduleOption = new Short( option.getValue() );
    }
    
	public ResourceDriver getResourceDriver() {
		return ResourceDriverManagerUtil.findResourceDriver(getNetFolderServerId());
	}
	
}
