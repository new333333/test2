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

import java.io.Serializable;

public class LibraryEntry implements Serializable {
	private final static long serialVersionUID=1;
	protected Long binderId;
	protected String name=""; //set by hibernate access=Field
	protected Long entityId; //must be folderEntryId
	protected Long type=FILE;
	public static Long FILE=Long.valueOf(1);
	public static Long TITLE=Long.valueOf(2);
	
	//used only by hibernate
	protected LibraryEntry() {
		
	}
	public LibraryEntry(Long binderId, Long type, String name) {
		this.binderId = binderId;
		if (type == FILE) setFileName(name);
		else setTitle(name);
	}
	/**
 	 * @hibernate.key-property 
 	 */
	public Long getBinderId() {
		return binderId;
	}
	protected void setBinderId(Long binderId) {
		this.binderId = binderId;
	}
	/**
 	 * @hibernate.key-property 
 	 */
	public Long getType() {
		return type;
	}
	protected void setType(Long type) {
		this.type = type;
	}
	/**
 	 * @hibernate.key-property 
 	 */
	public String getName() {
		return name;
	}
	protected void setName(String name) {
		this.name = name;
	}
	public String getFileName() {
		return name;
	}
	public void setFileName(String fileName) {
		setType(FILE);
		if (fileName != null) setName(fileName.toLowerCase());
	}
	public String getTitle() {
		return name;
	}
	public void setTitle(String title) {
		setName(title);
		setType(TITLE);
	}
	/**
	 * @hibernate.property
	 * @return
	 */
	public Long getEntityId() {
		return entityId;
	}
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof LibraryEntry) {
			LibraryEntry pk = (LibraryEntry) obj;
			if (pk.getBinderId().equals(binderId) && 
					pk.getName().equals(name)) return true;
		}
		return false;
	}
	public int hashCode() {
		return 31*binderId.hashCode() + name.hashCode();
	}
}
