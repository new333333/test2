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
import org.kablink.teaming.ObjectKeys;


/**
 * @author Janet McCann
 *
 */
public class UserPropertiesPK implements Serializable {
	private final static long serialVersionUID=1;
	private Long binderId,principalId;
	public UserPropertiesPK() {
		
	}
	public UserPropertiesPK(Long principalId) {
		this.principalId = principalId;
		binderId = ObjectKeys.RESERVED_BINDER_ID;
	}
	public UserPropertiesPK(Long principalId, Long binderId) {
		this.principalId = principalId;
		this.binderId = binderId;
	}
	/**
 	 * @hibernate.key-property position="1"
 	 */
	public Long getPrincipalId() {
		return principalId;
	}
	public void setPrincipalId(Long principalId) {
		this.principalId = principalId;
	}
	/**
 	 * @hibernate.key-property position="2"
 	 */
	public Long getBinderId() {
		return binderId;
	}
	public void setBinderId(Long binderId) {
		this.binderId = binderId;
	}
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof UserPropertiesPK) {
			UserPropertiesPK pk = (UserPropertiesPK) obj;
			if (pk.getPrincipalId().equals(principalId) && 
					pk.getBinderId().equals(binderId)) return true;
		}
		return false;
	}
	public int hashCode() {
		return 31*binderId.hashCode() + principalId.hashCode();
	}
	public String toString() {
		return "[principalId:" + principalId + ",binderId:" + binderId + "]";
	}
}
