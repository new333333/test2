/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.domain;

import java.util.Set;

/**
 * @author Jong Kim
 *
 */
public interface WorkflowSupport  {
	public void addWorkflowResponse(WorkflowResponse workflowResponse);
    public void addWorkflowState(WorkflowState workflowState);
	public boolean hasAclSet();
	public boolean isWorkAreaAccess(WfAcl.AccessType type);
	public Long getOwnerId();
	public Set getWorkflowResponses();
	public Set getStateMembers(WfAcl.AccessType type);
    public WorkflowState getWorkflowState(Long id);
    public WorkflowState getWorkflowStateByThread(Definition def, String threadName);
    public Set getWorkflowStates();
    public HistoryStamp getWorkflowChange();
   /**
     * @return Returns a list of <code>WorkflowStates</code>.
     */
    public void removeWorkflowState(WorkflowState workflowState);
    public void removeWorkflowResponse(WorkflowResponse workflowResponse);
	public void setStateChange(WorkflowState workflowState);
	public ChangeLog getStateChanges();
    public void setWorkflowChange(HistoryStamp workflowChange);
}