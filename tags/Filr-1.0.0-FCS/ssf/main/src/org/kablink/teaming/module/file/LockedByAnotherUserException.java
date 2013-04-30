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
package org.kablink.teaming.module.file;

import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.exception.UncheckedCodedException;
import org.kablink.util.api.ApiErrorCode;


public class LockedByAnotherUserException extends UncheckedCodedException {
	
	private static final String AlreadyLockedByAnotherUserException_ErrorCode = "file.error.locked.by.another.user";

	private DefinableEntity entity;
	private FileAttachment fa;
	private Principal lockOwner;
	
	public LockedByAnotherUserException(DefinableEntity entity, 
			FileAttachment fa, Principal lockOwner) {
		super(AlreadyLockedByAnotherUserException_ErrorCode, new Object[] { 
				entity.getId(),
				fa.getRepositoryName(),
				fa.getFileItem().getName(),
				lockOwner.getName() });
		
		this.entity = entity;
		this.fa = fa;
		this.lockOwner = lockOwner;
	}

	public DefinableEntity getEntity() {
		return entity;
	}

	public FileAttachment getFileAttachment() {
		return fa;
	}

	public Principal getLockOwner() {
		return lockOwner;
	}
	
    public int getHttpStatusCode() {
    	return 403; // Forbidden
    }

	/* (non-Javadoc)
	 * @see org.kablink.teaming.exception.UncheckedCodedException#getApiErrorCode()
	 */
	@Override
	public ApiErrorCode getApiErrorCode() {
		return ApiErrorCode.FILE_LOCKED;
	}

}
