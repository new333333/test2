/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.template.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.module.binder.impl.AbstractBinderProcessor;
import org.kablink.teaming.module.binder.processor.BinderProcessor;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.StatusTicket;

/**
 * This processor is used to setup the attributes of the target binder.  It is mostly here so
 * that the binder module and controllers can use existing code .
 * @author Janet
 *
 */
public class DefaultTemplateCoreProcessor extends AbstractBinderProcessor 
	implements BinderProcessor {
	//In this case we are configuring the target binder properties which may be workspace or folder
	//Many attribute are setup already as part of the actual template creation
	public Binder addBinder(final Binder binder, Definition def, Class clazz, 
	    		final InputDataAccessor inputData, Map fileItems, Map options) 
	    	throws AccessControlException, WriteFilesException {
		throw new NotSupportedException("Add", "TemplateBinder");

	}
    
    protected void modifyBinder_indexAdd(Binder binder, 
    		InputDataAccessor inputData, List fileUploadItems,
    		Collection<FileAttachment> filesToIndex, Map ctx) {
    }
    @Override
    public void deleteBinder(Binder binder, boolean deleteMirroredSource, Map options, boolean skipDbLog) {
    	//Delete the template.  The interalId isn't meant
    	// to force the template allways exist
    	binder.setInternalId(null); 
    	super.deleteBinder(binder, deleteMirroredSource, options, skipDbLog);
    		
    }
  	//not supported
	public void moveBinder(Binder source, Binder destination, Map options) {
		throw new NotSupportedException("Move", "TemplateBinder");
	
	}
	public boolean checkMoveBinderQuota(Binder source, Binder destination) {
		return false;
	}
	public boolean checkMoveEntryQuota(Binder source, Binder destination, FolderEntry entry) {
		return false;
	}
    public IndexErrors indexBinder(Binder binder, boolean includeEntries, boolean deleteIndex, Collection tags) {
    	return indexBinder(binder, includeEntries, deleteIndex, tags, false);
    }
    public IndexErrors indexBinder(Binder binder, boolean includeEntries, boolean deleteIndex, Collection tags, boolean skipFileContentIndexing) {
   		//nothing to do    	    	
    	return new IndexErrors();
    }
    public IndexErrors indexBinderIncremental(Binder binder, boolean includeEntries, boolean deleteIndex, Collection tags) {
   		//nothing to do    	    	
    	return new IndexErrors();
    }
	//nothing to index
	public Collection indexTree(Binder binder, Collection exclusions) {
		return indexTree(binder, exclusions, StatusTicket.NULL_TICKET);
	}
	public Collection indexTree(Binder binder, Collection exclusions, StatusTicket statusTicket) {
		return new ArrayList();
	}
	public Collection indexTree(Binder binder, Collection exclusions, StatusTicket statusTicket, IndexErrors errors) {
		return new ArrayList();
	}
	//nothing to log
	public ChangeLog processChangeLog(Binder binder, String operation) { 
		return null;
	}
    public void indexFunctionMembership(Binder binder, boolean cascade, Boolean runInBackground, boolean indexEntries) {
    	//don't index
    }
    public void indexTeamMembership(Binder binder, boolean cascade) {
    	//don't index
   }

    public Collection validateBinderQuotasTree(Binder binder, StatusTicket statusTicket, 
    		List<Long> errors) {
    	return new ArrayList();
    }
    
    public void setFileAgingDates(Binder binder) {
    	//Nothing to be done
    }
    
    public boolean isFolderEmpty(final Binder binder) {
    	return true;
    }

}
