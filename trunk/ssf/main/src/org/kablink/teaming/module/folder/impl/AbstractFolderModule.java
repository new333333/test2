/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
import org.dom4j.Document;
import org.kablink.teaming.ConfigurationException;
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
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.jobs.FolderDelete;
import org.kablink.teaming.jobs.ZoneSchedule;
import org.kablink.teaming.lucene.Hits;
import org.kablink.teaming.module.binder.BinderModule;
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
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.EntityIndexUtils;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.module.workflow.WorkflowModule;
import org.kablink.teaming.module.workflow.WorkflowProcessUtils;
import org.kablink.teaming.module.workflow.WorkflowUtils;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.search.LuceneReadSession;
import org.kablink.teaming.search.QueryBuilder;
import org.kablink.teaming.search.SearchObject;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SimpleMultipartFile;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.web.util.ExportHelper;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Jong Kim
 */
public abstract class AbstractFolderModule extends CommonDependencyInjection 
implements FolderModule, AbstractFolderModuleMBean, ZoneSchedule {
	protected String[] ratingAttrs = new String[]{"id.entityId", "id.entityType"};
	protected String[] entryTypes = {Constants.ENTRY_TYPE_ENTRY};
    protected DefinitionModule definitionModule;
    protected FileModule fileModule;
    protected BinderModule binderModule;
    
    AtomicInteger aeCount = new AtomicInteger();
    AtomicInteger meCount = new AtomicInteger();
    AtomicInteger deCount = new AtomicInteger();
    AtomicInteger arCount = new AtomicInteger();


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
	
	private TransactionTemplate transactionTemplate;
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
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
   		return (FolderDelete)ReflectHelper.getInstance(org.kablink.teaming.jobs.DefaultFolderDelete.class);
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
		}
	}
	public void checkAccess(Folder folder, FolderOperation operation) throws AccessControlException {
		switch (operation) {
			case addEntry: 
			case synchronize:
				getAccessControlManager().checkOperation(folder, WorkAreaOperation.CREATE_ENTRIES);
				break;
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
			default:
				throw new NotSupportedException(operation.toString(), "checkAccess");
				
		}
	}
	public boolean testAccess(FolderEntry entry, FolderOperation operation) {
		try {
			checkAccess(entry, operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}
	public void checkAccess(FolderEntry entry, FolderOperation operation) throws AccessControlException {
		switch (operation) {
			case readEntry:
				AccessUtils.readCheck(entry);   
				break;
			case modifyEntry:
			case addEntryWorkflow:
			case deleteEntryWorkflow:
			case reserveEntry:
			case copyEntry:
			case moveEntry:
				AccessUtils.operationCheck(entry, WorkAreaOperation.MODIFY_ENTRIES);   
				break;
			case modifyEntryFields:
				AccessUtils.modifyFieldCheck(entry);   
				break;
			case restoreEntry:
			case preDeleteEntry:
			case deleteEntry:
				AccessUtils.operationCheck(entry, WorkAreaOperation.DELETE_ENTRIES);   		
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
			default:
				throw new NotSupportedException(operation.toString(), "checkAccess");
					
		}

	}
	
	
	protected Folder loadFolder(Long folderId)  {
        Folder folder = getFolderDao().loadFolder(folderId, RequestContextHolder.getRequestContext().getZoneId());
		if (folder.isDeleted()) throw new NoBinderByTheIdException(folderId);
		return folder;

	}
	protected FolderEntry loadEntry(Long folderId, Long entryId) {
		//folderId may be null
        FolderEntry entry = getFolderDao().loadFolderEntry(folderId, entryId, RequestContextHolder.getRequestContext().getZoneId());             
		if (entry.isDeleted() || entry.getParentBinder().isDeleted()) throw new NoFolderEntryByTheIdException(entryId);
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
			getAccessControlManager().checkOperation(folder, WorkAreaOperation.READ_ENTRIES);
		} catch(AccessControlException ace) {
			//Can't read it, so try seeing if the folder title is readable
			try {
				getAccessControlManager().checkOperation(folder, WorkAreaOperation.VIEW_BINDER_TITLE);
			} catch(AccessControlException ace2) {
				throw ace;
			}
		}
		return folder;        
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
    	long begin = System.currentTimeMillis();
    	aeCount.incrementAndGet();

        Folder folder = loadFolder(folderId);
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
	public FolderEntry addReply(Long folderId, Long parentId, String definitionId, 
    		InputDataAccessor inputData, Map fileItems, Map options) 
			throws AccessControlException, WriteFilesException, WriteEntryDataException {
		long begin = System.currentTimeMillis();
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
			Definition parentDef = entry.getEntryDef();
			Document defDoc = parentDef.getDefinition();
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
    	long begin = System.currentTimeMillis();
    	meCount.incrementAndGet();
        FolderEntry entry = loadEntry(folderId, entryId);   	
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
					String folderIdString = hits.doc(i).getField(Constants.BINDER_ID_FIELD).stringValue();
					String entryIdString = hits.doc(i).getField(Constants.DOCID_FIELD).stringValue();
					Long entryId = null;
					if (entryIdString != null && !entryIdString.equals("")) {
						entryId = new Long(entryIdString);
					}
					try {
						modifyDate = DateTools.stringToDate(hits.doc(i).getField(Constants.LASTACTIVITY_FIELD).stringValue());
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
    	QueryBuilder qb = new QueryBuilder(true);
    	SearchObject so = qb.buildQuery(crit.toQuery());
    	
    	if(logger.isDebugEnabled())
    		logger.debug("Query is: " + so.getQueryString());
    	
    	LuceneReadSession luceneSession = getLuceneSessionFactory().openReadSession();
    	//RemoteInStreamSession instreamSession = getInstreamSessionFactory().openSession();
        
        try {
        	results = luceneSession.search(so.getQuery(),so.getSortBy(),0,0);
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
    @SuppressWarnings("unchecked")
	public void restoreEntry(Long folderId, Long entryId, Object renameData, boolean deleteMirroredSource, Map options) throws WriteEntryDataException, WriteFilesException {
    	restoreEntry(folderId, entryId, renameData,deleteMirroredSource, options, true);
    }
    @SuppressWarnings("unchecked")
	public void restoreEntry(Long folderId, Long entryId, Object renameData, boolean deleteMirroredSource, Map options, boolean reindex) throws WriteEntryDataException, WriteFilesException {
    	deCount.incrementAndGet();
    	
    	// Is the entry preDeleted and located in a non-mirrored
    	// Folder?
        FolderEntry entry = loadEntry(folderId, entryId);
        Folder folder = loadFolder(folderId);
        if ((null != entry)  &&    entry.isPreDeleted() &&
        	(null != folder) && (!(folder.isMirrored()))) {
			// Yes!  Validate we can restore it...
        	checkAccess(entry, FolderOperation.restoreEntry);
        	
	        // ...restore it...
        	entry.setPreDeleted(null);
        	entry.setPreDeletedWhen(null);
        	entry.setPreDeletedBy(null);
        	
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
        	}
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
    @SuppressWarnings("unchecked")
	public void preDeleteEntry(Long folderId, Long entryId, Long userId, boolean deleteMirroredSource, Map options) {
    	preDeleteEntry(folderId, entryId, userId, deleteMirroredSource, options, true);
    }
    @SuppressWarnings("unchecked")
	public void preDeleteEntry(Long folderId, Long entryId, Long userId, boolean deleteMirroredSource, Map options, boolean reindex) {
    	deCount.incrementAndGet();
        FolderEntry entry = loadEntry(folderId, entryId);
        Folder folder = loadFolder(folderId);
        if ((null != entry) && (null != folder) && (!(folder.isMirrored()))) {
        	checkAccess(entry, FolderOperation.preDeleteEntry);
        	
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
    }
    //inside write transaction    
    public void moveEntry(Long folderId, Long entryId, Long destinationId, Map options) {
        FolderEntry entry = loadEntry(folderId, entryId);   	
        checkAccess(entry, FolderOperation.moveEntry);
        Folder folder = entry.getParentFolder();
        FolderCoreProcessor processor=loadProcessor(folder);
                
        Folder destination =  loadFolder(destinationId);
        checkAccess(destination, FolderOperation.addEntry);
        processor.moveEntry(folder, entry, destination, options);
    }
    //inside write transaction    
    public FolderEntry copyEntry(Long folderId, Long entryId, Long destinationId, Map options) {
        FolderEntry entry = loadEntry(folderId, entryId);   	
        checkAccess(entry, FolderOperation.copyEntry);
        Folder folder = entry.getParentFolder();
        FolderCoreProcessor processor=loadProcessor(folder);
               
        Folder destination =  loadFolder(destinationId);
        checkAccess(destination, FolderOperation.addEntry);
        return (FolderEntry) processor.copyEntry(folder, entry, destination, options);
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
	public void setTag(Long binderId, Long entryId, String newTag, boolean community) {
		//read access checked by getEntry
		FolderEntry entry = getEntry(binderId, entryId);
		if (community) checkAccess(entry, FolderOperation.manageTag);
		if (Validator.isNull(newTag)) return;
		Collection<String> newTags = TagUtil.buildTags(newTag);		
		if (newTags.size() == 0) return;
		User user = RequestContextHolder.getRequestContext().getUser();
		EntityIdentifier uei = user.getEntityIdentifier();
		EntityIdentifier eei = entry.getEntityIdentifier();
		for (String tagName:newTags) {
			Tag tag = new Tag();
			//community tags belong to the binder - don't care who created it
		   	if (!community) tag.setOwnerIdentifier(uei);
		   	tag.setEntityIdentifier(eei);
		    tag.setPublic(community);
		   	tag.setName(tagName);
			getCoreDao().save(tag);
	   	}
 	    loadProcessor(entry.getParentFolder()).indexEntry(entry);
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
    public void reserveEntry(Long folderId, Long entryId)
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
    		getFileModule().RefreshLocks(folder, entry);
    		
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
    public SortedSet<String> getSubfoldersTitles(Folder folder) {
    	//already have access to folder
    	TreeSet<String> titles = new TreeSet<String>();
   		
    	for(Object o : folder.getFolders()) {
    		Folder f = (Folder) o;
    		if (f.isDeleted() || f.isPreDeleted()) continue;
    		if(getAccessControlManager().testOperation(f, WorkAreaOperation.READ_ENTRIES) ||
    				getAccessControlManager().testOperation(f, WorkAreaOperation.VIEW_BINDER_TITLE))
    			titles.add(f.getTitle());
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
    		if(getAccessControlManager().testOperation(f, WorkAreaOperation.READ_ENTRIES) || 
    				getAccessControlManager().testOperation(f, WorkAreaOperation.VIEW_BINDER_TITLE))
    			subFolders.add(f);
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
   public void modifyWorkflowState(Long folderId, Long entryId, Long stateId, String toState) throws AccessControlException {
       FolderEntry entry = loadEntry(folderId, entryId);   	
       Folder folder = entry.getParentFolder();
       FolderCoreProcessor processor=loadProcessor(folder);
       //access checks - not a simple modify
       checkTransitionOutStateAllowed(entry, stateId);
       checkTransitionInStateAllowed(entry, stateId, toState);
       processor.modifyWorkflowState(folder, entry, stateId, toState);
    }
	public Map<String, String> getManualTransitions(FolderEntry entry, Long stateId) {
		WorkflowState ws = entry.getWorkflowState(stateId);
		Map result = WorkflowUtils.getManualTransitions(ws.getDefinition(), ws.getState());
		Map transitionData = new LinkedHashMap();
		if (testTransitionOutStateAllowed(entry, stateId)) {
			for (Iterator iter=result.entrySet().iterator(); iter.hasNext();) {
				Map.Entry me = (Map.Entry)iter.next();
					try {
						//access check
					AccessUtils.checkTransitionIn(entry.getParentBinder(), entry, ws.getDefinition(), (String)me.getKey());  
					transitionData.put(me.getKey(), me.getValue());
					} catch (AccessControlException ac) {};
			}
			return transitionData;
		} 
		//cannot transition out, so don't return anyting
		return Collections.EMPTY_MAP;
		
    }		

	public Map getWorkflowQuestions(FolderEntry entry, Long stateId) {
		if (testTransitionOutStateAllowed(entry, stateId)) {
			WorkflowState ws = entry.getWorkflowState(stateId);
        	return  WorkflowUtils.getQuestions(ws.getDefinition(), ws.getState());
        }
        return Collections.EMPTY_MAP;
    }		

    public void setWorkflowResponse(Long folderId, Long entryId, Long stateId, InputDataAccessor inputData) {
        FolderEntry entry = loadEntry(folderId, entryId);   	
        checkAccess(entry, FolderOperation.addReply);
        checkTransitionOutStateAllowed(entry, stateId);
        Folder folder = entry.getParentFolder();
        FolderCoreProcessor processor=loadProcessor(folder);
        processor.setWorkflowResponse(folder, entry, stateId, inputData);
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
    
	public void clearStatistics() {
		aeCount.set(0);
		meCount.set(0);
		deCount.set(0);
		arCount.set(0);
	}
	public int getAddEntryCount() {
		return aeCount.get();
	}
	public int getDeleteEntryCount() {
		return deCount.get();
	}
	public int getModifyEntryCount() {
		return meCount.get();
	}
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

}