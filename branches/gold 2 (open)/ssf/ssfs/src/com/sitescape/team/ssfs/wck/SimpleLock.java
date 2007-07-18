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
package com.sitescape.team.ssfs.wck;

import java.util.Date;

import org.apache.slide.simple.store.WebdavStoreLockExtension.Lock;

public class SimpleLock implements Lock {

	private String id;
	private String subject;
	private Date expirationDate;
	private String owner;
	
	public SimpleLock(String id, String subject, Date expirationDate, String owner) {
		this.id = id;
		this.subject = subject;
		this.expirationDate = expirationDate;
		this.owner = owner;
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
		return subject;
	}

	public String getOwner() { 
		return owner;
	}

}
