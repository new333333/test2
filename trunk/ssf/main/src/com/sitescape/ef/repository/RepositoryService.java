package com.sitescape.ef.repository;

import java.io.OutputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;

public interface RepositoryService {

	/**
	 * Opens a session with the repository system. 
	 * 
	 * @return
	 */
	public Object openRepositorySession() throws RepositoryServiceException;
	
	/**
	 * Closes the session with the repository system.
	 * 
	 * @param session
	 */
	public void closeRepositorySession(Object session) throws RepositoryServiceException;
	
	/**
	 * Writes the file resource to the repository system. 
	 * <p>
	 * If it is an existing resource, it is expected to have been checked out
	 * prior to invoking this method. The changes made to the repository
	 * through this method are made permanent when {@link #checkin} is executed.
	 * If the resource is new, the first version is created immediately upon 
	 * completion of this call and no <code>checkin</code> is necessary.   
	 * 
	 * @param session
	 * @param folder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @param mf
	 * @throws RepositoryServiceException
	 */
	public void write(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath, MultipartFile mf) 
		throws RepositoryServiceException;
	
	/**
	 * Reads the content of the specified file resource from the repository 
	 * system. 
	 * <p>
	 * The content being read is identical to the latest checked-in version 
	 * of the resource.
	 * 
	 * @param session
	 * @param folder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @param out
	 * @throws RepositoryServiceException
	 */
	public void read(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath, OutputStream out) 
		throws RepositoryServiceException;
	
	/**
	 * Reads from the repository system the content of the specified version 
	 * of the file resource. 
	 * 
	 * @param session
	 * @param folder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @param versionName the name of the version
	 * @param out
	 * @throws RepositoryServiceException thrown if the specified version does
	 * not exist, or if some other error occurs
	 */
	public void readVersion(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath, String versionName, OutputStream out) 
		throws RepositoryServiceException;
	
	/**
	 * Checks out the specified file resource.
	 * <p>
	 * If the resource is already checked out (by anyone), this method has no 
	 * effect. The resource must already exist before checking it out.
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
	 * @param folder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @throws RepositoryServiceException
	 */
	public void checkout(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Cancels the checkout for the specified file resource. 
	 * <p>
	 * If the resource is not checked out, this method has no effect. 
	 * 
	 * @param session
	 * @param folder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @throws RepositoryServiceException
	 */
	public void uncheckout(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Checks in the specified file resource and returns the name of the new
	 * version created. 
	 * <p>
	 * If the resource is already checked in, this method has no effect but
	 * returns the name of the current checked-in version of the resource.  
	 * 
	 * @param session
	 * @param folder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. 
	 * This may simply be the name of the file. 
	 * @return the name of the new version
	 * @throws RepositoryServiceException
	 */
	public String checkin(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Returns whether the specified file resource is currently checked out
	 * or not.
	 * 
	 * @param
	 * @param folder
	 * @param entry
	 * @param relativeFilePath
	 * @return
	 * @throws RepositoryServiceException
	 */
	public boolean isCheckedOut(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Returns whether the specified file resource exists or not. 
	 * 
	 * @param session
	 * @param folder
	 * @param entry
	 * @param relativeFilePath
	 * @return
	 */
	public boolean exists(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Returns whether the repository service allows users to delete individual
	 * versions of a resource without deleting the entire resource. In other
	 * words, for repository system that does not support this, the only way
	 * to remove a particular resource is to delete it in its entirety which
	 * deletes all of its versions as well. Repository system that does not
	 * support versioning must return <code>false</code> from this method.   
	 * 
	 * @return
	 */
	public boolean supportVersionDeletion();
}
