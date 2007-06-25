/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.domain;

import java.util.Date;


/**
 * @hibernate.class table="SS_LicenseStats"
 * 
 * @author Joe
 *
 * Log information used for license monitoring.
 * 
 */
public class LicenseStats {
	protected String id;
    protected Long zoneId; 
    protected Date snapshotDate;
	protected long internalUserCount;
	protected long externalUserCount;
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
	public LicenseStats(Long zoneId, Date snapshotDate, long internalUserCount, long externalUserCount, long checksum) {
		super();
		setZoneId(zoneId);
		setSnapshotDate(snapshotDate);
		setInternalUserCount(internalUserCount);
		setExternalUserCount(externalUserCount);
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
