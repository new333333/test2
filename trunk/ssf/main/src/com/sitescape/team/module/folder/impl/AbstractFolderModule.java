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
package com.sitescape.team.module.folder.impl;

import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.document.DateTools;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.comparator.BinderComparator;
import com.sitescape.team.comparator.EntryComparator;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.Attachment;
import com.sitescape.team.domain.AverageRating;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.NoFolderByTheIdException;
import com.sitescape.team.domain.NoFolderEntryByTheIdException;
import com.sitescape.team.domain.Rating;
import com.sitescape.team.domain.ReservedByAnotherUserException;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.Tag;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Visits;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.jobs.FolderDelete;
import com.sitescape.team.jobs.ZoneSchedule;
import com.sitescape.team.lucene.Hits;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.file.FileModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.folder.FileLockInfo;
import com.sitescape.team.module.folder.FilesLockedByOtherUsersException;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.folder.index.IndexUtils;
import com.sitescape.team.module.folder.processor.FolderCoreProcessor;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.shared.AccessUtils;
import com.sitescape.team.module.shared.EmptyInputData;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.search.LuceneSession;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.search.SearchObject;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SimpleMultipartFile;
import com.sitescape.team.util.TagUtil;
import com.sitescape.util.Validator;
import com.sitescape.util.search.Constants;
/**
 *
 * @author Jong Kim
 */
