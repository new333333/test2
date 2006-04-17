package com.sitescape.ef.module.file.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.ef.InternalException;
import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FileItem;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.VersionAttachment;
import com.sitescape.ef.module.file.CheckedOutByOtherException;
import com.sitescape.ef.module.file.ContentFilter;
import com.sitescape.ef.module.file.FilesErrors;
import com.sitescape.ef.module.file.FileException;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.file.FilterException;
import com.sitescape.ef.module.file.NoSuchFileException;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.repository.RepositoryService;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.repository.RepositoryServiceUtil;
import com.sitescape.ef.util.DirPath;
import com.sitescape.ef.util.FileHelper;
import com.sitescape.ef.util.FileUploadItem;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.util.Thumbnail;
import com.sitescape.ef.util.ThumbnailException;

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
public class FileModuleImpl extends CommonDependencyInjection implements FileModule {

	private static final String FAILED_FILTER_FILE_DELETE 			= "DELETE";
	private static final String FAILED_FILTER_FILE_MOVE 			= "MOVE";
	private static final String FAILED_FILTER_FILE_DEFAULT			= FAILED_FILTER_FILE_DELETE;
	private static final String FAILED_FILTER_TRANSACTION_CONTINUE 	= "CONTINUE";
	private static final String FAILED_FILTER_TRANSACTION_ABORT 	= "ABORT";
	private static final String FAILED_FILTER_TRANSACTION_DEFAULT	= FAILED_FILTER_TRANSACTION_ABORT;

	private static final String SCALED_FILE_SUFFIX = "__ssfscaled_";
	private static final String THUMBNAIL_FILE_SUFFIX = "__ssfthumbnail_";
	
	protected Log logger = LogFactory.getLog(getClass());

	private CoreDao coreDao;
	private TransactionTemplate transactionTemplate;
	private ContentFilter contentFilter;
	private String failedFilterFile;
	private String failedFilterTransaction;
	
	protected CoreDao getCoreDao() {
		return coreDao;
	}

	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
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

	public void setFailedFilterFile(String failedFilterFile) {
		if(FAILED_FILTER_FILE_DELETE.equals(failedFilterFile)) {
			this.failedFilterFile = FAILED_FILTER_FILE_DELETE;
		}
		else if(FAILED_FILTER_FILE_MOVE.equals(failedFilterFile)) {
			this.failedFilterFile = FAILED_FILTER_FILE_MOVE;
		}
		else {
			logger.info("Unknown failedFilterFile " + failedFilterFile + " defaults to " + FAILED_FILTER_FILE_DEFAULT);
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
			logger.info("Unknown failedFilterTransaction " + failedFilterTransaction + " defaults to " + FAILED_FILTER_TRANSACTION_DEFAULT);
			this.failedFilterTransaction = FAILED_FILTER_TRANSACTION_DEFAULT;
		}
	}

	public void deleteFiles(Binder binder, DefinableEntity entry) {
		try {
			List fAtts = entry.getFileAttachments();
			for(int i = 0; i < fAtts.size(); i++) {
				FileAttachment fAtt = (FileAttachment) fAtts.get(i);
	
				deleteFile(binder, entry, fAtt);
			}
		}
		catch(RepositoryServiceException e) {
			throw new FileException(e);
		}
	}
	
	public void deleteFile(Binder binder,
			DefinableEntity entry, String repositoryServiceName, String fileName) {
		try {
			FileAttachment fAtt = entry.getFileAttachment(
					repositoryServiceName, fileName);

			if (fAtt == null)
				throw new NoSuchFileException(entry, fileName);

			deleteFile(binder, entry, fAtt);
		} catch (RepositoryServiceException e) {
			throw new FileException(e);
		}
	}
	
	public void readFile(Binder binder,
			DefinableEntity entry, String repositoryServiceName, String fileName, OutputStream out) {
		try {
			FileAttachment fAtt = entry.getFileAttachment(
					repositoryServiceName, fileName);

			if (fAtt == null)
				throw new NoSuchFileException(entry, fileName);

			RepositoryServiceUtil.read(repositoryServiceName, binder, entry,
					fileName, out);
		} catch (RepositoryServiceException e) {
			throw new FileException(e);
		}
	}

	public InputStream readFile(Binder binder,
			DefinableEntity entry, String repositoryServiceName, String fileName) {
		try {
			FileAttachment fAtt = entry.getFileAttachment(
					repositoryServiceName, fileName);

			if (fAtt == null)
				throw new NoSuchFileException(entry, fileName);

			return RepositoryServiceUtil.read(repositoryServiceName, binder, entry,
					fileName);
		} catch (RepositoryServiceException e) {
			throw new FileException(e);
		}
	}

	public void readFile(Binder binder, DefinableEntity entry, FileAttachment fa, 
			OutputStream out) {
		try {
			String repositoryServiceName = fa.getRepositoryServiceName();
			if(repositoryServiceName == null)
				repositoryServiceName = RepositoryServiceUtil.getDefaultRepositoryServiceName();
			
	    	RepositoryServiceUtil.read(repositoryServiceName, binder, entry, 
					fa.getFileItem().getName(), out);
		}
		catch(RepositoryServiceException e) {
			throw new FileException(e);
		}
	}
	
