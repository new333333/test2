/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.module.file.impl;

import static com.sitescape.util.search.Restrictions.conjunction;
import static com.sitescape.util.search.Restrictions.eq;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.Query;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.FileCopyUtils;

import com.sitescape.team.InternalException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.docconverter.IHtmlConverterManager;
import com.sitescape.team.docconverter.IImageConverterManager;
import com.sitescape.team.docconverter.ImageConverter;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FileItem;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.Reservable;
import com.sitescape.team.domain.ReservedByAnotherUserException;
import com.sitescape.team.domain.TitleException;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserPrincipal;
import com.sitescape.team.domain.VersionAttachment;
import com.sitescape.team.domain.FileAttachment.FileLock;
import com.sitescape.team.lucene.Hits;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.file.ContentFilter;
import com.sitescape.team.module.file.DeleteVersionException;
import com.sitescape.team.module.file.FileModule;
import com.sitescape.team.module.file.FilesErrors;
import com.sitescape.team.module.file.FilterException;
import com.sitescape.team.module.file.LockIdMismatchException;
import com.sitescape.team.module.file.LockedByAnotherUserException;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.shared.ChangeLogUtils;
import com.sitescape.team.repository.RepositoryServiceException;
import com.sitescape.team.repository.RepositorySession;
import com.sitescape.team.repository.RepositorySessionFactory;
import com.sitescape.team.repository.RepositorySessionFactoryUtil;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.repository.archive.ArchiveStore;
import com.sitescape.team.search.LuceneReadSession;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.search.SearchObject;
import com.sitescape.team.util.DatedMultipartFile;
import com.sitescape.team.util.FileHelper;
import com.sitescape.team.util.FilePathUtil;
import com.sitescape.team.util.FileStore;
import com.sitescape.team.util.FileUploadItem;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SimpleMultipartFile;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.util.KeyValuePair;
import com.sitescape.util.Validator;
import com.sitescape.util.search.Constants;
import com.sitescape.util.search.Criteria;

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
public class FileModuleImpl extends CommonDependencyInjection implements FileModule, InitializingBean {

	private static final String FAILED_FILTER_FILE_DELETE 			= "delete";
	private static final String FAILED_FILTER_FILE_MOVE 			= "move";
	private static final String FAILED_FILTER_FILE_DEFAULT			= FAILED_FILTER_FILE_DELETE;
	private static final String FAILED_FILTER_TRANSACTION_CONTINUE 	= "continue";
	private static final String FAILED_FILTER_TRANSACTION_ABORT 	= "abort";
	private static final String FAILED_FILTER_TRANSACTION_DEFAULT	= FAILED_FILTER_TRANSACTION_ABORT;
		
	protected Log logger = LogFactory.getLog(getClass());

	private TransactionTemplate transactionTemplate;
	private ContentFilter contentFilter; // may be null
	private String failedFilterFile;
	private String failedFilterTransaction;
	private int lockExpirationAllowance; // number of seconds
	private FileStore cacheFileStore;
	private IHtmlConverterManager htmlConverterManager;
	private IImageConverterManager imageConverterManager;
		
	protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public void setLockExpirationAllowance(int lockExpirationAllowance) {
		this.lockExpirationAllowance = lockExpirationAllowance;
	}
	
	protected int getLockExpirationAllowanceMilliseconds() {
		return this.lockExpirationAllowance * 1000;
	}
	
	protected String getFailedFilterFile() {
		return failedFilterFile;
	}
	
	protected void initFailedFilterFile() {
		String failedFilterFile = SPropsUtil.getString("file.content.filter.failed.filter.file", "");
		
		if(failedFilterFile.equals("")) {
			this.failedFilterFile = FAILED_FILTER_FILE_DEFAULT;
		}
		else if(FAILED_FILTER_FILE_DELETE.equalsIgnoreCase(failedFilterFile)) {
			this.failedFilterFile = FAILED_FILTER_FILE_DELETE;
		}
		else if(FAILED_FILTER_FILE_MOVE.equalsIgnoreCase(failedFilterFile)) {
			this.failedFilterFile = FAILED_FILTER_FILE_MOVE;
		}
		else {
			logger.info("Unknown value " + failedFilterFile + 
					" for property file.content.filter.failed.filter.file: Using default value " 
					+ FAILED_FILTER_FILE_DEFAULT);
			this.failedFilterFile = FAILED_FILTER_FILE_DEFAULT;
		}
	}
	
	public void setHtmlConverterManager(IHtmlConverterManager htmlConverterManager) {
		this.htmlConverterManager = htmlConverterManager;
	}
	
	protected IHtmlConverterManager getHtmlConverterManager() {
		return htmlConverterManager;
	}
	
	public void setImageConverterManager(IImageConverterManager imageConverterManager) {
		this.imageConverterManager = imageConverterManager;
	}
	
	protected IImageConverterManager getImageConverterManager() {
		return imageConverterManager;
	}
	
	protected String getFailedFilterTransaction() {
		return failedFilterTransaction;
	}
	
