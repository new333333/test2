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

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.SFQuery;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.WorkflowControlledEntry;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.web.util.FilterHelper;
import com.sitescape.ef.module.profile.ProfileCoreProcessor;
import com.sitescape.ef.module.binder.impl.AbstractEntryProcessor;
import com.sitescape.ef.module.profile.index.IndexUtils;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.module.shared.InputDataAccessor;
/**
 *
 * @author Jong Kim
 */
public class DefaultProfileCoreProcessor extends AbstractEntryProcessor
	implements ProfileCoreProcessor {
    
    //***********************************************************************************************************	
            
    protected void addEntry_fillIn(Binder binder, WorkflowControlledEntry entry, InputDataAccessor inputData, Map entryData) {  
        doFillin(entry, inputData, entryData);
        super.addEntry_fillIn(binder, entry, inputData, entryData);
        ((Principal)entry).setZoneName(binder.getZoneName());
     }
       
    protected void modifyEntry_fillIn(Binder binder, WorkflowControlledEntry entry, InputDataAccessor inputData, Map entryData) {  
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
    protected void doFillin( WorkflowControlledEntry entry, InputDataAccessor inputData, Map entryData) {  
    	if (inputData.exists("foreignName") && !entryData.containsKey("foreignName")) {
    		entryData.put("foreignName", inputData.getSingleValue("foreignName"));
    	}
    	if (inputData.exists("displayStyle") && !entryData.containsKey("displayStyle")) {
    		entryData.put("displayStyle", inputData.getSingleValue("displayStyle"));
    	}
    }
    //***********************************************************************************************************
    
   	protected SFQuery indexBinder_getQuery(Binder binder) {
   		//$$$return getCoreDao().queryUsers(new FilterControls(), binder.getZoneName());
   		return getCoreDao().queryAllPrincipals(new FilterControls(), binder.getZoneName());
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
    		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntryIndexUtils.ENTRY_TYPE_FIELD);
    		child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    		child.setText(entryTypes[0]);
    	} else {
    		Element orField = boolElement.addElement(QueryBuilder.OR_ELEMENT);
    		for (int i=0; i<entryTypes.length; ++i) {
    			field = orField.addElement(QueryBuilder.FIELD_ELEMENT);
    			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntryIndexUtils.ENTRY_TYPE_FIELD);
    			child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    			child.setText(entryTypes[i]);
    		}

    	}
      	
    	//Look only for binderId=binder and type = entry
       	field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntryIndexUtils.BINDER_ID_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(binder.getId().toString());
       	
    	field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(BasicIndexUtils.DOC_TYPE_ENTRY);
   	
    	return qTree;
 
    }



    //***********************************************************************************************************
           
    protected  WorkflowControlledEntry entry_load(Binder parentBinder, Long entryId) {
        return getCoreDao().loadPrincipal(entryId, parentBinder.getZoneName());        
    }
         
    protected  WorkflowControlledEntry entry_loadFull(Binder parentBinder, Long entryId) {
        return getCoreDao().loadFullPrincipal(entryId, parentBinder.getZoneName());        
    }
 
    protected void deleteEntry_delete(Binder parentBinder, WorkflowControlledEntry entry) {
    	Principal p = (Principal)entry;
    	//we just disable principals, cause their ids are used all over
    	p.setDisabled(true);
    }
    protected org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, WorkflowControlledEntry entry) {
    	org.apache.lucene.document.Document indexDoc = super.buildIndexDocumentFromEntry(binder, entry);
    	
		// Add doc type
		if (entry instanceof User) {
			User user = (User)entry;
		       // Add doc type
			IndexUtils.addName(indexDoc, user);
	        IndexUtils.addFirstName(indexDoc, user);
	        IndexUtils.addMiddleName(indexDoc, user);
	        IndexUtils.addLastName(indexDoc, user);
	        IndexUtils.addEmailAddress(indexDoc, user);
		} else {
	        IndexUtils.addName(indexDoc, (Group)entry);
			
		}
           
        
       return indexDoc;
    }
    /**
     * Use to synchronize a user with an outside source.
     * Don't index if not changed
     */
	public void syncEntry(final Principal entry, final InputDataAccessor inputData) {
	    Map entryDataAll = modifyEntry_toEntryData(entry, inputData, null);
	    final Map entryData = (Map) entryDataAll.get("entryData");
	        
        // The following part requires update database transaction.
        Boolean changed = (Boolean)getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		boolean result1 = syncEntry_fillIn(entry, inputData, entryData);
	                
        		boolean result2 = syncEntry_postFillIn(entry, inputData, entryData);
        		if (result1 || result2) return Boolean.TRUE;
        		return Boolean.FALSE;
        	}});
	    if (changed.booleanValue() == true) modifyEntry_indexAdd(entry.getParentBinder(), entry, inputData, null);		
		
	}
	public boolean syncEntry_fillIn(WorkflowControlledEntry entry, InputDataAccessor inputData, Map entryData) {
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

	public boolean syncEntry_postFillIn(WorkflowControlledEntry entry, InputDataAccessor inputData, Map entryData) {
		return false;		
	}
	/**
	 * Synchronize a list of entries.  The map key is entry Id, value
	 * is a map of updates
	 */
	public void syncEntries(final Map entries) {
	        
        // The following part requires update database transaction.
        Map changedEntries = (Map)getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		Map changes = new HashMap();
        	    for (Iterator i=entries.entrySet().iterator(); i.hasNext();) {
        	    	Map.Entry mEntry = (Map.Entry)i.next();
        	    	WorkflowControlledEntry entry = (WorkflowControlledEntry)mEntry.getKey();
        	    	InputDataAccessor inputData = (InputDataAccessor)mEntry.getValue();
        	    	
        	    	Map entryDataAll = modifyEntry_toEntryData(entry, inputData, null);
        	    	Map entryData = (Map) entryDataAll.get("entryData");
        	    	boolean result1 = syncEntry_fillIn(entry, inputData, entryData);
	                
        	    	boolean result2 = syncEntry_postFillIn(entry, inputData, entryData);
        	    	if (result1 || result2) changes.put(entry, inputData);
        	    } 
        	    return changes;
        	}});
        
	    for (Iterator i=changedEntries.entrySet().iterator(); i.hasNext();) {
	    	Map.Entry mEntry = (Map.Entry)i.next();
	    	WorkflowControlledEntry entry = (WorkflowControlledEntry)mEntry.getKey();
	    	InputDataAccessor inputData = (InputDataAccessor)mEntry.getValue();
	    	modifyEntry_indexAdd(entry.getParentBinder(), entry, inputData, null);	
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
	        			Map entryData = (Map) entryDataAll.get("entryData");
	   	        
	        			WorkflowControlledEntry entry = addEntry_create(clazz);
	        			entry.setEntryDef(definition);
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
	    	WorkflowControlledEntry entry = (WorkflowControlledEntry)mEntry.getKey();
	    	InputDataAccessor inputData = (InputDataAccessor)mEntry.getValue();
	    	addEntry_indexAdd(entry.getParentBinder(), entry, inputData, null);	
	    }
	    return new ArrayList(newEntries.keySet()); 
		
	}

    protected String getEntryPrincipalField() {
    	return EntryIndexUtils.DOCID_FIELD;
    }
}
