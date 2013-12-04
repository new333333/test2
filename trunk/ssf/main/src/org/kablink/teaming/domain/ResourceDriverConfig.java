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
package org.kablink.teaming.domain;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;

public class ResourceDriverConfig extends ZonedObject implements WorkArea {

	public static final String WORKAREA_TYPE = "resourceDriver";
	
	private Long id; // This is primary key
	private String name;
	private DriverType driverType;
	private Integer type;
	private Boolean readOnly;
	private Boolean synchTopDelete;
	private Boolean putRequiresContentLength;
	private Boolean allowSelfSignedCertificate;
	private Boolean fullSyncDirOnly;
	private String hostUrl;
	private String rootPath;
	private String accountName;
	private String password; //set by hibernate access="field" type="encrypted"
	private String shareName;
	private String serverName;
	private String serverIP;
	private String volume;
	private Date modifiedOn;
	private ChangeDetectionMechanism changeDetectionMechanism;
	private Short authenticationType;
	private Boolean useDirectoryRights;
	private Integer cachedRightsRefreshInterval;
		
	public enum DriverType {
		filesystem (0),
		webdav (1),
		windows_server (2),
		netware (3),
		oes (4),
		famt (5),
		cloud_folders (6),
		share_point_2010 (7),
		share_point_2013 (8);
		int dtValue;
		DriverType(int dtValue) {
			this.dtValue = dtValue;
		}
		public int getValue() {return dtValue;}
		public static DriverType valueOf(int type) {
			switch (type) {
			case 0: return DriverType.filesystem;
			case 1: return DriverType.webdav;
			case 2: return DriverType.windows_server;
			case 3: return DriverType.netware;
			case 4: return DriverType.oes;
			case 5: return DriverType.famt;
			case 6: return DriverType.cloud_folders;
			case 7: return DriverType.share_point_2010;
			case 8: return DriverType.share_point_2013;
			default: return DriverType.filesystem;
			}
		}
	};
	
	public enum ChangeDetectionMechanism {
		/**
		 * Change list can not be obtained. Only brute-force scanning will do.
		 */
		none,
		/**
		 * Change list can be obtained via an agent installed on the file server.
		 */
		agent,
		/**
		 * Change list can be obtained via log/journal information available from the file system.
		 */
		log
	}

	/**
	 * 
	 */
	public enum AuthenticationType
	{
		kerberos( (short)1 ),
		
		ntlm( (short)2 ),
		
		kerberos_then_ntlm( (short)3 ),
		
		nmas( (short) 4 );
		
		private short m_value;

		/**
		 * 
		 */
		AuthenticationType( short value )
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
		public static AuthenticationType valueOf( short value )
		{
			switch(value)
			{
			case 1:
				return AuthenticationType.kerberos;
				
			case 2:
				return AuthenticationType.ntlm;
				
			case 3:
				return AuthenticationType.kerberos_then_ntlm;

			case 4:
				return AuthenticationType.nmas;
				
			default:
				throw new IllegalArgumentException( "Invalid db value " + value + " for enum AuthenticationType" );
			}
		}
	}

