package com.sitescape.ef.module.binder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.dom4j.Document;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.security.AccessControlException;

/**
 * <code>EntryProcessor</code> is used by model processors for binders that
 * support AclControlledEntries.
  * 
 * @author Jong Kim
 */
public interface EntryProcessor extends BinderProcessor {
 
 	public Map getBinderEntries(Binder binder, String[] entryTypes, int maxNumEntries) throws AccessControlException;
	public Map getBinderEntries(Binder binder, String[] entryTypes, int maxNumEntries, Document searchFilter) throws AccessControlException;
	public ArrayList getBinderEntries_entriesArray(Hits hits);
	public void indexEntries(Binder binder);	
    
    public Long addEntry(Binder binder, Definition def, Class clazz, InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException;
     public void deleteEntry(Binder binder, Entry entry) throws AccessControlException;
    public Entry getEntry(Binder binder, Long entryId) throws AccessControlException;
    public Long modifyEntry(Binder binder, Entry entry, InputDataAccessor inputData, Map fileItems, Collection deleteAttachments) 
		throws AccessControlException, WriteFilesException;
    public void modifyWorkflowState(Binder binder, Entry entry, Long tokenId, String toState) 
		throws AccessControlException;
	public void reindexEntry(Entry entry); 
  	public void reindexEntries(Collection entries);
    public void moveEntry(Binder binder, Entry entry, Binder destination);


}
