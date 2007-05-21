package com.sitescape.team.module.folder.impl;

import java.util.Map;
import java.util.HashMap;
import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.fi.FIException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;

public class BaseFolderModule extends AbstractFolderModule implements BaseFolderModuleMBean {

	public boolean synchronize(Long folderId) throws FIException, UncheckedIOException {
		throw new UnsupportedOperationException("synchronize operation is not supported in the base edition");
	}
	/*
	 *  (non-Javadoc)
	 * Check access to folder.  If operation not listed, assume read_entries needed
	 * Use method names as operation so we can keep the logic out of application
	 * @see com.sitescape.team.module.folder.FolderModule#testAccess(com.sitescape.team.domain.FolderEntry, java.lang.String)
	 */
	public boolean testAccess(FolderEntry entry, String operation) {
		if ("addEntryWorkflow".equals(operation)) return false;
		return super.testAccess(entry, operation);
	}

   public void addEntryWorkflow(Long folderId, Long entryId, String definitionId) {
		throw new UnsupportedOperationException("Workflow is not supported in the base edition");
    }
    public boolean testTransitionOutStateAllowed(FolderEntry entry, Long stateId) {
		return false;
    }
	
    public boolean testTransitionInStateAllowed(FolderEntry entry, Long stateId, String toState) {
		return false;
    }
    public void modifyWorkflowState(Long folderId, Long entryId, Long stateId, String toState) throws AccessControlException {
		throw new UnsupportedOperationException("Workflow is not supported in the base edition");
    }
	public Map getManualTransitions(FolderEntry entry, Long stateId) {
		return new HashMap();
    }		

	public Map getWorkflowQuestions(FolderEntry entry, Long stateId) {
		return new HashMap();
    }		

    public void setWorkflowResponse(Long folderId, Long entryId, Long stateId, InputDataAccessor inputData) {
		throw new UnsupportedOperationException("Workflow is not supported in the base edition");       
    }

}
