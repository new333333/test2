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

import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.Validator;


public class Application extends ApplicationPrincipal implements IndividualPrincipal {

    private String postUrl;
    private Boolean trusted; // access="field"
    private Integer timeout; // in seconds, access="field"
    private Integer maxIdleTime; // in seconds, access="field"
    private Boolean sameAddrPolicy; // access="field"
    
    public Application() {
    	// The identity info is not applicable to application. However, because the database
    	// requires non-null value in these, we have to set it to some default values. 
    	setIdentityInfo(new IdentityInfo());
    }
    
	public EntityIdentifier.EntityType getEntityType() {
		return EntityIdentifier.EntityType.application;
	}

	public String getTitle() {
		// title is set by hibernate access=field
		//title is only kept in the db for sql queries
		String val = super.getTitle();
    	if (Validator.isNotNull(val)) return val;
    	return getName();		
	}

    public String getPostUrl() {
		return postUrl;
	}

	public void setPostUrl(String postUrl) {
		this.postUrl = postUrl;
	}
    
    public boolean isAllIndividualMember() {
    	return true; // any situation where this shouldn't be the case?
    }

	public boolean isTrusted() {
		if(trusted != null)
			return trusted.booleanValue();
		else
			return false;
	}

	public void setTrusted(boolean trusted) {
		this.trusted = Boolean.valueOf(trusted);
	}

	public int getTimeout() {
		if(timeout != null)
			return timeout.intValue();
		else
			return SPropsUtil.getInt("remoteapp.timeout");
	}

	public void setTimeout(int value) {
		if(value < 0)
			throw new IllegalArgumentException("Timeout value cannot be negative");
		this.timeout = Integer.valueOf(value);
	}

	public int getMaxIdleTime() {
		if(maxIdleTime != null)
			return maxIdleTime.intValue();
		else
			return SPropsUtil.getInt("remoteapp.maxIdleTime");
	}

	public void setMaxIdleTime(int value) {
		if(value < 0)
			throw new IllegalArgumentException("Max idle time cannot be negative");
		this.maxIdleTime = Integer.valueOf(value);
	}

	public boolean isSameAddrPolicy() {
		if(sameAddrPolicy != null)
			return sameAddrPolicy.booleanValue();
		else
			return true;
	}

	public void setSameAddrPolicy(boolean sameAddrPolicy) {
		this.sameAddrPolicy = Boolean.valueOf(sameAddrPolicy);
	}

}
