package com.sitescape.ef.module.file.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.stream.FileImageInputStream;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.ef.InternalException;
import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.FolderDao;
import com.sitescape.ef.dao.ProfileDao;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.ChangeLog;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FileItem;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.Reservable;
import com.sitescape.ef.domain.ReservedByAnotherUserException;
import com.sitescape.ef.domain.TitleException;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.VersionAttachment;
import com.sitescape.ef.domain.FileAttachment.FileLock;
import com.sitescape.ef.module.definition.DefinitionUtils;
import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.module.file.ContentFilter;
import com.sitescape.ef.module.file.DeleteVersionException;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.file.FilesErrors;
import com.sitescape.ef.module.file.FilterException;
import com.sitescape.ef.module.file.LockIdMismatchException;
import com.sitescape.ef.module.file.LockedByAnotherUserException;
import com.sitescape.ef.module.shared.ChangeLogUtils;
import com.sitescape.ef.module.shared.EntityIndexUtils;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.repository.RepositorySession;
import com.sitescape.ef.repository.RepositorySessionFactory;
import com.sitescape.ef.repository.RepositorySessionFactoryUtil;
import com.sitescape.ef.repository.RepositoryUtil;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.search.LuceneSessionFactory;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.search.SearchObject;
import com.sitescape.ef.util.FileHelper;
import com.sitescape.ef.util.FilePathUtil;
import com.sitescape.ef.util.FileStore;
import com.sitescape.ef.util.FileUploadItem;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.util.Thumbnail;
import com.sitescape.ef.util.ThumbnailException;
import com.sitescape.ef.docconverter.HtmlConverter;
import com.sitescape.ef.docconverter.IHtmlConverterManager;
import com.sitescape.ef.web.util.FilterHelper;

/**
 * This implementing class utilizes transactional demarcation strategies that 
 * are finer granularity than typical module implementations, in an effort to
 * avoid lengthy transaction duration that could have occured if the actual
 * file update operation was made during the transaction. To support that,
 * the public methods exposed by this implementation are not transaction 
 * demarcated. Instead, this implementation uses helper methods (defined in
 * another collaborator object) for the part of the operations that might
 * require interaction with the database (that is, those operations that 
 * change the state of one or more domain objects) and put the transactional 
 * support around those methods hence reducing individual transaction duration.
 * Of course, this finer granularity transactional control will be of no effect
 * if the caller of this service was already transactional (i.e., it controls
 * transaction bounrary that is more coarse). Whenever possible, this practise 
 * is discouraged for obvious performance/scalability reasons.  
 * 
 * @author jong
 *
 */
public class FileModuleImpl implements FileModule, InitializingBean {

	private static final String FAILED_FILTER_FILE_DELETE 			= "DELETE";
	private static final String FAILED_FILTER_FILE_MOVE 			= "MOVE";
	private static final String FAILED_FILTER_FILE_DEFAULT			= FAILED_FILTER_FILE_DELETE;
	private static final String FAILED_FILTER_TRANSACTION_CONTINUE 	= "CONTINUE";
	private static final String FAILED_FILTER_TRANSACTION_ABORT 	= "ABORT";
	private static final String FAILED_FILTER_TRANSACTION_DEFAULT	= FAILED_FILTER_TRANSACTION_ABORT;

	// TODO To be removed once fixup is no longer necessary
	private static final String SCALED_FILE_SUFFIX = "__ssfscaled_";
	private static final String THUMBNAIL_FILE_SUFFIX = "__ssfthumbnail_";
	private static final String HTML_FILE_SUFFIX = ".html";
	
	private static final String SCALED_SUBDIR = "scaled";
	private static final String THUMB_SUBDIR = "thumb";
	private static final String HTML_SUBDIR = "html";
		
	protected Log logger = LogFactory.getLog(getClass());

	private CoreDao coreDao;
	private FolderDao folderDao;
	private ProfileDao profileDao;
	protected LuceneSessionFactory luceneSessionFactory;
	private TransactionTemplate transactionTemplate;
	private ContentFilter contentFilter;
	private String failedFilterFile;
	private String failedFilterTransaction;
	private int lockExpirationAllowance; // number of seconds
	private FileStore cacheFileStore;
	
