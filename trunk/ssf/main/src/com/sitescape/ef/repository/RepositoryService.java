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
	 * Returns a list of URIs for all versions of the specified resource that
	 * exist.
	 * <p>
	 * If the repository service does not support versioning, it returns 
	 * <code>null</code>. 
	 * 
	 * @param folder
	 * @param entry
	 * @param relativeFilePath
	 * @return
	 * @throws RepositoryServiceException
	 */
	public List fileVersionsURIs(Folder folder, FolderEntry entry, String relativeFilePath)
		throws RepositoryServiceException;
	
	/**
	 * Writes the file resource to the repository system. 
	 * <p>
	 * If the underlying repository system supports versioning and the specified
	 * file resource already exists, it creates a new version. If the repository
	 * does not support versioning, it overrides the existing resource. 
	 * <p>
	 * If the underlying repository system supports checkout/checkin and the
	 * specified resource already exists, it is expected that the caller has
	 * already checked out the resource prior to calling this method. 
	 * If the condition is not met, it throws an exception. 
	 * 
	 * @param folder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @param mf
	 * @throws RepositoryServiceException
	 */
	public void write(Folder folder, FolderEntry entry, String relativeFilePath, MultipartFile mf) 
		throws RepositoryServiceException;
	
	/**
	 * Reads the content of the specified file resource from the repository 
	 * system. 
	 * <p>
	 * The content being read is identical to the latest checked-in version 
	 * of the file resource.
	 * 
	 * @param folder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @param out
	 * @throws RepositoryServiceException
	 */
	public void read(Folder folder, FolderEntry entry, String relativeFilePath, OutputStream out) 
		throws RepositoryServiceException;
	
	/**
	 * Reads from the repository system the content of the specified version 
	 * of the file resource identified by the file version URI.
	 * <p>
	 * If the specified version does not exist, it throws <code>RepositoryServiceException</code>.
	 * If the repository service does not support versioning, it throws 
	 * <code>UnsupportedOperationException</code>. It is because this API
	 * provides no mechanism for the caller to obtain a valid file version URI
	 * from the repository system with no versioning support, hence runtime
	 * invocation of this method is considered a programming error. See
	 * {@link #fileVersionsURIs} for more info.
	 *
	 * @param fileVersionURI
	 * @param out
	 * @throws RepositoryServiceException
	 */
	public void readVersion(String fileVersionURI, OutputStream out) 
		throws RepositoryServiceException, UnsupportedOperationException;
	
	/**
	 * Checks out the specified file resource. 
	 * <p>
	 * If the underlying repository system does not support checkout/checkin,
	 * this silently ignores the request. It allows for application to be written
	 * identically whether or not the repository system supports checkout/checkin.
	 * 
	 * @param folder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @throws RepositoryServiceException
	 */
	public void checkout(Folder folder, FolderEntry entry, String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Checks in the specified file resource. 
	 * <p>
	 * If the underlying repository system does not support checkout/checkin,
	 * this silently ignores the request. It allows for application to be written
	 * identically whether or not the repository system supports checkout/checkin. 
	 * 
	 * @param folder
	 * @param entry
	 * @param relativeFilePath A pathname of the file relative to the entry. This may
	 * simply be the name of the file. 
	 * @throws RepositoryServiceException
	 */
	public void checkin(Folder folder, FolderEntry entry, String relativeFilePath) throws RepositoryServiceException;
	
	/**
	 * Returns whether the repository service supports versioning or not. 
	 * @return
	 */
	public boolean supportVersioning();
	
	/**
	 * Returns whether the repository service supports checkout/checkin feature.
	 * 
	 * @return
	 */
	public boolean supportCheckout();
	
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
