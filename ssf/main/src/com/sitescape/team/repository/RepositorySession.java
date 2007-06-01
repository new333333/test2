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
package com.sitescape.team.repository;

import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.web.multipart.MultipartFile;
import javax.activation.DataSource;
import javax.activation.FileTypeMap;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;

public interface RepositorySession {

	/**
	 * The file exists and is versioned.
	 */
	public static final int VERSIONED_FILE 		= 0;
	/**
	 * The file exists and is unversioned.
	 */
	public static final int UNVERSIONED_FILE	= 1;
	/**
	 * The file does not exist.
	 */
	public static final int NON_EXISTING_FILE	= 2;
	
	/**
	 * End the session by disconnecting from the repository and cleaning up.
	 * 
	 * @param session
	 */
	public void close() 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Returns one of the following values:<br>
	 * <code>VERSIONED_FILE</code><br>
	 * <code>UNVERSIONED_FILE</code><br>
	 * <code>NON_EXISTING_FILE</code><br>
	 * 
	 * @param binder
	 * @param entity
	 * @param relativeFilePath
	 * @return
	 * @throws RepositoryServiceException
	 */
	public int fileInfo(Binder binder, DefinableEntity entity,
			String relativeFilePath) throws RepositoryServiceException,
			UncheckedIOException;
	
