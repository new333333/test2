/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.security.AccessControlException;


/**
 * <code>EntryProcessor</code> is used by model processors for binders that
 * support AclControlledEntries.
  * 
 * @author Jong Kim
 */
public interface EntryProcessor extends BinderProcessor {
 
 	public Map getBinderEntries(Binder binder, String[] entryTypes, Map options) throws AccessControlException;
    
    public Entry addEntry(Binder binder, Definition def, Class clazz, InputDataAccessor inputData, Map fileItems, Map options)
    	throws WriteFilesException;
    public void addEntryWorkflow(Binder binder, Entry entry, Definition definition, Map options);  
    public Entry copyEntry(Binder binder, Entry entry, Binder destination, Map options);
    public void copyEntries(Binder source, Binder binder, Map options);
    public void deleteEntry(Binder binder, Entry entry, boolean deleteMirroredSource, Map options);
    public void deleteEntryWorkflow(Binder binder, Entry entry, Definition definition);
    public void modifyEntry(Binder binder, Entry entry, InputDataAccessor inputData, Map fileItems, 
    		Collection deleteAttachments, Map<FileAttachment,String> fileRenamesTo, Map options)
    	throws WriteFilesException;
    public void modifyWorkflowState(Binder binder, Entry entry, Long tokenId, String toState);
    public void setWorkflowResponse(Binder binder, Entry entry, Long tokenId, InputDataAccessor inputData);
  	public void indexEntries(Collection entries);
  	public void indexEntry(Entry entry);
  	public org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, Entry entry, Collection tags);
    public void moveEntry(Binder binder, Entry entry, Binder destination, Map options);
	public ChangeLog processChangeLog(DefinableEntity entity, String operation);
 }
