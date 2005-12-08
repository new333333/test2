package com.sitescape.ef.domain;

/**
 * @author Jong Kim
 *
 */
public interface SingletonWorkflowSupport {
    public WorkflowStatus getWorkflow();
    public void setWorkflow(WorkflowStatus workflowStatus);
}
