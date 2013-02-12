/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.module.folder.impl;
import static org.kablink.util.search.Restrictions.between;
import static org.kablink.util.search.Restrictions.eq;
import static org.kablink.util.search.Restrictions.in;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.index.Term;
import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.BinderQuotaException;
import org.kablink.teaming.DataQuotaException;
import org.kablink.teaming.FileSizeLimitException;
import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.comparator.BinderComparator;
import org.kablink.teaming.comparator.EntryComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.ObjectControls;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.AverageRating;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HKey;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.Rating;
import org.kablink.teaming.domain.ReservedByAnotherUserException;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Visits;
import org.kablink.teaming.domain.WorkflowControlledEntry;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.jobs.DefaultFolderNotification;
import org.kablink.teaming.jobs.DefaultMirroredFolderSynchronization;
import org.kablink.teaming.jobs.FolderDelete;
import org.kablink.teaming.jobs.FolderNotification;
import org.kablink.teaming.jobs.MirroredFolderSynchronization;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.jobs.ZoneSchedule;
import org.kablink.teaming.lucene.Hits;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FileLockInfo;
import org.kablink.teaming.module.folder.FilesLockedByOtherUsersException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.folder.processor.FolderCoreProcessor;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.rss.RssModule;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.module.workflow.WorkflowModule;
import org.kablink.teaming.module.workflow.WorkflowProcessUtils;
import org.kablink.teaming.module.workflow.WorkflowUtils;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.runasync.RunAsyncManager;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.search.IndexSynchronizationManager;
import org.kablink.teaming.search.LuceneReadSession;
import org.kablink.teaming.search.QueryBuilder;
import org.kablink.teaming.search.SearchObject;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.OperationAccessControlException;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SimpleMultipartFile;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.ExportHelper;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Jong Kim
 */
