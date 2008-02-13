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
package com.sitescape.team.module.workspace;

import java.util.Map;
import java.util.SortedSet;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.NoWorkspaceByTheIdException;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;
/**
 * @author Jong Kim
 *
 */
public interface WorkspaceModule {
	public enum WorkspaceOperation {
		addFolder,
		addWorkspace
	}
	/**
	 * Add a new folder under this workspace
	 * @param workspaceId
	 * @param definitionId
	 * @param inputData
	 * @param fileItems May be <code>null</code>
     * @param options additional processing options or null
	 * @return
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	public Long addFolder(Long workspaceId, String definitionId, InputDataAccessor inputData,
			Map fileItems, Map options) throws AccessControlException, WriteFilesException;
	/**
	 * Add a new workspace under this workspace
	 * @param workspaceId
	 * @param definitionId
	 * @param inputData
	 * @param fileItems May be <code>null</code>
     * @param options additional processing options or null
 	 * @return
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	public Long addWorkspace(Long workspaceId, String definitionId, InputDataAccessor inputData,
			Map fileItems, Map options) throws AccessControlException, WriteFilesException;
	/**
	 * Check access to a workspace, throwing an exception if denied
	 * @param workspace
	 * @param operation
	 * @throws AccessControlException
	 */
	public void checkAccess(Workspace workspace, WorkspaceOperation operation) 
		throws AccessControlException;
	/**
	 * Return set of child binder titles
	 * @param top
	 * @return
	 */
   	public SortedSet<String> getChildrenTitles(Workspace top);
	/**
	 * Return a workspace
	 * @param workspaceId
	 * @return
	 * @throws NoWorkspaceByTheIdException
	 * @throws AccessControlException
	 */
	public Workspace getWorkspace(Long workspaceId)	
		throws NoWorkspaceByTheIdException, AccessControlException;
	/**
	 * Return the top workspace
	 * @return
	 * @throws AccessControlException
	 */
	public Workspace getTopWorkspace()
		throws AccessControlException;

  	/**
  	 * Return list of child binders, that have been verified for read access
  	 * and sorted by title
  	 * @param id
  	 * @return
  	 * @throws AccessControlException
  	 */
	public SortedSet<Binder> getWorkspaceTree(Long id) 
		throws AccessControlException; 

    /**
     * Test access to a workspace
     * @param workspace
     * @param operation
     * @return
     */
    public boolean testAccess(Workspace workspace, WorkspaceOperation operation);

}

