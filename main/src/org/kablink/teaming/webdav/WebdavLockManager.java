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
package org.kablink.teaming.webdav;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.ReservedByAnotherUserException;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.file.LockIdMismatchException;
import org.kablink.teaming.module.file.LockedByAnotherUserException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;

import com.bradmcevoy.http.LockInfo;
import com.bradmcevoy.http.LockResult;
import com.bradmcevoy.http.LockTimeout;
import com.bradmcevoy.http.LockToken;
import com.bradmcevoy.http.LockableResource;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.ettrema.http.fs.LockManager;

/*
 * This class implements lock manager for the new Milton-based WebDAV service which
 * persists lock information in the database in a way that is compatible with the
 * schema used by the old Slide-based WebDAV implementation. This is to allow the
 * new functionality to work without requiring database schema changes and also to
 * make it possible for both implementations to co-exist side by side if necessary. 
 * <p>
 * In this implementation, the <code>ownerInfo</code> field of the <code>FileLock</code>
 * is always set to the value represented by the <code>OWNER_INFO</code> constant.
 * This value can be used as a key for determining whether a particular lock was
 * issued by the old implementation or the new one.
 * <p>
 * Also, the <code>subject</code> field will store as string the seconds value of the
 * lock timeout "granted" by the server (which may or may not be the same as the
 * "requested" value).  
 * 
 * @author jong
 *
 */
public class WebdavLockManager implements LockManager {

	private static Log logger = LogFactory.getLog(WebdavLockManager.class);
	
	private static final String OWNER_INFO = "(MILTON)";
	
	private static final long DEFAULT_MAX_TIMEOUT_SECONDS = 3600; // one hour
	
	private static long maxTimeoutSeconds = -1; // -1 indicates that this variable is uninitialized
	
	/* (non-Javadoc)
	 * @see com.ettrema.http.fs.LockManager#lock(com.bradmcevoy.http.LockTimeout, com.bradmcevoy.http.LockInfo, com.bradmcevoy.http.LockableResource)
	 */
	@Override
	public LockResult lock(LockTimeout timeout, LockInfo lockInfo,
			LockableResource resource) throws NotAuthorizedException {
		FileAttachment fa = getFileAttachment(resource);
		DefinableEntity entity = fa.getOwner().getEntity();

		checkAccessForLocking(entity, resource);
		
		String tokenId = UUID.randomUUID().toString();
		
		long grantedTimeoutSeconds;
		Long requestedTimeoutSeconds = timeout.getSeconds();
		if(requestedTimeoutSeconds != null) {
			grantedTimeoutSeconds = Math.min(requestedTimeoutSeconds.longValue(), getMaxTimeoutSeconds());
		}
		else {
			// Timeout value not specified, meaning infinite
			grantedTimeoutSeconds = getMaxTimeoutSeconds();
		}
		
		LockTimeout.DateAndSeconds expirationDateAndSeconds = LockTimeout.addSeconds(grantedTimeoutSeconds);
		
		try {
			getFileModule().lock(entity.getParentBinder(), entity, fa, tokenId, makeSubjectFieldFromTimeoutSeconds(grantedTimeoutSeconds), expirationDateAndSeconds.date, OWNER_INFO);
		}
		catch(ReservedByAnotherUserException e) {
			throw new NotAuthorizedException(resource);
		}
		catch(LockedByAnotherUserException e) {
			return LockResult.failed( LockResult.FailureReason.ALREADY_LOCKED );				
		}
		catch(LockIdMismatchException e) {
			return LockResult.failed( LockResult.FailureReason.ALREADY_LOCKED );								
		}
		
		LockToken newToken = new LockToken(tokenId, lockInfo, timeout);

		return LockResult.success(newToken);
	}

	/* (non-Javadoc)
	 * @see com.ettrema.http.fs.LockManager#refresh(java.lang.String, com.bradmcevoy.http.LockableResource)
	 */
	@Override
	public LockResult refresh(String tokenId, LockableResource resource)
			throws NotAuthorizedException {
		FileAttachment fa = getFileAttachment(resource);
		DefinableEntity entity = fa.getOwner().getEntity();
		
		// If you've lost appropriate right to the entity since you last locked/refreshed it 
		// successfully, then you should no longer be able to renew the lock any more. 
		checkAccessForLocking(entity, resource);
			
		FileAttachment.FileLock fileLock = fa.getFileLock();
		if(fileLock == null) {
			// The file is not currently locked.
			return LockResult.failed( LockResult.FailureReason.PRECONDITION_FAILED);
		}

		// Get the previously stored timeout seconds value from the existing lock.
		Long timeoutSeconds = parseSubjectFieldIntoTimeoutSeconds(fileLock.getSubject());
		if(timeoutSeconds == null) {
			// The file wasn't locked by this implementation. Don't know how to refresh it.
			return LockResult.failed( LockResult.FailureReason.PRECONDITION_FAILED);				
		}

		LockTimeout.DateAndSeconds expirationDateAndSeconds = LockTimeout.addSeconds(timeoutSeconds);
		
		try {
			getFileModule().lock(entity.getParentBinder(), entity, fa, tokenId, makeSubjectFieldFromTimeoutSeconds(timeoutSeconds), expirationDateAndSeconds.date, OWNER_INFO);
		}
		catch(ReservedByAnotherUserException e) {
			throw new NotAuthorizedException(resource);
		}
		catch(LockedByAnotherUserException e) {
			return LockResult.failed( LockResult.FailureReason.PRECONDITION_FAILED);			
		}
		catch(LockIdMismatchException e) {
			return LockResult.failed( LockResult.FailureReason.PRECONDITION_FAILED);							
		}

		return LockResult.success(createTokenFromFileLock(tokenId, fileLock.getOwner().getName(), timeoutSeconds));
	}

