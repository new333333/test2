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
public class IdentityInfo {

	// internal vs non-internal
	private boolean internal = true;
	
	// Identity sources
	private boolean fromLdap = false;
	private boolean fromLocal = true;
	private boolean fromOpenid = false;
	
	/**
	 * Constructs IdentityInfo with default settings, which designates it as "internal" and "from local".
	 */
	public IdentityInfo() {}
	
	/**
	 * Constructs IdentityInfo with explicit settings.
	 * 
	 * @param internal
	 * @param fromLdap
	 * @param fromLocal
	 * @param fromOpenid
	 */
	public IdentityInfo(boolean internal, boolean fromLdap, boolean fromLocal, boolean fromOpenid) {
		this.internal = internal;
		this.fromLdap = fromLdap;
		this.fromLocal = fromLocal;
		this.fromOpenid = fromOpenid;
	}

	public boolean isFromLdap() {
		return fromLdap;
	}

	public void setFromLdap(boolean fromLdap) {
		this.fromLdap = fromLdap;
	}

	public boolean isFromLocal() {
		return fromLocal;
	}

	public void setFromLocal(boolean fromLocal) {
		this.fromLocal = fromLocal;
	}

	public boolean isFromOpenid() {
		return fromOpenid;
	}

	public void setFromOpenid(boolean fromOpenid) {
		this.fromOpenid = fromOpenid;
	}

	public boolean isInternal() {
		return internal;
	}

	public void setInternal(boolean internal) {
		this.internal = internal;
	}
	
	public void validate() throws IllegalArgumentException {
		boolean result;
		if(internal) {
			if(fromLdap) {
				result = !fromLocal && !fromOpenid;
			}
			else {
				if(fromLocal) {
					result = !fromOpenid;
				}
				else {
					result = false;
				}
			}
		}
		else {
			if(fromLdap) {
				result = false;
			}
			else {
				/*
				// In this case, it's possible that both fromLocal and fromOpenid are false, which
				// can happen at the time the user record is first created from the sharing dialogue.
				// Appropriate flag(s) should be set once the user accepts the invitation and select
				// either OpenID or Filr managed account or both.
				result = true;
				*/

				// TODO For now, I will assume that external users always use OpenID.
				if(fromLocal) {
					result = true;
				}
				else {
					result = fromOpenid;
				}
			}
		}
		
		if(!result)
			throw new IllegalArgumentException("Identity info is invalid");
	}
}
