package com.sitescape.ef.domain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.security.acl.AclSet;
import com.sitescape.util.Validator;

/**
 *
 * @author Jong Kim
 */
public abstract class WorkflowControlledEntry extends Entry 
	implements WorkflowSupport, AclControlled {
	private Set readMemberIds,writeMemberIds,deleteMemberIds,changeAclMemberIds;
    protected Set workflowStates; //initialized by hiberate access=field  
	protected Set iWorkflowStates;
    protected HistoryStamp workflowChange;
	/**
      * @hibernate.component class="com.sitescape.ef.domain.HistoryStamp" prefix="wrk_" 
     */
    public HistoryStamp getWorkflowChange() {
        return this.workflowChange;
    }
    public void setWorkflowChange(HistoryStamp workflowChange) {
        this.workflowChange = workflowChange;
    }

    public Set getWorkflowStates() {
    	if (iWorkflowStates != null) return iWorkflowStates;
    	if (workflowStates == null) workflowStates = new HashSet();
   	 	return workflowStates;  
     }
     public void setWorkflowStates(Set workflowStates) {
    	 //Since ids are assigned on WorkflowState, don't need to do anything
    	 //special to reduce updates.
    	 this.workflowStates = workflowStates;
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
  	/**
     * 
     */
    public boolean getInheritAclFromParent() {
    	return !hasAclSet();
    }

    /**
     * Determined by current workflowState
     */
    public void setInheritAclFromParent(boolean inherit) {
		throw new InternalError("Method not supported");
    }
    
    public Long getCreatorId() {
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
	public void setAclSet(AclSet aclSet) {
		throw new InternalError("Method not supported");
	}
	public AclSet getAclSet() {
	    return new WfAclSet();	        
	}   

	public boolean checkWorkArea(AccessType type) {
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
	public boolean checkOwner(AccessType type) {
	   Set states = getWorkflowStates();
	   if ((states == null) || states.isEmpty()) return true; 
	   //If any state allows creator, use it
	   for (Iterator iter=states.iterator(); iter.hasNext();) {
	    	WorkflowState state = (WorkflowState)iter.next();
	    	WfAcl a = state.getAcl(type);
	    	//return least restrictive
	       	if ((a != null) && a.isCreator()) return true;
	   }
	   return false;
	}
	public Set getStateMembers(AccessType type) {
	   	Set result = new HashSet();
	   	Set states = getWorkflowStates();
	    if ((states == null) || states.isEmpty()) return result; 
	    //If any state allows creator, use it
	    for (Iterator iter=states.iterator(); iter.hasNext();) {
	       	WorkflowState state = (WorkflowState)iter.next();
	       	WfAcl a = state.getAcl(type);
	       	if (a != null) {
	       		result.addAll(a.getPrincipals());
	       	}
	    }
	    return result;
	    	
	}

	public class WfAclSet implements AclSet {
			
	    public Set getReadMemberIds() {
	        if(readMemberIds == null) readMemberIds = getStateMembers(AccessType.READ);
	        return readMemberIds;
	    }
	    public Set getWriteMemberIds() {
	        if(writeMemberIds == null) writeMemberIds = getStateMembers(AccessType.WRITE);
	        return writeMemberIds;
	    }
	    public Set getDeleteMemberIds() {
	        if(deleteMemberIds == null) deleteMemberIds = getStateMembers(AccessType.DELETE);
	        return deleteMemberIds;
	    }

	    public Set getChangeAclMemberIds() {
	        if(changeAclMemberIds == null) changeAclMemberIds = getStateMembers(AccessType.CHANGE_ACL);
	        return changeAclMemberIds;
	    }
	        
	    public Set getMemberIds(AccessType accessType) {
	        if(accessType == AccessType.READ)
	            return getReadMemberIds();
	        else if(accessType == AccessType.WRITE)
	            return getWriteMemberIds();
	        else if(accessType == AccessType.DELETE)
	            return getDeleteMemberIds();
	        else if(accessType == AccessType.CHANGE_ACL)
	            return getChangeAclMemberIds();
	        else
	            throw new IllegalArgumentException("Illegal access type: " + accessType.toString());
	    }
	    
	    public void addMemberId(AccessType accessType, Long memberId) {
			throw new InternalError("Method not supported");
	    }
	    
	    public boolean removeMemberId(AccessType accessType, Long memberId) {
			throw new InternalError("Method not supported");
	    }
	    
	    public Object clone() {
			throw new InternalError("Method not supported");
	    }
	    
	    public void clear() {	        
	        readMemberIds = null;
	        writeMemberIds = null;
	        deleteMemberIds = null;
	        changeAclMemberIds = null;
	    }

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
