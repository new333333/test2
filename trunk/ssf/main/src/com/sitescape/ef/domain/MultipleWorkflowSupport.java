package com.sitescape.ef.domain;

import java.util.Set;

/**
 * @author Jong Kim
 *
 */
public interface MultipleWorkflowSupport {
    /**
     * @return Returns a list of <code>WorkflowStates</code>.
     */
    public Set getWorkflowStates();
    public void setWorkflowStates(Set workflowStates);   
    public void addWorkflowState(WorkflowState workflowState);
    public void removeWorkflowState(WorkflowState workflowState);

}