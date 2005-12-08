package com.sitescape.ef.domain;

import java.util.List;

/**
 * @author Jong Kim
 *
 */
public interface MultipleWorkflowSupport {
    /**
     * @return Returns a list of <code>WorkflowStates</code>.
     */
    public List getWorkflowStates();
    public void setWorkflowStates(List workflowStates);   
    public void addWorkflowState(WorkflowState workflowState);
    public void removeWorkflowState(WorkflowState workflowState);

}