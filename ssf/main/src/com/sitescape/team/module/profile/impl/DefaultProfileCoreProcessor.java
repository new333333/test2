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
package com.sitescape.team.module.profile.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.SFQuery;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.jobs.UserTitleChange;
import com.sitescape.team.module.binder.BinderProcessor;
import com.sitescape.team.module.binder.impl.AbstractEntryProcessor;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.profile.ProfileCoreProcessor;
import com.sitescape.team.module.profile.index.ProfileIndexUtils;
import com.sitescape.team.module.shared.ChangeLogUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.EntryBuilder;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.module.shared.SearchUtils;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.util.CollectionUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.team.web.util.FilterHelper;
import com.sitescape.util.Validator;
/**
 *
 * @author Jong Kim
 */
public class DefaultProfileCoreProcessor extends AbstractEntryProcessor
	implements ProfileCoreProcessor {
    
	//cannot be deleted
    protected void deleteBinder_preDelete(Binder binder, Map ctx) {     	
    }
  
    
    protected void deleteBinder_processFiles(Binder binder, Map ctx) {
    }
    
    protected void deleteBinder_delete(Binder binder, Map ctx) {
    }
    protected void deleteBinder_postDelete(Binder binder, Map ctx) {
    }

    protected void deleteBinder_indexDel(Binder binder, Map ctx) {
     }
    
    //*******************************************************************/
    protected void addBinder_fillIn(Binder parent, Binder binder, InputDataAccessor inputData, Map entryData, Map ctx) {
    	super.addBinder_fillIn(parent,binder, inputData, entryData, ctx);
    	Integer type = binder.getDefinitionType();
    	if ((type != null) && (type.intValue() == Definition.USER_WORKSPACE_VIEW)) {
    		Principal u = binder.getOwner();
    		if (!(u instanceof User)) {
    			u = getProfileDao().loadPrincipal(u.getId(), u.getZoneId(), false);
    		}
    		if (u instanceof User) {
    			((Workspace)binder).setSearchTitle(((User)u).getSearchTitle());
    		}
    	}
   	
    }
    //***********************************************************************************************************
            
    //inside write transaction
   protected void addEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {  
        ((Principal)entry).setZoneId(binder.getZoneId());
        doProfileEntryFillin(entry, inputData, entryData);
        super.addEntry_fillIn(binder, entry, inputData, entryData, ctx);
     }
    //inside write transaction
    protected void addEntry_postSave(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
    	//make user the owner so create_modify access works
    	if (entry instanceof User) {
    		entry.getCreation().setPrincipal((User)entry);
    		entry.getModification().setPrincipal((User)entry);
    	}
    	super.addEntry_postSave(binder, entry, inputData, entryData, ctx);
    }
       
    //***********************************************************************************************************	
    //inside write transaction
   protected void modifyEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {  
    	//see if we have updates to fields not covered by definition build
    	doProfileEntryFillin(entry, inputData, entryData);
    	super.modifyEntry_fillIn(binder, entry, inputData, entryData, ctx);
    }
   //inside write transaction
   protected void modifyEntry_postFillIn(Binder binder, Entry entry, InputDataAccessor inputData, 
   		Map entryData, Map<FileAttachment,String> fileRenamesTo, Map ctx) {
	   super.modifyEntry_postFillIn(binder, entry, inputData, entryData, fileRenamesTo, ctx);
	   if (entry instanceof User) {
		   //need to schedule a reindex of entries with my title
		   checkUserTitle((User)entry, ctx);
	   }
		   
   }
   protected void checkUserTitle(User user, Map ctx) {
	   String originalTitle = (String)ctx.get(ObjectKeys.FIELD_ENTITY_TITLE);
	   if (!user.getTitle().equals(originalTitle)) {
		   //need to update user workspace
		   List excludeIds=null;
		   if (user.getWorkspaceId() != null) {
			   Workspace ws = (Workspace)getCoreDao().load(Workspace.class, user.getWorkspaceId());
			   if (ws != null) {
				   BinderProcessor processor = (BinderProcessor)getProcessorManager().getProcessor(ws, ws.getProcessorKey(BinderProcessor.PROCESSOR_KEY));
				   Map updates = new HashMap();
				   updates.put(ObjectKeys.FIELD_ENTITY_TITLE, user.getTitle());
				   updates.put(ObjectKeys.FIELD_BINDER_SEARCHTITLE, user.getSearchTitle());
				   try {
					   processor.modifyBinder(ws, new MapInputData(updates), null, null);
					   excludeIds = new ArrayList();
					   excludeIds.add(ws.getId());
					   //modifyBinder will also reindex direct child binders, so remove them
					   List<Binder> binders = ws.getBinders();
					   for (Binder child:binders) {
						   excludeIds.add(child.getId());
					   }
				   } catch (WriteFilesException wf) {};
				   
			   }
		   }
		   List entryIds = getCoreDao().loadObjects("select id from com.sitescape.team.domain.FolderEntry where creation.principal=" +
				   user.getId() + " or modification.principal=" + user.getId(), null);
		   List binderIds = getCoreDao().loadObjects("select id from com.sitescape.team.domain.Binder where creation.principal=" +
				   	user.getId() + " or modification.principal=" + user.getId(), null);
		   //remove binders already processed in modifyBinder above
		   if (excludeIds != null) binderIds.removeAll(excludeIds);
		   String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		   String jobClass = SZoneConfig.getString(zoneName, "userConfiguration/property[@name='" + UserTitleChange.USER_TITLE_JOB + "']");
		   if (Validator.isNull(jobClass)) jobClass = "com.sitescape.team.jobs.DefaultUserTitleChange";
		   try {
			   Class processorClass = ReflectHelper.classForName(jobClass);
			   UserTitleChange job = (UserTitleChange)processorClass.newInstance();
			   job.schedule(user, binderIds, entryIds);
 	
		   } catch (ClassNotFoundException e) {
			   throw new ConfigurationException(
					   "Invalid UserTitleChange class name '" + jobClass + "'", e);
		   } catch (InstantiationException e) {
			   throw new ConfigurationException(
					   "Cannot instantiate UserTitleChange of type '"  + jobClass + "'");
		   } catch (IllegalAccessException e) {
			   throw new ConfigurationException(
					   "Cannot instantiate UserTitleChange of type '" + jobClass + "'");
		   }
	   }	   
   }
   /**
     * Handle fields that are not covered by the definition builder
     * @param entry
     * @param inputData
     * @param entryData
     */
    protected void doProfileEntryFillin(Entry entry, InputDataAccessor inputData, Map entryData) {  
    	if (entry instanceof User) {
    		if (inputData.exists(ObjectKeys.FIELD_USER_DISPLAYSTYLE) && !entryData.containsKey(ObjectKeys.FIELD_USER_DISPLAYSTYLE)) {
    			entryData.put(ObjectKeys.FIELD_USER_DISPLAYSTYLE, inputData.getSingleValue(ObjectKeys.FIELD_USER_DISPLAYSTYLE));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_FIRSTNAME) && !entryData.containsKey(ObjectKeys.FIELD_USER_FIRSTNAME)) {
    			entryData.put(ObjectKeys.FIELD_USER_FIRSTNAME, inputData.getSingleValue(ObjectKeys.FIELD_USER_FIRSTNAME));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_LASTNAME) && !entryData.containsKey(ObjectKeys.FIELD_USER_LASTNAME)) {
    			entryData.put(ObjectKeys.FIELD_USER_LASTNAME, inputData.getSingleValue(ObjectKeys.FIELD_USER_LASTNAME));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_MIDDLENAME) && !entryData.containsKey(ObjectKeys.FIELD_USER_MIDDLENAME)) {
    			entryData.put(ObjectKeys.FIELD_USER_MIDDLENAME, inputData.getSingleValue(ObjectKeys.FIELD_USER_MIDDLENAME));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_LOCALE) && !entryData.containsKey(ObjectKeys.FIELD_USER_LOCALE)) {
    			entryData.put(ObjectKeys.FIELD_USER_LOCALE, inputData.getSingleObject(ObjectKeys.FIELD_USER_LOCALE));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_EMAIL) && !entryData.containsKey(ObjectKeys.FIELD_USER_EMAIL)) {
    			entryData.put(ObjectKeys.FIELD_USER_EMAIL, inputData.getSingleValue(ObjectKeys.FIELD_USER_EMAIL));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_TIMEZONE) && !entryData.containsKey(ObjectKeys.FIELD_USER_TIMEZONE)) {
    			entryData.put(ObjectKeys.FIELD_USER_TIMEZONE, inputData.getSingleObject(ObjectKeys.FIELD_USER_TIMEZONE));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_PASSWORD) && !entryData.containsKey(ObjectKeys.FIELD_USER_PASSWORD)) {
    			entryData.put(ObjectKeys.FIELD_USER_PASSWORD, inputData.getSingleValue(ObjectKeys.FIELD_USER_PASSWORD));
    		}
    	} else {
    		//must be a group
        	if (inputData.exists(ObjectKeys.FIELD_GROUP_MEMBERS) && !entryData.containsKey(ObjectKeys.FIELD_GROUP_MEMBERS)) {
    			entryData.put(ObjectKeys.FIELD_GROUP_MEMBERS, inputData.getSingleObject(ObjectKeys.FIELD_GROUP_MEMBERS));
        	}
        	//hack to get member names from input and convert to set - Mostly for user/group load
        	if (inputData.exists(ObjectKeys.INPUT_FIELD_GROUP_MEMBERNAME) && !entryData.containsKey(ObjectKeys.FIELD_GROUP_MEMBERS)) {
        		String[] sNames = inputData.getValues(ObjectKeys.INPUT_FIELD_GROUP_MEMBERNAME);
        		
         		//see if they exist
        		Map params = new HashMap();
        		params.put("plist", sNames);
        		params.put("zoneId", ((Group)entry).getZoneId());
        		List exists;
        		if (inputData.exists(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME))
        			exists = getCoreDao().loadObjects("from com.sitescape.team.domain.Principal where zoneId=:zoneId and foreignName in (:plist)", params);
        		else
           			exists = getCoreDao().loadObjects("from com.sitescape.team.domain.Principal where zoneId=:zoneId and name in (:plist)", params);
        		Set members = new HashSet();
        		for (int x=0;x<exists.size(); ++x) {
				   Principal p = (Principal)exists.get(x);
				   if (p.isActive()) members.add(p);
        		}
	   			entryData.put(ObjectKeys.FIELD_GROUP_MEMBERS, members);
        	}

    	}
    	if (inputData.exists(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME) && !entryData.containsKey(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME)) {
    		entryData.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, inputData.getSingleValue(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME));
    	} 
       	if (inputData.exists(ObjectKeys.FIELD_PRINCIPAL_NAME) && !entryData.containsKey(ObjectKeys.FIELD_PRINCIPAL_NAME)) {
    		entryData.put(ObjectKeys.FIELD_PRINCIPAL_NAME, inputData.getSingleValue(ObjectKeys.FIELD_PRINCIPAL_NAME));
    	}
       	String name = (String)entryData.get(ObjectKeys.FIELD_PRINCIPAL_NAME);
       	String foreignName = (String)entryData.get(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME);
       	if (Validator.isNotNull(name)) {
       		//setting the name - see if new entry and force foreign name to be same
       		if (Validator.isNull(((Principal)entry).getName())) {
       			if (Validator.isNull(foreignName)) entryData.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, name);
       		}
       	}
       	
    }
    //***********************************************************************************************************
    
   	protected SFQuery indexEntries_getQuery(Binder binder) {
   		//$$$return getCoreDao().queryUsers(new FilterControls(), binder.getZoneName());
   		return getProfileDao().queryAllPrincipals(new FilterControls(), binder.getZoneId());
   	}
   	protected boolean indexEntries_validate(Binder binder, Entry entry) {
   		Principal p = (Principal)entry;
   		//don't index job processor
   		if (p.isReserved() && ObjectKeys.JOB_PROCESSOR_INTERNALID.equals(p.getInternalId())) return false;
   		return true;
   	}
   	protected void indexEntries_load(Binder binder, List entries)  {
   		// bulkd load any collections that neeed to be indexed
   		getProfileDao().bulkLoadCollections((List<Principal>)entries);
   	}
	protected Map indexEntries_loadTags(Binder binder, List<Entry> entries) {
		List<EntityIdentifier> uIds = new ArrayList();
		List<EntityIdentifier> gIds = new ArrayList();
		for (Entry e: entries) {
			EntityIdentifier id = e.getEntityIdentifier();
			if (id.getEntityType().equals(EntityIdentifier.EntityType.user))
				uIds.add(id);
			else if (id.getEntityType().equals(EntityIdentifier.EntityType.group))
				gIds.add(id);
		}
		if (uIds.isEmpty()) return getCoreDao().loadAllTagsByEntity(gIds);
		else if (gIds.isEmpty()) return  getCoreDao().loadAllTagsByEntity(uIds);
		Map result = getCoreDao().loadAllTagsByEntity(uIds);
		result.putAll(getCoreDao().loadAllTagsByEntity(gIds));
		return result;
	}

    //***********************************************************************************************************           
    protected  Entry entry_load(Binder parentBinder, Long entryId) {
        return getProfileDao().loadPrincipal(entryId, parentBinder.getZoneId(), false);        
    }
          
    //***********************************************************************************************************
    protected void deleteEntry_delete(Binder parentBinder, Entry entry, Map ctx) {
       	if (entry instanceof User) {
       		User p = (User)entry;
       		//mark deleted, cause their ids are used all over
       		//profileDao will delete all associations and groups
       		p.setDeleted(true);
      		Map updatesCtx = new HashMap();
       		updatesCtx.put(ObjectKeys.FIELD_ENTITY_TITLE, p.getTitle());
       		String newName = NLT.get("profile.deleted.label") + " " + entry.getModification().getDate().toString();
       		p.setName(newName); //mark as deleted
       		p.setTitle(p.getTitle() + " (" + newName + ")");
       		checkUserTitle(p, updatesCtx);
       	}
    	getProfileDao().delete((Principal)entry);   
    }

    //***********************************************************************************************************    
 
    protected org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, Entry entry, List tags) {
    	org.apache.lucene.document.Document indexDoc = super.buildIndexDocumentFromEntry(binder, entry, tags);
    	
		if (entry instanceof User) {
			User user = (User)entry;
			ProfileIndexUtils.addName(indexDoc, user);
			ProfileIndexUtils.addEmail(indexDoc, user);
			ProfileIndexUtils.addZonName(indexDoc, user);
//			ProfileIndexUtils.addMemberOf(indexDoc, user);
		} else {
	        ProfileIndexUtils.addName(indexDoc, (Group)entry);	
		}
        ProfileIndexUtils.addReservedId(indexDoc, (Principal)entry);	
		
       return indexDoc;
    }
    /**
     * Use to synchronize a user with an outside source.
     * Don't index if not changed - could be on a schedule and don't want to
     * reindex all users unnecessarily
     * Files are not handled here
     */
	public void syncEntry(final Principal entry, final InputDataAccessor inputData) {
		final Map ctx = syncEntry_setCtx(entry, null);
		Map entryDataAll = modifyEntry_toEntryData(entry, inputData, null, ctx);
	    final Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
	        
        // The following part requires update database transaction.
        Boolean changed = (Boolean)getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		boolean result1 = syncEntry_fillIn(entry, inputData, entryData, ctx);
	                
        		boolean result2 = syncEntry_postFillIn(entry, inputData, entryData, ctx);
        		if (result1 || result2) return Boolean.TRUE;
        		return Boolean.FALSE;
        	}});
	    if (changed.equals(Boolean.TRUE)) modifyEntry_indexAdd(entry.getParentBinder(), entry, inputData, null, null, ctx);		
		
	}
	protected Map syncEntry_setCtx(Entry entry, Map ctx) {
		return modifyEntry_setCtx(entry, ctx);
	}
	protected boolean syncEntry_fillIn(Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
	        for (Iterator iter=entryData.entrySet().iterator(); iter.hasNext();) {
	        	Map.Entry mEntry = (Map.Entry)iter.next();
	        	//need to generate id for the event so its id can be saved in customAttr
	        	if (entry.getCustomAttribute((String)mEntry.getKey()) == null) {
	        			Object obj = mEntry.getValue();
	        			if (obj instanceof Event)
	        				getCoreDao().save(obj);
	        	}
	        }
	        doProfileEntryFillin(entry, inputData, entryData);
	        boolean changed = EntryBuilder.updateEntry(entry, entryData);
	        //don't want to do this unless a real change
	        if (changed) {
	 	       User user = RequestContextHolder.getRequestContext().getUser();
	 	       entry.setModification(new HistoryStamp(user));
	 	       entry.incrLogVersion();
	 	       processChangeLog(entry, ChangeLog.MODIFYENTRY);

	        }
	        return changed;
		
	}

	protected boolean syncEntry_postFillIn(Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
  		if (entry.isTop() && entry.getParentBinder().isUniqueTitles()) getCoreDao().updateTitle(entry.getParentBinder(), entry, 
  				(String)ctx.get(ObjectKeys.FIELD_ENTITY_NORMALIZED_TITLE), entry.getNormalTitle());		
  		if (entry instanceof User) {
  			//need to schedule a reindex of entries with my title
  			checkUserTitle((User)entry, ctx);
  		}
  		return false;		
	}
	/**
	 * Synchronize a list of entries.  The map key is the entry, value
	 * is an InputDataAccessor of updates.  Only index entries that change
	 */
	public void syncEntries(final Map entries) {
	    
        // The following part requires update database transaction.
        Map changedEntries = (Map)getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		Map changes = new HashMap();
                 for (Iterator i=entries.entrySet().iterator(); i.hasNext();) {
                	 Map.Entry mEntry = (Map.Entry)i.next();
                	 Principal entry = (Principal)mEntry.getKey();
                	 Map ctx = syncEntry_setCtx(entry, null);
                	 InputDataAccessor inputData = (InputDataAccessor)mEntry.getValue();
        	    	
                	 Map entryDataAll = modifyEntry_toEntryData(entry, inputData, null, ctx);
                	 Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
                	 boolean result1 = syncEntry_fillIn(entry, inputData, entryData, ctx);
                	 boolean result2 = syncEntry_postFillIn(entry, inputData, entryData, ctx);
        	    	if (result1 || result2) changes.put(entry, inputData);
       	    } 
        	    return changes;
        	}});
        
	    for (Iterator i=changedEntries.entrySet().iterator(); i.hasNext();) {
	    	Map.Entry mEntry = (Map.Entry)i.next();
	    	Entry entry = (Entry)mEntry.getKey();
	    	InputDataAccessor inputData = (InputDataAccessor)mEntry.getValue();
	    	modifyEntry_indexAdd(entry.getParentBinder(), entry, inputData, null, null, null);	
	    }
		
	}
	public List syncNewEntries(final Binder binder, final Definition definition, final Class clazz, final List inputAccessors) {
	    // The following part requires update database transaction.
		Map newEntries = (Map)getTransactionTemplate().execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	        		Map newEntries = new HashMap();
	           		Map ctx = new HashMap();
	        		for (int i=0; i<inputAccessors.size(); ++i) {
	        			SimpleProfiler.startProfiler("syncNewEntries:dbTransaction");
	        			InputDataAccessor inputData = (InputDataAccessor)inputAccessors.get(i);
	        			Map entryDataAll = addEntry_toEntryData(binder, definition, inputData, null, ctx);
	        			Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
	   	        
	        			Entry entry = addEntry_create(definition, clazz, ctx);
	        			//	need to set entry/binder information before generating file attachments
	        			//	Attachments/Events need binder info for AnyOwner
	        			addEntry_fillIn(binder, entry, inputData, entryData, ctx);
	                
	        			addEntry_preSave(binder, entry, inputData, entryData, ctx);    

	        			addEntry_save(binder, entry, inputData, entryData, ctx);      
	                
	        			addEntry_postSave(binder, entry, inputData, entryData, ctx);
	        			newEntries.put(entry, inputData);
	        			addEntry_startWorkflow(entry, ctx);
	        			ctx.clear();
	        			SimpleProfiler.stopProfiler("syncNewEntries:dbTransaction");
	        		}
	                return newEntries;
	        	}
	        });
	    for (Iterator i=newEntries.entrySet().iterator(); i.hasNext();) {
	    	SimpleProfiler.startProfiler("syncNewEntries:indexAdd");
	    	Map.Entry mEntry = (Map.Entry)i.next();
	    	Entry entry = (Entry)mEntry.getKey();
	    	InputDataAccessor inputData = (InputDataAccessor)mEntry.getValue();
	    	addEntry_indexAdd(entry.getParentBinder(), entry, inputData, null, null);
	    	SimpleProfiler.stopProfiler("syncNewEntries:indexAdd");
	    }
	    return new ArrayList(newEntries.keySet()); 
		
	}

    protected String getEntryPrincipalField() {
    	return EntityIndexUtils.DOCID_FIELD;
    }
	public ChangeLog processChangeLog(DefinableEntity entry, String operation) {
		if (entry instanceof Binder) return processChangeLog((Binder)entry, operation);
		ChangeLog changes = new ChangeLog(entry, operation);
		Element element = ChangeLogUtils.buildLog(changes, entry);
		//add principal fields
		Principal prin = (Principal)entry;
		ChangeLogUtils.addLogAttribute(element, ObjectKeys.XTAG_PRINCIPAL_NAME, ObjectKeys.XTAG_TYPE_STRING, prin.getName());
		ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_PRINCIPAL_FOREIGNNAME, prin.getForeignName());
		ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_PRINCIPAL_DISABLED, String.valueOf(prin.isDisabled()));
		if (!Validator.isNull(prin.getInternalId())) {
			element.addAttribute(ObjectKeys.XTAG_PRINCIPAL_INTERNALID, prin.getInternalId());
		}
		if (prin instanceof User) {
			User user = (User)prin;
			//attributes are available through the definintion builder
			ChangeLogUtils.addLogAttribute(element, ObjectKeys.XTAG_USER_FIRSTNAME, ObjectKeys.XTAG_TYPE_STRING, user.getFirstName());
			ChangeLogUtils.addLogAttribute(element, ObjectKeys.XTAG_USER_MIDDLENAME, ObjectKeys.XTAG_TYPE_STRING, user.getMiddleName());
			ChangeLogUtils.addLogAttribute(element, ObjectKeys.XTAG_USER_LASTNAME, ObjectKeys.XTAG_TYPE_STRING, user.getLastName());
			ChangeLogUtils.addLogAttribute(element, ObjectKeys.XTAG_USER_ZONNAME, ObjectKeys.XTAG_TYPE_STRING, user.getZonName());
			ChangeLogUtils.addLogAttribute(element, ObjectKeys.XTAG_USER_TIMEZONE, ObjectKeys.XTAG_TYPE_STRING, user.getTimeZone().getID());
			ChangeLogUtils.addLogAttribute(element, ObjectKeys.XTAG_USER_EMAIL, ObjectKeys.XTAG_TYPE_STRING, user.getEmailAddress());
			ChangeLogUtils.addLogAttribute(element, ObjectKeys.XTAG_USER_ORGANIZATION, ObjectKeys.XTAG_TYPE_STRING, user.getOrganization());
			ChangeLogUtils.addLogAttribute(element, ObjectKeys.XTAG_USER_PHONE, ObjectKeys.XTAG_TYPE_STRING, user.getPhone());

			ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_USER_DISPLAYSTYLE, user.getDisplayStyle());
			ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_USER_LOCALE, user.getLocale());
			ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_USER_PASSWORD, user.getPassword());
			ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_USER_DIGESTSEED, user.getDigestSeed());
			ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_USER_LOGINDATE, user.getLoginDate());

		} else {
			Group group = (Group)prin;
  			ChangeLogUtils.addLogProperty(element, ObjectKeys.XTAG_GROUP_MEMBERS, CollectionUtil.toCommaIds(group.getMembers()));
		}
		getCoreDao().save(changes);
		return changes;
	}    
}
