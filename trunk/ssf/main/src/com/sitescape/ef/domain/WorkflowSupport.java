package com.sitescape.ef.domain;

import java.util.Set;

import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.acl.AclControlled;

/**
 * @author Jong Kim
 *
 */
public interface WorkflowSupport extends AclControlled {
   public HistoryStamp getWorkflowChange();
    public void setWorkflowChange(HistoryStamp workflowChange);
   /**
     * @return Returns a list of <code>WorkflowStates</code>.
     */
    public Set getWorkflowStates();
    public void setWorkflowStates(Set workflowStates);   
    public void addWorkflowState(WorkflowState workflowState);
    public void removeWorkflowState(WorkflowState workflowState);
    public WorkflowState getWorkflowState(Long id);
    public WorkflowState getWorkflowStateByThread(Definition def, String threadName);
	public boolean hasAclSet();
	public boolean checkWorkArea(AccessType type);
	public boolean checkOwner(AccessType type);
	public Set getStateMembers(AccessType type);

	 
}