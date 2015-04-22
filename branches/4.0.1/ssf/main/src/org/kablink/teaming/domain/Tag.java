/**
\ * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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


/**
 * 
 * @hibernate.class table="SS_Tags" dynamic-update="true" dynamic-insert="true" 
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * @author Janet McCann
 *
 */
public class Tag extends ZonedObject {
	protected String id;
	protected String name="";
	protected EntityIdentifier ownerId;
	protected EntityIdentifier entityId;
	protected boolean isPublic=true;
	
	public Tag() {
	}
	
	public Tag(EntityIdentifier entityId) {
		this.entityId = entityId;		
	}
	public Tag(Tag tag) {
		name = tag.name;
		ownerId = tag.ownerId;
		entityId = tag.entityId;
		isPublic = tag.isPublic;
	}
	/**
	 * Artificial database primary key
	 * @hibernate.id generator-class="uuid.hex" unsaved-value="null"
	 * @hibernate.column name="id" sql-type="char(32)"
	 */    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    /**
     * The entity the tag is marking
     * @hibernate.componenent
     * @return
     */
    public EntityIdentifier getEntityIdentifier() {
    	return entityId;
    }
    public void setEntityIdentifier(EntityIdentifier entityId) {
    	this.entityId = entityId;
    }

    /**
     * The Entity that owns the tag
     * @hibernate.componenent
     * @return
     */
    public EntityIdentifier getOwnerIdentifier() {
    	return ownerId;
    }
    public void setOwnerIdentifier(EntityIdentifier ownerId) {
    	this.ownerId = ownerId;
    }
	public boolean isOwner(EntityIdentifiable entity) {
 		if (entity == null) return false;
 		if (entity.getEntityIdentifier().equals(ownerId)) return true;
 		return false;
 	}
 	public boolean isOwner(EntityIdentifier entityId) {
 		if (entityId == null) return false;
 		if (this.entityId.equals(ownerId)) return true;
 		return false;
 	}
	/**
	 * @hibernate.property length="64"
	 * @return
	 */
	public String getName() {
	    return name;
	}
	public void setName(String name) {
	    this.name = name;
	}	
	/**
	 * @hibernate.property
	 */
	public boolean isPublic() {
		return isPublic;
	}
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	
}