public abstract class AbstractFolderModule extends CommonDependencyInjection 
implements FolderModule, AbstractFolderModuleMBean, ZoneSchedule {
	protected String[] ratingAttrs = new String[]{"id.entityId", "id.entityType"};
	protected String[] entryTypes = {EntityIndexUtils.ENTRY_TYPE_ENTRY};
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
	

 	protected FolderDelete getDeleteProcessor(Workspace zone) {
 	   String jobClass = SZoneConfig.getString(zone.getName(), "folderConfiguration/property[@name='" + FolderDelete.DELETE_JOB + "']");
 	   if (Validator.isNull(jobClass)) jobClass = "com.sitescape.team.jobs.DefaultFolderDelete";
 	   try {
 		   Class processorClass = ReflectHelper.classForName(jobClass);
 		   FolderDelete job = (FolderDelete)processorClass.newInstance();
 		   return job;
 	   } catch (ClassNotFoundException e) {
 		   throw new ConfigurationException(
 				"Invalid FolderDelete class name '" + jobClass + "'",
 				e);
 	   } catch (InstantiationException e) {
 		   throw new ConfigurationException(
 				"Cannot instantiate FolderDelete of type '"
                     	+ jobClass + "'");
 	   } catch (IllegalAccessException e) {
 		   throw new ConfigurationException(
 				"Cannot instantiate FolderDelete of type '"
 				+ jobClass + "'");
 	   } 
 	   
  		
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
    	int hours = 24;
    	try {
    		hours = Integer.parseInt(hrsString);
    	} catch (Exception ex) {};
    	job.schedule(zone.getId(), hours);
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
			case changeEntryTimestamps:
				getAccessControlManager().checkOperation(folder, WorkAreaOperation.SITE_ADMINISTRATION);
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
			case modifyEntry:
			case addEntryWorkflow:
			case deleteEntryWorkflow:
			case reserveEntry:
			case copyEntry:
			case moveEntry:
				AccessUtils.modifyCheck(entry);   
				break;
			case deleteEntry:
				AccessUtils.deleteCheck(entry);   		
				break;
			case overrideReserveEntry:
				AccessUtils.overrideReserveEntryCheck(entry);
				break;
			case addReply:
		    	getAccessControlManager().checkOperation(entry.getParentBinder(), WorkAreaOperation.ADD_REPLIES);
		    	break;				
			case manageTag:
				getAccessControlManager().checkOperation(entry.getParentBinder(), WorkAreaOperation.ADD_COMMUNITY_TAGS);
				break;
			case report:
				getAccessControlManager().checkOperation(entry.getParentBinder(), WorkAreaOperation.GENERATE_REPORTS);
				break;
			default:
				throw new NotSupportedException(operation.toString(), "checkAccess");
					
		}

	}
	
	
	Folder loadFolder(Long folderId)  {
        Folder folder = getFolderDao().loadFolder(folderId, RequestContextHolder.getRequestContext().getZoneId());
		if (folder.isDeleted()) throw new NoBinderByTheIdException(folderId);
		return folder;

	}
	protected FolderEntry loadEntry(Long folderId, Long entryId) {
        Folder folder = loadFolder(folderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);		
		if (entry.isDeleted()) throw new NoFolderEntryByTheIdException(entryId);
		return entry;
	}
	protected FolderCoreProcessor loadProcessor(Folder folder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.team.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.

		return (FolderCoreProcessor)getProcessorManager().getProcessor(folder, folder.getProcessorKey(FolderCoreProcessor.PROCESSOR_KEY));	
	}

	public Folder getFolder(Long folderId)
		throws NoFolderByTheIdException, AccessControlException {
		Folder folder = loadFolder(folderId);
	
		// Check if the user has "read" access to the folder.
		getAccessControlManager().checkOperation(folder, WorkAreaOperation.READ_ENTRIES);
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
    public Long addEntry(Long folderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems, Map options) throws AccessControlException, WriteFilesException {
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
        
        Entry entry = processor.addEntry(folder, def, FolderEntry.class, inputData, fileItems, options);
        return entry.getId();
    }
    //no transaction    
	public Long addReply(Long folderId, Long parentId, String definitionId, 
    		InputDataAccessor inputData, Map fileItems, Map options) throws AccessControlException, WriteFilesException {
    	arCount.incrementAndGet();
    	
        Folder folder = loadFolder(folderId);
		if (options != null && (options.containsKey(ObjectKeys.INPUT_OPTION_CREATION_DATE) || 
				options.containsKey(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE)))
			checkAccess(folder, FolderOperation.changeEntryTimestamps);
        FolderCoreProcessor processor = loadProcessor(folder);
        //load parent entry
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, parentId);

        if(Validator.isNull(definitionId)) {
			Definition parentDef = entry.getEntryDef();
			Document defDoc = parentDef.getDefinition();
			List replyStyles = DefinitionUtils.getPropertyValueList(defDoc.getRootElement(), "replyStyle");
			if (!replyStyles.isEmpty()) {
				definitionId = (String)replyStyles.get(0);
			}
        }

        Definition def = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
        checkAccess(entry, FolderOperation.addReply);
        FolderEntry reply = processor.addReply(entry, def, inputData, fileItems, options);
        return reply.getId();
    }
    //no transaction    
	public void addVote(Long folderId, Long entryId, InputDataAccessor inputData, Map options) throws AccessControlException {
	   	meCount.incrementAndGet();
   	
        Folder folder = loadFolder(folderId);
		if (options != null && (options.containsKey(ObjectKeys.INPUT_OPTION_CREATION_DATE) || 
				options.containsKey(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE)))
			checkAccess(folder, FolderOperation.changeEntryTimestamps);
        FolderCoreProcessor processor = loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        checkAccess(entry, FolderOperation.addReply);
        User user = RequestContextHolder.getRequestContext().getUser();
        HistoryStamp reservation = entry.getReservation();
        if(reservation != null && !reservation.getPrincipal().equals(user))
        	throw new ReservedByAnotherUserException(entry);
 
 		try {
			processor.modifyEntry(folder, entry, inputData, null, null, null, options);
    	} catch (WriteFilesException ex) {
    	    //should never happen   
    	}
	}
 

    //no transaction    
    public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData, 
    		Map fileItems, Collection<String> deleteAttachments, Map<FileAttachment,String> fileRenamesTo, Map options) 
    throws AccessControlException, WriteFilesException, ReservedByAnotherUserException {
        
    	meCount.incrementAndGet();

        Folder folder = loadFolder(folderId);
		if (options != null && (options.containsKey(ObjectKeys.INPUT_OPTION_CREATION_DATE) || 
				options.containsKey(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE)))
			checkAccess(folder, FolderOperation.changeEntryTimestamps);
		FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        checkAccess(entry, FolderOperation.modifyEntry);
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
         
    }    
    
    //no transaction
    public void modifyEntry(Long folderId, Long entryId, String fileDataItemName, String fileName, InputStream content)
	throws AccessControlException, WriteFilesException, ReservedByAnotherUserException {
    	MultipartFile mf = new SimpleMultipartFile(fileName, content);
    	Map<String, MultipartFile> fileItems = new HashMap<String, MultipartFile>();
    	if(fileDataItemName == null)
    		fileDataItemName = ObjectKeys.FILES_FROM_APPLET_FOR_BINDER + "1";
    	fileItems.put(fileDataItemName, mf);
    	modifyEntry(folderId, entryId, new EmptyInputData(), fileItems, null, null, null);
    }


    public Map getEntries(Long folderId, Map searchOptions) {
        Folder folder = loadFolder(folderId);
        //search query does access checks
        return loadProcessor(folder).getBinderEntries(folder, entryTypes, searchOptions);
    }
    
    
    public Map getFullEntries(Long folderId, Map searchOptions) {
    	//search query does access checks
        Map result =  getEntries(folderId, searchOptions);
        //now load the full database object
        List childEntries = (List)result.get(ObjectKeys.SEARCH_ENTRIES);
        ArrayList ids = new ArrayList();
        for (int i=0; i<childEntries.size();) {
        	Map searchEntry = (Map)childEntries.get(i);
        	String docId = (String)searchEntry.get(EntityIndexUtils.DOCID_FIELD);
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
        	String docId = (String)searchEntry.get(EntityIndexUtils.DOCID_FIELD);
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
					String folderIdString = hits.doc(i).getField(EntityIndexUtils.BINDER_ID_FIELD).stringValue();
					String entryIdString = hits.doc(i).getField(EntityIndexUtils.DOCID_FIELD).stringValue();
					Long entryId = null;
					if (entryIdString != null && !entryIdString.equals("")) {
						entryId = new Long(entryIdString);
					}
					try {
						modifyDate = DateTools.stringToDate(hits.doc(i).getField(IndexUtils.LASTACTIVITY_FIELD).stringValue());
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
    	Hits results = null;
       	// Build the query
    	org.dom4j.Document qTree = DocumentHelper.createDocument();
    	Element rootElement = qTree.addElement(Constants.QUERY_ELEMENT);
    	Element andElement = rootElement.addElement(Constants.AND_ELEMENT);
    	Element field,child;
    	//choose 1 of the folders
    	Element orElement = andElement.addElement(Constants.OR_ELEMENT);
    	Iterator itFolders = folders.iterator();
    	while (itFolders.hasNext()) {
    		Folder folder = (Folder) itFolders.next();
        	field = orElement.addElement(Constants.FIELD_ELEMENT);
        	field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.BINDER_ID_FIELD);
        	child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
    		child.setText(folder.getId().toString());
    	}
    	//choose only entries/ not replies
    	field = andElement.addElement(Constants.FIELD_ELEMENT);
    	field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.ENTRY_TYPE_FIELD);
    	child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
		child.setText(EntityIndexUtils.ENTRY_TYPE_ENTRY);

		//choose a range of dates
    	Element rangeElement = andElement.addElement(Constants.RANGE_ELEMENT);
    	rangeElement.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, IndexUtils.LASTACTIVITY_DAY_FIELD);
    	rangeElement.addAttribute(Constants.INCLUSIVE_ATTRIBUTE, Constants.INCLUSIVE_TRUE);
    	Element startRange = rangeElement.addElement(Constants.RANGE_START);
    	Date now = new Date();
    	Date startDate = new Date(now.getTime() - ObjectKeys.SEEN_MAP_TIMEOUT);
    	startRange.addText(EntityIndexUtils.formatDayString(startDate));
    	Element finishRange = rangeElement.addElement(Constants.RANGE_FINISH);
    	finishRange.addText(EntityIndexUtils.formatDayString(now));

     	
    	//Create the Lucene query
    	QueryBuilder qb = new QueryBuilder(true);
    	SearchObject so = qb.buildQuery(qTree);
    	
    	if(logger.isDebugEnabled())
    		logger.debug("Query is: " + so.getQueryString());
    	
    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
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
        Folder parent = entry.getParentFolder();
        try {
        	AccessUtils.readCheck(entry);
        } catch (AccessControlException ac) {
        	return null;
        }
        return parent;
    }
    public FolderEntry getEntry(Long parentFolderId, Long entryId) {
        FolderEntry entry = loadEntry(parentFolderId, entryId);
        AccessUtils.readCheck(entry);
        return entry;
    }
    public Map getEntryTree(Long parentFolderId, Long entryId) {
        Folder folder = loadFolder(parentFolderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        AccessUtils.readCheck(entry);
       return processor.getEntryTree(folder, entry);   	
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
    //no transaction        
    public void deleteEntry(Long parentFolderId, Long entryId) {
    	deleteEntry(parentFolderId, entryId, true, null);
    }
    //no transaction    
    public void deleteEntry(Long parentFolderId, Long entryId, boolean deleteMirroredSource, Map options) {
    	deCount.incrementAndGet();

        Folder folder = loadFolder(parentFolderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        checkAccess(entry, FolderOperation.deleteEntry);
        processor.deleteEntry(folder, entry, deleteMirroredSource, options);
    }
    //inside write transaction    
    public void moveEntry(Long folderId, Long entryId, Long destinationId, Map options) {
        Folder folder = loadFolder(folderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        checkAccess(entry, FolderOperation.moveEntry);
        
        
        Folder destination =  loadFolder(destinationId);
        checkAccess(destination, FolderOperation.addEntry);
        processor.moveEntry(folder, entry, destination, options);
    }
    //inside write transaction    
    public void copyEntry(Long folderId, Long entryId, Long destinationId, Map options) {
        Folder folder = loadFolder(folderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        checkAccess(entry, FolderOperation.copyEntry);
              
        Folder destination =  loadFolder(destinationId);
        checkAccess(destination, FolderOperation.addEntry);
        processor.copyEntry(folder, entry, destination, options);
    }
    //inside write transaction    
    public void addSubscription(Long folderId, Long entryId, Map<Integer,String[]> styles) {
    	//getEntry check read access
		FolderEntry entry = getEntry(folderId, entryId);
		//only subscribe at top level
		if (!entry.isTop()) entry = entry.getTopEntry();
		User user = RequestContextHolder.getRequestContext().getUser();
		Subscription s = getProfileDao().loadSubscription(user.getId(), entry.getEntityIdentifier());
		//digest doesn't make sense here - only individual messages are sent 
		if (s == null) {
			s = new Subscription(user.getId(), entry.getEntityIdentifier());
			s.setStyles(styles);
			getCoreDao().save(s);
		} else 	s.setStyles(styles);
		entry.setSubscribed(true);
  	
    }
    public Subscription getSubscription(FolderEntry entry) {
    	//have entry so assume read access
		User user = RequestContextHolder.getRequestContext().getUser();
		if (!entry.isTop()) entry = entry.getTopEntry();
		return getProfileDao().loadSubscription(user.getId(), entry.getEntityIdentifier());
    }
    //inside write transaction    
    public void deleteSubscription(Long folderId, Long entryId) {
    	//should be able to delete you own
		FolderEntry entry = loadEntry(folderId, entryId);
		if (!entry.isTop()) entry = entry.getTopEntry();
		User user = RequestContextHolder.getRequestContext().getUser();
		List subs = getCoreDao().loadSubscriptionByEntity(entry.getEntityIdentifier());
		Subscription s = getProfileDao().loadSubscription(user.getId(), entry.getEntityIdentifier());
		if (s != null) {
			getCoreDao().delete(s);
			//if this is the last subscription, let entry know
			if (subs.size() == 1) entry.setSubscribed(false);
		}
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
				getCoreDao().saveNewSession(visit);
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
    	
        Folder folder = loadFolder(folderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);

        // For now, check against the same access right needed for modifying
        // entry. We might want to have a separate right for reserving entry...
    	checkAccess(entry, FolderOperation.reserveEntry);

        User user = RequestContextHolder.getRequestContext().getUser();
    	
    	HistoryStamp reservation = entry.getReservation();
    	if(reservation == null) { // The entry is not currently reserved. 
    		// We must check if any of the files in the entry is locked
    		// by another user. 
    		
    		// Make sure that the file lock states are current before examining them.
    		getFileModule().RefreshLocks(folder, entry);
    		
    		// Now that lock states are up-to-date, we can examine them.
    		
    		boolean atLeastOneFileLockedByAnotherUser = false;
    		Collection<FileAttachment> fAtts = entry.getFileAttachments();
    		for(FileAttachment fa :fAtts) {
    			if(fa.getFileLock() != null && !fa.getFileLock().getOwner().equals(user)) {
    				atLeastOneFileLockedByAnotherUser = true;
    				break;
    			}
    		}	
    		
    		if(!atLeastOneFileLockedByAnotherUser) {
    			// All remaining effective locks are owned by the same user
    			// or there are no effective locks at all.
    			// Proceed and reserve the entry.
    			entry.setReservation(user);
    		}
    		else { // One or more lock is held by someone else.
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
    	}
    	else {	
    		// The entry is currently reserved. 
    		if(reservation.getPrincipal().equals(user)) {
    			// The entry is reserved by the same user. Noop.
    		}
    		else {
    			// The entry is reserved by another user.
    			throw new ReservedByAnotherUserException(entry);
    		}
    	}
    }
    
    //inside write transaction    
   public void unreserveEntry(Long folderId, Long entryId)
	throws AccessControlException, ReservedByAnotherUserException {
        Folder folder = loadFolder(folderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);

        // I will skip checking the user's access right for this operation.
        // If the user previously reserved the entry successfully, it is
        // inconceivable that the user no longer has the right to unreserve
        // the entry (although it is possible in theory...). If the user
        // hasn't been able to reserve it previously, unreserve won' work
        // anyway. So either way, we can skip the access checking. 
    	//checkModifyEntryAllowed(entry);

        User user = RequestContextHolder.getRequestContext().getUser();
    	
    	HistoryStamp reservation = entry.getReservation();
    	if(reservation == null) { 
    		// The entry is not currently reserved by anyone. 
    		// Nothing to do. 
    	}
    	else {
    		boolean isUserBinderAdministrator = false;
    		try {
    			checkAccess(entry, FolderOperation.overrideReserveEntry);
    			isUserBinderAdministrator = true;
    		}
    		catch (AccessControlException ac) {};    		
    		
    		if(reservation.getPrincipal().equals(user) || isUserBinderAdministrator) {
    			// The entry is currently reserved by the same user or if the user happens to be a binder administrator 
    			// Cancel the reservation.
    			entry.clearReservation();
    		}
    		else {
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
    		return getEntry(fileFolder.getId(), id);
    	} catch (NoObjectByTheIdException no) {
    		return null;
    	}
    }
    //this is for wiki links where normalize title is used
    public Set<FolderEntry> getFolderEntryByNormalizedTitle(Long folderId, String title)
	throws AccessControlException {
    	Folder folder = getFolder(folderId);
    	List<FolderEntry> results = getFolderDao().loadEntries(folder, new FilterControls(ObjectKeys.FIELD_ENTITY_NORMALIZED_TITLE, title));
   		Set views = new HashSet();
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
    		if (f.isDeleted()) continue;
    		if(getAccessControlManager().testOperation(f, WorkAreaOperation.READ_ENTRIES))
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
    		if (f.isDeleted()) continue;
    		if(getAccessControlManager().testOperation(f, WorkAreaOperation.READ_ENTRIES))
    			subFolders.add(f);
    	}
    	
    	return subFolders;    	
    }
    
    
    //called by scheduler to complete folder deletions
    //no transaction
    public void cleanupFolders() {
   	    List<Folder> folders = getCoreDao().loadObjects(Folder.class, new FilterControls("deleted", Boolean.TRUE), RequestContextHolder.getRequestContext().getZoneId());
   		logger.debug("checking for deleted folders");
   		for (Folder f: folders) {
   			FolderCoreProcessor processor = loadProcessor(f);
   			try {
  				processor.deleteBinder(f, true, null);
   			} catch (Exception ex) {
   				logger.error(ex);
   			}
   		}
    }

    public void indexEntry(FolderEntry entry, boolean includeReplies) {
		FolderCoreProcessor processor = loadProcessor(entry.getParentFolder());
		processor.indexEntry(entry);
		if (includeReplies) {
			List<FolderEntry> replies = new ArrayList();
			replies.addAll(entry.getReplies());
			while (!replies.isEmpty()) {
				FolderEntry reply = replies.get(0);
				replies.remove(0);
				replies.addAll(reply.getReplies());
				processor.indexEntry(reply);
			}
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
}