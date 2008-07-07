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

import com.sitescape.team.util.SPropsUtil;
import com.sitescape.util.Validator;

public class Application extends ApplicationPrincipal implements IndividualPrincipal {

    private String postUrl;
    private Boolean trusted; // access="field"
    private Integer timeout; // access="field"
    private String runAs;
    
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

	public void setTimeout(int timeout) {
		if(timeout < 0)
			throw new IllegalArgumentException("Timeout value cannot be negative");
		this.timeout = Integer.valueOf(timeout);
	}

	public String getRunAs() {
		return runAs;
	}

	public void setRunAs(String runAs) {
		this.runAs = runAs;
	}

}
