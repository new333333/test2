package com.sitescape.ef.module.file;

import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.exception.UncheckedCodedException;

public class LockedByAnotherUserException extends UncheckedCodedException {
	
	private static final String AlreadyLockedByAnotherUserException_ErrorCode = "errorcode.already.locked.by.another.user";

	private DefinableEntity entity;
	private String repositoryName;
	private String fileName;
	private Principal lockOwner;
	
	public LockedByAnotherUserException(DefinableEntity entity, 
			String repositoryName, String fileName, Principal lockOwner) {
		super(AlreadyLockedByAnotherUserException_ErrorCode, new Object[] { 
				entity.getId(), repositoryName, fileName, lockOwner.getName() });
		
		this.entity = entity;
		this.repositoryName = repositoryName;
		this.fileName = fileName;
		this.lockOwner = lockOwner;
	}

	public DefinableEntity getEntity() {
		return entity;
	}

	public String getRepositoryName() {
		return repositoryName;
	}
	
	public String getFileName() {
		return fileName;
	}

	public Principal getLockOwner() {
		return lockOwner;
	}
}
