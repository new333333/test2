package com.sitescape.ef.module.binder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.ChangeLog;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.team.lucene.Hits;

/**
 * <code>EntryProcessor</code> is used by model processors for binders that
 * support AclControlledEntries.
  * 
 * @author Jong Kim
 */
public interface EntryProcessor extends BinderProcessor {
 
 	public Map getBinderEntries(Binder binder, String[] entryTypes, Map options) throws AccessControlException;
	public ArrayList getBinderEntries_entriesArray(Hits hits);
    
    public Entry addEntry(Binder binder, Definition def, Class clazz, InputDataAccessor inputData, Map fileItems)
    	throws WriteFilesException;
    public void deleteEntry(Binder binder, Entry entry);
    public Entry getEntry(Binder binder, Long entryId);
    public void modifyEntry(Binder binder, Entry entry, InputDataAccessor inputData, Map fileItems, 
    		Collection deleteAttachments, Map<FileAttachment,String> fileRenamesTo)
    	throws WriteFilesException;
    public void modifyEntry(Binder binder, Entry entry, InputDataAccessor inputData, Map fileItems, 
    		Collection deleteAttachments, Map<FileAttachment,String> fileRenamesTo, Boolean filesFromApplet)
    	throws WriteFilesException;
    public void modifyWorkflowState(Binder binder, Entry entry, Long tokenId, String toState);
    public void setWorkflowResponse(Binder binder, Entry entry, Long tokenId, InputDataAccessor inputData);
	public void reindexEntry(Entry entry); 
  	public void reindexEntries(Collection entries);
  	public void indexEntry(Entry entry);
    public void moveEntry(Binder binder, Entry entry, Binder destination);
	public ChangeLog processChangeLog(DefinableEntity entity, String operation);

}
