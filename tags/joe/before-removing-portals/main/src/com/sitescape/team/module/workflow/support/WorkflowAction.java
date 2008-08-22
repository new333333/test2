package com.sitescape.team.module.workflow.support;

import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;

public interface WorkflowAction  {
	public void execute(WorkflowSupport entry, WorkflowState state);
	public void setHelper(WorkflowCallout helper);
}
