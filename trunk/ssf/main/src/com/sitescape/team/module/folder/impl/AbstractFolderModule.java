/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.folder.impl;

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
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.document.DateTools;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.comparator.BinderComparator;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.Attachment;
import com.sitescape.team.domain.AuditTrail;
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
import com.sitescape.team.jobs.FillEmailSubscription;
import com.sitescape.team.jobs.FolderDelete;
import com.sitescape.team.lucene.Hits;
import com.sitescape.team.module.binder.AccessUtils;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.file.FileModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.folder.FileLockInfo;
import com.sitescape.team.module.folder.FilesLockedByOtherUsersException;
import com.sitescape.team.module.folder.FolderCoreProcessor;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.folder.index.IndexUtils;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.search.LuceneSession;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.search.SearchObject;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.util.Validator;
/**
 *
 * @author Jong Kim
 */
public abstract class AbstractFolderModule extends CommonDependencyInjection 
implements FolderModule, AbstractFolderModuleMBean, InitializingBean {
	protected String[] ratingAttrs = new String[]{"id.entityId", "id.entityType"};
	protected String[] entryTypes = {EntityIndexUtils.ENTRY_TYPE_ENTRY};
    protected DefinitionModule definitionModule;
    protected FileModule fileModule;
    protected ReportModule reportModule;
    
    AtomicInteger aeCount = new AtomicInteger();
    AtomicInteger meCount = new AtomicInteger();
    AtomicInteger deCount = new AtomicInteger();
    AtomicInteger arCount = new AtomicInteger();
    AtomicInteger afCount = new AtomicInteger();


    protected ReportModule getReportModule() {
		return reportModule;
	}
	public void setReportModule(ReportModule reportModule) {
		this.reportModule = reportModule;
	}
	
	/**
     * Called after bean is initialized.  
     */
 	public void afterPropertiesSet() {
 		//make sure job to delete and log folders is running
 		List companies = getCoreDao().findCompanies();
 		for (int i=0; i<companies.size(); ++i) {
 			Workspace zone = (Workspace)companies.get(i);
 			startScheduledJobs(zone);
	   }
 	}
    public void startScheduledJobs(Workspace zone) {
	   String jobClass = SZoneConfig.getString(zone.getName(), "folderConfiguration/property[@name='" + FolderDelete.DELETE_JOB + "']");
 	   if (Validator.isNull(jobClass)) jobClass = "com.sitescape.team.jobs.DefaultFolderDelete";
 	   try {
 		   Class processorClass = ReflectHelper.classForName(jobClass);
 		  FolderDelete job = (FolderDelete)processorClass.newInstance();
 		   //make sure a delete job is scheduled for the zone
 		   String hrsString = (String)SZoneConfig.getString(zone.getName(), "folderConfiguration/property[@name='" + FolderDelete.DELETE_HOURS + "']");
 		   int hours = 24;
 		   try {
 			  hours = Integer.parseInt(hrsString);
 		   } catch (Exception ex) {};
 		   	job.schedule(zone.getId(), hours);
 	
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
			case addFolder:
				getAccessControlManager().checkOperation(folder, WorkAreaOperation.CREATE_FOLDERS);
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
			case setWorkflowResponse:
			case deleteEntryWorkflow:
			case reserveEntry:
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
			case modifyWorkflowState:
				AccessUtils.modifyCheck(entry);   
	    		break;				
			default:
				throw new NotSupportedException(operation.toString(), "checkAccess");
					
		}

	}
	
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
	
	public Set<Folder> getFolders(Collection<Long> folderIds) {
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
   public Long addFolder(Long parentFolderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems) throws AccessControlException, WriteFilesException {
    	afCount.incrementAndGet();
		Folder parentFolder = loadFolder(parentFolderId);
		checkAccess(parentFolder, FolderOperation.addFolder);
		Definition def = null;
		if (Validator.isNotNull(definitionId)) { 
			def = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
		} else {
			def = parentFolder.getEntryDef();
		}
    			
		Binder binder = loadProcessor(parentFolder).addBinder(parentFolder, def, Folder.class, inputData, fileItems);
		return binder.getId();
    }
    //no transaction by default
    public Long addEntry(Long folderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems) throws AccessControlException, WriteFilesException {
    	aeCount.incrementAndGet();

        Folder folder = loadFolder(folderId);
        checkAccess(folder, FolderOperation.addEntry);
        
        FolderCoreProcessor processor = loadProcessor(folder);
       
        
        Definition def = null;
        if (!Validator.isNull(definitionId)) { 
        	def = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
        } else {
        	def = folder.getDefaultEntryDef();
        }
        
        Entry entry = processor.addEntry(folder, def, FolderEntry.class, inputData, fileItems);
        return entry.getId();
    }
    //no transaction    
	public Long addReply(Long folderId, Long parentId, String definitionId, 
    		InputDataAccessor inputData, Map fileItems) throws AccessControlException, WriteFilesException {
    	arCount.incrementAndGet();
    	
        Folder folder = loadFolder(folderId);
        Definition def = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
        FolderCoreProcessor processor = loadProcessor(folder);
        //load parent entry
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, parentId);
        checkAccess(entry, FolderOperation.addReply);
        FolderEntry reply = processor.addReply(entry, def, inputData, fileItems);
        Date stamp = reply.getCreation().getDate();
        scheduleSubscription(folder, reply, new Date(stamp.getTime()-1));
        
        return reply.getId();
    }
    //no transaction    
	public void addVote(Long folderId, Long entryId, InputDataAccessor inputData) throws AccessControlException {
	   	meCount.incrementAndGet();
   	
        Folder folder = loadFolder(folderId);
        FolderCoreProcessor processor = loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        checkAccess(entry, FolderOperation.addReply);
        User user = RequestContextHolder.getRequestContext().getUser();
        HistoryStamp reservation = entry.getReservation();
        if(reservation != null && !reservation.getPrincipal().equals(user))
        	throw new ReservedByAnotherUserException(entry);
 
    	Date stamp = entry.getModification().getDate();
		try {
			processor.modifyEntry(folder, entry, inputData, null, null, null);
      		if (!stamp.equals(entry.getModification().getDate())) scheduleSubscription(folder, entry, stamp);    		
    	} catch (WriteFilesException ex) {
    	    //should never happen   
    	}
	}
    //no transaction    
    public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData, 
    		Map fileItems, Collection<String> deleteAttachments, Map<FileAttachment,String> fileRenamesTo) 
    throws AccessControlException, WriteFilesException, ReservedByAnotherUserException {
        
    	meCount.incrementAndGet();

        Folder folder = loadFolder(folderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        checkAccess(entry, FolderOperation.modifyEntry);
        User user = RequestContextHolder.getRequestContext().getUser();
        HistoryStamp reservation = entry.getReservation();
        if(reservation != null && !reservation.getPrincipal().equals(user))
        	throw new ReservedByAnotherUserException(entry);
        
    	List<Attachment> delAtts = new ArrayList<Attachment>();
    	if (deleteAttachments != null) {
    		for (String id: deleteAttachments) {
   				Attachment a = entry.getAttachment(id);
   				if (a != null) delAtts.add(a);
    		}
    	}
    	Date stamp = entry.getModification().getDate();
    	
    	try {
    		processor.modifyEntry(folder, entry, inputData, fileItems, delAtts, fileRenamesTo);
       		if (!stamp.equals(entry.getModification().getDate())) scheduleSubscription(folder, entry, stamp);
    		
    	} catch (WriteFilesException ex) {
    		if (!stamp.equals(entry.getModification().getDate())) scheduleSubscription(folder, entry, stamp);
    	    throw ex;   
    	}
        
    }    
    

    public Document getDomFolderTree(Long folderId, DomTreeBuilder domTreeHelper) {
    	return getDomFolderTree(folderId, domTreeHelper, -1);
    }
    public Document getDomFolderTree(Long folderId, DomTreeBuilder domTreeHelper, int levels) {
    	//access check in get Folder
    	Folder top = getFolder(folderId);
        
        User user = RequestContextHolder.getRequestContext().getUser();
    	Comparator c = new BinderComparator(user.getLocale(), BinderComparator.SortByField.title);
    	    	
    	org.dom4j.Document wsTree = DocumentHelper.createDocument();
    	Element rootElement = wsTree.addElement(DomTreeBuilder.NODE_ROOT);
    	      	
  	    buildFolderDomTree(rootElement, top, c, domTreeHelper, levels);
  	    return wsTree;
  	}
    
    protected void buildFolderDomTree(Element current, Folder top, Comparator c, DomTreeBuilder domTreeHelper, int levels) {
       	Element next; 
       	Folder f;
    	   	
       	//callback to setup tree
    	domTreeHelper.setupDomElement(DomTreeBuilder.TYPE_FOLDER, top, current);
 		if (levels == 0) return;
    	--levels;
    	
     	TreeSet folders = new TreeSet(c);
    	folders.addAll(top.getFolders());
       	for (Iterator iter=folders.iterator(); iter.hasNext();) {
       		f = (Folder)iter.next();
    		if (f.isDeleted()) continue;
      	    // Check if the user has the privilege to view the folder 
       		if(!getAccessControlManager().testOperation(f, WorkAreaOperation.READ_ENTRIES))
       			continue;
       		next = current.addElement(DomTreeBuilder.NODE_CHILD);
       		buildFolderDomTree(next, f, c, domTreeHelper, levels);
       	}
    }
   
    public Map getEntries(Long folderId) {
        return getEntries(folderId, new HashMap());
    }

    public Map getEntries(Long folderId, Map options) {
        Folder folder = loadFolder(folderId);
        //search query does access checks
        return loadProcessor(folder).getBinderEntries(folder, entryTypes, options);
    }
    
    public Map getFullEntries(Long folderId) {
    	return getFullEntries(folderId, new HashMap());
    }
    
    public Map getFullEntries(Long folderId, Map options) {
        //search query does access checks
        Map result =  getEntries(folderId, options);
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

    public Map getUnseenCounts(Collection<Long> folderIds) {
    	//search engine will do acl checks
        User user = RequestContextHolder.getRequestContext().getUser();
        SeenMap seenMap = getProfileDao().loadSeenMap(user.getId());
        Map results = new HashMap();
        Set<Folder> folders = new HashSet();
        for (Long id:folderIds) {
        	try {
        		folders.add(loadFolder(id));
        	} catch (NoFolderByTheIdException nf) {} 
        }
        if (folders.size() > 0) {
	        Hits hits = getRecentEntries(folders);
	        if (hits != null) {
	        	Map unseenCounts = new HashMap();
		        Date modifyDate = new Date();
		        for (int i = 0; i < hits.length(); i++) {
					String folderIdString = hits.doc(i).getField(IndexUtils.TOP_FOLDERID_FIELD).stringValue();
					String entryIdString = hits.doc(i).getField(EntityIndexUtils.DOCID_FIELD).stringValue();
					Long entryId = null;
					if (entryIdString != null && !entryIdString.equals("")) {
						entryId = new Long(entryIdString);
					}
					try {
						modifyDate = DateTools.stringToDate(hits.doc(i).getField(IndexUtils.LASTACTIVITY_FIELD).stringValue());
					} catch (ParseException pe) {} // no need to do anything
					Counter cnt = (Counter)unseenCounts.get(folderIdString);
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
		        	if (cnt == null) cnt = new Counter();
		        	results.put(f, cnt);
		        }
	        }
        }
        return results;
    }
 
    protected Hits getRecentEntries(Collection<Folder> folders) {
    	Hits results = null;
       	// Build the query
    	org.dom4j.Document qTree = DocumentHelper.createDocument();
    	Element rootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
    	Element andElement = rootElement.addElement(QueryBuilder.AND_ELEMENT);
    	Element field,child;
    	//choose 1 of the folders
    	Element orElement = andElement.addElement(QueryBuilder.OR_ELEMENT);
    	Iterator itFolders = folders.iterator();
    	while (itFolders.hasNext()) {
    		Folder folder = (Folder) itFolders.next();
        	field = orElement.addElement(QueryBuilder.FIELD_ELEMENT);
        	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,IndexUtils.TOP_FOLDERID_FIELD);
        	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
        	Folder topFolder = folder.getTopFolder();
        	if (topFolder == null) topFolder = folder;
    		child.setText(topFolder.getId().toString());
    	}
    	//choose only entries/ not replies
    	field = andElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.ENTRY_TYPE_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		child.setText(EntityIndexUtils.ENTRY_TYPE_ENTRY);

		//choose a range of dates
    	Element rangeElement = andElement.addElement(QueryBuilder.RANGE_ELEMENT);
    	rangeElement.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, IndexUtils.LASTACTIVITY_DAY_FIELD);
    	rangeElement.addAttribute(QueryBuilder.INCLUSIVE_ATTRIBUTE, QueryBuilder.INCLUSIVE_TRUE);
    	Element startRange = rangeElement.addElement(QueryBuilder.RANGE_START);
    	Date now = new Date();
    	Date startDate = new Date(now.getTime() - ObjectKeys.SEEN_MAP_TIMEOUT);
    	startRange.addText(EntityIndexUtils.formatDayString(startDate));
    	Element finishRange = rangeElement.addElement(QueryBuilder.RANGE_FINISH);
    	finishRange.addText(EntityIndexUtils.formatDayString(now));

     	
    	//Create the Lucene query
    	QueryBuilder qb = new QueryBuilder(getProfileDao().getPrincipalIds(RequestContextHolder.getRequestContext().getUser()));
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
    //inside write transaction        
    public void deleteEntry(Long parentFolderId, Long entryId) {
    	deleteEntry(parentFolderId, entryId, true);
    }
    //inside write transaction    
    public void deleteEntry(Long parentFolderId, Long entryId, boolean deleteMirroredSource) {
    	deCount.incrementAndGet();

        Folder folder = loadFolder(parentFolderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        checkAccess(entry, FolderOperation.deleteEntry);
        processor.deleteEntry(folder, entry, deleteMirroredSource);
    }
    //inside write transaction    
    public void moveEntry(Long folderId, Long entryId, Long destinationId) {
        Folder folder = loadFolder(folderId);
        FolderCoreProcessor processor=loadProcessor(folder);
        FolderEntry entry = (FolderEntry)processor.getEntry(folder, entryId);
        checkAccess(entry, FolderOperation.moveEntry);
        
        
        Folder destination =  loadFolder(destinationId);
        checkAccess(destination, FolderOperation.addEntry);
        processor.moveEntry(folder, entry, destination);
    }
    //inside write transaction    
    public void addSubscription(Long folderId, Long entryId, int style) {
    	//getEntry check read access
		FolderEntry entry = getEntry(folderId, entryId);
		User user = RequestContextHolder.getRequestContext().getUser();
		Subscription s = getProfileDao().loadSubscription(user.getId(), entry.getEntityIdentifier());
		//digest doesn't make sense here - only individual messages are sent 
		if (s == null) {
			s = new Subscription(user.getId(), entry.getEntityIdentifier());
			s.setStyle(style);
			getCoreDao().save(s);
		} else 	s.setStyle(style);
  	
    }
    public Subscription getSubscription(FolderEntry entry) {
    	//have entry so assume read access
		User user = RequestContextHolder.getRequestContext().getUser();
		return getProfileDao().loadSubscription(user.getId(), entry.getEntityIdentifier());
    }
    //inside write transaction    
    public void deleteSubscription(Long folderId, Long entryId) {
    	//should be able to delete you own
		FolderEntry entry = loadEntry(folderId, entryId);
		User user = RequestContextHolder.getRequestContext().getUser();
		Subscription s = getProfileDao().loadSubscription(user.getId(), entry.getEntityIdentifier());
		if (s != null) getCoreDao().delete(s);
    }

	public List<Tag> getTags(FolderEntry entry) {
		//have Entry - so assume read access
		//bulk load tags
        return getCoreDao().loadEntityTags(entry.getEntityIdentifier(), RequestContextHolder.getRequestContext().getUser().getEntityIdentifier());
	}
	
    //inside write transaction    
	public void setTag(Long binderId, Long entryId, String newtag, boolean community) {
		if (Validator.isNull(newtag)) return;
		newtag = newtag.replaceAll("[\\p{Punct}]", " ").trim().replaceAll("\\s+"," ");
		String[] newTags = newtag.split(" ");
		if (newTags.length == 0) return;
		List tags = new ArrayList();
		//read access checked by getEntry
		FolderEntry entry = getEntry(binderId, entryId);
		if (community) checkAccess(entry, FolderOperation.manageTag);
		User user = RequestContextHolder.getRequestContext().getUser();
		EntityIdentifier uei = user.getEntityIdentifier();
		EntityIdentifier eei = entry.getEntityIdentifier();
		for (int i = 0; i < newTags.length; i++) {
			Tag tag = new Tag();
			//community tags belong to the binder - don't care who created it
		   	if (!community) tag.setOwnerIdentifier(uei);
		   	tag.setEntityIdentifier(eei);
		    tag.setPublic(community);
		   	tag.setName(newTags[i]);
		   	tags.add(tag);
	   	}
		coreDao.save(tags);
 	    loadProcessor(entry.getParentFolder()).indexEntry(entry);
	}
	
    //inside write transaction    
	public void deleteTag(Long binderId, Long entryId, String tagId) {
	   	FolderEntry entry = loadEntry(binderId, entryId);
  		Tag tag = null;
   		try {
	   		tag = coreDao.loadTag(tagId);
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
     	Object[] cfValues = new Object[]{id.getEntityId(), id.getEntityType().getValue()};
	    User user = RequestContextHolder.getRequestContext().getUser();
       	Rating rating = getProfileDao().loadRating(user.getId(), id);
		if (rating == null) {
      		rating = new Rating(user.getId(), id);
			getCoreDao().save(rating);
		} 
		//set user rating
		rating.setRating(value);
    	// see if title exists for this folder
		FilterControls filter = new FilterControls(ratingAttrs, cfValues);
     	double result = getCoreDao().averageColumn(Rating.class, "rating", filter);
     	AverageRating avg = entity.getAverageRating();
     	long count;
     	if (avg == null) {
     		avg = new AverageRating();
     		entity.setAverageRating(avg);
     		count = 1;
     	}
     	else
     		count = avg.getCount() + 1;
     	
     	avg.setAverage(result);
   		avg.setCount(count);
 			
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
       		getCoreDao().save(visit);
       	}
        visit.incrReadCount();   	
       	//update entry average
     	Object[] cfValues = new Object[]{id.getEntityId(), id.getEntityType().getValue()};
    	// see if title exists for this folder
     	long result = getCoreDao().sumColumn(Visits.class, "readCount", new FilterControls(ratingAttrs, cfValues));
     	entry.setPopularity(Long.valueOf(result));
     	getReportModule().addAuditTrail(AuditTrail.AuditType.view, entry);
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
    	FilterControls fc = new FilterControls();
    	fc.add(ObjectKeys.FIELD_ENTITY_PARENTBINDER, folder);
    	fc.add(ObjectKeys.FIELD_ENTITY_NORMALIZED_TITLE, title);
   		List<FolderEntry> results = getCoreDao().loadObjects(FolderEntry.class, fc);
   		Set views = new HashSet();
   		for (FolderEntry entry: results) {
   			try {
   				AccessUtils.readCheck(entry);
   				views.add(entry);
   			} catch (AccessControlException ac) {}
   		}
   		return views;
    }
    public Set<String> getSubfoldersTitles(Folder folder) {
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
    
    public Set<Folder> getSubfolders(Folder folder) {
    	//already have access to folder
    	Set<Folder> subFolders = new HashSet<Folder>();
   		
    	for(Object o : folder.getFolders()) {
    		Folder f = (Folder) o;
    		if (f.isDeleted()) continue;
    		if(getAccessControlManager().testOperation(f, WorkAreaOperation.READ_ENTRIES))
    			subFolders.add(f);
    	}
    	
    	return subFolders;    	
    }
    
    protected void scheduleSubscription(Folder folder, FolderEntry entry, Date when) {
  		FillEmailSubscription process = (FillEmailSubscription)processorManager.getProcessor(folder, FillEmailSubscription.PROCESSOR_KEY);
  		//if anyone subscribed to the topLevel entry, notify them of a change
  		FolderEntry parent = entry.getTopEntry();
  		if (parent == null) parent = entry;
  		if (!getCoreDao().loadSubscriptionByEntity(parent.getEntityIdentifier()).isEmpty())
  			process.schedule(folder.getId(), entry.getId(), when);
    	
    }
    
    //called by scheduler to complete folder deletions
    public void cleanupFolders() {
   		FilterControls fc = new FilterControls();
   		fc.add(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId());
   		fc.add("deleted", Boolean.TRUE);
   	    List<Folder> folders = getCoreDao().loadObjects(Folder.class, fc);
   		logger.debug("checking for deleted folders");
   		for (Folder f: folders) {
   			FolderCoreProcessor processor = loadProcessor(f);
   			try {
  				processor.deleteBinder(f, true);
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
    public class Counter {
    	long count=0;
    	protected Counter() {	
    	}
    	public void increment() {
    		++count;
    	}
    	public long getCount() {
    		return count;
    	}
    	public String toString() {
    		return String.valueOf(count);
    	}
    	
    }
    
	public void clearStatistics() {
		aeCount.set(0);
		meCount.set(0);
		deCount.set(0);
		arCount.set(0);
		afCount.set(0);
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
	public int getAddFolderCount() {
		return afCount.get();
	}
	public int getAddReplyCount() {
		return arCount.get();
	}
}