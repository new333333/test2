package com.sitescape.ef.domain;

/**
 * @author Jong Kim
 *
 */
public interface SingletonWorkflowSupport {
    public WorkflowState getWorkflowState();
    public void setWorkflowState(WorkflowState workflowState);
}
