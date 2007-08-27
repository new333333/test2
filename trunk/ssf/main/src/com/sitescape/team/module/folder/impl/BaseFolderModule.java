package com.sitescape.team.module.folder.impl;

import java.util.Map;
import java.util.HashMap;
import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.fi.FIException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.StatusTicket;

public class BaseFolderModule extends AbstractFolderModule implements BaseFolderModuleMBean {

	public boolean synchronize(Long folderId, StatusTicket statusTicket) throws FIException, UncheckedIOException {
		throw new UnsupportedOperationException("synchronize operation is not supported in the base edition");
	}

	public boolean testAccess(FolderEntry entry, FolderOperation operation) {
		switch (operation) {
			case addEntryWorkflow:
			case deleteEntryWorkflow:
				return false;
			default:
				return super.testAccess(entry, operation);
		}
	}

   public void addEntryWorkflow(Long folderId, Long entryId, String definitionId) {
		throw new UnsupportedOperationException("Workflow is not supported in the base edition");
    }
   public void deleteEntryWorkflow(Long parentFolderId, Long entryId, String definitionId) 
   			throws AccessControlException {
	   
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
