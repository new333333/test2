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

public class SimpleName {
	
	private SimpleNamePK id;

	private String emailAddress; // access="field"
	
	private Long binderId;
	// folder (= EntityIdentifier.EntityType.folder.name()), or 
	// workspace (= EntityIdentifier.EntityType.workspace.name())
	private String binderType; 
	
	public SimpleName() {}
	
	public SimpleName(Long zoneId, String name) {
		setId(new SimpleNamePK(zoneId, name));
		setName(name);
	}
	
	public SimpleName(Long zoneId, String name, Long binderId, String binderType) {
		this(zoneId, name);
		this.binderId = binderId;
		this.binderType = binderType;
	}
	
	public SimpleNamePK getId() {
		return id;
	}

	public void setId(SimpleNamePK id) {
		this.id = id;
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
		return id.getName();
	}
	public void setName(String name) {
		id.setName(name);
		setEmailAddress(EmailFromURL(name));
	}
	
	private String EmailFromURL(String url)
	{
		return url.replace('/', '.');
	}

	public String getEmailAddress() {
		return emailAddress;
	}
	protected void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public static class SimpleNamePK implements Serializable {
		private static final long serialVersionUID = 1L;
		private Long zoneId;
		private String name;
		public SimpleNamePK() {
		}
		public SimpleNamePK(Long zoneId, String name) {
			this.zoneId = zoneId;
			this.name = name;
		}
		public Long getZoneId() {
			return zoneId;
		}
		public void setZoneId(Long zoneId) {
			this.zoneId = zoneId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public boolean equals(Object obj) {
	        if(this == obj)
	            return true;
	        if ((obj == null) || !(obj instanceof SimpleNamePK))
	            return false;     
	        SimpleNamePK sm = (SimpleNamePK) obj;
	        if(zoneId.equals(sm.zoneId) && name.equals(sm.name))
	        	return true;
	        else
	        	return false;
		}
		public int hashCode() {
	       	int hash = 7;
	    	hash = 31*hash + zoneId.hashCode();
	    	hash = 31*hash + name.hashCode();
	    	return hash;
		}		
	}
}
