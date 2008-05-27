package com.sitescape.team.module.workflow.support;

import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;

public interface WorkflowCondition {
	/**
	 * 
	 * @param entry
	 * @param state
	 * @return True if condition met and transition should be taken; false otherwise
	 */
	public boolean execute(WorkflowSupport entry, WorkflowState state);
	public void setHelper(WorkflowCalloutHelper helper);
}
