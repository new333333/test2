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


/**
 * @hibernate.class table="SS_LicenseStats"
 * 
 * @author Joe
 *
 * Log information used for license monitoring.
 * 
 */
public class LicenseStats extends ZonedObject {
	protected String id;
    protected Date snapshotDate;
	protected long internalUserCount;		//Local internal users 
	protected long externalUserCount;		//Ldap users
	protected Long openIdUserCount;			//OpenId users
	protected Long otherExtUserCount;		//Self-registered external local users
	protected Boolean guestAccessEnabled;	//Guest access enabled
	protected Long activeUserCount;
	protected long checksum;

	/**
	 * 
	 */
	public LicenseStats() {
		super();
	}

	/**
	 * @param snapshotDate
	 * @param internalUserCount
	 * @param externalUserCount
	 * @param checksum
	 */
	public LicenseStats(Long zoneId, Date snapshotDate, long internalUserCount, long externalUserCount, 
			long openIdUserCount, long otherExtUserCount, boolean guestAccessEnabled, long activeUserCount, 
			long checksum) {
		super();
		setZoneId(zoneId);
		setSnapshotDate(snapshotDate);
		setInternalUserCount(internalUserCount);
		setExternalUserCount(externalUserCount);
		setOpenIdUserCount(openIdUserCount);
		setOtherExtUserCount(otherExtUserCount);
		setGuestAccessEnabled(guestAccessEnabled);
		setActiveUserCount(activeUserCount);
		setChecksum(checksum);
	}
	
	/**
	 * @hibernate.id generator-class="uuid.hex" unsaved-value="null" node="@id"
	 * @hibernate.column name="id" sql-type="char(32)"
	 */    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    /**
     * @hibernate.property not-null="true"
     */
    public Long getZoneId() {
    	return this.zoneId;
    }
    public void setZoneId(Long zoneId) {
    	this.zoneId = zoneId;
    }
	/**
	 * @hibernate.property  
	 * @return the checksum
	 */
	public long getChecksum() {
		return checksum;
	}

	/**
	 * @param checksum the checksum to set
	 */
	public void setChecksum(long checksum) {
		this.checksum = checksum;
	}

	/**
	 * @hibernate.property  
	 * @return the externalUserCount
	 */
	public long getExternalUserCount() {
		return externalUserCount;
	}

	/**
	 * @param externalUserCount the externalUserCount to set
	 */
	public void setExternalUserCount(long externalUserCount) {
		this.externalUserCount = externalUserCount;
	}

	/**
	 * @hibernate.property  
	 * @return the internalUserCount
	 */
	public long getInternalUserCount() {
		return internalUserCount;
	}

	/**
	 * @param internalUserCount the internalUserCount to set
	 */
	public void setInternalUserCount(long internalUserCount) {
		this.internalUserCount = internalUserCount;
	}

	/**
	 * @hibernate.property  
	 * @return the activeUserCount
	 */
	public long getActiveUserCount() {
		if (activeUserCount == null) return 0;
		return activeUserCount;
	}

	/**
	 * @param activeUserCount  the activeUserCount to set
	 */
	public void setActiveUserCount(Long activeUserCount) {
		this.activeUserCount = activeUserCount;
	}

	
	public long getOpenIdUserCount() {
		if (openIdUserCount == null) return 0;
		return openIdUserCount;
	}

	public void setOpenIdUserCount(Long openIdUserCount) {
		this.openIdUserCount = openIdUserCount;
	}

	public long getOtherExtUserCount() {
		if (otherExtUserCount == null) return 0;
		return otherExtUserCount;
	}

	public void setOtherExtUserCount(Long otherExtUserCount) {
		this.otherExtUserCount = otherExtUserCount;
	}

	public boolean getGuestAccessEnabled() {
		if (guestAccessEnabled == null) return false;
		return guestAccessEnabled;
	}

	public void setGuestAccessEnabled(Boolean guestAccessEnabled) {
		this.guestAccessEnabled = guestAccessEnabled;
	}

	/**
	 * @hibernate.property  
	 * @return the snapshotDate
	 */
	public Date getSnapshotDate() {
		return snapshotDate;
	}

	/**
	 * @param snapshotDate the snapshotDate to set
	 */
	public void setSnapshotDate(Date snapshotDate) {
		this.snapshotDate = snapshotDate;
	}

}
