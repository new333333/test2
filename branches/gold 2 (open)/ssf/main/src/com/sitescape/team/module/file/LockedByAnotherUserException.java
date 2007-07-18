/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.file;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.exception.UncheckedCodedException;

public class LockedByAnotherUserException extends UncheckedCodedException {
	
	private static final String AlreadyLockedByAnotherUserException_ErrorCode = "errorcode.already.locked.by.another.user";

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
}
