package com.sitescape.ef.module.file;

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
