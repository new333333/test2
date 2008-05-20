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

public class SimpleName extends ZonedObject implements Serializable {

	public static final String TYPE_URL = "url";
	
	// The following two fields plus the zone id make up the primary key.
	private String name; // access="field"
	private String type;
	
	private Long binderId;
	// folder (= EntityIdentifier.EntityType.folder.name()), or 
	// workspace (= EntityIdentifier.EntityType.workspace.name())
	private String binderType; 
	
	public SimpleName() {}
	
	public SimpleName(Long zoneId, String name, String type) {
		this.zoneId = zoneId;
		this.name = name;
		this.name = name;
		this.type = type;
	}
	
	public SimpleName(Long zoneId, String name, String type, Long binderId, String binderType) {
		this(zoneId, name, type);
		this.binderId = binderId;
		this.binderType = binderType;
	}
	
	public Long getBinderId() {
		return binderId;
	}
	public void setBinderId(Long binderId) {
		this.binderId = binderId;
	}
	public String getBinderType() {
		return binderType;
	}
	public void setBinderType(String binderType) {
		this.binderType = binderType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if ((obj == null) || !(obj instanceof SimpleName))
            return false;
            
        SimpleName sm = (SimpleName) obj;
        if(zoneId.equals(sm.zoneId) && name.equals(sm.name) && type.equals(sm.type))
        	return true;
        else
        	return false;
	}
	public int hashCode() {
       	int hash = 7;
    	hash = 31*hash + zoneId.hashCode();
    	hash = 31*hash + name.hashCode();
    	hash = 31*hash + type.hashCode();
    	return hash;
	}
}
