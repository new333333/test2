package com.sitescape.ef.domain;

import java.io.Serializable;

public class LibraryEntry implements Serializable {
	private final static long serialVersionUID=1;
	protected Long binderId;
	protected String name=""; //set by hibernate access=Field
	protected Long entityId;
	
	//used only by hibernate
	protected LibraryEntry() {
		
	}
	public LibraryEntry(Long binderId, String name) {
		this.binderId = binderId;
		setName(name);
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
 	 * @hibernate.key-property access="field"
 	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		if (name != null) this.name = name.toLowerCase();
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
