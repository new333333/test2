package com.sitescape.team.module.workflow.support;


//marker interface - workflow processing will schedule job
public interface WorkflowScheduledAction  {
	//status input params
	public final static String WORKFLOW_APPLICATION_ID="workflow.application_id";
	public final static String WORKFLOW_APPLICATION_NAME="workflow.application_name";
	public final static String WORKFLOW_BINDER_ID="workflow.binder_id";
	public final static String WORKFLOW_ENTRY_ID="workflow.entry_id";
	public final static String WORKFLOW_STATE_ID="workflow.state_id";
	public final static String WORKFLOW_STATE_NAME="workflow.state_name";
	public final static String WORKFLOW_THREAD_NAME="workflow.thread_name";
	public final static String WORKFLOW_RESULT_NAME="workflow.result_name";
	public boolean execute(Long entryId, Long stateId, WorkflowStatus status);
}
