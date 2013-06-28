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

import org.kablink.teaming.security.accesstoken.AccessToken;

public class TokenInfoRequest extends TokenInfo {
	
	private static final long serialVersionUID = 1L;
	
	private Long applicationId; 
	private Long userId; 
	private Long binderId; // may be null
	private Integer binderAccessConstraints; // access by field; meaningful and required only when binderId is specified
	
	public TokenInfoRequest(Long applicationId, Long userId, Long binderId, 
			AccessToken.BinderAccessConstraints binderAccessConstraints, String seed) {
		this(applicationId, userId);
		this.binderId = binderId;
		setBinderAccessConstraints(binderAccessConstraints);
		setSeed(seed);
	}
	
	public TokenInfoRequest(Long applicationId, Long userId) {
		this.applicationId = applicationId;
		this.userId = userId;
	}
	
	public TokenInfoRequest() {
	}

	public Long getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}
	public Long getBinderId() {
		return binderId;
	}
	public void setBinderId(Long binderId) {
		this.binderId = binderId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public AccessToken.BinderAccessConstraints getBinderAccessConstraints() {
		if(binderAccessConstraints != null)
			return AccessToken.BinderAccessConstraints.valueOf(binderAccessConstraints.intValue());
		else
			return AccessToken.BinderAccessConstraints.NONE;
	}

	public void setBinderAccessConstraints(AccessToken.BinderAccessConstraints binderAccessConstraints) {
		if(binderAccessConstraints != null)
			this.binderAccessConstraints = Integer.valueOf(binderAccessConstraints.getNumber());
		else
			this.binderAccessConstraints = null;
	}

}