@SuppressWarnings("unchecked")
public abstract class AbstractFolderModule extends CommonDependencyInjection 
		implements FolderModule, AbstractFolderModuleMBean, ZoneSchedule {
	protected String[] ratingAttrs = new String[]{"id.entityId", "id.entityType"};
	protected String[] entryTypes = {Constants.ENTRY_TYPE_ENTRY};
    protected DefinitionModule definitionModule;
    protected FileModule fileModule;
    protected BinderModule binderModule;
    
    protected AtomicInteger aeCount = new AtomicInteger();
    protected AtomicInteger meCount = new AtomicInteger();
    protected AtomicInteger deCount = new AtomicInteger();
    protected AtomicInteger arCount = new AtomicInteger();


	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	 
	/**
	 * 
	 * Setup by spring
	 * @param definitionModule
	 */
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
	protected FileModule getFileModule() {
		return fileModule;
	}
	//set by spring
	public void setFileModule(FileModule fileModule) {
		this.fileModule = fileModule;
	}
	
	protected BinderModule getBinderModule() {
		return binderModule;
	}
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
	
	protected AdminModule getAdminModule() {
		// Can't use IoC due to circular dependency
		return (AdminModule) SpringContextUtil.getBean("adminModule");
	}
	
	protected RssModule getRssModule() {
		// Can't use IoC due to circular dependency
		return (RssModule) SpringContextUtil.getBean("rssModule");
	}
	
	private TransactionTemplate transactionTemplate;
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	private RunAsyncManager runAsyncManager;
	protected RunAsyncManager getRunAsyncManager() {
		return runAsyncManager;
	}
	public void setRunAsyncManager(RunAsyncManager runAsyncManager) {
		this.runAsyncManager = runAsyncManager;
	}
	
 	protected FolderDelete getDeleteProcessor(Workspace zone) {
 	   String jobClass = SZoneConfig.getString(zone.getName(), "folderConfiguration/property[@name='" + FolderDelete.DELETE_JOB + "']");
 	   if (Validator.isNotNull(jobClass)) {
		   try {
			   return  (FolderDelete)ReflectHelper.getInstance(jobClass);
		   } catch (Exception ex) {
			   logger.error("Cannot instantiate FolderDelete custom class", ex);
		   }
   		}
 	   String className = SPropsUtil.getString("job.folder.delete.class", "org.kablink.teaming.jobs.DefaultFolderDelete");
 	   return (FolderDelete)ReflectHelper.getInstance(className);
  	}

	public ScheduleInfo getNotificationSchedule(Long zoneId, Long folderId) {
  		return getNotificationScheduleObject().getScheduleInfo(zoneId, folderId);
	}
	
	public void setNotificationSchedule(ScheduleInfo config, Long folderId) {
    	checkAccess(getFolder(folderId),FolderOperation.manageEmail);
        //data is stored with job
        getNotificationScheduleObject().setScheduleInfo(config, folderId);
    }    

	private FolderNotification getNotificationScheduleObject() {
		return new DefaultFolderNotification();
    }    

	//called on zone delete
	public void stopScheduledJobs(Workspace zone) {
		FolderDelete job = getDeleteProcessor(zone);
		job.remove(zone.getId());
	}
 	//called on zone startup
     public void startScheduledJobs(Workspace zone) {
    	if (zone.isDeleted()) return;
    	//make sure a delete job is scheduled for the zone
		FolderDelete job = getDeleteProcessor(zone);
		String hrsString = (String)SZoneConfig.getString(zone.getName(), "folderConfiguration/property[@name='" + FolderDelete.DELETE_HOURS + "']");
    	int hours = 2;
    	try {
    		hours = Integer.parseInt(hrsString);
    	} catch (Exception ex) {};
    	job.schedule(zone.getId(), hours*60*60);
   }

	public boolean testAccess(Folder folder, FolderOperation operation) {
		try {
			checkAccess(folder, operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		} catch(NotSupportedException e) {
			return false;
		}
	}
	public void checkAccess(Folder folder, FolderOperation operation) throws AccessControlException {
		User user = RequestContextHolder.getRequestContext().getUser();
		if (user.isShared()) {
			//See if the user is only allowed "read only" rights
			ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
			if (zoneConfig.getAuthenticationConfig().isAnonymousReadOnly()) {
				//This is the guest account and it is read only. Only allow checks for read rights
				throw new AccessControlException(operation.toString(), new Object[] {});
			}
		}
		switch (operation) {
			case addEntry: 
				getAccessControlManager().checkOperation(folder, WorkAreaOperation.CREATE_ENTRIES);
				break;
			case fullSynchronize:
				getAccessControlManager().checkOperation(folder, WorkAreaOperation.BINDER_ADMINISTRATION);
				break;
			case manageEmail:
			case scheduleSynchronization:
			case changeEntryTimestamps:
				getAccessControlManager().checkOperation(folder, WorkAreaOperation.BINDER_ADMINISTRATION);
				break;				
			case entryOwnerSetAcl:
				getAccessControlManager().checkOperation(folder, WorkAreaOperation.CREATOR_CREATE_ENTRY_ACLS);
				break;
			case setEntryAcl:
				getAccessControlManager().checkOperation(folder, WorkAreaOperation.CREATE_ENTRY_ACLS);
				break;
			case report:
				getAccessControlManager().checkOperation(folder,
						WorkAreaOperation.GENERATE_REPORTS);
				break;
			case addFile:
				// true iff (the user has "addEntry" right to the folder) AND (the folder is a regular folder OR the folder is a mirrored folder with read/write driver)
				this.checkAccess(folder, FolderOperation.addEntry);
				if(folder.isMirrored() && folder.getResourceDriver() != null && folder.getResourceDriver().isReadonly()) {					
					throw new OperationAccessControlException(RequestContextHolder.getRequestContext().getUser().getName(),
							operation.name(), folder.toString());
				}
				break;
			default:
				throw new AccessControlException(operation.toString(), new Object[] {});
				
		}
	}
	public boolean testAccess(FolderEntry entry, FolderOperation operation) {
		try {
			checkAccess(entry, operation);
			return true;
		} catch (AccessControlException ac) {
			if ((FolderOperation.preDeleteEntry == operation) ||
			    (FolderOperation.restoreEntry   == operation)) {
				FolderEntry topEntry = entry.getTopEntry();
				if (null != topEntry) {
					return testAccess(topEntry, operation);
				}
			}
			return false;
		} catch(NotSupportedException e) {
			return false;
		}
	}
	public void checkAccess(FolderEntry entry, FolderOperation operation) throws AccessControlException {
		User user = RequestContextHolder.getRequestContext().getUser();
		if (user.isShared()) {
			//See if the user is only allowed "read only" rights
			ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
			if (zoneConfig.getAuthenticationConfig().isAnonymousReadOnly()) {
				//This is the guest account and it is read only. Only allow checks for read rights
				switch (operation) {
					case readEntry:
						//Allow this right to be checked. All other rights will fail
						break;
					default:
						throw new AccessControlException(operation.toString(), new Object[] {});
				}
			}
		}
		switch (operation) {
			case readEntry:
			case copyEntry:
				AccessUtils.readCheck(entry);   
				break;
			case modifyEntry:
			case addEntryWorkflow:
			case deleteEntryWorkflow:
			case reserveEntry:
			case changeEntryType:
				AccessUtils.operationCheck(entry, WorkAreaOperation.MODIFY_ENTRIES);   
				break;
			case moveEntry:
	    		if(entry.isAclExternallyControlled()) { // Net Folder entries
					getAccessControlManager().checkOperation(entry.getParentFolder(), WorkAreaOperation.DELETE_ENTRIES);
	    		}
	    		else { // Regular Vibe entries
					AccessUtils.operationCheck(entry, WorkAreaOperation.MODIFY_ENTRIES);   	    			
	    		}
	    		break;
			case modifyEntryFields:
				AccessUtils.modifyFieldCheck(entry);   
				break;
			case restoreEntry:
			case preDeleteEntry:
			case deleteEntry:
	    		if(entry.isAclExternallyControlled()) { // Net Folder entries
	    			// Do the checking in a way that is consistent with the file system semantic.
					getAccessControlManager().checkOperation(entry.getParentFolder(), WorkAreaOperation.DELETE_ENTRIES);
	    		}
	    		else { // Regular Vibe entries
	    			AccessUtils.operationCheck(entry, WorkAreaOperation.DELETE_ENTRIES);
	    		}
				break;
			case overrideReserveEntry:
				AccessUtils.overrideReserveEntryCheck(entry);
				break;
			case addReply:
				AccessUtils.operationCheck(entry, WorkAreaOperation.ADD_REPLIES);
		    	break;				
			case manageTag:
				AccessUtils.operationCheck(entry, WorkAreaOperation.ADD_COMMUNITY_TAGS);
				break;
			case report:
				AccessUtils.operationCheck(entry, WorkAreaOperation.GENERATE_REPORTS);
				break;
			case updateModificationStamp:
				// Initially, the timestamp on an entry can be updated
				// by anybody at any time.  This allows the activity
				// streams to work correctly when a user posts a reply
				// so that the top entry gets it's timestamp modified
				// without regard to access rights on that top entry. 
				break;
			case modifyFile: 
			{
				// true iff (the user has "modifyEntry" right to the entry) AND (the entry's parent folder is a regular folder OR the entry's parent folder is a mirrored folder with read/write driver)
				this.checkAccess(entry, FolderOperation.modifyEntry);
				Folder folder = entry.getParentFolder();
				if(folder.isMirrored() && folder.getResourceDriver() != null && folder.getResourceDriver().isReadonly()) {					
					throw new OperationAccessControlException(RequestContextHolder.getRequestContext().getUser().getName(),
							operation.name(), entry.toString());
				}
				break;
			}
			case deleteFile:
			{
				// true iff (the user has "deleteEntry" right to the entry) AND (the entry's parent folder is a regular folder OR the entry's parent folder is a mirrored folder with read/write driver)
				this.checkAccess(entry, FolderOperation.deleteEntry);
				Folder folder = entry.getParentFolder();
				if(folder.isMirrored() && folder.getResourceDriver() != null && folder.getResourceDriver().isReadonly()) {					
					throw new OperationAccessControlException(RequestContextHolder.getRequestContext().getUser().getName(),
							operation.name(), entry.toString());
				}
				break;
			}
			case allowSharing:
			{
				AccessUtils.operationCheck(entry, WorkAreaOperation.ALLOW_SHARING_INTERNAL);   
				break;
			}
			case allowSharingExternal:
			{
				AccessUtils.operationCheck(entry, WorkAreaOperation.ALLOW_SHARING_EXTERNAL);   
				break;
			}
			case allowSharingPublic:
			{
				AccessUtils.operationCheck(entry, WorkAreaOperation.ALLOW_SHARING_PUBLIC);   
				break;
			}
			case allowSharingForward:
			{
				AccessUtils.operationCheck(entry, WorkAreaOperation.ALLOW_SHARING_FORWARD);   
				break;
			}
			case allowAccessNetFolder:
			{
				AccessUtils.operationCheck(entry, WorkAreaOperation.ALLOW_ACCESS_NET_FOLDER);   
				break;
			}
			case changeACL:
			{
				AccessUtils.operationCheck(entry, WorkAreaOperation.CHANGE_ACCESS_CONTROL);   
				break;
			}
			default:
				throw new NotSupportedException(operation.toString(), "checkAccess");
					
		}

	}
    
	protected Folder loadFolder(Long folderId)  {
        Folder folder = getFolderDao().loadFolder(folderId, RequestContextHolder.getRequestContext().getZoneId());
		if (folder.isDeleted()) throw new NoBinderByTheIdException(folderId);
		return folder;

	}
	protected Folder loadFolderStrict(Long folderId)  {
		Folder folder = loadFolder(folderId);
		if (folder.isPreDeleted()) throw new NoBinderByTheIdException(folderId);
		return folder;
	}
	protected FolderEntry loadEntry(Long folderId, Long entryId) {
		//folderId may be null
        FolderEntry entry = getFolderDao().loadFolderEntry(folderId, entryId, RequestContextHolder.getRequestContext().getZoneId());             
		if (entry.isDeleted() || entry.getParentBinder().isDeleted()) throw new NoFolderEntryByTheIdException(entryId);
		return entry;
	}
	protected FolderEntry loadEntryStrict(Long folderId, Long entryId) {
		FolderEntry entry = loadEntry(folderId, entryId);
		if (entry.isPreDeleted() || entry.getParentFolder().isPreDeleted()) throw new NoFolderEntryByTheIdException(entryId);
		return entry;		
	}          
	    
	protected FolderCoreProcessor loadProcessor(Folder folder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // org.kablink.teaming.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.

		return (FolderCoreProcessor)getProcessorManager().getProcessor(folder, folder.getProcessorKey(FolderCoreProcessor.PROCESSOR_KEY));	
	}

	public Folder getFolder(Long folderId)
		throws NoFolderByTheIdException, AccessControlException {
		Folder folder = loadFolder(folderId);
	
		// Check if the user has "read" access to the folder.
		try {
			getBinderModule().checkAccess(folder, BinderOperation.readEntries);
		} catch(AccessControlException ace) {
			//Can't read it, so try seeing if the folder title is readable
			try {
				getBinderModule().checkAccess(folder, BinderOperation.viewBinderTitle);
			} catch(AccessControlException ace2) {
				throw ace;
			}
		}
		return folder;        
	}
	
    public Folder getFolderWithoutAccessCheck(Long folderId) throws NoFolderByTheIdException {
    	return loadFolder(folderId);
    }

	public SortedSet<Folder> getFolders(Collection<Long> folderIds) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new BinderComparator(user.getLocale(), BinderComparator.SortByField.title);
       	TreeSet<Folder> result = new TreeSet<Folder>(c);
		for (Long id:folderIds) {
			try {//access check done by getFolder
				//assume most folders are cached
				result.add(getFolder(id));
			} catch (NoFolderByTheIdException ex) {
			} catch (AccessControlException ax) {
			}
			
		}
		return result;
	}
 
    //no transaction by default
    public FolderEntry addEntry(Long folderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems, Map options) throws AccessControlException, WriteFilesException, WriteEntryDataException {
    	long begin = System.nanoTime();
    	aeCount.incrementAndGet();

        Folder folder = loadFolderStrict(folderId);
        checkAccess(folder, FolderOperation.addEntry);
		if (options != null && (options.containsKey(ObjectKeys.INPUT_OPTION_CREATION_DATE) || 
				options.containsKey(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE)))
			checkAccess(folder, FolderOperation.changeEntryTimestamps);
        
        FolderCoreProcessor processor = loadProcessor(folder);
       
        
        Definition def = null;
        if (!Validator.isNull(definitionId)) { 
        	def = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
        } else {
        	def = folder.getDefaultEntryDef();
        }
        
        FolderEntry entry = (FolderEntry) processor.addEntry(folder, def, FolderEntry.class, inputData, fileItems, options);
        
		updateModificationTime(folder, options);
        
        end(begin, "addEntry");
        return entry;
    }
    //no transaction    
	public FolderEntry addReply(Long folderId, Long parentId, String definitionId, 
    		InputDataAccessor inputData, Map fileItems, Map options) 
			throws AccessControlException, WriteFilesException, WriteEntryDataException {
		long begin = System.nanoTime();
    	arCount.incrementAndGet();
        //load parent entry
        FolderEntry entry = loadEntry(folderId, parentId);    	
        checkAccess(entry, FolderOperation.addReply);
        Folder folder = entry.getParentFolder();
		if (options != null && (options.containsKey(ObjectKeys.INPUT_OPTION_CREATION_DATE) || 
				options.containsKey(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE)))
			checkAccess(folder, FolderOperation.changeEntryTimestamps);
        FolderCoreProcessor processor = loadProcessor(folder);

        if(Validator.isNull(definitionId)) {
			Document defDoc = entry.getEntryDefDoc();
			List replyStyles = DefinitionUtils.getPropertyValueList(defDoc.getRootElement(), "replyStyle");
			if (!replyStyles.isEmpty()) {
				definitionId = (String)replyStyles.get(0);
			}
        }

        Definition def = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
        FolderEntry reply = processor.addReply(entry, def, inputData, fileItems, options);
        end(begin, "addReply");
        return reply;
    }
    //no transaction    
	public void addVote(Long folderId, Long entryId, InputDataAccessor inputData, Map options) throws AccessControlException {
	   	meCount.incrementAndGet();
        FolderEntry entry = loadEntry(folderId, entryId);   	
        checkAccess(entry, FolderOperation.addReply);
        Folder folder = entry.getParentFolder();
		if (options != null && (options.containsKey(ObjectKeys.INPUT_OPTION_CREATION_DATE) || 
				options.containsKey(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE)))
			checkAccess(folder, FolderOperation.changeEntryTimestamps);
        FolderCoreProcessor processor = loadProcessor(folder);
        User user = RequestContextHolder.getRequestContext().getUser();
        HistoryStamp reservation = entry.getReservation();
        if(reservation != null && !reservation.getPrincipal().equals(user))
        	throw new ReservedByAnotherUserException(entry);
 
 		try {
			processor.modifyEntry(folder, entry, inputData, null, null, null, options);
    	} catch (WriteFilesException ex) {
    	    //should never happen   
    	} catch (WriteEntryDataException ex) {
    	    //should never happen   
    	}
	}
 

    //no transaction    
    public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData, 
    		Map fileItems, Collection<String> deleteAttachments, Map<FileAttachment,String> fileRenamesTo, Map options) 
    throws AccessControlException, WriteFilesException, WriteEntryDataException, ReservedByAnotherUserException {
    	long begin = System.nanoTime();
    	meCount.incrementAndGet();
        FolderEntry entry = loadEntryStrict(folderId, entryId);   	
        if(entry.isAclExternallyControlled() && 
        		inputData.exists("title") &&
        		!inputData.getSingleValue("title").equals(entry.getTitle())) { 
        	// This is renaming of a Net Folder entry, which means that the user is attempting to rename a file.
			// Do the checking in a way that is consistent with the file system semantic.
        	// Renaming a file (a -> b) is like deleting a file (a) and then adding another with a different name
        	// (b) when looking at it from the directory membership point of view. So, we require the user to
        	// have CREATE_ENTRIES right on the parent folder to allow for this operation.
			getAccessControlManager().checkOperation(entry.getParentFolder(), WorkAreaOperation.CREATE_ENTRIES);
        }
        else {
			try {
				checkAccess(entry, FolderOperation.modifyEntry);
			} catch (AccessControlException e) {
				checkAccess(entry, FolderOperation.modifyEntryFields);
				inputData.setFieldsOnly(true);
			}
        }
        Folder folder = entry.getParentFolder();
		if (options != null && (options.containsKey(ObjectKeys.INPUT_OPTION_CREATION_DATE) || 
				options.containsKey(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE)))
			checkAccess(folder, FolderOperation.changeEntryTimestamps);
		FolderCoreProcessor processor=loadProcessor(folder);
        User user = RequestContextHolder.getRequestContext().getUser();
        HistoryStamp reservation = entry.getReservation();
        if(reservation != null && !reservation.getPrincipal().equals(user))
        	throw new ReservedByAnotherUserException(entry);
        
    	Set<Attachment> delAtts = new HashSet<Attachment>();
    	if (deleteAttachments != null) {
    		for (String id: deleteAttachments) {
   				Attachment a = entry.getAttachment(id);
   				if (a != null) delAtts.add(a);
    		}
    	}
    	processor.modifyEntry(folder, entry, inputData, fileItems, delAtts, fileRenamesTo, options);
        end(begin, "modifyEntry");
    }   
    
    //no transaction
    public void modifyEntry(Long folderId, Long entryId, String fileDataItemName, String fileName, InputStream content, Map options)
	throws AccessControlException, WriteFilesException, WriteEntryDataException, ReservedByAnotherUserException {
    	MultipartFile mf = new SimpleMultipartFile(fileName, content);
    	Map<String, MultipartFile> fileItems = new HashMap<String, MultipartFile>();
    	if(fileDataItemName == null)
    		fileDataItemName = ObjectKeys.FILES_FROM_APPLET_FOR_BINDER + "1";
    	fileItems.put(fileDataItemName, mf);
    	modifyEntry(folderId, entryId, new EmptyInputData(), fileItems, null, null, options);
    }


    public Map getEntries(Long folderId, Map searchOptions) {
        Folder folder = loadFolder(folderId);
        //search query does access checks
        return loadProcessor(folder).getBinderEntries(folder, entryTypes, searchOptions);

    }
    
    public void getEntryPrincipals(List entries) {
	    SearchUtils.extendPrincipalsInfo(entries, getProfileDao(), Constants.CREATORID_FIELD);
    }
    
    
    public Map getFullEntries(Long folderId, Map searchOptions) {
    	//search query does access checks
        Map result =  getEntries(folderId, searchOptions);
        //now load the full database object
        List childEntries = (List)result.get(ObjectKeys.SEARCH_ENTRIES);
        ArrayList ids = new ArrayList();
        for (int i=0; i<childEntries.size();) {
        	Map searchEntry = (Map)childEntries.get(i);
        	String docId = (String)searchEntry.get(Constants.DOCID_FIELD);
        	try {
        		Long id = Long.valueOf(docId);
        		ids.add(id);
        		++i;
        	} catch (Exception ex) {
        		childEntries.remove(i);
        	}
        }
        List preLoads = new ArrayList();
        preLoads.add("attachments");
        List entries = getCoreDao().loadObjects(ids, FolderEntry.class, null, preLoads);
        //return them in the same order
        List fullEntries = new ArrayList(entries.size());
        for (int i=0; i<childEntries.size(); ++i) {
        	Map searchEntry = (Map)childEntries.get(i);
        	String docId = (String)searchEntry.get(Constants.DOCID_FIELD);
       		Long id = Long.valueOf(docId);
       		for (int j=0; j<entries.size(); ++j) {
       			FolderEntry fe = (FolderEntry)entries.get(j);
       			if (id.equals(fe.getId())) {
       				fullEntries.add(fe);
       				entries.remove(j);
       				break;
       			}
       		}
        }
        	
        result.put(ObjectKeys.FULL_ENTRIES, fullEntries);
        //bulk load tags
        List<Tag> tags = getFolderDao().loadEntryTags(RequestContextHolder.getRequestContext().getUser().getEntityIdentifier(), ids);
        Map publicTags = new HashMap();
        Map privateTags = new HashMap();
        for (Tag t: tags) {
        	Long id = t.getEntityIdentifier().getEntityId();
        	List p;
        	if (t.isPublic()) {
        		p = (List)publicTags.get(id);
        		if (p == null) {
        			p = new ArrayList();
        			publicTags.put(id, p);
        		}
        	} else {
           		p = (List)privateTags.get(id);
        		if (p == null) {
        			p = new ArrayList();
        			privateTags.put(id, p);
        		}
        	}
        	//tags are returned in name order, remove duplicates
        	if (p.size() != 0) {
        		Tag exist = (Tag)p.get(p.size()-1);
        		if (!exist.getName().equals(t.getName())) p.add(t);
        	} else p.add(t);       		
        	        	
        }
        
        result.put(ObjectKeys.COMMUNITY_ENTITY_TAGS, publicTags);
        result.put(ObjectKeys.PERSONAL_ENTITY_TAGS, privateTags);
        return result;
    }

    public Map<Folder, Long> getUnseenCounts(Collection<Long> folderIds) {
    	//search engine will do acl checks
        User user = RequestContextHolder.getRequestContext().getUser();
        SeenMap seenMap = getProfileDao().loadSeenMap(user.getId());
        Map<Folder, Long> results = new HashMap();
        Set<Folder> folders = new HashSet();
        for (Long id:folderIds) {
        	try {
        		folders.add(loadFolder(id));
        	} catch (NoFolderByTheIdException nf) {} 
        }
        if (folders.size() > 0) {
	        Hits hits = getRecentEntries(folders);
	        if (hits != null) {
	        	Map<String, Counter> unseenCounts = new HashMap();
		        Date modifyDate = new Date();
		        for (int i = 0; i < hits.length(); i++) {
					String folderIdString = hits.doc(i).getFieldable(Constants.BINDER_ID_FIELD).stringValue();
					String entryIdString = hits.doc(i).getFieldable(Constants.DOCID_FIELD).stringValue();
					Long entryId = null;
					if (entryIdString != null && !entryIdString.equals("")) {
						entryId = new Long(entryIdString);
					}
					try {
						modifyDate = DateTools.stringToDate(hits.doc(i).getFieldable(Constants.LASTACTIVITY_FIELD).stringValue());
					} catch (ParseException pe) {} // no need to do anything
					Counter cnt = unseenCounts.get(folderIdString);
					if (cnt == null) {
						cnt = new Counter();
						unseenCounts.put(folderIdString, cnt);
					}
					if (entryId != null && (!seenMap.checkAndSetSeen(entryId, modifyDate, false))) {
						cnt.increment();
					}
				}
		        for (Folder f : folders) {
		        	Counter cnt = (Counter)unseenCounts.get(f.getId().toString());
		        	if (cnt == null) {
		        		results.put(f, Long.valueOf(0));
		        	} else {
		        		results.put(f, cnt.getCount());
		        	}
		        }
	        }
        }
        return results;
    }
 
    protected Hits getRecentEntries(Collection<Folder> folders) {
		ArrayList<String>ids = new ArrayList();
		for (Folder f:folders) {
			ids.add(f.getId().toString());
		}
	   	Date now = new Date();
    	Date startDate = new Date(now.getTime() - ObjectKeys.SEEN_MAP_TIMEOUT);

    	Criteria crit = new Criteria()
    		.add(eq(Constants.ENTRY_TYPE_FIELD,Constants.ENTRY_TYPE_ENTRY))  //choose only entries/ not replies
    		.add(in(Constants.BINDER_ID_FIELD, ids))
    		.add(between(Constants.LASTACTIVITY_DAY_FIELD,EntityIndexUtils.formatDayString(startDate), EntityIndexUtils.formatDayString(now)));
    	Hits results = null;
    	//Create the Lucene query
    	QueryBuilder qb = new QueryBuilder(true, false);
    	SearchObject so = qb.buildQuery(crit.toQuery());
    	
    	LuceneReadSession luceneSession = getLuceneSessionFactory().openReadSession();
    	//RemoteInStreamSession instreamSession = getInstreamSessionFactory().openSession();
        
        try {
        	results = luceneSession.search(RequestContextHolder.getRequestContext().getUserId(),
        			so.getAclQueryStr(), Constants.SEARCH_MODE_NORMAL, so.getLuceneQuery(),so.getSortBy(),0,0);
        	//results = instreamSession.search(so.getQueryString(),so.getSortBy(),0,0);
        } catch (Exception e) {
        	logger.warn("Exception throw while searching in getRecentEntries: " + e.toString());
        }
        finally {
            luceneSession.close();
        }
        return results;
    }
           
    public Folder locateEntry(Long entryId) {
        FolderEntry entry = (FolderEntry)getCoreDao().load(FolderEntry.class, entryId);
        if (entry == null) return null;
        try {
        	AccessUtils.readCheck(entry);
        } catch (AccessControlException ac) {
        	return null;
        }
        return entry.getParentFolder();
    }
    // get entry and check access
    public FolderEntry getEntry(Long folderId, Long entryId) {
        FolderEntry entry = loadEntry(folderId, entryId);
        AccessUtils.readCheck(entry);
        return entry;
    }
    public FolderEntry getEntryWithoutAccessCheck(Long folderId, Long entryId) {
        FolderEntry entry = loadEntry(folderId, entryId);
        return entry;
    }
    public FolderEntry getEntry(Long folderId, String entryNumber) {
    	Folder folder = getFolder(folderId);
    	String sortKey = HKey.getSortKeyFromEntryNumber(folder.getEntryRootKey(), entryNumber);
    	FolderEntry entry = getFolderDao().loadFolderEntry(sortKey, folder.getZoneId());
        AccessUtils.readCheck(entry);
        return entry;
    }

    public Map getEntryTree(Long folderId, Long entryId) {
    	return getEntryTree(folderId, entryId, false);
    }
    public Map getEntryTree(Long folderId, Long entryId, boolean includePreDeleted) {
    	//does read check
        FolderEntry entry = getEntry(folderId, entryId);   	
        Folder folder = entry.getParentFolder();
        FolderCoreProcessor processor=loadProcessor(folder);
        return processor.getEntryTree(folder, entry, includePreDeleted);
    }
    public SortedSet<FolderEntry>getEntries(Collection<Long>ids) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new EntryComparator(user.getLocale(), EntryComparator.SortByField.pathName);
       	TreeSet<FolderEntry> sEntries = new TreeSet<FolderEntry>(c);
       	List<FolderEntry>entries = getCoreDao().loadObjects(ids, FolderEntry.class, RequestContextHolder.getRequestContext().getZoneId());
    	for (FolderEntry e:entries) {
            try {
            	AccessUtils.readCheck(e);
            	sEntries.add(e);
            } catch (Exception ignoreMe) {};
    	}
    	return sEntries;
    }

    //inside write transaction    
    public void restoreEntry(Long parentFolderId, Long entryId, Object renameData) throws WriteEntryDataException, WriteFilesException {
    	restoreEntry(parentFolderId, entryId, renameData, true);
    }
    public void restoreEntry(Long parentFolderId, Long entryId, Object renameData, boolean reindex) throws WriteEntryDataException, WriteFilesException {
    	restoreEntry(parentFolderId, entryId, renameData, true, null, reindex);
    }
    //inside write transaction    
	public void restoreEntry(Long folderId, Long entryId, Object renameData, boolean deleteMirroredSource, Map options) throws WriteEntryDataException, WriteFilesException {
    	restoreEntry(folderId, entryId, renameData,deleteMirroredSource, options, true);
    }
	public void restoreEntry(Long folderId, Long entryId, Object renameData, boolean deleteMirroredSource, Map options, boolean reindex) throws WriteEntryDataException, WriteFilesException {
    	deCount.incrementAndGet();
    	
    	// Is the entry preDeleted and located in a non-mirrored
    	// Folder?
        FolderEntry entry = loadEntry(folderId, entryId);
        Folder folder = loadFolder(folderId);
        if ((null != entry)  &&    entry.isPreDeleted() &&
        	(null != folder) && (!(folder.isMirrored()))) {
			// Yes!  Validate we can restore it...
        	try {
        		checkAccess(entry, FolderOperation.restoreEntry);
        	}
        	catch (AccessControlException ace) {
        		FolderEntry topEntry = entry.getTopEntry();
        		if (null == topEntry) {
        			throw ace;
        		}
        		checkAccess(topEntry, FolderOperation.restoreEntry);
        	}
        	
	        // ...restore it...
        	entry.setPreDeleted(null);
        	entry.setPreDeletedWhen(null);
        	entry.setPreDeletedBy(null);

        	if (!entry.isTop()) {
        		//Increment all of the reply counts in parent entries. Must be done after clearing preDeleted
        		entry.getParentEntry().restorePreDeletedReply(entry);
        	}

	        // ...log the restoration...
			FolderCoreProcessor processor = loadProcessor(entry.getParentFolder());
			TrashHelper.changeEntry_Log(processor, entry, ChangeLog.RESTOREENTRY);
			
			// ...register the names so any naming conflicts get
			// ...handled...
        	TrashHelper.registerEntryNames(getCoreDao(), folder, entry, renameData);

        	// ...restart any workflows...
    		WorkflowModule workflowModule = (WorkflowModule)SpringContextUtil.getBean("workflowModule");
        	if (entry instanceof WorkflowControlledEntry) {
        		workflowModule.modifyWorkflowStateOnRestore(entry);
        	}
        	
	        // ...and finally, if requested to do so...
        	if (reindex) {
		        // ...re-index the entry.
        		processor.indexEntry(entry);
        		getRssModule().updateRssFeed(entry); 
        	}
        	
    		updateModificationTime(folder, options);
        }
    }
    
    //inside write transaction    
    public void preDeleteEntry(Long parentFolderId, Long entryId, Long userId) {
    	preDeleteEntry(parentFolderId, entryId, userId, true);
    }
    public void preDeleteEntry(Long parentFolderId, Long entryId, Long userId, boolean reindex) {
    	preDeleteEntry(parentFolderId, entryId, userId, true, null, reindex);
    }
    //inside write transaction    
	public void preDeleteEntry(Long folderId, Long entryId, Long userId, boolean deleteMirroredSource, Map options) {
    	preDeleteEntry(folderId, entryId, userId, deleteMirroredSource, options, true);
    }
	public void preDeleteEntry(Long folderId, Long entryId, Long userId, boolean deleteMirroredSource, Map options, boolean reindex) {
    	deCount.incrementAndGet();
        FolderEntry entry = loadEntry(folderId, entryId);
        Folder folder = loadFolder(folderId);
        if ((null != entry) && (null != folder) && (!(folder.isMirrored()))) {
        	try {
        		checkAccess(entry, FolderOperation.preDeleteEntry);
        	}
        	catch (AccessControlException ace) {
        		FolderEntry topEntry = entry.getTopEntry();
        		if (null == topEntry) {
        			throw ace;
        		}
        		checkAccess(topEntry, FolderOperation.preDeleteEntry);
        	}
        	
        	if (!entry.isTop()) {
        		//Decrement all of the reply counts in parent entries. Must be done before setting preDeleted
        		entry.getParentEntry().preDeleteReply(entry);
        	}
        	entry.setPreDeleted(Boolean.TRUE);
        	entry.setPreDeletedWhen(System.currentTimeMillis());
        	entry.setPreDeletedBy(userId);
        	
        	//Suspend any workflow timers
        	WorkflowProcessUtils.suspendTimers(entry);
        	
			FolderCoreProcessor processor = loadProcessor(entry.getParentFolder());
			TrashHelper.changeEntry_Log(processor, entry, ChangeLog.PREDELETEENTRY);
        	TrashHelper.unRegisterEntryNames(getCoreDao(), folder, entry);
        	if (reindex) {
        		processor.indexEntry(entry);
        	}
        	getRssModule().updateRssFeed(entry);
        	
    		updateModificationTime(folder, options);
        }
    }
    
    //inside write transaction    
    public void updateModificationStamp(Long parentFolderId, Long entryId) {
    	updateModificationStamp(parentFolderId, entryId, true);
    }
	public void updateModificationStamp(Long folderId, Long entryId, boolean reindex) {
    	deCount.incrementAndGet();
        FolderEntry entry = loadEntry(folderId, entryId);
        Folder folder = loadFolder(folderId);
        if ((null != entry) && (null != folder)) {
        	checkAccess(entry, FolderOperation.updateModificationStamp);
        	
        	entry.setModification(new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));
        	
			FolderCoreProcessor processor = loadProcessor(entry.getParentFolder());
			processor.processChangeLog(entry, ChangeLog.UPDATEMODIFICATIONSTAMP);
        	if (reindex) {
        		processor.indexEntry(entry);
        	}
        }
    }
    
    //no transaction        
    public void deleteEntry(Long parentFolderId, Long entryId) {
    	deleteEntry(parentFolderId, entryId, true, null);
    }
    //no transaction    
    public void deleteEntry(Long folderId, Long entryId, boolean deleteMirroredSource, Map options) {
    	deCount.incrementAndGet();
        FolderEntry entry = loadEntry(folderId, entryId);   	
        checkAccess(entry, FolderOperation.deleteEntry);
        Folder folder = entry.getParentFolder();
        FolderCoreProcessor processor=loadProcessor(folder);
        processor.deleteEntry(folder, entry, deleteMirroredSource, options);
		updateModificationTime(folder, options);
    }
    //inside write transaction    
    public void moveEntry(Long folderId, Long entryId, Long destinationId, String[] toFileNames, Map options) {
        FolderEntry entry = loadEntry(folderId, entryId);   	
        checkAccess(entry, FolderOperation.moveEntry);
        Folder folder = entry.getParentFolder();
        FolderCoreProcessor processor=loadProcessor(folder);
                
        Folder destination =  loadFolder(destinationId);
        checkAccess(destination, FolderOperation.addEntry);
        //Check if there is quota to do the move
        long fileSize = 0;
        for (Attachment att : entry.getAttachments()) {
        	if (att instanceof FileAttachment) {
        		long attFileSize = ((FileAttachment)att).getFileItem().getLength();
        		fileSize += attFileSize;
        		
        		//While in here, check the file size to see if it is within the upload size limit
        		checkFileUploadSizeLimit(destination, attFileSize, ((FileAttachment) att).getFileItem().getName());
        	}
        }
		if (!processor.checkMoveEntryQuota(entry.getParentBinder(), destination, entry)) {
			//Adding this file would cause the quota to be exceeded
			throw new BinderQuotaException(entry.getTitle());
		}

		if ((folder.isMirrored() || destination.isMirrored())) {
			//To move to and from mirrored folders, copy the entry to the destination folder then delete the original entry
			processor.copyEntry(folder, entry, destination, toFileNames, options);
			processor.deleteEntry(folder, entry, true, options);
		} else {
			processor.moveEntry(folder, entry, destination, toFileNames, options);
		}
        
		updateModificationTime(folder, options);
        if(destination != folder)
        	updateModificationTime(destination, options);
    }
    
	private void checkFileUploadSizeLimit(Binder binder, Long fileSize, String fileName) 
			throws DataQuotaException {
		User user = RequestContextHolder.getRequestContext().getUser();
		//Check that the file isn't too big
		//If there is a binder setting, it must match irrespective of user settings
		Long maxFileSize = getBinderModule().getBinderMaxFileSize(binder);
		if (maxFileSize != null) {
			//There is a file size limit, go check it
			if (fileSize > maxFileSize * ObjectKeys.MEGABYTES) {
				throw new FileSizeLimitException(fileName);
			}
		}
		//Check the system default and the user limits
		Long userMaxFileSize = user.getFileSizeLimit();
		Long userMaxGroupsFileSize = user.getMaxGroupsFileSizeLimit();
		Long fileSizeLimit = null;
		if (userMaxGroupsFileSize != null) {
			//Start with the group setting (if any)
			fileSizeLimit = userMaxGroupsFileSize;
		}
		if (userMaxFileSize != null) {
			//If there is a user setting, use that (even if it is less than the group setting)
			fileSizeLimit = userMaxFileSize;
		}
		if (fileSizeLimit == null) {
			//There aren't any per-user or per-group settings, so see if there is a site default
			fileSizeLimit = getAdminModule().getFileSizeLimitUserDefault();
		}
		//Now check to see if the file size is above the limit
		if (fileSizeLimit != null && fileSize > fileSizeLimit * ObjectKeys.MEGABYTES) {
			throw new FileSizeLimitException(fileName);
		}
	}

    //inside write transaction    
    public FolderEntry copyEntry(Long folderId, Long entryId, Long destinationId, String[] toFileNames, Map options) {
        FolderEntry entry = loadEntry(folderId, entryId);   	
        checkAccess(entry, FolderOperation.copyEntry);
        Folder folder = entry.getParentFolder();
        FolderCoreProcessor processor=loadProcessor(folder);
               
        Folder destination =  loadFolder(destinationId);
        checkAccess(destination, FolderOperation.addEntry);

        //Check if there is quota to do the move
        long fileSize = 0;
        for (Attachment att : entry.getAttachments()) {
        	if (att instanceof FileAttachment) {
        		long attFileSize = ((FileAttachment)att).getFileItem().getLength();
        		fileSize += attFileSize;
        		
        		//While in here, check the file size to see if it is within the upload size limit
        		checkFileUploadSizeLimit(destination, attFileSize, ((FileAttachment) att).getFileItem().getName());
        	}
        }
		if (!getBinderModule().isBinderDiskQuotaOk(destination, fileSize)) {
			//Adding this file would cause the quota to be exceeded
			throw new BinderQuotaException(entry.getTitle());
		}

		FolderEntry entryCopy = (FolderEntry) processor.copyEntry(folder, entry, destination, toFileNames, options);
		
		updateModificationTime(destination, options);
		
		return entryCopy;
    }
    //inside write transaction    
    public void setSubscription(Long folderId, Long entryId, Map<Integer,String[]> styles) {
    	//getEntry does read check
		FolderEntry entry = getEntry(folderId, entryId);
		//only subscribe at top level
		if (!entry.isTop()) entry = entry.getTopEntry();
		User user = RequestContextHolder.getRequestContext().getUser();
		Subscription s = getProfileDao().loadSubscription(user.getId(), entry.getEntityIdentifier());
		//digest doesn't make sense here - only individual messages are sent 
		if (styles == null || styles.isEmpty()) {
			if (s != null) {
				getCoreDao().delete(s);
				//if this is the last subscription, let entry know
				List subs = getCoreDao().loadSubscriptionByEntity(entry.getEntityIdentifier());
				if (subs.size() == 1) entry.setSubscribed(false);				
			}
		} else {
			if (s == null) {
				s = new Subscription(user.getId(), entry.getEntityIdentifier());
				s.setStyles(styles);
				getCoreDao().save(s);
			} else 	s.setStyles(styles);
			entry.setSubscribed(true);
		}
  	
    }
    public Subscription getSubscription(FolderEntry entry) {
    	//have entry so assume read access
		User user = RequestContextHolder.getRequestContext().getUser();
		if (!entry.isTop()) entry = entry.getTopEntry();
		return getProfileDao().loadSubscription(user.getId(), entry.getEntityIdentifier());
    }

	public Collection<Tag> getTags(FolderEntry entry) {
		//have Entry - so assume read access
		//bulk load tags
        return getCoreDao().loadEntityTags(entry.getEntityIdentifier(), RequestContextHolder.getRequestContext().getUser().getEntityIdentifier());
	}
	
    //inside write transaction    
	public Tag [] setTag(Long binderId, Long entryId, String newTag, boolean community) {
		//read access checked by getEntry
		FolderEntry entry = getEntry(binderId, entryId);
		if (community) checkAccess(entry, FolderOperation.manageTag);
		if (Validator.isNull(newTag)) return null;
		Collection<String> newTags = TagUtil.buildTags(newTag);		
		if (newTags.size() == 0) return null;
		User user = RequestContextHolder.getRequestContext().getUser();
		EntityIdentifier uei = user.getEntityIdentifier();
		EntityIdentifier eei = entry.getEntityIdentifier();
        Tag [] tags = new Tag[newTags.size()];
        int i=0;
		for (String tagName:newTags) {
			Tag tag = new Tag();
			//community tags belong to the binder - don't care who created it
		   	if (!community) tag.setOwnerIdentifier(uei);
		   	tag.setEntityIdentifier(eei);
		    tag.setPublic(community);
		   	tag.setName(tagName);
			getCoreDao().save(tag);
            tags[i++] = tag;
	   	}
 	    loadProcessor(entry.getParentFolder()).indexEntry(entry);
        return tags;
	}
	
    //inside write transaction    
	public void deleteTag(Long binderId, Long entryId, String tagId) {
	   	FolderEntry entry = loadEntry(binderId, entryId);
  		Tag tag = null;
   		try {
	   		tag = coreDao.loadTag(tagId, entry.getParentBinder().getZoneId());
	   	} catch(Exception e) {
	   		return;
	   	}
	   	if (tag.isPublic()) checkAccess(entry, FolderOperation.manageTag);
	   	//if created tag for this entry, by this user- can delete it
	   	else if (!tag.isOwner(RequestContextHolder.getRequestContext().getUser())) return;
	   	getCoreDao().delete(tag);
 	    loadProcessor(entry.getParentFolder()).indexEntry(entry);
	}
	
    //inside write transaction    
	public void setEntryDef(Long folderId, Long entryId, String entryDef) {
		FolderEntry entry = getEntry(folderId, entryId);
		entry.setEntryDef(definitionModule.getDefinition(entryDef));
	}
	
	//Change entry def type
    //inside write transaction    
	public void changeEntryType(Long entryId, String newDefId) {
		FolderEntry entry = loadEntry(null, entryId);
		if (entry == null) return;
		Folder folder = (Folder)entry.getParentBinder();
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		checkAccess(entry, FolderOperation.changeEntryType);
		List<Long> entryIds = new ArrayList<Long>();
		entryIds.add(entryId);
		getFolderDao().setFolderEntryType(folder, entryIds, newDefId);
		entry.setEntryDefId(newDefId);
		loadProcessor(entry.getParentFolder()).indexEntry(entry);
	}


    //inside write transaction    	
	public void setUserRating(Long folderId, Long entryId, long value) {
		//getEntry does read check
		FolderEntry entry = getEntry(folderId, entryId);
		setRating(entry, value);
	}
    //inside write transaction    
	public void setUserRating(Long folderId, long value) {
		//getFolder does read check
		Folder folder = getFolder(folderId);
		setRating(folder, value);
	} 
	protected void setRating(DefinableEntity entity, long value) {
		//Have the entity, you can rate it
		EntityIdentifier id = entity.getEntityIdentifier();
		//update entity average
	    User user = RequestContextHolder.getRequestContext().getUser();
       	Rating rating = getProfileDao().loadRating(user.getId(), id);
		if (rating == null) {
      		rating = new Rating(user.getId(), id);
			getCoreDao().save(rating);
		} 
		//set user rating
		rating.setRating(value);
		List<Object[]> results = getCoreDao().loadObjects("select count(*), avg(x.rating) from x in class " + Rating.class.getName() + " where x.id.entityId=" +
				id.getEntityId() + " and x.id.entityType=" + id.getEntityType().getValue() +" and not x.rating is null", null);
     	AverageRating avg = entity.getAverageRating();
     	if (avg == null) {
     		avg = new AverageRating();
     		entity.setAverageRating(avg);
     	}
      	Object[] row = results.get(0);
     	avg.setAverage((Double)row[1]);
   		avg.setCount((Long)row[0]);
 			
	}
    //inside write transaction    
	public void setUserVisit(FolderEntry entry) {
		//assume already have access
		EntityIdentifier id = entry.getEntityIdentifier();
		//set user visit
		
		User user = RequestContextHolder.getRequestContext().getUser();
		Visits visit = getProfileDao().loadVisit(user.getId(), id);
		if (visit == null) {
			visit = new Visits(user.getId(), id);
			try {
				visit = (Visits)getCoreDao().saveNewSession(visit);
			} catch (Exception ex) {
				//probably hit button 2X
				visit = getProfileDao().loadVisit(user.getId(), id);
			}
		}
		if (visit != null) {
			//visits don't use optimistic locking and the popularity field on an entry does not use optimistic locking
			//This allows us not to worry about contention, although the counts may be slightly off.
			//The only other choice is a retry loop by the controller
			visit.incrReadCount();   	
			//this takes to long and is only trying to readjust if users are deleted, which it probably shouldn't anyway
			//Object[] cfValues = new Object[]{id.getEntityId(), id.getEntityType().getValue()};
			//long result = getCoreDao().sumColumn(Visits.class, "readCount", new FilterControls(ratingAttrs, cfValues), user.getZoneId());
			Long pop = entry.getPopularity();
			if (pop == null) pop = 0L;
			entry.setPopularity(++pop);
		}
	}
	

    //inside write transaction    
    public HistoryStamp reserveEntry(Long folderId, Long entryId)
	throws AccessControlException, ReservedByAnotherUserException,
	FilesLockedByOtherUsersException {
    	// Because I don't expect customers to override or extend this 
    	// functionality, I don't delegate its implementation to a
    	// processor (Am I wrong about this?)
    	
        FolderEntry entry = loadEntry(folderId, entryId);   	
        Folder folder = entry.getParentFolder();
 
        // For now, check against the same access right needed for modifying
        // entry. We might want to have a separate right for reserving entry...
    	checkAccess(entry, FolderOperation.reserveEntry);

        User user = RequestContextHolder.getRequestContext().getUser();
    	
    	HistoryStamp reservation = entry.getReservation();
    	if (reservation == null) { // The entry is not currently reserved. 
    		// We must check if any of the files in the entry is locked
    		// by another user. 
    		
    		// Make sure that the file lock states are current before examining them.
    		getFileModule().bringLocksUptodate(folder, entry);
    		
    		// Now that lock states are up-to-date, we can examine them.
    		
    		boolean atLeastOneFileLockedByAnotherUser = false;
    		Collection<FileAttachment> fAtts = entry.getFileAttachments();
    		for (FileAttachment fa :fAtts) {
    			if(fa.getFileLock() != null && !fa.getFileLock().getOwner().equals(user)) {
    				atLeastOneFileLockedByAnotherUser = true;
    				break;
    			}
    		}	
    		
    		if (!atLeastOneFileLockedByAnotherUser) {
    			// All remaining effective locks are owned by the same user
    			// or there are no effective locks at all.
    			// Proceed and reserve the entry.
    			entry.setReservation(user);
                reservation = entry.getReservation();
    	 	    loadProcessor(entry.getParentFolder()).indexEntry(entry);
    		} else { // One or more lock is held by someone else.
    			// Build error information.
    			List<FileLockInfo> info = new ArrayList<FileLockInfo>();
        		for(FileAttachment fa :fAtts) {
	    			if(fa.getFileLock() != null) {
	    				info.add(new FileLockInfo
	    						(fa.getRepositoryName(), 
	    								fa.getFileItem().getName(), 
	    								fa.getFileLock().getOwner()));
	    			}
	    		}		    			
	    		throw new FilesLockedByOtherUsersException(info);
    		}
    	} else {	
    		// The entry is currently reserved. 
    		if (reservation.getPrincipal().equals(user)) {
    			// The entry is reserved by the same user. Noop.
    		} else {
    			// The entry is reserved by another user.
    			throw new ReservedByAnotherUserException(entry);
    		}
    	}
        return reservation;
    }
    
    //inside write transaction    
   public void unreserveEntry(Long folderId, Long entryId)
	throws AccessControlException, ReservedByAnotherUserException {
	   FolderEntry entry = loadEntry(folderId, entryId);   	
 
	   // I will skip checking the user's access right for this operation.
	   // If the user previously reserved the entry successfully, it is
	   // inconceivable that the user no longer has the right to unreserve
	   // the entry (although it is possible in theory...). If the user
	   // hasn't been able to reserve it previously, unreserve won' work
	   // anyway. So either way, we can skip the access checking. 
	   //checkModifyEntryAllowed(entry);

	   User user = RequestContextHolder.getRequestContext().getUser();
    	
	   HistoryStamp reservation = entry.getReservation();
	   if (reservation == null) { 
    		// The entry is not currently reserved by anyone. 
    		// Nothing to do. 
	   } else {
    		boolean isUserBinderAdministrator = false;
    		try {
    			checkAccess(entry, FolderOperation.overrideReserveEntry);
    			isUserBinderAdministrator = true;
    		} catch (AccessControlException ac) {};    		
    		
    		if (reservation.getPrincipal().equals(user) || isUserBinderAdministrator) {
    			// The entry is currently reserved by the same user or if the user happens to be a binder administrator 
    			// Cancel the reservation.
    			entry.clearReservation();
    	 	    loadProcessor(entry.getParentFolder()).indexEntry(entry);
    		} else {
    			// The entry is currently reserved by another user. 
    			throw new ReservedByAnotherUserException(entry);
    		}
	   }
   }
   //this is for webdav - where the file names are unqiue within a library folder
   public FolderEntry getLibraryFolderEntryByFileName(Folder fileFolder, String title)
	throws AccessControlException {
       	try {
    		Long id = getCoreDao().findFileNameEntryId(fileFolder, title);
    		//getEntry does read check
    		return getEntry(fileFolder.getId(), id);
    	} catch (NoObjectByTheIdException no) {
    		return null;
    	}
    }
    //this is for wiki links where normalize title is used
    public Set<FolderEntry> getFolderEntryByNormalizedTitle(Long folderId, String title, String zoneUUID)
	throws AccessControlException {
   		Set views = new HashSet();
   		Folder folder = null;
   		try {
    		folder = getFolder(folderId);
    	} catch(NoFolderByTheIdException e) {
    		return views;
    	}
    	if (folder == null) return views;
    	List<FolderEntry> results = getFolderDao().loadEntries(folder, new FilterControls(ObjectKeys.FIELD_ENTITY_NORMALIZED_TITLE, title));
   		for (FolderEntry entry: results) {
   			try {
   				AccessUtils.readCheck(entry);
   				views.add(entry);
   			} catch (AccessControlException ac) {}
   		}
   		return views;
    }
    public SortedSet<String> getSubfoldersTitles(Folder folder, boolean checkAccess) {
    	//already have access to folder
    	TreeSet<String> titles = new TreeSet<String>();
   		
    	for(Object o : folder.getFolders()) {
    		Folder f = (Folder) o;
    		if (f.isDeleted() || f.isPreDeleted()) continue;
    		if(!checkAccess || (getBinderModule().testAccess(f, BinderOperation.readEntries) ||
    				getBinderModule().testAccess(f, BinderOperation.viewBinderTitle))) {
    			titles.add(f.getTitle());
    		}
    	}
    	
    	return titles;    	
    }
    
    public SortedSet<Folder> getSubfolders(Folder folder) {
    	//already have access to folder
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new BinderComparator(user.getLocale(), BinderComparator.SortByField.title);
       	TreeSet<Folder> subFolders = new TreeSet<Folder>(c);
   		
    	for(Object o : folder.getFolders()) {
    		Folder f = (Folder) o;
    		if (f.isDeleted() || f.isPreDeleted()) continue;
    		if (getBinderModule().testAccess(f, BinderOperation.readEntries) ||
    				getBinderModule().testAccess(f, BinderOperation.viewBinderTitle)) {
    			subFolders.add(f);
    		}
    	}
    	
    	return subFolders;    	
    }
    
    public boolean testTransitionOutStateAllowed(FolderEntry entry, Long stateId) {
		try {
			checkTransitionOutStateAllowed(entry, stateId);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
    }
    protected void checkTransitionOutStateAllowed(FolderEntry entry, Long stateId) {
		WorkflowState ws = entry.getWorkflowState(stateId);
		AccessUtils.checkTransitionOut(entry.getParentBinder(), entry, ws.getDefinition(), ws);   		
    }
	
    public boolean testTransitionInStateAllowed(FolderEntry entry, Long stateId, String toState) {
		try {
			checkTransitionInStateAllowed(entry, stateId, toState);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
   }
    protected void checkTransitionInStateAllowed(FolderEntry entry, Long stateId, String toState) {
		WorkflowState ws = entry.getWorkflowState(stateId);
		AccessUtils.checkTransitionIn(entry.getParentBinder(), entry, ws.getDefinition(), toState);   		
    }

    public void addEntryWorkflow(Long folderId, Long entryId, String definitionId, Map options) {
    	//start a workflow on an entry
    	FolderEntry entry = loadEntry(folderId, entryId);
    	checkAccess(entry, FolderOperation.addEntryWorkflow);
		if (options != null && options.containsKey(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE)) { //used to import entries into system
			checkAccess(entry.getParentFolder(), FolderOperation.changeEntryTimestamps);
		}
        Definition def = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
        FolderCoreProcessor processor = loadProcessor(entry.getParentFolder());
        processor.addEntryWorkflow(entry.getParentBinder(), entry, def, options);
    }
    public void deleteEntryWorkflow(Long folderId, Long entryId, String definitionId) 
		throws AccessControlException {
       	//start a workflow on an entry
    	FolderEntry entry = loadEntry(folderId, entryId);
    	checkAccess(entry, FolderOperation.deleteEntryWorkflow);
    	Definition def = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
        FolderCoreProcessor processor = loadProcessor(entry.getParentFolder());
        processor.deleteEntryWorkflow(entry.getParentBinder(), entry, def);

    }
    public boolean checkIfManualTransitionAllowed(Long folderId, Long entryId, Long workflowTokenId, String toState) throws AccessControlException {
        boolean result = false;
        FolderEntry entry = loadEntry(folderId, entryId);
		Set states = entry.getWorkflowStates();
		for (Iterator iter=states.iterator(); iter.hasNext();) {
			WorkflowState ws = (WorkflowState)iter.next();
			if (ws.getTokenId().equals(workflowTokenId)) {
				Map transitions = getManualTransitions(entry, ws.getId());
				//See if the toState is in this list
				if (transitions.containsKey(toState)) result = true;
			}
		}
		return result;
     }
   public void modifyWorkflowState(Long folderId, Long entryId, Long stateId, String toState) throws AccessControlException {
       FolderEntry entry = loadEntry(folderId, entryId);   	
       Folder folder = entry.getParentFolder();
       FolderCoreProcessor processor=loadProcessor(folder);
       //access checks - not a simple modify
       WorkflowState ws = entry.getWorkflowState(stateId);
       Map transitions = getManualTransitions(entry, ws.getId());	//This checks both allowed transition out and in
       //See if the toState is in this list. If so, then allow the modify
       if (transitions.containsKey(toState)) {
    	   processor.modifyWorkflowState(folder, entry, stateId, toState);
       }
    }
	public Map<String, String> getManualTransitions(FolderEntry entry, Long stateId) {
		WorkflowState ws = entry.getWorkflowState(stateId);
		Map<String,Map> result = WorkflowUtils.getManualTransitions(ws.getDefinition(), ws.getState());
		Map transitionData = new LinkedHashMap();
		for (Iterator iter=result.entrySet().iterator(); iter.hasNext();) {
			Map.Entry me = (Map.Entry)iter.next();
				Map data = (Map)me.getValue();
				try {
					//access check
					Element accessEle = (Element)data.get("transitionAccessElement");
					if (AccessUtils.doesManualTransitionAclExist(entry.getParentBinder(), entry, ws, accessEle)) {
						//There is a manual transition acl, so check it.
						AccessUtils.checkManualTransitionAccess(entry.getParentBinder(), entry, ws, accessEle);
						if (AccessUtils.checkIfTransitionInAclExists(entry.getParentBinder(), entry, ws.getDefinition(), (String)me.getKey())) {
							//If there is an acl guarding transition in, test it
							AccessUtils.checkTransitionIn(entry.getParentBinder(), entry, ws.getDefinition(), (String)me.getKey()); 
						}
						transitionData.put(me.getKey(), (String)data.get("toStateCaption"));
					} else {
						//There is no manual transition acl, so do the default check
						if (testTransitionOutStateAllowed(entry, stateId)) {
							if (AccessUtils.checkIfTransitionInAclExists(entry.getParentBinder(), entry, ws.getDefinition(), (String)me.getKey())) {
								//If there is an acl guarding transition in, test it
								AccessUtils.checkTransitionIn(entry.getParentBinder(), entry, ws.getDefinition(), (String)me.getKey()); 
							}
							transitionData.put(me.getKey(), (String)data.get("toStateCaption"));
						}
					}
				} catch (AccessControlException ac) {};
		}
		return transitionData;
		
    }		

	@Override
	public Map<String, Map> getWorkflowQuestions(FolderEntry entry, Long stateId) {
		WorkflowState ws = entry.getWorkflowState(stateId);
    	Map<String, Map> qMap = WorkflowUtils.getQuestions(ws.getDefinition(), ws);
    	//Check if the user is allowed to respond
    	for (String question : qMap.keySet()) {
    		if (WorkflowProcessUtils.checkIfQuestionRespondersSpecified(entry, ws, question)) {
	    		if (!BinderHelper.checkIfWorkflowResponseAllowed((WorkflowSupport)entry, ws, question)) {
	    			//The user isn't on the list, so check for forum Default
	    			if (!WorkflowProcessUtils.checkIfQuestionRespondersIncludeForumDefault(entry, ws, question) ||
	    					!testAccess(entry, FolderOperation.modifyEntry)) {
		    			//This question is not allowed, so remove it
		    			qMap.remove(question);
	    			}
	    		}
    		} else {
    			if (!testAccess(entry, FolderOperation.modifyEntry)) {
	    			//This question is not allowed, so remove it
	    			qMap.remove(question);
    			}
    		}
    	}
    	return qMap;
    }		

    public void setWorkflowResponse(Long folderId, Long entryId, Long stateId, InputDataAccessor inputData) {
        FolderEntry entry = loadEntry(folderId, entryId);   	
        Folder folder = entry.getParentFolder();
        FolderCoreProcessor processor=loadProcessor(folder);
        Boolean canModifyEntry = testAccess(entry, FolderOperation.modifyEntry);
        processor.setWorkflowResponse(folder, entry, stateId, inputData, canModifyEntry);
    }
    
   //called by scheduler to complete folder deletions
    //no transaction
    public synchronized void cleanupFolders() {
		FilterControls fc = new FilterControls();
		fc.add("deleted", Boolean.TRUE);
		ObjectControls objs = new ObjectControls(Folder.class, new String[] {"id"});
		List<Object> folders = getCoreDao().loadObjects(objs, fc, RequestContextHolder.getRequestContext().getZoneId());
		logger.debug("checking for deleted folders");
		int success = 0;
		int fail = 0;
		for (Object obj: folders) {
			Long folderId;
			if (obj instanceof Long) {
				folderId = (Long)obj;
			} else  {
				folderId = (Long)((Object[])obj)[0];
			} 
			try {
				Folder f = getFolderDao().loadFolder(folderId, RequestContextHolder.getRequestContext()
						.getZoneId());
				FolderCoreProcessor processor = loadProcessor(f);
				processor.deleteBinder(f, true, null);
				getCoreDao().evict(f);
				success++;
			} catch (Exception ex) {
				fail++;
				logger.error(ex);
			}
		}
		if(folders != null && folders.size() > 0)
			logger.info("Folders cleaned up: success=" + success + ", fail=" + fail);
	}


    public IndexErrors indexEntry(FolderEntry entry, boolean includeReplies) {
    	FolderCoreProcessor processor = loadProcessor(entry.getParentFolder());
    	IndexErrors errors = processor.indexEntry(entry);
		if (includeReplies) {
			List<FolderEntry> replies = new ArrayList();
			replies.addAll(entry.getReplies());
			while (!replies.isEmpty()) {
				FolderEntry reply = replies.get(0);
				replies.remove(0);
				replies.addAll(reply.getReplies());
				IndexErrors replyErrors = processor.indexEntry(reply);
				errors.add(replyErrors);
			}
		}
		return errors;
    }
    public org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, Entry entry, Collection tags) {
		FolderCoreProcessor processor = loadProcessor((Folder)binder);
		return processor.buildIndexDocumentFromEntry(binder, entry, tags);
    }    

    public void indexFileContentInFolder(Long netFolderRoot) {
    	// $$$$$$$$$$$
    	
    	
    }
    
    protected void indexFileContentInEntry(FileAttachment fa) throws Exception {
        DefinableEntity entity = fa.getOwner().getEntity();
        if(!(entity instanceof FolderEntry)) {
        	logger.error("The file with id [" + fa.getId() + "] is owned by an entity that is not a folder entry");
        	return;
        }
        
        FolderEntry entry = (FolderEntry) entity;
        Folder folder = entry.getParentFolder();
        
        FolderCoreProcessor processor = loadProcessor(folder);
        
        // Delete existing document from the index.
        IndexSynchronizationManager.deleteDocuments(new Term(Constants.FILE_ID_FIELD, fa.getId()));
        // Add new document with file content.
    	try {
    		IndexSynchronizationManager.addDocument(processor.buildIndexDocumentFromEntryFile(folder, entry, fa, null, false));
    	}
    	catch(Exception e) {
    		logger.error("Error indexing file '" + fa.getFileItem().getName() + "' for entry '" + entry.getId() + "'", e);
    	}
    }
    
    /**
     * Helper classs to return folder unseen counts as an objects
     * @author Janet McCann
     *
     */
    protected class Counter {
    	private long count=0;
    	protected Counter() {	
    	}
    	protected void increment() {
    		++count;
    	}
    	protected Long getCount() {
    		return count;
    	}    	
    }
    
    @Override
	public void resetCounts() {
		aeCount.set(0);
		meCount.set(0);
		deCount.set(0);
		arCount.set(0);
	}
    @Override
	public int getAddEntryCount() {
		return aeCount.get();
	}
    @Override
	public int getDeleteEntryCount() {
		return deCount.get();
	}
    @Override
	public int getModifyEntryCount() {
		return meCount.get();
	}
    @Override
	public int getAddReplyCount() {
		return arCount.get();
	}
	public Long getZoneEntryId(Long entryId, String zoneUUID) {
		if (Validator.isNull(zoneUUID)) return entryId;
		List<Long> ids = getCoreDao().findZoneEntityIds(entryId, zoneUUID, EntityType.folderEntry.name());
		if (ids.isEmpty()) {
			ZoneInfo zoneInfo = ExportHelper.getZoneInfo();
			if (zoneInfo.getId().equals(zoneUUID)) return entryId;
			return null;
		}
		return ids.get(0);
	}
	
	private void updateModificationTime(final Folder folder, Map options) {
		// Since this method is invoked only as a necessary side-effect of some other operation
		// user invoked for which access checking was already done (that is, this is invoked by
		// system rather than user), there is no need for access checking. 
		// In order to make sure that this code can run without failure caused by access checking,
		// we run this in admin context.
		if(folder != null && !(options != null && Boolean.TRUE.equals(options.get(ObjectKeys.INPUT_OPTION_SKIP_PARENT_MODTIME_UPDATE)))) {
			RunasTemplate.runasAdmin(new RunasCallback() {
				public Object doAs() {
					getBinderModule().updateModificationTime(folder);
					return null;
				}
			}, RequestContextHolder.getRequestContext().getZoneName());
		}
	}
}