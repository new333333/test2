package com.sitescape.ef.module.folder;

import java.util.List;

import com.sitescape.ef.exception.UncheckedCodedException;

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