	/**
	 * Creates a new file resource in the repository system. 
	 * <p>
	 * The first version of the resource is created and its version name is
	 * returned. 
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath A pathname of the file relative to the entity. This may
	 * simply be the name of the file. 
	 * @param mf
	 * @return
	 * @throws RepositoryServiceException
	 */
	public String createVersioned(Binder binder, 
			DefinableEntity entity, String relativeFilePath, MultipartFile mf) 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Creates a new file resource in the repository system. 
	 * <p>
	 * The first version of the resource is created and its version name is
	 * returned. 
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath A pathname of the file relative to the entity. This may
	 * simply be the name of the file. 
	 * @param in
	 * @return
	 * @throws RepositoryServiceException
	 */
	public String createVersioned(Binder binder, 
			DefinableEntity entity, String relativeFilePath, InputStream in) 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Creates a new file resource in the repository system. The specified file
	 * resource is not versioned. 
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath A pathname of the file relative to the entity. This may
	 * simply be the name of the file. 
	 * @param in
	 * @throws RepositoryServiceException
	 */
	public void createUnversioned(Binder binder, 
			DefinableEntity entity, String relativeFilePath, InputStream in) 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Updates the existing file resource.  
	 * <p>
	 * If the resource is versioned, it is expected to have been checked out
	 * prior to invoking this method. The changes made to the repository 
	 * through this method are made permanent when {@link #checkin} is executed.
	 * <p>
	 * If the resource is unversioned, on the other hand, the change is 
	 * immediate and permanent.
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath A pathname of the file relative to the entity. This may
	 * simply be the name of the file. 
	 * @param mf
	 * @throws RepositoryServiceException
	 */
	public void update(Binder binder, DefinableEntity entity, 
			String relativeFilePath, MultipartFile mf) 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Updates the existing file resource.  
	 * <p>
	 * If the resource is versioned, it is expected to have been checked out
	 * prior to invoking this method. The changes made to the repository 
	 * through this method are made permanent when {@link #checkin} is executed.
	 * <p>
	 * If the resource is unversioned, on the other hand, the change is 
	 * immediate and permanent.
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath A pathname of the file relative to the entity. This may
	 * simply be the name of the file. 
	 * @param in
	 * @throws RepositoryServiceException
	 */
	public void update(Binder binder, DefinableEntity entity, 
			String relativeFilePath, InputStream in) 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Deletes the file resource. If the resource is versioned all its versions
	 * are also deleted.  
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath
	 * @throws RepositoryServiceException
	 */
	public void delete(Binder binder, DefinableEntity entity,
			String relativeFilePath) throws RepositoryServiceException,
			UncheckedIOException;
	
	/**
	 * Deletes the specified version of the file.
	 * <p>
	 * This method can only be called on a versioned file.
	 * 
	 * @param binder
	 * @param entity
	 * @param relativeFilePath
	 * @param versionName
	 * @throws RepositoryServiceException
	 * @throws UncheckedIOException
	 */
	public void deleteVersion(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName) 
		throws RepositoryServiceException, UncheckedIOException;
		
	/**
	 * Deletes all files that are under the binder. 
	 * If a file is versioned all its versions are also deleted.  
	 * 
	 * @param session
	 * @param binder
	 * @throws RepositoryServiceException
	 */
	public void delete(Binder binder) 
	throws RepositoryServiceException, UncheckedIOException;
			
	/**
	 * Reads the content of the specified version of the file resource from
	 * the repository.
	 * <p>
	 * This method can only be called on a versioned file. 
	 */
	public void readVersioned(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName, OutputStream out) 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Returns an <code>InputStream</code> from which to read the content of
	 * the specified version of the file resource from the repository.
	 * The caller is responsible for closing the stream after use. 
	 * <p>
	 * This method can only be called on a versioned file.
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath A pathname of the file relative to the entity. This may
	 * simply be the name of the file. 
	 * @param versionName the name of the version
	 * @throws RepositoryServiceException thrown if the specified version does
	 * not exist, or if some other error occurs
	 */
	public InputStream readVersioned(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName) 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Reads the content of the specified file resource from the repository system. 
	 * <p>
	 * This method can only be called on an unversioned file.
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath A pathname of the file relative to the entity. This may
	 * simply be the name of the file. 
	 * @param out
	 * @throws RepositoryServiceException
	 */
	public void readUnversioned(Binder binder, DefinableEntity entity, 
			String relativeFilePath, OutputStream out) 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Returns an <code>InputStream</code> from which to read the content
	 * of the specified file resource from the repository. 
	 * The caller is responsible for closing the stream after use. 
	 * <p>
	 * This method can only be called on an unversioned file.
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath
	 * @return
	 * @throws RepositoryServiceException
	 */
	public InputStream readUnversioned(Binder binder, 
			DefinableEntity entity, String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Return a datasource that will be used to read the file to a mime message.
	 * <p>
	 * This method can only be called on a versioned file.
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath A pathname of the file relative to the entity. This may
	 * simply be the name of the file. 
	 * @param versionName the name of the version
	 * @param fileTypeMap 
	 * @return
	 * @throws RepositoryServiceException
	 */
	public DataSource getDataSourceVersioned(Binder binder, 
			DefinableEntity entity, String relativeFilePath, String versionName, 
			FileTypeMap fileTypeMap) throws RepositoryServiceException,
			UncheckedIOException;
	
	/**
	 * Checks out the specified file resource. It is illegal to call this method
	 * on an unversioned resource.
	 * <p>
	 * If the resource is already checked out (by anyone), this method has no 
	 * effect. If the specified resource does not exist, it throws an exception.
	 * <p>
	 * Important: Notice the semantics of this method; It has nothing to do with
	 * granting an exclusive access to the resource to the caller. Checkout/
	 * checkin is merely a mechanism whereby creation of new versions can be
	 * controlled, which is orthogonal to the concept of locking issued under
	 * specific user. Locking is used to allow a user to temporarily lock 
	 * resources in order to prevent other users from changing them. The lock
	 * functionality is neither exposed nor required by this API. 
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath A pathname of the file relative to the entity. This may
	 * simply be the name of the file. 
	 * @throws RepositoryServiceException
	 */
	public void checkout(Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryServiceException,
			UncheckedIOException;
	
	/**
	 * Cancels the checkout for the specified file resource. It is illegal to 
	 * call this method on an unversioned resource.
	 * <p>
	 * If the resource is not checked out, this method has no effect. 
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath A pathname of the file relative to the entity. This may
	 * simply be the name of the file. 
	 * @throws RepositoryServiceException
	 */
	public void uncheckout(Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryServiceException,
			UncheckedIOException;
	
	/**
	 * Checks in the specified file resource and returns the name of the new
	 * version created. It is illegal to call this method on an unversioned 
	 * resource.
	 * <p>
	 * If the resource is already checked in, this method has no effect but
	 * returns the name of the current (latest) checked-in version of the 
	 * resource. 
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath A pathname of the file relative to the entity. 
	 * This may simply be the name of the file. 
	 * @return the name of the new version
	 * @throws RepositoryServiceException
	 */
	public String checkin(Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryServiceException,
			UncheckedIOException;
	
	/**
	 * Returns the length (in byte) of the content of the specific file resource.
	 * <p>
	 * This method can only be called on an unversioned file. 
	 *  
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath
	 * @return
	 * @throws RepositoryServiceException
	 */
	public long getContentLengthUnversioned(Binder binder, 
			DefinableEntity entity, String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Returns the length (in byte) of the content of the specific version
	 * of the file resource. 
	 * <p>
	 * This method can only be called on a versioned file.
	 *  
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath
	 * @param versionName
	 * @return
	 * @throws RepositoryServiceException
	 */
	public long getContentLengthVersioned(Binder binder, 
			DefinableEntity entity, String relativeFilePath, 
			String versionName) throws RepositoryServiceException,
			UncheckedIOException;
	
	/**
	 * Moves the file resource.
	 * 
	 * @param binder
	 * @param entity
	 * @param relativeFilePath
	 * @param destBinder
	 * @param destEntity
	 * @param destRelativeFilePath
	 * @throws RepositoryServiceException
	 * @throws UncheckedIOException
	 */
	public void move(Binder binder, DefinableEntity entity, 
			String relativeFilePath, Binder destBinder, 
			DefinableEntity destEntity, String destRelativeFilePath)
		throws RepositoryServiceException, UncheckedIOException;
	
	public RepositorySessionFactory getFactory();
}
