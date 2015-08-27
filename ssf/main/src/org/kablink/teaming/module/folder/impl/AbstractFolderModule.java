/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.kablink.teaming.domain.BinderState;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.CustomAttribute;
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
import org.kablink.teaming.domain.ShareItem;
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
import org.kablink.teaming.jobs.FolderDelete;
import org.kablink.teaming.jobs.FolderNotification;
import org.kablink.teaming.jobs.NetFolderContentIndexing;
import org.kablink.teaming.jobs.NonNetFolderContentIndexing;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.jobs.ZoneSchedule;
import org.kablink.teaming.lucene.Hits;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.binder.impl.EntryDataErrors;
import org.kablink.teaming.module.binder.impl.EntryDataErrors.Problem;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FileLockInfo;
import org.kablink.teaming.module.folder.FilesLockedByOtherUsersException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.processor.FolderCoreProcessor;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.rss.RssModule;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.shared.CommentAccessUtils;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.module.workflow.WorkflowModule;
import org.kablink.teaming.module.workflow.WorkflowProcessUtils;
import org.kablink.teaming.module.workflow.WorkflowUtils;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.runasync.RunAsyncManager;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.search.LuceneReadSession;
import org.kablink.teaming.search.QueryBuilder;
import org.kablink.teaming.search.SearchObject;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.OperationAccessControlException;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.survey.Survey;
import org.kablink.teaming.survey.SurveyModel;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SimpleMultipartFile;
import org.kablink.teaming.util.SizeMd5Pair;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.ExportHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;

import static org.kablink.util.search.Restrictions.between;
import static org.kablink.util.search.Restrictions.eq;
import static org.kablink.util.search.Restrictions.in;

import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * ?
 * 
 * @author Jong Kim
 */
