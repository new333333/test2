package com.sitescape.ef.module.binder;

import java.util.Map;

import org.dom4j.Document;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.AclControlledEntry;
import com.sitescape.ef.module.shared.WriteFilesException;
import com.sitescape.ef.security.AccessControlException;

/**
 * <code>EntryProcessor</code> is used by model processors for binders that
 * support AclControlledEntries.
  * 
 * @author Jong Kim
 */
public interface EntryProcessor {

    /**
     * This key is used to uniquely identify a type of processor (ie, a 
     * concrete class implementing this interface).
     */
 	public static int NEXT_ENTRY=1;
	public static int PREVIOUS_ENTRY=2;
    public static int CURRENT_ENTRY=3;


    public Long addEntry(Binder binder, Definition def, Class clazz, Map inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException;
    public void deleteEntry(Binder binder, Long entryId) throws AccessControlException;
    public AclControlledEntry getEntry(Binder binder, Long entryId, int type) throws AccessControlException;
    public Long modifyEntry(Binder binder, Long entryId, Map inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
    public Long modifyEntryData(Binder binder, Long entryId, Map entryData) 
		throws AccessControlException;
    public void modifyWorkflowState(Binder binder, Long entryId, Long tokenId, String toState) 
		throws AccessControlException;
	public Map getBinderEntries(Binder binder, String[] entryTypes, int maxNumEntries) throws AccessControlException;
	public Map getBinderEntries(Binder binder, String[] entryTypes, int maxNumEntries, Document qTree) throws AccessControlException;
	public void indexBinder(Binder binder);
	   	  
}
