/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
/*
 * Created on Oct 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kablink.teaming.domain;

import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.util.api.ApiErrorCode;

/**
 * Exception thrown when there is no ProxyIdentity for a given ID. 
 * 
 * @author drfoster@novell.com
 */
public class NoProxyIdentityByTheIdException extends NoObjectByTheIdException {
    private static final String NoProxyIdentityByTheIdException_ErrorCode = "errorcode.no.proxy.identity.by.the.id";
    
    public NoProxyIdentityByTheIdException(Long proxyIdentityId) {
        super(NoProxyIdentityByTheIdException_ErrorCode, proxyIdentityId);
    }
    public NoProxyIdentityByTheIdException(Long proxyIdentityId, String message) {
        super(NoProxyIdentityByTheIdException_ErrorCode, proxyIdentityId, message);
    }
    public NoProxyIdentityByTheIdException(Long proxyIdentityId, String message, Throwable cause) {
        super(NoProxyIdentityByTheIdException_ErrorCode,proxyIdentityId, message, cause);
    }
    public NoProxyIdentityByTheIdException(Long proxyIdentityId, Throwable cause) {
        super(NoProxyIdentityByTheIdException_ErrorCode, proxyIdentityId, cause);
    }
	/* (non-Javadoc)
	 * @see org.kablink.teaming.exception.ApiErrorCodeSupport#getApiErrorCode()
	 */
	@Override
	public ApiErrorCode getApiErrorCode() {
		return ApiErrorCode.PROXY_IDENTITY_NOT_FOUND;
	}

}