@SuppressWarnings({"unchecked", "unused"})
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
	
	protected ReportModule getReportModule() {
		return (ReportModule) SpringContextUtil.getBean("reportModule");
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
 	
	protected NonNetFolderContentIndexing getNonNetFolderContentIndexingObject() {
		String className = SPropsUtil.getString("job.non.net.folder.content.indexing.class", "org.kablink.teaming.jobs.DefaultNonNetFolderContentIndexing");
		return (NonNetFolderContentIndexing)ReflectHelper.getInstance(className);
    }    

	@Override
	public ScheduleInfo getNotificationSchedule(Long zoneId, Long folderId) {
  		return getNotificationScheduleObject().getScheduleInfo(zoneId, folderId);
	}
	
	@Override
	public void setNotificationSchedule(ScheduleInfo config, Long folderId) {
    	checkAccess(getFolder(folderId),FolderOperation.manageEmail);
        //data is stored with job
        getNotificationScheduleObject().setScheduleInfo(config, folderId);
    }    

	private FolderNotification getNotificationScheduleObject() {
		return new DefaultFolderNotification();
    }    

	//called on zone delete
	@Override
	public void stopScheduledJobs(Workspace zone) {
		FolderDelete job = getDeleteProcessor(zone);
		job.remove(zone.getId());
    	NonNetFolderContentIndexing nonNetFolderContentIndexingObj = getNonNetFolderContentIndexingObject();
    	nonNetFolderContentIndexingObj.remove(zone.getId());
	}
 	//called on zone startup
     @Override
	public void startScheduledJobs(Workspace zone) {
    	if (zone.isDeleted()) return;
    	//make sure a delete job is scheduled for the zone
		FolderDelete job = getDeleteProcessor(zone);
		String minutesString = (String)SZoneConfig.getString(zone.getName(), "folderConfiguration/property[@name='" + FolderDelete.DELETE_MINUTES + "']");
    	int minutes = 10;
    	try {
    		minutes = Integer.parseInt(minutesString);
    	} catch (Exception ex) {};
    	job.schedule(zone.getId(), minutes*60);
    	// make sure content indexing job for adhoc files is scheduled 
    	NonNetFolderContentIndexing nonNetFolderContentIndexingObj = getNonNetFolderContentIndexingObject();
    	nonNetFolderContentIndexingObj.schedule(zone.getId(), SPropsUtil.getInt("job.non.net.folder.content.indexing.interval.minutes", 10));
	}
     
	@Override
	public boolean testReadAccess(User user, WorkArea workArea, boolean checkSharing) {
		return getAccessControlManager().testOperation(user, workArea, WorkAreaOperation.READ_ENTRIES, checkSharing);
	}

	@Override
	public boolean testFolderRenameAccess(User user, Binder binder, boolean checkSharing) {
		WorkArea workArea = (WorkArea)binder;
		return getAccessControlManager().testOperation(user, workArea, WorkAreaOperation.RENAME_ENTRIES, checkSharing);
	}

	@Override
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
	@Override
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
			case scheduleSynchronization:
			case manageEmail:
			case changeEntryTimestamps:
			case fullSynchronize:
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
			case downloadFolderAsCsv:
				getAccessControlManager().checkOperation(folder,
						WorkAreaOperation.DOWNLOAD_FOLDER_AS_CSV);
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
	@Override
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
	@Override
	public void checkAccess(FolderEntry entry, FolderOperation operation) throws AccessControlException {
		checkAccess(RequestContextHolder.getRequestContext().getUser(), entry, operation);
	}
	
	@Override
	public void checkAccess(User user, FolderEntry entry, FolderOperation operation) throws AccessControlException {
		// Special case handle those operations on a comment that
		// require it.
		switch (CommentAccessUtils.checkCommentAccess(entry, operation, user)) {
		case ALLOWED:   return;
		case REJECTED:  throw new AccessControlException(operation.toString(), new Object[] {});
		
		default:
		case PROCESS_ACLS:
			break;
		}
		
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
				AccessUtils.readCheck(entry);   
				break;
			case copyEntry:
				AccessUtils.readCheck(entry);   
				Document def = entry.getEntryDefDoc();
				Element familyProperty = (Element) def.getRootElement().selectSingleNode("//properties/property[@name='family']");
				if (familyProperty != null) {
					String family = familyProperty.attributeValue("value", "");
					if (family.equals(Definition.FAMILY_SURVEY)) {
						//Copying a survey requires modify rights since there is potentially hidden data involved (Bug #876543)
						AccessUtils.operationCheck(entry, WorkAreaOperation.MODIFY_ENTRIES);   
					}
				}

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
			case renameEntry:
				AccessUtils.operationCheck(entry, WorkAreaOperation.RENAME_ENTRIES);   
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
			case downloadFolderAsCsv:
				AccessUtils.operationCheck(entry, WorkAreaOperation.DOWNLOAD_FOLDER_AS_CSV);
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
			case allowSharingPublicLinks:
			{
				AccessUtils.operationCheck(entry, WorkAreaOperation.ALLOW_SHARING_PUBLIC_LINKS);   
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
    
	protected Folder loadFolder(Long folderId) throws NoFolderByTheIdException {
        Folder folder = getFolderDao().loadFolder(folderId, RequestContextHolder.getRequestContext().getZoneId());
		if (folder.isDeleted()) throw new NoFolderByTheIdException(folderId);
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

	@Override
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
	
    @Override
	public Folder getFolderWithoutAccessCheck(Long folderId) throws NoFolderByTheIdException {
    	return loadFolder(folderId);
    }

	@Override
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
    @Override
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
        
        end(begin, "addEntry");
        return entry;
    }
    //no transaction    
	@Override
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
	@Override
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
    @Override
	public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData, 
    		Map fileItems, Collection<String> deleteAttachments, Map<FileAttachment,String> fileRenamesTo, Map options) 
    throws AccessControlException, WriteFilesException, WriteEntryDataException, ReservedByAnotherUserException {
    	long begin = System.nanoTime();
    	meCount.incrementAndGet();
        FolderEntry entry = loadEntryStrict(folderId, entryId);   	
        if (inputData.exists("title") &&
        		!inputData.getSingleValue("title").equals(entry.getTitle())) { 
        	// This is a request to rename the entry. We must also check that the user has the right to rename it
        	checkAccess(entry, FolderOperation.renameEntry);
        }
        //Must have modify rights, too
		try {
			checkAccess(entry, FolderOperation.modifyEntry);
		} catch (AccessControlException e) {
			checkAccess(entry, FolderOperation.modifyEntryFields);
			inputData.setFieldsOnly(true);
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
    	
    	// (Bug #880839) JK - With the added logic for retrying the request upon failure due to
    	// optimistic locking error, we now have to ensure that the supplied input stream can
    	// be read multiple times across multiple retries and database transaction. 
    	// We accomplish it by delaying the close of input stream in the lower level code. 
    	// We instruct the SimpleMultipartFile object to not close associated input stream
    	// in the usual place.
    	if(fileItems != null) {
    		for(Object fileItem:fileItems.values()) {
    			if(fileItem instanceof SimpleMultipartFile)
    				((SimpleMultipartFile)fileItem).setDeferCloseTilForced(true);
    		}
    	}
    	
    	try {
	        int tryMaxCount = 1 + SPropsUtil.getInt("select.database.transaction.retry.max.count", ObjectKeys.SELECT_DATABASE_TRANSACTION_RETRY_MAX_COUNT);
	        int tryCount = 0;
	        while(true) {
	        	tryCount++;
	        	try {
	        	   	processor.modifyEntry(folder, entry, inputData, fileItems, delAtts, fileRenamesTo, options);
	        	   	break; // successful transaction
	        	}
	        	catch(WriteEntryDataException e) {
	        		try {
		        		Exception cause = e.getErrors().getProblems().get(0).getException();
		        		if(cause instanceof HibernateOptimisticLockingFailureException) {
			        		if(tryCount < tryMaxCount) {
			        			if(logger.isDebugEnabled())
			        				logger.warn("(" + tryCount + ") 'modify entry' failed due to wrapped optimistic locking failure - Retrying in new transaction", cause);
			        			else 
			        				logger.warn("(" + tryCount + ") 'modify entry' failed due to wrapped optimistic locking failure - Retrying in new transaction: " + cause.toString());
			        			getCoreDao().refresh(folder);
			        			getCoreDao().refresh(entry);
			        		}
			        		else {
		        				logger.error("(" + tryCount + ") 'modify entry' failed due to wrapped optimistic locking failure - Aborting", cause);
			        			throw e;
			        		}
		        		}
		        		else {
		        			throw e;
		        		}
	        		}
	        		catch(Exception exc) {
	        			throw e; // Re-throw the original exception.
	        		}
	        	}
	        	catch(HibernateOptimisticLockingFailureException e) {
	        		if(tryCount < tryMaxCount) {
	        			if(logger.isDebugEnabled())
	        				logger.warn("(" + tryCount + ") 'modify entry' failed due to optimistic locking failure - Retrying in new transaction", e);
	        			else 
	        				logger.warn("(" + tryCount + ") 'modify entry' failed due to optimistic locking failure - Retrying in new transaction: " + e.toString());
	        			getCoreDao().refresh(folder);
	        			getCoreDao().refresh(entry);
	        		}
	        		else {
        				logger.error("(" + tryCount + ") 'modify entry' failed due to optimistic locking failure - Aborting", e);
	        			throw e;
	        		}
	        	}
	        }    
    	}
    	finally {
    		// Now, we must close the input stream associated with the client request,
    		// which has been delayed in order to accommodate retry logic.
        	if(fileItems != null) {
        		for(Object fileItem:fileItems.values()) {
        			if(fileItem instanceof SimpleMultipartFile)
        				((SimpleMultipartFile)fileItem).forceClose();
        		}
        	}
    	}
    	
    	end(begin, "modifyEntry");
    }   
    
    //no transaction
    @Override
	public void modifyEntry(Long folderId, Long entryId, String fileDataItemName, String fileName, InputStream content, Map options)
	throws AccessControlException, WriteFilesException, WriteEntryDataException, ReservedByAnotherUserException {
    	MultipartFile mf = new SimpleMultipartFile(fileName, content);
    	Map<String, MultipartFile> fileItems = new HashMap<String, MultipartFile>();
    	if(fileDataItemName == null)
    		fileDataItemName = ObjectKeys.FILES_FROM_APPLET_FOR_BINDER + "1";
    	fileItems.put(fileDataItemName, mf);
    	modifyEntry(folderId, entryId, new EmptyInputData(), fileItems, null, null, options);
    }


    @Override
	public Map getEntries(Long folderId, Map searchOptions) {
        Folder folder = loadFolder(folderId);
        //search query does access checks
        String[] types = entryTypes;
        if ((null != searchOptions) &&
        		searchOptions.containsKey(ObjectKeys.SEARCH_INCLUDE_NESTED_ENTRIES) &&
                (!((Boolean)searchOptions.get(ObjectKeys.SEARCH_INCLUDE_NESTED_ENTRIES)))) {
            // Use a fake entry type that won't actually match anything
            types = new String[] {"fakeEntry"};
        }
        return loadProcessor(folder).getBinderEntries(folder, types, searchOptions);

    }
    
    @Override
	public void getEntryPrincipals(List entries) {
	    SearchUtils.extendPrincipalsInfo(entries, getProfileDao(), Constants.CREATORID_FIELD);
    }
    
    
    @Override
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

    @Override
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
        	
	        Hits hits = getRecentEntries(folders, SearchUtils.fieldNamesList(Constants.BINDER_ID_FIELD,Constants.DOCID_FIELD,Constants.LASTACTIVITY_FIELD));
	        if (hits != null) {
	        	Map<String, Counter> unseenCounts = new HashMap();
		        Date modifyDate = new Date();
		        for (int i = 0; i < hits.length(); i++) {
					String folderIdString = (String) hits.doc(i).get(Constants.BINDER_ID_FIELD);
					String entryIdString = (String) hits.doc(i).get(Constants.DOCID_FIELD);
					Long entryId = null;
					if (entryIdString != null && !entryIdString.equals("")) {
						entryId = new Long(entryIdString);
					}
					modifyDate = ((Date) hits.doc(i).get(Constants.LASTACTIVITY_FIELD));
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
 
    protected Hits getRecentEntries(Collection<Folder> folders, List<String> fieldNames) {
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
        			so.getBaseAclQueryStr(), so.getExtendedAclQueryStr(), Constants.SEARCH_MODE_NORMAL, so.getLuceneQuery(), fieldNames, so.getSortBy(),0,0);
        	//results = instreamSession.search(so.getQueryString(),so.getSortBy(),0,0);
        } catch (Exception e) {
        	logger.warn("Exception throw while searching in getRecentEntries: " + e.toString());
        }
        finally {
            luceneSession.close();
        }
        return results;
    }
           
    @Override
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
    @Override
	public FolderEntry getEntry(Long folderId, Long entryId) {
        FolderEntry entry = loadEntry(folderId, entryId);
        AccessUtils.readCheck(entry);
        return entry;
    }
    @Override
	public FolderEntry getEntryWithoutAccessCheck(Long folderId, Long entryId) {
        FolderEntry entry = loadEntry(folderId, entryId);
        return entry;
    }
    @Override
	public FolderEntry getEntry(Long folderId, String entryNumber) {
    	Folder folder = getFolder(folderId);
    	String sortKey = HKey.getSortKeyFromEntryNumber(folder.getEntryRootKey(), entryNumber);
    	FolderEntry entry = getFolderDao().loadFolderEntry(sortKey, folder.getZoneId());
        AccessUtils.readCheck(entry);
        return entry;
    }

    @Override
	public Map getEntryTree(Long folderId, Long entryId) {
    	return getEntryTree(folderId, entryId, false);
    }
    @Override
	public Map getEntryTree(Long folderId, Long entryId, boolean includePreDeleted) {
    	//does read check
        FolderEntry entry = getEntry(folderId, entryId);   	
        Folder folder = entry.getParentFolder();
        FolderCoreProcessor processor=loadProcessor(folder);
        return processor.getEntryTree(folder, entry, includePreDeleted);
    }
    @Override
	public SortedSet<FolderEntry>getEntries(Collection<Long>ids) {
    	return getEntries(ids, Boolean.TRUE);
    }
    @Override
	public SortedSet<FolderEntry>getEntries(Collection<Long>ids, boolean doCheckAccess) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new EntryComparator(user.getLocale(), EntryComparator.SortByField.pathName);
       	TreeSet<FolderEntry> sEntries = new TreeSet<FolderEntry>(c);
       	List<FolderEntry>entries = getCoreDao().loadObjects(ids, FolderEntry.class, RequestContextHolder.getRequestContext().getZoneId());
    	for (FolderEntry e:entries) {
            try {
            	if (doCheckAccess) {
            		AccessUtils.readCheck(e);
            	}
            	sEntries.add(e);
            } catch (Exception ignoreMe) {};
    	}
    	return sEntries;
    }

    //inside write transaction    
    @Override
	public void restoreEntry(Long parentFolderId, Long entryId, Object renameData) throws WriteEntryDataException, WriteFilesException {
    	restoreEntry(parentFolderId, entryId, renameData, true);
    }
    @Override
	public void restoreEntry(Long parentFolderId, Long entryId, Object renameData, boolean reindex) throws WriteEntryDataException, WriteFilesException {
    	restoreEntry(parentFolderId, entryId, renameData, true, null, reindex);
    }
    //inside write transaction    
	@Override
	public void restoreEntry(Long folderId, Long entryId, Object renameData, boolean deleteMirroredSource, Map options) throws WriteEntryDataException, WriteFilesException {
    	restoreEntry(folderId, entryId, renameData,deleteMirroredSource, options, true);
    }
	
	// in write transaction
	@Override
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
        	processor.updateParentModTime(folder, options, reindex);
        }
    }
    
    //inside write transaction    
    @Override
	public void preDeleteEntry(Long parentFolderId, Long entryId, Long userId) {
    	preDeleteEntry(parentFolderId, entryId, userId, true);
    }
    @Override
	public void preDeleteEntry(Long parentFolderId, Long entryId, Long userId, boolean reindex) {
    	preDeleteEntry(parentFolderId, entryId, userId, true, null, reindex);
    }
    //inside write transaction    
	@Override
	public void preDeleteEntry(Long folderId, Long entryId, Long userId, boolean deleteMirroredSource, Map options) {
    	preDeleteEntry(folderId, entryId, userId, deleteMirroredSource, options, true);
    }
	
	// in write transaction
	@Override
	public void preDeleteEntry(Long folderId, Long entryId, Long userId, boolean deleteMirroredSource, Map options, boolean reindex) {
    	deCount.incrementAndGet();
        FolderEntry entry = loadEntry(folderId, entryId);

        // If somebody besides the requesting user has the entry
        // reserved...
        HistoryStamp reservation = entry.getReservation();
        if ((null != reservation) && (!(reservation.getPrincipal().getId().equals(userId)))) {
        	// ...we don't allow it to be trashed.
        	throw new ReservedByAnotherUserException(entry);
        }
        
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
        	processor.updateParentModTime(folder, options, reindex);
        }
    }
    
    //inside write transaction    
    @Override
	public void updateModificationStamp(Long parentFolderId, Long entryId) {
    	updateModificationStamp(parentFolderId, entryId, true);
    }
	@Override
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
    @Override
	public void deleteEntry(Long parentFolderId, Long entryId) {
    	try {
    		deleteEntry(parentFolderId, entryId, true, null);
    	} catch(WriteFilesException e) {
    		//Failure to delete the files attached to this entry are ignored for this type of call
    	}
    }
    //no transaction    
    @Override
	public void deleteEntry(Long folderId, Long entryId, boolean deleteMirroredSource, Map options) 
			throws WriteFilesException {
    	deCount.incrementAndGet();
        FolderEntry entry = loadEntry(folderId, entryId);   	
        checkAccess(entry, FolderOperation.deleteEntry);
        
        // If somebody besides the requesting user has the entry
        // reserved...
        HistoryStamp reservation = entry.getReservation();
        if ((null != reservation) && (!(reservation.getPrincipal().getId().equals(RequestContextHolder.getRequestContext().getUserId())))) {
        	// ...we don't allow it to be deleted.
        	throw new ReservedByAnotherUserException(entry);
        }
        
        Folder folder = entry.getParentFolder();
        FolderCoreProcessor processor=loadProcessor(folder);
        try {
        	processor.deleteEntry(folder, entry, deleteMirroredSource, options);
		} catch(WriteFilesException e) {
			//The files attached to this entry could not be deleted
			if (options.containsKey(ObjectKeys.INPUT_OPTION_PROPAGATE_ERRORS) && 
					(boolean)options.get(ObjectKeys.INPUT_OPTION_PROPAGATE_ERRORS)) {
				throw e;
			}
		}
    }
    //inside write transaction    
    @Override
	public FolderEntry moveEntry(Long folderId, Long entryId, Long destinationId, String[] toFileNames, Map options) throws WriteFilesException {
        FolderEntry entry = loadEntry(folderId, entryId);   	
        checkAccess(entry, FolderOperation.moveEntry);
        
        // If somebody besides the requesting user has the entry
        // reserved...
        HistoryStamp reservation = entry.getReservation();
        if ((null != reservation) && (!(reservation.getPrincipal().getId().equals(RequestContextHolder.getRequestContext().getUserId())))) {
        	// ...we don't allow it to be moved.
        	throw new ReservedByAnotherUserException(entry);
        }
        
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

        FolderEntry newEntry;
        if ((folder.isMirrored() || destination.isMirrored())) {
			//To move to and from mirrored folders, copy the entry to the destination folder then delete the original entry
			newEntry = (FolderEntry) processor.copyEntry(folder, entry, destination, toFileNames, options);
			//See if the original entity was shared
			List<Long> shareItemIds = getProfileDao().getShareItemIdsByEntity(entry);
			if (!shareItemIds.isEmpty()) {
				//Move the share items over to the new entry
				getProfileDao().changeSharedEntityId(shareItemIds, newEntry);
				//After changing the shareItems, we must re-index to get the new share ACLs correct
				Set<Entry> entriesToIndex = newEntry.getChildWorkAreas();
				entriesToIndex.add(newEntry);
				processor.indexEntries(entriesToIndex);
			}
			try {
				processor.deleteEntry(folder, entry, true, options);
			} catch(WriteFilesException wfe) {
				//The files attached to this entry could not be deleted
				if (options.containsKey(ObjectKeys.INPUT_OPTION_PROPAGATE_ERRORS) && 
						(boolean)options.get(ObjectKeys.INPUT_OPTION_PROPAGATE_ERRORS)) {
		        	// Since the new entry was created, delete it.
	        		try {deleteEntry(destinationId, newEntry.getId(), true, new HashMap());}
	        		catch(Exception e) {/* Ignored. */}	// Any further errors while trying to delete the entry are ignored
					throw wfe;
				}
			}
		} else {
			processor.moveEntry(folder, entry, destination, toFileNames, options);
            newEntry = entry;
		}
        
        processor.updateParentModTime(folder, options);
        if(destination != folder)
        	processor.updateParentModTime(destination, options);

        return newEntry;
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

	// no transaction
	@Override
	public void copyFolderEntries(Long sourceId, Long destinationId) throws NotSupportedException {
        Folder source =  loadFolder(sourceId);
        Folder destination =  loadFolder(destinationId);
		//See if there is enough quota to do this
		if (loadProcessor(source).checkMoveBinderQuota(source, destination)) {
    		//We must guard against invalid copy attempts (such as complex entries copied to mirrored folder)
			//This type of request could have invalid entries, so check each one
			Map getEntriesOptions = new HashMap();
			//Specify if this request is to copy children binders, too.
      		Map folderEntries = getEntries(source.getId(), getEntriesOptions);
	      	List<Map> searchEntries = (List)folderEntries.get(ObjectKeys.SEARCH_ENTRIES);

			for (Map se : searchEntries) {
				String entryIdStr = (String)se.get(Constants.DOCID_FIELD);
				if (entryIdStr != null && !entryIdStr.equals("")) {
    				Long entryId = Long.valueOf(entryIdStr);
    				Entry entry = getEntry(null, entryId);
					try {
			    		if (!source.isAclExternallyControlled() && destination.isAclExternallyControlled()) {
			    			//Make sure this copy is compatible
			    			BinderHelper.copyEntryCheckMirrored(source, entry, destination);
			    		}
		    			copyEntry(source.getId(), entryId, destination.getId(), null, null);
					} catch(Exception e) {
						//This entry cannot be copied, so don't copy this binder
						throw new NotSupportedException("errorcode.notsupported.copyEntry.complexEntryToMirrored." + (destination.isAclExternallyControlled() ? "net" : "mirrored"));
					}
				}
    		}
		} else {
			throw new NotSupportedException(NLT.get("quota.binder.exceeded"));
		}
	}

    //inside write transaction    
    @Override
	public FolderEntry copyEntry(Long folderId, Long entryId, Long destinationId, String[] toFileNames, Map options) throws WriteFilesException {
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
		
		processor.updateParentModTime(destination, options);
		
		return entryCopy;
    }
    
    /**
     * Stores a user's subscriptions to an entry.
     * 
     * @param user
     * @param folderId
     * @param entryId
     * @param styles
     */
    //inside write transaction    
    @Override
	public void setSubscription(User user, Long folderId, Long entryId, Map<Integer,String[]> styles) {
		FolderEntry entry;
		boolean deleteSub = ((null == styles) || styles.isEmpty());
		if (deleteSub)
		     entry = getEntryWithoutAccessCheck(folderId, entryId);	// We allow a subscription to be deleted without access to the item.
		else entry = getEntry(                  folderId, entryId);	// getEntry() does read check.
		
		// Only subscribe at top level, no comments.
		if (!(entry.isTop())) {
			entry = entry.getTopEntry();
		}
		Subscription s = getProfileDao().loadSubscription(user.getId(), entry.getEntityIdentifier());
		
		// Digest doesn't make sense here - only individual messages
		// are sent. 
		if (deleteSub) {
			if (s != null) {
				getCoreDao().delete(s);
				
				// If this is the last subscription, let entry know.
				List subs = getCoreDao().loadSubscriptionByEntity(entry.getEntityIdentifier());
				if (subs.size() == 1) {
					entry.setSubscribed(false);				
				}
			}
		}
		else {
			if (s == null) {
				s = new Subscription(user.getId(), entry.getEntityIdentifier());
				s.setStyles(styles);
				getCoreDao().save(s);
			}
			else {
				s.setStyles(styles);
			}
			entry.setSubscribed(true);
		}
    }
    
    //inside write transaction    
    @Override
	public void setSubscription(Long folderId, Long entryId, Map<Integer,String[]> styles) {
    	// Always use the initial form of the method.
    	setSubscription(RequestContextHolder.getRequestContext().getUser(), folderId, entryId, styles);
    }

    /**
     * Returns a user's subscriptions to an entry.
     * 
     * @param user
     * @param entry
     * 
     * @return
     */
    @Override
	public Subscription getSubscription(User user, FolderEntry entry) {
		// Only subscribe at top level, no comments.
		if (!(entry.isTop())) {
			entry = entry.getTopEntry();
		}
		return getProfileDao().loadSubscription(user.getId(), entry.getEntityIdentifier());
    }

    @Override
	public Subscription getSubscription(FolderEntry entry) {
    	// Always use the initial form of the method.
		return getSubscription(RequestContextHolder.getRequestContext().getUser(), entry);
    }

	@Override
	public Collection<Tag> getTags(FolderEntry entry) {
		//have Entry - so assume read access
		//bulk load tags
        return getCoreDao().loadEntityTags(entry.getEntityIdentifier(), RequestContextHolder.getRequestContext().getUser().getEntityIdentifier());
	}
	
    //inside write transaction    
	@Override
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
	@Override
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
	@Override
	public void deleteAllVotes(Long binderId, Long entryId) 
			throws AccessControlException, ReservedByAnotherUserException, WriteFilesException, WriteEntryDataException {
	   	FolderEntry entry = loadEntry(binderId, entryId);
	   	checkAccess(entry, FolderOperation.deleteEntry);
	   	
	   	Document defDoc = entry.getEntryDefDoc();
	   	Element surveyItem = DefinitionHelper.findDataItem("survey", defDoc);
	   	if (surveyItem != null) {
	   		//This entry has a survey element
	   		String attributeName = DefinitionHelper.getItemProperty(surveyItem, "name");
			CustomAttribute surveyAttr = entry.getCustomAttribute(attributeName);
			if (surveyAttr == null || surveyAttr.getValue() == null) {
				return;
			}
			
			Survey surveyAttrValue = ((Survey)surveyAttr.getValue());
			SurveyModel survey = surveyAttrValue.getSurveyModel();
			if (survey == null) {
				return;
			}
			
			survey.removeAllVotes();
			survey.setVoteRequest();
			
			Map formData = new HashMap(); 
			formData.put(attributeName, surveyAttrValue.toString());
			addVote(binderId, entryId, new MapInputData(formData), null);

	 	    loadProcessor(entry.getParentFolder()).indexEntry(entry);
	   	}
	}
	
    //inside write transaction    
	@Override
	public void setEntryDef(Long folderId, Long entryId, String entryDef) {
		FolderEntry entry = getEntry(folderId, entryId);
		entry.setEntryDef(definitionModule.getDefinition(entryDef));
	}
	
	//Change entry def type
    //inside write transaction    
	@Override
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
	@Override
	public void setUserRating(Long folderId, Long entryId, long value) {
		//getEntry does read check
		FolderEntry entry = getEntry(folderId, entryId);
		setRating(entry, value);
	}
    //inside write transaction    
	@Override
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
	@Override
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
    @Override
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
   @Override
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
   @Override
public FolderEntry getLibraryFolderEntryByFileName(Folder fileFolder, String title)
	throws AccessControlException {
       	try {
    		Long id = getCoreDao().findFileNameEntryId(fileFolder, title);	// Won't throw a NoObjectByTheIdException if title matches a nested folder name.  It simply returns null.
    		//getEntry does read check
    		return ((null == id) ? null : getEntry(fileFolder.getId(), id));
    	} catch (NoObjectByTheIdException no) {
    		return null;
    	}
    }
    //this is for wiki links where normalize title is used
    @Override
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
    @Override
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

	@Override
	public Set<FolderEntry> getFolderEntryByTitle(Long folderId, String title) throws AccessControlException {
		Set<FolderEntry> entries = new HashSet<>();
		Folder folder = null;
		try {
			folder = getFolder(folderId);
		} catch(NoFolderByTheIdException e) {
			return entries;
		}
		if (folder == null) return entries;
		List<FolderEntry> results = getFolderDao().loadEntries(folder, new FilterControls(ObjectKeys.FIELD_ENTITY_TITLE, title));
		for (FolderEntry entry: results) {
			try {
				AccessUtils.readCheck(entry);
				entries.add(entry);
			} catch (AccessControlException ac) {}
		}
		return entries;
	}

	@Override
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
    
    @Override
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
	
    @Override
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

    @Override
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
    @Override
	public void deleteEntryWorkflow(Long folderId, Long entryId, String definitionId) 
		throws AccessControlException {
       	//start a workflow on an entry
    	FolderEntry entry = loadEntry(folderId, entryId);
    	checkAccess(entry, FolderOperation.deleteEntryWorkflow);
    	Definition def = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
        FolderCoreProcessor processor = loadProcessor(entry.getParentFolder());
        processor.deleteEntryWorkflow(entry.getParentBinder(), entry, def);

    }
    @Override
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
   @Override
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
	@Override
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

    @Override
	public void setWorkflowResponse(Long folderId, Long entryId, Long stateId, InputDataAccessor inputData) {
        FolderEntry entry = loadEntry(folderId, entryId);   	
        Folder folder = entry.getParentFolder();
        FolderCoreProcessor processor=loadProcessor(folder);
        Boolean canModifyEntry = testAccess(entry, FolderOperation.modifyEntry);
        processor.setWorkflowResponse(folder, entry, stateId, inputData, canModifyEntry);
    }
    
   //called by scheduler to complete folder deletions
    //no transaction
    @Override
	public synchronized void cleanupFolders() {
		FilterControls fc = new FilterControls();
		fc.add("deleted", Boolean.TRUE);
		ObjectControls objs = new ObjectControls(Folder.class, new String[] {"id"});
		List<Object> folders = getCoreDao().loadObjects(objs, fc, RequestContextHolder.getRequestContext().getZoneId());
		if(traceEnabled)
			logger.trace("Checking for folders marked as deleted to clean up");
		int success = 0;
		int fail = 0;
		int invalid = 0;
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
				if(f.isDeleted()) {
					FolderCoreProcessor processor = loadProcessor(f);
					// (Bug 815697) Don't add change log for entry deletion when it is caused by deletion of parent folder
					// (Bug #885763) When Fir client requests folder deletion, it is the first phase that goes and deletes
					// mirrored source if requested. Therefore, there is NO reason to ever do it during the second phase.
					processor.deleteBinder(f, false, null, true);
					success++;
				}
				else {
					// Basically, the object loaded from the database is showing different data than the where
					// clause specified for the initial match. This is impossible unless that database value
					// was changed between the time the query was executed and the time the matching object was
					// subsequently loaded. I'm not aware of any code execution path in Filr that would change 
					// the delete column value from TRUE (1) back to FALSE (0). Another possibility would be
					// that something is wrong with the first or second level cache that it returned incorrect
					// or stale information.
					// So while I have no clue how and why this can happen, apparently and to our amusement,
					// this problem was observed at least twice from two different customers. So I'm adding
					// precautionary check that should help avoid executing the wrong code path.
					// See (Bug #885763) for more details.
					logger.warn("Cannot clean up folder [" + f.getPathName() + "] (id=" + folderId + ") because it is in invalid state");
					invalid++;
				}
				getCoreDao().evict(f);
			} catch (Exception ex) {
				fail++;
				logger.error("Error cleaning up folder " + folderId, ex);
			}
		}
		if(folders != null && folders.size() > 0) {
			logger.info("Folders cleaned up: success=" + success + ", fail=" + fail + ", invalid=" + invalid);
		}
		else {
			if(debugEnabled)
				logger.debug("No folders to clean up");
		}
	}


    @Override
	public IndexErrors indexEntry(FolderEntry entry, boolean includeReplies) {
    	return indexEntry(entry, includeReplies, false);
    }
    
	@Override
	public IndexErrors indexEntry(FolderEntry entry, boolean includeReplies, boolean skipFileContentIndexing) {
    	FolderCoreProcessor processor = loadProcessor(entry.getParentFolder());
    	IndexErrors errors = processor.indexEntry(entry, skipFileContentIndexing);
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
    
    @Override
	public org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, Entry entry, Collection tags) {
		FolderCoreProcessor processor = loadProcessor((Folder)binder);
		return processor.buildIndexDocumentFromEntry(binder, entry, tags);
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

    @Override
	public FolderEntry refreshFromRepository(FolderEntry fileEntry) {
        if (!fileEntry.getParentFolder().isMirrored()) {
            for (Attachment attachment : fileEntry.getAttachments()) {
                if (attachment instanceof FileAttachment) {
                    FileAttachment fa = (FileAttachment) attachment;
                    SizeMd5Pair sizeMd5 = getFileModule().getFileInfoFromRepository(null, fileEntry, fa);
                    if (sizeMd5!=null) {
                        fa.getFileItem().setLength(sizeMd5.getSize());
                        fa.getFileItem().setMd5(sizeMd5.getMd5());
                    }
                }
            }

            // Trigger Hibernate to commit the modified FileItem to the database.
            getTransactionTemplate().execute(new TransactionCallback<Object>() {
                @Override
                public Object doInTransaction(final TransactionStatus status) {
                    return null;
                }
            });
            indexEntry(fileEntry, false);
        }
        return fileEntry;
    }

    @Override
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

    @Override
	public Date getLastFullSyncCompletionTime(Long folderId) {
        Folder topMostMirroredFolder = FolderUtils.getTopMostMirroredFolder(getFolder(folderId));
        if (topMostMirroredFolder!=null) {
        	return getLastFullSyncCompletionTime(topMostMirroredFolder);
        }
        return null;
    }

    protected Date getLastFullSyncCompletionTime(Folder topMostMirroredFolder) {
        BinderState binderState = (BinderState) getCoreDao().load(BinderState.class, topMostMirroredFolder.getId());
        if (binderState!=null) {
            return binderState.getLastFullSyncCompletionTime();
        }
        return null;
    }
    
    protected Date getUpdatedToDate(Folder topMostMirroredFolder) {
        BinderState binderState = (BinderState) getCoreDao().load(BinderState.class, topMostMirroredFolder.getId());
        if (binderState!=null) {
            return binderState.getUpdatedToDate();
        }
        return null;
    }
}
