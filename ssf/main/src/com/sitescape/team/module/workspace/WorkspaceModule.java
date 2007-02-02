package com.sitescape.team.module.workspace;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;

import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.NoWorkspaceByTheIdException;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.DomTreeBuilder;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;
/**
 * @author Jong Kim
 *
 */
public interface WorkspaceModule {
    public void checkAccess(Workspace workspace, String operation) throws AccessControlException;
    /**
     * 
     * @return
     * @throws NoWorkspaceByTheIdException
     * @throws AccessControlException
     */
    public Workspace getWorkspace() throws NoWorkspaceByTheIdException, AccessControlException;
	public Workspace getWorkspace(Long workspaceId)	throws NoWorkspaceByTheIdException, AccessControlException;
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
    public Long addFolder(Long folderId, String definitionId, InputDataAccessor inputData,
       		Map fileItems) throws AccessControlException, WriteFilesException;
}

