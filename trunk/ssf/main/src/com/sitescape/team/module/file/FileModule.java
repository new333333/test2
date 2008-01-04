/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.module.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.ReservedByAnotherUserException;
import com.sitescape.team.domain.VersionAttachment;
import com.sitescape.team.repository.RepositoryServiceException;
import com.sitescape.team.util.FileUploadItem;


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
	 * the primary files. This method does NOT remove corresponding
	 * FileAttachment objects from the entity so as to allow for a bulk
	 * delete of Hibernate objects.
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
	 * @param errors errors object or <code>null</code>
	 */
	public FilesErrors deleteFiles(Binder binder, DefinableEntity entity,
			boolean deleteMirroredSource, FilesErrors errors);

	/**
	 * Deletes the specified file. If applicable, also delete generated files
	 * (scaled file and thumbnail file) associated with the primary file. 
	 * This method also removes the FileAttachment from the entity.
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
	 * @param errors errors object or <code>null</code>
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
     * If the thumbnail was originally stored as "directly accessible" file
     * in a directory visible to the web client without requiring any access
     * control or assistance from the server-side service, the caller must not 
     * use this method (In other words, it is the caller's responsibility to 
     * keep track of whether a thumbnail file is directly accessible or not. 
     * The file module does not maintain that information.).
     * 
     * @param fa
     * @param binder
     * @param entity
     * @param out
     */
	public void readThumbnailFile(
			Binder binder, DefinableEntity entity, FileAttachment fa, OutputStream out) 
		throws UncheckedIOException, RepositoryServiceException;
	
	public void readCacheHtmlFile(
			String url, Binder binder, DefinableEntity entity, FileAttachment fa, OutputStream out) 
		throws UncheckedIOException, RepositoryServiceException;
	
	public void readCacheImageReferenceFile(
			Binder binder, DefinableEntity entity, FileAttachment fa, OutputStream out, String imageFileName) 
		throws UncheckedIOException, RepositoryServiceException;
	
	public void readCacheUrlReferenceFile(
			Binder binder, DefinableEntity entity, FileAttachment fa, OutputStream out, String urlFileName) 
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
    		List<FileUploadItem> fileUploadItems, FilesErrors errors) 
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
    public FilesErrors filterFiles(Binder binder, List<FileUploadItem> fileUploadItems) 
    	throws FilterException;
    
    /**
     * Locks the file so that subsequent updates can be made to the file. 
     * This serves two purposes:<br>
     * 1) Provide the user with exclusive write access to the file, which
     * prevents other users from making modification to the same resource.<br>
     * 2) Allows the user to make multiple updates (that is, multiple saves) 
     * to the resource without causing each update to create a new version 
     * of the file. Up to one new version is created at the time of unlock.<br> 
     * <p>
     * IMPORTANT: THIS METHOD IS FOR WEBDAV CLIENT ONLY!!!
     * <p>
     * 1) If the enclosing entity is not reserved by anyone, this locks the
     * file and increments locked-file-count on the entity.<br>
     * 2) If the entity is reserved by another user, it throws 
     * <code>ReservedByAnotherUserException</code>.<br> 
     * 3) If the entity is reserved by the same user:<br>
     * 3.1) If the file is not locked, it locks it and increments 
     * locked-file-count count on the entity.<br> 
     * 3.2) If the file is locked and lock id does not match, it throws
     * <code>LockIdMismatchException</code>.<br> 
     * 3.3) If the file is locked and lock id matches, renew the lock by 
     * extending/updating expiration date. The locked-file-count on the entity
     * remains the same. 
     * 
     * @param binder
     * @param entity
     * @param fa
     * @param lockId Lock token id. 
     * @param lockSubject String representing owner of this lock. This value is
     * treated as opaque string and has no meaning/utility on the server side. 
     * @param expirationDate Lock expiration date.
     * @param lockOwnerInfo Provides information about the principal taking out 
     * a lock. Not used on the server side.
     * @throws ReservedByAnotherUserException If the enclosing entity is already
     * under reservation by another user
     * @throws LockedByAnotherUserException If the file is locked by another user.
     * @throws LockIdMismatchException The file is already locked by the same
     * user but the lock id does not match.
     * @throws UncheckedIOException I/O error
     * @throws RepositoryServiceException Any other internal or unexpected error	
     */
    public void lock(Binder binder, DefinableEntity entity, FileAttachment fa,
    		String lockId, String lockSubject, Date expirationDate, String lockOwnerInfo)
    	throws ReservedByAnotherUserException, LockedByAnotherUserException,
    	LockIdMismatchException, UncheckedIOException, RepositoryServiceException;
    
    /**
     * Unlocks the file and commits pending changes associated with it if any.
     * <p>
     * IMPORTANT: THIS METHOD IS FOR WEBDAV CLIENT ONLY!!!
     * <p>
     * If the file is locked by the same user and the lock id matches, it
     * commits pending changes associated with the lock, and then clears
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
    
    /**
     * Bring the states of the locks up-to-date by identifying and processing
     * expired locks. Any pending change associated with expired lock is
     * committed and the lock is cleared. All effective locks (ones that
     * have not expired) remain intact. The caller can assume that by the
     * time this method returns all expired locks are gone and all remaining
     * locks are effective. 
     */
    public void RefreshLocks(Binder binder, DefinableEntity entity) 
	throws RepositoryServiceException, UncheckedIOException;
    
	
	/**
	 * Rename the file.
	 *  
	 * Important: Unlike many other methods in this class, this method
	 * assumes that the caller is responsible for transaction demarcation
	 * with respect to updating the metadata in the database. 
	 * This inconsistency is here merely for improved efficiency.
	 */
	public void renameFile(Binder binder, DefinableEntity entity, 
			FileAttachment fa, String newName) 
		throws UncheckedIOException, RepositoryServiceException;

	/**
	 * Moves the file.
	 * 
	 * Important: Unlike many other methods in this class, this method
	 * assumes that the caller is responsible for transaction demarcation
	 * with respect to updating the metadata in the database. 
	 * This inconsistency is here merely for improved efficiency.
	 * 
	 * @param binder
	 * @param entity
	 * @param destBinder
	 * @param destEntity
	 * @param fa
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
	 */
	//public void moveFile(Binder binder, DefinableEntity entity, 
	//		FileAttachment fa, Binder destBinder, DefinableEntity destEntity) 
	//throws UncheckedIOException, RepositoryServiceException;

	/**
	 * Moves the files.
	 * 
	 * Important: Unlike many other methods in this class, this method
	 * assumes that the caller is responsible for transaction demarcation
	 * with respect to updating the metadata in the database. 
	 * This inconsistency is here merely for improved efficiency.
	 * 
	 * @param binder
	 * @param entity
	 * @param destBinder
	 * @param destEntity
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
	 */
	public void moveFiles(Binder binder, DefinableEntity entity, 
			Binder destBinder, DefinableEntity destEntity)
	throws UncheckedIOException, RepositoryServiceException;
	
	/**
	 * Copy the files.
	 * 
	 * Important: Unlike many other methods in this class, this method
	 * assumes that the caller is responsible for transaction demarcation
	 * with respect to updating the metadata in the database. 
	 * This inconsistency is here merely for improved efficiency.
	 * 
	 * @param binder
	 * @param entity
	 * @param destBinder
	 * @param destEntity
	 * @throws UncheckedIOException
	 * @throws RepositoryServiceException
	 */
	public void copyFiles(Binder binder, DefinableEntity entity, 
			Binder destBinder, DefinableEntity destEntity)
	throws UncheckedIOException, RepositoryServiceException;
	
	/**
	 * Delete the specified version. 
	 * If it is the only remaining version for the file, the request fails and 
	 * the method throws <code>DeleteVersionException</code>.
	 * Like <code>renameFile</code>, this method assumes that the caller is
	 * responsible for transaction demarcation. 
	 * 
	 * @param binder
	 * @param entity
	 * @param va
	 * @throws DeleteVersionException
	 */
	public void deleteVersion(Binder binder, DefinableEntity entity,
			VersionAttachment va) throws DeleteVersionException; 
	
	/**
	 * Returns a map of names of the files contained in the specified binder
	 * to its enclosing entry ids.
	 * 
	 * @param binder
	 * @return
	 */
	public Map<String,Long> getChildrenFileNames(Binder binder);
}	
