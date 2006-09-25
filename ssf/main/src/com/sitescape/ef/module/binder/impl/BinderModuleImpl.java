
package com.sitescape.ef.module.binder.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.Query;
import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.InvalidArgumentException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.domain.Attachment;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.BinderConfig;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.domain.Subscription;
import com.sitescape.ef.domain.Tag;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.binder.BinderProcessor;
import com.sitescape.ef.module.binder.EntryProcessor;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.module.shared.ObjectBuilder;
import com.sitescape.ef.module.workflow.WorkflowModule;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.search.SearchObject;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.function.WorkAreaFunctionMembershipManager;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.util.TagUtil;
import com.sitescape.ef.web.util.FilterHelper;
/**
 * @author Janet McCann
 *
 */
public class BinderModuleImpl extends CommonDependencyInjection implements BinderModule {
	private static final String[] defaultDefAttrs = new String[]{"internalId", "zoneName", "definitionType"};
	private static final String default_folder="402883b90d0de1f3010d0df5582b0001";
	private static final String default_workspace="402883b90d0de1f3010d0df5582b0002";
	private static final String default_file_folder="402883b90d0de1f3010d0df5582b0003";
    protected DefinitionModule definitionModule;
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
	public Binder getBinderByName(String binderName) 
   			throws NoBinderByTheNameException, AccessControlException {
		Binder binder = getCoreDao().findBinderByName(binderName, RequestContextHolder.getRequestContext().getZoneName());
	    // Check if the user has "read" access to the binder.
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);		
		return binder;
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
   		deleteChildBinders(binder);
     	loadBinderProcessor(binder).deleteBinder(binder);
    }
    protected void deleteChildBinders(Binder binder) {
    	//First process all child folders
    	List binders = new ArrayList(binder.getBinders());
    	for (int i=0; i<binders.size(); ++i) {
    		Binder b = (Binder)binders.get(i);
   			deleteChildBinders(b);
    		loadBinderProcessor(b).deleteBinder(b);
    	}
    	return;
    	
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
	public Binder setConfiguration(Long binderId, boolean inheritFromParent) {
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
    public Binder setConfiguration(Long binderId, List definitionIds, Map workflowAssociations) 
	throws AccessControlException {
		Binder binder = setConfiguration(binderId, definitionIds);
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_WORKFLOW_DEFINITIONS);    	
		Map wf = new HashMap();
		Definition def;
		String companyId = binder.getZoneName();
		for (Iterator iter=workflowAssociations.entrySet().iterator(); iter.hasNext();) {
			Map.Entry me = (Map.Entry)iter.next();
			//	TODO:	getAccessControlManager().checkAcl(def, AccessType.READ);
			try {
				def = getCoreDao().loadDefinition((String)me.getValue(), companyId);
				wf.put(me.getKey(), def);
			} catch (NoDefinitionByTheIdException nd) {}
		}
		binder.setWorkflowAssociations(wf);
		binder.setDefinitionsInherited(false);
		return binder;
	}
	public Binder setConfiguration(Long binderId, List definitionIds) throws AccessControlException {
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
	   	coreDao.update(tag);
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

    public void addSubscription(Long binderId) {
    	//getEntry check read access
		Binder binder = loadBinder(binderId);
		User user = RequestContextHolder.getRequestContext().getUser();
		Subscription s = getProfileDao().loadSubscription(user.getId(), binder.getEntityIdentifier());
		if (s == null) {
			s = new Subscription(user.getId(), binder.getEntityIdentifier());
			getCoreDao().save(s);
		}  	
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

	public List executeSearchQuery(Document searchQuery) {
		Binder binder = null;
		return executeSearchQuery(binder, searchQuery);
	}
	public List executeSearchQuery(Binder binder, Document searchQuery) {
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
		    	QueryBuilder qb = new QueryBuilder();
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
        
    	return entries; 
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
	public BinderConfig createDefaultConfiguration(int type) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		String title=null;
		String internalId=null;
		switch (type) {
			case Definition.FOLDER_VIEW: {
				List result = getCoreDao().loadObjects(BinderConfig.class, 
						new FilterControls(defaultDefAttrs, new Object[]{default_folder, zoneName, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (BinderConfig)result.get(0);
				title = "__configuration_default_folder";
				internalId = default_folder;
				break;
			}

			case Definition.WORKSPACE_VIEW: {
				List result = getCoreDao().loadObjects(BinderConfig.class, 
						new FilterControls(defaultDefAttrs, new Object[]{default_workspace, zoneName, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (BinderConfig)result.get(0);
				title = "__configuration_default_workspace";
				internalId = default_workspace;
				break;
			}
			case Definition.FILE_FOLDER_VIEW: {
				List result = getCoreDao().loadObjects(BinderConfig.class, 
					new FilterControls(defaultDefAttrs, new Object[]{default_file_folder, zoneName, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (BinderConfig)result.get(0);
				title = "__configuration_default_file_folder";
				internalId = default_file_folder;
				break;
			}
		default: {
			throw new InvalidArgumentException("Invalid type:" + type);
			}
		}
		String id = addConfiguration(type, title);
		BinderConfig cfg = getCoreDao().loadConfiguration(id, zoneName);
		cfg.setInternalId(internalId);
		return cfg;

	}
	 public String addConfiguration(int type, String title) {
		List defs = new ArrayList();
		defs.add(getDefinitionModule().createDefaultDefinition(type).getId());
		BinderConfig config = new BinderConfig();
		switch (type) {
			case Definition.WORKSPACE_VIEW: {
				break;
			}
			case Definition.FOLDER_VIEW: {
				defs.add(getDefinitionModule().createDefaultDefinition(Definition.FOLDER_ENTRY).getId());
				break;
			}
			case Definition.FILE_FOLDER_VIEW: {
				defs.add(getDefinitionModule().createDefaultDefinition(Definition.FILE_ENTRY_VIEW).getId());
				break;
			}
			default: {
				throw new InvalidArgumentException("Invalid type:" + type);
			}
		}
		
		config.setTitle(title);
		config.setZoneName(RequestContextHolder.getRequestContext().getZoneName());
		config.setDefinitionIds(defs);
		config.setDefinitionType(type);
		getCoreDao().save(config);
		return config.getId();
		
	}
	public void deleteConfiguration(String id) {
		BinderConfig config = getCoreDao().loadConfiguration(id, RequestContextHolder.getRequestContext().getZoneName());
		getCoreDao().delete(config);
	}
	public void modifyConfiguration(String id, Map updates) {
		BinderConfig config = getCoreDao().loadConfiguration(id, RequestContextHolder.getRequestContext().getZoneName());
		ObjectBuilder.updateObject(config, updates);
	}
	public BinderConfig getConfiguration(String id) {
		BinderConfig config = getCoreDao().loadConfiguration(id, RequestContextHolder.getRequestContext().getZoneName());
		//TODO: access check
		return config;
		
	}
	public List getConfigurations() {
		List result = getCoreDao().loadConfigurations(RequestContextHolder.getRequestContext().getZoneName());
		//TODO: access check
		return result;
	}
	public List getConfigurations(int type) {
		List result = getCoreDao().loadConfigurations( RequestContextHolder.getRequestContext().getZoneName(), type);
		//TODO: access check
		return result;
	}
}