	public InputStream readFile(Binder binder, DefinableEntity entry, FileAttachment fa) { 
		try {
			String repositoryServiceName = fa.getRepositoryServiceName();
			if(repositoryServiceName == null)
				repositoryServiceName = RepositoryServiceUtil.getDefaultRepositoryServiceName();
			
	    	return RepositoryServiceUtil.read(repositoryServiceName, binder, entry, 
					fa.getFileItem().getName());
		}
		catch(RepositoryServiceException e) {
			throw new FileException(e);
		}
	}
	
	public boolean scaledFileExists(Binder binder, 
			DefinableEntity entry, String repositoryServiceName, String primaryFileName) throws NoSuchFileException, FileException {
		FileAttachment fAtt = null;
		try {
			fAtt = entry.getFileAttachment(
					repositoryServiceName, primaryFileName);

		}
		catch(RepositoryServiceException e) {
			throw new FileException(e);
		}
		if (fAtt == null)
			throw new NoSuchFileException(entry, primaryFileName);
		
		return scaledFileExists(binder, entry, fAtt);
	}
	
	public boolean scaledFileExists(Binder binder, 
			DefinableEntity entry, FileAttachment fAtt) throws NoSuchFileException, FileException {
		try {
			if (fAtt == null)
				throw new IllegalArgumentException("FileAttachment must be passed in");

			int fileInfo = RepositoryServiceUtil.fileInfo(fAtt.getRepositoryServiceName(), 
					binder, entry, makeScaledFileName(fAtt.getFileItem().getName()));
			
			if(fileInfo == RepositoryService.UNVERSIONED_FILE)
				return true;
			else if(fileInfo == RepositoryService.NON_EXISTING_FILE)
				return false;
			else
				throw new InternalException();
		}
		catch(RepositoryServiceException e) {
			throw new FileException(e);
		}
	}
	
	public boolean thumbnailFileExists(Binder binder, 
			DefinableEntity entry, String repositoryServiceName, String primaryFileName) throws NoSuchFileException, FileException {
		try {
			FileAttachment fAtt = entry.getFileAttachment(
					repositoryServiceName, primaryFileName);

			if (fAtt == null)
				throw new NoSuchFileException(entry, primaryFileName);

			int fileInfo = RepositoryServiceUtil.fileInfo(repositoryServiceName, 
					binder, entry, makeThumbnailFileName(primaryFileName));
			
			if(fileInfo == RepositoryService.UNVERSIONED_FILE)
				return true;
			else if(fileInfo == RepositoryService.NON_EXISTING_FILE)
				return false;
			else
				throw new InternalException();
		}
		catch(RepositoryServiceException e) {
			throw new FileException(e);
		}
	}
	
	public HistoryStamp getCheckoutInfo(
			Binder binder, DefinableEntity entry, String repositoryServiceName, String fileName) {
		FileAttachment fAtt = entry.getFileAttachment(repositoryServiceName, fileName);
		return fAtt.getCheckout();
	}

	public void checkout(Binder binder, 
			DefinableEntity entry, String repositoryServiceName, String fileName) {
		try {
			checkoutInternal(repositoryServiceName, binder, entry, fileName);
		}
		catch(RepositoryServiceException e) {
			throw new FileException(e);
		}
	}
	
	public void uncheckout(Binder binder, 
			DefinableEntity entry, String repositoryServiceName, String fileName) {
		try {
			uncheckoutInternal(repositoryServiceName, binder, entry, fileName);
		}
		catch(RepositoryServiceException e) {
			throw new FileException(e);
		}
	}

	public void checkin(Binder binder, 
			DefinableEntity entry, String repositoryServiceName, String fileName) {
		try {
			checkinInternal(repositoryServiceName, binder, entry, fileName);
		}
		catch(RepositoryServiceException e) {
			throw new FileException(e);
		}
	}
	
	public void readScaledFile(Binder binder, 
			DefinableEntity entry, String repositoryServiceName, String fileName, OutputStream out) {
		RepositoryServiceUtil.read(repositoryServiceName, binder, entry, makeScaledFileName(fileName), out);
	}
	
	public void readScaledFile(Binder binder, DefinableEntity entry, FileAttachment fa, OutputStream out) {
		readScaledFile(binder, entry, fa.getRepositoryServiceName(), 
				fa.getFileItem().getName(), out);
	}
	
	public void readIndirectlyAccessibleThumbnailFile(
			Binder binder, DefinableEntity entry, String repositoryServiceName, String fileName, OutputStream out) {
		RepositoryServiceUtil.read(repositoryServiceName, binder, entry, makeThumbnailFileName(fileName), out);	
	}
	
