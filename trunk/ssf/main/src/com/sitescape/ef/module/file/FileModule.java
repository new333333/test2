package com.sitescape.ef.module.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.ReservedByAnotherUserException;
import com.sitescape.ef.repository.RepositoryServiceException;

/**
 * Provides uniform interface and integrated management for various file 
 * resources in the system hiding the details of the actual repository
 * systems from the caller. Provides higher-level wrapper around underlying
 * repository system implementations and adds metadata management which
 * may require database transaction support.   
 * 
 * IMPORTANT: Although most of the methods defined in this module take
 * binder and entity/entry as arguments, they are used primarily to 
 * construct full path to file resource. In other words, the focus of
 * the operations is files not binders or entries. For that reason, this
 * module does NOT perform any access-control because files are NOT
 * access-controlled objects themselves. Instead, access control is enforced
 * by their enclosing entries. Therefore, when implementing an application
 * feature using the services provided by this module, it is important
 * for the feature implementation to perform appropriate access checking
 * either by first calling another module that does the access checking
 * or by wrapping invocation of FileModule methods within another higher-level 
 * module and calling the method in the wrapper.   
 * 
 * @author jong
 *
 */
public interface FileModule {
	
	/**
	 * Delete all files attached to the entity. If applicable, also delete 
	 * generated files (scaled files and thumbnail files) associated with 
	 * the primary files. 
	 * <p>
	 * If any of the files is currently locked by anyone, this forcefully 
	 * unlocks it before deleting it.
	 * <p>
	 * This method differs from other methods in that it returns accumulated
	 * error information in FilesErrors object rather than throwing an 
	 * exception. The operation does not necessarily stop upon the first
	 * error encountered (depending on the nature of the error). Instead
	 * it continues with processing (when possible), accumulates all errors,
	 * and then returns. 
	 * 
	 * @param binder
	 * @param entity
	 * metadata on the <code>entity</code>. 
	 */
	public FilesErrors deleteFiles(Binder binder, DefinableEntity entity,
			FilesErrors errors);
	
	/**
	 * Deletes the specified file. If applicable, also delete generated files
	 * (scaled file and thumbnail file) associated with the primary file. 
	 * <p>
	 * If the file is currently locked by anyone, this forcefully unlocks 
	 * it before deleting it.
	 * <p> 
	 * This method differs from other methods in that it returns accumulated
	 * error information in FilesErrors object rather than throwing an 
	 * exception. The operation does not necessarily stop upon the first
	 * error encountered (depending on the nature of the error). Instead
	 * it continues with processing (when possible), accumulates all errors,
	 * and then returns. 
	 * 
	 * @param binder
	 * @param entity
	 * @param fa
	 */
	public FilesErrors deleteFile(Binder binder, DefinableEntity entity, 
			FileAttachment fa, FilesErrors errors); 
    
    /**
     * Reads the specified file into the output stream.
     * 
     * @param fa
     * @param binder
     * @param entity
     * @param out
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
     */
	public void readFile(Binder binder, DefinableEntity entity, FileAttachment fa, 
			OutputStream out) throws UncheckedIOException, 
			RepositoryServiceException;
	
	/**
     * Returns <code>InputStream</code> from which to read the content
     * of the specified file. The caller is responsible for closing
     * the stream after use. 
     * 
	 * @param binder
	 * @param entity
	 * @param fa
	 * @return input stream
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
	 */
	public InputStream readFile(Binder binder, DefinableEntity entity, 
			FileAttachment fa) throws UncheckedIOException, 
			RepositoryServiceException;
	    
    /**
     * Reads the specified scaled file into the output stream.
     * 
     * @param fa
     * @param binder
     * @param entity
     * @param out
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
     */
	public void readScaledFile(Binder binder, DefinableEntity entity, 
			FileAttachment fa, OutputStream out) throws  
			UncheckedIOException, RepositoryServiceException;
	    
    /**
     * Reads the specified scaled file into the output stream.
     * If the thumbnail was originally stored as "directly accessible" file,
     * the caller must not use this method (In other words, it is the
     * caller's responsibility to keep track of whether a thumbnail
     * file is directly accessible or not. The file module does not
     * maintain that information.).
     * 
     * @param fa
     * @param binder
     * @param entity
     * @param out
     */
	public void readIndirectlyAccessibleThumbnailFile(
			Binder binder, DefinableEntity entity, FileAttachment fa, OutputStream out) 
		throws UncheckedIOException, RepositoryServiceException;
	
	/**
	 * (Re)generate scaled file from the specified primary file.
	 * Scaled file is not versioned, thus newly generated file replaces 
	 * old one if exists.   
	 * 
	 * @param binder
	 * @param entity
	 * @param fa
	 * @param maxWidth
	 * @param maxHeight
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
	 */
	public void generateScaledFile(Binder binder, 
    		DefinableEntity entity, FileAttachment fa, 
    		int maxWidth, int maxHeight) 
		throws UncheckedIOException, RepositoryServiceException;
	
	/**
	 * (Re)generate thumbnail file from the specified primary file.
	 * Thumbnail file is not versioned, thus newly generated file replaces 
	 * old one if exists.   
	 * 
	 * @param binder
	 * @param entity
	 * @param fa
	 * @param thumbnailDirectlyAccessible
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
	 */
	public void generateThumbnailFile(Binder binder, 
    		DefinableEntity entity, FileAttachment fa, 
    		int maxWidth, int maxHeight, 
    		boolean thumbnailDirectlyAccessible) 
		throws UncheckedIOException, RepositoryServiceException;

