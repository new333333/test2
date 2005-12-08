package com.sitescape.ef.repository;

import java.io.OutputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;

public interface RepositoryService {

	//public static final String DEFAULT_REPOSITORY_SERVICE = "defaultWebdavRepositoryService";
	public static final String DEFAULT_REPOSITORY_SERVICE = "fileRepositoryService";
				
	/**
	 * Writes the file resource to the repository system. 
	 * <p>
	 * If it is an existing resource, it should already have been checked out
	 * under the caller's name prior to invoking this method. If this condition
	 * is not met, it throws an exception. 
	 * 
	 * @param folder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @param mf
	 * @throws RepositoryServiceException
	 */
	public void write(Folder folder, FolderEntry entry, 
			String relativeFilePath, MultipartFile mf) 
		throws RepositoryServiceException;
	
	/**
	 * Reads the content of the specified file resource from the repository 
	 * system. 
	 * <p>
	 * The content being read is identical to the latest checked-in version 
	 * of the resource.
	 * 
	 * @param folder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @param out
	 * @throws RepositoryServiceException
	 */
	public void read(Folder folder, FolderEntry entry, 
			String relativeFilePath, OutputStream out) 
		throws RepositoryServiceException;
	
	/**
	 * Reads from the repository system the content of the specified version 
	 * of the file resource. 
	 * 
	 * @param folder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @param versionName the name of the version
	 * @param out
	 * @throws RepositoryServiceException thrown if the specified version does
	 * not exist, or if some other error occurs
	 */
	public void readVersion(Folder folder, FolderEntry entry, 
			String relativeFilePath, String versionName, OutputStream out) 
		throws RepositoryServiceException;
	
	/**
	 * Checks out the specified file resource. 
	 * 
	 * @param folder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @throws RepositoryServiceException
	 */
	public void checkout(Folder folder, FolderEntry entry, 
			String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Cancels the checkout for the specified file resource. It is an error to
	 * apply this method to a resource that has not been previously checked out 
	 * under the caller's name. 
	 * 
	 * @param folder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @throws RepositoryServiceException
	 */
	public void uncheckout(Folder folder, FolderEntry entry, 
			String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Checks in the specified file resource creating a new version.
	 * 
	 * @param folder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @return the name of the new version
	 * @throws RepositoryServiceException
	 */
	public String checkin(Folder folder, FolderEntry entry, 
			String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Returns whether the specified file resource is currently checked out
	 * or not. This does not tell who checked it out though. 
	 * 
	 * @param folder
	 * @param entry
	 * @param relativeFilePath
	 * @return
	 * @throws RepositoryServiceException
	 */
	public boolean isCheckedOut(Folder folder, FolderEntry entry, 
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
