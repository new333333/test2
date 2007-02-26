package com.sitescape.team.module.workflow;
import org.jbpm.graph.def.ProcessDefinition;

import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.domain.Workspace;

public interface WorkflowModule {
	/**
	 * Start workflow on entry.
	 * @param entry
	 * @param id
	 * @param workflowDef
	 */
	public void addEntryWorkflow(WorkflowSupport entry, EntityIdentifier id, Definition workflowDef);
	/**
	 * Delete workflow associated with an entry
	 * @param entry
	 */
	public void deleteEntryWorkflow(WorkflowSupport entry);
	/**
	 * Delete a process definition by id
	 * @param id
	 */
	public void deleteProcessDefinition(Long id);
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
	public void modifyProcessDefinition(ProcessDefinition pD, Definition def);
	/**
	 * Start manual transition
	 * @param entry
	 * @param state
	 * @param toState
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
	 * A timeout has occurred.  Process the transition
	 * @param timerId
	 */
	public void modifyWorkflowStateOnTimeout(Long timerId);
	/**
	 * An update has occured on the entry.
	 * See if that triggers a transition and process
	 * @param entry
	 * @return
	 */
	public boolean modifyWorkflowStateOnUpdate(WorkflowSupport entry);
	/**
	 * Start any workflow jobs for the zone
	 * @param zone
	 */
    public void startScheduledJobs(Workspace zone);

}
