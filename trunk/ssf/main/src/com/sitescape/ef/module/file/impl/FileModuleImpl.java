package com.sitescape.ef.module.file.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.ef.InternalException;
import com.sitescape.ef.UncheckedIOException;
import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FileAttachment.FileLock;
import com.sitescape.ef.domain.FileItem;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.Reservable;
import com.sitescape.ef.domain.ReservedByAnotherUserException;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.VersionAttachment;
import com.sitescape.ef.module.file.ContentFilter;
import com.sitescape.ef.module.file.FilesErrors;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.file.FilterException;
import com.sitescape.ef.module.file.LockIdMismatchException;
import com.sitescape.ef.module.file.LockedByAnotherUserException;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.repository.RepositoryException;
import com.sitescape.ef.repository.RepositoryUtil;
import com.sitescape.ef.repository.RepositorySession;
import com.sitescape.ef.repository.RepositorySessionFactoryUtil;
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
	
	private static final String NON_WEBDAV_LOCK_ID = "";
	
	private static final Date MAX_DATE = new Date(Long.MAX_VALUE);
	
	protected Log logger = LogFactory.getLog(getClass());

	private CoreDao coreDao;
	private TransactionTemplate transactionTemplate;
	private ContentFilter contentFilter;
	private String failedFilterFile;
	private String failedFilterTransaction;
	private int lockExpirationAllowance; // number of seconds
	
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

	public FilesErrors deleteFiles(Binder binder, DefinableEntity entry,
			FilesErrors errors) {
		if(errors == null)
			errors = new FilesErrors();
		
		List fAtts = entry.getFileAttachments();
		for(int i = 0; i < fAtts.size(); i++) {
			FileAttachment fAtt = (FileAttachment) fAtts.get(i);

			try {
				deleteFileInternal(binder, entry, fAtt, errors);
			}
			catch(Exception e) {
				logger.error("Error deleting file " + fAtt.getFileItem().getName(), e);
    			errors.addProblem(new FilesErrors.Problem
    					(fAtt.getRepositoryServiceName(),  fAtt.getFileItem().getName(), 
    							FilesErrors.Problem.OTHER_PROBLEM, e));
			}
		}
		
		// Even in the situation where the operation was not entirely successful,
		// we need to reflect the corresponding metadata changes back to the
		// database. 
		triggerUpdateTransaction();
		
		return errors;
	}
	
	public FilesErrors deleteFile(Binder binder, DefinableEntity entry,
			FileAttachment fAtt, FilesErrors errors) {
		if(errors == null)
			errors = new FilesErrors();
		
		try {
			deleteFileInternal(binder, entry, fAtt, errors);
		}
		catch(Exception e) {
			logger.error("Error deleting file " + fAtt.getFileItem().getName(), e);
			errors.addProblem(new FilesErrors.Problem
					(fAtt.getRepositoryServiceName(),  fAtt.getFileItem().getName(), 
							FilesErrors.Problem.OTHER_PROBLEM, e));
		}
				
		triggerUpdateTransaction();
		
		return errors;
	}
	
	public void readFile(Binder binder, DefinableEntity entry, FileAttachment fa, 
			OutputStream out) {
    	RepositoryUtil.read(fa.getRepositoryServiceName(), binder, entry, 
				fa.getFileItem().getName(), out);
	}
	
	public InputStream readFile(Binder binder, DefinableEntity entry, FileAttachment fa) { 
		return RepositoryUtil.read(fa.getRepositoryServiceName(), binder, entry, 
				fa.getFileItem().getName());
	}
	
	public boolean scaledFileExists(Binder binder, 
			DefinableEntity entry, FileAttachment fAtt) {
		int fileInfo = RepositoryUtil.fileInfo(fAtt.getRepositoryServiceName(), 
				binder, entry, makeScaledFileName(fAtt.getFileItem().getName()));
		
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
		RepositoryUtil.read(fa.getRepositoryServiceName(), binder, 
				entry, makeScaledFileName(fa.getFileItem().getName()), out);
	}
	
	public void readIndirectlyAccessibleThumbnailFile(
			Binder binder, DefinableEntity entry, FileAttachment fa, 
			OutputStream out) {
		RepositoryUtil.read(fa.getRepositoryServiceName(), binder, 
				entry, makeThumbnailFileName(fa.getFileItem().getName()), out);	
	}
	
	public void generateScaledFile(Binder binder, DefinableEntity entry, 
			FileAttachment fa, int maxWidth, int maxHeight) {
		String repositoryServiceName = fa.getRepositoryServiceName();
		String relativeFilePath = fa.getFileItem().getName();
		
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryServiceName);
		
		try {
			// Read the input file from the repository into a byte array. 
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
			session.read(binder, entry, relativeFilePath, baos);
	
			generateAndStoreScaledFile(session, binder, entry, 
					relativeFilePath, baos.toByteArray(), maxWidth, maxHeight);
		}
		finally {
			session.close();
		}	
	}
	
	public void generateThumbnailFile(Binder binder, 
			DefinableEntity entry, FileAttachment fa, int maxWidth, 
			int maxHeight, boolean thumbnailDirectlyAccessible) {
		String repositoryServiceName = fa.getRepositoryServiceName();
		String relativeFilePath = fa.getFileItem().getName();

		try {
			RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryServiceName);

			try {
				// Read the input file from the repository into a byte array. 
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
				session.read(binder, entry, relativeFilePath, baos);
		
				generateAndStoreThumbnailFile(session, binder, entry, 
						relativeFilePath, baos.toByteArray(), maxWidth, maxHeight, thumbnailDirectlyAccessible);
			}
			finally {
				session.close();
			}
		}
		catch(FileNotFoundException e) {
			throw new UncheckedIOException(e);
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
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
			int thumbnailMaxWidth, int thumbnailMaxHeight, 
			boolean thumbnailDirectlyAccessible) {
		String repositoryServiceName = fa.getRepositoryServiceName();
		String relativeFilePath = fa.getFileItem().getName();

		try {
			RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryServiceName);

			try {
				// Read the input file from the repository into a byte array. 
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
				session.read(binder, entry, relativeFilePath, baos);
		
				// Generate and store scaled file.
				generateAndStoreScaledFile(session, binder, entry, 
						relativeFilePath, baos.toByteArray(), maxWidth, maxHeight);
				
				// Generate and store thumbnail file.
				generateAndStoreThumbnailFile(session, binder, entry, 
						relativeFilePath, baos.toByteArray(), maxWidth, maxHeight, 
						thumbnailDirectlyAccessible);
			}
			finally {
				session.close();
			}
		}
		catch(FileNotFoundException e) {
			throw new UncheckedIOException(e);
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}
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
    	
    	for(int i = 0; i < fileUploadItems.size(); i++) {
    		FileUploadItem fui = (FileUploadItem) fileUploadItems.get(i);
    		try {
    			// Unlike deleteFileInternal, writeFileTransactional is transactional.
    			// See the comment in writeFileMetadataTransactional for reason. 
    			this.writeFileTransactional(binder, entry, fui, errors);
    		}
    		catch(Exception e) {
    			logger.error("Error writing file " + fui.getOriginalFilename(), e);
    			errors.addProblem(new FilesErrors.Problem
    					(fui.getRepositoryServiceName(),  fui.getOriginalFilename(), 
    							FilesErrors.Problem.OTHER_PROBLEM, e));
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
	
	public void lock(Binder binder, DefinableEntity entity, FileAttachment fa, 
			String lockId, String lockSubject, Date expirationDate) 
		throws ReservedByAnotherUserException, 
			LockedByAnotherUserException, LockIdMismatchException, 
			UncheckedIOException, RepositoryException {
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
    					user, expirationDate));
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
    							user, expirationDate));
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
    		String lockId) throws UncheckedIOException, RepositoryException {
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
		throws RepositoryException, UncheckedIOException {
		closeExpiredLocksTransactional(binder, entity, true);
	}

	private void triggerUpdateTransaction() {
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {  
                return null;
        	}
        });	
	}
	
	private void deleteFileInternal(Binder binder, DefinableEntity entry,
			FileAttachment fAtt, FilesErrors errors) {
		String relativeFilePath = fAtt.getFileItem().getName();
		String repositoryServiceName = fAtt.getRepositoryServiceName();
		
		// Forcefully unlock the file (if locked). We discard pending
		// changes for the file for obvious reason - The file is soon
		// to be deleted. 
		try {
			closeLock(binder, entry, fAtt, false);
		}
		catch(Exception e) {
			logger.error("Error canceling lock on file " + relativeFilePath, e);
			errors.addProblem(new FilesErrors.Problem
					(repositoryServiceName, relativeFilePath, 
							FilesErrors.Problem.PROBLEM_CANCELING_LOCK, e));
			return;
		}

		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryServiceName);

		try {
			try {
				// Delete primary file
				session.delete(binder, entry, relativeFilePath);
			}
			catch(Exception e) {
				logger.error("Error deleting primary file " + relativeFilePath, e);
				errors.addProblem(new FilesErrors.Problem
						(repositoryServiceName, relativeFilePath, 
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
				String scaledFileName = makeScaledFileName(relativeFilePath);
				if (session.fileInfo(binder, entry, scaledFileName) 
						!= RepositorySession.NON_EXISTING_FILE) {
					session.delete(binder, entry, scaledFileName);
				}
			}
			catch(Exception e) {
				logger.error("Error deleting scaled copy of " + relativeFilePath, e);
				errors.addProblem(new FilesErrors.Problem
						(repositoryServiceName, relativeFilePath, 
								FilesErrors.Problem.PROBLEM_DELETING_SCALED_FILE, e));
				// Since we successfully deleted the primary file above (which
				// indicates that at least the repository seems up and running),
				// let's not the failure to delete generated file to abort the
				// entire operation. So we proceed. 
			}

			// Try deleting thumbnail file if exists

			try {
				// Directly-accessible thumbnail file?
				File directlyAccessibleThumbnailFile = getDirectlyAccessibleThumbnailFile(
						entry, relativeFilePath);
				// Delete it if exists
				FileHelper.delete(directlyAccessibleThumbnailFile);
	
				// thumbnail file stored in repository?
				String thumbnailFileName = makeThumbnailFileName(relativeFilePath);
				if (session.fileInfo(binder, entry, thumbnailFileName) 
						!= RepositorySession.NON_EXISTING_FILE) {
					session.delete(binder, entry, thumbnailFileName);
				}
			}
			catch(Exception e) {
				logger.error("Error deleting thumbnail copy of " + relativeFilePath, e);
				errors.addProblem(new FilesErrors.Problem
						(repositoryServiceName, relativeFilePath, 
								FilesErrors.Problem.PROBLEM_DELETING_THUMBNAIL_FILE, e));
				// We proceed and update metadata.
			}
		} finally {
			session.close();
		}

		// Remove metadata
		entry.removeAttachment(fAtt);
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
	 
    private void writeFileTransactional(Binder binder, DefinableEntity entry, 
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
		
		String relativeFilePath = fui.getOriginalFilename();
		String repositoryServiceName = fui.getRepositoryServiceName();

		// First, find out whether or not this is a new file for the entry.
		// It is important to note that, as far as identity/existence test
		// goes, the namespace is flat for a single instance of Entry.
		// In other words, regardless of the data elements used for accessing
		// the file, the files are treated identical globally within a single
		// Entry instance as long as their file names are identical. 
    	FileAttachment fAtt = entry.getFileAttachment(repositoryServiceName, relativeFilePath);

    	boolean isNew = false;
    	
		RepositorySession session = RepositorySessionFactoryUtil.openSession(repositoryServiceName);

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
	    					(repositoryServiceName, relativeFilePath, 
	    							FilesErrors.Problem.PROBLEM_STORING_PRIMARY_FILE, e));
	    			return;
	    		}		

	    		// Scaled file
	        	if(fui.getMaxWidth() > 0 && fui.getMaxHeight() > 0) {
	            	// Generate scaled file which goes into the same repository as
	        		// the primary file except that the generated file is not versioned.
	        		try {
	        			generateAndStoreScaledFile(session, binder, entry, relativeFilePath,
	        				primaryContent, fui.getMaxWidth(),fui.getMaxWidth());
	        		}
	        		catch(ThumbnailException e) {
	        			// Scaling operation can fail for a variety of reasons, primarily
	        			// when the file format is not supported. Do not cause this to
	        			// fail the entire operation. Simply log it and proceed.  
	        			logger.warn("Error generating scaled copy of " + relativeFilePath, e);
		    			errors.addProblem(new FilesErrors.Problem
		    					(repositoryServiceName, relativeFilePath, 
		    							FilesErrors.Problem.PROBLEM_GENERATING_SCALED_FILE, e));
	        		}
	        		catch(Exception e) {
		    			// Failed to store scaled file. Record the problem and proceed.
	        			logger.warn("Error storing scaled copy of " + relativeFilePath, e);
		    			errors.addProblem(new FilesErrors.Problem
		    					(repositoryServiceName, relativeFilePath, 
		    							FilesErrors.Problem.PROBLEM_STORING_SCALED_FILE, e));	        			
	        		}
	        	}    

	        	// Thumbnail file
	        	if(fui.getGenerateThumbnail()) {
	        		try {
	        			generateAndStoreThumbnailFile(session, binder, entry,
	        				relativeFilePath, primaryContent, fui.getThumbnailMaxWidth(), 
	        				fui.getThumbnailMaxHeight(), fui.isThumbnailDirectlyAccessible());
	        		}
	        		catch(ThumbnailException e) {
	        			logger.warn("Error generating thumbnail copy of " + relativeFilePath, e);
		    			errors.addProblem(new FilesErrors.Problem
		    					(repositoryServiceName, relativeFilePath, 
		    							FilesErrors.Problem.PROBLEM_GENERATING_THUMBNAIL_FILE, e));
	        		}
	        		catch(Exception e) {
	        			logger.warn("Error storing thumbnail copy of " + relativeFilePath, e);
		    			errors.addProblem(new FilesErrors.Problem
		    					(repositoryServiceName, relativeFilePath, 
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
	    					(repositoryServiceName, relativeFilePath, 
	    							FilesErrors.Problem.PROBLEM_STORING_PRIMARY_FILE, e));
	    			return;
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
    }
    
    private File getDirectlyAccessibleThumbnailFile(DefinableEntity entry, String primaryFileName) {
    	return new File(directlyAccessibleThumbnailFilePath(entry, primaryFileName));
    }
    
    private String directlyAccessibleThumbnailFilePath(DefinableEntity entry, String primaryFileName) {
    	return DirPath.getThumbnailDirPath() + File.separator + entry.getId() + "_" + primaryFileName;
    }

    private void writeExistingFile(RepositorySession session,
    		Binder binder, DefinableEntity entry, FileUploadItem fui, Object inputData)
		throws LockedByAnotherUserException, RepositoryException, UncheckedIOException {
    	User user = RequestContextHolder.getRequestContext().getUser();
    	String relativeFilePath = fui.getOriginalFilename();
    	FileAttachment fAtt = entry.getFileAttachment(fui.getRepositoryServiceName(), relativeFilePath);
    	
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
    	
    	if(versionName != null) {
    		try {
    			updateFileAttachment(fAtt, user, versionName, fui.getSize());
    		}
    		catch(IOException e) {
    			throw new UncheckedIOException(e);
    		}
    	}
    }

    private void updateFileAttachment(FileAttachment fAtt, 
			Principal user, String versionName, long contentLength) {
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
	private FileAttachment createFile(RepositorySession session, 
			Binder binder, DefinableEntity entry, FileUploadItem fui, Object inputData) 
		throws RepositoryException, UncheckedIOException {	
		// Since we are creating a new file, file locking doesn't concern us.
		
		FileAttachment fAtt = createFileAttachment(entry, fui);
		
		String versionName = createVersionedWithInputData(session, binder, entry,
				fui.getOriginalFilename(), inputData);

		createVersionAttachment(fAtt, versionName);

		return fAtt;
	}
	
	private String createVersionedWithInputData(RepositorySession session,
			Binder binder, DefinableEntity entry, String relativeFilePath, Object inputData)
		throws RepositoryException {
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
		throws RepositoryException {
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
		fAtt.setModification(fAtt.getCreation());
    	fAtt.setRepositoryServiceName(fui.getRepositoryServiceName());
    	//set attribute name - null if not not named
    	fAtt.setName(fui.getName());
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
	
	private void generateAndStoreScaledFile(RepositorySession session, 
			Binder binder, DefinableEntity entry, String relativeFilePath, 
			byte[] inputData, int maxWidth, int maxHeight) 
		throws ThumbnailException, RepositoryException {
		String scaledFileName = makeScaledFileName(relativeFilePath);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		Thumbnail.createThumbnail(inputData, baos, maxWidth, maxHeight);

		int fileInfo = session.fileInfo(binder, entry, scaledFileName);
		
		if(fileInfo == RepositorySession.UNVERSIONED_FILE) {
			session.update(binder, entry, scaledFileName,
					new ByteArrayInputStream(baos.toByteArray()));									
		}
		else if(fileInfo == RepositorySession.NON_EXISTING_FILE) {
			session.createUnversioned(binder, entry, scaledFileName,
					new ByteArrayInputStream(baos.toByteArray()));						
		}
		else {
			throw new InternalException();
		}
	}
	
	/**
	 * 
	 * @param session
	 * @param binder
	 * @param entry
	 * @param relativeFilePath
	 * @param inputData
	 * @param maxWidth
	 * @param maxHeight
	 * @param directlyAccessible
	 * @throws ThumbnailException if thumbnail generation procedure itself fails; 
	 * typically indicates that the data format is not supported.
	 * @throws RepositoryException error in repository operation; can occur
	 * when thumbnail is stored by repository service
	 * @throws FileNotFoundException error in file operation; can occur when thumbnail
	 * is stored in a client-visible directory on file system
	 * @throws IOException error in file operation; can occur when thumbnail
	 * is stored in a client-visible directory on file system
	 */
	private void generateAndStoreThumbnailFile(RepositorySession session, 
			Binder binder, DefinableEntity entry, String relativeFilePath, 
			byte[] inputData, int maxWidth, int maxHeight, boolean directlyAccessible) 
		throws ThumbnailException, RepositoryException, FileNotFoundException,
		IOException {

		if(directlyAccessible) {
			// The thumbnail is to be stored in a directory where 
			// the client can access it directly without going
			// through the repository service layer.
			File directlyAccessibleThumbnailFile = getDirectlyAccessibleThumbnailFile(entry, relativeFilePath);
			
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
			String thumbnailFileName = makeThumbnailFileName(relativeFilePath);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			// Generate thumbnail
			Thumbnail.createThumbnail(inputData, baos, maxWidth, maxHeight);
			
			// Store the thumbnail into the repository.
			
			int fileInfo = session.fileInfo(binder, entry, thumbnailFileName);
			
			if(fileInfo == RepositorySession.UNVERSIONED_FILE) {
				session.update(binder, entry, thumbnailFileName, 
	    				new ByteArrayInputStream(((ByteArrayOutputStream) baos).toByteArray()));				
			}
			else if(fileInfo == RepositorySession.NON_EXISTING_FILE) { 
				session.createUnversioned(binder, entry, thumbnailFileName, 
    				new ByteArrayInputStream(((ByteArrayOutputStream) baos).toByteArray()));
			}
			else {
				throw new InternalException();
			}
		}		
	}       
    
    private void closeLocksTransactional(Binder binder, DefinableEntity entity,
    		boolean commit) throws RepositoryException,
    		UncheckedIOException {
    	if(closeLocks(binder, entity, commit))
    		triggerUpdateTransaction();   	
    }
    
    private boolean closeLocks(Binder binder, DefinableEntity entity,
    		boolean commit) throws RepositoryException,
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
    }
    
    private void closeLockTransactional(Binder binder, DefinableEntity entity,
    		FileAttachment fa, boolean commit) throws RepositoryException,
    		UncheckedIOException {
    	if(closeLock(binder, entity, fa, commit))
    		this.triggerUpdateTransaction();
    }

    private boolean closeLock(Binder binder, DefinableEntity entity, 
    		FileAttachment fa, boolean commit) throws RepositoryException,
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
				RepositoryUtil.uncheckout(fa.getRepositoryServiceName(), 
						binder, entity, fa.getFileItem().getName()); 					
			}

			fa.setFileLock(null); // Clear the lock
			if(entity instanceof Reservable) {
				((Reservable)entity).decrLockedFileCount(); // Decrement lock count
			}
			metadataDirty = true;
		}
    	
    	return metadataDirty;
    }
    
    private void closeExpiredLocksTransactional(Binder binder, DefinableEntity entity,
    		boolean commit) throws RepositoryException,
    		UncheckedIOException {
    	if(closeExpiredLocks(binder, entity, commit))
    		triggerUpdateTransaction();   	
    }
    		
    private boolean closeExpiredLocks(Binder binder, DefinableEntity entity,
    		boolean commit) throws RepositoryException,
    		UncheckedIOException {
    	if((entity instanceof Reservable) &&
    			((Reservable)entity).getLockedFileCount() <= 0) {
    		// A little optimization for reservable entity.
    		return false; 
    	}
    	else {
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
    }
    
    private void closeExpiredLockTransactional(Binder binder, DefinableEntity entity,
    		FileAttachment fa, boolean commit) throws RepositoryException,
    		UncheckedIOException {
    	if(closeExpiredLock(binder, entity, fa, commit))
    		this.triggerUpdateTransaction();
    }
    
    private boolean closeExpiredLock(Binder binder, DefinableEntity entity, 
    		FileAttachment fa, boolean commit) throws RepositoryException,
    		UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(fa.getRepositoryServiceName());

		try {
			return closeExpiredLock(session, binder, entity, fa, commit);
		}
		finally {
			session.close();
		}	
	}
    
    private boolean closeExpiredLock(RepositorySession session,
    		Binder binder, DefinableEntity entity, FileAttachment fa, 
    		boolean commit) throws RepositoryException, UncheckedIOException {
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
				if(entity instanceof Reservable) {
					((Reservable)entity).decrLockedFileCount(); // Decrement lock count
				}
				metadataDirty = true;	
        	}
    	}
    	
    	return metadataDirty;
    }
    
    private boolean commitPendingChanges(Binder binder, DefinableEntity entity,
    		FileAttachment fa, Principal changeOwner)
    	throws RepositoryException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(fa.getRepositoryServiceName());

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
    	throws RepositoryException, UncheckedIOException {
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
			updateFileAttachment(fa, changeOwner, versionName, contentLength);
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
}
