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
package com.sitescape.team.module.binder.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.NonUniqueObjectException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

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
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.LibraryEntry;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.domain.NoFolderByTheIdException;
import com.sitescape.team.domain.NotificationDef;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.Tag;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.jobs.EmailNotification;
import com.sitescape.team.jobs.ScheduleInfo;
import com.sitescape.team.lucene.Hits;
import com.sitescape.team.module.binder.BinderComparator;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.binder.BinderProcessor;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.shared.ObjectBuilder;
import com.sitescape.team.module.shared.SearchUtils;
import com.sitescape.team.search.IndexSynchronizationManager;
import com.sitescape.team.search.LuceneSession;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.search.SearchObject;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.TagUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.Validator;
/**
 * @author Janet McCann
 *
 */
public class BinderModuleImpl extends CommonDependencyInjection implements BinderModule, InitializingBean {
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
		operations.put("setDefinitions", new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("modifyTag", new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("deleteTag", new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("setNotificationConfig", new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("modifyNotification", new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("getTeamMembers", new WorkAreaOperation[] {WorkAreaOperation.TEAM_MEMBER,WorkAreaOperation.BINDER_ADMINISTRATION});
		operations.put("setPosting", new WorkAreaOperation[] {WorkAreaOperation.MANAGE_BINDER_INCOMING, WorkAreaOperation.SITE_ADMINISTRATION});
		operations.put("accessControl", new WorkAreaOperation[]{WorkAreaOperation.CHANGE_ACCESS_CONTROL});
	}
 
	private TransactionTemplate transactionTemplate;
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	/*
	 * Check access to binder.  If operation not listed, assume read_entries needed
	 * Use method names as operation so we can keep the logic out of application
	 * and easisly change the required rights
	 * 
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
	 * Use method names as operation so we can keep the logic out of application
	 * and easisly change the required rights
	 * @see com.sitescape.team.module.binder.BinderModule#checkAccess(com.sitescape.team.domain.Binder, java.lang.String)
	 */
	public boolean testAccess(Long binderId, String operation)  {
		return testAccess(loadBinder(binderId), operation);
	}
	
	/**
	 * Use method names instead of WorkAreaOperation so application doesn't have the required knowledge.  
	 * This also makes it easier to change what operations and allow multiple operations need to execute a method.
	 * @param binder
	 * @param operation
	 * @throws AccessControlException
	 */	
	protected void checkAccess(Binder binder, String operation) throws AccessControlException {
		if (binder instanceof TemplateBinder) {
  			//guess anyone can read a template
  			if ("getBinder".equals(operation)) return;
  			getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getZone(), WorkAreaOperation.SITE_ADMINISTRATION);
  		} else {
  			WorkAreaOperation[] wfo = (WorkAreaOperation[])operations.get(operation);
  			if (wfo == null) {
  				getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);
  			} else {
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
  		
	protected void checkAccess(Binder binder, WorkAreaOperation operation) throws AccessControlException {
		if (binder instanceof TemplateBinder) {
			//Only allow template access checks if site-admin
			getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getZone(), WorkAreaOperation.SITE_ADMINISTRATION);
  		} else {
			//	will throw exception on failure
			getAccessControlManager().checkOperation(binder, operation);
		}
	}
	
	private Binder loadBinder(Long binderId) {
		Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneId());
		if (binder.isDeleted()) throw new NoBinderByTheIdException(binderId);
		return binder;
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
	public Set<Binder> getBinders(Collection<Long> binderIds) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new BinderComparator(user.getLocale(), BinderComparator.SortByField.title);
       	TreeSet<Binder> result = new TreeSet<Binder>(c);
		for (Long id:binderIds) {
			try {//access check done by getFolder
				//assume most folders are cached
				result.add(getBinder(id));
			} catch (NoFolderByTheIdException ex) {
			} catch (AccessControlException ax) {
			}
			
		}
		return result;
	}
	// Use search engine
	public Map getBinders(Binder binder, Map options) {
		//assume have access to binder cause have a reference
		BinderProcessor processor = loadBinderProcessor(binder);
		return processor.getBinders(binder, options);
	}
    public boolean hasBinders(Binder binder) {
    	List binders = binder.getBinders();
    	for (int i=0; i<binders.size(); ++i) {
    		Binder b = (Binder)binders.get(i);
    		if (b.isDeleted()) continue;
            if (getAccessControlManager().testOperation(b, WorkAreaOperation.READ_ENTRIES)) return true;    	       		
    	}
    	return false;
    }	
    public boolean hasBinders(Binder binder, EntityIdentifier.EntityType binderType) {
    	List binders = binder.getBinders();
    	for (int i=0; i<binders.size(); ++i) {
    		Binder b = (Binder)binders.get(i);
    		if (b.isDeleted()) continue;
    		//only check for specific types
    		if (b.getEntityType().equals(binderType)) 
    			if (getAccessControlManager().testOperation(b, WorkAreaOperation.READ_ENTRIES)) return true;    	       		
    	}
    	return false;
    }	
    public Collection indexTree(Long binderId) {
    	List ids = new ArrayList();
    	ids.add(binderId);
    	return indexTree(ids);
    }
    //optimization so we can manage the deletion to the searchEngine
    public Collection indexTree(Collection binderIds) {
    	//make list of binders we have access to first
    	boolean clearAll = false;
    	List<Binder> binders = getCoreDao().loadObjects(binderIds, Binder.class, RequestContextHolder.getRequestContext().getZoneId());
    	List<Binder> checked = new ArrayList();
    	for (Binder binder:binders) {
    		try {
    			checkAccess(binder, "indexTree");
    			if (binder.isDeleted()) continue;
    			if (binder.isZone()) clearAll = true;
    			checked.add(binder);
    		} catch (Exception ex) {};
    		
    	}
    	List done = new ArrayList();
    	if (checked.isEmpty()) return done;

		if (clearAll) {
			LuceneSession luceneSession = getLuceneSessionFactory().openSession();
			try {
				luceneSession.clearIndex();

			} catch (Exception e) {
				System.out.println("Exception:" + e);
			} finally {
				luceneSession.close();
			}
		} else {
			//	delete all sub-binders - walk the ancestry list
			// 	and delete all the entries under each folderid.
			for (Binder binder:checked) {
				IndexSynchronizationManager.deleteDocuments(new Term(EntityIndexUtils.ENTRY_ANCESTRY, binder.getId().toString()));
			}
		}
	   	for (Binder binder:checked) {
	   		done.addAll(loadBinderProcessor(binder).indexTree(binder, done));
	   	}
	   	return done;
	} 
    public void indexBinder(Long binderId) {
    	indexBinder(binderId, false);
    }
    public void indexBinder(Long binderId, boolean includeEntries) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, "indexBinder");
 	    loadBinderProcessor(binder).indexBinder(binder, includeEntries);
    }

    public void modifyBinder(Long binderId, final InputDataAccessor inputData) 
    	throws AccessControlException, WriteFilesException {
    	modifyBinder(binderId, inputData, new HashMap(),  null);
    }
    public void modifyBinder(Long binderId, InputDataAccessor inputData, 
    		Map fileItems, Collection deleteAttachments) throws AccessControlException, WriteFilesException {
    	final Binder binder = loadBinder(binderId);
    	//save library flag
    	boolean oldLibrary = binder.isLibrary();
    	boolean oldUnique = binder.isUniqueTitles();
    	
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
   		if (inputData.exists(ObjectKeys.FIELD_BINDER_LIBRARY)) {
   			final boolean newLibrary = Boolean.valueOf(inputData.getSingleValue(ObjectKeys.FIELD_BINDER_LIBRARY));
   			if (oldLibrary != newLibrary) {
   				//wrap in a transaction
   		        getTransactionTemplate().execute(new TransactionCallback() {
   		        	public Object doInTransaction(TransactionStatus status) {
	        			//remove old reserved names
	        			getCoreDao().clearFileNames(binder);
  		        		if (newLibrary) {
   		        			//add new ones
   		        			//get all attachments in this binder
   		        			FilterControls filter = new FilterControls(new String[]{"owner.owningBinderId", "type"},
   		        					new Object[] {binder.getId(), "F"});
   		        			ObjectControls objs = new ObjectControls(FileAttachment.class, new String[] {"fileItem.name", "owner.ownerId"});
   		        			SFQuery query = getCoreDao().queryObjects(objs, filter);
   		        			try {
   		        				while (query.hasNext()) {
   		        					Object [] result = (Object[])query.next();
   		        					//skip files attached to the binder itself
   		        					if (result[1].equals(binder.getId())) continue;
   		        					LibraryEntry le = new LibraryEntry(binder.getId(), LibraryEntry.FILE, (String)result[0]);
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
   		        		binder.setLibrary(newLibrary);
   		        		return null;
	        	}});

   			}
    	}
   		if (inputData.exists(ObjectKeys.FIELD_BINDER_UNIQUETITLES)) {
   			final boolean newUnique = Boolean.valueOf(inputData.getSingleValue(ObjectKeys.FIELD_BINDER_UNIQUETITLES));
  			if (newUnique != oldUnique) {
   				//wrap in a transaction
   		        getTransactionTemplate().execute(new TransactionCallback() {
   		        	public Object doInTransaction(TransactionStatus status) {
	        			//remove old reserved names
	        			getCoreDao().clearTitles(binder);
  		        		if (newUnique) {
   		        			List<Binder> binders = binder.getBinders();
   		        			//first add subfolder titles
   		        			try {
   		        				for (Binder b:binders) {
   		        					getCoreDao().updateTitle(binder, b, null, b.getNormalTitle());   		        				
   		        				}
  		        			} catch (HibernateSystemException he) {
   		        				if (he.contains(NonUniqueObjectException.class)) {
   		        					throw new ConfigurationException(NLT.get("errorcode.cannot.make.unique"));
   		        				}
  		        			}
   		        			//add entry titles
  		        			if (binder instanceof Folder) {
  		        				Folder parentFolder = (Folder)binder;
  		        				SFQuery query = getFolderDao().queryChildEntries(parentFolder); 
  		        				try {
  		        					while (query.hasNext()) {
  	  		        					Object obj = query.next();
  	   		        					if (obj instanceof Object[])
  	   		        						obj = ((Object [])obj)[0];
  	   		     		        		FolderEntry entry = (FolderEntry)obj;
  	   		        					LibraryEntry le = new LibraryEntry(binder.getId(), LibraryEntry.TITLE, entry.getNormalTitle());
  	   		        					le.setEntityId(entry.getId());
  		        						getCoreDao().save(le);
  		        					}
  		        				} catch (HibernateSystemException he) {
  		        					if (he.contains(NonUniqueObjectException.class)) {
  		        						throw new ConfigurationException(NLT.get("errorcode.cannot.make.unique"));
  		        					}
   		        				
  		        				} finally {
  		        					query.close();
  		        				}
  		        			}
   		        		}
   		        		binder.setUniqueTitles(newUnique);
   		        		return null;
	        	}});

   			}
  		}
    }
 
    public void setProperty(Long binderId, String property, Object value) {
    	Binder binder = loadBinder(binderId);
		checkAccess(binder, "setProperty");
		binder.setProperty(property, value);	
   }    
    public List deleteBinder(Long binderId) {
    	return deleteBinder(binderId, true);
    }
    public List deleteBinder(Long binderId, boolean deleteMirroredSource) {
    	Binder binder = loadBinder(binderId);
		checkAccess(binder, "deleteBinder");
		
   		List errors = deleteChildBinders(binder, deleteMirroredSource);
   		if (!errors.isEmpty()) return errors;
   		loadBinderProcessor(binder).deleteBinder(binder, deleteMirroredSource);
   		return null;
    }
    protected List deleteChildBinders(Binder binder, boolean deleteMirroredSource) {
    	//First process all child folders
    	List binders = new ArrayList(binder.getBinders());
    	List errors = new ArrayList();
    	for (int i=0; i<binders.size(); ++i) {
    		Binder b = (Binder)binders.get(i);
    		//see if already taken care of
    		if (b.isDeleted()) continue;
        	try {
        		checkAccess(b, "deleteBinder");
       			List e = deleteChildBinders(b, deleteMirroredSource);
       			if (e.isEmpty()) loadBinderProcessor(b).deleteBinder(b, deleteMirroredSource);
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
    	//access checked in setDefinitions
    	Binder binder = setDefinitions(binderId, definitionIds);
		Map wf = new HashMap();
		Definition def;
		if (workflowAssociations != null) {
			for (Iterator iter=workflowAssociations.entrySet().iterator(); iter.hasNext();) {
				Map.Entry me = (Map.Entry)iter.next();
				//	TODO:(not implemented yet) getAccessControlManager().checkAcl(def, AccessType.READ);
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
		checkAccess(binder, "setDefinitions"); 
		List definitions = new ArrayList(); 
		Definition def;
		//	Build up new set - domain object will handle associations
		if (definitionIds != null) {
			for (int i=0; i<definitionIds.size(); ++i) {
				try {
					def = getCoreDao().loadDefinition((String)definitionIds.get(i), 
							RequestContextHolder.getRequestContext().getZoneId());
					//	TODO:	(not implemented yet) getAccessControlManager().checkAcl(def, AccessType.READ);
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
		List<Tag> tags = getCoreDao().loadCommunityTagsByEntity(binder.getEntityIdentifier());
		return TagUtil.uniqueTags(tags);		
	}
	
	public List getPersonalTags(Long binderId) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, "getPersonalTags");		
		User user = RequestContextHolder.getRequestContext().getUser();
		List<Tag> tags = getCoreDao().loadPersonalEntityTags(binder.getEntityIdentifier(),user.getEntityIdentifier());
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
 	    loadBinderProcessor(binder).indexBinder(binder, false);
	}
	/**
	 * Add a new tag, owned by this binder
	 */
	public void setTag(Long binderId, String newTag, boolean community) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, "setTag"); 
		if (Validator.isNull(newTag)) return;
		newTag = newTag.replaceAll("\\W", " ").trim().replaceAll("\\s+"," ");
		String[] newTags = newTag.split(" ");
		if (newTags.length == 0) return;
		List tags = new ArrayList();
		User user = RequestContextHolder.getRequestContext().getUser();
	   	EntityIdentifier uei = user.getEntityIdentifier();
	   	EntityIdentifier bei = binder.getEntityIdentifier();
	   	for (int i = 0; i < newTags.length; i++) {
	   		String tagName = newTags[i].trim();
	   		if (tagName.length() > ObjectKeys.MAX_TAG_LENGTH) {
	   			//Truncate the tag so it fits in the database field
	   			tagName = tagName.substring(0, ObjectKeys.MAX_TAG_LENGTH);
	   		}
			Tag tag = new Tag();
		   	tag.setOwnerIdentifier(uei);
		   	tag.setEntityIdentifier(bei);
		   	tag.setPublic(community);
	   		tag.setName(newTags[i]);
	   		tags.add(tag);
	   	}
	   	coreDao.save(tags);
 	    loadBinderProcessor(binder).indexBinder(binder, false);
	}
	
	/**
	 * Delete a tag owned by this binder
	 */
	public void deleteTag(Long binderId, String tagId) {
		Binder binder = loadBinder(binderId);
		checkAccess(binder, "deleteTag"); 
	   	Tag tag = coreDao.loadTagById(tagId);
	   	getCoreDao().delete(tag);
	   	indexBinder(binderId);
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
		return executeSearchQuery(searchQuery, null);
	}
	public Map executeSearchQuery(Document searchQuery, Map options) {
        List entries = new ArrayList();
        Hits hits = new Hits(0);
       
       	Document qTree = SearchUtils.getInitalSearchDocument(searchQuery, options);
    	SearchUtils.getQueryFields(qTree, options); 

       	//Create the Lucene query
	   	QueryBuilder qb = new QueryBuilder(getProfileDao().getPrincipalIds(RequestContextHolder.getRequestContext().getUser()));
	   	SearchObject so = qb.buildQuery(qTree);
		    	
	   	//Set the sort order
	   	SortField[] fields = SearchUtils.getSortFields(options); 
	   	so.setSortBy(fields);
		    	
	   	Query soQuery = so.getQuery();    //Get the query into a variable to avoid doing this very slow operation twice
		    	
	   	if(logger.isDebugEnabled()) {
	   		logger.debug("Query is in executeSearchQuery: " + searchQuery.asXML());
	   		logger.debug("Query is in executeSearchQuery: " + soQuery.toString());
	   	}
		    	
	   	int maxResults = 10;
	   	int offset = 0;
	   	if (options != null) {
	   		if (options.containsKey(ObjectKeys.SEARCH_MAX_HITS)) 
	   			maxResults = (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS);
	   		if (options.containsKey(ObjectKeys.SEARCH_OFFSET)) 
	   			offset = (Integer) options.get(ObjectKeys.SEARCH_OFFSET);
	   	}
	   	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
	   	try {
	        hits = luceneSession.search(soQuery,so.getSortBy(),offset,maxResults);
	   	} catch(Exception e) {
	   		System.out.println("Exception:" + e);
	   	} finally {
	   		luceneSession.close();
	    }
	       
	    entries = SearchUtils.getSearchEntries(hits);
	    SearchUtils.extendPrincipalsInfo(entries, getProfileDao(), EntityIndexUtils.CREATORID_FIELD);
               
        Map retMap = new HashMap();
        retMap.put(ObjectKeys.SEARCH_ENTRIES,entries);
        retMap.put(ObjectKeys.SEARCH_COUNT_TOTAL, new Integer(hits.getTotalHits()));
        retMap.put(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED, new Integer(hits.length()));
 
    	return retMap; 
	}	
    


	public ArrayList getSearchTags(String wordroot, String type) {
		ArrayList tags = new ArrayList();
		
		User user = RequestContextHolder.getRequestContext().getUser();
		SearchObject so = null;
		if (!user.isSuper()) {		
			// Top of query doc 
			Document qTree = DocumentHelper.createDocument();
			Element qTreeRootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
			//qTreeRootElement.addElement(QueryBuilder.USERACL_ELEMENT);
				
	    	//Create the query
	    	QueryBuilder qb = new QueryBuilder(getProfileDao().getPrincipalIds(RequestContextHolder.getRequestContext().getUser()));
			so = qb.buildQuery(qTree);
		}
    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        
        try {
	        tags = luceneSession.getTags(so!=null?so.getQuery():null, wordroot, type);
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
	   	List<Binder> binders = getCoreDao().loadObjectsCacheable(Binder.class, new FilterControls("lower(pathName)", pathName.toLowerCase()));
	    	
	    // only maximum of one matching non-deleted binder
	   	for(Binder binder : binders) {
	   		if(binder.isDeleted()) continue;
			checkAccess(binder, "getBinder"); 			
	   		return binder;
	   	}
	   	
	   	return null;
	 }

	public boolean hasTeamMembers(Long binderId, boolean explodeGroups) {
		Binder binder = loadBinder(binderId);
		//give access to team members OR binder Admins.
		checkAccess(binder, "getTeamMembers");
		Set ids = getAccessControlManager().getWorkAreaAccessControl(binder, WorkAreaOperation.TEAM_MEMBER);		
		if (explodeGroups) {
			// explode groups
			return getProfileDao().explodeGroups(ids, binder.getZoneId()).size() > 0;
		}
		if (ids.isEmpty()) return false;
		return true;
	}
		

	public List getTeamMembers(Long binderId, boolean explodeGroups) {
		//give access to team members  or binder Admins.
		Set ids = getTeamMemberIds(binderId, explodeGroups);
		//turn ids into real Principals
 	    return getProfileDao().loadPrincipals(ids, RequestContextHolder.getRequestContext().getZoneId(), true);
	}
	
	
	public Set getTeamMemberIds(Long binderId, boolean explodeGroups) {
		Binder binder = loadBinder(binderId);
		//give access to team members  or binder Admins.
		if (testAccess(binder, "getTeamMembers")) {
			return getTeamMemberIds(binder, explodeGroups);
		}
		//Not privileged, send back an empty set
		return new HashSet();
	}
	/*
	 *  (non-Javadoc)
	 *  No access check - used internally to send notifications
	 * @see com.sitescape.team.module.binder.BinderModule#getTeamMemberIds(com.sitescape.team.domain.Binder, boolean)
	 */
	public Set getTeamMemberIds(Binder binder, boolean explodeGroups) {
		Set ids = getAccessControlManager().getWorkAreaAccessControl(binder, WorkAreaOperation.TEAM_MEMBER);		
	    // explode groups
		if (explodeGroups) return getProfileDao().explodeGroups(ids, binder.getZoneId());
		return ids;
	}

    public void setPosting(Long binderId, String emailAddress) {
    	Map updates = new HashMap();
    	updates.put("emailAddress", emailAddress);
    	setPosting(binderId, updates);
    }
    public void setPosting(Long binderId, Map updates) {
        Binder binder = loadBinder(binderId); 
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
        	getCoreDao().save(post);
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
	public ScheduleInfo getNotificationConfig(Long binderId) {
        Binder binder = loadBinder(binderId); 
        //Anyone can read 
        //data is stored with job
		EmailNotification process = (EmailNotification)processorManager.getProcessor(binder, EmailNotification.PROCESSOR_KEY);
  		return process.getScheduleInfo(binder);
	}
	    
    public void setNotificationConfig(Long binderId, ScheduleInfo config) {
        Binder binder = loadBinder(binderId); 
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
    public void modifyNotification(Long binderId, Map updates, Collection principals) {
        Binder binder = loadBinder(binderId); 
        checkAccess(binder, "modifyNotification"); 
    	NotificationDef current = binder.getNotificationDef();
    	if (current == null) {
    		current = new NotificationDef();
    		binder.setNotificationDef(current);
    	}
    	ObjectBuilder.updateObject(current, updates);
    	if (principals == null) return;
  		//	Pre-load for performance
    	List notifyUsers = getProfileDao().loadPrincipals(principals, binder.getZoneId(), true);
   		current.setDistribution(notifyUsers);
    }
    
}