	/* (non-Javadoc)
	 * @see com.ettrema.http.fs.LockManager#unlock(java.lang.String, com.bradmcevoy.http.LockableResource)
	 */
	@Override
	public void unlock(String tokenId, LockableResource resource)
			throws NotAuthorizedException {
		FileAttachment fa = getFileAttachment(resource);
		DefinableEntity entity = fa.getOwner().getEntity();

		// Note: It is possible that the owner of the lock has lost the modify right on the entry
		// since she locked the file successfully. It can happen by someone else changing the
		// access control on the entry or by a workflow, etc. In such case, we want the lock
		// owner to be able to graciously release the lock as long as the lock matches, even if 
		// she may no longer have the modify right on the entry itself (see bug 584878).
		// As a side effect of unlocking the file, if she had managed to get any updated content
		// of the file into the system before she lost the modify right on the entry, the updated
		// content will be committed to the system creating a new version of the file. 
		// For the above reason, the access checking is skipped here.
		//checkAccessForLocking(entity, resource);
			
		try {
			getFileModule().unlock(entity.getParentBinder(), entity, fa, tokenId);
		}
		catch(LockedByAnotherUserException e) {
			throw new NotAuthorizedException(resource);		
		}
		catch(LockIdMismatchException e) {
			throw new NotAuthorizedException(resource);							
		}
	}

	/* (non-Javadoc)
	 * @see com.ettrema.http.fs.LockManager#getCurrentToken(com.bradmcevoy.http.LockableResource)
	 */
	@Override
	public LockToken getCurrentToken(LockableResource resource) {
		FileAttachment fa = getFileAttachment(resource);
		DefinableEntity entity = fa.getOwner().getEntity();

		FileAttachment.FileLock fileLock = fa.getFileLock();
		if(fileLock == null) {
			// The file is not currently locked.
			return null;
		}
		else if(getFileModule().isLockExpired(fileLock)) {
			// The file is locked, but that lock is expired. In this case, return null as if the file wasn't locked at all.
			// Otherwise, LibreOffice won't use another to edit the file (which, in my opinion, is a bug in LibreOffice)
			// (See bug #869422)
			return null;
		}

		// Get the previously stored timeout seconds value from the existing lock.
		Long timeoutSeconds = parseSubjectFieldIntoTimeoutSeconds(fileLock.getSubject());

		return createTokenFromFileLock(fileLock.getId(), fileLock.getOwner().getName(), timeoutSeconds);
	}

	private FileAttachment getFileAttachment(LockableResource resource) {
		if(resource instanceof FileAttachmentResource)
			return ((FileAttachmentResource) resource).getFileAttachment();
		else 
			throw new UnsupportedOperationException("Locking is not supported on resource of type '" + resource.getClass().getName() + "'"); 
	}
	
	private long getMaxTimeoutSeconds() {
		if(maxTimeoutSeconds < 0) {
			maxTimeoutSeconds = SPropsUtil.getLong("wd.max.lock.timeout.seconds", DEFAULT_MAX_TIMEOUT_SECONDS);
		}
		return maxTimeoutSeconds;
	}
	
	private String makeSubjectFieldFromTimeoutSeconds(long timeoutSeconds) {
		return "timeout-seconds:" + timeoutSeconds;
	}
	
	private Long parseSubjectFieldIntoTimeoutSeconds(String subject) {
		if(subject == null)
			return null;
		if(subject.startsWith("timeout-seconds:")) {
			String secondsStr = subject.substring(16);
			try {
				return Long.parseLong(secondsStr);
			}
			catch(NumberFormatException e) {
				logger.error("Invalid value: " + subject, e);
				return null;
			}
		}
		else {
			return null;
		}
	}
	
	private LockToken createTokenFromFileLock(String tokenId, String lockedByUser, Long timeout) {
		LockInfo lockInfo = new LockInfo( LockInfo.LockScope.EXCLUSIVE, LockInfo.LockType.WRITE, lockedByUser, LockInfo.LockDepth.ZERO);
		LockTimeout lockTimeout = new LockTimeout(timeout);
		LockToken lockToken = new LockToken(tokenId, lockInfo, lockTimeout);
		return lockToken;
	}
	
	private void checkAccessForLocking(DefinableEntity entity, LockableResource resource) throws NotAuthorizedException {
		if(entity instanceof Entry) {
			// Check access
			try {
				AccessUtils.modifyCheck((Entry) entity);
			}
			catch(AccessControlException e) {
				throw new NotAuthorizedException(resource);
			}
		}
		else if(entity instanceof Binder) {
			try {
				getBinderModule().checkAccess((Binder)entity, BinderOperation.modifyBinder);
			}
			catch(AccessControlException e) {
				throw new NotAuthorizedException(resource);
			}
		}
	}
			
	protected FolderModule getFolderModule () {
		return (FolderModule) SpringContextUtil.getBean("folderModule");
	}

	protected BinderModule getBinderModule () {
		return (BinderModule) SpringContextUtil.getBean("binderModule");
	}

	protected FileModule getFileModule () {
		return (FileModule) SpringContextUtil.getBean("fileModule");
	}
}
