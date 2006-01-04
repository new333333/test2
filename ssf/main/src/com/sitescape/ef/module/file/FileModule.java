package com.sitescape.ef.module.file;

import java.io.OutputStream;

import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.util.FileUploadItem;

/**
 * Provides higher-level wrapper around repository service, and adds
 * metadata management (hence needs database transaction support).
 * 
 * @author jong
 *
 */
public interface FileModule {
	
	/**
	 * Delete all files attached to the entry.
	 * <p>
	 * If any of the files is currently checked out, this forcefully unchecks 
	 * it before deleting it.
	 * 
	 * @param binder
	 * @param entry
	 * metadata on the <code>entry</code>. 
	 */
	public void deleteFiles(Binder binder, Entry entry);
	
	/**
	 * Deletes the specified file. 
	 * <p>
	 * If the file is currently checked out by anyone, this forcefully unchecks 
	 * it before deleting it. 
	 * 
	 * @param repositoryServiceName
	 * @param binder
	 * @param entry
	 * @param fileName
	 * @throws NoSuchFileException
	 * @throws RepositoryServiceException
	 */
	public void deleteFile(String repositoryServiceName, Binder binder, 
			Entry entry, String fileName) throws NoSuchFileException, 
			RepositoryServiceException;
	
	/**
	 * Writes the specified file to the system.
	 * <p>
	 * If the file doesn't already exist, it creates it.
	 * <p>
	 * If the file already exists and it is not currently checked out by anyone,
	 * it attempts to check out, update the file, and check it back in, which
	 * will create a new version of the file if the underlying repository system
	 * supports versioning.<br>
	 * If the file is already checked out by the user, the content of the file
	 * is updated, but new version is not created until an explicit
	 * <code>checkin</code> is performed by the user. In this case, the update
	 * can be subsequently rolled back by the user by calling
	 * <code>uncheckout</code>.<br>
	 * If the file is currently checked out by someone else, it throws
	 * <code>CheckedOutByOtherException</code>.
	 * 
	 * @param binder
	 * @param entry
	 * @param fui
	 * @throws RepositoryServiceException
	 */
    public void writeFile(Binder binder, Entry entry, FileUploadItem fui) 
    	throws CheckedOutByOtherException, RepositoryServiceException;
    
    /**
     * Reads the specified file into the output stream.
     * 
     * @param repositoryServiceName
     * @param binder
     * @param entry
     * @param fileName
     * @param out
     * @throws NoSuchFileException
     * @throws RepositoryServiceException
     */
    public void readFile(String repositoryServiceName, Binder binder, 
    		Entry entry, String fileName, OutputStream out) 
    	throws NoSuchFileException, RepositoryServiceException;
    
    /**
     * Reads the specified file into the output stream.
     * 
     * @param fa
     * @param binder
     * @param entry
     * @param out
     * @throws RepositoryServiceException
     */
	public void readFile(FileAttachment fa, Binder binder, Entry entry, 
			OutputStream out) throws RepositoryServiceException;
	
	/**
	 * If the specified file is checked out, returns <code>HistoryStamp</code>
	 * containing when/by whom the file was checked out. If the file is not
	 * checked out, it returns <code>null</code>.
	 * 
	 * @param binder
	 * @param entry
	 * @param fileName
	 * @return
	 */
	public HistoryStamp getCheckoutInfo(String repositoryServiceName, 
			Binder binder, Entry entry, String fileName);
	
	/**
	 * Checkes out the specified file. 
	 * <p>
	 * If the file is already checked out by the user making this call, this
	 * operation is noop. If it is currently checked out by someone else, it
	 * throws <code>CheckedOutByOtherException</code>.
	 * 
	 * @param binder
	 * @param entry
	 * @param fileName
	 */
	/**
	 * @param binder
	 * @param entry
	 * @param fileName
	 * @throws CheckedOutByOtherException
	 */
	public void checkout(String repositoryServiceName, Binder binder, 
			Entry entry, String fileName) throws CheckedOutByOtherException, 
			NoSuchFileException, RepositoryServiceException;
	
	/**
	 * Cancels the checkout for the specified file. 
	 * <p>
	 * If the file is not checked out by anyone, this method has no effect.<br>
	 * If the file is checked out by the user making this call, it cancels
	 * the checkout. If the underlying repository system supports versioning,
	 * this will restore the state of the file back to what it was prior to
	 * checking it out.<br>
	 * If the file is checked out by someone else, it throws 
	 * <code>CheckedOutByOtherException</code>.
	 * 
	 * @param binder
	 * @param entry
	 * @param fileName
	 * @throws CheckedOutByOtherException
	 * @throws RepositoryServiceException
	 */
	public void uncheckout(String repositoryServiceName, Binder binder, 
			Entry entry, String fileName) throws CheckedOutByOtherException, 
			NoSuchFileException, RepositoryServiceException;

	/**
	 * Checkes in the specified file. 
	 * <p>
	 * If the file is already checked in (i.e., currently not checked out), 
	 * this method has no effect.<br>
	 * If the file is checked out by the user making this call, it makes the
	 * changes made since previous checkout permanent by creating a new version
	 * of the file assuming that the underlying repository system supports
	 * versioning.<br>
	 * If the file is checked out by someone else, it throws
	 * <code>CheckedOutByOtherException</code>.
	 * 
	 * @param binder
	 * @param entry
	 * @param fileName
	 * @throws RepositoryServiceException
	 */
	public void checkin(String repositoryServiceName, Binder binder, 
			Entry entry, String fileName) throws CheckedOutByOtherException, 
			NoSuchFileException, RepositoryServiceException;
	
	public void createThumbnail(String repositoryServiceName, Binder binder,
			Entry entry, String fileName, String thumbFileName,
			int maxWidth, int maxHeight) 
		throws NoSuchFileException, RepositoryServiceException;
	
	public void createThumbnail(FileAttachment fa, Binder binder, Entry entry,
			String thumbFileName,int maxWidth, int maxHeight) 
	throws RepositoryServiceException;
}
