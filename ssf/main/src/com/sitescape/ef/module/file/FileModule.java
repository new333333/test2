package com.sitescape.ef.module.file;

import java.io.OutputStream;
import java.util.List;

import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.HistoryStamp;

/**
 * Provides uniform interface and integrated management for various file 
 * resources in the system hiding the details of the actual repository
 * systems from the caller. Provides higher-level wrapper around underlying
 * repository system implementations and adds metadata management which
 * may require database transaction support.   
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
	 * If any of the files is currently checked out, this forcefully unchecks 
	 * it before deleting it.
	 * 
	 * @param binder
	 * @param entity
	 * metadata on the <code>entity</code>. 
	 */
	public void deleteFiles(Binder binder, DefinableEntity entity) throws FileException;
	
	/**
	 * Deletes the specified file. If applicable, also delete generated files
	 * (scaled file and thumbnail file) associated with the primary file. 
	 * <p>
	 * If the file is currently checked out by anyone, this forcefully unchecks 
	 * it before deleting it. 
	 * 
	 * @param repositoryServiceName
	 * @param binder
	 * @param entity
	 * @param fileName
	 * @throws NoSuchFileException
	 * @throws FileException
	 */
	public void deleteFile(Binder binder, DefinableEntity entity, 
			String repositoryServiceName, String fileName) 
		throws NoSuchFileException, FileException;
	
	/**
	 * Writes the specified file to the system. If applicable, generate 
	 * secondary files (scaled file and thumbnail file) associated with
	 * the primary file and write them out as well. Generated files are 
	 * NOT versioned. 
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
	 * @param entity
	 * @param fui
	 * @throws CheckedOutByOtherException
	 * @throws FileException
	 */
    //public void writeFile(Binder binder, DefinableEntity entity, FileUploadItem fui) 
    //	throws CheckedOutByOtherException, FileException;
    
    /**
     * Reads the specified file into the output stream.
     * 
     * @param repositoryServiceName
     * @param binder
     * @param entity
     * @param fileName
     * @param out
     * @throws NoSuchFileException
     * @throws FileException
     */
    public void readFile(Binder binder, DefinableEntity entity, 
    		String repositoryServiceName, String fileName, OutputStream out) 
    	throws NoSuchFileException, FileException;
    
    /**
     * Reads the specified file into the output stream.
     * 
     * @param fa
     * @param binder
     * @param entity
     * @param out
	 * @throws NoSuchFileException
     * @throws FileException
     */
	public void readFile(Binder binder, DefinableEntity entity, FileAttachment fa, 
			OutputStream out) throws NoSuchFileException, FileException;
	
    /**
     * Reads the specified scaled file into the output stream.
     * 
     * @param repositoryServiceName
     * @param binder
     * @param entity
     * @param primaryFileName
     * @param out
     * @throws NoSuchFileException
     * @throws FileException
     */
    public void readScaledFile(Binder binder, 
    		DefinableEntity entity, String repositoryServiceName, String primaryFileName, 
    		OutputStream out) 
    	throws NoSuchFileException, FileException;
    
    /**
     * Reads the specified scaled file into the output stream.
     * 
     * @param fa
     * @param binder
     * @param entity
     * @param out
	 * @throws NoSuchFileException
     * @throws FileException
     */
	public void readScaledFile(Binder binder, DefinableEntity entity, FileAttachment fa, 
			OutputStream out) throws NoSuchFileException, FileException;
	
	/**
	 * Returns a file object representing the thumbnail of the specified file. 
	 * The returned thumbnail file is directly accessible by the caller.
	 * If the thumbnail was originally stored as "indirectly accessible" file,
	 * the caller must not use this method. 
	 * <p>
	 * This method does NOT tell whether or not the physical file actually
	 * exists on the file system. The caller will have to use <code>exists</code>
	 * method on the returned file object to actually determine the existence
	 * of the file.
	 * 
	 * @param binder
	 * @param entity
	 * @param fileName
	 * @return
	 */
	//public File getDirectlyAccessibleThumbnailFile(Binder binder, DefinableEntity entity, 
	//		String fileName);
	
    /**
     * Reads the specified thumbnail file into the output stream.
     * If the thumbnail was originally stored as "directly accessible" file,
     * the caller must not use this method. 
     * 
     * @param repositoryServiceName
     * @param binder
     * @param entity
     * @param primaryFileName
     * @param out
     * @throws NoSuchFileException
     * @throws FileException
     */
    public void readIndirectlyAccessibleThumbnailFile
    	(Binder binder, 
    		DefinableEntity entity, String repositoryServiceName, String primaryFileName, OutputStream out) 
    	throws NoSuchFileException, FileException;
    
    /**
     * Reads the specified scaled file into the output stream.
     * If the thumbnail was originally stored as "directly accessible" file,
     * the caller must not use this method. 
     * 
     * @param fa
     * @param binder
     * @param entity
     * @param out
	 * @throws NoSuchFileException
     * @throws FileException
     */
	public void readIndirectlyAccessibleThumbnailFile(
			Binder binder, DefinableEntity entity, FileAttachment fa, OutputStream out) 
		throws NoSuchFileException, FileException;
	
	/**
	 * (Re)generate scaled file from the specified primary file.
	 * Scaled file is not versioned, thus newly generated file replaces 
	 * old one if exists.   
	 * 
	 * @param repositoryServiceName
	 * @param binder
	 * @param entity
	 * @param primaryFileName
	 * @param maxWidth
	 * @param maxHeight
	 * @throws NoSuchFileException
	 * @throws FileException
	 */
	public void generateScaledFile(Binder binder, 
    		DefinableEntity entity, String repositoryServiceName, String primaryFileName, int maxWidth, int maxHeight) 
		throws NoSuchFileException, FileException;
	
	/**
	 * (Re)generate thumbnail file from the specified primary file.
	 * Thumbnail file is not versioned, thus newly generated file replaces 
	 * old one if exists.   
	 * 
	 * @param repositoryServiceName
	 * @param binder
	 * @param entity
	 * @param primaryFileName
	 * @param thumbnailDirectlyAccessible
	 * @throws NoSuchFileException
	 * @throws FileException
	 */
	public void generateThumbnailFile(Binder binder, 
    		DefinableEntity entity, String repositoryServiceName, String primaryFileName, int maxWidth, int maxHeight,
    		boolean thumbnailDirectlyAccessible) 
		throws NoSuchFileException, FileException;

	/**
	 * (Re)generate both scaled file and thumbnail file from the specified 
	 * primary file. Generated files are not versioned, thus newly generated 
	 * files replace old ones if exists.   
	 * 
	 * @param repositoryServiceName
	 * @param binder
	 * @param entity
	 * @param primaryFileName
	 * @param thumbnailDirectlyAccessible
	 * @throws NoSuchFileException
	 * @throws FileException
	 */
	public void generateFiles(Binder binder, 
    		DefinableEntity entity, String repositoryServiceName, String primaryFileName, int maxWidth, int maxHeight,
    		int thumbnailMaxWidth, int thumbnailMaxHeight, 
    		boolean thumbnailDirectlyAccessible) 
		throws NoSuchFileException, FileException;
	
	/**
	 * Returns whether a scaled copy of the file exists or not. 
	 * 
	 * @param repositoryServiceName
	 * @param binder
	 * @param entity
	 * @param primaryFileName
	 * @return
	 * @throws NoSuchFileException raised if unrecognized primary file
	 * @throws FileException
	 */
	public boolean scaledFileExists(Binder binder, DefinableEntity entity, 
			String repositoryServiceName, String primaryFileName) 
		throws NoSuchFileException, FileException;
	
	public boolean scaledFileExists(Binder binder, DefinableEntity entity, 
			FileAttachment fAtt) 
		throws NoSuchFileException, FileException;
	
	/**
	 * Returns whether a thumbnail of the file exists or not. 
	 * 
	 * @param repositoryServiceName
	 * @param binder
	 * @param entity
	 * @param primaryFileName
	 * @return
	 * @throws NoSuchFileException raised if unrecognized primary file
	 * @throws FileException
	 */
	public boolean thumbnailFileExists(Binder binder, DefinableEntity entity, 
			String repositoryServiceName, String primaryFileName) 
	throws NoSuchFileException, FileException;
	
	/**
	 * If the specified file is checked out, returns <code>HistoryStamp</code>
	 * containing when/by whom the file was checked out. If the file is not
	 * checked out, it returns <code>null</code>.
	 * 
	 * @param binder
	 * @param entity
	 * @param fileName
	 * @return
	 */
	public HistoryStamp getCheckoutInfo(Binder binder, DefinableEntity entity, 
			String repositoryServiceName, String fileName);
	
	/**
	 * Checkes out the specified file. 
	 * <p>
	 * If the file is already checked out by the user making this call, this
	 * operation is noop. If it is currently checked out by someone else, it
	 * throws <code>CheckedOutByOtherException</code>.
	 * 
	 * @param binder
	 * @param entity
	 * @param fileName
	 */
	/**
	 * @param binder
	 * @param entity
	 * @param fileName
	 * @throws NoSuchFileException
	 * @throws CheckedOutByOtherException
	 */
	public void checkout(Binder binder, 
			DefinableEntity entity, String repositoryServiceName, String fileName) throws CheckedOutByOtherException, 
			NoSuchFileException, FileException;
	
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
	 * @param entity
	 * @param fileName
	 * @throws CheckedOutByOtherException
	 * @throws NoSuchFileException
	 * @throws FileException
	 */
	public void uncheckout(Binder binder, 
			DefinableEntity entity, String repositoryServiceName, String fileName) throws CheckedOutByOtherException, 
			NoSuchFileException, FileException;

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
	 * @param entity
	 * @param fileName
	 * @throws CheckedOutByOtherException
	 * @throws NoSuchFileException
	 * @throws FileException
	 */
	public void checkin(Binder binder, 
			DefinableEntity entity, String repositoryServiceName, String fileName) throws CheckedOutByOtherException, 
			NoSuchFileException, FileException;
	/*
	public void createThumbnail(String repositoryServiceName, Binder binder,
			DefinableEntity entity, String fileName, String thumbFileName,
			int maxWidth, int maxHeight) 
		throws NoSuchFileException, FileException;
	
	public void createThumbnail(FileAttachment fa, Binder binder, DefinableEntity entity,
			String thumbFileName,int maxWidth, int maxHeight) 
		throws FileException;
		*/
	
    public FilesErrors writeFiles(Binder binder, DefinableEntity entity, List fileUploadItems, 
    		FilesErrors errors);
    
    public FilesErrors filterFiles(Binder binder, List fileUploadItems) throws FilterException;
}
