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
package com.sitescape.team.module.binder;

import java.util.Collection;
import java.util.Map;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.module.file.FilesErrors;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;

/**
 * <code>EntryProcessor</code> is used by model processors for binders that
 * support AclControlledEntries.
  * 
 * @author Jong Kim
 */
public interface EntryProcessor extends BinderProcessor {
 
 	public Map getBinderEntries(Binder binder, String[] entryTypes, Map options) throws AccessControlException;
    
    public Entry addEntry(Binder binder, Definition def, Class clazz, InputDataAccessor inputData, Map fileItems)
    	throws WriteFilesException;
    public Entry addEntry(Binder binder, Definition def, Class clazz, InputDataAccessor inputData, Map fileItems, Boolean filesFromApplet)
		throws WriteFilesException;
    public void addEntryWorkflow(Binder binder, Entry entry, Definition definition);   public void deleteEntry(Binder binder, Entry entry, boolean deleteMirroredSource);
    public void deleteEntryWorkflow(Binder binder, Entry entry, Definition definition);
    public Entry getEntry(Binder binder, Long entryId);
    public void modifyEntry(Binder binder, Entry entry, InputDataAccessor inputData, Map fileItems, 
    		Collection deleteAttachments, Map<FileAttachment,String> fileRenamesTo)
    	throws WriteFilesException;
    public void modifyEntry(Binder binder, Entry entry, InputDataAccessor inputData, Map fileItems, 
    		Collection deleteAttachments, Map<FileAttachment,String> fileRenamesTo, Boolean filesFromApplet)
    	throws WriteFilesException;
    public void modifyWorkflowState(Binder binder, Entry entry, Long tokenId, String toState);
    public void setWorkflowResponse(Binder binder, Entry entry, Long tokenId, InputDataAccessor inputData);
  	public void indexEntries(Collection entries);
  	public void indexEntry(Entry entry);
    public void moveEntry(Binder binder, Entry entry, Binder destination);
	public ChangeLog processChangeLog(DefinableEntity entity, String operation);

}
