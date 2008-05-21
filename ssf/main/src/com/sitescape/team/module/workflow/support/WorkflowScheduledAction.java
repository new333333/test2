package com.sitescape.team.module.workflow.support;

import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;

//marker interface - workflow processing will schedule job
public interface WorkflowScheduledAction  {
	public boolean execute(WorkflowSupport entry, WorkflowState state, WorkflowStatus status);
}
