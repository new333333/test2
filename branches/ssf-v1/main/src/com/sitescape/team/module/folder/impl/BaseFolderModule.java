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
