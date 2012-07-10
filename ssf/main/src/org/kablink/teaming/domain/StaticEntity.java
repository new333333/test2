/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
 * @author jong
 *
 */
public abstract class StaticEntity extends ZonedObject implements Entity {

	protected Long id;
    protected EntityIdentifier entityIdentifier;
	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.domain.Entity#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.domain.Entity#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.domain.Entity#getEntityIdentifier()
	 */
	@Override
	public EntityIdentifier getEntityIdentifier() {
		if (entityIdentifier == null) 
			entityIdentifier = new EntityIdentifier(getId(), getEntityType());
		return entityIdentifier;
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.domain.Entity#getEntityTypedId()
	 */
	@Override
	public String getEntityTypedId() {
	   	return getEntityType().name() + "_" + getEntityIdentifier().getEntityId();
	}

    public int hashCode() {
    	if(id != null)
    		return id.hashCode();
    	else
    		return Long.valueOf(0).hashCode();
    }
    
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if (obj == null)
            return false;
      
        if (!(obj instanceof StaticEntity)) 
        	return false;

        StaticEntity o = (StaticEntity) obj;
        if (o.getId() == null) 
        	return false;
        if (getId() == null) 
        	return false;
        return this.id.equals(o.getId());
    }    

}
