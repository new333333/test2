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
package com.sitescape.team.module.workflow;

import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.domain.Workspace;

public interface WorkflowModule {
	   // Defines variable names
    public final static String ENTRY_TYPE = "__entryType";
    public final static String ENTRY_ID = "__entryId";
    public final static String BINDER_ID = "__binderId";
	/**
	 * Start workflow on entry.
	 * @param entry
	 * @param id
	 * @param workflowDef
	 */
	public void addEntryWorkflow(WorkflowSupport entry, EntityIdentifier id, Definition workflowDef);
	/**
	 * Delete all workflows associated with an entry
	 * @param entry
	 */
	public void deleteEntryWorkflow(WorkflowSupport entry);
	/**
	 * Delete a specific workflow token
	 * @param wEntry
	 * @param state
	 */
	public void deleteEntryWorkflow(WorkflowSupport wEntry, WorkflowState state);
	/**
	 * Delete all tokens associated with a definition
	 * @param wEntry
	 * @param def
	 */
	public void deleteEntryWorkflow(WorkflowSupport wEntry, Definition def);
	/**
	 * Delete a process definition by name.  
	 * Use use UUID as the name
	 * @param name
	 */
	public void deleteProcessDefinition(String name);
	
	/**
	 * Update process definition
	 * @param definitionName
	 * @param def
	 */
	public void modifyProcessDefinition(String definitionName, Definition def);
	/**
	 * Same as <code>modifyProcessDefinition</code>
	 * @param pD
	 * @param def
	 */
	public void modifyWorkflowState(WorkflowSupport entry, WorkflowState state, String toState);
	/**
	 * A reply was entered.  
	 * See if that triggers a transition and process
	 * @param entry
	 * @return
	 */
	public boolean modifyWorkflowStateOnReply(WorkflowSupport entry);
	/**
	 * A response to a workflow question was supplied.
	 * See if that triggers a transition and process
	 * @param entry
	 * @return
	 */
	public boolean modifyWorkflowStateOnResponse(WorkflowSupport entry);
	/**
	 * An update has occured on the entry.
	 * See if that triggers a transition and process
	 * @param entry
	 * @return
	 */
	public boolean modifyWorkflowStateOnUpdate(WorkflowSupport entry);
	public void processTimers();
	/**
	 * Start any workflow jobs for the zone
	 * @param zone
	 */
    public void startScheduledJobs(Workspace zone);

}
