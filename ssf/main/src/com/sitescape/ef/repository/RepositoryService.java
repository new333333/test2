package com.sitescape.ef.repository;

import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.web.multipart.MultipartFile;
import javax.activation.DataSource;
import javax.activation.FileTypeMap;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Binder;

public interface RepositoryService {

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
	public static final int NON_EXISTING_FILE	=2;
	
	/**
	 * Opens a session with the repository system. 
	 * 
	 * @return
	 */
	public Object openRepositorySession() throws RepositoryServiceException,
		UncheckedIOException;
	
	/**
	 * Closes the session with the repository system.
	 * 
	 * @param session
	 */
	public void closeRepositorySession(Object session) 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Returns one of the following values:<br>
	 * <code>VERSIONED_FILE</code><br>
	 * <code>UNVERSIONED_FILE</code><br>
	 * <code>NON_EXISTING_FILE</code><br>
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath
	 * @return
	 * @throws RepositoryServiceException
	 */
	public int fileInfo(Object session, Binder binder, DefinableEntity entity,
			String relativeFilePath) throws RepositoryServiceException,
			UncheckedIOException;
	
	/**
	 * Returns whether or not the specified file resource is versioned.
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath
	 * @return
	 * @throws RepositoryServiceException
	 */
	//public boolean isVersioned(Object session, Binder binder, DefinableEntity entity,
	//		String relativeFilePath) throws RepositoryServiceException;
	
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
	public String createVersioned(Object session, Binder binder, 
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
	public String createVersioned(Object session, Binder binder, 
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
	public void createUnversioned(Object session, Binder binder, 
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
	public void update(Object session, Binder binder, DefinableEntity entity, 
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
	public void update(Object session, Binder binder, DefinableEntity entity, 
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
	public void delete(Object session, Binder binder, DefinableEntity entity,
			String relativeFilePath) throws RepositoryServiceException,
			UncheckedIOException;
		
	/**
	 * Reads the content of the specified file resource from the repository 
	 * system. 
	 * <p>
	 * If the resource is versioned, it reads the latest snapshot of the file,
	 * which may be either the latest checked-in version of the file or the 
	 * working copy in progress if the file is currently checked out. 
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath A pathname of the file relative to the entity. This may
	 * simply be the name of the file. 
	 * @param out
	 * @throws RepositoryServiceException
	 */
	public void read(Object session, Binder binder, DefinableEntity entity, 
			String relativeFilePath, OutputStream out) 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Returns an <code>InputStream</code> from which to read the content of
	 * the specified file resource from the repository. The caller is responsible
	 * for closing the stream after use. 
	 * <p>
	 * If the resource is versioned, it reads the latest snapshot of the file,
	 * which may be either the latest checked-in version of the file or the 
	 * working copy in progress if the file is currently checked out. 
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath
	 * @return
	 * @throws RepositoryServiceException
	 */
	public InputStream read(Object session, Binder binder, 
			DefinableEntity entity, String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Reads from the repository system the content of the specified version 
	 * of the file resource. 
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath A pathname of the file relative to the entity. This may
	 * simply be the name of the file. 
	 * @param versionName the name of the version
	 * @param out
	 * @throws RepositoryServiceException thrown if the specified version does
	 * not exist, or if some other error occurs
	 */
	public void readVersion(Object session, Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName, OutputStream out) 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Return a datasource that will be used to read the file to a mime message
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath A pathname of the file relative to the entity. This may
	 * simply be the name of the file. 
	 * @param fileTypeMap
	 * @return
	 * @throws RepositoryServiceException
	 */
	public DataSource getDataSource(Object session, Binder binder, 
			DefinableEntity entity, String relativeFilePath, 
			FileTypeMap fileTypeMap) throws RepositoryServiceException,
			UncheckedIOException;
	
	/**
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
	public DataSource getDataSourceVersion(Object session, Binder binder, 
			DefinableEntity entity, String relativeFilePath, String versionName, 
			FileTypeMap fileTypeMap) throws RepositoryServiceException,
			UncheckedIOException;

	/**
	 * Returns the names of the versions for the specified file resource. 
	 * The specified file resource must exist. 
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath
	 * @return
	 * @throws RepositoryServiceException
	 */
	//public List getVersionNames(Object session, Binder binder, DefinableEntity entity,
	//		String relativeFilePath) throws RepositoryServiceException;
	
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
	public void checkout(Object session, Binder binder, DefinableEntity entity, 
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
	public void uncheckout(Object session, Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryServiceException,
			UncheckedIOException;
	
	/**
	 * Checks in the specified file resource and returns the name of the new
	 * version created. It is illegal to call this method on an unversioned 
	 * resource.
	 * <p>
	 * If the resource is already checked in, this method has no effect but
	 * returns the name of the current checked-in version of the resource.  
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath A pathname of the file relative to the entity. 
	 * This may simply be the name of the file. 
	 * @return the name of the new version
	 * @throws RepositoryServiceException
	 */
	public String checkin(Object session, Binder binder, DefinableEntity entity, 
			String relativeFilePath) throws RepositoryServiceException,
			UncheckedIOException;
	
	/**
	 * Returns whether the specified file resource is currently checked out
	 * or not. It is illegal to call this method on an unversioned resource.
	 * 
	 * @param
	 * @param binder
	 * @param entity
	 * @param relativeFilePath
	 * @return
	 * @throws RepositoryServiceException
	 */
	public boolean isCheckedOut(Object session, Binder binder, 
			DefinableEntity entity, String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Returns whether the specified file resource exists or not. 
	 * 
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath
	 * @return
	 */
	//public boolean exists(Object session, Binder binder, DefinableEntity entity, 
	//		String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Returns the length (in byte) of the content of the specific file resource. 
	 *  
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath
	 * @return
	 * @throws RepositoryServiceException
	 */
	public long getContentLength(Object session, Binder binder, 
			DefinableEntity entity, String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException;
	
	/**
	 * Returns the length (in byte) of the content of the specific version
	 * of the file resource. 
	 *  
	 * @param session
	 * @param binder
	 * @param entity
	 * @param relativeFilePath
	 * @param versionName
	 * @return
	 * @throws RepositoryServiceException
	 */
	public long getContentLength(Object session, Binder binder, 
			DefinableEntity entity, String relativeFilePath, 
			String versionName) throws RepositoryServiceException,
			UncheckedIOException;
	
	/**
	 * Returns whether or not the repository service supports versioning.
	 * 
	 * @return
	 */
	public boolean supportVersioning();
	
	/**
	 * Returns whether the repository service allows users to delete individual
	 * versions of a resource without deleting the entire resource. In other
	 * words, for repository system that does not support this, the only way
	 * to remove a particular resource is to delete it in its entirety which
	 * deletes all of its versions as well. 
	 * 
	 * @return
	 */
	public boolean supportVersionDeletion();
}