	protected CoreDao getCoreDao() {
		return coreDao;
	}

	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}

	public FolderDao getFolderDao() {
		return folderDao;
	}
	public void setFolderDao(FolderDao folderDao) {
		this.folderDao = folderDao;
	}
	protected ProfileDao getProfileDao() {
		return profileDao;
	}
	
	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}
	
	protected LuceneSessionFactory getLuceneSessionFactory() {
		return luceneSessionFactory;
	}
	public void setLuceneSessionFactory(LuceneSessionFactory luceneSessionFactory) {
		this.luceneSessionFactory = luceneSessionFactory;
	}
	
	protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	protected ContentFilter getContentFilter() {
		return contentFilter;
	}
	
	public void setContentFilter(ContentFilter contentFilter) {
		this.contentFilter = contentFilter;
	}
	
	protected String getFailedFilterFile() {
		return failedFilterFile;
	}

	public void setLockExpirationAllowance(int lockExpirationAllowance) {
		this.lockExpirationAllowance = lockExpirationAllowance;
	}
	
	protected int getLockExpirationAllowanceMilliseconds() {
		return this.lockExpirationAllowance * 1000;
	}
	
	public void setFailedFilterFile(String failedFilterFile) {
		if(FAILED_FILTER_FILE_DELETE.equals(failedFilterFile)) {
			this.failedFilterFile = FAILED_FILTER_FILE_DELETE;
		}
		else if(FAILED_FILTER_FILE_MOVE.equals(failedFilterFile)) {
			this.failedFilterFile = FAILED_FILTER_FILE_MOVE;
		}
		else {
			logger.info("Unknown failedFilterFile " + failedFilterFile + 
					" defaults to " + FAILED_FILTER_FILE_DEFAULT);
			this.failedFilterFile = FAILED_FILTER_FILE_DEFAULT;
		}
	}

	protected String getFailedFilterTransaction() {
		return failedFilterTransaction;
	}

	public void setFailedFilterTransaction(String failedFilterTransaction) {
		if(FAILED_FILTER_TRANSACTION_CONTINUE.equals(failedFilterTransaction)) {
			this.failedFilterTransaction = FAILED_FILTER_TRANSACTION_CONTINUE;
		}
		else if(FAILED_FILTER_TRANSACTION_ABORT.equals(failedFilterTransaction)) {
			this.failedFilterTransaction = FAILED_FILTER_TRANSACTION_ABORT;
		}
		else {
			logger.info("Unknown failedFilterTransaction " + failedFilterTransaction + 
					" defaults to " + FAILED_FILTER_TRANSACTION_DEFAULT);
			this.failedFilterTransaction = FAILED_FILTER_TRANSACTION_DEFAULT;
		}
	}

	public void afterPropertiesSet() throws Exception {
		cacheFileStore = new FileStore(SPropsUtil.getString("cache.file.store.dir"));
	}
	
	public FilesErrors deleteFiles(Binder binder, DefinableEntity entry,
			FilesErrors errors) {
		return deleteFiles(binder, entry, errors, true);
	}
	// optimization : don't delete attachments, only delete the actual file
	//this allows bulk deletes of attachments
	public FilesErrors deleteFiles(final Binder binder, final DefinableEntity entry,
			FilesErrors errors, boolean deleteAttachment) {
		if(errors == null)
			errors = new FilesErrors();
		
		List fAtts = entry.getFileAttachments();
		for(int i = 0; i < fAtts.size(); i++) {
			final FileAttachment fAtt = (FileAttachment) fAtts.get(i);

			try {
				deleteFileInternal(binder, entry, fAtt, errors, deleteAttachment);

			}
			catch(Exception e) {
				logger.error("Error deleting file " + fAtt.getFileItem().getName(), e);
    			errors.addProblem(new FilesErrors.Problem
    					(fAtt.getRepositoryName(),  fAtt.getFileItem().getName(), 
    							FilesErrors.Problem.OTHER_PROBLEM, e));
			}
		}
		
		// Even in the situation where the operation was not entirely successful,
		// we need to reflect the corresponding metadata changes back to the
		// database. 
		if (!errors.getProblems().isEmpty()) triggerUpdateTransaction();
		
		return errors;
	}
	
	public FilesErrors deleteFile(Binder binder, DefinableEntity entry,
			FileAttachment fAtt, FilesErrors errors) {
		if(errors == null)
			errors = new FilesErrors();
		
		try {
			deleteFileInternal(binder, entry, fAtt, errors, true);
		}
		catch(Exception e) {
			logger.error("Error deleting file " + fAtt.getFileItem().getName(), e);
			errors.addProblem(new FilesErrors.Problem
					(fAtt.getRepositoryName(),  fAtt.getFileItem().getName(), 
							FilesErrors.Problem.OTHER_PROBLEM, e));
			//make sure any updates that happened get recored
			triggerUpdateTransaction();
		}
				
		
		return errors;
	}
	public void readFile(Binder binder, DefinableEntity entry, FileAttachment fa, 
			OutputStream out) {
		if(fa instanceof VersionAttachment) {
			RepositoryUtil.readVersion(fa.getRepositoryName(), binder, entry, 
					fa.getFileItem().getName(), ((VersionAttachment) fa).getVersionName(), out);			
		}
		else {
			RepositoryUtil.read(fa.getRepositoryName(), binder, entry, 
				fa.getFileItem().getName(), out);
		}
	}
	
	public InputStream readFile(Binder binder, DefinableEntity entry, FileAttachment fa) { 
		if(fa instanceof VersionAttachment) {
			return RepositoryUtil.readVersion(fa.getRepositoryName(), binder, entry, 
					fa.getFileItem().getName(), ((VersionAttachment) fa).getVersionName());
		}
		else {
			return RepositoryUtil.read(fa.getRepositoryName(), binder, entry, 
				fa.getFileItem().getName());
		}
	}
	
	public boolean scaledFileExists(Binder binder, 
			DefinableEntity entry, FileAttachment fAtt) {
		String filePath = FilePathUtil.getFilePath(binder, entry, SCALED_SUBDIR, fAtt.getFileItem().getName());
		if(cacheFileStore.fileExists(filePath)) {
			return true;
		}
		else {
			// TODO temporary fixup code - to be removed
			return scaledFileExistsInRepository(binder, entry, fAtt);
		}
	}

	private boolean scaledFileExistsInRepository(Binder binder, 
			DefinableEntity entry, FileAttachment fAtt) {
		int fileInfo = RepositoryUtil.fileInfo(fAtt.getRepositoryName(), 
				binder, entry, makeScaledFileNameInRepository(fAtt.getFileItem().getName()));
		
		if(fileInfo == RepositorySession.UNVERSIONED_FILE)
			return true;
		else if(fileInfo == RepositorySession.NON_EXISTING_FILE)
			return false;
		else
			throw new InternalException();
	}

	/*
	public HistoryStamp getCheckoutInfo(
			Binder binder, DefinableEntity entry, FileAttachment fa) {
		return fa.getCheckout();
	}*/
	
	public void readScaledFile(Binder binder, DefinableEntity entry, 
			FileAttachment fa, OutputStream out) {
		String filePath = FilePathUtil.getFilePath(binder, entry, SCALED_SUBDIR, fa.getFileItem().getName());
		if(cacheFileStore.fileExists(filePath)) {
			try {
				cacheFileStore.readFile(filePath, out);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		else {
			// TODO temporary fixup code - to be removed
			RepositoryUtil.read(fa.getRepositoryName(), binder, 
					entry, makeScaledFileNameInRepository(fa.getFileItem().getName()), out);

		}
	}
	
	public void readThumbnailFile(
			Binder binder, DefinableEntity entry, FileAttachment fa, 
			OutputStream out) {

		String relativeFilePath = fa.getFileItem().getName();
		
		String filePath = FilePathUtil.getFilePath(binder, entry, THUMB_SUBDIR, relativeFilePath);
		if(!cacheFileStore.fileExists(filePath)) {
			if (relativeFilePath.endsWith(".jpg")) {
				generateThumbnailFile(binder, entry, fa, 150, 0);
			}			
		}

		try {
			cacheFileStore.readFile(filePath, out);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Read cached HTML conversion file. If this file does not exist we must create it by going into the file
	 * respository fetching the non-HTML file an running conversion program to generate HTML file.
	 * 
	 *	@param	url		If images or URL tags exist we need the url to insert a valid HTML reference to items
	 *	@param	binder	File location information
	 *	@param	entry	File location information
	 *	@param	fa		File attachment information
	 *	@param	out		Output Stream that we will feed HTML file too
	 *
	 */
	public void readCacheHtmlFile(String url, Binder binder, DefinableEntity entry, FileAttachment fa, OutputStream out) 
	{
		InputStream is = null;
		File htmlFile = null;
		String filePath = "";

		try
		{
			// See if we already have a cached version of file.
			// The cached version of the file will have an HTML extension as opposed to the original file extension
			// such as (DOC, PPT, etc). We need to change the filename to reflect this.
			filePath = FilePathUtil.getFilePath(binder, entry, HTML_SUBDIR, fa.getId() + File.separator + fa.getFileItem().getName());
			filePath = filePath.substring(0, filePath.lastIndexOf('.')) + HTML_FILE_SUFFIX;
			htmlFile = cacheFileStore.getFile(filePath);
			if (htmlFile != null
			&& htmlFile.exists()
			&& htmlFile.lastModified() >= fa.getModification().getDate().getTime())
			{
				// Process Character file
				is = new FileInputStream(htmlFile);
				byte[] bbuf = new byte[is.available()];
				is.read(bbuf);
				out.write(bbuf);
				return;
			}
			else
			{
				generateHtmlFile(url, binder, entry, fa);
				// Process Character file
				is = new FileInputStream(htmlFile);
				byte[] bbuf = new byte[is.available()];
				is.read(bbuf);
				out.write(bbuf);
			}
		}
		catch(FileNotFoundException e) {
			throw new UncheckedIOException(e);
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}	
		finally {
			if (is != null)
			{
				try
				{
					is.close();
				} catch (Exception e) {}
			}
		}
	}
	
	/**
	 * Read cached URL referenced file from cache repository.
	 * 
	 *	@param	binder			File location information
	 *	@param	entry			File location information
	 *	@param	fa				File attachment information
	 *	@param	out				Output Stream that we will feed HTML file too
	 *	@param	urlFileName		Name of url file we will process
	 *
	 */
	public void readCacheUrlReferenceFile(
			Binder binder, DefinableEntity entry, FileAttachment fa, 
			OutputStream out, String urlFileName)
	{
		byte[] bbuf = null;
		File urlFile = null;
		InputStream is = null;
		String filePath = "";
		
		try
		{
			filePath = FilePathUtil.getFilePath(binder, entry, HTML_SUBDIR, fa.getId() + File.separator + urlFileName);
			urlFile = cacheFileStore.getFile(filePath);
						
			is = new FileInputStream(urlFile);
			bbuf = new byte[is.available()];
			is.read(bbuf);
			out.write(bbuf);
		}
		catch(FileNotFoundException e) {
			throw new UncheckedIOException(e);
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}	
		finally {
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException io) {}
			}
		}
	}
	
	/**
	 * Read cached image file from cache repository.
	 * 
	 *	@param	binder			File location information
	 *	@param	entry			File location information
	 *	@param	fa				File attachment information
	 *	@param	out				Output Stream that we will feed HTML file too
	 *	@param	imageFileName	Name of image file we will process
	 *
	 */
	public void readCacheImageReferenceFile(
			Binder binder, DefinableEntity entry, FileAttachment fa, 
			OutputStream out, String imageFileName)
	{
		String filePath = "";
		byte[] bbuf = null;
		File imageFile = null;
		FileImageInputStream fis = null;
		
		try
		{
			filePath = FilePathUtil.getFilePath(binder, entry, HTML_SUBDIR, fa.getId() + File.separator + imageFileName);
			imageFile = cacheFileStore.getFile(filePath);

			// Process Image file
			fis = new FileImageInputStream(imageFile);
			bbuf = new byte[(int)fis.length()];
			
			fis.readFully(bbuf, 0, (int)fis.length());			
			out.write(bbuf);
		}
		catch(FileNotFoundException e) {
			throw new UncheckedIOException(e);
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}	
		finally {
			if (fis != null)
			{
				try
				{
					fis.close();
				}
				catch (IOException io) {}
			}
		}		
	}
	
	public void readHtmlViewFile(Binder binder, DefinableEntity entity, 
			FileAttachment fa, OutputStream out) throws  
			UncheckedIOException, RepositoryServiceException {
		// TODO - To be written
	}

	public void generateScaledFile(Binder binder, DefinableEntity entry, 
			FileAttachment fa, int maxWidth, int maxHeight) {
		String repositoryName = fa.getRepositoryName();
		String relativeFilePath = fa.getFileItem().getName();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		RepositoryUtil.read(repositoryName, binder, entry, relativeFilePath, baos);
		
		generateAndStoreScaledFile(binder, entry, relativeFilePath, 
				baos.toByteArray(), maxWidth, maxHeight);
	}
	
	public void generateThumbnailFile(Binder binder, 
			DefinableEntity entry, FileAttachment fa, int maxWidth, 
			int maxHeight) {
		String repositoryName = fa.getRepositoryName();
		String relativeFilePath = fa.getFileItem().getName();
		
		// Read the input file from the repository into a byte array. 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		RepositoryUtil.read(repositoryName, binder, entry, relativeFilePath, baos);

		generateAndStoreThumbnailFile(binder, entry, relativeFilePath, 
				baos.toByteArray(), maxWidth, maxHeight);
	}

	/**
	 * Generate HTML file based on an attachment file held in repository. This functionality is used for
	 * 'view as html' functionality. We need to convert a non-HTML file type into a HTML file type.
	 * 
	 * @param url		Url that will be inserted into image an url sources to make them valid
	 * @param binder	SiteScape Binder Object - holds path information in repository
	 * @param entry		SiteScape DefinableEntity Object - holds path information in repository
	 * @param fa		SiteScape FileAttachment Object - represents file in respository
	 *
	 */
	public void generateHtmlFile(String url, Binder binder, 
			 DefinableEntity entry, FileAttachment fa)
	{
		InputStream is = null;
		FileOutputStream fos = null;
		RepositorySession session = null;
		File htmlfile = null,
		 	 originalFile = null;
		String filePath = "",
			   outFile = "",
			   relativeFilePath = "";

		try 
		{
			session = RepositorySessionFactoryUtil.openSession(fa.getRepositoryName());
			relativeFilePath = fa.getFileItem().getName();
			
			filePath = FilePathUtil.getFilePath(binder, entry, HTML_SUBDIR, fa.getId() + File.separator + fa.getFileItem().getName());
			htmlfile = cacheFileStore.getFile(filePath);
			// If the output file's parent directory doesn't already exist, create it.
			File parentDir = htmlfile.getParentFile();
			if(!parentDir.exists())
				parentDir.mkdirs();
			
			try
			{
				is = RepositoryUtil.read(fa.getRepositoryName(), binder, entry, relativeFilePath);
				byte[] bbuf = new byte[is.available()];
				is.read(bbuf);
				filePath = FilePathUtil.getFilePath(binder, entry, HTML_SUBDIR, fa.getId() + File.separator + relativeFilePath);
				originalFile = cacheFileStore.getFile(filePath);
				fos = new FileOutputStream(originalFile);
				fos.write(bbuf);
				fos.flush();
			}
			catch(Exception e)
			{
				if (is != null)
					is.close();
				if (fos != null)
					fos.close();
			}
			
			outFile = htmlfile.getAbsolutePath();
			outFile = outFile.substring(0, outFile.lastIndexOf('.')) + HTML_FILE_SUFFIX;			
			generateAndStoreHtmlFile(url, binder.getId(), entry.getId(), fa.getId(), originalFile.getAbsolutePath(), outFile);
		}
		catch(FileNotFoundException e) {
			throw new UncheckedIOException(e);
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}
	
	/**
	 * This method is functionally equivalent to <code>generateScaledFile</code>
	 * and <code>generateThumbnailFile</code> combined. But this is potentially
	 * more efficient than calling them separately because it reads in the
	 * primary file only once. 
	 */
	public void generateFiles(Binder binder, DefinableEntity entry, 
			FileAttachment fa, int maxWidth, int maxHeight, 
			int thumbnailMaxWidth, int thumbnailMaxHeight) {
		String repositoryName = fa.getRepositoryName();
		String relativeFilePath = fa.getFileItem().getName();

		// Read the input file from the repository into a byte array. 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		RepositoryUtil.read(repositoryName, binder, entry, relativeFilePath, baos);

		// Generate and store scaled file.
		generateAndStoreScaledFile(binder, entry, 
				relativeFilePath, baos.toByteArray(), maxWidth, maxHeight);
		
		// Generate and store thumbnail file.
		generateAndStoreThumbnailFile(binder, entry, 
				relativeFilePath, baos.toByteArray(), maxWidth, maxHeight);
	}
	
    public FilesErrors writeFiles(Binder binder, DefinableEntity entry, 
    		List fileUploadItems, FilesErrors errors) 
    	throws ReservedByAnotherUserException {
		if(errors == null)
    		errors = new FilesErrors();
    	
    	// Read-write operations require up-to-date view of the lock and
    	// reservation state. So we must take care of expired locks first
    	// (if any). This runs in its own transaction.
    	//closeExpiredLocksTransactional(binder, entry, true);
    	
    	checkReservation(entry);
    	
    	for(int i = 0; i < fileUploadItems.size();) {
    		FileUploadItem fui = (FileUploadItem) fileUploadItems.get(i);
    		try {
    			// Unlike deleteFileInternal, writeFileTransactional is transactional.
    			// See the comment in writeFileMetadataTransactional for reason. 
    			if (this.writeFileTransactional(binder, entry, fui, errors)) {
    				//	only advance on success
    				++i;
    			} else {//error handled
	    			if (fui.isRegistered()) getCoreDao().unRegisterLibraryEntry(binder, fui.getOriginalFilename());
    				fileUploadItems.remove(i);
    			}
    		} catch (TitleException lx) {
    			//pass up
    			throw lx;
    		}
    		catch(Exception e) {
    			if (fui.isRegistered()) getCoreDao().unRegisterLibraryEntry(binder, fui.getOriginalFilename());
    			logger.error("Error writing file " + fui.getOriginalFilename(), e);
    			errors.addProblem(new FilesErrors.Problem
    					(fui.getRepositoryName(),  fui.getOriginalFilename(), 
    							FilesErrors.Problem.OTHER_PROBLEM, e));
    			fileUploadItems.remove(i);
    		}
    	}
    	
    	// Because writeFileTransactional itself is transactional, we do not trigger
    	// another transaction here. 
		
		return errors;
    }
    
	public FilesErrors filterFiles(Binder binder, List fileUploadItems) 
		throws FilterException {
		FilesErrors errors = null;
		// Note that we do not have to use String comparison in the expression
		// below. Just reference comparison is enough. 
		if(getFailedFilterTransaction() == FAILED_FILTER_TRANSACTION_CONTINUE) {
			errors = new FilesErrors();
		}
		
    	for(int i = 0; i < fileUploadItems.size();) {
    		FileUploadItem fui = (FileUploadItem) fileUploadItems.get(i);
    		try {
    			getContentFilter().filter(fui);
    			//Only advance on success
    			++i;
    		}
    		catch(FilterException e) {
    			if(errors != null) {
    				// Since we are not throwing an exception immediately in 
    				// this case, log the error right here. 
    				logger.error("Error filtering file " + fui.getOriginalFilename(), e);
    			}
    			// Remove the failed file from the list first. 
    			fileUploadItems.remove(i);
    			if(getFailedFilterFile() == FAILED_FILTER_FILE_DELETE) {
    				try {
						fui.delete();
					} catch (IOException e1) {
						logger.error("Failed to delete bad file " + fui.getOriginalFilename(), e1);
					}
    			}
    			else {
    				try {
						move(binder, fui);
					} catch (IOException e1) {
						logger.error("Failed to move bad file " + fui.getOriginalFilename(), e1);
					}
    			}
    			if(errors != null) {
        			if (fui.isRegistered()) getCoreDao().unRegisterLibraryEntry(binder, fui.getOriginalFilename());
    				errors.addProblem(new FilesErrors.Problem
    					(fui.getRepositoryName(),  fui.getOriginalFilename(), 
    							FilesErrors.Problem.PROBLEM_FILTERING, e));
    			}
    			else {
    				//clean all newly registered titles out
   					for (int j=0; j< fileUploadItems.size(); ++j) {
   						FileUploadItem fu = (FileUploadItem) fileUploadItems.get(j);
   		       			if (fu.isRegistered()) { 
 	    					getCoreDao().unRegisterLibraryEntry(binder, fu.getOriginalFilename());
   						}  		       		     	
    				}
    				throw e;
    			}
    		}
    	}
	
    	return errors;
	}
	
	public void lock(Binder binder, DefinableEntity entity, FileAttachment fa, 
			String lockId, String lockSubject, Date expirationDate, String lockOwnerInfo)
		throws ReservedByAnotherUserException, 
			LockedByAnotherUserException, LockIdMismatchException, 
			UncheckedIOException, RepositoryServiceException {
		User user = RequestContextHolder.getRequestContext().getUser();
		
		// The following call is commented out because: We want to write a
		// bit more tolerating system where client (WebDAV client in this case)
		// can still renew a lock that has already expired. For that reason,
		// we do not want to close all expired locks here. 
    	//closeExpiredLocksTransactional(binder, entity, true);
    	
    	Reservable reservable = null;
    	HistoryStamp reservation = null;
    	if(entity instanceof Reservable) {
    		reservable = (Reservable) entity;
    		reservation = reservable.getReservation();
    	}
    	
    	if(reservation == null || reservation.getPrincipal().equals(user)) {
    		// This is one of the following three conditions:
    		// 1) The entity is not reservable
    		// 2) The entity is reservable but not reserved by anyone
    		// 3) The entity is reservable and reserved by the calling user
    		
    		FileAttachment.FileLock lock = fa.getFileLock();
    		if(lock == null) { // The file is not locked
    			// Lock the file
    			fa.setFileLock(new FileAttachment.FileLock(lockId, lockSubject, 
    					user, expirationDate, lockOwnerInfo));
    		}
    		else { // The file is locked
    			if(lock.getOwner().equals(user)) {
    				// The lock is owned by the same user
        			if(lock.getId().equals(lockId)) { // Lock id matches
        				// Renew the lock
        				lock.setExpirationDate(expirationDate);
        			}
        			else { // Lock id does not match
        				throw new LockIdMismatchException();    				
        			}     		
    			}
    			else { // The lock is owned by another user
    				// Because we chose not to close expired locks above, we 
    				// must test for expired lock here. 
    				if(isLockExpired(lock)) { // The lock has expired
    					// Commit any pending changes associated with the expired lock
    					commitPendingChanges(binder, entity, fa, lock.getOwner()); 
    					// Set the new lock.
    					fa.setFileLock(new FileLock(lockId, lockSubject, 
    							user, expirationDate, lockOwnerInfo));
    				}
    				else { // The lock is still effective
        				throw new LockedByAnotherUserException(entity, fa, lock.getOwner());    					
    				}
    			}
    		}
    	}
    	else {
    		// The entity is reservable and reserved by another user
    		throw new ReservedByAnotherUserException(reservable);
    	}
    	
    	triggerUpdateTransaction();
	}

    public void unlock(Binder binder, DefinableEntity entity, FileAttachment fa,
    		String lockId) throws UncheckedIOException, RepositoryServiceException {
		User user = RequestContextHolder.getRequestContext().getUser();
		
    	//closeExpiredLocksTransactional(binder, entity, true);
    	
		FileAttachment.FileLock lock = fa.getFileLock();

		if(lock != null && lock.getOwner().equals(user) && lock.getId().equals(lockId)) {
			// The file is locked by the calling user and the lock id matches.
			
			// Commit any pending changes associated with the lock. In this 
			// case, we don't care if the lock is effective or expired.
			commitPendingChanges(binder, entity, fa, lock.getOwner());
			
			fa.setFileLock(null); // Clear the lock
			
			triggerUpdateTransaction();
		}
    }


	public void RefreshLocks(Binder binder, DefinableEntity entity) 
		throws RepositoryServiceException, UncheckedIOException {
		closeExpiredLocksTransactional(binder, entity, true);
	}

	
	public void renameFile(Binder binder, DefinableEntity entity, 
			FileAttachment fa, String newName) 
	throws UncheckedIOException, RepositoryServiceException {
		// Rename the file in the repository
		RepositoryUtil.move(fa.getRepositoryName(), binder, entity, 
				fa.getFileItem().getName(), binder, entity, newName);
		// Change our metadata - note that all that needs to change is the
		// file name. Other things such as mod date, etc., remain unchanged.
		if (binder.isLibrary() && !binder.equals(entity)) getCoreDao().updateLibraryName(binder, entity, fa.getFileItem().getName(), newName);
        if ((entity.getEntryDef() != null)  && DefinitionUtils.isSourceItem(entity.getEntryDef().getDefinition(), fa.getName(), "title")) {
        	//check title
        	entity.setTitle(newName);			   			   
		}
		fa.getFileItem().setName(newName);
		
		for(Iterator i = fa.getFileVersionsUnsorted().iterator(); i.hasNext();) {
			VersionAttachment v = (VersionAttachment) i.next();
			v.getFileItem().setName(newName);
		}
		ChangeLog changes = new ChangeLog(entity, ChangeLog.FILERENAME);
		ChangeLogUtils.buildLog(changes, fa);
		getCoreDao().save(changes);
	}
	
	public void moveFile(Binder binder, DefinableEntity entity, 
			FileAttachment fa, Binder destBinder) 
	throws UncheckedIOException, RepositoryServiceException {
		// Rename the file in the repository
		RepositoryUtil.move(fa.getRepositoryName(), binder, entity, 
				fa.getFileItem().getName(), destBinder, entity, 
				fa.getFileItem().getName());
		
		if (binder.isLibrary() && !binder.equals(entity))
			getCoreDao().unRegisterLibraryEntry(binder, fa.getFileItem().getName());
		if (destBinder.isLibrary() && !destBinder.equals(entity))
			getCoreDao().registerLibraryEntry(destBinder, entity, fa.getFileItem().getName());


		ChangeLog changes = new ChangeLog(entity, ChangeLog.FILEMOVE);
		ChangeLogUtils.buildLog(changes, fa);
		getCoreDao().save(changes);
	}
	
	public void deleteVersion(Binder binder, DefinableEntity entity, 
			VersionAttachment va) throws DeleteVersionException {
		//List<String> beforeVersionNames = RepositoryUtil.getVersionNames(va.getRepositoryName(), binder, entity, 
		//		va.getFileItem().getName());
		
		// Check if the version is the only one remaining for the file. 
		FileAttachment fa = va.getParentAttachment();
		if(fa.getFileVersionsUnsorted().size() <= 1)
			throw new DeleteVersionException(va);
			
		// Delete the version from the repository if the repository allows
		// deletion of a version.
		RepositorySessionFactory rsf = 
			RepositorySessionFactoryUtil.getRepositorySessionFactory
			(fa.getRepositoryName());
		
		if(rsf.isVersionDeletionAllowed()) {		
			RepositoryUtil.deleteVersion(fa.getRepositoryName(), binder, entity, 
				va.getFileItem().getName(), va.getVersionName());
		}
		else {
			logger.info("Version " + va.getVersionNumber() + " of file [" + va.getFileItem().getName() + 
					"] is not physically deleted from repository " + fa.getRepositoryName() + 
					" because it does not allow deletion of a version");
		}

		// Update the metadata
		ChangeLog changes = new ChangeLog(entity, ChangeLog.FILEVERSIONDELETE);
		ChangeLogUtils.buildLog(changes, va);
		getCoreDao().save(changes);

		fa.removeFileVersion(va);
		
		// Get the highest previous version
		VersionAttachment highestVa = (VersionAttachment) fa.getFileVersions().iterator().next();
		
		// Copy the last-modified date
		fa.setModification(highestVa.getModification());
		// Copy the file length
		fa.setFileItem(highestVa.getFileItem());
		// Since creation date is not really useful, we will leave it alone. 
		
		//List<String> afterVersionNames = RepositoryUtil.getVersionNames(va.getRepositoryName(), binder, entity, 
		//		va.getFileItem().getName());
	}

	public Set<String> getChildrenFileNames(Binder binder) {
		// We use search engine to get the list of file names in the specified folder.
		
		// create empty search filter
		org.dom4j.Document searchFilter = DocumentHelper.createDocument();
		Element sfRoot = searchFilter.addElement(FilterHelper.FilterRootName);
		sfRoot.addElement(FilterHelper.FilterTerms);
		
		/*
		Element filterTerms = sfRoot.addElement(FilterHelper.FilterTerms);
		Element filterTerm = filterTerms.addElement(FilterHelper.FilterTerm);
		filterTerm.addAttribute(FilterHelper.FilterElementName, BasicIndexUtils.DOC_TYPE_FIELD);
		Element filterTermValueEle = filterTerm.addElement(FilterHelper.FilterElementValue);
		filterTermValueEle.setText(BasicIndexUtils.DOC_TYPE_ATTACHMENT);
		*/
		
		org.dom4j.Document qTree = FilterHelper.convertSearchFilterToSearchBoolean(searchFilter);
		
		Element rootElement = qTree.getRootElement();
		Element boolElement = rootElement.element(QueryBuilder.AND_ELEMENT);
		boolElement.addElement(QueryBuilder.USERACL_ELEMENT);
		
		// look for the specific binder id
		Element field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.BINDER_ID_FIELD);
    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(binder.getId().toString());

    	// look only for attachments
    	field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(BasicIndexUtils.DOC_TYPE_ATTACHMENT);

    	QueryBuilder qb = new QueryBuilder(getProfileDao().getPrincipalIds(RequestContextHolder.getRequestContext().getUser()));
    	SearchObject so = qb.buildQuery(qTree);
    	
    	// create Lucene query    	
    	Query soQuery = so.getQuery();
    	    	
    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        
    	Hits hits = null;
        try {
	        hits = luceneSession.search(soQuery, null, 0, Integer.MAX_VALUE);
        }
        finally {
            luceneSession.close();
        }
    	
        Set<String> result = new HashSet<String>();
        int count = hits.length();
        org.apache.lucene.document.Document doc;
        String fileName;
        for(int i = 0; i < count; i++) {
        	doc = hits.doc(i);
        	fileName = doc.get(EntityIndexUtils.FILENAME_FIELD);
        	if(fileName != null)
        		result.add(fileName);
        }
        
        return result;
	}

	private void triggerUpdateTransaction() {
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {  
                return null;
        	}
        });	
	}
	
	private void deleteFileInternal(Binder binder, DefinableEntity entry,
			FileAttachment fAtt, FilesErrors errors, boolean deleteAttachment) {
		String relativeFilePath = fAtt.getFileItem().getName();
		String repositoryName = fAtt.getRepositoryName();
		
		// Forcefully unlock the file (if locked). We discard pending
		// changes for the file for obvious reason - The file is soon
		// to be deleted.
		// 
		// 6/18/06 JK - It appears that JSR-170 does not provide a way to 
		// uncheckout a previously checked-in file (strange?) Therefore, 
		// we will simply checkin the file (hence possibly creating a 
		// new version) before deletion. Not ideal, but it works. 
		try {
			//closeLock(binder, entry, fAtt, false);
			closeLock(binder, entry, fAtt, true);
		}
		catch(Exception e) {
			logger.error("Error canceling lock on file " + relativeFilePath, e);
			errors.addProblem(new FilesErrors.Problem
					(repositoryName, relativeFilePath, 
							FilesErrors.Problem.PROBLEM_CANCELING_LOCK, e));
			return;
		}

		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

		try {
			try {
				// Delete primary file
				session.delete(binder, entry, relativeFilePath);
			}
			catch(Exception e) {
				logger.error("Error deleting primary file " + relativeFilePath, e);
				errors.addProblem(new FilesErrors.Problem
						(repositoryName, relativeFilePath, 
								FilesErrors.Problem.PROBLEM_DELETING_PRIMARY_FILE, e));
				// If failed to delete primary file, it's not worth trying to
				// delete generated files. Stop here and return.
				return;				
			}

			// Currently file module does not maintain metadata about "generated"
			// files, which unfortunately, leads to this clumsy and not-so-precise
			// attempt at deleting those generated files. However, storing too
			// much metadata has its own share of problems. So...
			
			// Try deleting scaled file if exists
			try {
				String filePath = FilePathUtil.getFilePath(binder, entry, SCALED_SUBDIR, fAtt.getFileItem().getName());
				if(cacheFileStore.fileExists(filePath)) {
					cacheFileStore.deleteFile(filePath);
				}
			}
			catch(Exception e) {
				logger.error("Error deleting scaled copy of " + relativeFilePath, e);
				errors.addProblem(new FilesErrors.Problem
						(repositoryName, relativeFilePath, 
								FilesErrors.Problem.PROBLEM_DELETING_SCALED_FILE, e));
				// Since we successfully deleted the primary file above (which
				// indicates that at least the repository seems up and running),
				// let's not the failure to delete generated file to abort the
				// entire operation. So we proceed. 
			}
			// Try deleting scaled file if exists in the repository
			// TODO - This code exists only for temporary fixup - to be removed
			try {
				String scaledFileName = makeScaledFileNameInRepository(relativeFilePath);
				if (session.fileInfo(binder, entry, scaledFileName) 
						!= RepositorySession.NON_EXISTING_FILE) {
					session.delete(binder, entry, scaledFileName);
				}
			}
			catch(Exception e) {
				logger.error("Error deleting scaled copy of " + relativeFilePath, e);
				errors.addProblem(new FilesErrors.Problem
						(repositoryName, relativeFilePath, 
								FilesErrors.Problem.PROBLEM_DELETING_SCALED_FILE, e));
				// Since we successfully deleted the primary file above (which
				// indicates that at least the repository seems up and running),
				// let's not the failure to delete generated file to abort the
				// entire operation. So we proceed. 
			}

			// Try deleting thumbnail file if exists
			try {
				String filePath = FilePathUtil.getFilePath(binder, entry, THUMB_SUBDIR, fAtt.getFileItem().getName());
				if(cacheFileStore.fileExists(filePath)) {
					cacheFileStore.deleteFile(filePath);
				}
			}
			catch(Exception e) {
				logger.error("Error deleting thumbnail copy of " + relativeFilePath, e);
				errors.addProblem(new FilesErrors.Problem
						(repositoryName, relativeFilePath, 
								FilesErrors.Problem.PROBLEM_DELETING_THUMBNAIL_FILE, e));
				// We proceed and update metadata.
			}

			// Try deleting thumbnail file if exists in the repository
			// TODO - This code exists only for temporary fixup - to be removed
			try {
				String thumbnailFileName = makeThumbnailFileName(relativeFilePath);
				if (session.fileInfo(binder, entry, thumbnailFileName) 
						!= RepositorySession.NON_EXISTING_FILE) {
					session.delete(binder, entry, thumbnailFileName);
				}
			}
			catch(Exception e) {
				logger.error("Error deleting thumbnail copy of " + relativeFilePath, e);
				errors.addProblem(new FilesErrors.Problem
						(repositoryName, relativeFilePath, 
								FilesErrors.Problem.PROBLEM_DELETING_THUMBNAIL_FILE, e));
				// We proceed and update metadata.
			}
		} finally {
			session.close();
		}
		if (deleteAttachment) writeDeleteMetaDataTransactional(binder, entry, fAtt);
	}
	private void writeDeleteMetaDataTransactional(final Binder binder, final DefinableEntity entry, final FileAttachment fAtt)  {
		// Remove metadata and log change
	       getTransactionTemplate().execute(new TransactionCallback() {
	       	public Object doInTransaction(TransactionStatus status) {  
	       		ChangeLog changes = new ChangeLog(entry, ChangeLog.FILEDELETE);
	       		ChangeLogUtils.buildLog(changes, fAtt);
	       		getCoreDao().save(changes);

				entry.removeAttachment(fAtt);
				//if we are deleteing a binder, the the libary names will be deleted elsewhere
				if (binder.isLibrary() && !binder.equals(entry)) getCoreDao().updateLibraryName(binder, entry, fAtt.getFileItem().getName(), null);
		        if (!binder.equals(entry) && (entry.getEntryDef() != null)  && DefinitionUtils.isSourceItem(entry.getEntryDef().getDefinition(), fAtt.getName(), ObjectKeys.FIELD_ENTITY_TITLE)) {
		        	//check title for entries
		        	entry.getEntryDef().setTitle("");			   			   
				}
			        
	            return null;
	       	}
	     });	
	}

	private void move(Binder binder, FileUploadItem fui) throws IOException {
		File filteringFailedDir = SPropsUtil.getFile("filtering.failed.dir");
		if(!filteringFailedDir.exists())
			FileHelper.mkdirs(filteringFailedDir);
		FileHelper.move(fui.getFile(), 
				new File(filteringFailedDir, makeFileName(binder, fui)));
	}
	
	private static final String DELIM = "_";
	private String makeFileName(Binder binder, FileUploadItem fui) {
		RequestContext rc = RequestContextHolder.getRequestContext();
		StringBuffer sb = new StringBuffer();
		sb.append(rc.getZoneName()).	// zone name
			append(DELIM).
			append(rc.getUserName()).	// user name
			append(DELIM).
			append(binder.getId()).		// binder id
			append(DELIM).
			append(fui.getOriginalFilename()).	// file name
			append(DELIM).
			append(System.currentTimeMillis());	// timestamp (in milliseconds)
		return sb.toString();
	}
	
	private void writeFileMetadataTransactional(final Binder binder, final DefinableEntity entry, 
    		final FileUploadItem fui, final FileAttachment fAtt, final boolean isNew) {	
    	
		getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		if(isNew) {
            		// Important: Since file attachment is stored into custom 
        			// attribute using its id value rather than association, 
        			// this new object must be persisted here just in case it 
        			// is to be put into custom attribute down below. This 
        			// piece of code makes it impossible for the caller to 
        			// utilize the usual tactic of delaying update db 
        			// transaction to the very end of the operation.  
            		getCoreDao().save(fAtt);    		
            	}
            	if (fui.getType() == FileUploadItem.TYPE_FILE) {
        			// Find custom attribute by the attribute name. 
        			Set fAtts = null;
        			CustomAttribute ca = entry.getCustomAttribute(fui.getName());
        			if(ca != null)
        				fAtts = (Set) ca.getValueSet();
        			else
        				fAtts = new HashSet();

        			// Simply because the file already exists for the entry does not 
        			// mean that it is known through this particular data element
        			// (i.e., custom attribute). So we need to make sure that it is
        			// made visible through this element.
        			fAtts.add(fAtt); // If it is already in the set, it will have no effect
        			
        			if(ca != null)
        				ca.setValue(fAtts);
        			else
        				entry.addCustomAttribute(fui.getName(), fAtts);
        		} else if (fui.getType() == FileUploadItem.TYPE_ATTACHMENT) {
        			// Add the file attachment to the entry only if new file. 
        			if(isNew) {
        				entry.addAttachment(fAtt);
        			}
        		}  else if (fui.getType() == FileUploadItem.TYPE_TITLE) {
        			String title = fui.getOriginalFilename();
        			CustomAttribute ca = entry.getCustomAttribute(fui.getName());
        			if (ca != null) {
        				//exists - only allow 1 file, move to attachments
        				Set fAtts = (Set) ca.getValueSet();
        				for (Iterator iter=fAtts.iterator(); iter.hasNext(); ) {
        					FileAttachment fa = (FileAttachment)iter.next();
        					//if not the same - move it
        					if (!fAtt.equals(fa)) {
        						fa.setName(null);
        						for (Iterator iter2=fa.getFileVersions().iterator(); iter2.hasNext();) {
        							FileAttachment a = (FileAttachment)iter2.next();
        							a.setName(null);
        						}
        					}
        				}
           				fAtts = new HashSet();
        				fAtts.add(fAtt); 
    			    	ca.setValue(fAtts);
        			} else {
        				Set fAtts = new HashSet();
        				fAtts.add(fAtt);
        				entry.addCustomAttribute(fui.getName(), fAtts);
        			}
        			entry.setTitle(title);
        		}
        		ChangeLog changes;
            	if (isNew)
            		changes = new ChangeLog(entry, ChangeLog.FILEADD);
            	else
            		changes = new ChangeLog(entry, ChangeLog.FILEMODIFY);
        		ChangeLogUtils.buildLog(changes, fAtt);
        		getCoreDao().save(changes);

                return null;
        	}
        });
    }

	/*
	protected void setLockMetadata(final FileAttachment fAtt, final FileLock lock) {
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		fAtt.setFileLock(lock);    
                return null;
        	}
        });
	}
	
	protected void removeAttachmentMetadata(final DefinableEntity entry, final FileAttachment fAtt) {
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		entry.removeAttachment(fAtt);  
                
                return null;
        	}
        });
	}*/
	 
	/**
	 * return true if primary file successfull written
	 * return false or throw exception if either primary not written or metadata update failed.
	 */
    private boolean writeFileTransactional(Binder binder, DefinableEntity entry, 
    		FileUploadItem fui, FilesErrors errors) {
    	
    	/// Work Flow:
    	/// step1: write primary file
    	/// step2: generate and write scaled file (if necessary)
    	/// step3: generate and write thumbnail file (if necessary)
    	/// step4: update metadata in database
    	
		int type = fui.getType();
		if(type != FileUploadItem.TYPE_FILE && type != FileUploadItem.TYPE_ATTACHMENT && type != FileUploadItem.TYPE_TITLE) {
			logger.error("Unrecognized file processing type " + type + " for ["
					+ fui.getName() + ","
					+ fui.getOriginalFilename() + "]");
			throw new InternalException();
		}
		
		String relativeFilePath = fui.getOriginalFilename();
		String repositoryName = fui.getRepositoryName();

		// First, find out whether or not this is a new file for the entry.
		// It is important to note that, as far as identity/existence test
		// goes, the namespace is flat for a single instance of Entry.
		// In other words, regardless of the data elements used for accessing
		// the file, the files are treated identical globally within a single
		// Entry instance as long as their file names are identical. 
		// flatten repository namespace to reduce confusion
//    	FileAttachment fAtt = entry.getFileAttachment(repositoryName, relativeFilePath);
		FileAttachment fAtt = entry.getFileAttachment(relativeFilePath);
    	if ((fAtt != null) && !repositoryName.equals(fAtt.getRepositoryName())) {
			errors.addProblem(new FilesErrors.Problem
					(fAtt.getRepositoryName(), relativeFilePath, 
							FilesErrors.Problem.PROBLEM_FILE_EXISTS, new TitleException(relativeFilePath)));
			return false;
    	}

    	boolean isNew = false;
    	
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryName);

    	try {
	    	// Determine if we need to generate secondary files from the primary file.
	    	if((fui.getMaxHeight() > 0 && fui.getMaxWidth() > 0) || (fui.getGenerateThumbnail())) {
	    		// Unfortunately the scaling function we use expects byte array 
	    		// not input stream as input. In this case we read the file content
	    		// into a byte array once and reuse it for all repository operations
	    		// to avoid having to incur file I/O multiple times. 
	    		byte[] primaryContent = null;
	    		
	    		try {
		    		primaryContent = fui.getBytes();
		    		
		    		// Store primary file first, since we do not want to generate secondary
		    		// files unless we could successfully store the primary file first. 
		    		
		    		if(fAtt == null) { // New file for the entry
		    			isNew = true;
		    			fAtt = createFile(session, binder, entry, fui, primaryContent);
		    		}
		    		else { // Existing file for the entry
		    			writeExistingFile(session, binder, entry, fui, primaryContent);
		    		}
	    		}
	    		catch(Exception e) {
	    			logger.error("Error storing primary file " + relativeFilePath, e);
	    			// We failed to write the primary file. In this case, we 
	    			// discard the rest of the operation (i.e., step2 thru 4).
	    			errors.addProblem(new FilesErrors.Problem
	    					(repositoryName, relativeFilePath, 
	    							FilesErrors.Problem.PROBLEM_STORING_PRIMARY_FILE, e));
	    			return false;
	    		}		

	    		// Scaled file
	        	if(fui.getMaxWidth() > 0 && fui.getMaxHeight() > 0) {
	            	// Generate scaled file which goes into the same repository as
	        		// the primary file except that the generated file is not versioned.
	        		try {
	        			generateAndStoreScaledFile(binder, entry, relativeFilePath,
	        				primaryContent, fui.getMaxWidth(),fui.getMaxWidth());
	        		}
	        		catch(ThumbnailException e) {
	        			// Scaling operation can fail for a variety of reasons, primarily
	        			// when the file format is not supported. Do not cause this to
	        			// fail the entire operation. Simply log it and proceed.  
	        			logger.warn("Error generating scaled copy of " + relativeFilePath, e);
		    			errors.addProblem(new FilesErrors.Problem
		    					(repositoryName, relativeFilePath, 
		    							FilesErrors.Problem.PROBLEM_GENERATING_SCALED_FILE, e));
	        		}
	        		catch(Exception e) {
		    			// Failed to store scaled file. Record the problem and proceed.
	        			logger.warn("Error storing scaled copy of " + relativeFilePath, e);
		    			errors.addProblem(new FilesErrors.Problem
		    					(repositoryName, relativeFilePath, 
		    							FilesErrors.Problem.PROBLEM_STORING_SCALED_FILE, e));	        			
	        		}
	        	}    

	        	// Thumbnail file
	        	if(fui.getGenerateThumbnail()) {
	        		try {
	        			generateAndStoreThumbnailFile(binder, entry,
	        				relativeFilePath, primaryContent, fui.getThumbnailMaxWidth(), 
	        				fui.getThumbnailMaxHeight());
	        		}
	        		catch(ThumbnailException e) {
	        			logger.warn("Error generating thumbnail copy of " + relativeFilePath, e);
		    			errors.addProblem(new FilesErrors.Problem
		    					(repositoryName, relativeFilePath, 
		    							FilesErrors.Problem.PROBLEM_GENERATING_THUMBNAIL_FILE, e));
	        		}
	        		catch(Exception e) {
	        			logger.warn("Error storing thumbnail copy of " + relativeFilePath, e);
		    			errors.addProblem(new FilesErrors.Problem
		    					(repositoryName, relativeFilePath, 
		    							FilesErrors.Problem.PROBLEM_STORING_THUMBNAIL_FILE, e));	        			
	        		}
	        	}
	    	}
	    	else { // We do not need to generate secondary files. 	  
	    		try {
		    		InputStream is = fui.getInputStream();
		    		
		    		try {
			    		if(fAtt == null) { // New file for the entry
			    			isNew = true;
			    			fAtt = createFile(session, binder, entry, fui, is);
			    		}
			    		else { // Existing file for the entry
			    			writeExistingFile(session, binder, entry, fui, is);
			    		}	 
		    		}
		    		finally {
		    			try {
		    				is.close();
		    			}
		    			catch(IOException e) {
		    				logger.error(e.getMessage(), e);
		    			}
		    		}
	    		}
	    		catch(Exception e) {
	    			// We failed to write the primary file. In this case, we 
	    			// discard the rest of the operation (i.e., step2 thru 4).
	    			logger.error("Error storing primary file " + relativeFilePath, e);
	    			errors.addProblem(new FilesErrors.Problem
	    					(repositoryName, relativeFilePath, 
	    							FilesErrors.Problem.PROBLEM_STORING_PRIMARY_FILE, e));
	    			return false;
	    		}
	    	}
    	}
    	finally {
    		session.close();
    	}
    	
    	// Finally update metadata - We do this only after successfully writing
    	// the file to the repository to ensure that our metadata describes
    	// what actually exists. Of course, there could be a failure scenario
    	// where this metadata update fails leaving the file dangling in the
    	// repository. But that is expected to be a lot more rare, and not
    	// quite as destructive as the other case. But the bottom line is, 
    	// unless we have a single transaction that spans both repository
    	// update and database update all within a single unit, there will
    	// always be error cases that can leave the data inconsistent. 
    	// When a repository supports JCA, this should be possible to do
    	// using JTA. But that's not always available, and this version of
    	// the system does not try to address that. 
    	writeFileMetadataTransactional(binder, entry, fui, fAtt, isNew);
    	return true;
    }
    
    private void writeExistingFile(RepositorySession session,
    		Binder binder, DefinableEntity entry, FileUploadItem fui, Object inputData)
		throws LockedByAnotherUserException, RepositoryServiceException, UncheckedIOException {
    	User user = RequestContextHolder.getRequestContext().getUser();
    	String relativeFilePath = fui.getOriginalFilename();
		// flatten repository namespace to reduce confusion
//    	FileAttachment fAtt = entry.getFileAttachment(fui.getRepositoryName(), relativeFilePath);
    	FileAttachment fAtt = entry.getFileAttachment(relativeFilePath);
    	
    	// Before checking the lock, we must make sure that the lock state is
    	// up-to-date.
    	closeExpiredLock(session, binder, entry, fAtt, true);
    	
    	// Now that lock state is current, we can test it for the user.
    	checkLock(entry, fAtt);
    	
    	FileAttachment.FileLock lock = fAtt.getFileLock();
    	
    	// All expired locks were taken care of higher up in the call stack.
    	// Also owner check was done already. So we can assume that lock, 
    	// if exists, is effective and owned by the calling user.
    	
    	String versionName = null;
    	int fileInfo = session.fileInfo(binder, entry, relativeFilePath);
    	if(fileInfo == RepositorySession.VERSIONED_FILE) { // Normal condition
    		// Attempt to check out the file. If the file was already checked out
    		// this is noop. So no harm. 
    		session.checkout(binder, entry, relativeFilePath);
    		// Update the file content
    		updateWithInputData(session, binder, entry, relativeFilePath, inputData);
    		if(lock == null) {
    			// This update request is being made without the user's prior 
    			// obtaining lock. Since there's no lock to associate the 
    			// checkout with, we must checkin the file here. 
    			versionName = session.checkin(binder, entry, relativeFilePath);
    		}
    	}
    	else if(fileInfo == RepositorySession.NON_EXISTING_FILE) {
			// For some reason the file doesn't exist in the repository.
			// That is, our metadata says it exists, but the repository 
			// says otherwise. This reflects some previous error condition.
			// For example, previous attempt to add the file may have
			// failed partially. Or someone may have gone and errorneously
			// deleted the file from the repository. At any rate, the
			// end result is descrepency between the repository system
			// and our metadata. Although not ideal, better response to
			// this kind of situation appears to be the one that is more
			// forgiving or self-curing. This part of code implements that.
			versionName = createVersionedWithInputData(session, binder,
					entry, relativeFilePath, inputData);
    	}
    	else {
    		throw new InternalException();
    	}
    	
		try {
			//if we are adding a new version of an existing attachment to 
			//a uniqueName item, set flag - (will already be set if originally added
			//through a unique element.  In other works, once unique always unique
			if (fui.isUniqueName()) fAtt.setUniqueName(true);
			updateFileAttachment(fAtt, user, versionName, fui.getSize(), fui.getModDate());
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}
    }

    private void updateFileAttachment(FileAttachment fAtt, 
			Principal user, String versionName, long contentLength,
			Date modDate) {
    	HistoryStamp now = new HistoryStamp(user);
    	HistoryStamp mod;
    	if(modDate != null)
    		mod = new HistoryStamp(user, modDate);
    	else
    		mod = now;
    	
		fAtt.setModification(mod);
		
		FileItem fItem = fAtt.getFileItem();
		fItem.setLength(contentLength);
		
		if(versionName != null) {
			// The repository system supports versioning.        			
			int versionNumber = fAtt.getLastVersion().intValue() + 1;
			fAtt.setLastVersion(new Integer(versionNumber));
			
			VersionAttachment vAtt = new VersionAttachment();
			// Creation time is always current real time, whereas modification
			// time could be anything that the caller specified it to be
			// (only the latter contains useful business value). 
			vAtt.setCreation(now);
			vAtt.setModification(fAtt.getModification());
			vAtt.setFileItem(fItem);
			vAtt.setVersionNumber(versionNumber);
			vAtt.setVersionName(versionName);
			vAtt.setRepositoryName(fAtt.getRepositoryName());
			fAtt.addFileVersion(vAtt);
		}
	}
    
    /**
	 * Creates a new file in the system.
	 * <p>
	 * This method adds the file to the repository system, creates a new
	 * <code>FileAttachment</code> object corresponding to the file being 
	 * created, and returns it so that it can be persisted by the caller.
	 * In other words, persisting the new metadata in our database is not
	 * a responsibility of this method.  
	 */
	private FileAttachment createFile(RepositorySession session, 
			Binder binder, DefinableEntity entry, FileUploadItem fui, Object inputData) 
		throws RepositoryServiceException, UncheckedIOException {	
		// Since we are creating a new file, file locking doesn't concern us.
		
		FileAttachment fAtt = createFileAttachment(entry, fui);
		
		String versionName = createVersionedWithInputData(session, binder, entry,
				fui.getOriginalFilename(), inputData);

		createVersionAttachment(fAtt, versionName);

		return fAtt;
	}
	
	private String createVersionedWithInputData(RepositorySession session,
			Binder binder, DefinableEntity entry, String relativeFilePath, Object inputData)
		throws RepositoryServiceException {
		String versionName = null;
		/*if(inputData instanceof MultipartFile) {
			versionName = service.createVersioned(session, binder, entry, 
					relativeFilePath, (MultipartFile) inputData);
		}
		else*/ if(inputData instanceof byte[]) {
			versionName = session.createVersioned(binder, entry, relativeFilePath,
					new ByteArrayInputStream((byte[]) inputData));
		}
		else if(inputData instanceof InputStream) {
			versionName = session.createVersioned(binder, entry, relativeFilePath, 
					(InputStream) inputData);
		}
		else {
			throw new InternalException("Illegal input type [" + inputData.getClass().getName() + "]");
		}		
		
		return versionName;
	}
	
	private void updateWithInputData(RepositorySession session,
			Binder binder, DefinableEntity entry, String relativeFilePath, Object inputData)
		throws RepositoryServiceException {
		/*if(inputData instanceof MultipartFile) {
			service.update(session, binder, entry, 
					relativeFilePath, (MultipartFile) inputData);
		}
		else*/ if(inputData instanceof byte[]) {
			session.update(binder, entry, relativeFilePath,
					new ByteArrayInputStream((byte[]) inputData));
		}
		else if(inputData instanceof InputStream) {
			session.update(binder, entry, relativeFilePath, 
					(InputStream) inputData);
		}
		else {
			throw new InternalException("Illegal input type [" + inputData.getClass().getName() + "]");
		}		
	}
	
	private FileAttachment createFileAttachment(DefinableEntity entry, FileUploadItem fui) {
    	// TODO Take care of file path info?
    	
        User user = RequestContextHolder.getRequestContext().getUser();

        String relativeFilePath = fui.getOriginalFilename();
	
		FileAttachment fAtt = new FileAttachment();
		fAtt.setOwner(entry);
		fAtt.setCreation(new HistoryStamp(user));
		HistoryStamp mod;
		if(fui.getModDate() != null) // mod date specified
			mod = new HistoryStamp(user, fui.getModDate());
		else // set mod date equal to creation date
			mod = fAtt.getCreation();
		fAtt.setModification(mod);
    	fAtt.setRepositoryName(fui.getRepositoryName());
    	//set attribute name - null if not not named
    	fAtt.setName(fui.getName());
    	fAtt.setUniqueName(fui.isUniqueName());
    	FileItem fItem = new FileItem();
    	fItem.setName(relativeFilePath);
    	try {
			fItem.setLength(fui.getSize());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
    	fAtt.setFileItem(fItem);
	
    	return fAtt;
	}
	
	/**
	 * This method is called ONLY WHEN a new file is being created
	 * (not to be confused with updateVersionAttachment which is called when
	 * a file is being updated). 
	 * 
	 * @param fAtt
	 * @param versionName
	 */
	private void createVersionAttachment(FileAttachment fAtt, String versionName) {
		fAtt.setLastVersion(new Integer(1));

		VersionAttachment vAtt = new VersionAttachment();
		// Since this is the only version for the file, we can safely set its
		// dates equal to those of FileAttachment.
		vAtt.setCreation(fAtt.getCreation());
		vAtt.setModification(fAtt.getModification());
		vAtt.setFileItem(fAtt.getFileItem());
		vAtt.setUniqueName(false);
		vAtt.setVersionNumber(1);
		vAtt.setVersionName(versionName);
		vAtt.setRepositoryName(fAtt.getRepositoryName());
		fAtt.addFileVersion(vAtt);
	}
	
	// TODO - This method is obsolete. Used only for temporary fixup code. To be removed.
	private String makeScaledFileNameInRepository(String primaryFileName) {
		int index = primaryFileName.lastIndexOf(".");
		String scaledFileName = null;
		if(index == -1) {
			// The file name doesn't contain extension 
			scaledFileName = primaryFileName + SCALED_FILE_SUFFIX;
		}
		else {
			scaledFileName = primaryFileName.substring(0, index) + SCALED_FILE_SUFFIX + 
				"." + primaryFileName.substring(index+1);
		}
		return scaledFileName;
	}
	
	// TODO - This method is obsolete. Used only for temporary fixup code. To be removed.
	private String makeThumbnailFileName(String primaryFileName) {
		int index = primaryFileName.lastIndexOf(".");
		String thumbnailFileName = null;
		if(index == -1) {
			// The file name doesn't contain extension 
			thumbnailFileName = primaryFileName + THUMBNAIL_FILE_SUFFIX;
		}
		else {
			thumbnailFileName = primaryFileName.substring(0, index) + THUMBNAIL_FILE_SUFFIX + 
				"." + primaryFileName.substring(index+1);
		}
		return thumbnailFileName;
	}

	private void generateAndStoreScaledFile(
			Binder binder, DefinableEntity entry, String relativeFilePath, 
			byte[] inputData, int maxWidth, int maxHeight) 
		throws ThumbnailException, UncheckedIOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		Thumbnail.createThumbnail(inputData, baos, maxWidth, maxHeight);

		String filePath = FilePathUtil.getFilePath(binder, entry, SCALED_SUBDIR, relativeFilePath);

		try {
			cacheFileStore.writeFile(filePath, baos.toByteArray());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private void generateAndStoreThumbnailFile(Binder binder, 
			DefinableEntity entry, String relativeFilePath, 
			byte[] inputData, int maxWidth, int maxHeight) 
		throws ThumbnailException, RepositoryServiceException, UncheckedIOException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// Generate thumbnail
		// maxHeight is 0 if square thumbnail
		if (maxHeight == 0) {
			Thumbnail.createThumbnail(inputData, baos, maxWidth);
		} else {
			Thumbnail.createThumbnail(inputData, baos, maxWidth, maxHeight);
		}

		String filePath = FilePathUtil.getFilePath(binder, entry, THUMB_SUBDIR, relativeFilePath);

		try {
			cacheFileStore.writeFile(filePath, baos.toByteArray());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private void generateAndStoreHtmlFile(String url, Long binderId, Long entryId, String fileId, String inFile, String outFile) 
		throws RepositoryServiceException, FileNotFoundException,
		IOException
	{
		int length = 2048;
		char[] cbuf = new char[length];
		HtmlConverter converter = null;
		IHtmlConverterManager htmlConverter = null;
		StringBuffer buffer = null,
					 bufferAlter = null;
			
			try
			{
				//Document document = null;
				//Element image = null;
				String src = "";
				buffer = new StringBuffer();
			
				int j = outFile.lastIndexOf(File.separator);
				//outFile = outFile.substring(0, j+1) + fileId + File.separator + outFile.substring(j+1);
				
				htmlConverter = (IHtmlConverterManager)SpringContextUtil.getBean("htmlConverterMgr");
				converter = htmlConverter.getConverter();
				
				converter.convert(inFile, outFile, 30000);
				// When generating the HMTL equivalent file.
				// Many HTML files can be generated. Open file(s) an make adjustments to image src attribute
				// Every HTML file in directory should be related to converter process
				File outputDir = new File(outFile.substring(0, j+1));
				if (outputDir.isDirectory())
				{
					src = url + "?binderId=" + binderId + "&entryId=" + entryId + "&fileId=" + fileId + "&viewType=XXXX&filename=";
					File[] files = outputDir.listFiles();
					for (int x=0; x < files.length; x++)
					{
						if (files[x].isFile() && files[x].getName().endsWith(".html"))
							parseHtml(files[x], files[x], src);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
	}
    
	/*
	 * private void closeLocksTransactional(Binder binder, DefinableEntity
	 * entity, boolean commit) throws RepositoryServiceException,
	 * UncheckedIOException { if(closeLocks(binder, entity, commit))
	 * triggerUpdateTransaction(); }
	 */
    
	/*
    private boolean closeLocks(Binder binder, DefinableEntity entity,
    		boolean commit) throws RepositoryServiceException,
    		UncheckedIOException {
    	if((entity instanceof Reservable) &&
    			((Reservable)entity).getLockedFileCount() <= 0) {
    		// A little optimization for reservable entity.
    		return false; 
    	}
    	else {
	    	boolean metadataDirty = false; 
	    	
			// Iterate over file attachments and close each lock.
			List fAtts = entity.getFileAttachments();
			for(int i = 0; i < fAtts.size(); i++) {
				FileAttachment fa = (FileAttachment) fAtts.get(i);
				if(closeLock(binder, entity, fa, commit))
					metadataDirty = true;
			}
	    	
	    	return metadataDirty;  	
    	}
    }*/
    
    /*
    private void closeLockTransactional(Binder binder, DefinableEntity entity,
    		FileAttachment fa, boolean commit) throws RepositoryServiceException,
    		UncheckedIOException {
    	if(closeLock(binder, entity, fa, commit))
    		this.triggerUpdateTransaction();
    }*/

    private boolean closeLock(Binder binder, DefinableEntity entity, 
    		FileAttachment fa, boolean commit) throws RepositoryServiceException,
    		UncheckedIOException {
    	boolean metadataDirty = false;
    	
    	FileAttachment.FileLock lock = fa.getFileLock();
    	
    	if (lock != null) {
			if (commit) { 
				// Commit pending changes if any. We don't care whether the 
				// lock is currently effective or expired.
				commitPendingChanges(binder, entity, fa, lock.getOwner());
			} 
			else { // Discard pending changes if any
				RepositoryUtil.uncheckout(fa.getRepositoryName(), 
						binder, entity, fa.getFileItem().getName()); 					
			}

			fa.setFileLock(null); // Clear the lock

			metadataDirty = true;
		}
    	
    	return metadataDirty;
    }
    
    private void closeExpiredLocksTransactional(Binder binder, DefinableEntity entity,
    		boolean commit) throws RepositoryServiceException,
    		UncheckedIOException {
    	if(closeExpiredLocks(binder, entity, commit))
    		triggerUpdateTransaction();   	
    }
    		
    private boolean closeExpiredLocks(Binder binder, DefinableEntity entity,
    		boolean commit) throws RepositoryServiceException,
    		UncheckedIOException {
    	boolean metadataDirty = false; 
    	
		// Iterate over file attachments and close each expired lock.
		List fAtts = entity.getFileAttachments();
		for(int i = 0; i < fAtts.size(); i++) {
			FileAttachment fa = (FileAttachment) fAtts.get(i);
			if(closeExpiredLock(binder, entity, fa, commit))
				metadataDirty = true;
		}

		return metadataDirty;
    }
    
    /*
    private void closeExpiredLockTransactional(Binder binder, DefinableEntity entity,
    		FileAttachment fa, boolean commit) throws RepositoryServiceException,
    		UncheckedIOException {
    	if(closeExpiredLock(binder, entity, fa, commit))
    		this.triggerUpdateTransaction();
    }*/
    
    private boolean closeExpiredLock(Binder binder, DefinableEntity entity, 
    		FileAttachment fa, boolean commit) throws RepositoryServiceException,
    		UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(fa.getRepositoryName());

		try {
			return closeExpiredLock(session, binder, entity, fa, commit);
		}
		finally {
			session.close();
		}	
	}
    
    private boolean closeExpiredLock(RepositorySession session,
    		Binder binder, DefinableEntity entity, FileAttachment fa, 
    		boolean commit) throws RepositoryServiceException, UncheckedIOException {
    	boolean metadataDirty = false;
    	
    	FileAttachment.FileLock lock = fa.getFileLock();
    	
    	if(lock != null) {
        	if(isLockExpired(lock)) { // Lock expired
				if(commit) { // Commit pending changes if any
					commitPendingChanges(session, binder, entity, fa, lock.getOwner()); 
				}
				else {	// Discard pending changes if any
					session.uncheckout(binder, entity, fa.getFileItem().getName());
				}

				fa.setFileLock(null); // Clear the expired lock

				metadataDirty = true;	
        	}
    	}
    	
    	return metadataDirty;
    }
    
    private boolean commitPendingChanges(Binder binder, DefinableEntity entity,
    		FileAttachment fa, Principal changeOwner)
    	throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(fa.getRepositoryName());

		try {
			return commitPendingChanges(session, binder, entity, fa,
					changeOwner);
		}
		finally {
			session.close();
		}			
    }
    
    private boolean commitPendingChanges(RepositorySession session,
    		Binder binder, DefinableEntity entity, FileAttachment fa, 
    		Principal changeOwner)
    	throws RepositoryServiceException, UncheckedIOException {
    	String relativeFilePath = fa.getFileItem().getName();
		
		boolean metadataDirty = false; 
		
		// Attempt to check in. If the file was previously checked out (and
		// only if any change has made since checkout??), this will create 
		// a new version and return the name of the new version. If not,
		// it will return the name of the latest existing version.
		String versionName = session.checkin(binder, entity, relativeFilePath);
		VersionAttachment va = fa.findFileVersion(versionName);
		if(va == null) {
			// This means that the checkin above created a new version
			// of the file. 
			long contentLength = session.getContentLength(binder, entity, 
					relativeFilePath, versionName);
			updateFileAttachment(fa, changeOwner, versionName, contentLength, null);
			metadataDirty = true;
		}   			
		
		return metadataDirty;
    }
    
    private void checkReservation(DefinableEntity entity) 
    	throws ReservedByAnotherUserException {
		User user = RequestContextHolder.getRequestContext().getUser();

    	HistoryStamp reservation = null;
    	if(entity instanceof Reservable)
    		reservation = ((Reservable) entity).getReservation();
    	
    	if(reservation != null && !reservation.getPrincipal().equals(user)) {
    		// The entry is currently under reservation by another user.
    		throw new ReservedByAnotherUserException((Reservable) entity);
    	}
    }
    
    private void checkLock(DefinableEntity entity, FileAttachment fa) 
    	throws LockedByAnotherUserException {
    	// This method assumes that the lock has been brought up-to-date
    	// prior to the execution of this method. In other words, expired
    	// lock has already been taken care of. Therefore the lock, if
    	// exists, is effective. 
    	
		User user = RequestContextHolder.getRequestContext().getUser();

		FileLock lock = fa.getFileLock();
		if(lock != null && !lock.getOwner().equals(user)) {
			// The file is locked by another user.
			throw new LockedByAnotherUserException(entity, fa, lock.getOwner());
		}
    }
    
    private boolean isLockExpired(FileLock lock) {
    	// Note that we take additional 
		// "allowance" value into consideration when computing the
		// expiration date used for the comparison. This is to 
		// account for the situation where subsequent lock renewal
		// or data update request following initial lock request is
		// delayed significantly that it actually arrives at the server
		// after the initial lock has expired. Although expected to be
		// rare, this situation can occur by environmental flunctuations
		// such as unusual network latency or extremely high system 
		// load, etc. To prevent undesired version proliferation from
		// occuring as result, we give each lock some reasonable 
		// allowance (ie, extended life).
    	
    	return (lock.getExpirationDate().getTime() + 
    			this.getLockExpirationAllowanceMilliseconds() <= System.currentTimeMillis());
    }
    
    /**
     * Alter tag data held in HTML file. We need to alter Image and Url file path information to reflect were
     * the image or files actually reside on the server. After converted a file into an HTML file the Image an
     * Url file paths are specified to be relative to HTML file just generated. We must change these entries so
     * we can recall these items and stream items into browser.
     * 
     * @param indata		Data to check for alterations
     * @param tag			What tag item are we going to look to change
     * @param attrtag		What attribute on 'tag' are we going to change
     * @param newdata		New data to insert into attribute we are changing
     * 
     * @return				Altered 'indata'
     */
    private StringBuffer alterTagData(String indata, String tag, String attrtag, String newdata)
	{
		String[] splits = null;
		StringBuffer buffer = null;
		String s = "",
			   src = "",
			   data = "",
			   altdata = "",
			   predata = "",
			   imageurl = "";
		
		buffer = new StringBuffer();
		splits = indata.split(tag);
        for (int x=0; x < splits.length; x++)
        {
        	s = splits[x];
        	int i = s.indexOf(attrtag);
        	if (i > -1)
        	{
        		predata = s.substring(0, i);
        		data = s.substring(i + attrtag.length());
        		imageurl = data.substring(0, data.indexOf("\""));
        		if (imageurl.startsWith("#")
        		|| imageurl.startsWith("http:")
        		|| imageurl.startsWith("https:"))
        			src = imageurl;
        		else
        			src = newdata + imageurl;
        		
        		altdata = tag + predata;
        		altdata += attrtag + (src + "\"" + data.substring(data.indexOf("\"")+1));
        		buffer.append(altdata);
        	}
        	else
        	// we could have a file like (ex) this is a test <a name='rsordillo' /> for testing
        	// we would not want to add 'tag' to beginning of file
        	if (x == 0)
        		buffer.append(s);
        	else
        		buffer.append(tag + s);
        }
        
        return buffer;
	}
	
    /**
     * Parse HTML file replacing URL an IMAGE paths to conform with were the actual Images or Url files exist
     * on the system.
     * 
     * @param fin			Input file to be adjusted
     * @param fout			Output file after adjustments have been made
     * @param attrdata		What attribute data to change if required
     * 
     * @throws Exception	Something goes wrong with parsing/changing input file
     * 
     */
	public void parseHtml(File fin, File fout, String attrdata)
		throws Exception
	{
		int length = 2048;
		FileReader fr = null;
		FileWriter fw = null;
		char[] cbuf = new char[length];
		StringBuffer buffer = null;
		String fileData = "";
		
		try
		{
			buffer = new StringBuffer();
			
			fr = new FileReader(fin);			
			while (fr.read(cbuf, 0, length) > -1)
			{
				buffer.append(cbuf);
				// clear buffer
				for (int x=0; x < cbuf.length; x++)
					cbuf[x] = '\0';
			}
			fileData = buffer.toString().trim();
			fr.close();
	
			buffer = alterTagData(fileData, "<img ", "src=\"", attrdata.replaceAll("XXXX", "image"));
			buffer = alterTagData(buffer.toString(), "<IMG ", "SRC=\"", attrdata.replaceAll("XXXX", "image"));
			buffer = alterTagData(buffer.toString(), "<a ", "href=\"", attrdata.replaceAll("XXXX", "url"));
			buffer = alterTagData(buffer.toString(), "<A ", "HREF=\"", attrdata.replaceAll("XXXX", "url"));
			
			fw = new FileWriter(fout);
	        fw.write(buffer.toString());
		}
		finally
		{
			try
			{
				if (fr != null)
					fr.close();
			} catch (Exception e) {}
			
			try
			{
				if (fw != null)
				{
					fw.flush();
					fw.close();
				}
			}
			catch (Exception e) {}
		}
        
        return;
	}
}
