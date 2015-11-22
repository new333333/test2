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
import java.util.List;
import java.util.Map;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.StatusTicket;

/**
 * <code>EntryProcessor</code> is used by model processors for binders that
 * support AclControlledEntries.
  * 
 * @author Jong Kim
 */
@SuppressWarnings("unchecked")
public interface BinderProcessor {
    public static final String PROCESSOR_KEY = "processorKey_binderCoreProcessor";

	public Binder addBinder(Binder binder, Definition def, Class clazz, InputDataAccessor inputData, Map fileItems, Map options) 
    	throws AccessControlException, WriteFilesException, WriteEntryDataException;
    public Binder copyBinder(Binder source, Binder destination, Map options);
    public void deleteBinder(Binder binder, boolean deleteMirroredSource, Map options, boolean skipDbLog) throws AccessControlException;
  	//return search results
  	public Map getBinders(Binder binder, Map options);
  	public Map getBinders(Binder binder, List binderIds, Map options);
    public void indexFunctionMembership(Binder binder, boolean cascade, Boolean runInBackground, boolean indexEntries);
    public void indexFunctionMembership(Binder binder, boolean cascade, Boolean runInBackground, boolean indexEntries, boolean skipFileContentIndexing, Boolean dealingWithExternalAcl);
    public void indexTeamMembership(Binder binder, boolean cascade);
    public void indexOwner(Collection<Binder>binders, Long ownerId);
	public IndexErrors indexBinder(Binder binder, boolean includeEntries);	
	public IndexErrors indexBinder(Binder binder, boolean includeEntries, boolean deleteIndex, Collection tags);
	public IndexErrors indexBinder(Binder binder, boolean includeEntries, boolean deleteIndex, Collection tags, boolean skipFileContentIndexing);
	public IndexErrors indexBinderIncremental(Binder binder, boolean includeEntries);
	public IndexErrors indexBinderIncremental(Binder binder, boolean includeEntries, boolean skipFileContentIndexing);
	public Collection indexTree(Binder binder, Collection exclusions);
    public Collection indexTree(Binder binder, Collection exclusions, StatusTicket statusTicket);
    public Collection indexTree(Binder binder, Collection exclusions, StatusTicket statusTicket, IndexErrors errors);
    public Collection indexTree(Binder binder, Collection exclusions, StatusTicket statusTicket, IndexErrors errors, boolean skipFileContentIndexing);
    public Collection validateBinderQuotasTree(Binder binder, StatusTicket statusTicket, List<Long> errors);
    public void setFileAgingDates(Binder binder);
    public boolean isFolderEmpty(final Binder binder);
      
 	public void modifyBinder(Binder binder, InputDataAccessor inputData, Map fileItems, Collection deleteAttachments, Map options) 
		throws AccessControlException, WriteFilesException, WriteEntryDataException;
    public void moveBinder(Binder source, Binder destination, Map options);
    public boolean checkMoveBinderQuota(Binder source, Binder destination);
    public boolean checkMoveEntryQuota(Binder source, Binder destination, FolderEntry entry);
    /**
     * Fix up a binder after its parent have been moved
     * Needs to be public, since calls cross binders and may be
     * implemented by different processors.
     * @param binder
     */
    public void moveBinderFixup(Binder binder);
	public ChangeLog processChangeLog(Binder binder, String operation);
	public ChangeLog processChangeLog(Binder binder, String operation, boolean skipDbLog);
	
    public void updateParentModTime(final Binder parentBinder, Map options);
    public void updateParentModTime(final Binder parentBinder, Map options, boolean reindex);
}
