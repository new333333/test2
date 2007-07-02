/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.workflow.impl;

import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.workflow.WorkflowModule;

public class NullWorkflowModuleImpl extends CommonDependencyInjection implements WorkflowModule {
   public void startScheduledJobs(Workspace zone) {	   
    }

	public void addEntryWorkflow(WorkflowSupport entry, EntityIdentifier id, Definition workflowDef) {
	}
	public void deleteEntryWorkflow(WorkflowSupport entry) {
	}	
	public void deleteEntryWorkflow(WorkflowSupport wEntry, WorkflowState state) {
	}
	public void deleteEntryWorkflow(WorkflowSupport wEntry, Definition def)  {
	}
	public void deleteProcessDefinition(String name) {
	};
	//Routine to build (or modify) a workflow process definition from a Definition
	public void modifyProcessDefinition(String definitionName, Definition def) {		
	}
	public void modifyWorkflowState(WorkflowSupport entry, WorkflowState state, String toState) {
	}

	public boolean modifyWorkflowStateOnUpdate(WorkflowSupport entry) {
		return false;
	}
	public boolean modifyWorkflowStateOnResponse(WorkflowSupport entry) {
		return false;
	}
	public boolean modifyWorkflowStateOnReply(WorkflowSupport entry) {
		return false;	
	}	
	public void processTimers() {
		
	}

}
