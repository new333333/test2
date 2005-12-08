package com.sitescape.ef.domain;

import java.util.List;

/**
 * @author Jong Kim
 *
 */
public interface MultipleWorkflowSupport {
    /**
     * @return Returns an order list of <code>WorkflowStatus</code>.
     */
    public List getWorkflows();
    public void setWorkflows(List workflows);    

}