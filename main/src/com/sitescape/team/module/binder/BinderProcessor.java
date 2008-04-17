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
package com.sitescape.team.module.binder;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.StatusTicket;

/**
 * <code>EntryProcessor</code> is used by model processors for binders that
 * support AclControlledEntries.
  * 
 * @author Jong Kim
 */
public interface BinderProcessor {
    public static final String PROCESSOR_KEY = "processorKey_binderCoreProcessor";

    public Binder addBinder(Binder binder, Definition def, Class clazz, InputDataAccessor inputData, Map fileItems, Map options) 
    	throws AccessControlException, WriteFilesException;
  	public void deleteBinder(Binder binder, boolean deleteMirroredSource) throws AccessControlException;
  	//return search results
  	public Map getBinders(Binder binder, Map options);
    public void indexFunctionMembership(Binder binder, boolean cascade);
    public void indexTeamMembership(Binder binder, boolean cascade);
    public void indexOwner(Collection<Binder>binders, Long ownerId);
	public void indexBinder(Binder binder, boolean includeEntries);	
    public void indexBinder(Binder binder, boolean includeEntries, boolean deleteIndex, Collection tags);
   public Collection indexTree(Binder binder, Collection exclusions);
    public Collection indexTree(Binder binder, Collection exclusions, StatusTicket statusTicket);
   
 	public void modifyBinder(Binder binder, InputDataAccessor inputData, Map fileItems, Collection deleteAttachments) 
		throws AccessControlException, WriteFilesException;
    public void moveBinder(Binder source, Binder destination);
    /**
     * Fix up a binder after its parent have been moved
     * @param binder
     */
    public void moveBinderFixup(Binder binder);
	public ChangeLog processChangeLog(Binder binder, String operation);
}
