package com.sitescape.ef.module.profile.impl;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.lang.Long;
import java.util.HashMap;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.SFQuery;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.IndexSynchronizationManager;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.web.util.FilterHelper;
import com.sitescape.ef.module.profile.ProfileCoreProcessor;
import com.sitescape.ef.module.binder.impl.AbstractEntryProcessor;
import com.sitescape.ef.module.profile.index.ProfileIndexUtils;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.module.shared.EntityIndexUtils;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.InternalException;
import com.sitescape.util.Validator;
/**
 *
 * @author Jong Kim
 */
public class DefaultProfileCoreProcessor extends AbstractEntryProcessor
	implements ProfileCoreProcessor {
    
    //***********************************************************************************************************	
            
    protected void addEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData) {  
        doFillin(entry, inputData, entryData);
        super.addEntry_fillIn(binder, entry, inputData, entryData);
        ((Principal)entry).setZoneName(binder.getZoneName());
     }
       
    protected void modifyEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData) {  
    	//see if we have updates to fields not covered by definition build
    	doFillin(entry, inputData, entryData);
    	super.modifyEntry_fillIn(binder, entry, inputData, entryData);
    }
    /**
     * Handle fields that are not covered by the definition builder
     * @param entry
     * @param inputData
     * @param entryData
     */
    protected void doFillin(Entry entry, InputDataAccessor inputData, Map entryData) {  
    	if (inputData.exists(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME) && !entryData.containsKey(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME)) {
    		entryData.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, inputData.getSingleValue(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME));
    	} 
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
    		entryData.put(ObjectKeys.FIELD_USER_LOCALE, inputData.getSingleValue(ObjectKeys.FIELD_USER_LOCALE));
    	}
       	if (inputData.exists(ObjectKeys.FIELD_USER_EMAIL) && !entryData.containsKey(ObjectKeys.FIELD_USER_EMAIL)) {
    		entryData.put(ObjectKeys.FIELD_USER_EMAIL, inputData.getSingleValue(ObjectKeys.FIELD_USER_EMAIL));
    	}
       	if (inputData.exists(ObjectKeys.FIELD_USER_TIMEZONE) && !entryData.containsKey(ObjectKeys.FIELD_USER_TIMEZONE)) {
    		entryData.put(ObjectKeys.FIELD_USER_TIMEZONE, inputData.getSingleValue(ObjectKeys.FIELD_USER_TIMEZONE));
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
   		return getProfileDao().queryAllPrincipals(new FilterControls(), binder.getZoneName());
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
    protected org.dom4j.Document getBinderEntries_getSearchDocument(Binder binder, String [] entryTypes, org.dom4j.Document searchFilter) {
  
    	if (searchFilter == null) {
    		//Build a null search filter
    		searchFilter = DocumentHelper.createDocument();
    		Element rootElement = searchFilter.addElement(FilterHelper.FilterRootName);
        	rootElement.addElement(FilterHelper.FilterTerms);
    	}
    	org.dom4j.Document qTree = FilterHelper.convertSearchFilterToSearchBoolean(searchFilter);
    	Element rootElement = qTree.getRootElement();
    	Element boolElement = rootElement.element(QueryBuilder.AND_ELEMENT);
    	if (boolElement == null) return qTree;
    	boolElement.addElement(QueryBuilder.USERACL_ELEMENT);
    	Element field,child;
    	//Look only for entryType=entry
    	if (entryTypes.length == 1) {
    		field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.ENTRY_TYPE_FIELD);
    		child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    		child.setText(entryTypes[0]);
    	} else {
    		Element orField = boolElement.addElement(QueryBuilder.OR_ELEMENT);
    		for (int i=0; i<entryTypes.length; ++i) {
    			field = orField.addElement(QueryBuilder.FIELD_ELEMENT);
    			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.ENTRY_TYPE_FIELD);
    			child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    			child.setText(entryTypes[i]);
    		}

    	}
      	
    	//Look only for binderId=binder and type = entry
       	field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.BINDER_ID_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(binder.getId().toString());
       	
    	field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(BasicIndexUtils.DOC_TYPE_ENTRY);
   	
    	return qTree;
 
    }


    //***********************************************************************************************************
           
    protected  Entry entry_load(Binder parentBinder, Long entryId) {
        return getProfileDao().loadPrincipal(entryId, parentBinder.getZoneName());        
    }
          
    //***********************************************************************************************************
    //Overload entire delete entry.  Only want to disable users
    protected Object deleteEntry_preDelete(Binder parentBinder, Entry entry) {
    	if (entry instanceof User) return null;
    	return super.deleteEntry_preDelete(parentBinder, entry);
    }
        
    protected Object deleteEntry_workflow(Binder parentBinder, Entry entry, Object ctx) {
       	if (entry instanceof User) return ctx;
       	return super.deleteEntry_workflow(parentBinder, entry, ctx);
    }
    
    protected Object deleteEntry_delete(Binder parentBinder, Entry entry, Object ctx) {
       	if (entry instanceof User) {
       		User p = (User)entry;
       		//we just disable principals, cause their ids are used all over
       		p.setDisabled(true);
       		return ctx;
       	}
       	return super.deleteEntry_delete(parentBinder, entry, ctx);
    }

   protected Object deleteEntry_processFiles(Binder parentBinder, Entry entry, Object ctx) {
     	if (entry instanceof User) return ctx;
     	return super.deleteEntry_processFiles(parentBinder, entry, ctx);
    }
    

    protected Object deleteEntry_postDelete(Binder parentBinder, Entry entry, Object ctx) {
      	//TODO: what about disabled users??
    	if (entry instanceof User) return ctx;
      	return super.deleteEntry_postDelete(parentBinder, entry, ctx);
   }

    protected Object deleteEntry_indexDel(Entry entry, Object ctx) {
        // Delete the document that's currently in the index.
    	// Since all matches will be deleted, this will also delete the attachments
      	if (entry instanceof User) {
      		IndexSynchronizationManager.deleteDocument(entry.getIndexDocumentUid());
      		return ctx;
      	} 
      	return super.deleteEntry_indexDel(entry, ctx);
   }
    //***********************************************************************************************************
    
    public void deleteBinder(Binder binder) {
    	throw new InternalException("Cannot delete profile binder");
    }
    //***********************************************************************************************************    
    public void moveBinder(Binder source, Binder destination) {
    	throw new InternalException("Cannot move profile binder");
    }

    protected org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, Entry entry, List tags) {
    	org.apache.lucene.document.Document indexDoc = super.buildIndexDocumentFromEntry(binder, entry, tags);
    	
		if (entry instanceof User) {
			User user = (User)entry;
			ProfileIndexUtils.addName(indexDoc, user);
			ProfileIndexUtils.addEmail(indexDoc, user);
			ProfileIndexUtils.addZonName(indexDoc, user);
			ProfileIndexUtils.addMemberOf(indexDoc, user);
		} else {
	        ProfileIndexUtils.addName(indexDoc, (Group)entry);	
		}
		
       return indexDoc;
    }
    /**
     * Use to synchronize a user with an outside source.
     * Don't index if not changed - could be on a schedule and don't want to
     * reindex all uses unnecessarily
     */
	public void syncEntry(final Principal entry, final InputDataAccessor inputData) {
	    Map entryDataAll = modifyEntry_toEntryData(entry, inputData, null);
	    final Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
	        
        // The following part requires update database transaction.
        Boolean changed = (Boolean)getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		boolean result1 = syncEntry_fillIn(entry, inputData, entryData);
	                
        		boolean result2 = syncEntry_postFillIn(entry, inputData, entryData);
        		if (result1 || result2) return Boolean.TRUE;
        		return Boolean.FALSE;
        	}});
	    if (changed.booleanValue() == true) modifyEntry_indexAdd(entry.getParentBinder(), entry, inputData, null, null);		
		
	}
	public boolean syncEntry_fillIn(Entry entry, InputDataAccessor inputData, Map entryData) {
	        for (Iterator iter=entryData.entrySet().iterator(); iter.hasNext();) {
	        	Map.Entry mEntry = (Map.Entry)iter.next();
	        	//need to generate id for the event so its id can be saved in customAttr
	        	if (entry.getCustomAttribute((String)mEntry.getKey()) == null) {
	        			Object obj = mEntry.getValue();
	        			if (obj instanceof Event)
	        				getCoreDao().save(obj);
	        	}
	        }
	        doFillin(entry, inputData, entryData);
	        boolean changed = EntryBuilder.updateEntry(entry, entryData);
	        if (changed) {
	 	       User user = RequestContextHolder.getRequestContext().getUser();
	 	       entry.setModification(new HistoryStamp(user));
	        }
	        return changed;
		
	}

	public boolean syncEntry_postFillIn(Entry entry, InputDataAccessor inputData, Map entryData) {
		return false;		
	}
	/**
	 * Synchronize a list of entries.  The map key is the entry, value
	 * is an InputDataAccessor of updates
	 */
	public void syncEntries(final Map entries) {
	    
        // The following part requires update database transaction.
        Map changedEntries = (Map)getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		Map changes = new HashMap();
        	    for (Iterator i=entries.entrySet().iterator(); i.hasNext();) {
        	    	Map.Entry mEntry = (Map.Entry)i.next();
        	    	Entry entry = (Entry)mEntry.getKey();
        	    	InputDataAccessor inputData = (InputDataAccessor)mEntry.getValue();
        	    	
        	    	Map entryDataAll = modifyEntry_toEntryData(entry, inputData, null);
        	    	Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
        	    	boolean result1 = syncEntry_fillIn(entry, inputData, entryData);
	                
        	    	boolean result2 = syncEntry_postFillIn(entry, inputData, entryData);
        	    	if (result1 || result2) changes.put(entry, inputData);
        	    } 
        	    return changes;
        	}});
        
	    for (Iterator i=changedEntries.entrySet().iterator(); i.hasNext();) {
	    	Map.Entry mEntry = (Map.Entry)i.next();
	    	Entry entry = (Entry)mEntry.getKey();
	    	InputDataAccessor inputData = (InputDataAccessor)mEntry.getValue();
	    	modifyEntry_indexAdd(entry.getParentBinder(), entry, inputData, null, null);	
	    }
		
	}
	public List syncNewEntries(final Binder binder, final Definition definition, final Class clazz, final List inputAccessors) {
	        
	    // The following part requires update database transaction.
		Map newEntries = (Map)getTransactionTemplate().execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	        		Map newEntries = new HashMap();
	        		for (int i=0; i<inputAccessors.size(); ++i) {
	        			InputDataAccessor inputData = (InputDataAccessor)inputAccessors.get(i);
	        			Map entryDataAll = addEntry_toEntryData(binder, definition, inputData, null);
	        			Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
	   	        
	        			Entry entry = addEntry_create(definition, clazz);
	        			//	need to set entry/binder information before generating file attachments
	        			//	Attachments/Events need binder info for AnyOwner
	        			addEntry_fillIn(binder, entry, inputData, entryData);
	                
	        			addEntry_preSave(binder, entry, inputData, entryData);      

	        			addEntry_save(binder, entry, inputData, entryData);      
	                
	        			addEntry_postSave(binder, entry, inputData, entryData);
	        			newEntries.put(entry, inputData);
	        			addEntry_startWorkflow(entry);
	        		}
	                return newEntries;
	        	}
	        });
	    for (Iterator i=newEntries.entrySet().iterator(); i.hasNext();) {
	    	Map.Entry mEntry = (Map.Entry)i.next();
	    	Entry entry = (Entry)mEntry.getKey();
	    	InputDataAccessor inputData = (InputDataAccessor)mEntry.getValue();
	    	addEntry_indexAdd(entry.getParentBinder(), entry, inputData, null);	
	    }
	    return new ArrayList(newEntries.keySet()); 
		
	}

    protected String getEntryPrincipalField() {
    	return EntityIndexUtils.DOCID_FIELD;
    }
}
