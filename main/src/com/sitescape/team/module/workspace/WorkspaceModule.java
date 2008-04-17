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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;

import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.NoWorkspaceByTheIdException;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.web.tree.DomTreeBuilder;
/**
 * @author Jong Kim
 *
 */
public interface WorkspaceModule {
	public enum WorkspaceOperation {
		addFolder,
		addWorkspace,
	   changeEntryTimestamps

	}
    public boolean testAccess(Workspace workspace, WorkspaceOperation operation);
    public void checkAccess(Workspace workspace, WorkspaceOperation operation) throws AccessControlException;
    /**
     * 
     * @return
     * @throws NoWorkspaceByTheIdException
     * @throws AccessControlException
     */
    public Workspace getWorkspace() throws NoWorkspaceByTheIdException, AccessControlException;
	public Workspace getWorkspace(Long workspaceId)	throws NoWorkspaceByTheIdException, AccessControlException;
	public Workspace getTopWorkspace();
  	/**
  	 * Return list of child binders, that have been verified for read access
  	 * and sorted by title
  	 * @param id
  	 * @return
  	 * @throws AccessControlException
  	 */
	public Collection getWorkspaceTree(Long id) throws AccessControlException; 
   	public Set<String> getChildrenTitles(Workspace top);
  	public Document getDomWorkspaceTree(DomTreeBuilder domTreeHelper) throws AccessControlException;
    /**
     * Traverse the workspace tree  returing a DOM structure containing workspaces and
     * folders
     * @param id
     * @param domTreeHelper
     * @param levels = depth to return.  -1 means all
     * @return
     * @throws AccessControlException
     */
  	public Document getDomWorkspaceTree(Long id, DomTreeBuilder domTreeHelper, int levels) throws AccessControlException;
 	public Document getDomWorkspaceTree(Long topId, Long bottonId, DomTreeBuilder domTreeHelper) throws AccessControlException;

    public Long addWorkspace(Long folderId, String definitionId, InputDataAccessor inputData,
       		Map fileItems) throws AccessControlException, WriteFilesException;
    public Long addWorkspace(Long folderId, String definitionId, InputDataAccessor inputData,
       		Map fileItems, Map options) throws AccessControlException, WriteFilesException;
     public Long addFolder(Long folderId, String definitionId, InputDataAccessor inputData,
       		Map fileItems) throws AccessControlException, WriteFilesException;
     public Long addFolder(Long folderId, String definitionId, InputDataAccessor inputData,
        		Map fileItems, Map options) throws AccessControlException, WriteFilesException;
}