	public void readIndirectlyAccessibleThumbnailFile(
			Binder binder, DefinableEntity entry, FileAttachment fa, OutputStream out) {
		readIndirectlyAccessibleThumbnailFile(
				binder, entry, fa.getRepositoryServiceName(), fa.getFileItem().getName(), out);
	}
	
	public void generateScaledFile(Binder binder, 
			DefinableEntity entry, String repositoryServiceName, String fileName, int maxWidth, int maxHeight) {
		try {
			RepositoryService service = RepositoryServiceUtil.lookupRepositoryService(repositoryServiceName);
			
			Object session = service.openRepositorySession();
			
			try {
				// Read the input file from the repository into a byte array. 
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
				service.read(session, binder, entry, fileName, baos);
		
				generateAndStoreScaledFile(service, session, binder, entry, 
						fileName, baos.toByteArray(), maxWidth, maxHeight);
			}
			finally {
				service.closeRepositorySession(session);
			}
		}
		catch(RepositoryServiceException e) {
			throw new FileException(e);
		}		
	}
	
	public void generateThumbnailFile(Binder binder, 
			DefinableEntity entry, String repositoryServiceName, String fileName, int maxWidth, int maxHeight, 
			boolean thumbnailDirectlyAccessible) {
		try {
			RepositoryService service = RepositoryServiceUtil.lookupRepositoryService(repositoryServiceName);
			
			Object session = service.openRepositorySession();
			
			try {
				// Read the input file from the repository into a byte array. 
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
				service.read(session, binder, entry, fileName, baos);
		
				generateAndStoreThumbnailFile(service, session, binder, entry, 
						fileName, baos.toByteArray(), maxWidth, maxHeight, thumbnailDirectlyAccessible);
			}
			finally {
				service.closeRepositorySession(session);
			}
		}
		catch(FileNotFoundException e) {
			throw new FileException(e);
		}
		catch(IOException e) {
			throw new FileException(e);
		}
		catch(RepositoryServiceException e) {
			throw new FileException(e);
		}		
	}
	
	/**
	 * This method is functionally equivalent to <code>generateScaledFile</code>
	 * and <code>generateThumbnailFile</code> combined. But this is potentially
	 * more efficient than calling them separately because it reads in the
	 * primary file only once. 
	 */
	public void generateFiles(Binder binder, 
			DefinableEntity entry, String repositoryServiceName, String fileName, int maxWidth, int maxHeight, 
			int thumbnailMaxWidth, int thumbnailMaxHeight, 
			boolean thumbnailDirectlyAccessible) {
		try {
			RepositoryService service = RepositoryServiceUtil.lookupRepositoryService(repositoryServiceName);
			
			Object session = service.openRepositorySession();
			
			try {
				// Read the input file from the repository into a byte array. 
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
				service.read(session, binder, entry, fileName, baos);
		
				// Generate and store scaled file.
				generateAndStoreScaledFile(service, session, binder, entry, 
						fileName, baos.toByteArray(), maxWidth, maxHeight);
				
				// Generate and store thumbnail file.
				generateAndStoreThumbnailFile(service, session, binder, entry, 
						fileName, baos.toByteArray(), maxWidth, maxHeight, thumbnailDirectlyAccessible);
			}
			finally {
				service.closeRepositorySession(session);
			}
		}
		catch(FileNotFoundException e) {
			throw new FileException(e);
		}
		catch(IOException e) {
			throw new FileException(e);
		}
		catch(RepositoryServiceException e) {
			throw new FileException(e);
		}
	}
	
    public FilesErrors writeFiles(Binder binder, DefinableEntity entry, List fileUploadItems,
    		FilesErrors errors) {
    	if(errors == null)
    		errors = new FilesErrors();
    	
    	for(int i = 0; i < fileUploadItems.size(); i++) {
    		FileUploadItem fui = (FileUploadItem) fileUploadItems.get(i);
    		try {
    			this.writeFileInternal(binder, entry, fui, errors);
    		}
    		catch(Exception e) {
    			logger.error("Error processing file " + fui.getOriginalFilename(), e);
    			errors.addProblem(new FilesErrors.Problem
    					(fui.getRepositoryServiceName(),  fui.getOriginalFilename(), 
    							FilesErrors.Problem.OTHER_PROBLEM, e));
    		}
    	}
	
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
		
    	for(int i = 0; i < fileUploadItems.size(); i++) {
    		FileUploadItem fui = (FileUploadItem) fileUploadItems.get(i);
    		try {
    			getContentFilter().filter(fui);
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
    				errors.addProblem(new FilesErrors.Problem
    					(fui.getRepositoryServiceName(),  fui.getOriginalFilename(), 
    							FilesErrors.Problem.PROBLEM_FILTERING, e));
    			}
    			else {
    				throw e;
    			}
    		}
    	}
	
