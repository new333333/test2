package com.sitescape.team.module.binder.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.lucene.document.Field;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.NonUniqueObjectException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.HibernateSystemException;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.ObjectControls;
import com.sitescape.team.dao.util.SFQuery;
import com.sitescape.team.domain.Attachment;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.LibraryEntry;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.domain.NotificationDef;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.Tag;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.exception.UncheckedCodedContainerException;
import com.sitescape.team.jobs.EmailNotification;
import com.sitescape.team.jobs.ScheduleInfo;
import com.sitescape.team.lucene.Hits;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.binder.BinderProcessor;
import com.sitescape.team.module.binder.EntryProcessor;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.shared.ObjectBuilder;
import com.sitescape.team.search.LuceneSession;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.search.SearchObject;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.TagUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.FilterHelper;
import com.sitescape.util.Validator;
/**
 * @author Janet McCann
 *
 */
public class BinderModuleImpl extends CommonDependencyInjection implements BinderModule, InitializingBean {
    protected DefinitionModule definitionModule;
    protected ProfileModule profileModule;
	protected Map operations = new HashMap();

   /**
    * Called after bean is initialized.  
    */
	public void afterPropertiesSet() {
		operations.put("indexBinder", new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("indexTree", new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("modifyBinder", new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("deleteBinder", new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("moveBinder", new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("setProperty", new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("setDefinitions", new WorkAreaOperation[]{WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS});
		operations.put("modifyTag", new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("deleteTag", new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("setNotificationConfig", new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("modifyNotification", new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("setLibrary", new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("getTeamMembers", new WorkAreaOperation[] {WorkAreaOperation.TEAM_MEMBER,WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("setPosting", new WorkAreaOperation[] {WorkAreaOperation.MANAGE_BINDER_INCOMING, WorkAreaOperation.SITE_ADMINISTRATION});

	}
 
   protected DefinitionModule getDefinitionModule() {
		return definitionModule;
   }
	/**
	 * Setup by spring
	 * @param definitionModule
	 */
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
	public ProfileModule getProfileModule() {
		return profileModule;
	}
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	/*
	 * Check access to binder.  If operation not listed, assume read_entries needed
	 * This should not be called inside a transaction because it results in a rollback.
	 * @see com.sitescape.team.module.binder.BinderModule#checkAccess(com.sitescape.team.domain.Binder, java.lang.String)
	 */
	public boolean testAccess(Binder binder, String operation)  {
		try {
			checkAccess(binder, operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}
	
	/*
	 * Check access to binder.  If operation not listed, assume read_entries needed
	 * This should not be called inside a transaction because it results in a rollback.
	 * @see com.sitescape.team.module.binder.BinderModule#checkAccess(com.sitescape.team.domain.Binder, java.lang.String)
	 */
	public boolean testAccess(Long binderId, String operation)  {
		return testAccess(loadBinder(binderId), operation);
	}
	
	public boolean testAccessGetTeamMembers(Binder binder)  {
		try {
			checkAccess(binder, "getTeamMembers");
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}

	public boolean testAccessGetTeamMembers(Long binderId)  {
		return testAccessGetTeamMembers(loadBinder(binderId));
	}
		
	protected void checkAccess(Binder binder, String operation) throws AccessControlException {
		if (binder instanceof TemplateBinder) {
  			//gues anyone can read a template
  			if ("getBinder".equals(operation)) return;
			getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getZone(), WorkAreaOperation.SITE_ADMINISTRATION);
  		} else {
  			WorkAreaOperation[] wfo = (WorkAreaOperation[])operations.get(operation);
  			if (wfo == null) getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);
  			else {
  				for (int i=0; i<wfo.length-1; ++i) {
				//	only need to have 1
  					if (getAccessControlManager().testOperation(binder, wfo[i])) return;
  				}
  				//	will throw exception on failure
  				getAccessControlManager().checkOperation(binder, wfo[wfo.length-1]);
  			}
		}
  		
// fall under read_entries: getBinder,getCommunityTags,getPersonalTags,getNotificationConfig
//addSubscription,modifySubscription,deleteSubscription (personal)
	}
	private Binder loadBinder(Long binderId) {
		return getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneId());
	}
	private EntryProcessor loadEntryProcessor(Binder binder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.team.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.

		return (EntryProcessor)getProcessorManager().getProcessor(binder, binder.getProcessorKey(EntryProcessor.PROCESSOR_KEY));			
	}
	private BinderProcessor loadBinderProcessor(Binder binder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.team.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.

		return (BinderProcessor)getProcessorManager().getProcessor(binder, binder.getProcessorKey(BinderProcessor.PROCESSOR_KEY));

	}

	public Binder getBinder(Long binderId)
			throws NoBinderByTheIdException, AccessControlException {
		Binder binder = loadBinder(binderId);
		// Check if the user has "read" access to the binder.
		checkAccess(binder, "getBinder"); 
        return binder;        
	}
    public boolean hasBinders(Binder binder) {
    	List binders = binder.getBinders();
    	for (int i=0; i<binders.size(); ++i) {
    		Binder b = (Binder)binders.get(i);
            if (getAccessControlManager().testOperation(b, WorkAreaOperation.READ_ENTRIES)) return true;    	       		
    	}
    	return false;
    }	
    public boolean hasBinders(Binder binder, EntityIdentifier.EntityType binderType) {
    	List binders = binder.getBinders();
    	for (int i=0; i<binders.size(); ++i) {
    		Binder b = (Binder)binders.get(i);
    		//only check for specific types
    		if (b.getEntityType().equals(binderType)) 
    			if (getAccessControlManager().testOperation(b, WorkAreaOperation.READ_ENTRIES)) return true;    	       		
    	}
    	return false;
    }	
    public Collection indexTree(Long binderId) {
    	return indexTree(binderId, null);
    }
    public Collection indexTree(Long binderId, Collection exclusions) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, "indexTree");
		return loadBinderProcessor(binder).indexTree(binder, exclusions);
	}    
    public void indexBinder(Long binderId) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, "indexBinder");
 	    loadBinderProcessor(binder).indexBinder(binder);
    }

    public void modifyBinder(Long binderId, final InputDataAccessor inputData) 
    	throws AccessControlException, WriteFilesException {
    	modifyBinder(binderId, inputData, new HashMap(),  null);
    }
    public void modifyBinder(Long binderId, InputDataAccessor inputData, 
    		Map fileItems, Collection deleteAttachments) throws AccessControlException, WriteFilesException {
    	Binder binder = loadBinder(binderId);
    	//save library flag
    	boolean library = binder.isLibrary();
		checkAccess(binder, "modifyBinder");
    	List atts = new ArrayList();
    	if (deleteAttachments != null) {
    		for (Iterator iter=deleteAttachments.iterator(); iter.hasNext();) {
    			String id = (String)iter.next();
    			Attachment a = binder.getAttachment(id);
    			if (a != null) atts.add(a);
    		}
    	}
    	loadBinderProcessor(binder).modifyBinder(binder, inputData, fileItems, atts);
    	//if not longer a library - clear names
    	if (!binder.isLibrary() && library) {
			//remove old reserved names
			getCoreDao().clearLibraryEntries(binder);
    	} else if (binder.isLibrary() && !library) {
    		// make it a library
			getCoreDao().clearLibraryEntries(binder);
			//add new ones
			//get all attachments in this binder
		   	FilterControls filter = new FilterControls(new String[]{"owner.owningBinderId", "type"},
		   			new Object[] {binder.getId(), "F"});
		   	ObjectControls objs = new ObjectControls(FileAttachment.class, new String[] {"fileItem.name", "owner.ownerId"});
        	SFQuery query = getCoreDao().queryObjects(objs, filter);
	        try {
	        	while (query.hasNext()) {
	        		Object [] result = (Object[])query.next();
	        		LibraryEntry le = new LibraryEntry(binder.getId(), (String)result[0]);
	        		le.setEntityId((Long)result[1]);
	        		getCoreDao().save(le);
	        	}
	        } catch (HibernateSystemException he) {
	        	if (he.contains(NonUniqueObjectException.class)) {
	        		throw new ConfigurationException(NLT.get("errorcode.cannot.make.library"));
	        	}
	        	
	        } finally {
	        	query.close();
	        }
   		
    	}
    }
 
    public void setProperty(Long binderId, String property, Object value) {
    	Binder binder = loadBinder(binderId);
		checkAccess(binder, "setProperty");
		binder.setProperty(property, value);	
   }    
    public void deleteBinder(Long binderId) {
    	Binder binder = loadBinder(binderId);
    	//if can delete this binder, can delete everything under it??
		checkAccess(binder, "deleteBinder");
		if (binder.isReserved()) throw new NotSupportedException(
				NLT.get("errorcode.notsupported.deleteBinder", new String[]{binder.getPathName()}));

   		List errors = deleteChildBinders(binder);
   		if (errors.isEmpty()) loadBinderProcessor(binder).deleteBinder(binder);
   		else {
   			UncheckedCodedContainerException ue = new UncheckedCodedContainerException("errorcode.delete.binder");
   			ue.addExceptions(errors);
   		}
     }
    protected List deleteChildBinders(Binder binder) {
    	//First process all child folders
    	List binders = new ArrayList(binder.getBinders());
    	List errors = new ArrayList();
    	for (int i=0; i<binders.size(); ++i) {
    		Binder b = (Binder)binders.get(i);
        	try {
        		checkAccess(b, "deleteBinder");
       			List e = deleteChildBinders(b);
       			if (e.isEmpty()) loadBinderProcessor(b).deleteBinder(b);
       			else errors.addAll(e);
        	} catch (Exception ex) {
        		errors.add(ex);
        	}
    		
    	}
    	return errors;
    }
     public void moveBinder(Long fromId, Long toId) {
       	Binder source = loadBinder(fromId);
		checkAccess(source, "moveBinder");
       	Binder destination = loadBinder(toId);
		getAccessControlManager().checkOperation(destination, WorkAreaOperation.CREATE_BINDERS); 
       	
     	loadBinderProcessor(source).moveBinder(source,destination);
           	
    }
	public Binder setDefinitions(Long binderId, boolean inheritFromParent) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, "setDefinitions"); 
		boolean oldInherit = binder.isDefinitionsInherited();
		if (inheritFromParent != oldInherit) {
			if (inheritFromParent) {
				//remove old mappings
				Map m = binder.getWorkflowAssociations();
				m.clear();
				binder.setWorkflowAssociations(m);
				List l = binder.getDefinitions();
				l.clear();
				binder.setDefinitions(l);
			} else {
				//copy parents definitions to this binder before changing setting
				binder.setWorkflowAssociations(binder.getWorkflowAssociations());
				binder.setDefinitions(binder.getDefinitions());
			}
			binder.setDefinitionsInherited(inheritFromParent);
		}
		return binder;
		
	}
    public Binder setDefinitions(Long binderId, List definitionIds, Map workflowAssociations) 
	throws AccessControlException {
		Binder binder = setDefinitions(binderId, definitionIds);
		checkAccess(binder, "setDefinitions"); 
		Map wf = new HashMap();
		Definition def;
		if (workflowAssociations != null) {
			for (Iterator iter=workflowAssociations.entrySet().iterator(); iter.hasNext();) {
				Map.Entry me = (Map.Entry)iter.next();
				//	TODO:	getAccessControlManager().checkAcl(def, AccessType.READ);
				try {
					def = getCoreDao().loadDefinition((String)me.getValue(), 
							RequestContextHolder.getRequestContext().getZoneId());
					wf.put(me.getKey(), def);
				} catch (NoDefinitionByTheIdException nd) {}
			}
		}
		binder.setWorkflowAssociations(wf);
		binder.setDefinitionsInherited(false);
		return binder;
	}
	public Binder setDefinitions(Long binderId, List definitionIds) throws AccessControlException {
		Binder binder = loadBinder(binderId);
		List definitions = new ArrayList(); 
		Definition def;
		checkAccess(binder, "setDefinitions"); 
		//	Build up new set - domain object will handle associations
		if (definitionIds != null) {
			for (int i=0; i<definitionIds.size(); ++i) {
				try {
					def = getCoreDao().loadDefinition((String)definitionIds.get(i), 
							RequestContextHolder.getRequestContext().getZoneId());
					//	TODO:	getAccessControlManager().checkAcl(def, AccessType.READ);
					definitions.add(def);
				} catch (NoDefinitionByTheIdException nd) {}
			}
		}
	
		binder.setDefinitions(definitions);
		binder.setDefinitionsInherited(false);
		
		return binder;
	}
	/**
	 * Get tags owned by this binder
	 */
	
	public List getCommunityTags(Long binderId) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, "getCommunityTags");
		List tags = new ArrayList<Tag>();
		tags = getCoreDao().loadCommunityTagsByEntity(binder.getEntityIdentifier());
		return TagUtil.uniqueTags(tags);		
	}
	
	public List getPersonalTags(Long binderId) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, "getPersonalTags");
		List tags = new ArrayList<Tag>();
		User user = RequestContextHolder.getRequestContext().getUser();
		tags = getCoreDao().loadPersonalEntityTags(binder.getEntityIdentifier(),user.getEntityIdentifier());
		return TagUtil.uniqueTags(tags);		
	}
	
	/**
	 * Modify tag owned by this binder
	 * @see com.sitescape.team.module.binder.BinderModule#modifyTag(java.lang.Long, java.lang.String, java.util.Map)
	 */
	public void modifyTag(Long binderId, String tagId, String newTag) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, "modifyTag"); 
	   	Tag tag = coreDao.loadTagById(tagId);
	   	tag.setName(newTag);
	   	reindex(binderId);
	}
	/**
	 * Add a new tag, owned by this binder
	 */
	public void setTag(Long binderId, String newTag, boolean community) {
		if ("".equals(newTag)) return;
		newTag = newTag.replaceAll("\\W", " ").trim().replaceAll("\\s+"," ");
		String[] newTags = newTag.split(" ");
		if (newTags.length == 0) return;
		List tags = new ArrayList();
		Binder binder = loadBinder(binderId);
		checkAccess(binder, "setTag"); 
		User user = RequestContextHolder.getRequestContext().getUser();
	   	EntityIdentifier uei = user.getEntityIdentifier();
	   	EntityIdentifier bei = binder.getEntityIdentifier();
	   	for (int i = 0; i < newTags.length; i++) {
			Tag tag = new Tag();
		   	tag.setOwnerIdentifier(uei);
		   	tag.setEntityIdentifier(bei);
		   	tag.setPublic(community);
	   		tag.setName(newTags[i]);
	   		tags.add(tag);
	   	}
	   	coreDao.save(tags);
	   	reindex(binderId);   	
	}
	
	/**
	 * Delete a tag owned by this binder
	 */
	public void deleteTag(Long binderId, String tagId) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, "deleteTag"); 
	   	Tag tag = coreDao.loadTagById(tagId);
	   	getCoreDao().delete(tag);
	   	reindex(binderId);
	}
	
	// this should just reindex the binder, and not the entries associated with it.
	public void reindex(Long binderId) {	
		Binder binder = loadBinder(binderId);
        BinderProcessor processor = loadBinderProcessor(binder);
        processor.indexBinder(binder);
	}

    public void addSubscription(Long binderId, int style) {
    	//getEntry check read access
		Binder binder = loadBinder(binderId);
		checkAccess(binder, "addSubscription"); 
		User user = RequestContextHolder.getRequestContext().getUser();
		Subscription s = getProfileDao().loadSubscription(user.getId(), binder.getEntityIdentifier());
		if (s == null) {
			s = new Subscription(user.getId(), binder.getEntityIdentifier());
			s.setStyle(style);
			getCoreDao().save(s);
		} else s.setStyle(style); 	
    }
    public Subscription getSubscription(Long binderId) {
    	//getEntry check read access
		Binder binder = loadBinder(binderId);
		checkAccess(binder, "getSubscription"); 
		User user = RequestContextHolder.getRequestContext().getUser();
		return getProfileDao().loadSubscription(user.getId(), binder.getEntityIdentifier());
    }
    public void deleteSubscription(Long binderId) {
    	//getEntry check read access
		Binder binder = loadBinder(binderId);
		checkAccess(binder, "deleteSubscription"); 
		User user = RequestContextHolder.getRequestContext().getUser();
		Subscription s = getProfileDao().loadSubscription(user.getId(), binder.getEntityIdentifier());
		if (s != null) getCoreDao().delete(s);
    }
	public Map executeSearchQuery(Document searchQuery) {
		Map options = new HashMap();
		return executeSearchQuery(searchQuery, options);
	}
	public Map executeSearchQuery(Document searchQuery, Map options) {
        List entries = new ArrayList();
        Hits hits = new Hits(0);
        
        if (searchQuery != null) {
        	Document qTree = FilterHelper.convertSearchFilterToSearchBoolean(searchQuery, options);
        	Element rootElement = qTree.getRootElement();
        	if (rootElement != null) {
	        	//Find the first "and" element and add to it
	        	Element boolElement = (Element) rootElement.selectSingleNode(QueryBuilder.AND_ELEMENT);
	        	if (boolElement == null) {
	        		//If there isn't one, then create one.
	        		boolElement = rootElement.addElement(QueryBuilder.AND_ELEMENT);
	        	}
	        	boolElement.addElement(QueryBuilder.USERACL_ELEMENT);

	        	//Create the Lucene query
		    	QueryBuilder qb = new QueryBuilder(getProfileDao().getPrincipalIds(RequestContextHolder.getRequestContext().getUser()));
		    	SearchObject so = qb.buildQuery(qTree);
		    	
		    	//Set the sort order
		    	SortField[] fields = BinderHelper.getBinderEntries_getSortFields(options); 
		    	so.setSortBy(fields);
		    	
		    	Query soQuery = so.getQuery();    //Get the query into a variable to avoid doing this very slow operation twice
		    	
		    	if(logger.isInfoEnabled()) {
		    		logger.info("Query is: " + searchQuery.asXML());
		    		logger.info("Query is: " + soQuery.toString());
		    	}
		    	
		    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
		    	int maxResults = 10;
		    	int offset = 0;
		    	if (options.containsKey(ObjectKeys.SEARCH_MAX_HITS)) 
		    		maxResults = (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS);
		    	if (options.containsKey(ObjectKeys.SEARCH_OFFSET)) 
		    		offset = (Integer) options.get(ObjectKeys.SEARCH_OFFSET);
		        try {
			        hits = luceneSession.search(soQuery,so.getSortBy(),offset,maxResults);
		        }
		        catch(Exception e) {}
		        finally {
		            luceneSession.close();
		        }
        	}
        }
		EntryProcessor processor = 
			(EntryProcessor) getProcessorManager().getProcessor("com.sitescape.team.domain.Folder", 
						EntryProcessor.PROCESSOR_KEY);
        entries = (List) processor.getBinderEntries_entriesArray(hits);
        Map retMap = new HashMap();
        retMap.put(WebKeys.FOLDER_ENTRIES,entries);
        retMap.put(WebKeys.ENTRY_SEARCH_COUNT, new Integer(hits.getTotalHits()));
        retMap.put(WebKeys.ENTRY_SEARCH_RECORDS_RETURNED, new Integer(hits.length()));
        
    	return retMap; 
	}	
	
	public Map executePeopleSearchQuery(Document searchQuery) {
		Binder binder = null;
		return executePeopleSearchQuery(binder, searchQuery);
	}
	public Map executePeopleSearchQuery(Binder binder, Document searchQuery) {
        List entries = new ArrayList();
        Hits hits = new Hits(0);
        
        if (searchQuery != null) {
        	Document qTree = FilterHelper.convertSearchFilterToPeopleSearchBoolean(searchQuery);
        	Element rootElement = qTree.getRootElement();
        	if (rootElement != null) {
	        	//Find the first "and" element and add to it
	        	Element boolElement = (Element) rootElement.selectSingleNode(QueryBuilder.AND_ELEMENT);
	        	if (boolElement == null) {
	        		//If there isn't one, then create one.
	        		boolElement = rootElement.addElement(QueryBuilder.AND_ELEMENT);
	        	}
	        	boolElement.addElement(QueryBuilder.USERACL_ELEMENT);
	        	
	        	if (!getProfileModule().checkUserSeeAll()) {
	    			Element field = boolElement.addElement(QueryBuilder.GROUP_VISIBILITY_ELEMENT);
	    			if (getProfileModule().checkUserSeeCommunity())
	    	    	{
	    	    		// Add the group visibility element to the filter terms document
	    				field.addAttribute(QueryBuilder.GROUP_VISIBILITY_ATTRIBUTE,EntityIndexUtils.GROUP_SEE_COMMUNITY);
	    	    	} else {
	    	    		field.addAttribute(QueryBuilder.GROUP_VISIBILITY_ATTRIBUTE,EntityIndexUtils.GROUP_SEE_ANY);
	    	    	}
	        	}
	        	
	        	//Create the Lucene query
		    	QueryBuilder qb = new QueryBuilder(getProfileDao().getPrincipalIds(RequestContextHolder.getRequestContext().getUser()));
		    	SearchObject so = qb.buildQuery(qTree);
		    	
		    	//Set the sort order
		    	//SortField[] fields = getBinderEntries_getSortFields(binder); 
		    	//so.setSortBy(fields);
		    	
		    	Query soQuery = so.getQuery();    //Get the query into a variable to avoid doing this very slow operation twice
		    	
		    	if(logger.isInfoEnabled()) {
		    		logger.info("Query is: " + searchQuery.asXML());
		    		logger.info("Query is: " + soQuery.toString());
		    	}
		    	
		    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
		        
		        int maxResults = 10;
		        try {
			        hits = luceneSession.search(soQuery,so.getSortBy(),0,maxResults);
		        }
		        finally {
		            luceneSession.close();
		        }
        	}
        }
		        
        Set ids = new HashSet();
        org.apache.lucene.document.Document doc;
        Field field;
        for (int i = 0; i < hits.length(); i++) {
            doc = hits.doc(i);
            field = doc.getField(EntityIndexUtils.ENTRY_TYPE_FIELD);
            if (field.stringValue().equalsIgnoreCase(EntityIndexUtils.ENTRY_TYPE_USER)) {
            	field = doc.getField(EntityIndexUtils.DOCID_FIELD);
            	try {ids.add(new Long(field.stringValue()));
        	    } catch (Exception ex) {}
            }
            if (field.stringValue().equalsIgnoreCase(EntityIndexUtils.ENTRY_TYPE_GROUP)) {
            	field = doc.getField(EntityIndexUtils.DOCID_FIELD);
            	try {ids.add(new Long(field.stringValue()));
        	    } catch (Exception ex) {}
            }
        }
        entries =  getProfileDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneId());
        Map retMap = new HashMap();
        retMap.put(WebKeys.PEOPLE_RESULTS, entries);
        retMap.put(WebKeys.PEOPLE_RESULTCOUNT, new Integer(hits.getTotalHits()));
        return retMap;
	}

	public ArrayList getSearchTags(String wordroot) {
		ArrayList tags = new ArrayList();
		Element qTreeElement = null;
		
		// Top of query doc 
		Document qTree = DocumentHelper.createDocument();
		Element qTreeRootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
		Element qTreeAclElement = qTreeRootElement.addElement(QueryBuilder.USERACL_ELEMENT);
			
    	//Create the query
    	QueryBuilder qb = new QueryBuilder(getProfileDao().getPrincipalIds(RequestContextHolder.getRequestContext().getUser()));
    	SearchObject so = qb.buildQuery(qTree);
    	
    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        
        try {
	        tags = luceneSession.getTags(so.getQuery(), wordroot);
        }
        finally {
            luceneSession.close();
        }
        ArrayList tagList = new ArrayList();
		for (int j = 0; j < tags.size(); j++) {
			HashMap tag = new HashMap();
			String strTag = (String) tags.get(j);
			tag.put(WebKeys.TAG_NAME, strTag);
			tagList.add(tag);
		}
		return tagList;
	}
	
   	public Binder getBinderByPathName(String pathName) throws AccessControlException {
	   	List binders = getCoreDao().loadObjectsCacheable(Binder.class, new FilterControls("lower(pathName)", pathName.toLowerCase()));
	    	
	   	if(binders.size() > 0) {
	   		Binder binder = (Binder) binders.get(0); // only one matching binder
			checkAccess(binder, "getBinder"); 
	
	   		return binder;
	   	}
	   	else {
	   		return null;
	   	}
	 }

	public boolean hasTeamMembers(Long binderId) {
		Binder binder = loadBinder(binderId);
		//give access to team members OR binder Admins.
		checkAccess(binder, "getTeamMembers");
		return hasTeamMembers(binder);
	}

	public boolean hasTeamUserMembers(Long binderId) {
		Binder binder = loadBinder(binderId);
		//give access to team members OR binder Admins.
		checkAccess(binder, "getTeamMembers");
		return hasTeamUserMembers(binder);
	}

	public boolean hasTeamMembers(Binder binder) {
		List <WorkAreaFunctionMembership> wfms=null;
		if (!binder.isFunctionMembershipInherited() || (binder.getParentWorkArea() == null)) {
	    	wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(RequestContextHolder.getRequestContext().getZoneId(), binder, WorkAreaOperation.TEAM_MEMBER);
		} else {
	    	WorkArea source = binder.getParentWorkArea();
	    	
	    	while (source.isFunctionMembershipInherited()) {
		    	source = source.getParentWorkArea();
		    }
	    	wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(RequestContextHolder.getRequestContext().getZoneId(), source, WorkAreaOperation.TEAM_MEMBER);
	    }
		for (WorkAreaFunctionMembership fm: wfms) {
			// don't explode groups
			if (fm.getMemberIds() != null && fm.getMemberIds().size() > 0) {
				return true;
			}
		}
	    return false;
	}
	
	public boolean hasTeamUserMembers(Binder binder) {
		List <WorkAreaFunctionMembership> wfms=null;
		if (!binder.isFunctionMembershipInherited() || (binder.getParentWorkArea() == null)) {
	    	wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(RequestContextHolder.getRequestContext().getZoneId(), binder, WorkAreaOperation.TEAM_MEMBER);
		} else {
	    	WorkArea source = binder.getParentWorkArea();
	    	
	    	while (source.isFunctionMembershipInherited()) {
		    	source = source.getParentWorkArea();
		    }
	    	wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(RequestContextHolder.getRequestContext().getZoneId(), source, WorkAreaOperation.TEAM_MEMBER);
	    }
		Set ids = new HashSet();
		for (WorkAreaFunctionMembership fm: wfms) {
			ids.addAll(fm.getMemberIds());
		}
		
	    // explode groups
		return getProfileDao().explodeGroups(ids, binder.getZoneId()).size() > 0;	
	}
	

	public List getTeamMembers(Long binderId) {
		Binder binder = loadBinder(binderId);
		//give access to team members  or binder Admins.
		checkAccess(binder, "getTeamMembers");
		return getTeamMembers(binder);
	}
	
	public List getTeamUserMembers(Long binderId) {
		Binder binder = loadBinder(binderId);
		//give access to team members  or binder Admins.
		checkAccess(binder, "getTeamMembers");
		return getTeamUserMembers(binder);
	}
	
	public Set getTeamUserMembersIds(Long binderId) {
		Binder binder = loadBinder(binderId);
		//give access to team members  or binder Admins.
		checkAccess(binder, "getTeamMembers");
		return getTeamUserMembersIds(binder);
	}
	
	public List getTeamMembers(Binder binder) {
		List <WorkAreaFunctionMembership> wfms=null;
		if (!binder.isFunctionMembershipInherited() || (binder.getParentWorkArea() == null)) {
	    	wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(RequestContextHolder.getRequestContext().getZoneId(), binder, WorkAreaOperation.TEAM_MEMBER);
		} else {
	    	WorkArea source = binder.getParentWorkArea();
	    	
	    	while (source.isFunctionMembershipInherited()) {
		    	source = source.getParentWorkArea();
		    }
	    	wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(RequestContextHolder.getRequestContext().getZoneId(), source, WorkAreaOperation.TEAM_MEMBER);
	    }
		Set ids = new HashSet();
		for (WorkAreaFunctionMembership fm: wfms) {
			ids.addAll(fm.getMemberIds());
		}
	    //don't explode groups
	    return getProfileDao().loadPrincipals(ids, binder.getZoneId());
	}
	
	public List getTeamUserMembers(Binder binder) {
		List <WorkAreaFunctionMembership> wfms=null;
		if (!binder.isFunctionMembershipInherited() || (binder.getParentWorkArea() == null)) {
	    	wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(RequestContextHolder.getRequestContext().getZoneId(), binder, WorkAreaOperation.TEAM_MEMBER);
		} else {
	    	WorkArea source = binder.getParentWorkArea();
	    	
	    	while (source.isFunctionMembershipInherited()) {
		    	source = source.getParentWorkArea();
		    }
	    	wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(RequestContextHolder.getRequestContext().getZoneId(), source, WorkAreaOperation.TEAM_MEMBER);
	    }
		Set ids = new HashSet();
		for (WorkAreaFunctionMembership fm: wfms) {
			ids.addAll(fm.getMemberIds());
		}
		
	    // explode groups
		return getProfileDao().loadPrincipals(getProfileDao().explodeGroups(ids, binder.getZoneId()), binder.getZoneId());		
	}
	
	public Set getTeamUserMembersIds(Binder binder) {
		List <WorkAreaFunctionMembership> wfms=null;
		if (!binder.isFunctionMembershipInherited() || (binder.getParentWorkArea() == null)) {
	    	wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(RequestContextHolder.getRequestContext().getZoneId(), binder, WorkAreaOperation.TEAM_MEMBER);
		} else {
	    	WorkArea source = binder.getParentWorkArea();
	    	
	    	while (source.isFunctionMembershipInherited()) {
		    	source = source.getParentWorkArea();
		    }
	    	wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(RequestContextHolder.getRequestContext().getZoneId(), source, WorkAreaOperation.TEAM_MEMBER);
	    }
		Set ids = new HashSet();
		for (WorkAreaFunctionMembership fm: wfms) {
			ids.addAll(fm.getMemberIds());
		}
		
	    // explode groups
		return getProfileDao().explodeGroups(ids, binder.getZoneId());
	}

    public void setPosting(Long binderId, String emailAddress) {
    	Map updates = new HashMap();
    	updates.put("emailAddress", emailAddress);
    	setPosting(binderId, updates);
    }
    public void setPosting(Long binderId, Map updates) {
        Binder binder = coreDao.loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneId()); 
        checkAccess(binder, "setPosting");
        PostingDef post = binder.getPosting();
        String email = (String)updates.get("emailAddress");
        if (Validator.isNull(email)) {
        	//if posting exists for this binder, remove it
        	if (post == null) return;
        	getCoreDao().delete(post);
        	return;
        } else {
            //see if it exists already
        	email = email.toLowerCase();
        	//see if assigned to someone else
        	if ((post == null) || !email.equals(post.getEmailAddress())) {
        		FilterControls fc = new FilterControls();
        		fc.add("emailAddress", email);
        		fc.add("zoneId", binder.getZoneId());
        		List results = getCoreDao().loadObjects(PostingDef.class, fc);
        		if (!results.isEmpty()) {
        			//exists, see if it is assigned
        			PostingDef oldPost = (PostingDef)results.get(0);
        			//if address is assigned, cannot continue
        			if (oldPost.getBinder() != null) {
        				if (!oldPost.getBinder().equals(binder)) {
        					throw new NotSupportedException(NLT.get("errorcode.posting.assigned", new String[]{email}));
        				}
        			}
        			if (post != null) getCoreDao().delete(post);
        			post = oldPost;
        		}
            }
        }
        if (post == null) {
        	post = new PostingDef();
        	post.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
        }
        post.setBinder(binder);
        post.setEnabled(true);
        post.setReplyPostingOption(PostingDef.POST_AS_A_REPLY);
       	ObjectBuilder.updateObject(post, updates);
      	post.setEmailAddress(email);
      	binder.setPosting(post);
    }
	/**
     * Do actual work to either enable or disable email notification.
     * @param id
     * @param value
     */
	public ScheduleInfo getNotificationConfig(Long id) {
       Binder binder = coreDao.loadBinder(id, RequestContextHolder.getRequestContext().getZoneId()); 
        checkAccess(binder, "getNotificationConfig"); 
        //data is stored with job
		EmailNotification process = (EmailNotification)processorManager.getProcessor(binder, EmailNotification.PROCESSOR_KEY);
  		return process.getScheduleInfo(binder);
	}
	    
    public void setNotificationConfig(Long id, ScheduleInfo config) {
        Binder binder = coreDao.loadBinder(id, RequestContextHolder.getRequestContext().getZoneId()); 
        checkAccess(binder, "setNotificationConfig"); 
        //data is stored with job
        EmailNotification process = (EmailNotification)processorManager.getProcessor(binder, EmailNotification.PROCESSOR_KEY);
  		process.setScheduleInfo(config, binder);
    }
    /**
     * Set the notification definition for a folder.  
     * @param id
     * @param updates
     * @param principals - if null, don't change list.
     */
    public void modifyNotification(Long id, Map updates, Collection principals) 
    {
        Binder binder = coreDao.loadBinder(id, RequestContextHolder.getRequestContext().getZoneId()); 
        checkAccess(binder, "modifyNotification"); 
    	NotificationDef current = binder.getNotificationDef();
    	if (current == null) {
    		current = new NotificationDef();
    		binder.setNotificationDef(current);
    	}
    	ObjectBuilder.updateObject(current, updates);
    	if (principals == null) return;
  		//	Pre-load for performance
    	List notifyUsers = new ArrayList();
    	getProfileDao().loadPrincipals(principals, binder.getZoneId());
   		for (Iterator iter=principals.iterator(); iter.hasNext();) {
   			//	make sure user exists and is in this zone
   			Principal p = getProfileDao().loadPrincipal((Long)iter.next(),binder.getZoneId());
   			notifyUsers.add(p);   			
   		}
   		current.setDistribution(notifyUsers);
    }
    
}
