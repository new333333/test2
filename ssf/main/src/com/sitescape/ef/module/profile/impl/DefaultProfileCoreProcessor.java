package com.sitescape.ef.module.profile.impl;

import java.util.Map;
import java.lang.Long;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.SFQuery;
import com.sitescape.ef.domain.AclControlledEntry;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.module.profile.ProfileCoreProcessor;
import com.sitescape.ef.module.binder.impl.AbstractEntryProcessor;
import com.sitescape.ef.module.profile.index.IndexUtils;
import com.sitescape.ef.module.shared.EntryIndexUtils;
/**
 *
 * @author Jong Kim
 */
public class DefaultProfileCoreProcessor extends AbstractEntryProcessor
	implements ProfileCoreProcessor {
    
    //***********************************************************************************************************	
            
    protected void addEntry_fillIn(Binder binder, AclControlledEntry entry, Map inputData, Map entryData) {  
    	super.addEntry_fillIn(binder, entry, inputData, entryData);
        ((Principal)entry).setZoneName(binder.getZoneName());
    }
       
 
    //***********************************************************************************************************
    
   	protected SFQuery indexBinder_getQuery(Binder binder) {
   		return getCoreDao().queryUsers(new FilterControls(), binder.getZoneName());
   	}

    //***********************************************************************************************************
    protected org.dom4j.Document getBinderEntries_getSearchDocument(Binder binder, String [] entryTypes, org.dom4j.Document qTree) {
  
    	if (qTree == null) {
    		qTree = DocumentHelper.createDocument();
        	qTree.addElement(QueryBuilder.QUERY_ELEMENT);
        	qTree.getRootElement().addElement(QueryBuilder.AND_ELEMENT);
    	}
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
      	
    	//Look only for binderId=binder
    	field = boolElement.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntryIndexUtils.BINDER_ID_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(binder.getId().toString());
    	return qTree;
 
    }



    //***********************************************************************************************************
           
    protected  AclControlledEntry entry_load(Binder parentBinder, Long entryId) {
        return getCoreDao().loadPrincipal(entryId, parentBinder.getZoneName());        
    }
         
    protected  AclControlledEntry entry_loadFull(Binder parentBinder, Long entryId) {
        return getCoreDao().loadFullPrincipal(entryId, parentBinder.getZoneName());        
    }
 
    protected void deleteEntry_delete(Binder parentBinder, AclControlledEntry entry) {
    	Principal p = (Principal)entry;
    	//we just disable principals, cause their ids are used all over
    	p.setDisabled(true);
    }
    protected org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, AclControlledEntry entry) {
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
       
}
