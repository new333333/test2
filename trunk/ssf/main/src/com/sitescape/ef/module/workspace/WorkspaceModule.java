package com.sitescape.ef.module.workspace;

import java.util.Map;

import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.security.AccessControlException;

/**
 * @author Jong Kim
 *
 */
public interface WorkspaceModule {
    /**
     * 
     * @param zoneName
     * @param workspaceName If <code>null</code>, default workspace is assumed.
     * @return
     * @throws NoWorkspaceByTheNameException
     */
	public Map showWorkspace();
	public Map showWorkspace(Long workspaceId);
    public org.dom4j.Document getDomWorkspaceTree() throws AccessControlException;
    public org.dom4j.Document getDomWorkspaceTree(Long id) throws AccessControlException;
}