	/**
	 * (Re)generate both scaled file and thumbnail file from the specified 
	 * primary file. Generated files are not versioned, thus newly generated 
	 * files replace old ones if exists.   
	 * 
	 * @param binder
	 * @param entity
	 * @param primaryFileName
	 * @param thumbnailDirectlyAccessible
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
	 */
	public void generateFiles(Binder binder, 
    		DefinableEntity entity, FileAttachment fa, 
    		int maxWidth, int maxHeight,
    		int thumbnailMaxWidth, int thumbnailMaxHeight, 
    		boolean thumbnailDirectlyAccessible) 
		throws UncheckedIOException, RepositoryServiceException;
		
	/**
	 * Returns whether a scaled copy of the file exists or not.
	 * 
	 * @param binder
	 * @param entity
	 * @param fAtt
	 * @return
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
	 */
	public boolean scaledFileExists(Binder binder, DefinableEntity entity, 
			FileAttachment fAtt) 
		throws UncheckedIOException, RepositoryServiceException;
			
	/**
	 * Write multiple files for the specified entity. If the entity is
	 * currently reserved by another user, it throws 
	 * <code>ReservedByAnotherUserException</code>. Otherwise, it proceeds
	 * to write the uploaded files. If an error occurs while writing the
	 * files, it does not stop upon the first error encountered. Instead,
	 * it continues to process all files in the list, and then returns 
	 * with the accumulated errors.  
	 */
    public FilesErrors writeFiles(Binder binder, DefinableEntity entity, 
    		List fileUploadItems, FilesErrors errors) 
    	throws ReservedByAnotherUserException;
    
    /**
     * Run configured filter on the files in the list. Depending on how the
     * implementation is configured, the method may throw <code>FilterException</code>
     * upon the first error encountered, or may return <code>FilesErrors</code>
     * object containing accumulated information about the errors.  
     * 
     * @param binder
     * @param fileUploadItems
     * @return
     * @throws FilterException
     */
    public FilesErrors filterFiles(Binder binder, List fileUploadItems) 
    	throws FilterException;
    
    /**
     * Locks the file so that subsequent updates can be made to the file. 
     * <p>
     * USED BY WEBDAV CLIENT ONLY!!!
     * <p>
     * 1) If the enclosing entity is not reserved by anyone, this locks the
     * file and increments locked-file-count on the entity.<br>
     * 2) If the entity is reserved by another user, it throws 
     * <code>ReservedByAnotherUserException</code>.<br> 
     * 3) If the entity is reserved by the same user:<br>
     * 3.1) If the file is not locked, it locks it and increments lock
     * count on the reservation object.<br> 
     * 3.2) If the file is locked and lock id does not match, it throws
     * <code>LockIdMismatchException</code>.<br> 
     * 3.3) If the file is locked and lock id matches, renew the lock by 
     * extending/updating expiration date. The lock count on the reservation
     * object remains the same. 
     * 
     * @param binder
     * @param entity
     * @param fa
     * @param lockId Lock token id. 
     * @param expirationDate Lock expiration date. This is meaningful only
     * when called by WebDAV client. For non-WebDAV clients, use <code>null</code>.
     * @throws ReservedByAnotherUserException If the enclosing entity is already
     * under reservation by another user
     * @throws LockedByAnotherUserException
     * @throws LockIdMismatchException The file is already locked by the same
     * user but the lock id does not match.
     * @throws UncheckedIOException I/O error
     * @throws RepositoryServiceException Any other internal or unexpected error	
     */
    public void lock(Binder binder, DefinableEntity entity, FileAttachment fa,
    		String lockId, Date expirationDate)
    	throws ReservedByAnotherUserException, LockedByAnotherUserException,
    	LockIdMismatchException, UncheckedIOException, RepositoryServiceException;
    
    /**
     * Unlocks the file and commits pending changes associated with it if any.
     * <p>
     * USED BY WEBDAV CLIENT ONLY!!!
     * <p>
     * If the file is locked by the same user and the lock id matches, it
     * commits pending changes associated with the lock, and then releases
     * the lock. In all other conditions, this is noop and returns silently
     * (that is, this method is more tolerating than <code>lock</code> method).
     * Lock count on the reservation object is adjusted accordingly. 
     * 
     * @param binder
     * @param entity
     * @param fa
     * @param lockId Lock token id.
     * @throws UncheckedIOException I/O error
     * @throws RepositoryServiceException Any other internal or unexpected error	
     */
    public void unlock(Binder binder, DefinableEntity entity, FileAttachment fa,
    		String lockId) throws UncheckedIOException, RepositoryServiceException;
    
    /**
     * Forcefully unlocks the file and commits pending changes associated
     * with it if any. This differs from <code>unlock</code> in that anyone
     * with appropriate privilege (eg. administrator) can call this to unlock
     * a file that was not previously locked by the caller. 
     * <p>
     * If the file is locked by anyone (regardless of whether the lock is
     * currently in effect or has expired), it commits pending changes 
     * associated with the lock and releases the lock. Reservation reference 
     * count on the enclosing entity is modified appropriately.
     * 
     * @param binder
     * @param entity
     * @param fa
     * @throws UncheckedIOException
     * @throws RepositoryServiceException
     */
    //public void forceUnlock(Binder binder, DefinableEntity entity, FileAttachment fa) 
    //	throws UncheckedIOException, RepositoryServiceException;
    
    /**
     * Brings locks and reservation state up-to-date by processing expired
     * locks. Pending changes associated with expired lock is either
     * committed or discarded depending on the <code>commit</code> value.
     * All effective locks (ones that have not expired) remain intact.  
     * 
     * @param service
     * @param session
     * @param binder
     * @param entity
     * @param commit
     * @throws RepositoryServiceException
     * @throws UncheckedIOException
     */
    /*
    public void updateExpiredLocks(RepositoryService service, Object session,
    		Binder binder, DefinableEntity entity, boolean commit) 
		throws RepositoryServiceException, UncheckedIOException;
	*/
}
