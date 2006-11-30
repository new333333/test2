
package com.sitescape.ef.module.binder.impl;

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
import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.NoObjectByTheIdException;
import com.sitescape.ef.NotSupportedException;
import com.sitescape.ef.ErrorCodes;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.domain.Attachment;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.domain.PostingDef;
import com.sitescape.ef.domain.Subscription;
import com.sitescape.ef.domain.Tag;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.exception.UncheckedCodedContainerException;
import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.binder.BinderProcessor;
import com.sitescape.ef.module.binder.EntryProcessor;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.shared.EntityIndexUtils;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.module.shared.ObjectBuilder;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.search.SearchObject;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.function.Function;
import com.sitescape.ef.security.function.WorkArea;
import com.sitescape.ef.security.function.WorkAreaFunctionMembership;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.util.TagUtil;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.FilterHelper;
/**
 * @author Janet McCann
 *
 */
public class BinderModuleImpl extends CommonDependencyInjection implements BinderModule {
    protected DefinitionModule definitionModule;
    protected ProfileModule profileModule;
    
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
	
	private Binder loadBinder(Long binderId) {
		return getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneName());
	}
	private EntryProcessor loadEntryProcessor(Binder binder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.ef.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.

		return (EntryProcessor)getProcessorManager().getProcessor(binder, binder.getProcessorKey(EntryProcessor.PROCESSOR_KEY));			
	}
	private BinderProcessor loadBinderProcessor(Binder binder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.ef.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.

		return (BinderProcessor)getProcessorManager().getProcessor(binder, binder.getProcessorKey(BinderProcessor.PROCESSOR_KEY));

	}
   
	public Binder getBinder(Long binderId)
			throws NoBinderByTheIdException, AccessControlException {
		Binder binder = loadBinder(binderId);
		// Check if the user has "read" access to the binder.
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);		

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
    public void indexTree(Long binderId) {
		Binder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder,  WorkAreaOperation.BINDER_ADMINISTRATION);
    	//get sub-binder and index them all
		indexBinder(binder);
    }
    private void indexBinder(Binder binder) {
    	List binders = binder.getBinders();
		for (int i=0; i<binders.size(); ++i) {
	    	Binder b = (Binder)binders.get(i);
	    	indexBinder(b);
		}
	    loadBinderProcessor(binder).indexBinder(binder);
    	
    }
    public void indexBinder(Long binderId) {
		Binder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder,  WorkAreaOperation.BINDER_ADMINISTRATION);
 	    loadBinderProcessor(binder).indexBinder(binder);
    }

    public void modifyBinder(Long binderId, final InputDataAccessor inputData) 
	throws AccessControlException, WriteFilesException {
    	modifyBinder(binderId, inputData, new HashMap(),  null);
}
    public void modifyBinder(Long binderId, InputDataAccessor inputData, 
    		Map fileItems, Collection deleteAttachments) throws AccessControlException, WriteFilesException {
    	Binder binder = loadBinder(binderId);
    	checkModifyBinderAllowed(binder);
    	List atts = new ArrayList();
    	if (deleteAttachments != null) {
    		for (Iterator iter=deleteAttachments.iterator(); iter.hasNext();) {
    			String id = (String)iter.next();
    			Attachment a = binder.getAttachment(id);
    			if (a != null) atts.add(a);
    		}
    	}
    	loadBinderProcessor(binder).modifyBinder(binder, inputData, fileItems, atts);
    }
    public void checkModifyBinderAllowed(Binder binder) {
    	getAccessControlManager().checkOperation(binder, WorkAreaOperation.BINDER_ADMINISTRATION);
    }
    public void setProperty(Long binderId, String property, Object value) {
    	Binder binder = loadBinder(binderId);
    	checkModifyBinderAllowed(binder);
		binder.setProperty(property, value);	
   }    
    public void deleteBinder(Long binderId) {
    	Binder binder = loadBinder(binderId);
    	//if can delete this binder, can delete everything under it??
    	checkDeleteBinderAllowed(binder);
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
        		checkDeleteBinderAllowed(b);
       			List e = deleteChildBinders(b);
       			if (e.isEmpty()) loadBinderProcessor(b).deleteBinder(b);
       			else errors.addAll(e);
        	} catch (Exception ex) {
        		errors.add(ex);
        	}
    		
    	}
    	return errors;
    }
    public void checkDeleteBinderAllowed(Binder binder) {
    	getAccessControlManager().checkOperation(binder, WorkAreaOperation.BINDER_ADMINISTRATION);
    }
    public void checkMoveBinderAllowed(Binder binder) {
    	getAccessControlManager().checkOperation(binder, WorkAreaOperation.BINDER_ADMINISTRATION);
    }
    public void moveBinder(Long fromId, Long toId) {
       	Binder source = loadBinder(fromId);
       	checkMoveBinderAllowed(source);
       	Binder destination = loadBinder(toId);
		getAccessControlManager().checkOperation(destination, WorkAreaOperation.CREATE_BINDERS); 
       	
     	loadBinderProcessor(source).moveBinder(source,destination);
           	
    }
	public Binder setDefinitions(Long binderId, boolean inheritFromParent) {
		Binder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS); 
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
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_WORKFLOW_DEFINITIONS);    	
		Map wf = new HashMap();
		Definition def;
		String companyId = binder.getZoneName();
		if (workflowAssociations != null) {
			for (Iterator iter=workflowAssociations.entrySet().iterator(); iter.hasNext();) {
				Map.Entry me = (Map.Entry)iter.next();
				//	TODO:	getAccessControlManager().checkAcl(def, AccessType.READ);
				try {
					def = getCoreDao().loadDefinition((String)me.getValue(), companyId);
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
		String companyId = binder.getZoneName();
		List definitions = new ArrayList(); 
		Definition def;
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS);    	
		//	Build up new set - domain object will handle associations
		if (definitionIds != null) {
			for (int i=0; i<definitionIds.size(); ++i) {
				try {
					def = getCoreDao().loadDefinition((String)definitionIds.get(i), companyId);
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
		List tags = new ArrayList<Tag>();
		tags = getCoreDao().loadCommunityTagsByEntity(binder.getEntityIdentifier());
		return TagUtil.uniqueTags(tags);		
	}
	
	public List getPersonalTags(Long binderId) {
		Binder binder = loadBinder(binderId);
		List tags = new ArrayList<Tag>();
		User user = RequestContextHolder.getRequestContext().getUser();
		tags = getCoreDao().loadPersonalEntityTags(binder.getEntityIdentifier(),user.getEntityIdentifier());
		return TagUtil.uniqueTags(tags);		
	}
	
	/**
	 * Modify tag owned by this binder
	 * @see com.sitescape.ef.module.binder.BinderModule#modifyTag(java.lang.Long, java.lang.String, java.util.Map)
	 */
	public void modifyTag(Long binderId, String tagId, String newTag) {
		Binder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS);    	
	   	Tag tag = coreDao.loadTagById(tagId);
	   	tag.setName(newTag);
	}
	/**
	 * Add a new tag, owned by this binder
	 */
	public void setTag(Long binderId, String newTag, boolean community) {
		Binder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS);    	
	   	Tag tag = new Tag();
	   	User user = RequestContextHolder.getRequestContext().getUser();
	   	tag.setOwnerIdentifier(user.getEntityIdentifier());
	   	tag.setEntityIdentifier(binder.getEntityIdentifier());
	   	tag.setPublic(community);
	  	tag.setName(newTag);
	  	coreDao.save(tag);   	
	}
	/**
	 * Delete a tag owned by this binder
	 */
	public void deleteTag(Long binderId, String tagId) {
		Binder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS);    	
	   	Tag tag = coreDao.loadTagById(tagId);
	   	getCoreDao().delete(tag);
	}

    public void addSubscription(Long binderId, int style) {
    	//getEntry check read access
		Binder binder = loadBinder(binderId);
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
		User user = RequestContextHolder.getRequestContext().getUser();
		return getProfileDao().loadSubscription(user.getId(), binder.getEntityIdentifier());
    }
    public void deleteSubscription(Long binderId) {
    	//getEntry check read access
		Binder binder = loadBinder(binderId);
		User user = RequestContextHolder.getRequestContext().getUser();
		Subscription s = getProfileDao().loadSubscription(user.getId(), binder.getEntityIdentifier());
		if (s != null) getCoreDao().delete(s);
    }
/* not needed
    public void modifySubscription(Long binderId, Map updates) {
		Binder binder = loadBinder(binderId);
		User user = RequestContextHolder.getRequestContext().getUser();
		Subscription s = getProfileDao().loadSubscription(user.getId(), binder.getEntityIdentifier());
		if (s == null) {
			s = new Subscription(user.getId(), binder.getEntityIdentifier());
			getCoreDao().save(s);		
		}
    	ObjectBuilder.updateObject(s, updates);
    }	
*/
	public Map executeSearchQuery(Document searchQuery) {
		Binder binder = null;
		return executeSearchQuery(binder, searchQuery);
	}
	public Map executeSearchQuery(Binder binder, Document searchQuery) {
        List entries = new ArrayList();
        Hits hits = new Hits(0);
        
        if (searchQuery != null) {
        	Document qTree = FilterHelper.convertSearchFilterToSearchBoolean(searchQuery);
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
		EntryProcessor processor = 
			(EntryProcessor) getProcessorManager().getProcessor("com.sitescape.ef.domain.Folder", 
						EntryProcessor.PROCESSOR_KEY);
        entries = (List) processor.getBinderEntries_entriesArray(hits);
        Map retMap = new HashMap();
        retMap.put(WebKeys.FOLDER_ENTRIES,entries);
        retMap.put(WebKeys.ENTRY_SEARCH_COUNT, new Integer(hits.getTotalHits()));
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
        entries =  getProfileDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneName());
        Map retMap = new HashMap();
        retMap.put(WebKeys.PEOPLE_RESULTS, entries);
        retMap.put(WebKeys.PEOPLE_RESULTCOUNT, new Integer(hits.getTotalHits()));
        return retMap;
	}

	public Binder getBinderByPathName(String pathName) throws AccessControlException {
	   	List binders = getCoreDao().loadObjectsCacheable(Binder.class, new FilterControls("lower(pathName)", pathName.toLowerCase()));
	    	
	   	if(binders.size() > 0) {
	   		Binder binder = (Binder) binders.get(0); // only one matching binder
	   		
	   		getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);		
	
	   		return binder;
	   	}
	   	else {
	   		return null;
	   	}
	 }

	public List getTeamMembers(Long binderId) {
		Binder binder = loadBinder(binderId);
		User user = RequestContextHolder.getRequestContext().getUser();
		try {
			//team membership is implemented by a reserved role
			Function function = getFunctionManager().getReservedFunction(binder.getZoneName(), ObjectKeys.TEAM_MEMBER_ROLE_ID);
			//give access to members of role or binder Admins.
			if (!getAccessControlManager().testFunction(user, binder, function)) 
				getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.BINDER_ADMINISTRATION);
			WorkAreaFunctionMembership wfm;
			if (!binder.isFunctionMembershipInherited() || (binder.getParentWorkArea() == null)) {
		    	wfm = getWorkAreaFunctionMembershipManager().getWorkAreaFunctionMembership(binder.getZoneName(), binder, function.getId());
		    } else {
		    	WorkArea source = binder.getParentWorkArea();
		    	while (source.isFunctionMembershipInherited()) {
			    	source = source.getParentWorkArea();
			    }
		    	wfm = getWorkAreaFunctionMembershipManager().getWorkAreaFunctionMembership(binder.getZoneName(), source, function.getId());
		    }
		    if (wfm == null) return new ArrayList();
		    Set ids = wfm.getMemberIds();
		    //do we want to explode groups??
		    return getProfileDao().loadPrincipals(ids, binder.getZoneName());

		} catch (NoObjectByTheIdException no) {
			return new ArrayList();
		}
	}
	   public void modifyPosting(Long binderId, Map updates) {
	    	//posting defs are defined by admin
	    	Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneName());
			checkAdminBinderAllowed(binder); 	       		
	    	//Locate the posting
	   		PostingDef post = binder.getPosting(); 
	   		//cannot modify address through this interface
	   		updates.remove("emailAddress");
	    	if (post != null) ObjectBuilder.updateObject(post, updates);
	    }
	    public void setPosting(Long binderId, String postingId) {
	    	Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneName());
			checkAdminBinderAllowed(binder); 	       		
	   		PostingDef post = getCoreDao().loadPosting(postingId, binder.getZoneName());
	   		Binder oldBinder = post.getBinder();
	   		if ((oldBinder != null) && !oldBinder.equals(binder)) {
				if (getAccessControlManager().testOperation(oldBinder, WorkAreaOperation.BINDER_ADMINISTRATION) == false)
					throw new NotSupportedException(NLT.get(ErrorCodes.PostingAssigned,new Object[] {post.getEmailAddress()}));
				oldBinder.setPosting(null);
	   		}
	   		binder.setPosting(post);
	   		post.setBinder(binder);
	   		post.setEnabled(true);
	   		post.setReplyPostingOption(PostingDef.POST_AS_A_REPLY);
	    }    	
	    public void deletePosting(Long binderId) {
	    	Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneName());
			checkAdminBinderAllowed(binder); 	       		
	   		PostingDef post = binder.getPosting(); 
	    	if (post != null) {
	    		post.setBinder(null);
	    		binder.setPosting(null);
	    	}
	    }
	    public void checkAdminBinderAllowed(Binder binder) throws AccessControlException {
			getAccessControlManager().checkOperation(binder, WorkAreaOperation.BINDER_ADMINISTRATION); 	       		
	    	
	    }


}
