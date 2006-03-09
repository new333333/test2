package com.sitescape.ef.module.binder;

import java.util.Collection;
import java.util.Map;

import org.dom4j.Document;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.WorkflowControlledEntry;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.security.AccessControlException;

/**
 * <code>EntryProcessor</code> is used by model processors for binders that
 * support AclControlledEntries.
  * 
 * @author Jong Kim
 */
public interface EntryProcessor {
    public static final String PROCESSOR_KEY = "processorKey_binderCoreProcessor";

    /**
     * This key is used to uniquely identify a type of processor (ie, a 
     * concrete class implementing this interface).
     */
 	public static int NEXT_ENTRY=1;
	public static int PREVIOUS_ENTRY=2;
    public static int CURRENT_ENTRY=3;


    public Long addEntry(Binder binder, Definition def, Class clazz, InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException;
    public void addEntry_accessControl(Binder binder) throws AccessControlException;
    public void deleteEntry(Binder binder, Long entryId) throws AccessControlException;
    public void deleteEntry_accessControl(Binder binder, WorkflowControlledEntry entry) throws AccessControlException;
    public WorkflowControlledEntry getEntry(Binder binder, Long entryId, int type) throws AccessControlException;
    public Long modifyEntry(Binder binder, Long entryId, InputDataAccessor inputData, Map fileItems) 
		throws AccessControlException, WriteFilesException;
    public void modifyEntry_accessControl(Binder binder, WorkflowControlledEntry entry) throws AccessControlException;
    public void modifyWorkflowState(Binder binder, Long entryId, Long tokenId, String toState) 
		throws AccessControlException;
	public Map getBinderEntries(Binder binder, String[] entryTypes, int maxNumEntries) throws AccessControlException;
	public Map getBinderEntries(Binder binder, String[] entryTypes, int maxNumEntries, Document searchFilter) throws AccessControlException;
	public void indexBinder(Binder binder);
	public void indexEntry(WorkflowControlledEntry entry); 
  	public void indexEntry(Collection entries);
	
}
