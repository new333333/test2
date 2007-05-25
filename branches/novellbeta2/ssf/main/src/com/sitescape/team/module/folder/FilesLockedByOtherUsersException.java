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
package com.sitescape.team.module.folder;

import java.util.List;

import com.sitescape.team.exception.UncheckedCodedException;

/**
 * Thrown to indicate that one or more files are locked by other user(s).
 * User can obtain detailed information about currently-held locks by calling 
 * <code>getFileLockInfos</code> method. Note that the information does not
 * exclude those locks that may be held by the calling user. 
 * 
 * @author jong
 *
 */
public class FilesLockedByOtherUsersException extends UncheckedCodedException {

	private static final String FilesLockedByOtherUsersException_ErrorCode = "errorcode.files.locked.by.other.users";
	
	private List<FileLockInfo> fileLockInfos;
	
	public FilesLockedByOtherUsersException(List<FileLockInfo> fileLockInfos) {
		super(FilesLockedByOtherUsersException_ErrorCode, new Object[] {});
		
		this.fileLockInfos = fileLockInfos;
	}

	public List<FileLockInfo> getFileLockInfos() {
		return fileLockInfos;
	}	
}