	protected void initFailedFilterTransaction() {
		String failedFilterTransaction = SPropsUtil.getString("file.content.filter.failed.filter.transaction", "");
		
		if(failedFilterTransaction.equals("")) {
			this.failedFilterTransaction = FAILED_FILTER_TRANSACTION_DEFAULT;			
		}
		else if(FAILED_FILTER_TRANSACTION_CONTINUE.equals(failedFilterTransaction)) {
			this.failedFilterTransaction = FAILED_FILTER_TRANSACTION_CONTINUE;
		}
		else if(FAILED_FILTER_TRANSACTION_ABORT.equals(failedFilterTransaction)) {
			this.failedFilterTransaction = FAILED_FILTER_TRANSACTION_ABORT;
		}
		else {
			logger.info("Unknown value " + failedFilterTransaction +
					" for property file.content.filter.failed.filter.transaction: Using default value "
					+ FAILED_FILTER_TRANSACTION_DEFAULT);
			this.failedFilterTransaction = FAILED_FILTER_TRANSACTION_DEFAULT;
		}
	}
		
	protected ContentFilter getContentFilter() {
		return contentFilter;
	}
	
	protected void initContentFilter() throws Exception {
		String contentFilterClassName = SPropsUtil.getString("file.content.filter.class", "");
		if(Validator.isNotNull(contentFilterClassName)) {
			Class contentFilterClass = ReflectHelper.classForName(contentFilterClassName);
			contentFilter = (ContentFilter) contentFilterClass.newInstance();
		}
		
		initFailedFilterFile();
		
		initFailedFilterTransaction();		
	}
	
	public void afterPropertiesSet() throws Exception {
		cacheFileStore = new FileStore(SPropsUtil.getString("cache.file.store.dir"));
		
		initContentFilter();	
	}

	public FilesErrors deleteFiles(final Binder binder, 
			final DefinableEntity entry, boolean deleteMirroredSource, 
			FilesErrors errors) {
		return deleteFiles(binder, entry, deleteMirroredSource, errors, false);
	}
	
	private FilesErrors deleteFiles(final Binder binder, 
			final DefinableEntity entry, boolean deleteMirroredSource, 
			FilesErrors errors, boolean deleteAttachment) {
		if(errors == null)
			errors = new FilesErrors();
		
		Collection<FileAttachment> fAtts = entry.getFileAttachments();
		if (fAtts.isEmpty()) return errors;
		List<ChangeLog> changeLogs = new ArrayList<ChangeLog>();
		boolean updateMetadata = deleteAttachment;
		for(FileAttachment fAtt :fAtts) {

			try {
				ChangeLog changeLog = deleteFileInternal(binder, entry, fAtt, 
						deleteMirroredSource, errors, updateMetadata);
				if(changeLog != null)
					changeLogs.add(changeLog);
			}
			catch(Exception e) {
				logger.error("Error deleting file " + fAtt.getFileItem().getName(), e);
    			errors.addProblem(new FilesErrors.Problem
    					(fAtt.getRepositoryName(),  fAtt.getFileItem().getName(), 
    							FilesErrors.Problem.OTHER_PROBLEM, e));
			}
		}
		
		String entityPath = FilePathUtil.getEntityDirPath(binder, entry);
		try {
			cacheFileStore.deleteDirectory(entityPath);
		}
		catch(Exception e) {
			logger.error("Error deleting the entry's cache directory [" +
					cacheFileStore.getAbsolutePath(entityPath) + "]", e);
		}
		
		if(!updateMetadata) {
			// Since there was no in-line transaction for updating metadata,
			// we must run a separate transaction to record the change logs. 
			// The following call also ensures that, even in the situation where
			// the operation was not entirely successful, we still reflect the
			// correponding metadata changes back to the database. 
			writeDeleteChangeLogTransactional(changeLogs);
		}
				
		return errors;
	}
	
	public FilesErrors deleteFile(Binder binder, DefinableEntity entry,
			FileAttachment fAtt, FilesErrors errors) {
		if(errors == null)
			errors = new FilesErrors();
		
		try {
			deleteFileInternal(binder, entry, fAtt, true, errors, true);
		}
		catch(Exception e) {
			logger.error("Error deleting file " + fAtt.getFileItem().getName(), e);
			errors.addProblem(new FilesErrors.Problem
					(fAtt.getRepositoryName(),  fAtt.getFileItem().getName(), 
							FilesErrors.Problem.OTHER_PROBLEM, e));
			//make sure any updates that happened get recored
			triggerUpdateTransaction();
		}
				
		String faPath = FilePathUtil.getFileAttachmentDirPath(binder, entry, fAtt);
		try {
			cacheFileStore.deleteDirectory(faPath);
		}
		catch(Exception e) {
			logger.error("Error deleting the file attachment's cache directory [" +
					cacheFileStore.getAbsolutePath(faPath) + "]", e);
		}
		
		return errors;
	}
	public void readFile(Binder binder, DefinableEntity entry, FileAttachment fa, 
			OutputStream out) {
		String versionName = null;
		String latestVersionName = null;
		
		if(fa instanceof VersionAttachment) {
			versionName = ((VersionAttachment) fa).getVersionName();
		}
		else {
			if(fa.getFileLock() == null)
				versionName = fa.getHighestVersion().getVersionName();
			else
				latestVersionName = fa.getHighestVersion().getVersionName();
		}
		
		RepositoryUtil.readVersioned(fa.getRepositoryName(), binder, entry, 
				fa.getFileItem().getName(), versionName, latestVersionName, out);			
	}
	
	public InputStream readFile(Binder binder, DefinableEntity entry, FileAttachment fa) { 
		String versionName = null;
		String latestVersionName = null;
		
		if(fa instanceof VersionAttachment) {
			versionName = ((VersionAttachment) fa).getVersionName();
		}
		else {
			if(fa.getFileLock() == null)
				versionName = fa.getHighestVersion().getVersionName();
			else
				latestVersionName = fa.getHighestVersion().getVersionName();
		}
		
		return RepositoryUtil.readVersioned(fa.getRepositoryName(), binder, entry, 
				fa.getFileItem().getName(), versionName, latestVersionName);
	}
	