    public boolean equals(Object obj) {
    	if(!(obj instanceof ResourceDriverConfig))
    		return false;
    	ResourceDriverConfig config = (ResourceDriverConfig)obj;
    	if(!objectEquals(id, config.id))
    		return false;
    	if(!objectEquals(name, config.name))
    		return false;
    	if(!objectEquals(driverType, config.driverType))
    		return false;
    	if(!objectEquals(type, config.type))
    		return false;
    	if(!objectEquals(readOnly, config.readOnly))
    		return false;
    	if(!objectEquals(synchTopDelete, config.synchTopDelete))
    		return false;
    	if(!objectEquals(putRequiresContentLength, config.putRequiresContentLength))
    		return false;
    	if(!objectEquals(allowSelfSignedCertificate, config.allowSelfSignedCertificate))
    		return false;
    	if ( !objectEquals( fullSyncDirOnly, config.fullSyncDirOnly ) )
    		return false;
    	if(!objectEquals(hostUrl, config.hostUrl))
    		return false;
    	if(!objectEquals(rootPath, config.rootPath))
    		return false;
    	if(!objectEquals(accountName, config.accountName))
    		return false;
    	if(!objectEquals(password, config.password))
    		return false;
    	if(!objectEquals(shareName, config.shareName))
    		return false;
    	if(!objectEquals(serverName, config.serverName))
    		return false;
    	if(!objectEquals(serverIP, config.serverIP))
    		return false;
    	if(!objectEquals(volume, config.volume))
    		return false;
    	if(!objectEquals(modifiedOn, config.getModifiedOn()))
    		return false;
    	if(!objectEquals(getChangeDetectionMechanism(), config.getChangeDetectionMechanism()))
    		return false;
    	
    	if ( !objectEquals( getAuthenticationType(), config.getAuthenticationType() ) )
    		return false;

    	if ( !objectEquals( getUseDirectoryRights(), config.getUseDirectoryRights() ) )
    		return false;
    	
    	if ( !objectEquals( getCachedRightsRefreshInterval(), config.getCachedRightsRefreshInterval() ) )
    		return false;

    	return true;
    }
    
	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}

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

	protected int getType() {
		return driverType.getValue();
	}
	protected void setType(int type) {
		for (DriverType dT : driverType.values()) {
			if (type == dT.getValue()) {
				driverType=dT;
				break;
			}
		}
		this.type = driverType.getValue();
	}
	
	public DriverType getDriverType() {
		return this.driverType;
	}
	public void setDriverType(DriverType type) {
		this.driverType = type;
	}
	
	public boolean isReadOnly() {
		if(readOnly == null)
			return true;
		else
			return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isSynchTopDelete() {
		if(synchTopDelete == null)
			return false;
		else 
			return synchTopDelete;
	}

	public void setSynchTopDelete(boolean synchTopDelete) {
		this.synchTopDelete = synchTopDelete;
	}

	public boolean isPutRequiresContentLength() {
		if(putRequiresContentLength == null)
			return false;
		else
			return putRequiresContentLength;
	}

	public void setPutRequiresContentLength(boolean putRequiresContentLength) {
		this.putRequiresContentLength = putRequiresContentLength;
	}

	public boolean isAllowSelfSignedCertificate() {
		if(allowSelfSignedCertificate == null)
			return false;
		else 
			return allowSelfSignedCertificate;
	}

	public void setAllowSelfSignedCertificate(boolean allowSelfSignedCertificate) {
		this.allowSelfSignedCertificate = allowSelfSignedCertificate;
	}

	/**
	 * 
	 */
	public Boolean getFullSyncDirOnly()
	{
		return fullSyncDirOnly;
	}

	/**
	 * 
	 */
	public void setFullSyncDirOnly( Boolean fullSyncDirOnly )
	{
		this.fullSyncDirOnly = fullSyncDirOnly;
	}

	public String getHostUrl() {
		return hostUrl;
	}

	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	//Workarea implementation
	@Override
	public Long getWorkAreaId() {
		return this.id;
	}

	@Override
	public String getWorkAreaType() {
		return WORKAREA_TYPE;
	}

	@Override
	public WorkArea getParentWorkArea() {
		return null;
	}

	@Override
	public boolean isFunctionMembershipInheritanceSupported() {
		return false;
	}

	@Override
	public boolean isFunctionMembershipInherited() {
		return false;
	}

	@Override
	public void setFunctionMembershipInherited(boolean functionMembershipInherited) {
	}

	@Override
	public boolean isExtFunctionMembershipInherited() {
		return false;
	}

	@Override
	public void setExtFunctionMembershipInherited(boolean functionMembershipInherited) {
	}

	@Override
	public Long getOwnerId() {
		return null;
	}

	@Override
	public Principal getOwner() {
		return null;
	}

	@Override
	public void setOwner(Principal owner) {
	}

	@Override
	public boolean isTeamMembershipInherited() {
		return false;
	}

	@Override
	public Set<Long> getTeamMemberIds() {
		return null;
	}

	@Override
	public void setTeamMemberIds(Set<Long> memberIds) {
	}

	@Override
	public Set<Long> getChildWorkAreas() {
		return null;
	}
	
	//Routine to determine if this resource driver handles its own acls
	public boolean isAclAware() {
		if (ResourceDriverConfig.DriverType.famt == this.getDriverType() ||
				ResourceDriverConfig.DriverType.windows_server == this.getDriverType() ||
				ResourceDriverConfig.DriverType.netware == this.getDriverType() ||
				ResourceDriverConfig.DriverType.oes == this.getDriverType() ||
				ResourceDriverConfig.DriverType.cloud_folders == this.getDriverType() ||
				ResourceDriverConfig.DriverType.share_point_2010 == this.getDriverType() ||
				ResourceDriverConfig.DriverType.share_point_2013 == this.getDriverType()) {
			return true;
		} else {
			return false;
		}
	}

    public boolean isAclExternallyControlled() {
    	return Boolean.FALSE;
    }
    public List<WorkAreaOperation> getExternallyControlledRights() {
    	return WorkAreaOperation.getDefaultExternallyControlledRights();
    }
    public String getRegisteredRoleType() {
    	return "";
    }

	public String getShareName() {
		return shareName;
	}

	public void setShareName(String shareName) {
		this.shareName = shareName;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}
	
	public Date getModifiedOn() {
		if (modifiedOn == null) {
			modifiedOn = new Date();
			modifiedOn.setTime(0);
		}
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	
	/**
	 * 
	 */
	public AuthenticationType getAuthenticationType()
	{
		if ( authenticationType == null )
			return null;
		
		return AuthenticationType.valueOf( authenticationType.shortValue() );
	}
	
	/**
	 * 
	 */
	public void setAuthenticationType( AuthenticationType type )
	{
		if ( type == null )
			authenticationType = null;
		else
			authenticationType = type.getValue();
	}

	/**
	 * 
	 */
	public Boolean getUseDirectoryRights()
	{
		return useDirectoryRights;
	}

	/**
	 * 
	 */
	public void setUseDirectoryRights( Boolean value )
	{
		useDirectoryRights = value;
	}
	
	/**
	 * 
	 */
	public Integer getCachedRightsRefreshInterval()
	{
		return cachedRightsRefreshInterval;
	}
	
	/**
	 * 
	 */
	public void setCachedRightsRefreshInterval( Integer value )
	{
		cachedRightsRefreshInterval = value;
	}


	// Used by application
	public ChangeDetectionMechanism getChangeDetectionMechanism() {
		if(changeDetectionMechanism == null)
			return ChangeDetectionMechanism.none; // Default to none for backward compatibility
		else
			return changeDetectionMechanism;
	}

	public void setChangeDetectionMechanism(
			ChangeDetectionMechanism changeDetectionMechanism) {
		this.changeDetectionMechanism = changeDetectionMechanism;
	}
	
	// Used by Hibernate only
	private String getChangeDetectionMechanismStr() {
		if(changeDetectionMechanism == null)
			return null;
		else
			return changeDetectionMechanism.name();
	}
	
	private void setChangeDetectionMechanism(String changeDetectionMechanismStr) {
		if(changeDetectionMechanismStr == null) {
			changeDetectionMechanism = null;
		}
		else {
			changeDetectionMechanism = ChangeDetectionMechanism.valueOf(changeDetectionMechanismStr);
		}
	}

	private boolean objectEquals(Object first, Object second) {
		if(first != null) {
			if(second != null) {
				if(first.getClass().equals(second.getClass())) {
					return first.equals(second);
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
		else {
			if(second != null) {
				return false;
			}
			else {
				return true;
			}
		}
	}
}
