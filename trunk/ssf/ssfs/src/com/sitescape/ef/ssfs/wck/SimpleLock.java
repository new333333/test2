package com.sitescape.ef.ssfs.wck;

import java.util.Date;

import org.apache.slide.simple.store.WebdavStoreLockExtension.Lock;

public class SimpleLock implements Lock {

	private String id;
	private String subject;
	private Date expirationDate;
	
	public SimpleLock(String id, String subject, Date expirationDate) {
		this.id = id;
		this.subject = subject;
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
		return subject;
	}

}