    	return errors;
	}

	private void move(Binder binder, FileUploadItem fui) throws IOException {
		File filteringFailedDir = SPropsUtil.getFile("filtering.failed.dir");
		if(!filteringFailedDir.exists())
			FileHelper.mkdirs(filteringFailedDir);
		FileHelper.move(fui.getFile(), new File(filteringFailedDir, makeFileName(binder, fui)));
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
	
	protected void writeFileMetadata(final Binder binder, final DefinableEntity entry, 
    		final FileUploadItem fui, final FileAttachment fAtt, final boolean isNew) {	
    	
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		if(isNew) {
            		// Since file attachment is stored into custom attribute using
            		// its id value rather than association, this new object must
            		// be persisted here just in case it is to be put into custom
            		// attribute down below. 
            		getCoreDao().save(fAtt);    		
            	}

            	if (fui.getType() == FileUploadItem.TYPE_FILE) {
        			// Find custom attribute by the attribute name. 
        			Set fAtts = null;
        			CustomAttribute ca = entry.getCustomAttribute(fui.getName());
        			if(ca != null)
        				fAtts = (Set) ca.getValue();
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
        		}     	
                return null;
        	}
        });
    }

	protected void setCheckoutMetadata(final FileAttachment fAtt, final HistoryStamp co) {
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		fAtt.setCheckout(co);      
                
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
	}
	
	private void checkinInternal(String repositoryServiceName, Binder binder, 
			DefinableEntity entry, String fileName) {
    	FileAttachment fAtt = entry.getFileAttachment(repositoryServiceName, fileName);
    	
    	if(fAtt == null)
    		throw new NoSuchFileException(entry, fileName);
    	
        User user = RequestContextHolder.getRequestContext().getUser();
        
        HistoryStamp co = fAtt.getCheckout();
    	
    	if(co == null) { // The file is not checked out.
    		// Nothing to do
    	}
    	else { // The file is checked out by someone. 
    		if(user.equals(co.getPrincipal())) {
    			// The file is checked out by the same person calling this. 
    			
    			RepositoryService service = RepositoryServiceUtil.lookupRepositoryService
    				(fAtt.getRepositoryServiceName());
    			Object session = service.openRepositorySession();
    			String versionName = null;
    			long contentLength = 0;
    			try {
    				versionName = service.checkin(session, binder, entry, fileName);
    				if(versionName == null)
    					throw new InternalException();
    				if(versionName != null)
    					contentLength = service.getContentLength(session, binder, entry, fileName, versionName);
    				else
    					contentLength = service.getContentLength(session, binder, entry, fileName);
    			} finally {
    				service.closeRepositorySession(session);
    			}

    			updateFileAttachment(fAtt, user, versionName, contentLength);		
    			
        		// Mark our metadata that the file is not checked out.
    			setCheckoutMetadata(fAtt, null);
    		}
    		else {
    			// The file is checked out by some other person. 
    			throw new CheckedOutByOtherException(entry, fileName, user);
    		}
    	}
		
	}
	
	private void uncheckoutInternal(String repositoryServiceName, Binder binder, 
			DefinableEntity entry, String fileName) {
    	FileAttachment fAtt = entry.getFileAttachment(repositoryServiceName, fileName);
    	
    	if(fAtt == null)
    		throw new NoSuchFileException(entry, fileName);
    	
        User user = RequestContextHolder.getRequestContext().getUser();
        
        HistoryStamp co = fAtt.getCheckout();
    	
    	if(co == null) { // The file is not checked out.
    		// Nothing to do
    	}
    	else { // The file is checked out by someone. 
    		if(user.equals(co.getPrincipal())) {
    			// The file is checked out by the same person calling this. 
        		RepositoryServiceUtil.uncheckout(fAtt.getRepositoryServiceName(),
        				binder, entry, fileName);
        		// Mark our metadata that the file is not checked out.
        		setCheckoutMetadata(fAtt, null);
    		}
    		else {
    			// The file is checked out by some other person. 
    			throw new CheckedOutByOtherException(entry, fileName, user);
    		}
    	}
	}

	private void checkoutInternal(String repositoryServiceName, Binder binder, 
			DefinableEntity entry, String fileName) {
    	FileAttachment fAtt = entry.getFileAttachment(repositoryServiceName, fileName);
    	
    	if(fAtt == null)
    		throw new NoSuchFileException(entry, fileName);
    	
        User user = RequestContextHolder.getRequestContext().getUser();
        
        HistoryStamp co = fAtt.getCheckout();
    	
    	if(co == null) { // The file is not checked out.
    		// Instruct the underlying repository system to actually check out
    		// the file. If the repository system does not support versioning,
    		// this call is noop, which means that the checkout/checkin is 
    		// implemented soley on our side using metadata only. In that case,
    		// checkout facility is only used to enforce exclusive locking on
    		// the resource, but actual versioning of the content does not
    		// take place. 
    		RepositoryServiceUtil.checkout(fAtt.getRepositoryServiceName(),
    				binder, entry, fileName);
    		// Mark our metadata that the file is checked out by the user.
    		setCheckoutMetadata(fAtt, new HistoryStamp(user));
    	}
    	else { // The file is checked out by someone. 
    		if(user.equals(co.getPrincipal())) {
    			// The file is checked out by the same person calling this. 
    			// Nothing to do. 
    		}
    		else {
    			// The file is checked out by some other person. 
    			throw new CheckedOutByOtherException(entry, fileName, user);
    		}
    	}
	}

    private void writeFileInternal(Binder binder, DefinableEntity entry, 
    		FileUploadItem fui, FilesErrors errors) {
    	
    	/// Work Flow:
    	/// step1: write primary file
    	/// step2: generate and write scaled file (if necessary)
    	/// step3: generate and write thumbnail file (if necessary)
    	/// step4: update metadata in database
    	
		int type = fui.getType();
		if(type != FileUploadItem.TYPE_FILE && type != FileUploadItem.TYPE_ATTACHMENT) {
			logger.error("Unrecognized file processing type " + type + " for ["
					+ fui.getName() + ","
					+ fui.getOriginalFilename() + "]");
			throw new InternalException();
		}
		
		String fileName = fui.getOriginalFilename();
		String repositoryServiceName = fui.getRepositoryServiceName();

		// First, find out whether or not this is a new file for the entry.
		// It is important to note that, as far as identity/existence test
		// goes, the namespace is flat for a single instance of Entry.
		// In other words, regardless of the data elements used for accessing
		// the file, the files are treated identical globally within a single
		// Entry instance as long as their file names are identical. 
    	FileAttachment fAtt = entry.getFileAttachment(repositoryServiceName, fileName);

    	boolean isNew = false;
    	
		RepositoryService service = 
			RepositoryServiceUtil.lookupRepositoryService(repositoryServiceName);
		
		Object session = service.openRepositorySession();

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
		    			fAtt = createFile(service, session, binder, entry, fui, primaryContent);
		    		}
		    		else { // Existing file for the entry
		    			writeExistingFile(service, session, binder, entry, fui, primaryContent);
		    		}
	    		}
	    		catch(Exception e) {
	    			logger.error("Error storing file " + fileName, e);
	    			// We failed to write the primary file. In this case, we 
	    			// discard the rest of the operation (i.e., step2 thru 4).
	    			errors.addProblem(new FilesErrors.Problem
	    					(repositoryServiceName, fileName, 
	    							FilesErrors.Problem.PROBLEM_STORING_PRIMARY_FILE, e));
	    			return;
	    		}
	    		

	    		// Scaled file
	        	if(fui.getMaxWidth() > 0 && fui.getMaxHeight() > 0) {
	            	// Generate scaled file which goes into the same repository as
	        		// the primary file except that the generated file is not versioned.
	        		try {
	        			//for testing only
	        			//if(1==1)
	        			//	throw new ThumbnailException("Fake error");
	        			
	        			generateAndStoreScaledFile(service, session, binder, entry, fileName,
	        				primaryContent, fui.getMaxWidth(),fui.getMaxWidth());
	        		}
	        		catch(ThumbnailException e) {
	        			// Scaling operation can fail for a variety of reasons, primarily
	        			// when the file format is not supported. Do not cause this to
	        			// fail the entire operation. Simply log it and proceed.  
	        			logger.warn("Error generating scaled version of " + fileName, e);
		    			errors.addProblem(new FilesErrors.Problem
		    					(repositoryServiceName, fileName, 
		    							FilesErrors.Problem.PROBLEM_GENERATING_SCALED_FILE, e));
	        		}
	        		catch(Exception e) {
		    			// Failed to store scaled file. In this case, we report the 
	        			// problem to the client but still proceed here.
	        			logger.warn("Error storing scaled version of " + fileName, e);
		    			errors.addProblem(new FilesErrors.Problem
		    					(repositoryServiceName, fileName, 
		    							FilesErrors.Problem.PROBLEM_STORING_SCALED_FILE, e));	        			
	        		}
	        	}    

	        	// Thumbnail file
	        	if(fui.getGenerateThumbnail()) {
	        		try {
	        			generateAndStoreThumbnailFile(service, session, binder, entry,
	        				fileName, primaryContent, fui.getThumbnailMaxWidth(), 
	        				fui.getThumbnailMaxHeight(), fui.isThumbnailDirectlyAccessible());
	        		}
	        		catch(ThumbnailException e) {
	        			logger.warn("Error generating thumbnail version of " + fileName, e);
		    			errors.addProblem(new FilesErrors.Problem
		    					(repositoryServiceName, fileName, 
		    							FilesErrors.Problem.PROBLEM_GENERATING_THUMBNAIL_FILE, e));
	        		}
	        		catch(Exception e) {
	        			logger.warn("Error storing thumbnail version of " + fileName, e);
		    			errors.addProblem(new FilesErrors.Problem
		    					(repositoryServiceName, fileName, 
		    							FilesErrors.Problem.PROBLEM_STORING_THUMBNAIL_FILE, e));	        			
	        		}
	        	}
	    	}
	    	else { // We do not need to generate secondary files. 	  
	    		try {
	    			//for testing only
	    			//if(fileName.equals("junk.txt"))
	    			//	throw new IllegalArgumentException("Something bad happend");
	    			
		    		InputStream is = fui.getInputStream();
		    		
		    		try {
			    		if(fAtt == null) { // New file for the entry
			    			isNew = true;
			    			fAtt = createFile(service, session, binder, entry, fui, is);
			    		}
			    		else { // Existing file for the entry
			    			writeExistingFile(service, session, binder, entry, fui, is);
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
	    			logger.error("Error storing file " + fileName, e);
	    			errors.addProblem(new FilesErrors.Problem
	    					(repositoryServiceName, fileName, 
	    							FilesErrors.Problem.PROBLEM_STORING_PRIMARY_FILE, e));
	    			return;
	    		}
	    	}
    	}
    	finally {
    		service.closeRepositorySession(session);
    	}
    	
    	// Finally update metadata - We do this only after successfully writing
    	// the file to the repository to ensure that our metadata describes
    	// what actually exists (Of course, there could be a failure scenario
    	// where this metadata update fails leaving the file dangling in the
    	// repository. But that is expected to be a lot more rare, and not
    	// quite as destructive as the other case. But the bottom line is, 
    	// unless we have a single transaction that spans both repository
    	// update and database update all within a single unit, there will
    	// always be error cases that can leave the data inconsistent. 
    	// When a repository supports JCA, this should be possible to do
    	// using JTA. But that's not always available, and this version of
    	// the system does not try to address that). 
    	writeFileMetadata(binder, entry, fui, fAtt, isNew);
    }
    
    private File getDirectlyAccessibleThumbnailFile(DefinableEntity entry, String primaryFileName) {
    	return new File(directlyAccessibleThumbnailFilePath(entry, primaryFileName));
    }
    
    private String directlyAccessibleThumbnailFilePath(DefinableEntity entry, String primaryFileName) {
    	return DirPath.getThumbnailDirPath() + File.separator + entry.getId() + "_" + primaryFileName;
    }
    
	private void forceUncheckoutIfNecessary(Binder binder, DefinableEntity entry, 
			FileAttachment fAtt) 
		throws RepositoryServiceException {
		HistoryStamp co = fAtt.getCheckout();
		
		if(co != null) { // The file is checked out (by someone).
			// Uncheck it out from the underlying repository.
    		RepositoryServiceUtil.uncheckout(fAtt.getRepositoryServiceName(),
    				binder, entry, fAtt.getFileItem().getName());
    		// Mark our metadata that the file is not checked out.
    		setCheckoutMetadata(fAtt, null);	
		}
	}

    private void writeExistingFile(RepositoryService service, Object session,
    		Binder binder, DefinableEntity entry, FileUploadItem fui, Object inputData)
		throws CheckedOutByOtherException, RepositoryServiceException {
    	String fileName = fui.getOriginalFilename();
    	FileAttachment fAtt = entry.getFileAttachment(fui.getRepositoryServiceName(), fileName);
    	User user = RequestContextHolder.getRequestContext().getUser();

    	String versionName = null;
    	HistoryStamp co = fAtt.getCheckout();
		if(co == null) { // This file is not checked out by anyone.
			// In this case we create a new version silently.
			int fileInfo = service.fileInfo(session, binder, entry, fileName);
			if(fileInfo == RepositoryService.VERSIONED_FILE) { // Normal condition
				service.checkout(session, binder, entry, fileName);
				updateWithInputData(service, session, binder, entry, fileName, inputData);
				versionName = service.checkin(session, binder, entry, fileName);	    					
			}
			else if(fileInfo == RepositoryService.NON_EXISTING_FILE) {
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
				versionName = createVersionedWithInputData(service, session, binder,
						entry, fileName, inputData);
			}
			else {
				throw new InternalException();
			}
			try {
				updateFileAttachment(fAtt, user, versionName, fui.getSize());
			} catch (IOException e) {
				throw new FileException(e);
			}
		}
		else {
	   		if(user.equals(co.getPrincipal())) {
				// The file is checked out by the same person calling this.
	   			// Update the file to the repository.
	   			updateWithInputData(service, session, binder, entry, fileName, inputData);
			}
			else {
				// The file is checked out by some other person. 
				throw new CheckedOutByOtherException(entry, fileName, user);
			}				    				
		}
    }

    private void updateFileAttachment(FileAttachment fAtt, 
			User user, String versionName, long contentLength) {
		fAtt.setModification(new HistoryStamp(user));
		
		FileItem fItem = fAtt.getFileItem();
		fItem.setLength(contentLength);
		
		if(versionName != null) {
			// The repository system supports versioning.        			
			int versionNumber = fAtt.getLastVersion().intValue() + 1;
			fAtt.setLastVersion(new Integer(versionNumber));
			
			VersionAttachment vAtt = new VersionAttachment();
			vAtt.setCreation(fAtt.getModification());
			vAtt.setModification(vAtt.getCreation());
			vAtt.setFileItem(fItem);
			vAtt.setVersionNumber(versionNumber);
			vAtt.setVersionName(versionName);
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
	private FileAttachment createFile(RepositoryService service, Object session, 
			Binder binder, DefinableEntity entry, FileUploadItem fui, Object inputData) 
		throws RepositoryServiceException {
		String fileName = fui.getOriginalFilename();
		
		FileAttachment fAtt = createFileAttachment(entry, fui);
		
		String versionName = createVersionedWithInputData(service, session, binder, entry,
				fileName, inputData);

		createVersionAttachment(fAtt, versionName);

		return fAtt;
	}
	
	private String createVersionedWithInputData(RepositoryService service, Object session,
			Binder binder, DefinableEntity entry, String fileName, Object inputData)
		throws RepositoryServiceException {
		String versionName = null;
		/*if(inputData instanceof MultipartFile) {
			versionName = service.createVersioned(session, binder, entry, 
					fileName, (MultipartFile) inputData);
		}
		else*/ if(inputData instanceof byte[]) {
			versionName = service.createVersioned(session, binder, entry, fileName,
					new ByteArrayInputStream((byte[]) inputData));
		}
		else if(inputData instanceof InputStream) {
			versionName = service.createVersioned(session, binder, entry, fileName, 
					(InputStream) inputData);
		}
		else {
			throw new InternalException("Illegal input type [" + inputData.getClass().getName() + "]");
		}		
		
		return versionName;
	}
	
	private void updateWithInputData(RepositoryService service, Object session,
			Binder binder, DefinableEntity entry, String fileName, Object inputData)
		throws RepositoryServiceException {
		/*if(inputData instanceof MultipartFile) {
			service.update(session, binder, entry, 
					fileName, (MultipartFile) inputData);
		}
		else*/ if(inputData instanceof byte[]) {
			service.update(session, binder, entry, fileName,
					new ByteArrayInputStream((byte[]) inputData));
		}
		else if(inputData instanceof InputStream) {
			service.update(session, binder, entry, fileName, 
					(InputStream) inputData);
		}
		else {
			throw new InternalException("Illegal input type [" + inputData.getClass().getName() + "]");
		}		
	}
	
	private void deleteFile(Binder binder, DefinableEntity entry, FileAttachment fAtt) 
		throws RepositoryServiceException {
		forceUncheckoutIfNecessary(binder, entry, fAtt);
		
		String fileName = fAtt.getFileItem().getName();
		String repositoryServiceName = fAtt.getRepositoryServiceName();
		
		RepositoryService service = RepositoryServiceUtil.lookupRepositoryService(repositoryServiceName);
			
		Object session = service.openRepositorySession();
		
		try {
			// Delete primary file
			service.delete(session, binder, entry, fileName);
	
			// Currently we do not store metadata about "generated" files (should
			// we?). Unfortunately this results in clumsy and less efficient
			// attempt at deleting those files. 
			
			// Try deleting scaled file if exists
			String scaledFileName = makeScaledFileName(fileName);
			if(service.fileInfo(session, binder, entry, scaledFileName) != RepositoryService.NON_EXISTING_FILE) {
				try {
					service.delete(session, binder, entry, scaledFileName);
				}
				catch(RepositoryServiceException e) {
					// Don't let the failure to delete generated file abort the
					// entire operation. Log and proceed.
					logger.warn(e.getMessage(), e);
				}
			}
			
			// Try deleting thumbnail file if exists
			
			// Directly-accessible thumbnail file?
			File directlyAccessibleThumbnailFile = getDirectlyAccessibleThumbnailFile(entry, fileName);
			try {
				FileHelper.delete(directlyAccessibleThumbnailFile);
			}
			catch(IOException e) {
				logger.warn(e.getMessage(), e);
			}
			
			// thumbnail file stored in repository?
			String thumbnailFileName = makeThumbnailFileName(fileName);
			if(service.fileInfo(session, binder, entry, thumbnailFileName) != RepositoryService.NON_EXISTING_FILE) {
				try {
					service.delete(session, binder, entry, thumbnailFileName);
				}
				catch(RepositoryServiceException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}
		finally {
			service.closeRepositorySession(session);
		}
		
		// Remove metadata
		removeAttachmentMetadata(entry, fAtt);
	}
	
	private FileAttachment createFileAttachment(DefinableEntity entry, FileUploadItem fui) {
    	// TODO Take care of file path info?
    	
        User user = RequestContextHolder.getRequestContext().getUser();

        String fileName = fui.getOriginalFilename();
	
		FileAttachment fAtt = new FileAttachment();
		fAtt.setOwner(entry);
		fAtt.setCreation(new HistoryStamp(user));
		fAtt.setModification(fAtt.getCreation());
    	fAtt.setRepositoryServiceName(fui.getRepositoryServiceName());
    	//set attribute name - null if not not named
    	fAtt.setName(fui.getName());
    	FileItem fItem = new FileItem();
    	fItem.setName(fileName);
    	try {
			fItem.setLength(fui.getSize());
		} catch (IOException e) {
			throw new FileException(e);
		}
    	fAtt.setFileItem(fItem);
	
    	return fAtt;
	}
	
	private void createVersionAttachment(FileAttachment fAtt, String versionName) {
		fAtt.setLastVersion(new Integer(1));

		VersionAttachment vAtt = new VersionAttachment();
		vAtt.setCreation(fAtt.getCreation());
		vAtt.setModification(vAtt.getCreation());
		vAtt.setFileItem(fAtt.getFileItem());

		vAtt.setVersionNumber(1);
		vAtt.setVersionName(versionName);
		fAtt.addFileVersion(vAtt);
	}
	
	private String makeScaledFileName(String primaryFileName) {
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
	
	private void generateAndStoreScaledFile(RepositoryService service, 
			Object session, Binder binder, DefinableEntity entry, String fileName, 
			byte[] inputData, int maxWidth, int maxHeight) 
		throws ThumbnailException, RepositoryServiceException {
		String scaledFileName = makeScaledFileName(fileName);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		Thumbnail.createThumbnail(inputData, baos, maxWidth, maxHeight);

		int fileInfo = service.fileInfo(session, binder, entry, scaledFileName);
		
		if(fileInfo == RepositoryService.UNVERSIONED_FILE) {
			service.update(session, binder, entry, scaledFileName,
					new ByteArrayInputStream(baos.toByteArray()));									
		}
		else if(fileInfo == RepositoryService.NON_EXISTING_FILE) {
			service.createUnversioned(session, binder, entry, scaledFileName,
					new ByteArrayInputStream(baos.toByteArray()));						
		}
		else {
			throw new InternalException();
		}
	}
	
	/**
	 * 
	 * @param service
	 * @param session
	 * @param binder
	 * @param entry
	 * @param fileName
	 * @param inputData
	 * @param maxWidth
	 * @param maxHeight
	 * @param directlyAccessible
	 * @throws ThumbnailException if thumbnail generation procedure itself fails; 
	 * typically indicates that the data format is not supported.
	 * @throws RepositoryServiceException error in repository operation; can occur
	 * when thumbnail is stored by repository service
	 * @throws FileNotFoundException error in file operation; can occur when thumbnail
	 * is stored in a client-visible directory on file system
	 * @throws IOException error in file operation; can occur when thumbnail
	 * is stored in a client-visible directory on file system
	 */
	private void generateAndStoreThumbnailFile(RepositoryService service, 
			Object session, Binder binder, DefinableEntity entry, String fileName, 
			byte[] inputData, int maxWidth, int maxHeight, boolean directlyAccessible) 
		throws ThumbnailException, RepositoryServiceException, FileNotFoundException,
		IOException {

		if(directlyAccessible) {
			// The thumbnail is to be stored in a directory where 
			// the client can access it directly without going
			// through the repository service layer.
			File directlyAccessibleThumbnailFile = getDirectlyAccessibleThumbnailFile(entry, fileName);
			
			// Make sure that the directory exists before opening a file in it.
			FileHelper.mkdirsIfNecessary(directlyAccessibleThumbnailFile.getParentFile());

			FileOutputStream fos = new FileOutputStream(directlyAccessibleThumbnailFile);
			
			// Generate thumbnail.
			ThumbnailException te = null;
			try {
				Thumbnail.createThumbnail(inputData, fos, maxWidth, maxHeight);
			}
			catch(ThumbnailException e) {
				te = e;
			}
			finally {
				try {
					fos.close();
				} catch (IOException e) {
					// Ignore this.
				}
			}
			
			if(te != null) {
				// An error occured during thumbnail generation, which may have
				// left an empty or corrupt file. Probably we should remove it.
				try {
					FileHelper.delete(directlyAccessibleThumbnailFile);
				}
				catch(IOException e) {
					// Log the error but do not propogate this exception.
					logger.warn(e);
				}
				throw te; // Rethrow it. 
			}
		}
		else {
			// The thumbnail is to be stored in the same repository
			// as the primary file. 
			String thumbnailFileName = makeThumbnailFileName(fileName);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			// Generate thumbnail
			Thumbnail.createThumbnail(inputData, baos, maxWidth, maxHeight);
			
			// Store the thumbnail into the repository.
			
			int fileInfo = service.fileInfo(session, binder, entry, thumbnailFileName);
			
			if(fileInfo == RepositoryService.UNVERSIONED_FILE) {
				service.update(session, binder, entry, thumbnailFileName, 
	    				new ByteArrayInputStream(((ByteArrayOutputStream) baos).toByteArray()));				
			}
			else if(fileInfo == RepositoryService.NON_EXISTING_FILE) { 
				service.createUnversioned(session, binder, entry, thumbnailFileName, 
    				new ByteArrayInputStream(((ByteArrayOutputStream) baos).toByteArray()));
			}
			else {
				throw new InternalException();
			}
		}		
	}
}
