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
 * This class represents an email address for a principal.
 *
 */
//This could be a composite element, but then the primary key contains all fields, and don't want that.
public class EmailAddress extends ZonedObject {

	private static final long serialVersionUID = 1L;
	protected String address;
	protected Long zoneId; //hibernate field access
	protected ID id;

	protected EmailAddress() {
	}
	public EmailAddress(Principal principal, String type, String address) {
		this.id = new ID(principal, type);
		this.address = address;
	}

	public ID getId() {
		return id;
	}
	public void setId(ID id) {
		this.id = id;
	}
	/**
	 * Return email address.
	 * @hibernate.property length="356"
	 * @return
	 */
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public static class ID implements Serializable {
		private Long principal;
		private String type;
		private transient Principal principalObj;
		protected ID() {
		}
		public ID(Principal principalObj, String type) {
			this.principalObj = principalObj;
			this.principal = principalObj.getId();
			this.type = type;
		}
		public Long getPrincipal() {
			// Using this unusual technique to fix the bug 558061 while minimizing impact on the rest of the system. 
			if(principal == null && principalObj != null)
				principal = principalObj.getId();
			return principal;
		}
		public void setPrincipal(Long principal) {
			this.principal = principal;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public boolean equals(Object obj) {
			if(obj == null) 
				return false;
			if(obj == this) 
				return true;
			if(getPrincipal() == null || getType() == null)
				return false;
			if(obj instanceof ID) {
				ID id = (ID) obj;
				if(id.getPrincipal() == null || id.getType() == null)
					return false;
				if(id.getPrincipal().equals(getPrincipal()) && id.getType().equals(getType()))
					return true;
			}
			return false;
		}
		public int hashCode() {
			int result = 0;
			if(getPrincipal() != null)
				result = 31*getPrincipal().hashCode();
			if(getType() != null)
				result += getType().hashCode();
			return result;
		}
	}
}
