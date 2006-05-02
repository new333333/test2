package com.sitescape.ef.ssfs.wck;

import java.util.Date;

import org.apache.slide.simple.store.WebdavStoreLockExtension.Lock;

public class SimpleLock implements Lock {

	private String id;
	private String ownerZoneName;
	private String ownerUserName;
	private Date expirationDate;
	
	public SimpleLock(String id, String zoneName, String userName, Date expirationDate) {
		this.id = id;
		this.ownerZoneName = zoneName;
		this.ownerUserName = userName;
		this.expirationDate = expirationDate;
	}
	
	public String getId() {
		return id;
	}

	public boolean isExclusive() {
		return true; // We only support exclusive write lock
	}

	public boolean isInheritable() {
		return false; // We don't support lock for folders
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public String getSubject() {
		// Subject string is computed rather than stored.
		return Util.makeSubject(ownerZoneName, ownerUserName);
	}

}
