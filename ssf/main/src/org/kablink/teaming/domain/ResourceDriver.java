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

import java.util.Set;

import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.security.function.WorkArea;

public class ResourceDriver extends ZonedObject implements WorkArea {

	private Long id; // This is primary key
	private String name;
	private DriverType driverType;
	private Integer type;
	private Boolean readOnly;
	private Boolean synchTopDelete;
	private Boolean putRequiresContentLength;
	private Boolean allowSelfSignedCertificate;
	private String hostUrl;
	private String rootPath;
	private String accountName;
	private String password; //set by hibernate access="field" type="encrypted"
		
	public enum DriverType {
		filesystem (0),
		webdav (1);
		int dtValue;
		DriverType(int dtValue) {
			this.dtValue = dtValue;
		}
		public int getValue() {return dtValue;}
		public static DriverType valueOf(int type) {
			switch (type) {
			case 0: return DriverType.filesystem;
			case 1: return DriverType.webdav;
			default: return DriverType.filesystem;
			}
		}
	};

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
	
	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Boolean getSynchTopDelete() {
		return synchTopDelete;
	}

	public void setSynchTopDelete(Boolean synchTopDelete) {
		this.synchTopDelete = synchTopDelete;
	}

	public Boolean getPutRequiresContentLength() {
		return putRequiresContentLength;
	}

	public void setPutRequiresContentLength(Boolean putRequiresContentLength) {
		this.putRequiresContentLength = putRequiresContentLength;
	}

	public Boolean getAllowSelfSignedCertificate() {
		return allowSelfSignedCertificate;
	}

	public void setAllowSelfSignedCertificate(Boolean allowSelfSignedCertificate) {
		this.allowSelfSignedCertificate = allowSelfSignedCertificate;
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
		return null;
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
	
	
}
