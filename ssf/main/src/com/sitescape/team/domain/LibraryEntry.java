/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.domain;

import java.io.Serializable;

public class LibraryEntry extends ZonedObject implements Serializable {
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