	public void readScaledFile(Binder binder, DefinableEntity entry, 
			FileAttachment fa, OutputStream out)
	{
		ImageConverter converter = null;
		
		try
		{
			converter = this.imageConverterManager.getConverter();
			FileCopyUtils.copy(converter.convertToScaledImage(binder, entry, fa,
									new ImageConverter.Parameters(IImageConverterManager.IMAGEWIDTH, IImageConverterManager.IMAGEHEIGHT)),
							   out);
		}
		catch (IOException e)
		{
			try {
				readFile(binder, entry, fa, out);				
			}
			catch(Exception ex) {
				// out.print(NLT.get("file.error") + ": " + e.getLocalizedMessage());
				throw new UncheckedIOException(e);
			}
		}
	}
	
	public void readThumbnailFile(
			Binder binder, DefinableEntity entry, FileAttachment fa, 
			OutputStream out)
	{
		ImageConverter converter = null;
		
		try
		{
			converter = this.imageConverterManager.getConverter();
			FileCopyUtils.copy(converter.convertToThumbnail(binder, entry, fa,
									new ImageConverter.Parameters(IImageConverterManager.IMAGEWIDTH, 0)),
							   out);
		}
		catch (IOException e)
		{
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

		try
		{
			is = htmlConverterManager.getConverter().convert(url, binder, entry, fa);
			FileCopyUtils.copy(is, out);
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
		InputStream is = null;
		
		try
		{
			urlFileName = (new File(urlFileName)).getName();  // Prevent ../ filename hacks
			is = htmlConverterManager.getConverter().getCachedFile(binder, entry, fa, urlFileName);
			FileCopyUtils.copy(is, out);
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
		InputStream is = null;
		
		try
		{
			is = htmlConverterManager.getConverter().getCachedFile(binder, entry, fa, imageFileName);
			FileCopyUtils.copy(is, out);
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
	    			if (fui.isRegistered()) getCoreDao().unRegisterFileName(binder, fui.getOriginalFilename());
    				fileUploadItems.remove(i);
    			}
    		} catch (TitleException lx) {
    			//pass up
    			throw lx;
    		}
    		catch(Exception e) {
    			if (fui.isRegistered()) getCoreDao().unRegisterFileName(binder, fui.getOriginalFilename());
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
		if(contentFilter == null)
			return new FilesErrors(); // Nothing to filter with
		
		FilesErrors errors = null;
		// Note that we do not have to use String comparison in the expression
		// below. Just reference comparison is enough. 
		if(getFailedFilterTransaction() == FAILED_FILTER_TRANSACTION_CONTINUE) {
			errors = new FilesErrors();
		}
		
		InputStream is;
		
    	for(int i = 0; i < fileUploadItems.size();) {
    		FileUploadItem fui = (FileUploadItem) fileUploadItems.get(i);
    		try {
        		fui.makeReentrant();
        		
        		is = fui.getInputStream();
        		
        		try {
        			contentFilter.filter(fui.getOriginalFilename(), is);
        		}
        		finally {
        			try {
        				is.close();
        			}
        			catch(IOException ignore) {}
        		}
    			
    			//Only advance on success
    			++i;
    		}
    		catch(IOException e) {
    			throw new UncheckedIOException(e);
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
    					moveFilterFailedFile(binder, fui);
					} catch (IOException e1) {
						logger.error("Failed to move bad file " + fui.getOriginalFilename(), e1);
					}
    			}
    			if(errors != null) {
        			if (fui.isRegistered()) getCoreDao().unRegisterFileName(binder, fui.getOriginalFilename());
    				errors.addProblem(new FilesErrors.Problem
    					(fui.getRepositoryName(),  fui.getOriginalFilename(), 
    							FilesErrors.Problem.PROBLEM_FILTERING, e));
    			}
    			else {
    				//clean all newly registered titles out
   					for (int j=0; j< fileUploadItems.size(); ++j) {
   						FileUploadItem fu = (FileUploadItem) fileUploadItems.get(j);
   		       			if (fu.isRegistered()) { 
 	    					getCoreDao().unRegisterFileName(binder, fu.getOriginalFilename());
   						}  		       		     	
    				}
    				throw e;
    			}
    		}
    	}
	
    	if(errors == null)
    		errors = new FilesErrors();
    	
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
    					commitPendingChanges(binder, entity, fa, lock); 
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
			commitPendingChanges(binder, entity, fa, lock);
			
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
		//binder files are not registered
		if (binder.isLibrary() && !binder.equals(entity)) getCoreDao().updateFileName(binder, entity, fa.getFileItem().getName(), newName);
        if ((entity.getEntryDef() != null)  && DefinitionUtils.isSourceItem(entity.getEntryDef().getDefinition(), fa.getName(), "title")) {
          	//if tracking unique titles, remove old title
        	String oldTitle = entity.getNormalTitle();
            //check title
        	entity.setTitle(newName);			   			   
           	if ((entity.getParentBinder() != null) && entity.getParentBinder().isUniqueTitles()) getCoreDao().updateTitle(entity.getParentBinder(), entity, oldTitle, entity.getNormalTitle());
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
	
	public void moveFiles(Binder binder, DefinableEntity entity, 
			Binder destBinder, DefinableEntity destEntity)
	throws UncheckedIOException, RepositoryServiceException {
    	Collection<FileAttachment> atts = entity.getFileAttachments();
    	//first register file names, so if one fails, files are not copied
    	for(FileAttachment fa :atts) {    			
   			if (binder.isLibrary() && !binder.equals(entity))
   				getCoreDao().unRegisterFileName(binder, fa.getFileItem().getName());
    		if (destBinder.isLibrary() && !destBinder.equals(destEntity))
    			getCoreDao().registerFileName(destBinder, destEntity, fa.getFileItem().getName());
    		}
		// Rename the file in the repository
       	for(FileAttachment fa :atts) {    			
       	 		RepositoryUtil.move(fa.getRepositoryName(), binder, entity, 
				fa.getFileItem().getName(), destBinder, destEntity, 
				fa.getFileItem().getName());
   			ChangeLog changes = new ChangeLog(entity, ChangeLog.FILEMOVE);
			ChangeLogUtils.buildLog(changes, fa);
			getCoreDao().save(changes);
       	}
	}

	public void copyFiles(Binder binder, DefinableEntity entity, 
			Binder destBinder, DefinableEntity destEntity)
	throws UncheckedIOException, RepositoryServiceException {
		List<FileUploadItem> fuis = new ArrayList<FileUploadItem>();
    	Collection<FileAttachment> atts = entity.getFileAttachments();
    	FileUploadItem fui;
     	String name;
    	SimpleMultipartFile file;
    	for(FileAttachment fa :atts) {
    		name = fa.getName(); 
    		int type = FileUploadItem.TYPE_FILE;
    		if(Validator.isNull(name))
    			type = FileUploadItem.TYPE_ATTACHMENT;
 //   		try {
    			
    			// Preserve modification time of the source for the target
  	  			file = new DatedMultipartFile(fa.getFileItem().getName(),
    				readFile(binder, entity, fa), fa.getModification().getDate());
    			fui = new FileUploadItem(type, name, file, fa.getRepositoryName());
    			//register here so entire copy fails if any one file is an issue
  		   		if (destBinder.isLibrary() && !(destEntity instanceof Binder)) {
		   			getCoreDao().registerFileName(destBinder, destEntity, fa.getFileItem().getName());
		   			fui.setRegistered(true);
		   		}
    	   		fuis.add(fui);
  //     		} catch (TitleException tx) {
  //     			throw tx;
  //  		} catch (Exception ex) {
  //  			logger.error("Error copying file:" +  ex.getLocalizedMessage());
  //  		}
    	}
    	try {	
    		writeFiles(destBinder, destEntity, fuis, null);
    	}
    	finally {
	    	for(FileUploadItem f : fuis) {
	    		try {
	    			f.delete();
	    		}
	    		catch(IOException ignore) {}
	    	}
    	}
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

	public Map<String,Long> getChildrenFileNames(Binder binder) {
		// look for the specific binder id
    	// look only for attachments
    	Criteria crit = new Criteria()
    	    .add(conjunction()	
    			.add(eq(Constants.BINDER_ID_FIELD, binder.getId().toString()))
   				.add(eq(Constants.DOC_TYPE_FIELD,Constants.DOC_TYPE_ATTACHMENT))
     		);
		// We use search engine to get the list of file names in the specified folder.
		QueryBuilder qb = new QueryBuilder(true);
    	org.dom4j.Document qTree = crit.toQuery(); //save for debug
		SearchObject so = qb.buildQuery(qTree);   	
   	
    	// create Lucene query    	
    	Query soQuery = so.getQuery();
    	    	
    	if(logger.isDebugEnabled()) {
    		logger.debug("Query is: " + qTree.asXML());
    		logger.debug("Query is: " + soQuery.toString());
    	}
    	
    	LuceneReadSession luceneSession = getLuceneSessionFactory().openReadSession();
        
    	Hits hits = null;
        try {
	        hits = luceneSession.search(soQuery, null, 0, Integer.MAX_VALUE);
        }
        finally {
            luceneSession.close();
        }
    	
        Map<String,Long> result = new HashMap<String,Long>();
        int count = hits.length();
        org.apache.lucene.document.Document doc;
        String fileName;
        Long entryId;
        for(int i = 0; i < count; i++) {
        	doc = hits.doc(i);
        	fileName = doc.get(Constants.FILENAME_FIELD);
        	if(fileName != null) {
        		try {
	        		entryId = Long.valueOf(doc.get(Constants.DOCID_FIELD));
	        		result.put(fileName, entryId);
        		}
        		catch(Exception ignore) {}
        	}
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
	
	private void deletePrimaryFile(Binder binder, DefinableEntity entry,
			String relativeFilePath, String repositoryName, FilesErrors errors) {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);
		
		try {
			try {
				session.delete(binder, entry, relativeFilePath);
			}
			catch(Exception e) {
				logger.error("Error deleting primary file " + relativeFilePath, e);
				errors.addProblem(new FilesErrors.Problem
						(repositoryName, relativeFilePath, 
								FilesErrors.Problem.PROBLEM_DELETING_PRIMARY_FILE, e));
			}
		} finally {
			session.close();
		}		
	}
	
	private ChangeLog deleteFileInternal(Binder binder, DefinableEntity entry,
			FileAttachment fAtt, boolean deleteMirroredSource, 
			FilesErrors errors, boolean updateMetadata) {
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
			// We proceed despite of the problem hoping we can still delete it.
		}

		List<KeyValuePair> archiveURIs = new ArrayList<KeyValuePair>();
		String archiveStoreName = null;

		if(!ObjectKeys.FI_ADAPTER.equals(fAtt.getRepositoryName())) { // regular repository
			// If there is any problem with the repository itself or communication 
			// with the repository, the following call is expected to detect it
			// (indirectly) and throw an exception right here. In essence, it will 
			// prevent delete operation from proceeding when it really shouldn't. 
			// The idea is that, we want delete operation to be reasonably forgiving, 
			// since it is quite frustrating for people to unable to delete
			// something just because the operation encountered a non-critical error.
			// The most important thing about deletion is the post-condition. As long
			// as the post-condition is acceptable, deletion should proceed even 
			// when there are errors. However, there are obviously exceptions to the
			// tolerability, and the following call reveals one such condition - 
			// that is, connection or configuration related problems about the
			// repository. Such problems must be reported, remedied, and then the
			// user should be able to repeate the same operation, which is, in 
			// this case, a delete operation. 
			int fileInfo = RepositoryUtil.fileInfo(repositoryName, binder, entry, relativeFilePath);
			
			if(fileInfo != RepositorySession.NON_EXISTING_FILE) {
				// Archive the contents of the file. We archive all versions of the file.
				ArchiveStore archiveStore = RepositorySessionFactoryUtil.getArchiveStore(repositoryName);
				if(archiveStore != null) {
					archiveStoreName = archiveStore.getName();
					for(Iterator i = fAtt.getFileVersionsUnsorted().iterator(); i.hasNext();) {
						VersionAttachment v = (VersionAttachment) i.next();
						try {
							String archiveURI = archiveStore.write(binder, entry, v);
							
							if(archiveURI != null)
								archiveURIs.add(new KeyValuePair(String.valueOf(v.getVersionNumber()), archiveURI));
						}
						catch(Exception e) {
							logger.error("Error archiving file " + relativeFilePath + " version " + v.getVersionNumber(), e);
							errors.addProblem(new FilesErrors.Problem
									(repositoryName, relativeFilePath,
											FilesErrors.Problem.PROBLEM_ARCHIVING, e));
							// Well, this is not a good situation. The repository is reachable, 
							// the file exists in the repository, but for some reason, we could
							// not archive it. It is most likely that, for some reason, the 
							// particular version of the file does not exist in the repository, etc.
							// Since there is no other good alternative, we proceed, hoping that
							// we can at least archive "some" versions of the file. 
						}
					}
				}
				// Delete primary file
				deletePrimaryFile(binder, entry, relativeFilePath, repositoryName, errors);
			}
			else {
				// Since regular repository is supposed to be owned exclusively by Aspen,
				// this is not expected to occur. So we log it as warning.
				logger.warn("The file " + relativeFilePath + " does not exist in the repository " + repositoryName);
			}	
		}
		else if (deleteMirroredSource) { // mirrored repository & delete mirrored source
			// We do not archive files in mirrored repository.
			int fileInfo = RepositoryUtil.fileInfo(repositoryName, binder, entry, relativeFilePath);
			
			if(fileInfo != RepositorySession.NON_EXISTING_FILE) {
				// Delete primary file.
				deletePrimaryFile(binder, entry, relativeFilePath, repositoryName, errors);
			}
			else {
				// Since mirrored repository is accessible outside of Aspen as well, it
				// is conceivable that the file was removed externally just prior to
				// this code being executed. So we log it as info.
				logger.info("The file " + relativeFilePath + " does not exist in the repository " + repositoryName + " through driver " + binder.getResourceDriverName());				
			}
		}
		else { // mirrored repository & do not delete mirrored source
			// We do not archive files in mirrored repository. 
			// No primary file to delete.
		}
				
   		ChangeLog changeLog = new ChangeLog(entry, ChangeLog.FILEDELETE);
   		Element parent = ChangeLogUtils.buildLog(changeLog, fAtt);
   		if(archiveURIs.size() > 0) {
   			Element fileElem = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_FILEARCHIVE);
   			if(archiveStoreName != null)
   				fileElem.addAttribute(ObjectKeys.XTAG_FILE_ARCHIVE_STORE_NAME, archiveStoreName);
   			for(KeyValuePair pair : archiveURIs) {
   				Element verElem = fileElem.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_VERSIONARCHIVE);
   				verElem.addAttribute(ObjectKeys.XTAG_FILE_VERSION_NUMBER, pair.getKey()); 
   				verElem.addAttribute(ObjectKeys.XTAG_FILE_ARCHIVE_URI, pair.getValue());
   			}
   		}
   		
		//System.out.println(changeLog.getXmlNoHeader());

		if(updateMetadata)
			writeDeleteMetaDataTransactional(binder, entry, fAtt, changeLog);
		
		return changeLog;
	}
	
	private void writeDeleteMetaDataTransactional(final Binder binder, 
			final DefinableEntity entry, final FileAttachment fAtt,
			final ChangeLog changeLog)  {
		// Remove metadata and log change
	       getTransactionTemplate().execute(new TransactionCallback() {
	       	public Object doInTransaction(TransactionStatus status) {  
	       		getCoreDao().save(changeLog);
				entry.removeAttachment(fAtt);
				//file names on binders are not registered
				if (binder.isLibrary() && !binder.equals(entry)) getCoreDao().updateFileName(binder, entry, fAtt.getFileItem().getName(), null);
				// Do NOT reset the title of the entry even if the title came from the file 
				// being deleted and the parent folder required unique titles.
				// In other word, deleting a file from an entry returns its file name to the
				// namespace pool but not the entry's title. If the user needs to create a
				// new entry with the same title (as opposed to re-using the old entry), he
				// will have to manually delete the old entry before creating a new one
				// (related ICEcore issue number is #1803).
				/*
		        if ((entry.getEntryDef() != null)  && DefinitionUtils.isSourceItem(entry.getEntryDef().getDefinition(), fAtt.getName(), ObjectKeys.FIELD_ENTITY_TITLE)) {
		        	//if tracking unique titles, remove old title
		        	if ((entry.getParentBinder() != null) && entry.getParentBinder().isUniqueTitles()) getCoreDao().updateTitle(entry.getParentBinder(), entry, entry.getNormalTitle(), null);
		        	//check title for entries
		        	entry.setTitle("");			   			   
				}
				*/
			        
	            return null;
	       	}
	     });	
	}

	private void writeDeleteChangeLogTransactional(final List<ChangeLog> changeLogs) {
		// We want to start a transaction even when there is nothing to write
		// (ie, empty changeLogs), so that any pending updates that the caller
		// made up to this point can get recorded permanently.
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {  
                for(ChangeLog changeLog : changeLogs) {
                	getCoreDao().save(changeLog);
                }
            	return null;
        	}
        });	
	}
	
