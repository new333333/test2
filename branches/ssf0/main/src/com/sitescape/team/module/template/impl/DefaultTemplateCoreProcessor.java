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
package com.sitescape.team.module.template.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.module.binder.impl.AbstractBinderProcessor;
import com.sitescape.team.module.binder.processor.BinderProcessor;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.StatusTicket;
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
    public void deleteBinder(Binder binder, boolean deleteMirroredSource, Map options) {
    	//Delete the template.  The interalId isn't meant
    	// to force the template allways exist
    	binder.setInternalId(null); 
    	super.deleteBinder(binder, deleteMirroredSource, options);
    		
    }
  	//not supported
	public void moveBinder(Binder source, Binder destination, Map options) {
		throw new NotSupportedException("Move", "TemplateBinder");
	
	}
    public void indexBinder(Binder binder, boolean includeEntries, boolean deleteIndex, Collection tags) {
   		//nothing to do    	    	
    }
	//nothing to index
	public Collection indexTree(Binder binder, Collection exclusions) {
		return indexTree(binder, exclusions, StatusTicket.NULL_TICKET);
	}
	public Collection indexTree(Binder binder, Collection exclusions, StatusTicket statusTicket) {
		return new ArrayList();
	}
	//nothing to log
	public ChangeLog processChangeLog(Binder binder, String operation) { 
		return null;
	}
    public void indexFunctionMembership(Binder binder, boolean cascade) {
    	//don't index
    }
    public void indexTeamMembership(Binder binder, boolean cascade) {
    	//don't index
   }
	
}
