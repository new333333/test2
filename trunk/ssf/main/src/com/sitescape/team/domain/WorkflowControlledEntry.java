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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sitescape.team.ObjectKeys;
import com.sitescape.util.Validator;

/**
 *
 * @author Jong Kim
 */
public abstract class WorkflowControlledEntry extends Entry 
	implements WorkflowSupport {
    protected Set workflowStates; //initialized by hiberate access=field  
	protected Set iWorkflowStates;
    protected HistoryStamp workflowChange;//initialized by hiberate access=field  
    protected Set workflowResponses; //initialized by hiberate access=field  
	protected ChangeLog changes=null;

    /**
      * @hibernate.component class="com.sitescape.team.domain.HistoryStamp" prefix="wrk_" 
     */
    public HistoryStamp getWorkflowChange() {
        return this.workflowChange;
    }
    public void setWorkflowChange(HistoryStamp workflowChange) {
        if (workflowChange != null) {
        	if (workflowChange.compareDate(this.workflowChange) > 0)
        		this.workflowChange = workflowChange;
        	if (workflowChange.compareDate(getModification()) > 0) 
        		setModification(workflowChange);
        } else this.workflowChange = null;
    }

    public Set getWorkflowResponses() {
    	if (workflowResponses == null) workflowResponses = new HashSet();
   	 	return workflowResponses;  
     }
    public void addWorkflowResponse(WorkflowResponse workflowResponse) {
    	getWorkflowResponses().add(workflowResponse);
    }
    public void removeWorkflowResponse(WorkflowResponse workflowResponse) {
    	getWorkflowResponses().remove(workflowResponse);
    }
    public Set getWorkflowStates() {
    	if (iWorkflowStates != null) return iWorkflowStates;
    	if (workflowStates == null) workflowStates = new HashSet();
   	 	return workflowStates;  
     }
     public WorkflowState getWorkflowState(Long id) {
     	//Make sure initialized
     	getWorkflowStates();
     	
		WorkflowState ws=null;
		for (Iterator iter=workflowStates.iterator(); iter.hasNext();) {
			ws = (WorkflowState)iter.next();
			if (ws.getId().equals(id)) return ws;
		}
		return null;
     }
     public WorkflowState getWorkflowStateByThread(Definition def, String threadName) {
      	//Make sure initialized
      	getWorkflowStates();
      	
 		WorkflowState ws=null;
 		for (Iterator iter=workflowStates.iterator(); iter.hasNext();) {
 			ws = (WorkflowState)iter.next();
 			if (!def.equals(ws.getDefinition())) continue;
 			if (Validator.isNull(ws.getThreadName())) {
 				if (Validator.isNull(threadName)) return ws; 
 			} else {
 				if (ws.getThreadName().equals(threadName)) return ws;
 			}
 		}
 		return null;
      }
     public void addWorkflowState(WorkflowState state) {
     	if (state == null) return;
    	//Make sure initialized
    	getWorkflowStates();
        workflowStates.add(state);
 	   	state.setOwner(this);
    }
    public void removeWorkflowState(WorkflowState state) {
     	if (state == null) return;
    	//Make sure initialized
    	getWorkflowStates();
        workflowStates.remove(state);
 	   	state.setOwner((AnyOwner)null);
    }
    
    public Long getOwnerId() {
    	HistoryStamp creation = getCreation();
    	if(creation != null) {
    		Principal principal = creation.getPrincipal();
    		if(principal != null)
    			return principal.getId();
    	}
    	return null;
    }
	public boolean hasAclSet() {
		Set states = getWorkflowStates();
	    if ((states == null) || states.isEmpty()) return false;
	    return true;
	}

	public boolean isWorkAreaAccess(WfAcl.AccessType type) {
	    Set states = getWorkflowStates();
	    if ((states == null) || states.isEmpty()) return true; 
	     //If any state uses parent, use it
	    for (Iterator iter=states.iterator(); iter.hasNext();) {
	    	WorkflowState state = (WorkflowState)iter.next();
	    	WfAcl a = state.getAcl(type);
	    	//return least restrictive
	    	if ((a != null) && a.isUseDefault()) return true;
	       }
       return false;
	}
	public Set getStateMembers(WfAcl.AccessType type) {
	   	Set result = new HashSet();
	   	Set states = getWorkflowStates();
	    if ((states == null) || states.isEmpty()) return result; 
	    for (Iterator iter=states.iterator(); iter.hasNext();) {
	       	WorkflowState state = (WorkflowState)iter.next();
	       	WfAcl a = state.getAcl(type);
	       	if (a != null) {
	       		result.addAll(a.getPrincipals());
	       	}
	    }
        if (result.remove(ObjectKeys.OWNER_USER_ID)) result.add(getOwnerId());
    	if (result.remove(ObjectKeys.TEAM_MEMBER_ID)) result.add(getParentBinder().getTeamMemberIds());

	    return result;
	    	
	}
	/**
	 * Hold change document.  Must be saved by some other mechanism.
	 * This allows a string of state changes to be handled by one log entry
	 * @param ws
	 */
	public void setStateChange(WorkflowState ws) {
		if (changes == null) {
			changes = new ChangeLog(this, ChangeLog.MODIFYWORKFLOWSTATE);
			changes.setOperationDate(ws.getWorkflowChange().getDate());
		}
		//record change
		ws.addChangeLog(changes.getEntityRoot());
	}
	public ChangeLog getStateChanges() {
		return changes;
	}

    /*
     * The following methods are used for performance optimization during indexing.
     * The values of each collection are loaded and built by hand.  
     * They are not persisted.  This allows us to load greater than the 
     * hibernate "batch-size" number of collections at once.
     */
    public void setIndexWorkflowStates(Set iWorkflowStates) {
    	this.iWorkflowStates = iWorkflowStates;
    }
	
}