	private void moveFilterFailedFile(Binder binder, FileUploadItem fui) throws IOException {
		File filteringFailedDir = SPropsUtil.getFile("file.content.filter.failed.dir");
		if(!filteringFailedDir.exists())
			FileHelper.mkdirs(filteringFailedDir);
		File outFile = new File(filteringFailedDir, makeFileName(binder, fui));
		// The caller is responsible for ensuring that the fui is reentrant.
		InputStream is = fui.getInputStream();
		try {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(outFile));
			try {
				FileCopyUtils.copy(is, os);
				fui.delete();
			}
			finally {
				try {
					os.close();
				}
				catch(IOException ignore) {}
			}
		}
		finally {
			try {
				is.close();
			}
			catch(IOException ignore) {}
		}
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
        			
        			String contentId = fui.getContentId();
        			if (contentId != null && !"".equals(contentId)) {
	        			String description = entry.getDescription().getText();
	        			if (description != null && description.indexOf(contentId) > -1) {
	        				description = description.replaceAll("\"cid:[\\s]*" + Pattern.quote(contentId) + "\"", "\"{{attachmentUrl: " + fAtt.getFileItem().getName() + "}}\"");
	        				entry.getDescription().setText(description);
	        			}
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
                	//if tracking unique titles, remove old title and add new
                 	String oldTitle = entry.getNormalTitle();
           			entry.setTitle(title);
           		   	if ((entry.getParentBinder() != null) && entry.getParentBinder().isUniqueTitles()) getCoreDao().updateTitle(entry.getParentBinder(), entry, oldTitle, entry.getNormalTitle());
        		} 
           		//add file name so not null
            	if (Validator.isNull(entry.getTitle())) entry.setTitle(fAtt.getFileItem().getName());
            	//if file was referenced from a field, resolve markup now as part of this transaction
            	if (fui.isMarkup()) {
            		String fieldName = fui.getMarkupFieldName();
            		if (Validator.isNotNull(fieldName) && !fieldName.equals(ObjectKeys.FIELD_ENTITY_DESCRIPTION)) {
            			CustomAttribute attr = entry.getCustomAttribute(fieldName);
            			if (attr != null) {
            				Object val = attr.getValue();
            				if (val instanceof Description) {
            					WebHelper.scanDescriptionForAttachmentUrls((Description)val, entry);
            				}
            			}
            		} else {
    					WebHelper.scanDescriptionForAttachmentUrls(entry.getDescription(), entry);           			
            		}
            		
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
    	
    	SimpleProfiler sp = new SimpleProfiler("writeFileTransactional", false);
    	
    	/// Work Flow:
    	/// step1: write primary file
    	/// step2: update metadata in database
    	
		int type = fui.getType();
		if(type != FileUploadItem.TYPE_FILE && 
				type != FileUploadItem.TYPE_ATTACHMENT && 
				type != FileUploadItem.TYPE_TITLE) {
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
		FileAttachment fAtt = entry.getFileAttachment(relativeFilePath);
    	if ((fAtt != null) && !repositoryName.equals(fAtt.getRepositoryName())) {
			errors.addProblem(new FilesErrors.Problem
					(fAtt.getRepositoryName(), relativeFilePath, 
							FilesErrors.Problem.PROBLEM_FILE_EXISTS));
			return false;
    	}

		if(repositoryName.equals(ObjectKeys.FI_ADAPTER)) {
			if(!binder.isMirrored()) {
				errors.addProblem(new FilesErrors.Problem
						(repositoryName, relativeFilePath, 
								FilesErrors.Problem.PROBLEM_MIRRORED_FILE_IN_REGULAR_FOLDER, 
								new IllegalArgumentException("Binder [" + binder.getPathName() + "] is not a mirrored folder")));
				return false;				
			}
			else {
				List<FileAttachment> fas = entry.getFileAttachments(ObjectKeys.FI_ADAPTER); // should be at most 1 in size
	   			if(fas.size() > 1)
	   				logger.warn("Integrity error: Entry " + entry.getId() + " in binder [" + binder.getPathName() + "] mirrors multiple files");
				for(FileAttachment fa : fas) {
					if(!relativeFilePath.equals(fa.getFileItem().getName())) {
						errors.addProblem(new FilesErrors.Problem
								(repositoryName, relativeFilePath, 
										FilesErrors.Problem.PROBLEM_MIRRORED_FILE_MULTIPLE, 
										new IllegalArgumentException("The entry " + entry.getId() + 
												" already mirrors another file [" + fa.getFileItem().getName() + "]")));
						return false;					
					}
				}
			}
		}
		
    	boolean isNew = false;
    	
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, repositoryName);

    	try {
    		try {
	    		// Store primary file first, since we do not want to generate secondary
	    		// files unless we could successfully store the primary file first. 
	    		
	    		if(fAtt == null) { // New file for the entry
	    			sp.start("createFile");
	    			isNew = true;
	    			fAtt = createFile(session, binder, entry, fui);
	    			sp.stop("createFile");
	    		}
	    		else { // Existing file for the entry
	    			sp.start("writeExistingFile");
	    			writeExistingFile(session, binder, entry, fui);
	    			sp.stop("writeExistingFile");
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

	    	// Update metadata - We do this only after successfully writing
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
    		sp.start("writeFileMetadataTransactional");
	    	writeFileMetadataTransactional(binder, entry, fui, fAtt, isNew);
    		sp.stop("writeFileMetadataTransactional");
	    	
     	
        	sp.print();

	    	return true;
    	}
    	finally {
    		session.close();
    	}
    }
    
    private void writeExistingFile(RepositorySession session,
    		Binder binder, DefinableEntity entry, FileUploadItem fui)
		throws LockedByAnotherUserException, RepositoryServiceException, IOException {
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
    		versionName = updateVersionedFile(session, binder, entry, fui, lock);
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
    		versionName = createVersionedFile(session, binder, entry, fui);
    	}
    	else {
    		throw new InternalException();
    	}
    	
    	if(lock != null && versionName == null) {
    		// A lock existed at the time of writing the content and the writing
    		// didn't create a new version as of yet. This means that we have a
    		// pending change that we need to commit/checkin when we release the
    		// lock later. So mark the lock as dirty.
    		lock.setDirty(Boolean.TRUE);
    	}
    	
		//if we are adding a new version of an existing attachment to 
		//a uniqueName item, set flag - (will already be set if originally added
		//through a unique element.  In other works, once unique always unique
    	Long fileSize = null;
    	if(versionName != null)
    		fileSize = Long.valueOf(session.getContentLengthVersioned(binder, entry, relativeFilePath, versionName));
		updateFileAttachment(fAtt, user, versionName, fileSize, fui.getModDate(), fui.getModifierName());
    }

    private void updateFileAttachment(FileAttachment fAtt, 
			UserPrincipal user, String versionName, Long contentLength,
			Date modDate, String modName) {
    	HistoryStamp now = new HistoryStamp(user);
    	HistoryStamp mod;
       	if(modDate != null) {
			if (Validator.isNotNull(modName)) {
				user = getProfileDao().findUserByName(modName, RequestContextHolder.getRequestContext().getZoneName());
			}
    		mod = new HistoryStamp(user, modDate);
    	} else
    		mod = now;
    	
		fAtt.setModification(mod);
		
		FileItem fItem = fAtt.getFileItem();

		if(contentLength != null)
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
    
    private String updateVersionedFile(RepositorySession session, Binder binder, 
    		DefinableEntity entity, FileUploadItem fui, FileAttachment.FileLock lock) 
    throws IOException {
    	String relativeFilePath = fui.getOriginalFilename();
    	String versionName = null;
		if(fui.isSynchToRepository()) {
    		// Attempt to check out the file. If the file was already checked out
    		// this is noop. So no harm. 
    		session.checkout(binder, entity, relativeFilePath);
    		// Update the file content
    		InputStream in = fui.getInputStream();
    		try {
    			updateWithInputData(session, binder, entity, relativeFilePath, in);
    		}
    		finally {
    			try {
    				in.close();
    			}
    			catch(IOException e) {}
    		}
    		if(lock == null) {
    			// This update request is being made without the user's prior 
    			// obtaining lock. Since there's no lock to associate the 
    			// checkout with, we must checkin the file here. 
    			versionName = session.checkin(binder, entity, relativeFilePath);
    		}
		}
		else {
			versionName = RepositoryUtil.generateRandomVersionName();
		}

		return versionName;
    }
    
    private String createVersionedFile(RepositorySession session, Binder binder, 
    		DefinableEntity entity, FileUploadItem fui) 
    throws IOException {
    	String versionName = null;
    	
		if(fui.isSynchToRepository()) {
			InputStream in = fui.getInputStream();
			try {
				versionName = createVersionedWithInputData(session, binder, entity,
						fui.getOriginalFilename(), fui.isSynchToRepository(), in);
			}
			finally {
				// Make sure to close the stream even when the above call fails. 
				// In normal case, we will be closing it twice, but that should be ok.
				try {
					in.close();
				}
				catch(IOException e) {}
			}
		}
		else {
			versionName = RepositoryUtil.generateRandomVersionName();
		}
		return versionName;
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
			Binder binder, DefinableEntity entry, FileUploadItem fui) 
		throws RepositoryServiceException, IOException {	
		// Since we are creating a new file, file locking doesn't concern us.
		
		FileAttachment fAtt = createFileAttachment(entry, fui);
		
		String versionName = createVersionedFile(session, binder, entry, fui);
						
		long fileSize = session.getContentLengthVersioned(binder, entry, fui.getOriginalFilename(), versionName);
		
		fAtt.getFileItem().setLength(fileSize);

		createVersionAttachment(fAtt, versionName);

		return fAtt;
	}
	
	private String createVersionedWithInputData(RepositorySession session,
			Binder binder, DefinableEntity entry, String relativeFilePath, 
			boolean synchToRepository, Object inputData)
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
		if(fui.getModDate() != null) { // mod date specified
			String name = fui.getModifierName();
			if (Validator.isNotNull(name)) {
				user = getProfileDao().findUserByName(name, RequestContextHolder.getRequestContext().getZoneName());
			}
			mod = new HistoryStamp(user, fui.getModDate());
		} else // set mod date equal to creation date
			mod = fAtt.getCreation();
		fAtt.setModification(mod);
    	fAtt.setRepositoryName(fui.getRepositoryName());
    	//set attribute name - null if not not named
    	fAtt.setName(fui.getName());
    	FileItem fItem = new FileItem();
    	fItem.setName(relativeFilePath);
    	// Optimization: Do NOT try to get the file size directly from the 
    	// FileUploadItem object. In the case where the content is available
    	// only as a InputStream (as opposed to a File), getting the size 
    	// here causes the content to be stored in a temporary file on disk
    	// just to be able to figure out the size. This additional copy is,
    	// not to mention, inefficient. Therefore, we will get the size 
    	// information from the repository after the content has been actually
    	// written to the repository.
    	
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
		vAtt.setVersionNumber(1);
		vAtt.setVersionName(versionName);
		vAtt.setRepositoryName(fAtt.getRepositoryName());
		fAtt.addFileVersion(vAtt);
	}
	
	
    private boolean closeLock(Binder binder, DefinableEntity entity, 
    		FileAttachment fa, boolean commit) throws RepositoryServiceException,
    		UncheckedIOException {
    	boolean metadataDirty = false;
    	
    	FileAttachment.FileLock lock = fa.getFileLock();
    	
    	if (lock != null) {
			if (commit) { 
				// Commit pending changes if any. We don't care whether the 
				// lock is currently effective or expired.
				commitPendingChanges(binder, entity, fa, lock);
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
		Collection<FileAttachment> fAtts = entity.getFileAttachments();
    	for(FileAttachment fa :fAtts) {
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
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, fa.getRepositoryName());

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
					commitPendingChanges(session, binder, entity, fa, lock); 
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
    		FileAttachment fa, FileAttachment.FileLock lock)
    	throws RepositoryServiceException, UncheckedIOException {
		RepositorySession session = RepositorySessionFactoryUtil.openSession(binder, fa.getRepositoryName());

		try {
			return commitPendingChanges(session, binder, entity, fa,
					lock);
		}
		finally {
			session.close();
		}			
    }
    
    private boolean commitPendingChanges(RepositorySession session,
    		Binder binder, DefinableEntity entity, FileAttachment fa, 
    		FileAttachment.FileLock lock)
    	throws RepositoryServiceException, UncheckedIOException {
    	String relativeFilePath = fa.getFileItem().getName();
		
		boolean metadataDirty = false; 
		boolean tryCheckin = false;
		
		if(session.getFactory().supportSmartCheckin()) {			
			// Attempt to check in. If the file was previously checked out (and
			// only if any change has made since checkout??), this will create 
			// a new version and return the name of the new version. If not,
			// it will return the name of the latest existing version.
			
			// Let the repository make the decision as to whether we should 
			// create a new version or not. Although client has enough info
			// to make this decision, I prefer delegating it to the repository
			// to increase the chance of being in the right state after the
			// call is finished (ie, post-condition is better met this way
			// in an non-ideal world).
			tryCheckin = true;
		}
		else {
			// Let the client make the decision.
			if(Boolean.TRUE.equals(lock.isDirty()))
				tryCheckin = true;
		}
		
		if(tryCheckin) {
			String versionName = session.checkin(binder, entity, relativeFilePath);
			if(versionName != null) {
				// The repository says it created a new version. Although repository
				// wouldn't lie, we still need to check to make sure that we do not
				// end up in a situation where two metadata rows point to the same
				// version in the repository based on the reality that the operations
				// on the repository and the metadata are not performed in a truly
				// atomic manner due to lack of such transactional support (Paranoic?)
				VersionAttachment va = fa.findFileVersion(versionName);
				if(va == null) {
					// The checkin method above returned a version name that we don't
					// know about, which means that it must have created a new version
					// of the file.
					// Note: Never compare the returned version name against the highest
					// version alone in our metadata. It is not guaranteed to work always.
					// Instead, we must check the name against all versions. This is due
					// to the possibility (extremely unlikely but nevertheless theoretically
					// possible) of version out-of-orderness between the metadata side
					// and the repository side. For example, it is possible that V1 in 
					// metadata points to v2 in the repository, while V2 in metadata
					// points to v1 in the repository. This can happen when you have two
					// threads (or two nodes in a cluster) try to create a new version
					// of the same file exactly at the same time. Because the updates
					// to the db metadata and to the repository metadata are not 
					// transactionally atomic, two concurrent requests can in theory
					// result in such intertwined scenario. Consequently the notion of 
					// highest version may not necessarily agree between the application
					// side and the repository side. Therefore it is important to check
					// the returned version name against all versions.
					Long contentLength = Long.valueOf(session.getContentLengthVersioned(binder, entity, 
							relativeFilePath, versionName));
					updateFileAttachment(fa, lock.getOwner(), versionName, contentLength, null, null);
					metadataDirty = true;
				}  
			}
		}
		
		lock.setDirty(Boolean.FALSE);
		
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
