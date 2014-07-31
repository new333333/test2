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
package org.kablink.teaming.security.accesstoken.impl;

import java.io.Serializable;
import java.util.Date;

import org.kablink.teaming.domain.ZonedObject;


public abstract class TokenInfo extends ZonedObject implements Serializable {

	private String id;
	private String seed;
	private Date lastAccessTime;
	
	public TokenInfo() {
		this.lastAccessTime = new Date(); // initialize to current time
	}
	
	public TokenInfo(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSeed() {
		return seed;
	}
	public void setSeed(String seed) {
		if(seed.length() > 128)
			seed = seed.substring(0, 128);
		this.seed = seed;
	}
	
	public Date getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

    public int hashCode() {
    	if(id != null)
    		return id.hashCode();
    	else
    		return "".hashCode();
    }
     
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if (obj == null) 
            return false;
        
        if (!(obj instanceof TokenInfo)) return false;
        TokenInfo o = (TokenInfo) obj;

        if (o.getId() == null) return false;
        if (getId() == null) return false;
        if (this.id.equals(o.getId()))
            return true;
                
        return false;
    }

}
