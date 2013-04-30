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
import java.io.Serializable;

/**
 * This is a persistent class that is defined externally.
 * 
 * This is implemented as a class so we can do acl checking and group membership 
 * checking without loading in
 * the user and group records. The same table is referenced from
 * Principal.java and Group.java but in those cases the ids are mapped to real objects
 */
public class Membership implements Serializable {
    Long user_id;
    Long group_id;
    public Membership(){
    }
    public Membership(Long group, Long user) {
    	group_id = group;
		user_id = user;
    }
    /**
     * @hibernate.property
     * @hibernate.column name="user"
     * @return
     */
    public Long getUserId() {
        return this.user_id;
    }
    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }
    /**
     * @hibernate.property
     * @hibernate.column name="group"
     * @return
     */
    public Long getGroupId() {
        return this.group_id;
    }
    public void setGroupId(Long group_id) {
        this.group_id = group_id;
    }    

    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if ((obj == null) || !(obj instanceof Membership))
            return false;
            
        Membership mem = (Membership) obj;
        if (!(user_id.equals(mem.getUserId()))) return false;
        if (!(group_id.equals(mem.getGroupId()))) return false;
        return true;
    }
    public int hashCode() {
       	int hash = 7;
    	hash = 31*hash + user_id.hashCode();
    	hash = 31*hash + group_id.hashCode();
    	return hash;
    }
}
