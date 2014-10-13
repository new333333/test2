/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.module.binder.processor;

import java.util.Collection;
import java.util.Map;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.security.AccessControlException;

/**
 * <code>EntryProcessor</code> is used by model processors for binders that
 * support AclControlledEntries.
  * 
 * @author Jong Kim
 */
@SuppressWarnings("unchecked")
public interface EntryProcessor extends BinderProcessor {
 	public Map getBinderEntries(Binder binder, String[] entryTypes, Map options) throws AccessControlException;
    
    public Entry addEntry(Binder binder, Definition def, Class clazz, InputDataAccessor inputData, Map fileItems, Map options)
    	throws WriteFilesException, WriteEntryDataException;
    public void addEntryWorkflow(Binder binder, Entry entry, Definition definition, Map options);  
    public Entry copyEntry(Binder binder, Entry entry, Binder destination, String[] toFileNames, Map options) throws WriteFilesException;
    public void copyEntries(Binder source, Binder binder, Map options);
    public void disableEntry(Principal entry, boolean disable);
    public void deleteEntry(Binder binder, Entry entry, boolean deleteMirroredSource, Map options) throws WriteFilesException;
    public void deleteEntryWorkflow(Binder binder, Entry entry, Definition definition);
    public void modifyEntry(Binder binder, Entry entry, InputDataAccessor inputData, Map fileItems, 
    		Collection deleteAttachments, Map<FileAttachment,String> fileRenamesTo, Map options)
    	throws WriteFilesException, WriteEntryDataException;
    public void modifyWorkflowState(Binder binder, Entry entry, Long tokenId, String toState);
    public void setWorkflowResponse(Binder binder, Entry entry, Long tokenId, InputDataAccessor inputData, Boolean canModifyEntry);
  	public IndexErrors indexEntries(Collection entries);
  	public IndexErrors indexEntries(Collection entries, boolean skipFileContentIndexing);
  	public IndexErrors indexEntry(Entry entry);
	public IndexErrors indexEntry(Entry entry, boolean skipFileContentIndexing);
  	public org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, Entry entry, Collection tags);
    public void moveEntry(Binder binder, Entry entry, Binder destination, String[] toFileNames, Map options);
	public ChangeLog processChangeLog(DefinableEntity entity, String operation);
	public ChangeLog processChangeLog(DefinableEntity entity, String operation, boolean skipDbLog, boolean skipNotifyStatus);
	
    public org.apache.lucene.document.Document buildIndexDocumentFromEntryFile
	(Binder binder, Entry entry, FileAttachment fa, Collection tags, boolean skipFileContentIndexing);
 }
